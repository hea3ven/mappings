package com.hea3ven.tools.mappings;

import javax.annotation.Nonnull;
import java.util.Map;

public class MthdMapping extends ElementMapping {

	@Nonnull
	private final Desc desc;

	public MthdMapping(ClsMapping parent, Map<ObfLevel, String> names, Desc desc) {
		super(parent, names);

		if (parent == null)
			throw new MappingException("null parent");

		this.desc = desc;
	}

	@Override
	protected String getParentPathSep() {
		return ".";
	}

	public Desc getDesc() {
		return desc;
	}

	public ClsMapping getParent() {
		return (ClsMapping) parent;
	}

	public boolean matches(ObfLevel level, String parent, String name, String desc) {
		return matches(level, parent + "." + name, desc);
	}

	private boolean matches(ObfLevel level, String path, String desc) {
		return getPath(level).equals(path) && this.desc.get(level).equals(desc);
	}

	@Override
	public final boolean equals(Object other) {
		if (!super.equals(other))
			return false;

		if (!(other instanceof MthdMapping))
			return false;

		MthdMapping otherFld = (MthdMapping) other;
		return ((desc == null && otherFld.desc == null) || (desc != null && desc.equals(otherFld.desc)));
	}

	@Override
	public boolean canEqual(Object other) {
		return other instanceof MthdMapping;
	}

	@Override
	public final int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + desc.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return String.format("<MthdMapping '%s %s' -> '%s %s'>", getPath(ObfLevel.OBF),
				desc.get(ObfLevel.OBF),
				getPath(ObfLevel.DEOBF), desc.get(ObfLevel.DEOBF));
	}
}
