package com.hea3ven.tools.mappings;

public class ArgMapping extends ElementMapping {

	public ArgMapping(MthdMapping parent, int index, String name) {
		super(parent, null);
//		super(parent, String.valueOf(index), name);
	}

	@Override
	protected String getParentPathSep() {
		return "@";
	}

	public MthdMapping getParent() {
		return (MthdMapping) parent;
	}

}
