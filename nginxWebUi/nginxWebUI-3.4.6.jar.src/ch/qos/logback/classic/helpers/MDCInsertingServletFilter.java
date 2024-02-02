/*    */ package ch.qos.logback.classic.helpers;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.FilterConfig;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import org.slf4j.MDC;
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
/*    */ public class MDCInsertingServletFilter
/*    */   implements Filter
/*    */ {
/*    */   public void destroy() {}
/*    */   
/*    */   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
/* 47 */     insertIntoMDC(request);
/*    */     try {
/* 49 */       chain.doFilter(request, response);
/*    */     } finally {
/* 51 */       clearMDC();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   void insertIntoMDC(ServletRequest request) {
/* 57 */     MDC.put("req.remoteHost", request.getRemoteHost());
/*    */     
/* 59 */     if (request instanceof HttpServletRequest) {
/* 60 */       HttpServletRequest httpServletRequest = (HttpServletRequest)request;
/* 61 */       MDC.put("req.requestURI", httpServletRequest.getRequestURI());
/* 62 */       StringBuffer requestURL = httpServletRequest.getRequestURL();
/* 63 */       if (requestURL != null) {
/* 64 */         MDC.put("req.requestURL", requestURL.toString());
/*    */       }
/* 66 */       MDC.put("req.method", httpServletRequest.getMethod());
/* 67 */       MDC.put("req.queryString", httpServletRequest.getQueryString());
/* 68 */       MDC.put("req.userAgent", httpServletRequest.getHeader("User-Agent"));
/* 69 */       MDC.put("req.xForwardedFor", httpServletRequest.getHeader("X-Forwarded-For"));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   void clearMDC() {
/* 75 */     MDC.remove("req.remoteHost");
/* 76 */     MDC.remove("req.requestURI");
/* 77 */     MDC.remove("req.queryString");
/*    */     
/* 79 */     MDC.remove("req.requestURL");
/* 80 */     MDC.remove("req.method");
/* 81 */     MDC.remove("req.userAgent");
/* 82 */     MDC.remove("req.xForwardedFor");
/*    */   }
/*    */   
/*    */   public void init(FilterConfig arg0) throws ServletException {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\helpers\MDCInsertingServletFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */