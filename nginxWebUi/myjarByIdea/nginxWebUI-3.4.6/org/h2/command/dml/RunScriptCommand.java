package org.h2.command.dml;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.h2.command.CommandContainer;
import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.util.ScriptReader;

public class RunScriptCommand extends ScriptBase {
   private static final char UTF8_BOM = '\ufeff';
   private Charset charset;
   private boolean quirksMode;
   private boolean variableBinary;
   private boolean from1X;

   public RunScriptCommand(SessionLocal var1) {
      super(var1);
      this.charset = StandardCharsets.UTF_8;
   }

   public long update() {
      this.session.getUser().checkAdmin();
      int var1 = 0;
      boolean var2 = this.session.isQuirksMode();
      boolean var3 = this.session.isVariableBinary();

      try {
         this.openInput(this.charset);
         this.reader.mark(1);
         if (this.reader.read() != 65279) {
            this.reader.reset();
         }

         if (this.quirksMode) {
            this.session.setQuirksMode(true);
         }

         if (this.variableBinary) {
            this.session.setVariableBinary(true);
         }

         ScriptReader var4 = new ScriptReader(this.reader);

         while(true) {
            String var5 = var4.readStatement();
            if (var5 == null) {
               var4.close();
               return (long)var1;
            }

            this.execute(var5);
            ++var1;
            if ((var1 & 127) == 0) {
               this.checkCanceled();
            }
         }
      } catch (IOException var9) {
         throw DbException.convertIOException(var9, (String)null);
      } finally {
         if (this.quirksMode) {
            this.session.setQuirksMode(var2);
         }

         if (this.variableBinary) {
            this.session.setVariableBinary(var3);
         }

         this.closeIO();
      }
   }

   private void execute(String var1) {
      if (this.from1X) {
         var1 = var1.trim();
         if (var1.startsWith("INSERT INTO SYSTEM_LOB_STREAM VALUES(")) {
            int var2 = var1.indexOf(", NULL, '");
            if (var2 >= 0) {
               var1 = (new StringBuilder(var1.length() + 1)).append(var1, 0, var2 + 8).append("X'").append(var1, var2 + 9, var1.length()).toString();
            }
         }
      }

      try {
         Prepared var5 = this.session.prepare(var1);
         CommandContainer var3 = new CommandContainer(this.session, var1, var5);
         if (var3.isQuery()) {
            var3.executeQuery(0L, false);
         } else {
            var3.executeUpdate((Object)null);
         }

      } catch (DbException var4) {
         throw var4.addSQL(var1);
      }
   }

   public void setCharset(Charset var1) {
      this.charset = var1;
   }

   public void setQuirksMode(boolean var1) {
      this.quirksMode = var1;
   }

   public void setVariableBinary(boolean var1) {
      this.variableBinary = var1;
   }

   public void setFrom1X() {
      this.variableBinary = this.quirksMode = this.from1X = true;
   }

   public ResultInterface queryMeta() {
      return null;
   }

   public int getType() {
      return 64;
   }
}
