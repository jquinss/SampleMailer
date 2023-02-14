package com.jquinss.samplemailer.mail;

import org.xbill.DNS.MXRecord;

import java.util.ArrayList;
import java.util.List;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DNSResolver {
	
	private DNSResolver() {}
	
	public static List<MXRecord> resolveMXRecords(String domainName) throws TextParseException {
		List<MXRecord> mxRecords = new ArrayList<MXRecord>();
		Lookup lookup = new Lookup(domainName, Type.MX);
		Record[] records = lookup.run();
		if (records != null) {
			for (Record record : records) {
				mxRecords.add((MXRecord) record);
			}
		}
		
		return mxRecords;
	}
}
