package freemarker.core;

import freemarker.template.TemplateModelIterator;

abstract class LazilyGeneratedCollectionModel extends SingleIterationCollectionModel {
   private final boolean sequence;

   protected LazilyGeneratedCollectionModel(TemplateModelIterator iterator, boolean sequence) {
      super(iterator);
      this.sequence = sequence;
   }

   final boolean isSequence() {
      return this.sequence;
   }

   final LazilyGeneratedCollectionModel withIsSequenceTrue() {
      return this.isSequence() ? this : this.withIsSequenceFromFalseToTrue();
   }

   protected abstract LazilyGeneratedCollectionModel withIsSequenceFromFalseToTrue();
}
