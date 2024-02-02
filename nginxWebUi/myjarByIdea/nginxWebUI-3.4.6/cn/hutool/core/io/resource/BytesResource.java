package cn.hutool.core.io.resource;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;

public class BytesResource implements Resource, Serializable {
   private static final long serialVersionUID = 1L;
   private final byte[] bytes;
   private final String name;

   public BytesResource(byte[] bytes) {
      this(bytes, (String)null);
   }

   public BytesResource(byte[] bytes, String name) {
      this.bytes = bytes;
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public URL getUrl() {
      return null;
   }

   public InputStream getStream() {
      return new ByteArrayInputStream(this.bytes);
   }

   public String readStr(Charset charset) throws IORuntimeException {
      return StrUtil.str(this.bytes, charset);
   }

   public byte[] readBytes() throws IORuntimeException {
      return this.bytes;
   }
}
