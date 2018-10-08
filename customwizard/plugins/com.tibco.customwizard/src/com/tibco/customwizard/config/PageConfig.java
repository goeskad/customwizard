package com.tibco.customwizard.config;


public class PageConfig extends ConfigElement {
	private String id;
	
	private String title;

    private String description;
    
	private String referenceFile;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReferenceFile() {
		return referenceFile;
	}

	public void setReferenceFile(String referenceFile) {
		this.referenceFile = referenceFile;
	}
}
