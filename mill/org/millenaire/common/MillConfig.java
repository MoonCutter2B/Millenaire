package org.millenaire.common;

import java.lang.reflect.Field;

public class MillConfig {

	private static final Object[] BOOLEAN_VALS=new Object[]{Boolean.TRUE,Boolean.FALSE};

	public static final int LANGUAGE=1,EDITABLE_STRING=2,KEY=3;

	final Field field;
	final public String key;
	public Object defaultVal;
	private Object[] possibleVals;

	public int special=0,strLimit=20;

	public MillConfig(Field field,String key,Object... possibleVals) {
		this.field=field;
		this.possibleVals=possibleVals;
		this.key=key;

		if (isBoolean()) {
			this.possibleVals=BOOLEAN_VALS;
		} else if (possibleVals.length==0) {
			MLN.error(null, "No possible values specified for non-boolean config: "+field.getName());
		}
	}

	public MillConfig(Field field,String key,int special) {
		this.field=field;
		this.special=special;
		this.key=key;
	}

	public Object[] getPossibleVals() {
		
		if (special==1) {
			return new Object[]{MLN.loadedLanguages.get("fr"),MLN.loadedLanguages.get("en")};
		}
		
		return possibleVals;
	}
	
	public MillConfig setMaxStringLength(int len) {
		strLimit=len;
		return this;
	}
	
	public boolean hasTextField() {
		return special==EDITABLE_STRING || special==KEY;
	}

	public boolean isBoolean() {
		return field.getType().equals(Boolean.class) || field.getType().equals(boolean.class);
	}

	public boolean isString() {
		return field.getType().equals(String.class);
	}

	public boolean isInteger() {
		return field.getType().equals(Integer.class) || field.getType().equals(int.class);
	}

	public String getLabel() {
		return MLN.string("config."+key+".label");
	}

	public String getDesc() {
		return MLN.string("config."+key+".desc",""+defaultVal);
	}

	public boolean compareValuesFromString(String newValStr) {
		Object newVal=getValueFromString(newValStr);

		if (newVal==null)
			return false;

		return newVal.equals(getValue());

	}

	public void setDefaultValue(Object defaultVal) {
		this.defaultVal=defaultVal;
	}

	public void setValue(Object val) {
		try {
			field.set(null, val);
		} catch (Exception e) {
			MLN.error(this, "Exception when setting the field.");
		}
	}

	public Object getValueFromString(String val) {
		
		if (special==LANGUAGE) {
			return MLN.loadedLanguages.get(val);
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

	public void setValueFromString(String val,boolean setDefault) {

		setValue(getValueFromString(val));
		if (setDefault)
			setDefaultValue(getValueFromString(val));
	}

	public Object getValue() {
		try {
			return field.get(null);
		} catch (Exception e) {
			MLN.error(this, "Exception when getting the field.");
		}
		return null;
	}

	public boolean hasDefaultValue() {

		if (defaultVal==null)
			return false;

		return defaultVal.equals(getValue());

	}

	@Override
	public String toString() {
		return "MillConfig:"+key;
	}




}
