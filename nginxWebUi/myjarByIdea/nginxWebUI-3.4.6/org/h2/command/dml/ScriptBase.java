package org.h2.command.dml;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.h2.command.Prepared;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.SysProperties;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.store.FileStore;
import org.h2.store.FileStoreInputStream;
import org.h2.store.FileStoreOutputStream;
import org.h2.store.fs.FileUtils;
import org.h2.tools.CompressTool;
import org.h2.util.IOUtils;
import org.h2.util.StringUtils;

abstract class ScriptBase extends Prepared {
   private static final String SCRIPT_SQL = "script.sql";
   protected OutputStream out;
   protected BufferedReader reader;
   private Expression fileNameExpr;
   private Expression password;
   private String fileName;
   private String cipher;
   private FileStore store;
   private String compressionAlgorithm;

   ScriptBase(SessionLocal var1) {
      super(var1);
   }

   public void setCipher(String var1) {
      this.cipher = var1;
   }

   private boolean isEncrypted() {
      return this.cipher != null;
   }

   public void setPassword(Expression var1) {
      this.password = var1;
   }

   public void setFileNameExpr(Expression var1) {
      this.fileNameExpr = var1;
   }

   protected String getFileName() {
      if (this.fileNameExpr != null && this.fileName == null) {
         this.fileName = this.fileNameExpr.optimize(this.session).getValue(this.session).getString();
         if (this.fileName == null || StringUtils.isWhitespaceOrEmpty(this.fileName)) {
            this.fileName = "script.sql";
         }

         this.fileName = SysProperties.getScriptDirectory() + this.fileName;
      }

      return this.fileName;
   }

   public boolean isTransactional() {
      return false;
   }

   void deleteStore() {
      String var1 = this.getFileName();
      if (var1 != null) {
         FileUtils.delete(var1);
      }

   }

   private void initStore() {
      Database var1 = this.session.getDatabase();
      byte[] var2 = null;
      if (this.cipher != null && this.password != null) {
         char[] var3 = this.password.optimize(this.session).getValue(this.session).getString().toCharArray();
         var2 = SHA256.getKeyPasswordHash("script", var3);
      }

      String var4 = this.getFileName();
      this.store = FileStore.open(var1, var4, "rw", this.cipher, var2);
      this.store.setCheckedWriting(false);
      this.store.init();
   }

   void openOutput() {
      String var1 = this.getFileName();
      if (var1 != null) {
         if (this.isEncrypted()) {
            this.initStore();
            this.out = new FileStoreOutputStream(this.store, this.compressionAlgorithm);
            this.out = new BufferedOutputStream(this.out, 131072);
         } else {
            OutputStream var2;
            try {
               var2 = FileUtils.newOutputStream(var1, false);
            } catch (IOException var4) {
               throw DbException.convertIOException(var4, (String)null);
            }

            this.out = new BufferedOutputStream(var2, 4096);
            this.out = CompressTool.wrapOutputStream(this.out, this.compressionAlgorithm, "script.sql");
         }

      }
   }

   void openInput(Charset var1) {
      String var2 = this.getFileName();
      if (var2 != null) {
         Object var3;
         if (this.isEncrypted()) {
            this.initStore();
            var3 = new FileStoreInputStream(this.store, this.compressionAlgorithm != null, false);
         } else {
            InputStream var6;
            try {
               var6 = FileUtils.newInputStream(var2);
            } catch (IOException var5) {
               throw DbException.convertIOException(var5, var2);
            }

            var3 = CompressTool.wrapInputStream(var6, this.compressionAlgorithm, "script.sql");
            if (var3 == null) {
               throw DbException.get(90124, "script.sql in " + var2);
            }
         }

         this.reader = new BufferedReader(new InputStreamReader((InputStream)var3, var1), 4096);
      }
   }

   void closeIO() {
      IOUtils.closeSilently(this.out);
      this.out = null;
      IOUtils.closeSilently(this.reader);
      this.reader = null;
      if (this.store != null) {
         this.store.closeSilently();
         this.store = null;
      }

   }

   public boolean needRecompile() {
      return false;
   }

   public void setCompressionAlgorithm(String var1) {
      this.compressionAlgorithm = var1;
   }
}
