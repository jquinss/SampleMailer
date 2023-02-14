package com.jquinss.samplemailer.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.jquinss.samplemailer.mail.DNSResolver;

import org.xbill.DNS.MXRecord;
import org.xbill.DNS.TextParseException;

import jakarta.mail.Message.RecipientType;

public class ServerToRecipientsMapper {
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&�*+/=?`{|}~^.-]+@(?<domain>[a-zA-Z0-9.-]+)$";
	private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
	
	public static HashMap<String, HashMap<RecipientType, List<String>>> getServerToRecipientsMap(HashMap<RecipientType, List<String>> rcptTypeToRcptMap) throws TextParseException {
		
		List<String> uniqueDomains = getUniqueDomains(rcptTypeToRcptMap);
		HashMap<String, List<MXRecord>> mxRecordsMap = resolveMXRecords(uniqueDomains);
		HashMap<String, String> domainToServerMap =  getDomainToServerMap(mxRecordsMap);
		HashMap<String, HashMap<RecipientType, List<String>>> serverToRecipientsMap = buildServerToRecipientsMap(domainToServerMap, rcptTypeToRcptMap);
		
		return serverToRecipientsMap;
	}
	
	private static List<String> getUniqueDomains(HashMap<RecipientType, List<String>> rcptTypeToRcptMap) {
		List<String> uniqueDomains = new ArrayList<String>();
		
		Iterator<List<String>> iterator = rcptTypeToRcptMap.values().iterator();
		while (iterator.hasNext()) {
			List<String> rcpts = iterator.next();
			for (String rcpt : rcpts) {
				Matcher rcptMatcher = emailPattern.matcher(rcpt);
				if (rcptMatcher.matches()) {
					String domain = rcptMatcher.group("domain");
					if (!uniqueDomains.contains(domain)) {
						uniqueDomains.add(domain);
					}
				}
			}
		}
		
		return uniqueDomains;
	}
	
	private static HashMap<String, List<MXRecord>> resolveMXRecords(List<String> uniqueDomains) throws TextParseException {
		HashMap<String, List<MXRecord>> mxRecordsMap = new HashMap<String, List<MXRecord>>();
		
		for (String domain : uniqueDomains) {
			List<MXRecord> mxRecords = DNSResolver.resolveMXRecords(domain);
			mxRecordsMap.put(domain, mxRecords);
		}
		
		return mxRecordsMap;
	}
	
	private static HashMap<String, String> getDomainToServerMap(HashMap<String, List<MXRecord>> mxRecordsMap) {
		HashMap<String, String> domainToServerMap = new HashMap<String, String>();
		for (String domain : mxRecordsMap.keySet()) {
			List<MXRecord> mxRecords = mxRecordsMap.get(domain);
			// order records by priority and extract server name of the first record
			mxRecords.sort(new MXRecordComparator());
			MXRecord mxRecord = mxRecords.get(0);
			String server = mxRecord.getTarget().toString();
			// remove trailing dot
			server = server.substring(0, server.length() - 1);
			domainToServerMap.put(domain, server);
		}
		
		return domainToServerMap;
	}
	
	private static HashMap<String, HashMap<RecipientType, List<String>>> buildServerToRecipientsMap(HashMap<String, String> domainToServerMap, HashMap<RecipientType, List<String>> rcptTypeToRcptsMap) {
		HashMap<String, HashMap<RecipientType, List<String>>> serverToRcptsMap = new HashMap<String, HashMap<RecipientType, List<String>>>();
		 
		for (RecipientType rcptType : rcptTypeToRcptsMap.keySet()) {
			
			for (String rcpt : rcptTypeToRcptsMap.get(rcptType)) {
				String domain = rcpt.substring(rcpt.lastIndexOf('@') + 1);
				String server = domainToServerMap.get(domain);
				
				if (!serverToRcptsMap.containsKey(server)) {
					HashMap<RecipientType,List<String>> rcptTypeToRcptListMap = new HashMap<RecipientType,List<String>>();
					List<String> rcptList = new ArrayList<String>();
					rcptList.add(rcpt);
					rcptTypeToRcptListMap.put(rcptType, rcptList);
					serverToRcptsMap.put(server, rcptTypeToRcptListMap);
				}
				else {
					HashMap<RecipientType,List<String>> rcptTypeToRcptListMap = serverToRcptsMap.get(server);
					if (!rcptTypeToRcptListMap.containsKey(rcptType)) {
						List<String> rcptList = new ArrayList<String>();
						rcptList.add(rcpt);
						rcptTypeToRcptListMap.put(rcptType, rcptList);
					}
					else {
						List<String> rcptList = rcptTypeToRcptListMap.get(rcptType);
						rcptList.add(rcpt);
					}
				}
			}
		}
		
		return serverToRcptsMap;
	}
}
