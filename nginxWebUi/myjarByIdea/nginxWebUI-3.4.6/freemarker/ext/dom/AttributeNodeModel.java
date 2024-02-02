package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.template.TemplateScalarModel;
import org.w3c.dom.Attr;

class AttributeNodeModel extends NodeModel implements TemplateScalarModel {
   public AttributeNodeModel(Attr att) {
      super(att);
   }

   public String getAsString() {
      return ((Attr)this.node).getValue();
   }

   public String getNodeName() {
      String result = this.node.getLocalName();
      if (result == null || result.equals("")) {
         result = this.node.getNodeName();
      }

      return result;
   }

   public boolean isEmpty() {
      return true;
   }

   String getQualifiedName() {
      String nsURI = this.node.getNamespaceURI();
      if (nsURI != null && !nsURI.equals("")) {
         Environment env = Environment.getCurrentEnvironment();
         String defaultNS = env.getDefaultNS();
         String prefix = null;
         if (nsURI.equals(defaultNS)) {
            prefix = "D";
         } else {
            prefix = env.getPrefixForNamespace(nsURI);
         }

         return prefix == null ? null : prefix + ":" + this.node.getLocalName();
      } else {
         return this.node.getNodeName();
      }
   }
}
