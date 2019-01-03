package com.activiti.listener.ch07;

import com.activiti.entity.ch07.Leave;
import com.activiti.service.ch07.LeaveService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author restep
 * @date 2019/1/3
 */
@Component
@Transactional
public class ReportBackEndListener implements TaskListener {
    private static final long serialVersionUID = 6705343978143539307L;

    @Autowired
    private LeaveService leaveService;

    @Override
    public void notify(DelegateTask delegateTask) {
        Leave leave = leaveService.get(Integer.parseInt(delegateTask
                .getExecution().getProcessBusinessKey()));
        leave.setRealityStartTime((Date) delegateTask.getVariable("realityStartTime"));
        leave.setRealityEndTime((Date) delegateTask.getVariable("realityEndTime"));

        leaveService.save(leave);
    }
}
