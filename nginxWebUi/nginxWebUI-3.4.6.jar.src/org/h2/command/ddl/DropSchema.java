/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.constraint.ConstraintActionType;
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
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
/*    */ public class DropSchema
/*    */   extends DefineCommand
/*    */ {
/*    */   private String schemaName;
/*    */   private boolean ifExists;
/*    */   private ConstraintActionType dropAction;
/*    */   
/*    */   public DropSchema(SessionLocal paramSessionLocal) {
/* 29 */     super(paramSessionLocal);
/* 30 */     this.dropAction = (paramSessionLocal.getDatabase().getSettings()).dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setSchemaName(String paramString) {
/* 35 */     this.schemaName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 40 */     Database database = this.session.getDatabase();
/* 41 */     Schema schema = database.findSchema(this.schemaName);
/* 42 */     if (schema == null) {
/* 43 */       if (!this.ifExists) {
/* 44 */         throw DbException.get(90079, this.schemaName);
/*    */       }
/*    */     } else {
/* 47 */       this.session.getUser().checkSchemaOwner(schema);
/* 48 */       if (!schema.canDrop()) {
/* 49 */         throw DbException.get(90090, this.schemaName);
/*    */       }
/* 51 */       if (this.dropAction == ConstraintActionType.RESTRICT && !schema.isEmpty()) {
/* 52 */         ArrayList<SchemaObject> arrayList = schema.getAll(null);
/* 53 */         int i = arrayList.size();
/* 54 */         if (i > 0) {
/* 55 */           StringBuilder stringBuilder = new StringBuilder();
/* 56 */           for (byte b = 0; b < i; b++) {
/* 57 */             if (b > 0) {
/* 58 */               stringBuilder.append(", ");
/*    */             }
/* 60 */             stringBuilder.append(((SchemaObject)arrayList.get(b)).getName());
/*    */           } 
/* 62 */           throw DbException.get(90107, new String[] { this.schemaName, stringBuilder.toString() });
/*    */         } 
/*    */       } 
/* 65 */       database.removeDatabaseObject(this.session, (DbObject)schema);
/*    */     } 
/* 67 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 71 */     this.ifExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setDropAction(ConstraintActionType paramConstraintActionType) {
/* 75 */     this.dropAction = paramConstraintActionType;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 80 */     return 42;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */