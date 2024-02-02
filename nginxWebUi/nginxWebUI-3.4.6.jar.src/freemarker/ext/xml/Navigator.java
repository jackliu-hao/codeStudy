/*     */ package freemarker.ext.xml;
/*     */ 
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.io.StringWriter;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import org.jaxen.NamespaceContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class Navigator
/*     */ {
/*  36 */   private final Map xpathCache = new WeakHashMap<>();
/*     */   
/*  38 */   private final Map operators = createOperatorMap();
/*  39 */   private final NodeOperator attributeOperator = getOperator("_attributes");
/*  40 */   private final NodeOperator childrenOperator = getOperator("_children");
/*     */   
/*     */   NodeOperator getOperator(String key) {
/*  43 */     return (NodeOperator)this.operators.get(key);
/*     */   }
/*     */   
/*     */   NodeOperator getAttributeOperator() {
/*  47 */     return this.attributeOperator;
/*     */   }
/*     */   
/*     */   NodeOperator getChildrenOperator() {
/*  51 */     return this.childrenOperator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   List applyXPath(List nodes, String xpathString, Object namespaces) throws TemplateModelException {
/*  59 */     XPathEx xpath = null;
/*     */     try {
/*  61 */       synchronized (this.xpathCache) {
/*  62 */         xpath = (XPathEx)this.xpathCache.get(xpathString);
/*  63 */         if (xpath == null) {
/*  64 */           xpath = createXPathEx(xpathString);
/*  65 */           this.xpathCache.put(xpathString, xpath);
/*     */         } 
/*     */       } 
/*  68 */       return xpath.selectNodes(nodes, (NamespaceContext)namespaces);
/*  69 */     } catch (Exception e) {
/*  70 */       throw new TemplateModelException("Could not evaulate XPath expression " + xpathString, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void getAncestors(Object node, List<Object> result) {
/*     */     while (true) {
/*  94 */       Object parent = getParent(node);
/*  95 */       if (parent == null) {
/*     */         break;
/*     */       }
/*  98 */       result.add(parent);
/*  99 */       node = parent;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getQualifiedName(Object node) {
/* 112 */     String lname = getLocalName(node);
/* 113 */     if (lname == null) {
/* 114 */       return null;
/*     */     }
/* 116 */     String nsprefix = getNamespacePrefix(node);
/* 117 */     if (nsprefix == null || nsprefix.length() == 0) {
/* 118 */       return lname;
/*     */     }
/* 120 */     return nsprefix + ":" + lname;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean equal(String s1, String s2) {
/* 129 */     return (s1 == null) ? ((s2 == null)) : s1.equals(s2);
/*     */   } abstract void getAsString(Object paramObject, StringWriter paramStringWriter) throws TemplateModelException; abstract XPathEx createXPathEx(String paramString) throws TemplateModelException; abstract void getChildren(Object paramObject, String paramString1, String paramString2, List paramList); abstract void getAttributes(Object paramObject, String paramString1, String paramString2, List paramList); abstract void getDescendants(Object paramObject, List paramList); abstract Object getParent(Object paramObject); abstract Object getDocument(Object paramObject);
/*     */   abstract Object getDocumentType(Object paramObject);
/*     */   private Map createOperatorMap() {
/* 133 */     Map<Object, Object> map = new HashMap<>();
/* 134 */     map.put("_attributes", new AttributesOp());
/* 135 */     map.put("@*", map.get("_attributes"));
/* 136 */     map.put("_children", new ChildrenOp());
/* 137 */     map.put("*", map.get("_children"));
/* 138 */     map.put("_descendantOrSelf", new DescendantOrSelfOp());
/* 139 */     map.put("_descendant", new DescendantOp());
/* 140 */     map.put("_document", new DocumentOp());
/* 141 */     map.put("_doctype", new DocumentTypeOp());
/* 142 */     map.put("_ancestor", new AncestorOp());
/* 143 */     map.put("_ancestorOrSelf", new AncestorOrSelfOp());
/* 144 */     map.put("_content", new ContentOp());
/* 145 */     map.put("_name", new LocalNameOp());
/* 146 */     map.put("_nsprefix", new NamespacePrefixOp());
/* 147 */     map.put("_nsuri", new NamespaceUriOp());
/* 148 */     map.put("_parent", new ParentOp());
/* 149 */     map.put("_qname", new QualifiedNameOp());
/* 150 */     map.put("_text", new TextOp());
/* 151 */     map.put("_type", new TypeOp());
/* 152 */     return map;
/*     */   } abstract void getContent(Object paramObject, List paramList); abstract String getText(Object paramObject); abstract String getLocalName(Object paramObject); abstract String getNamespacePrefix(Object paramObject); abstract String getType(Object paramObject); abstract String getNamespaceUri(Object paramObject);
/*     */   static interface XPathEx {
/*     */     List selectNodes(Object param1Object, NamespaceContext param1NamespaceContext) throws TemplateModelException; }
/*     */   private class ChildrenOp implements NodeOperator { private ChildrenOp() {}
/*     */     public void process(Object node, String localName, String namespaceUri, List result) {
/* 158 */       Navigator.this.getChildren(node, localName, namespaceUri, result);
/*     */     } }
/*     */ 
/*     */   
/*     */   private class AttributesOp implements NodeOperator { private AttributesOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List result) {
/* 165 */       Navigator.this.getAttributes(node, localName, namespaceUri, result);
/*     */     } }
/*     */   
/*     */   private class DescendantOrSelfOp implements NodeOperator {
/*     */     private DescendantOrSelfOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List<Object> result) {
/* 172 */       result.add(node);
/* 173 */       Navigator.this.getDescendants(node, result);
/*     */     }
/*     */   }
/*     */   
/*     */   private class DescendantOp implements NodeOperator { private DescendantOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List result) {
/* 180 */       Navigator.this.getDescendants(node, result);
/*     */     } }
/*     */   
/*     */   private class AncestorOrSelfOp implements NodeOperator {
/*     */     private AncestorOrSelfOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List<Object> result) {
/* 187 */       result.add(node);
/* 188 */       Navigator.this.getAncestors(node, result);
/*     */     }
/*     */   }
/*     */   
/*     */   private class AncestorOp implements NodeOperator { private AncestorOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List result) {
/* 195 */       Navigator.this.getAncestors(node, result);
/*     */     } }
/*     */   
/*     */   private class ParentOp implements NodeOperator {
/*     */     private ParentOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List<Object> result) {
/* 202 */       Object parent = Navigator.this.getParent(node);
/* 203 */       if (parent != null)
/* 204 */         result.add(parent); 
/*     */     }
/*     */   }
/*     */   
/*     */   private class DocumentOp implements NodeOperator {
/*     */     private DocumentOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List<Object> result) {
/* 212 */       Object document = Navigator.this.getDocument(node);
/* 213 */       if (document != null)
/* 214 */         result.add(document); 
/*     */     }
/*     */   }
/*     */   
/*     */   private class DocumentTypeOp implements NodeOperator {
/*     */     private DocumentTypeOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List<Object> result) {
/* 222 */       Object documentType = Navigator.this.getDocumentType(node);
/* 223 */       if (documentType != null)
/* 224 */         result.add(documentType); 
/*     */     }
/*     */   }
/*     */   
/*     */   private class ContentOp implements NodeOperator {
/*     */     private ContentOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List result) {
/* 232 */       Navigator.this.getContent(node, result);
/*     */     } }
/*     */   
/*     */   private class TextOp implements NodeOperator {
/*     */     private TextOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List<String> result) {
/* 239 */       String text = Navigator.this.getText(node);
/* 240 */       if (text != null)
/* 241 */         result.add(text); 
/*     */     }
/*     */   }
/*     */   
/*     */   private class LocalNameOp implements NodeOperator {
/*     */     private LocalNameOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List<String> result) {
/* 249 */       String text = Navigator.this.getLocalName(node);
/* 250 */       if (text != null)
/* 251 */         result.add(text); 
/*     */     }
/*     */   }
/*     */   
/*     */   private class QualifiedNameOp implements NodeOperator {
/*     */     private QualifiedNameOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List<String> result) {
/* 259 */       String qname = Navigator.this.getQualifiedName(node);
/* 260 */       if (qname != null)
/* 261 */         result.add(qname); 
/*     */     }
/*     */   }
/*     */   
/*     */   private class NamespacePrefixOp implements NodeOperator {
/*     */     private NamespacePrefixOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List<String> result) {
/* 269 */       String text = Navigator.this.getNamespacePrefix(node);
/* 270 */       if (text != null)
/* 271 */         result.add(text); 
/*     */     }
/*     */   }
/*     */   
/*     */   private class NamespaceUriOp implements NodeOperator {
/*     */     private NamespaceUriOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List<String> result) {
/* 279 */       String text = Navigator.this.getNamespaceUri(node);
/* 280 */       if (text != null)
/* 281 */         result.add(text); 
/*     */     }
/*     */   }
/*     */   
/*     */   private class TypeOp implements NodeOperator {
/*     */     private TypeOp() {}
/*     */     
/*     */     public void process(Object node, String localName, String namespaceUri, List<String> result) {
/* 289 */       result.add(Navigator.this.getType(node));
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\xml\Navigator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */