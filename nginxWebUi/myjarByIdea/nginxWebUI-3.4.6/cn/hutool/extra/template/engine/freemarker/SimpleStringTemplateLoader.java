package cn.hutool.extra.template.engine.freemarker;

import freemarker.cache.TemplateLoader;
import java.io.Reader;
import java.io.StringReader;

public class SimpleStringTemplateLoader implements TemplateLoader {
   public Object findTemplateSource(String name) {
      return name;
   }

   public long getLastModified(Object templateSource) {
      return System.currentTimeMillis();
   }

   public Reader getReader(Object templateSource, String encoding) {
      return new StringReader((String)templateSource);
   }

   public void closeTemplateSource(Object templateSource) {
   }
}
