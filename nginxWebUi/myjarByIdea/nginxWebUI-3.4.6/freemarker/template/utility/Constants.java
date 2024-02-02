package freemarker.template.utility;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class Constants {
   public static final TemplateBooleanModel TRUE;
   public static final TemplateBooleanModel FALSE;
   public static final TemplateScalarModel EMPTY_STRING;
   public static final TemplateNumberModel ZERO;
   public static final TemplateNumberModel ONE;
   public static final TemplateNumberModel MINUS_ONE;
   public static final TemplateModelIterator EMPTY_ITERATOR;
   public static final TemplateCollectionModel EMPTY_COLLECTION;
   public static final TemplateSequenceModel EMPTY_SEQUENCE;
   public static final TemplateHashModelEx2 EMPTY_HASH_EX2;
   public static final TemplateHashModelEx EMPTY_HASH;
   public static final TemplateHashModelEx2.KeyValuePairIterator EMPTY_KEY_VALUE_PAIR_ITERATOR;

   static {
      TRUE = TemplateBooleanModel.TRUE;
      FALSE = TemplateBooleanModel.FALSE;
      EMPTY_STRING = (TemplateScalarModel)TemplateScalarModel.EMPTY_STRING;
      ZERO = new SimpleNumber(0);
      ONE = new SimpleNumber(1);
      MINUS_ONE = new SimpleNumber(-1);
      EMPTY_ITERATOR = new EmptyIteratorModel();
      EMPTY_COLLECTION = new EmptyCollectionModel();
      EMPTY_SEQUENCE = new EmptySequenceModel();
      EMPTY_HASH_EX2 = new EmptyHashModel();
      EMPTY_HASH = EMPTY_HASH_EX2;
      EMPTY_KEY_VALUE_PAIR_ITERATOR = new EmptyKeyValuePairIterator();
   }

   private static class EmptyKeyValuePairIterator implements TemplateHashModelEx2.KeyValuePairIterator {
      private EmptyKeyValuePairIterator() {
      }

      public boolean hasNext() throws TemplateModelException {
         return false;
      }

      public TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException {
         throw new NoSuchElementException("Can't retrieve element from empty key-value pair iterator.");
      }

      // $FF: synthetic method
      EmptyKeyValuePairIterator(Object x0) {
         this();
      }
   }

   private static class EmptyHashModel implements TemplateHashModelEx2, Serializable {
      private EmptyHashModel() {
      }

      public int size() throws TemplateModelException {
         return 0;
      }

      public TemplateCollectionModel keys() throws TemplateModelException {
         return Constants.EMPTY_COLLECTION;
      }

      public TemplateCollectionModel values() throws TemplateModelException {
         return Constants.EMPTY_COLLECTION;
      }

      public TemplateModel get(String key) throws TemplateModelException {
         return null;
      }

      public boolean isEmpty() throws TemplateModelException {
         return true;
      }

      public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
         return Constants.EMPTY_KEY_VALUE_PAIR_ITERATOR;
      }

      // $FF: synthetic method
      EmptyHashModel(Object x0) {
         this();
      }
   }

   private static class EmptySequenceModel implements TemplateSequenceModel, Serializable {
      private EmptySequenceModel() {
      }

      public TemplateModel get(int index) throws TemplateModelException {
         return null;
      }

      public int size() throws TemplateModelException {
         return 0;
      }

      // $FF: synthetic method
      EmptySequenceModel(Object x0) {
         this();
      }
   }

   private static class EmptyCollectionModel implements TemplateCollectionModel, Serializable {
      private EmptyCollectionModel() {
      }

      public TemplateModelIterator iterator() throws TemplateModelException {
         return Constants.EMPTY_ITERATOR;
      }

      // $FF: synthetic method
      EmptyCollectionModel(Object x0) {
         this();
      }
   }

   private static class EmptyIteratorModel implements TemplateModelIterator, Serializable {
      private EmptyIteratorModel() {
      }

      public TemplateModel next() throws TemplateModelException {
         throw new TemplateModelException("The collection has no more elements.");
      }

      public boolean hasNext() throws TemplateModelException {
         return false;
      }

      // $FF: synthetic method
      EmptyIteratorModel(Object x0) {
         this();
      }
   }
}
