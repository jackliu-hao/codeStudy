/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.h2.constraint.Constraint;
/*     */ import org.h2.constraint.ConstraintActionType;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableView;
/*     */ import org.h2.util.Utils;
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
/*     */ public class DropTable
/*     */   extends DefineCommand
/*     */ {
/*     */   private boolean ifExists;
/*     */   private ConstraintActionType dropAction;
/*  34 */   private final ArrayList<SchemaAndTable> tables = Utils.newSmallArrayList();
/*     */   
/*     */   public DropTable(SessionLocal paramSessionLocal) {
/*  37 */     super(paramSessionLocal);
/*  38 */     this.dropAction = (paramSessionLocal.getDatabase().getSettings()).dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIfExists(boolean paramBoolean) {
/*  44 */     this.ifExists = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTable(Schema paramSchema, String paramString) {
/*  54 */     this.tables.add(new SchemaAndTable(paramSchema, paramString));
/*     */   }
/*     */   
/*     */   private boolean prepareDrop() {
/*  58 */     HashSet<Table> hashSet = new HashSet();
/*  59 */     for (SchemaAndTable schemaAndTable : this.tables) {
/*  60 */       String str = schemaAndTable.tableName;
/*  61 */       Table table = schemaAndTable.schema.findTableOrView(this.session, str);
/*  62 */       if (table == null) {
/*  63 */         if (!this.ifExists)
/*  64 */           throw DbException.get(42102, str); 
/*     */         continue;
/*     */       } 
/*  67 */       this.session.getUser().checkTableRight(table, 32);
/*  68 */       if (!table.canDrop()) {
/*  69 */         throw DbException.get(90118, str);
/*     */       }
/*  71 */       hashSet.add(table);
/*     */     } 
/*     */     
/*  74 */     if (hashSet.isEmpty()) {
/*  75 */       return false;
/*     */     }
/*  77 */     for (Table table : hashSet) {
/*  78 */       ArrayList<String> arrayList = new ArrayList();
/*  79 */       if (this.dropAction == ConstraintActionType.RESTRICT) {
/*  80 */         CopyOnWriteArrayList copyOnWriteArrayList = table.getDependentViews();
/*  81 */         if (copyOnWriteArrayList != null && !copyOnWriteArrayList.isEmpty()) {
/*  82 */           for (TableView tableView : copyOnWriteArrayList) {
/*  83 */             if (!hashSet.contains(tableView)) {
/*  84 */               arrayList.add(tableView.getName());
/*     */             }
/*     */           } 
/*     */         }
/*  88 */         ArrayList arrayList1 = table.getConstraints();
/*  89 */         if (arrayList1 != null && !arrayList1.isEmpty()) {
/*  90 */           for (Constraint constraint : arrayList1) {
/*  91 */             if (!hashSet.contains(constraint.getTable())) {
/*  92 */               arrayList.add(constraint.getName());
/*     */             }
/*     */           } 
/*     */         }
/*  96 */         if (!arrayList.isEmpty()) {
/*  97 */           throw DbException.get(90107, new String[] { table.getName(), String.join(", ", arrayList) });
/*     */         }
/*     */       } 
/* 100 */       table.lock(this.session, 2);
/*     */     } 
/* 102 */     return true;
/*     */   }
/*     */   
/*     */   private void executeDrop() {
/* 106 */     for (SchemaAndTable schemaAndTable : this.tables) {
/*     */ 
/*     */       
/* 109 */       Table table = schemaAndTable.schema.findTableOrView(this.session, schemaAndTable.tableName);
/* 110 */       if (table != null) {
/* 111 */         table.setModified();
/* 112 */         Database database = this.session.getDatabase();
/* 113 */         database.lockMeta(this.session);
/* 114 */         database.removeSchemaObject(this.session, (SchemaObject)table);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/* 121 */     if (prepareDrop()) {
/* 122 */       executeDrop();
/*     */     }
/* 124 */     return 0L;
/*     */   }
/*     */   
/*     */   public void setDropAction(ConstraintActionType paramConstraintActionType) {
/* 128 */     this.dropAction = paramConstraintActionType;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 133 */     return 44;
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class SchemaAndTable
/*     */   {
/*     */     final Schema schema;
/*     */     final String tableName;
/*     */     
/*     */     SchemaAndTable(Schema param1Schema, String param1String) {
/* 143 */       this.schema = param1Schema;
/* 144 */       this.tableName = param1String;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */