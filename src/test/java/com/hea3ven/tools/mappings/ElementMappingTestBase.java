package com.hea3ven.tools.mappings;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;

public class ElementMappingTestBase {
	@Nonnull
	protected ImmutableMap<ObfLevel, String> createPathMap(String path, String path2) {
		return ImmutableMap.of(ObfLevel.OBF, path, ObfLevel.DEOBF, path2);
	}
}
