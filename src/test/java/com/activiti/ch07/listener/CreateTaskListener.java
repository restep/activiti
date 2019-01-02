package com.activiti.ch07.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author restep
 * @date 2019/1/2
 */
public class CreateTaskListener implements TaskListener {
    private static final long serialVersionUID = 3706545256900000787L;

    private Expression content;
    private Expression task;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println(task.getValue(delegateTask));

        delegateTask.setVariable("setInTaskCreate", delegateTask.getEventName()
                + " : " + content.getValue(delegateTask));

        System.out.println(delegateTask.getEventName() + " 任务分配给: " + delegateTask.getAssignee());

        delegateTask.setAssignee("jenny");
    }
}
