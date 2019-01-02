package com.activiti.ch07.shared;

import com.activiti.ch07.expression.MyBean;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author restep
 * @date 2019/1/2
 */
public class ExpressionShared {
    public static void expression(IdentityService identityService, RuntimeService runtimeService,
                                  ManagementService managementService, TaskService taskService) {
        MyBean myBean = new MyBean();
        Map<String, Object> variables = new HashMap<>();
        variables.put("myBean", myBean);
        String name = "Henry Yan";
        variables.put("name", name);

        //运行期表达式
        identityService.setAuthenticatedUserId("henryyan");
        //业务ID
        String businessKey = "9999";
        ProcessInstance processInstance = runtimeService.
                startProcessInstanceByKey("expression", businessKey, variables);
        Assert.assertEquals("henryyan",
                runtimeService.getVariable(processInstance.getId(), "authenticatedUserIdForTest"));
        Assert.assertEquals("Henry Yan, added by print(String name)",
                runtimeService.getVariable(processInstance.getId(), "returnValue"));
        Assert.assertEquals(businessKey,
                runtimeService.getVariable(processInstance.getId(), "businessKey"));

        List<Map<String, Object>> rowList = managementService.createTablePageQuery()
                .tableName("ACT_HI_DETAIL").listPage(0, 100).getRows();
        for (Map<String, Object> row : rowList) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
            System.out.println("-------------------------");
        }

        System.out.println("==============字节流数据==============");
        //引擎将MyBean已二进制存入ACT_GE_BYTEARRAY
        rowList = managementService.createTablePageQuery()
                .tableName("ACT_GE_BYTEARRAY").listPage(0, 100).getRows();
        for (Map<String, Object> row : rowList) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
            System.out.println("-------------------------");
        }

        // DelegateTask 设置的变量
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId()).singleResult();
        String setByTask = (String) taskService.getVariable(task.getId(), "setByTask");
        Assert.assertEquals("I'm setted by DelegateTask, " + name, setByTask);
    }
}
