package org.h2.expression;

import java.util.List;
import org.h2.engine.SessionLocal;
import org.h2.expression.function.NamedExpression;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.HasSQL;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Typed;
import org.h2.value.Value;

public abstract class Expression implements HasSQL, Typed {
   public static final int MAP_INITIAL = 0;
   public static final int MAP_IN_WINDOW = 1;
   public static final int MAP_IN_AGGREGATE = 2;
   public static final int AUTO_PARENTHESES = 0;
   public static final int WITH_PARENTHESES = 1;
   public static final int WITHOUT_PARENTHESES = 2;
   private boolean addedToFilter;

   public static StringBuilder writeExpressions(StringBuilder var0, List<? extends Expression> var1, int var2) {
      int var3 = 0;

      for(int var4 = var1.size(); var3 < var4; ++var3) {
         if (var3 > 0) {
            var0.append(", ");
         }

         ((Expression)var1.get(var3)).getUnenclosedSQL(var0, var2);
      }

      return var0;
   }

   public static StringBuilder writeExpressions(StringBuilder var0, Expression[] var1, int var2) {
      int var3 = 0;

      for(int var4 = var1.length; var3 < var4; ++var3) {
         if (var3 > 0) {
            var0.append(", ");
         }

         Expression var5 = var1[var3];
         if (var5 == null) {
            var0.append("DEFAULT");
         } else {
            var5.getUnenclosedSQL(var0, var2);
         }
      }

      return var0;
   }

   public abstract Value getValue(SessionLocal var1);

   public abstract TypeInfo getType();

   public abstract void mapColumns(ColumnResolver var1, int var2, int var3);

   public abstract Expression optimize(SessionLocal var1);

   public final Expression optimizeCondition(SessionLocal var1) {
      Expression var2 = this.optimize(var1);
      if (var2.isConstant()) {
         return var2.getBooleanValue(var1) ? null : ValueExpression.FALSE;
      } else {
         return var2;
      }
   }

   public abstract void setEvaluatable(TableFilter var1, boolean var2);

   public final String getSQL(int var1) {
      return this.getSQL(new StringBuilder(), var1, 0).toString();
   }

   public final StringBuilder getSQL(StringBuilder var1, int var2) {
      return this.getSQL(var1, var2, 0);
   }

   public final String getSQL(int var1, int var2) {
      return this.getSQL(new StringBuilder(), var1, var2).toString();
   }

   public final StringBuilder getSQL(StringBuilder var1, int var2, int var3) {
      return var3 != 1 && (var3 == 2 || !this.needParentheses()) ? this.getUnenclosedSQL(var1, var2) : this.getUnenclosedSQL(var1.append('('), var2).append(')');
   }

   public boolean needParentheses() {
      return false;
   }

   public final StringBuilder getEnclosedSQL(StringBuilder var1, int var2) {
      return this.getUnenclosedSQL(var1.append('('), var2).append(')');
   }

   public abstract StringBuilder getUnenclosedSQL(StringBuilder var1, int var2);

   public abstract void updateAggregate(SessionLocal var1, int var2);

   public abstract boolean isEverything(ExpressionVisitor var1);

   public abstract int getCost();

   public Expression getNotIfPossible(SessionLocal var1) {
      return null;
   }

   public boolean isConstant() {
      return false;
   }

   public boolean isNullConstant() {
      return false;
   }

   public boolean isValueSet() {
      return false;
   }

   public boolean isIdentity() {
      return false;
   }

   public boolean getBooleanValue(SessionLocal var1) {
      return this.getValue(var1).isTrue();
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
   }

   public String getColumnName(SessionLocal var1, int var2) {
      return this.getAlias(var1, var2);
   }

   public String getSchemaName() {
      return null;
   }

   public String getTableName() {
      return null;
   }

   public int getNullable() {
      return 2;
   }

   public String getTableAlias() {
      return null;
   }

   public String getAlias(SessionLocal var1, int var2) {
      switch (var1.getMode().expressionNames) {
         case EMPTY:
            return "";
         case NUMBER:
            return Integer.toString(var2 + 1);
         case POSTGRESQL_STYLE:
            if (this instanceof NamedExpression) {
               return StringUtils.toLowerEnglish(((NamedExpression)this).getName());
            }

            return "?column?";
         default:
            String var3 = this.getSQL(5, 2);
            if (var3.length() <= 256) {
               return var3;
            }
         case C_NUMBER:
            return "C" + (var2 + 1);
      }
   }

   public String getColumnNameForView(SessionLocal var1, int var2) {
      switch (var1.getMode().viewExpressionNames) {
         case AS_IS:
         default:
            return this.getAlias(var1, var2);
         case EXCEPTION:
            throw DbException.get(90156, this.getTraceSQL());
         case MYSQL_STYLE:
            String var3 = this.getSQL(5, 2);
            if (var3.length() > 64) {
               var3 = "Name_exp_" + (var2 + 1);
            }

            return var3;
      }
   }

   public Expression getNonAliasExpression() {
      return this;
   }

   public void addFilterConditions(TableFilter var1) {
      if (!this.addedToFilter && this.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
         var1.addFilterCondition(this, false);
         this.addedToFilter = true;
      }

   }

   public String toString() {
      return this.getTraceSQL();
   }

   public int getSubexpressionCount() {
      return 0;
   }

   public Expression getSubexpression(int var1) {
      throw new IndexOutOfBoundsException();
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      return var1.compareWithNull(var2, this.getValue(var1), true) == 0;
   }

   public StringBuilder getWhenSQL(StringBuilder var1, int var2) {
      return this.getUnenclosedSQL(var1.append(' '), var2);
   }

   public boolean isWhenConditionOperand() {
      return false;
   }
}
