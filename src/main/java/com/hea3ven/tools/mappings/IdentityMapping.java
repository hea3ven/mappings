package com.hea3ven.tools.mappings;

public class IdentityMapping extends Mapping {
	@Override
	public ClsMapping getCls(String name) {
		ClsMapping elem = super.getCls(name);
		if (elem == null)
			elem = super.addCls(name, name);
		if (elem.getDstPath() == null)
			elem = super.addCls(elem.getSrcPath(), elem.getSrcPath());
		return elem;
	}

	@Override
	public MthdMapping getMthd(String name, String desc) {
		MthdMapping elem = super.getMthd(name, desc);
		if (elem == null)
			elem = super.addMthd(name, name, desc);
		return elem;
	}

	@Override
	public FldMapping getFld(String name) {
		FldMapping elem = super.getFld(name);
		if (elem == null)
			elem = super.addFld(name, name);
		return elem;
	}
}
