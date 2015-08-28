package com.hea3ven.tools.mappings;

public class ClsMapping extends ElementMapping {

	public ClsMapping(String src, String dst) {
		super(src, dst);
	}

	public ClsMapping(ClsMapping parent, String src, String dst) {
		super(parent, src, dst);
	}

	@Override
	protected String getParentPathSep() {
		return "$";
	}

	public boolean matches(String path) {
		return getSrcPath().equals(path) || (getDstPath() != null && getDstPath().equals(path));
	}

	@Override
	public String toString() {
		// TODO: Fix
		return String.format("<ClsMapping '%s' -> '%s'>", getSrcPath(), getDstPath());
	}

	public ClsMapping getParent() {
		return (ClsMapping) parent;
	}
}
