package com.hea3ven.tools.mappings;

public class BuiltInTypeDesc extends TypeDesc {

	public static final BuiltInTypeDesc VOID = new BuiltInTypeDesc('V');
	public static final BuiltInTypeDesc BOOLEAN = new BuiltInTypeDesc('Z');
	public static final BuiltInTypeDesc BYTE = new BuiltInTypeDesc('B');
	public static final BuiltInTypeDesc SHORT = new BuiltInTypeDesc('S');
	public static final BuiltInTypeDesc INTEGER = new BuiltInTypeDesc('I');
	public static final BuiltInTypeDesc LONG = new BuiltInTypeDesc('J');
	public static final BuiltInTypeDesc FLOAT = new BuiltInTypeDesc('F');
	public static final BuiltInTypeDesc DOUBLE = new BuiltInTypeDesc('D');
	public static final BuiltInTypeDesc CHARACTER = new BuiltInTypeDesc('C');

	private static final BuiltInTypeDesc[] TYPES = new BuiltInTypeDesc[] {VOID, BOOLEAN, BYTE,
			SHORT, INTEGER, LONG, FLOAT, DOUBLE, CHARACTER};

	public static TypeDesc get(Character desc) {
		for (BuiltInTypeDesc typ : TYPES) {
			if (typ.desc == desc)
				return typ;
		}
		return null;
	}

	private char desc;

	public BuiltInTypeDesc(char desc) {
		this.desc = desc;
	}

	@Override
	public String get(ObfLevel level) {
		return String.valueOf(desc);
	}
}
