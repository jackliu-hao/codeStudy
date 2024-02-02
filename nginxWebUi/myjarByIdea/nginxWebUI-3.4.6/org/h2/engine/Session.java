package org.h2.engine;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.command.CommandInterface;
import org.h2.jdbc.meta.DatabaseMeta;
import org.h2.message.Trace;
import org.h2.result.ResultInterface;
import org.h2.store.DataHandler;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.value.ValueLob;

public abstract class Session implements CastDataProvider, AutoCloseable {
   private ArrayList<String> sessionState;
   boolean sessionStateChanged;
   private boolean sessionStateUpdating;
   volatile StaticSettings staticSettings;

   Session() {
   }

   public abstract ArrayList<String> getClusterServers();

   public abstract CommandInterface prepareCommand(String var1, int var2);

   public abstract void close();

   public abstract Trace getTrace();

   public abstract boolean isClosed();

   public abstract DataHandler getDataHandler();

   public abstract boolean hasPendingTransaction();

   public abstract void cancel();

   public abstract boolean getAutoCommit();

   public abstract void setAutoCommit(boolean var1);

   public abstract ValueLob addTemporaryLob(ValueLob var1);

   public abstract boolean isRemote();

   public abstract void setCurrentSchemaName(String var1);

   public abstract String getCurrentSchemaName();

   public abstract void setNetworkConnectionInfo(NetworkConnectionInfo var1);

   public abstract IsolationLevel getIsolationLevel();

   public abstract void setIsolationLevel(IsolationLevel var1);

   public abstract StaticSettings getStaticSettings();

   public abstract DynamicSettings getDynamicSettings();

   public abstract DatabaseMeta getDatabaseMeta();

   public abstract boolean isOldInformationSchema();

   void recreateSessionState() {
      if (this.sessionState != null && !this.sessionState.isEmpty()) {
         this.sessionStateUpdating = true;

         try {
            Iterator var1 = this.sessionState.iterator();

            while(var1.hasNext()) {
               String var2 = (String)var1.next();
               CommandInterface var3 = this.prepareCommand(var2, Integer.MAX_VALUE);
               var3.executeUpdate((Object)null);
            }
         } finally {
            this.sessionStateUpdating = false;
            this.sessionStateChanged = false;
         }
      }

   }

   public void readSessionState() {
      if (this.sessionStateChanged && !this.sessionStateUpdating) {
         this.sessionStateChanged = false;
         this.sessionState = Utils.newSmallArrayList();
         CommandInterface var1 = this.prepareCommand(!this.isOldInformationSchema() ? "SELECT STATE_COMMAND FROM INFORMATION_SCHEMA.SESSION_STATE" : "SELECT SQL FROM INFORMATION_SCHEMA.SESSION_STATE", Integer.MAX_VALUE);
         ResultInterface var2 = var1.executeQuery(0L, false);

         while(var2.next()) {
            this.sessionState.add(var2.currentRow()[0].getString());
         }

      }
   }

   public Session setThreadLocalSession() {
      return null;
   }

   public void resetThreadLocalSession(Session var1) {
   }

   public static final class DynamicSettings {
      public final Mode mode;
      public final TimeZoneProvider timeZone;

      public DynamicSettings(Mode var1, TimeZoneProvider var2) {
         this.mode = var1;
         this.timeZone = var2;
      }
   }

   public static final class StaticSettings {
      public final boolean databaseToUpper;
      public final boolean databaseToLower;
      public final boolean caseInsensitiveIdentifiers;

      public StaticSettings(boolean var1, boolean var2, boolean var3) {
         this.databaseToUpper = var1;
         this.databaseToLower = var2;
         this.caseInsensitiveIdentifiers = var3;
      }
   }
}
