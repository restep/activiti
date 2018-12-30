package com.activiti.ch05.controller;

import com.activiti.controller.AbstractController;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @author restep
 * @date 2018/12/24
 */
@Controller
@RequestMapping(value = "/ch05")
public class DeploymentController extends AbstractController {
    /**
     * 流程定义列表
     *
     * @return
     */
    @RequestMapping(value = "/processList")
    public ModelAndView processList() {
        //对应WEB-INF/views/ch05/processList.jsp
        ModelAndView modelAndView = new ModelAndView("ch05/processList");
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().list();
        modelAndView.addObject("processDefinitionList", processDefinitionList);

        return modelAndView;
    }

    /**
     * 部署
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/deploy")
    public String deploy(@RequestParam(value = "file") MultipartFile file) {
        //获取上传的文件名
        String fileName = file.getOriginalFilename();

        try {
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
            //得到输入流对象
            InputStream inputStream = file.getInputStream();

            //文件的扩展名
            String extension = FilenameUtils.getExtension(fileName);
            if (StringUtils.equals("zip", extension) || StringUtils.equals("bar", extension)) {
                //zip或者bar类型用ZipInputStream方式部署
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                deploymentBuilder.addZipInputStream(zipInputStream);
            } else {
                //其他类型的文件直接部署
                deploymentBuilder.addInputStream(fileName, inputStream);
            }

            deploymentBuilder.deploy();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:ch05/processList";
    }

    /**
     * 读取流程资源
     *
     * @param processDefinitionId
     * @param resourceName
     * @param response
     */
    @RequestMapping(value = "/readResource")
    public void readResource(@RequestParam("processDefinitionId") String processDefinitionId,
                             @RequestParam("resourceName") String resourceName,
                             HttpServletResponse response) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        ProcessDefinition processDefinition = processDefinitionQuery.processDefinitionId(processDefinitionId).singleResult();

        //通过接口读取
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);

        //输出资源内容到相应的对象
        try {
            byte[] arr = new byte[1024];
            int len = -1;
            while (-1 != (len = inputStream.read(arr, 0, 1024))) {
                response.getOutputStream().write(arr, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除部署的流程 级联删除流程实例
     * @param deploymentId
     * @return
     */
    @RequestMapping(value = "/deleteDeployment")
    public String deleteDeployment(@RequestParam("deploymentId") String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);

        return "redirect:ch05/processList";
    }
}

