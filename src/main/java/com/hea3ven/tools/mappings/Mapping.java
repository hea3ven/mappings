package com.hea3ven.tools.mappings;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import com.hea3ven.tools.mappings.Path.PartType;

public class Mapping {

	private Set<PkgMapping> pkgs = new HashSet<>();
	private Set<ClsMapping> clss = new HashSet<>();
	private Set<FldMapping> flds = new HashSet<>();
	private Set<MthdMapping> mthds = new HashSet<>();

	public PkgMapping addPkg(String src, String dst) {
		return addPkg(ImmutableMap.of(ObfLevel.OBF, src, ObfLevel.DEOBF, dst));
	}

	public PkgMapping addPkg(Map<ObfLevel, String> paths) {
		return addPkgPaths(paths.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> Path.parse(e.getValue() + "/"))));
	}

	private PkgMapping addPkgPaths(Map<ObfLevel, Path> paths) {
		for (Entry<ObfLevel, Path> entry : paths.entrySet()) {
			PkgMapping pkg;
			pkg = getPkg(entry.getValue().getPath(), entry.getKey());
			if (pkg != null) {
				pkg.updateNames(paths.entrySet().stream()
						.filter(e -> e.getKey() != entry.getKey())
						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
				return pkg;
			}
		}

		PkgMapping pkg = new PkgMapping(paths.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getName())));
		pkgs.add(pkg);
		return pkg;
	}

	public ClsMapping addCls(String src, String dst) {
		return addCls(ImmutableMap.of(ObfLevel.OBF, src, ObfLevel.DEOBF, dst));
	}

	public ClsMapping addCls(Map<ObfLevel, String> paths) {
		return addClsPaths(paths.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> Path.parse(e.getValue()))));
	}

	private ClsMapping addClsPaths(Map<ObfLevel, Path> paths) {
		for (Entry<ObfLevel, Path> entry : paths.entrySet()) {
			ClsMapping cls;
			cls = getCls(entry.getValue().getPath(), entry.getKey());
			if (cls != null) {
				cls.updateNames(paths.entrySet().stream()
						.filter(e -> e.getKey() != entry.getKey())
						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
				return cls;
			}
		}

		Map<ObfLevel, String> names = paths.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getName()));

		ClsMapping cls;
		if (paths.values().stream().anyMatch(p -> p.getParent() != null)) {
			Map<ObfLevel, Path> parentPaths = paths.entrySet()
					.stream()
					.filter(e -> e.getValue().getParent() != null &&
							e.getValue().getParent().getName() != null)
					.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getParent()));
			if (parentPaths.values().stream().anyMatch(p -> p.getType() == PartType.CLASS)) {
				cls = new ClsMapping(addClsPaths(parentPaths), names);
			} else {
				cls = new ClsMapping(addPkgPaths(parentPaths), names);
			}
		} else {
			cls = new ClsMapping(names);
		}
		clss.add(cls);
		return cls;
	}

	public FldMapping addFld(String src, String dst, ObfLevel descObfLevel, String typeDesc) {
		return addFld(ImmutableMap.of(ObfLevel.OBF, src, ObfLevel.DEOBF, dst), descObfLevel, typeDesc);
	}

	public FldMapping addFld(Map<ObfLevel, String> paths, ObfLevel descObfLevel, String typeDesc) {
		return addFldPaths(paths.entrySet()
						.stream()
						.collect(Collectors.toMap(e -> e.getKey(), e -> Path.parse(e.getValue()))), descObfLevel,
				typeDesc);
	}

	public FldMapping addFldPaths(Map<ObfLevel, Path> paths, ObfLevel descObfLevel, String typeDesc) {
		Optional<Path> errorPath =
				paths.values().stream().filter(e -> e.getType() != PartType.MEMBER).findAny();
		if (errorPath.isPresent())
			throw new IllegalArgumentException("The path '" + errorPath.get().getPath() + "' is not a field");

		Path srcPath = paths.get(ObfLevel.OBF);
		for (Entry<ObfLevel, Path> entry : paths.entrySet()) {
			FldMapping fld;
			fld = getFld(entry.getValue().getPath(), entry.getKey());
			if (fld != null) {
				fld.updateNames(paths.entrySet().stream()
						.filter(e -> e.getKey() != entry.getKey())
						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
				return fld;
			}
		}

		Map<ObfLevel, String> names = paths.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getName()));

		Map<ObfLevel, Path> parentPaths = paths.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getParent()));
		FldMapping fld =
				new FldMapping(addClsPaths(parentPaths), names, parseTypeDesc(descObfLevel, typeDesc));
		flds.add(fld);
		return fld;
	}

	public MthdMapping addMthd(String src, String dst, ObfLevel descObfLevel, String desc) {
		return addMthd(ImmutableMap.of(ObfLevel.OBF, src, ObfLevel.DEOBF, dst), descObfLevel, desc);
	}

	public MthdMapping addMthd(Map<ObfLevel, String> paths, ObfLevel descObfLevel, String desc) {
		return addMthdPaths(paths.entrySet()
						.stream()
						.collect(Collectors.toMap(e -> e.getKey(), e -> Path.parse(e.getValue()))), descObfLevel,
				desc);
	}

	public MthdMapping addMthdPaths(Map<ObfLevel, Path> paths, ObfLevel descObfLevel, String desc) {
		Optional<Path> errorPath =
				paths.values().stream().filter(e -> e.getType() != PartType.MEMBER).findAny();
		if (errorPath.isPresent())
			throw new IllegalArgumentException(
					"The path '" + errorPath.get().getPath() + "' is not a method");

		Path srcPath = paths.get(ObfLevel.OBF);
		for (Entry<ObfLevel, Path> entry : paths.entrySet()) {
			MthdMapping mthd;
			mthd = getMthd(entry.getValue().getPath(), desc, entry.getKey());
			if (mthd != null) {
				mthd.updateNames(paths.entrySet().stream()
						.filter(e -> e.getKey() != entry.getKey())
						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
				return mthd;
			}
		}

		Map<ObfLevel, String> names = paths.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getName()));

		Map<ObfLevel, Path> parentPaths = paths.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getParent()));
		MthdMapping mthd = new MthdMapping(addClsPaths(parentPaths), names, parseDesc(descObfLevel, desc));
		mthds.add(mthd);
		return mthd;
	}

	public PkgMapping getPkg(String name, ObfLevel level) {
		return pkgs.stream()
				.filter(e -> e.getPath(level) != null && e.getPath(level).equals(name))
				.findFirst()
				.orElse(null);
	}

	public ClsMapping getCls(String name, ObfLevel level) {
		return clss.stream()
				.filter(e -> e.getPath(level) != null && e.getPath(level).equals(name))
				.findFirst()
				.orElse(null);
	}

	public MthdMapping getMthd(String name, String desc, ObfLevel level) {
		return mthds.stream()
				.filter(e -> e.getPath(level) != null && e.getPath(level).equals(name) &&
						e.getDesc().get(level).equals(desc))
				.findFirst()
				.orElse(null);
	}

	public FldMapping getFld(String name, ObfLevel level) {
		return flds.stream()
				.filter(e -> e.getPath(level) != null && e.getPath(level).equals(name))
				.findFirst()
				.orElse(null);
	}

