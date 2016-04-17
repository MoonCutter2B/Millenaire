package org.millenaire.common;

import java.lang.reflect.Field;

import org.millenaire.common.forge.Mill;

public class MillConfig {

	private static final Object[] BOOLEAN_VALS = new Object[] { Boolean.TRUE, Boolean.FALSE };

	public static final int LANGUAGE = 1, EDITABLE_STRING = 2, KEY = 3, EDITABLE_INTEGER = 4, LOG = 5, BONUS_KEY = 6;

	private static String getBooleanString(final boolean b) {
		if (b) {
			return MLN.string("config.valuetrue");
		}
		return MLN.string("config.valuefalse");
	}

	final Field field;
	final public String key;
	public Object defaultVal;

	private Object[] possibleVals;
	public int special = 0, strLimit = 20;
	public boolean displayConfig = true;

	public boolean displayConfigDev = false;

	public MillConfig(final Field field, final String key, final int special) {
		this.field = field;
		this.special = special;
		this.key = key.toLowerCase();

		if (special == LOG) {
			defaultVal = "";
		}

	}

	public MillConfig(final Field field, final String key, final Object... possibleVals) {
		this.field = field;
		this.possibleVals = possibleVals;
		this.key = key.toLowerCase();

		if (isBoolean()) {
			this.possibleVals = BOOLEAN_VALS;
		} else if (possibleVals.length == 0) {
			MLN.error(null, "No possible values specified for non-boolean config: " + field.getName());
		}
	}

	public boolean compareValuesFromString(final String newValStr) {
		final Object newVal = getValueFromString(newValStr);

		if (newVal == null) {
			return false;
		}

		return newVal.equals(getValue());

	}

	public Object getDefaultValue() {
		if (special == KEY) {
			if (defaultVal != null) {
				return Mill.proxy.getKeyString((Integer) defaultVal);
			}
		}

		return defaultVal;
	}

	public String getDesc() {

		if (special == LOG) {
			return "";
		}

		return MLN.string("config." + key + ".desc", getStringFromValue(defaultVal));
	}

	public String getLabel() {

		if (special == LOG) {
			return key;
		}

		return MLN.string("config." + key + ".label");
	}

	public Object[] getPossibleVals() {

		if (special == LANGUAGE) {
			return new Object[] { MLN.loadedLanguages.get("fr"), MLN.loadedLanguages.get("en") };
		} else if (special == LOG) {
			return new Object[] { 0, 1, 2, 3 };
		}

		return possibleVals;
	}

	public String getSaveValue() {
		try {
			return getSaveValue(field.get(null));
		} catch (final Exception e) {
			MLN.printException(this + ": Exception when getting the field.", e);
		}
		return null;
	}

	public String getSaveValue(final Object o) {

		if (special == KEY && o != null) {
			return Mill.proxy.getKeyString((Integer) o);
		} else if (special == LOG) {
			return MLN.getLogLevel((Integer) o);
		}

		if (o == null) {
			return "";
		} else {
			return o.toString();
		}
	}

	public String getStringFromValue(final Object o) {

		if (special == KEY && o != null) {
			return Mill.proxy.getKeyString((Integer) o);
		} else if (special == LOG) {
			return MLN.getLogLevel((Integer) o);
		} else if (isBoolean() && o != null) {
			return getBooleanString((Boolean) o);
		}

		if (o == null) {
			return "";
		} else {
			return o.toString();
		}
	}

	public String getStringValue() {
		try {
			return getStringFromValue(field.get(null));
		} catch (final Exception e) {
			MLN.printException(this + ": Exception when getting the field.", e);
		}
		return null;
	}

	public Object getValue() {
		try {
			return field.get(null);
		} catch (final Exception e) {
			MLN.printException(this + ": Exception when getting the field.", e);
		}
		return null;
	}

	public Object getValueFromString(final String val) {

		if (special == LANGUAGE) {
			return MLN.loadedLanguages.get(val);
		} else if (special == KEY) {
			return Mill.proxy.loadKeySetting(val);
		} else if (special == LOG) {
			return MLN.readLogLevel(val);
		}

		if (isString()) {
			return val;
		} else if (isInteger()) {
			return Integer.parseInt(val);
		} else if (isBoolean()) {
			return Boolean.parseBoolean(val);
		}
		return null;
	}

	public boolean hasDefaultValue() {

		if (defaultVal == null) {
			return false;
		}

		if (special == LOG) {
			if ((Integer) getValue() == 0) {
				return true;
			}
		}

		return defaultVal.equals(getValue());

	}

	public boolean hasTextField() {
		return special == EDITABLE_STRING || special == KEY || special == EDITABLE_INTEGER || special == BONUS_KEY;
	}

	public boolean isBoolean() {
		return field.getType().equals(Boolean.class) || field.getType().equals(boolean.class);
	}

	public boolean isInteger() {
		return field.getType().equals(Integer.class) || field.getType().equals(int.class);
	}

	public boolean isString() {
		return field.getType().equals(String.class);
	}

	public void setDefaultValue(final Object defaultVal) {
		this.defaultVal = defaultVal;
	}

	public MillConfig setDisplay(final boolean display) {
		displayConfig = display;
		return this;
	}

	public MillConfig setDisplayDev(final boolean display) {
		displayConfigDev = display;
		return this;
	}

	public MillConfig setMaxStringLength(final int len) {
		strLimit = len;
		return this;
	}

	public void setValue(Object val) {

		if (special == LOG && val instanceof String) {
			val = MLN.readLogLevel((String) val);
		}

		if (special == KEY && val.equals(0)) {
			return;
		}

		try {
			field.set(null, val);
		} catch (final Exception e) {
			MLN.printException(this + ": Exception when setting the field.", e);
		}

		if (special == BONUS_KEY && Mill.proxy.getTheSinglePlayer() != null) {// to
																				// avoid
																				// triggering
																				// before
																				// world
																				// is
																				// loaded
			MLN.checkBonusCode(true);
		}

	}

	public void setValueFromString(final String val, final boolean setDefault) {
		setValue(getValueFromString(val));
		if (setDefault) {
			setDefaultValue(getValueFromString(val));
		}
	}

	@Override
	public String toString() {
		return "MillConfig:" + key;
	}

}
