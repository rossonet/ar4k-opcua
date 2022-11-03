package org.rossonet.utils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public final class DnsHelper {

	public static String fromDnsRecord(final String hostPart, final String domainPart)
			throws TextParseException, UnknownHostException {
		final StringBuilder resultString = new StringBuilder();
		final Set<String> errors = new HashSet<>();
		final Lookup l = new Lookup(hostPart + "-max" + domainPart, Type.TXT, DClass.IN);
		l.setResolver(new SimpleResolver());
		l.run();
		if (l.getResult() == Lookup.SUCCESSFUL) {
			final int chunkSize = Integer
					.parseInt(l.getAnswers()[0].rdataToString().replaceAll("^\"", "").replaceAll("\"$", ""));
			if (chunkSize > 0) {
				for (int c = 0; c < chunkSize; c++) {
					final Lookup cl = new Lookup(hostPart + "-" + String.valueOf(c) + domainPart, Type.TXT, DClass.IN);
					cl.setResolver(new SimpleResolver());
					cl.run();
					if (cl.getResult() == Lookup.SUCCESSFUL) {
						resultString
								.append(cl.getAnswers()[0].rdataToString().replaceAll("^\"", "").replaceAll("\"$", ""));
					} else {
						errors.add("error in chunk " + hostPart + "-" + String.valueOf(c) + domainPart + " -> "
								+ cl.getErrorString());
					}
				}
			} else {
				errors.add("error, size of data is " + l.getAnswers()[0].rdataToString());
			}
		}
		return new String(Base64.getDecoder().decode(resultString.toString().getBytes()));
	}

	public static String toDnsRecord(final String name, final String payload) throws IOException {
		final Iterable<String> chunks = TextHelper
				.splitFixSize(Base64.getEncoder().encodeToString(payload.getBytes()), 254);
		final StringBuilder result = new StringBuilder();
		int counter = 0;
		for (final String s : chunks) {
			result.append(name + "-" + String.valueOf(counter) + "\tIN\tTXT\t" + '"' + s + '"' + "\n");
			counter++;
		}
		result.append(name + "-max" + "\tIN\tTXT\t" + '"' + String.valueOf(counter) + '"' + "\n");
		return result.toString();
	}

	private DnsHelper() {
		throw new UnsupportedOperationException("Just for static usage");

	}

}
