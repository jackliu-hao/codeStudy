package org.h2.index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.h2.command.Parser;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.command.query.Query;
import org.h2.command.query.SelectUnion;
import org.h2.engine.SessionLocal;
import org.h2.expression.Parameter;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.table.TableView;
import org.h2.util.IntArray;
import org.h2.value.Value;

public class ViewIndex extends Index implements SpatialIndex {
   private static final long MAX_AGE_NANOS;
   private final TableView view;
   private final String querySQL;
   private final ArrayList<Parameter> originalParameters;
   private boolean recursive;
   private final int[] indexMasks;
   private Query query;
   private final SessionLocal createSession;
   private final long evaluatedAt;

   public ViewIndex(TableView var1, String var2, ArrayList<Parameter> var3, boolean var4) {
      super(var1, 0, (String)null, (IndexColumn[])null, 0, IndexType.createNonUnique(false));
      this.view = var1;
      this.querySQL = var2;
      this.originalParameters = var3;
      this.recursive = var4;
      this.columns = new Column[0];
      this.createSession = null;
      this.indexMasks = null;
      this.evaluatedAt = Long.MIN_VALUE;
   }

   public ViewIndex(TableView var1, ViewIndex var2, SessionLocal var3, int[] var4, TableFilter[] var5, int var6, SortOrder var7) {
      super(var1, 0, (String)null, (IndexColumn[])null, 0, IndexType.createNonUnique(false));
      this.view = var1;
      this.querySQL = var2.querySQL;
      this.originalParameters = var2.originalParameters;
      this.recursive = var2.recursive;
      this.indexMasks = var4;
      this.createSession = var3;
      this.columns = new Column[0];
      if (!this.recursive) {
         this.query = this.getQuery(var3, var4);
      }

      if (!this.recursive && var1.getTopQuery() == null) {
         long var8 = System.nanoTime();
         if (var8 == Long.MAX_VALUE) {
            ++var8;
         }

         this.evaluatedAt = var8;
      } else {
         this.evaluatedAt = Long.MAX_VALUE;
      }

   }

   public SessionLocal getSession() {
      return this.createSession;
   }

   public boolean isExpired() {
      assert this.evaluatedAt != Long.MIN_VALUE : "must not be called for main index of TableView";

      return !this.recursive && this.view.getTopQuery() == null && System.nanoTime() - this.evaluatedAt > MAX_AGE_NANOS;
   }

   public String getPlanSQL() {
      return this.query == null ? null : this.query.getPlanSQL(11);
   }

   public void close(SessionLocal var1) {
   }

   public void add(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("VIEW");
   }

   public void remove(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("VIEW");
   }

   public double getCost(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      return this.recursive ? 1000.0 : this.query.getCost();
   }

   public Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3) {
      return this.find(var1, var2, var3, (SearchRow)null);
   }

   public Cursor findByGeometry(SessionLocal var1, SearchRow var2, SearchRow var3, SearchRow var4) {
      return this.find(var1, var2, var3, var4);
   }

   private Cursor findRecursive(SearchRow var1, SearchRow var2) {
      assert this.recursive;

      ResultInterface var3 = this.view.getRecursiveResult();
      if (var3 != null) {
         var3.reset();
         return new ViewCursor(this, var3, var1, var2);
      } else {
         if (this.query == null) {
            Parser var4 = new Parser(this.createSession);
            var4.setRightsChecked(true);
            var4.setSuppliedParameters(this.originalParameters);
            this.query = (Query)var4.prepare(this.querySQL);
            this.query.setNeverLazy(true);
         }

         if (!this.query.isUnion()) {
            throw DbException.get(42001, (String)"recursive queries without UNION");
         } else {
            SelectUnion var10 = (SelectUnion)this.query;
            Query var5 = var10.getLeft();
            var5.setNeverLazy(true);
            var5.disableCache();
            ResultInterface var6 = var5.query(0L);
            LocalResult var7 = var10.getEmptyResult();
            var7.setMaxMemoryRows(Integer.MAX_VALUE);

            while(var6.next()) {
               Value[] var8 = var6.currentRow();
               var7.addRow(var8);
            }

            Query var11 = var10.getRight();
            var11.setNeverLazy(true);
            var6.reset();
            this.view.setRecursiveResult(var6);
            var11.disableCache();

            while(true) {
               var6 = var11.query(0L);
               if (!var6.hasNext()) {
                  this.view.setRecursiveResult((ResultInterface)null);
                  var7.done();
                  return new ViewCursor(this, var7, var1, var2);
               }

               while(var6.next()) {
                  Value[] var9 = var6.currentRow();
                  var7.addRow(var9);
               }

               var6.reset();
               this.view.setRecursiveResult(var6);
            }
         }
      }
   }

   public void setupQueryParameters(SessionLocal var1, SearchRow var2, SearchRow var3, SearchRow var4) {
      ArrayList var5 = this.query.getParameters();
      int var8;
      if (this.originalParameters != null) {
         Iterator var6 = this.originalParameters.iterator();

         while(var6.hasNext()) {
            Parameter var7 = (Parameter)var6.next();
            if (var7 != null) {
               var8 = var7.getIndex();
               Value var9 = var7.getValue(var1);
               setParameter(var5, var8, var9);
            }
         }
      }

      int var10;
      if (var2 != null) {
         var10 = var2.getColumnCount();
      } else if (var3 != null) {
         var10 = var3.getColumnCount();
      } else if (var4 != null) {
         var10 = var4.getColumnCount();
      } else {
         var10 = 0;
      }

      int var11 = this.view.getParameterOffset(this.originalParameters);

      for(var8 = 0; var8 < var10; ++var8) {
         int var12 = this.indexMasks[var8];
         if ((var12 & 1) != 0) {
            setParameter(var5, var11++, var2.getValue(var8));
         }

         if ((var12 & 2) != 0) {
            setParameter(var5, var11++, var2.getValue(var8));
         }

         if ((var12 & 4) != 0) {
            setParameter(var5, var11++, var3.getValue(var8));
         }

         if ((var12 & 16) != 0) {
            setParameter(var5, var11++, var4.getValue(var8));
         }
      }

   }

   private Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3, SearchRow var4) {
      if (this.recursive) {
         return this.findRecursive(var2, var3);
      } else {
         this.setupQueryParameters(var1, var2, var3, var4);
         ResultInterface var5 = this.query.query(0L);
         return new ViewCursor(this, var5, var2, var3);
      }
   }

   private static void setParameter(ArrayList<Parameter> var0, int var1, Value var2) {
      if (var1 < var0.size()) {
         Parameter var3 = (Parameter)var0.get(var1);
         var3.setValue(var2);
      }
   }

   public Query getQuery() {
      return this.query;
   }

   private Query getQuery(SessionLocal var1, int[] var2) {
      Query var3 = (Query)var1.prepare(this.querySQL, true, true);
      if (var2 == null) {
         return var3;
      } else if (!var3.allowGlobalConditions()) {
         return var3;
      } else {
         int var4 = this.view.getParameterOffset(this.originalParameters);
         IntArray var5 = new IntArray();
         int var6 = 0;

         int var7;
         int var9;
         int var10;
         for(var7 = 0; var7 < var2.length; ++var7) {
            int var8 = var2[var7];
            if (var8 != 0) {
               ++var6;
               var9 = Integer.bitCount(var8);

               for(var10 = 0; var10 < var9; ++var10) {
                  var5.add(var7);
               }
            }
         }

         var7 = var5.size();
         ArrayList var14 = new ArrayList(var7);
         var9 = 0;

         int var11;
         while(var9 < var7) {
            var10 = var5.get(var9);
            var14.add(this.table.getColumn(var10));
            var11 = var2[var10];
            Parameter var12;
            if ((var11 & 1) != 0) {
               var12 = new Parameter(var4 + var9);
               var3.addGlobalCondition(var12, var10, 6);
               ++var9;
            }

            if ((var11 & 2) != 0) {
               var12 = new Parameter(var4 + var9);
               var3.addGlobalCondition(var12, var10, 5);
               ++var9;
            }

            if ((var11 & 4) != 0) {
               var12 = new Parameter(var4 + var9);
               var3.addGlobalCondition(var12, var10, 4);
               ++var9;
            }

            if ((var11 & 16) != 0) {
               var12 = new Parameter(var4 + var9);
               var3.addGlobalCondition(var12, var10, 8);
               ++var9;
            }
         }

         this.columns = (Column[])var14.toArray(new Column[0]);
         this.indexColumns = new IndexColumn[var6];
         this.columnIds = new int[var6];
         var9 = 0;

         for(var10 = 0; var9 < 2; ++var9) {
            for(var11 = 0; var11 < var2.length; ++var11) {
               int var16 = var2[var11];
               if (var16 != 0) {
                  if (var9 == 0) {
                     if ((var16 & 1) == 0) {
                        continue;
                     }
                  } else if ((var16 & 1) != 0) {
                     continue;
                  }

                  Column var13 = this.table.getColumn(var11);
                  this.indexColumns[var10] = new IndexColumn(var13);
                  this.columnIds[var10] = var13.getColumnId();
                  ++var10;
               }
            }
         }

         String var15 = var3.getPlanSQL(0);
         var3 = (Query)var1.prepare(var15, true, true);
         return var3;
      }
   }

   public void remove(SessionLocal var1) {
      throw DbException.getUnsupportedException("VIEW");
   }

   public void truncate(SessionLocal var1) {
      throw DbException.getUnsupportedException("VIEW");
   }

   public void checkRename() {
      throw DbException.getUnsupportedException("VIEW");
   }

   public boolean needRebuild() {
      return false;
   }

   public void setRecursive(boolean var1) {
      this.recursive = var1;
   }

   public long getRowCount(SessionLocal var1) {
      return 0L;
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return 0L;
   }

   public boolean isRecursive() {
      return this.recursive;
   }

   static {
      MAX_AGE_NANOS = TimeUnit.MILLISECONDS.toNanos(10000L);
   }
}
