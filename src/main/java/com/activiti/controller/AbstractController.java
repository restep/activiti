package com.activiti.controller;

import org.activiti.engine.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author restep
 * @date 2018/12/24
 */
public abstract class AbstractController {
    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected IdentityService identityService;

    @Autowired
    protected ManagementService managementService;

    @Autowired
    protected FormService formService;
}
