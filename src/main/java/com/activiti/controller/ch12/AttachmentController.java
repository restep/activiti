package com.activiti.controller.ch12;

import com.activiti.controller.AbstractController;
import com.activiti.util.SessionUtil;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Attachment;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author restep
 * @date 2019/1/12
 */
@Controller
@RequestMapping(value = "/ch12")
public class AttachmentController extends AbstractController {
    /**
     * 文件类型的附件
     *
     * @param taskId
     * @param processInstanceId
     * @param attachmentName
     * @param attachmentDescription
     * @param file
     * @param session
     * @return
     */
    @RequestMapping(value = "/attachment/new/file")
    public String newFile(@RequestParam("taskId") String taskId,
                          @RequestParam("processInstanceId") String processInstanceId,
                          @RequestParam("attachmentName") String attachmentName,
                          @RequestParam("attachmentDescription") String attachmentDescription,
                          @RequestParam("file") MultipartFile file,
                          HttpSession session) {
        User user = SessionUtil.getUserFromSession(session);
        identityService.setAuthenticatedUserId(user.getId());

        String attachmentType = file.getContentType() + ";"
                + FilenameUtils.getExtension(file.getOriginalFilename());
        try {
            Attachment attachment = taskService.createAttachment(attachmentType, taskId, processInstanceId,
                    attachmentName, attachmentDescription, file.getInputStream());
            taskService.saveAttachment(attachment);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch06/task/getForm/" + taskId;
    }

    /**
     * URL类型的附件
     *
     * @param taskId
     * @param processInstanceId
     * @param attachmentName
     * @param attachmentDescription
     * @param url
     * @param session
     * @return
     */
    @RequestMapping(value = "/attachment/new/url")
    public String newUrl(@RequestParam("taskId") String taskId,
                         @RequestParam("processInstanceId") String processInstanceId,
                         @RequestParam("attachmentName") String attachmentName,
                         @RequestParam("attachmentDescription") String attachmentDescription,
                         @RequestParam("url") String url,
                         HttpSession session) {
        User user = SessionUtil.getUserFromSession(session);
        identityService.setAuthenticatedUserId(user.getId());

        taskService.createAttachment("url", taskId, processInstanceId,
                attachmentName, attachmentDescription, url);

        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/ch06/task/getForm/" + taskId;
    }

    /**
     * 删除附件
     *
     * @param attachmentId
     * @return
     */
    @RequestMapping(value = "/attachment/delete/{attachmentId}")
    @ResponseBody
    public String delete(@PathVariable("attachmentId") String attachmentId) {
        taskService.deleteAttachment(attachmentId);

        return "true";
    }

    /**
     * 下载附件
     *
     * @param attachmentId
     * @return
     */
    @RequestMapping(value = "/attachment/download/{attachmentId}")
    @ResponseBody
    public void download(@PathVariable("attachmentId") String attachmentId,
                         HttpServletResponse response) {
        Attachment attachment = taskService.getAttachment(attachmentId);
        String contentType = StringUtils.substringBefore(attachment.getType(), ";");
        response.addHeader("Content-Type", contentType + ";charset=UTF-8");

        String extensionFileName = StringUtils.substringAfter(attachment.getType(), ";");
        String fileName = attachment.getName() + "." + extensionFileName;
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        InputStream attachmentContent = taskService.getAttachmentContent(attachmentId);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(attachmentContent);
        try {
            IOUtils.copy(bufferedInputStream, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
