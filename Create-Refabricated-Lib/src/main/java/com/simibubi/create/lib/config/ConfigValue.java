package com.simibubi.create.lib.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigValue<T> {
	public T value;
	public T defaultValue;
	public ConfigGroup group;
	public String key;
	public Constraint constraint;
	public List<String> comments = new ArrayList<>();
	// only for number-based values
	public Number min;
	public Number max;
	// only for enums
	public Class<T> clazz;

	public ConfigValue(String key, T value) {
		this.value = value;
		this.defaultValue = value;
		this.key = key;
	}

	public T get() {
		return value;
	}

	public boolean set(T value) {
		if (fitsConstraint(value)) {
			this.value = value;
			saveValue();
			return true;
		}
		return false;
	}

	public void saveValue() {
		group.config.set(this);
	}

	// specific setters

	public void setClass(Class<T> clazz) {
		this.clazz = clazz;
	}

	public void setMin(Number min) {
		this.min = min;
	}

	public void setMax(Number max) {
		this.max = max;
	}

	// comments

	public void addComment(String comment) {
		comments.add(comment);
	}

	public void addComments(String... comments) {
		for (String comment : comments) {
			addComment(comment);
		}
	}

	// group

	public void setGroup(ConfigGroup group) {
		this.group = group;
	}

	// constraints

	@FunctionalInterface
	public interface Constraint {
		boolean fits(Object newValue, ConfigValue value);
	}

	public boolean fitsConstraint(T newValue) {
		return constraint.fits(newValue, this);
	}

	public static final Constraint MIN_MAX = (newValue, value) -> ((double) newValue) >= ((double) value.min) && ((double) value.max) >= ((double) newValue);
	public static final Constraint TYPE = (newValue, value) -> newValue.getClass() == value.clazz;
}
