package freemarker.ext.dom;

import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.template.utility.StringUtil;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class NodeOutputter {
   private Element contextNode;
   private Environment env;
   private String defaultNS;
   private boolean hasDefaultNS;
   private boolean explicitDefaultNSPrefix;
   private LinkedHashMap<String, String> namespacesToPrefixLookup = new LinkedHashMap();
   private String namespaceDecl;
   int nextGeneratedPrefixNumber = 1;

   NodeOutputter(Node node) {
      if (node instanceof Element) {
         this.setContext((Element)node);
      } else if (node instanceof Attr) {
         this.setContext(((Attr)node).getOwnerElement());
      } else if (node instanceof Document) {
         this.setContext(((Document)node).getDocumentElement());
      }

   }

   private void setContext(Element contextNode) {
      this.contextNode = contextNode;
      this.env = Environment.getCurrentEnvironment();
      this.defaultNS = this.env.getDefaultNS();
      this.hasDefaultNS = this.defaultNS != null && this.defaultNS.length() > 0;
      this.namespacesToPrefixLookup.put((Object)null, "");
      this.namespacesToPrefixLookup.put("", "");
      this.buildPrefixLookup(contextNode);
      if (!this.explicitDefaultNSPrefix && this.hasDefaultNS) {
         this.namespacesToPrefixLookup.put(this.defaultNS, "");
      }

      this.constructNamespaceDecl();
   }

   private void buildPrefixLookup(Node n) {
      String nsURI = n.getNamespaceURI();
      if (nsURI != null && nsURI.length() > 0) {
         String prefix = this.env.getPrefixForNamespace(nsURI);
         if (prefix == null) {
            prefix = (String)this.namespacesToPrefixLookup.get(nsURI);
            if (prefix == null) {
               do {
                  prefix = StringUtil.toLowerABC(this.nextGeneratedPrefixNumber++);
               } while(this.env.getNamespaceForPrefix(prefix) != null);
            }
         }

         this.namespacesToPrefixLookup.put(nsURI, prefix);
      } else if (this.hasDefaultNS && n.getNodeType() == 1) {
         this.namespacesToPrefixLookup.put(this.defaultNS, "D");
         this.explicitDefaultNSPrefix = true;
      } else if (n.getNodeType() == 2 && this.hasDefaultNS && this.defaultNS.equals(nsURI)) {
         this.namespacesToPrefixLookup.put(this.defaultNS, "D");
         this.explicitDefaultNSPrefix = true;
      }

      NodeList childNodes = n.getChildNodes();

      for(int i = 0; i < childNodes.getLength(); ++i) {
         this.buildPrefixLookup(childNodes.item(i));
      }

   }

   private void constructNamespaceDecl() {
      StringBuilder buf = new StringBuilder();
      if (this.explicitDefaultNSPrefix) {
         buf.append(" xmlns=\"");
         buf.append(this.defaultNS);
         buf.append("\"");
      }

      Iterator<String> it = this.namespacesToPrefixLookup.keySet().iterator();

      while(it.hasNext()) {
         String nsURI = (String)it.next();
         if (nsURI != null && nsURI.length() != 0) {
            String prefix = (String)this.namespacesToPrefixLookup.get(nsURI);
            if (prefix == null) {
               throw new BugException("No xmlns prefix was associated to URI: " + nsURI);
            }

            buf.append(" xmlns");
            if (prefix.length() > 0) {
               buf.append(":");
               buf.append(prefix);
            }

            buf.append("=\"");
            buf.append(nsURI);
            buf.append("\"");
         }
      }

      this.namespaceDecl = buf.toString();
   }

   private void outputQualifiedName(Node n, StringBuilder buf) {
      String nsURI = n.getNamespaceURI();
      if (nsURI != null && nsURI.length() != 0) {
         String prefix = (String)this.namespacesToPrefixLookup.get(nsURI);
         if (prefix == null) {
            buf.append(n.getNodeName());
         } else {
            if (prefix.length() > 0) {
               buf.append(prefix);
               buf.append(':');
            }

            buf.append(n.getLocalName());
         }
      } else {
         buf.append(n.getNodeName());
      }

   }

   void outputContent(Node n, StringBuilder buf) {
      switch (n.getNodeType()) {
         case 1:
            buf.append('<');
            this.outputQualifiedName(n, buf);
            if (n == this.contextNode) {
               buf.append(this.namespaceDecl);
            }

            this.outputContent(n.getAttributes(), buf);
            NodeList children = n.getChildNodes();
            if (children.getLength() == 0) {
               buf.append(" />");
            } else {
               buf.append('>');
               this.outputContent(n.getChildNodes(), buf);
               buf.append("</");
               this.outputQualifiedName(n, buf);
               buf.append('>');
            }
            break;
         case 2:
            if (((Attr)n).getSpecified()) {
               buf.append(' ');
               this.outputQualifiedName(n, buf);
               buf.append("=\"").append(StringUtil.XMLEncQAttr(n.getNodeValue())).append('"');
            }
            break;
         case 3:
         case 4:
            buf.append(StringUtil.XMLEncNQG(n.getNodeValue()));
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
               buf.append(" \"").append(dt.getSystemId()).append('"');
            }

            if (dt.getInternalSubset() != null) {
               buf.append(" [").append(dt.getInternalSubset()).append(']');
            }

            buf.append('>');
      }

   }

   void outputContent(NodeList nodes, StringBuilder buf) {
      for(int i = 0; i < nodes.getLength(); ++i) {
         this.outputContent(nodes.item(i), buf);
      }

   }

   void outputContent(NamedNodeMap nodes, StringBuilder buf) {
      for(int i = 0; i < nodes.getLength(); ++i) {
         Node n = nodes.item(i);
         if (n.getNodeType() != 2 || !n.getNodeName().startsWith("xmlns:") && !n.getNodeName().equals("xmlns")) {
            this.outputContent(n, buf);
         }
      }

   }

   String getOpeningTag(Element element) {
      StringBuilder buf = new StringBuilder();
      buf.append('<');
      this.outputQualifiedName(element, buf);
      buf.append(this.namespaceDecl);
      this.outputContent(element.getAttributes(), buf);
      buf.append('>');
      return buf.toString();
   }

   String getClosingTag(Element element) {
      StringBuilder buf = new StringBuilder();
      buf.append("</");
      this.outputQualifiedName(element, buf);
      buf.append('>');
      return buf.toString();
   }
}
