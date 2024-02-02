/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.Right;
/*     */ import org.h2.engine.RightOwner;
/*     */ import org.h2.engine.Role;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.User;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.schema.Sequence;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableType;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public class DropDatabase
/*     */   extends DefineCommand
/*     */ {
/*     */   private boolean dropAllObjects;
/*     */   private boolean deleteFiles;
/*     */   
/*     */   public DropDatabase(SessionLocal paramSessionLocal) {
/*  36 */     super(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/*  41 */     if (this.dropAllObjects) {
/*  42 */       dropAllObjects();
/*     */     }
/*  44 */     if (this.deleteFiles) {
/*  45 */       this.session.getDatabase().setDeleteFilesOnDisconnect(true);
/*     */     }
/*  47 */     return 0L;
/*     */   }
/*     */   private void dropAllObjects() {
/*     */     boolean bool;
/*  51 */     User user = this.session.getUser();
/*  52 */     user.checkAdmin();
/*  53 */     Database database = this.session.getDatabase();
/*  54 */     database.lockMeta(this.session);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/*  60 */       ArrayList arrayList1 = database.getAllTablesAndViews();
/*  61 */       ArrayList<Table> arrayList2 = new ArrayList(arrayList1.size());
/*  62 */       for (Table table : arrayList1) {
/*  63 */         if (table.getName() != null && TableType.VIEW == table
/*  64 */           .getTableType()) {
/*  65 */           arrayList2.add(table);
/*     */         }
/*     */       } 
/*  68 */       for (Table table : arrayList1) {
/*  69 */         if (table.getName() != null && TableType.TABLE_LINK == table
/*  70 */           .getTableType()) {
/*  71 */           arrayList2.add(table);
/*     */         }
/*     */       } 
/*  74 */       for (Table table : arrayList1) {
/*  75 */         if (table.getName() != null && TableType.TABLE == table
/*  76 */           .getTableType() && 
/*  77 */           !table.isHidden()) {
/*  78 */           arrayList2.add(table);
/*     */         }
/*     */       } 
/*  81 */       for (Table table : arrayList1) {
/*  82 */         if (table.getName() != null && TableType.EXTERNAL_TABLE_ENGINE == table
/*  83 */           .getTableType() && 
/*  84 */           !table.isHidden()) {
/*  85 */           arrayList2.add(table);
/*     */         }
/*     */       } 
/*  88 */       bool = false;
/*  89 */       for (Table table : arrayList2) {
/*  90 */         if (table.getName() == null)
/*     */           continue; 
/*  92 */         if (database.getDependentTable((SchemaObject)table, table) == null) {
/*  93 */           database.removeSchemaObject(this.session, (SchemaObject)table); continue;
/*     */         } 
/*  95 */         bool = true;
/*     */       }
/*     */     
/*  98 */     } while (bool);
/*     */ 
/*     */     
/* 101 */     Collection<Schema> collection = database.getAllSchemasNoMeta();
/* 102 */     for (Schema schema : collection) {
/* 103 */       if (schema.canDrop()) {
/* 104 */         database.removeDatabaseObject(this.session, (DbObject)schema);
/*     */       }
/*     */     } 
/* 107 */     ArrayList<Sequence> arrayList = new ArrayList();
/* 108 */     for (Schema schema : collection) {
/* 109 */       for (Sequence sequence : schema.getAllSequences()) {
/*     */ 
/*     */ 
/*     */         
/* 113 */         if (!sequence.getBelongsToTable()) {
/* 114 */           arrayList.add(sequence);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 120 */     addAll(collection, 5, (ArrayList)arrayList);
/* 121 */     addAll(collection, 4, (ArrayList)arrayList);
/* 122 */     addAll(collection, 11, (ArrayList)arrayList);
/*     */     
/* 124 */     addAll(collection, 9, (ArrayList)arrayList);
/* 125 */     addAll(collection, 12, (ArrayList)arrayList);
/* 126 */     for (SchemaObject schemaObject : arrayList) {
/* 127 */       if (!schemaObject.getSchema().isValid() || schemaObject.isHidden()) {
/*     */         continue;
/*     */       }
/* 130 */       database.removeSchemaObject(this.session, schemaObject);
/*     */     } 
/* 132 */     Role role = database.getPublicRole();
/* 133 */     for (RightOwner rightOwner : database.getAllUsersAndRoles()) {
/* 134 */       if (rightOwner != user && rightOwner != role) {
/* 135 */         database.removeDatabaseObject(this.session, (DbObject)rightOwner);
/*     */       }
/*     */     } 
/* 138 */     for (Right right : database.getAllRights()) {
/* 139 */       database.removeDatabaseObject(this.session, (DbObject)right);
/*     */     }
/* 141 */     for (SessionLocal sessionLocal : database.getSessions(false)) {
/* 142 */       sessionLocal.setLastIdentity((Value)ValueNull.INSTANCE);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void addAll(Collection<Schema> paramCollection, int paramInt, ArrayList<SchemaObject> paramArrayList) {
/* 147 */     for (Schema schema : paramCollection) {
/* 148 */       schema.getAll(paramInt, paramArrayList);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setDropAllObjects(boolean paramBoolean) {
/* 153 */     this.dropAllObjects = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setDeleteFiles(boolean paramBoolean) {
/* 157 */     this.deleteFiles = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 162 */     return 38;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropDatabase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */