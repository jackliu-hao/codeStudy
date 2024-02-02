/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.schema.Schema;
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
/*    */ public abstract class SchemaCommand
/*    */   extends DefineCommand
/*    */ {
/*    */   private final Schema schema;
/*    */   
/*    */   public SchemaCommand(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 25 */     super(paramSessionLocal);
/* 26 */     this.schema = paramSchema;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected final Schema getSchema() {
/* 35 */     return this.schema;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\SchemaCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */