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

	@Test(expected=DuplicateMappingException.class)
	public void addCls_clasessWithSameSource_throwsException() {
		Mapping map = new Mapping();
		map.add(new ClsMapping("a/b", "c/d"));
		map.add(new ClsMapping("a/b", "e/f"));
	}

	@Test(expected=DuplicateMappingException.class)
	public void addCls_clasessWithDstEqDst_throwsException() {
		Mapping map = new Mapping();
		map.add(new ClsMapping("a/b", "c/d"));
		map.add(new ClsMapping("e/f", "c/d"));
	}
}
