package freemarker.template.utility;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import java.util.List;

public class ObjectConstructor implements TemplateMethodModelEx {
   public Object exec(List args) throws TemplateModelException {
      if (args.isEmpty()) {
         throw new TemplateModelException("This method must have at least one argument, the name of the class to instantiate.");
      } else {
         String classname = args.get(0).toString();
         Class cl = null;

         try {
            cl = ClassUtil.forName(classname);
         } catch (Exception var6) {
            throw new TemplateModelException(var6.getMessage());
         }

         BeansWrapper bw = BeansWrapper.getDefaultInstance();
         Object obj = bw.newInstance(cl, args.subList(1, args.size()));
         return bw.wrap(obj);
      }
   }
}
