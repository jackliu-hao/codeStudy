/*     */ package freemarker.ext.dom;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.util.Collections;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Element;
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
/*     */ class ElementModel
/*     */   extends NodeModel
/*     */   implements TemplateScalarModel
/*     */ {
/*     */   public ElementModel(Element element) {
/*  40 */     super(element);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  45 */     return false;
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
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*  61 */     if (key.equals("*")) {
/*  62 */       NodeListModel ns = new NodeListModel(this);
/*  63 */       TemplateSequenceModel children = getChildNodes();
/*  64 */       int size = children.size();
/*  65 */       for (int i = 0; i < size; i++) {
/*  66 */         NodeModel child = (NodeModel)children.get(i);
/*  67 */         if (child.node.getNodeType() == 1) {
/*  68 */           ns.add(child);
/*     */         }
/*     */       } 
/*  71 */       return (TemplateModel)ns;
/*  72 */     }  if (key.equals("**"))
/*  73 */       return (TemplateModel)new NodeListModel(((Element)this.node).getElementsByTagName("*"), this); 
/*  74 */     if (key.startsWith("@")) {
/*  75 */       if (key.startsWith("@@")) {
/*  76 */         if (key.equals(AtAtKey.ATTRIBUTES.getKey()))
/*  77 */           return (TemplateModel)new NodeListModel(this.node.getAttributes(), this); 
/*  78 */         if (key.equals(AtAtKey.START_TAG.getKey())) {
/*  79 */           NodeOutputter nodeOutputter = new NodeOutputter(this.node);
/*  80 */           return (TemplateModel)new SimpleScalar(nodeOutputter.getOpeningTag((Element)this.node));
/*  81 */         }  if (key.equals(AtAtKey.END_TAG.getKey())) {
/*  82 */           NodeOutputter nodeOutputter = new NodeOutputter(this.node);
/*  83 */           return (TemplateModel)new SimpleScalar(nodeOutputter.getClosingTag((Element)this.node));
/*  84 */         }  if (key.equals(AtAtKey.ATTRIBUTES_MARKUP.getKey())) {
/*  85 */           StringBuilder buf = new StringBuilder();
/*  86 */           NodeOutputter nu = new NodeOutputter(this.node);
/*  87 */           nu.outputContent(this.node.getAttributes(), buf);
/*  88 */           return (TemplateModel)new SimpleScalar(buf.toString().trim());
/*  89 */         }  if (key.equals(AtAtKey.PREVIOUS_SIBLING_ELEMENT.getKey())) {
/*  90 */           Node previousSibling = this.node.getPreviousSibling();
/*  91 */           while (previousSibling != null && !isSignificantNode(previousSibling)) {
/*  92 */             previousSibling = previousSibling.getPreviousSibling();
/*     */           }
/*  94 */           return (previousSibling != null && previousSibling.getNodeType() == 1) ? 
/*  95 */             (TemplateModel)wrap(previousSibling) : (TemplateModel)new NodeListModel(Collections.emptyList(), null);
/*  96 */         }  if (key.equals(AtAtKey.NEXT_SIBLING_ELEMENT.getKey())) {
/*  97 */           Node nextSibling = this.node.getNextSibling();
/*  98 */           while (nextSibling != null && !isSignificantNode(nextSibling)) {
/*  99 */             nextSibling = nextSibling.getNextSibling();
/*     */           }
/* 101 */           return (nextSibling != null && nextSibling.getNodeType() == 1) ? 
/* 102 */             (TemplateModel)wrap(nextSibling) : (TemplateModel)new NodeListModel(Collections.emptyList(), null);
/*     */         } 
/*     */         
/* 105 */         return super.get(key);
/*     */       } 
/*     */       
/* 108 */       if (DomStringUtil.isXMLNameLike(key, 1)) {
/* 109 */         Attr att = getAttribute(key.substring(1));
/* 110 */         if (att == null) {
/* 111 */           return (TemplateModel)new NodeListModel(this);
/*     */         }
/* 113 */         return (TemplateModel)wrap(att);
/* 114 */       }  if (key.equals("@*")) {
/* 115 */         return (TemplateModel)new NodeListModel(this.node.getAttributes(), this);
/*     */       }
/*     */       
/* 118 */       return super.get(key);
/*     */     } 
/*     */     
/* 121 */     if (DomStringUtil.isXMLNameLike(key)) {
/*     */       
/* 123 */       NodeListModel result = ((NodeListModel)getChildNodes()).filterByName(key);
/* 124 */       return (result.size() != 1) ? (TemplateModel)result : result.get(0);
/*     */     } 
/*     */     
/* 127 */     return super.get(key);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAsString() throws TemplateModelException {
/* 133 */     NodeList nl = this.node.getChildNodes();
/* 134 */     String result = "";
/* 135 */     for (int i = 0; i < nl.getLength(); i++) {
/* 136 */       Node child = nl.item(i);
/* 137 */       int nodeType = child.getNodeType();
/* 138 */       if (nodeType == 1) {
/*     */ 
/*     */ 
/*     */         
/* 142 */         String msg = "Only elements with no child elements can be processed as text.\nThis element with name \"" + this.node.getNodeName() + "\" has a child element named: " + child.getNodeName();
/* 143 */         throw new TemplateModelException(msg);
/* 144 */       }  if (nodeType == 3 || nodeType == 4) {
/* 145 */         result = result + child.getNodeValue();
/*     */       }
/*     */     } 
/* 148 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getNodeName() {
/* 153 */     String result = this.node.getLocalName();
/* 154 */     if (result == null || result.equals("")) {
/* 155 */       result = this.node.getNodeName();
/*     */     }
/* 157 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   String getQualifiedName() {
/* 162 */     String prefix, nodeName = getNodeName();
/* 163 */     String nsURI = getNodeNamespace();
/* 164 */     if (nsURI == null || nsURI.length() == 0) {
/* 165 */       return nodeName;
/*     */     }
/* 167 */     Environment env = Environment.getCurrentEnvironment();
/* 168 */     String defaultNS = env.getDefaultNS();
/*     */     
/* 170 */     if (defaultNS != null && defaultNS.equals(nsURI)) {
/* 171 */       prefix = "";
/*     */     } else {
/* 173 */       prefix = env.getPrefixForNamespace(nsURI);
/*     */     } 
/*     */     
/* 176 */     if (prefix == null) {
/* 177 */       return null;
/*     */     }
/* 179 */     if (prefix.length() > 0) {
/* 180 */       prefix = prefix + ":";
/*     */     }
/* 182 */     return prefix + nodeName;
/*     */   }
/*     */   
/*     */   private Attr getAttribute(String qname) {
/* 186 */     Element element = (Element)this.node;
/* 187 */     Attr result = element.getAttributeNode(qname);
/* 188 */     if (result != null)
/* 189 */       return result; 
/* 190 */     int colonIndex = qname.indexOf(':');
/* 191 */     if (colonIndex > 0) {
/* 192 */       String uri, prefix = qname.substring(0, colonIndex);
/*     */       
/* 194 */       if (prefix.equals("D")) {
/* 195 */         uri = Environment.getCurrentEnvironment().getDefaultNS();
/*     */       } else {
/* 197 */         uri = Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
/*     */       } 
/* 199 */       String localName = qname.substring(1 + colonIndex);
/* 200 */       if (uri != null) {
/* 201 */         result = element.getAttributeNodeNS(uri, localName);
/*     */       }
/*     */     } 
/* 204 */     return result;
/*     */   }
/*     */   
/*     */   private boolean isSignificantNode(Node node) throws TemplateModelException {
/* 208 */     return (node.getNodeType() == 3 || node.getNodeType() == 4) ? (
/* 209 */       !isBlankXMLText(node.getTextContent())) : ((node
/* 210 */       .getNodeType() != 7 && node.getNodeType() != 8));
/*     */   }
/*     */   
/*     */   private boolean isBlankXMLText(String s) {
/* 214 */     if (s == null) {
/* 215 */       return true;
/*     */     }
/* 217 */     for (int i = 0; i < s.length(); i++) {
/* 218 */       if (!isXMLWhiteSpace(s.charAt(i))) {
/* 219 */         return false;
/*     */       }
/*     */     } 
/* 222 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isXMLWhiteSpace(char c) {
/* 229 */     if (c != ' ' && c != '\t') { if ((((c == '\n') ? 1 : 0) | ((c == '\r') ? 1 : 0)) != 0); return false; }
/*     */   
/*     */   }
/*     */   boolean matchesName(String name, Environment env) {
/* 233 */     return DomStringUtil.matchesName(name, getNodeName(), getNodeNamespace(), env);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\ElementModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */