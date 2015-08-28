package com.hea3ven.tools.mappings.parser.enigma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

import com.hea3ven.tools.mappings.ArgMapping;
import com.hea3ven.tools.mappings.ArrayTypeDesc;
import com.hea3ven.tools.mappings.BuiltInTypeDesc;
import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.ClsTypeDesc;
import com.hea3ven.tools.mappings.Desc;
import com.hea3ven.tools.mappings.ElementMapping;
import com.hea3ven.tools.mappings.FldMapping;
import com.hea3ven.tools.mappings.Mapping;
import com.hea3ven.tools.mappings.MthdMapping;
import com.hea3ven.tools.mappings.TypeDesc;

public class EnigmaMappingsParser {

	private static class ScopeEntry {
		public String indent;
		public ClsMapping cls;

		public ScopeEntry(String indent, ClsMapping cls) {
			this.indent = indent;
			this.cls = cls;
		}
	}

	private static class ScopeManager {

		private Stack<ScopeEntry> stack = new Stack<ScopeEntry>();

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

	private Mapping mapping = new Mapping();

	private HashMap<String, ClsMapping> missingClss = Maps.newHashMap();

	private ScopeManager scope = new ScopeManager();
	private MthdMapping currentMthd = null;

	private Pattern clsPattern = Pattern.compile("^(\\s*)CLASS\\s+(\\S+)\\s*(\\S*)\\s*$");
	private Pattern fldPattern = Pattern.compile("^(\\s*)FIELD\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*$");
	private Pattern mthdPattern = Pattern
			.compile("^(\\s*)METHOD\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*$");
	private Pattern argPattern = Pattern.compile("^(\\s*)ARG\\s+(\\d+)\\s+(\\S+)\\s*$");

	public Mapping add(InputStream stream) throws IOException {
		return add(new InputStreamReader(stream));
	}

	public Mapping add(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			parseLine(line);
		}
		for (ClsMapping cls : missingClss.values()) {
			if (mapping.getCls(cls.getSrcPath()) == null)
				mapping.add(cls);
		}
		return mapping;
	}

	private void parseLine(String line) {
		Matcher m = clsPattern.matcher(line);
		if (m.matches()) {
			ClsMapping cls = mapping.getCls(m.group(2).replace("none/", ""));
			cls.setDst((m.group(3) != null && !m.group(3).equals(""))
					? m.group(3).replace("none/", "") : null);
			scope.addScope(m.group(1), cls);
			return;
		}

		m = fldPattern.matcher(line);
		if (m.matches())

		{
			mapping.add(new FldMapping(scope.getScope(m.group(1)), m.group(2), m.group(3),
					new Desc(parseType(m.group(4)))));
		}

		m = mthdPattern.matcher(line);
		if (m.matches())

		{
			MthdMapping mthd = new MthdMapping(scope.getScope(m.group(1)), m.group(2), m.group(3),
					parseMethodDesc(m.group(4)));
			mapping.add(mthd);
			currentMthd = mthd;
		}

		m = argPattern.matcher(line);
		if (m.matches())

		{
			mapping.add(new ArgMapping(currentMthd, Integer.parseInt(m.group(2)), m.group(3)));
		}

	}

	private TypeDesc parseType(String typeData) {
		TypeDesc typ = BuiltInTypeDesc.get(typeData.charAt(0));
		if (typ == null) {
			if (typeData.charAt(0) == '[') {
				typ = new ArrayTypeDesc(parseType(typeData.substring(1)));
			} else {
				typ = new ClsTypeDesc(getCls(typeData.substring(1, typeData.indexOf(';'))));
			}
		}
		return typ;
	}

	private Desc parseMethodDesc(String descData) {
		if (descData.charAt(0) != '(')
			throw new EnigmaParserException("invalid method desc " + descData);
		int i = 1;
		List<TypeDesc> params = new ArrayList<TypeDesc>();
		while (descData.charAt(i) != ')') {
			params.add(parseType(descData.substring(i)));
			i += typeDescLenght(descData.substring(i));

		}

		return new Desc(parseType(descData.substring(i + 1)), params.toArray(new TypeDesc[0]));
	}

	private int typeDescLenght(String typeData) {
		if (BuiltInTypeDesc.get(typeData.charAt(0)) != null)
			return 1;
		if (typeData.charAt(0) == '[')
			return 1;
		return typeData.indexOf(';') + 1;
	}

	private ClsMapping getCls(String name) {
		name = name.replace("none/", "");
		return mapping.getCls(name);
	}

	public void write(Mapping mapping, Writer writer) throws IOException {
		List<ClsMapping> out = new ArrayList<ClsMapping>();
		for (ElementMapping elem : mapping.getAll()) {
			if (elem instanceof ClsMapping) {
				ClsMapping clsMap = (ClsMapping) elem;
				if (clsMap.getParent() == null) {
					out.add(clsMap);
				}
			}
		}
		out.sort(new Comparator<ClsMapping>() {
			@Override
			public int compare(ClsMapping o1, ClsMapping o2) {
				return o1.getSrcPath().compareTo(o2.getSrcPath());
			}
		});

		for (ClsMapping clsMap : out) {
			writeCls(writer, clsMap, "");
		}
	}

	protected void writeCls(Writer writer, ClsMapping clsMap, String indent) throws IOException {
		if (clsMap.getDstName() == null && clsMap.getChildren().size() == 0)
			return;

		String srcPath = null;
		if (!topParentHasPkg(clsMap)) {
			if (indent.equals(""))
				srcPath = "none/" + clsMap.getSrcName();
			else
				srcPath = "none/" + clsMap.getSrcPath();
		} else {
			srcPath = clsMap.getSrcPath();
		}
		String dstPath = null;
		if (!clsMap.getSrcPath().equals(clsMap.getDstPath())) {
			if (indent.equals(""))
				dstPath = (clsMap.getDstPath() != null) ? clsMap.getDstPath() : "";
			else
				dstPath = clsMap.getDstName();
		} else
			dstPath = "";
		writer.write(String.format("%sCLASS %s %s\n", indent, srcPath, dstPath));

		for (ElementMapping elem : clsMap.getChildren()) {
			if (elem instanceof MthdMapping) {
				MthdMapping mthdMap = (MthdMapping) elem;
				writer.write(String.format("%s\tMETHOD %s %s %s\n", indent, mthdMap.getSrcName(),
						mthdMap.getDstName(), descToString(mthdMap.getDesc())));
			} else if (elem instanceof FldMapping) {
				FldMapping fldMap = (FldMapping) elem;
				writer.write(String.format("%s\tFIELD %s %s %s\n", indent, fldMap.getSrcName(),
						fldMap.getDstName(), descToString(fldMap.getDesc())));
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
					return "Lnone/" + clsTypDesc.getCls().getSrcName() + ";";
				else
					return "Lnone/" + clsTypDesc.getCls().getSrcPath() + ";";
			}
		} else if (param instanceof ArrayTypeDesc) {
			return "[" + typeDescToString(((ArrayTypeDesc) param).getDescType());
		}

		return param.getSrc();
	}

	private boolean topParentHasPkg(ClsMapping clsMap) {
		if (clsMap.getParent() != null)
			return topParentHasPkg(clsMap.getParent());
		return clsMap.getSrcScope() != null;
	}
}
