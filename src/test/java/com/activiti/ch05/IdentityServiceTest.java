package com.activiti.ch05;

import com.activiti.base.AbstractTest;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author restep
 * @date 2018/12/22
 */
public class IdentityServiceTest extends AbstractTest {
    @Test
    public void groupTest() {
        //创建一个组对象
        Group group = identityService.newGroup("deptLeader");
        group.setName("部门领导");
        group.setType("assignment");

        //保存组
        identityService.saveGroup(group);

        //验证组是否已经保存成功 创建组查询对象
        List<Group> groupList = identityService.createGroupQuery().groupId("deptLeader").list();
        Assert.assertNotNull(groupList);

        //删除组
        identityService.deleteGroup("deptLeader");

        //验证是否删除成功
        groupList = identityService.createGroupQuery().groupId("deptLeader").list();
        Assert.assertNotNull(groupList);
    }

    @Test
    public void userTest() {
        //创建一个用户
        User user = identityService.newUser("restep");
        user.setFirstName("restep");
        user.setLastName("shi");
        user.setEmail("restep@163.com");

        //保存用户到数据库
        identityService.saveUser(user);

        //查询用户
        User userDB = identityService.createUserQuery().userId("restep").singleResult();
        Assert.assertNotNull(userDB);

        //删除用户
        identityService.deleteUser("restep");

        //查询用户
        userDB = identityService.createUserQuery().userId("restep").singleResult();
        Assert.assertNull(userDB);
    }

    @Test
    public void userAndGroupTest() {
        //创建组对象
        Group group = identityService.newGroup("it");
        group.setName("IT部");
        group.setType("it");
        identityService.saveGroup(group);

        //创建用户
        User user = identityService.newUser("henry");
        user.setFirstName("henry");
        user.setPassword("111111");
        identityService.saveUser(user);

        //把用户加入到组
        identityService.createMembership("henry", "it");

        //创建组对象
        group = identityService.newGroup("deptLeader");
        group.setName("总经理室");
        group.setType("deptLeader");
        identityService.saveGroup(group);

        //创建用户
        user = identityService.newUser("bill");
        user.setFirstName("bill");
        user.setPassword("111111");
        identityService.saveUser(user);

        //把用户加入到组
        identityService.createMembership("bill", "deptLeader");

        //创建组对象
        group = identityService.newGroup("hr");
        group.setName("人事部");
        group.setType("hr");
        identityService.saveGroup(group);

        //创建用户
        user = identityService.newUser("jenny");
        user.setFirstName("jenny");
        user.setPassword("111111");
        identityService.saveUser(user);

        //把用户加入到组
        identityService.createMembership("jenny", "hr");

        //创建组对象
        group = identityService.newGroup("supportCrew");
        group.setName("供货方");
        group.setType("supportCrew");
        identityService.saveGroup(group);

        //创建用户
        user = identityService.newUser("houqin");
        user.setFirstName("houqin");
        user.setPassword("111111");
        identityService.saveUser(user);

        //把用户加入到组
        identityService.createMembership("houqin", "supportCrew");

        //创建组对象
        group = identityService.newGroup("treasurer");
        group.setName("财务");
        group.setType("treasurer");
        identityService.saveGroup(group);

        //创建用户
        user = identityService.newUser("caiwu");
        user.setFirstName("caiwu");
        user.setPassword("111111");
        identityService.saveUser(user);

        //把用户加入到组
        identityService.createMembership("caiwu", "treasurer");

        //创建组对象
        group = identityService.newGroup("generalManager");
        group.setName("总经理");
        group.setType("generalManager");
        identityService.saveGroup(group);

        //创建用户
        user = identityService.newUser("boss");
        user.setFirstName("boss");
        user.setPassword("111111");
        identityService.saveUser(user);

        //把用户加入到组
        identityService.createMembership("boss", "generalManager");

        //创建组对象
        group = identityService.newGroup("cashier");
        group.setName("出纳");
        group.setType("cashier");
        identityService.saveGroup(group);

        //创建用户
        user = identityService.newUser("chuna");
        user.setFirstName("chuna");
        user.setPassword("111111");
        identityService.saveUser(user);

        //把用户加入到组
        identityService.createMembership("chuna", "cashier");
    }
}
