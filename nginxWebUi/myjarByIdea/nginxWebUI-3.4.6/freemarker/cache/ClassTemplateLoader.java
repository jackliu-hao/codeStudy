package freemarker.cache;

import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import java.net.URL;

public class ClassTemplateLoader extends URLTemplateLoader {
   private final Class<?> resourceLoaderClass;
   private final ClassLoader classLoader;
   private final String basePackagePath;

   /** @deprecated */
   @Deprecated
   public ClassTemplateLoader() {
      this((Class)null, true, (ClassLoader)null, "/");
   }

   /** @deprecated */
   @Deprecated
   public ClassTemplateLoader(Class<?> resourceLoaderClass) {
      this(resourceLoaderClass, "");
   }

   public ClassTemplateLoader(Class<?> resourceLoaderClass, String basePackagePath) {
      this(resourceLoaderClass, false, (ClassLoader)null, basePackagePath);
   }

   public ClassTemplateLoader(ClassLoader classLoader, String basePackagePath) {
      this((Class)null, true, classLoader, basePackagePath);
   }

   private ClassTemplateLoader(Class<?> resourceLoaderClass, boolean allowNullResourceLoaderClass, ClassLoader classLoader, String basePackagePath) {
      if (!allowNullResourceLoaderClass) {
         NullArgumentException.check("resourceLoaderClass", resourceLoaderClass);
      }

      NullArgumentException.check("basePackagePath", basePackagePath);
      this.resourceLoaderClass = classLoader == null ? (resourceLoaderClass == null ? this.getClass() : resourceLoaderClass) : null;
      if (this.resourceLoaderClass == null && classLoader == null) {
         throw new NullArgumentException("classLoader");
      } else {
         this.classLoader = classLoader;
         String canonBasePackagePath = canonicalizePrefix(basePackagePath);
         if (this.classLoader != null && canonBasePackagePath.startsWith("/")) {
            canonBasePackagePath = canonBasePackagePath.substring(1);
         }

         this.basePackagePath = canonBasePackagePath;
      }
   }

   protected URL getURL(String name) {
      String fullPath = this.basePackagePath + name;
      if (this.basePackagePath.equals("/") && !isSchemeless(fullPath)) {
         return null;
      } else {
         return this.resourceLoaderClass != null ? this.resourceLoaderClass.getResource(fullPath) : this.classLoader.getResource(fullPath);
      }
   }

   private static boolean isSchemeless(String fullPath) {
      int i = 0;
      int ln = fullPath.length();
      if (i < ln && fullPath.charAt(i) == '/') {
         ++i;
      }

      while(i < ln) {
         char c = fullPath.charAt(i);
         if (c == '/') {
            return true;
         }

         if (c == ':') {
            return false;
         }

         ++i;
      }

      return true;
   }

   public String toString() {
      return TemplateLoaderUtils.getClassNameForToString(this) + "(" + (this.resourceLoaderClass != null ? "resourceLoaderClass=" + this.resourceLoaderClass.getName() : "classLoader=" + StringUtil.jQuote((Object)this.classLoader)) + ", basePackagePath=" + StringUtil.jQuote(this.basePackagePath) + (this.resourceLoaderClass != null ? (this.basePackagePath.startsWith("/") ? "" : " /* relatively to resourceLoaderClass pkg */") : "") + ")";
   }

   public Class getResourceLoaderClass() {
      return this.resourceLoaderClass;
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   public String getBasePackagePath() {
      return this.basePackagePath;
   }
}
