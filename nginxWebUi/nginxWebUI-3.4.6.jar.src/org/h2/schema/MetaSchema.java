/*    */ package org.h2.schema;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Map;
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.RightOwner;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.engine.User;
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
/*    */ 
/*    */ public abstract class MetaSchema
/*    */   extends Schema
/*    */ {
/*    */   public MetaSchema(Database paramDatabase, int paramInt, String paramString, User paramUser) {
/* 35 */     super(paramDatabase, paramInt, paramString, (RightOwner)paramUser, true);
/*    */   }
/*    */ 
/*    */   
/*    */   public Table findTableOrView(SessionLocal paramSessionLocal, String paramString) {
/* 40 */     Map<String, Table> map = getMap(paramSessionLocal);
/* 41 */     Table table = map.get(paramString);
/* 42 */     if (table != null) {
/* 43 */       return table;
/*    */     }
/* 45 */     return super.findTableOrView(paramSessionLocal, paramString);
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<Table> getAllTablesAndViews(SessionLocal paramSessionLocal) {
/* 50 */     Collection<Table> collection1 = super.getAllTablesAndViews(paramSessionLocal);
/* 51 */     if (paramSessionLocal == null) {
/* 52 */       return collection1;
/*    */     }
/* 54 */     Collection<Table> collection2 = getMap(paramSessionLocal).values();
/* 55 */     if (collection1.isEmpty()) {
/* 56 */       return collection2;
/*    */     }
/* 58 */     ArrayList<Table> arrayList = new ArrayList(collection2.size() + collection1.size());
/* 59 */     arrayList.addAll(collection2);
/* 60 */     arrayList.addAll(collection1);
/* 61 */     return arrayList;
/*    */   }
/*    */ 
/*    */   
/*    */   public Table getTableOrView(SessionLocal paramSessionLocal, String paramString) {
/* 66 */     Map<String, Table> map = getMap(paramSessionLocal);
/* 67 */     Table table = map.get(paramString);
/* 68 */     if (table != null) {
/* 69 */       return table;
/*    */     }
/* 71 */     return super.getTableOrView(paramSessionLocal, paramString);
/*    */   }
/*    */ 
/*    */   
/*    */   public Table getTableOrViewByName(SessionLocal paramSessionLocal, String paramString) {
/* 76 */     Map<String, Table> map = getMap(paramSessionLocal);
/* 77 */     Table table = map.get(paramString);
/* 78 */     if (table != null) {
/* 79 */       return table;
/*    */     }
/* 81 */     return super.getTableOrViewByName(paramSessionLocal, paramString);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract Map<String, Table> getMap(SessionLocal paramSessionLocal);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 94 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\schema\MetaSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */