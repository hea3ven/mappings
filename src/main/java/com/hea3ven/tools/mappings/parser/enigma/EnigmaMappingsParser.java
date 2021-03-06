package com.hea3ven.tools.mappings.parser.enigma;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.hea3ven.tools.mappings.*;
import com.hea3ven.tools.mappings.parser.IMappingsParser;

public class EnigmaMappingsParser implements IMappingsParser {

	private static class ScopeEntry {
		public String indent;
		public ClsMapping cls;

		public ScopeEntry(String indent, ClsMapping cls) {
			this.indent = indent;
			this.cls = cls;
		}
	}

	private static class ScopeManager {

		private Stack<ScopeEntry> stack = new Stack<>();

		public void addScope(String indent, ClsMapping cls) {
			while (!stack.isEmpty() && stack.peek().indent.length() > indent.length())
				stack.pop();
			stack.push(new ScopeEntry(indent, cls));
		}

		public ClsMapping getScope(String indent) {
			while (stack.peek().indent.length() >= indent.length())
				stack.pop();
			return stack.peek().cls;
		}
	}

	private Mapping mapping;

	private ScopeManager scope = new ScopeManager();
//	private MthdMapping currentMthd = null;

	private Pattern clsPattern = Pattern.compile("^(\\s*)CLASS\\s+(\\S+)\\s*(\\S*)\\s*$");
	private Pattern fldPattern = Pattern.compile("^(\\s*)FIELD\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*$");
	private Pattern mthdPattern = Pattern.compile("^(\\s*)METHOD\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*$");
	private Pattern argPattern = Pattern.compile("^(\\s*)ARG\\s+(\\d+)\\s+(\\S+)\\s*$");

	public EnigmaMappingsParser() {
		this(new Mapping());
	}

	public EnigmaMappingsParser(Mapping mapping) {
		this.mapping = mapping;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public void parse(InputStream stream) throws IOException {
		parse(new InputStreamReader(stream));
	}

	public void parse(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			parseLine(line);
		}
	}

	private void parseLine(String line) {
		Matcher m = clsPattern.matcher(line);
		if (m.matches()) {
			String dst = null;
			String src = m.group(2).replace("none/", "");
			if (m.group(3) != null && !m.group(3).equals(""))
				dst = m.group(3).replace("none/", "");
			if (dst != null && src.contains("$")) {
				dst = "$" + dst;
			}
			ClsMapping cls = mapping.addCls(src, dst);
			scope.addScope(m.group(1), cls);
			return;
		}

		m = fldPattern.matcher(line);
		if (m.matches()) {
			ClsMapping cls = this.scope.getScope(m.group(1));
			mapping.addFld(cls.getPath(ObfLevel.OBF) + "." + m.group(2),
					cls.getPath(ObfLevel.DEOBF) + "." + m.group(3), ObfLevel.OBF, m.group(4));
		}

		m = mthdPattern.matcher(line);
		if (m.matches()) {
			ClsMapping cls = this.scope.getScope(m.group(1));
//			currentMthd =
			mapping.addMthd(cls.getPath(ObfLevel.OBF) + "." + m.group(2),
					cls.getPath(ObfLevel.DEOBF) + "." + m.group(3), ObfLevel.OBF, m.group(4));
		}

//		m = argPattern.matcher(line);
//		if (m.matches()) {
//			mapping.parse(new ArgMapping(currentMthd, Integer.parseInt(m.group(2)), m.group(3)));
//		}
	}

	private ClsMapping getCls(String name) {
		name = name.replace("none/", "");
		return mapping.getCls(name, ObfLevel.OBF);
	}

	public void write(OutputStream stream) throws IOException {
		write(new OutputStreamWriter(stream));
	}

	public void write(Writer writer) throws IOException {
		List<ClsMapping> out = mapping.getAll()
				.stream()
				.filter(elem -> elem instanceof ClsMapping &&
						(elem.getParent() == null || elem.getParent() instanceof PkgMapping))
				.sorted((o1, o2) -> o1.getPath(ObfLevel.OBF).compareTo(o2.getPath(ObfLevel.OBF)))
				.map(elem -> (ClsMapping) elem)
				.collect(Collectors.toList());

		for (ClsMapping clsMap : out) {
			writeCls(writer, clsMap, "");
		}
	}

	protected void writeCls(Writer writer, ClsMapping clsMap, String indent) throws IOException {
		if (clsMap.getName(ObfLevel.DEOBF) == null && mapping.getChildren(clsMap).size() == 0)
			return;

		String srcPath;
		if (!topParentHasPkg(clsMap)) {
			if (indent.equals(""))
				srcPath = "none/" + clsMap.getName(ObfLevel.OBF);
			else
				srcPath = "none/" + clsMap.getPath(ObfLevel.OBF);
		} else {
			srcPath = clsMap.getPath(ObfLevel.OBF);
		}
		String dstPath;
		if (!clsMap.getPath(ObfLevel.OBF).equals(clsMap.getPath(ObfLevel.DEOBF))) {
			if (indent.equals(""))
				dstPath = (clsMap.getPath(ObfLevel.DEOBF) != null) ? clsMap.getPath(ObfLevel.DEOBF) : "";
			else
				dstPath = clsMap.getName(ObfLevel.DEOBF);
		} else
			dstPath = "";
		writer.write(String.format("%sCLASS %s %s\n", indent, srcPath, dstPath));

		for (ElementMapping elem : mapping.getChildren(clsMap)) {
			if (elem instanceof MthdMapping) {
				MthdMapping mthdMap = (MthdMapping) elem;
				writer.write(String.format("%s\tMETHOD %s %s %s\n", indent, mthdMap.getName(ObfLevel.OBF),
						mthdMap.getName(ObfLevel.DEOBF), descToString(mthdMap.getDesc())));
			} else if (elem instanceof FldMapping) {
				FldMapping fldMap = (FldMapping) elem;
				if (fldMap.getDesc() != null)
					writer.write(String.format("%s\tFIELD %s %s %s\n", indent, fldMap.getName(ObfLevel.OBF),
							fldMap.getName(ObfLevel.DEOBF), typeDescToString(fldMap.getDesc())));
				else
					writer.write(String.format("%s\tFIELD %s %s\n", indent, fldMap.getName(ObfLevel.OBF),
							fldMap.getName(ObfLevel.DEOBF)));
			} else if (elem instanceof ClsMapping) {
				ClsMapping innerClsMap = (ClsMapping) elem;
				writeCls(writer, innerClsMap, indent + "\t");
			}
		}
	}

	private String descToString(Desc desc) {
		StringBuilder sb = new StringBuilder();
		if (desc.getParams() != null) {
			sb.append('(');
			for (TypeDesc param : desc.getParams()) {
				sb.append(typeDescToString(param));
			}
			sb.append(')');
		}
		sb.append(typeDescToString(desc.getReturn()));
		return sb.toString();
	}

	private String typeDescToString(TypeDesc param) {
		if (param instanceof ClsTypeDesc) {
			ClsTypeDesc clsTypDesc = (ClsTypeDesc) param;
			if (!topParentHasPkg(clsTypDesc.getCls())) {
				if (clsTypDesc.getCls().getParent() == null)
					return "Lnone/" + clsTypDesc.getCls().getName(ObfLevel.OBF) + ";";
				else
					return "Lnone/" + clsTypDesc.getCls().getPath(ObfLevel.OBF) + ";";
			}
		} else if (param instanceof ArrayTypeDesc) {
			return "[" + typeDescToString(((ArrayTypeDesc) param).getDescType());
		}

		return param.get(ObfLevel.OBF);
	}

	private boolean topParentHasPkg(ClsMapping clsMap) {
		if (clsMap.getParent() != null) {
			if (clsMap.getParent() instanceof PkgMapping)
				return clsMap.getParent().getName(ObfLevel.OBF) != null;
			else
				return topParentHasPkg((ClsMapping) clsMap.getParent());
		}
		return false;
	}

	/**
	 * @deprecated Use {@link #parse(InputStream)}.
	 */
	@Deprecated
	public Mapping add(InputStream stream) throws IOException {
		parse(stream);
		return getMapping();
	}

	/**
	 * @deprecated Use {@link #parse(Reader)}.
	 */
	@Deprecated
	public Mapping add(Reader reader) throws IOException {
		parse(reader);
		return getMapping();
	}

	/**
	 * @deprecated Use {@link #write(Writer)}.
	 */
	@Deprecated
	public void write(Mapping mapping, Writer writer) throws IOException {
		setMapping(mapping);
		write(writer);
	}
}
