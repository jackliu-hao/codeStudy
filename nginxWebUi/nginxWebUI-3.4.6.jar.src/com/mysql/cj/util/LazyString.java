/*    */ package com.mysql.cj.util;
/*    */ 
/*    */ import java.util.function.Supplier;
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
/*    */ public class LazyString
/*    */   implements Supplier<String>
/*    */ {
/*    */   private String string;
/*    */   private byte[] buffer;
/*    */   private int offset;
/*    */   private int length;
/*    */   private String encoding;
/*    */   
/*    */   public LazyString(String string) {
/* 47 */     this.string = string;
/*    */   }
/*    */   
/*    */   public LazyString(byte[] buffer, int offset, int length, String encoding) {
/* 51 */     this.buffer = buffer;
/* 52 */     this.offset = offset;
/* 53 */     this.length = length;
/* 54 */     this.encoding = encoding;
/*    */   }
/*    */   
/*    */   public LazyString(byte[] buffer, int offset, int length) {
/* 58 */     this.buffer = buffer;
/* 59 */     this.offset = offset;
/* 60 */     this.length = length;
/*    */   }
/*    */   
/*    */   private String createAndCacheString() {
/* 64 */     if (this.length > 0) {
/* 65 */       this
/* 66 */         .string = (this.encoding == null) ? StringUtils.toString(this.buffer, this.offset, this.length) : StringUtils.toString(this.buffer, this.offset, this.length, this.encoding);
/*    */     }
/*    */     
/* 69 */     return this.string;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 74 */     if (this.string != null) {
/* 75 */       return this.string;
/*    */     }
/* 77 */     return createAndCacheString();
/*    */   }
/*    */   
/*    */   public int length() {
/* 81 */     if (this.string != null) {
/* 82 */       return this.string.length();
/*    */     }
/* 84 */     return this.length;
/*    */   }
/*    */ 
/*    */   
/*    */   public String get() {
/* 89 */     return toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\LazyString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */