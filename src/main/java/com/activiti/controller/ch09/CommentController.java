package com.activiti.controller.ch09;

import com.activiti.util.SessionUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author restep
 * @date 2019/1/7
 */
@Controller
@RequestMapping(value = "/ch09")
public class CommentController {
    @Autowired
    TaskService taskService;

    @Autowired
    IdentityService identityService;

    @Autowired
    HistoryService historyService;

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
    public Map<String, Object> list(@PathVariable("processInstanceId") String processInstanceId) {
        List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);

        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).list();
        Map<String, String> taskNameMap = new HashMap<>();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            taskNameMap.put(historicTaskInstance.getId(), historicTaskInstance.getName());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("commentList", commentList);
        map.put("taskNameMap", taskNameMap);

        return map;
    }
}
