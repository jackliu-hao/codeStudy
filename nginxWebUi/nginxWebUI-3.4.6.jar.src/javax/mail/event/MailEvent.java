/*    */ package javax.mail.event;
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
/*    */ public abstract class MailEvent
/*    */   extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = 1846275636325456631L;
/*    */   
/*    */   public MailEvent(Object source) {
/* 55 */     super(source);
/*    */   }
/*    */   
/*    */   public abstract void dispatch(Object paramObject);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\MailEvent.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */