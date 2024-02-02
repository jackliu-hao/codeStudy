package org.h2.command.ddl;

import java.util.Iterator;
import java.util.function.BiPredicate;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;

public abstract class AlterDomain extends SchemaOwnerCommand {
   String domainName;
   boolean ifDomainExists;

   public static void forAllDependencies(SessionLocal var0, Domain var1, BiPredicate<Domain, Column> var2, BiPredicate<Domain, Domain> var3, boolean var4) {
      Database var5 = var0.getDatabase();
      Iterator var6 = var5.getAllSchemasNoMeta().iterator();

      label57:
      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllDomains().iterator();

         while(true) {
            Domain var9;
            do {
               do {
                  if (!var8.hasNext()) {
                     var8 = var7.getAllTablesAndViews((SessionLocal)null).iterator();

                     while(var8.hasNext()) {
                        Table var11 = (Table)var8.next();
                        if (forTable(var0, var1, var2, var4, var11)) {
                           var5.updateMeta(var0, var11);
                        }
                     }
                     continue label57;
                  }

                  var9 = (Domain)var8.next();
               } while(var9.getDomain() != var1);
            } while(var3 != null && !var3.test(var1, var9));

            if (var4) {
               var1.prepareExpressions(var0);
            }

            var5.updateMeta(var0, var9);
         }
      }

      var6 = var0.getLocalTempTables().iterator();

      while(var6.hasNext()) {
         Table var10 = (Table)var6.next();
         forTable(var0, var1, var2, var4, var10);
      }

   }

   private static boolean forTable(SessionLocal var0, Domain var1, BiPredicate<Domain, Column> var2, boolean var3, Table var4) {
      boolean var5 = false;
      Column[] var6 = var4.getColumns();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Column var9 = var6[var8];
         if (var9.getDomain() == var1) {
            boolean var10 = var2 == null || var2.test(var1, var9);
            if (var10) {
               if (var3) {
                  var9.prepareExpressions(var0);
               }

               var5 = true;
            }
         }
      }

      return var5;
   }

   AlterDomain(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public final void setDomainName(String var1) {
      this.domainName = var1;
   }

   public final void setIfDomainExists(boolean var1) {
      this.ifDomainExists = var1;
   }

   final long update(Schema var1) {
      Domain var2 = this.getSchema().findDomain(this.domainName);
      if (var2 == null) {
         if (this.ifDomainExists) {
            return 0L;
         } else {
            throw DbException.get(90120, this.domainName);
         }
      } else {
         return this.update(var1, var2);
      }
   }

   abstract long update(Schema var1, Domain var2);
}
