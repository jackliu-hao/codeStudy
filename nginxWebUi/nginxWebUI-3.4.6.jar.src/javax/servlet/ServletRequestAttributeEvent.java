/*    */ package javax.servlet;
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
/*    */ public class ServletRequestAttributeEvent
/*    */   extends ServletRequestEvent
/*    */ {
/*    */   private static final long serialVersionUID = -1466635426192317793L;
/*    */   private String name;
/*    */   private Object value;
/*    */   
/*    */   public ServletRequestAttributeEvent(ServletContext sc, ServletRequest request, String name, Object value) {
/* 45 */     super(sc, request);
/* 46 */     this.name = name;
/* 47 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 56 */     return this.name;
/*    */   }
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
/*    */   public Object getValue() {
/* 69 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletRequestAttributeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */