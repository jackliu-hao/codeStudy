/*    */ package org.noear.solon.core.handle;
/*    */ 
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
/*    */ public class HandlerAide
/*    */ {
/* 16 */   protected List<Handler> befores = new ArrayList<>();
/*    */   
/* 18 */   protected List<Handler> afters = new ArrayList<>();
/*    */ 
/*    */   
/*    */   public void before(Handler handler) {
/* 22 */     this.befores.add(handler);
/*    */   }
/*    */ 
/*    */   
/*    */   public void after(Handler handler) {
/* 27 */     this.afters.add(handler);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\HandlerAide.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */