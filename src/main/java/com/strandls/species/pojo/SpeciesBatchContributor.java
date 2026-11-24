package com.strandls.species.pojo;

/**
 * A contributor email from a batch upload row and the user it resolved to.
 */
public class SpeciesBatchContributor {

	private String email;
	private Long userId;
	private String name;

	public SpeciesBatchContributor() {
		super();
	}

	public SpeciesBatchContributor(String email, Long userId, String name) {
		super();
		this.email = email;
		this.userId = userId;
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
