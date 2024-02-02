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
/*    */ public class ServletContextEvent
/*    */   extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = -7501701636134222423L;
/*    */   
/*    */   public ServletContextEvent(ServletContext source) {
/* 37 */     super(source);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ServletContext getServletContext() {
/* 46 */     return (ServletContext)getSource();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletContextEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */