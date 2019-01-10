<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>《Activiti实战》示例[第6章-任务表单]-登录系统</title>
    <%@ include file="/common/global.jsp" %>
    <%@ include file="/common/meta.jsp" %>
    <%@ include file="/common/include-base-styles.jsp" %>
</head>

<body style="margin-top: 3em;">
<center>
    <c:if test="${not empty param.error}">
        <h2 id="error" class="alert alert-error">用户名或密码错误！！！</h2>
    </c:if>
    <c:if test="${not empty param.timeout}">
        <h2 id="error" class="alert alert-error">未登录或超时！！！</h2>
    </c:if>
    <div style="width: 500px">
        <h2>第6章—任务表单配套示例</h2>
        <form action="${ctx}/user/login" method="get">
            <table>
                <tr>
                    <td width="80">用户名：</td>
                    <td><input id="username" name="username" style="width: 100px"/></td>
                </tr>
                <tr>
                    <td>密码：</td>
                    <td><input id="password" name="password" type="password" style="width: 100px"/></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>
                        <button type="submit" class="btn btn-primary">登录系统</button>
                    </td>
                </tr>
            </table>
        </form>

        <div class="row">
            <h5>登录失败时 请运行com.activiti.ch05.IdentityServiceTest.userAndGroupTest</h5>
            <hr/>
            <h4 class="text-info">用户与部门列表（密码：111111）</h4>
            <table class="table table-hover">
                <thead>
                <tr>
                    <th>#</th>
                    <th>用户名</th>
                    <th>部门</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>1</td>
                    <td>henry</td>
                    <td>IT</td>
                </tr>
                <tr>
                    <td>2</td>
                    <td>bill</td>
                    <td>领导</td>
                </tr>
                <tr>
                    <td>3</td>
                    <td>jenny</td>
                    <td>人事</td>
                </tr>
                <tr>
                    <td>4</td>
                    <td>houqin</td>
                    <td>供货方</td>
                </tr>
                <tr>
                    <td>5</td>
                    <td>caiwu</td>
                    <td>财务</td>
                </tr>
                <tr>
                    <td>6</td>
                    <td>boss</td>
                    <td>总经理</td>
                </tr>
                <tr>
                    <td>7</td>
                    <td>chuna</td>
                    <td>出纳</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</center>
</body>
</html>