package net.mcmt.mappings;

import java.util.Set;

import com.google.common.collect.Sets;

public class Mapping {

	private Set<ElementMapping> clsMaps = Sets.newHashSet();

	public ElementMapping get(String name) {
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
		else
			return null;
	}

	public MthdMapping getMthd(String name) {
		ElementMapping mthd = get(name);
		if (mthd instanceof MthdMapping)
			return (MthdMapping) mthd;
		else
			return null;
	}

	public MthdMapping getMthd(String name, String desc) {
		for (ElementMapping clsMap : clsMaps) {
			if (clsMap instanceof MthdMapping) {
				MthdMapping mthd = (MthdMapping) clsMap;
				if ((name.equals(mthd.getSrcPath()) || name.equals(mthd.getDstPath()))
						&& (desc.equals(mthd.getDesc().getSrc())
								|| desc.equals(mthd.getDesc().getDst())))
					return mthd;
			}
		}
		return null;
	}

	public FldMapping getFld(String name) {
		ElementMapping fld = get(name);
		if (fld instanceof FldMapping)
			return (FldMapping) fld;
		else
			return null;
	}

	public ArgMapping getArg(String name) {
		ElementMapping arg = get(name);
		if (arg instanceof ArgMapping)
			return (ArgMapping) arg;
		else
			return null;
	}

	public void add(ElementMapping clsMap) {
		if (getCls(clsMap.getSrcPath()) != null)
			throw new DuplicateMappingException(clsMap.getSrcPath());
		if (getCls(clsMap.getDstPath()) != null)
			throw new DuplicateMappingException(clsMap.getDstPath());
		this.clsMaps.add(clsMap);
	}
}
