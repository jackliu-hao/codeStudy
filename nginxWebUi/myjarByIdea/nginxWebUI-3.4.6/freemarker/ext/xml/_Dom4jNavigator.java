package freemarker.ext.xml;

import freemarker.template.TemplateModelException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.tree.DefaultAttribute;
import org.jaxen.Context;
import org.jaxen.NamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

public class _Dom4jNavigator extends Navigator {
   void getAsString(Object node, StringWriter sw) {
      sw.getBuffer().append(((Node)node).asXML());
   }

   void getChildren(Object node, String localName, String namespaceUri, List result) {
      Element root;
      if (node instanceof Element) {
         root = (Element)node;
         if (localName == null) {
            result.addAll(root.elements());
         } else {
            result.addAll(root.elements(root.getQName().getDocumentFactory().createQName(localName, "", namespaceUri)));
         }
      } else if (node instanceof Document) {
         root = ((Document)node).getRootElement();
         if (localName == null || this.equal(root.getName(), localName) && this.equal(root.getNamespaceURI(), namespaceUri)) {
            result.add(root);
         }
      }

   }

   void getAttributes(Object node, String localName, String namespaceUri, List result) {
      if (node instanceof Element) {
         Element e = (Element)node;
         if (localName == null) {
            result.addAll(e.attributes());
         } else {
            Attribute attr = e.attribute(e.getQName().getDocumentFactory().createQName(localName, "", namespaceUri));
            if (attr != null) {
               result.add(attr);
            }
         }
      } else if (node instanceof ProcessingInstruction) {
         ProcessingInstruction pi = (ProcessingInstruction)node;
         if ("target".equals(localName)) {
            result.add(new DefaultAttribute("target", pi.getTarget()));
         } else if ("data".equals(localName)) {
            result.add(new DefaultAttribute("data", pi.getText()));
         } else {
            result.add(new DefaultAttribute(localName, pi.getValue(localName)));
         }
      } else if (node instanceof DocumentType) {
         DocumentType doctype = (DocumentType)node;
         if ("publicId".equals(localName)) {
            result.add(new DefaultAttribute("publicId", doctype.getPublicID()));
         } else if ("systemId".equals(localName)) {
            result.add(new DefaultAttribute("systemId", doctype.getSystemID()));
         } else if ("elementName".equals(localName)) {
            result.add(new DefaultAttribute("elementName", doctype.getElementName()));
         }
      }

   }

   void getDescendants(Object node, List result) {
      if (node instanceof Branch) {
         this.getDescendants((Branch)node, result);
      }

   }

   private void getDescendants(Branch node, List result) {
      List content = node.content();
      Iterator iter = content.iterator();

      while(iter.hasNext()) {
         Node subnode = (Node)iter.next();
         if (subnode instanceof Element) {
            result.add(subnode);
            this.getDescendants((Object)subnode, result);
         }
      }

   }

   Object getParent(Object node) {
      return ((Node)node).getParent();
   }

   Object getDocument(Object node) {
      return ((Node)node).getDocument();
   }

   Object getDocumentType(Object node) {
      return node instanceof Document ? ((Document)node).getDocType() : null;
   }

   void getContent(Object node, List result) {
      if (node instanceof Branch) {
         result.addAll(((Branch)node).content());
      }

   }

   String getText(Object node) {
      return ((Node)node).getText();
   }

   String getLocalName(Object node) {
      return ((Node)node).getName();
   }

   String getNamespacePrefix(Object node) {
      if (node instanceof Element) {
         return ((Element)node).getNamespacePrefix();
      } else {
         return node instanceof Attribute ? ((Attribute)node).getNamespacePrefix() : null;
      }
   }

   String getNamespaceUri(Object node) {
      if (node instanceof Element) {
         return ((Element)node).getNamespaceURI();
      } else {
         return node instanceof Attribute ? ((Attribute)node).getNamespaceURI() : null;
      }
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
         case 11:
         case 12:
         default:
            return "unknown";
         case 7:
            return "processingInstruction";
         case 8:
            return "comment";
         case 9:
            return "document";
         case 10:
            return "documentType";
         case 13:
            return "namespace";
      }
   }

   Navigator.XPathEx createXPathEx(String xpathString) throws TemplateModelException {
      try {
         return new Dom4jXPathEx(xpathString);
      } catch (Exception var3) {
         throw new TemplateModelException(var3);
      }
   }

   private static final class Dom4jXPathEx extends Dom4jXPath implements Navigator.XPathEx {
      Dom4jXPathEx(String path) throws Exception {
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
