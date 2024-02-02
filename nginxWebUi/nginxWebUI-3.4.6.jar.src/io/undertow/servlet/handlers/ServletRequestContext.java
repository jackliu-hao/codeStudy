/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.api.ServletStackTraces;
/*     */ import io.undertow.servlet.api.SingleConstraintMatch;
/*     */ import io.undertow.servlet.api.TransportGuaranteeType;
/*     */ import io.undertow.servlet.spec.HttpServletRequestImpl;
/*     */ import io.undertow.servlet.spec.HttpServletResponseImpl;
/*     */ import io.undertow.servlet.spec.HttpSessionImpl;
/*     */ import io.undertow.servlet.spec.ServletContextImpl;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.Headers;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.List;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
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
/*     */ 
/*     */ public class ServletRequestContext
/*     */ {
/*  56 */   private static final RuntimePermission GET_CURRENT_REQUEST = new RuntimePermission("io.undertow.servlet.GET_CURRENT_REQUEST");
/*  57 */   private static final RuntimePermission SET_CURRENT_REQUEST = new RuntimePermission("io.undertow.servlet.SET_CURRENT_REQUEST");
/*     */   
/*  59 */   private static final ThreadLocal<ServletRequestContext> CURRENT = new ThreadLocal<>();
/*     */   
/*     */   public static void setCurrentRequestContext(ServletRequestContext servletRequestContext) {
/*  62 */     SecurityManager sm = System.getSecurityManager();
/*  63 */     if (sm != null) {
/*  64 */       sm.checkPermission(SET_CURRENT_REQUEST);
/*     */     }
/*  66 */     CURRENT.set(servletRequestContext);
/*     */   }
/*     */   
/*     */   public static void clearCurrentServletAttachments() {
/*  70 */     SecurityManager sm = System.getSecurityManager();
/*  71 */     if (sm != null) {
/*  72 */       sm.checkPermission(SET_CURRENT_REQUEST);
/*     */     }
/*  74 */     CURRENT.remove();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ServletRequestContext requireCurrent() {
/*  85 */     ServletRequestContext attachments = current();
/*  86 */     if (attachments == null) {
/*  87 */       throw UndertowMessages.MESSAGES.noRequestActive();
/*     */     }
/*  89 */     return attachments;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ServletRequestContext current() {
/*  98 */     SecurityManager sm = System.getSecurityManager();
/*  99 */     if (sm != null) {
/* 100 */       sm.checkPermission(GET_CURRENT_REQUEST);
/*     */     }
/* 102 */     return CURRENT.get();
/*     */   }
/*     */   
/* 105 */   public static final AttachmentKey<ServletRequestContext> ATTACHMENT_KEY = AttachmentKey.create(ServletRequestContext.class);
/*     */   
/*     */   private final Deployment deployment;
/*     */   
/*     */   private final HttpServletRequestImpl originalRequest;
/*     */   
/*     */   private final HttpServletResponseImpl originalResponse;
/*     */   
/*     */   private final ServletPathMatch originalServletPathMatch;
/*     */   
/*     */   private ServletResponse servletResponse;
/*     */   
/*     */   private ServletRequest servletRequest;
/*     */   
/*     */   private DispatcherType dispatcherType;
/*     */   
/*     */   private ServletChain currentServlet;
/*     */   private ServletPathMatch servletPathMatch;
/*     */   private List<SingleConstraintMatch> requiredConstrains;
/*     */   private TransportGuaranteeType transportGuarenteeType;
/*     */   private HttpSessionImpl session;
/*     */   private ServletContextImpl currentServletContext;
/*     */   private String overridenSessionId;
/*     */   private boolean runningInsideHandler = false;
/* 129 */   private int errorCode = -1;
/*     */   private String errorMessage;
/*     */   private boolean asyncSupported = true;
/*     */   
/*     */   public ServletRequestContext(Deployment deployment, HttpServletRequestImpl originalRequest, HttpServletResponseImpl originalResponse, ServletPathMatch originalServletPathMatch) {
/* 134 */     this.deployment = deployment;
/* 135 */     this.originalRequest = originalRequest;
/* 136 */     this.originalResponse = originalResponse;
/* 137 */     this.servletRequest = (ServletRequest)originalRequest;
/* 138 */     this.servletResponse = (ServletResponse)originalResponse;
/* 139 */     this.originalServletPathMatch = originalServletPathMatch;
/* 140 */     this.currentServletContext = deployment.getServletContext();
/*     */   }
/*     */   
/*     */   public Deployment getDeployment() {
/* 144 */     return this.deployment;
/*     */   }
/*     */   
/*     */   public ServletChain getCurrentServlet() {
/* 148 */     return this.currentServlet;
/*     */   }
/*     */   
/*     */   public void setCurrentServlet(ServletChain currentServlet) {
/* 152 */     this.currentServlet = currentServlet;
/*     */   }
/*     */   
/*     */   public ServletPathMatch getServletPathMatch() {
/* 156 */     return this.servletPathMatch;
/*     */   }
/*     */   
/*     */   public void setServletPathMatch(ServletPathMatch servletPathMatch) {
/* 160 */     this.servletPathMatch = servletPathMatch;
/*     */   }
/*     */   
/*     */   public List<SingleConstraintMatch> getRequiredConstrains() {
/* 164 */     return this.requiredConstrains;
/*     */   }
/*     */   
/*     */   public void setRequiredConstrains(List<SingleConstraintMatch> requiredConstrains) {
/* 168 */     this.requiredConstrains = requiredConstrains;
/*     */   }
/*     */   
/*     */   public TransportGuaranteeType getTransportGuarenteeType() {
/* 172 */     return this.transportGuarenteeType;
/*     */   }
/*     */   
/*     */   public void setTransportGuarenteeType(TransportGuaranteeType transportGuarenteeType) {
/* 176 */     this.transportGuarenteeType = transportGuarenteeType;
/*     */   }
/*     */   
/*     */   public ServletResponse getServletResponse() {
/* 180 */     return this.servletResponse;
/*     */   }
/*     */   
/*     */   public void setServletResponse(ServletResponse servletResponse) {
/* 184 */     this.servletResponse = servletResponse;
/*     */   }
/*     */   
/*     */   public ServletRequest getServletRequest() {
/* 188 */     return this.servletRequest;
/*     */   }
/*     */   
/*     */   public void setServletRequest(ServletRequest servletRequest) {
/* 192 */     this.servletRequest = servletRequest;
/*     */   }
/*     */   
/*     */   public DispatcherType getDispatcherType() {
/* 196 */     return this.dispatcherType;
/*     */   }
/*     */   
/*     */   public void setDispatcherType(DispatcherType dispatcherType) {
/* 200 */     this.dispatcherType = dispatcherType;
/*     */   }
/*     */   
/*     */   public HttpServletRequestImpl getOriginalRequest() {
/* 204 */     return this.originalRequest;
/*     */   }
/*     */   
/*     */   public HttpServletResponseImpl getOriginalResponse() {
/* 208 */     return this.originalResponse;
/*     */   }
/*     */   
/*     */   public HttpSessionImpl getSession() {
/* 212 */     return this.session;
/*     */   }
/*     */   
/*     */   public void setSession(HttpSessionImpl session) {
/* 216 */     this.session = session;
/*     */   }
/*     */   
/*     */   public HttpServerExchange getExchange() {
/* 220 */     return this.originalRequest.getExchange();
/*     */   }
/*     */   
/*     */   public ServletPathMatch getOriginalServletPathMatch() {
/* 224 */     return this.originalServletPathMatch;
/*     */   }
/*     */   
/*     */   public ServletContextImpl getCurrentServletContext() {
/* 228 */     return this.currentServletContext;
/*     */   }
/*     */   
/*     */   public void setCurrentServletContext(ServletContextImpl currentServletContext) {
/* 232 */     this.currentServletContext = currentServletContext;
/*     */   }
/*     */   
/*     */   public boolean displayStackTraces() {
/* 236 */     ServletStackTraces mode = this.deployment.getDeploymentInfo().getServletStackTraces();
/* 237 */     if (mode == ServletStackTraces.NONE)
/* 238 */       return false; 
/* 239 */     if (mode == ServletStackTraces.ALL) {
/* 240 */       return true;
/*     */     }
/* 242 */     InetSocketAddress localAddress = getExchange().getSourceAddress();
/* 243 */     if (localAddress == null) {
/* 244 */       return false;
/*     */     }
/* 246 */     InetAddress address = localAddress.getAddress();
/* 247 */     if (address == null) {
/* 248 */       return false;
/*     */     }
/* 250 */     if (!address.isLoopbackAddress()) {
/* 251 */       return false;
/*     */     }
/* 253 */     return !getExchange().getRequestHeaders().contains(Headers.X_FORWARDED_FOR);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setError(int sc, String msg) {
/* 259 */     this.errorCode = sc;
/* 260 */     this.errorMessage = msg;
/*     */   }
/*     */   
/*     */   public int getErrorCode() {
/* 264 */     return this.errorCode;
/*     */   }
/*     */   
/*     */   public String getErrorMessage() {
/* 268 */     return this.errorMessage;
/*     */   }
/*     */   
/*     */   public boolean isRunningInsideHandler() {
/* 272 */     return this.runningInsideHandler;
/*     */   }
/*     */   
/*     */   public void setRunningInsideHandler(boolean runningInsideHandler) {
/* 276 */     this.runningInsideHandler = runningInsideHandler;
/*     */   }
/*     */   
/*     */   public boolean isAsyncSupported() {
/* 280 */     return this.asyncSupported;
/*     */   }
/*     */   
/*     */   public String getOverridenSessionId() {
/* 284 */     return this.overridenSessionId;
/*     */   }
/*     */   
/*     */   public void setOverridenSessionId(String overridenSessionId) {
/* 288 */     this.overridenSessionId = overridenSessionId;
/*     */   }
/*     */   
/*     */   public void setAsyncSupported(boolean asyncSupported) {
/* 292 */     this.asyncSupported = asyncSupported;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\ServletRequestContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */