/*    */ package ch.qos.logback.classic.selector.servlet;
/*    */ 
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.classic.selector.ContextJNDISelector;
/*    */ import ch.qos.logback.classic.selector.ContextSelector;
/*    */ import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
/*    */ import java.io.IOException;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.FilterConfig;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LoggerContextFilter
/*    */   implements Filter
/*    */ {
/*    */   public void destroy() {}
/*    */   
/*    */   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
/* 59 */     LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
/* 60 */     ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
/* 61 */     ContextJNDISelector sel = null;
/*    */     
/* 63 */     if (selector instanceof ContextJNDISelector) {
/* 64 */       sel = (ContextJNDISelector)selector;
/* 65 */       sel.setLocalContext(lc);
/*    */     } 
/*    */     
/*    */     try {
/* 69 */       chain.doFilter(request, response);
/*    */     } finally {
/* 71 */       if (sel != null)
/* 72 */         sel.removeLocalContext(); 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void init(FilterConfig arg0) throws ServletException {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\selector\servlet\LoggerContextFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */