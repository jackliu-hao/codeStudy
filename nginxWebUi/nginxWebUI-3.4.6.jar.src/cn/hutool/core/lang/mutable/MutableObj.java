/*    */ package cn.hutool.core.lang.mutable;
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
/*    */ public class MutableObj<T>
/*    */   implements Mutable<T>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private T value;
/*    */   
/*    */   public static <T> MutableObj<T> of(T value) {
/* 22 */     return new MutableObj<>(value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MutableObj() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MutableObj(T value) {
/* 39 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public T get() {
/* 45 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public void set(T value) {
/* 50 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 56 */     if (obj == null) {
/* 57 */       return false;
/*    */     }
/* 59 */     if (this == obj) {
/* 60 */       return true;
/*    */     }
/* 62 */     if (getClass() == obj.getClass()) {
/* 63 */       MutableObj<?> that = (MutableObj)obj;
/* 64 */       return this.value.equals(that.value);
/*    */     } 
/* 66 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 71 */     return (this.value == null) ? 0 : this.value.hashCode();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 77 */     return (this.value == null) ? "null" : this.value.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\mutable\MutableObj.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */