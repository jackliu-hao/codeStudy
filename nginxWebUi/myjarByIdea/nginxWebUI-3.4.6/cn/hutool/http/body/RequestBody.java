package cn.hutool.http.body;

import cn.hutool.core.io.IoUtil;
import java.io.OutputStream;

public interface RequestBody {
   void write(OutputStream var1);

   default void writeClose(OutputStream out) {
      try {
         this.write(out);
      } finally {
         IoUtil.close(out);
      }

   }
}
