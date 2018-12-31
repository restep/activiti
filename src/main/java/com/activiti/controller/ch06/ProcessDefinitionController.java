package com.activiti.controller.ch06;

import com.activiti.controller.AbstractController;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 流程定义相关功能：读取动态表单字段 读取外置表单内容
 * @author restep
 * @date 2018/12/31
 */
@Controller
@RequestMapping(value = "/ch06")
public class ProcessDefinitionController extends AbstractController {
    /**
     * 读取启动流程的表单字段
     * @param processDefinitionId
     * @return
     */
    @RequestMapping(value = "/getForm/start/{processDefinitionId}")
    public ModelAndView readStartForm(@PathVariable("processDefinitionId") String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();

        //根据是否有formKey属性判断使用哪个展示层
        boolean hasStartFormKey = processDefinition.hasStartFormKey();
        ModelAndView mav = new ModelAndView("ch06/startProcessForm");
        if (hasStartFormKey) {
            Object obj = formService.getRenderedStartForm(processDefinitionId);
            mav.addObject("startFormData", obj);
            mav.addObject("processDefinition", processDefinition);
        } else {
            StartFormData startFormData = formService.getStartFormData(processDefinitionId);
            mav.addObject("startFormData", startFormData);
        }

        mav.addObject("hasStartFormKey", hasStartFormKey);
        mav.addObject("processDefinitionId", processDefinitionId);
        return mav;
    }
}
