package freemarker.template;

import freemarker.template.utility.Constants;
import java.util.List;

final class GeneralPurposeNothing implements TemplateBooleanModel, TemplateScalarModel, TemplateSequenceModel, TemplateHashModelEx2, TemplateMethodModelEx {
   private static final TemplateModel instance = new GeneralPurposeNothing();

   private GeneralPurposeNothing() {
   }

   static TemplateModel getInstance() {
      return instance;
   }

   public String getAsString() {
      return "";
   }

   public boolean getAsBoolean() {
      return false;
   }

   public boolean isEmpty() {
      return true;
   }

   public int size() {
      return 0;
   }

   public TemplateModel get(int i) throws TemplateModelException {
      throw new TemplateModelException("Can't get item from an empty sequence.");
   }

   public TemplateModel get(String key) {
      return null;
   }

   public Object exec(List args) {
      return null;
   }

   public TemplateCollectionModel keys() {
      return Constants.EMPTY_COLLECTION;
   }

   public TemplateCollectionModel values() {
      return Constants.EMPTY_COLLECTION;
   }

   public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
      return Constants.EMPTY_KEY_VALUE_PAIR_ITERATOR;
   }
}
