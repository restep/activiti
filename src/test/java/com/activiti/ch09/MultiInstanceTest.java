package com.activiti.ch09;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.activiti.spring.impl.test.SpringActivitiTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多实例测试用例
 *
 * @author restep
 * @date 2019/1/5
 */
@ContextConfiguration("classpath:applicationContext.xml")
public class MultiInstanceTest extends SpringActivitiTestCase {
    /**
     * Java Service多实例（是否顺序结果一样）
     */
    @Test
    @Deployment(resources = "ch09/fixedNumbers.bpmn")
    public void testFixedNumbers() {
        Map<String, Object> variables = new HashMap<>();
        long loop = 3;
        variables.put("loop", loop);
        //计数器
        variables.put("counter", 0);

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("fixedNumbers", variables);
        long counter = (Long) runtimeService.getVariable(processInstance.getId(), "counter");
        Assert.assertEquals(loop, counter);
    }

    /**
     * 用户任务多实例--顺序
     */
    @Test
    @Deployment(resources = "ch09/sequential.bpmn")
    public void testSequential() {
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("userTask");
        long count = taskService.createTaskQuery().processInstanceId(processInstance.getId()).count();
        Assert.assertEquals(1, count);

        Task task = taskService.createTaskQuery().singleResult();
        taskService.complete(task.getId());
        count = taskService.createTaskQuery().processInstanceId(processInstance.getId()).count();
        Assert.assertEquals(1, count);

        task = taskService.createTaskQuery().singleResult();
        taskService.complete(task.getId());
        count = taskService.createTaskQuery().processInstanceId(processInstance.getId()).count();
        assertEquals(1, count);

        task = taskService.createTaskQuery().singleResult();
        taskService.complete(task.getId());
        count = taskService.createTaskQuery().processInstanceId(processInstance.getId()).count();
        Assert.assertEquals(0, count);
    }

    /**
     * 用户任务多实例--并行
     */
    @Test
    @Deployment(resources = "ch09/nosequential.bpmn")
    public void testNoSequential() {
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("userTask");
        long count = taskService.createTaskQuery().processInstanceId(processInstance.getId()).count();
        Assert.assertEquals(3, count);
    }

    /**
     * 用户任务多实例，通过用户数量决定实例个数--并行
     */
    @Test
    @Deployment(resources = "ch09/usersNosequential.bpmn")
    public void testUsersNoSequential() {
        Map<String, Object> variables = new HashMap<>();
        List<String> userList = Arrays.asList("user1", "user2", "user3");
        variables.put("userList", userList);
        runtimeService.startProcessInstanceByKey("userTask", variables);

        for (String userId : userList) {
            Assert.assertEquals(1, taskService.createTaskQuery().taskAssignee(userId).count());
        }
    }

    /**
     * 用户任务多实例，通过用户数量决定实例个数--顺序
     */
    @Test
    @Deployment(resources = "ch09/usersSequential.bpmn")
    public void testUsersSequential() {
        Map<String, Object> variables = new HashMap<>();
        List<String> userList = Arrays.asList("user1", "user2", "user3");
        variables.put("userList", userList);
        runtimeService.startProcessInstanceByKey("userTask", variables);

        for (String userId : userList) {
            Task task = taskService.createTaskQuery().taskAssignee(userId).singleResult();
            taskService.complete(task.getId());
        }
    }

    /**
     * 用户任务多实例，按照任务完成的百分比比率决定是否提前结束流程
     */
    @Test
    @Deployment(resources = "ch09/rate.bpmn")
    public void testRate() {
        Map<String, Object> variables = new HashMap<>();
        List<String> userList = Arrays.asList("user1", "user2", "user3");
        variables.put("userList", userList);
        variables.put("rate", 0.6d);
        runtimeService.startProcessInstanceByKey("userTask", variables);

        Task task = taskService.createTaskQuery().taskAssignee("user1").singleResult();
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().taskAssignee("user2").singleResult();
        taskService.complete(task.getId());

        long count = historyService.createHistoricProcessInstanceQuery().finished().count();
        Assert.assertEquals(1, count);

    }

    @Test
    @Deployment(resources = "ch09/exception.bpmn")
    public void testException() {
        Map<String, Object> variables = new HashMap();
        List<String> userList = Arrays.asList("user1", "user2", "user3");
        variables.put("userList", userList);
        runtimeService.startProcessInstanceByKey("userTask", variables);

        Task task = taskService.createTaskQuery().taskAssignee("user1").singleResult();
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().taskAssignee("user2").singleResult();
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().taskAssignee("user3").singleResult();
        taskService.complete(task.getId());

        List<Task> taskList = taskService.createTaskQuery().list();
        for (Task element : taskList) {
            System.out.println(element.getName());
        }
    }
}
