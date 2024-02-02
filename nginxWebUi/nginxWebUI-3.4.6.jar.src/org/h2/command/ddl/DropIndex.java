/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.constraint.Constraint;
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.index.Index;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
/*    */ import org.h2.table.Table;
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
/*    */ public class DropIndex
/*    */   extends SchemaCommand
/*    */ {
/*    */   private String indexName;
/*    */   private boolean ifExists;
/*    */   
/*    */   public DropIndex(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 31 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 35 */     this.ifExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setIndexName(String paramString) {
/* 39 */     this.indexName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 44 */     Database database = this.session.getDatabase();
/* 45 */     Index index = getSchema().findIndex(this.session, this.indexName);
/* 46 */     if (index == null) {
/* 47 */       if (!this.ifExists) {
/* 48 */         throw DbException.get(42112, this.indexName);
/*    */       }
/*    */     } else {
/* 51 */       Table table = index.getTable();
/* 52 */       this.session.getUser().checkTableRight(index.getTable(), 32);
/* 53 */       Constraint constraint = null;
/* 54 */       ArrayList<Constraint> arrayList = table.getConstraints();
/* 55 */       for (byte b = 0; arrayList != null && b < arrayList.size(); b++) {
/* 56 */         Constraint constraint1 = arrayList.get(b);
/* 57 */         if (constraint1.usesIndex(index))
/*    */         {
/* 59 */           if (Constraint.Type.PRIMARY_KEY == constraint1.getConstraintType()) {
/* 60 */             for (Constraint constraint2 : arrayList) {
/* 61 */               if (constraint2.getReferencedConstraint() == constraint1) {
/* 62 */                 throw DbException.get(90085, new String[] { this.indexName, constraint1
/* 63 */                       .getName() });
/*    */               }
/*    */             } 
/* 66 */             constraint = constraint1;
/*    */           } else {
/* 68 */             throw DbException.get(90085, new String[] { this.indexName, constraint1.getName() });
/*    */           } 
/*    */         }
/*    */       } 
/* 72 */       index.getTable().setModified();
/* 73 */       if (constraint != null) {
/* 74 */         database.removeSchemaObject(this.session, (SchemaObject)constraint);
/*    */       } else {
/* 76 */         database.removeSchemaObject(this.session, (SchemaObject)index);
/*    */       } 
/*    */     } 
/* 79 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 84 */     return 40;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */