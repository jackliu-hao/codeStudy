/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.table.TableSynonym;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CreateSynonym
/*     */   extends SchemaOwnerCommand
/*     */ {
/*  22 */   private final CreateSynonymData data = new CreateSynonymData();
/*     */   private boolean ifNotExists;
/*     */   private boolean orReplace;
/*     */   private String comment;
/*     */   
/*     */   public CreateSynonym(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  28 */     super(paramSessionLocal, paramSchema);
/*     */   }
/*     */   
/*     */   public void setName(String paramString) {
/*  32 */     this.data.synonymName = paramString;
/*     */   }
/*     */   
/*     */   public void setSynonymFor(String paramString) {
/*  36 */     this.data.synonymFor = paramString;
/*     */   }
/*     */   
/*     */   public void setSynonymForSchema(Schema paramSchema) {
/*  40 */     this.data.synonymForSchema = paramSchema;
/*     */   }
/*     */   
/*     */   public void setIfNotExists(boolean paramBoolean) {
/*  44 */     this.ifNotExists = paramBoolean;
/*     */   }
/*     */   public void setOrReplace(boolean paramBoolean) {
/*  47 */     this.orReplace = paramBoolean;
/*     */   }
/*     */   
/*     */   long update(Schema paramSchema) {
/*  51 */     Database database = this.session.getDatabase();
/*  52 */     this.data.session = this.session;
/*  53 */     database.lockMeta(this.session);
/*     */     
/*  55 */     if (paramSchema.findTableOrView(this.session, this.data.synonymName) != null) {
/*  56 */       throw DbException.get(42101, this.data.synonymName);
/*     */     }
/*     */     
/*  59 */     if (this.data.synonymForSchema.findTableOrView(this.session, this.data.synonymFor) != null) {
/*  60 */       return createTableSynonym(database);
/*     */     }
/*     */     
/*  63 */     throw DbException.get(42102, this.data.synonymForSchema
/*  64 */         .getName() + "." + this.data.synonymFor);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int createTableSynonym(Database paramDatabase) {
/*  70 */     TableSynonym tableSynonym = getSchema().getSynonym(this.data.synonymName);
/*  71 */     if (tableSynonym == null || 
/*  72 */       this.orReplace) {
/*     */       TableSynonym tableSynonym1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  82 */       if (tableSynonym != null) {
/*  83 */         tableSynonym1 = tableSynonym;
/*  84 */         this.data.schema = tableSynonym1.getSchema();
/*  85 */         tableSynonym1.updateData(this.data);
/*  86 */         tableSynonym1.setComment(this.comment);
/*  87 */         tableSynonym1.setModified();
/*  88 */         paramDatabase.updateMeta(this.session, (DbObject)tableSynonym1);
/*     */       } else {
/*  90 */         this.data.id = getObjectId();
/*  91 */         tableSynonym1 = getSchema().createSynonym(this.data);
/*  92 */         tableSynonym1.setComment(this.comment);
/*  93 */         paramDatabase.addSchemaObject(this.session, (SchemaObject)tableSynonym1);
/*     */       } 
/*     */       
/*  96 */       tableSynonym1.updateSynonymFor();
/*  97 */       return 0;
/*     */     } 
/*     */     if (this.ifNotExists)
/*     */       return 0; 
/* 101 */     throw DbException.get(42101, this.data.synonymName); } public void setComment(String paramString) { this.comment = paramString; }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/* 106 */     return 88;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateSynonym.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */