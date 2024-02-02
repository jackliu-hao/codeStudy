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
/*    */ public class NamedInheritableThreadLocal<T>
/*    */   extends InheritableThreadLocal<T>
/*    */ {
/*    */   private final String name;
/*    */   
/*    */   public NamedInheritableThreadLocal(String name) {
/* 20 */     this.name = name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 25 */     return this.name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\threadlocal\NamedInheritableThreadLocal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */