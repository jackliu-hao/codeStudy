/*     */ package org.h2.table;
/*     */ 
/*     */ import org.h2.command.ddl.CreateSynonymData;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.util.ParserUtil;
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
/*     */ public class TableSynonym
/*     */   extends SchemaObject
/*     */ {
/*     */   private CreateSynonymData data;
/*     */   private Table synonymFor;
/*     */   
/*     */   public TableSynonym(CreateSynonymData paramCreateSynonymData) {
/*  30 */     super(paramCreateSynonymData.schema, paramCreateSynonymData.id, paramCreateSynonymData.synonymName, 11);
/*  31 */     this.data = paramCreateSynonymData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table getSynonymFor() {
/*  38 */     return this.synonymFor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateData(CreateSynonymData paramCreateSynonymData) {
/*  47 */     this.data = paramCreateSynonymData;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/*  52 */     return 15;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/*  57 */     return this.synonymFor.getCreateSQLForCopy(paramTable, paramString);
/*     */   }
/*     */   
/*     */   public void rename(String paramString) {
/*  61 */     throw DbException.getUnsupportedException("SYNONYM");
/*     */   }
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/*  65 */     this.synonymFor.removeSynonym(this);
/*  66 */     this.database.removeMeta(paramSessionLocal, getId());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  71 */     StringBuilder stringBuilder = new StringBuilder("CREATE SYNONYM ");
/*  72 */     getSQL(stringBuilder, 0).append(" FOR ");
/*  73 */     ParserUtil.quoteIdentifier(stringBuilder, this.data.synonymForSchema.getName(), 0).append('.');
/*  74 */     ParserUtil.quoteIdentifier(stringBuilder, this.data.synonymFor, 0);
/*  75 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDropSQL() {
/*  80 */     return getSQL(new StringBuilder("DROP SYNONYM "), 0).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkRename() {
/*  85 */     throw DbException.getUnsupportedException("SYNONYM");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSynonymForName() {
/*  92 */     return this.data.synonymFor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Schema getSynonymForSchema() {
/*  99 */     return this.data.synonymForSchema;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInvalid() {
/* 106 */     return this.synonymFor.isValid();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateSynonymFor() {
/* 113 */     if (this.synonymFor != null) {
/* 114 */       this.synonymFor.removeSynonym(this);
/*     */     }
/* 116 */     this.synonymFor = this.data.synonymForSchema.getTableOrView(this.data.session, this.data.synonymFor);
/* 117 */     this.synonymFor.addSynonym(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\TableSynonym.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */