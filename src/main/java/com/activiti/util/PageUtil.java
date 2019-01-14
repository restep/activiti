package com.activiti.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author restep
 * @date 2019/1/14
 */
public class PageUtil {
    public static int PAGE_SIZE = 5;

    public static int[] init(Page<?> page, HttpServletRequest request) {
        int pageNumber = Integer.parseInt(StringUtils.defaultIfEmpty(request.getParameter("p"), "1"));
        page.setPageNo(pageNumber);

        int pageSize = Integer.parseInt(StringUtils.defaultIfEmpty(request.getParameter("ps"), String.valueOf(PAGE_SIZE)));
        page.setPageSize(pageSize);

        int firstResult = page.getFirst() - 1;
        int maxResults = page.getPageSize();
        return new int[]{firstResult, maxResults};
    }
}
