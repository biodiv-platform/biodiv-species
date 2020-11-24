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

	private FieldNew parentField;
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
	public FieldRender(FieldNew parentField, List<FieldDisplay> childField) {
		super();
		this.parentField = parentField;
		this.childField = childField;
	}

	public FieldNew getParentField() {
		return parentField;
	}

	public void setParentField(FieldNew parentField) {
		this.parentField = parentField;
	}

	public List<FieldDisplay> getChildField() {
		return childField;
	}

	public void setChildField(List<FieldDisplay> childField) {
		this.childField = childField;
	}

}
