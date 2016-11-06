package com.hea3ven.tools.mappings;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static com.hea3ven.tools.mappings.parser.MappingTestUtils.*;
import static org.junit.Assert.assertEquals;

public class MappingTest {

	private MthdMapping mthd(ClsMapping parent, String nameObf, String nameDeobf, Desc desc) {
		return new MthdMapping(parent, ImmutableMap.of(ObfLevel.OBF, nameObf, ObfLevel.DEOBF, nameDeobf),
				desc);
	}

	@Test
	public void addPkg_mappingWithOnePackageGetSrc_returnsThePackage() {
		Mapping map = new Mapping();
		map.addPkg("a/b/c", "d/e/f");

		assertEquals(pkg("a/b/c", "d/e/f"), map.getPkg("a/b/c", ObfLevel.OBF));
	}

	@Test
	public void addPkg_mappingWithOnePackageGetDst_returnsThePackage() {
		Mapping map = new Mapping();
		map.addPkg("a/b/c", "d/e/f");

		assertEquals(pkg("a/b/c", "d/e/f"), map.getPkg("d/e/f", ObfLevel.DEOBF));
	}

	@Test(expected = DuplicateMappingException.class)
	public void addPkg_packagesWithSameSource_throwsException() {
		Mapping map = new Mapping();
		map.addPkg("a/b", "c/d");
		map.addPkg("a/b", "e/f");
	}

	@Test(expected = DuplicateMappingException.class)
	public void addPkg_packagesWithDstEqDst_throwsException() {
		Mapping map = new Mapping();
		map.addPkg("a/b", "c/d");
		map.addPkg("e/f", "c/d");
	}

	@Test
	public void addPkg_updatingPkgWithoutDst_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addPkg(ImmutableMap.of(ObfLevel.OBF, "a/b"));

		assertEquals(new PkgMapping(ImmutableMap.of(ObfLevel.OBF, "a/b")), map.getPkg("a/b", ObfLevel.OBF));
		assertEquals(null, map.getPkg("c/d", ObfLevel.DEOBF));
		map.addPkg("a/b", "c/d");
		assertEquals(pkg("a/b", "c/d"), map.getPkg("a/b", ObfLevel.OBF));
		assertEquals(pkg("a/b", "c/d"), map.getPkg("c/d", ObfLevel.DEOBF));
	}

	@Test
	public void addCls_mappingWithOneClassGetSrc_returnsTheClass() {
		Mapping map = new Mapping();
		map.addCls("a/b", "c/d");

		assertEquals(cls(pkg("a", "c"), "b", "d"), map.getCls("a/b", ObfLevel.OBF));
	}

	@Test
	public void addCls_mappingWithTwoClassesGetSrc_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addCls("a/b", "c/d");
		map.addCls("e/f", "g/h");

		assertEquals(cls(pkg("a", "c"), "b", "d"), map.getCls("a/b", ObfLevel.OBF));
		assertEquals(cls(pkg("e", "g"), "f", "h"), map.getCls("e/f", ObfLevel.OBF));
	}

	@Test
	public void addCls_mappingWithOneClassGetDst_returnsTheClass() {
		Mapping map = new Mapping();
		map.addCls("a/b", "c/d");

		assertEquals(cls(pkg("a", "c"), "b", "d"), map.getCls("c/d", ObfLevel.DEOBF));
	}

	@Test
	public void addCls_mappingWithTwoClasses_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addCls("a/b", "c/d");
		map.addCls("e/f", "g/h");

		assertEquals(cls(pkg("a", "c"), "b", "d"), map.getCls("c/d", ObfLevel.DEOBF));
		assertEquals(cls(pkg("e", "g"), "f", "h"), map.getCls("g/h", ObfLevel.DEOBF));
	}

	@Test(expected = DuplicateMappingException.class)
	public void addCls_clasessWithSameSource_throwsException() {
		Mapping map = new Mapping();
		map.addCls("a/b", "c/d");
		map.addCls("a/b", "e/f");
	}

	@Test(expected = DuplicateMappingException.class)
	public void addCls_clasessWithDstEqDst_throwsException() {
		Mapping map = new Mapping();
		map.addCls("a/b", "c/d");
		map.addCls("e/f", "c/d");
	}

	@Test
	public void addCls_updatingClsWithoutDst_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addCls(ImmutableMap.of(ObfLevel.OBF, "a/b"));

		assertEquals(new ClsMapping(new PkgMapping(ImmutableMap.of(ObfLevel.OBF, "a")),
				ImmutableMap.of(ObfLevel.OBF, "b")), map.getCls("a/b", ObfLevel.OBF));
		assertEquals(null, map.getCls("c/d", ObfLevel.DEOBF));
		map.addCls("a/b", "c/d");
		assertEquals(cls(pkg("a", "c"), "b", "d"), map.getCls("a/b", ObfLevel.OBF));
		assertEquals(cls(pkg("a", "c"), "b", "d"), map.getCls("c/d", ObfLevel.DEOBF));
	}

	@Test
	public void addCls_updatingInnerClsWithoutDst_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addCls(ImmutableMap.of(ObfLevel.OBF, "a/b$c"));

		assertEquals(new ClsMapping(new ClsMapping(new PkgMapping(ImmutableMap.of(ObfLevel.OBF, "a")),
						ImmutableMap.of(ObfLevel.OBF, "b")), ImmutableMap.of(ObfLevel.OBF, "c")),
				map.getCls("a/b$c", ObfLevel.OBF));
		assertEquals(null, map.getCls("d/e$f", ObfLevel.DEOBF));
		map.addCls("a/b$c", "d/e$f");
		assertEquals(cls(cls(pkg("a", "d"), "b", "e"), "c", "f"), map.getCls("a/b$c", ObfLevel.OBF));
		assertEquals(cls(cls(pkg("a", "d"), "b", "e"), "c", "f"), map.getCls("d/e$f", ObfLevel.DEOBF));
	}

	@Test
	public void addCls_addInnerWithoutParent_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addCls("a/b$c", "d/e$f");

		assertEquals(cls(pkg("a", "d"), "b", "e"), map.getCls("a/b", ObfLevel.OBF));
		assertEquals(cls(pkg("a", "d"), "b", "e"), map.getCls("d/e", ObfLevel.DEOBF));
		assertEquals(cls(cls(pkg("a", "d"), "b", "e"), "c", "f"), map.getCls("a/b$c", ObfLevel.OBF));
		assertEquals(cls(cls(pkg("a", "d"), "b", "e"), "c", "f"), map.getCls("d/e$f", ObfLevel.DEOBF));
	}

	@Test
	public void getCls_DifferentInnerClasses_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addCls(ImmutableMap.of(ObfLevel.OBF, "a/b$1"));
		map.addCls(ImmutableMap.of(ObfLevel.OBF, "c/d$1"));

		assertEquals(new ClsMapping(new ClsMapping(new PkgMapping(ImmutableMap.of(ObfLevel.OBF, "a")),
						ImmutableMap.of(ObfLevel.OBF, "b")), ImmutableMap.of(ObfLevel.OBF, "1")),
				map.getCls("a/b$1", ObfLevel.OBF));
		assertEquals(new ClsMapping(new ClsMapping(new PkgMapping(ImmutableMap.of(ObfLevel.OBF, "c")),
						ImmutableMap.of(ObfLevel.OBF, "d")), ImmutableMap.of(ObfLevel.OBF, "1")),
				map.getCls("c/d$1", ObfLevel.OBF));
	}

	@Test
	public void addCls_innerClassWithoutParentMapping_returnsTheClass() {
		Mapping map = new Mapping();
		map.addCls("a/b$c", "$d");

		assertEquals(cls(cls(pkg("a"), "b"), "c", "d"), map.getCls("a/b$c", ObfLevel.OBF));
	}

	@Test
	public void addFld_addFld_returnsTheCorrectFld() {
		Mapping map = new Mapping();
		map.addFld("a/b.c", "d/e.f", ObfLevel.OBF, "I");
		map.addFld("g/h$i.j", "k/l$m.n", ObfLevel.OBF, "I");

		assertEquals(fld(cls(pkg("a", "d"), "b", "e"), "c", "f", BuiltInTypeDesc.INTEGER),
				map.getFld("a/b.c", ObfLevel.OBF));
		assertEquals(fld(cls(pkg("a", "d"), "b", "e"), "c", "f", BuiltInTypeDesc.INTEGER),
				map.getFld("d/e.f", ObfLevel.DEOBF));
		assertEquals(fld(cls(cls(pkg("g", "k"), "h", "l"), "i", "m"), "j", "n", BuiltInTypeDesc.INTEGER),
				map.getFld("g/h$i.j", ObfLevel.OBF));
		assertEquals(fld(cls(cls(pkg("g", "k"), "h", "l"), "i", "m"), "j", "n", BuiltInTypeDesc.INTEGER),
				map.getFld("k/l$m.n", ObfLevel.DEOBF));
	}

	@Test
	public void addFld_updatingFldWithoutDst_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addFld(ImmutableMap.of(ObfLevel.OBF, "a/b.c"), ObfLevel.OBF, "F");

		assertEquals(new FldMapping(new ClsMapping(new PkgMapping(ImmutableMap.of(ObfLevel.OBF, "a")),
				ImmutableMap.of(ObfLevel.OBF, "b")), ImmutableMap.of(ObfLevel.OBF, "c"),
				BuiltInTypeDesc.FLOAT), map.getFld("a/b.c", ObfLevel.OBF));
		assertEquals(null, map.getFld("d/e.f", ObfLevel.DEOBF));
		map.addFld("a/b.c", "d/e.f", ObfLevel.OBF, "F");
		assertEquals(fld(cls(pkg("a", "d"), "b", "e"), "c", "f", BuiltInTypeDesc.FLOAT),
				map.getFld("a/b.c", ObfLevel.OBF));
		assertEquals(fld(cls(pkg("a", "d"), "b", "e"), "c", "f", BuiltInTypeDesc.FLOAT),
				map.getFld("d/e.f", ObfLevel.DEOBF));
	}

	@Test(expected = DuplicateMappingException.class)
	public void addFld_fieldsWithSameSource_throwsException() {
		Mapping map = new Mapping();
		map.addFld("a/b.c", "d/e.f", ObfLevel.OBF, "F");
		map.addFld("a/b.c", "g/h.i", ObfLevel.OBF, "F");
	}

	@Test(expected = DuplicateMappingException.class)
	public void addFld_fieldsWithDstEqDst_throwsException() {
		Mapping map = new Mapping();
		map.addFld("a/b.c", "d/e.f", ObfLevel.OBF, "F");
		map.addFld("g/h.i", "d/e.f", ObfLevel.OBF, "F");
	}

	@Test(expected = IllegalArgumentException.class)
	public void addFld_fieldsPathWithoutPointSeparator_throwsException() {
		Mapping map = new Mapping();
		map.addFld("a/b/c", "d/e/f", ObfLevel.OBF, "F");
	}

	@Test
	public void addMthd_addMthd_returnsTheCorrectMthd() {
		Mapping map = new Mapping();
		map.addMthd("a/b.c", "d/e.f", ObfLevel.OBF, "()V");
		map.addMthd("g/h$i.j", "k/l$m.n", ObfLevel.OBF, "(La/b;)F");

		assertEquals(
				mthd(cls(pkg("a", "d"), "b", "e"), "c", "f", new Desc(BuiltInTypeDesc.VOID, new TypeDesc[0])),
				map.getMthd("a/b.c", "()V", ObfLevel.OBF));
		assertEquals(
				mthd(cls(pkg("a", "d"), "b", "e"), "c", "f", new Desc(BuiltInTypeDesc.VOID, new TypeDesc[0])),
				map.getMthd("d/e.f", "()V", ObfLevel.DEOBF));
		assertEquals(mthd(cls(cls(pkg("g", "k"), "h", "l"), "i", "m"), "j", "n",
				new Desc(BuiltInTypeDesc.FLOAT, new ClsTypeDesc(cls(pkg("a", "d"), "b", "e")))),
				map.getMthd("g/h$i.j", "(La/b;)F", ObfLevel.OBF));
		assertEquals(mthd(cls(cls(pkg("g", "k"), "h", "l"), "i", "m"), "j", "n",
				new Desc(BuiltInTypeDesc.FLOAT, new ClsTypeDesc(cls(pkg("a", "d"), "b", "e")))),
				map.getMthd("k/l$m.n", "(Ld/e;)F", ObfLevel.DEOBF));
//		assertEquals(new MthdMapping(new ClsMapping(ImmutableMap.of(ObfLevel.OBF, Path.parse("a/b"), ObfLevel.DEOBF, Path.parse(
//				"d/e"))), "c", "f",
//				new Desc(BuiltInTypeDesc.VOID, new TypeDesc[0])), map.getMthd("a/b/c", "()V"));
//		assertEquals(new MthdMapping(new ClsMapping(ImmutableMap.of(ObfLevel.OBF, Path.parse("a/b"), ObfLevel.DEOBF, Path.parse(
//				"d/e"))), "c", "f",
//				new Desc(BuiltInTypeDesc.VOID, new TypeDesc[0])), map.getMthd("d/e/f", "()V"));
//		assertEquals(new MthdMapping(new ClsMapping(new ClsMapping(ImmutableMap.of(ObfLevel.OBF, Path.parse(
//				"g/h"), ObfLevel.DEOBF, Path.parse("k/l"))),
//						ImmutableMap.of(ObfLevel.OBF, Path.parse("i"), ObfLevel.DEOBF, Path.parse("m"))), "j", "n",
//						new Desc(BuiltInTypeDesc.FLOAT, new ClsTypeDesc(new ClsMapping(ImmutableMap.of(ObfLevel.OBF, Path.parse(
//								"a/b"), ObfLevel.DEOBF, Path.parse("d/e")))))),
//				map.getMthd("g/h$i/j", "(La/b;)F"));
//		assertEquals(new MthdMapping(new ClsMapping(new ClsMapping(ImmutableMap.of(ObfLevel.OBF, Path.parse(
//				"g/h"), ObfLevel.DEOBF, Path.parse("k/l"))),
//						ImmutableMap.of(ObfLevel.OBF, Path.parse("i"), ObfLevel.DEOBF, Path.parse("m"))), "j", "n",
//						new Desc(BuiltInTypeDesc.FLOAT, new ClsTypeDesc(new ClsMapping(ImmutableMap.of(ObfLevel.OBF, Path.parse(
//								"a/b"), ObfLevel.DEOBF, Path.parse("d/e")))))),
//				map.getMthd("k/l$m/n", "(Ld/e;)F"));
	}

	@Test
	public void addMthd_updatingMthdWithoutDst_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addMthd(ImmutableMap.of(ObfLevel.OBF, "a/b.c"), ObfLevel.OBF, "()F");

		assertEquals(new MthdMapping(new ClsMapping(new PkgMapping(ImmutableMap.of(ObfLevel.OBF, "a")),
				ImmutableMap.of(ObfLevel.OBF, "b")), ImmutableMap.of(ObfLevel.OBF, "c"),
				new Desc(BuiltInTypeDesc.FLOAT, new TypeDesc[0])), map.getMthd("a/b.c", "()F", ObfLevel.OBF));
		assertEquals(null, map.getMthd("d/e.f", "()F", ObfLevel.DEOBF));
		map.addMthd("a/b.c", "d/e.f", ObfLevel.OBF, "()F");
		assertEquals(mthd(cls(pkg("a", "d"), "b", "e"), "c", "f",
				new Desc(BuiltInTypeDesc.FLOAT, new TypeDesc[0])), map.getMthd("a/b.c", "()F", ObfLevel.OBF));
		assertEquals(mthd(cls(pkg("a", "d"), "b", "e"), "c", "f",
				new Desc(BuiltInTypeDesc.FLOAT, new TypeDesc[0])),
				map.getMthd("d/e.f", "()F", ObfLevel.DEOBF));
	}

	@Test(expected = DuplicateMappingException.class)
	public void addMthd_methodsWithSameSource_throwsException() {
		Mapping map = new Mapping();
		map.addMthd("a/b.c", "d/e.f", ObfLevel.OBF, "()F");
		map.addMthd("a/b.c", "g/h.i", ObfLevel.OBF, "()F");
	}

	@Test(expected = DuplicateMappingException.class)
	public void addMthd_methodsWithDstEqDst_throwsException() {
		Mapping map = new Mapping();
		map.addMthd("a/b.c", "d/e.f", ObfLevel.OBF, "()F");
		map.addMthd("g/h.i", "d/e.f", ObfLevel.OBF, "()F");
	}

	@Test(expected = IllegalArgumentException.class)
	public void addMthd_methodsPathWithoutPointSeparator_throwsException() {
		Mapping map = new Mapping();
		map.addMthd("a/b/c", "d/e/f", ObfLevel.OBF, "()F");
	}
}
