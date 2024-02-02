/*     */ package freemarker.ext.xml;
/*     */ 
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.io.IOException;
/*     */ import java.io.StringWriter;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.jaxen.Context;
/*     */ import org.jaxen.NamespaceContext;
/*     */ import org.jaxen.jdom.JDOMXPath;
/*     */ import org.jdom.Attribute;
/*     */ import org.jdom.CDATA;
/*     */ import org.jdom.Comment;
/*     */ import org.jdom.DocType;
/*     */ import org.jdom.Document;
/*     */ import org.jdom.Element;
/*     */ import org.jdom.EntityRef;
/*     */ import org.jdom.Namespace;
/*     */ import org.jdom.ProcessingInstruction;
/*     */ import org.jdom.Text;
/*     */ import org.jdom.output.XMLOutputter;
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
/*     */ public class _JdomNavigator
/*     */   extends Navigator
/*     */ {
/*  49 */   private static final XMLOutputter OUTPUT = new XMLOutputter();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void getAsString(Object node, StringWriter sw) throws TemplateModelException {
/*     */     try {
/*  58 */       if (node instanceof Element) {
/*  59 */         OUTPUT.output((Element)node, sw);
/*  60 */       } else if (node instanceof Attribute) {
/*  61 */         Attribute attribute = (Attribute)node;
/*  62 */         sw.write(" ");
/*  63 */         sw.write(attribute.getQualifiedName());
/*  64 */         sw.write("=\"");
/*  65 */         sw.write(OUTPUT.escapeAttributeEntities(attribute.getValue()));
/*  66 */         sw.write("\"");
/*  67 */       } else if (node instanceof Text) {
/*  68 */         OUTPUT.output((Text)node, sw);
/*  69 */       } else if (node instanceof Document) {
/*  70 */         OUTPUT.output((Document)node, sw);
/*  71 */       } else if (node instanceof ProcessingInstruction) {
/*  72 */         OUTPUT.output((ProcessingInstruction)node, sw);
/*  73 */       } else if (node instanceof Comment) {
/*  74 */         OUTPUT.output((Comment)node, sw);
/*  75 */       } else if (node instanceof CDATA) {
/*  76 */         OUTPUT.output((CDATA)node, sw);
/*  77 */       } else if (node instanceof DocType) {
/*  78 */         OUTPUT.output((DocType)node, sw);
/*  79 */       } else if (node instanceof EntityRef) {
/*  80 */         OUTPUT.output((EntityRef)node, sw);
/*     */       } else {
/*  82 */         throw new TemplateModelException(node.getClass().getName() + " is not a core JDOM class");
/*     */       } 
/*  84 */     } catch (IOException e) {
/*  85 */       throw new TemplateModelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void getChildren(Object node, String localName, String namespaceUri, List<Element> result) {
/*  91 */     if (node instanceof Element) {
/*  92 */       Element e = (Element)node;
/*  93 */       if (localName == null) {
/*  94 */         result.addAll(e.getChildren());
/*     */       } else {
/*  96 */         result.addAll(e.getChildren(localName, Namespace.getNamespace("", namespaceUri)));
/*     */       } 
/*  98 */     } else if (node instanceof Document) {
/*  99 */       Element root = ((Document)node).getRootElement();
/* 100 */       if (localName == null || (equal(root.getName(), localName) && equal(root.getNamespaceURI(), namespaceUri))) {
/* 101 */         result.add(root);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void getAttributes(Object node, String localName, String namespaceUri, List<Attribute> result) {
/* 108 */     if (node instanceof Element) {
/* 109 */       Element e = (Element)node;
/* 110 */       if (localName == null) {
/* 111 */         result.addAll(e.getAttributes());
/*     */       } else {
/* 113 */         Attribute attr = e.getAttribute(localName, Namespace.getNamespace("", namespaceUri));
/* 114 */         if (attr != null) {
/* 115 */           result.add(attr);
/*     */         }
/*     */       } 
/* 118 */     } else if (node instanceof ProcessingInstruction) {
/* 119 */       ProcessingInstruction pi = (ProcessingInstruction)node;
/* 120 */       if ("target".equals(localName)) {
/* 121 */         result.add(new Attribute("target", pi.getTarget()));
/* 122 */       } else if ("data".equals(localName)) {
/* 123 */         result.add(new Attribute("data", pi.getData()));
/*     */       } else {
/* 125 */         result.add(new Attribute(localName, pi.getValue(localName)));
/*     */       } 
/* 127 */     } else if (node instanceof DocType) {
/* 128 */       DocType doctype = (DocType)node;
/* 129 */       if ("publicId".equals(localName)) {
/* 130 */         result.add(new Attribute("publicId", doctype.getPublicID()));
/* 131 */       } else if ("systemId".equals(localName)) {
/* 132 */         result.add(new Attribute("systemId", doctype.getSystemID()));
/* 133 */       } else if ("elementName".equals(localName)) {
/* 134 */         result.add(new Attribute("elementName", doctype.getElementName()));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void getDescendants(Object node, List<Element> result) {
/* 141 */     if (node instanceof Document) {
/* 142 */       Element root = ((Document)node).getRootElement();
/* 143 */       result.add(root);
/* 144 */       getDescendants(root, result);
/* 145 */     } else if (node instanceof Element) {
/* 146 */       getDescendants((Element)node, result);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void getDescendants(Element node, List<Element> result) {
/* 151 */     for (Iterator<Element> iter = node.getChildren().iterator(); iter.hasNext(); ) {
/* 152 */       Element subnode = iter.next();
/* 153 */       result.add(subnode);
/* 154 */       getDescendants(subnode, result);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParent(Object node) {
/* 160 */     if (node instanceof Element) {
/* 161 */       return ((Element)node).getParent();
/*     */     }
/* 163 */     if (node instanceof Attribute) {
/* 164 */       return ((Attribute)node).getParent();
/*     */     }
/* 166 */     if (node instanceof Text) {
/* 167 */       return ((Text)node).getParent();
/*     */     }
/* 169 */     if (node instanceof ProcessingInstruction) {
/* 170 */       return ((ProcessingInstruction)node).getParent();
/*     */     }
/* 172 */     if (node instanceof Comment) {
/* 173 */       return ((Comment)node).getParent();
/*     */     }
/* 175 */     if (node instanceof EntityRef) {
/* 176 */       return ((EntityRef)node).getParent();
/*     */     }
/* 178 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getDocument(Object node) {
/* 183 */     if (node instanceof Element)
/* 184 */       return ((Element)node).getDocument(); 
/* 185 */     if (node instanceof Attribute) {
/* 186 */       Element parent = ((Attribute)node).getParent();
/* 187 */       return (parent == null) ? null : parent.getDocument();
/* 188 */     }  if (node instanceof Text) {
/* 189 */       Element parent = ((Text)node).getParent();
/* 190 */       return (parent == null) ? null : parent.getDocument();
/* 191 */     }  if (node instanceof Document)
/* 192 */       return node; 
/* 193 */     if (node instanceof ProcessingInstruction)
/* 194 */       return ((ProcessingInstruction)node).getDocument(); 
/* 195 */     if (node instanceof EntityRef)
/* 196 */       return ((EntityRef)node).getDocument(); 
/* 197 */     if (node instanceof Comment) {
/* 198 */       return ((Comment)node).getDocument();
/*     */     }
/* 200 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getDocumentType(Object node) {
/* 205 */     return (node instanceof Document) ? ((Document)node)
/*     */       
/* 207 */       .getDocType() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void getContent(Object node, List result) {
/* 213 */     if (node instanceof Element) {
/* 214 */       result.addAll(((Element)node).getContent());
/* 215 */     } else if (node instanceof Document) {
/* 216 */       result.addAll(((Document)node).getContent());
/*     */     } 
/*     */   }
/*     */   
/*     */   String getText(Object node) {
/* 221 */     if (node instanceof Element) {
/* 222 */       return ((Element)node).getTextTrim();
/*     */     }
/* 224 */     if (node instanceof Attribute) {
/* 225 */       return ((Attribute)node).getValue();
/*     */     }
/* 227 */     if (node instanceof CDATA) {
/* 228 */       return ((CDATA)node).getText();
/*     */     }
/* 230 */     if (node instanceof Comment) {
/* 231 */       return ((Comment)node).getText();
/*     */     }
/* 233 */     if (node instanceof ProcessingInstruction) {
/* 234 */       return ((ProcessingInstruction)node).getData();
/*     */     }
/* 236 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   String getLocalName(Object node) {
/* 241 */     if (node instanceof Element) {
/* 242 */       return ((Element)node).getName();
/*     */     }
/* 244 */     if (node instanceof Attribute) {
/* 245 */       return ((Attribute)node).getName();
/*     */     }
/* 247 */     if (node instanceof EntityRef) {
/* 248 */       return ((EntityRef)node).getName();
/*     */     }
/* 250 */     if (node instanceof ProcessingInstruction) {
/* 251 */       return ((ProcessingInstruction)node).getTarget();
/*     */     }
/* 253 */     if (node instanceof DocType) {
/* 254 */       return ((DocType)node).getElementName();
/*     */     }
/* 256 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   String getNamespacePrefix(Object node) {
/* 261 */     if (node instanceof Element) {
/* 262 */       return ((Element)node).getNamespacePrefix();
/*     */     }
/* 264 */     if (node instanceof Attribute) {
/* 265 */       return ((Attribute)node).getNamespacePrefix();
/*     */     }
/* 267 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   String getNamespaceUri(Object node) {
/* 272 */     if (node instanceof Element) {
/* 273 */       return ((Element)node).getNamespaceURI();
/*     */     }
/* 275 */     if (node instanceof Attribute) {
/* 276 */       return ((Attribute)node).getNamespaceURI();
/*     */     }
/* 278 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   String getType(Object node) {
/* 283 */     if (node instanceof Attribute) {
/* 284 */       return "attribute";
/*     */     }
/* 286 */     if (node instanceof CDATA) {
/* 287 */       return "cdata";
/*     */     }
/* 289 */     if (node instanceof Comment) {
/* 290 */       return "comment";
/*     */     }
/* 292 */     if (node instanceof Document) {
/* 293 */       return "document";
/*     */     }
/* 295 */     if (node instanceof DocType) {
/* 296 */       return "documentType";
/*     */     }
/* 298 */     if (node instanceof Element) {
/* 299 */       return "element";
/*     */     }
/* 301 */     if (node instanceof EntityRef) {
/* 302 */       return "entityReference";
/*     */     }
/* 304 */     if (node instanceof Namespace) {
/* 305 */       return "namespace";
/*     */     }
/* 307 */     if (node instanceof ProcessingInstruction) {
/* 308 */       return "processingInstruction";
/*     */     }
/* 310 */     if (node instanceof Text) {
/* 311 */       return "text";
/*     */     }
/* 313 */     return "unknown";
/*     */   }
/*     */ 
/*     */   
/*     */   Navigator.XPathEx createXPathEx(String xpathString) throws TemplateModelException {
/*     */     try {
/* 319 */       return new JDOMXPathEx(xpathString);
/* 320 */     } catch (Exception e) {
/* 321 */       throw new TemplateModelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class JDOMXPathEx
/*     */     extends JDOMXPath
/*     */     implements Navigator.XPathEx
/*     */   {
/*     */     JDOMXPathEx(String path) throws Exception {
/* 332 */       super(path);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public List selectNodes(Object object, NamespaceContext namespaces) throws TemplateModelException {
/* 338 */       Context context = getContext(object);
/* 339 */       context.getContextSupport().setNamespaceContext(namespaces);
/*     */       try {
/* 341 */         return selectNodesForContext(context);
/* 342 */       } catch (Exception e) {
/* 343 */         throw new TemplateModelException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\xml\_JdomNavigator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */