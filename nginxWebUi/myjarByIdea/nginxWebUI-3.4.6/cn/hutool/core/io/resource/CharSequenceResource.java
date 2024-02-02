package cn.hutool.core.io.resource;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;

public class CharSequenceResource implements Resource, Serializable {
   private static final long serialVersionUID = 1L;
   private final CharSequence data;
   private final CharSequence name;
   private final Charset charset;

   public CharSequenceResource(CharSequence data) {
      this(data, (String)null);
   }

   public CharSequenceResource(CharSequence data, String name) {
      this(data, name, CharsetUtil.CHARSET_UTF_8);
   }

   public CharSequenceResource(CharSequence data, CharSequence name, Charset charset) {
      this.data = data;
      this.name = name;
      this.charset = charset;
   }

   public String getName() {
      return StrUtil.str(this.name);
   }

   public URL getUrl() {
      return null;
   }

   public InputStream getStream() {
      return new ByteArrayInputStream(this.readBytes());
   }

   public BufferedReader getReader(Charset charset) {
      return IoUtil.getReader((Reader)(new StringReader(this.data.toString())));
   }

   public String readStr(Charset charset) throws IORuntimeException {
      return this.data.toString();
   }

   public byte[] readBytes() throws IORuntimeException {
      return this.data.toString().getBytes(this.charset);
   }
}
