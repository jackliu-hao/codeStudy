package freemarker.template;

import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import java.io.Serializable;
import java.util.Map;
import java.util.SortedMap;

public class DefaultMapAdapter extends WrappingTemplateModel implements TemplateHashModelEx2, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable {
   private final Map map;

   public static DefaultMapAdapter adapt(Map map, ObjectWrapperWithAPISupport wrapper) {
      return new DefaultMapAdapter(map, wrapper);
   }

   private DefaultMapAdapter(Map map, ObjectWrapper wrapper) {
      super(wrapper);
      this.map = map;
   }

   public TemplateModel get(String key) throws TemplateModelException {
      Object val;
      try {
         val = this.map.get(key);
      } catch (ClassCastException var5) {
         throw new _TemplateModelException(var5, new Object[]{"ClassCastException while getting Map entry with String key ", new _DelayedJQuote(key)});
      } catch (NullPointerException var6) {
         throw new _TemplateModelException(var6, new Object[]{"NullPointerException while getting Map entry with String key ", new _DelayedJQuote(key)});
      }

      if (val == null) {
         if (key.length() != 1 || this.map instanceof SortedMap) {
            TemplateModel wrappedNull = this.wrap((Object)null);
            return wrappedNull != null && this.map.containsKey(key) ? wrappedNull : null;
         }

         Character charKey = key.charAt(0);

         try {
            val = this.map.get(charKey);
            if (val == null) {
               TemplateModel wrappedNull = this.wrap((Object)null);
               if (wrappedNull != null && (this.map.containsKey(key) || this.map.containsKey(charKey))) {
                  return wrappedNull;
               }

               return null;
            }
         } catch (ClassCastException var7) {
            throw new _TemplateModelException(var7, new Object[]{"Class casting exception while getting Map entry with Character key ", new _DelayedJQuote(charKey)});
         } catch (NullPointerException var8) {
            throw new _TemplateModelException(var8, new Object[]{"NullPointerException while getting Map entry with Character key ", new _DelayedJQuote(charKey)});
         }
      }

      return this.wrap(val);
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public int size() {
      return this.map.size();
   }

   public TemplateCollectionModel keys() {
      return new SimpleCollection(this.map.keySet(), this.getObjectWrapper());
   }

   public TemplateCollectionModel values() {
      return new SimpleCollection(this.map.values(), this.getObjectWrapper());
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
      return ((ObjectWrapperWithAPISupport)this.getObjectWrapper()).wrapAsAPI(this.map);
   }
}
