package cn.hutool.http.body;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.http.MultipartOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

public class MultipartBody implements RequestBody {
   private static final String CONTENT_TYPE_MULTIPART_PREFIX;
   private final Map<String, Object> form;
   private final Charset charset;
   private final String boundary = HttpGlobalConfig.getBoundary();

   public static MultipartBody create(Map<String, Object> form, Charset charset) {
      return new MultipartBody(form, charset);
   }

   public String getContentType() {
      return CONTENT_TYPE_MULTIPART_PREFIX + this.boundary;
   }

   public MultipartBody(Map<String, Object> form, Charset charset) {
      this.form = form;
      this.charset = charset;
   }

   public void write(OutputStream out) {
      MultipartOutputStream stream = new MultipartOutputStream(out, this.charset, this.boundary);
      if (MapUtil.isNotEmpty(this.form)) {
         this.form.forEach(stream::write);
      }

      stream.finish();
   }

   public String toString() {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      this.write(out);
      return IoUtil.toStr(out, this.charset);
   }

   static {
      CONTENT_TYPE_MULTIPART_PREFIX = ContentType.MULTIPART.getValue() + "; boundary=";
   }
}
