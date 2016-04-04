package com.hea3ven.tools.mappings.parser.srg;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import com.hea3ven.tools.mappings.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SrgMappingsParserTest {

	private Reader getReader(String data) {
		return new StringReader(data);
	}

	private Mapping parse(SrgMappingsParser parser, String data) {
		try {
			parser.parse(getReader(data));
			return parser.getMapping();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void parse_aSingleCls_parsesTheCls() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "CL: a/b c/d");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
	}

	@Test
	public void parse_twoClss_parsesTheClss() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "CL: a/b c/d\nCL: e/f g/h");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new ClsMapping("e/f", "g/h"), mapping.getCls("e/f"));
	}

	@Test
	public void parse_nestedClss_parsesTheClss() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "CL: a/b c/d\nCL: a/b$e c/d$f");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"),
				mapping.getCls("a/b$e"));
	}

	@Test
	public void parse_nestedClssWithoutParent_createsParent() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "CL: a/b$c a/b$d");

		assertEquals("mapping parsed", new ClsMapping("a/b", "a/b"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new ClsMapping(new ClsMapping("a/b", "a/b"), "c", "d"),
				mapping.getCls("a/b$c"));
	}

	@Test
	public void parse_field_parsesTheFld() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "FD: a/b/e c/d/f");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new FldMapping(new ClsMapping("a/b", "c/d"), "e", "f", null),
				mapping.getFld("a/b/e"));
	}

	@Test
	public void parse_nestedClsFld_parsesTheFld() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "FD: a/b$e/g c/d$f/h");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed",
				new FldMapping(new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"), "g", "h", null),
				mapping.getFld("a/b$e/g"));
	}

	@Test
	public void parse_fieldWithDollarSign_parsesTheFld() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "FD: a/b/e c/d/$VALUES");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new FldMapping(new ClsMapping("a/b", "c/d"), "e", "$VALUES", null),
				mapping.getFld("a/b/e"));
	}

	@Test
	public void parse_method_parsesTheMethod() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "MD: a/b/e (IF)V c/d/f (IF)V");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new MthdMapping(new ClsMapping("a/b", "c/d"), "e", "f",
						new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b/e", "(IF)V"));
	}

	@Test
	public void parse_nestedClsMthd_parsesTheMethod() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "MD: a/b$e/g (IF)V c/d$f/h (IF)V");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed",
				new MthdMapping(new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"), "g", "h",
						new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b$e/g", "(IF)V"));
	}

	@Test
	public void parse_methodWithTypesInDesc_parsesTheMethod() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping =
				parse(parser, "MD: a/b/e (ILa/b;)Ljava/lang/String; c/d/f (ILc/d;)Ljava/lang/String;");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new MthdMapping(new ClsMapping("a/b", "c/d"), "e", "f",
						new Desc(new ClsTypeDesc(new ClsMapping("java/lang/String", null)), BuiltInTypeDesc.INTEGER,
								new ClsTypeDesc(new ClsMapping("a/b", "c/d")))),
				mapping.getMthd("a/b/e", "(ILa/b;)Ljava/lang/String;"));
	}
}
