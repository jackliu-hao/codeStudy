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
/*    */ public class StructureWriteContext
/*    */   extends ToNativeContext
/*    */ {
/*    */   private Structure struct;
/*    */   private Field field;
/*    */   
/*    */   StructureWriteContext(Structure struct, Field field) {
/* 37 */     this.struct = struct;
/* 38 */     this.field = field;
/*    */   }
/*    */   public Structure getStructure() {
/* 41 */     return this.struct;
/*    */   }
/*    */   public Field getField() {
/* 44 */     return this.field;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\StructureWriteContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */