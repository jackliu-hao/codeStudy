package cn.hutool.extra.template.engine.jetbrick.loader;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import jetbrick.io.resource.AbstractResource;
import jetbrick.io.resource.Resource;
import jetbrick.io.resource.ResourceNotFoundException;
import jetbrick.template.loader.AbstractResourceLoader;

public class StringResourceLoader extends AbstractResourceLoader {
   private Charset charset;

   public Resource load(String name) {
      return new StringTemplateResource(name, this.charset);
   }

   public void setCharset(Charset charset) {
      this.charset = charset;
   }

   static class StringTemplateResource extends AbstractResource {
      private final String content;
      private final Charset charset;

      public StringTemplateResource(String content, Charset charset) {
         this.content = content;
         this.charset = charset;
      }

      public InputStream openStream() throws ResourceNotFoundException {
         return IoUtil.toStream(this.content, this.charset);
      }

      public URL getURL() {
         throw new UnsupportedOperationException();
      }

      public boolean exist() {
         return StrUtil.isEmpty(this.content);
      }

      public String toString() {
         return this.content;
      }

      public long lastModified() {
         return 1L;
      }
   }
}
