package com.hea3ven.tools.mappings;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

import com.hea3ven.tools.mappings.Path.PartType;
import static org.junit.Assert.*;

public class PathTest {
	@Test
	public void pase_class_parsesTheNames() {
		Path result = Path.parse("a/b");

		assertEquals("b", result.getName());
		assertEquals(PartType.CLASS, result.getType());
		assertNotNull(result.getParent());
		assertEquals("a", result.getParent().getName());
		assertEquals(PartType.PACKAGE, result.getParent().getType());
		assertEquals("a/b", result.getPath());
	}

	@Test
	public void pase_package_parsesTheNames() {
		Path result = Path.parse("a/");

		assertEquals("a", result.getName());
		assertEquals(PartType.PACKAGE, result.getType());
		assertNull(result.getParent());
		assertEquals("a", result.getPath());
	}

	@Test
	public void pase_deepPackage_parsesTheNames() {
		Path result = Path.parse("a/b/c/");

		assertEquals("a/b/c", result.getName());
		assertEquals(PartType.PACKAGE, result.getType());
		assertNull(result.getParent());
	}

	@Test
	public void pase_classWithoutPackage_parsesTheNames() {
		Path result = Path.parse("a");

		assertEquals("a", result.getName());
		assertEquals(PartType.CLASS, result.getType());
		assertNull(result.getParent());
		assertEquals("a", result.getPath());
	}

	@Test
	public void pase_member_parsesTheNames() {
		Path result = Path.parse("a/b.c");

		assertEquals("c", result.getName());
		assertEquals(PartType.MEMBER, result.getType());
		assertNotNull(result.getParent());
		assertEquals("b", result.getParent().getName());
		assertEquals(PartType.CLASS, result.getParent().getType());
		assertNotNull(result.getParent().getParent());
		assertEquals("a", result.getParent().getParent().getName());
		assertEquals(PartType.PACKAGE, result.getParent().getParent().getType());
		assertEquals("a/b.c", result.getPath());
	}

	@Test
	public void paseSimple_withDollarSign_parsesTheNames() {
		Path result = Path.parse("a/b$c");

		assertEquals("c", result.getName());
		assertEquals(PartType.CLASS, result.getType());
		assertNotNull(result.getParent());
		assertEquals("b", result.getParent().getName());
		assertEquals(PartType.CLASS, result.getParent().getType());
		assertNotNull(result.getParent().getParent());
		assertEquals("a", result.getParent().getParent().getName());
		assertEquals(PartType.PACKAGE, result.getParent().getParent().getType());
		assertEquals("a/b$c", result.getPath());
	}

	@Test
	public void pase_innerClassWithMember_parsesTheNames() {
		Path result = Path.parse("a/b$c.d");

		assertEquals("d", result.getName());
		assertEquals(PartType.MEMBER, result.getType());
		assertNotNull(result.getParent());
		assertEquals("c", result.getParent().getName());
		assertEquals(PartType.CLASS, result.getParent().getType());
		assertNotNull(result.getParent().getParent());
		assertEquals("b", result.getParent().getParent().getName());
		assertEquals(PartType.CLASS, result.getParent().getParent().getType());
		assertNotNull(result.getParent().getParent().getParent());
		assertEquals("a", result.getParent().getParent().getParent().getName());
		assertEquals(PartType.PACKAGE, result.getParent().getParent().getParent().getType());
		assertEquals("a/b$c.d", result.getPath());
	}

	@Test
	public void pase_innerClassAlone_parsesTheNames() {
		Path result = Path.parse("$a");

		assertEquals("a", result.getName());
		assertEquals(PartType.CLASS, result.getType());
		assertNotNull(result.getParent());
		assertEquals(null, result.getParent().getName());
		assertEquals(PartType.CLASS, result.getParent().getType());
		assertNull(result.getParent().getParent());
	}
	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Path.class)
				.withPrefabValues(Path.class, new Path(null, "a", PartType.CLASS),
						new Path(null, "b", PartType.CLASS))
				.verify();
	}
}