/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
/*    */ import org.h2.schema.UserAggregate;
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
/*    */ public class DropAggregate
/*    */   extends SchemaOwnerCommand
/*    */ {
/*    */   private String name;
/*    */   private boolean ifExists;
/*    */   
/*    */   public DropAggregate(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 26 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */ 
/*    */   
/*    */   long update(Schema paramSchema) {
/* 31 */     Database database = this.session.getDatabase();
/* 32 */     UserAggregate userAggregate = paramSchema.findAggregate(this.name);
/* 33 */     if (userAggregate == null) {
/* 34 */       if (!this.ifExists) {
/* 35 */         throw DbException.get(90132, this.name);
/*    */       }
/*    */     } else {
/* 38 */       database.removeSchemaObject(this.session, (SchemaObject)userAggregate);
/*    */     } 
/* 40 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setName(String paramString) {
/* 44 */     this.name = paramString;
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 48 */     this.ifExists = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 53 */     return 36;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropAggregate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */