/*    */ package com.google.protobuf;
/*    */ 
/*    */ import java.lang.reflect.Field;
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
/*    */ final class OneofInfo
/*    */ {
/*    */   private final int id;
/*    */   private final Field caseField;
/*    */   private final Field valueField;
/*    */   
/*    */   public OneofInfo(int id, Field caseField, Field valueField) {
/* 44 */     this.id = id;
/* 45 */     this.caseField = caseField;
/* 46 */     this.valueField = valueField;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getId() {
/* 54 */     return this.id;
/*    */   }
/*    */ 
/*    */   
/*    */   public Field getCaseField() {
/* 59 */     return this.caseField;
/*    */   }
/*    */ 
/*    */   
/*    */   public Field getValueField() {
/* 64 */     return this.valueField;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\OneofInfo.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */