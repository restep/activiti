package com.activiti.util;

import org.activiti.engine.identity.User;

import javax.servlet.http.HttpSession;

/**
 * @author restep
 * @date 2018/12/31
 */
public class SessionUtil {
    private static final String USER = "user";

    /**
     * 将用户保存到session
     *
     * @param session
     * @param user
     */
    public static void saveUserToSession(HttpSession session, User user) {
        session.setAttribute(USER, user);
    }

    /**
     * 从session中取出用户
     *
     * @param session
     * @return
     */
    public static User getUserFromSession(HttpSession session) {
        Object obj = session.getAttribute(USER);
        if (null == obj) {
            return null;
        }

        return (User) obj;
    }
}
