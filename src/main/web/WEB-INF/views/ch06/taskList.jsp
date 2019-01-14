<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/common/global.jsp" %>
    <%@ include file="/common/meta.jsp" %>
    <%@ include file="/common/include-base-styles.jsp" %>
    <title>待办任务列表--chapter6</title>

    <script src="${ctx}/js/common/jquery.js" type="text/javascript"></script>
</head>
<body>
<c:if test="${not empty message}">
    <div id="message" class="alert alert-success">${message}</div>
    <!-- 自动隐藏提示信息 -->
    <script type="text/javascript">
        setTimeout(function () {
            $('#message').hide('slow');
        }, 5000);
    </script>
</c:if>
<table width="100%" class="table table-bordered table-hover table-condensed">
    <thead>
    <tr>
        <th>任务ID</th>
        <th>任务名称</th>
        <th>流程实例ID</th>
        <th>流程定义ID</th>
        <th>任务创建时间</th>
        <th>办理人</th>
        <th>操作<a href="#newTaskModal" data-toggle="modal" class="btn btn-primary btn-small" style="float:right;">
            <i class="icon-plus"></i>新任务</a></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${taskList}" var="task">
        <tr>
            <td>${task.id }</td>
            <td>${task.name }</td>
            <td>${task.processInstanceId }</td>
            <td>${task.processDefinitionId }</td>
            <td style="text-align:center;">
                <c:if test="${empty task.parentTaskId}">无</c:if>
                <c:if test="${not empty task.parentTaskId}">
                    <a href="${ctx}/task/getForm/${task.parentTaskId}">${task.parentTaskId}</a>
                </c:if>
            </td>
            <td style="text-align: center">
                <c:if test="${task.delegationState == 'PENDING' }">
                    <i class="icon-ok"></i>被委派
                </c:if>
                <c:if test="${task.delegationState == 'RESOLVED' }">
                    <i class="icon-ok"></i>任务已处理完成
                </c:if>
            </td>
            <td>
                <fmt:formatDate value="${task.createTime}" pattern="yyyy-MM-dd hh:mm:ss"/>
            </td>
            <td>${task.assignee }</td>
            <td>
                <c:if test="${empty task.assignee }">
                    <a class="btn" href="${ctx}/ch06/task/getForm/${task.id}"><i class="icon-eye-open"></i>查看</a>
                    <a class="btn" href="${ctx}/ch06/task/claim/${task.id}"><i class="icon-eye-open"></i>签收</a>
                </c:if>
                <c:if test="${not empty task.assignee }">
                    <a class="btn" href="${ctx}/ch06/task/getForm/${task.id}"><i class="icon-user"></i>办理</a>
                    <a class="btn" href="${ctx}/ch06/task/unclaim/${task.id}"><i class="icon-remove"></i>反签收</a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>