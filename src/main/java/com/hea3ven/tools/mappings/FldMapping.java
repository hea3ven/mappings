package com.hea3ven.tools.mappings;

import javax.annotation.Nonnull;
import java.util.Map;

public class FldMapping extends ElementMapping {

	@Nonnull
	private final TypeDesc desc;

	public FldMapping(ClsMapping parent, Map<ObfLevel, String> names, TypeDesc desc) {
		super(parent, names);

		if (parent == null)
			throw new MappingException("null parent");

		this.desc = desc;
	}

	@Override
	protected String getParentPathSep() {
		return ".";
	}

	public ClsMapping getParent() {
		return (ClsMapping) parent;
	}

	public TypeDesc getDesc() {
		return desc;
	}

	@Override
	public final boolean equals(Object other) {
		if (!super.equals(other))
			return false;

		if (!(other instanceof FldMapping))
			return false;

		FldMapping otherFld = (FldMapping) other;
		return ((desc == null && otherFld.desc == null) || (desc != null && desc.equals(otherFld.desc)));
	}

	@Override
	public final int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + desc.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return String.format("<FldMapping '%s %s' -> '%s %s'>", getPath(ObfLevel.OBF),
				(desc != null) ? desc.get(ObfLevel.OBF) : "", getPath(ObfLevel.DEOBF),
				(desc != null) ? desc.get(ObfLevel.DEOBF) : "");
	}

	@Override
	public boolean canEqual(Object other) {
		return other instanceof FldMapping;
	}
}
