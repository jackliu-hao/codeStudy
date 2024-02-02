package org.h2.constraint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.util.StringUtils;

public class ConstraintUnique extends Constraint {
   private Index index;
   private boolean indexOwner;
   private IndexColumn[] columns;
   private final boolean primaryKey;

   public ConstraintUnique(Schema var1, int var2, String var3, Table var4, boolean var5) {
      super(var1, var2, var3, var4);
      this.primaryKey = var5;
   }

   public Constraint.Type getConstraintType() {
      return this.primaryKey ? Constraint.Type.PRIMARY_KEY : Constraint.Type.UNIQUE;
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      return this.getCreateSQLForCopy(var1, var2, true);
   }

   private String getCreateSQLForCopy(Table var1, String var2, boolean var3) {
      StringBuilder var4 = new StringBuilder("ALTER TABLE ");
      var1.getSQL(var4, 0).append(" ADD CONSTRAINT ");
      if (var1.isHidden()) {
         var4.append("IF NOT EXISTS ");
      }

      var4.append(var2);
      if (this.comment != null) {
         var4.append(" COMMENT ");
         StringUtils.quoteStringSQL(var4, this.comment);
      }

      var4.append(' ').append(this.getConstraintType().getSqlName()).append('(');
      IndexColumn.writeColumns(var4, this.columns, 0).append(')');
      if (var3 && this.indexOwner && var1 == this.table) {
         var4.append(" INDEX ");
         this.index.getSQL(var4, 0);
      }

      return var4.toString();
   }

   public String getCreateSQLWithoutIndexes() {
      return this.getCreateSQLForCopy(this.table, this.getSQL(0), false);
   }

   public String getCreateSQL() {
      return this.getCreateSQLForCopy(this.table, this.getSQL(0));
   }

   public void setColumns(IndexColumn[] var1) {
      this.columns = var1;
   }

   public IndexColumn[] getColumns() {
      return this.columns;
   }

   public void setIndex(Index var1, boolean var2) {
      this.index = var1;
      this.indexOwner = var2;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      ArrayList var2 = this.table.getConstraints();
      if (var2 != null) {
         var2 = new ArrayList(this.table.getConstraints());
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Constraint var4 = (Constraint)var3.next();
            if (var4.getReferencedConstraint() == this) {
               this.database.removeSchemaObject(var1, var4);
            }
         }
      }

      this.table.removeConstraint(this);
      if (this.indexOwner) {
         this.table.removeIndexOrTransferOwnership(var1, this.index);
      }

      this.database.removeMeta(var1, this.getId());
      this.index = null;
      this.columns = null;
      this.table = null;
      this.invalidate();
   }

   public void checkRow(SessionLocal var1, Table var2, Row var3, Row var4) {
   }

   public boolean usesIndex(Index var1) {
      return var1 == this.index;
   }

   public void setIndexOwner(Index var1) {
      this.indexOwner = true;
   }

   public HashSet<Column> getReferencedColumns(Table var1) {
      HashSet var2 = new HashSet();
      IndexColumn[] var3 = this.columns;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         IndexColumn var6 = var3[var5];
         var2.add(var6.column);
      }

      return var2;
   }

   public boolean isBefore() {
      return true;
   }

   public void checkExistingData(SessionLocal var1) {
   }

   public Index getIndex() {
      return this.index;
   }

   public void rebuild() {
   }
}
