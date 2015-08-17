package net.mcmt.mappings;

public class MthdMapping extends ElementMapping {

	private Desc desc;

	public MthdMapping(ClsMapping parent, String src, String dst, Desc desc) {
		super(parent, src, dst);

		if (parent == null)
			throw new MappingException("null parent");

		this.desc = desc;
	}

	@Override
	protected String getParentPathSep() {
		return "/";
	}

	public Desc getDesc() {
		return desc;
	}

	public ClsMapping getParent() {
		return (ClsMapping) parent;
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		if (getClass() != other.getClass())
			return false;
		MthdMapping otherFld = (MthdMapping) other;
		return ((desc == null && otherFld.desc == null) || desc.equals(otherFld.desc));
	}

	@Override
	public String toString() {
		return String.format("<MthdMapping '%s %s' -> '%s %s'>", getSrcPath(), desc.getSrc(),
				getDstPath(), desc.getDst());
	}
}
