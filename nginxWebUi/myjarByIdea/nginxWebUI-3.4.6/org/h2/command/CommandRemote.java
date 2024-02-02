package org.h2.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.h2.engine.GeneratedKeysMode;
import org.h2.engine.SessionRemote;
import org.h2.engine.SysProperties;
import org.h2.expression.ParameterInterface;
import org.h2.expression.ParameterRemote;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.result.ResultInterface;
import org.h2.result.ResultRemote;
import org.h2.result.ResultWithGeneratedKeys;
import org.h2.util.Utils;
import org.h2.value.Transfer;
import org.h2.value.Value;
import org.h2.value.ValueLob;
import org.h2.value.ValueNull;

public class CommandRemote implements CommandInterface {
   private final ArrayList<Transfer> transferList;
   private final ArrayList<ParameterInterface> parameters;
   private final Trace trace;
   private final String sql;
   private final int fetchSize;
   private SessionRemote session;
   private int id;
   private boolean isQuery;
   private int cmdType = 0;
   private boolean readonly;
   private final int created;

   public CommandRemote(SessionRemote var1, ArrayList<Transfer> var2, String var3, int var4) {
      this.transferList = var2;
      this.trace = var1.getTrace();
      this.sql = var3;
      this.parameters = Utils.newSmallArrayList();
      this.prepare(var1, true);
      this.session = var1;
      this.fetchSize = var4;
      this.created = var1.getLastReconnect();
   }

   public void stop() {
   }

   private void prepare(SessionRemote var1, boolean var2) {
      this.id = var1.getNextId();
      int var3 = 0;

      for(int var4 = 0; var3 < this.transferList.size(); ++var3) {
         try {
            Transfer var5 = (Transfer)this.transferList.get(var3);
            if (var2) {
               var1.traceOperation("SESSION_PREPARE_READ_PARAMS2", this.id);
               var5.writeInt(18).writeInt(this.id).writeString(this.sql);
            } else {
               var1.traceOperation("SESSION_PREPARE", this.id);
               var5.writeInt(0).writeInt(this.id).writeString(this.sql);
            }

            var1.done(var5);
            this.isQuery = var5.readBoolean();
            this.readonly = var5.readBoolean();
            this.cmdType = var2 ? var5.readInt() : 0;
            int var6 = var5.readInt();
            if (var2) {
               this.parameters.clear();

               for(int var7 = 0; var7 < var6; ++var7) {
                  ParameterRemote var8 = new ParameterRemote(var7);
                  var8.readMetaData(var5);
                  this.parameters.add(var8);
               }
            }
         } catch (IOException var9) {
            int var10002 = var3--;
            ++var4;
            var1.removeServer(var9, var10002, var4);
         }
      }

   }

   public boolean isQuery() {
      return this.isQuery;
   }

   public ArrayList<ParameterInterface> getParameters() {
      return this.parameters;
   }

   private void prepareIfRequired() {
      if (this.session.getLastReconnect() != this.created) {
         this.id = Integer.MIN_VALUE;
      }

      this.session.checkClosed();
      if (this.id <= this.session.getCurrentId() - SysProperties.SERVER_CACHED_OBJECTS) {
         this.prepare(this.session, false);
      }

   }

   public ResultInterface getMetaData() {
      synchronized(this.session) {
         if (!this.isQuery) {
            return null;
         } else {
            int var2 = this.session.getNextId();
            ResultRemote var3 = null;
            int var4 = 0;
            int var5 = 0;

            while(var4 < this.transferList.size()) {
               this.prepareIfRequired();
               Transfer var6 = (Transfer)this.transferList.get(var4);

               try {
                  this.session.traceOperation("COMMAND_GET_META_DATA", this.id);
                  var6.writeInt(10).writeInt(this.id).writeInt(var2);
                  this.session.done(var6);
                  int var7 = var6.readInt();
                  var3 = new ResultRemote(this.session, var6, var2, var7, Integer.MAX_VALUE);
                  break;
               } catch (IOException var9) {
                  int var10002 = var4--;
                  ++var5;
                  this.session.removeServer(var9, var10002, var5);
                  ++var4;
               }
            }

            this.session.autoCommitIfCluster();
            return var3;
         }
      }
   }

   public ResultInterface executeQuery(long var1, boolean var3) {
      this.checkParameters();
      synchronized(this.session) {
         int var5 = this.session.getNextId();
         ResultRemote var6 = null;
         int var7 = 0;

         for(int var8 = 0; var7 < this.transferList.size(); ++var7) {
            this.prepareIfRequired();
            Transfer var9 = (Transfer)this.transferList.get(var7);

            try {
               this.session.traceOperation("COMMAND_EXECUTE_QUERY", this.id);
               var9.writeInt(2).writeInt(this.id).writeInt(var5);
               var9.writeRowCount(var1);
               int var10;
               if (!this.session.isClustered() && !var3) {
                  var10 = this.fetchSize;
               } else {
                  var10 = Integer.MAX_VALUE;
               }

               var9.writeInt(var10);
               this.sendParameters(var9);
               this.session.done(var9);
               int var11 = var9.readInt();
               if (var6 != null) {
                  var6.close();
                  var6 = null;
               }

               var6 = new ResultRemote(this.session, var9, var5, var11, var10);
               if (this.readonly) {
                  break;
               }
            } catch (IOException var13) {
               int var10002 = var7--;
               ++var8;
               this.session.removeServer(var13, var10002, var8);
            }
         }

         this.session.autoCommitIfCluster();
         this.session.readSessionState();
         return var6;
      }
   }

   public ResultWithGeneratedKeys executeUpdate(Object var1) {
      this.checkParameters();
      int var2 = GeneratedKeysMode.valueOf(var1);
      boolean var3 = var2 != 0;
      int var4 = var3 ? this.session.getNextId() : 0;
      synchronized(this.session) {
         long var6 = 0L;
         ResultRemote var8 = null;
         boolean var9 = false;
         int var10 = 0;

         for(int var11 = 0; var10 < this.transferList.size(); ++var10) {
            this.prepareIfRequired();
            Transfer var12 = (Transfer)this.transferList.get(var10);

            try {
               this.session.traceOperation("COMMAND_EXECUTE_UPDATE", this.id);
               var12.writeInt(3).writeInt(this.id);
               this.sendParameters(var12);
               var12.writeInt(var2);
               int var15;
               int var16;
               label61:
               switch (var2) {
                  case 2:
                     int[] var21 = (int[])((int[])var1);
                     var12.writeInt(var21.length);
                     int[] var23 = var21;
                     var15 = var21.length;
                     var16 = 0;

                     while(true) {
                        if (var16 >= var15) {
                           break label61;
                        }

                        int var24 = var23[var16];
                        var12.writeInt(var24);
                        ++var16;
                     }
                  case 3:
                     String[] var13 = (String[])((String[])var1);
                     var12.writeInt(var13.length);
                     String[] var14 = var13;
                     var15 = var13.length;

                     for(var16 = 0; var16 < var15; ++var16) {
                        String var17 = var14[var16];
                        var12.writeString(var17);
                     }
               }

               this.session.done(var12);
               var6 = var12.readRowCount();
               var9 = var12.readBoolean();
               if (var3) {
                  int var22 = var12.readInt();
                  if (var8 != null) {
                     var8.close();
                     var8 = null;
                  }

                  var8 = new ResultRemote(this.session, var12, var4, var22, Integer.MAX_VALUE);
               }
            } catch (IOException var19) {
               int var10002 = var10--;
               ++var11;
               this.session.removeServer(var19, var10002, var11);
            }
         }

         this.session.setAutoCommitFromServer(var9);
         this.session.autoCommitIfCluster();
         this.session.readSessionState();
         return (ResultWithGeneratedKeys)(var8 != null ? new ResultWithGeneratedKeys.WithKeys(var6, var8) : ResultWithGeneratedKeys.of(var6));
      }
   }

   private void checkParameters() {
      if (this.cmdType != 60) {
         Iterator var1 = this.parameters.iterator();

         while(var1.hasNext()) {
            ParameterInterface var2 = (ParameterInterface)var1.next();
            var2.checkSet();
         }
      }

   }

   private void sendParameters(Transfer var1) throws IOException {
      int var2 = this.parameters.size();
      var1.writeInt(var2);

      Object var5;
      for(Iterator var3 = this.parameters.iterator(); var3.hasNext(); var1.writeValue((Value)var5)) {
         ParameterInterface var4 = (ParameterInterface)var3.next();
         var5 = var4.getParamValue();
         if (var5 == null && this.cmdType == 60) {
            var5 = ValueNull.INSTANCE;
         }
      }

   }

   public void close() {
      if (this.session != null && !this.session.isClosed()) {
         synchronized(this.session) {
            this.session.traceOperation("COMMAND_CLOSE", this.id);
            Iterator var2 = this.transferList.iterator();

            while(true) {
               if (!var2.hasNext()) {
                  break;
               }

               Transfer var3 = (Transfer)var2.next();

               try {
                  var3.writeInt(4).writeInt(this.id);
               } catch (IOException var6) {
                  this.trace.error(var6, "close");
               }
            }
         }

         this.session = null;

         try {
            Iterator var1 = this.parameters.iterator();

            while(var1.hasNext()) {
               ParameterInterface var9 = (ParameterInterface)var1.next();
               Value var10 = var9.getParamValue();
               if (var10 instanceof ValueLob) {
                  ((ValueLob)var10).remove();
               }
            }
         } catch (DbException var7) {
            this.trace.error(var7, "close");
         }

         this.parameters.clear();
      }
   }

   public void cancel() {
      this.session.cancelStatement(this.id);
   }

   public String toString() {
      return this.sql + Trace.formatParams(this.getParameters());
   }

   public int getCommandType() {
      return this.cmdType;
   }
}