//	public ArgMapping getArg(String name) {
//		ElementMapping arg = get(name);
//		if (arg instanceof ArgMapping)
//			return (ArgMapping) arg;
//		else
//			return null;
//	}

	public Set<ElementMapping> getAll() {
		Set<ElementMapping> result = new HashSet<>();
		result.addAll(pkgs);
		result.addAll(clss);
		result.addAll(flds);
		result.addAll(mthds);
		return result;
	}

	public Desc parseDesc(ObfLevel descObfLevel, String desc) {
		if (desc.charAt(0) != '(')
			return new Desc(parseTypeDesc(descObfLevel, desc));
		int i = 1;
		List<TypeDesc> params = new ArrayList<>();
		while (desc.charAt(i) != ')') {
			params.add(parseTypeDesc(descObfLevel, desc.substring(i)));
			i += typeDescLength(desc.substring(i));
		}

		return new Desc(parseTypeDesc(descObfLevel, desc.substring(i + 1)), params.toArray(new TypeDesc[0]));
	}

	private static int typeDescLength(String typeData) {
		if (BuiltInTypeDesc.get(typeData.charAt(0)) != null)
			return 1;
		if (typeData.charAt(0) == '[')
			return 1;
		return typeData.indexOf(';') + 1;
	}

	public TypeDesc parseTypeDesc(ObfLevel descObfLevel, String typeDesc) {
		TypeDesc typ = BuiltInTypeDesc.get(typeDesc.charAt(0));
		if (typ == null) {
			if (typeDesc.charAt(0) == '[') {
				typ = new ArrayTypeDesc(parseTypeDesc(descObfLevel, typeDesc.substring(1)));
			} else {
				String cls = typeDesc.substring(1, typeDesc.indexOf(';'));
				ClsMapping clsMap = getCls(cls, descObfLevel);
				if (clsMap == null)
					clsMap = addCls(ImmutableMap.of(descObfLevel, cls));
				typ = new ClsTypeDesc(clsMap);
			}
		}
		return typ;
	}

	public Set<ElementMapping> getChildren(ClsMapping clsMap) {
		return Stream.concat(Stream.concat(clss.stream(), flds.stream()), mthds.stream())
				.filter(c -> c.getParent() == clsMap)
				.collect(Collectors.toSet());
	}
}
