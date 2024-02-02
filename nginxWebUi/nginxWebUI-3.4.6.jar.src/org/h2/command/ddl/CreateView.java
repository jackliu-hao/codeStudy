/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.command.query.Query;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableType;
/*     */ import org.h2.table.TableView;
/*     */ import org.h2.value.TypeInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CreateView
/*     */   extends SchemaOwnerCommand
/*     */ {
/*     */   private Query select;
/*     */   private String viewName;
/*     */   private boolean ifNotExists;
/*     */   private String selectSQL;
/*     */   private String[] columnNames;
/*     */   private String comment;
/*     */   private boolean orReplace;
/*     */   private boolean force;
/*     */   private boolean isTableExpression;
/*     */   
/*     */   public CreateView(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  41 */     super(paramSessionLocal, paramSchema);
/*     */   }
/*     */   
/*     */   public void setViewName(String paramString) {
/*  45 */     this.viewName = paramString;
/*     */   }
/*     */   
/*     */   public void setSelect(Query paramQuery) {
/*  49 */     this.select = paramQuery;
/*     */   }
/*     */   
/*     */   public void setIfNotExists(boolean paramBoolean) {
/*  53 */     this.ifNotExists = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setSelectSQL(String paramString) {
/*  57 */     this.selectSQL = paramString;
/*     */   }
/*     */   
/*     */   public void setColumnNames(String[] paramArrayOfString) {
/*  61 */     this.columnNames = paramArrayOfString;
/*     */   }
/*     */   
/*     */   public void setComment(String paramString) {
/*  65 */     this.comment = paramString;
/*     */   }
/*     */   
/*     */   public void setOrReplace(boolean paramBoolean) {
/*  69 */     this.orReplace = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setForce(boolean paramBoolean) {
/*  73 */     this.force = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setTableExpression(boolean paramBoolean) {
/*  77 */     this.isTableExpression = paramBoolean;
/*     */   }
/*     */   
/*     */   long update(Schema paramSchema) {
/*     */     String str;
/*  82 */     Database database = this.session.getDatabase();
/*  83 */     TableView tableView = null;
/*  84 */     Table table = paramSchema.findTableOrView(this.session, this.viewName);
/*  85 */     if (table != null) {
/*  86 */       if (this.ifNotExists) {
/*  87 */         return 0L;
/*     */       }
/*  89 */       if (!this.orReplace || TableType.VIEW != table.getTableType()) {
/*  90 */         throw DbException.get(90038, this.viewName);
/*     */       }
/*  92 */       tableView = (TableView)table;
/*     */     } 
/*  94 */     int i = getObjectId();
/*     */     
/*  96 */     if (this.select == null) {
/*  97 */       str = this.selectSQL;
/*     */     } else {
/*  99 */       ArrayList arrayList = this.select.getParameters();
/* 100 */       if (arrayList != null && !arrayList.isEmpty()) {
/* 101 */         throw DbException.getUnsupportedException("parameters in views");
/*     */       }
/* 103 */       str = this.select.getPlanSQL(0);
/*     */     } 
/* 105 */     Column[] arrayOfColumn1 = null;
/* 106 */     Column[] arrayOfColumn2 = null;
/* 107 */     if (this.columnNames != null) {
/* 108 */       arrayOfColumn1 = new Column[this.columnNames.length];
/* 109 */       arrayOfColumn2 = new Column[this.columnNames.length];
/* 110 */       for (byte b = 0; b < this.columnNames.length; b++) {
/*     */         
/* 112 */         arrayOfColumn1[b] = new Column(this.columnNames[b], TypeInfo.TYPE_UNKNOWN);
/*     */         
/* 114 */         arrayOfColumn2[b] = new Column(this.columnNames[b], TypeInfo.TYPE_VARCHAR);
/*     */       } 
/*     */     } 
/* 117 */     if (tableView == null) {
/* 118 */       if (this.isTableExpression) {
/* 119 */         tableView = TableView.createTableViewMaybeRecursive(paramSchema, i, this.viewName, str, null, arrayOfColumn2, this.session, false, this.isTableExpression, false, database);
/*     */       }
/*     */       else {
/*     */         
/* 123 */         tableView = new TableView(paramSchema, i, this.viewName, str, null, arrayOfColumn1, this.session, false, false, this.isTableExpression, false);
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 128 */       tableView.replace(str, arrayOfColumn1, this.session, false, this.force, false);
/* 129 */       tableView.setModified();
/*     */     } 
/* 131 */     if (this.comment != null) {
/* 132 */       tableView.setComment(this.comment);
/*     */     }
/* 134 */     if (table == null) {
/* 135 */       database.addSchemaObject(this.session, (SchemaObject)tableView);
/* 136 */       database.unlockMeta(this.session);
/*     */     } else {
/* 138 */       database.updateMeta(this.session, (DbObject)tableView);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 144 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 149 */     return 34;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */