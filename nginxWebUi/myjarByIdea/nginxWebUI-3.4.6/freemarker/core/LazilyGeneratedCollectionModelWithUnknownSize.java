package freemarker.core;

import freemarker.template.TemplateModelIterator;

final class LazilyGeneratedCollectionModelWithUnknownSize extends LazilyGeneratedCollectionModel {
   public LazilyGeneratedCollectionModelWithUnknownSize(TemplateModelIterator iterator, boolean sequence) {
      super(iterator, sequence);
   }

   protected LazilyGeneratedCollectionModelWithUnknownSize withIsSequenceFromFalseToTrue() {
      return new LazilyGeneratedCollectionModelWithUnknownSize(this.getIterator(), true);
   }
}
