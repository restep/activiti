package com.activiti.ch07.listener;

import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.activiti.spring.impl.test.SpringActivitiTestCase;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author restep
 * @date 2019/1/2
 */
@ContextConfiguration("classpath:applicationContext.xml")
public class ListenerTest extends SpringActivitiTestCase {
    @Test
    @Deployment(resources = "ch07/listener/listener.bpmn")
    public void testListener() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("endListener", new ProcessEndExecutionListener());
        variables.put("assignmentDelegate", new TaskAssigneeListener());
        variables.put("name", "Henry Yan");

        identityService.setAuthenticatedUserId("henryyan");
        ProcessInstance processInstance = runtimeService.
                startProcessInstanceByKey("listener", variables);

        //校验是否执行了启动监听
        Assert.assertTrue((Boolean) runtimeService
                .getVariable(processInstance.getId(), "setInStartListener"));

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId())
                .taskAssignee("jenny").singleResult();
        String setInTaskCreate = (String) taskService.getVariable(task.getId(), "setInTaskCreate");
        Assert.assertNotNull(setInTaskCreate);
        taskService.complete(task.getId());

        //流程结束后查询变量
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId()).list();
        boolean hasVariableOfEndListener = false;
        for (HistoricVariableInstance variableInstance : list) {
            if (StringUtils.equals("setInEndListener", variableInstance.getVariableName())) {
                hasVariableOfEndListener = true;
            }
        }
        Assert.assertTrue(hasVariableOfEndListener);
    }
}
