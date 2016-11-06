package com.hea3ven.tools.mappings;

import java.util.Map;

public class PkgMapping extends ElementMapping {

	public PkgMapping(Map<ObfLevel, String> names) {
		super(null, names);
	}

	@Override
	protected String getParentPathSep() {
		return "/";
	}

	@Override
	public String toString() {
		return String.format("<PkgMapping '%s' -> '%s'>", getPath(ObfLevel.OBF), getPath(ObfLevel.DEOBF));
	}

	@Override
	public final boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public boolean canEqual(Object other) {
		return other instanceof PkgMapping;
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}
}
