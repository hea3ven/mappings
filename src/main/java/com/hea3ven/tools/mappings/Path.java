package com.hea3ven.tools.mappings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Path {
	private final Path parent;
	@Nonnull
	private final PartType type;
	@Nonnull
	private final String name;

	public static Path parse(String path) {
		int sep = path.lastIndexOf('.');
		if (sep != -1)
			return new Path(parse(path.substring(0, sep)), path.substring(sep + 1), PartType.MEMBER);
		sep = path.indexOf('$');
		if (sep != -1)
			return new Path(parse(path.substring(0, sep)), path.substring(sep + 1), PartType.CLASS);
		sep = path.lastIndexOf('/');
		if (sep == -1 || sep != path.length() - 1)
			return new Path(sep != -1 ? parse(path.substring(0, sep + 1)) : null,
					path.length() > 0 ? (sep == -1 ? path : path.substring(sep + 1)) : null, PartType.CLASS);
		if (sep == path.length() - 1) {
			path = path.substring(0, path.length() - 1);
			sep = path.lastIndexOf('/');
		}
		return new Path( null, path, PartType.PACKAGE);
	}

//	public static List<Path> parse(String path) {
//		String[] parts = path.split("\\$");
//		List<Path> result = new ArrayList<>();
//		for (String part : parts)
//			result.add(parseSimple(part));
//		return result;
//	}

	public Path(Path parent, @Nonnull String name, PartType type) {
		this.parent = parent;
		this.name = name;
		this.type = type;
		if(type == PartType.PACKAGE && parent != null)
			throw new IllegalArgumentException("packages can not have parents");
	}

	public Path getParent() {
		return parent;
	}

	public String getPath() {
		if (parent == null)
			return name;
		String sep = "";
		switch (type) {
			case PACKAGE:
				sep = "/";
				break;
			case CLASS:
				sep = (parent.getType() == PartType.CLASS) ? "$" : "/";
				break;
			case MEMBER:
				sep = ".";
				break;
		}
		return parent.getPath() + sep + name;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Path))
			return false;
		Path otherPath = (Path) obj;
		return StringUtils.equals(name, otherPath.name) && type == otherPath.type &&
				(parent == null ? otherPath.parent == null : parent.equals(otherPath.parent));
	}

	@Override
	public final int hashCode() {
		int hash = 27;
		hash = hash * 31 + name.hashCode();
		hash = hash * 31 + type.hashCode();
		if (parent != null)
			hash = hash * 31 + parent.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return "Path(" + getPath() + ")";
	}

	public PartType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public enum PartType {
		PACKAGE,
		CLASS,
		MEMBER
	}
}
