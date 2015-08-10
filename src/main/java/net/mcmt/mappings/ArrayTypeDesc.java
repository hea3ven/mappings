package net.mcmt.mappings;

public class ArrayTypeDesc extends TypeDesc {

	private TypeDesc desc;

	public ArrayTypeDesc(TypeDesc desc) {
		this.desc = desc;
	}

	@Override
	public String getSrc() {
		return "[" + desc.getSrc();
	}

	@Override
	public String getDst() {
		return "[" + desc.getDst();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (!(other instanceof TypeDesc))
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
