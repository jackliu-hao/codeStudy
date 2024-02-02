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
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class SchemaOwnerCommand
/*    */   extends SchemaCommand
/*    */ {
/*    */   SchemaOwnerCommand(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 26 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */ 
/*    */   
/*    */   public final long update() {
/* 31 */     Schema schema = getSchema();
/* 32 */     this.session.getUser().checkSchemaOwner(schema);
/* 33 */     return update(schema);
/*    */   }
/*    */   
/*    */   abstract long update(Schema paramSchema);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\SchemaOwnerCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */