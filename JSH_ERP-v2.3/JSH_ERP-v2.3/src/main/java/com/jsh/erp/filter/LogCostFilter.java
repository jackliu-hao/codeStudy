package com.jsh.erp.filter;

import org.springframework.util.StringUtils;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter(filterName = "LogCostFilter", urlPatterns = {"/*"},
        initParams = {@WebInitParam(name = "ignoredUrl", value = ".css#.js#.jpg#.png#.gif#.ico"),
                      @WebInitParam(name = "filterPath",
                              value = "/user/login#/user/registerUser#/v2/api-docs")})
public class LogCostFilter implements Filter {

    private static final String FILTER_PATH = "filterPath";
    private static final String IGNORED_PATH = "ignoredUrl";

    private static final List<String> ignoredList = new ArrayList<>();
    private String[] allowUrls;
    private String[] ignoredUrls;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String filterPath = filterConfig.getInitParameter(FILTER_PATH);
        if (!StringUtils.isEmpty(filterPath)) {
            allowUrls = filterPath.contains("#") ? filterPath.split("#") : new String[]{filterPath};
        }

        String ignoredPath = filterConfig.getInitParameter(IGNORED_PATH);
        if (!StringUtils.isEmpty(ignoredPath)) {
            ignoredUrls = ignoredPath.contains("#") ? ignoredPath.split("#") : new String[]{ignoredPath};
            for (String ignoredUrl : ignoredUrls) {
                ignoredList.add(ignoredUrl);
            }
        }
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        String requestUrl = servletRequest.getRequestURI();
        //具体，比如：处理若用户未登录，则跳转到登录页
        Object userInfo = servletRequest.getSession().getAttribute("user");
        if(userInfo!=null) { //如果已登录，不阻止
            chain.doFilter(request, response);
            return;
        }
        if (requestUrl != null && (requestUrl.contains("/doc.html") ||
            requestUrl.contains("/register.html") || requestUrl.contains("/login.html"))) {
            chain.doFilter(request, response);
            return;
        }
        if (verify(ignoredList, requestUrl)) {
            chain.doFilter(servletRequest, response);
            return;
        }
        if (null != allowUrls && allowUrls.length > 0) {
            for (String url : allowUrls) {
                if (requestUrl.startsWith(url)) {
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        servletResponse.sendRedirect("/login.html");
    }

    private static String regexPrefix = "^.*";
    private static String regexSuffix = ".*$";

        /**
     * 验证给定的URL是否在忽略列表中。忽略列表中的每个元素都会与URL通过一个经过动态构建的正则表达式进行匹配。
     *
     * @param ignoredList 忽略列表，其中的字符串将与URL结合前后缀构造成完整的正则表达式用于匹配
     * @param url 待验证的URL
     * @return 如果URL经过构造后的任一正则表达式匹配成功，则返回true，表示URL应该被忽略；否则返回false，表示URL不在忽略列表中
     */
    private static boolean verify(List<String> ignoredList, String url) {
        for (String regex : ignoredList) {
            // 使用预定义的regexPrefix和regexSuffix与列表中的正则表达式组合，形成完整的正则表达式以匹配URL
            Pattern pattern = Pattern.compile(regexPrefix + regex + regexSuffix);
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                // 如果URL与当前正则表达式完全匹配，则返回true
                return true;
            }
        }
        // 所有正则表达式均未匹配成功，则返回false
        return false;
    }

    @Override
    public void destroy() {

    }
}