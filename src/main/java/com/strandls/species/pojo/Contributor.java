/**
 * 
 */
package com.strandls.species.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Abhishek Rudra
 *
 * 
 */

@Entity
@Table(name = "contributor")
public class Contributor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8328192609469909643L;
	private Long id;
	private String name;

	/**
	 * 
	 */
	public Contributor() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 */
	public Contributor(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "name", columnDefinition = "TEXT")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
