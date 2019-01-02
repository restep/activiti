package com.activiti.ch07.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * 流程结束监听器
 *
 * @author restep
 * @date 2019/1/2
 */
public class ProcessEndExecutionListener implements ExecutionListener {
    private static final long serialVersionUID = 2456234628583676859L;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        execution.setVariable("setInEndListener", true);
        System.out.println(this.getClass().getSimpleName() + " : " + execution.getEventName());
    }
}
