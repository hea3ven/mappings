package com.hea3ven.tools.mappings;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClsMappingTest extends ElementMappingTestBase {

	@Test
	public void initialization_noParent_initializes() {
		ClsMapping result = new ClsMapping(createPathMap("a", "b"));

		assertEquals("obf path", "a", result.getName(ObfLevel.OBF));
		assertEquals("deobf path", "b", result.getName(ObfLevel.DEOBF));
	}

	@Test
	public void initialization_withParentClass_initializes() {
		ClsMapping result =
				new ClsMapping(new ClsMapping(createPathMap("a", "b")), createPathMap("c", "d"));

		assertEquals("obf path", "c", result.getName(ObfLevel.OBF));
		assertEquals("deobf path", "d", result.getName(ObfLevel.DEOBF));
		assertEquals("obf path", "a$c", result.getPath(ObfLevel.OBF));
		assertEquals("deobf path", "b$d", result.getPath(ObfLevel.DEOBF));
	}

	@Test
	public void initialization_withParentPackage_initializes() {
		ClsMapping result =
				new ClsMapping(new PkgMapping(createPathMap("a", "b")), createPathMap("c", "d"));

		assertEquals("obf path", "c", result.getName(ObfLevel.OBF));
		assertEquals("deobf path", "d", result.getName(ObfLevel.DEOBF));
		assertEquals("obf path", "a/c", result.getPath(ObfLevel.OBF));
		assertEquals("deobf path", "b/d", result.getPath(ObfLevel.DEOBF));
	}

	@Test
	public void getPath_withParentPackageNoName_getsPath() {
		ClsMapping result =
				new ClsMapping(new PkgMapping(ImmutableMap.of(ObfLevel.OBF, "a")), createPathMap("b", "c"));

		assertEquals("obf path", "a/b", result.getPath(ObfLevel.OBF));
		assertEquals("deobf path", "c", result.getPath(ObfLevel.DEOBF));
	}

	@Test
	public void testEquals() {
		EqualsVerifier.forClass(ClsMapping.class)
				.withPrefabValues(ClsMapping.class, new ClsMapping(createPathMap("a", "b")),
						new ClsMapping(createPathMap("c", "d")))
				.withPrefabValues(ElementMapping.class, new ClsMapping(createPathMap("e", "f")),
						new ClsMapping(createPathMap("g", "h")))
				.suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
				.withRedefinedSuperclass()
				.verify();
	}
}
