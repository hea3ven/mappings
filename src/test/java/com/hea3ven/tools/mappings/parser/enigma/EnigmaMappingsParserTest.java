package com.hea3ven.tools.mappings.parser.enigma;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import com.google.common.collect.ImmutableMap;
import org.junit.Ignore;
import org.junit.Test;

import com.hea3ven.tools.mappings.*;
import static org.junit.Assert.assertEquals;

import static com.hea3ven.tools.mappings.parser.MappingTestUtils.*;

public class EnigmaMappingsParserTest {

	private Reader getReader(String data) {
		return new StringReader(data);
	}

	private Mapping parse(EnigmaMappingsParser parser, String data) {
		try {
			parser.parse(getReader(data));
			return parser.getMapping();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void parse_aSingleCls_parsesTheCls() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
	}

	@Test
	public void parse_twoClss_parsesTheClss() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\nCLASS e/f g/h");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(pkg("e", "g"), "f", "h"), mapping.getCls("e/f", ObfLevel.OBF));
	}

	@Test
	public void parse_nestedClss_parsesTheClss() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\nCLASS a/b$e f");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(cls(pkg("a", "c"), "b", "d"), "e", "f"),
				mapping.getCls("a/b$e", ObfLevel.OBF));
	}

	@Test
	public void parse_nestedClssWithoutParent_createsParent() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b$c d");

		assertEquals("mapping parsed", cls(pkg("a"), "b"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(cls(pkg("a"), "b"), "c", "d"),
				mapping.getCls("a/b$c", ObfLevel.OBF));
	}

	@Test
	public void parse_field_parsesTheFld() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n FIELD e f Lg/h;");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed",
				fld(cls(pkg("a", "c"), "b", "d"), "e", "f", new ClsTypeDesc(cls(pkg("g"), "h"))),
				mapping.getFld("a/b.e", ObfLevel.OBF));
	}

	@Test
	public void parse_nestedClsFld_parsesTheFld() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n CLASS a/b$e f\n  FIELD g h I");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(cls(pkg("a", "c"), "b", "d"), "e", "f"),
				mapping.getCls("a/b$e", ObfLevel.OBF));
		assertEquals("mapping parsed",
				fld(cls(cls(pkg("a", "c"), "b", "d"), "e", "f"), "g", "h", BuiltInTypeDesc.INTEGER),
				mapping.getFld("a/b$e.g", ObfLevel.OBF));
	}

	@Test
	public void parse_nestedClsFldFollowedByFld_parsesTheFlds() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n CLASS a/b$e f\n  FIELD g h I\n FIELD i j I");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(cls(pkg("a", "c"), "b", "d"), "e", "f"),
				mapping.getCls("a/b$e", ObfLevel.OBF));
		assertEquals("mapping parsed",
				fld(cls(cls(pkg("a", "c"), "b", "d"), "e", "f"), "g", "h", BuiltInTypeDesc.INTEGER),
				mapping.getFld("a/b$e.g", ObfLevel.OBF));
		assertEquals("mapping parsed", fld(cls(pkg("a", "c"), "b", "d"), "i", "j", BuiltInTypeDesc.INTEGER),
				mapping.getFld("a/b.i", ObfLevel.OBF));
	}

	@Test
	public void parse_method_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n METHOD e f (IF)V");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", mthd(cls(pkg("a", "c"), "b", "d"), "e", "f",
				new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b.e", "(IF)V", ObfLevel.OBF));
	}

	@Test
	public void parse_nestedClsMthd_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n CLASS a/b$e f\n  METHOD g h (IF)V");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(cls(pkg("a", "c"), "b", "d"), "e", "f"),
				mapping.getCls("a/b$e", ObfLevel.OBF));
		assertEquals("mapping parsed", mthd(cls(cls(pkg("a", "c"), "b", "d"), "e", "f"), "g", "h",
				new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b$e.g", "(IF)V", ObfLevel.OBF));
	}

	@Test
	public void parse_nestedClsMthdFollowedByMthd_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping =
				parse(parser, "CLASS a/b c/d\n CLASS a/b$e f\n  METHOD g h (IF)V\n METHOD i j (FI)V");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(cls(pkg("a", "c"), "b", "d"), "e", "f"),
				mapping.getCls("a/b$e", ObfLevel.OBF));
		assertEquals("mapping parsed", mthd(cls(cls(pkg("a", "c"), "b", "d"), "e", "f"), "g", "h",
				new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b$e.g", "(IF)V", ObfLevel.OBF));
		assertEquals("mapping parsed", mthd(cls(pkg("a", "c"), "b", "d"), "i", "j",
				new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.FLOAT, BuiltInTypeDesc.INTEGER)),
				mapping.getMthd("a/b.i", "(FI)V", ObfLevel.OBF));
	}

	@Test
	public void parse_methodWithTypesInDesc_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n METHOD e f (ILa/b;)Ljava/lang/String;");

		assertEquals("mapping parsed", cls(pkg("a", "c"), "b", "d"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals("mapping parsed", mthd(cls(pkg("a", "c"), "b", "d"), "e", "f",
				new Desc(new ClsTypeDesc(cls(pkg("java/lang"), "String")), BuiltInTypeDesc.INTEGER,
						new ClsTypeDesc(cls(pkg("a", "c"), "b", "d")))),
				mapping.getMthd("a/b.e", "(ILa/b;)Ljava/lang/String;", ObfLevel.OBF));
	}

	@Test
	@Ignore
	public void parse_argument_parsesTheArgument() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n METHOD e f (IF)V\n  ARG 0 g");

//		assertEquals("mapping parsed", new ClsMapping(ImmutableMap.of(ObfLevel.OBF, Path.parse("a/b"), ObfLevel.DEOBF, Path.parse(
//				"c/d"))), mapping.getCls("a/b", ObfLevel.OBF));
//		MthdMapping mthdExpected = new MthdMapping(new ClsMapping(ImmutableMap.of(ObfLevel.OBF, Path.parse(
//				"a/b"), ObfLevel.DEOBF, Path.parse("c/d"))), "e", "f",
//				new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT));
//		assertEquals("mapping parsed", mthdExpected, mapping.getMthd("a/b/e", "(IF)V"));
//		assertEquals("mapping parsed", new ArgMapping(mthdExpected, 0, "g"), mapping.getArg("a/b/e@0"));
	}

	@Test
	public void parse_ClsWithNoPkg_parsesTheCls() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS none/a b/c\nCLASS d/e none/f");

		assertEquals("mapping parsed", cls(new PkgMapping(ImmutableMap.of(ObfLevel.DEOBF, "b")), "a", "c"),
				mapping.getCls("a", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(new PkgMapping(ImmutableMap.of(ObfLevel.DEOBF, "b")), "a", "c"),
				mapping.getCls("b/c", ObfLevel.DEOBF));
		assertEquals("mapping parsed", cls(new PkgMapping(ImmutableMap.of(ObfLevel.OBF, "d")), "e", "f"),
				mapping.getCls("d/e", ObfLevel.OBF));
		assertEquals("mapping parsed", cls(new PkgMapping(ImmutableMap.of(ObfLevel.OBF, "d")), "e", "f"),
				mapping.getCls("f", ObfLevel.DEOBF));
	}

	@Test
	public void write_Cls() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n", sw.toString());
	}

	@Test
	public void write_ClsSorted() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("e/f", "g/h");
		mapping.addCls("a/b", "c/d");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\nCLASS e/f g/h\n", sw.toString());
	}

	@Test
	public void write_Mthd() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addMthd("a/b.e", "c/d.f", ObfLevel.OBF, "(F)La/b;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tMETHOD e f (F)La/b;\n", sw.toString());
	}

	@Test
	public void write_MthdWithNoParams() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addMthd("a/b.e", "c/d.f", ObfLevel.OBF, "()La/b;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tMETHOD e f ()La/b;\n", sw.toString());
	}

	@Test
	public void write_Fld() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addFld("a/b.e", "c/d.f", ObfLevel.OBF, "La/b;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tFIELD e f La/b;\n", sw.toString());
	}

	@Test
	public void write_Fld_WithNoDesc() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addFld("a/b.e", "c/d.f", ObfLevel.OBF, "F");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tFIELD e f F\n", sw.toString());
	}

	@Test
	public void write_InnerCls() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addCls("a/b$e", "c/d$f");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tCLASS a/b$e f\n", sw.toString());
	}

	@Test
	public void write_InnerClsInDesc() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addCls("a/b$e", "c/d$f");
		mapping.addFld("a/b$e.g", "c/d$f.h", ObfLevel.OBF, "La/b$e;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tCLASS a/b$e f\n\t\tFIELD g h La/b$e;\n", sw.toString());
	}

	@Test
	public void write_InnerClsInDescWithNoPkg() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a", "b/c");
		mapping.addCls("a$d", "b/c$e");
		mapping.addFld("a$d.f", "b/c$e.g", ObfLevel.OBF, "La$d;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS none/a b/c\n\tCLASS none/a$d e\n\t\tFIELD f g Lnone/a$d;\n", sw.toString());
	}

	@Test
	public void write_ArrayInDescWithNoPkg() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a", "b/c");
		mapping.addFld("a.d", "b/c.e", ObfLevel.OBF, "[La;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS none/a b/c\n\tFIELD d e [Lnone/a;\n", sw.toString());
	}

	@Test
	public void write_InnerMthd() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addCls("a/b$e", "c/d$f");
		mapping.addMthd("a/b$e.g", "c/d$f.h", ObfLevel.OBF, "(F)La/b;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tCLASS a/b$e f\n\t\tMETHOD g h (F)La/b;\n", sw.toString());
	}

	@Test
	public void write_InnerFld() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addCls("a/b$e", "c/d$f");
		mapping.addFld("a/b$e.g", "c/d$f.h", ObfLevel.OBF, "La/b;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tCLASS a/b$e f\n\t\tFIELD g h La/b;\n", sw.toString());
	}

	@Test
	public void write_ClsNoPkg() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a", "b/c");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS none/a b/c\n", sw.toString());
	}

	@Test
	public void write_InnerClsNoPkg() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a", "b/c");
		mapping.addCls("a$d", "b/c$e");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS none/a b/c\n\tCLASS none/a$d e\n", sw.toString());
	}

	@Test
	public void write_DescNoPkg() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a", "b/c");
		mapping.addMthd("a.d", "b/c.e", ObfLevel.OBF, "(La;)La;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS none/a b/c\n\tMETHOD d e (Lnone/a;)Lnone/a;\n", sw.toString());
	}
}
