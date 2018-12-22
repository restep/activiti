package com.restep.ch05;

import com.restep.activiti.AbstractTest;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * @author restep
 * @date 2018/12/22
 */
public class UserAndGroupInUserTaskTest extends AbstractTest {
    @Before
    public void before() {
        //创建组对象
        Group group = identityService.newGroup("deptLeader");
        group.setName("部门领导");
        group.setType("assignment");
        identityService.saveGroup(group);

        //创建用户
        User user = identityService.newUser("restep");
        user.setFirstName("restep");
        user.setLastName("feng");
        user.setEmail("restep@163.com");
        identityService.saveUser(user);

        //把用户加入到组
        identityService.createMembership("restep", "deptLeader");
    }

    @After
    public void after() {
        identityService.deleteMembership("restep", "deptLeader");
        identityService.deleteGroup("deptLeader");
        identityService.deleteUser("restep");
    }

    @Test
    public void userAndGroupInUserTask() {
        //部署流程
        repositoryService.createDeployment().addClasspathResource("ch05/userAndGroupInUserTask.bpmn").deploy();

        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("userAndGroupInUserTask");
        Assert.assertNotNull(processInstance);

        //根据角色查询任务
        Task task = taskService.createTaskQuery().taskCandidateUser("restep").singleResult();
        taskService.claim(task.getId(), "restep");
        taskService.complete(task.getId());
    }

    @Test
    public void userTaskWithGroupContainsTwoUser() {
        //再添加一个用户到组中
        User user = identityService.newUser("zhangsan");
        user.setFirstName("张");
        user.setLastName("三");
        user.setEmail("zhangsan@163.com");
        identityService.saveUser(user);

        //把用户加入组
        identityService.createMembership("zhangsan", "deptLeader");

        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("userAndGroupInUserTask");
        Assert.assertNotNull(processInstance);

        //将zhangsan作为候选人
        Task zhangsanTask = taskService.createTaskQuery().taskCandidateUser("zhangsan").singleResult();
        Assert.assertNotNull(zhangsanTask);

        //将restep作为候选人
        Task restepTask = taskService.createTaskQuery().taskCandidateUser("restep").singleResult();
        Assert.assertNotNull(restepTask);

        //zhangsan签收任务
        taskService.claim(zhangsanTask.getId(), "zhangsan");

        //查询restep是否拥有刚刚的候选人物
        restepTask = taskService.createTaskQuery().taskCandidateUser("restep").singleResult();
        Assert.assertNull(restepTask);
    }
}
