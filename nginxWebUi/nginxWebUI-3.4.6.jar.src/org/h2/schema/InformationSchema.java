/*    */ package org.h2.schema;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.engine.User;
/*    */ import org.h2.table.InformationSchemaTable;
/*    */ import org.h2.table.InformationSchemaTableLegacy;
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
/*    */ 
/*    */ 
/*    */ public final class InformationSchema
/*    */   extends MetaSchema
/*    */ {
/*    */   private volatile HashMap<String, Table> newTables;
/*    */   private volatile HashMap<String, Table> oldTables;
/*    */   
/*    */   public InformationSchema(Database paramDatabase, User paramUser) {
/* 38 */     super(paramDatabase, -1, paramDatabase.sysIdentifier("INFORMATION_SCHEMA"), paramUser);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Map<String, Table> getMap(SessionLocal paramSessionLocal) {
/* 43 */     if (paramSessionLocal == null) {
/* 44 */       return Collections.emptyMap();
/*    */     }
/* 46 */     boolean bool = paramSessionLocal.isOldInformationSchema();
/* 47 */     HashMap<String, Table> hashMap = bool ? this.oldTables : this.newTables;
/* 48 */     if (hashMap == null) {
/* 49 */       hashMap = fillMap(bool);
/*    */     }
/* 51 */     return hashMap;
/*    */   }
/*    */   
/*    */   private synchronized HashMap<String, Table> fillMap(boolean paramBoolean) {
/* 55 */     HashMap<String, Table> hashMap = paramBoolean ? this.oldTables : this.newTables;
/* 56 */     if (hashMap == null) {
/* 57 */       hashMap = this.database.newStringMap(64);
/* 58 */       if (paramBoolean) {
/* 59 */         for (byte b = 0; b < 36; b++) {
/* 60 */           InformationSchemaTableLegacy informationSchemaTableLegacy = new InformationSchemaTableLegacy(this, -1 - b, b);
/*    */           
/* 62 */           hashMap.put(informationSchemaTableLegacy.getName(), informationSchemaTableLegacy);
/*    */         } 
/* 64 */         this.oldTables = hashMap;
/*    */       } else {
/* 66 */         for (byte b = 0; b < 35; b++) {
/* 67 */           InformationSchemaTable informationSchemaTable = new InformationSchemaTable(this, -1 - b, b);
/*    */           
/* 69 */           hashMap.put(informationSchemaTable.getName(), informationSchemaTable);
/*    */         } 
/* 71 */         this.newTables = hashMap;
/*    */       } 
/*    */     } 
/* 74 */     return hashMap;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\schema\InformationSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */