package com.hea3ven.tools.mappings.parser.srg;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import com.hea3ven.tools.mappings.*;
import static com.hea3ven.tools.mappings.parser.MappingTestUtils.*;
import static org.junit.Assert.assertEquals;

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

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
	}

	@Test
	public void parse_aSingleClsWithNoSrcPkg_parsesTheCls() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "CL: a b/c");

		assertEquals("mapping parsed", cls(new PkgMapping(ImmutableMap.of(ObfLevel.DEOBF, "b")), "a", "c"),
				mapping.getCls("a", ObfLevel.OBF));
	}

	@Test
	public void parse_twoClss_parsesTheClss() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "CL: a/b c/d\nCL: e/f g/h");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(pkg("e", "g"), "f", "h"), mapping.getCls("e/f", ObfLevel.OBF));
	}

	@Test
	public void parse_nestedClss_parsesTheClss() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "CL: a/b c/d\nCL: a/b$e c/d$f");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(cls(pkg("a", "c"), "b", "d"), "e", "f"),
				mapping.getCls("a/b$e", ObfLevel.OBF));
	}

	@Test
	public void parse_nestedClssWithoutParent_createsParent() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "CL: a/b$c a/b$d");

		assertEquals("mapping parsed", cls(pkg("a", "a"), "b", "b"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(cls(pkg("a", "a"), "b", "b"), "c", "d"),
				mapping.getCls("a/b$c", ObfLevel.OBF));
	}

	@Test
	public void parse_field_parsesTheFld() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "FD: a/b/e c/d/f");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", fld(cls(pkg("a", "c"), "b", "d"), "e", "f", BuiltInTypeDesc.BOOLEAN),
				mapping.getFld("a/b.e", ObfLevel.OBF));
	}

	@Test
	public void parse_nestedClsFld_parsesTheFld() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "FD: a/b$e/g c/d$f/h");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(cls(pkg("a", "c"), "b", "d"), "e", "f"),
				mapping.getCls("a/b$e", ObfLevel.OBF));
		assertEquals("mapping parsed",
				fld(cls(cls(pkg("a", "c"), "b", "d"), "e", "f"), "g", "h", BuiltInTypeDesc.BOOLEAN),
				mapping.getFld("a/b$e.g", ObfLevel.OBF));
	}

	@Test
	public void parse_fieldWithDollarSign_parsesTheFld() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "FD: a/b/e c/d/$VALUES");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed",
				fld(cls(pkg("a", "c"), "b", "d"), "e", "$VALUES", BuiltInTypeDesc.BOOLEAN),
				mapping.getFld("a/b.e", ObfLevel.OBF));
	}

	@Test
	public void parse_method_parsesTheMethod() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "MD: a/b/e (IF)V c/d/f (IF)V");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", mthd(cls(pkg("a", "c"), "b", "d"), "e", "f",
				new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b.e", "(IF)V", ObfLevel.OBF));
	}

	@Test
	public void parse_nestedClsMthd_parsesTheMethod() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping = parse(parser, "MD: a/b$e/g (IF)V c/d$f/h (IF)V");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(cls(pkg("a", "c"), "b", "d"), "e", "f"),
				mapping.getCls("a/b$e", ObfLevel.OBF));
		assertEquals("mapping parsed", mthd(cls(cls(pkg("a", "c"), "b", "d"), "e", "f"), "g", "h",
				new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b$e.g", "(IF)V", ObfLevel.OBF));
	}

	@Test
	public void parse_methodWithTypesInDesc_parsesTheMethod() {
		SrgMappingsParser parser = new SrgMappingsParser();

		Mapping mapping =
				parse(parser, "MD: a/b/e (ILa/b;)Ljava/lang/String; c/d/f (ILc/d;)Ljava/lang/String;");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", mthd(cls(pkg("a", "c"), "b", "d"), "e", "f",
				new Desc(new ClsTypeDesc(cls(pkg("java/lang"), "String")), BuiltInTypeDesc.INTEGER,
						new ClsTypeDesc(cls(pkg("a", "c"), "b", "d")))),
				mapping.getMthd("a/b.e", "(ILa/b;)Ljava/lang/String;", ObfLevel.OBF));
	}
}
