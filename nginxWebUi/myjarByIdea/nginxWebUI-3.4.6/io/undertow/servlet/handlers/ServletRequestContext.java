package io.undertow.servlet.handlers;

import io.undertow.UndertowMessages;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.ServletStackTraces;
import io.undertow.servlet.api.SingleConstraintMatch;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import io.undertow.servlet.spec.HttpServletResponseImpl;
import io.undertow.servlet.spec.HttpSessionImpl;
import io.undertow.servlet.spec.ServletContextImpl;
import io.undertow.util.AttachmentKey;
import io.undertow.util.Headers;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServletRequestContext {
   private static final RuntimePermission GET_CURRENT_REQUEST = new RuntimePermission("io.undertow.servlet.GET_CURRENT_REQUEST");
   private static final RuntimePermission SET_CURRENT_REQUEST = new RuntimePermission("io.undertow.servlet.SET_CURRENT_REQUEST");
   private static final ThreadLocal<ServletRequestContext> CURRENT = new ThreadLocal();
   public static final AttachmentKey<ServletRequestContext> ATTACHMENT_KEY = AttachmentKey.create(ServletRequestContext.class);
   private final Deployment deployment;
   private final HttpServletRequestImpl originalRequest;
   private final HttpServletResponseImpl originalResponse;
   private final ServletPathMatch originalServletPathMatch;
   private ServletResponse servletResponse;
   private ServletRequest servletRequest;
   private DispatcherType dispatcherType;
   private ServletChain currentServlet;
   private ServletPathMatch servletPathMatch;
   private List<SingleConstraintMatch> requiredConstrains;
   private TransportGuaranteeType transportGuarenteeType;
   private HttpSessionImpl session;
   private ServletContextImpl currentServletContext;
   private String overridenSessionId;
   private boolean runningInsideHandler = false;
   private int errorCode = -1;
   private String errorMessage;
   private boolean asyncSupported = true;

   public static void setCurrentRequestContext(ServletRequestContext servletRequestContext) {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(SET_CURRENT_REQUEST);
      }

      CURRENT.set(servletRequestContext);
   }

   public static void clearCurrentServletAttachments() {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(SET_CURRENT_REQUEST);
      }

      CURRENT.remove();
   }

   public static ServletRequestContext requireCurrent() {
      ServletRequestContext attachments = current();
      if (attachments == null) {
         throw UndertowMessages.MESSAGES.noRequestActive();
      } else {
         return attachments;
      }
   }

   public static ServletRequestContext current() {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(GET_CURRENT_REQUEST);
      }

      return (ServletRequestContext)CURRENT.get();
   }

   public ServletRequestContext(Deployment deployment, HttpServletRequestImpl originalRequest, HttpServletResponseImpl originalResponse, ServletPathMatch originalServletPathMatch) {
      this.deployment = deployment;
      this.originalRequest = originalRequest;
      this.originalResponse = originalResponse;
      this.servletRequest = originalRequest;
      this.servletResponse = originalResponse;
      this.originalServletPathMatch = originalServletPathMatch;
      this.currentServletContext = deployment.getServletContext();
   }

   public Deployment getDeployment() {
      return this.deployment;
   }

   public ServletChain getCurrentServlet() {
      return this.currentServlet;
   }

   public void setCurrentServlet(ServletChain currentServlet) {
      this.currentServlet = currentServlet;
   }

   public ServletPathMatch getServletPathMatch() {
      return this.servletPathMatch;
   }

   public void setServletPathMatch(ServletPathMatch servletPathMatch) {
      this.servletPathMatch = servletPathMatch;
   }

   public List<SingleConstraintMatch> getRequiredConstrains() {
      return this.requiredConstrains;
   }

   public void setRequiredConstrains(List<SingleConstraintMatch> requiredConstrains) {
      this.requiredConstrains = requiredConstrains;
   }

   public TransportGuaranteeType getTransportGuarenteeType() {
      return this.transportGuarenteeType;
   }

   public void setTransportGuarenteeType(TransportGuaranteeType transportGuarenteeType) {
      this.transportGuarenteeType = transportGuarenteeType;
   }

   public ServletResponse getServletResponse() {
      return this.servletResponse;
   }

   public void setServletResponse(ServletResponse servletResponse) {
      this.servletResponse = servletResponse;
   }

   public ServletRequest getServletRequest() {
      return this.servletRequest;
   }

   public void setServletRequest(ServletRequest servletRequest) {
      this.servletRequest = servletRequest;
   }

   public DispatcherType getDispatcherType() {
      return this.dispatcherType;
   }

   public void setDispatcherType(DispatcherType dispatcherType) {
      this.dispatcherType = dispatcherType;
   }

   public HttpServletRequestImpl getOriginalRequest() {
      return this.originalRequest;
   }

   public HttpServletResponseImpl getOriginalResponse() {
      return this.originalResponse;
   }

   public HttpSessionImpl getSession() {
      return this.session;
   }

   public void setSession(HttpSessionImpl session) {
      this.session = session;
   }

   public HttpServerExchange getExchange() {
      return this.originalRequest.getExchange();
   }

   public ServletPathMatch getOriginalServletPathMatch() {
      return this.originalServletPathMatch;
   }

   public ServletContextImpl getCurrentServletContext() {
      return this.currentServletContext;
   }

   public void setCurrentServletContext(ServletContextImpl currentServletContext) {
      this.currentServletContext = currentServletContext;
   }

   public boolean displayStackTraces() {
      ServletStackTraces mode = this.deployment.getDeploymentInfo().getServletStackTraces();
      if (mode == ServletStackTraces.NONE) {
         return false;
      } else if (mode == ServletStackTraces.ALL) {
         return true;
      } else {
         InetSocketAddress localAddress = this.getExchange().getSourceAddress();
         if (localAddress == null) {
            return false;
         } else {
            InetAddress address = localAddress.getAddress();
            if (address == null) {
               return false;
            } else if (!address.isLoopbackAddress()) {
               return false;
            } else {
               return !this.getExchange().getRequestHeaders().contains(Headers.X_FORWARDED_FOR);
            }
         }
      }
   }

   public void setError(int sc, String msg) {
      this.errorCode = sc;
      this.errorMessage = msg;
   }

   public int getErrorCode() {
      return this.errorCode;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public boolean isRunningInsideHandler() {
      return this.runningInsideHandler;
   }

   public void setRunningInsideHandler(boolean runningInsideHandler) {
      this.runningInsideHandler = runningInsideHandler;
   }

   public boolean isAsyncSupported() {
      return this.asyncSupported;
   }

   public String getOverridenSessionId() {
      return this.overridenSessionId;
   }

   public void setOverridenSessionId(String overridenSessionId) {
      this.overridenSessionId = overridenSessionId;
   }

   public void setAsyncSupported(boolean asyncSupported) {
      this.asyncSupported = asyncSupported;
   }
}
