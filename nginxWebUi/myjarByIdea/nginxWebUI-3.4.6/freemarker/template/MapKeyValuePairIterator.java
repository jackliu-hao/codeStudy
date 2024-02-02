package freemarker.template;

import java.util.Iterator;
import java.util.Map;

public class MapKeyValuePairIterator implements TemplateHashModelEx2.KeyValuePairIterator {
   private final Iterator<Map.Entry<?, ?>> entrySetIterator;
   private final ObjectWrapper objectWrapper;

   public <K, V> MapKeyValuePairIterator(Map<?, ?> map, ObjectWrapper objectWrapper) {
      this.entrySetIterator = map.entrySet().iterator();
      this.objectWrapper = objectWrapper;
   }

   public boolean hasNext() {
      return this.entrySetIterator.hasNext();
   }

   public TemplateHashModelEx2.KeyValuePair next() {
      final Map.Entry<?, ?> entry = (Map.Entry)this.entrySetIterator.next();
      return new TemplateHashModelEx2.KeyValuePair() {
         public TemplateModel getKey() throws TemplateModelException {
            return MapKeyValuePairIterator.this.wrap(entry.getKey());
         }

         public TemplateModel getValue() throws TemplateModelException {
            return MapKeyValuePairIterator.this.wrap(entry.getValue());
         }
      };
   }

   private TemplateModel wrap(Object obj) throws TemplateModelException {
      return obj instanceof TemplateModel ? (TemplateModel)obj : this.objectWrapper.wrap(obj);
   }
}
