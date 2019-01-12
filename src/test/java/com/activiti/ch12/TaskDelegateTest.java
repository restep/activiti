package com.activiti.ch12;

import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.activiti.spring.impl.test.SpringActivitiTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author restep
 * @date 2019/1/12
 */
@ContextConfiguration("classpath:applicationContext.xml")
public class TaskDelegateTest extends SpringActivitiTestCase {
    /**
     * 任务分配给bill
     * 然后bill把任务委派给henryyan
     * henryyan处理完成后任务回归到bill
     */
    @Test
    @Deployment(resources = "ch12/taskDelegate.bpmn")
    public void testTaskDelegate() {
        Map<String, Object> variableMap = new HashMap<>();
        String userId = "bill";
        variableMap.put("userId", userId);

        runtimeService.startProcessInstanceByKey("taskDelegateProcess", variableMap);
        Task task = taskService.createTaskQuery().taskAssignee(userId).singleResult();
        Assert.assertNotNull(task);
        Assert.assertNull(task.getOwner());

        //委派任务给henryyan
        String delegatedUserId = "henryyan";
        taskService.delegateTask(task.getId(), delegatedUserId);

        //查看数据状态
        task = taskService.createTaskQuery().taskAssignee(delegatedUserId)
                .taskDelegationState(DelegationState.PENDING).singleResult();
        Assert.assertEquals(task.getOwner(), userId);
        Assert.assertEquals(task.getAssignee(), delegatedUserId);

        //henryyan完成任务
        taskService.resolveTask(task.getId());

        //任务回归到bill
        task = taskService.createTaskQuery().taskAssignee(userId)
                .taskDelegationState(DelegationState.RESOLVED).singleResult();
        Assert.assertEquals(task.getOwner(), userId);
        Assert.assertEquals(task.getAssignee(), userId);

        //bill完成任务
        taskService.complete(task.getId());

        long count = historyService.createHistoricTaskInstanceQuery().finished().count();
        Assert.assertEquals(1L, count);
    }
}
