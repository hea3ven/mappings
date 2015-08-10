package net.mcmt.mappings;

import static org.junit.Assert.*;

import org.junit.Test;

public class FldMappingTest {

	@Test(expected=MappingException.class)
	public void initialization_nullParent_throwsException() {
		new FldMapping(null, "a", "b", new Desc(BuiltInTypeDesc.INTEGER));
	}

	@Test
	public void testEquals() {
		ElementMapping result = new FldMapping(new ClsMapping("a/b/c", "d/e/f"), "g", "h",
				new Desc(BuiltInTypeDesc.INTEGER));

		assertEquals("are equal", new FldMapping(new ClsMapping("a/b/c", "d/e/f"), "g", "h",
				new Desc(BuiltInTypeDesc.INTEGER)), result);
		assertNotEquals("are not equal", new FldMapping(new ClsMapping("a/b/i", "d/e/f"), "g", "h",
				new Desc(BuiltInTypeDesc.INTEGER)), result);
		assertNotEquals("are not equal", new FldMapping(new ClsMapping("a/b/c", "d/e/f"), "i", "h",
				new Desc(BuiltInTypeDesc.INTEGER)), result);
		assertNotEquals("are not equal", new FldMapping(new ClsMapping("a/b/c", "d/e/f"), "g", "i",
				new Desc(BuiltInTypeDesc.INTEGER)), result);
		assertNotEquals("are not equal", new FldMapping(new ClsMapping("a/b/c", "d/e/f"), "g", "h",
				new Desc(BuiltInTypeDesc.FLOAT)), result);
	}

}
