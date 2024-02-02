package org.h2.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.h2.command.Prepared;
import org.h2.command.ddl.CreateTableData;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.command.query.Query;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.index.ViewIndex;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SortOrder;
import org.h2.schema.Schema;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;

public class TableView extends Table {
   private static final long ROW_COUNT_APPROXIMATION = 100L;
   private String querySQL;
   private ArrayList<Table> tables;
   private Column[] columnTemplates;
   private Query viewQuery;
   private ViewIndex index;
   private boolean allowRecursive;
   private DbException createException;
   private long lastModificationCheck;
   private long maxDataModificationId;
   private User owner;
   private Query topQuery;
   private ResultInterface recursiveResult;
   private boolean isRecursiveQueryDetected;
   private boolean isTableExpression;

   public TableView(Schema var1, int var2, String var3, String var4, ArrayList<Parameter> var5, Column[] var6, SessionLocal var7, boolean var8, boolean var9, boolean var10, boolean var11) {
      super(var1, var2, var3, false, true);
      this.setTemporary(var11);
      this.init(var4, var5, var6, var7, var8, var9, var10);
   }

   public void replace(String var1, Column[] var2, SessionLocal var3, boolean var4, boolean var5, boolean var6) {
      String var7 = this.querySQL;
      Column[] var8 = this.columnTemplates;
      boolean var9 = this.allowRecursive;
      this.init(var1, (ArrayList)null, var2, var3, var4, var6, this.isTableExpression);
      DbException var10 = this.recompile(var3, var5, true);
      if (var10 != null) {
         this.init(var7, (ArrayList)null, var8, var3, var9, var6, this.isTableExpression);
         this.recompile(var3, true, false);
         throw var10;
      }
   }

   private synchronized void init(String var1, ArrayList<Parameter> var2, Column[] var3, SessionLocal var4, boolean var5, boolean var6, boolean var7) {
      this.querySQL = var1;
      this.columnTemplates = var3;
      this.allowRecursive = var5;
      this.isRecursiveQueryDetected = false;
      this.isTableExpression = var7;
      this.index = new ViewIndex(this, var1, var2, var5);
      this.initColumnsAndTables(var4, var6);
   }

   private Query compileViewQuery(SessionLocal var1, String var2, boolean var3) {
      var1.setParsingCreateView(true);

      Prepared var4;
      try {
         var4 = var1.prepare(var2, false, var3);
      } finally {
         var1.setParsingCreateView(false);
      }

      if (!(var4 instanceof Query)) {
         throw DbException.getSyntaxError(var2, 0);
      } else {
         Query var5 = (Query)var4;
         if (this.isTableExpression && this.allowRecursive) {
            var5.setNeverLazy(true);
         }

         return var5;
      }
   }

   public synchronized DbException recompile(SessionLocal var1, boolean var2, boolean var3) {
      try {
         this.compileViewQuery(var1, this.querySQL, false);
      } catch (DbException var8) {
         if (!var2) {
            return var8;
         }
      }

      ArrayList var4 = new ArrayList(this.getDependentViews());
      this.initColumnsAndTables(var1, false);
      Iterator var5 = var4.iterator();

      DbException var7;
      do {
         if (!var5.hasNext()) {
            if (var3) {
               clearIndexCaches(this.database);
            }

            return var2 ? null : this.createException;
         }

         TableView var6 = (TableView)var5.next();
         var7 = var6.recompile(var1, var2, false);
      } while(var7 == null || var2);

      return var7;
   }

   private void initColumnsAndTables(SessionLocal var1, boolean var2) {
      this.removeCurrentViewFromOtherTables();
      this.setTableExpression(this.isTableExpression);

      Column[] var3;
      try {
         Query var4 = this.compileViewQuery(var1, this.querySQL, var2);
         this.querySQL = var4.getPlanSQL(0);
         this.tables = new ArrayList(var4.getTables());
         ArrayList var13 = var4.getExpressions();
         int var6 = var4.getColumnCount();
         ArrayList var7 = new ArrayList(var6);

         for(int var8 = 0; var8 < var6; ++var8) {
            Expression var9 = (Expression)var13.get(var8);
            String var10 = null;
            TypeInfo var11 = TypeInfo.TYPE_UNKNOWN;
            if (this.columnTemplates != null && this.columnTemplates.length > var8) {
               var10 = this.columnTemplates[var8].getName();
               var11 = this.columnTemplates[var8].getType();
            }

            if (var10 == null) {
               var10 = var9.getColumnNameForView(var1, var8);
            }

            if (var11.getValueType() == -1) {
               var11 = var9.getType();
            }

            var7.add(new Column(var10, var11, this, var8));
         }

         var3 = (Column[])var7.toArray(new Column[0]);
         this.createException = null;
         this.viewQuery = var4;
      } catch (DbException var12) {
         if (var12.getErrorCode() == 90156) {
            throw var12;
         }

         var12.addSQL(this.getCreateSQL());
         this.createException = var12;
         if (this.isRecursiveQueryExceptionDetected(this.createException)) {
            this.isRecursiveQueryDetected = true;
         }

         this.tables = Utils.newSmallArrayList();
         var3 = new Column[0];
         if (this.allowRecursive && this.columnTemplates != null) {
            var3 = new Column[this.columnTemplates.length];

            for(int var5 = 0; var5 < this.columnTemplates.length; ++var5) {
               var3[var5] = this.columnTemplates[var5].getClone();
            }

            this.index.setRecursive(true);
            this.createException = null;
         }
      }

      this.setColumns(var3);
      if (this.getId() != 0) {
         this.addDependentViewToTables();
      }

   }

   public boolean isView() {
      return true;
   }

   public boolean isInvalid() {
      return this.createException != null;
   }

   public PlanItem getBestPlanItem(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      CacheKey var7 = new CacheKey(var2, this);
      Map var8 = var1.getViewIndexCache(this.topQuery != null);
      ViewIndex var9 = (ViewIndex)var8.get(var7);
      if (var9 == null || var9.isExpired()) {
         var9 = new ViewIndex(this, this.index, var1, var2, var3, var4, var5);
         var8.put(var7, var9);
      }

      PlanItem var10 = new PlanItem();
      var10.cost = var9.getCost(var1, var2, var3, var4, var5, var6);
      var10.setIndex(var9);
      return var10;
   }

   public boolean isQueryComparable() {
      if (!super.isQueryComparable()) {
         return false;
      } else {
         Iterator var1 = this.tables.iterator();

         Table var2;
         do {
            if (!var1.hasNext()) {
               if (this.topQuery != null && !this.topQuery.isEverything(ExpressionVisitor.QUERY_COMPARABLE_VISITOR)) {
                  return false;
               }

               return true;
            }

            var2 = (Table)var1.next();
         } while(var2.isQueryComparable());

         return false;
      }
   }

   public Query getTopQuery() {
      return this.topQuery;
   }

   public String getDropSQL() {
      return this.getSQL(new StringBuilder("DROP VIEW IF EXISTS "), 0).append(" CASCADE").toString();
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      return this.getCreateSQL(false, true, var2);
   }

   public String getCreateSQL() {
      return this.getCreateSQL(false, true);
   }

   public String getCreateSQL(boolean var1, boolean var2) {
      return this.getCreateSQL(var1, var2, this.getSQL(0));
   }

   private String getCreateSQL(boolean var1, boolean var2, String var3) {
      StringBuilder var4 = new StringBuilder("CREATE ");
      if (var1) {
         var4.append("OR REPLACE ");
      }

      if (var2) {
         var4.append("FORCE ");
      }

      var4.append("VIEW ");
      if (this.isTableExpression) {
         var4.append("TABLE_EXPRESSION ");
      }

      var4.append(var3);
      if (this.comment != null) {
         var4.append(" COMMENT ");
         StringUtils.quoteStringSQL(var4, this.comment);
      }

      if (this.columns != null && this.columns.length > 0) {
         var4.append('(');
         Column.writeColumns(var4, this.columns, 0);
         var4.append(')');
      } else if (this.columnTemplates != null) {
         var4.append('(');
         Column.writeColumns(var4, this.columnTemplates, 0);
         var4.append(')');
      }

      return var4.append(" AS\n").append(this.querySQL).toString();
   }

   public void close(SessionLocal var1) {
   }

   public Index addIndex(SessionLocal var1, String var2, int var3, IndexColumn[] var4, int var5, IndexType var6, boolean var7, String var8) {
      throw DbException.getUnsupportedException("VIEW");
   }

   public boolean isInsertable() {
      return false;
   }

   public void removeRow(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("VIEW");
   }

   public void addRow(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("VIEW");
   }

   public void checkSupportAlter() {
      throw DbException.getUnsupportedException("VIEW");
   }

   public long truncate(SessionLocal var1) {
      throw DbException.getUnsupportedException("VIEW");
   }

   public long getRowCount(SessionLocal var1) {
      throw DbException.getInternalError(this.toString());
   }

   public boolean canGetRowCount(SessionLocal var1) {
      return false;
   }

   public boolean canDrop() {
      return true;
   }

   public TableType getTableType() {
      return TableType.VIEW;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.removeCurrentViewFromOtherTables();
      super.removeChildrenAndResources(var1);
      this.database.removeMeta(var1, this.getId());
      this.querySQL = null;
      this.index = null;
      clearIndexCaches(this.database);
      this.invalidate();
   }

   public static void clearIndexCaches(Database var0) {
      SessionLocal[] var1 = var0.getSessions(true);
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         SessionLocal var4 = var1[var3];
         var4.clearViewIndexCache();
      }

   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      if (this.isTemporary() && this.querySQL != null) {
         var1.append("(\n");
         return StringUtils.indent(var1, this.querySQL, 4, true).append(')');
      } else {
         return super.getSQL(var1, var2);
      }
   }

   public String getQuery() {
      return this.querySQL;
   }

   public Index getScanIndex(SessionLocal var1) {
      return this.getBestPlanItem(var1, (int[])null, (TableFilter[])null, -1, (SortOrder)null, (AllColumnsForPlan)null).getIndex();
   }

   public Index getScanIndex(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      if (this.createException != null) {
         String var8 = this.createException.getMessage();
         throw DbException.get(90109, this.createException, this.getTraceSQL(), var8);
      } else {
         PlanItem var7 = this.getBestPlanItem(var1, var2, var3, var4, var5, var6);
         return var7.getIndex();
      }
   }

   public boolean canReference() {
      return false;
   }

   public ArrayList<Index> getIndexes() {
      return null;
   }

   public long getMaxDataModificationId() {
      if (this.createException != null) {
         return Long.MAX_VALUE;
      } else if (this.viewQuery == null) {
         return Long.MAX_VALUE;
      } else {
         long var1 = this.database.getModificationDataId();
         if (var1 > this.lastModificationCheck && this.maxDataModificationId <= var1) {
            this.maxDataModificationId = this.viewQuery.getMaxDataModificationId();
            this.lastModificationCheck = var1;
         }

         return this.maxDataModificationId;
      }
   }

   private void removeCurrentViewFromOtherTables() {
      if (this.tables != null) {
         Iterator var1 = this.tables.iterator();

         while(var1.hasNext()) {
            Table var2 = (Table)var1.next();
            var2.removeDependentView(this);
         }

         this.tables.clear();
      }

   }

   private void addDependentViewToTables() {
      Iterator var1 = this.tables.iterator();

      while(var1.hasNext()) {
         Table var2 = (Table)var1.next();
         var2.addDependentView(this);
      }

   }

   private void setOwner(User var1) {
      this.owner = var1;
   }

   public User getOwner() {
      return this.owner;
   }

   public static TableView createTempView(SessionLocal var0, User var1, String var2, Column[] var3, Query var4, Query var5) {
      Schema var6 = var0.getDatabase().getMainSchema();
      String var7 = var4.getPlanSQL(0);
      TableView var8 = new TableView(var6, 0, var2, var7, var4.getParameters(), var3, var0, false, true, false, true);
      if (var8.createException != null) {
         throw var8.createException;
      } else {
         var8.setTopQuery(var5);
         var8.setOwner(var1);
         var8.setTemporary(true);
         return var8;
      }
   }

   private void setTopQuery(Query var1) {
      this.topQuery = var1;
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return 100L;
   }

   public int getParameterOffset(ArrayList<Parameter> var1) {
      int var2 = this.topQuery == null ? -1 : getMaxParameterIndex(this.topQuery.getParameters());
      if (var1 != null) {
         var2 = Math.max(var2, getMaxParameterIndex(var1));
      }

      return var2 + 1;
   }

   private static int getMaxParameterIndex(ArrayList<Parameter> var0) {
      int var1 = -1;
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Parameter var3 = (Parameter)var2.next();
         if (var3 != null) {
            var1 = Math.max(var1, var3.getIndex());
         }
      }

      return var1;
   }

   public boolean isRecursive() {
      return this.allowRecursive;
   }

   public boolean isDeterministic() {
      return !this.allowRecursive && this.viewQuery != null ? this.viewQuery.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) : false;
   }

   public void setRecursiveResult(ResultInterface var1) {
      if (this.recursiveResult != null) {
         this.recursiveResult.close();
      }

      this.recursiveResult = var1;
   }

   public ResultInterface getRecursiveResult() {
      return this.recursiveResult;
   }

   public void addDependencies(HashSet<DbObject> var1) {
      super.addDependencies(var1);
      if (this.tables != null) {
         Iterator var2 = this.tables.iterator();

         while(var2.hasNext()) {
            Table var3 = (Table)var2.next();
            if (TableType.VIEW != var3.getTableType()) {
               var3.addDependencies(var1);
            }
         }
      }

   }

   public boolean isRecursiveQueryDetected() {
      return this.isRecursiveQueryDetected;
   }

   private boolean isRecursiveQueryExceptionDetected(DbException var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = var1.getErrorCode();
         return var2 != 42102 && var2 != 42104 && var2 != 42103 ? false : var1.getMessage().contains("\"" + this.getName() + "\"");
      }
   }

   public List<Table> getTables() {
      return this.tables;
   }

   public static TableView createTableViewMaybeRecursive(Schema var0, int var1, String var2, String var3, ArrayList<Parameter> var4, Column[] var5, SessionLocal var6, boolean var7, boolean var8, boolean var9, Database var10) {
      Table var11 = createShadowTableForRecursiveTableExpression(var9, var6, var2, var0, Arrays.asList(var5), var10);
      String[] var13 = new String[1];
      ArrayList var14 = new ArrayList();
      Column[] var15 = var5;
      int var16 = var5.length;

      for(int var17 = 0; var17 < var16; ++var17) {
         Column var18 = var15[var17];
         var14.add(var18.getName());
      }

      List var12;
      try {
         Prepared var22 = var6.prepare(var3, false, false);
         if (!var9) {
            var22.setSession(var6);
         }

         var12 = createQueryColumnTemplateList((String[])var14.toArray(new String[1]), (Query)var22, var13);
      } finally {
         destroyShadowTableForRecursiveExpression(var9, var6, var11);
      }

      TableView var23 = new TableView(var0, var1, var2, var3, var4, (Column[])var12.toArray(var5), var6, true, var7, var8, var9);
      if (!var23.isRecursiveQueryDetected()) {
         if (!var9) {
            var10.addSchemaObject(var6, var23);
            var23.lock(var6, 2);
            var6.getDatabase().removeSchemaObject(var6, var23);
            var23.removeChildrenAndResources(var6);
         } else {
            var6.removeLocalTempTable(var23);
         }

         var23 = new TableView(var0, var1, var2, var3, var4, var5, var6, false, var7, var8, var9);
      }

      return var23;
   }

   public static List<Column> createQueryColumnTemplateList(String[] var0, Query var1, String[] var2) {
      ArrayList var3 = new ArrayList();
      var1.prepare();
      var2[0] = StringUtils.cache(var1.getPlanSQL(8));
      SessionLocal var4 = var1.getSession();
      ArrayList var5 = var1.getExpressions();

      for(int var6 = 0; var6 < var5.size(); ++var6) {
         Expression var7 = (Expression)var5.get(var6);
         String var8 = var0 != null && var0.length > var6 ? var0[var6] : var7.getColumnNameForView(var4, var6);
         var3.add(new Column(var8, var7.getType()));
      }

      return var3;
   }

   public static Table createShadowTableForRecursiveTableExpression(boolean var0, SessionLocal var1, String var2, Schema var3, List<Column> var4, Database var5) {
      CreateTableData var6 = new CreateTableData();
      var6.id = var5.allocateObjectId();
      var6.columns = new ArrayList(var4);
      var6.tableName = var2;
      var6.temporary = var0;
      var6.persistData = true;
      var6.persistIndexes = !var0;
      var6.session = var1;
      Table var7 = var3.createTable(var6);
      if (!var0) {
         var5.unlockMeta(var1);
         synchronized(var1) {
            var5.addSchemaObject(var1, var7);
         }
      } else {
         var1.addLocalTempTable(var7);
      }

      return var7;
   }

   public static void destroyShadowTableForRecursiveExpression(boolean var0, SessionLocal var1, Table var2) {
      if (var2 != null) {
         if (!var0) {
            var2.lock(var1, 2);
            var1.getDatabase().removeSchemaObject(var1, var2);
         } else {
            var1.removeLocalTempTable(var2);
         }

         var1.getDatabase().unlockMeta(var1);
      }

   }

   private static final class CacheKey {
      private final int[] masks;
      private final TableView view;

      CacheKey(int[] var1, TableView var2) {
         this.masks = var1;
         this.view = var2;
      }

      public int hashCode() {
         int var2 = 1;
         var2 = 31 * var2 + Arrays.hashCode(this.masks);
         var2 = 31 * var2 + this.view.hashCode();
         return var2;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 == null) {
            return false;
         } else if (this.getClass() != var1.getClass()) {
            return false;
         } else {
            CacheKey var2 = (CacheKey)var1;
            return this.view != var2.view ? false : Arrays.equals(this.masks, var2.masks);
         }
      }
   }
}
