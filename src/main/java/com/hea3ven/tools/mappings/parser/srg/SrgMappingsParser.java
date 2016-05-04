package com.hea3ven.tools.mappings.parser.srg;

import java.io.*;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import com.hea3ven.tools.mappings.Mapping;
import com.hea3ven.tools.mappings.parser.IMappingsParser;

public class SrgMappingsParser implements IMappingsParser {
	private Mapping mapping = new Mapping();

	private Splitter splitter = Splitter.on(CharMatcher.WHITESPACE);

	public SrgMappingsParser() {
		this(new Mapping());
	}

	public SrgMappingsParser(Mapping mapping) {
		this.mapping = mapping;
	}

	@Override
	public Mapping getMapping() {
		return mapping;
	}

	@Override
	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	@Override
	public void parse(InputStream stream) throws IOException {
		parse(new InputStreamReader(stream));
	}

	@Override
	public void parse(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			parseLine(line);
		}
	}

	private void parseLine(String line) {
		String type = line.substring(0, line.indexOf(':'));
		String value = line.substring(line.indexOf(':') + 1).trim();
		switch (type) {
			case "CL": {
				String[] parts = Iterables.toArray(splitter.split(value), String.class);
				mapping.addCls(parts[0], parts[1]);
				break;
			}
			case "FD": {
				String[] parts = Iterables.toArray(splitter.split(value), String.class);
				mapping.addFld(parts[0], parts[1]);
				break;
			}
			case "MD": {
				String[] parts = Iterables.toArray(splitter.split(value), String.class);
				mapping.addMthd(parts[0], parts[2], parts[1]);
				break;
			}
		}
	}

	@Override
	public void write(OutputStream stream) throws IOException {
		write(new OutputStreamWriter(stream));
	}

	@Override
	public void write(Writer writer) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * @deprecated Use {@link #parse(InputStream)}.
	 */
	@Deprecated
	public Mapping add(InputStream stream) throws IOException {
		parse(stream);
		return getMapping();
	}

	/**
	 * @deprecated Use {@link #parse(Reader)}.
	 */
	@Deprecated
	public Mapping add(Reader reader) throws IOException {
		parse(reader);
		return getMapping();
	}
}
