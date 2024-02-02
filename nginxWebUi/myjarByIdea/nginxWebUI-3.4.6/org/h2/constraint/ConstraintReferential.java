package org.h2.constraint;

import java.util.ArrayList;
import java.util.HashSet;
import org.h2.command.Command;
import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Parameter;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public class ConstraintReferential extends Constraint {
   private IndexColumn[] columns;
   private IndexColumn[] refColumns;
   private ConstraintActionType deleteAction;
   private ConstraintActionType updateAction;
   private Table refTable;
   private Index index;
   private ConstraintUnique refConstraint;
   private boolean indexOwner;
   private String deleteSQL;
   private String updateSQL;
   private boolean skipOwnTable;

   public ConstraintReferential(Schema var1, int var2, String var3, Table var4) {
      super(var1, var2, var3, var4);
      this.deleteAction = ConstraintActionType.RESTRICT;
      this.updateAction = ConstraintActionType.RESTRICT;
   }

   public Constraint.Type getConstraintType() {
      return Constraint.Type.REFERENTIAL;
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      return this.getCreateSQLForCopy(var1, this.refTable, var2, true);
   }

   public String getCreateSQLForCopy(Table var1, Table var2, String var3, boolean var4) {
      StringBuilder var5 = new StringBuilder("ALTER TABLE ");
      var1.getSQL(var5, 0).append(" ADD CONSTRAINT ");
      if (var1.isHidden()) {
         var5.append("IF NOT EXISTS ");
      }

      var5.append(var3);
      if (this.comment != null) {
         var5.append(" COMMENT ");
         StringUtils.quoteStringSQL(var5, this.comment);
      }

      IndexColumn[] var6 = this.columns;
      IndexColumn[] var7 = this.refColumns;
      var5.append(" FOREIGN KEY(");
      IndexColumn.writeColumns(var5, var6, 0);
      var5.append(')');
      if (var4 && this.indexOwner && var1 == this.table) {
         var5.append(" INDEX ");
         this.index.getSQL(var5, 0);
      }

      var5.append(" REFERENCES ");
      if (this.table == this.refTable) {
         var1.getSQL(var5, 0);
      } else {
         var2.getSQL(var5, 0);
      }

      var5.append('(');
      IndexColumn.writeColumns(var5, var7, 0);
      var5.append(')');
      if (this.deleteAction != ConstraintActionType.RESTRICT) {
         var5.append(" ON DELETE ").append(this.deleteAction.getSqlName());
      }

      if (this.updateAction != ConstraintActionType.RESTRICT) {
         var5.append(" ON UPDATE ").append(this.updateAction.getSqlName());
      }

      return var5.append(" NOCHECK").toString();
   }

   private String getShortDescription(Index var1, SearchRow var2) {
      StringBuilder var3 = (new StringBuilder(this.getName())).append(": ");
      this.table.getSQL(var3, 3).append(" FOREIGN KEY(");
      IndexColumn.writeColumns(var3, this.columns, 3);
      var3.append(") REFERENCES ");
      this.refTable.getSQL(var3, 3).append('(');
      IndexColumn.writeColumns(var3, this.refColumns, 3);
      var3.append(')');
      if (var1 != null && var2 != null) {
         var3.append(" (");
         Column[] var4 = var1.getColumns();
         int var5 = Math.min(this.columns.length, var4.length);

         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var4[var6].getColumnId();
            Value var8 = var2.getValue(var7);
            if (var6 > 0) {
               var3.append(", ");
            }

            var3.append(var8 == null ? "" : var8.toString());
         }

         var3.append(')');
      }

      return var3.toString();
   }

   public String getCreateSQLWithoutIndexes() {
      return this.getCreateSQLForCopy(this.table, this.refTable, this.getSQL(0), false);
   }

   public String getCreateSQL() {
      return this.getCreateSQLForCopy(this.table, this.getSQL(0));
   }

   public void setColumns(IndexColumn[] var1) {
      this.columns = var1;
   }

   public IndexColumn[] getColumns() {
      return this.columns;
   }

   public HashSet<Column> getReferencedColumns(Table var1) {
      HashSet var2 = new HashSet();
      IndexColumn[] var3;
      int var4;
      int var5;
      IndexColumn var6;
      if (var1 == this.table) {
         var3 = this.columns;
         var4 = var3.length;

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var3[var5];
            var2.add(var6.column);
         }
      } else if (var1 == this.refTable) {
         var3 = this.refColumns;
         var4 = var3.length;

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var3[var5];
            var2.add(var6.column);
         }
      }

      return var2;
   }

   public void setRefColumns(IndexColumn[] var1) {
      this.refColumns = var1;
   }

   public IndexColumn[] getRefColumns() {
      return this.refColumns;
   }

   public void setRefTable(Table var1) {
      this.refTable = var1;
      if (var1.isTemporary()) {
         this.setTemporary(true);
      }

   }

   public void setIndex(Index var1, boolean var2) {
      this.index = var1;
      this.indexOwner = var2;
   }

   public void setRefConstraint(ConstraintUnique var1) {
      this.refConstraint = var1;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.table.removeConstraint(this);
      this.refTable.removeConstraint(this);
      if (this.indexOwner) {
         this.table.removeIndexOrTransferOwnership(var1, this.index);
      }

      this.database.removeMeta(var1, this.getId());
      this.refTable = null;
      this.index = null;
      this.refConstraint = null;
      this.columns = null;
      this.refColumns = null;
      this.deleteSQL = null;
      this.updateSQL = null;
      this.table = null;
      this.invalidate();
   }

   public void checkRow(SessionLocal var1, Table var2, Row var3, Row var4) {
      if (this.database.getReferentialIntegrity()) {
         if (this.table.getCheckForeignKeyConstraints() && this.refTable.getCheckForeignKeyConstraints()) {
            if (var2 == this.table && !this.skipOwnTable) {
               this.checkRowOwnTable(var1, var3, var4);
            }

            if (var2 == this.refTable) {
               this.checkRowRefTable(var1, var3, var4);
            }

         }
      }
   }

   private void checkRowOwnTable(SessionLocal var1, Row var2, Row var3) {
      if (var3 != null) {
         boolean var4 = var2 != null;
         IndexColumn[] var5 = this.columns;
         int var6 = var5.length;

         int var7;
         for(var7 = 0; var7 < var6; ++var7) {
            IndexColumn var8 = var5[var7];
            int var9 = var8.column.getColumnId();
            Value var10 = var3.getValue(var9);
            if (var10 == ValueNull.INSTANCE) {
               return;
            }

            if (var4 && !var1.areEqual(var10, var2.getValue(var9))) {
               var4 = false;
            }
         }

         if (!var4) {
            int var11;
            int var16;
            Value var17;
            Column var18;
            if (this.refTable == this.table) {
               boolean var13 = true;
               var6 = 0;

               for(var7 = this.columns.length; var6 < var7; ++var6) {
                  var16 = this.columns[var6].column.getColumnId();
                  var17 = var3.getValue(var16);
                  var18 = this.refColumns[var6].column;
                  var11 = var18.getColumnId();
                  Value var12 = var3.getValue(var11);
                  if (!var1.areEqual(var12, var17)) {
                     var13 = false;
                     break;
                  }
               }

               if (var13) {
                  return;
               }
            }

            Row var14 = this.refTable.getTemplateRow();
            var6 = 0;

            for(var7 = this.columns.length; var6 < var7; ++var6) {
               var16 = this.columns[var6].column.getColumnId();
               var17 = var3.getValue(var16);
               var18 = this.refColumns[var6].column;
               var11 = var18.getColumnId();
               var14.setValue(var11, var18.convert(var1, var17));
            }

            Index var15 = this.refConstraint.getIndex();
            if (!this.existsRow(var1, var15, var14, (Row)null)) {
               throw DbException.get(23506, (String)this.getShortDescription(var15, var14));
            }
         }
      }
   }

   private boolean existsRow(SessionLocal var1, Index var2, SearchRow var3, Row var4) {
      Table var5 = var2.getTable();
      var5.lock(var1, 0);
      Cursor var6 = var2.find(var1, var3, var3);

      boolean var9;
      do {
         SearchRow var7;
         do {
            if (!var6.next()) {
               return false;
            }

            var7 = var6.getSearchRow();
         } while(var4 != null && var7.getKey() == var4.getKey());

         Column[] var8 = var2.getColumns();
         var9 = true;
         int var10 = Math.min(this.columns.length, var8.length);

         for(int var11 = 0; var11 < var10; ++var11) {
            int var12 = var8[var11].getColumnId();
            Value var13 = var3.getValue(var12);
            Value var14 = var7.getValue(var12);
            if (var5.compareValues(var1, var13, var14) != 0) {
               var9 = false;
               break;
            }
         }
      } while(!var9);

      return true;
   }

   private boolean isEqual(Row var1, Row var2) {
      return this.refConstraint.getIndex().compareRows(var1, var2) == 0;
   }

   private void checkRow(SessionLocal var1, Row var2) {
      SearchRow var3 = this.table.getRowFactory().createRow();
      int var4 = 0;

      for(int var5 = this.columns.length; var4 < var5; ++var4) {
         Column var6 = this.refColumns[var4].column;
         int var7 = var6.getColumnId();
         Column var8 = this.columns[var4].column;
         Value var9 = var8.convert(var1, var2.getValue(var7));
         if (var9 == ValueNull.INSTANCE) {
            return;
         }

         var3.setValue(var8.getColumnId(), var9);
      }

      Row var10 = this.refTable == this.table ? var2 : null;
      if (this.existsRow(var1, this.index, var3, var10)) {
         throw DbException.get(23503, (String)this.getShortDescription(this.index, var3));
      }
   }

   private void checkRowRefTable(SessionLocal var1, Row var2, Row var3) {
      if (var2 != null) {
         if (var3 == null || !this.isEqual(var2, var3)) {
            if (var3 == null) {
               if (this.deleteAction == ConstraintActionType.RESTRICT) {
                  this.checkRow(var1, var2);
               } else {
                  int var4 = this.deleteAction == ConstraintActionType.CASCADE ? 0 : this.columns.length;
                  Prepared var5 = this.getDelete(var1);
                  this.setWhere(var5, var4, var2);
                  this.updateWithSkipCheck(var5);
               }
            } else if (this.updateAction == ConstraintActionType.RESTRICT) {
               this.checkRow(var1, var2);
            } else {
               Prepared var10 = this.getUpdate(var1);
               if (this.updateAction == ConstraintActionType.CASCADE) {
                  ArrayList var11 = var10.getParameters();
                  int var6 = 0;

                  for(int var7 = this.columns.length; var6 < var7; ++var6) {
                     Parameter var8 = (Parameter)var11.get(var6);
                     Column var9 = this.refColumns[var6].column;
                     var8.setValue(var3.getValue(var9.getColumnId()));
                  }
               }

               this.setWhere(var10, this.columns.length, var2);
               this.updateWithSkipCheck(var10);
            }

         }
      }
   }

   private void updateWithSkipCheck(Prepared var1) {
      try {
         this.skipOwnTable = true;
         var1.update();
      } finally {
         this.skipOwnTable = false;
      }

   }

   private void setWhere(Prepared var1, int var2, Row var3) {
      int var4 = 0;

      for(int var5 = this.refColumns.length; var4 < var5; ++var4) {
         int var6 = this.refColumns[var4].column.getColumnId();
         Value var7 = var3.getValue(var6);
         ArrayList var8 = var1.getParameters();
         Parameter var9 = (Parameter)var8.get(var2 + var4);
         var9.setValue(var7);
      }

   }

   public ConstraintActionType getDeleteAction() {
      return this.deleteAction;
   }

   public void setDeleteAction(ConstraintActionType var1) {
      if (var1 != this.deleteAction || this.deleteSQL != null) {
         if (this.deleteAction != ConstraintActionType.RESTRICT) {
            throw DbException.get(90045, "ON DELETE");
         } else {
            this.deleteAction = var1;
            this.buildDeleteSQL();
         }
      }
   }

   public void updateOnTableColumnRename() {
      if (this.deleteAction != null) {
         this.deleteSQL = null;
         this.buildDeleteSQL();
      }

      if (this.updateAction != null) {
         this.updateSQL = null;
         this.buildUpdateSQL();
      }

   }

   private void buildDeleteSQL() {
      if (this.deleteAction != ConstraintActionType.RESTRICT) {
         StringBuilder var1 = new StringBuilder();
         if (this.deleteAction == ConstraintActionType.CASCADE) {
            var1.append("DELETE FROM ");
            this.table.getSQL(var1, 0);
         } else {
            this.appendUpdate(var1);
         }

         this.appendWhere(var1);
         this.deleteSQL = var1.toString();
      }
   }

   private Prepared getUpdate(SessionLocal var1) {
      return this.prepare(var1, this.updateSQL, this.updateAction);
   }

   private Prepared getDelete(SessionLocal var1) {
      return this.prepare(var1, this.deleteSQL, this.deleteAction);
   }

   public ConstraintActionType getUpdateAction() {
      return this.updateAction;
   }

   public void setUpdateAction(ConstraintActionType var1) {
      if (var1 != this.updateAction || this.updateSQL != null) {
         if (this.updateAction != ConstraintActionType.RESTRICT) {
            throw DbException.get(90045, "ON UPDATE");
         } else {
            this.updateAction = var1;
            this.buildUpdateSQL();
         }
      }
   }

   private void buildUpdateSQL() {
      if (this.updateAction != ConstraintActionType.RESTRICT) {
         StringBuilder var1 = new StringBuilder();
         this.appendUpdate(var1);
         this.appendWhere(var1);
         this.updateSQL = var1.toString();
      }
   }

   public void rebuild() {
      this.buildUpdateSQL();
      this.buildDeleteSQL();
   }

   private Prepared prepare(SessionLocal var1, String var2, ConstraintActionType var3) {
      Prepared var4 = var1.prepare(var2);
      if (var3 != ConstraintActionType.CASCADE) {
         ArrayList var5 = var4.getParameters();
         int var6 = 0;

         for(int var7 = this.columns.length; var6 < var7; ++var6) {
            Column var8 = this.columns[var6].column;
            Parameter var9 = (Parameter)var5.get(var6);
            Object var10;
            if (var3 == ConstraintActionType.SET_NULL) {
               var10 = ValueNull.INSTANCE;
            } else {
               Expression var11 = var8.getEffectiveDefaultExpression();
               if (var11 == null) {
                  throw DbException.get(23507, (String)var8.getName());
               }

               var10 = var11.getValue(var1);
            }

            var9.setValue((Value)var10);
         }
      }

      return var4;
   }

   private void appendUpdate(StringBuilder var1) {
      var1.append("UPDATE ");
      this.table.getSQL(var1, 0).append(" SET ");
      IndexColumn.writeColumns(var1, this.columns, ", ", "=?", Integer.MIN_VALUE);
   }

   private void appendWhere(StringBuilder var1) {
      var1.append(" WHERE ");
      IndexColumn.writeColumns(var1, this.columns, " AND ", "=?", Integer.MIN_VALUE);
   }

   public Table getRefTable() {
      return this.refTable;
   }

   public boolean usesIndex(Index var1) {
      return var1 == this.index;
   }

   public void setIndexOwner(Index var1) {
      if (this.index == var1) {
         this.indexOwner = true;
      } else {
         throw DbException.getInternalError(var1 + " " + this.toString());
      }
   }

   public boolean isBefore() {
      return false;
   }

   public void checkExistingData(SessionLocal var1) {
      if (!var1.getDatabase().isStarting()) {
         StringBuilder var2 = new StringBuilder("SELECT 1 FROM (SELECT ");
         IndexColumn.writeColumns(var2, this.columns, Integer.MIN_VALUE);
         var2.append(" FROM ");
         this.table.getSQL(var2, 0).append(" WHERE ");
         IndexColumn.writeColumns(var2, this.columns, " AND ", " IS NOT NULL ", Integer.MIN_VALUE);
         var2.append(" ORDER BY ");
         IndexColumn.writeColumns(var2, this.columns, 0);
         var2.append(") C WHERE NOT EXISTS(SELECT 1 FROM ");
         this.refTable.getSQL(var2, 0).append(" P WHERE ");
         int var3 = 0;

         for(int var4 = this.columns.length; var3 < var4; ++var3) {
            if (var3 > 0) {
               var2.append(" AND ");
            }

            var2.append("C.");
            this.columns[var3].column.getSQL(var2, 0).append('=').append("P.");
            this.refColumns[var3].column.getSQL(var2, 0);
         }

         var2.append(')');
         var1.startStatementWithinTransaction((Command)null);

         try {
            ResultInterface var8 = var1.prepare(var2.toString()).query(1L);
            if (var8.next()) {
               throw DbException.get(23506, (String)this.getShortDescription((Index)null, (SearchRow)null));
            }
         } finally {
            var1.endStatement();
         }

      }
   }

   public Index getIndex() {
      return this.index;
   }

   public ConstraintUnique getReferencedConstraint() {
      return this.refConstraint;
   }
}
