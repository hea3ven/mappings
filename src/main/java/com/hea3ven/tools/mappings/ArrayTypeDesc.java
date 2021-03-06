package com.hea3ven.tools.mappings;

public class ArrayTypeDesc extends TypeDesc {

	private TypeDesc desc;

	public ArrayTypeDesc(TypeDesc desc) {
		this.desc = desc;
	}

	@Override
	public String get(ObfLevel level) {
		return "[" + desc.get(level);
	}

	public TypeDesc getDescType() {
		return desc;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (!(other instanceof ArrayTypeDesc))
			return false;

		ArrayTypeDesc otherMapping = (ArrayTypeDesc) other;
		return desc.equals(otherMapping.desc);
	}

	@Override
	public int hashCode() {
		int hash = 27;
		hash = hash * 31 + desc.hashCode();

		return hash;
	}
}
