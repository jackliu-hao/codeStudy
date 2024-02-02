/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import org.h2.constraint.Constraint;
/*     */ import org.h2.engine.Comment;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.Role;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.User;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Constant;
/*     */ import org.h2.schema.Domain;
/*     */ import org.h2.schema.FunctionAlias;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.Sequence;
/*     */ import org.h2.schema.TriggerObject;
/*     */ import org.h2.table.Table;
/*     */ 
/*     */ 
/*     */ public class SetComment
/*     */   extends DefineCommand
/*     */ {
/*     */   private String schemaName;
/*     */   private String objectName;
/*     */   private boolean column;
/*     */   private String columnName;
/*     */   private int objectType;
/*     */   private Expression expr;
/*     */   
/*     */   public SetComment(SessionLocal paramSessionLocal) {
/*  33 */     super(paramSessionLocal); } public long update() { Constraint constraint; FunctionAlias functionAlias; Index index; Role role; Schema schema1; Sequence sequence; Table table;
/*     */     TriggerObject triggerObject;
/*     */     User user;
/*     */     Domain domain;
/*     */     Schema schema2;
/*  38 */     Database database = this.session.getDatabase();
/*  39 */     Constant constant = null;
/*  40 */     int i = 50000;
/*  41 */     if (this.schemaName == null) {
/*  42 */       this.schemaName = this.session.getCurrentSchemaName();
/*     */     }
/*  44 */     switch (this.objectType) {
/*     */       case 11:
/*  46 */         schema2 = database.getSchema(this.schemaName);
/*  47 */         this.session.getUser().checkSchemaOwner(schema2);
/*  48 */         constant = schema2.getConstant(this.objectName);
/*     */         break;
/*     */       
/*     */       case 5:
/*  52 */         schema2 = database.getSchema(this.schemaName);
/*  53 */         this.session.getUser().checkSchemaOwner(schema2);
/*  54 */         constraint = schema2.getConstraint(this.objectName);
/*     */         break;
/*     */       
/*     */       case 9:
/*  58 */         schema2 = database.getSchema(this.schemaName);
/*  59 */         this.session.getUser().checkSchemaOwner(schema2);
/*  60 */         functionAlias = schema2.findFunction(this.objectName);
/*  61 */         i = 90077;
/*     */         break;
/*     */       
/*     */       case 1:
/*  65 */         schema2 = database.getSchema(this.schemaName);
/*  66 */         this.session.getUser().checkSchemaOwner(schema2);
/*  67 */         index = schema2.getIndex(this.objectName);
/*     */         break;
/*     */       
/*     */       case 7:
/*  71 */         this.session.getUser().checkAdmin();
/*  72 */         this.schemaName = null;
/*  73 */         role = database.findRole(this.objectName);
/*  74 */         i = 90070;
/*     */         break;
/*     */       case 10:
/*  77 */         this.schemaName = null;
/*  78 */         schema2 = database.getSchema(this.objectName);
/*  79 */         this.session.getUser().checkSchemaOwner(schema2);
/*  80 */         schema1 = schema2;
/*     */         break;
/*     */       
/*     */       case 3:
/*  84 */         schema2 = database.getSchema(this.schemaName);
/*  85 */         this.session.getUser().checkSchemaOwner(schema2);
/*  86 */         sequence = schema2.getSequence(this.objectName);
/*     */         break;
/*     */       
/*     */       case 0:
/*  90 */         schema2 = database.getSchema(this.schemaName);
/*  91 */         this.session.getUser().checkSchemaOwner(schema2);
/*  92 */         table = schema2.getTableOrView(this.session, this.objectName);
/*     */         break;
/*     */       
/*     */       case 4:
/*  96 */         schema2 = database.getSchema(this.schemaName);
/*  97 */         this.session.getUser().checkSchemaOwner(schema2);
/*  98 */         triggerObject = schema2.findTrigger(this.objectName);
/*  99 */         i = 90042;
/*     */         break;
/*     */       
/*     */       case 2:
/* 103 */         this.session.getUser().checkAdmin();
/* 104 */         this.schemaName = null;
/* 105 */         user = database.getUser(this.objectName);
/*     */         break;
/*     */       case 12:
/* 108 */         schema2 = database.getSchema(this.schemaName);
/* 109 */         this.session.getUser().checkSchemaOwner(schema2);
/* 110 */         domain = schema2.findDomain(this.objectName);
/* 111 */         i = 90120;
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/* 116 */     if (domain == null) {
/* 117 */       throw DbException.get(i, this.objectName);
/*     */     }
/* 119 */     String str = this.expr.optimize(this.session).getValue(this.session).getString();
/* 120 */     if (str != null && str.isEmpty()) {
/* 121 */       str = null;
/*     */     }
/* 123 */     if (this.column) {
/* 124 */       Table table1 = (Table)domain;
/* 125 */       table1.getColumn(this.columnName).setComment(str);
/*     */     } else {
/* 127 */       domain.setComment(str);
/*     */     } 
/* 129 */     if (this.column || this.objectType == 0 || this.objectType == 2 || this.objectType == 1 || this.objectType == 5) {
/*     */ 
/*     */ 
/*     */       
/* 133 */       database.updateMeta(this.session, (DbObject)domain);
/*     */     } else {
/* 135 */       Comment comment = database.findComment((DbObject)domain);
/* 136 */       if (comment == null) {
/* 137 */         if (str != null)
/*     */         {
/*     */           
/* 140 */           int j = getObjectId();
/* 141 */           comment = new Comment(database, j, (DbObject)domain);
/* 142 */           comment.setCommentText(str);
/* 143 */           database.addDatabaseObject(this.session, (DbObject)comment);
/*     */         }
/*     */       
/* 146 */       } else if (str == null) {
/* 147 */         database.removeDatabaseObject(this.session, (DbObject)comment);
/*     */       } else {
/* 149 */         comment.setCommentText(str);
/* 150 */         database.updateMeta(this.session, (DbObject)comment);
/*     */       } 
/*     */     } 
/*     */     
/* 154 */     return 0L; }
/*     */ 
/*     */   
/*     */   public void setCommentExpression(Expression paramExpression) {
/* 158 */     this.expr = paramExpression;
/*     */   }
/*     */   
/*     */   public void setObjectName(String paramString) {
/* 162 */     this.objectName = paramString;
/*     */   }
/*     */   
/*     */   public void setObjectType(int paramInt) {
/* 166 */     this.objectType = paramInt;
/*     */   }
/*     */   
/*     */   public void setColumnName(String paramString) {
/* 170 */     this.columnName = paramString;
/*     */   }
/*     */   
/*     */   public void setSchemaName(String paramString) {
/* 174 */     this.schemaName = paramString;
/*     */   }
/*     */   
/*     */   public void setColumn(boolean paramBoolean) {
/* 178 */     this.column = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 183 */     return 52;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\SetComment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */