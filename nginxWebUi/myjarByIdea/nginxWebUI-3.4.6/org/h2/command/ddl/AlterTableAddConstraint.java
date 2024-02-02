package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.command.query.Select;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintActionType;
import org.h2.constraint.ConstraintCheck;
import org.h2.constraint.ConstraintReferential;
import org.h2.constraint.ConstraintUnique;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.IndexHints;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.value.DataType;

public class AlterTableAddConstraint extends AlterTable {
   private final int type;
   private String constraintName;
   private IndexColumn[] indexColumns;
   private ConstraintActionType deleteAction;
   private ConstraintActionType updateAction;
   private Schema refSchema;
   private String refTableName;
   private IndexColumn[] refIndexColumns;
   private Expression checkExpression;
   private Index index;
   private Index refIndex;
   private String comment;
   private boolean checkExisting;
   private boolean primaryKeyHash;
   private final boolean ifNotExists;
   private final ArrayList<Index> createdIndexes;
   private ConstraintUnique createdUniqueConstraint;

   public AlterTableAddConstraint(SessionLocal var1, Schema var2, int var3, boolean var4) {
      super(var1, var2);
      this.deleteAction = ConstraintActionType.RESTRICT;
      this.updateAction = ConstraintActionType.RESTRICT;
      this.createdIndexes = new ArrayList();
      this.ifNotExists = var4;
      this.type = var3;
   }

   private String generateConstraintName(Table var1) {
      if (this.constraintName == null) {
         this.constraintName = this.getSchema().getUniqueConstraintName(this.session, var1);
      }

      return this.constraintName;
   }

   public long update(Table var1) {
      long var2;
      try {
         var2 = (long)this.tryUpdate(var1);
      } catch (DbException var10) {
         try {
            if (this.createdUniqueConstraint != null) {
               Index var3 = this.createdUniqueConstraint.getIndex();
               this.session.getDatabase().removeSchemaObject(this.session, this.createdUniqueConstraint);
               this.createdIndexes.remove(var3);
            }

            Iterator var12 = this.createdIndexes.iterator();

            while(var12.hasNext()) {
               Index var4 = (Index)var12.next();
               this.session.getDatabase().removeSchemaObject(this.session, var4);
            }
         } catch (Throwable var9) {
            var10.addSuppressed(var9);
         }

         throw var10;
      } finally {
         this.getSchema().freeUniqueName(this.constraintName);
      }

      return var2;
   }

   private int tryUpdate(Table var1) {
      if (this.constraintName != null && this.getSchema().findConstraint(this.session, this.constraintName) != null) {
         if (this.ifNotExists) {
            return 0;
         }

         if (!this.session.isQuirksMode()) {
            throw DbException.get(90045, this.constraintName);
         }

         this.constraintName = null;
      }

      Database var2 = this.session.getDatabase();
      var2.lockMeta(this.session);
      var1.lock(this.session, 2);
      Object var3;
      int var5;
      int var7;
      int var21;
      ConstraintUnique var22;
      Column var27;
      switch (this.type) {
         case 3:
            int var16 = this.getObjectId();
            String var28 = this.generateConstraintName(var1);
            ConstraintCheck var26 = new ConstraintCheck(this.getSchema(), var16, var28, var1);
            TableFilter var31 = new TableFilter(this.session, var1, (String)null, false, (Select)null, 0, (IndexHints)null);
            this.checkExpression.mapColumns(var31, 0, 0);
            this.checkExpression = this.checkExpression.optimize(this.session);
            var26.setExpression(this.checkExpression);
            var26.setTableFilter(var31);
            var3 = var26;
            if (this.checkExisting) {
               var26.checkExistingData(this.session);
            }
            break;
         case 4:
            if (this.indexColumns != null) {
               IndexColumn.mapColumns(this.indexColumns, var1);
            } else {
               Column[] var15 = var1.getColumns();
               var5 = var15.length;
               ArrayList var24 = new ArrayList(var5);

               for(var7 = 0; var7 < var5; ++var7) {
                  var27 = var15[var7];
                  if (var27.getVisible()) {
                     IndexColumn var32 = new IndexColumn(var27.getName());
                     var32.column = var27;
                     var24.add(var32);
                  }
               }

               if (var24.isEmpty()) {
                  throw DbException.get(42000, (String)"UNIQUE(VALUE) on table without columns");
               }

               this.indexColumns = (IndexColumn[])var24.toArray(new IndexColumn[0]);
            }

            var3 = this.createUniqueConstraint(var1, this.index, this.indexColumns, false);
            break;
         case 5:
            Table var14 = this.refSchema.resolveTableOrView(this.session, this.refTableName);
            if (var14 == null) {
               throw DbException.get(42102, (String)this.refTableName);
            }

            if (var14 != var1) {
               this.session.getUser().checkTableRight(var14, 32);
            }

            if (!var14.canReference()) {
               StringBuilder var25 = new StringBuilder("Reference ");
               var14.getSQL(var25, 3);
               throw DbException.getUnsupportedException(var25.toString());
            }

            boolean var20 = false;
            IndexColumn.mapColumns(this.indexColumns, var1);
            if (this.refIndexColumns == null) {
               this.refIndexColumns = var14.getPrimaryKey().getIndexColumns();
            } else {
               IndexColumn.mapColumns(this.refIndexColumns, var14);
            }

            var21 = this.indexColumns.length;
            if (this.refIndexColumns.length != var21) {
               throw DbException.get(21002);
            }

            IndexColumn[] var23 = this.indexColumns;
            int var8 = var23.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               IndexColumn var10 = var23[var9];
               Column var11 = var10.column;
               if (var11.isGeneratedAlways()) {
                  switch (this.deleteAction) {
                     case SET_DEFAULT:
                     case SET_NULL:
                        throw DbException.get(90155, var11.getSQLWithTable(new StringBuilder(), 3).toString(), "ON DELETE " + this.deleteAction.getSqlName());
                     default:
                        switch (this.updateAction) {
                           case SET_DEFAULT:
                           case SET_NULL:
                           case CASCADE:
                              throw DbException.get(90155, var11.getSQLWithTable(new StringBuilder(), 3).toString(), "ON UPDATE " + this.updateAction.getSqlName());
                        }
                  }
               }
            }

            for(var7 = 0; var7 < var21; ++var7) {
               var27 = this.indexColumns[var7].column;
               Column var29 = this.refIndexColumns[var7].column;
               if (!DataType.areStableComparable(var27.getType(), var29.getType())) {
                  throw DbException.get(90153, var27.getCreateSQL(), var29.getCreateSQL());
               }
            }

            var22 = getUniqueConstraint(var14, this.refIndexColumns);
            if (var22 == null && !this.session.isQuirksMode() && !this.session.getMode().createUniqueConstraintForReferencedColumns) {
               throw DbException.get(90057, IndexColumn.writeColumns(new StringBuilder("PRIMARY KEY | UNIQUE ("), this.refIndexColumns, 3).append(')').toString());
            }

            if (this.index != null && canUseIndex(this.index, var1, this.indexColumns, false)) {
               var20 = true;
               this.index.getIndexType().setBelongsToConstraint(true);
            } else {
               this.index = getIndex(var1, this.indexColumns, false);
               if (this.index == null) {
                  this.index = this.createIndex(var1, this.indexColumns, false);
                  var20 = true;
               }
            }

            var8 = this.getObjectId();
            String var30 = this.generateConstraintName(var1);
            ConstraintReferential var33 = new ConstraintReferential(this.getSchema(), var8, var30, var1);
            var33.setColumns(this.indexColumns);
            var33.setIndex(this.index, var20);
            var33.setRefTable(var14);
            var33.setRefColumns(this.refIndexColumns);
            if (var22 == null) {
               var22 = this.createUniqueConstraint(var14, this.refIndex, this.refIndexColumns, true);
               this.addConstraintToTable(var2, var14, var22);
               this.createdUniqueConstraint = var22;
            }

            var33.setRefConstraint(var22);
            if (this.checkExisting) {
               var33.checkExistingData(this.session);
            }

            var14.addConstraint(var33);
            var33.setDeleteAction(this.deleteAction);
            var33.setUpdateAction(this.updateAction);
            var3 = var33;
            break;
         case 6:
            IndexColumn.mapColumns(this.indexColumns, var1);
            this.index = var1.findPrimaryKey();
            ArrayList var4 = var1.getConstraints();

            for(var5 = 0; var4 != null && var5 < var4.size(); ++var5) {
               Constraint var6 = (Constraint)var4.get(var5);
               if (Constraint.Type.PRIMARY_KEY == var6.getConstraintType()) {
                  throw DbException.get(90017);
               }
            }

            String var19;
            if (this.index != null) {
               IndexColumn[] var18 = this.index.getIndexColumns();
               if (var18.length != this.indexColumns.length) {
                  throw DbException.get(90017);
               }

               for(var21 = 0; var21 < var18.length; ++var21) {
                  if (var18[var21].column != this.indexColumns[var21].column) {
                     throw DbException.get(90017);
                  }
               }
            } else {
               IndexType var17 = IndexType.createPrimaryKey(var1.isPersistIndexes(), this.primaryKeyHash);
               var19 = var1.getSchema().getUniqueIndexName(this.session, var1, "PRIMARY_KEY_");
               var7 = this.session.getDatabase().allocateObjectId();

               try {
                  this.index = var1.addIndex(this.session, var19, var7, this.indexColumns, this.indexColumns.length, var17, true, (String)null);
               } finally {
                  this.getSchema().freeUniqueName(var19);
               }
            }

            this.index.getIndexType().setBelongsToConstraint(true);
            var5 = this.getObjectId();
            var19 = this.generateConstraintName(var1);
            var22 = new ConstraintUnique(this.getSchema(), var5, var19, var1, true);
            var22.setColumns(this.indexColumns);
            var22.setIndex(this.index, true);
            var3 = var22;
            break;
         default:
            throw DbException.getInternalError("type=" + this.type);
      }

