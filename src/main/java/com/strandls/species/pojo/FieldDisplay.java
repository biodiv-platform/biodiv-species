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

	private FieldNewExtended parentField;
	private List<FieldNewExtended> childFields;

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
	public FieldDisplay(FieldNewExtended parentField, List<FieldNewExtended> childFields) {
		super();
		this.parentField = parentField;
		this.childFields = childFields;
	}

	public FieldNew getParentField() {
		return parentField;
	}

	public void setParentField(FieldNewExtended parentField) {
		this.parentField = parentField;
	}

	public List<FieldNewExtended> getChildFields() {
		return childFields;
	}

	public void setChildFields(List<FieldNewExtended> childFields) {
		this.childFields = childFields;
	}

}
