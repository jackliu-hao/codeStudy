package org.h2.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.command.query.Select;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.condition.ConditionAndOr;
import org.h2.index.Index;
import org.h2.index.IndexCondition;
import org.h2.index.IndexCursor;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTinyint;

public class TableFilter implements ColumnResolver {
   private static final int BEFORE_FIRST = 0;
   private static final int FOUND = 1;
   private static final int AFTER_LAST = 2;
   private static final int NULL_ROW = 3;
   public static final Comparator<TableFilter> ORDER_IN_FROM_COMPARATOR = Comparator.comparing(TableFilter::getOrderInFrom);
   private static final TableFilterVisitor JOI_VISITOR = (var0) -> {
      var0.joinOuterIndirect = true;
   };
   protected boolean joinOuterIndirect;
   private SessionLocal session;
   private final Table table;
   private final Select select;
   private String alias;
   private Index index;
   private final IndexHints indexHints;
   private int[] masks;
   private int scanCount;
   private boolean evaluatable;
   private boolean used;
   private final IndexCursor cursor;
   private final ArrayList<IndexCondition> indexConditions = Utils.newSmallArrayList();
   private Expression filterCondition;
   private Expression joinCondition;
   private SearchRow currentSearchRow;
   private Row current;
   private int state;
   private TableFilter join;
   private boolean joinOuter;
   private TableFilter nestedJoin;
   private LinkedHashMap<Column, Column> commonJoinColumns;
   private TableFilter commonJoinColumnsFilter;
   private ArrayList<Column> commonJoinColumnsToExclude;
   private boolean foundOne;
   private Expression fullCondition;
   private final int hashCode;
   private final int orderInFrom;
   private LinkedHashMap<Column, String> derivedColumnMap;

   public TableFilter(SessionLocal var1, Table var2, String var3, boolean var4, Select var5, int var6, IndexHints var7) {
      this.session = var1;
      this.table = var2;
      this.alias = var3;
      this.select = var5;
      this.cursor = new IndexCursor();
      if (!var4) {
         var1.getUser().checkTableRight(var2, 1);
      }

      this.hashCode = var1.nextObjectId();
      this.orderInFrom = var6;
      this.indexHints = var7;
   }

   public int getOrderInFrom() {
      return this.orderInFrom;
   }

   public IndexCursor getIndexCursor() {
      return this.cursor;
   }

   public Select getSelect() {
      return this.select;
   }

   public Table getTable() {
      return this.table;
   }

   public void lock(SessionLocal var1) {
      this.table.lock(var1, 0);
      if (this.join != null) {
         this.join.lock(var1);
      }

   }

   public PlanItem getBestPlanItem(SessionLocal var1, TableFilter[] var2, int var3, AllColumnsForPlan var4) {
      PlanItem var5 = null;
      SortOrder var6 = null;
      if (this.select != null) {
         var6 = this.select.getSortOrder();
      }

      if (this.indexConditions.isEmpty()) {
         var5 = new PlanItem();
         var5.setIndex(this.table.getScanIndex(var1, (int[])null, var2, var3, var6, var4));
         var5.cost = var5.getIndex().getCost(var1, (int[])null, var2, var3, var6, var4);
      }

      int var7 = this.table.getColumns().length;
      int[] var8 = new int[var7];
      Iterator var9 = this.indexConditions.iterator();

      while(var9.hasNext()) {
         IndexCondition var10 = (IndexCondition)var9.next();
         if (var10.isEvaluatable()) {
            if (var10.isAlwaysFalse()) {
               var8 = null;
               break;
            }

            int var11 = var10.getColumn().getColumnId();
            if (var11 >= 0) {
               var8[var11] |= var10.getMask(this.indexConditions);
            }
         }
      }

      PlanItem var12 = this.table.getBestPlanItem(var1, var8, var2, var3, var6, var4);
      var12.setMasks(var8);
      var12.cost -= var12.cost * (double)this.indexConditions.size() / 100.0 / (double)(var3 + 1);
      if (var5 != null && var5.cost < var12.cost) {
         var12 = var5;
      }

      if (this.nestedJoin != null) {
         this.setEvaluatable(true);
         var12.setNestedJoinPlan(this.nestedJoin.getBestPlanItem(var1, var2, var3, var4));
         var12.cost += var12.cost * var12.getNestedJoinPlan().cost;
      }

      if (this.join != null) {
         this.setEvaluatable(true);

         do {
            ++var3;
         } while(var2[var3] != this.join);

         var12.setJoinPlan(this.join.getBestPlanItem(var1, var2, var3, var4));
         var12.cost += var12.cost * var12.getJoinPlan().cost;
      }

      return var12;
   }

   public void setPlanItem(PlanItem var1) {
      if (var1 != null) {
         this.setIndex(var1.getIndex());
         this.masks = var1.getMasks();
         if (this.nestedJoin != null) {
            if (var1.getNestedJoinPlan() != null) {
               this.nestedJoin.setPlanItem(var1.getNestedJoinPlan());
            } else {
               this.nestedJoin.setScanIndexes();
            }
         }

         if (this.join != null) {
            if (var1.getJoinPlan() != null) {
               this.join.setPlanItem(var1.getJoinPlan());
            } else {
               this.join.setScanIndexes();
            }
         }

      }
   }

   private void setScanIndexes() {
      if (this.index == null) {
         this.setIndex(this.table.getScanIndex(this.session));
      }

      if (this.join != null) {
         this.join.setScanIndexes();
      }

      if (this.nestedJoin != null) {
         this.nestedJoin.setScanIndexes();
      }

   }

   public void prepare() {
      for(int var1 = 0; var1 < this.indexConditions.size(); ++var1) {
         IndexCondition var2 = (IndexCondition)this.indexConditions.get(var1);
         if (!var2.isAlwaysFalse()) {
            Column var3 = var2.getColumn();
            if (var3.getColumnId() >= 0 && this.index.getColumnIndex(var3) < 0) {
               this.indexConditions.remove(var1);
               --var1;
            }
         }
      }

      if (this.nestedJoin != null) {
         if (this.nestedJoin == this) {
            throw DbException.getInternalError("self join");
         }

         this.nestedJoin.prepare();
      }

      if (this.join != null) {
         if (this.join == this) {
            throw DbException.getInternalError("self join");
         }

         this.join.prepare();
      }

      if (this.filterCondition != null) {
         this.filterCondition = this.filterCondition.optimizeCondition(this.session);
      }

      if (this.joinCondition != null) {
         this.joinCondition = this.joinCondition.optimizeCondition(this.session);
      }

   }

   public void startQuery(SessionLocal var1) {
      this.session = var1;
      this.scanCount = 0;
      if (this.nestedJoin != null) {
         this.nestedJoin.startQuery(var1);
      }

      if (this.join != null) {
         this.join.startQuery(var1);
      }

   }

   public void reset() {
      if (this.nestedJoin != null) {
         this.nestedJoin.reset();
      }

      if (this.join != null) {
         this.join.reset();
      }

      this.state = 0;
      this.foundOne = false;
   }

   public boolean next() {
      if (this.state == 2) {
         return false;
      } else {
         if (this.state == 0) {
            this.cursor.find(this.session, this.indexConditions);
            if (!this.cursor.isAlwaysFalse()) {
               if (this.nestedJoin != null) {
                  this.nestedJoin.reset();
               }

               if (this.join != null) {
                  this.join.reset();
               }
            }
         } else if (this.join != null && this.join.next()) {
            return true;
         }

         while(true) {
            while(true) {
               if (this.state != 3) {
                  if (this.cursor.isAlwaysFalse()) {
                     this.state = 2;
                  } else if (this.nestedJoin != null) {
                     if (this.state == 0) {
                        this.state = 1;
                     }
                  } else {
                     if ((++this.scanCount & 4095) == 0) {
                        this.checkTimeout();
                     }

                     if (this.cursor.next()) {
                        this.currentSearchRow = this.cursor.getSearchRow();
                        this.current = null;
                        this.state = 1;
                     } else {
                        this.state = 2;
                     }
                  }

                  if (this.nestedJoin != null && this.state == 1 && !this.nestedJoin.next()) {
                     this.state = 2;
                     if (!this.joinOuter || this.foundOne) {
                        continue;
                     }
                  }

                  if (this.state != 2) {
                     break;
                  }

                  if (this.joinOuter && !this.foundOne) {
                     this.setNullRow();
                     break;
                  }
               }

               this.state = 2;
               return false;
            }

            if (this.isOk(this.filterCondition)) {
               boolean var1 = this.isOk(this.joinCondition);
               if (this.state == 1) {
                  if (!var1) {
                     continue;
                  }

                  this.foundOne = true;
               }

               if (this.join != null) {
                  this.join.reset();
                  if (!this.join.next()) {
                     continue;
                  }
               }

               if (this.state == 3 || var1) {
                  return true;
               }
            }
         }
      }
   }

