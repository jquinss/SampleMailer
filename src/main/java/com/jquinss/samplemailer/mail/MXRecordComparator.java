package com.jquinss.samplemailer.mail;

import java.util.Comparator;

import org.xbill.DNS.MXRecord;

public class MXRecordComparator implements Comparator<MXRecord> {

	@Override
	public int compare(MXRecord record1, MXRecord record2) {
		return Integer.compare(record1.getPriority(), record2.getPriority());
	}
	
}
