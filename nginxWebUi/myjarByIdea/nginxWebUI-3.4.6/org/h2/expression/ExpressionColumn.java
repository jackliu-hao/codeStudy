package org.h2.expression;

import org.h2.command.query.Select;
import org.h2.command.query.SelectGroups;
import org.h2.command.query.SelectListColumnResolver;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.condition.Comparison;
import org.h2.expression.function.CurrentDateTimeValueFunction;
import org.h2.index.IndexCondition;
import org.h2.message.DbException;
import org.h2.schema.Constant;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueReal;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTinyint;

public final class ExpressionColumn extends Expression {
   private final Database database;
   private final String schemaName;
   private final String tableAlias;
   private final String columnName;
   private final boolean rowId;
   private final boolean quotedName;
   private ColumnResolver columnResolver;
   private int queryLevel;
   private Column column;

   public ExpressionColumn(Database var1, Column var2) {
      this.database = var1;
      this.column = var2;
      this.columnName = this.tableAlias = this.schemaName = null;
      this.rowId = var2.isRowId();
      this.quotedName = true;
   }

   public ExpressionColumn(Database var1, String var2, String var3, String var4) {
      this(var1, var2, var3, var4, true);
   }

   public ExpressionColumn(Database var1, String var2, String var3, String var4, boolean var5) {
      this.database = var1;
      this.schemaName = var2;
      this.tableAlias = var3;
      this.columnName = var4;
      this.rowId = false;
      this.quotedName = var5;
   }

   public ExpressionColumn(Database var1, String var2, String var3) {
      this.database = var1;
      this.schemaName = var2;
      this.tableAlias = var3;
      this.columnName = "_ROWID_";
      this.quotedName = this.rowId = true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      if (this.schemaName != null) {
         ParserUtil.quoteIdentifier(var1, this.schemaName, var2).append('.');
      }

      if (this.tableAlias != null) {
         ParserUtil.quoteIdentifier(var1, this.tableAlias, var2).append('.');
      }

      if (this.column != null) {
         if (this.columnResolver != null && this.columnResolver.hasDerivedColumnList()) {
            ParserUtil.quoteIdentifier(var1, this.columnResolver.getColumnName(this.column), var2);
         } else {
            this.column.getSQL(var1, var2);
         }
      } else if (this.rowId) {
         var1.append(this.columnName);
      } else {
         ParserUtil.quoteIdentifier(var1, this.columnName, var2);
      }

      return var1;
   }

   public TableFilter getTableFilter() {
      return this.columnResolver == null ? null : this.columnResolver.getTableFilter();
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      if (this.tableAlias == null || this.database.equalsIdentifiers(this.tableAlias, var1.getTableAlias())) {
         if (this.schemaName == null || this.database.equalsIdentifiers(this.schemaName, var1.getSchemaName())) {
            Column var4;
            if (this.rowId) {
               var4 = var1.getRowIdColumn();
               if (var4 != null) {
                  this.mapColumn(var1, var4, var2);
               }

            } else {
               var4 = var1.findColumn(this.columnName);
               if (var4 != null) {
                  this.mapColumn(var1, var4, var2);
               } else {
                  Column[] var5 = var1.getSystemColumns();

                  for(int var6 = 0; var5 != null && var6 < var5.length; ++var6) {
                     var4 = var5[var6];
                     if (this.database.equalsIdentifiers(this.columnName, var4.getName())) {
                        this.mapColumn(var1, var4, var2);
                        return;
                     }
                  }

               }
            }
         }
      }
   }

   private void mapColumn(ColumnResolver var1, Column var2, int var3) {
      if (this.columnResolver == null) {
         this.queryLevel = var3;
         this.column = var2;
         this.columnResolver = var1;
      } else if (this.queryLevel == var3 && this.columnResolver != var1 && !(var1 instanceof SelectListColumnResolver)) {
         throw DbException.get(90059, this.columnName);
      }

   }

   public Expression optimize(SessionLocal var1) {
      if (this.columnResolver == null) {
         Schema var2 = var1.getDatabase().findSchema(this.tableAlias == null ? var1.getCurrentSchemaName() : this.tableAlias);
         if (var2 != null) {
            Constant var3 = var2.findConstant(this.columnName);
            if (var3 != null) {
               return var3.getValue();
            }
         }

         return this.optimizeOther();
      } else {
         return this.columnResolver.optimize(this, this.column);
      }
   }

   private Expression optimizeOther() {
      if (this.tableAlias == null && !this.quotedName) {
         switch (StringUtils.toUpperEnglish(this.columnName)) {
            case "SYSDATE":
            case "TODAY":
               return new CurrentDateTimeValueFunction(0, -1);
            case "SYSTIME":
               return new CurrentDateTimeValueFunction(2, -1);
            case "SYSTIMESTAMP":
               return new CurrentDateTimeValueFunction(3, -1);
         }
      }

      throw this.getColumnException(42122);
   }

   public DbException getColumnException(int var1) {
      String var2 = this.columnName;
      if (this.tableAlias != null) {
         if (this.schemaName != null) {
            var2 = this.schemaName + '.' + this.tableAlias + '.' + var2;
         } else {
            var2 = this.tableAlias + '.' + var2;
         }
      }

      return DbException.get(var1, var2);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      Select var3 = this.columnResolver.getSelect();
      if (var3 == null) {
         throw DbException.get(90016, this.getTraceSQL());
      } else if (var2 != 0) {
         SelectGroups var4 = var3.getGroupDataIfCurrent(false);
         if (var4 != null) {
            Value var5 = (Value)var4.getCurrentGroupExprData(this);
            if (var5 == null) {
               var4.setCurrentGroupExprData(this, this.columnResolver.getValue(this.column));
            } else if (!var3.isGroupWindowStage2() && !var1.areEqual(this.columnResolver.getValue(this.column), var5)) {
               throw DbException.get(90016, this.getTraceSQL());
            }

         }
      }
   }

