package org.h2.index;

import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.IndexColumn;
import org.h2.table.RangeTable;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueBigint;

public class RangeIndex extends VirtualTableIndex {
   private final RangeTable rangeTable;

   public RangeIndex(RangeTable var1, IndexColumn[] var2) {
      super(var1, "RANGE_INDEX", var2);
      this.rangeTable = var1;
   }

   public Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3) {
      long var4 = this.rangeTable.getMin(var1);
      long var6 = this.rangeTable.getMax(var1);
      long var8 = this.rangeTable.getStep(var1);
      if (var8 == 0L) {
         throw DbException.get(90142);
      } else {
         long var10;
         if (var2 != null) {
            try {
               var10 = var2.getValue(0).getLong();
               if (var8 > 0L) {
                  if (var10 > var4) {
                     var4 += (var10 - var4 + var8 - 1L) / var8 * var8;
                  }
               } else if (var10 > var6) {
                  var6 = var10;
               }
            } catch (DbException var13) {
            }
         }

         if (var3 != null) {
            try {
               var10 = var3.getValue(0).getLong();
               if (var8 > 0L) {
                  if (var10 < var6) {
                     var6 = var10;
                  }
               } else if (var10 < var4) {
                  var4 -= (var4 - var10 - var8 - 1L) / var8 * var8;
               }
            } catch (DbException var12) {
            }
         }

         return new RangeCursor(var4, var6, var8);
      }
   }

   public double getCost(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      return 1.0;
   }

   public String getCreateSQL() {
      return null;
   }

   public boolean canGetFirstOrLast() {
      return true;
   }

   public Cursor findFirstOrLast(SessionLocal var1, boolean var2) {
      long var3 = this.rangeTable.getMin(var1);
      long var5 = this.rangeTable.getMax(var1);
      long var7 = this.rangeTable.getStep(var1);
      if (var7 == 0L) {
         throw DbException.get(90142);
      } else {
         SingleRowCursor var10000;
         Row var10002;
         label34: {
            label29: {
               var10000 = new SingleRowCursor;
               if (var7 > 0L) {
                  if (var3 <= var5) {
                     break label29;
                  }
               } else if (var3 >= var5) {
                  break label29;
               }

               var10002 = null;
               break label34;
            }

            var10002 = Row.get(new Value[]{ValueBigint.get(var2 ^ var3 >= var5 ? var3 : var5)}, 1);
         }

         var10000.<init>(var10002);
         return var10000;
      }
   }

   public String getPlanSQL() {
      return "range index";
   }
}
