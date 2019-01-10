package com.activiti.listener.ch10;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;

/**
 * @author restep
 * @date 2019/1/10
 */
public class HandleErrorListener implements ExecutionListener {
    private static final long serialVersionUID = -2959842954461483268L;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String currentActivityId = execution.getCurrentActivityId();
        if (StringUtils.equals("exclusivegateway-treasurerAudit", currentActivityId)) {
            execution.setVariable("message", "财务审批未通过");
        } else if (StringUtils.equals("exclusivegateway-generalManagerAudit", currentActivityId)) {
            execution.setVariable("message", "总经理审批未通过");
        }
    }
}
