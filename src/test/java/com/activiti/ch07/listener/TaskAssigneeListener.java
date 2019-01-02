package com.activiti.ch07.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author restep
 * @date 2019/1/2
 */
public class TaskAssigneeListener implements TaskListener {
    private static final long serialVersionUID = -8973643425036326606L;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println(delegateTask.getEventName() + " 任务分配给: " + delegateTask.getAssignee());
    }
}
