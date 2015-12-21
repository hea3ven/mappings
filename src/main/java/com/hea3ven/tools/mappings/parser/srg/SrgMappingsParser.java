package com.hea3ven.tools.mappings.parser.srg;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.hea3ven.tools.mappings.*;

public class SrgMappingsParser {
	private Mapping mapping = new Mapping();

	public Mapping add(InputStream stream) throws IOException {
		return add(new InputStreamReader(stream));
	}

	public Mapping add(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			parseLine(line);
		}
		return mapping;
	}

	private void parseLine(String line) {
		String[] parts = line.split(":");
		if (parts[0].equals("CL")) {
			parts = parts[1].trim().split("\\s");
			ClsMapping cls = mapping.getCls(parts[0]);
			cls.setDst((!parts[1].contains("$")) ? parts[1] : parts[1].split("\\$")[1]);
		} else if (parts[0].equals("FD")) {
			parts = parts[1].trim().split("\\s");
			int srcSplit = parts[0].lastIndexOf('/');
			int dstSplit = parts[1].lastIndexOf('/');
			String srcClsName = parts[0].substring(0, srcSplit);
			String srcFldName = parts[0].substring(srcSplit + 1);
			String dstClsName = parts[1].substring(0, dstSplit);
			String dstFldName = parts[1].substring(dstSplit + 1);
			ClsMapping cls = mapping.getCls(srcClsName);
			if (cls.getDstName() == null && !srcClsName.equals(dstClsName)) {
				if (!dstClsName.contains("$"))
					cls.setDst(dstClsName);
				else {
					if (cls.getParent().getDstName() == null)
						cls.getParent().setDst(dstClsName.split("\\$")[0]);
					cls.setDst(dstClsName.split("\\$")[1]);
				}
			}
			mapping.add(new FldMapping(cls, srcFldName, dstFldName, null));
		} else if (parts[0].equals("MD")) {
			parts = parts[1].trim().split("\\s");
			int srcSplit = parts[0].lastIndexOf('/');
			int dstSplit = parts[2].lastIndexOf('/');
			String srcClsName = parts[0].substring(0, srcSplit);
			String srcFldName = parts[0].substring(srcSplit + 1);
			String dstClsName = parts[2].substring(0, dstSplit);
			String dstFldName = parts[2].substring(dstSplit + 1);
			ClsMapping cls = mapping.getCls(srcClsName);
			if (cls.getDstName() == null && !srcClsName.equals(dstClsName)) {
				if (!dstClsName.contains("$"))
					cls.setDst(dstClsName);
				else {
					if (cls.getParent().getDstName() == null)
						cls.getParent().setDst(dstClsName.split("\\$")[0]);
					cls.setDst(dstClsName.split("\\$")[1]);
				}
			}
			mapping.add(new MthdMapping(cls, srcFldName, dstFldName, parseMethodDesc(parts[1])));
		}
	}

	private TypeDesc parseType(String typeData) {
		TypeDesc typ = BuiltInTypeDesc.get(typeData.charAt(0));
		if (typ == null) {
			if (typeData.charAt(0) == '[') {
				typ = new ArrayTypeDesc(parseType(typeData.substring(1)));
			} else {
				typ = new ClsTypeDesc(mapping.getCls(typeData.substring(1, typeData.indexOf(';'))));
			}
		}
		return typ;
	}

	private Desc parseMethodDesc(String descData) {
		if (descData.charAt(0) != '(')
			throw new SrgParserException("invalid method desc " + descData);
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
}
