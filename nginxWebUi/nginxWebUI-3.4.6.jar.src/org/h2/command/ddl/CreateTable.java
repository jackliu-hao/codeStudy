/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import org.h2.command.dml.Insert;
/*     */ import org.h2.command.query.Query;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.schema.Sequence;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
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
/*     */ public class CreateTable
/*     */   extends CommandWithColumns
/*     */ {
/*  31 */   private final CreateTableData data = new CreateTableData();
/*     */   private boolean ifNotExists;
/*     */   private boolean onCommitDrop;
/*     */   private boolean onCommitTruncate;
/*     */   private Query asQuery;
/*     */   private String comment;
/*     */   private boolean withNoData;
/*     */   
/*     */   public CreateTable(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  40 */     super(paramSessionLocal, paramSchema);
/*  41 */     this.data.persistIndexes = true;
/*  42 */     this.data.persistData = true;
/*     */   }
/*     */   
/*     */   public void setQuery(Query paramQuery) {
/*  46 */     this.asQuery = paramQuery;
/*     */   }
/*     */   
/*     */   public void setTemporary(boolean paramBoolean) {
/*  50 */     this.data.temporary = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setTableName(String paramString) {
/*  54 */     this.data.tableName = paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addColumn(Column paramColumn) {
/*  59 */     this.data.columns.add(paramColumn);
/*     */   }
/*     */   
/*     */   public ArrayList<Column> getColumns() {
/*  63 */     return this.data.columns;
/*     */   }
/*     */   
/*     */   public void setIfNotExists(boolean paramBoolean) {
/*  67 */     this.ifNotExists = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/*  72 */     Schema schema = getSchema();
/*  73 */     boolean bool = (this.data.temporary && !this.data.globalTemporary) ? true : false;
/*  74 */     if (!bool) {
/*  75 */       this.session.getUser().checkSchemaOwner(schema);
/*     */     }
/*  77 */     Database database = this.session.getDatabase();
/*  78 */     if (!database.isPersistent()) {
/*  79 */       this.data.persistIndexes = false;
/*     */     }
/*  81 */     if (!bool) {
/*  82 */       database.lockMeta(this.session);
/*     */     }
/*  84 */     if (schema.resolveTableOrView(this.session, this.data.tableName) != null) {
/*  85 */       if (this.ifNotExists) {
/*  86 */         return 0L;
/*     */       }
/*  88 */       throw DbException.get(42101, this.data.tableName);
/*     */     } 
/*  90 */     if (this.asQuery != null) {
/*  91 */       this.asQuery.prepare();
/*  92 */       if (this.data.columns.isEmpty())
/*  93 */       { generateColumnsFromQuery(); }
/*  94 */       else { if (this.data.columns.size() != this.asQuery.getColumnCount()) {
/*  95 */           throw DbException.get(21002);
/*     */         }
/*  97 */         ArrayList<Column> arrayList1 = this.data.columns;
/*  98 */         for (byte b = 0; b < arrayList1.size(); b++) {
/*  99 */           Column column = arrayList1.get(b);
/* 100 */           if (column.getType().getValueType() == -1) {
/* 101 */             arrayList1.set(b, new Column(column.getName(), ((Expression)this.asQuery.getExpressions().get(b)).getType()));
/*     */           }
/*     */         }  }
/*     */     
/*     */     } 
/* 106 */     changePrimaryKeysToNotNull(this.data.columns);
/* 107 */     this.data.id = getObjectId();
/* 108 */     this.data.session = this.session;
/* 109 */     Table table = schema.createTable(this.data);
/* 110 */     ArrayList<Sequence> arrayList = generateSequences(this.data.columns, this.data.temporary);
/* 111 */     table.setComment(this.comment);
/* 112 */     if (bool) {
/* 113 */       if (this.onCommitDrop) {
/* 114 */         table.setOnCommitDrop(true);
/*     */       }
/* 116 */       if (this.onCommitTruncate) {
/* 117 */         table.setOnCommitTruncate(true);
/*     */       }
/* 119 */       this.session.addLocalTempTable(table);
/*     */     } else {
/* 121 */       database.lockMeta(this.session);
/* 122 */       database.addSchemaObject(this.session, (SchemaObject)table);
/*     */     } 
/*     */     try {
/* 125 */       for (Column column : this.data.columns) {
/* 126 */         column.prepareExpressions(this.session);
/*     */       }
/* 128 */       for (Sequence sequence : arrayList) {
/* 129 */         table.addSequence(sequence);
/*     */       }
/* 131 */       createConstraints();
/* 132 */       HashSet hashSet = new HashSet();
/* 133 */       table.addDependencies(hashSet);
/* 134 */       for (DbObject dbObject : hashSet) {
/* 135 */         if (dbObject == table) {
/*     */           continue;
/*     */         }
/* 138 */         if (dbObject.getType() == 0 && 
/* 139 */           dbObject instanceof Table) {
/* 140 */           Table table1 = (Table)dbObject;
/* 141 */           if (table1.getId() > table.getId()) {
/* 142 */             throw DbException.get(50100, "Table depends on another table with a higher ID: " + table1 + ", this is currently not supported, as it would prevent the database from being re-opened");
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 153 */       if (this.asQuery != null && !this.withNoData) {
/* 154 */         boolean bool1 = false;
/* 155 */         if (!bool) {
/* 156 */           database.unlockMeta(this.session);
/* 157 */           for (Column column : table.getColumns()) {
/* 158 */             Sequence sequence = column.getSequence();
/* 159 */             if (sequence != null) {
/* 160 */               bool1 = true;
/* 161 */               sequence.setTemporary(true);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         try {
/* 166 */           this.session.startStatementWithinTransaction(null);
/* 167 */           Insert insert = new Insert(this.session);
/* 168 */           insert.setQuery(this.asQuery);
/* 169 */           insert.setTable(table);
/* 170 */           insert.setInsertFromSelect(true);
/* 171 */           insert.prepare();
/* 172 */           insert.update();
/*     */         } finally {
/* 174 */           this.session.endStatement();
/*     */         } 
/* 176 */         if (bool1) {
/* 177 */           database.lockMeta(this.session);
/* 178 */           for (Column column : table.getColumns()) {
/* 179 */             Sequence sequence = column.getSequence();
/* 180 */             if (sequence != null) {
/* 181 */               sequence.setTemporary(false);
/* 182 */               sequence.flush(this.session);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 187 */     } catch (DbException dbException) {
/*     */       try {
/* 189 */         database.checkPowerOff();
/* 190 */         database.removeSchemaObject(this.session, (SchemaObject)table);
/* 191 */         if (!this.transactional) {
/* 192 */           this.session.commit(true);
/*     */         }
/* 194 */       } catch (Throwable throwable) {
/* 195 */         dbException.addSuppressed(throwable);
/*     */       } 
/* 197 */       throw dbException;
/*     */     } 
/* 199 */     return 0L;
/*     */   }
/*     */   
/*     */   private void generateColumnsFromQuery() {
/* 203 */     int i = this.asQuery.getColumnCount();
/* 204 */     ArrayList<Expression> arrayList = this.asQuery.getExpressions();
/* 205 */     for (byte b = 0; b < i; b++) {
/* 206 */       Expression expression = arrayList.get(b);
/* 207 */       addColumn(new Column(expression.getColumnNameForView(this.session, b), expression.getType()));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setPersistIndexes(boolean paramBoolean) {
/* 212 */     this.data.persistIndexes = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setGlobalTemporary(boolean paramBoolean) {
/* 216 */     this.data.globalTemporary = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOnCommitDrop() {
/* 223 */     this.onCommitDrop = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOnCommitTruncate() {
/* 230 */     this.onCommitTruncate = true;
/*     */   }
/*     */   
/*     */   public void setComment(String paramString) {
/* 234 */     this.comment = paramString;
/*     */   }
/*     */   
/*     */   public void setPersistData(boolean paramBoolean) {
/* 238 */     this.data.persistData = paramBoolean;
/* 239 */     if (!paramBoolean) {
/* 240 */       this.data.persistIndexes = false;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setWithNoData(boolean paramBoolean) {
/* 245 */     this.withNoData = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setTableEngine(String paramString) {
/* 249 */     this.data.tableEngine = paramString;
/*     */   }
/*     */   
/*     */   public void setTableEngineParams(ArrayList<String> paramArrayList) {
/* 253 */     this.data.tableEngineParams = paramArrayList;
/*     */   }
/*     */   
/*     */   public void setHidden(boolean paramBoolean) {
/* 257 */     this.data.isHidden = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 262 */     return 30;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */