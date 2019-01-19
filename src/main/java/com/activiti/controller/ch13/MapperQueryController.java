package com.activiti.controller.ch13;

import com.activiti.controller.AbstractController;
import com.activiti.dao.ch13.TaskQueryMapper;
import com.activiti.entity.ch13.RunningTask;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.activiti.engine.impl.cmd.CustomSqlExecution;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author restep
 * @date 2019/1/18
 */
@Controller
@RequestMapping(value = "/ch13")
public class MapperQueryController extends AbstractController {
    /**
     * 查询正在运行的任务
     *
     * @param processKey
     * @return
     */
    @RequestMapping(value = "/task/running")
    public ModelAndView list(@RequestParam(value = "processKey", required = false) String processKey) {
        ModelAndView mav = new ModelAndView("ch13/taskRunning");

        CustomSqlExecution<TaskQueryMapper, List<RunningTask>> customSqlExecution =
                new AbstractCustomSqlExecution<TaskQueryMapper, List<RunningTask>>(TaskQueryMapper.class) {
                    @Override
                    public List<RunningTask> execute(TaskQueryMapper customMapper) {
                        // 使用内置实体对象查询
                        // List<TaskEntity> taskByVariable = customMapper.findTasks("applyUserId");

                        List<RunningTask> tasks;
                        if (StringUtils.isBlank(processKey)) {
                            tasks = customMapper.selectRunningTasks();
                        } else {
                            tasks = customMapper.selectRunningTasksByProcessKey(processKey);
                        }

                        return tasks;
                    }
                };

        List<RunningTask> tasks = managementService.executeCustomSql(customSqlExecution);
        mav.addObject("tasks", tasks);

        // 读取引擎中所有的流程定义（只查询最新版本，目的在于获取流程定义的KEY和NAME）
        List<ProcessDefinition> processes = repositoryService.createProcessDefinitionQuery()
                .latestVersion().list();
        mav.addObject("processes", processes);

        return mav;
    }
}
