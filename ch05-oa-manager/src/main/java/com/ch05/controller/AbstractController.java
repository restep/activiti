package com.ch05.controller;

import com.ch05.util.ProcessEngineUtil;
import org.activiti.engine.*;

/**
 * @author restep
 * @date 2018/12/24
 */
public abstract class AbstractController {
    protected RepositoryService repositoryService;
    protected RuntimeService runtimeService;
    protected TaskService taskService;
    protected HistoryService historyService;
    protected IdentityService identityService;
    protected ManagementService managementService;
    protected FormService formService;

    public AbstractController() {
        ProcessEngine processEngine = ProcessEngineUtil.getInstance();
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
        identityService = processEngine.getIdentityService();
        managementService = processEngine.getManagementService();
        formService = processEngine.getFormService();
    }
}
