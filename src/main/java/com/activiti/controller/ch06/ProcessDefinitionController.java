package com.activiti.controller.ch06;

import com.activiti.controller.AbstractController;
import com.activiti.util.SessionUtil;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程定义相关功能：读取动态表单字段 读取外置表单内容
 *
 * @author restep
 * @date 2018/12/31
 */
@Controller
@RequestMapping(value = "/ch06")
public class ProcessDefinitionController extends AbstractController {
    /**
     * 读取启动流程的表单字段
     *
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

    /**
     * @param processDefinitionId
     * @param request
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/processInstance/start/{processDefinitionId}")
    public String startProcessInstance(@PathVariable("processDefinitionId") String processDefinitionId,
                                       HttpServletRequest request,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        boolean hasStartFormKey = processDefinition.hasStartFormKey();

        Map<String, String> formValue = new HashMap<>();
        //formkey表单
        if (hasStartFormKey) {
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                formValue.put(key, entry.getValue()[0]);
            }
        } else {    //动态表单
            //先读取表单字段 再根据表单字段的ID读取请求参数值
            StartFormData startFormData = formService.getStartFormData(processDefinitionId);
            //从请求中获取表单字段的值
            List<FormProperty> formPropertyList = startFormData.getFormProperties();
            for (FormProperty formProperty : formPropertyList) {
                String value = request.getParameter(formProperty.getId());
                formValue.put(formProperty.getId(), value);
            }
        }

        //获取当前登录的用户
        User user = SessionUtil.getUserFromSession(session);
        //用户未登录不能操作
        if (null == user || StringUtils.isBlank(user.getId())) {
            return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/login?timeout=true";
        }
        identityService.setAuthenticatedUserId(user.getId());

        //提交表单字段并启动一个新的流程
        ProcessInstance processInstance = formService.submitStartFormData(processDefinitionId, formValue);
        redirectAttributes.addFlashAttribute("message", "流程已启动, 实例ID: " + processInstance.getId());
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch05/processList";
    }
}
