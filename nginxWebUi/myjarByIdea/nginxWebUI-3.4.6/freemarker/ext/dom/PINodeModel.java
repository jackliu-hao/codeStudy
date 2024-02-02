package freemarker.ext.dom;

import freemarker.template.TemplateScalarModel;
import org.w3c.dom.ProcessingInstruction;

class PINodeModel extends NodeModel implements TemplateScalarModel {
   public PINodeModel(ProcessingInstruction pi) {
      super(pi);
   }

   public String getAsString() {
      return ((ProcessingInstruction)this.node).getData();
   }

   public String getNodeName() {
      return "@pi$" + ((ProcessingInstruction)this.node).getTarget();
   }

   public boolean isEmpty() {
      return true;
   }
}
