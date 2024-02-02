package freemarker.ext.beans;

import freemarker.core.CollectionAndSequence;
import freemarker.ext.util.ModelFactory;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.MapKeyValuePairIterator;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.utility.RichObjectWrapper;
import java.util.List;
import java.util.Map;

public class SimpleMapModel extends WrappingTemplateModel implements TemplateHashModelEx2, TemplateMethodModelEx, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport {
   static final ModelFactory FACTORY = new ModelFactory() {
      public TemplateModel create(Object object, ObjectWrapper wrapper) {
         return new SimpleMapModel((Map)object, (BeansWrapper)wrapper);
      }
   };
   private final Map map;

   public SimpleMapModel(Map map, BeansWrapper wrapper) {
      super(wrapper);
      this.map = map;
   }

   public TemplateModel get(String key) throws TemplateModelException {
      Object val = this.map.get(key);
      if (val == null) {
         if (key.length() == 1) {
            Character charKey = key.charAt(0);
            val = this.map.get(charKey);
            if (val == null && !this.map.containsKey(key) && !this.map.containsKey(charKey)) {
               return null;
            }
         } else if (!this.map.containsKey(key)) {
            return null;
         }
      }

      return this.wrap(val);
   }

   public Object exec(List args) throws TemplateModelException {
      Object key = ((BeansWrapper)this.getObjectWrapper()).unwrap((TemplateModel)args.get(0));
      Object value = this.map.get(key);
      return value == null && !this.map.containsKey(key) ? null : this.wrap(value);
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public int size() {
      return this.map.size();
   }

   public TemplateCollectionModel keys() {
      return new CollectionAndSequence(new SimpleSequence(this.map.keySet(), this.getObjectWrapper()));
   }

   public TemplateCollectionModel values() {
      return new CollectionAndSequence(new SimpleSequence(this.map.values(), this.getObjectWrapper()));
   }

   public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
      return new MapKeyValuePairIterator(this.map, this.getObjectWrapper());
   }

   public Object getAdaptedObject(Class hint) {
      return this.map;
   }

   public Object getWrappedObject() {
      return this.map;
   }

   public TemplateModel getAPI() throws TemplateModelException {
      return ((RichObjectWrapper)this.getObjectWrapper()).wrapAsAPI(this.map);
   }
}
