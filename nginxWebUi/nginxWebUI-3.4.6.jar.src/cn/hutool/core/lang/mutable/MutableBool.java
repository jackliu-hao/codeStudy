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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MutableBool
/*    */   implements Comparable<MutableBool>, Mutable<Boolean>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private boolean value;
/*    */   
/*    */   public MutableBool() {}
/*    */   
/*    */   public MutableBool(boolean value) {
/* 27 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MutableBool(String value) throws NumberFormatException {
/* 36 */     this.value = Boolean.parseBoolean(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public Boolean get() {
/* 41 */     return Boolean.valueOf(this.value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void set(boolean value) {
/* 49 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public void set(Boolean value) {
/* 54 */     this.value = value.booleanValue();
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
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 71 */     if (obj instanceof MutableBool) {
/* 72 */       return (this.value == ((MutableBool)obj).value);
/*    */     }
/* 74 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 79 */     return this.value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
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
/*    */   public int compareTo(MutableBool other) {
/* 91 */     return Boolean.compare(this.value, other.value);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 97 */     return String.valueOf(this.value);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\mutable\MutableBool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */