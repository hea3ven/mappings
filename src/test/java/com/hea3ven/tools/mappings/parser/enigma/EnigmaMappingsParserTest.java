package com.hea3ven.tools.mappings.parser.enigma;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Ignore;
import org.junit.Test;

import com.hea3ven.tools.mappings.ArgMapping;
import com.hea3ven.tools.mappings.BuiltInTypeDesc;
import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.ClsTypeDesc;
import com.hea3ven.tools.mappings.Desc;
import com.hea3ven.tools.mappings.FldMapping;
import com.hea3ven.tools.mappings.Mapping;
import com.hea3ven.tools.mappings.MthdMapping;

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

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
	}

	@Test
	public void parse_twoClss_parsesTheClss() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\nCLASS e/f g/h");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new ClsMapping("e/f", "g/h"), mapping.getCls("e/f"));
	}

	@Test
	public void parse_nestedClss_parsesTheClss() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\nCLASS a/b$e f");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"),
				mapping.getCls("a/b$e"));
	}

	@Test
	public void parse_nestedClssWithoutParent_createsParent() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b$c d");

		assertEquals("mapping parsed", new ClsMapping("a/b", null), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new ClsMapping(new ClsMapping("a/b", null), "c", "d"),
				mapping.getCls("a/b$c"));
	}

	@Test
	public void parse_field_parsesTheFld() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n FIELD e f Lg/h;");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new FldMapping(new ClsMapping("a/b", "c/d"), "e", "f",
				new Desc(new ClsTypeDesc(new ClsMapping("g/h", null)))), mapping.getFld("a/b/e"));
	}

	@Test
	public void parse_nestedClsFld_parsesTheFld() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n CLASS a/b$e f\n  FIELD g h I");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed",
				new FldMapping(new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"), "g", "h",
						new Desc(BuiltInTypeDesc.INTEGER)), mapping.getFld("a/b$e/g"));
	}

	@Test
	public void parse_nestedClsFldFollowedByFld_parsesTheFlds() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n CLASS a/b$e f\n  FIELD g h I\n FIELD i j I");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed",
				new FldMapping(new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"), "g", "h",
						new Desc(BuiltInTypeDesc.INTEGER)), mapping.getFld("a/b$e/g"));
		assertEquals("mapping parsed",
				new FldMapping(new ClsMapping("a/b", "c/d"), "i", "j", new Desc(BuiltInTypeDesc.INTEGER)),
				mapping.getFld("a/b/i"));
	}

	@Test
	public void parse_method_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n METHOD e f (IF)V");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new MthdMapping(new ClsMapping("a/b", "c/d"), "e", "f",
						new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b/e", "(IF)V"));
	}

	@Test
	public void parse_nestedClsMthd_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n CLASS a/b$e f\n  METHOD g h (IF)V");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed",
				new MthdMapping(new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"), "g", "h",
						new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b$e/g", "(IF)V"));
	}

	@Test
	public void parse_nestedClsMthdFollowedByMthd_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping =
				parse(parser, "CLASS a/b c/d\n CLASS a/b$e f\n  METHOD g h (IF)V\n METHOD i j (FI)V");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed",
				new MthdMapping(new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"), "g", "h",
						new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b$e/g", "(IF)V"));
		assertEquals("mapping parsed", new MthdMapping(new ClsMapping("a/b", "c/d"), "i", "j",
						new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.FLOAT, BuiltInTypeDesc.INTEGER)),
				mapping.getMthd("a/b/i", "(FI)V"));
	}

	@Test
	public void parse_methodWithTypesInDesc_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n METHOD e f (ILa/b;)Ljava/lang/String;");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new MthdMapping(new ClsMapping("a/b", "c/d"), "e", "f",
						new Desc(new ClsTypeDesc(new ClsMapping("java/lang/String", null)), BuiltInTypeDesc.INTEGER,
								new ClsTypeDesc(new ClsMapping("a/b", "c/d")))),
				mapping.getMthd("a/b/e", "(ILa/b;)Ljava/lang/String;"));
	}

	@Test
	@Ignore
	public void parse_argument_parsesTheArgument() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n METHOD e f (IF)V\n  ARG 0 g");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		MthdMapping mthdExpected = new MthdMapping(new ClsMapping("a/b", "c/d"), "e", "f",
				new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT));
		assertEquals("mapping parsed", mthdExpected, mapping.getMthd("a/b/e", "(IF)V"));
		assertEquals("mapping parsed", new ArgMapping(mthdExpected, 0, "g"), mapping.getArg("a/b/e@0"));
	}

	@Test
	public void parse_ClsWithNoPkg_parsesTheCls() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS none/a b/c\nCLASS d/e none/f");

		assertEquals("mapping parsed", new ClsMapping("a", "b/c"), mapping.getCls("a"));
		assertEquals("mapping parsed", new ClsMapping("d/e", "f"), mapping.getCls("d/e"));
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
		mapping.addMthd("a/b/e", "c/d/f", "(F)La/b;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tMETHOD e f (F)La/b;\n", sw.toString());
	}

	@Test
	public void write_MthdWithNoParams() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addMthd("a/b/e", "c/d/f", "()La/b;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tMETHOD e f ()La/b;\n", sw.toString());
	}

	@Test
	@Ignore
	public void write_Fld() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addFld("a/b/e", "c/d/f");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tFIELD e f La/b;\n", sw.toString());
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
	@Ignore
	public void write_InnerClsInDesc() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addCls("a/b$e", "c/d$f");
		mapping.addFld("a/b$e/g", "c/d$f/h");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tCLASS a/b$e f\n\tFIELD g h La/b$e;\n", sw.toString());
	}

	@Test
	@Ignore
	public void write_InnerClsInDescWithNoPkg() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a", "b/c");
		mapping.addCls("a$d", "b/c$e");
		mapping.addFld("a$d/f", "b/c$e/g");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS none/a b/c\n\tCLASS none/a$d e\n\tFIELD f g Lnone/a$d;\n", sw.toString());
	}

	@Test
	@Ignore
	public void write_ArrayInDescWithNoPkg() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a", "b/c");
		mapping.addFld("a/d", "b/c/e");

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
		mapping.addMthd("a/b$e/g", "c/d$f/h", "(F)La/b;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS a/b c/d\n\tCLASS a/b$e f\n\t\tMETHOD g h (F)La/b;\n", sw.toString());
	}

	@Test
	@Ignore
	public void write_InnerFld() throws IOException {
		Mapping mapping = new Mapping();
		mapping.addCls("a/b", "c/d");
		mapping.addCls("a/b$e", "c/d$f");
		mapping.addFld("a/b$e/g", "c/d$f/h");

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
		mapping.addMthd("a/d", "b/c/e", "(La;)La;");

		StringWriter sw = new StringWriter();
		EnigmaMappingsParser parser = new EnigmaMappingsParser(mapping);
		parser.write(sw);

		assertEquals("CLASS none/a b/c\n\tMETHOD d e (Lnone/a;)Lnone/a;\n", sw.toString());
	}
}
