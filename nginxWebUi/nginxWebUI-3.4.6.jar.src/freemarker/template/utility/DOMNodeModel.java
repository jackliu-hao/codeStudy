/*     */ package freemarker.template.utility;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleHash;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateMethodModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.Text;
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
/*     */ @Deprecated
/*     */ public class DOMNodeModel
/*     */   implements TemplateHashModel
/*     */ {
/*  51 */   private static HashMap equivalenceTable = new HashMap<>();
/*     */   static {
/*  53 */     equivalenceTable.put("*", "children");
/*  54 */     equivalenceTable.put("@*", "attributes");
/*     */   }
/*     */   
/*     */   private Node node;
/*  58 */   private HashMap cache = new HashMap<>();
/*     */   
/*     */   public DOMNodeModel(Node node) {
/*  61 */     this.node = node;
/*     */   }
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*     */     SimpleScalar simpleScalar;
/*  66 */     TemplateModel result = null;
/*  67 */     if (equivalenceTable.containsKey(key)) {
/*  68 */       key = (String)equivalenceTable.get(key);
/*     */     }
/*  70 */     if (this.cache.containsKey(key)) {
/*  71 */       result = (TemplateModel)this.cache.get(key);
/*     */     }
/*  73 */     if (result == null) {
/*  74 */       if ("attributes".equals(key)) {
/*  75 */         NamedNodeMap attributes = this.node.getAttributes();
/*  76 */         if (attributes != null) {
/*  77 */           SimpleHash hash = new SimpleHash((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*  78 */           for (int i = 0; i < attributes.getLength(); i++) {
/*  79 */             Attr att = (Attr)attributes.item(i);
/*  80 */             hash.put(att.getName(), att.getValue());
/*     */           } 
/*  82 */           SimpleHash simpleHash1 = hash;
/*     */         } 
/*  84 */       } else if (key.charAt(0) == '@') {
/*  85 */         if (this.node instanceof Element) {
/*  86 */           String attValue = ((Element)this.node).getAttribute(key.substring(1));
/*  87 */           simpleScalar = new SimpleScalar(attValue);
/*     */         } else {
/*  89 */           throw new TemplateModelException("Trying to get an attribute value for a non-element node");
/*     */         } 
/*  91 */       } else if ("is_element".equals(key)) {
/*  92 */         TemplateBooleanModel templateBooleanModel = (this.node instanceof Element) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
/*     */       }
/*  94 */       else if ("is_text".equals(key)) {
/*  95 */         TemplateBooleanModel templateBooleanModel = (this.node instanceof Text) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
/*     */       }
/*  97 */       else if ("name".equals(key)) {
/*  98 */         simpleScalar = new SimpleScalar(this.node.getNodeName());
/*  99 */       } else if ("children".equals(key)) {
/* 100 */         NodeListTM nodeListTM = new NodeListTM(this.node.getChildNodes());
/* 101 */       } else if ("parent".equals(key)) {
/* 102 */         Node parent = this.node.getParentNode();
/* 103 */         DOMNodeModel dOMNodeModel = (parent == null) ? null : new DOMNodeModel(parent);
/* 104 */       } else if ("ancestorByName".equals(key)) {
/* 105 */         AncestorByName ancestorByName = new AncestorByName();
/* 106 */       } else if ("nextSibling".equals(key)) {
/* 107 */         Node next = this.node.getNextSibling();
/* 108 */         DOMNodeModel dOMNodeModel = (next == null) ? null : new DOMNodeModel(next);
/* 109 */       } else if ("previousSibling".equals(key)) {
/* 110 */         Node previous = this.node.getPreviousSibling();
/* 111 */         DOMNodeModel dOMNodeModel = (previous == null) ? null : new DOMNodeModel(previous);
/* 112 */       } else if ("nextSiblingElement".equals(key)) {
/* 113 */         Node next = nextSiblingElement(this.node);
/* 114 */         DOMNodeModel dOMNodeModel = (next == null) ? null : new DOMNodeModel(next);
/* 115 */       } else if ("previousSiblingElement".equals(key)) {
/* 116 */         Node previous = previousSiblingElement(this.node);
/* 117 */         DOMNodeModel dOMNodeModel = (previous == null) ? null : new DOMNodeModel(previous);
/* 118 */       } else if ("nextElement".equals(key)) {
/* 119 */         Node next = nextElement(this.node);
/* 120 */         DOMNodeModel dOMNodeModel = (next == null) ? null : new DOMNodeModel(next);
/* 121 */       } else if ("previousElement".equals(key)) {
/* 122 */         Node previous = previousElement(this.node);
/* 123 */         DOMNodeModel dOMNodeModel = (previous == null) ? null : new DOMNodeModel(previous);
/* 124 */       } else if ("text".equals(key)) {
/* 125 */         simpleScalar = new SimpleScalar(getText(this.node));
/*     */       } 
/* 127 */       this.cache.put(key, simpleScalar);
/*     */     } 
/* 129 */     return (TemplateModel)simpleScalar;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 134 */     return false;
/*     */   }
/*     */   
/*     */   private static String getText(Node node) {
/* 138 */     String result = "";
/* 139 */     if (node instanceof Text) {
/* 140 */       result = ((Text)node).getData();
/* 141 */     } else if (node instanceof Element) {
/* 142 */       NodeList children = node.getChildNodes();
/* 143 */       for (int i = 0; i < children.getLength(); i++) {
/* 144 */         result = result + getText(children.item(i));
/*     */       }
/*     */     } 
/* 147 */     return result;
/*     */   }
/*     */   
/*     */   private static Element nextSiblingElement(Node node) {
/* 151 */     Node next = node;
/* 152 */     while (next != null) {
/* 153 */       next = next.getNextSibling();
/* 154 */       if (next instanceof Element) {
/* 155 */         return (Element)next;
/*     */       }
/*     */     } 
/* 158 */     return null;
/*     */   }
/*     */   
/*     */   private static Element previousSiblingElement(Node node) {
/* 162 */     Node previous = node;
/* 163 */     while (previous != null) {
/* 164 */       previous = previous.getPreviousSibling();
/* 165 */       if (previous instanceof Element) {
/* 166 */         return (Element)previous;
/*     */       }
/*     */     } 
/* 169 */     return null;
/*     */   }
/*     */   
/*     */   private static Element nextElement(Node node) {
/* 173 */     if (node.hasChildNodes()) {
/* 174 */       NodeList children = node.getChildNodes();
/* 175 */       for (int i = 0; i < children.getLength(); i++) {
/* 176 */         Node child = children.item(i);
/* 177 */         if (child instanceof Element) {
/* 178 */           return (Element)child;
/*     */         }
/*     */       } 
/*     */     } 
/* 182 */     Element nextSiblingElement = nextSiblingElement(node);
/* 183 */     if (nextSiblingElement != null) {
/* 184 */       return nextSiblingElement;
/*     */     }
/* 186 */     Node parent = node.getParentNode();
/* 187 */     while (parent instanceof Element) {
/* 188 */       Element next = nextSiblingElement(parent);
/* 189 */       if (next != null) {
/* 190 */         return next;
/*     */       }
/* 192 */       parent = parent.getParentNode();
/*     */     } 
/* 194 */     return null;
/*     */   }
/*     */   
/*     */   private static Element previousElement(Node node) {
/* 198 */     Element result = previousSiblingElement(node);
/* 199 */     if (result != null) {
/* 200 */       return result;
/*     */     }
/* 202 */     Node parent = node.getParentNode();
/* 203 */     if (parent instanceof Element) {
/* 204 */       return (Element)parent;
/*     */     }
/* 206 */     return null;
/*     */   }
/*     */   
/*     */   void setParent(DOMNodeModel parent) {
/* 210 */     if (parent != null) {
/* 211 */       this.cache.put("parent", parent);
/*     */     }
/*     */   }
/*     */   
/*     */   String getNodeName() {
/* 216 */     return this.node.getNodeName();
/*     */   }
/*     */   
/*     */   class AncestorByName
/*     */     implements TemplateMethodModel
/*     */   {
/*     */     public Object exec(List<String> arguments) throws TemplateModelException {
/* 223 */       if (arguments.size() != 1) {
/* 224 */         throw new TemplateModelException("Expecting exactly one string argument here");
/*     */       }
/* 226 */       String nodeName = arguments.get(0);
/* 227 */       DOMNodeModel ancestor = (DOMNodeModel)DOMNodeModel.this.get("parent");
/* 228 */       while (ancestor != null) {
/* 229 */         if (nodeName.equals(ancestor.getNodeName())) {
/* 230 */           return ancestor;
/*     */         }
/* 232 */         ancestor = (DOMNodeModel)ancestor.get("parent");
/*     */       } 
/* 234 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   class NodeListTM
/*     */     implements TemplateSequenceModel, TemplateMethodModel
/*     */   {
/*     */     private NodeList nodeList;
/*     */     private TemplateModel[] nodes;
/*     */     
/*     */     NodeListTM(NodeList nodeList) {
/* 245 */       this.nodeList = nodeList;
/* 246 */       this.nodes = new TemplateModel[nodeList.getLength()];
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) {
/* 251 */       DOMNodeModel result = (DOMNodeModel)this.nodes[index];
/* 252 */       if (result == null) {
/* 253 */         result = new DOMNodeModel(this.nodeList.item(index));
/* 254 */         this.nodes[index] = (TemplateModel)result;
/* 255 */         result.setParent(DOMNodeModel.this);
/*     */       } 
/* 257 */       return (TemplateModel)result;
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 262 */       return this.nodes.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object exec(List<String> arguments) throws TemplateModelException {
/* 267 */       if (arguments.size() != 1) {
/* 268 */         throw new TemplateModelException("Expecting exactly one string argument here");
/*     */       }
/* 270 */       if (!(DOMNodeModel.this.node instanceof Element)) {
/* 271 */         throw new TemplateModelException("Expecting element here.");
/*     */       }
/* 273 */       Element elem = (Element)DOMNodeModel.this.node;
/* 274 */       return new NodeListTM(elem.getElementsByTagName(arguments.get(0)));
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\DOMNodeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */