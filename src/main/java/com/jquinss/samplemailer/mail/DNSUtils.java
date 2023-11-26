package com.jquinss.samplemailer.mail;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DNSUtils {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&�*+/=?`{|}~^.-]+@(?<domain>[a-zA-Z0-9.-]+)$";
    private DNSUtils() {}

    public static List<Record> resolveDomain(String domainName, int recordType) throws TextParseException {
        List<Record> records = new ArrayList<>();
        Lookup lookup = new Lookup(domainName, recordType);
        Record[] resolvedRecords = lookup.run();
        if (resolvedRecords != null) {
            Collections.addAll(records, resolvedRecords);
        }

        return records;
    }

    public static List<String> getUniqueDomainsFromEmailRecipients(Collection<List<String>> emailRecipientLists) {
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
        List<String> uniqueDomains = new ArrayList<>();
        for (List<String> emailRecipients : emailRecipientLists) {
            emailRecipients.forEach(recipient -> {
                Matcher rcptMatcher = emailPattern.matcher(recipient);
                if (rcptMatcher.matches() && !uniqueDomains.contains(rcptMatcher.group("domain"))){
                    uniqueDomains.add(rcptMatcher.group("domain"));
                }
            });
        }
        return uniqueDomains;
    }

    public static HashMap<String, String> buildDomainToMXServerMap(HashMap<String, List<MXRecord>> domainToMXRecordsMap) {
        HashMap<String, String> domainToMXServerMap = new HashMap<>();
        for (String domain : domainToMXRecordsMap.keySet()) {
            List<MXRecord> mxRecords = domainToMXRecordsMap.get(domain);
            // order records by priority and extract server name of the first record
            mxRecords.sort(new MXRecordComparator());
            MXRecord mxRecord = mxRecords.get(0);
            String server = mxRecord.getTarget().toString();
            // remove trailing dot
            server = server.substring(0, server.length() - 1);
            domainToMXServerMap.put(domain, server);
        }

        return domainToMXServerMap;
    }
}
