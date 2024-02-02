package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;
import org.h2.util.StringUtils;

public class CreateFunctionAlias extends SchemaCommand {
   private String aliasName;
   private String javaClassMethod;
   private boolean deterministic;
   private boolean ifNotExists;
   private boolean force;
   private String source;

   public CreateFunctionAlias(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public long update() {
      this.session.getUser().checkAdmin();
      Database var1 = this.session.getDatabase();
      Schema var2 = this.getSchema();
      if (var2.findFunctionOrAggregate(this.aliasName) != null) {
         if (!this.ifNotExists) {
            throw DbException.get(90076, this.aliasName);
         }
      } else {
         int var3 = this.getObjectId();
         FunctionAlias var4;
         if (this.javaClassMethod != null) {
            var4 = FunctionAlias.newInstance(var2, var3, this.aliasName, this.javaClassMethod, this.force);
         } else {
            var4 = FunctionAlias.newInstanceFromSource(var2, var3, this.aliasName, this.source, this.force);
         }

         var4.setDeterministic(this.deterministic);
         var1.addSchemaObject(this.session, var4);
      }

      return 0L;
   }

   public void setAliasName(String var1) {
      this.aliasName = var1;
   }

   public void setJavaClassMethod(String var1) {
      this.javaClassMethod = StringUtils.replaceAll(var1, " ", "");
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public void setForce(boolean var1) {
      this.force = var1;
   }

   public void setDeterministic(boolean var1) {
      this.deterministic = var1;
   }

   public void setSource(String var1) {
      this.source = var1;
   }

   public int getType() {
      return 24;
   }
}
