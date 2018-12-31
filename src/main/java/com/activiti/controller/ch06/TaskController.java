package com.activiti.controller.ch06;

import com.activiti.controller.AbstractController;
import com.activiti.util.SessionUtil;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author restep
 * @date 2018/12/31
 */
@Controller
@RequestMapping(value = "/ch06")
public class TaskController extends AbstractController {
    @RequestMapping(value = "/task/list")
    public ModelAndView taskList(HttpSession session) {
        ModelAndView mav = new ModelAndView("ch06/taskList");
        User user = SessionUtil.getUserFromSession(session);

        List<Task> taskList = taskService.createTaskQuery()
                .taskCandidateOrAssigned(user.getId()).list();
        mav.addObject("taskList", taskList);
        return mav;
    }
}
