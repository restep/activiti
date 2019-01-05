package com.activiti.form;

import org.activiti.engine.form.AbstractFormType;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author restep
 * @date 2019/1/5
 */
public class UsersFormType extends AbstractFormType {
    @Override
    public Object convertFormValueToModelValue(String propertyValue) {
        String[] strList = StringUtils.split(propertyValue, ",");
        return Arrays.asList(strList);
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
        return "userList";
    }
}
