package net.mcmt.mappings;

public abstract class ElementMapping {

	protected ElementMapping parent;
	private String src;
	private String dst;

	public ElementMapping(String src, String dst) {
		this(null, src, dst);
	}

	public ElementMapping(ElementMapping parent, String src, String dst) {
		this.parent = parent;
		this.src = validate(src);
		this.dst = validate(dst);
	}

	private String validate(String path) {
		path = path.replace('.', '/');
		for (int i = 0; i < path.length(); i++) {
			Character c = path.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != '_' && c != '/') {
				String msg = String.format("invalid character at position %d in %s", i, path);
				throw new InvalidCharacterMappingException(msg);
			}
		}
		return path;
	}

	protected abstract String getParentPathSep();

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
		if (parent != null)
			return parent.getDstPath() + getParentPathSep() + dst;
		return dst;
	}

	public String getDstScope() {
		if (parent != null)
			return parent.getDstPath();

		if (src.lastIndexOf('/') == -1)
			return null;
		return dst.substring(0, dst.lastIndexOf('/'));
	}

	public String getDstName() {
		return dst.substring(dst.lastIndexOf('/') + 1);
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
				&& src.equals(otherMapping.src) && dst.equals(otherMapping.dst);
	}

	@Override
	public int hashCode() {
		int hash = 27;
		hash = hash * 31 + src.hashCode();
		hash = hash * 31 + dst.hashCode();

		return hash;
	}
}
