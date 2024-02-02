package org.h2.value.lob;

import java.io.IOException;
import java.io.InputStream;
import org.h2.message.DbException;
import org.h2.store.DataHandler;
import org.h2.value.ValueLob;

public final class LobDataDatabase extends LobData {
   private DataHandler handler;
   private final int tableId;
   private final long lobId;
   private boolean isRecoveryReference;

   public LobDataDatabase(DataHandler var1, int var2, long var3) {
      this.handler = var1;
      this.tableId = var2;
      this.lobId = var3;
   }

   public void remove(ValueLob var1) {
      if (this.handler != null) {
         this.handler.getLobStorage().removeLob(var1);
      }

   }

   public boolean isLinkedToTable() {
      return this.tableId >= 0;
   }

   public int getTableId() {
      return this.tableId;
   }

   public long getLobId() {
      return this.lobId;
   }

   public InputStream getInputStream(long var1) {
      try {
         return this.handler.getLobStorage().getInputStream(this.lobId, this.tableId, var1);
      } catch (IOException var4) {
         throw DbException.convertIOException(var4, this.toString());
      }
   }

   public DataHandler getDataHandler() {
      return this.handler;
   }

   public String toString() {
      return "lob-table: table: " + this.tableId + " id: " + this.lobId;
   }

   public void setRecoveryReference(boolean var1) {
      this.isRecoveryReference = var1;
   }

   public boolean isRecoveryReference() {
      return this.isRecoveryReference;
   }
}
