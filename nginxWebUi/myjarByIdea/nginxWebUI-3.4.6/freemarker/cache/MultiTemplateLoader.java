package freemarker.cache;

import freemarker.template.utility.NullArgumentException;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTemplateLoader implements StatefulTemplateLoader {
   private final TemplateLoader[] templateLoaders;
   private final Map<String, TemplateLoader> lastTemplateLoaderForName = new ConcurrentHashMap();
   private boolean sticky = true;

   public MultiTemplateLoader(TemplateLoader[] templateLoaders) {
      NullArgumentException.check("templateLoaders", templateLoaders);
      this.templateLoaders = (TemplateLoader[])templateLoaders.clone();
   }

   public Object findTemplateSource(String name) throws IOException {
      TemplateLoader lastTemplateLoader = null;
      if (this.sticky) {
         lastTemplateLoader = (TemplateLoader)this.lastTemplateLoaderForName.get(name);
         if (lastTemplateLoader != null) {
            Object source = lastTemplateLoader.findTemplateSource(name);
            if (source != null) {
               return new MultiSource(source, lastTemplateLoader);
            }
         }
      }

      TemplateLoader[] var8 = this.templateLoaders;
      int var4 = var8.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TemplateLoader templateLoader = var8[var5];
         if (lastTemplateLoader != templateLoader) {
            Object source = templateLoader.findTemplateSource(name);
            if (source != null) {
               if (this.sticky) {
                  this.lastTemplateLoaderForName.put(name, templateLoader);
               }

               return new MultiSource(source, templateLoader);
            }
         }
      }

      if (this.sticky) {
         this.lastTemplateLoaderForName.remove(name);
      }

      return null;
   }

   public long getLastModified(Object templateSource) {
      return ((MultiSource)templateSource).getLastModified();
   }

   public Reader getReader(Object templateSource, String encoding) throws IOException {
      return ((MultiSource)templateSource).getReader(encoding);
   }

   public void closeTemplateSource(Object templateSource) throws IOException {
      ((MultiSource)templateSource).close();
   }

   public void resetState() {
      this.lastTemplateLoaderForName.clear();
      TemplateLoader[] var1 = this.templateLoaders;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         TemplateLoader loader = var1[var3];
         if (loader instanceof StatefulTemplateLoader) {
            ((StatefulTemplateLoader)loader).resetState();
         }
      }

   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("MultiTemplateLoader(");

      for(int i = 0; i < this.templateLoaders.length; ++i) {
         if (i != 0) {
            sb.append(", ");
         }

         sb.append("loader").append(i + 1).append(" = ").append(this.templateLoaders[i]);
      }

      sb.append(")");
      return sb.toString();
   }

   public int getTemplateLoaderCount() {
      return this.templateLoaders.length;
   }

   public TemplateLoader getTemplateLoader(int index) {
      return this.templateLoaders[index];
   }

   public boolean isSticky() {
      return this.sticky;
   }

   public void setSticky(boolean sticky) {
      this.sticky = sticky;
   }

   static final class MultiSource {
      private final Object source;
      private final TemplateLoader loader;

      MultiSource(Object source, TemplateLoader loader) {
         this.source = source;
         this.loader = loader;
      }

      long getLastModified() {
         return this.loader.getLastModified(this.source);
      }

      Reader getReader(String encoding) throws IOException {
         return this.loader.getReader(this.source, encoding);
      }

      void close() throws IOException {
         this.loader.closeTemplateSource(this.source);
      }

      Object getWrappedSource() {
         return this.source;
      }

      public boolean equals(Object o) {
         if (!(o instanceof MultiSource)) {
            return false;
         } else {
            MultiSource m = (MultiSource)o;
            return m.loader.equals(this.loader) && m.source.equals(this.source);
         }
      }

      public int hashCode() {
         return this.loader.hashCode() + 31 * this.source.hashCode();
      }

      public String toString() {
         return this.source.toString();
      }
   }
}
