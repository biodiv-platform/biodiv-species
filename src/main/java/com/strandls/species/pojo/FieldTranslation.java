package com.strandls.species.pojo;

public class FieldTranslation {
    private Long langId;
    private String header;
    private String description;
    private String urlIdentifier;

    // Default constructor
    public FieldTranslation() {
        super();
    }

    // Constructor with all fields
    public FieldTranslation(Long langId, String header, String description, String urlIdentifier) {
        super();
        this.langId = langId;
        this.header = header;
        this.description = description;
        this.urlIdentifier = urlIdentifier;
    }

    // Getters and setters
    public Long getLangId() {
        return langId;
    }

    public void setLangId(Long langId) {
        this.langId = langId;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlIdentifier() {
        return urlIdentifier;
    }

    public void setUrlIdentifier(String urlIdentifier) {
        this.urlIdentifier = urlIdentifier;
    }
} 