package com.hea3ven.tools.mappings;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ElementMapping {

	protected final ElementMapping parent;
	@Nonnull
	private final Map<ObfLevel, String> names;

	public ElementMapping(ElementMapping parent, Map<ObfLevel, String> names) {
		if (names == null)
			throw new IllegalArgumentException("names can not be null");
		this.names = names;
		this.parent = parent;
	}

	public ElementMapping getParent() {
		return parent;
	}

	protected abstract String getParentPathSep();

	public String getPath(ObfLevel level) {
		if (!names.containsKey(level))
			return null;
		if (parent != null) {
			String parentPath = parent.getPath(level);
			if (parentPath != null)
				return parentPath + getParentPathSep() + getName(level);
		}
		return getName(level);
	}

	public String getName(ObfLevel level) {
		return names.get(level);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (!(other instanceof ElementMapping))
			return false;

		ElementMapping otherMapping = (ElementMapping) other;

		if (!otherMapping.canEqual(this))
			return false;

		return ((parent == null && otherMapping.parent == null) ||
				(parent != null && parent.equals(otherMapping.parent))) &&
				names.keySet().equals(otherMapping.names.keySet()) &&
				names.entrySet()
						.stream()
						.allMatch(entry -> entry.getValue().equals(otherMapping.getName(entry.getKey())));
	}

	@Override
	public int hashCode() {
		int hash = 27;
		for (Entry<ObfLevel, String> entry : names.entrySet()) {
			hash = hash * 31 + entry.getKey().hashCode();
			hash = hash * 31 + entry.getValue().hashCode();
		}
		if (parent != null)
			hash = hash * 31 + parent.hashCode();
		return hash;
	}

	public boolean canEqual(Object other) {
		return false;
	}

	void updateNames(Map<ObfLevel, Path> names) {

		for (ObfLevel level : names.keySet()) {
			if (this.names.containsKey(level)) {
				if (!this.names.get(level).equals(names.get(level).getName()))
					throw new DuplicateMappingException("cannot overwrite a mapping");
			} else {
				this.names.put(level, names.get(level).getName());
			}
		}
		if (parent != null)
			parent.updateNames(names.entrySet()
					.stream()
					.filter(e -> e.getValue().getParent() != null)
					.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getParent())));
	}
}
