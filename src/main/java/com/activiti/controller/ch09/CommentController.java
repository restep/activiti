package com.activiti.controller.ch09;

import com.activiti.controller.AbstractController;
import com.activiti.util.SessionUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Event;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author restep
 * @date 2019/1/7
 */
@Controller
@RequestMapping(value = "/ch09")
public class CommentController extends AbstractController {
    /**
     * 保存意见
     */
    @RequestMapping(value = "/comment/save")
    public void addComment(@RequestParam("taskId") String taskId,
                           @RequestParam("processInstanceId") String processInstanceId,
                           @RequestParam("message") String message, HttpSession session) {
        User user = SessionUtil.getUserFromSession(session);
        identityService.setAuthenticatedUserId(user.getId());
        taskService.addComment(taskId, processInstanceId, message);
    }

    /**
     * 读取意见
     */
    @RequestMapping(value = "/comment/list/{processInstanceId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> list(@PathVariable("processInstanceId") String processInstanceId,
                                    @RequestParam("taskId") String taskId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> commentAndEventsMap = new HashMap<>();

        if (StringUtils.isNotBlank(processInstanceId)) {
            List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
            for (Comment comment : commentList) {
                try {
                    String commentId = PropertyUtils.getProperty(comment, "id").toString();
                    commentAndEventsMap.put(commentId, comment);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

            //提取任务名称
            List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId).list();
            Map<String, String> taskNameMap = new HashMap<>();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                taskNameMap.put(historicTaskInstance.getId(), historicTaskInstance.getName());
            }
            result.put("taskNameMap", taskNameMap);
        }

        //查询所有类型的事件
        if (StringUtils.isNotBlank(taskId)) {
            List<Event> eventList = taskService.getTaskEvents(taskId);
            for (Event event : eventList) {
                try {
                    String commentId = PropertyUtils.getProperty(event, "id").toString();
                    commentAndEventsMap.put(commentId, event);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        result.put("events", commentAndEventsMap.values());

        return result;
    }
}
