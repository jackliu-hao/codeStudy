/*    */ package cn.hutool.core.builder;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ final class IDKey
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Object value;
/*    */   private final int id;
/*    */   
/*    */   public IDKey(Object obj) {
/* 26 */     this.id = System.identityHashCode(obj);
/*    */ 
/*    */ 
/*    */     
/* 30 */     this.value = obj;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 40 */     return this.id;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 51 */     if (!(other instanceof IDKey)) {
/* 52 */       return false;
/*    */     }
/* 54 */     IDKey idKey = (IDKey)other;
/* 55 */     if (this.id != idKey.id) {
/* 56 */       return false;
/*    */     }
/*    */     
/* 59 */     return (this.value == idKey.value);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\builder\IDKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */