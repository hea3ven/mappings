package com.hea3ven.tools.mappings.parser;

import java.io.*;

import com.hea3ven.tools.mappings.Mapping;

public interface IMappingsParser {
	Mapping getMapping();

	void setMapping(Mapping mapping);

	void parse(InputStream stream) throws IOException;

	void parse(Reader reader) throws IOException;

	void write(OutputStream stream) throws IOException;

	void write(Writer writer) throws IOException;
}
