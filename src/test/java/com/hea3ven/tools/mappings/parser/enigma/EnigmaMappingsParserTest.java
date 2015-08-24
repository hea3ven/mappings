package com.hea3ven.tools.mappings.parser.enigma;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;

import com.hea3ven.tools.mappings.ArgMapping;
import com.hea3ven.tools.mappings.ArrayTypeDesc;
import com.hea3ven.tools.mappings.BuiltInTypeDesc;
import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.ClsTypeDesc;
import com.hea3ven.tools.mappings.Desc;
import com.hea3ven.tools.mappings.FldMapping;
import com.hea3ven.tools.mappings.Mapping;
import com.hea3ven.tools.mappings.MthdMapping;
import com.hea3ven.tools.mappings.parser.enigma.EnigmaMappingsParser;

public class EnigmaMappingsParserTest {

	private Reader getReader(String data) {
		return new StringReader(data);
	}

	private Mapping parse(EnigmaMappingsParser parser, String data) {
		try {
			return parser.add(getReader(data));
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
		assertEquals("mapping parsed",
				new FldMapping(new ClsMapping("a/b", "c/d"), "e", "f",
						new Desc(new ClsTypeDesc(new ClsMapping("g/h", null)))),
				mapping.getFld("a/b/e"));
	}

	@Test
	public void parse_nestedClsFld_parsesTheFld() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n CLASS a/b$e f\n  FIELD g h I");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed",
				new FldMapping(new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"), "g", "h",
						new Desc(BuiltInTypeDesc.INTEGER)),
				mapping.getFld("a/b$e/g"));
	}

	@Test
	public void parse_nestedClsFldFollowedByFld_parsesTheFlds() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser,
				"CLASS a/b c/d\n CLASS a/b$e f\n  FIELD g h I\n FIELD i j I");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed",
				new FldMapping(new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"), "g", "h",
						new Desc(BuiltInTypeDesc.INTEGER)),
				mapping.getFld("a/b$e/g"));
		assertEquals("mapping parsed", new FldMapping(new ClsMapping("a/b", "c/d"), "i", "j",
				new Desc(BuiltInTypeDesc.INTEGER)), mapping.getFld("a/b/i"));
	}

	@Test
	public void parse_method_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n METHOD e f (IF)V");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed",
				new MthdMapping(new ClsMapping("a/b", "c/d"), "e", "f", new Desc(
						BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b/e"));
	}

	@Test
	public void parse_nestedClsMthd_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n CLASS a/b$e f\n  METHOD g h (IF)V");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new MthdMapping(
				new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"), "g", "h",
				new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b$e/g"));
	}

	@Test
	public void parse_nestedClsMthdFollowedByMthd_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser,
				"CLASS a/b c/d\n CLASS a/b$e f\n  METHOD g h (IF)V\n METHOD i j (FI)V");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new MthdMapping(
				new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f"), "g", "h",
				new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT)),
				mapping.getMthd("a/b$e/g"));
		assertEquals("mapping parsed",
				new MthdMapping(new ClsMapping("a/b", "c/d"), "i", "j", new Desc(
						BuiltInTypeDesc.VOID, BuiltInTypeDesc.FLOAT, BuiltInTypeDesc.INTEGER)),
				mapping.getMthd("a/b/i"));
	}

	@Test
	public void parse_methodWithTypesInDesc_parsesTheMethod() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n METHOD e f (ILa/b;)Ljava/lang/String;");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new MthdMapping(new ClsMapping("a/b", "c/d"), "e", "f",
				new Desc(new ClsTypeDesc(new ClsMapping("java/lang/String", null)),
						BuiltInTypeDesc.INTEGER, new ClsTypeDesc(new ClsMapping("a/b", "c/d")))),
				mapping.getMthd("a/b/e"));
	}

	@Test
	public void parse_argument_parsesTheArgument() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n METHOD e f (IF)V\n  ARG 0 g");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		MthdMapping mthdExpected = new MthdMapping(new ClsMapping("a/b", "c/d"), "e", "f",
				new Desc(BuiltInTypeDesc.VOID, BuiltInTypeDesc.INTEGER, BuiltInTypeDesc.FLOAT));
		assertEquals("mapping parsed", mthdExpected, mapping.getMthd("a/b/e"));
		assertEquals("mapping parsed", new ArgMapping(mthdExpected, 0, "g"),
				mapping.getArg("a/b/e@0"));
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
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a/b", "c/d");
		mapping.add(clsMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS a/b c/d\n", sw.toString());
	}

	@Test
	public void write_ClsSorted() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("e/f", "g/h");
		mapping.add(clsMap);
		clsMap = new ClsMapping("a/b", "c/d");
		mapping.add(clsMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS a/b c/d\nCLASS e/f g/h\n", sw.toString());
	}

	@Test
	public void write_Mthd() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a/b", "c/d");
		mapping.add(clsMap);
		MthdMapping mthdMap = new MthdMapping(clsMap, "e", "f",
				new Desc(new ClsTypeDesc(clsMap), BuiltInTypeDesc.FLOAT));
		mapping.add(mthdMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS a/b c/d\n\tMETHOD e f (F)La/b;\n", sw.toString());
	}

	@Test
	public void write_MthdWithNoParams() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a/b", "c/d");
		mapping.add(clsMap);
		MthdMapping mthdMap = new MthdMapping(clsMap, "e", "f", new Desc(new ClsTypeDesc(clsMap)));
		mapping.add(mthdMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS a/b c/d\n\tMETHOD e f ()La/b;\n", sw.toString());
	}

	@Test
	public void write_Fld() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a/b", "c/d");
		mapping.add(clsMap);
		FldMapping fldMap = new FldMapping(clsMap, "e", "f", new Desc(new ClsTypeDesc(clsMap)));
		mapping.add(fldMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS a/b c/d\n\tFIELD e f La/b;\n", sw.toString());
	}

	@Test
	public void write_InnerCls() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a/b", "c/d");
		mapping.add(clsMap);
		ClsMapping innerClsMap = new ClsMapping(clsMap, "e", "f");
		mapping.add(innerClsMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS a/b c/d\n\tCLASS a/b$e f\n", sw.toString());
	}

	@Test
	public void write_InnerClsInDesc() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a/b", "c/d");
		mapping.add(clsMap);
		ClsMapping innerClsMap = new ClsMapping(clsMap, "e", "f");
		mapping.add(innerClsMap);
		FldMapping fldMap = new FldMapping(clsMap, "g", "h",
				new Desc(new ClsTypeDesc(innerClsMap)));
		mapping.add(fldMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS a/b c/d\n\tCLASS a/b$e f\n\tFIELD g h La/b$e;\n", sw.toString());
	}

	@Test
	public void write_InnerClsInDescWithNoPkg() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a", "b/c");
		mapping.add(clsMap);
		ClsMapping innerClsMap = new ClsMapping(clsMap, "d", "e");
		mapping.add(innerClsMap);
		FldMapping fldMap = new FldMapping(clsMap, "f", "g",
				new Desc(new ClsTypeDesc(innerClsMap)));
		mapping.add(fldMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS none/a b/c\n\tCLASS none/a$d e\n\tFIELD f g Lnone/a$d;\n", sw.toString());
	}

	@Test
	public void write_ArrayInDescWithNoPkg() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a", "b/c");
		mapping.add(clsMap);
		FldMapping fldMap = new FldMapping(clsMap, "d", "e", new Desc(new ArrayTypeDesc(new ClsTypeDesc(clsMap))));
		mapping.add(fldMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS none/a b/c\n\tFIELD d e [Lnone/a;\n", sw.toString());
	}

	@Test
	public void write_InnerMthd() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a/b", "c/d");
		mapping.add(clsMap);
		ClsMapping innerClsMap = new ClsMapping(clsMap, "e", "f");
		mapping.add(innerClsMap);
		MthdMapping mthdMap = new MthdMapping(innerClsMap, "g", "h",
				new Desc(new ClsTypeDesc(clsMap), BuiltInTypeDesc.FLOAT));
		mapping.add(mthdMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS a/b c/d\n\tCLASS a/b$e f\n\t\tMETHOD g h (F)La/b;\n", sw.toString());
	}

	@Test
	public void write_InnerFld() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a/b", "c/d");
		mapping.add(clsMap);
		ClsMapping innerClsMap = new ClsMapping(clsMap, "e", "f");
		mapping.add(innerClsMap);
		FldMapping mthdMap = new FldMapping(innerClsMap, "g", "h",
				new Desc(new ClsTypeDesc(clsMap)));
		mapping.add(mthdMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS a/b c/d\n\tCLASS a/b$e f\n\t\tFIELD g h La/b;\n", sw.toString());
	}

	@Test
	public void write_ClsNoPkg() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a", "b/c");
		mapping.add(clsMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS none/a b/c\n", sw.toString());
	}

	@Test
	public void write_InnerClsNoPkg() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a", "b/c");
		mapping.add(clsMap);
		clsMap = new ClsMapping(clsMap, "d", "e");
		mapping.add(clsMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS none/a b/c\n\tCLASS none/a$d e\n", sw.toString());
	}

	@Test
	public void write_DescNoPkg() throws IOException {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();
		Mapping mapping = new Mapping();
		ClsMapping clsMap = new ClsMapping("a", "b/c");
		mapping.add(clsMap);
		MthdMapping mthdMap = new MthdMapping(clsMap, "d", "e",
				new Desc(new ClsTypeDesc(clsMap), new ClsTypeDesc(clsMap)));
		mapping.add(mthdMap);

		StringWriter sw = new StringWriter();
		parser.write(mapping, sw);

		assertEquals("CLASS none/a b/c\n\tMETHOD d e (Lnone/a;)Lnone/a;\n", sw.toString());
	}
}
