package org.h2.command.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionList;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.table.TableValueConstructorTable;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class TableValueConstructor extends Query {
   private final ArrayList<ArrayList<Expression>> rows;
   TableValueConstructorTable table;
   private TableValueColumnResolver columnResolver;
   private double cost;

   public TableValueConstructor(SessionLocal var1, ArrayList<ArrayList<Expression>> var2) {
      super(var1);
      this.rows = var2;
      if ((this.visibleColumnCount = ((ArrayList)var2.get(0)).size()) > 16384) {
         throw DbException.get(54011, (String)"16384");
      } else {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            ArrayList var4 = (ArrayList)var3.next();
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               Expression var6 = (Expression)var5.next();
               if (!var6.isConstant()) {
                  return;
               }
            }
         }

         this.createTable();
      }
   }

   public static void getVisibleResult(SessionLocal var0, ResultTarget var1, Column[] var2, ArrayList<ArrayList<Expression>> var3) {
      int var4 = var2.length;
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         ArrayList var6 = (ArrayList)var5.next();
         Value[] var7 = new Value[var4];

         for(int var8 = 0; var8 < var4; ++var8) {
            var7[var8] = ((Expression)var6.get(var8)).getValue(var0).convertTo(var2[var8].getType(), var0);
         }

         var1.addRow(var7);
      }

   }

   public static void getValuesSQL(StringBuilder var0, int var1, ArrayList<ArrayList<Expression>> var2) {
      var0.append("VALUES ");
      int var3 = var2.size();

      for(int var4 = 0; var4 < var3; ++var4) {
         if (var4 > 0) {
            var0.append(", ");
         }

         Expression.writeExpressions(var0.append('('), (List)var2.get(var4), var1).append(')');
      }

   }

   public boolean isUnion() {
      return false;
   }

   protected ResultInterface queryWithoutCache(long var1, ResultTarget var3) {
      Query.OffsetFetch var4 = this.getOffsetFetch(var1);
      long var5 = var4.offset;
      long var7 = var4.fetch;
      boolean var9 = var4.fetchPercent;
      int var10 = this.visibleColumnCount;
      int var11 = this.resultColumnCount;
      LocalResult var12 = new LocalResult(this.session, this.expressionArray, var10, var11);
      if (this.sort != null) {
         var12.setSortOrder(this.sort);
      }

      if (this.distinct) {
         var12.setDistinct();
      }

      Column[] var13 = this.table.getColumns();
      if (var10 == var11) {
         getVisibleResult(this.session, var12, var13, this.rows);
      } else {
         Iterator var14 = this.rows.iterator();

         while(var14.hasNext()) {
            ArrayList var15 = (ArrayList)var14.next();
            Value[] var16 = new Value[var11];

            int var17;
            for(var17 = 0; var17 < var10; ++var17) {
               var16[var17] = ((Expression)var15.get(var17)).getValue(this.session).convertTo(var13[var17].getType(), this.session);
            }

            this.columnResolver.currentRow = var16;

            for(var17 = var10; var17 < var11; ++var17) {
               var16[var17] = this.expressionArray[var17].getValue(this.session);
            }

            var12.addRow(var16);
         }

         this.columnResolver.currentRow = null;
      }

      return this.finishResult(var12, var5, var7, var9, var3);
   }

   public void init() {
      if (this.checkInit) {
         throw DbException.getInternalError();
      } else {
         this.checkInit = true;
         if (this.withTies && !this.hasOrder()) {
            throw DbException.get(90122);
         }
      }
   }

   public void prepare() {
      if (!this.isPrepared) {
         if (!this.checkInit) {
            throw DbException.getInternalError("not initialized");
         } else {
            this.isPrepared = true;
            if (this.columnResolver == null) {
               this.createTable();
            }

            if (this.orderList != null) {
               ArrayList var1 = new ArrayList();
               Iterator var2 = this.expressions.iterator();

               while(var2.hasNext()) {
                  Expression var3 = (Expression)var2.next();
                  var1.add(var3.getSQL(0, 2));
               }

               if (this.initOrder(var1, false, (ArrayList)null)) {
                  this.prepareOrder(this.orderList, this.expressions.size());
               }
            }

            this.resultColumnCount = this.expressions.size();

            int var7;
            for(var7 = 0; var7 < this.resultColumnCount; ++var7) {
               ((Expression)this.expressions.get(var7)).mapColumns(this.columnResolver, 0, 0);
            }

            for(var7 = this.visibleColumnCount; var7 < this.resultColumnCount; ++var7) {
               this.expressions.set(var7, ((Expression)this.expressions.get(var7)).optimize(this.session));
            }

            if (this.sort != null) {
               this.cleanupOrder();
            }

            this.expressionArray = (Expression[])this.expressions.toArray(new Expression[0]);
            double var8 = 0.0;
            int var9 = this.visibleColumnCount;
            Iterator var4 = this.rows.iterator();

            while(var4.hasNext()) {
               ArrayList var5 = (ArrayList)var4.next();

               for(int var6 = 0; var6 < var9; ++var6) {
                  var8 += (double)((Expression)var5.get(var6)).getCost();
               }
            }

            this.cost = var8 + (double)this.rows.size();
         }
      }
   }

   private void createTable() {
      int var1 = this.rows.size();
      ArrayList var2 = (ArrayList)this.rows.get(0);
      int var3 = var2.size();
      TypeInfo[] var4 = new TypeInfo[var3];

      int var5;
      TypeInfo var7;
      for(var5 = 0; var5 < var3; ++var5) {
         Expression var6 = ((Expression)var2.get(var5)).optimize(this.session);
         var2.set(var5, var6);
         var7 = var6.getType();
         if (var7.getValueType() == -1) {
            var7 = TypeInfo.TYPE_VARCHAR;
         }

         var4[var5] = var7;
      }

      int var10;
      for(var5 = 1; var5 < var1; ++var5) {
         var2 = (ArrayList)this.rows.get(var5);

         for(var10 = 0; var10 < var3; ++var10) {
            Expression var12 = ((Expression)var2.get(var10)).optimize(this.session);
            var2.set(var10, var12);
            var4[var10] = TypeInfo.getHigherType(var4[var10], var12.getType());
         }
      }

      Column[] var9 = new Column[var3];

      int var10001;
      StringBuilder var10004;
      for(var10 = 0; var10 < var3; var9[var10001] = new Column(var10004.append(var10).toString(), var7)) {
         var7 = var4[var10];
         var10001 = var10;
         var10004 = (new StringBuilder()).append("C");
         ++var10;
      }

      Database var11 = this.session.getDatabase();
      ArrayList var13 = new ArrayList(var3);

      for(int var8 = 0; var8 < var3; ++var8) {
         var13.add(new ExpressionColumn(var11, (String)null, (String)null, var9[var8].getName()));
      }

      this.expressions = var13;
      this.table = new TableValueConstructorTable(this.session.getDatabase().getMainSchema(), this.session, var9, this.rows);
      this.columnResolver = new TableValueColumnResolver();
   }

   public double getCost() {
      return this.cost;
   }

   public HashSet<Table> getTables() {
      HashSet var1 = new HashSet(1, 1.0F);
      var1.add(this.table);
      return var1;
   }

   public void setForUpdate(boolean var1) {
      throw DbException.get(90140);
   }

   public void mapColumns(ColumnResolver var1, int var2) {
      int var3 = this.visibleColumnCount;
      Iterator var4 = this.rows.iterator();

      while(var4.hasNext()) {
         ArrayList var5 = (ArrayList)var4.next();

         for(int var6 = 0; var6 < var3; ++var6) {
            ((Expression)var5.get(var6)).mapColumns(var1, var2, 0);
         }
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      int var3 = this.visibleColumnCount;
      Iterator var4 = this.rows.iterator();

      while(var4.hasNext()) {
         ArrayList var5 = (ArrayList)var4.next();

         for(int var6 = 0; var6 < var3; ++var6) {
            ((Expression)var5.get(var6)).setEvaluatable(var1, var2);
         }
      }

   }

   public void addGlobalCondition(Parameter var1, int var2, int var3) {
   }

   public boolean allowGlobalConditions() {
      return false;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      ExpressionVisitor var2 = var1.incrementQueryLevel(1);
      Expression[] var3 = this.expressionArray;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Expression var6 = var3[var5];
         if (!var6.isEverything(var2)) {
            return false;
         }
      }

      return true;
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      int var3 = this.visibleColumnCount;
      Iterator var4 = this.rows.iterator();

      while(var4.hasNext()) {
         ArrayList var5 = (ArrayList)var4.next();

         for(int var6 = 0; var6 < var3; ++var6) {
            ((Expression)var5.get(var6)).updateAggregate(var1, var2);
         }
      }

   }

   public void fireBeforeSelectTriggers() {
   }

   public String getPlanSQL(int var1) {
      StringBuilder var2 = new StringBuilder();
      getValuesSQL(var2, var1, this.rows);
      this.appendEndOfQueryToSQL(var2, var1, this.expressionArray);
      return var2.toString();
   }

   public Table toTable(String var1, Column[] var2, ArrayList<Parameter> var3, boolean var4, Query var5) {
      return (Table)(!this.hasOrder() && this.offsetExpr == null && this.fetchExpr == null && this.table != null ? this.table : super.toTable(var1, var2, var3, var4, var5));
   }

   public boolean isConstantQuery() {
      if (!super.isConstantQuery()) {
         return false;
      } else {
         Iterator var1 = this.rows.iterator();

         while(var1.hasNext()) {
            ArrayList var2 = (ArrayList)var1.next();

            for(int var3 = 0; var3 < this.visibleColumnCount; ++var3) {
               if (!((Expression)var2.get(var3)).isConstant()) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public Expression getIfSingleRow() {
      if (this.offsetExpr == null && this.fetchExpr == null && this.rows.size() == 1) {
         ArrayList var1 = (ArrayList)this.rows.get(0);
         if (this.visibleColumnCount == 1) {
            return (Expression)var1.get(0);
         } else {
            Expression[] var2 = new Expression[this.visibleColumnCount];

            for(int var3 = 0; var3 < this.visibleColumnCount; ++var3) {
               var2[var3] = (Expression)var1.get(var3);
            }

            return new ExpressionList(var2, false);
         }
      } else {
         return null;
      }
   }

   private final class TableValueColumnResolver implements ColumnResolver {
      Value[] currentRow;

      TableValueColumnResolver() {
      }

      public Column[] getColumns() {
         return TableValueConstructor.this.table.getColumns();
      }

      public Column findColumn(String var1) {
         return TableValueConstructor.this.table.findColumn(var1);
      }

      public Value getValue(Column var1) {
         return this.currentRow[var1.getColumnId()];
      }

      public Expression optimize(ExpressionColumn var1, Column var2) {
         return (Expression)TableValueConstructor.this.expressions.get(var2.getColumnId());
      }
   }
}
