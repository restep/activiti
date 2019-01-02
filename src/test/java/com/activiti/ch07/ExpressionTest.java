package com.activiti.ch07;

import com.activiti.base.AbstractTest;
import com.activiti.ch07.shared.ExpressionShared;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Assert;
import org.junit.Test;

/**
 * 不使用spring的表达式
 *
 * @author restep
 * @date 2019/1/2
 */
public class ExpressionTest extends AbstractTest {
    /**
     * 流程key
     */
    private static final String PROCESS_DEFINITION_KEY = "expression";

    @Test
    public void deploy() {
        //部署流程定义文件
        Deployment deployment = repositoryService.createDeployment().
                addClasspathResource("ch07/expression/expression.bpmn").deploy();

        //验证是否部署成功
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        Assert.assertEquals(PROCESS_DEFINITION_KEY, processDefinition.getKey());
    }

    @Test
    public void expression() {
        ExpressionShared.expression(identityService, runtimeService,
                managementService, taskService);
    }
}
