package freemarker.core;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import java.io.Serializable;
import java.util.ArrayList;

public final class CollectionAndSequence implements TemplateCollectionModel, TemplateSequenceModel, Serializable {
   private TemplateCollectionModel collection;
   private TemplateSequenceModel sequence;
   private ArrayList<TemplateModel> data;

   public CollectionAndSequence(TemplateCollectionModel collection) {
      this.collection = collection;
   }

   public CollectionAndSequence(TemplateSequenceModel sequence) {
      this.sequence = sequence;
   }

   public TemplateModelIterator iterator() throws TemplateModelException {
      return (TemplateModelIterator)(this.collection != null ? this.collection.iterator() : new SequenceIterator(this.sequence));
   }

   public TemplateModel get(int i) throws TemplateModelException {
      if (this.sequence != null) {
         return this.sequence.get(i);
      } else {
         this.initSequence();
         return (TemplateModel)this.data.get(i);
      }
   }

   public int size() throws TemplateModelException {
      if (this.sequence != null) {
         return this.sequence.size();
      } else if (this.collection instanceof TemplateCollectionModelEx) {
         return ((TemplateCollectionModelEx)this.collection).size();
      } else {
         this.initSequence();
         return this.data.size();
      }
   }

   private void initSequence() throws TemplateModelException {
      if (this.data == null) {
         this.data = new ArrayList();
         TemplateModelIterator it = this.collection.iterator();

         while(it.hasNext()) {
            this.data.add(it.next());
         }
      }

   }
}
