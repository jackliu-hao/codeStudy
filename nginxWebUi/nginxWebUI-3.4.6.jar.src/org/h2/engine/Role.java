/*    */ package org.h2.engine;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.message.DbException;
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
/*    */ public final class Role
/*    */   extends RightOwner
/*    */ {
/*    */   private final boolean system;
/*    */   
/*    */   public Role(Database paramDatabase, int paramInt, String paramString, boolean paramBoolean) {
/* 23 */     super(paramDatabase, paramInt, paramString, 13);
/* 24 */     this.system = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/* 29 */     throw DbException.getInternalError(toString());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCreateSQL(boolean paramBoolean) {
/* 39 */     if (this.system) {
/* 40 */       return null;
/*    */     }
/* 42 */     StringBuilder stringBuilder = new StringBuilder("CREATE ROLE ");
/* 43 */     if (paramBoolean) {
/* 44 */       stringBuilder.append("IF NOT EXISTS ");
/*    */     }
/* 46 */     return getSQL(stringBuilder, 0).toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCreateSQL() {
/* 51 */     return getCreateSQL(false);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 56 */     return 7;
/*    */   }
/*    */ 
/*    */   
/*    */   public ArrayList<DbObject> getChildren() {
/* 61 */     ArrayList<Schema> arrayList = new ArrayList();
/* 62 */     for (Schema schema : this.database.getAllSchemas()) {
/* 63 */       if (schema.getOwner() == this) {
/* 64 */         arrayList.add(schema);
/*    */       }
/*    */     } 
/* 67 */     return (ArrayList)arrayList;
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 72 */     for (RightOwner rightOwner : this.database.getAllUsersAndRoles()) {
/* 73 */       Right right = rightOwner.getRightForRole(this);
/* 74 */       if (right != null) {
/* 75 */         this.database.removeDatabaseObject(paramSessionLocal, right);
/*    */       }
/*    */     } 
/* 78 */     for (Right right : this.database.getAllRights()) {
/* 79 */       if (right.getGrantee() == this) {
/* 80 */         this.database.removeDatabaseObject(paramSessionLocal, right);
/*    */       }
/*    */     } 
/* 83 */     this.database.removeMeta(paramSessionLocal, getId());
/* 84 */     invalidate();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\Role.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */