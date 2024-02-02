package cn.hutool.http;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.MultiResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.StringResource;
import cn.hutool.core.util.StrUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

public class MultipartOutputStream extends OutputStream {
   private static final String CONTENT_DISPOSITION_TEMPLATE = "Content-Disposition: form-data; name=\"{}\"\r\n";
   private static final String CONTENT_DISPOSITION_FILE_TEMPLATE = "Content-Disposition: form-data; name=\"{}\"; filename=\"{}\"\r\n";
   private static final String CONTENT_TYPE_FILE_TEMPLATE = "Content-Type: {}\r\n";
   private final OutputStream out;
   private final Charset charset;
   private final String boundary;
   private boolean isFinish;

   public MultipartOutputStream(OutputStream out, Charset charset) {
      this(out, charset, HttpGlobalConfig.getBoundary());
   }

   public MultipartOutputStream(OutputStream out, Charset charset, String boundary) {
      this.out = out;
      this.charset = charset;
      this.boundary = boundary;
   }

   public MultipartOutputStream write(String formFieldName, Object value) throws IORuntimeException {
      if (!(value instanceof MultiResource)) {
         this.beginPart();
         if (value instanceof Resource) {
            this.appendResource(formFieldName, (Resource)value);
         } else {
            this.appendResource(formFieldName, new StringResource(Convert.toStr(value), (String)null, this.charset));
         }

         this.write("\r\n");
         return this;
      } else {
         Iterator var3 = ((MultiResource)value).iterator();

         while(var3.hasNext()) {
            Resource subResource = (Resource)var3.next();
            this.write(formFieldName, subResource);
         }

         return this;
      }
   }

   public void write(int b) throws IOException {
      this.out.write(b);
   }

   public void finish() throws IORuntimeException {
      if (!this.isFinish) {
         this.write(StrUtil.format("--{}--\r\n", new Object[]{this.boundary}));
         this.isFinish = true;
      }

   }

   public void close() {
      this.finish();
      IoUtil.close(this.out);
   }

   private void appendResource(String formFieldName, Resource resource) throws IORuntimeException {
      String fileName = resource.getName();
      if (null == fileName) {
         this.write(StrUtil.format("Content-Disposition: form-data; name=\"{}\"\r\n", new Object[]{formFieldName}));
      } else {
         this.write(StrUtil.format("Content-Disposition: form-data; name=\"{}\"; filename=\"{}\"\r\n", new Object[]{formFieldName, fileName}));
      }

      if (resource instanceof HttpResource) {
         String contentType = ((HttpResource)resource).getContentType();
         if (StrUtil.isNotBlank(contentType)) {
            this.write(StrUtil.format("Content-Type: {}\r\n", new Object[]{contentType}));
         }
      } else if (StrUtil.isNotEmpty(fileName)) {
         this.write(StrUtil.format("Content-Type: {}\r\n", new Object[]{HttpUtil.getMimeType(fileName, ContentType.OCTET_STREAM.getValue())}));
      }

      this.write("\r\n");
      resource.writeTo(this);
   }

   private void beginPart() {
      this.write("--", this.boundary, "\r\n");
   }

   private void write(Object... objs) {
      IoUtil.write(this, (Charset)this.charset, false, objs);
   }
}
