package com.hea3ven.tools.mappings;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Mapping {

	private Map<String, ElementMapping> elementsBySrc = Maps.newHashMap();
	private Map<String, ElementMapping> elementsByDst = Maps.newHashMap();

	public ClsMapping addCls(String src, String dst) {
		ElementMapping otherClsMap = elementsBySrc.get(src);
		if (otherClsMap != null && otherClsMap.getDstPath() != null)
			throw new DuplicateMappingException(src);
		if (dst != null) {
			otherClsMap = elementsByDst.get(dst);
			if (otherClsMap != null)
				throw new DuplicateMappingException(dst);
		}
		if (!src.contains("$")) {
			ClsMapping clsMap = otherClsMap != null ? new ClsMapping(otherClsMap.getSrcPath(), dst) :
					new ClsMapping(src, dst);
			elementsBySrc.put(src, clsMap);
			if (dst != null)
				elementsByDst.put(dst, clsMap);
			return clsMap;
		} else {
			int srcIdx = src.lastIndexOf('$');
			int dstIdx = (dst != null) ? dst.lastIndexOf('$') : -1;
			String parentSrc = (src.substring(0, srcIdx));
			ClsMapping parent = (ClsMapping) elementsBySrc.get(parentSrc);
			if (parent == null)
				parent = addCls(parentSrc, dst != null && dstIdx != 0 ? dst.substring(0, dstIdx) : null);
			ClsMapping clsMap = new ClsMapping(parent, src.substring(srcIdx + 1),
					dst != null ? dst.substring(dstIdx + 1) : null);
			if (otherClsMap != null) {
				otherClsMap.parent.children.remove(otherClsMap);
			}
			parent.children.add(clsMap);
			elementsBySrc.put(src, clsMap);
			if (dst != null)
				elementsByDst.put(dst, clsMap);
			return clsMap;
		}
	}

	public FldMapping addFld(String src, String dst) {
		return addFld(src, dst, null);
	}

	public FldMapping addFld(String src, String dst, String desc) {
		int srcIdx = src.lastIndexOf('/');
		int dstIdx = dst.lastIndexOf('/');
		String srcCls = src.substring(0, srcIdx);
		ClsMapping parent = (ClsMapping) elementsBySrc.get(srcCls);
		if (parent == null) {
			parent = (ClsMapping) elementsByDst.get(srcCls);
			if (parent == null)
				parent = addCls(srcCls, dst.substring(0, dstIdx));
		}
		FldMapping fldMap = new FldMapping(parent, src.substring(srcIdx + 1), dst.substring(dstIdx + 1),
				desc != null ? Desc.parse(this, desc) : null);
		elementsBySrc.put(src, fldMap);
		elementsByDst.put(dst, fldMap);
		parent.children.add(fldMap);
		return fldMap;
	}

	public MthdMapping addMthd(String src, String dst, String desc) {
		int srcIdx = src.lastIndexOf('/');
		int dstIdx = dst.lastIndexOf('/');
		String srcCls = src.substring(0, srcIdx);
		ClsMapping parent = (ClsMapping) elementsBySrc.get(srcCls);
		if (parent == null) {
			parent = (ClsMapping) elementsByDst.get(srcCls);
			if (parent == null)
				parent = addCls(srcCls, dst.substring(0, dstIdx));
		}
		MthdMapping mthdMap = new MthdMapping(parent, src.substring(srcIdx + 1), dst.substring(dstIdx + 1),
				Desc.parse(this, desc));
		elementsBySrc.put(src + mthdMap.getDesc().getSrc(), mthdMap);
		elementsByDst.put(dst + mthdMap.getDesc().getDst(), mthdMap);
		parent.children.add(mthdMap);
		return mthdMap;
	}

	public ElementMapping get(String name) {
		ElementMapping elem = elementsBySrc.get(name);
		if (elem == null)
			elem = elementsByDst.get(name);
		return elem;
	}

	public ClsMapping getCls(String name) {
		ElementMapping cls = get(name);
		if (cls instanceof ClsMapping)
			return (ClsMapping) cls;
		else
			return null;
	}

	public MthdMapping getMthd(String name, String desc) {
		ElementMapping clsMap = get(name + desc);
		if (clsMap instanceof MthdMapping)
			return (MthdMapping) clsMap;
		else
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

	public Set<ElementMapping> getAll() {
		return Sets.newHashSet(elementsBySrc.values());
	}
}
