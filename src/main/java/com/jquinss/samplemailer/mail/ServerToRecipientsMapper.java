package com.jquinss.samplemailer.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.mail.Message.RecipientType;

public class ServerToRecipientsMapper {
	public static HashMap<String, HashMap<RecipientType, List<String>>> buildServerToRecipientsMap(HashMap<String, String> domainToMXServerMap,
																								   HashMap<RecipientType, List<String>> rcptTypeToRcptsMap) {
		HashMap<String, HashMap<RecipientType, List<String>>> serverToRcptsMap = new HashMap<>();
		 
		for (RecipientType rcptType : rcptTypeToRcptsMap.keySet()) {
			for (String rcpt : rcptTypeToRcptsMap.get(rcptType)) {
				String domain = rcpt.substring(rcpt.lastIndexOf('@') + 1);
				String server = domainToMXServerMap.get(domain);
				
				if (!serverToRcptsMap.containsKey(server)) {
					HashMap<RecipientType,List<String>> rcptTypeToRcptListMap = createRcptTypeToRcptListMap(rcpt, rcptType);
					serverToRcptsMap.put(server, rcptTypeToRcptListMap);
				}
				else {
					HashMap<RecipientType,List<String>> rcptTypeToRcptListMap = serverToRcptsMap.get(server);
					if (!rcptTypeToRcptListMap.containsKey(rcptType)) {
						addRcptToRcptTypeToRcptListMap(rcpt, rcptType, rcptTypeToRcptListMap);
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

	private static void addRcptToRcptTypeToRcptListMap(String rcpt, RecipientType rcptType,
												  HashMap<RecipientType,List<String>> rcptTypeToRcptListMap) {
		List<String> rcptList = new ArrayList<>();
		rcptList.add(rcpt);
		rcptTypeToRcptListMap.put(rcptType, rcptList);
	}

	private static HashMap<RecipientType,List<String>> createRcptTypeToRcptListMap(String rcpt, RecipientType rcptType) {
		HashMap<RecipientType,List<String>> rcptTypeToRcptListMap = new HashMap<>();
		addRcptToRcptTypeToRcptListMap(rcpt, rcptType, rcptTypeToRcptListMap);
		return rcptTypeToRcptListMap;
	}
}