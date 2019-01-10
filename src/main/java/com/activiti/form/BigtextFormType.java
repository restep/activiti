package com.activiti.form;

import org.activiti.engine.form.AbstractFormType;

/**
 * @author restep
 * @date 2019/1/10
 */
public class BigtextFormType extends AbstractFormType {
    @Override
    public Object convertFormValueToModelValue(String propertyValue) {
        return propertyValue;
    }

    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        if (null == modelValue) {
            return "";
        }

        return modelValue.toString();
    }

    @Override
    public String getName() {
        return "bigtext";
    }
}
