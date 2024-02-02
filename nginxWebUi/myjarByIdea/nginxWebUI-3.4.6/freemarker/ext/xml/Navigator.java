package freemarker.ext.xml;

import freemarker.template.TemplateModelException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jaxen.NamespaceContext;

abstract class Navigator {
   private final Map xpathCache = new WeakHashMap();
   private final Map operators = this.createOperatorMap();
   private final NodeOperator attributeOperator = this.getOperator("_attributes");
   private final NodeOperator childrenOperator = this.getOperator("_children");

   NodeOperator getOperator(String key) {
      return (NodeOperator)this.operators.get(key);
   }

   NodeOperator getAttributeOperator() {
      return this.attributeOperator;
   }

   NodeOperator getChildrenOperator() {
      return this.childrenOperator;
   }

   abstract void getAsString(Object var1, StringWriter var2) throws TemplateModelException;

   List applyXPath(List nodes, String xpathString, Object namespaces) throws TemplateModelException {
      XPathEx xpath = null;

      try {
         synchronized(this.xpathCache) {
            xpath = (XPathEx)this.xpathCache.get(xpathString);
            if (xpath == null) {
               xpath = this.createXPathEx(xpathString);
               this.xpathCache.put(xpathString, xpath);
            }
         }

         return xpath.selectNodes(nodes, (NamespaceContext)namespaces);
      } catch (Exception var8) {
         throw new TemplateModelException("Could not evaulate XPath expression " + xpathString, var8);
      }
   }

   abstract XPathEx createXPathEx(String var1) throws TemplateModelException;

   abstract void getChildren(Object var1, String var2, String var3, List var4);

   abstract void getAttributes(Object var1, String var2, String var3, List var4);

   abstract void getDescendants(Object var1, List var2);

   abstract Object getParent(Object var1);

   abstract Object getDocument(Object var1);

   abstract Object getDocumentType(Object var1);

   private void getAncestors(Object node, List result) {
      while(true) {
         Object parent = this.getParent(node);
         if (parent == null) {
            return;
         }

         result.add(parent);
         node = parent;
      }
   }

   abstract void getContent(Object var1, List var2);

   abstract String getText(Object var1);

   abstract String getLocalName(Object var1);

   abstract String getNamespacePrefix(Object var1);

   String getQualifiedName(Object node) {
      String lname = this.getLocalName(node);
      if (lname == null) {
         return null;
      } else {
         String nsprefix = this.getNamespacePrefix(node);
         return nsprefix != null && nsprefix.length() != 0 ? nsprefix + ":" + lname : lname;
      }
   }

   abstract String getType(Object var1);

   abstract String getNamespaceUri(Object var1);

   boolean equal(String s1, String s2) {
      return s1 == null ? s2 == null : s1.equals(s2);
   }

   private Map createOperatorMap() {
      Map map = new HashMap();
      map.put("_attributes", new AttributesOp());
      map.put("@*", map.get("_attributes"));
      map.put("_children", new ChildrenOp());
      map.put("*", map.get("_children"));
      map.put("_descendantOrSelf", new DescendantOrSelfOp());
      map.put("_descendant", new DescendantOp());
      map.put("_document", new DocumentOp());
      map.put("_doctype", new DocumentTypeOp());
      map.put("_ancestor", new AncestorOp());
      map.put("_ancestorOrSelf", new AncestorOrSelfOp());
      map.put("_content", new ContentOp());
      map.put("_name", new LocalNameOp());
      map.put("_nsprefix", new NamespacePrefixOp());
      map.put("_nsuri", new NamespaceUriOp());
      map.put("_parent", new ParentOp());
      map.put("_qname", new QualifiedNameOp());
      map.put("_text", new TextOp());
      map.put("_type", new TypeOp());
      return map;
   }

   private class TypeOp implements NodeOperator {
      private TypeOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         result.add(Navigator.this.getType(node));
      }

      // $FF: synthetic method
      TypeOp(Object x1) {
         this();
      }
   }

   private class NamespaceUriOp implements NodeOperator {
      private NamespaceUriOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         String text = Navigator.this.getNamespaceUri(node);
         if (text != null) {
            result.add(text);
         }

      }

      // $FF: synthetic method
      NamespaceUriOp(Object x1) {
         this();
      }
   }

   private class NamespacePrefixOp implements NodeOperator {
      private NamespacePrefixOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         String text = Navigator.this.getNamespacePrefix(node);
         if (text != null) {
            result.add(text);
         }

      }

      // $FF: synthetic method
      NamespacePrefixOp(Object x1) {
         this();
      }
   }

   private class QualifiedNameOp implements NodeOperator {
      private QualifiedNameOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         String qname = Navigator.this.getQualifiedName(node);
         if (qname != null) {
            result.add(qname);
         }

      }

      // $FF: synthetic method
      QualifiedNameOp(Object x1) {
         this();
      }
   }

   private class LocalNameOp implements NodeOperator {
      private LocalNameOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         String text = Navigator.this.getLocalName(node);
         if (text != null) {
            result.add(text);
         }

      }

      // $FF: synthetic method
      LocalNameOp(Object x1) {
         this();
      }
   }

   private class TextOp implements NodeOperator {
      private TextOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         String text = Navigator.this.getText(node);
         if (text != null) {
            result.add(text);
         }

      }

      // $FF: synthetic method
      TextOp(Object x1) {
         this();
      }
   }

   private class ContentOp implements NodeOperator {
      private ContentOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         Navigator.this.getContent(node, result);
      }

      // $FF: synthetic method
      ContentOp(Object x1) {
         this();
      }
   }

   private class DocumentTypeOp implements NodeOperator {
      private DocumentTypeOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         Object documentType = Navigator.this.getDocumentType(node);
         if (documentType != null) {
            result.add(documentType);
         }

      }

      // $FF: synthetic method
      DocumentTypeOp(Object x1) {
         this();
      }
   }

   private class DocumentOp implements NodeOperator {
      private DocumentOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         Object document = Navigator.this.getDocument(node);
         if (document != null) {
            result.add(document);
         }

      }

      // $FF: synthetic method
      DocumentOp(Object x1) {
         this();
      }
   }

   private class ParentOp implements NodeOperator {
      private ParentOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         Object parent = Navigator.this.getParent(node);
         if (parent != null) {
            result.add(parent);
         }

      }

      // $FF: synthetic method
      ParentOp(Object x1) {
         this();
      }
   }

   private class AncestorOp implements NodeOperator {
      private AncestorOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         Navigator.this.getAncestors(node, result);
      }

      // $FF: synthetic method
      AncestorOp(Object x1) {
         this();
      }
   }

   private class AncestorOrSelfOp implements NodeOperator {
      private AncestorOrSelfOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         result.add(node);
         Navigator.this.getAncestors(node, result);
      }

      // $FF: synthetic method
      AncestorOrSelfOp(Object x1) {
         this();
      }
   }

   private class DescendantOp implements NodeOperator {
      private DescendantOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         Navigator.this.getDescendants(node, result);
      }

      // $FF: synthetic method
      DescendantOp(Object x1) {
         this();
      }
   }

   private class DescendantOrSelfOp implements NodeOperator {
      private DescendantOrSelfOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         result.add(node);
         Navigator.this.getDescendants(node, result);
      }

      // $FF: synthetic method
      DescendantOrSelfOp(Object x1) {
         this();
      }
   }

   private class AttributesOp implements NodeOperator {
      private AttributesOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         Navigator.this.getAttributes(node, localName, namespaceUri, result);
      }

      // $FF: synthetic method
      AttributesOp(Object x1) {
         this();
      }
   }

   private class ChildrenOp implements NodeOperator {
      private ChildrenOp() {
      }

      public void process(Object node, String localName, String namespaceUri, List result) {
         Navigator.this.getChildren(node, localName, namespaceUri, result);
      }

      // $FF: synthetic method
      ChildrenOp(Object x1) {
         this();
      }
   }

   interface XPathEx {
      List selectNodes(Object var1, NamespaceContext var2) throws TemplateModelException;
   }
}
