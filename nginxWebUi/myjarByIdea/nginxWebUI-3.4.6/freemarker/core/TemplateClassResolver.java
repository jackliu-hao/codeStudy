package freemarker.core;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.Execute;
import freemarker.template.utility.ObjectConstructor;

public interface TemplateClassResolver {
   TemplateClassResolver UNRESTRICTED_RESOLVER = new TemplateClassResolver() {
      public Class resolve(String className, Environment env, Template template) throws TemplateException {
         try {
            return ClassUtil.forName(className);
         } catch (ClassNotFoundException var5) {
            throw new _MiscTemplateException(var5, env);
         }
      }
   };
   TemplateClassResolver SAFER_RESOLVER = new TemplateClassResolver() {
      public Class resolve(String className, Environment env, Template template) throws TemplateException {
         if (!className.equals(ObjectConstructor.class.getName()) && !className.equals(Execute.class.getName()) && !className.equals("freemarker.template.utility.JythonRuntime")) {
            try {
               return ClassUtil.forName(className);
            } catch (ClassNotFoundException var5) {
               throw new _MiscTemplateException(var5, env);
            }
         } else {
            throw _MessageUtil.newInstantiatingClassNotAllowedException(className, env);
         }
      }
   };
   TemplateClassResolver ALLOWS_NOTHING_RESOLVER = new TemplateClassResolver() {
      public Class resolve(String className, Environment env, Template template) throws TemplateException {
         throw _MessageUtil.newInstantiatingClassNotAllowedException(className, env);
      }
   };

   Class resolve(String var1, Environment var2, Template var3) throws TemplateException;
}