      ((Constraint)var3).setComment(this.comment);
      this.addConstraintToTable(var2, var1, (Constraint)var3);
      return 0;
   }

   private ConstraintUnique createUniqueConstraint(Table var1, Index var2, IndexColumn[] var3, boolean var4) {
      boolean var5 = false;
      if (var2 != null && canUseIndex(var2, var1, var3, true)) {
         var5 = true;
         var2.getIndexType().setBelongsToConstraint(true);
      } else {
         var2 = getIndex(var1, var3, true);
         if (var2 == null) {
            var2 = this.createIndex(var1, var3, true);
            var5 = true;
         }
      }

      Schema var8 = var1.getSchema();
      int var6;
      String var7;
      if (var4) {
         var6 = this.session.getDatabase().allocateObjectId();

         try {
            var8.reserveUniqueName(this.constraintName);
            var7 = var8.getUniqueConstraintName(this.session, var1);
         } finally {
            var8.freeUniqueName(this.constraintName);
         }
      } else {
         var6 = this.getObjectId();
         var7 = this.generateConstraintName(var1);
      }

      ConstraintUnique var9 = new ConstraintUnique(var8, var6, var7, var1, false);
      var9.setColumns(var3);
      var9.setIndex(var2, var5);
      return var9;
   }

   private void addConstraintToTable(Database var1, Table var2, Constraint var3) {
      if (var2.isTemporary() && !var2.isGlobalTemporary()) {
         this.session.addLocalTempTableConstraint(var3);
      } else {
         var1.addSchemaObject(this.session, var3);
      }

      var2.addConstraint(var3);
   }

   private Index createIndex(Table var1, IndexColumn[] var2, boolean var3) {
      int var4 = this.session.getDatabase().allocateObjectId();
      IndexType var5;
      if (var3) {
         var5 = IndexType.createUnique(var1.isPersistIndexes(), false);
      } else {
         var5 = IndexType.createNonUnique(var1.isPersistIndexes());
      }

      var5.setBelongsToConstraint(true);
      String var6 = this.constraintName == null ? "CONSTRAINT" : this.constraintName;
      String var7 = var1.getSchema().getUniqueIndexName(this.session, var1, var6 + "_INDEX_");

      Index var9;
      try {
         Index var8 = var1.addIndex(this.session, var7, var4, var2, var3 ? var2.length : 0, var5, true, (String)null);
         this.createdIndexes.add(var8);
         var9 = var8;
      } finally {
         this.getSchema().freeUniqueName(var7);
      }

      return var9;
   }

   public void setDeleteAction(ConstraintActionType var1) {
      this.deleteAction = var1;
   }

   public void setUpdateAction(ConstraintActionType var1) {
      this.updateAction = var1;
   }

   private static ConstraintUnique getUniqueConstraint(Table var0, IndexColumn[] var1) {
      ArrayList var2 = var0.getConstraints();
      if (var2 != null) {
         Iterator var3 = var2.iterator();

         Constraint var4;
         Constraint.Type var5;
         do {
            do {
               do {
                  if (!var3.hasNext()) {
                     return null;
                  }

                  var4 = (Constraint)var3.next();
               } while(var4.getTable() != var0);

               var5 = var4.getConstraintType();
            } while(var5 != Constraint.Type.PRIMARY_KEY && var5 != Constraint.Type.UNIQUE);
         } while(!canUseIndex(var4.getIndex(), var0, var1, true));

         return (ConstraintUnique)var4;
      } else {
         return null;
      }
   }

   private static Index getIndex(Table var0, IndexColumn[] var1, boolean var2) {
      ArrayList var3 = var0.getIndexes();
      Index var4 = null;
      if (var3 != null) {
         Iterator var5 = var3.iterator();

         while(true) {
            Index var6;
            do {
               do {
                  if (!var5.hasNext()) {
                     return var4;
                  }

                  var6 = (Index)var5.next();
               } while(!canUseIndex(var6, var0, var1, var2));
            } while(var4 != null && var6.getIndexColumns().length >= var4.getIndexColumns().length);

            var4 = var6;
         }
      } else {
         return var4;
      }
   }

   private static boolean canUseIndex(Index var0, Table var1, IndexColumn[] var2, boolean var3) {
      if (var0.getTable() != var1) {
         return false;
      } else {
         int var4;
         if (var3) {
            var4 = var0.getUniqueColumnCount();
            if (var4 != var2.length) {
               return false;
            }
         } else if (var0.getCreateSQL() == null || (var4 = var0.getColumns().length) != var2.length) {
            return false;
         }

         IndexColumn[] var5 = var2;
         int var6 = var2.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            IndexColumn var8 = var5[var7];
            int var9 = var0.getColumnIndex(var8.column);
            if (var9 < 0 || var9 >= var4) {
               return false;
            }
         }

         return true;
      }
   }

   public void setConstraintName(String var1) {
      this.constraintName = var1;
   }

   public String getConstraintName() {
      return this.constraintName;
   }

   public int getType() {
      return this.type;
   }

   public void setCheckExpression(Expression var1) {
      this.checkExpression = var1;
   }

   public void setIndexColumns(IndexColumn[] var1) {
      this.indexColumns = var1;
   }

   public IndexColumn[] getIndexColumns() {
      return this.indexColumns;
   }

   public void setRefTableName(Schema var1, String var2) {
      this.refSchema = var1;
      this.refTableName = var2;
   }

   public void setRefIndexColumns(IndexColumn[] var1) {
      this.refIndexColumns = var1;
   }

   public void setIndex(Index var1) {
      this.index = var1;
   }

   public void setRefIndex(Index var1) {
      this.refIndex = var1;
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public void setCheckExisting(boolean var1) {
      this.checkExisting = var1;
   }

   public void setPrimaryKeyHash(boolean var1) {
      this.primaryKeyHash = var1;
   }
}
