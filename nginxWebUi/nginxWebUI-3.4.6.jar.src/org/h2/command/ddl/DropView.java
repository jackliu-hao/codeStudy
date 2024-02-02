/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.constraint.ConstraintActionType;
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
/*    */ import org.h2.table.Table;
/*    */ import org.h2.table.TableType;
/*    */ import org.h2.table.TableView;
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
/*    */ public class DropView
/*    */   extends SchemaCommand
/*    */ {
/*    */   private String viewName;
/*    */   private boolean ifExists;
/*    */   private ConstraintActionType dropAction;
/*    */   
/*    */   public DropView(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 31 */     super(paramSessionLocal, paramSchema);
/* 32 */     this.dropAction = (paramSessionLocal.getDatabase().getSettings()).dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 38 */     this.ifExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setDropAction(ConstraintActionType paramConstraintActionType) {
/* 42 */     this.dropAction = paramConstraintActionType;
/*    */   }
/*    */   
/*    */   public void setViewName(String paramString) {
/* 46 */     this.viewName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 51 */     Table table = getSchema().findTableOrView(this.session, this.viewName);
/* 52 */     if (table == null) {
/* 53 */       if (!this.ifExists) {
/* 54 */         throw DbException.get(90037, this.viewName);
/*    */       }
/*    */     } else {
/* 57 */       if (TableType.VIEW != table.getTableType()) {
/* 58 */         throw DbException.get(90037, this.viewName);
/*    */       }
/* 60 */       this.session.getUser().checkSchemaOwner(table.getSchema());
/*    */       
/* 62 */       if (this.dropAction == ConstraintActionType.RESTRICT) {
/* 63 */         for (DbObject dbObject : table.getChildren()) {
/* 64 */           if (dbObject instanceof TableView) {
/* 65 */             throw DbException.get(90107, new String[] { this.viewName, dbObject.getName() });
/*    */           }
/*    */         } 
/*    */       }
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 73 */       TableView tableView = (TableView)table;
/* 74 */       ArrayList arrayList = new ArrayList(tableView.getTables());
/*    */       
/* 76 */       table.lock(this.session, 2);
/* 77 */       this.session.getDatabase().removeSchemaObject(this.session, (SchemaObject)table);
/*    */ 
/*    */       
/* 80 */       for (Table table1 : arrayList) {
/* 81 */         if (TableType.VIEW == table1.getTableType()) {
/* 82 */           TableView tableView1 = (TableView)table1;
/* 83 */           if (tableView1.isTableExpression() && tableView1.getName() != null) {
/* 84 */             this.session.getDatabase().removeSchemaObject(this.session, (SchemaObject)tableView1);
/*    */           }
/*    */         } 
/*    */       } 
/*    */       
/* 89 */       this.session.getDatabase().unlockMeta(this.session);
/*    */     } 
/* 91 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 96 */     return 48;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */