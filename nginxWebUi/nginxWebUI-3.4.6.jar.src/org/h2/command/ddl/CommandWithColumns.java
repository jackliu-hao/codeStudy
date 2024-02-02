/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.Sequence;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
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
/*     */ public abstract class CommandWithColumns
/*     */   extends SchemaCommand
/*     */ {
/*     */   private ArrayList<DefineCommand> constraintCommands;
/*     */   private AlterTableAddConstraint primaryKey;
/*     */   
/*     */   protected CommandWithColumns(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  27 */     super(paramSessionLocal, paramSchema);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void addColumn(Column paramColumn);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addConstraintCommand(DefineCommand paramDefineCommand) {
/*  46 */     if (!(paramDefineCommand instanceof CreateIndex)) {
/*  47 */       AlterTableAddConstraint alterTableAddConstraint = (AlterTableAddConstraint)paramDefineCommand;
/*  48 */       if (alterTableAddConstraint.getType() == 6 && 
/*  49 */         setPrimaryKey(alterTableAddConstraint)) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/*  54 */     getConstraintCommands().add(paramDefineCommand);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void changePrimaryKeysToNotNull(ArrayList<Column> paramArrayList) {
/*  64 */     if (this.primaryKey != null) {
/*  65 */       IndexColumn[] arrayOfIndexColumn = this.primaryKey.getIndexColumns();
/*  66 */       for (Column column : paramArrayList) {
/*  67 */         for (IndexColumn indexColumn : arrayOfIndexColumn) {
/*  68 */           if (column.getName().equals(indexColumn.columnName)) {
/*  69 */             column.setNullable(false);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void createConstraints() {
/*  80 */     if (this.constraintCommands != null) {
/*  81 */       for (DefineCommand defineCommand : this.constraintCommands) {
/*  82 */         defineCommand.setTransactional(this.transactional);
/*  83 */         defineCommand.update();
/*     */       } 
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
/*     */   protected ArrayList<Sequence> generateSequences(ArrayList<Column> paramArrayList, boolean paramBoolean) {
/*  98 */     ArrayList<Sequence> arrayList = new ArrayList((paramArrayList == null) ? 0 : paramArrayList.size());
/*  99 */     if (paramArrayList != null) {
/* 100 */       for (Column column : paramArrayList) {
/* 101 */         if (column.hasIdentityOptions()) {
/* 102 */           int i = this.session.getDatabase().allocateObjectId();
/* 103 */           column.initializeSequence(this.session, getSchema(), i, paramBoolean);
/* 104 */           if (!"''".equals(this.session.getDatabase().getCluster())) {
/* 105 */             throw DbException.getUnsupportedException("CLUSTERING && identity columns");
/*     */           }
/*     */         } 
/* 108 */         Sequence sequence = column.getSequence();
/* 109 */         if (sequence != null) {
/* 110 */           arrayList.add(sequence);
/*     */         }
/*     */       } 
/*     */     }
/* 114 */     return arrayList;
/*     */   }
/*     */   
/*     */   private ArrayList<DefineCommand> getConstraintCommands() {
/* 118 */     if (this.constraintCommands == null) {
/* 119 */       this.constraintCommands = new ArrayList<>();
/*     */     }
/* 121 */     return this.constraintCommands;
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
/*     */   private boolean setPrimaryKey(AlterTableAddConstraint paramAlterTableAddConstraint) {
/* 139 */     if (this.primaryKey != null) {
/* 140 */       IndexColumn[] arrayOfIndexColumn1 = this.primaryKey.getIndexColumns();
/* 141 */       IndexColumn[] arrayOfIndexColumn2 = paramAlterTableAddConstraint.getIndexColumns();
/* 142 */       int i = arrayOfIndexColumn2.length;
/* 143 */       if (i != arrayOfIndexColumn1.length) {
/* 144 */         throw DbException.get(90017);
/*     */       }
/* 146 */       for (byte b = 0; b < i; b++) {
/* 147 */         if (!(arrayOfIndexColumn2[b]).columnName.equals((arrayOfIndexColumn1[b]).columnName)) {
/* 148 */           throw DbException.get(90017);
/*     */         }
/*     */       } 
/* 151 */       if (this.primaryKey.getConstraintName() != null) {
/* 152 */         return true;
/*     */       }
/*     */       
/* 155 */       this.constraintCommands.remove(this.primaryKey);
/*     */     } 
/* 157 */     this.primaryKey = paramAlterTableAddConstraint;
/* 158 */     return false;
/*     */   }
/*     */   
/*     */   public AlterTableAddConstraint getPrimaryKey() {
/* 162 */     return this.primaryKey;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CommandWithColumns.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */