package com.hea3ven.tools.mappings;

import org.junit.Test;

import static com.hea3ven.tools.mappings.parser.MappingTestUtils.cls;
import static com.hea3ven.tools.mappings.parser.MappingTestUtils.pkg;
import static org.junit.Assert.*;

public class IdentityMappingTest {

	@Test
	public void getCls_getACls_returnsIdentityCls() {
		IdentityMapping mapping = new IdentityMapping();

		assertEquals(cls(pkg("a", "a"), "b", "b"), mapping.getCls("a/b", ObfLevel.OBF));
		assertEquals(cls(pkg("a", "a"), "b", "b"), mapping.getCls("a/b", ObfLevel.DEOBF));
	}
}