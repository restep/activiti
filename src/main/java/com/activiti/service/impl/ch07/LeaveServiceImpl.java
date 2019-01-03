package com.activiti.service.impl.ch07;

import com.activiti.dao.ch07.LeaveDao;
import com.activiti.entity.ch07.Leave;
import com.activiti.service.ch07.LeaveService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author restep
 * @date 2019/1/3
 */
@Service
@Transactional
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private LeaveDao leaveDao;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public void save(Leave leave) {
        leaveDao.save(leave);
    }

    @Override
    public void delete(Integer id) {
        leaveDao.delete(id);
    }

    @Override
    public Leave get(Integer id) {
        return leaveDao.get(id);
    }

    @Override
    public void start(Leave leave, String userId, Map<String, Object> variables) {
        if (null == leave.getId()) {
            leave.setApplyTime(new Date());
            leave.setUserId(userId);
        }

        this.save(leave);

        //用来设置启动流程的人员ID 引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(userId);

        String businessKey = leave.getId().toString();
        ProcessInstance processInstance = runtimeService.
                startProcessInstanceByKey("leave", businessKey, variables);
        leave.setProcessInstanceId(processInstance.getId());
        this.save(leave);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Leave> queryTodoTaskList(String userId) {
        List<Task> taskList = taskService.createTaskQuery().processDefinitionKey("leave")
                .taskCandidateOrAssigned(userId).list();

        List<Leave> leaveList = new ArrayList<>();
        for (Task task : taskList) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).singleResult();
            Leave leave = this.get(Integer.parseInt(processInstance.getBusinessKey()));
            leave.setProcessInstance(processInstance);

            leave.setTask(task);

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
            leave.setProcessDefinition(processDefinition);

            leaveList.add(leave);
        }

        return leaveList;
    }

    @Override
    public void complete(Leave leave, Boolean saveEntity, String taskId, Map<String, Object> variables) {
        if (saveEntity) {
            this.save(leave);
        }

        taskService.complete(taskId, variables);
    }
}
