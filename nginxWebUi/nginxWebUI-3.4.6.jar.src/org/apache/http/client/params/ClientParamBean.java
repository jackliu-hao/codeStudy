/*     */ package org.apache.http.client.params;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.params.HttpAbstractParamBean;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class ClientParamBean
/*     */   extends HttpAbstractParamBean
/*     */ {
/*     */   public ClientParamBean(HttpParams params) {
/*  50 */     super(params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setConnectionManagerFactoryClassName(String factory) {
/*  58 */     this.params.setParameter("http.connection-manager.factory-class-name", factory);
/*     */   }
/*     */   
/*     */   public void setHandleRedirects(boolean handle) {
/*  62 */     this.params.setBooleanParameter("http.protocol.handle-redirects", handle);
/*     */   }
/*     */   
/*     */   public void setRejectRelativeRedirect(boolean reject) {
/*  66 */     this.params.setBooleanParameter("http.protocol.reject-relative-redirect", reject);
/*     */   }
/*     */   
/*     */   public void setMaxRedirects(int maxRedirects) {
/*  70 */     this.params.setIntParameter("http.protocol.max-redirects", maxRedirects);
/*     */   }
/*     */   
/*     */   public void setAllowCircularRedirects(boolean allow) {
/*  74 */     this.params.setBooleanParameter("http.protocol.allow-circular-redirects", allow);
/*     */   }
/*     */   
/*     */   public void setHandleAuthentication(boolean handle) {
/*  78 */     this.params.setBooleanParameter("http.protocol.handle-authentication", handle);
/*     */   }
/*     */   
/*     */   public void setCookiePolicy(String policy) {
/*  82 */     this.params.setParameter("http.protocol.cookie-policy", policy);
/*     */   }
/*     */   
/*     */   public void setVirtualHost(HttpHost host) {
/*  86 */     this.params.setParameter("http.virtual-host", host);
/*     */   }
/*     */   
/*     */   public void setDefaultHeaders(Collection<Header> headers) {
/*  90 */     this.params.setParameter("http.default-headers", headers);
/*     */   }
/*     */   
/*     */   public void setDefaultHost(HttpHost host) {
/*  94 */     this.params.setParameter("http.default-host", host);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConnectionManagerTimeout(long timeout) {
/* 101 */     this.params.setLongParameter("http.conn-manager.timeout", timeout);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\params\ClientParamBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */