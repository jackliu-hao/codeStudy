package org.h2.constraint;

import java.util.HashSet;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.table.Column;
import org.h2.table.Table;

public abstract class Constraint extends SchemaObject implements Comparable<Constraint> {
   protected Table table;

   Constraint(Schema var1, int var2, String var3, Table var4) {
      super(var1, var2, var3, 1);
      this.table = var4;
      if (var4 != null) {
         this.setTemporary(var4.isTemporary());
      }

   }

   public abstract Type getConstraintType();

   public abstract void checkRow(SessionLocal var1, Table var2, Row var3, Row var4);

   public abstract boolean usesIndex(Index var1);

   public abstract void setIndexOwner(Index var1);

   public abstract HashSet<Column> getReferencedColumns(Table var1);

   public Expression getExpression() {
      return null;
   }

   public abstract String getCreateSQLWithoutIndexes();

   public abstract boolean isBefore();

   public abstract void checkExistingData(SessionLocal var1);

   public abstract void rebuild();

   public Index getIndex() {
      return null;
   }

   public ConstraintUnique getReferencedConstraint() {
      return null;
   }

   public int getType() {
      return 5;
   }

   public Table getTable() {
      return this.table;
   }

   public Table getRefTable() {
      return this.table;
   }

   public int compareTo(Constraint var1) {
      return this == var1 ? 0 : Integer.compare(this.getConstraintType().ordinal(), var1.getConstraintType().ordinal());
   }

   public boolean isHidden() {
      return this.table != null && this.table.isHidden();
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return true;
   }

   public static enum Type {
      CHECK,
      PRIMARY_KEY,
      UNIQUE,
      REFERENTIAL,
      DOMAIN;

      public String getSqlName() {
         if (this == PRIMARY_KEY) {
            return "PRIMARY KEY";
         } else {
            return this == REFERENTIAL ? "FOREIGN KEY" : this.name();
         }
      }
   }
}
