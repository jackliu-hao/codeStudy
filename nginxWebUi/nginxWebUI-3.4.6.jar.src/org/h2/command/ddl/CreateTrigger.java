/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.schema.TriggerObject;
/*     */ import org.h2.table.Table;
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
/*     */ 
/*     */ public class CreateTrigger
/*     */   extends SchemaCommand
/*     */ {
/*     */   private String triggerName;
/*     */   private boolean ifNotExists;
/*     */   private boolean insteadOf;
/*     */   private boolean before;
/*     */   private int typeMask;
/*     */   private boolean rowBased;
/*  31 */   private int queueSize = 1024;
/*     */   private boolean noWait;
/*     */   private String tableName;
/*     */   private String triggerClassName;
/*     */   private String triggerSource;
/*     */   private boolean force;
/*     */   private boolean onRollback;
/*     */   
/*     */   public CreateTrigger(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  40 */     super(paramSessionLocal, paramSchema);
/*     */   }
/*     */   
/*     */   public void setInsteadOf(boolean paramBoolean) {
/*  44 */     this.insteadOf = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setBefore(boolean paramBoolean) {
/*  48 */     this.before = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setTriggerClassName(String paramString) {
/*  52 */     this.triggerClassName = paramString;
/*     */   }
/*     */   
/*     */   public void setTriggerSource(String paramString) {
/*  56 */     this.triggerSource = paramString;
/*     */   }
/*     */   
/*     */   public void setTypeMask(int paramInt) {
/*  60 */     this.typeMask = paramInt;
/*     */   }
/*     */   
/*     */   public void setRowBased(boolean paramBoolean) {
/*  64 */     this.rowBased = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setQueueSize(int paramInt) {
/*  68 */     this.queueSize = paramInt;
/*     */   }
/*     */   
/*     */   public void setNoWait(boolean paramBoolean) {
/*  72 */     this.noWait = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setTableName(String paramString) {
/*  76 */     this.tableName = paramString;
/*     */   }
/*     */   
/*     */   public void setTriggerName(String paramString) {
/*  80 */     this.triggerName = paramString;
/*     */   }
/*     */   
/*     */   public void setIfNotExists(boolean paramBoolean) {
/*  84 */     this.ifNotExists = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/*  89 */     this.session.getUser().checkAdmin();
/*  90 */     Database database = this.session.getDatabase();
/*  91 */     if (getSchema().findTrigger(this.triggerName) != null) {
/*  92 */       if (this.ifNotExists) {
/*  93 */         return 0L;
/*     */       }
/*  95 */       throw DbException.get(90041, this.triggerName);
/*     */     } 
/*     */ 
/*     */     
/*  99 */     if ((this.typeMask & 0x8) != 0) {
/* 100 */       if (this.rowBased) {
/* 101 */         throw DbException.get(90005, "SELECT + FOR EACH ROW");
/*     */       }
/* 103 */       if (this.onRollback) {
/* 104 */         throw DbException.get(90005, "SELECT + ROLLBACK");
/*     */       }
/* 106 */     } else if ((this.typeMask & 0x7) == 0) {
/* 107 */       if (this.onRollback) {
/* 108 */         throw DbException.get(90005, "(!INSERT & !UPDATE & !DELETE) + ROLLBACK");
/*     */       }
/* 110 */       throw DbException.getInternalError();
/*     */     } 
/* 112 */     int i = getObjectId();
/* 113 */     Table table = getSchema().getTableOrView(this.session, this.tableName);
/* 114 */     TriggerObject triggerObject = new TriggerObject(getSchema(), i, this.triggerName, table);
/* 115 */     triggerObject.setInsteadOf(this.insteadOf);
/* 116 */     triggerObject.setBefore(this.before);
/* 117 */     triggerObject.setNoWait(this.noWait);
/* 118 */     triggerObject.setQueueSize(this.queueSize);
/* 119 */     triggerObject.setRowBased(this.rowBased);
/* 120 */     triggerObject.setTypeMask(this.typeMask);
/* 121 */     triggerObject.setOnRollback(this.onRollback);
/* 122 */     if (this.triggerClassName != null) {
/* 123 */       triggerObject.setTriggerClassName(this.triggerClassName, this.force);
/*     */     } else {
/* 125 */       triggerObject.setTriggerSource(this.triggerSource, this.force);
/*     */     } 
/* 127 */     database.addSchemaObject(this.session, (SchemaObject)triggerObject);
/* 128 */     table.addTrigger(triggerObject);
/* 129 */     return 0L;
/*     */   }
/*     */   
/*     */   public void setForce(boolean paramBoolean) {
/* 133 */     this.force = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setOnRollback(boolean paramBoolean) {
/* 137 */     this.onRollback = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 142 */     return 31;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateTrigger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */