package cn.hutool.http.body;

import cn.hutool.core.io.IoUtil;
import java.io.OutputStream;

public class BytesBody implements RequestBody {
   private final byte[] content;

   public static BytesBody create(byte[] content) {
      return new BytesBody(content);
   }

   public BytesBody(byte[] content) {
      this.content = content;
   }

   public void write(OutputStream out) {
      IoUtil.write(out, false, this.content);
   }
}