   public boolean isNullRow() {
      return this.state == 3;
   }

   protected void setNullRow() {
      this.state = 3;
      this.current = this.table.getNullRow();
      this.currentSearchRow = this.current;
      if (this.nestedJoin != null) {
         this.nestedJoin.visit(TableFilter::setNullRow);
      }

   }

   private void checkTimeout() {
      this.session.checkCanceled();
   }

   boolean isOk(Expression var1) {
      return var1 == null || var1.getBooleanValue(this.session);
   }

   public Row get() {
      if (this.current == null && this.currentSearchRow != null) {
         this.current = this.cursor.get();
      }

      return this.current;
   }

   public void set(Row var1) {
      this.current = var1;
      this.currentSearchRow = var1;
   }

   public String getTableAlias() {
      return this.alias != null ? this.alias : this.table.getName();
   }

   public void addIndexCondition(IndexCondition var1) {
      this.indexConditions.add(var1);
   }

   public void addFilterCondition(Expression var1, boolean var2) {
      if (var2) {
         if (this.joinCondition == null) {
            this.joinCondition = var1;
         } else {
            this.joinCondition = new ConditionAndOr(0, this.joinCondition, var1);
         }
      } else if (this.filterCondition == null) {
         this.filterCondition = var1;
      } else {
         this.filterCondition = new ConditionAndOr(0, this.filterCondition, var1);
      }

   }

   public void addJoin(TableFilter var1, boolean var2, Expression var3) {
      if (var3 != null) {
         var3.mapColumns(this, 0, 0);
         MapColumnsVisitor var4 = new MapColumnsVisitor(var3);
         this.visit(var4);
         var1.visit(var4);
      }

      if (this.join == null) {
         this.join = var1;
         var1.joinOuter = var2;
         if (var2) {
            var1.visit(JOI_VISITOR);
         }

         if (var3 != null) {
            var1.mapAndAddFilter(var3);
         }
      } else {
         this.join.addJoin(var1, var2, var3);
      }

   }

   public void setNestedJoin(TableFilter var1) {
      this.nestedJoin = var1;
   }

   public void mapAndAddFilter(Expression var1) {
      var1.mapColumns(this, 0, 0);
      this.addFilterCondition(var1, true);
      if (this.nestedJoin != null) {
         var1.mapColumns(this.nestedJoin, 0, 0);
      }

      if (this.join != null) {
         this.join.mapAndAddFilter(var1);
      }

   }

   public void createIndexConditions() {
      if (this.joinCondition != null) {
         this.joinCondition = this.joinCondition.optimizeCondition(this.session);
         if (this.joinCondition != null) {
            this.joinCondition.createIndexConditions(this.session, this);
            if (this.nestedJoin != null) {
               this.joinCondition.createIndexConditions(this.session, this.nestedJoin);
            }
         }
      }

      if (this.join != null) {
         this.join.createIndexConditions();
      }

      if (this.nestedJoin != null) {
         this.nestedJoin.createIndexConditions();
      }

   }

   public TableFilter getJoin() {
      return this.join;
   }

   public boolean isJoinOuter() {
      return this.joinOuter;
   }

   public boolean isJoinOuterIndirect() {
      return this.joinOuterIndirect;
   }

