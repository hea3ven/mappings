package net.mcmt.mappings.parser.enigma;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import net.mcmt.mappings.ArgMapping;
import net.mcmt.mappings.BuiltInTypeDesc;
import net.mcmt.mappings.ClsMapping;
import net.mcmt.mappings.ClsTypeDesc;
import net.mcmt.mappings.Desc;
import net.mcmt.mappings.FldMapping;
import net.mcmt.mappings.Mapping;
import net.mcmt.mappings.MthdMapping;

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

		assertEquals("mapping parsed", new ClsMapping("a/b", "a/b"), mapping.getCls("a/b"));
		assertEquals("mapping parsed", new ClsMapping(new ClsMapping("a/b", "a/b"), "c", "d"),
				mapping.getCls("a/b$c"));
	}

	@Test
	public void parse_field_parsesTheFld() {
		EnigmaMappingsParser parser = new EnigmaMappingsParser();

		Mapping mapping = parse(parser, "CLASS a/b c/d\n FIELD e f Lg/h;");

		assertEquals("mapping parsed", new ClsMapping("a/b", "c/d"), mapping.getCls("a/b"));
		assertEquals("mapping parsed",
				new FldMapping(new ClsMapping("a/b", "c/d"), "e", "f",
						new Desc(new ClsTypeDesc(new ClsMapping("g/h", "g/h")))),
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
				new Desc(new ClsTypeDesc(new ClsMapping("java/lang/String", "java/lang/String")),
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

}
