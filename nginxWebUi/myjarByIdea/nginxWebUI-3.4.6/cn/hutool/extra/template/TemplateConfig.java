package cn.hutool.extra.template;

import cn.hutool.core.util.CharsetUtil;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Objects;

public class TemplateConfig implements Serializable {
   private static final long serialVersionUID = 2933113779920339523L;
   public static final TemplateConfig DEFAULT = new TemplateConfig();
   private Charset charset;
   private String path;
   private ResourceMode resourceMode;
   private Class<? extends TemplateEngine> customEngine;

   public TemplateConfig() {
      this((String)null);
   }

   public TemplateConfig(String path) {
      this(path, TemplateConfig.ResourceMode.STRING);
   }

   public TemplateConfig(String path, ResourceMode resourceMode) {
      this(CharsetUtil.CHARSET_UTF_8, path, resourceMode);
   }

   public TemplateConfig(Charset charset, String path, ResourceMode resourceMode) {
      this.charset = charset;
      this.path = path;
      this.resourceMode = resourceMode;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public String getCharsetStr() {
      return null == this.charset ? null : this.charset.toString();
   }

   public void setCharset(Charset charset) {
      this.charset = charset;
   }

   public String getPath() {
      return this.path;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public ResourceMode getResourceMode() {
      return this.resourceMode;
   }

   public void setResourceMode(ResourceMode resourceMode) {
      this.resourceMode = resourceMode;
   }

   public Class<? extends TemplateEngine> getCustomEngine() {
      return this.customEngine;
   }

   public TemplateConfig setCustomEngine(Class<? extends TemplateEngine> customEngine) {
      this.customEngine = customEngine;
      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TemplateConfig that = (TemplateConfig)o;
         return Objects.equals(this.charset, that.charset) && Objects.equals(this.path, that.path) && this.resourceMode == that.resourceMode && Objects.equals(this.customEngine, that.customEngine);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.charset, this.path, this.resourceMode, this.customEngine});
   }

   public static enum ResourceMode {
      CLASSPATH,
      FILE,
      WEB_ROOT,
      STRING,
      COMPOSITE;
   }
}
