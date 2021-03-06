package com.activiti.ch05;

import com.activiti.base.AbstractTest;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @author restep
 * @date 2018/12/22
 */
public class ClasspathDeploymentTest extends AbstractTest {
    @Test
    public void classpathDeployment() {
        //部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("ch05/candidateUserInUserTask.bpmn")
                .addClasspathResource("ch05/candidateUserInUserTask.png").deploy();
        Assert.assertNotNull(deployment);

        //验证流程定义是否部署成功
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        Assert.assertNotNull(processDefinition);

        Map<String, Object> vars = new HashMap<>();
        List<Date> objList = new ArrayList<>();
        objList.add(new Date());
        vars.put("list", objList);

        runtimeService.startProcessInstanceByKey(processDefinition.getKey(), vars);

        List<Task> taskList = taskService.createTaskQuery().includeProcessVariables().list();
        Assert.assertNotNull(taskList);

        Task task = taskService.createTaskQuery()
                .taskId(taskList.get(0).getId()).includeProcessVariables()
                .includeTaskLocalVariables().singleResult();
        Assert.assertNotNull(task);
    }
}
