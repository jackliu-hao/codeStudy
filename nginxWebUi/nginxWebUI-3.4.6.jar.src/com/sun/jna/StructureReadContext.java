/*    */ package com.sun.jna;
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
/*    */ public class StructureReadContext
/*    */   extends FromNativeContext
/*    */ {
/*    */   private Structure structure;
/*    */   private Field field;
/*    */   
/*    */   StructureReadContext(Structure struct, Field field) {
/* 37 */     super(field.getType());
/* 38 */     this.structure = struct;
/* 39 */     this.field = field;
/*    */   }
/*    */   public Structure getStructure() {
/* 42 */     return this.structure;
/*    */   } public Field getField() {
/* 44 */     return this.field;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\StructureReadContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */