package com.activiti.listener.ch09;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 请假会签任务监听器 当会签任务完成时统计同意的数量
 *
 * @author restep
 * @date 2019/1/5
 */
@Component
public class SignCompleteListener implements TaskListener {
    private static final long serialVersionUID = 6460623230080985382L;

    @Override
    public void notify(DelegateTask delegateTask) {
        String approved = (String) delegateTask.getVariable("approved");
        if (StringUtils.equals("true", approved)) {
            Long approvedCounter = (Long) delegateTask.getVariable("approvedCounter");
            delegateTask.setVariable("approvedCounter", approvedCounter + 1);
        }
    }
}
