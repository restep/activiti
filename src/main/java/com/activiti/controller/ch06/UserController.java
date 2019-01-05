package com.activiti.controller.ch06;

import com.activiti.controller.AbstractController;
import com.activiti.util.SessionUtil;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author restep
 * @date 2018/12/31
 */
@Controller
@RequestMapping("/user")
public class UserController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session) {
        //比较userId + password
        boolean checkPassword = identityService.checkPassword(username, password);
        LOGGER.info("checkPassword: " + checkPassword);
        if (checkPassword) {
            //查看用户是否存在
            User user = identityService.createUserQuery().userId(username)
                    .singleResult();
            SessionUtil.saveUserToSession(session, user);

            //读取角色
            List<Group> groupList = identityService.createGroupQuery()
                    .groupMember(user.getId()).list();
            session.setAttribute("groups", groupList);

            String[] groupNames = new String[groupList.size()];
            for (int i = 0; i < groupNames.length; i++) {
                groupNames[i] = groupList.get(i).getName();
            }
            session.setAttribute("groupNames", ArrayUtils.toString(groupNames));

            return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/main/index";
        }

        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/login.jsp?error=true";
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "/login";
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public Map<String, List<User>> userList() {
        List<Group> groupList = identityService.createGroupQuery().list();
        Map<String, List<User>> map = new HashMap<>();
        for (Group group : groupList) {
            List<User> userList = identityService.createUserQuery()
                    .memberOfGroup(group.getId()).list();
            map.put(group.getName(), userList);
        }

        return map;
    }
}
