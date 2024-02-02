/*    */ package javax.servlet.http;
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
/*    */ public class HttpSessionEvent
/*    */   extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = -7622791603672342895L;
/*    */   
/*    */   public HttpSessionEvent(HttpSession source) {
/* 36 */     super(source);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpSession getSession() {
/* 44 */     return (HttpSession)getSource();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpSessionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */