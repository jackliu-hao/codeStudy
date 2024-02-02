/*    */ package ch.qos.logback.core.read;
/*    */ 
/*    */ import ch.qos.logback.core.AppenderBase;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class ListAppender<E>
/*    */   extends AppenderBase<E>
/*    */ {
/* 23 */   public List<E> list = new ArrayList<E>();
/*    */   
/*    */   protected void append(E e) {
/* 26 */     this.list.add(e);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\read\ListAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */