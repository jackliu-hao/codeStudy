/*    */ package org.h2.schema;
/*    */ 
/*    */ import org.h2.engine.DbObject;
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
/*    */ public abstract class SchemaObject
/*    */   extends DbObject
/*    */ {
/*    */   private final Schema schema;
/*    */   
/*    */   protected SchemaObject(Schema paramSchema, int paramInt1, String paramString, int paramInt2) {
/* 26 */     super(paramSchema.getDatabase(), paramInt1, paramString, paramInt2);
/* 27 */     this.schema = paramSchema;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final Schema getSchema() {
/* 36 */     return this.schema;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSQL(int paramInt) {
/* 41 */     return getSQL(new StringBuilder(), paramInt).toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 46 */     this.schema.getSQL(paramStringBuilder, paramInt).append('.');
/* 47 */     return super.getSQL(paramStringBuilder, paramInt);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isHidden() {
/* 57 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\schema\SchemaObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */