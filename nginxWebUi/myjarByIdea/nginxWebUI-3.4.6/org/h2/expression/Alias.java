package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.ParserUtil;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class Alias extends Expression {
   private final String alias;
   private Expression expr;
   private final boolean aliasColumnName;

   public Alias(Expression var1, String var2, boolean var3) {
      this.expr = var1;
      this.alias = var2;
      this.aliasColumnName = var3;
   }

   public Expression getNonAliasExpression() {
      return this.expr;
   }

   public Value getValue(SessionLocal var1) {
      return this.expr.getValue(var1);
   }

   public TypeInfo getType() {
      return this.expr.getType();
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.expr.mapColumns(var1, var2, var3);
   }

   public Expression optimize(SessionLocal var1) {
      this.expr = this.expr.optimize(var1);
      return this;
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.expr.setEvaluatable(var1, var2);
   }

   public boolean isIdentity() {
      return this.expr.isIdentity();
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      this.expr.getUnenclosedSQL(var1, var2).append(" AS ");
      return ParserUtil.quoteIdentifier(var1, this.alias, var2);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.expr.updateAggregate(var1, var2);
   }

   public String getAlias(SessionLocal var1, int var2) {
      return this.alias;
   }

   public String getColumnNameForView(SessionLocal var1, int var2) {
      return this.alias;
   }

   public int getNullable() {
      return this.expr.getNullable();
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.expr.isEverything(var1);
   }

   public int getCost() {
      return this.expr.getCost();
   }

   public String getSchemaName() {
      return this.aliasColumnName ? null : this.expr.getSchemaName();
   }

   public String getTableName() {
      return this.aliasColumnName ? null : this.expr.getTableName();
   }

   public String getColumnName(SessionLocal var1, int var2) {
      return this.expr instanceof ExpressionColumn && !this.aliasColumnName ? this.expr.getColumnName(var1, var2) : this.alias;
   }
}
