package net.mcmt.mappings;

public class ClsTypeDesc extends TypeDesc {

	private ClsMapping cls;

	public ClsTypeDesc(ClsMapping cls) {
		this.cls = cls;
	}

	@Override
	public String getSrc() {
		return "L" + cls.getSrcPath() + ";";
	}

	@Override
	public String getDst() {
		return "L" + cls.getDstPath() + ";";
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (!(other instanceof TypeDesc))
			return false;

		ClsTypeDesc otherMapping = (ClsTypeDesc) other;
		return cls.equals(otherMapping.cls);
	}

	@Override
	public int hashCode() {
		int hash = 27;
		hash = hash * 31 + cls.hashCode();

		return hash;
	}
}
