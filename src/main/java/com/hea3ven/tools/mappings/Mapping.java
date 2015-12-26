package com.hea3ven.tools.mappings;

import java.util.Set;

import com.google.common.collect.Sets;

public class Mapping {

	private Set<ElementMapping> clsMaps = Sets.newHashSet();

	public ElementMapping get(String name) {
		name = name.replace('.', '/');

		for (ElementMapping clsMap : clsMaps) {
			if (name.equals(clsMap.getSrcPath()) || name.equals(clsMap.getDstPath()))
				return clsMap;
		}

		return null;
	}

	public ClsMapping getCls(String name) {
		ElementMapping cls = get(name);
		if (cls instanceof ClsMapping)
			return (ClsMapping) cls;
		else {
			ClsMapping clsMap = null;
			if (!name.contains("$")) {
				clsMap = new ClsMapping(name, null);
			} else {
				ClsMapping parent = getCls(name.substring(0, name.lastIndexOf('$')));
				clsMap = new ClsMapping(parent, name.substring(name.lastIndexOf('$') + 1), null);
			}
			add(clsMap);
			return clsMap;
		}
	}

	public MthdMapping getMthd(String name, String desc) {
		for (ElementMapping clsMap : clsMaps) {
			if (clsMap instanceof MthdMapping) {
				MthdMapping mthd = (MthdMapping) clsMap;
				if ((name.equals(mthd.getSrcPath()) || name.equals(mthd.getDstPath())) &&
						(desc.equals(mthd.getDesc().getSrc()) || desc.equals(mthd.getDesc().getDst())))
					return mthd;
			}
		}
		ClsMapping parent = getCls(name.substring(0, name.lastIndexOf('/')));
		String mthdName = name.substring(name.lastIndexOf('/') + 1);
		MthdMapping mthd = new MthdMapping(parent, mthdName, mthdName, Desc.parse(this, desc));
		add(mthd);
		return mthd;
	}

	public FldMapping getFld(String name) {
		ElementMapping fld = get(name);
		if (!(fld instanceof FldMapping)) {
			ClsMapping parent = getCls(name.substring(0, name.lastIndexOf('/')));
			String fldName = name.substring(name.lastIndexOf('/') + 1);
			fld = new FldMapping(parent, fldName, fldName, null);
		}
		return (FldMapping) fld;
	}

	public ArgMapping getArg(String name) {
		ElementMapping arg = get(name);
		if (arg instanceof ArgMapping)
			return (ArgMapping) arg;
		else
			return null;
	}

	public void add(ElementMapping clsMap) {
		if (clsMap instanceof ClsMapping) {
			for (ElementMapping otherClsMap : clsMaps) {
				if (otherClsMap instanceof ClsMapping) {
					if (clsMap.getSrcPath().equals(otherClsMap.getSrcPath()))
						throw new DuplicateMappingException(clsMap.getSrcPath());
					if (clsMap.getDstPath() != null && otherClsMap.getDstPath() != null &&
							clsMap.getDstPath().equals(otherClsMap.getDstPath()))
						throw new DuplicateMappingException(clsMap.getDstPath());
				}
			}
		}
		if (clsMap.parent != null) {
			clsMap.parent.addChild(clsMap);
		}
		this.clsMaps.add(clsMap);
	}

	public Set<ElementMapping> getAll() {
		return clsMaps;
	}
}
