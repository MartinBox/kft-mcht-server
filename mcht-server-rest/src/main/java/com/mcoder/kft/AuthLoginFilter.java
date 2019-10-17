package com.mcoder.kft;

import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Order(1)
@WebFilter(
        filterName = "authLoginFilter",
        urlPatterns = {"/*"},

        asyncSupported = true
)
public class AuthLoginFilter implements Filter {
    /**
     * 不过滤的uri
     */
    List<String> excludeFilter = Arrays.asList("/user_login",
            "/login",
            "/test",
            "/identify_send",
            "/static/",
            "/plugins/",
            "/easyui/");

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Object userName = request.getSession().getAttribute("userName");
        if (excludeFilter.stream().filter(s -> request.getRequestURI().startsWith(s)).findFirst().isPresent()) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (null == userName) {
            response.sendRedirect("login");
            return;
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
