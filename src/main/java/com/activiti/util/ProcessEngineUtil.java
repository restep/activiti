package com.activiti.util;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;

/**
 * @author restep
 * @date 2018/12/24
 */
public class ProcessEngineUtil {
    private static ProcessEngine processEngine;

    public static ProcessEngine getInstance() {
        if (null == processEngine) {
            //使用默认的配置文件 base.cfg.xml
            processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault().buildProcessEngine();
        }

        return processEngine;
    }
}
