package com.hea3ven.tools.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.ElementMapping;
import com.hea3ven.tools.mappings.MappingException;

public class ClsMappingTest {

	@Test
	public void testFullInitialization() {
		ClsMapping result = new ClsMapping("a/b/c", "d/e/f");

		assertEquals("source path", result.getSrcPath(), "a/b/c");
		assertEquals("source package", result.getSrcScope(), "a/b");
		assertEquals("source name", result.getSrcName(), "c");
		assertEquals("destination path", result.getDstPath(), "d/e/f");
		assertEquals("destination package", result.getDstScope(), "d/e");
		assertEquals("destination package", result.getDstName(), "f");
	}

	@Test
	public void testFullInitializationFromDot() {
		ClsMapping result = new ClsMapping("a.b.c", "d.e.f");

		assertEquals("source path", result.getSrcPath(), "a/b/c");
		assertEquals("source package", result.getSrcScope(), "a/b");
		assertEquals("source name", result.getSrcName(), "c");
		assertEquals("destination path", result.getDstPath(), "d/e/f");
		assertEquals("destination package", result.getDstScope(), "d/e");
		assertEquals("destination package", result.getDstName(), "f");
	}

	@Test
	public void testInitializationWithoutPackage() {
		ClsMapping result = new ClsMapping("c", "f");

		assertEquals("source path", result.getSrcPath(), "c");
		assertEquals("source package", result.getSrcScope(), null);
		assertEquals("source name", result.getSrcName(), "c");
		assertEquals("destination path", result.getDstPath(), "f");
		assertEquals("destination package", result.getDstScope(), null);
		assertEquals("destination package", result.getDstName(), "f");
	}

	@Test
	public void testInitializationInnerCls() {
		ClsMapping result = new ClsMapping(new ClsMapping("a/b", "c/d"), "e", "f");

		assertEquals("source path", result.getSrcPath(), "a/b$e");
		assertEquals("source scope", result.getSrcScope(), "a/b");
		assertEquals("source name", result.getSrcName(), "e");
		assertEquals("destination path", result.getDstPath(), "c/d$f");
		assertEquals("destination scope", result.getDstScope(), "c/d");
		assertEquals("destination package", result.getDstName(), "f");
	}

	@Test
	public void initialization_invalidCharacters_throwsException() {
		String invalidChars = "$#@!%^&*()=-";
		for (int i = 0; i < invalidChars.length(); i++) {
			try {
				new ClsMapping("c" + invalidChars.charAt(i), "f");
				fail("did not throw the exception for " + invalidChars.charAt(i));
			} catch (MappingException e) {

			}
		}
	}

	@Test
	public void initialization_validCharacters_doesntThrowsException() {
		String validChars = "_/";
		for (int i = 0; i < validChars.length(); i++) {
			new ClsMapping("c" + validChars.charAt(i) + "d", "f");
		}
	}

	@Test
	public void testEquals() {
		ElementMapping result = new ClsMapping("a/b/c", "d/e/f");

		assertEquals("are equal", new ClsMapping("a/b/c", "d/e/f"), result);
	}

}
