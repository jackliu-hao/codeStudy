/*    */ package org.apache.http.conn.ssl;
/*    */ 
/*    */ import org.apache.http.util.Args;
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
/*    */ final class SubjectName
/*    */ {
/*    */   static final int DNS = 2;
/*    */   static final int IP = 7;
/*    */   private final String value;
/*    */   private final int type;
/*    */   
/*    */   static SubjectName IP(String value) {
/* 40 */     return new SubjectName(value, 7);
/*    */   }
/*    */   
/*    */   static SubjectName DNS(String value) {
/* 44 */     return new SubjectName(value, 2);
/*    */   }
/*    */   
/*    */   SubjectName(String value, int type) {
/* 48 */     this.value = (String)Args.notNull(value, "Value");
/* 49 */     this.type = Args.positive(type, "Type");
/*    */   }
/*    */   
/*    */   public int getType() {
/* 53 */     return this.type;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 57 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 62 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ssl\SubjectName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */