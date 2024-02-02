package org.h2.expression.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import org.h2.command.query.QueryOrderBy;
import org.h2.command.query.Select;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.result.SortOrder;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueRow;

public final class Window {
   private ArrayList<Expression> partitionBy;
   private ArrayList<QueryOrderBy> orderBy;
   private WindowFrame frame;
   private String parent;

   public static void appendOrderBy(StringBuilder var0, ArrayList<QueryOrderBy> var1, int var2, boolean var3) {
      if (var1 != null && !var1.isEmpty()) {
         appendOrderByStart(var0);

         for(int var4 = 0; var4 < var1.size(); ++var4) {
            QueryOrderBy var5 = (QueryOrderBy)var1.get(var4);
            if (var4 > 0) {
               var0.append(", ");
            }

            var5.expression.getUnenclosedSQL(var0, var2);
            SortOrder.typeToString(var0, var5.sortType);
         }
      } else if (var3) {
         appendOrderByStart(var0);
         var0.append("NULL");
      }

   }

   private static void appendOrderByStart(StringBuilder var0) {
      if (var0.charAt(var0.length() - 1) != '(') {
         var0.append(' ');
      }

      var0.append("ORDER BY ");
   }

   public Window(String var1, ArrayList<Expression> var2, ArrayList<QueryOrderBy> var3, WindowFrame var4) {
      this.parent = var1;
      this.partitionBy = var2;
      this.orderBy = var3;
      this.frame = var4;
   }

   public void mapColumns(ColumnResolver var1, int var2) {
      this.resolveWindows(var1);
      Iterator var3;
      if (this.partitionBy != null) {
         var3 = this.partitionBy.iterator();

         while(var3.hasNext()) {
            Expression var4 = (Expression)var3.next();
            var4.mapColumns(var1, var2, 1);
         }
      }

      if (this.orderBy != null) {
         var3 = this.orderBy.iterator();

         while(var3.hasNext()) {
            QueryOrderBy var5 = (QueryOrderBy)var3.next();
            var5.expression.mapColumns(var1, var2, 1);
         }
      }

      if (this.frame != null) {
         this.frame.mapColumns(var1, var2, 1);
      }

   }

   private void resolveWindows(ColumnResolver var1) {
      if (this.parent != null) {
         Select var2 = var1.getSelect();

         Window var3;
         while((var3 = var2.getWindow(this.parent)) == null) {
            var2 = var2.getParentSelect();
            if (var2 == null) {
               throw DbException.get(90136, this.parent);
            }
         }

         var3.resolveWindows(var1);
         if (this.partitionBy == null) {
            this.partitionBy = var3.partitionBy;
         }

         if (this.orderBy == null) {
            this.orderBy = var3.orderBy;
         }

         if (this.frame == null) {
            this.frame = var3.frame;
         }

         this.parent = null;
      }

   }

   public void optimize(SessionLocal var1) {
      if (this.partitionBy != null) {
         ListIterator var2 = this.partitionBy.listIterator();

         while(var2.hasNext()) {
            Expression var3 = ((Expression)var2.next()).optimize(var1);
            if (var3.isConstant()) {
               var2.remove();
            } else {
               var2.set(var3);
            }
         }

         if (this.partitionBy.isEmpty()) {
            this.partitionBy = null;
         }
      }

      if (this.orderBy != null) {
         Iterator var5 = this.orderBy.iterator();

         while(var5.hasNext()) {
            QueryOrderBy var6 = (QueryOrderBy)var5.next();
            Expression var4 = var6.expression.optimize(var1);
            if (var4.isConstant()) {
               var5.remove();
            } else {
               var6.expression = var4;
            }
         }

         if (this.orderBy.isEmpty()) {
            this.orderBy = null;
         }
      }

      if (this.frame != null) {
         this.frame.optimize(var1);
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      Iterator var3;
      if (this.partitionBy != null) {
         var3 = this.partitionBy.iterator();

         while(var3.hasNext()) {
            Expression var4 = (Expression)var3.next();
            var4.setEvaluatable(var1, var2);
         }
      }

      if (this.orderBy != null) {
         var3 = this.orderBy.iterator();

         while(var3.hasNext()) {
            QueryOrderBy var5 = (QueryOrderBy)var3.next();
            var5.expression.setEvaluatable(var1, var2);
         }
      }

   }

   public ArrayList<QueryOrderBy> getOrderBy() {
      return this.orderBy;
   }

   public WindowFrame getWindowFrame() {
      return this.frame;
   }

   public boolean isOrdered() {
      if (this.orderBy != null) {
         return true;
      } else if (this.frame != null && this.frame.getUnits() == WindowFrameUnits.ROWS) {
         if (this.frame.getStarting().getType() == WindowFrameBoundType.UNBOUNDED_PRECEDING) {
            WindowFrameBound var1 = this.frame.getFollowing();
            if (var1 != null && var1.getType() == WindowFrameBoundType.UNBOUNDED_FOLLOWING) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public Value getCurrentKey(SessionLocal var1) {
      if (this.partitionBy == null) {
         return null;
      } else {
         int var2 = this.partitionBy.size();
         if (var2 == 1) {
            return ((Expression)this.partitionBy.get(0)).getValue(var1);
         } else {
            Value[] var3 = new Value[var2];

            for(int var4 = 0; var4 < var2; ++var4) {
               Expression var5 = (Expression)this.partitionBy.get(var4);
               var3[var4] = var5.getValue(var1);
            }

            return ValueRow.get(var3);
         }
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2, boolean var3) {
      var1.append("OVER (");
      if (this.partitionBy != null) {
         var1.append("PARTITION BY ");

         for(int var4 = 0; var4 < this.partitionBy.size(); ++var4) {
            if (var4 > 0) {
               var1.append(", ");
            }

            ((Expression)this.partitionBy.get(var4)).getUnenclosedSQL(var1, var2);
         }
      }

      appendOrderBy(var1, this.orderBy, var2, var3);
      if (this.frame != null) {
         if (var1.charAt(var1.length() - 1) != '(') {
            var1.append(' ');
         }

         this.frame.getSQL(var1, var2);
      }

      return var1.append(')');
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      Iterator var3;
      if (this.partitionBy != null) {
         var3 = this.partitionBy.iterator();

         while(var3.hasNext()) {
            Expression var4 = (Expression)var3.next();
            var4.updateAggregate(var1, var2);
         }
      }

      if (this.orderBy != null) {
         var3 = this.orderBy.iterator();

         while(var3.hasNext()) {
            QueryOrderBy var5 = (QueryOrderBy)var3.next();
            var5.expression.updateAggregate(var1, var2);
         }
      }

      if (this.frame != null) {
         this.frame.updateAggregate(var1, var2);
      }

   }

   public String toString() {
      return this.getSQL(new StringBuilder(), 3, false).toString();
   }
}
