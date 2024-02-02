package org.h2.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Parameter;
import org.h2.expression.ParameterInterface;
import org.h2.result.ResultInterface;
import org.h2.result.ResultWithGeneratedKeys;

class CommandList extends Command {
   private CommandContainer command;
   private final ArrayList<Prepared> commands;
   private final ArrayList<Parameter> parameters;
   private String remaining;
   private Command remainingCommand;

   CommandList(SessionLocal var1, String var2, CommandContainer var3, ArrayList<Prepared> var4, ArrayList<Parameter> var5, String var6) {
      super(var1, var2);
      this.command = var3;
      this.commands = var4;
      this.parameters = var5;
      this.remaining = var6;
   }

   public ArrayList<? extends ParameterInterface> getParameters() {
      return this.parameters;
   }

   private void executeRemaining() {
      Iterator var1 = this.commands.iterator();

      while(var1.hasNext()) {
         Prepared var2 = (Prepared)var1.next();
         var2.prepare();
         if (var2.isQuery()) {
            var2.query(0L);
         } else {
            var2.update();
         }
      }

      if (this.remaining != null) {
         this.remainingCommand = this.session.prepareLocal(this.remaining);
         this.remaining = null;
         if (this.remainingCommand.isQuery()) {
            this.remainingCommand.query(0L);
         } else {
            this.remainingCommand.update((Object)null);
         }
      }

   }

   public ResultWithGeneratedKeys update(Object var1) {
      ResultWithGeneratedKeys var2 = this.command.executeUpdate((Object)null);
      this.executeRemaining();
      return var2;
   }

   public ResultInterface query(long var1) {
      ResultInterface var3 = this.command.query(var1);
      this.executeRemaining();
      return var3;
   }

   public void stop() {
      this.command.stop();
      Iterator var1 = this.commands.iterator();

      while(var1.hasNext()) {
         Prepared var2 = (Prepared)var1.next();
         CommandContainer.clearCTE(this.session, var2);
      }

      if (this.remainingCommand != null) {
         this.remainingCommand.stop();
      }

   }

   public boolean isQuery() {
      return this.command.isQuery();
   }

   public boolean isTransactional() {
      return true;
   }

   public boolean isReadOnly() {
      return false;
   }

   public ResultInterface queryMeta() {
      return this.command.queryMeta();
   }

   public int getCommandType() {
      return this.command.getCommandType();
   }

   public Set<DbObject> getDependencies() {
      HashSet var1 = new HashSet();
      Iterator var2 = this.commands.iterator();

      while(var2.hasNext()) {
         Prepared var3 = (Prepared)var2.next();
         var3.collectDependencies(var1);
      }

      return var1;
   }

   protected boolean isCurrentCommandADefineCommand() {
      return this.command.isCurrentCommandADefineCommand();
   }
}
