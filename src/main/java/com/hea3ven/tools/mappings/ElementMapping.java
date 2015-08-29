package com.hea3ven.tools.mappings;

import java.util.HashSet;
import java.util.Set;

public abstract class ElementMapping {

	protected ElementMapping parent;
	protected Set<ElementMapping> children;
	private String src;
	private String dst;

	public ElementMapping(String src, String dst) {
		this(null, src, dst);
	}

	public ElementMapping(ElementMapping parent, String src, String dst) {
		this.parent = parent;
		this.src = validate(src);
		this.dst = validate(dst);
		this.children = new HashSet<ElementMapping>();
	}

	private String validate(String path) {
		if (path != null) {
			path = path.replace('.', '/');
			for (int i = 0; i < path.length(); i++) {
				Character c = path.charAt(i);
				if (!Character.isLetterOrDigit(c) && c != '_' && c != '/' && c != '<' && c != '>') {
					String msg = String.format("invalid character at position %d in %s", i, path);
					throw new InvalidCharacterMappingException(msg);
				}
			}
		}
		return path;
	}

	protected abstract String getParentPathSep();

	public void setDst(String dst) {
		this.dst = validate(dst);
	}

	public String getPath(boolean src) {
		return src ? getSrcPath() : getDstPath();
	}

	public String getName(boolean src) {
		return src ? getSrcName() : getDstName();
	}

	public String getSrcPath() {
		if (parent != null)
			return parent.getSrcPath() + getParentPathSep() + src;
		return src;
	}

	public String getSrcScope() {
		if (parent != null)
			return parent.getSrcPath();

		if (src.lastIndexOf('/') == -1)
			return null;
		return src.substring(0, src.lastIndexOf('/'));
	}

	public String getSrcName() {
		return src.substring(src.lastIndexOf('/') + 1);
	}

	public String getDstPath() {
		if (dst == null || (parent != null && parent.getDstPath() == null))
			return null;

		if (parent != null)
			return parent.getDstPath() + getParentPathSep() + dst;
		return dst;
	}

	public String getDstScope() {
		if (dst == null)
			return null;

		if (parent != null)
			return parent.getDstPath();

		if (src.lastIndexOf('/') == -1)
			return null;
		return dst.substring(0, dst.lastIndexOf('/'));
	}

	public String getDstName() {
		if (dst == null)
			return null;
		return dst.substring(dst.lastIndexOf('/') + 1);
	}

	public void addChild(ElementMapping child) {
		children.add(child);
	}

	public Set<ElementMapping> getChildren() {
		return children;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (!(other instanceof ElementMapping))
			return false;

		ElementMapping otherMapping = (ElementMapping) other;
		return ((parent == null && otherMapping.parent == null)
				|| (parent != null && parent.equals(otherMapping.parent)))
				&& src.equals(otherMapping.src)
				&& ((dst == null && otherMapping.dst == null) || dst.equals(otherMapping.dst));
	}

	@Override
	public int hashCode() {
		int hash = 27;
		hash = hash * 31 + src.hashCode();
		if (dst != null)
			hash = hash * 31 + dst.hashCode();

		return hash;
	}
}
