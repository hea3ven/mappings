package net.mcmt.mappings.parser.enigma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

import net.mcmt.mappings.ArgMapping;
import net.mcmt.mappings.ArrayTypeDesc;
import net.mcmt.mappings.BuiltInTypeDesc;
import net.mcmt.mappings.ClsMapping;
import net.mcmt.mappings.ClsTypeDesc;
import net.mcmt.mappings.Desc;
import net.mcmt.mappings.ElementMapping;
import net.mcmt.mappings.FldMapping;
import net.mcmt.mappings.Mapping;
import net.mcmt.mappings.MthdMapping;
import net.mcmt.mappings.TypeDesc;

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

	private Pattern clsPattern = Pattern
			.compile("^(\\s*)CLASS\\s+((\\S+)\\$)?(\\S+)\\s*(\\S*)\\s*$");
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
			ClsMapping cls = null;
			if (m.group(3) != null) {
				ClsMapping parent = mapping.getCls(m.group(3));
				if (parent == null) {
					parent = getCls(m.group(3));
				}
				cls = new ClsMapping(parent, m.group(4), (m.group(5) != null && !m.group(5).equals(""))? m.group(5) : m.group(4));
				mapping.add(cls);
			} else {
				cls = new ClsMapping(m.group(4).replace("none/", ""),
						((m.group(5) != null && !m.group(5).equals(""))? m.group(5) : m.group(4)).replace("none/", ""));
				mapping.add(cls);
			}
			scope.addScope(m.group(1), cls);
			return;
		}

		m = fldPattern.matcher(line);
		if (m.matches()) {
			mapping.add(new FldMapping(scope.getScope(m.group(1)), m.group(2), m.group(3),
					new Desc(parseType(m.group(4)))));
		}

		m = mthdPattern.matcher(line);
		if (m.matches()) {
			MthdMapping mthd = new MthdMapping(scope.getScope(m.group(1)), m.group(2), m.group(3),
					parseMethodDesc(m.group(4)));
			mapping.add(mthd);
			currentMthd = mthd;
		}

		m = argPattern.matcher(line);
		if (m.matches()) {
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
		ClsMapping cls = mapping.getCls(name);
		if (cls != null)
			return cls;

		if (!missingClss.containsKey(name)) {
			if (name.contains("$")) {
				String[] parts = name.split("\\$", 2);
				missingClss.put(name, new ClsMapping(getCls(parts[0]), parts[1], parts[1]));
			} else
				missingClss.put(name, new ClsMapping(name, name));
		}
		return missingClss.get(name);
	}

}
