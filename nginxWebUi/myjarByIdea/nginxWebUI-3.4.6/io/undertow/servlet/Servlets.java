package io.undertow.servlet;

import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ErrorPage;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.LoginConfig;
import io.undertow.servlet.api.SecurityConstraint;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.api.WebResourceCollection;
import io.undertow.servlet.core.ServletContainerImpl;
import java.util.EventListener;
import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

public class Servlets {
   private static volatile ServletContainer container;

   public static ServletContainer defaultContainer() {
      if (container != null) {
         return container;
      } else {
         Class var0 = Servlets.class;
         synchronized(Servlets.class) {
            return container != null ? container : (container = ServletContainer.Factory.newInstance());
         }
      }
   }

   public static ServletContainer newContainer() {
      return new ServletContainerImpl();
   }

   public static DeploymentInfo deployment() {
      return new DeploymentInfo();
   }

   public static ServletInfo servlet(Class<? extends Servlet> servletClass) {
      return servlet(servletClass.getSimpleName(), servletClass);
   }

   public static ServletInfo servlet(String name, Class<? extends Servlet> servletClass) {
      return new ServletInfo(name, servletClass);
   }

   public static ServletInfo servlet(String name, Class<? extends Servlet> servletClass, InstanceFactory<? extends Servlet> servlet) {
      return new ServletInfo(name, servletClass, servlet);
   }

   public static FilterInfo filter(Class<? extends Filter> filterClass) {
      return filter(filterClass.getSimpleName(), filterClass);
   }

   public static FilterInfo filter(String name, Class<? extends Filter> filterClass) {
      return new FilterInfo(name, filterClass);
   }

   public static FilterInfo filter(String name, Class<? extends Filter> filterClass, InstanceFactory<? extends Filter> filter) {
      return new FilterInfo(name, filterClass, filter);
   }

   public static MultipartConfigElement multipartConfig(String location, long maxFileSize, long maxRequestSize, int fileSizeThreshold) {
      return new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
   }

   public static ListenerInfo listener(Class<? extends EventListener> listenerClass, InstanceFactory<? extends EventListener> instanceFactory) {
      return new ListenerInfo(listenerClass, instanceFactory);
   }

   public static ListenerInfo listener(Class<? extends EventListener> listenerClass) {
      return new ListenerInfo(listenerClass);
   }

   public static SecurityConstraint securityConstraint() {
      return new SecurityConstraint();
   }

   public static WebResourceCollection webResourceCollection() {
      return new WebResourceCollection();
   }

   private Servlets() {
   }

   public static LoginConfig loginConfig(String realmName, String loginPage, String errorPage) {
      return new LoginConfig(realmName, loginPage, errorPage);
   }

   public static LoginConfig loginConfig(String realmName) {
      return new LoginConfig(realmName);
   }

   public static LoginConfig loginConfig(String mechanismName, String realmName, String loginPage, String errorPage) {
      return new LoginConfig(mechanismName, realmName, loginPage, errorPage);
   }

   public static LoginConfig loginConfig(String mechanismName, String realmName) {
      return new LoginConfig(mechanismName, realmName);
   }

   public static ErrorPage errorPage(String location, Class<? extends Throwable> exceptionType) {
      return new ErrorPage(location, exceptionType);
   }

   public static ErrorPage errorPage(String location, int statusCode) {
      return new ErrorPage(location, statusCode);
   }

   public static ErrorPage errorPage(String location) {
      return new ErrorPage(location);
   }
}
