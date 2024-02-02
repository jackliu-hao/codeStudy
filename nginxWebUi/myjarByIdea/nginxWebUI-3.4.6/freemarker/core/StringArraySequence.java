package freemarker.core;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;

public class StringArraySequence implements TemplateSequenceModel {
   private String[] stringArray;
   private TemplateScalarModel[] array;

   public StringArraySequence(String[] stringArray) {
      this.stringArray = stringArray;
   }

   public TemplateModel get(int index) {
      if (this.array == null) {
         this.array = new TemplateScalarModel[this.stringArray.length];
      }

      TemplateScalarModel result = this.array[index];
      if (result == null) {
         result = new SimpleScalar(this.stringArray[index]);
         this.array[index] = (TemplateScalarModel)result;
      }

      return (TemplateModel)result;
   }

   public int size() {
      return this.stringArray.length;
   }
}
