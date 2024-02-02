package freemarker.template;

import java.util.List;

public class TemplateModelListSequence implements TemplateSequenceModel {
   private List list;

   public TemplateModelListSequence(List list) {
      this.list = list;
   }

   public TemplateModel get(int index) {
      return (TemplateModel)this.list.get(index);
   }

   public int size() {
      return this.list.size();
   }

   public Object getWrappedObject() {
      return this.list;
   }
}
