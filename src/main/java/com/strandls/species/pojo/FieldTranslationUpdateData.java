package com.strandls.species.pojo;

import java.util.List;

public class FieldTranslationUpdateData {
    private Long fieldId;
    private List<FieldTranslation> translations;

    // Default constructor
    public FieldTranslationUpdateData() {
        super();
    }

    // Constructor with all fields
    public FieldTranslationUpdateData(Long fieldId, List<FieldTranslation> translations) {
        super();
        this.fieldId = fieldId;
        this.translations = translations;
    }

    // Getters and setters
    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public List<FieldTranslation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<FieldTranslation> translations) {
        this.translations = translations;
    }
} 