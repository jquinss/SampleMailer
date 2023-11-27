package com.jquinss.samplemailer.mail;

import org.xbill.DNS.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DNSResolutionResults {
    private HashMap<String, List<Record>> domainToDNSRecordsMap = new HashMap<>();
    private List<String> unresolvedDomains = new ArrayList<>();

    public void addDomainToDNSRecord(String domainName, List<Record> records) {
        domainToDNSRecordsMap.put(domainName, records);
    }

    public void addUnresolvedDomain(String domainName) {
        unresolvedDomains.add(domainName);
    }

    public HashMap<String, List<Record>> getDomainToDNSRecordsMap() {
        return domainToDNSRecordsMap;
    }

    public List<String> getUnresolvedDomains() {
        return unresolvedDomains;
    }
}
