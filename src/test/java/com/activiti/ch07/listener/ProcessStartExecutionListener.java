package com.activiti.ch07.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * 流程启动监听器
 *
 * @author restep
 * @date 2019/1/2
 */
public class ProcessStartExecutionListener implements ExecutionListener {
    private static final long serialVersionUID = -899833296735786924L;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        execution.setVariable("setInStartListener", true);
        System.out.println(this.getClass().getSimpleName() + " : " + execution.getEventName());
    }
}
