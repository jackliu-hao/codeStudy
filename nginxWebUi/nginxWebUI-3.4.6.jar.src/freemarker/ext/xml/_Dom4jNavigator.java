/*     */ package freemarker.ext.xml;
/*     */ 
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.io.StringWriter;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.dom4j.Attribute;
/*     */ import org.dom4j.Branch;
/*     */ import org.dom4j.Document;
/*     */ import org.dom4j.DocumentType;
/*     */ import org.dom4j.Element;
/*     */ import org.dom4j.Node;
/*     */ import org.dom4j.ProcessingInstruction;
/*     */ import org.dom4j.tree.DefaultAttribute;
/*     */ import org.jaxen.Context;
/*     */ import org.jaxen.NamespaceContext;
/*     */ import org.jaxen.dom4j.Dom4jXPath;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class _Dom4jNavigator
/*     */   extends Navigator
/*     */ {
/*     */   void getAsString(Object node, StringWriter sw) {
/*  51 */     sw.getBuffer().append(((Node)node).asXML());
/*     */   }
/*     */ 
/*     */   
/*     */   void getChildren(Object node, String localName, String namespaceUri, List<Element> result) {
/*  56 */     if (node instanceof Element) {
/*  57 */       Element e = (Element)node;
/*  58 */       if (localName == null) {
/*  59 */         result.addAll(e.elements());
/*     */       } else {
/*  61 */         result.addAll(e.elements(e.getQName().getDocumentFactory().createQName(localName, "", namespaceUri)));
/*     */       } 
/*  63 */     } else if (node instanceof Document) {
/*  64 */       Element root = ((Document)node).getRootElement();
/*  65 */       if (localName == null || (equal(root.getName(), localName) && equal(root.getNamespaceURI(), namespaceUri))) {
/*  66 */         result.add(root);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void getAttributes(Object node, String localName, String namespaceUri, List<Attribute> result) {
/*  73 */     if (node instanceof Element) {
/*  74 */       Element e = (Element)node;
/*  75 */       if (localName == null) {
/*  76 */         result.addAll(e.attributes());
/*     */       } else {
/*  78 */         Attribute attr = e.attribute(e.getQName().getDocumentFactory().createQName(localName, "", namespaceUri));
/*  79 */         if (attr != null) {
/*  80 */           result.add(attr);
/*     */         }
/*     */       } 
/*  83 */     } else if (node instanceof ProcessingInstruction) {
/*  84 */       ProcessingInstruction pi = (ProcessingInstruction)node;
/*  85 */       if ("target".equals(localName)) {
/*  86 */         result.add(new DefaultAttribute("target", pi.getTarget()));
/*  87 */       } else if ("data".equals(localName)) {
/*  88 */         result.add(new DefaultAttribute("data", pi.getText()));
/*     */       } else {
/*  90 */         result.add(new DefaultAttribute(localName, pi.getValue(localName)));
/*     */       } 
/*  92 */     } else if (node instanceof DocumentType) {
/*  93 */       DocumentType doctype = (DocumentType)node;
/*  94 */       if ("publicId".equals(localName)) {
/*  95 */         result.add(new DefaultAttribute("publicId", doctype.getPublicID()));
/*  96 */       } else if ("systemId".equals(localName)) {
/*  97 */         result.add(new DefaultAttribute("systemId", doctype.getSystemID()));
/*  98 */       } else if ("elementName".equals(localName)) {
/*  99 */         result.add(new DefaultAttribute("elementName", doctype.getElementName()));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void getDescendants(Object node, List result) {
/* 106 */     if (node instanceof Branch) {
/* 107 */       getDescendants((Branch)node, result);
/*     */     }
/*     */   }
/*     */   
/*     */   private void getDescendants(Branch node, List<Node> result) {
/* 112 */     List content = node.content();
/* 113 */     for (Iterator<Node> iter = content.iterator(); iter.hasNext(); ) {
/* 114 */       Node subnode = iter.next();
/* 115 */       if (subnode instanceof Element) {
/* 116 */         result.add(subnode);
/* 117 */         getDescendants(subnode, result);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParent(Object node) {
/* 124 */     return ((Node)node).getParent();
/*     */   }
/*     */ 
/*     */   
/*     */   Object getDocument(Object node) {
/* 129 */     return ((Node)node).getDocument();
/*     */   }
/*     */ 
/*     */   
/*     */   Object getDocumentType(Object node) {
/* 134 */     return (node instanceof Document) ? ((Document)node)
/*     */       
/* 136 */       .getDocType() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void getContent(Object node, List result) {
/* 142 */     if (node instanceof Branch) {
/* 143 */       result.addAll(((Branch)node).content());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   String getText(Object node) {
/* 149 */     return ((Node)node).getText();
/*     */   }
/*     */ 
/*     */   
/*     */   String getLocalName(Object node) {
/* 154 */     return ((Node)node).getName();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNamespacePrefix(Object node) {
/* 159 */     if (node instanceof Element) {
/* 160 */       return ((Element)node).getNamespacePrefix();
/*     */     }
/* 162 */     if (node instanceof Attribute) {
/* 163 */       return ((Attribute)node).getNamespacePrefix();
/*     */     }
/* 165 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   String getNamespaceUri(Object node) {
/* 170 */     if (node instanceof Element) {
/* 171 */       return ((Element)node).getNamespaceURI();
/*     */     }
/* 173 */     if (node instanceof Attribute) {
/* 174 */       return ((Attribute)node).getNamespaceURI();
/*     */     }
/* 176 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   String getType(Object node) {
/* 181 */     switch (((Node)node).getNodeType()) {
/*     */       case 2:
/* 183 */         return "attribute";
/*     */       
/*     */       case 4:
/* 186 */         return "cdata";
/*     */       
/*     */       case 8:
/* 189 */         return "comment";
/*     */       
/*     */       case 9:
/* 192 */         return "document";
/*     */       
/*     */       case 10:
/* 195 */         return "documentType";
/*     */       
/*     */       case 1:
/* 198 */         return "element";
/*     */       
/*     */       case 5:
/* 201 */         return "entityReference";
/*     */       
/*     */       case 13:
/* 204 */         return "namespace";
/*     */       
/*     */       case 7:
/* 207 */         return "processingInstruction";
/*     */       
/*     */       case 3:
/* 210 */         return "text";
/*     */     } 
/*     */     
/* 213 */     return "unknown";
/*     */   }
/*     */ 
/*     */   
/*     */   Navigator.XPathEx createXPathEx(String xpathString) throws TemplateModelException {
/*     */     try {
/* 219 */       return new Dom4jXPathEx(xpathString);
/* 220 */     } catch (Exception e) {
/* 221 */       throw new TemplateModelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class Dom4jXPathEx
/*     */     extends Dom4jXPath
/*     */     implements Navigator.XPathEx
/*     */   {
/*     */     Dom4jXPathEx(String path) throws Exception {
/* 232 */       super(path);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public List selectNodes(Object object, NamespaceContext namespaces) throws TemplateModelException {
/* 238 */       Context context = getContext(object);
/* 239 */       context.getContextSupport().setNamespaceContext(namespaces);
/*     */       try {
/* 241 */         return selectNodesForContext(context);
/* 242 */       } catch (Exception e) {
/* 243 */         throw new TemplateModelException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\xml\_Dom4jNavigator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */