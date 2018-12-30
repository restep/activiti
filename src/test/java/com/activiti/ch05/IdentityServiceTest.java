package com.activiti.ch05;

import com.activiti.activiti.AbstractTest;
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

        //查询组的用户
        List<User> userList = identityService.createUserQuery().memberOfGroup("deptLeader").list();
        Assert.assertNotNull(userList);

        //查询用户的组
        List<Group> groupList = identityService.createGroupQuery().groupMember("restep").list();
        Assert.assertNotNull(groupList);
    }
}
