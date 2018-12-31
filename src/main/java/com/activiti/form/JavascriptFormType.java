package com.activiti.form;

import org.activiti.engine.form.AbstractFormType;

/**
 * javascript表单
 * @author restep
 * @date 2018/12/31
 */
public class JavascriptFormType extends AbstractFormType {
    @Override
    public String getName() {
        return "javascript";
    }

    @Override
    public Object convertFormValueToModelValue(String propertyValue) {
        return propertyValue;
    }

    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        return (String) modelValue;
    }
}
