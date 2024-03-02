package com.xq.tmall.controller.test;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author   Email:
 * @description:
 * @Version
 * @create 2023-12-27 14:35
 */

public class TestRequestMappingService implements InitializingBean {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    /**
     * 核心配置类，因为`RequestMappingHandlerMapping`中config属性未提供公共方法，所以需要自行构建
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.config.setTrailingSlashMatch(handlerMapping.useTrailingSlashMatch());
        this.config.setContentNegotiationManager(handlerMapping.getContentNegotiationManager());

        if (handlerMapping.getPatternParser() != null) {
            this.config.setPatternParser(handlerMapping.getPatternParser());
            Assert.isTrue(!handlerMapping.useSuffixPatternMatch() && !handlerMapping.useRegisteredSuffixPatternMatch(),
                    "Suffix pattern matching not supported with PathPatternParser.");
        }
        else {
            this.config.setSuffixPatternMatch(handlerMapping.useSuffixPatternMatch());
            this.config.setRegisteredSuffixPatternMatch(handlerMapping.useRegisteredSuffixPatternMatch());
            this.config.setPathMatcher(handlerMapping.getPathMatcher());
        }
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity execute(@PathVariable(required = false) Map<String, String> pathVar,
                                  @RequestParam(required = false) Map<String, Object> param,
                                  HttpServletRequest request, HttpServletResponse response) throws Throwable {
        //todo 业务执行逻辑
        return null;
    }

    /**
     * 注册
     * @param pattern
     * @param method
     */
    public void registerMapping(String pattern,String method) throws NoSuchMethodException {
        RequestMappingInfo mappingInfo = RequestMappingInfo.paths(pattern)
                .methods(RequestMethod.valueOf(method))
                .options(this.config)
                .build();
        Method targetMethod = TestRequestMappingService.class.getDeclaredMethod("execute", Map.class, Map.class, HttpServletRequest.class, HttpServletResponse.class);
        handlerMapping.registerMapping(mappingInfo, this, targetMethod);
    }

    /**
     * 取消注册mapping
     * @param pattern
     * @param method
     */
    public synchronized void cancelMapping(String pattern,String method) {
        RequestMappingInfo mappingInfo = RequestMappingInfo.paths(pattern)
                .methods(RequestMethod.valueOf(method))
                .options(this.config)
                .build();
        handlerMapping.unregisterMapping(mappingInfo);
    }
}