   public Value getValue(SessionLocal var1) {
      Select var2 = this.columnResolver.getSelect();
      if (var2 != null) {
         SelectGroups var3 = var2.getGroupDataIfCurrent(false);
         if (var3 != null) {
            Value var4 = (Value)var3.getCurrentGroupExprData(this);
            if (var4 != null) {
               return var4;
            }

            if (var2.isGroupWindowStage2()) {
               throw DbException.get(90016, this.getTraceSQL());
            }
         }
      }

      Value var5 = this.columnResolver.getValue(this.column);
      if (var5 == null) {
         if (var2 == null) {
            throw DbException.get(23502, (String)this.getTraceSQL());
         } else {
            throw DbException.get(90016, this.getTraceSQL());
         }
      } else {
         return var5;
      }
   }

   public TypeInfo getType() {
      return this.column != null ? this.column.getType() : (this.rowId ? TypeInfo.TYPE_BIGINT : TypeInfo.TYPE_UNKNOWN);
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
   }

   public Column getColumn() {
      return this.column;
   }

   public String getOriginalColumnName() {
      return this.columnName;
   }

   public String getOriginalTableAliasName() {
      return this.tableAlias;
   }

   public String getColumnName(SessionLocal var1, int var2) {
      if (this.column != null) {
         return this.columnResolver != null ? this.columnResolver.getColumnName(this.column) : this.column.getName();
      } else {
         return this.columnName;
      }
   }

   public String getSchemaName() {
      Table var1 = this.column.getTable();
      return var1 == null ? null : var1.getSchema().getName();
   }

   public String getTableName() {
      Table var1 = this.column.getTable();
      return var1 == null ? null : var1.getName();
   }

   public String getAlias(SessionLocal var1, int var2) {
      if (this.column != null) {
         return this.columnResolver != null ? this.columnResolver.getColumnName(this.column) : this.column.getName();
      } else {
         return this.tableAlias != null ? this.tableAlias + '.' + this.columnName : this.columnName;
      }
   }

   public String getColumnNameForView(SessionLocal var1, int var2) {
      return this.getAlias(var1, var2);
   }

   public boolean isIdentity() {
      return this.column.isIdentity();
   }

   public int getNullable() {
      return this.column.isNullable() ? 1 : 0;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 0:
            return this.queryLevel < var1.getQueryLevel();
         case 1:
            return false;
         case 3:
            if (var1.getQueryLevel() < this.queryLevel) {
               return true;
            } else {
               if (this.getTableFilter() == null) {
                  return false;
               }

               return this.getTableFilter().isEvaluatable();
            }
         case 4:
            var1.addDataModificationId(this.column.getTable().getMaxDataModificationId());
            return true;
         case 6:
            return this.columnResolver != var1.getResolver();
         case 7:
            if (this.column != null) {
               var1.addDependency(this.column.getTable());
            }

            return true;
         case 9:
            if (this.column == null) {
               throw DbException.get(42122, (String)this.getTraceSQL());
            }

            var1.addColumn1(this.column);
            return true;
         case 10:
            if (this.column == null) {
               throw DbException.get(42122, (String)this.getTraceSQL());
            }

            var1.addColumn2(this.column);
            return true;
         case 11:
            if (this.column == null) {
               throw DbException.get(42122, (String)this.getTraceSQL());
            } else if (var1.getColumnResolvers().contains(this.columnResolver)) {
               int var2 = var1.getQueryLevel();
               if (var2 > 0) {
                  if (this.queryLevel > 0) {
                     --this.queryLevel;
                     return true;
                  }

                  throw DbException.getInternalError("queryLevel=0");
               }

               return this.queryLevel > 0;
            }
         case 2:
         case 5:
         case 8:
         default:
            return true;
      }
   }

   public int getCost() {
      return 2;
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      TableFilter var3 = this.getTableFilter();
      if (var2 == var3 && this.column.getType().getValueType() == 8) {
         var2.addIndexCondition(IndexCondition.get(0, this, ValueExpression.TRUE));
      }

   }

   public Expression getNotIfPossible(SessionLocal var1) {
      Expression var2 = this.optimize(var1);
      if (var2 != this) {
         return var2.getNotIfPossible(var1);
      } else {
         Object var3;
         switch (this.column.getType().getValueType()) {
            case 8:
               var3 = ValueBoolean.FALSE;
               break;
            case 9:
               var3 = ValueTinyint.get((byte)0);
               break;
            case 10:
               var3 = ValueSmallint.get((short)0);
               break;
            case 11:
               var3 = ValueInteger.get(0);
               break;
            case 12:
               var3 = ValueBigint.get(0L);
               break;
            case 13:
               var3 = ValueNumeric.ZERO;
               break;
            case 14:
               var3 = ValueReal.ZERO;
               break;
            case 15:
               var3 = ValueDouble.ZERO;
               break;
            case 16:
               var3 = ValueDecfloat.ZERO;
               break;
            default:
               return null;
         }

         return new Comparison(0, this, ValueExpression.get((Value)var3), false);
      }
   }
}
