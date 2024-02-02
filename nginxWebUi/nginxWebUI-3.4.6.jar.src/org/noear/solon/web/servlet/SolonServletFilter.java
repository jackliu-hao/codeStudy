/*    */ package org.noear.solon.web.servlet;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.FilterConfig;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.core.event.EventBus;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.ContextUtil;
/*    */ import org.noear.solon.core.handle.Handler;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SolonServletFilter
/*    */   implements Filter
/*    */ {
/*    */   public static Handler onFilterStart;
/*    */   public static Handler onFilterError;
/*    */   public static Handler onFilterEnd;
/*    */   
/*    */   public void init(FilterConfig filterConfig) throws ServletException {}
/*    */   
/*    */   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
/* 30 */     if (request instanceof HttpServletRequest) {
/*    */ 
/*    */ 
/*    */       
/* 34 */       Context ctx = new SolonServletContext((HttpServletRequest)request, (HttpServletResponse)response);
/*    */       
/*    */       try {
/* 37 */         ContextUtil.currentSet(ctx);
/*    */ 
/*    */         
/* 40 */         doFilterStart(ctx);
/*    */ 
/*    */         
/* 43 */         Solon.app().tryHandle(ctx);
/*    */ 
/*    */         
/* 46 */         ContextUtil.currentSet(ctx);
/*    */         
/* 48 */         if (!ctx.getHandled())
/*    */         {
/* 50 */           filterChain.doFilter(request, response);
/*    */         }
/* 52 */       } catch (Throwable err) {
/* 53 */         ctx.errors = err;
/* 54 */         doFilterError(ctx);
/*    */         
/* 56 */         throw err;
/*    */       } finally {
/*    */         
/* 59 */         doFilterEnd(ctx);
/* 60 */         ContextUtil.currentRemove();
/*    */       } 
/*    */     } else {
/*    */       
/* 64 */       filterChain.doFilter(request, response);
/*    */     } 
/*    */   }
/*    */   
/*    */   protected void doFilterStart(Context ctx) {
/* 69 */     doHandler(onFilterStart, ctx);
/*    */   }
/*    */   
/*    */   protected void doFilterError(Context ctx) {
/* 73 */     doHandler(onFilterError, ctx);
/*    */   }
/*    */   
/*    */   protected void doFilterEnd(Context ctx) {
/* 77 */     doHandler(onFilterEnd, ctx);
/*    */   }
/*    */   
/*    */   protected void doHandler(Handler h, Context ctx) {
/* 81 */     if (h != null)
/*    */       try {
/* 83 */         h.handle(ctx);
/* 84 */       } catch (Throwable ex) {
/* 85 */         EventBus.push(ex);
/*    */       }  
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\servlet\SolonServletFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */