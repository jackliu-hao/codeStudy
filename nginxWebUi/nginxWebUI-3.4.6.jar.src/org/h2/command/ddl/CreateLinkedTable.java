/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableLink;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CreateLinkedTable
/*     */   extends SchemaCommand
/*     */ {
/*     */   private String tableName;
/*     */   private String driver;
/*     */   private String url;
/*     */   private String user;
/*     */   private String password;
/*     */   private String originalSchema;
/*     */   private String originalTable;
/*     */   private boolean ifNotExists;
/*     */   private String comment;
/*     */   private boolean emitUpdates;
/*     */   private boolean force;
/*     */   private boolean temporary;
/*     */   private boolean globalTemporary;
/*     */   private boolean readOnly;
/*     */   private int fetchSize;
/*     */   private boolean autocommit = true;
/*     */   
/*     */   public CreateLinkedTable(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  35 */     super(paramSessionLocal, paramSchema);
/*     */   }
/*     */   
/*     */   public void setTableName(String paramString) {
/*  39 */     this.tableName = paramString;
/*     */   }
/*     */   
/*     */   public void setDriver(String paramString) {
/*  43 */     this.driver = paramString;
/*     */   }
/*     */   
/*     */   public void setOriginalTable(String paramString) {
/*  47 */     this.originalTable = paramString;
/*     */   }
/*     */   
/*     */   public void setPassword(String paramString) {
/*  51 */     this.password = paramString;
/*     */   }
/*     */   
/*     */   public void setUrl(String paramString) {
/*  55 */     this.url = paramString;
/*     */   }
/*     */   
/*     */   public void setUser(String paramString) {
/*  59 */     this.user = paramString;
/*     */   }
/*     */   
/*     */   public void setIfNotExists(boolean paramBoolean) {
/*  63 */     this.ifNotExists = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFetchSize(int paramInt) {
/*  72 */     this.fetchSize = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAutoCommit(boolean paramBoolean) {
/*  81 */     this.autocommit = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/*  86 */     this.session.getUser().checkAdmin();
/*  87 */     Database database = this.session.getDatabase();
/*  88 */     if (getSchema().resolveTableOrView(this.session, this.tableName) != null) {
/*  89 */       if (this.ifNotExists) {
/*  90 */         return 0L;
/*     */       }
/*  92 */       throw DbException.get(42101, this.tableName);
/*     */     } 
/*     */     
/*  95 */     int i = getObjectId();
/*  96 */     TableLink tableLink = getSchema().createTableLink(i, this.tableName, this.driver, this.url, this.user, this.password, this.originalSchema, this.originalTable, this.emitUpdates, this.force);
/*     */     
/*  98 */     tableLink.setTemporary(this.temporary);
/*  99 */     tableLink.setGlobalTemporary(this.globalTemporary);
/* 100 */     tableLink.setComment(this.comment);
/* 101 */     tableLink.setReadOnly(this.readOnly);
/* 102 */     if (this.fetchSize > 0) {
/* 103 */       tableLink.setFetchSize(this.fetchSize);
/*     */     }
/* 105 */     tableLink.setAutoCommit(this.autocommit);
/* 106 */     if (this.temporary && !this.globalTemporary) {
/* 107 */       this.session.addLocalTempTable((Table)tableLink);
/*     */     } else {
/* 109 */       database.addSchemaObject(this.session, (SchemaObject)tableLink);
/*     */     } 
/* 111 */     return 0L;
/*     */   }
/*     */   
/*     */   public void setEmitUpdates(boolean paramBoolean) {
/* 115 */     this.emitUpdates = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setComment(String paramString) {
/* 119 */     this.comment = paramString;
/*     */   }
/*     */   
/*     */   public void setForce(boolean paramBoolean) {
/* 123 */     this.force = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setTemporary(boolean paramBoolean) {
/* 127 */     this.temporary = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setGlobalTemporary(boolean paramBoolean) {
/* 131 */     this.globalTemporary = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setReadOnly(boolean paramBoolean) {
/* 135 */     this.readOnly = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setOriginalSchema(String paramString) {
/* 139 */     this.originalSchema = paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 144 */     return 26;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateLinkedTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */