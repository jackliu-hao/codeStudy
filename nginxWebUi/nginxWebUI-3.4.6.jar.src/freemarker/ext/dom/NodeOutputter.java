/*     */ package freemarker.ext.dom;
/*     */ 
/*     */ import freemarker.core.BugException;
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.DocumentType;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
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
/*     */ class NodeOutputter
/*     */ {
/*     */   private Element contextNode;
/*     */   private Environment env;
/*     */   private String defaultNS;
/*     */   private boolean hasDefaultNS;
/*     */   private boolean explicitDefaultNSPrefix;
/*  45 */   private LinkedHashMap<String, String> namespacesToPrefixLookup = new LinkedHashMap<>();
/*     */   private String namespaceDecl;
/*  47 */   int nextGeneratedPrefixNumber = 1;
/*     */   
/*     */   NodeOutputter(Node node) {
/*  50 */     if (node instanceof Element) {
/*  51 */       setContext((Element)node);
/*  52 */     } else if (node instanceof Attr) {
/*  53 */       setContext(((Attr)node).getOwnerElement());
/*  54 */     } else if (node instanceof Document) {
/*  55 */       setContext(((Document)node).getDocumentElement());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void setContext(Element contextNode) {
/*  60 */     this.contextNode = contextNode;
/*  61 */     this.env = Environment.getCurrentEnvironment();
/*  62 */     this.defaultNS = this.env.getDefaultNS();
/*  63 */     this.hasDefaultNS = (this.defaultNS != null && this.defaultNS.length() > 0);
/*  64 */     this.namespacesToPrefixLookup.put(null, "");
/*  65 */     this.namespacesToPrefixLookup.put("", "");
/*  66 */     buildPrefixLookup(contextNode);
/*  67 */     if (!this.explicitDefaultNSPrefix && this.hasDefaultNS) {
/*  68 */       this.namespacesToPrefixLookup.put(this.defaultNS, "");
/*     */     }
/*  70 */     constructNamespaceDecl();
/*     */   }
/*     */   
/*     */   private void buildPrefixLookup(Node n) {
/*  74 */     String nsURI = n.getNamespaceURI();
/*  75 */     if (nsURI != null && nsURI.length() > 0) {
/*  76 */       String prefix = this.env.getPrefixForNamespace(nsURI);
/*  77 */       if (prefix == null) {
/*  78 */         prefix = this.namespacesToPrefixLookup.get(nsURI);
/*  79 */         if (prefix == null) {
/*     */           
/*     */           do {
/*  82 */             prefix = StringUtil.toLowerABC(this.nextGeneratedPrefixNumber++);
/*  83 */           } while (this.env.getNamespaceForPrefix(prefix) != null);
/*     */         }
/*     */       } 
/*  86 */       this.namespacesToPrefixLookup.put(nsURI, prefix);
/*  87 */     } else if (this.hasDefaultNS && n.getNodeType() == 1) {
/*  88 */       this.namespacesToPrefixLookup.put(this.defaultNS, "D");
/*  89 */       this.explicitDefaultNSPrefix = true;
/*  90 */     } else if (n.getNodeType() == 2 && this.hasDefaultNS && this.defaultNS.equals(nsURI)) {
/*  91 */       this.namespacesToPrefixLookup.put(this.defaultNS, "D");
/*  92 */       this.explicitDefaultNSPrefix = true;
/*     */     } 
/*  94 */     NodeList childNodes = n.getChildNodes();
/*  95 */     for (int i = 0; i < childNodes.getLength(); i++) {
/*  96 */       buildPrefixLookup(childNodes.item(i));
/*     */     }
/*     */   }
/*     */   
/*     */   private void constructNamespaceDecl() {
/* 101 */     StringBuilder buf = new StringBuilder();
/* 102 */     if (this.explicitDefaultNSPrefix) {
/* 103 */       buf.append(" xmlns=\"");
/* 104 */       buf.append(this.defaultNS);
/* 105 */       buf.append("\"");
/*     */     } 
/* 107 */     for (Iterator<String> it = this.namespacesToPrefixLookup.keySet().iterator(); it.hasNext(); ) {
/* 108 */       String nsURI = it.next();
/* 109 */       if (nsURI == null || nsURI.length() == 0) {
/*     */         continue;
/*     */       }
/* 112 */       String prefix = this.namespacesToPrefixLookup.get(nsURI);
/* 113 */       if (prefix == null) {
/* 114 */         throw new BugException("No xmlns prefix was associated to URI: " + nsURI);
/*     */       }
/* 116 */       buf.append(" xmlns");
/* 117 */       if (prefix.length() > 0) {
/* 118 */         buf.append(":");
/* 119 */         buf.append(prefix);
/*     */       } 
/* 121 */       buf.append("=\"");
/* 122 */       buf.append(nsURI);
/* 123 */       buf.append("\"");
/*     */     } 
/* 125 */     this.namespaceDecl = buf.toString();
/*     */   }
/*     */   
/*     */   private void outputQualifiedName(Node n, StringBuilder buf) {
/* 129 */     String nsURI = n.getNamespaceURI();
/* 130 */     if (nsURI == null || nsURI.length() == 0) {
/* 131 */       buf.append(n.getNodeName());
/*     */     } else {
/* 133 */       String prefix = this.namespacesToPrefixLookup.get(nsURI);
/* 134 */       if (prefix == null) {
/*     */         
/* 136 */         buf.append(n.getNodeName());
/*     */       } else {
/* 138 */         if (prefix.length() > 0) {
/* 139 */           buf.append(prefix);
/* 140 */           buf.append(':');
/*     */         } 
/* 142 */         buf.append(n.getLocalName());
/*     */       } 
/*     */     } 
/*     */   } void outputContent(Node n, StringBuilder buf) {
/*     */     DocumentType dt;
/*     */     NodeList children;
/* 148 */     switch (n.getNodeType()) {
/*     */       case 2:
/* 150 */         if (((Attr)n).getSpecified()) {
/* 151 */           buf.append(' ');
/* 152 */           outputQualifiedName(n, buf);
/* 153 */           buf.append("=\"")
/* 154 */             .append(StringUtil.XMLEncQAttr(n.getNodeValue()))
/* 155 */             .append('"');
/*     */         } 
/*     */         break;
/*     */       
/*     */       case 8:
/* 160 */         buf.append("<!--").append(n.getNodeValue()).append("-->");
/*     */         break;
/*     */       
/*     */       case 9:
/* 164 */         outputContent(n.getChildNodes(), buf);
/*     */         break;
/*     */       
/*     */       case 10:
/* 168 */         buf.append("<!DOCTYPE ").append(n.getNodeName());
/* 169 */         dt = (DocumentType)n;
/* 170 */         if (dt.getPublicId() != null) {
/* 171 */           buf.append(" PUBLIC \"").append(dt.getPublicId()).append('"');
/*     */         }
/* 173 */         if (dt.getSystemId() != null) {
/* 174 */           buf.append(" \"").append(dt.getSystemId()).append('"');
/*     */         }
/* 176 */         if (dt.getInternalSubset() != null) {
/* 177 */           buf.append(" [").append(dt.getInternalSubset()).append(']');
/*     */         }
/* 179 */         buf.append('>');
/*     */         break;
/*     */       
/*     */       case 1:
/* 183 */         buf.append('<');
/* 184 */         outputQualifiedName(n, buf);
/* 185 */         if (n == this.contextNode) {
/* 186 */           buf.append(this.namespaceDecl);
/*     */         }
/* 188 */         outputContent(n.getAttributes(), buf);
/* 189 */         children = n.getChildNodes();
/* 190 */         if (children.getLength() == 0) {
/* 191 */           buf.append(" />"); break;
/*     */         } 
/* 193 */         buf.append('>');
/* 194 */         outputContent(n.getChildNodes(), buf);
/* 195 */         buf.append("</");
/* 196 */         outputQualifiedName(n, buf);
/* 197 */         buf.append('>');
/*     */         break;
/*     */ 
/*     */       
/*     */       case 6:
/* 202 */         outputContent(n.getChildNodes(), buf);
/*     */         break;
/*     */       
/*     */       case 5:
/* 206 */         buf.append('&').append(n.getNodeName()).append(';');
/*     */         break;
/*     */       
/*     */       case 7:
/* 210 */         buf.append("<?").append(n.getNodeName()).append(' ').append(n.getNodeValue()).append("?>");
/*     */         break;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 3:
/*     */       case 4:
/* 220 */         buf.append(StringUtil.XMLEncNQG(n.getNodeValue()));
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void outputContent(NodeList nodes, StringBuilder buf) {
/* 227 */     for (int i = 0; i < nodes.getLength(); i++) {
/* 228 */       outputContent(nodes.item(i), buf);
/*     */     }
/*     */   }
/*     */   
/*     */   void outputContent(NamedNodeMap nodes, StringBuilder buf) {
/* 233 */     for (int i = 0; i < nodes.getLength(); i++) {
/* 234 */       Node n = nodes.item(i);
/* 235 */       if (n.getNodeType() != 2 || (
/* 236 */         !n.getNodeName().startsWith("xmlns:") && !n.getNodeName().equals("xmlns"))) {
/* 237 */         outputContent(n, buf);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   String getOpeningTag(Element element) {
/* 243 */     StringBuilder buf = new StringBuilder();
/* 244 */     buf.append('<');
/* 245 */     outputQualifiedName(element, buf);
/* 246 */     buf.append(this.namespaceDecl);
/* 247 */     outputContent(element.getAttributes(), buf);
/* 248 */     buf.append('>');
/* 249 */     return buf.toString();
/*     */   }
/*     */   
/*     */   String getClosingTag(Element element) {
/* 253 */     StringBuilder buf = new StringBuilder();
/* 254 */     buf.append("</");
/* 255 */     outputQualifiedName(element, buf);
/* 256 */     buf.append('>');
/* 257 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\NodeOutputter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */