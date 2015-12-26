package com.hea3ven.tools.mappings.parser.srg;

import java.io.*;

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
			mapping.add(new MthdMapping(cls, srcFldName, dstFldName, Desc.parse(mapping, parts[1])));
		}
	}
}
