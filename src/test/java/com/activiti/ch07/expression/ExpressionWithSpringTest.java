package com.activiti.ch07.expression;

import com.activiti.ch07.shared.ExpressionShared;
import org.activiti.engine.test.Deployment;
import org.activiti.spring.impl.test.SpringActivitiTestCase;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * 使用Spring的表达式
 *
 * @author restep
 * @date 2019/1/2
 */
@ContextConfiguration("classpath:applicationContext.xml")
public class ExpressionWithSpringTest extends SpringActivitiTestCase {
    /**
     * 必须以test开头
     */
    @Test
    @Deployment(resources = "ch07/expression/expression.bpmn")
    public void testExpression() {
        ExpressionShared.expression(identityService, runtimeService,
                managementService, taskService);
    }

}
