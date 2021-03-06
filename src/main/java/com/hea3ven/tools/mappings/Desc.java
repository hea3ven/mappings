package com.hea3ven.tools.mappings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Desc {

	private TypeDesc ret;
	private TypeDesc[] params;

	public Desc(TypeDesc ret) {
		this(ret, (TypeDesc[]) null);
	}

	public Desc(TypeDesc ret, TypeDesc... params) {
		this.ret = ret;
		this.params = params;
	}

	public String get(ObfLevel level) {
		StringBuilder sb = new StringBuilder();
		if (params != null) {
			sb.append('(');
			for (TypeDesc typDesc : params) {
				sb.append(typDesc.get(level));
			}
			sb.append(')');
		}
		sb.append(ret.get(level));
		return sb.toString();
	}

	public TypeDesc[] getParams() {
		return params;
	}

	public TypeDesc getReturn() {
		return ret;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (!(other instanceof Desc))
			return false;

		Desc otherDesc = (Desc) other;
		return ret.equals(otherDesc.ret) && Arrays.equals(params, otherDesc.params);
	}

	@Override
	public int hashCode() {
		int hash = 27;
		hash = hash * 31 + ret.hashCode();
		hash = hash * 31 + Arrays.hashCode(params);

		return hash;
	}

	public static Desc parse(Mapping mappings, String desc) {
		if (desc.charAt(0) != '(')
			return new Desc(parseType(mappings, desc));
		int i = 1;
		List<TypeDesc> params = new ArrayList<>();
		while (desc.charAt(i) != ')') {
			params.add(parseType(mappings, desc.substring(i)));
			i += typeDescLength(desc.substring(i));
		}

		return new Desc(parseType(mappings, desc.substring(i + 1)), params.toArray(new TypeDesc[0]));
	}

	private static TypeDesc parseType(Mapping mappings, String typeData) {
		TypeDesc typ = BuiltInTypeDesc.get(typeData.charAt(0));
		if (typ == null) {
			if (typeData.charAt(0) == '[') {
				typ = new ArrayTypeDesc(parseType(mappings, typeData.substring(1)));
			} else {
				String cls = typeData.substring(1, typeData.indexOf(';'));
				ClsMapping clsMap = mappings.getCls(cls, ObfLevel.OBF);
				if (clsMap == null)
					clsMap = mappings.addCls(cls, null);
				typ = new ClsTypeDesc(clsMap);
			}
		}
		return typ;
	}

	private static int typeDescLength(String typeData) {
		if (BuiltInTypeDesc.get(typeData.charAt(0)) != null)
			return 1;
		if (typeData.charAt(0) == '[')
			return 1;
		return typeData.indexOf(';') + 1;
	}
}
