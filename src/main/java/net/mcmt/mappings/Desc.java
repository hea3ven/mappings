package net.mcmt.mappings;

import java.util.Arrays;

public class Desc {

	private TypeDesc ret;
	private TypeDesc[] params;

	public Desc(TypeDesc ret, TypeDesc... params) {
		this.ret = ret;
		this.params = params;
	}

	public String getSrc() {
		StringBuilder sb = new StringBuilder();
		if (params.length > 0) {
			sb.append('(');
			for (TypeDesc typDesc : params) {
				sb.append(typDesc.getSrc());
			}
			sb.append(')');
		}
		sb.append(ret.getSrc());
		return sb.toString();
	}

	public String getDst() {
		StringBuilder sb = new StringBuilder();
		if (params.length > 0) {
			sb.append('(');
			for (TypeDesc typDesc : params) {
				sb.append(typDesc.getDst());
			}
			sb.append(')');
		}
		sb.append(ret.getDst());
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
}
