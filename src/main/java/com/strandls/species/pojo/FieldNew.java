/**
 * 
 */
package com.strandls.species.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * @author Abhishek Rudra
 *
 * 
 */

@Entity
@Table(name = "field_new")
public class FieldNew {

	private Long id;
	private Long parentId;
	private Long displayOrder;
	private String label;
	private String header;
	private String path;

	public FieldNew() {
		super();
	}

	public FieldNew(Long id, Long parentId, Long displayOrder, String label, String header,String path) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.displayOrder = displayOrder;
		this.label = label;
		this.header = header;
		this.path = path;	
	}

	@Id
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "parent_id")
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Column(name = "display_order")
	public Long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Long displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Column(name = "label")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Column(name = "header")
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	
	@Column(name = "path", columnDefinition = "ltree")
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


}
