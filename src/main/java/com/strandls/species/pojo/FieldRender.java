/**
 * 
 */
package com.strandls.species.pojo;

import java.util.List;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class FieldRender {

	private FieldNewExtended parentField;
	private List<FieldDisplay> childField;

	/**
	 * 
	 */
	public FieldRender() {
		super();
	}

	/**
	 * @param parentField
	 * @param childField
	 */
	public FieldRender(FieldNewExtended parentField, List<FieldDisplay> childField) {
		super();
		this.parentField = parentField;
		this.childField = childField;
	}

	public FieldNew getParentField() {
		return parentField;
	}

	public void setParentField(FieldNewExtended parentField) {
		this.parentField = parentField;
	}

	public List<FieldDisplay> getChildField() {
		return childField;
	}

	public void setChildField(List<FieldDisplay> childField) {
		this.childField = childField;
	}

}