   public StringBuilder getPlanSQL(StringBuilder var1, boolean var2, int var3) {
      if (var2) {
         if (this.joinOuter) {
            var1.append("LEFT OUTER JOIN ");
         } else {
            var1.append("INNER JOIN ");
         }
      }

      String var6;
      StringBuilder var8;
      if (this.nestedJoin != null) {
         var8 = new StringBuilder();
         TableFilter var11 = this.nestedJoin;

         do {
            var11.getPlanSQL(var8, var11 != this.nestedJoin, var3).append('\n');
            var11 = var11.getJoin();
         } while(var11 != null);

         var6 = var8.toString();
         boolean var7 = !var6.startsWith("(");
         if (var7) {
            var1.append("(\n");
         }

         StringUtils.indent(var1, var6, 4, false);
         if (var7) {
            var1.append(')');
         }

         if (var2) {
            var1.append(" ON ");
            if (this.joinCondition == null) {
               var1.append("1=1");
            } else {
               this.joinCondition.getUnenclosedSQL(var1, var3);
            }
         }

         return var1;
      } else {
         if (this.table instanceof TableView && ((TableView)this.table).isRecursive()) {
            this.table.getSchema().getSQL(var1, var3).append('.');
            ParserUtil.quoteIdentifier(var1, this.table.getName(), var3);
         } else {
            this.table.getSQL(var1, var3);
         }

         if (this.table instanceof TableView && ((TableView)this.table).isInvalid()) {
            throw DbException.get(90109, this.table.getName(), "not compiled");
         } else {
            boolean var4;
            Iterator var5;
            if (this.alias != null) {
               var1.append(' ');
               ParserUtil.quoteIdentifier(var1, this.alias, var3);
               if (this.derivedColumnMap != null) {
                  var1.append('(');
                  var4 = false;
                  var5 = this.derivedColumnMap.values().iterator();

                  while(var5.hasNext()) {
                     var6 = (String)var5.next();
                     if (var4) {
                        var1.append(", ");
                     }

                     var4 = true;
                     ParserUtil.quoteIdentifier(var1, var6, var3);
                  }

                  var1.append(')');
               }
            }

            if (this.indexHints != null) {
               var1.append(" USE INDEX (");
               var4 = true;

               for(var5 = this.indexHints.getAllowedIndexes().iterator(); var5.hasNext(); ParserUtil.quoteIdentifier(var1, var6, var3)) {
                  var6 = (String)var5.next();
                  if (!var4) {
                     var1.append(", ");
                  } else {
                     var4 = false;
                  }
               }

               var1.append(")");
            }

            if (this.index != null && (var3 & 8) != 0) {
               var1.append('\n');
               var8 = (new StringBuilder()).append("/* ").append(this.index.getPlanSQL());
               if (!this.indexConditions.isEmpty()) {
                  var8.append(": ");
                  int var9 = 0;

                  for(int var12 = this.indexConditions.size(); var9 < var12; ++var9) {
                     if (var9 > 0) {
                        var8.append("\n    AND ");
                     }

                     var8.append(((IndexCondition)this.indexConditions.get(var9)).getSQL(11));
                  }
               }

               if (var8.indexOf("\n", 3) >= 0) {
                  var8.append('\n');
               }

               StringUtils.indent(var1, var8.append(" */").toString(), 4, false);
            }

            if (var2) {
               var1.append("\n    ON ");
               if (this.joinCondition == null) {
                  var1.append("1=1");
               } else {
                  this.joinCondition.getUnenclosedSQL(var1, var3);
               }
            }

            if ((var3 & 8) != 0) {
               if (this.filterCondition != null) {
                  var1.append('\n');
                  String var10 = this.filterCondition.getSQL(11, 2);
                  var10 = "/* WHERE " + var10 + "\n*/";
                  StringUtils.indent(var1, var10, 4, false);
               }

               if (this.scanCount > 0) {
                  var1.append("\n    /* scanCount: ").append(this.scanCount).append(" */");
               }
            }

            return var1;
         }
      }
   }

   void removeUnusableIndexConditions() {
      for(int var1 = 0; var1 < this.indexConditions.size(); ++var1) {
         IndexCondition var2 = (IndexCondition)this.indexConditions.get(var1);
         if (var2.getMask(this.indexConditions) == 0 || !var2.isEvaluatable()) {
            this.indexConditions.remove(var1--);
         }
      }

   }

   public int[] getMasks() {
      return this.masks;
   }

   public ArrayList<IndexCondition> getIndexConditions() {
      return this.indexConditions;
   }

   public Index getIndex() {
      return this.index;
   }

