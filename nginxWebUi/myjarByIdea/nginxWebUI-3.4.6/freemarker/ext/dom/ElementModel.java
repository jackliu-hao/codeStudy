package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import java.util.Collections;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ElementModel extends NodeModel implements TemplateScalarModel {
   public ElementModel(Element element) {
      super(element);
   }

   public boolean isEmpty() {
      return false;
   }

   public TemplateModel get(String key) throws TemplateModelException {
      NodeListModel result;
      if (key.equals("*")) {
         result = new NodeListModel(this);
         TemplateSequenceModel children = this.getChildNodes();
         int size = children.size();

         for(int i = 0; i < size; ++i) {
            NodeModel child = (NodeModel)children.get(i);
            if (child.node.getNodeType() == 1) {
               result.add(child);
            }
         }

         return result;
      } else if (key.equals("**")) {
         return new NodeListModel(((Element)this.node).getElementsByTagName("*"), this);
      } else if (!key.startsWith("@")) {
         if (DomStringUtil.isXMLNameLike(key)) {
            result = ((NodeListModel)this.getChildNodes()).filterByName(key);
            return (TemplateModel)(result.size() != 1 ? result : result.get(0));
         } else {
            return super.get(key);
         }
      } else if (!key.startsWith("@@")) {
         if (DomStringUtil.isXMLNameLike(key, 1)) {
            Attr att = this.getAttribute(key.substring(1));
            return (TemplateModel)(att == null ? new NodeListModel(this) : wrap(att));
         } else {
            return (TemplateModel)(key.equals("@*") ? new NodeListModel(this.node.getAttributes(), this) : super.get(key));
         }
      } else if (key.equals(AtAtKey.ATTRIBUTES.getKey())) {
         return new NodeListModel(this.node.getAttributes(), this);
      } else {
         NodeOutputter nodeOutputter;
         if (key.equals(AtAtKey.START_TAG.getKey())) {
            nodeOutputter = new NodeOutputter(this.node);
            return new SimpleScalar(nodeOutputter.getOpeningTag((Element)this.node));
         } else if (key.equals(AtAtKey.END_TAG.getKey())) {
            nodeOutputter = new NodeOutputter(this.node);
            return new SimpleScalar(nodeOutputter.getClosingTag((Element)this.node));
         } else if (key.equals(AtAtKey.ATTRIBUTES_MARKUP.getKey())) {
            StringBuilder buf = new StringBuilder();
            NodeOutputter nu = new NodeOutputter(this.node);
            nu.outputContent(this.node.getAttributes(), buf);
            return new SimpleScalar(buf.toString().trim());
         } else {
            Node nextSibling;
            if (key.equals(AtAtKey.PREVIOUS_SIBLING_ELEMENT.getKey())) {
               for(nextSibling = this.node.getPreviousSibling(); nextSibling != null && !this.isSignificantNode(nextSibling); nextSibling = nextSibling.getPreviousSibling()) {
               }

               return (TemplateModel)(nextSibling != null && nextSibling.getNodeType() == 1 ? wrap(nextSibling) : new NodeListModel(Collections.emptyList(), (NodeModel)null));
            } else if (!key.equals(AtAtKey.NEXT_SIBLING_ELEMENT.getKey())) {
               return super.get(key);
            } else {
               for(nextSibling = this.node.getNextSibling(); nextSibling != null && !this.isSignificantNode(nextSibling); nextSibling = nextSibling.getNextSibling()) {
               }

               return (TemplateModel)(nextSibling != null && nextSibling.getNodeType() == 1 ? wrap(nextSibling) : new NodeListModel(Collections.emptyList(), (NodeModel)null));
            }
         }
      }
   }

   public String getAsString() throws TemplateModelException {
      NodeList nl = this.node.getChildNodes();
      String result = "";

      for(int i = 0; i < nl.getLength(); ++i) {
         Node child = nl.item(i);
         int nodeType = child.getNodeType();
         if (nodeType == 1) {
            String msg = "Only elements with no child elements can be processed as text.\nThis element with name \"" + this.node.getNodeName() + "\" has a child element named: " + child.getNodeName();
            throw new TemplateModelException(msg);
         }

         if (nodeType == 3 || nodeType == 4) {
            result = result + child.getNodeValue();
         }
      }

      return result;
   }

   public String getNodeName() {
      String result = this.node.getLocalName();
      if (result == null || result.equals("")) {
         result = this.node.getNodeName();
      }

      return result;
   }

   String getQualifiedName() {
      String nodeName = this.getNodeName();
      String nsURI = this.getNodeNamespace();
      if (nsURI != null && nsURI.length() != 0) {
         Environment env = Environment.getCurrentEnvironment();
         String defaultNS = env.getDefaultNS();
         String prefix;
         if (defaultNS != null && defaultNS.equals(nsURI)) {
            prefix = "";
         } else {
            prefix = env.getPrefixForNamespace(nsURI);
         }

         if (prefix == null) {
            return null;
         } else {
            if (prefix.length() > 0) {
               prefix = prefix + ":";
            }

            return prefix + nodeName;
         }
      } else {
         return nodeName;
      }
   }

   private Attr getAttribute(String qname) {
      Element element = (Element)this.node;
      Attr result = element.getAttributeNode(qname);
      if (result != null) {
         return result;
      } else {
         int colonIndex = qname.indexOf(58);
         if (colonIndex > 0) {
            String prefix = qname.substring(0, colonIndex);
            String uri;
            if (prefix.equals("D")) {
               uri = Environment.getCurrentEnvironment().getDefaultNS();
            } else {
               uri = Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
            }

            String localName = qname.substring(1 + colonIndex);
            if (uri != null) {
               result = element.getAttributeNodeNS(uri, localName);
            }
         }

         return result;
      }
   }

   private boolean isSignificantNode(Node node) throws TemplateModelException {
      return node.getNodeType() != 3 && node.getNodeType() != 4 ? node.getNodeType() != 7 && node.getNodeType() != 8 : !this.isBlankXMLText(node.getTextContent());
   }

   private boolean isBlankXMLText(String s) {
      if (s == null) {
         return true;
      } else {
         for(int i = 0; i < s.length(); ++i) {
            if (!this.isXMLWhiteSpace(s.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isXMLWhiteSpace(char c) {
      return c == ' ' || c == '\t' || c == '\n' | c == '\r';
   }

   boolean matchesName(String name, Environment env) {
      return DomStringUtil.matchesName(name, this.getNodeName(), this.getNodeNamespace(), env);
   }
}
