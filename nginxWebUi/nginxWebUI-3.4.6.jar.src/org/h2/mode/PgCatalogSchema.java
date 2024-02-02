/*    */ package org.h2.mode;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.engine.User;
/*    */ import org.h2.schema.MetaSchema;
/*    */ import org.h2.schema.Schema;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class PgCatalogSchema
/*    */   extends MetaSchema
/*    */ {
/*    */   private volatile HashMap<String, Table> tables;
/*    */   
/*    */   public PgCatalogSchema(Database paramDatabase, User paramUser) {
/* 34 */     super(paramDatabase, -1000, paramDatabase.sysIdentifier("PG_CATALOG"), paramUser);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Map<String, Table> getMap(SessionLocal paramSessionLocal) {
/* 39 */     HashMap<String, Table> hashMap = this.tables;
/* 40 */     if (hashMap == null) {
/* 41 */       hashMap = fillMap();
/*    */     }
/* 43 */     return hashMap;
/*    */   }
/*    */   
/*    */   private synchronized HashMap<String, Table> fillMap() {
/* 47 */     HashMap<String, Table> hashMap = this.tables;
/* 48 */     if (hashMap == null) {
/* 49 */       hashMap = this.database.newStringMap();
/* 50 */       for (byte b = 0; b < 19; b++) {
/* 51 */         PgCatalogTable pgCatalogTable = new PgCatalogTable((Schema)this, -1000 - b, b);
/* 52 */         hashMap.put(pgCatalogTable.getName(), pgCatalogTable);
/*    */       } 
/* 54 */       this.tables = hashMap;
/*    */     } 
/* 56 */     return hashMap;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\PgCatalogSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */