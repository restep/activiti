package com.example.ch02;

import com.example.activiti.AbstractTest;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author restep
 * @date 2018/12/21
 */
public class VerySimpleLeaveProcessTest extends AbstractTest {
    @Test
    public void startProcessTest() throws Exception {
        //部署流程定义文件
        repositoryService.createDeployment().addClasspathResource("ch02/sayhelloleave.bpmn").deploy();

        //验证已部署流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
        Assert.assertNotNull(processDefinition);

        //启动流程并返回流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leavesayhello");
        Assert.assertNotNull(processInstance);
    }
}
