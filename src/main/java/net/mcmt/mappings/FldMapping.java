package net.mcmt.mappings;

public class FldMapping extends ElementMapping {

	private Desc desc;

	public FldMapping(ClsMapping parent, String src, String dst, Desc desc) {
		super(parent, src, dst);
		
		if (parent == null)
			throw new MappingException("null parent");

		this.desc = desc;
	}
	
	@Override
	protected String getParentPathSep() {
		return "/";
	}
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		FldMapping otherFld = (FldMapping) other;
		return ((desc == null && otherFld.desc == null) || desc.equals(otherFld.desc));
	}

	@Override
	public String toString() {
		return String.format("<FldMapping '%s %s' -> '%s %s'>", getSrcPath(), desc.getSrc(), getDstPath(), desc.getDst());
	}
}
