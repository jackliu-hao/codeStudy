package freemarker.core;

import freemarker.template.TemplateModel;

final class TemplateNullModel implements TemplateModel {
   static final TemplateNullModel INSTANCE = new TemplateNullModel();

   private TemplateNullModel() {
   }
}
