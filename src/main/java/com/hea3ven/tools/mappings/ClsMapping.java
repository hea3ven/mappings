package com.hea3ven.tools.mappings;

import java.util.Map;

public class ClsMapping extends ElementMapping {

	public ClsMapping(Map<ObfLevel, String> names) {
		super(null, names);
	}

	public ClsMapping(ClsMapping parent, Map<ObfLevel, String> names) {
		super(parent, names);
	}

	public ClsMapping(PkgMapping parent, Map<ObfLevel, String> names) {
		super(parent, names);
	}

	private static boolean isAnonymous(String src) {
		for (char c : src.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

	@Override
	protected String getParentPathSep() {
		return parent instanceof PkgMapping ? "/" : "$";
	}

	public boolean matches(ObfLevel level, String path) {
		return getPath(level).equals(path);
	}

	@Override
	public String toString() {
		return String.format("<ClsMapping '%s' -> '%s'>", getPath(ObfLevel.OBF), getPath(ObfLevel.DEOBF));
	}

	@Override
	public final boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public boolean canEqual(Object other) {
		return other instanceof ClsMapping;
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}
}
