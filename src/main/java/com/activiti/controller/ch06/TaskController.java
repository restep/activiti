package com.activiti.controller.ch06;

import com.activiti.controller.AbstractController;
import com.activiti.util.SessionUtil;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public String claim(@PathVariable("taskId") String taskId,
                        @RequestParam(value = "nextDo") String nextDo,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        User user = SessionUtil.getUserFromSession(session);
        taskService.claim(taskId, user.getId());
        redirectAttributes.addFlashAttribute("message", "任务已签收");

        if (StringUtils.equals("handle", nextDo)) {
            return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch06/task/getForm/" + taskId;
        }

        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch06/task/list";
    }

    /**
     * 反签收任务
     *
     * @param taskId
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/task/unclaim/{taskId}")
    public String unclaim(@PathVariable("taskId") String taskId,
                          RedirectAttributes redirectAttributes) {
        //反签收条件过滤
        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(taskId);
        for (IdentityLink identityLink : identityLinkList) {
            //如果一个任务有相关的候选人 组就可以反签收
            if (StringUtils.equals(IdentityLinkType.CANDIDATE, identityLink.getType())) {
                taskService.claim(taskId, null);
                redirectAttributes.addFlashAttribute("message", "任务已反签收");
                return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch06/task/list";
            }
        }

        redirectAttributes.addFlashAttribute("error", "该任务不允许反签收");
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
        Task task = null;

        //手动创建的任务(包含子任务)
        if (null == taskFormData) {
            task = taskService.createTaskQuery().taskId(taskId).singleResult();
            mav.addObject("manualTask", true);
        } else {
            //外置表单
            if (null != taskFormData.getFormKey()) {
                Object renderTaskForm = formService.getRenderedTaskForm(taskId);
                task = taskService.createTaskQuery().taskId(taskId).singleResult();
                mav.addObject("taskFormData", renderTaskForm);
                mav.addObject("hasFormKey", true);
            } else {
                //动态表单
                mav.addObject("taskFormData", taskFormData);
                task = taskFormData.getTask();
            }
        }

        mav.addObject("task", task);

        //读取任务参与人列表
        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(taskId);
        mav.addObject("identityLinksForTask", identityLinkList);

        //读取所有人员
        List<User> userList = identityService.createUserQuery().list();
        mav.addObject("userList", userList);

        //读取所有组
        List<Group> groupList = identityService.createGroupQuery().list();
        mav.addObject("groupList", groupList);

        //读取子任务
        List<HistoricTaskInstance> subTasks = historyService.createHistoricTaskInstanceQuery()
                .taskParentTaskId(taskId).list();
        mav.addObject("subTasks", subTasks);

        //读取上级任务
        if (null != task && null != task.getParentTaskId()) {
            HistoricTaskInstance parentTask = historyService.createHistoricTaskInstanceQuery()
                    .taskId(task.getParentTaskId()).singleResult();
            mav.addObject("parentTask", parentTask);
        }

        //读取附件
        List<Attachment> attachmentList = null;
        if (null != task.getTaskDefinitionKey()) {
            attachmentList = taskService.getTaskAttachments(taskId);
        } else {
            attachmentList = taskService.getProcessInstanceAttachments(task.getProcessInstanceId());
        }
        mav.addObject("attachmentList", attachmentList);

        return mav;
    }

    @RequestMapping(value = "/task/complete/{taskId}")
    public String completeTask(@PathVariable("taskId") String taskId,
                               HttpServletRequest request,
                               HttpSession session) {
        // 设置当前操作人，对于调用活动可以获取到当前操作人
        User user = SessionUtil.getUserFromSession(session);
        identityService.setAuthenticatedUserId(user.getId());

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

    /**
     * 更改任务属性
     *
     * @param taskId
     * @param propertyName
     * @param value
     * @return
     */
    @RequestMapping(value = "/task/changeProperty/{taskId}")
    @ResponseBody
    public String changeProperty(@PathVariable("taskId") String taskId,
                                 @RequestParam("propertyName") String propertyName,
                                 @RequestParam("value") String value) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //更改到期日
        if (StringUtils.equals("dueDate", propertyName)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                task.setDueDate(sdf.parse(value));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (StringUtils.equals("priority", propertyName)) {
            //更改任务优先级
            task.setPriority(Integer.parseInt(value));
        } else if (StringUtils.equals("owner", propertyName)) {
            //更改拥有人
            task.setOwner(value);
        } else if (StringUtils.equals("assignee", propertyName)) {
            //更改办理人
            task.setAssignee(value);
        } else {
            return "不支持" + propertyName + "属性";
        }

        taskService.saveTask(task);

        return "success";
    }

    /**
     * 添加参与人
     *
     * @param taskId
     * @param userIds
     * @param types
     * @param session
     * @return
     */
    @RequestMapping(value = "/task/participant/add/{taskId}")
    @ResponseBody
    public String participantAdd(@PathVariable("taskId") String taskId,
                                 @RequestParam("userId[]") String[] userIds,
                                 @RequestParam("type[]") String[] types,
                                 HttpSession session) {
        //设置当前操作人 对于调用活动可以获取到当前操作人
        User user = SessionUtil.getUserFromSession(session);
        identityService.setAuthenticatedUserId(user.getId());

        for (int i = 0; i < userIds.length; i++) {
            taskService.addUserIdentityLink(taskId, userIds[i], types[i]);
        }

        return "success";
    }

    /**
     * 删除参与人
     *
     * @param taskId
     * @param userId
     * @param groupId
     * @param type
     * @return
     */
    @RequestMapping(value = "/task/participant/delete/{taskId}")
    @ResponseBody
    public String participantDelete(@PathVariable("taskId") String taskId,
                                    @RequestParam("userId") String userId,
                                    @RequestParam("groupId") String groupId,
                                    @RequestParam("type") String type) {
        //区分用户 组 使用不同的处理方式
        if (StringUtils.isNotBlank(groupId)) {
            taskService.deleteCandidateGroup(taskId, groupId);
        } else {
            taskService.deleteUserIdentityLink(taskId, userId, type);
        }

        return "success";
    }

    /**
     * 添加候选人
     *
     * @param taskId
     * @param userOrGroupIds
     * @param types
     * @param session
     * @return
     */
    @RequestMapping(value = "/task/candidate/add/{taskId}")
    @ResponseBody
    public String addCandidate(@PathVariable("taskId") String taskId,
                               @RequestParam("userOrGroupIds[]") String[] userOrGroupIds,
                               @RequestParam("type[]") String[] types,
                               HttpSession session) {
        //设置当前操作人 对于调用活动可以获取到当前操作人
        User user = SessionUtil.getUserFromSession(session);
        identityService.setAuthenticatedUserId(user.getId());

        for (int i = 0; i < userOrGroupIds.length; i++) {
            String type = types[i];
            if (StringUtils.equals("user", type)) {
                taskService.addCandidateUser(taskId, userOrGroupIds[i]);
            } else if (StringUtils.equals("group", type)) {
                taskService.addCandidateGroup(taskId, userOrGroupIds[i]);
            }
        }

        return "success";
    }

    /**
     * 添加子任务
     *
     * @param taskId
     * @param taskName
     * @param description
     * @param session
     * @return
     */
    @RequestMapping("/task/subtask/add/{taskId}")
    public String addSubTask(@PathVariable("taskId") String taskId,
                             @RequestParam("taskName") String taskName,
                             @RequestParam("description") String description,
                             HttpSession session) {
        Task newTask = taskService.newTask();
        newTask.setParentTaskId(taskId);

        User user = SessionUtil.getUserFromSession(session);
        newTask.setOwner(user.getId());
        newTask.setAssignee(user.getId());
        newTask.setName(taskName);
        newTask.setDescription(description);

        taskService.saveTask(newTask);

        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch06/task/getForm/" + taskId;
    }

    /**
     * 删除子任务
     *
     * @param taskId
     * @param session
     * @return
     */
    @RequestMapping("/task/subtask/delete/{taskId}")
    public String deleteSubTask(@PathVariable("taskId") String taskId,
                                HttpSession session) {
        User user = SessionUtil.getUserFromSession(session);
        taskService.deleteTask(taskId, "deleteByUser: " + user.getId());

        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch06/task/archived/" + taskId;
    }

    /**
     * 查询历史任务
     *
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/task/archived/{taskId}")
    public ModelAndView taskArchived(@PathVariable("taskId") String taskId) {
        ModelAndView mav = new ModelAndView("/ch06/taskFormArchived");

        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId).singleResult();
        if (null != task.getParentTaskId()) {
            HistoricTaskInstance parentTask = historyService.createHistoricTaskInstanceQuery()
                    .taskId(task.getParentTaskId()).singleResult();
            mav.addObject("parentTask", parentTask);
        }
        mav.addObject("task", task);

        //读取子任务
        List<HistoricTaskInstance> subTasks = historyService.createHistoricTaskInstanceQuery()
                .taskParentTaskId(taskId).list();
        mav.addObject("subTasks", subTasks);

        //读取附件
        List<Attachment> attachmentList = null;
        if (null != task.getTaskDefinitionKey()) {
            attachmentList = taskService.getTaskAttachments(taskId);
        } else {
            attachmentList = taskService.getProcessInstanceAttachments(task.getProcessInstanceId());
        }
        mav.addObject("attachmentList", attachmentList);

        return mav;
    }

    /**
     * 创建任务
     *
     * @param taskName
     * @param description
     * @param priority
     * @param dueDate
     * @param session
     * @return
     */
    @RequestMapping("/task/new")
    public String newTask(@RequestParam("taskName") String taskName,
                          @RequestParam("description") String description,
                          @RequestParam("priority") int priority,
                          @RequestParam("dueDate") String dueDate,
                          HttpSession session) {
        Task newTask = taskService.newTask();

        User user = SessionUtil.getUserFromSession(session);
        newTask.setOwner(user.getId());
        newTask.setAssignee(user.getId());
        newTask.setName(taskName);
        newTask.setDescription(description);

        if (StringUtils.isNotBlank(dueDate)) {
            newTask.setDueDate(Date.valueOf(dueDate));
        }

        newTask.setPriority(priority);

        taskService.saveTask(newTask);

        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch06/task/getForm/" + newTask.getId();
    }

    /**
     * 任务委派
     *
     * @param taskId
     * @param delegateUserId
     * @return
     */
    @RequestMapping("/task/delegate/{taskId}")
    @ResponseBody
    public String delegate(@PathVariable("taskId") String taskId,
                           @RequestParam("delegateUserId") String delegateUserId) {
        taskService.delegateTask(taskId, delegateUserId);

        return "success";
    }
}
