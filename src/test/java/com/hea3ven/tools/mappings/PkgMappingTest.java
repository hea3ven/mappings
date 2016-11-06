package com.hea3ven.tools.mappings;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PkgMappingTest extends ElementMappingTestBase {

	@Test
	public void initialization_withNames_initializes() {
		PkgMapping result = new PkgMapping(createPathMap("a", "b"));

		assertEquals("obf path", "a", result.getName(ObfLevel.OBF));
		assertEquals("deobf path", "b", result.getName(ObfLevel.DEOBF));
	}

	@Test
	public void testEquals() {
		EqualsVerifier.forClass(PkgMapping.class)
				.withPrefabValues(PkgMapping.class,
						new PkgMapping(createPathMap("a", "b")),new PkgMapping(createPathMap("c", "d")))
				.withPrefabValues(ElementMapping.class,
						new PkgMapping(createPathMap("e", "f")),
						new PkgMapping(createPathMap("g", "h")))
				.suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
				.withRedefinedSuperclass()
				.verify();
	}
}
