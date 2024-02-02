package freemarker.core;

import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateModelIterator;

abstract class LazilyGeneratedCollectionModelEx extends LazilyGeneratedCollectionModel implements TemplateCollectionModelEx {
   LazilyGeneratedCollectionModelEx(TemplateModelIterator iterator, boolean sequence) {
      super(iterator, sequence);
   }
}
