package com.activiti.form;

import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.FormEngine;

import javax.swing.*;

/**
 * @author restep
 * @date 2018/12/31
 */
public class MyFormEngine implements FormEngine {
    @Override
    public String getName() {
        return "myformengine";
    }

    @Override
    public Object renderStartForm(StartFormData startForm) {
        JButton jButton = new JButton();
        jButton.setName("My Start Form Button");
        return jButton;
    }

    @Override
    public Object renderTaskForm(TaskFormData taskForm) {
        JButton jButton = new JButton();
        jButton.setName("My Task Form Button");
        return jButton;
    }
}
