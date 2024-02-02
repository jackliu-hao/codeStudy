package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class NodeListModel extends SimpleSequence implements TemplateHashModel, _UnexpectedTypeErrorExplainerTemplateModel {
   NodeModel contextNode;
   XPathSupport xpathSupport;
   private static final ObjectWrapper NODE_WRAPPER = new ObjectWrapper() {
      public TemplateModel wrap(Object obj) {
         return obj instanceof NodeModel ? (NodeModel)obj : NodeModel.wrap((Node)obj);
      }
   };

   NodeListModel(Node contextNode) {
      this(NodeModel.wrap(contextNode));
   }

   NodeListModel(NodeModel contextNode) {
      super(NODE_WRAPPER);
      this.contextNode = contextNode;
   }

   NodeListModel(NodeList nodeList, NodeModel contextNode) {
      super(NODE_WRAPPER);

      for(int i = 0; i < nodeList.getLength(); ++i) {
         this.list.add(nodeList.item(i));
      }

      this.contextNode = contextNode;
   }

   NodeListModel(NamedNodeMap nodeList, NodeModel contextNode) {
      super(NODE_WRAPPER);

      for(int i = 0; i < nodeList.getLength(); ++i) {
         this.list.add(nodeList.item(i));
      }

      this.contextNode = contextNode;
   }

   NodeListModel(List list, NodeModel contextNode) {
      super(list, NODE_WRAPPER);
      this.contextNode = contextNode;
   }

   NodeListModel filterByName(String name) throws TemplateModelException {
      NodeListModel result = new NodeListModel(this.contextNode);
      int size = this.size();
      if (size == 0) {
         return result;
      } else {
         Environment env = Environment.getCurrentEnvironment();

         for(int i = 0; i < size; ++i) {
            NodeModel nm = (NodeModel)this.get(i);
            if (nm instanceof ElementModel && ((ElementModel)nm).matchesName(name, env)) {
               result.add(nm);
            }
         }

         return result;
      }
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public TemplateModel get(String key) throws TemplateModelException {
      int size = this.size();
      if (size == 1) {
         NodeModel nm = (NodeModel)this.get(0);
         return nm.get(key);
      } else {
         int i;
         NodeModel nm;
         if (key.startsWith("@@")) {
            if (key.equals(AtAtKey.MARKUP.getKey()) || key.equals(AtAtKey.NESTED_MARKUP.getKey()) || key.equals(AtAtKey.TEXT.getKey())) {
               StringBuilder result = new StringBuilder();

               for(i = 0; i < size; ++i) {
                  nm = (NodeModel)this.get(i);
                  TemplateScalarModel textModel = (TemplateScalarModel)nm.get(key);
                  result.append(textModel.getAsString());
               }

               return new SimpleScalar(result.toString());
            }

            if (key.length() != 2) {
               if (AtAtKey.containsKey(key)) {
                  throw new TemplateModelException("\"" + key + "\" is only applicable to a single XML node, but it was applied on " + (size != 0 ? size + " XML nodes (multiple matches)." : "an empty list of XML nodes (no matches)."));
               }

               throw new TemplateModelException("Unsupported @@ key: " + key);
            }
         }

         if (!DomStringUtil.isXMLNameLike(key) && (!key.startsWith("@") || !DomStringUtil.isXMLNameLike(key, 1) && !key.equals("@@") && !key.equals("@*")) && !key.equals("*") && !key.equals("**")) {
            XPathSupport xps = this.getXPathSupport();
            if (xps == null) {
               throw new TemplateModelException("No XPath support is available (add Apache Xalan or Jaxen as dependency). This is either malformed, or an XPath expression: " + key);
            } else {
               Object context = size == 0 ? null : this.rawNodeList();
               return xps.executeQuery(context, key);
            }
         } else {
            NodeListModel result = new NodeListModel(this.contextNode);

            for(i = 0; i < size; ++i) {
               nm = (NodeModel)this.get(i);
               if (nm instanceof ElementModel) {
                  TemplateSequenceModel tsm = (TemplateSequenceModel)nm.get(key);
                  if (tsm != null) {
                     int tsmSize = tsm.size();

                     for(int j = 0; j < tsmSize; ++j) {
                        result.add(tsm.get(j));
                     }
                  }
               }
            }

            if (result.size() == 1) {
               return result.get(0);
            } else {
               return result;
            }
         }
      }
   }

   private List rawNodeList() throws TemplateModelException {
      int size = this.size();
      ArrayList al = new ArrayList(size);

      for(int i = 0; i < size; ++i) {
         al.add(((NodeModel)this.get(i)).node);
      }

      return al;
   }

   XPathSupport getXPathSupport() throws TemplateModelException {
      if (this.xpathSupport == null) {
         if (this.contextNode != null) {
            this.xpathSupport = this.contextNode.getXPathSupport();
         } else if (this.size() > 0) {
            this.xpathSupport = ((NodeModel)this.get(0)).getXPathSupport();
         }
      }

      return this.xpathSupport;
   }

   public Object[] explainTypeError(Class[] expectedClasses) {
      for(int i = 0; i < expectedClasses.length; ++i) {
         Class expectedClass = expectedClasses[i];
         if (TemplateScalarModel.class.isAssignableFrom(expectedClass) || TemplateDateModel.class.isAssignableFrom(expectedClass) || TemplateNumberModel.class.isAssignableFrom(expectedClass) || TemplateBooleanModel.class.isAssignableFrom(expectedClass)) {
            return this.newTypeErrorExplanation("string");
         }

         if (TemplateNodeModel.class.isAssignableFrom(expectedClass)) {
            return this.newTypeErrorExplanation("node");
         }
      }

      return null;
   }

   private Object[] newTypeErrorExplanation(String type) {
      int size = this.size();
      return new Object[]{"This XML query result can't be used as ", type, " because for that it had to contain exactly 1 XML node, but it contains ", size, " nodes. That is, the constructing XML query has found ", size == 0 ? "no matches." : "multiple matches."};
   }
}
