package com.activiti.service.ch07;

import com.activiti.entity.ch07.Leave;

import java.util.List;
import java.util.Map;

/**
 * @author restep
 * @date 2019/1/3
 */
public interface LeaveService {
    /**
     * 添加
     * @param leave
     */
    void save(Leave leave);

    /**
     * 删除
     * @param id
     */
    void delete(Integer id);

    /**
     * 查询单个对象
     * @param id
     * @return
     */
    Leave get(Integer id);

    /**
     * 启动流程
     * @param leave
     * @param userId
     * @param variables
     */
    void start(Leave leave, String userId, Map<String, Object> variables);

    /**
     * 待办列表
     * @param userId
     * @return
     */
    List<Leave> queryTodoTaskList(String userId);

    /**
     * 完成任务
     * @param leave
     * @param saveEntity
     * @param taskId
     * @param variables
     */
    void complete(Leave leave, Boolean saveEntity, String taskId, Map<String, Object> variables);
}
