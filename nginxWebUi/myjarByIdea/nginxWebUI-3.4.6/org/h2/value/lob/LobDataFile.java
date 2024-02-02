package org.h2.value.lob;

import java.io.BufferedInputStream;
import java.io.InputStream;
import org.h2.engine.SysProperties;
import org.h2.store.DataHandler;
import org.h2.store.FileStore;
import org.h2.store.FileStoreInputStream;
import org.h2.store.fs.FileUtils;
import org.h2.value.ValueLob;

public final class LobDataFile extends LobData {
   private DataHandler handler;
   private final String fileName;
   private final FileStore tempFile;

   public LobDataFile(DataHandler var1, String var2, FileStore var3) {
      this.handler = var1;
      this.fileName = var2;
      this.tempFile = var3;
   }

   public void remove(ValueLob var1) {
      if (this.fileName != null) {
         if (this.tempFile != null) {
            this.tempFile.stopAutoDelete();
         }

         synchronized(this.handler.getLobSyncObject()) {
            FileUtils.delete(this.fileName);
         }
      }

   }

   public InputStream getInputStream(long var1) {
      FileStore var3 = this.handler.openFile(this.fileName, "r", true);
      boolean var4 = SysProperties.lobCloseBetweenReads;
      return new BufferedInputStream(new FileStoreInputStream(var3, false, var4), 4096);
   }

   public DataHandler getDataHandler() {
      return this.handler;
   }

   public String toString() {
      return "lob-file: " + this.fileName;
   }
}
