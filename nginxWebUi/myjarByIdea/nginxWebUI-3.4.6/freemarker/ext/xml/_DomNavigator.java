package freemarker.ext.xml;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.StringWriter;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.NamespaceContext;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class _DomNavigator extends Navigator {
   void getAsString(Object node, StringWriter sw) {
      this.outputContent((Node)node, sw);
   }

   private void outputContent(Node n, StringWriter buf) {
      switch (n.getNodeType()) {
         case 1:
            buf.append('<').append(this.getQualifiedName(n));
            this.outputContent(n.getAttributes(), buf);
            buf.append('>');
            this.outputContent(n.getChildNodes(), buf);
            buf.append("</").append(this.getQualifiedName(n)).append('>');
            break;
         case 2:
            buf.append(' ').append(this.getQualifiedName(n)).append("=\"").append(StringUtil.XMLEncNA(n.getNodeValue())).append('"');
            break;
         case 3:
            buf.append(StringUtil.XMLEncNQG(n.getNodeValue()));
            break;
         case 4:
            buf.append("<![CDATA[").append(n.getNodeValue()).append("]]>");
            break;
         case 5:
            buf.append('&').append(n.getNodeName()).append(';');
            break;
         case 6:
            this.outputContent(n.getChildNodes(), buf);
            break;
         case 7:
            buf.append("<?").append(n.getNodeName()).append(' ').append(n.getNodeValue()).append("?>");
            break;
         case 8:
            buf.append("<!--").append(n.getNodeValue()).append("-->");
            break;
         case 9:
            this.outputContent(n.getChildNodes(), buf);
            break;
         case 10:
            buf.append("<!DOCTYPE ").append(n.getNodeName());
            DocumentType dt = (DocumentType)n;
            if (dt.getPublicId() != null) {
               buf.append(" PUBLIC \"").append(dt.getPublicId()).append('"');
            }

            if (dt.getSystemId() != null) {
               buf.append('"').append(dt.getSystemId()).append('"');
            }

            if (dt.getInternalSubset() != null) {
               buf.append(" [").append(dt.getInternalSubset()).append(']');
            }

            buf.append('>');
      }

   }

   private void outputContent(NodeList nodes, StringWriter buf) {
      for(int i = 0; i < nodes.getLength(); ++i) {
         this.outputContent(nodes.item(i), buf);
      }

   }

   private void outputContent(NamedNodeMap nodes, StringWriter buf) {
      for(int i = 0; i < nodes.getLength(); ++i) {
         this.outputContent(nodes.item(i), buf);
      }

   }

   void getChildren(Object node, String localName, String namespaceUri, List result) {
      if ("".equals(namespaceUri)) {
         namespaceUri = null;
      }

      NodeList children = ((Node)node).getChildNodes();

      for(int i = 0; i < children.getLength(); ++i) {
         Node subnode = children.item(i);
         if ((subnode.getNodeType() == 1 || subnode.getNodeType() == 3) && (localName == null || this.equal(subnode.getNodeName(), localName) && this.equal(subnode.getNamespaceURI(), namespaceUri))) {
            result.add(subnode);
         }
      }

   }

   void getAttributes(Object node, String localName, String namespaceUri, List result) {
      if (node instanceof Element) {
         Element e = (Element)node;
         if (localName == null) {
            NamedNodeMap atts = e.getAttributes();

            for(int i = 0; i < atts.getLength(); ++i) {
               result.add(atts.item(i));
            }
         } else {
            if ("".equals(namespaceUri)) {
               namespaceUri = null;
            }

            Attr attr = e.getAttributeNodeNS(namespaceUri, localName);
            if (attr != null) {
               result.add(attr);
            }
         }
      } else if (node instanceof ProcessingInstruction) {
         ProcessingInstruction pi = (ProcessingInstruction)node;
         if ("target".equals(localName)) {
            result.add(this.createAttribute(pi, "target", pi.getTarget()));
         } else if ("data".equals(localName)) {
            result.add(this.createAttribute(pi, "data", pi.getData()));
         }
      } else if (node instanceof DocumentType) {
         DocumentType doctype = (DocumentType)node;
         if ("publicId".equals(localName)) {
            result.add(this.createAttribute(doctype, "publicId", doctype.getPublicId()));
         } else if ("systemId".equals(localName)) {
            result.add(this.createAttribute(doctype, "systemId", doctype.getSystemId()));
         } else if ("elementName".equals(localName)) {
            result.add(this.createAttribute(doctype, "elementName", doctype.getNodeName()));
         }
      }

   }

   private Attr createAttribute(Node node, String name, String value) {
      Attr attr = node.getOwnerDocument().createAttribute(name);
      attr.setNodeValue(value);
      return attr;
   }

   void getDescendants(Object node, List result) {
      NodeList children = ((Node)node).getChildNodes();

      for(int i = 0; i < children.getLength(); ++i) {
         Node subnode = children.item(i);
         if (subnode.getNodeType() == 1) {
            result.add(subnode);
            this.getDescendants(subnode, result);
         }
      }

   }

   Object getParent(Object node) {
      return ((Node)node).getParentNode();
   }

   Object getDocument(Object node) {
      return ((Node)node).getOwnerDocument();
   }

   Object getDocumentType(Object node) {
      return node instanceof Document ? ((Document)node).getDoctype() : null;
   }

   void getContent(Object node, List result) {
      NodeList children = ((Node)node).getChildNodes();

      for(int i = 0; i < children.getLength(); ++i) {
         result.add(children.item(i));
      }

   }

   String getText(Object node) {
      StringBuilder buf = new StringBuilder();
      if (node instanceof Element) {
         NodeList children = ((Node)node).getChildNodes();

         for(int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child instanceof Text) {
               buf.append(child.getNodeValue());
            }
         }

         return buf.toString();
      } else {
         return ((Node)node).getNodeValue();
      }
   }

   String getLocalName(Object node) {
      return ((Node)node).getNodeName();
   }

   String getNamespacePrefix(Object node) {
      return ((Node)node).getPrefix();
   }

   String getNamespaceUri(Object node) {
      return ((Node)node).getNamespaceURI();
   }

   String getType(Object node) {
      switch (((Node)node).getNodeType()) {
         case 1:
            return "element";
         case 2:
            return "attribute";
         case 3:
            return "text";
         case 4:
            return "cdata";
         case 5:
            return "entityReference";
         case 6:
            return "entity";
         case 7:
            return "processingInstruction";
         case 8:
            return "comment";
         case 9:
            return "document";
         case 10:
            return "documentType";
         default:
            return "unknown";
      }
   }

   Navigator.XPathEx createXPathEx(String xpathString) throws TemplateModelException {
      try {
         return new DomXPathEx(xpathString);
      } catch (Exception var3) {
         throw new TemplateModelException(var3);
      }
   }

   private static final class DomXPathEx extends DOMXPath implements Navigator.XPathEx {
      DomXPathEx(String path) throws Exception {
         super(path);
      }

      public List selectNodes(Object object, NamespaceContext namespaces) throws TemplateModelException {
         Context context = this.getContext(object);
         context.getContextSupport().setNamespaceContext(namespaces);

         try {
            return this.selectNodesForContext(context);
         } catch (Exception var5) {
            throw new TemplateModelException(var5);
         }
      }
   }
}
