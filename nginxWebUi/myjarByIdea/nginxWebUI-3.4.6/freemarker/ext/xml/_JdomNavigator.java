package freemarker.ext.xml;

import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.NamespaceContext;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.output.XMLOutputter;

public class _JdomNavigator extends Navigator {
   private static final XMLOutputter OUTPUT = new XMLOutputter();

   void getAsString(Object node, StringWriter sw) throws TemplateModelException {
      try {
         if (node instanceof Element) {
            OUTPUT.output((Element)node, sw);
         } else if (node instanceof Attribute) {
            Attribute attribute = (Attribute)node;
            sw.write(" ");
            sw.write(attribute.getQualifiedName());
            sw.write("=\"");
            sw.write(OUTPUT.escapeAttributeEntities(attribute.getValue()));
            sw.write("\"");
         } else if (node instanceof Text) {
            OUTPUT.output((Text)node, sw);
         } else if (node instanceof Document) {
            OUTPUT.output((Document)node, sw);
         } else if (node instanceof ProcessingInstruction) {
            OUTPUT.output((ProcessingInstruction)node, sw);
         } else if (node instanceof Comment) {
            OUTPUT.output((Comment)node, sw);
         } else if (node instanceof CDATA) {
            OUTPUT.output((CDATA)node, sw);
         } else if (node instanceof DocType) {
            OUTPUT.output((DocType)node, sw);
         } else {
            if (!(node instanceof EntityRef)) {
               throw new TemplateModelException(node.getClass().getName() + " is not a core JDOM class");
            }

            OUTPUT.output((EntityRef)node, sw);
         }

      } catch (IOException var4) {
         throw new TemplateModelException(var4);
      }
   }

   void getChildren(Object node, String localName, String namespaceUri, List result) {
      Element root;
      if (node instanceof Element) {
         root = (Element)node;
         if (localName == null) {
            result.addAll(root.getChildren());
         } else {
            result.addAll(root.getChildren(localName, Namespace.getNamespace("", namespaceUri)));
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
            result.addAll(e.getAttributes());
         } else {
            Attribute attr = e.getAttribute(localName, Namespace.getNamespace("", namespaceUri));
            if (attr != null) {
               result.add(attr);
            }
         }
      } else if (node instanceof ProcessingInstruction) {
         ProcessingInstruction pi = (ProcessingInstruction)node;
         if ("target".equals(localName)) {
            result.add(new Attribute("target", pi.getTarget()));
         } else if ("data".equals(localName)) {
            result.add(new Attribute("data", pi.getData()));
         } else {
            result.add(new Attribute(localName, pi.getValue(localName)));
         }
      } else if (node instanceof DocType) {
         DocType doctype = (DocType)node;
         if ("publicId".equals(localName)) {
            result.add(new Attribute("publicId", doctype.getPublicID()));
         } else if ("systemId".equals(localName)) {
            result.add(new Attribute("systemId", doctype.getSystemID()));
         } else if ("elementName".equals(localName)) {
            result.add(new Attribute("elementName", doctype.getElementName()));
         }
      }

   }

   void getDescendants(Object node, List result) {
      if (node instanceof Document) {
         Element root = ((Document)node).getRootElement();
         result.add(root);
         this.getDescendants(root, result);
      } else if (node instanceof Element) {
         this.getDescendants((Element)node, result);
      }

   }

   private void getDescendants(Element node, List result) {
      Iterator iter = node.getChildren().iterator();

      while(iter.hasNext()) {
         Element subnode = (Element)iter.next();
         result.add(subnode);
         this.getDescendants(subnode, result);
      }

   }

   Object getParent(Object node) {
      if (node instanceof Element) {
         return ((Element)node).getParent();
      } else if (node instanceof Attribute) {
         return ((Attribute)node).getParent();
      } else if (node instanceof Text) {
         return ((Text)node).getParent();
      } else if (node instanceof ProcessingInstruction) {
         return ((ProcessingInstruction)node).getParent();
      } else if (node instanceof Comment) {
         return ((Comment)node).getParent();
      } else {
         return node instanceof EntityRef ? ((EntityRef)node).getParent() : null;
      }
   }

   Object getDocument(Object node) {
      if (node instanceof Element) {
         return ((Element)node).getDocument();
      } else {
         Element parent;
         if (node instanceof Attribute) {
            parent = ((Attribute)node).getParent();
            return parent == null ? null : parent.getDocument();
         } else if (node instanceof Text) {
            parent = ((Text)node).getParent();
            return parent == null ? null : parent.getDocument();
         } else if (node instanceof Document) {
            return node;
         } else if (node instanceof ProcessingInstruction) {
            return ((ProcessingInstruction)node).getDocument();
         } else if (node instanceof EntityRef) {
            return ((EntityRef)node).getDocument();
         } else {
            return node instanceof Comment ? ((Comment)node).getDocument() : null;
         }
      }
   }

   Object getDocumentType(Object node) {
      return node instanceof Document ? ((Document)node).getDocType() : null;
   }

   void getContent(Object node, List result) {
      if (node instanceof Element) {
         result.addAll(((Element)node).getContent());
      } else if (node instanceof Document) {
         result.addAll(((Document)node).getContent());
      }

   }

   String getText(Object node) {
      if (node instanceof Element) {
         return ((Element)node).getTextTrim();
      } else if (node instanceof Attribute) {
         return ((Attribute)node).getValue();
      } else if (node instanceof CDATA) {
         return ((CDATA)node).getText();
      } else if (node instanceof Comment) {
         return ((Comment)node).getText();
      } else {
         return node instanceof ProcessingInstruction ? ((ProcessingInstruction)node).getData() : null;
      }
   }

   String getLocalName(Object node) {
      if (node instanceof Element) {
         return ((Element)node).getName();
      } else if (node instanceof Attribute) {
         return ((Attribute)node).getName();
      } else if (node instanceof EntityRef) {
         return ((EntityRef)node).getName();
      } else if (node instanceof ProcessingInstruction) {
         return ((ProcessingInstruction)node).getTarget();
      } else {
         return node instanceof DocType ? ((DocType)node).getElementName() : null;
      }
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
      if (node instanceof Attribute) {
         return "attribute";
      } else if (node instanceof CDATA) {
         return "cdata";
      } else if (node instanceof Comment) {
         return "comment";
      } else if (node instanceof Document) {
         return "document";
      } else if (node instanceof DocType) {
         return "documentType";
      } else if (node instanceof Element) {
         return "element";
      } else if (node instanceof EntityRef) {
         return "entityReference";
      } else if (node instanceof Namespace) {
         return "namespace";
      } else if (node instanceof ProcessingInstruction) {
         return "processingInstruction";
      } else {
         return node instanceof Text ? "text" : "unknown";
      }
   }

   Navigator.XPathEx createXPathEx(String xpathString) throws TemplateModelException {
      try {
         return new JDOMXPathEx(xpathString);
      } catch (Exception var3) {
         throw new TemplateModelException(var3);
      }
   }

   private static final class JDOMXPathEx extends JDOMXPath implements Navigator.XPathEx {
      JDOMXPathEx(String path) throws Exception {
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