   public void setIndex(Index var1) {
      this.index = var1;
      this.cursor.setIndex(var1);
   }

   public void setUsed(boolean var1) {
      this.used = var1;
   }

   public boolean isUsed() {
      return this.used;
   }

   public void removeJoin() {
      this.join = null;
   }

   public Expression getJoinCondition() {
      return this.joinCondition;
   }

   public void removeJoinCondition() {
      this.joinCondition = null;
   }

   public Expression getFilterCondition() {
      return this.filterCondition;
   }

   public void removeFilterCondition() {
      this.filterCondition = null;
   }

   public void setFullCondition(Expression var1) {
      this.fullCondition = var1;
      if (this.join != null) {
         this.join.setFullCondition(var1);
      }

   }

   void optimizeFullCondition() {
      if (!this.joinOuter && this.fullCondition != null) {
         this.fullCondition.addFilterConditions(this);
         if (this.nestedJoin != null) {
            this.nestedJoin.optimizeFullCondition();
         }

         if (this.join != null) {
            this.join.optimizeFullCondition();
         }
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      var1.setEvaluatable(var2);
      if (this.filterCondition != null) {
         this.filterCondition.setEvaluatable(var1, var2);
      }

      if (this.joinCondition != null) {
         this.joinCondition.setEvaluatable(var1, var2);
      }

      if (this.nestedJoin != null && this == var1) {
         this.nestedJoin.setEvaluatable(this.nestedJoin, var2);
      }

      if (this.join != null) {
         this.join.setEvaluatable(var1, var2);
      }

   }

   public void setEvaluatable(boolean var1) {
      this.evaluatable = var1;
   }

   public String getSchemaName() {
      return this.alias == null && !(this.table instanceof VirtualTable) ? this.table.getSchema().getName() : null;
   }

   public Column[] getColumns() {
      return this.table.getColumns();
   }

   public Column findColumn(String var1) {
      LinkedHashMap var2 = this.derivedColumnMap;
      if (var2 != null) {
         Database var3 = this.session.getDatabase();
         Iterator var4 = this.derivedColumnMap.entrySet().iterator();

         Map.Entry var5;
         do {
            if (!var4.hasNext()) {
               return null;
            }

            var5 = (Map.Entry)var4.next();
         } while(!var3.equalsIdentifiers((String)var5.getValue(), var1));

         return (Column)var5.getKey();
      } else {
         return this.table.findColumn(var1);
      }
   }

   public String getColumnName(Column var1) {
      LinkedHashMap var2 = this.derivedColumnMap;
      return var2 != null ? (String)var2.get(var1) : var1.getName();
   }

   public boolean hasDerivedColumnList() {
      return this.derivedColumnMap != null;
   }

   public Column getColumn(String var1, boolean var2) {
      LinkedHashMap var3 = this.derivedColumnMap;
      if (var3 != null) {
         Database var4 = this.session.getDatabase();
         Iterator var5 = var3.entrySet().iterator();

         Map.Entry var6;
         do {
            if (!var5.hasNext()) {
               if (var2) {
                  return null;
               }

               throw DbException.get(42122, (String)var1);
            }

            var6 = (Map.Entry)var5.next();
         } while(!var4.equalsIdentifiers(var1, (String)var6.getValue()));

         return (Column)var6.getKey();
      } else {
         return this.table.getColumn(var1, var2);
      }
   }

   public Column[] getSystemColumns() {
      if (!this.session.getDatabase().getMode().systemColumns) {
         return null;
      } else {
         Column[] var1 = new Column[]{new Column("oid", TypeInfo.TYPE_INTEGER, this.table, 0), new Column("ctid", TypeInfo.TYPE_VARCHAR, this.table, 0)};
         return var1;
      }
   }

   public Column getRowIdColumn() {
      return this.table.getRowIdColumn();
   }

   public Value getValue(Column var1) {
      if (this.currentSearchRow == null) {
         return null;
      } else {
         int var2 = var1.getColumnId();
         if (var2 == -1) {
            return ValueBigint.get(this.currentSearchRow.getKey());
         } else {
            if (this.current == null) {
               Value var3 = this.currentSearchRow.getValue(var2);
               if (var3 != null) {
                  return var3;
               }

               if (var2 == var1.getTable().getMainIndexColumn()) {
                  return this.getDelegatedValue(var1);
               }

               this.current = this.cursor.get();
               if (this.current == null) {
                  return ValueNull.INSTANCE;
               }
            }

            return this.current.getValue(var2);
         }
      }
   }

   private Value getDelegatedValue(Column var1) {
      long var2 = this.currentSearchRow.getKey();
      switch (var1.getType().getValueType()) {
         case 9:
            return ValueTinyint.get((byte)((int)var2));
         case 10:
            return ValueSmallint.get((short)((int)var2));
         case 11:
            return ValueInteger.get((int)var2);
         case 12:
            return ValueBigint.get(var2);
         default:
            throw DbException.getInternalError();
      }
   }

   public TableFilter getTableFilter() {
      return this;
   }

   public void setAlias(String var1) {
      this.alias = var1;
   }

   public void setDerivedColumns(ArrayList<String> var1) {
      Column[] var2 = this.getColumns();
      int var3 = var2.length;
      if (var3 != var1.size()) {
         throw DbException.get(21002);
      } else {
         LinkedHashMap var4 = new LinkedHashMap();

         for(int var5 = 0; var5 < var3; ++var5) {
            String var6 = (String)var1.get(var5);

            for(int var7 = 0; var7 < var5; ++var7) {
               if (var6.equals(var1.get(var7))) {
                  throw DbException.get(42121, (String)var6);
               }
            }

            var4.put(var2[var5], var6);
         }

         this.derivedColumnMap = var4;
      }
   }

   public String toString() {
      return this.alias != null ? this.alias : this.table.toString();
   }

   public void addCommonJoinColumns(Column var1, Column var2, TableFilter var3) {
      if (this.commonJoinColumns == null) {
         this.commonJoinColumns = new LinkedHashMap();
         this.commonJoinColumnsFilter = var3;
      } else {
         assert this.commonJoinColumnsFilter == var3;
      }

      this.commonJoinColumns.put(var1, var2);
   }

   public void addCommonJoinColumnToExclude(Column var1) {
      if (this.commonJoinColumnsToExclude == null) {
         this.commonJoinColumnsToExclude = Utils.newSmallArrayList();
      }

      this.commonJoinColumnsToExclude.add(var1);
   }

   public LinkedHashMap<Column, Column> getCommonJoinColumns() {
      return this.commonJoinColumns;
   }

   public TableFilter getCommonJoinColumnsFilter() {
      return this.commonJoinColumnsFilter;
   }

   public boolean isCommonJoinColumnToExclude(Column var1) {
      return this.commonJoinColumnsToExclude != null && this.commonJoinColumnsToExclude.contains(var1);
   }

   public int hashCode() {
      return this.hashCode;
   }

   public boolean hasInComparisons() {
      Iterator var1 = this.indexConditions.iterator();

      int var3;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         IndexCondition var2 = (IndexCondition)var1.next();
         var3 = var2.getCompareType();
      } while(var3 != 11 && var3 != 10);

      return true;
   }

   public TableFilter getNestedJoin() {
      return this.nestedJoin;
   }

   public void visit(TableFilterVisitor var1) {
      TableFilter var2 = this;

      do {
         var1.accept(var2);
         TableFilter var3 = var2.nestedJoin;
         if (var3 != null) {
            var3.visit(var1);
         }

         var2 = var2.join;
      } while(var2 != null);

   }

   public boolean isEvaluatable() {
      return this.evaluatable;
   }

   public SessionLocal getSession() {
      return this.session;
   }

   public IndexHints getIndexHints() {
      return this.indexHints;
   }

   public boolean isNoFromClauseFilter() {
      return this.table instanceof DualTable && this.join == null && this.nestedJoin == null && this.joinCondition == null && this.filterCondition == null;
   }

   private static final class MapColumnsVisitor implements TableFilterVisitor {
      private final Expression on;

      MapColumnsVisitor(Expression var1) {
         this.on = var1;
      }

      public void accept(TableFilter var1) {
         this.on.mapColumns(var1, 0, 0);
      }
   }

   public interface TableFilterVisitor {
      void accept(TableFilter var1);
   }
}
