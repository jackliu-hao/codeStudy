/*     */ package io.undertow.servlet;
/*     */ 
/*     */ import io.undertow.servlet.api.DeploymentInfo;
/*     */ import io.undertow.servlet.api.ErrorPage;
/*     */ import io.undertow.servlet.api.FilterInfo;
/*     */ import io.undertow.servlet.api.InstanceFactory;
/*     */ import io.undertow.servlet.api.ListenerInfo;
/*     */ import io.undertow.servlet.api.LoginConfig;
/*     */ import io.undertow.servlet.api.SecurityConstraint;
/*     */ import io.undertow.servlet.api.ServletContainer;
/*     */ import io.undertow.servlet.api.ServletInfo;
/*     */ import io.undertow.servlet.api.WebResourceCollection;
/*     */ import io.undertow.servlet.core.ServletContainerImpl;
/*     */ import java.util.EventListener;
/*     */ import javax.servlet.Filter;
/*     */ import javax.servlet.MultipartConfigElement;
/*     */ import javax.servlet.Servlet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Servlets
/*     */ {
/*     */   private static volatile ServletContainer container;
/*     */   
/*     */   public static ServletContainer defaultContainer() {
/*  55 */     if (container != null) {
/*  56 */       return container;
/*     */     }
/*  58 */     synchronized (Servlets.class) {
/*  59 */       if (container != null) {
/*  60 */         return container;
/*     */       }
/*  62 */       return container = ServletContainer.Factory.newInstance();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ServletContainer newContainer() {
/*  72 */     return (ServletContainer)new ServletContainerImpl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DeploymentInfo deployment() {
/*  81 */     return new DeploymentInfo();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ServletInfo servlet(Class<? extends Servlet> servletClass) {
/*  91 */     return servlet(servletClass.getSimpleName(), servletClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ServletInfo servlet(String name, Class<? extends Servlet> servletClass) {
/* 102 */     return new ServletInfo(name, servletClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ServletInfo servlet(String name, Class<? extends Servlet> servletClass, InstanceFactory<? extends Servlet> servlet) {
/* 113 */     return new ServletInfo(name, servletClass, servlet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FilterInfo filter(Class<? extends Filter> filterClass) {
/* 124 */     return filter(filterClass.getSimpleName(), filterClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FilterInfo filter(String name, Class<? extends Filter> filterClass) {
/* 135 */     return new FilterInfo(name, filterClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FilterInfo filter(String name, Class<? extends Filter> filterClass, InstanceFactory<? extends Filter> filter) {
/* 146 */     return new FilterInfo(name, filterClass, filter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MultipartConfigElement multipartConfig(String location, long maxFileSize, long maxRequestSize, int fileSizeThreshold) {
/* 160 */     return new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
/*     */   }
/*     */   
/*     */   public static ListenerInfo listener(Class<? extends EventListener> listenerClass, InstanceFactory<? extends EventListener> instanceFactory) {
/* 164 */     return new ListenerInfo(listenerClass, instanceFactory);
/*     */   }
/*     */   
/*     */   public static ListenerInfo listener(Class<? extends EventListener> listenerClass) {
/* 168 */     return new ListenerInfo(listenerClass);
/*     */   }
/*     */   
/*     */   public static SecurityConstraint securityConstraint() {
/* 172 */     return new SecurityConstraint();
/*     */   }
/*     */   
/*     */   public static WebResourceCollection webResourceCollection() {
/* 176 */     return new WebResourceCollection();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LoginConfig loginConfig(String realmName, String loginPage, String errorPage) {
/* 183 */     return new LoginConfig(realmName, loginPage, errorPage);
/*     */   }
/*     */   
/*     */   public static LoginConfig loginConfig(String realmName) {
/* 187 */     return new LoginConfig(realmName);
/*     */   }
/*     */   
/*     */   public static LoginConfig loginConfig(String mechanismName, String realmName, String loginPage, String errorPage) {
/* 191 */     return new LoginConfig(mechanismName, realmName, loginPage, errorPage);
/*     */   }
/*     */   
/*     */   public static LoginConfig loginConfig(String mechanismName, String realmName) {
/* 195 */     return new LoginConfig(mechanismName, realmName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ErrorPage errorPage(String location, Class<? extends Throwable> exceptionType) {
/* 205 */     return new ErrorPage(location, exceptionType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ErrorPage errorPage(String location, int statusCode) {
/* 215 */     return new ErrorPage(location, statusCode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ErrorPage errorPage(String location) {
/* 225 */     return new ErrorPage(location);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\Servlets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */