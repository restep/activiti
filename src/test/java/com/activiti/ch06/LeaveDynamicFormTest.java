package com.activiti.ch06;

import com.activiti.base.AbstractTest;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricFormProperty;
import org.activiti.engine.history.HistoricVariableUpdate;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author restep
 * @date 2018/12/27
 */
public class LeaveDynamicFormTest extends AbstractTest {
    /**
     * 起始日期
     */
    private static final String START_DATE = "startDate";

    /**
     * 结束日期
     */
    private static final String END_DATE = "endDate";

    /**
     * 原因
     */
    private static final String REASON = "reason";

    /**
     * 部门领导审批
     */
    private static final String DEPT_LEADER_APPROVED = "deptLeaderApproved";

    /**
     * 人事审批
     */
    private static final String HR_APPROVED = "hrApproved";

    /**
     * 销假
     */
    private static final String REPORT_BACK_DATE = "reportBackDate";

    /**
     * 重新申请
     */
    private static final String RE_APPLY = "reApply";

    @Test
    public void javascriptFormTypeTest() {
        //部署流程定义文件
        Deployment deployment = repositoryService.createDeployment().
                addClasspathResource("ch06/dynamicform/leave.bpmn").deploy();

        //验证是否部署成功
        long count = repositoryService.createProcessDefinitionQuery().
                deploymentId(deployment.getId()).count();
        Assert.assertEquals(1L, count);

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        Assert.assertEquals("leave", processDefinition.getKey());

        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        List<FormProperty> formPropertyList = startFormData.getFormProperties();
        for (FormProperty formProperty : formPropertyList) {
            System.out.println(formProperty.getId() + " : " + formProperty.getValue());
        }

    }

    /**
     * 部门领导和人事全部通过
     */
    @Test
    public void allApproved() {
        //验证是否部署成功
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("leave").singleResult();
        Assert.assertNotNull(processDefinition);

        //设置当前用户
        String currentUserId = "restep";
        identityService.setAuthenticatedUserId(currentUserId);

        //获取表单填写的内容
        Map<String, String> variables = getVariables();

        //启动流程
        ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), variables);
        Assert.assertNotNull(processInstance);

        //部门领导审批通过
        Task deptLeaderTask = taskService.createTaskQuery().taskCandidateGroup("deptLeader").singleResult();
        variables = new HashMap<>();
        variables.put(DEPT_LEADER_APPROVED, "true");
        formService.submitTaskFormData(deptLeaderTask.getId(), variables);

        //人事审批通过
        Task hrTask = taskService.createTaskQuery().taskCandidateGroup("hr").singleResult();
        variables = new HashMap<>();
        variables.put(HR_APPROVED, "true");
        formService.submitTaskFormData(hrTask.getId(), variables);

        //销假(根据申请人的用户ID读取)
        Task reportBackTask = taskService.createTaskQuery().taskAssignee(currentUserId).singleResult();
        variables = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        variables.put(REPORT_BACK_DATE, sdf.format(calendar.getTime()));
        formService.submitTaskFormData(reportBackTask.getId(), variables);

        //验证流程是否已经结束
        Map<String, Object> historyVariables = packageVariables(processInstance.getId());
        Assert.assertNotNull(historyVariables);
    }

    /**
     * 领导驳回后申请人取消申请
     */
    @Test
    public void cancelApply() {
        //设置当前用户
        String currentUserId = "restep";
        identityService.setAuthenticatedUserId(currentUserId);

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("leave").singleResult();

        //获取表单填写的内容
        Map<String, String> variables = getVariables();

        //启动流程流程
        ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), variables);
        Assert.assertNotNull(processInstance);

        //部门领导审批通过
        Task deptLeaderTask = taskService.createTaskQuery().taskCandidateGroup("deptLeader").singleResult();
        variables = new HashMap<>();
        variables.put(DEPT_LEADER_APPROVED, "false");
        formService.submitTaskFormData(deptLeaderTask.getId(), variables);

        //调整申请
        Task modifyTask = taskService.createTaskQuery().taskAssignee(currentUserId).singleResult();
        variables = new HashMap<>();
        variables.put(RE_APPLY, "false");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String startDate = sdf.format(calendar.getTime());
        //当前日期加2天
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        String endDate = sdf.format(calendar.getTime());
        variables.put(START_DATE, startDate);
        variables.put(END_DATE, endDate);
        variables.put(REASON, "公休");
        formService.submitTaskFormData(modifyTask.getId(), variables);

        // 读取历史变量
        Map<String, Object> historyVariables = packageVariables(processInstance.getId());
        Assert.assertNotNull(historyVariables);
    }

    /**
     * 获取表单填写的内容
     *
     * @return
     */
    private Map<String, String> getVariables() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String startDate = sdf.format(calendar.getTime());
        //当前日期加2天
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        String endDate = sdf.format(calendar.getTime());

        Map<String, String> variables = new HashMap<>();
        variables.put(START_DATE, startDate);
        variables.put(END_DATE, endDate);
        variables.put(REASON, "公休");

        return variables;
    }

    /**
     * 读取历史变量并封装到map中
     *
     * @param processInstanceId
     * @return
     */
    private Map<String, Object> packageVariables(String processInstanceId) {
        Map<String, Object> historyVariables = new HashMap<>();
        List<HistoricDetail> historicDetailList = historyService.createHistoricDetailQuery()
                .processInstanceId(processInstanceId).list();
        for (HistoricDetail historicDetail : historicDetailList) {
            if (historicDetail instanceof HistoricFormProperty) {
                //表单中的字段
                HistoricFormProperty historicFormProperty = (HistoricFormProperty) historicDetail;
                historyVariables.put(historicFormProperty.getPropertyId(), historicFormProperty.getPropertyValue());
                System.out.println("form field: taskId=" + historicFormProperty.getTaskId()
                        + " , " + historicFormProperty.getPropertyId()
                        + " = " + historicFormProperty.getPropertyValue());
            } else if (historicDetail instanceof HistoricVariableUpdate) {
                HistoricVariableUpdate historicVariableUpdate = (HistoricVariableUpdate) historicDetail;
                historyVariables.put(historicVariableUpdate.getVariableName(), historicVariableUpdate.getValue());
                System.out.println("variable: " + historicVariableUpdate.getVariableName()
                        + " = " + historicVariableUpdate.getValue());
            }
        }

        return historyVariables;
    }
}
