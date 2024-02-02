package org.h2.index;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.value.ExtTypeInfoGeometry;
import org.h2.value.Value;
import org.h2.value.ValueGeometry;
import org.h2.value.ValueNull;

public class IndexCursor implements Cursor {
   private SessionLocal session;
   private Index index;
   private Table table;
   private IndexColumn[] indexColumns;
   private boolean alwaysFalse;
   private SearchRow start;
   private SearchRow end;
   private SearchRow intersects;
   private Cursor cursor;
   private Column inColumn;
   private int inListIndex;
   private Value[] inList;
   private ResultInterface inResult;

   public void setIndex(Index var1) {
      this.index = var1;
      this.table = var1.getTable();
      Column[] var2 = this.table.getColumns();
      this.indexColumns = new IndexColumn[var2.length];
      IndexColumn[] var3 = var1.getIndexColumns();
      if (var3 != null) {
         int var4 = 0;

         for(int var5 = var2.length; var4 < var5; ++var4) {
            int var6 = var1.getColumnIndex(var2[var4]);
            if (var6 >= 0) {
               this.indexColumns[var4] = var3[var6];
            }
         }
      }

   }

   public void prepare(SessionLocal var1, ArrayList<IndexCondition> var2) {
      this.session = var1;
      this.alwaysFalse = false;
      this.start = this.end = null;
      this.inList = null;
      this.inColumn = null;
      this.inResult = null;
      this.intersects = null;
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         IndexCondition var4 = (IndexCondition)var3.next();
         if (var4.isAlwaysFalse()) {
            this.alwaysFalse = true;
            break;
         }

         if (!this.index.isFindUsingFullTableScan()) {
            Column var5 = var4.getColumn();
            if (var4.getCompareType() == 10) {
               if (this.start == null && this.end == null && this.canUseIndexForIn(var5)) {
                  this.inColumn = var5;
                  this.inList = var4.getCurrentValueList(var1);
                  this.inListIndex = 0;
               }
            } else if (var4.getCompareType() == 11) {
               if (this.start == null && this.end == null && this.canUseIndexForIn(var5)) {
                  this.inColumn = var5;
                  this.inResult = var4.getCurrentResult();
               }
            } else {
               Value var6 = var4.getCurrentValue(var1);
               boolean var7 = var4.isStart();
               boolean var8 = var4.isEnd();
               boolean var9 = var4.isSpatialIntersects();
               int var10 = var5.getColumnId();
               if (var10 != -1) {
                  IndexColumn var11 = this.indexColumns[var10];
                  if (var11 != null && (var11.sortType & 1) != 0) {
                     boolean var12 = var7;
                     var7 = var8;
                     var8 = var12;
                  }
               }

               if (var7) {
                  this.start = this.getSearchRow(this.start, var10, var6, true);
               }

               if (var8) {
                  this.end = this.getSearchRow(this.end, var10, var6, false);
               }

               if (var9) {
                  this.intersects = this.getSpatialSearchRow(this.intersects, var10, var6);
               }

               if ((var7 || var8) && !this.canUseIndexFor(this.inColumn)) {
                  this.inColumn = null;
                  this.inList = null;
                  this.inResult = null;
               }
            }
         }
      }

      if (this.inColumn != null) {
         this.start = this.table.getTemplateRow();
      }

   }

   public void find(SessionLocal var1, ArrayList<IndexCondition> var2) {
      this.prepare(var1, var2);
      if (this.inColumn == null) {
         if (!this.alwaysFalse) {
            if (this.intersects != null && this.index instanceof SpatialIndex) {
               this.cursor = ((SpatialIndex)this.index).findByGeometry(this.session, this.start, this.end, this.intersects);
            } else if (this.index != null) {
               this.cursor = this.index.find(this.session, this.start, this.end);
            }
         }

      }
   }

   private boolean canUseIndexForIn(Column var1) {
      return this.inColumn != null ? false : this.canUseIndexFor(var1);
   }

   private boolean canUseIndexFor(Column var1) {
      IndexColumn[] var2 = this.index.getIndexColumns();
      if (var2 == null) {
         return true;
      } else {
         IndexColumn var3 = var2[0];
         return var3 == null || var3.column == var1;
      }
   }

   private SearchRow getSpatialSearchRow(SearchRow var1, int var2, Value var3) {
      if (var1 == null) {
         var1 = this.table.getTemplateRow();
      } else if (((SearchRow)var1).getValue(var2) != null) {
         ValueGeometry var4 = ((SearchRow)var1).getValue(var2).convertToGeometry((ExtTypeInfoGeometry)null);
         var3 = var3.convertToGeometry((ExtTypeInfoGeometry)null).getEnvelopeUnion(var4);
      }

      if (var2 == -1) {
         ((SearchRow)var1).setKey(var3 == ValueNull.INSTANCE ? Long.MIN_VALUE : var3.getLong());
      } else {
         ((SearchRow)var1).setValue(var2, var3);
      }

      return (SearchRow)var1;
   }

   private SearchRow getSearchRow(SearchRow var1, int var2, Value var3, boolean var4) {
      if (var1 == null) {
         var1 = this.table.getTemplateRow();
      } else {
         var3 = this.getMax(((SearchRow)var1).getValue(var2), var3, var4);
      }

      if (var2 == -1) {
         ((SearchRow)var1).setKey(var3 == ValueNull.INSTANCE ? Long.MIN_VALUE : var3.getLong());
      } else {
         ((SearchRow)var1).setValue(var2, var3);
      }

      return (SearchRow)var1;
   }

   private Value getMax(Value var1, Value var2, boolean var3) {
      if (var1 == null) {
         return var2;
      } else if (var2 == null) {
         return var1;
      } else if (var1 == ValueNull.INSTANCE) {
         return var2;
      } else if (var2 == ValueNull.INSTANCE) {
         return var1;
      } else {
         int var4 = this.session.compare(var1, var2);
         if (var4 == 0) {
            return var1;
         } else {
            return var4 > 0 == var3 ? var1 : var2;
         }
      }
   }

   public boolean isAlwaysFalse() {
      return this.alwaysFalse;
   }

   public SearchRow getStart() {
      return this.start;
   }

   public SearchRow getEnd() {
      return this.end;
   }

   public Row get() {
      return this.cursor == null ? null : this.cursor.get();
   }

   public SearchRow getSearchRow() {
      return this.cursor.getSearchRow();
   }

   public boolean next() {
      while(true) {
         if (this.cursor == null) {
            this.nextCursor();
            if (this.cursor == null) {
               return false;
            }
         }

         if (this.cursor.next()) {
            return true;
         }

         this.cursor = null;
      }
   }

   private void nextCursor() {
      Value var1;
      if (this.inList != null) {
         while(this.inListIndex < this.inList.length) {
            var1 = this.inList[this.inListIndex++];
            if (var1 != ValueNull.INSTANCE) {
               this.find(var1);
               break;
            }
         }
      } else if (this.inResult != null) {
         while(this.inResult.next()) {
            var1 = this.inResult.currentRow()[0];
            if (var1 != ValueNull.INSTANCE) {
               this.find(var1);
               break;
            }
         }
      }

   }

   private void find(Value var1) {
      var1 = this.inColumn.convert(this.session, var1);
      int var2 = this.inColumn.getColumnId();
      this.start.setValue(var2, var1);
      this.cursor = this.index.find(this.session, this.start, this.start);
   }

   public boolean previous() {
      throw DbException.getInternalError(this.toString());
   }
}
