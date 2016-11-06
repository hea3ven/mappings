package com.hea3ven.tools.mappings;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MthdMappingTest extends ElementMappingTestBase {

	@Test(expected = MappingException.class)
	public void initialization_nullParent_throwsException() {
		new MthdMapping(null, createPathMap("a", "b"), new Desc(BuiltInTypeDesc.INTEGER));
	}

	@Test
	public void initialization_withParentClass_initializes() {
		MthdMapping result = new MthdMapping(new ClsMapping(createPathMap("a", "b")), createPathMap("c", "d"),
				new Desc(BuiltInTypeDesc.INTEGER));

		assertEquals("obf path", "c", result.getName(ObfLevel.OBF));
		assertEquals("deobf path", "d", result.getName(ObfLevel.DEOBF));
		assertEquals("obf path", "a.c", result.getPath(ObfLevel.OBF));
		assertEquals("deobf path", "b.d", result.getPath(ObfLevel.DEOBF));
	}

	@Test
	public void testEquals() {
		EqualsVerifier.forClass(MthdMapping.class)
				.withPrefabValues(MthdMapping.class,
						new MthdMapping(new ClsMapping(createPathMap("a", "b")), createPathMap("c", "d"),
								new Desc(BuiltInTypeDesc.INTEGER)),
						new MthdMapping(new ClsMapping(createPathMap("e", "f")), createPathMap("g", "h"),
								new Desc(BuiltInTypeDesc.INTEGER)))
				.withPrefabValues(ElementMapping.class,
						new ClsMapping(createPathMap("i", "j")),
						new ClsMapping(createPathMap("m", "n")))
				.suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
				.withRedefinedSuperclass()
				.verify();
	}
}
