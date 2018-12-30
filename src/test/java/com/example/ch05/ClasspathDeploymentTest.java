package com.example.ch05;

import com.example.activiti.AbstractTest;
import org.activiti.engine.repository.DeploymentBuilder;
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
        //定义classpath
        String bpmnClasspath = "ch05/candidateUserInUserTask.bpmn";
        String pngClasspath = "ch05/candidateUserInUserTask.png";

        //部署
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource(bpmnClasspath).addClasspathResource(pngClasspath).deploy();

        //验证流程定义是否部署成功
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("candidateUserInUserTask")
                .singleResult();
        Assert.assertNotNull(processDefinition);

        Map<String, Object> vars = new HashMap<>();
        List<Date> objList = new ArrayList<>();
        objList.add(new Date());
        vars.put("list", objList);

        runtimeService.startProcessInstanceByKey("candidateUserInUserTask", vars);

        List<Task> taskList = taskService.createTaskQuery().includeProcessVariables().list();
        Assert.assertNotNull(taskList);

        Task task = taskService.createTaskQuery()
                .taskId(taskList.get(0).getId()).includeProcessVariables()
                .includeTaskLocalVariables().singleResult();
        Assert.assertNotNull(task);
    }
}
