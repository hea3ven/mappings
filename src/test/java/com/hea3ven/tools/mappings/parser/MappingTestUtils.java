package com.hea3ven.tools.mappings.parser;

import com.google.common.collect.ImmutableMap;

import com.hea3ven.tools.mappings.*;

public class MappingTestUtils {
	public static PkgMapping pkg(String nameObf) {
		return new PkgMapping(ImmutableMap.of(ObfLevel.OBF, nameObf));
	}

	public static PkgMapping pkg(String nameObf, String nameDeobf) {
		return new PkgMapping(ImmutableMap.of(ObfLevel.OBF, nameObf, ObfLevel.DEOBF, nameDeobf));
	}

	public static ClsMapping cls(PkgMapping pkg, String pathObf) {
		return new ClsMapping(pkg, ImmutableMap.of(ObfLevel.OBF, pathObf));
	}

	public static ClsMapping cls(PkgMapping pkg, String pathObf, String pathDeobf) {
		return new ClsMapping(pkg, ImmutableMap.of(ObfLevel.OBF, pathObf, ObfLevel.DEOBF, pathDeobf));
	}

	public static ClsMapping cls(ClsMapping parent, String pathObf) {
		return new ClsMapping(parent, ImmutableMap.of(ObfLevel.OBF, pathObf));
	}

	public static ClsMapping cls(ClsMapping parent, String pathObf, String pathDeobf) {
		return new ClsMapping(parent, ImmutableMap.of(ObfLevel.OBF, pathObf, ObfLevel.DEOBF, pathDeobf));
	}

	public static FldMapping fld(ClsMapping parent, String nameObf, TypeDesc desc) {
		return new FldMapping(parent, ImmutableMap.of(ObfLevel.OBF, nameObf),
				desc);
	}

	public static FldMapping fld(ClsMapping parent, String nameObf, String nameDeobf, TypeDesc desc) {
		return new FldMapping(parent, ImmutableMap.of(ObfLevel.OBF, nameObf, ObfLevel.DEOBF, nameDeobf),
				desc);
	}

	public static MthdMapping mthd(ClsMapping parent, String nameObf, Desc desc) {
		return new MthdMapping(parent, ImmutableMap.of(ObfLevel.OBF, nameObf),
				desc);
	}

	public static MthdMapping mthd(ClsMapping parent, String nameObf, String nameDeobf, Desc desc) {
		return new MthdMapping(parent, ImmutableMap.of(ObfLevel.OBF, nameObf, ObfLevel.DEOBF, nameDeobf),
				desc);
	}
}
