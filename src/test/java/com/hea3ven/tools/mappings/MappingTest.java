package com.hea3ven.tools.mappings;

import static org.junit.Assert.*;

import org.junit.Test;

import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.DuplicateMappingException;
import com.hea3ven.tools.mappings.Mapping;

public class MappingTest {

	@Test
	public void getBySrc_mappingWithOneClass_returnsTheClass() {
		Mapping map = new Mapping();
		map.add(new ClsMapping("a/b", "c/d"));

		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("a/b"));
	}

	@Test
	public void getBySrc_mappingWithTwoClasses_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.add(new ClsMapping("a/b", "c/d"));
		map.add(new ClsMapping("e/f", "g/h"));

		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("a/b"));
		assertEquals(new ClsMapping("e/f", "g/h"), map.getCls("e/f"));
	}

	@Test
	public void getByDst_mappingWithOneClass_returnsTheClass() {
		Mapping map = new Mapping();
		map.add(new ClsMapping("a/b", "c/d"));

		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("c/d"));
	}

	@Test
	public void getByDst_mappingWithTwoClasses_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.add(new ClsMapping("a/b", "c/d"));
		map.add(new ClsMapping("e/f", "g/h"));

		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("c/d"));
		assertEquals(new ClsMapping("e/f", "g/h"), map.getCls("g/h"));
	}

	@Test(expected = DuplicateMappingException.class)
	public void addCls_clasessWithSameSource_throwsException() {
		Mapping map = new Mapping();
		map.add(new ClsMapping("a/b", "c/d"));
		map.add(new ClsMapping("a/b", "e/f"));
	}

	@Test(expected = DuplicateMappingException.class)
	public void addCls_clasessWithDstEqDst_throwsException() {
		Mapping map = new Mapping();
		map.add(new ClsMapping("a/b", "c/d"));
		map.add(new ClsMapping("e/f", "c/d"));
	}

	@Test
	public void getCls_partialyMappedDoubleInnerClass_returnsTheCorrectClass() {
		Mapping map = new Mapping();
		map.add(new ClsMapping("a/b", "c/d"));

		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("c/d"));
		assertEquals(new ClsMapping(new ClsMapping("a/b", "c/d"), "1", null), map.getCls("c/d$1"));
		assertEquals(
				new ClsMapping(new ClsMapping(new ClsMapping("a/b", "c/d"), "1", null), "2", null),
				map.getCls("c/d$1$2"));
	}

	@Test
	public void getCls_DifferentInnerClasses_returnsTheCorrectClass() {
		Mapping map = new Mapping();

		assertEquals(new ClsMapping(new ClsMapping("a/b", null),"1","1"), map.getCls("a/b$1"));
		assertEquals(new ClsMapping(new ClsMapping("c/d", null),"1","1"), map.getCls("c/d$1"));
	}

//	@Test
//	public void getFld_getByDstWithoutMapping_returnsTheCorrectFld() {
//		Mapping map = new Mapping();
//		map.add(new ClsMapping("a/b", "c/d"));
//
//		assertEquals(new ClsMapping("a/b", "c/d"), map.getCls("c/d"));
//		assertEquals(new FldMapping(new ClsMapping("a/b", "c/d"), "e", null,
//				new Desc(BuiltInTypeDesc.VOID)), map.getFld("c/d/e"));
//	}
}
