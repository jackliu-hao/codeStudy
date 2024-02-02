/*    */ package cn.hutool.core.thread.threadlocal;
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
/*    */ public class NamedThreadLocal<T>
/*    */   extends ThreadLocal<T>
/*    */ {
/*    */   private final String name;
/*    */   
/*    */   public NamedThreadLocal(String name) {
/* 20 */     this.name = name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 25 */     return this.name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\threadlocal\NamedThreadLocal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */