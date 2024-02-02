package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.constraint.ConstraintActionType;
import org.h2.constraint.ConstraintDomain;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.ColumnTemplate;
import org.h2.table.Table;

public class DropDomain extends AlterDomain {
   private ConstraintActionType dropAction;

   public DropDomain(SessionLocal var1, Schema var2) {
      super(var1, var2);
      this.dropAction = var1.getDatabase().getSettings().dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
   }

   public void setDropAction(ConstraintActionType var1) {
      this.dropAction = var1;
   }

   long update(Schema var1, Domain var2) {
      forAllDependencies(this.session, var2, this::copyColumn, this::copyDomain, true);
      this.session.getDatabase().removeSchemaObject(this.session, var2);
      return 0L;
   }

   private boolean copyColumn(Domain var1, Column var2) {
      Table var3 = var2.getTable();
      if (this.dropAction == ConstraintActionType.RESTRICT) {
         throw DbException.get(90107, this.domainName, var3.getCreateSQL());
      } else {
         String var4 = var2.getName();
         ArrayList var5 = var1.getConstraints();
         if (var5 != null && !var5.isEmpty()) {
            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
               ConstraintDomain var7 = (ConstraintDomain)var6.next();
               Expression var8 = var7.getCheckConstraint(this.session, var4);
               AlterTableAddConstraint var9 = new AlterTableAddConstraint(this.session, var3.getSchema(), 3, false);
               var9.setTableName(var3.getName());
               var9.setCheckExpression(var8);
               var9.update();
            }
         }

         copyExpressions(this.session, var1, var2);
         return true;
      }
   }

   private boolean copyDomain(Domain var1, Domain var2) {
      if (this.dropAction == ConstraintActionType.RESTRICT) {
         throw DbException.get(90107, this.domainName, var2.getTraceSQL());
      } else {
         ArrayList var3 = var1.getConstraints();
         if (var3 != null && !var3.isEmpty()) {
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               ConstraintDomain var5 = (ConstraintDomain)var4.next();
               Expression var6 = var5.getCheckConstraint(this.session, (String)null);
               AlterDomainAddConstraint var7 = new AlterDomainAddConstraint(this.session, var2.getSchema(), false);
               var7.setDomainName(var2.getName());
               var7.setCheckExpression(var6);
               var7.update();
            }
         }

         copyExpressions(this.session, var1, var2);
         return true;
      }
   }

   private static boolean copyExpressions(SessionLocal var0, Domain var1, ColumnTemplate var2) {
      var2.setDomain(var1.getDomain());
      Expression var3 = var1.getDefaultExpression();
      boolean var4 = false;
      if (var3 != null && var2.getDefaultExpression() == null) {
         var2.setDefaultExpression(var0, var3);
         var4 = true;
      }

      var3 = var1.getOnUpdateExpression();
      if (var3 != null && var2.getOnUpdateExpression() == null) {
         var2.setOnUpdateExpression(var0, var3);
         var4 = true;
      }

      return var4;
   }

   public int getType() {
      return 47;
   }
}
