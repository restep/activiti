package com.activiti.controller.ch07;

import com.activiti.controller.AbstractController;
import com.activiti.entity.ch07.Leave;
import com.activiti.service.ch07.LeaveService;
import com.activiti.util.SessionUtil;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author restep
 * @date 2019/1/3
 */
@Controller
@RequestMapping(value = "/ch07")
public class LeaveController extends AbstractController {
    @Autowired
    private LeaveService leaveService;

    @RequestMapping(value = "/apply", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("leave", new Leave());
        return "/ch07/apply";
    }

    /**
     * 启动请假流程
     */
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public String start(Leave leave,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {
        User user = SessionUtil.getUserFromSession(session);
        Map<String, Object> variables = new HashMap<>();
        leaveService.start(leave, user.getId(), variables);

        redirectAttributes.addFlashAttribute("message", "流程已启动");
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/main/index";
    }

    /**
     * 任务列表
     */
    @RequestMapping(value = "/task/list", method = RequestMethod.GET)
    public ModelAndView taskList(HttpSession session) {
        User user = SessionUtil.getUserFromSession(session);
        List<Leave> leaveList = leaveService.queryTodoTaskList(user.getId());

        ModelAndView mav = new ModelAndView("/ch07/taskList");
        mav.addObject("leaveList", leaveList);
        return mav;
    }

    /**
     * 签收任务
     */
    @RequestMapping(value = "/task/claim/{id}", method = RequestMethod.GET)
    public String claim(@PathVariable("id") String taskId,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        User user = SessionUtil.getUserFromSession(session);
        taskService.claim(taskId, user.getId());

        redirectAttributes.addFlashAttribute("message", "任务已签收");
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch07/task/list";
    }

    /**
     * 任务列表(带leave信息)
     */
    @RequestMapping(value = "/task/view/{taskId}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable("taskId") String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
        Leave leave = leaveService.get(Integer.parseInt(processInstance.getBusinessKey()));

        ModelAndView mav = new ModelAndView("/ch07/" + task.getTaskDefinitionKey());
        mav.addObject("leave", leave);
        mav.addObject("task", task);
        return mav;
    }

    @RequestMapping(value = "/task/complete/{taskId}", method = RequestMethod.POST)
    public String complete(@PathVariable("taskId") String taskId,
                           @RequestParam(value = "saveEntity", required = false) String saveEntity,
                           @ModelAttribute("preloadLeave") Leave leave,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        Map<String, Object> variables = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (parameterName.startsWith("p_")) {
                //参数结构：p_B_name p为参数的前缀 B为类型 name为属性名称
                String[] parameter = parameterName.split("_");
                if (parameter.length == 3) {
                    String paramValue = request.getParameter(parameterName);
                    Object value = paramValue;
                    if (StringUtils.equals(parameter[1], "B")) {
                        value = BooleanUtils.toBoolean(paramValue);
                    } else if (StringUtils.equals(parameter[1], "DT")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        try {
                            value = sdf.parse(paramValue);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    variables.put(parameter[2], value);
                }
            }
        }

        leaveService.complete(leave, BooleanUtils.toBoolean(saveEntity), taskId, variables);
        redirectAttributes.addFlashAttribute("message", "任务已完成");

        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch07/task/list";
    }

    /**
     * 自动绑定页面字段
     *
     * @param id
     * @return
     */
    @ModelAttribute("preloadLeave")
    public Leave getLeave(@RequestParam(value = "id", required = false) Integer id) {
        if (null != id) {
            return leaveService.get(id);
        }

        return new Leave();
    }
}
