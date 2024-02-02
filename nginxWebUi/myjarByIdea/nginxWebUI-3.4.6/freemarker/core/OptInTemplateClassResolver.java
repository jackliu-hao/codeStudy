package freemarker.core;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class OptInTemplateClassResolver implements TemplateClassResolver {
   private final Set allowedClasses;
   private final List trustedTemplatePrefixes;
   private final Set trustedTemplateNames;

   public OptInTemplateClassResolver(Set allowedClasses, List trustedTemplates) {
      this.allowedClasses = allowedClasses != null ? allowedClasses : Collections.EMPTY_SET;
      if (trustedTemplates != null) {
         this.trustedTemplateNames = new HashSet();
         this.trustedTemplatePrefixes = new ArrayList();
         Iterator it = trustedTemplates.iterator();

         while(it.hasNext()) {
            String li = (String)it.next();
            if (li.startsWith("/")) {
               li = li.substring(1);
            }

            if (li.endsWith("*")) {
               this.trustedTemplatePrefixes.add(li.substring(0, li.length() - 1));
            } else {
               this.trustedTemplateNames.add(li);
            }
         }
      } else {
         this.trustedTemplateNames = Collections.EMPTY_SET;
         this.trustedTemplatePrefixes = Collections.EMPTY_LIST;
      }

   }

   public Class resolve(String className, Environment env, Template template) throws TemplateException {
      String templateName = this.safeGetTemplateName(template);
      if (templateName != null && (this.trustedTemplateNames.contains(templateName) || this.hasMatchingPrefix(templateName))) {
         return TemplateClassResolver.SAFER_RESOLVER.resolve(className, env, template);
      } else if (!this.allowedClasses.contains(className)) {
         throw new _MiscTemplateException(env, new Object[]{"Instantiating ", className, " is not allowed in the template for security reasons. (If you run into this problem when using ?new in a template, you may want to check the \"", "new_builtin_class_resolver", "\" setting in the FreeMarker configuration.)"});
      } else {
         try {
            return ClassUtil.forName(className);
         } catch (ClassNotFoundException var6) {
            throw new _MiscTemplateException(var6, env);
         }
      }
   }

   protected String safeGetTemplateName(Template template) {
      if (template == null) {
         return null;
      } else {
         String name = template.getName();
         if (name == null) {
            return null;
         } else {
            String decodedName = name;
            if (name.indexOf(37) != -1) {
               decodedName = StringUtil.replace(name, "%2e", ".", false, false);
               decodedName = StringUtil.replace(decodedName, "%2E", ".", false, false);
               decodedName = StringUtil.replace(decodedName, "%2f", "/", false, false);
               decodedName = StringUtil.replace(decodedName, "%2F", "/", false, false);
               decodedName = StringUtil.replace(decodedName, "%5c", "\\", false, false);
               decodedName = StringUtil.replace(decodedName, "%5C", "\\", false, false);
            }

            int dotDotIdx = decodedName.indexOf("..");
            if (dotDotIdx != -1) {
               int before = dotDotIdx - 1 >= 0 ? decodedName.charAt(dotDotIdx - 1) : -1;
               int after = dotDotIdx + 2 < decodedName.length() ? decodedName.charAt(dotDotIdx + 2) : -1;
               if ((before == -1 || before == 47 || before == 92) && (after == -1 || after == 47 || after == 92)) {
                  return null;
               }
            }

            return name.startsWith("/") ? name.substring(1) : name;
         }
      }
   }

   private boolean hasMatchingPrefix(String name) {
      for(int i = 0; i < this.trustedTemplatePrefixes.size(); ++i) {
         String prefix = (String)this.trustedTemplatePrefixes.get(i);
         if (name.startsWith(prefix)) {
            return true;
         }
      }

      return false;
   }
}
