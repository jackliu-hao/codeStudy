/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Schema
/*     */   extends DatabaseObject
/*     */ {
/*     */   List<Collection> getCollections();
/*     */   
/*     */   List<Collection> getCollections(String paramString);
/*     */   
/*     */   List<Table> getTables();
/*     */   
/*     */   List<Table> getTables(String paramString);
/*     */   
/*     */   Collection getCollection(String paramString);
/*     */   
/*     */   Collection getCollection(String paramString, boolean paramBoolean);
/*     */   
/*     */   Table getCollectionAsTable(String paramString);
/*     */   
/*     */   Table getTable(String paramString);
/*     */   
/*     */   Table getTable(String paramString, boolean paramBoolean);
/*     */   
/*     */   Collection createCollection(String paramString);
/*     */   
/*     */   Collection createCollection(String paramString, boolean paramBoolean);
/*     */   
/*     */   Collection createCollection(String paramString, CreateCollectionOptions paramCreateCollectionOptions);
/*     */   
/*     */   void modifyCollection(String paramString, ModifyCollectionOptions paramModifyCollectionOptions);
/*     */   
/*     */   void dropCollection(String paramString);
/*     */   
/*     */   public static class CreateCollectionOptions
/*     */   {
/* 205 */     private Boolean reuseExisting = null;
/* 206 */     private Schema.Validation validation = null;
/*     */     
/*     */     public CreateCollectionOptions setReuseExisting(boolean reuse) {
/* 209 */       this.reuseExisting = Boolean.valueOf(reuse);
/* 210 */       return this;
/*     */     }
/*     */     
/*     */     public Boolean getReuseExisting() {
/* 214 */       return this.reuseExisting;
/*     */     }
/*     */     
/*     */     public CreateCollectionOptions setValidation(Schema.Validation validation) {
/* 218 */       this.validation = validation;
/* 219 */       return this;
/*     */     }
/*     */     
/*     */     public Schema.Validation getValidation() {
/* 223 */       return this.validation;
/*     */     }
/*     */   }
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
/*     */   public static class ModifyCollectionOptions
/*     */   {
/* 239 */     private Schema.Validation validation = null;
/*     */     
/*     */     public ModifyCollectionOptions setValidation(Schema.Validation validation) {
/* 242 */       this.validation = validation;
/* 243 */       return this;
/*     */     }
/*     */     
/*     */     public Schema.Validation getValidation() {
/* 247 */       return this.validation;
/*     */     }
/*     */   }
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Validation
/*     */   {
/*     */     public enum ValidationLevel
/*     */     {
/* 272 */       STRICT, OFF;
/*     */     }
/*     */     
/* 275 */     private ValidationLevel level = null;
/* 276 */     private String schema = null;
/*     */     
/*     */     public Validation setLevel(ValidationLevel level) {
/* 279 */       this.level = level;
/* 280 */       return this;
/*     */     }
/*     */     
/*     */     public ValidationLevel getLevel() {
/* 284 */       return this.level;
/*     */     }
/*     */     
/*     */     public Validation setSchema(String schema) {
/* 288 */       this.schema = schema;
/* 289 */       return this;
/*     */     }
/*     */     
/*     */     public String getSchema() {
/* 293 */       return this.schema;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\Schema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */