package com.hea3ven.tools.mappings;

public class IdentityMapping extends Mapping {
	@Override
	public ElementMapping get(String name) {
		ElementMapping elem = super.get(name);
		if (elem == null)
			return elem;
		if (elem.getDstPath() == null)
			elem.setDst(elem.getSrcName());
		return elem;
	}
}
