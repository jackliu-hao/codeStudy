package org.h2.command.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.analysis.DataAnalysisOperation;
import org.h2.expression.analysis.PartitionData;
import org.h2.value.Value;
import org.h2.value.ValueRow;

public abstract class SelectGroups {
   final SessionLocal session;
   final ArrayList<Expression> expressions;
   Object[] currentGroupByExprData;
   private final HashMap<Expression, Integer> exprToIndexInGroupByData = new HashMap();
   private final HashMap<DataAnalysisOperation, PartitionData> windowData = new HashMap();
   private final HashMap<DataAnalysisOperation, TreeMap<Value, PartitionData>> windowPartitionData = new HashMap();
   int currentGroupRowId;

   public static SelectGroups getInstance(SessionLocal var0, ArrayList<Expression> var1, boolean var2, int[] var3) {
      return (SelectGroups)(var2 ? new Grouped(var0, var1, var3) : new Plain(var0, var1));
   }

   SelectGroups(SessionLocal var1, ArrayList<Expression> var2) {
      this.session = var1;
      this.expressions = var2;
   }

   public boolean isCurrentGroup() {
      return this.currentGroupByExprData != null;
   }

   public final Object getCurrentGroupExprData(Expression var1) {
      Integer var2 = (Integer)this.exprToIndexInGroupByData.get(var1);
      return var2 == null ? null : this.currentGroupByExprData[var2];
   }

   public final void setCurrentGroupExprData(Expression var1, Object var2) {
      Integer var3 = (Integer)this.exprToIndexInGroupByData.get(var1);
      if (var3 != null) {
         assert this.currentGroupByExprData[var3] == null;

         this.currentGroupByExprData[var3] = var2;
      } else {
         var3 = this.exprToIndexInGroupByData.size();
         this.exprToIndexInGroupByData.put(var1, var3);
         if (var3 >= this.currentGroupByExprData.length) {
            this.currentGroupByExprData = Arrays.copyOf(this.currentGroupByExprData, this.currentGroupByExprData.length * 2);
            this.updateCurrentGroupExprData();
         }

         this.currentGroupByExprData[var3] = var2;
      }
   }

   final Object[] createRow() {
      return new Object[Math.max(this.exprToIndexInGroupByData.size(), this.expressions.size())];
   }

   public final PartitionData getWindowExprData(DataAnalysisOperation var1, Value var2) {
      if (var2 == null) {
         return (PartitionData)this.windowData.get(var1);
      } else {
         TreeMap var3 = (TreeMap)this.windowPartitionData.get(var1);
         return var3 != null ? (PartitionData)var3.get(var2) : null;
      }
   }

   public final void setWindowExprData(DataAnalysisOperation var1, Value var2, PartitionData var3) {
      if (var2 == null) {
         Object var4 = this.windowData.put(var1, var3);

         assert var4 == null;
      } else {
         TreeMap var5 = (TreeMap)this.windowPartitionData.get(var1);
         if (var5 == null) {
            var5 = new TreeMap(this.session.getDatabase().getCompareMode());
            this.windowPartitionData.put(var1, var5);
         }

         var5.put(var2, var3);
      }

   }

   abstract void updateCurrentGroupExprData();

   public int getCurrentGroupRowId() {
      return this.currentGroupRowId;
   }

   public void reset() {
      this.currentGroupByExprData = null;
      this.exprToIndexInGroupByData.clear();
      this.windowData.clear();
      this.windowPartitionData.clear();
      this.currentGroupRowId = 0;
   }

   public abstract void nextSource();

   public void done() {
      this.currentGroupRowId = 0;
   }

   public abstract ValueRow next();

   public void remove() {
      throw new UnsupportedOperationException();
   }

   public void resetLazy() {
      this.currentGroupByExprData = null;
      this.currentGroupRowId = 0;
   }

   public void nextLazyGroup() {
      this.currentGroupByExprData = new Object[Math.max(this.exprToIndexInGroupByData.size(), this.expressions.size())];
   }

   public void nextLazyRow() {
      ++this.currentGroupRowId;
   }

   private static final class Plain extends SelectGroups {
      private ArrayList<Object[]> rows;
      private Iterator<Object[]> cursor;

      Plain(SessionLocal var1, ArrayList<Expression> var2) {
         super(var1, var2);
      }

      public void reset() {
         super.reset();
         this.rows = new ArrayList();
         this.cursor = null;
      }

      public void nextSource() {
         Object[] var1 = this.createRow();
         this.rows.add(var1);
         this.currentGroupByExprData = var1;
         ++this.currentGroupRowId;
      }

      void updateCurrentGroupExprData() {
         this.rows.set(this.rows.size() - 1, this.currentGroupByExprData);
      }

      public void done() {
         super.done();
         this.cursor = this.rows.iterator();
      }

      public ValueRow next() {
         if (this.cursor.hasNext()) {
            this.currentGroupByExprData = (Object[])this.cursor.next();
            ++this.currentGroupRowId;
            return ValueRow.EMPTY;
         } else {
            return null;
         }
      }
   }

   private static final class Grouped extends SelectGroups {
      private final int[] groupIndex;
      private TreeMap<ValueRow, Object[]> groupByData;
      private ValueRow currentGroupsKey;
      private Iterator<Map.Entry<ValueRow, Object[]>> cursor;

      Grouped(SessionLocal var1, ArrayList<Expression> var2, int[] var3) {
         super(var1, var2);
         this.groupIndex = var3;
      }

      public void reset() {
         super.reset();
         this.groupByData = new TreeMap(this.session.getDatabase().getCompareMode());
         this.currentGroupsKey = null;
         this.cursor = null;
      }

      public void nextSource() {
         if (this.groupIndex == null) {
            this.currentGroupsKey = ValueRow.EMPTY;
         } else {
            Value[] var1 = new Value[this.groupIndex.length];

            for(int var2 = 0; var2 < this.groupIndex.length; ++var2) {
               int var3 = this.groupIndex[var2];
               Expression var4 = (Expression)this.expressions.get(var3);
               var1[var2] = var4.getValue(this.session);
            }

            this.currentGroupsKey = ValueRow.get(var1);
         }

         Object[] var5 = (Object[])this.groupByData.get(this.currentGroupsKey);
         if (var5 == null) {
            var5 = this.createRow();
            this.groupByData.put(this.currentGroupsKey, var5);
         }

         this.currentGroupByExprData = var5;
         ++this.currentGroupRowId;
      }

      void updateCurrentGroupExprData() {
         if (this.currentGroupsKey != null) {
            this.groupByData.put(this.currentGroupsKey, this.currentGroupByExprData);
         }

      }

      public void done() {
         super.done();
         if (this.groupIndex == null && this.groupByData.size() == 0) {
            this.groupByData.put(ValueRow.EMPTY, this.createRow());
         }

         this.cursor = this.groupByData.entrySet().iterator();
      }

      public ValueRow next() {
         if (this.cursor.hasNext()) {
            Map.Entry var1 = (Map.Entry)this.cursor.next();
            this.currentGroupByExprData = (Object[])var1.getValue();
            ++this.currentGroupRowId;
            return (ValueRow)var1.getKey();
         } else {
            return null;
         }
      }

      public void remove() {
         this.cursor.remove();
         this.currentGroupByExprData = null;
         --this.currentGroupRowId;
      }

      public void resetLazy() {
         super.resetLazy();
         this.currentGroupsKey = null;
      }
   }
}
