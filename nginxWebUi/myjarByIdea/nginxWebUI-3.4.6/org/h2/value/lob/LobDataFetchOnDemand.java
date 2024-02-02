package org.h2.value.lob;

import java.io.BufferedInputStream;
import java.io.InputStream;
import org.h2.engine.SessionRemote;
import org.h2.store.DataHandler;
import org.h2.store.LobStorageRemoteInputStream;

public final class LobDataFetchOnDemand extends LobData {
   private SessionRemote handler;
   private final int tableId;
   private final long lobId;
   protected final byte[] hmac;

   public LobDataFetchOnDemand(DataHandler var1, int var2, long var3, byte[] var5) {
      this.hmac = var5;
      this.handler = (SessionRemote)var1;
      this.tableId = var2;
      this.lobId = var3;
   }

   public boolean isLinkedToTable() {
      throw new IllegalStateException();
   }

   public int getTableId() {
      return this.tableId;
   }

   public long getLobId() {
      return this.lobId;
   }

   public InputStream getInputStream(long var1) {
      return new BufferedInputStream(new LobStorageRemoteInputStream(this.handler, this.lobId, this.hmac));
   }

   public DataHandler getDataHandler() {
      return this.handler;
   }

   public String toString() {
      return "lob-table: table: " + this.tableId + " id: " + this.lobId;
   }
}
