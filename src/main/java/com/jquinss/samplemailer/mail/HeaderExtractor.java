package com.jquinss.samplemailer.mail;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import jakarta.mail.Header;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.Enumeration;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;

public class HeaderExtractor {
	private static final String HEADER_SYNTAX = "(?<name>^[\\p{ASCII}]+?) ?:\\s(?<value>[\\p{ASCII}]+)?$";
	private static Pattern headerPattern = Pattern.compile(HEADER_SYNTAX);
	
	private HeaderExtractor() {}
	
	private static List<EmailHeader> convertRawHeaderLinestoHeaders(String[] rawHeaders) {
		List<EmailHeader> headers = new ArrayList<EmailHeader>();
		String headerName = null;
		StringBuilder headerValue = null;
		EmailHeader header = null;
		
		for (String rawHeader : rawHeaders) {
			Matcher headerMatcher = headerPattern.matcher(rawHeader);
			if (headerMatcher.matches()) {
				if (headerName != null && headerValue != null) {
					header = new EmailHeader(headerName, headerValue.toString());
					headers.add(header);
				}
				
				headerName = headerMatcher.group("name");
				headerValue = new StringBuilder();
				
				String value = headerMatcher.group("value");
				if (value != null) {
					headerValue.append(value);
				}
			}
			else {
				headerValue.append(rawHeader);
			}
		}
		
		if (headerName != null && headerValue != null) {
			header = new EmailHeader(headerName, headerValue.toString());
			headers.add(header);
		}
		
		return headers;
	}
	
	public static List<EmailHeader> extractHeaders(MAPIMessage msg) throws ChunkNotFoundException {
		String[] rawHeaders = msg.getHeaders();
		return convertRawHeaderLinestoHeaders(rawHeaders);
	}
	
	public static List<EmailHeader> extractHeaders(String file) throws IOException {
		List<String> fileLines = Files.readAllLines(new File(file).toPath());
		String[] rawHeaders = fileLines.toArray(new String[0]);
		return convertRawHeaderLinestoHeaders(rawHeaders);
	}
	
	public static List<EmailHeader> extractHeaders(MimeMessage msg) throws MessagingException {
		List<EmailHeader> headerList = new ArrayList<EmailHeader>();
		Enumeration<Header> headerEnum = msg.getAllHeaders();
		
		if (headerEnum != null) {	
			while (headerEnum.hasMoreElements()) {
				Header header = headerEnum.nextElement();
				headerList.add(new EmailHeader(header.getName(), header.getValue()));
			}
		}
		
		return headerList;
	}
	
	public static List<EmailHeader> getMatchingHeaders(List<EmailHeader> headers, List<String> matchingHeaderNames) {
		return headers.stream().filter(header -> matchingHeaderNames.contains(header.getName())).collect(Collectors.toList());
	}
	
	public static List<EmailHeader> getNotMatchingHeaders(List<EmailHeader> headers, List<String> excludedHeaderNames) {
		return headers.stream().filter(header -> !excludedHeaderNames.contains(header.getName())).collect(Collectors.toList());
	}
	
}
