/*    */ package cn.hutool.core.lang.ansi;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
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
/*    */ public final class Ansi8BitColor
/*    */   implements AnsiElement
/*    */ {
/*    */   private static final String PREFIX_FORE = "38;5;";
/*    */   private static final String PREFIX_BACK = "48;5;";
/*    */   private final String prefix;
/*    */   private final int code;
/*    */   
/*    */   public static Ansi8BitColor foreground(int code) {
/* 33 */     return new Ansi8BitColor("38;5;", code);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Ansi8BitColor background(int code) {
/* 43 */     return new Ansi8BitColor("48;5;", code);
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
/*    */   private Ansi8BitColor(String prefix, int code) {
/* 57 */     Assert.isTrue((code >= 0 && code <= 255), "Code must be between 0 and 255", new Object[0]);
/* 58 */     this.prefix = prefix;
/* 59 */     this.code = code;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 64 */     if (this == obj) {
/* 65 */       return true;
/*    */     }
/* 67 */     if (obj == null || getClass() != obj.getClass()) {
/* 68 */       return false;
/*    */     }
/* 70 */     Ansi8BitColor other = (Ansi8BitColor)obj;
/* 71 */     return (this.prefix.equals(other.prefix) && this.code == other.code);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 76 */     return this.prefix.hashCode() * 31 + this.code;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 81 */     return this.prefix + this.code;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\ansi\Ansi8BitColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */