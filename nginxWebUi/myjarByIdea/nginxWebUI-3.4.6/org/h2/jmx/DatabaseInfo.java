package org.h2.jmx;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.h2.command.Command;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.table.Table;
import org.h2.util.NetworkConnectionInfo;

public class DatabaseInfo implements DatabaseInfoMBean {
   private static final Map<String, ObjectName> MBEANS = new HashMap();
   private final Database database;

   private DatabaseInfo(Database var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Argument 'database' must not be null");
      } else {
         this.database = var1;
      }
   }

   private static ObjectName getObjectName(String var0, String var1) throws JMException {
      var0 = var0.replace(':', '_');
      var1 = var1.replace(':', '_');
      Hashtable var2 = new Hashtable();
      var2.put("name", var0);
      var2.put("path", var1);
      return new ObjectName("org.h2", var2);
   }

   public static void registerMBean(ConnectionInfo var0, Database var1) throws JMException {
      String var2 = var0.getName();
      if (!MBEANS.containsKey(var2)) {
         MBeanServer var3 = ManagementFactory.getPlatformMBeanServer();
         String var4 = var1.getShortName();
         ObjectName var5 = getObjectName(var4, var2);
         MBEANS.put(var2, var5);
         DatabaseInfo var6 = new DatabaseInfo(var1);
         DocumentedMBean var7 = new DocumentedMBean(var6, DatabaseInfoMBean.class);
         var3.registerMBean(var7, var5);
      }

   }

   public static void unregisterMBean(String var0) throws Exception {
      ObjectName var1 = (ObjectName)MBEANS.remove(var0);
      if (var1 != null) {
         MBeanServer var2 = ManagementFactory.getPlatformMBeanServer();
         var2.unregisterMBean(var1);
      }

   }

   public boolean isExclusive() {
      return this.database.getExclusiveSession() != null;
   }

   public boolean isReadOnly() {
      return this.database.isReadOnly();
   }

   public String getMode() {
      return this.database.getMode().getName();
   }

   public int getTraceLevel() {
      return this.database.getTraceSystem().getLevelFile();
   }

   public void setTraceLevel(int var1) {
      this.database.getTraceSystem().setLevelFile(var1);
   }

   public long getFileWriteCount() {
      return this.database.isPersistent() ? this.database.getStore().getMvStore().getFileStore().getWriteCount() : 0L;
   }

   public long getFileReadCount() {
      return this.database.isPersistent() ? this.database.getStore().getMvStore().getFileStore().getReadCount() : 0L;
   }

   public long getFileSize() {
      long var1 = 0L;
      if (this.database.isPersistent()) {
         var1 = this.database.getStore().getMvStore().getFileStore().size();
      }

      return var1 / 1024L;
   }

   public int getCacheSizeMax() {
      return this.database.isPersistent() ? this.database.getStore().getMvStore().getCacheSize() * 1024 : 0;
   }

   public void setCacheSizeMax(int var1) {
      if (this.database.isPersistent()) {
         this.database.setCacheSize(var1);
      }

   }

   public int getCacheSize() {
      return this.database.isPersistent() ? this.database.getStore().getMvStore().getCacheSizeUsed() * 1024 : 0;
   }

   public String getVersion() {
      return Constants.FULL_VERSION;
   }

   public String listSettings() {
      StringBuilder var1 = new StringBuilder();
      Map.Entry[] var2 = this.database.getSettings().getSortedSettings();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Map.Entry var5 = var2[var4];
         var1.append((String)var5.getKey()).append(" = ").append((String)var5.getValue()).append('\n');
      }

      return var1.toString();
   }

   public String listSessions() {
      StringBuilder var1 = new StringBuilder();
      SessionLocal[] var2 = this.database.getSessions(false);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SessionLocal var5 = var2[var4];
         var1.append("session id: ").append(var5.getId());
         var1.append(" user: ").append(var5.getUser().getName()).append('\n');
         NetworkConnectionInfo var6 = var5.getNetworkConnectionInfo();
         if (var6 != null) {
            var1.append("server: ").append(var6.getServer()).append('\n').append("clientAddr: ").append(var6.getClient()).append('\n');
            String var7 = var6.getClientInfo();
            if (var7 != null) {
               var1.append("clientInfo: ").append(var7).append('\n');
            }
         }

         var1.append("connected: ").append(var5.getSessionStart().getString()).append('\n');
         Command var10 = var5.getCurrentCommand();
         if (var10 != null) {
            var1.append("statement: ").append(var10).append('\n').append("started: ").append(var5.getCommandStartOrEnd().getString()).append('\n');
         }

         Table var9;
         for(Iterator var8 = var5.getLocks().iterator(); var8.hasNext(); var1.append(var9.getSchema().getName()).append('.').append(var9.getName()).append('\n')) {
            var9 = (Table)var8.next();
            if (var9.isLockedExclusivelyBy(var5)) {
               var1.append("write lock on ");
            } else {
               var1.append("read lock on ");
            }
         }

         var1.append('\n');
      }

      return var1.toString();
   }
}
