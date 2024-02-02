/*    */ package io.undertow.util;
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
/*    */ public class ETag
/*    */ {
/*    */   private final boolean weak;
/*    */   private final String tag;
/*    */   
/*    */   public ETag(boolean weak, String tag) {
/* 30 */     this.weak = weak;
/* 31 */     this.tag = tag;
/*    */   }
/*    */   
/*    */   public boolean isWeak() {
/* 35 */     return this.weak;
/*    */   }
/*    */   
/*    */   public String getTag() {
/* 39 */     return this.tag;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 44 */     if (this.weak) {
/* 45 */       return "W/\"" + this.tag + "\"";
/*    */     }
/* 47 */     return "\"" + this.tag + "\"";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 53 */     if (this == o) return true; 
/* 54 */     if (o == null || getClass() != o.getClass()) return false;
/*    */     
/* 56 */     ETag eTag = (ETag)o;
/*    */     
/* 58 */     if (this.weak != eTag.weak) return false; 
/* 59 */     if ((this.tag != null) ? !this.tag.equals(eTag.tag) : (eTag.tag != null)) return false;
/*    */     
/* 61 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 66 */     int result = this.weak ? 1 : 0;
/* 67 */     result = 31 * result + ((this.tag != null) ? this.tag.hashCode() : 0);
/* 68 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ETag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */