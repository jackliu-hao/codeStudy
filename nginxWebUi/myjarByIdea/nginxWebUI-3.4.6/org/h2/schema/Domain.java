package org.h2.schema;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintDomain;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.table.ColumnTemplate;
import org.h2.table.Table;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class Domain extends SchemaObject implements ColumnTemplate {
   private TypeInfo type;
   private Domain domain;
   private Expression defaultExpression;
   private Expression onUpdateExpression;
   private ArrayList<ConstraintDomain> constraints;

   public Domain(Schema var1, int var2, String var3) {
      super(var1, var2, var3, 8);
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      throw DbException.getInternalError(this.toString());
   }

   public String getDropSQL() {
      StringBuilder var1 = new StringBuilder("DROP DOMAIN IF EXISTS ");
      return this.getSQL(var1, 0).toString();
   }

   public String getCreateSQL() {
      StringBuilder var1 = this.getSQL(new StringBuilder("CREATE DOMAIN "), 0).append(" AS ");
      if (this.domain != null) {
         this.domain.getSQL(var1, 0);
      } else {
         this.type.getSQL(var1, 0);
      }

      if (this.defaultExpression != null) {
         this.defaultExpression.getUnenclosedSQL(var1.append(" DEFAULT "), 0);
      }

      if (this.onUpdateExpression != null) {
         this.onUpdateExpression.getUnenclosedSQL(var1.append(" ON UPDATE "), 0);
      }

      return var1.toString();
   }

   public void setDataType(TypeInfo var1) {
      this.type = var1;
   }

   public TypeInfo getDataType() {
      return this.type;
   }

   public void setDomain(Domain var1) {
      this.domain = var1;
   }

   public Domain getDomain() {
      return this.domain;
   }

   public void setDefaultExpression(SessionLocal var1, Expression var2) {
      if (var2 != null) {
         var2 = ((Expression)var2).optimize(var1);
         if (((Expression)var2).isConstant()) {
            var2 = ValueExpression.get(((Expression)var2).getValue(var1));
         }
      }

      this.defaultExpression = (Expression)var2;
   }

   public Expression getDefaultExpression() {
      return this.defaultExpression;
   }

   public Expression getEffectiveDefaultExpression() {
      return this.defaultExpression != null ? this.defaultExpression : (this.domain != null ? this.domain.getEffectiveDefaultExpression() : null);
   }

   public String getDefaultSQL() {
      return this.defaultExpression == null ? null : this.defaultExpression.getUnenclosedSQL(new StringBuilder(), 0).toString();
   }

   public void setOnUpdateExpression(SessionLocal var1, Expression var2) {
      if (var2 != null) {
         var2 = ((Expression)var2).optimize(var1);
         if (((Expression)var2).isConstant()) {
            var2 = ValueExpression.get(((Expression)var2).getValue(var1));
         }
      }

      this.onUpdateExpression = (Expression)var2;
   }

   public Expression getOnUpdateExpression() {
      return this.onUpdateExpression;
   }

   public Expression getEffectiveOnUpdateExpression() {
      return this.onUpdateExpression != null ? this.onUpdateExpression : (this.domain != null ? this.domain.getEffectiveOnUpdateExpression() : null);
   }

   public String getOnUpdateSQL() {
      return this.onUpdateExpression == null ? null : this.onUpdateExpression.getUnenclosedSQL(new StringBuilder(), 0).toString();
   }

   public void prepareExpressions(SessionLocal var1) {
      if (this.defaultExpression != null) {
         this.defaultExpression = this.defaultExpression.optimize(var1);
      }

      if (this.onUpdateExpression != null) {
         this.onUpdateExpression = this.onUpdateExpression.optimize(var1);
      }

      if (this.domain != null) {
         this.domain.prepareExpressions(var1);
      }

   }

   public void addConstraint(ConstraintDomain var1) {
      if (this.constraints == null) {
         this.constraints = Utils.newSmallArrayList();
      }

      if (!this.constraints.contains(var1)) {
         this.constraints.add(var1);
      }

   }

   public ArrayList<ConstraintDomain> getConstraints() {
      return this.constraints;
   }

   public void removeConstraint(Constraint var1) {
      if (this.constraints != null) {
         this.constraints.remove(var1);
      }

   }

   public int getType() {
      return 12;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      if (this.constraints != null && !this.constraints.isEmpty()) {
         ConstraintDomain[] var2 = (ConstraintDomain[])this.constraints.toArray(new ConstraintDomain[0]);
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ConstraintDomain var5 = var2[var4];
            this.database.removeSchemaObject(var1, var5);
         }

         this.constraints = null;
      }

      this.database.removeMeta(var1, this.getId());
   }

   public void checkConstraints(SessionLocal var1, Value var2) {
      if (this.constraints != null) {
         Iterator var3 = this.constraints.iterator();

         while(var3.hasNext()) {
            ConstraintDomain var4 = (ConstraintDomain)var3.next();
            var4.check(var1, var2);
         }
      }

      if (this.domain != null) {
         this.domain.checkConstraints(var1, var2);
      }

   }
}
