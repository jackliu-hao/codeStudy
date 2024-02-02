/*     */ package freemarker.ext.xml;
/*     */ 
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.StringWriter;
/*     */ import java.util.List;
/*     */ import org.jaxen.Context;
/*     */ import org.jaxen.NamespaceContext;
/*     */ import org.jaxen.dom.DOMXPath;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.DocumentType;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.ProcessingInstruction;
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
/*     */ public class _DomNavigator
/*     */   extends Navigator
/*     */ {
/*     */   void getAsString(Object node, StringWriter sw) {
/*  51 */     outputContent((Node)node, sw);
/*     */   }
/*     */   private void outputContent(Node n, StringWriter buf) {
/*     */     DocumentType dt;
/*  55 */     switch (n.getNodeType()) {
/*     */       case 2:
/*  57 */         buf.append(' ')
/*  58 */           .append(getQualifiedName(n))
/*  59 */           .append("=\"")
/*  60 */           .append(StringUtil.XMLEncNA(n.getNodeValue()))
/*  61 */           .append('"');
/*     */         break;
/*     */       
/*     */       case 4:
/*  65 */         buf.append("<![CDATA[").append(n.getNodeValue()).append("]]>");
/*     */         break;
/*     */       
/*     */       case 8:
/*  69 */         buf.append("<!--").append(n.getNodeValue()).append("-->");
/*     */         break;
/*     */       
/*     */       case 9:
/*  73 */         outputContent(n.getChildNodes(), buf);
/*     */         break;
/*     */       
/*     */       case 10:
/*  77 */         buf.append("<!DOCTYPE ").append(n.getNodeName());
/*  78 */         dt = (DocumentType)n;
/*  79 */         if (dt.getPublicId() != null) {
/*  80 */           buf.append(" PUBLIC \"").append(dt.getPublicId()).append('"');
/*     */         }
/*  82 */         if (dt.getSystemId() != null) {
/*  83 */           buf.append('"').append(dt.getSystemId()).append('"');
/*     */         }
/*  85 */         if (dt.getInternalSubset() != null) {
/*  86 */           buf.append(" [").append(dt.getInternalSubset()).append(']');
/*     */         }
/*  88 */         buf.append('>');
/*     */         break;
/*     */       
/*     */       case 1:
/*  92 */         buf.append('<').append(getQualifiedName(n));
/*  93 */         outputContent(n.getAttributes(), buf);
/*  94 */         buf.append('>');
/*  95 */         outputContent(n.getChildNodes(), buf);
/*  96 */         buf.append("</").append(getQualifiedName(n)).append('>');
/*     */         break;
/*     */       
/*     */       case 6:
/* 100 */         outputContent(n.getChildNodes(), buf);
/*     */         break;
/*     */       
/*     */       case 5:
/* 104 */         buf.append('&').append(n.getNodeName()).append(';');
/*     */         break;
/*     */       
/*     */       case 7:
/* 108 */         buf.append("<?").append(n.getNodeName()).append(' ').append(n.getNodeValue()).append("?>");
/*     */         break;
/*     */       
/*     */       case 3:
/* 112 */         buf.append(StringUtil.XMLEncNQG(n.getNodeValue()));
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void outputContent(NodeList nodes, StringWriter buf) {
/* 119 */     for (int i = 0; i < nodes.getLength(); i++) {
/* 120 */       outputContent(nodes.item(i), buf);
/*     */     }
/*     */   }
/*     */   
/*     */   private void outputContent(NamedNodeMap nodes, StringWriter buf) {
/* 125 */     for (int i = 0; i < nodes.getLength(); i++) {
/* 126 */       outputContent(nodes.item(i), buf);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   void getChildren(Object node, String localName, String namespaceUri, List<Node> result) {
/* 132 */     if ("".equals(namespaceUri)) {
/* 133 */       namespaceUri = null;
/*     */     }
/* 135 */     NodeList children = ((Node)node).getChildNodes();
/* 136 */     for (int i = 0; i < children.getLength(); i++) {
/* 137 */       Node subnode = children.item(i);
/*     */       
/* 139 */       if ((subnode.getNodeType() == 1 || subnode.getNodeType() == 3) && (
/* 140 */         localName == null || (equal(subnode.getNodeName(), localName) && equal(subnode.getNamespaceURI(), namespaceUri)))) {
/* 141 */         result.add(subnode);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void getAttributes(Object node, String localName, String namespaceUri, List<Node> result) {
/* 149 */     if (node instanceof Element) {
/* 150 */       Element e = (Element)node;
/* 151 */       if (localName == null) {
/* 152 */         NamedNodeMap atts = e.getAttributes();
/* 153 */         for (int i = 0; i < atts.getLength(); i++) {
/* 154 */           result.add(atts.item(i));
/*     */         }
/*     */       } else {
/* 157 */         if ("".equals(namespaceUri)) {
/* 158 */           namespaceUri = null;
/*     */         }
/* 160 */         Attr attr = e.getAttributeNodeNS(namespaceUri, localName);
/* 161 */         if (attr != null) {
/* 162 */           result.add(attr);
/*     */         }
/*     */       } 
/* 165 */     } else if (node instanceof ProcessingInstruction) {
/* 166 */       ProcessingInstruction pi = (ProcessingInstruction)node;
/* 167 */       if ("target".equals(localName)) {
/* 168 */         result.add(createAttribute(pi, "target", pi.getTarget()));
/* 169 */       } else if ("data".equals(localName)) {
/* 170 */         result.add(createAttribute(pi, "data", pi.getData()));
/*     */       
/*     */       }
/*     */ 
/*     */     
/*     */     }
/* 176 */     else if (node instanceof DocumentType) {
/* 177 */       DocumentType doctype = (DocumentType)node;
/* 178 */       if ("publicId".equals(localName)) {
/* 179 */         result.add(createAttribute(doctype, "publicId", doctype.getPublicId()));
/* 180 */       } else if ("systemId".equals(localName)) {
/* 181 */         result.add(createAttribute(doctype, "systemId", doctype.getSystemId()));
/* 182 */       } else if ("elementName".equals(localName)) {
/* 183 */         result.add(createAttribute(doctype, "elementName", doctype.getNodeName()));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private Attr createAttribute(Node node, String name, String value) {
/* 189 */     Attr attr = node.getOwnerDocument().createAttribute(name);
/* 190 */     attr.setNodeValue(value);
/* 191 */     return attr;
/*     */   }
/*     */ 
/*     */   
/*     */   void getDescendants(Object node, List<Node> result) {
/* 196 */     NodeList children = ((Node)node).getChildNodes();
/* 197 */     for (int i = 0; i < children.getLength(); i++) {
/* 198 */       Node subnode = children.item(i);
/* 199 */       if (subnode.getNodeType() == 1) {
/* 200 */         result.add(subnode);
/* 201 */         getDescendants(subnode, result);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParent(Object node) {
/* 208 */     return ((Node)node).getParentNode();
/*     */   }
/*     */ 
/*     */   
/*     */   Object getDocument(Object node) {
/* 213 */     return ((Node)node).getOwnerDocument();
/*     */   }
/*     */ 
/*     */   
/*     */   Object getDocumentType(Object node) {
/* 218 */     return (node instanceof Document) ? ((Document)node)
/*     */       
/* 220 */       .getDoctype() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void getContent(Object node, List<Node> result) {
/* 226 */     NodeList children = ((Node)node).getChildNodes();
/* 227 */     for (int i = 0; i < children.getLength(); i++) {
/* 228 */       result.add(children.item(i));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   String getText(Object node) {
/* 234 */     StringBuilder buf = new StringBuilder();
/* 235 */     if (node instanceof Element) {
/* 236 */       NodeList children = ((Node)node).getChildNodes();
/* 237 */       for (int i = 0; i < children.getLength(); i++) {
/* 238 */         Node child = children.item(i);
/* 239 */         if (child instanceof org.w3c.dom.Text) {
/* 240 */           buf.append(child.getNodeValue());
/*     */         }
/*     */       } 
/* 243 */       return buf.toString();
/*     */     } 
/* 245 */     return ((Node)node).getNodeValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   String getLocalName(Object node) {
/* 251 */     return ((Node)node).getNodeName();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNamespacePrefix(Object node) {
/* 256 */     return ((Node)node).getPrefix();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNamespaceUri(Object node) {
/* 261 */     return ((Node)node).getNamespaceURI();
/*     */   }
/*     */ 
/*     */   
/*     */   String getType(Object node) {
/* 266 */     switch (((Node)node).getNodeType()) {
/*     */       case 2:
/* 268 */         return "attribute";
/*     */       
/*     */       case 4:
/* 271 */         return "cdata";
/*     */       
/*     */       case 8:
/* 274 */         return "comment";
/*     */       
/*     */       case 9:
/* 277 */         return "document";
/*     */       
/*     */       case 10:
/* 280 */         return "documentType";
/*     */       
/*     */       case 1:
/* 283 */         return "element";
/*     */       
/*     */       case 6:
/* 286 */         return "entity";
/*     */       
/*     */       case 5:
/* 289 */         return "entityReference";
/*     */       
/*     */       case 7:
/* 292 */         return "processingInstruction";
/*     */       
/*     */       case 3:
/* 295 */         return "text";
/*     */     } 
/*     */     
/* 298 */     return "unknown";
/*     */   }
/*     */ 
/*     */   
/*     */   Navigator.XPathEx createXPathEx(String xpathString) throws TemplateModelException {
/*     */     try {
/* 304 */       return new DomXPathEx(xpathString);
/* 305 */     } catch (Exception e) {
/* 306 */       throw new TemplateModelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class DomXPathEx
/*     */     extends DOMXPath
/*     */     implements Navigator.XPathEx
/*     */   {
/*     */     DomXPathEx(String path) throws Exception {
/* 317 */       super(path);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public List selectNodes(Object object, NamespaceContext namespaces) throws TemplateModelException {
/* 323 */       Context context = getContext(object);
/* 324 */       context.getContextSupport().setNamespaceContext(namespaces);
/*     */       try {
/* 326 */         return selectNodesForContext(context);
/* 327 */       } catch (Exception e) {
/* 328 */         throw new TemplateModelException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\xml\_DomNavigator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */