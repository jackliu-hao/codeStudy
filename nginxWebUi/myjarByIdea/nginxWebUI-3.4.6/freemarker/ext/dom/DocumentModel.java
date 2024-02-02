package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

class DocumentModel extends NodeModel implements TemplateHashModel {
   private ElementModel rootElement;

   DocumentModel(Document doc) {
      super(doc);
   }

   public String getNodeName() {
      return "@document";
   }

   public TemplateModel get(String key) throws TemplateModelException {
      if (key.equals("*")) {
         return this.getRootElement();
      } else if (key.equals("**")) {
         NodeList nl = ((Document)this.node).getElementsByTagName("*");
         return new NodeListModel(nl, this);
      } else if (DomStringUtil.isXMLNameLike(key)) {
         ElementModel em = (ElementModel)NodeModel.wrap(((Document)this.node).getDocumentElement());
         return (TemplateModel)(em.matchesName(key, Environment.getCurrentEnvironment()) ? em : new NodeListModel(this));
      } else {
         return super.get(key);
      }
   }

   ElementModel getRootElement() {
      if (this.rootElement == null) {
         this.rootElement = (ElementModel)wrap(((Document)this.node).getDocumentElement());
      }

      return this.rootElement;
   }

   public boolean isEmpty() {
      return false;
   }
}
