package org.springframework.content.examples;

import org.springframework.content.annotations.Content;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Claim {

	@Id
	private String claimId;

	private String lastName;
	private String firstName;
	
	@Content
	private ClaimForm claimForm;

	public String getClaimId() {
		return claimId;
	}

	public void setClaimId(String claimId) {
		this.claimId = claimId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public ClaimForm getClaimForm() {
		return claimForm;
	}

	public void setClaimForm(ClaimForm claimForm) {
		this.claimForm = claimForm;
	}
}
