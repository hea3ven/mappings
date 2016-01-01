package com.hea3ven.tools.mappings.parser.srg;

import java.io.*;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import com.hea3ven.tools.mappings.*;

public class SrgMappingsParser {
	private Mapping mapping = new Mapping();

	private Splitter splitter = Splitter.on(CharMatcher.WHITESPACE);

	public Mapping add(InputStream stream) throws IOException {
		return add(new InputStreamReader(stream));
	}

	public Mapping add(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			parseLine(line);
		}
		return mapping;
	}

	private void parseLine(String line) {
		String type = line.substring(0, line.indexOf(':'));
		String value = line.substring(line.indexOf(':') + 1).trim();
		if (type.equals("CL")) {
			String[] parts = Iterables.toArray(splitter.split(value), String.class);
//			ClsMapping cls = mapping.getCls("asd");
//			cls.setDst((!parts[1].contains("$")) ? parts[1] : parts[1].split("\\$")[1]);
			mapping.addCls(parts[0], parts[1]);
		} else if (type.equals("FD")) {
			String[] parts = Iterables.toArray(splitter.split(value), String.class);
			mapping.addFld(parts[0], parts[1]);
		} else if (type.equals("MD")) {
			String[] parts = Iterables.toArray(splitter.split(value), String.class);
			mapping.addMthd(parts[0], parts[2], parts[1]);
		}
	}
}
