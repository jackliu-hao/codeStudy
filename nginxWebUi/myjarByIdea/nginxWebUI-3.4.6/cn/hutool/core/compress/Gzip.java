package cn.hutool.core.compress;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Gzip implements Closeable {
   private InputStream source;
   private OutputStream target;

   public static Gzip of(InputStream source, OutputStream target) {
      return new Gzip(source, target);
   }

   public Gzip(InputStream source, OutputStream target) {
      this.source = source;
      this.target = target;
   }

   public OutputStream getTarget() {
      return this.target;
   }

   public Gzip gzip() {
      try {
         this.target = this.target instanceof GZIPOutputStream ? (GZIPOutputStream)this.target : new GZIPOutputStream(this.target);
         IoUtil.copy(this.source, this.target);
         ((GZIPOutputStream)this.target).finish();
         return this;
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public Gzip unGzip() {
      try {
         this.source = this.source instanceof GZIPInputStream ? (GZIPInputStream)this.source : new GZIPInputStream(this.source);
         IoUtil.copy(this.source, this.target);
         return this;
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public void close() {
      IoUtil.close(this.target);
      IoUtil.close(this.source);
   }
}
