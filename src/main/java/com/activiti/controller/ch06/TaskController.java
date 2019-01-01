package com.activiti.controller.ch06;

import com.activiti.controller.AbstractController;
import com.activiti.util.SessionUtil;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 签收任务
     *
     * @param taskId
     * @param session
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/task/claim/{taskId}")
    public String claim(@PathVariable("taskId") String taskId, HttpSession session,
                        RedirectAttributes redirectAttributes) {
        User user = SessionUtil.getUserFromSession(session);
        taskService.claim(taskId, user.getId());
        redirectAttributes.addFlashAttribute("message", "任务已签收");
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch06/task/list";
    }

    /**
     * 读取待办任务的表单字段
     *
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/task/getForm/{taskId}")
    public ModelAndView renderTaskForm(@PathVariable("taskId") String taskId) {
        ModelAndView mav = new ModelAndView("/ch06/taskForm");
        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        if (null != taskFormData.getFormKey()) {
            Object renderTaskForm = formService.getRenderedTaskForm(taskId);
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            mav.addObject("task", task);
            mav.addObject("taskFormData", renderTaskForm);
            mav.addObject("hasFormKey", true);
        } else {
            mav.addObject("taskFormData", taskFormData);
        }

        return mav;
    }

    @RequestMapping(value = "/task/complete/{taskId}")
    public String completeTask(@PathVariable("taskId") String taskId,
                               HttpServletRequest request) {
        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        String formKey = taskFormData.getFormKey();
        //从请求中获取表单字段的值
        List<FormProperty> formPropertyList = taskFormData.getFormProperties();
        Map<String, String> formValues = new HashMap<>();

        //formKey表单
        if (StringUtils.isNotBlank(formKey)) {
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                formValues.put(entry.getKey(), entry.getValue()[0]);
            }
        } else {    //动态表单
            for (FormProperty formProperty : formPropertyList) {
                if (formProperty.isWritable()) {
                    String value = request.getParameter(formProperty.getId());
                    formValues.put(formProperty.getId(), value);
                }
            }
        }

        formService.submitTaskFormData(taskId, formValues);
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch06/task/list";
    }
}
