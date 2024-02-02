/*    */ package javax.servlet;
/*    */ 
/*    */ import java.util.EventObject;
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
/*    */ public class ServletRequestEvent
/*    */   extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = -7467864054698729101L;
/*    */   private final transient ServletRequest request;
/*    */   
/*    */   public ServletRequestEvent(ServletContext sc, ServletRequest request) {
/* 40 */     super(sc);
/* 41 */     this.request = request;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ServletRequest getServletRequest() {
/* 50 */     return this.request;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ServletContext getServletContext() {
/* 59 */     return (ServletContext)getSource();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */