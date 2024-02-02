package freemarker.cache;

import freemarker.template.Configuration;

final class TemplateLoaderUtils {
   private TemplateLoaderUtils() {
   }

   public static String getClassNameForToString(TemplateLoader templateLoader) {
      Class tlClass = templateLoader.getClass();
      Package tlPackage = tlClass.getPackage();
      return tlPackage != Configuration.class.getPackage() && tlPackage != TemplateLoader.class.getPackage() ? tlClass.getName() : tlClass.getSimpleName();
   }
}
