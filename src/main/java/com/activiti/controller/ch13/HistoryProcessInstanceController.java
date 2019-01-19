package com.activiti.controller.ch13;

import com.activiti.controller.AbstractController;
import com.activiti.util.Page;
import com.activiti.util.PageUtil;
import org.activiti.engine.history.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 已归档流程实例
 *
 * @author restep
 * @date 2019/1/18
 */
@Controller
@RequestMapping(value = "/ch13")
public class HistoryProcessInstanceController extends AbstractController {
    /**
     * 查询已结束流程实例
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/history/finished")
    public ModelAndView finishedProcessInstanceList(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("ch13/processFinished");
        Page<HistoricProcessInstance> page = new Page<>(PageUtil.PAGE_SIZE);
        int[] pageParams = PageUtil.init(page, request);
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService
                .createHistoricProcessInstanceQuery().finished();
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery
                .listPage(pageParams[0], pageParams[1]);

        // 查询流程定义对象
        Map<String, ProcessDefinition> definitionMap = new HashMap<>();

        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
            definitionCache(definitionMap, historicProcessInstance.getProcessDefinitionId());
        }

        page.setResult(historicProcessInstances);
        page.setTotalCount(historicProcessInstanceQuery.count());
        mav.addObject("page", page);
        mav.addObject("definitions", definitionMap);

        return mav;
    }

    /**
     * 查询历史相关信息
     *
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "/finished/view/{processInstanceId}")
    public ModelAndView historyDatas(@PathVariable("processInstanceId") String processInstanceId) {
        ModelAndView mav = new ModelAndView("ch13/processViewFinished");

        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();

        // 查询历史流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();

        // 查询流程有关的变量
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).list();

        List<HistoricDetail> formProperties = historyService.createHistoricDetailQuery().processInstanceId(processInstanceId).formProperties().list();

        // 查询流程定义对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(historicProcessInstance.getProcessDefinitionId()).singleResult();

        mav.addObject("historicProcessInstance", historicProcessInstance);
        mav.addObject("variableInstances", variableInstances);
        mav.addObject("activities", activityInstances);
        mav.addObject("formProperties", formProperties);
        mav.addObject("processDefinition", processDefinition);

        return mav;
    }

    /**
     * 流程定义对象缓存
     *
     * @param definitionMap
     * @param processDefinitionId
     */
    private void definitionCache(Map<String, ProcessDefinition> definitionMap, String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();

        //放入缓存
        definitionMap.put(processDefinitionId, processDefinition);
    }
}
