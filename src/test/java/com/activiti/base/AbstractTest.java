package com.activiti.base;

import org.activiti.engine.*;
import org.junit.Before;

/**
 * @author restep
 * @date 2018/12/21
 */
public class AbstractTest {
    protected RepositoryService repositoryService;
    protected RuntimeService runtimeService;
    protected TaskService taskService;
    protected HistoryService historyService;
    protected IdentityService identityService;
    protected ManagementService managementService;
    protected FormService formService;

    @Before
    public void setUp() {
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        configuration.setJdbcDriver("com.mysql.jdbc.Driver");
        configuration.setJdbcUrl("jdbc:mysql://localhost:3306/activiti?useUnicode=true&characterEncoding=UTF-8");
        configuration.setJdbcUsername("root");
        configuration.setJdbcPassword("root");

        /**
         * DB_SCHEMA_UPDATE_FALSE 不能自动创建表
         * DB_SCHEMA_UPDATE_CREATE_DROP 先删除表再创建表
         * DB_SCHEMA_UPDATE_TRUE 如何表不存在 自动创建和更新表
         */
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = configuration.buildProcessEngine();
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
        identityService = processEngine.getIdentityService();
        managementService = processEngine.getManagementService();
        formService = processEngine.getFormService();
    }
}
