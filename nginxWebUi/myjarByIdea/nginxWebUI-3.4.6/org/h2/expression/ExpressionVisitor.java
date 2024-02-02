package org.h2.expression;

import java.util.HashSet;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.DbObject;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.Table;
import org.h2.table.TableFilter;

public final class ExpressionVisitor {
   public static final int INDEPENDENT = 0;
   public static final ExpressionVisitor INDEPENDENT_VISITOR = new ExpressionVisitor(0);
   public static final int OPTIMIZABLE_AGGREGATE = 1;
   public static final int DETERMINISTIC = 2;
   public static final ExpressionVisitor DETERMINISTIC_VISITOR = new ExpressionVisitor(2);
   public static final int EVALUATABLE = 3;
   public static final ExpressionVisitor EVALUATABLE_VISITOR = new ExpressionVisitor(3);
   private static final int CACHED = 8;
   private static final ExpressionVisitor[] INDEPENDENT_VISITORS;
   private static final ExpressionVisitor[] EVALUATABLE_VISITORS;
   public static final int SET_MAX_DATA_MODIFICATION_ID = 4;
   public static final int READONLY = 5;
   public static final ExpressionVisitor READONLY_VISITOR;
   public static final int NOT_FROM_RESOLVER = 6;
   public static final int GET_DEPENDENCIES = 7;
   public static final int QUERY_COMPARABLE = 8;
   public static final int GET_COLUMNS1 = 9;
   public static final int GET_COLUMNS2 = 10;
   public static final int DECREMENT_QUERY_LEVEL = 11;
   public static final ExpressionVisitor QUERY_COMPARABLE_VISITOR;
   private final int type;
   private final int queryLevel;
   private final HashSet<?> set;
   private final AllColumnsForPlan columns1;
   private final Table table;
   private final long[] maxDataModificationId;
   private final ColumnResolver resolver;

   private ExpressionVisitor(int var1, int var2, HashSet<?> var3, AllColumnsForPlan var4, Table var5, ColumnResolver var6, long[] var7) {
      this.type = var1;
      this.queryLevel = var2;
      this.set = var3;
      this.columns1 = var4;
      this.table = var5;
      this.resolver = var6;
      this.maxDataModificationId = var7;
   }

   private ExpressionVisitor(int var1) {
      this.type = var1;
      this.queryLevel = 0;
      this.set = null;
      this.columns1 = null;
      this.table = null;
      this.resolver = null;
      this.maxDataModificationId = null;
   }

   private ExpressionVisitor(int var1, int var2) {
      this.type = var1;
      this.queryLevel = var2;
      this.set = null;
      this.columns1 = null;
      this.table = null;
      this.resolver = null;
      this.maxDataModificationId = null;
   }

   public static ExpressionVisitor getDependenciesVisitor(HashSet<DbObject> var0) {
      return new ExpressionVisitor(7, 0, var0, (AllColumnsForPlan)null, (Table)null, (ColumnResolver)null, (long[])null);
   }

   public static ExpressionVisitor getOptimizableVisitor(Table var0) {
      return new ExpressionVisitor(1, 0, (HashSet)null, (AllColumnsForPlan)null, var0, (ColumnResolver)null, (long[])null);
   }

   public static ExpressionVisitor getNotFromResolverVisitor(ColumnResolver var0) {
      return new ExpressionVisitor(6, 0, (HashSet)null, (AllColumnsForPlan)null, (Table)null, var0, (long[])null);
   }

   public static ExpressionVisitor getColumnsVisitor(AllColumnsForPlan var0) {
      return new ExpressionVisitor(9, 0, (HashSet)null, var0, (Table)null, (ColumnResolver)null, (long[])null);
   }

   public static ExpressionVisitor getColumnsVisitor(HashSet<Column> var0, Table var1) {
      return new ExpressionVisitor(10, 0, var0, (AllColumnsForPlan)null, var1, (ColumnResolver)null, (long[])null);
   }

   public static ExpressionVisitor getMaxModificationIdVisitor() {
      return new ExpressionVisitor(4, 0, (HashSet)null, (AllColumnsForPlan)null, (Table)null, (ColumnResolver)null, new long[1]);
   }

   public static ExpressionVisitor getDecrementQueryLevelVisitor(HashSet<ColumnResolver> var0, int var1) {
      return new ExpressionVisitor(11, var1, var0, (AllColumnsForPlan)null, (Table)null, (ColumnResolver)null, (long[])null);
   }

   public void addDependency(DbObject var1) {
      this.set.add(var1);
   }

   void addColumn1(Column var1) {
      this.columns1.add(var1);
   }

   void addColumn2(Column var1) {
      if (this.table == null || this.table == var1.getTable()) {
         this.set.add(var1);
      }

   }

   public HashSet<DbObject> getDependencies() {
      return this.set;
   }

   public ExpressionVisitor incrementQueryLevel(int var1) {
      if (this.type == 0) {
         var1 += this.queryLevel;
         return var1 < 8 ? INDEPENDENT_VISITORS[var1] : new ExpressionVisitor(0, var1);
      } else if (this.type == 3) {
         var1 += this.queryLevel;
         return var1 < 8 ? EVALUATABLE_VISITORS[var1] : new ExpressionVisitor(3, var1);
      } else {
         return this;
      }
   }

   public ColumnResolver getResolver() {
      return this.resolver;
   }

   public HashSet<ColumnResolver> getColumnResolvers() {
      return this.set;
   }

   public void addDataModificationId(long var1) {
      long var3 = this.maxDataModificationId[0];
      if (var1 > var3) {
         this.maxDataModificationId[0] = var1;
      }

   }

   public long getMaxDataModificationId() {
      return this.maxDataModificationId[0];
   }

   int getQueryLevel() {
      assert this.type == 0 || this.type == 3 || this.type == 11;

      return this.queryLevel;
   }

   public Table getTable() {
      return this.table;
   }

   public int getType() {
      return this.type;
   }

   public static void allColumnsForTableFilters(TableFilter[] var0, AllColumnsForPlan var1) {
      TableFilter[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TableFilter var5 = var2[var4];
         if (var5.getSelect() != null) {
            var5.getSelect().isEverything(getColumnsVisitor(var1));
         }
      }

   }

   static {
      ExpressionVisitor[] var0 = new ExpressionVisitor[8];
      var0[0] = INDEPENDENT_VISITOR;

      int var1;
      for(var1 = 1; var1 < 8; ++var1) {
         var0[var1] = new ExpressionVisitor(0, var1);
      }

      INDEPENDENT_VISITORS = var0;
      var0 = new ExpressionVisitor[8];
      var0[0] = EVALUATABLE_VISITOR;

      for(var1 = 1; var1 < 8; ++var1) {
         var0[var1] = new ExpressionVisitor(3, var1);
      }

      EVALUATABLE_VISITORS = var0;
      READONLY_VISITOR = new ExpressionVisitor(5);
      QUERY_COMPARABLE_VISITOR = new ExpressionVisitor(8);
   }
}
