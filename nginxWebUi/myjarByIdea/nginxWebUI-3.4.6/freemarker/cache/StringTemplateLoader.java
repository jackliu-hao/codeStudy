package freemarker.cache;

import freemarker.template.utility.StringUtil;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StringTemplateLoader implements TemplateLoader {
   private final Map<String, StringTemplateSource> templates = new HashMap();

   public void putTemplate(String name, String templateContent) {
      this.putTemplate(name, templateContent, System.currentTimeMillis());
   }

   public void putTemplate(String name, String templateContent, long lastModified) {
      this.templates.put(name, new StringTemplateSource(name, templateContent, lastModified));
   }

   public boolean removeTemplate(String name) {
      return this.templates.remove(name) != null;
   }

   public void closeTemplateSource(Object templateSource) {
   }

   public Object findTemplateSource(String name) {
      return this.templates.get(name);
   }

   public long getLastModified(Object templateSource) {
      return ((StringTemplateSource)templateSource).lastModified;
   }

   public Reader getReader(Object templateSource, String encoding) {
      return new StringReader(((StringTemplateSource)templateSource).templateContent);
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(TemplateLoaderUtils.getClassNameForToString(this));
      sb.append("(Map { ");
      int cnt = 0;
      Iterator var3 = this.templates.keySet().iterator();

      while(var3.hasNext()) {
         String name = (String)var3.next();
         ++cnt;
         if (cnt != 1) {
            sb.append(", ");
         }

         if (cnt > 10) {
            sb.append("...");
            break;
         }

         sb.append(StringUtil.jQuote(name));
         sb.append("=...");
      }

      if (cnt != 0) {
         sb.append(' ');
      }

      sb.append("})");
      return sb.toString();
   }

   private static class StringTemplateSource {
      private final String name;
      private final String templateContent;
      private final long lastModified;

      StringTemplateSource(String name, String templateContent, long lastModified) {
         if (name == null) {
            throw new IllegalArgumentException("name == null");
         } else if (templateContent == null) {
            throw new IllegalArgumentException("source == null");
         } else if (lastModified < -1L) {
            throw new IllegalArgumentException("lastModified < -1L");
         } else {
            this.name = name;
            this.templateContent = templateContent;
            this.lastModified = lastModified;
         }
      }

      public int hashCode() {
         int prime = true;
         int result = 1;
         result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
         return result;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            StringTemplateSource other = (StringTemplateSource)obj;
            if (this.name == null) {
               if (other.name != null) {
                  return false;
               }
            } else if (!this.name.equals(other.name)) {
               return false;
            }

            return true;
         }
      }

      public String toString() {
         return this.name;
      }
   }
}
