package com.hea3ven.tools.mappings;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MappingTest {

	@Test
	public void getBySrc_mappingWithOneClass_returnsTheClass() {
		Mapping map = new Mapping();
		map.addCls("a/b", "c/d");

		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("a/b"));
	}

	@Test
	public void getBySrc_mappingWithTwoClasses_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addCls("a/b", "c/d");
		map.addCls("e/f", "g/h");

		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("a/b"));
		assertEquals(new ClsMapping("e/f", "g/h"), map.getCls("e/f"));
	}

	@Test
	public void getByDst_mappingWithOneClass_returnsTheClass() {
		Mapping map = new Mapping();
		map.addCls("a/b", "c/d");

		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("c/d"));
	}

	@Test
	public void getByDst_mappingWithTwoClasses_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addCls("a/b", "c/d");
		map.addCls("e/f", "g/h");

		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("c/d"));
		assertEquals(new ClsMapping("e/f", "g/h"), map.getCls("g/h"));
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
		map.addCls("a/b", null);
		assertEquals(new ClsMapping("a/b", null), map.getCls("a/b"));
		assertEquals(null, map.getCls("c/d"));
		map.addCls("a/b", "c/d");
		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("a/b"));
		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("c/d"));
	}

	@Test
	public void addCls_addInnerWithoutParent_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addCls("a/b$c", "d/e$f");

		assertEquals(new ClsMapping("a/b", "d/e"), map.getCls("a/b"));
		assertEquals(new ClsMapping("a/b", "d/e"), map.getCls("d/e"));
		assertEquals(new ClsMapping(new ClsMapping("a/b", "d/e"), "c", "f"), map.getCls("a/b$c"));
		assertEquals(new ClsMapping(new ClsMapping("a/b", "d/e"), "c", "f"), map.getCls("d/e$f"));
	}

	@Test
	public void getCls_DifferentInnerClasses_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.addCls("a/b$1", null);
		map.addCls("c/d$1", null);

		assertEquals(new ClsMapping(new ClsMapping("a/b", null), "1", "1"), map.getCls("a/b$1"));
		assertEquals(new ClsMapping(new ClsMapping("c/d", null), "1", "1"), map.getCls("c/d$1"));
	}

//	@Test
//	public void getFld_getByDstWithoutMapping_returnsTheCorrectFld() {
//		Mapping map = new Mapping();
//		map.parse(new ClsMapping("a/b", "c/d"));
//
//		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("c/d"));
//		assertEquals(new FldMapping(new ClsMapping("a/b", "c/d"), "e", null,
//				new Desc(BuiltInTypeDesc.VOID)), map.getFld("c/d/e"));
//	}

	@Test
	public void addFld_addFld_returnsTheCorrectFld() {
		Mapping map = new Mapping();
		map.addFld("a/b/c", "d/e/f");
		map.addFld("g/h$i/j", "k/l$m/n");

		assertEquals(new FldMapping(new ClsMapping("a/b", "d/e"), "c", "f", null), map.getFld("a/b/c"));
		assertEquals(new FldMapping(new ClsMapping("a/b", "d/e"), "c", "f", null), map.getFld("d/e/f"));
		assertEquals(new FldMapping(new ClsMapping(new ClsMapping("g/h", "k/l"), "i", "m"), "j", "n", null),
				map.getFld("g/h$i/j"));
		assertEquals(new FldMapping(new ClsMapping(new ClsMapping("g/h", "k/l"), "i", "m"), "j", "n", null),
				map.getFld("k/l$m/n"));
	}

	@Test
	public void addMthd_addMthd_returnsTheCorrectMthd() {
		Mapping map = new Mapping();
		map.addMthd("a/b/c", "d/e/f", "()V");
		map.addMthd("g/h$i/j", "k/l$m/n", "(La/b;)F");

		assertEquals(new MthdMapping(new ClsMapping("a/b", "d/e"), "c", "f",
				new Desc(BuiltInTypeDesc.VOID, new TypeDesc[0])), map.getMthd("a/b/c", "()V"));
		assertEquals(new MthdMapping(new ClsMapping("a/b", "d/e"), "c", "f",
				new Desc(BuiltInTypeDesc.VOID, new TypeDesc[0])), map.getMthd("d/e/f", "()V"));
		assertEquals(new MthdMapping(new ClsMapping(new ClsMapping("g/h", "k/l"), "i", "m"), "j", "n",
						new Desc(BuiltInTypeDesc.FLOAT, new ClsTypeDesc(new ClsMapping("a/b", "d/e")))),
				map.getMthd("g/h$i/j", "(La/b;)F"));
		assertEquals(new MthdMapping(new ClsMapping(new ClsMapping("g/h", "k/l"), "i", "m"), "j", "n",
						new Desc(BuiltInTypeDesc.FLOAT, new ClsTypeDesc(new ClsMapping("a/b", "d/e")))),
				map.getMthd("k/l$m/n", "(Ld/e;)F"));
	}
}
