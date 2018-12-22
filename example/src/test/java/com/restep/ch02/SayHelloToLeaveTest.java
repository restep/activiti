package com.restep.ch02;

import com.restep.activiti.AbstractTest;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author restep
 * @date 2018/12/22
 */
public class SayHelloToLeaveTest extends AbstractTest {
    @Test
    public void startProcessTest() {
        Deployment deployment = repositoryService.createDeployment()
                .addInputStream("SayHelloToLeave.bpmn", this.getClass().getClassLoader().getResourceAsStream("ch02/helloworld/SayHelloToLeave.bpmn"))
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        Assert.assertNotNull(processDefinition);

        Map<String, Object> variables = new HashMap<>();
        variables.put("applyUser", "employee1");
        variables.put("days", 3);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("SayHelloToLeave", variables);
        Assert.assertNotNull(processInstance);

        Task taskOfDeptLeader = taskService.createTaskQuery().taskCandidateGroup("deptLeader").singleResult();
        Assert.assertNotNull(taskOfDeptLeader);

        taskService.claim(taskOfDeptLeader.getId(), "leaderUser");
        variables = new HashMap<>();
        variables.put("approved", true);
        taskService.complete(taskOfDeptLeader.getId(), variables);

        taskOfDeptLeader = taskService.createTaskQuery().taskCandidateGroup("deptLeader").singleResult();
        Assert.assertNull(taskOfDeptLeader);

        long count = historyService.createHistoricProcessInstanceQuery().finished().count();
        Assert.assertNotNull(count);
    }
}
