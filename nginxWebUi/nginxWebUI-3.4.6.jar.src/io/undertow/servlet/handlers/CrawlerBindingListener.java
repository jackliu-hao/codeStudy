/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import javax.servlet.http.HttpSessionBindingEvent;
/*     */ import javax.servlet.http.HttpSessionBindingListener;
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
/*     */ class CrawlerBindingListener
/*     */   implements HttpSessionBindingListener, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -8841692120840734349L;
/*     */   private transient Map<String, String> clientIpSessionId;
/*     */   private transient Map<String, String> sessionIdClientIp;
/*     */   
/*     */   CrawlerBindingListener(Map<String, String> clientIpSessionId, Map<String, String> sessionIdClientIp) {
/* 163 */     this.clientIpSessionId = clientIpSessionId;
/* 164 */     this.sessionIdClientIp = sessionIdClientIp;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void valueBound(HttpSessionBindingEvent event) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void valueUnbound(HttpSessionBindingEvent event) {
/* 174 */     if (this.sessionIdClientIp != null) {
/* 175 */       String clientIp = this.sessionIdClientIp.remove(event.getSession().getId());
/* 176 */       if (clientIp != null)
/* 177 */         this.clientIpSessionId.remove(clientIp); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\CrawlerBindingListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */