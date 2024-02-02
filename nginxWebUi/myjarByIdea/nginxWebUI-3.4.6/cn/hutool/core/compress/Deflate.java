package cn.hutool.core.compress;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

public class Deflate implements Closeable {
   private final InputStream source;
   private OutputStream target;
   private final boolean nowrap;

   public static Deflate of(InputStream source, OutputStream target, boolean nowrap) {
      return new Deflate(source, target, nowrap);
   }

   public Deflate(InputStream source, OutputStream target, boolean nowrap) {
      this.source = source;
      this.target = target;
      this.nowrap = nowrap;
   }

   public OutputStream getTarget() {
      return this.target;
   }

   public Deflate deflater(int level) {
      this.target = this.target instanceof DeflaterOutputStream ? (DeflaterOutputStream)this.target : new DeflaterOutputStream(this.target, new Deflater(level, this.nowrap));
      IoUtil.copy(this.source, this.target);

      try {
         ((DeflaterOutputStream)this.target).finish();
         return this;
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public Deflate inflater() {
      this.target = this.target instanceof InflaterOutputStream ? (InflaterOutputStream)this.target : new InflaterOutputStream(this.target, new Inflater(this.nowrap));
      IoUtil.copy(this.source, this.target);

      try {
         ((InflaterOutputStream)this.target).finish();
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
