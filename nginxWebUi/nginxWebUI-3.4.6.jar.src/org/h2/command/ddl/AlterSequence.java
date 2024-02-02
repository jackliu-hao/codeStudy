/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.Sequence;
/*     */ import org.h2.table.Column;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AlterSequence
/*     */   extends SchemaOwnerCommand
/*     */ {
/*     */   private boolean ifExists;
/*     */   private Column column;
/*     */   private Boolean always;
/*     */   private String sequenceName;
/*     */   private Sequence sequence;
/*     */   private SequenceOptions options;
/*     */   
/*     */   public AlterSequence(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  35 */     super(paramSessionLocal, paramSchema);
/*  36 */     this.transactional = true;
/*     */   }
/*     */   
/*     */   public void setIfExists(boolean paramBoolean) {
/*  40 */     this.ifExists = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setSequenceName(String paramString) {
/*  44 */     this.sequenceName = paramString;
/*     */   }
/*     */   
/*     */   public void setOptions(SequenceOptions paramSequenceOptions) {
/*  48 */     this.options = paramSequenceOptions;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTransactional() {
/*  53 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColumn(Column paramColumn, Boolean paramBoolean) {
/*  64 */     this.column = paramColumn;
/*  65 */     this.always = paramBoolean;
/*  66 */     this.sequence = paramColumn.getSequence();
/*  67 */     if (this.sequence == null && !this.ifExists) {
/*  68 */       throw DbException.get(90036, paramColumn.getTraceSQL());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   long update(Schema paramSchema) {
/*  74 */     if (this.sequence == null) {
/*  75 */       this.sequence = paramSchema.findSequence(this.sequenceName);
/*  76 */       if (this.sequence == null) {
/*  77 */         if (!this.ifExists) {
/*  78 */           throw DbException.get(90036, this.sequenceName);
/*     */         }
/*  80 */         return 0L;
/*     */       } 
/*     */     } 
/*  83 */     if (this.column != null) {
/*  84 */       this.session.getUser().checkTableRight(this.column.getTable(), 32);
/*     */     }
/*  86 */     this.options.setDataType(this.sequence.getDataType());
/*  87 */     Long long_ = this.options.getStartValue(this.session);
/*  88 */     this.sequence.modify(this.options
/*  89 */         .getRestartValue(this.session, (long_ != null) ? long_.longValue() : this.sequence.getStartValue()), long_, this.options
/*     */         
/*  91 */         .getMinValue(this.sequence, this.session), this.options.getMaxValue(this.sequence, this.session), this.options
/*  92 */         .getIncrement(this.session), this.options.getCycle(), this.options.getCacheSize(this.session));
/*  93 */     this.sequence.flush(this.session);
/*  94 */     if (this.column != null && this.always != null) {
/*  95 */       this.column.setSequence(this.sequence, this.always.booleanValue());
/*  96 */       this.session.getDatabase().updateMeta(this.session, (DbObject)this.column.getTable());
/*     */     } 
/*  98 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 103 */     return 54;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */