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
public class FieldDisplay {

	private FieldNew parentField;
	private List<FieldNew> childFields;

	/**
	 * 
	 */
	public FieldDisplay() {
		super();
	}

	/**
	 * @param parentField
	 * @param childFields
	 */
	public FieldDisplay(FieldNew parentField, List<FieldNew> childFields) {
		super();
		this.parentField = parentField;
		this.childFields = childFields;
	}

	public FieldNew getParentField() {
		return parentField;
	}

	public void setParentField(FieldNew parentField) {
		this.parentField = parentField;
	}

	public List<FieldNew> getChildFields() {
		return childFields;
	}

	public void setChildFields(List<FieldNew> childFields) {
		this.childFields = childFields;
	}

}
