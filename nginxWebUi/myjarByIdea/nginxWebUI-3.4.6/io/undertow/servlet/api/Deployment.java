package io.undertow.servlet.api;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.core.ApplicationListeners;
import io.undertow.servlet.core.ErrorPages;
import io.undertow.servlet.core.ManagedFilters;
import io.undertow.servlet.core.ManagedServlets;
import io.undertow.servlet.handlers.ServletPathMatches;
import io.undertow.servlet.spec.ServletContextImpl;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

public interface Deployment {
   DeploymentInfo getDeploymentInfo();

   ServletContainer getServletContainer();

   ApplicationListeners getApplicationListeners();

   ManagedServlets getServlets();

   ManagedFilters getFilters();

   ServletContextImpl getServletContext();

   HttpHandler getHandler();

   ServletPathMatches getServletPaths();

   <T, C> ThreadSetupHandler.Action<T, C> createThreadSetupAction(ThreadSetupHandler.Action<T, C> var1);

   ErrorPages getErrorPages();

   Map<String, String> getMimeExtensionMappings();

   ServletDispatcher getServletDispatcher();

   SessionManager getSessionManager();

   Executor getExecutor();

   Executor getAsyncExecutor();

   /** @deprecated */
   @Deprecated
   Charset getDefaultCharset();

   Charset getDefaultRequestCharset();

   Charset getDefaultResponseCharset();

   List<AuthenticationMechanism> getAuthenticationMechanisms();

   DeploymentManager.State getDeploymentState();

   Set<String> tryAddServletMappings(ServletInfo var1, String... var2);
}
