package org.h2.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.h2.command.query.QueryOrderBy;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.mode.DefaultNullOrdering;
import org.h2.table.Column;
import org.h2.table.TableFilter;
import org.h2.util.Utils;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

public final class SortOrder implements Comparator<Value[]> {
   public static final int ASCENDING = 0;
   public static final int DESCENDING = 1;
   public static final int NULLS_FIRST = 2;
   public static final int NULLS_LAST = 4;
   private final SessionLocal session;
   private final int[] queryColumnIndexes;
   private final int[] sortTypes;
   private final ArrayList<QueryOrderBy> orderList;

   public SortOrder(SessionLocal var1, int[] var2) {
      this(var1, var2, new int[var2.length], (ArrayList)null);
   }

   public SortOrder(SessionLocal var1, int[] var2, int[] var3, ArrayList<QueryOrderBy> var4) {
      this.session = var1;
      this.queryColumnIndexes = var2;
      this.sortTypes = var3;
      this.orderList = var4;
   }

   public StringBuilder getSQL(StringBuilder var1, Expression[] var2, int var3, int var4) {
      int var5 = 0;
      int[] var6 = this.queryColumnIndexes;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         int var9 = var6[var8];
         if (var5 > 0) {
            var1.append(", ");
         }

         if (var9 < var3) {
            var1.append(var9 + 1);
         } else {
            var2[var9].getUnenclosedSQL(var1, var4);
         }

         typeToString(var1, this.sortTypes[var5++]);
      }

      return var1;
   }

   public static void typeToString(StringBuilder var0, int var1) {
      if ((var1 & 1) != 0) {
         var0.append(" DESC");
      }

      if ((var1 & 2) != 0) {
         var0.append(" NULLS FIRST");
      } else if ((var1 & 4) != 0) {
         var0.append(" NULLS LAST");
      }

   }

   public int compare(Value[] var1, Value[] var2) {
      int var3 = 0;

      for(int var4 = this.queryColumnIndexes.length; var3 < var4; ++var3) {
         int var5 = this.queryColumnIndexes[var3];
         int var6 = this.sortTypes[var3];
         Value var7 = var1[var5];
         Value var8 = var2[var5];
         boolean var9 = var7 == ValueNull.INSTANCE;
         boolean var10 = var8 == ValueNull.INSTANCE;
         if (!var9 && !var10) {
            int var11 = this.session.compare(var7, var8);
            if (var11 != 0) {
               return (var6 & 1) == 0 ? var11 : -var11;
            }
         } else if (var9 != var10) {
            return this.session.getDatabase().getDefaultNullOrdering().compareNull(var9, var6);
         }
      }

      return 0;
   }

   public void sort(ArrayList<Value[]> var1) {
      var1.sort(this);
   }

   public void sort(ArrayList<Value[]> var1, int var2, int var3) {
      if (var3 == 1 && var2 == 0) {
         var1.set(0, Collections.min(var1, this));
      } else {
         Value[][] var4 = (Value[][])var1.toArray(new Value[0][]);
         Utils.sortTopN(var4, var2, var3, this);

         for(int var5 = var2; var5 < var3; ++var5) {
            var1.set(var5, var4[var5]);
         }

      }
   }

   public int[] getQueryColumnIndexes() {
      return this.queryColumnIndexes;
   }

   public Column getColumn(int var1, TableFilter var2) {
      if (this.orderList == null) {
         return null;
      } else {
         QueryOrderBy var3 = (QueryOrderBy)this.orderList.get(var1);
         Expression var4 = var3.expression;
         if (var4 == null) {
            return null;
         } else {
            var4 = var4.getNonAliasExpression();
            if (var4.isConstant()) {
               return null;
            } else if (!(var4 instanceof ExpressionColumn)) {
               return null;
            } else {
               ExpressionColumn var5 = (ExpressionColumn)var4;
               return var5.getTableFilter() != var2 ? null : var5.getColumn();
            }
         }
      }
   }

   public int[] getSortTypes() {
      return this.sortTypes;
   }

   public ArrayList<QueryOrderBy> getOrderList() {
      return this.orderList;
   }

   public int[] getSortTypesWithNullOrdering() {
      return addNullOrdering(this.session.getDatabase(), (int[])this.sortTypes.clone());
   }

   public static int[] addNullOrdering(Database var0, int[] var1) {
      DefaultNullOrdering var2 = var0.getDefaultNullOrdering();
      int var3 = 0;

      for(int var4 = var1.length; var3 < var4; ++var3) {
         var1[var3] = var2.addExplicitNullOrdering(var1[var3]);
      }

      return var1;
   }

   public Comparator<Value> getRowValueComparator() {
      return (var1, var2) -> {
         return this.compare(((ValueRow)var1).getList(), ((ValueRow)var2).getList());
      };
   }
}
