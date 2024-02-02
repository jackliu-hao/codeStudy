package freemarker.ext.beans;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapModel extends StringModel implements TemplateMethodModelEx {
   static final ModelFactory FACTORY = new ModelFactory() {
      public TemplateModel create(Object object, ObjectWrapper wrapper) {
         return new MapModel((Map)object, (BeansWrapper)wrapper);
      }
   };

   public MapModel(Map map, BeansWrapper wrapper) {
      super(map, wrapper);
   }

   public Object exec(List arguments) throws TemplateModelException {
      Object key = this.unwrap((TemplateModel)arguments.get(0));
      return this.wrap(((Map)this.object).get(key));
   }

   protected TemplateModel invokeGenericGet(Map keyMap, Class clazz, String key) throws TemplateModelException {
      Map map = (Map)this.object;
      Object val = map.get(key);
      if (val == null) {
         if (key.length() == 1) {
            Character charKey = key.charAt(0);
            val = map.get(charKey);
            if (val == null && !map.containsKey(key) && !map.containsKey(charKey)) {
               return UNKNOWN;
            }
         } else if (!map.containsKey(key)) {
            return UNKNOWN;
         }
      }

      return this.wrap(val);
   }

   public boolean isEmpty() {
      return ((Map)this.object).isEmpty() && super.isEmpty();
   }

   public int size() {
      return this.keySet().size();
   }

   protected Set keySet() {
      Set set = super.keySet();
      set.addAll(((Map)this.object).keySet());
      return set;
   }
}
