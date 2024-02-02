package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import java.util.LinkedHashMap;
import java.util.Map;

public class _EnumModels extends ClassBasedModelFactory {
   public _EnumModels(BeansWrapper wrapper) {
      super(wrapper);
   }

   protected TemplateModel createModel(Class clazz) {
      Object[] obj = clazz.getEnumConstants();
      if (obj == null) {
         return null;
      } else {
         Map map = new LinkedHashMap();

         for(int i = 0; i < obj.length; ++i) {
            Enum value = (Enum)obj[i];
            map.put(value.name(), value);
         }

         return new SimpleMapModel(map, this.getWrapper());
      }
   }
}
