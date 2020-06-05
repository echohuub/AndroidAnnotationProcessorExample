package com.h3.android.annotationprocessor.butterknife.compiler;

import javax.lang.model.type.TypeMirror;

class FieldViewBinding {
    private String fieldName;
    private TypeMirror typeMirror;
    private int viewId;

    public FieldViewBinding() {
    }

    public FieldViewBinding(String fieldName, TypeMirror typeMirror, int viewId) {
        this.fieldName = fieldName;
        this.typeMirror = typeMirror;
        this.viewId = viewId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }

    public void setTypeMirror(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }
}
