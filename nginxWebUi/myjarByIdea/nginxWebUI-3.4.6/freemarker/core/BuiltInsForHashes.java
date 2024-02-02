package freemarker.core;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

class BuiltInsForHashes {
   private BuiltInsForHashes() {
   }

   static class valuesBI extends BuiltInForHashEx {
      TemplateModel calculateResult(TemplateHashModelEx hashExModel, Environment env) throws TemplateModelException, InvalidReferenceException {
         TemplateCollectionModel values = hashExModel.values();
         if (values == null) {
            throw this.newNullPropertyException("values", hashExModel, env);
         } else {
            return (TemplateModel)(values instanceof TemplateSequenceModel ? values : new CollectionAndSequence(values));
         }
      }
   }

   static class keysBI extends BuiltInForHashEx {
      TemplateModel calculateResult(TemplateHashModelEx hashExModel, Environment env) throws TemplateModelException, InvalidReferenceException {
         TemplateCollectionModel keys = hashExModel.keys();
         if (keys == null) {
            throw this.newNullPropertyException("keys", hashExModel, env);
         } else {
            return (TemplateModel)(keys instanceof TemplateSequenceModel ? keys : new CollectionAndSequence(keys));
         }
      }
   }
}
