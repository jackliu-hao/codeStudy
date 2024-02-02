package cn.hutool.core.io;

import cn.hutool.core.lang.Assert;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class BomReader extends Reader {
   private InputStreamReader reader;

   public BomReader(InputStream in) {
      Assert.notNull(in, "InputStream must be not null!");
      BOMInputStream bin = in instanceof BOMInputStream ? (BOMInputStream)in : new BOMInputStream(in);

      try {
         this.reader = new InputStreamReader(bin, bin.getCharset());
      } catch (UnsupportedEncodingException var4) {
      }

   }

   public int read(char[] cbuf, int off, int len) throws IOException {
      return this.reader.read(cbuf, off, len);
   }

   public void close() throws IOException {
      this.reader.close();
   }
}
