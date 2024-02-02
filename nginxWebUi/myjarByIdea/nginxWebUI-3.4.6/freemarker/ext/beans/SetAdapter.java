package freemarker.ext.beans;

import freemarker.template.TemplateCollectionModel;
import java.util.Set;

class SetAdapter extends CollectionAdapter implements Set {
   SetAdapter(TemplateCollectionModel model, BeansWrapper wrapper) {
      super(model, wrapper);
   }
}
