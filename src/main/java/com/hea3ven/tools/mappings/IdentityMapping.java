package com.hea3ven.tools.mappings;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class IdentityMapping extends Mapping {
	private Set<String> insertingNames = new HashSet<>();

	@Override
	public ClsMapping addCls(Map<ObfLevel, String> paths) {
		for (Entry<ObfLevel, String> entry : paths.entrySet())
			insertingNames.add(entry.getKey() + entry.getValue());
		ClsMapping cls = super.addCls(paths);
		for (Entry<ObfLevel, String> entry : paths.entrySet())
			insertingNames.remove(entry.getKey() + entry.getValue());
		return cls;
	}

	@Override
	public FldMapping addFld(Map<ObfLevel, String> paths, ObfLevel descObfLevel, String typeDesc) {
		for (Entry<ObfLevel, String> entry : paths.entrySet())
			insertingNames.add(entry.getKey() + entry.getValue());
		FldMapping fld = super.addFld(paths, descObfLevel, typeDesc);
		for (Entry<ObfLevel, String> entry : paths.entrySet())
			insertingNames.remove(entry.getKey() + entry.getValue());
		return fld;
	}

	@Override
	public MthdMapping addMthd(Map<ObfLevel, String> paths, ObfLevel descObfLevel, String desc) {
		for (Entry<ObfLevel, String> entry : paths.entrySet())
			insertingNames.add(entry.getKey() + entry.getValue() + desc);
		MthdMapping mthd = super.addMthd(paths, descObfLevel, desc);
		for (Entry<ObfLevel, String> entry : paths.entrySet())
			insertingNames.remove(entry.getKey() + entry.getValue() + desc);
		return mthd;
	}

	@Override
	public ClsMapping getCls(String name, ObfLevel level) {
		if (insertingNames.contains(level + name))
			return null;
		ClsMapping elem = super.getCls(name, level);
		if (elem == null) {
			insertingNames.add(level + name);
			elem = super.addCls(name, name);
			insertingNames.remove(level + name);
		}
		if (elem.getPath(ObfLevel.DEOBF) == null)
			elem = super.addCls(elem.getPath(ObfLevel.OBF), elem.getPath(ObfLevel.OBF));
		return elem;
	}

	@Override
	public MthdMapping getMthd(String name, String desc, ObfLevel level) {
		if (insertingNames.contains(level + name + desc))
			return null;
		MthdMapping elem = super.getMthd(name, desc, level);
		if (elem == null) {
			insertingNames.add(level + name + desc);
			elem = super.addMthd(name, name, level, desc);
			insertingNames.remove(level + name + desc);
		}
		return elem;
	}

	@Override
	public FldMapping getFld(String name, ObfLevel level) {
		if (insertingNames.contains(level + name))
			return null;
		FldMapping elem = super.getFld(name, level);
		if (elem == null) {
			insertingNames.add(level + name);
			elem = super.addFld(name, name, level, "I");
			insertingNames.remove(level + name);
		}
		return elem;
	}
}
