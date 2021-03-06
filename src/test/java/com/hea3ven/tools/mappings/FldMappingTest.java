package com.hea3ven.tools.mappings;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.*;

import org.junit.Test;

public class FldMappingTest extends ElementMappingTestBase {

	@Test(expected = MappingException.class)
	public void initialization_nullParent_throwsException() {
		new FldMapping(null, ImmutableMap.of(ObfLevel.OBF, "a", ObfLevel.DEOBF, "b"),
				BuiltInTypeDesc.INTEGER);
	}

	@Test
	public void initialization_withParentClass_initializes() {
		FldMapping result = new FldMapping(new ClsMapping(createPathMap("a", "b")), createPathMap("c", "d"),
				BuiltInTypeDesc.INTEGER);

		assertEquals("obf path", "c", result.getName(ObfLevel.OBF));
		assertEquals("deobf path", "d", result.getName(ObfLevel.DEOBF));
		assertEquals("obf path", "a.c", result.getPath(ObfLevel.OBF));
		assertEquals("deobf path", "b.d", result.getPath(ObfLevel.DEOBF));
	}

	@Test
	public void testEquals() {
		EqualsVerifier.forClass(FldMapping.class)
				.withPrefabValues(FldMapping.class,
						new FldMapping(new ClsMapping(createPathMap("a", "b")), createPathMap("c", "d"),
								BuiltInTypeDesc.INTEGER),
						new FldMapping(new ClsMapping(createPathMap("e", "f")), createPathMap("g", "h"),
								BuiltInTypeDesc.INTEGER))
				.withPrefabValues(ElementMapping.class, new ClsMapping(createPathMap("i", "j")),
						new ClsMapping(createPathMap("m", "n")))
				.suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
				.withRedefinedSuperclass()
				.verify();
	}
}
