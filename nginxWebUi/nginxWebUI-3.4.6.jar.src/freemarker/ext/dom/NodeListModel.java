/*     */ package freemarker.ext.dom;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNodeModel;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class NodeListModel
/*     */   extends SimpleSequence
/*     */   implements TemplateHashModel, _UnexpectedTypeErrorExplainerTemplateModel
/*     */ {
/*     */   NodeModel contextNode;
/*     */   XPathSupport xpathSupport;
/*     */   
/*  60 */   private static final ObjectWrapper NODE_WRAPPER = new ObjectWrapper()
/*     */     {
/*     */       public TemplateModel wrap(Object obj) {
/*  63 */         if (obj instanceof NodeModel) {
/*  64 */           return (TemplateModel)obj;
/*     */         }
/*  66 */         return (TemplateModel)NodeModel.wrap((Node)obj);
/*     */       }
/*     */     };
/*     */   
/*     */   NodeListModel(Node contextNode) {
/*  71 */     this(NodeModel.wrap(contextNode));
/*     */   }
/*     */   
/*     */   NodeListModel(NodeModel contextNode) {
/*  75 */     super(NODE_WRAPPER);
/*  76 */     this.contextNode = contextNode;
/*     */   }
/*     */   
/*     */   NodeListModel(NodeList nodeList, NodeModel contextNode) {
/*  80 */     super(NODE_WRAPPER);
/*  81 */     for (int i = 0; i < nodeList.getLength(); i++) {
/*  82 */       this.list.add(nodeList.item(i));
/*     */     }
/*  84 */     this.contextNode = contextNode;
/*     */   }
/*     */   
/*     */   NodeListModel(NamedNodeMap nodeList, NodeModel contextNode) {
/*  88 */     super(NODE_WRAPPER);
/*  89 */     for (int i = 0; i < nodeList.getLength(); i++) {
/*  90 */       this.list.add(nodeList.item(i));
/*     */     }
/*  92 */     this.contextNode = contextNode;
/*     */   }
/*     */   
/*     */   NodeListModel(List list, NodeModel contextNode) {
/*  96 */     super(list, NODE_WRAPPER);
/*  97 */     this.contextNode = contextNode;
/*     */   }
/*     */   
/*     */   NodeListModel filterByName(String name) throws TemplateModelException {
/* 101 */     NodeListModel result = new NodeListModel(this.contextNode);
/* 102 */     int size = size();
/* 103 */     if (size == 0) {
/* 104 */       return result;
/*     */     }
/* 106 */     Environment env = Environment.getCurrentEnvironment();
/* 107 */     for (int i = 0; i < size; i++) {
/* 108 */       NodeModel nm = (NodeModel)get(i);
/* 109 */       if (nm instanceof ElementModel && (
/* 110 */         (ElementModel)nm).matchesName(name, env)) {
/* 111 */         result.add(nm);
/*     */       }
/*     */     } 
/*     */     
/* 115 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 120 */     return (size() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/* 125 */     int size = size();
/* 126 */     if (size == 1) {
/* 127 */       NodeModel nm = (NodeModel)get(0);
/* 128 */       return nm.get(key);
/*     */     } 
/* 130 */     if (key.startsWith("@@")) {
/* 131 */       if (key.equals(AtAtKey.MARKUP.getKey()) || key
/* 132 */         .equals(AtAtKey.NESTED_MARKUP.getKey()) || key
/* 133 */         .equals(AtAtKey.TEXT.getKey())) {
/* 134 */         StringBuilder result = new StringBuilder();
/* 135 */         for (int i = 0; i < size; i++) {
/* 136 */           NodeModel nm = (NodeModel)get(i);
/* 137 */           TemplateScalarModel textModel = (TemplateScalarModel)nm.get(key);
/* 138 */           result.append(textModel.getAsString());
/*     */         } 
/* 140 */         return (TemplateModel)new SimpleScalar(result.toString());
/* 141 */       }  if (key.length() != 2) {
/*     */         
/* 143 */         if (AtAtKey.containsKey(key)) {
/* 144 */           throw new TemplateModelException("\"" + key + "\" is only applicable to a single XML node, but it was applied on " + ((size != 0) ? (size + " XML nodes (multiple matches).") : "an empty list of XML nodes (no matches)."));
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 150 */         throw new TemplateModelException("Unsupported @@ key: " + key);
/*     */       } 
/*     */     } 
/*     */     
/* 154 */     if (DomStringUtil.isXMLNameLike(key) || (key
/* 155 */       .startsWith("@") && (
/* 156 */       DomStringUtil.isXMLNameLike(key, 1) || key.equals("@@") || key.equals("@*"))) || key
/* 157 */       .equals("*") || key.equals("**")) {
/* 158 */       NodeListModel result = new NodeListModel(this.contextNode);
/* 159 */       for (int i = 0; i < size; i++) {
/* 160 */         NodeModel nm = (NodeModel)get(i);
/* 161 */         if (nm instanceof ElementModel) {
/* 162 */           TemplateSequenceModel tsm = (TemplateSequenceModel)nm.get(key);
/* 163 */           if (tsm != null) {
/* 164 */             int tsmSize = tsm.size();
/* 165 */             for (int j = 0; j < tsmSize; j++) {
/* 166 */               result.add(tsm.get(j));
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/* 171 */       if (result.size() == 1) {
/* 172 */         return result.get(0);
/*     */       }
/* 174 */       return (TemplateModel)result;
/*     */     } 
/* 176 */     XPathSupport xps = getXPathSupport();
/* 177 */     if (xps == null) {
/* 178 */       throw new TemplateModelException("No XPath support is available (add Apache Xalan or Jaxen as dependency). This is either malformed, or an XPath expression: " + key);
/*     */     }
/*     */ 
/*     */     
/* 182 */     Object context = (size == 0) ? null : rawNodeList();
/* 183 */     return xps.executeQuery(context, key);
/*     */   }
/*     */   
/*     */   private List rawNodeList() throws TemplateModelException {
/* 187 */     int size = size();
/* 188 */     ArrayList<Node> al = new ArrayList(size);
/* 189 */     for (int i = 0; i < size; i++) {
/* 190 */       al.add(((NodeModel)get(i)).node);
/*     */     }
/* 192 */     return al;
/*     */   }
/*     */   
/*     */   XPathSupport getXPathSupport() throws TemplateModelException {
/* 196 */     if (this.xpathSupport == null) {
/* 197 */       if (this.contextNode != null) {
/* 198 */         this.xpathSupport = this.contextNode.getXPathSupport();
/* 199 */       } else if (size() > 0) {
/* 200 */         this.xpathSupport = ((NodeModel)get(0)).getXPathSupport();
/*     */       } 
/*     */     }
/* 203 */     return this.xpathSupport;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object[] explainTypeError(Class[] expectedClasses) {
/* 208 */     for (int i = 0; i < expectedClasses.length; i++) {
/* 209 */       Class<?> expectedClass = expectedClasses[i];
/* 210 */       if (TemplateScalarModel.class.isAssignableFrom(expectedClass) || TemplateDateModel.class
/* 211 */         .isAssignableFrom(expectedClass) || TemplateNumberModel.class
/* 212 */         .isAssignableFrom(expectedClass) || TemplateBooleanModel.class
/* 213 */         .isAssignableFrom(expectedClass))
/* 214 */         return newTypeErrorExplanation("string"); 
/* 215 */       if (TemplateNodeModel.class.isAssignableFrom(expectedClass)) {
/* 216 */         return newTypeErrorExplanation("node");
/*     */       }
/*     */     } 
/* 219 */     return null;
/*     */   }
/*     */   
/*     */   private Object[] newTypeErrorExplanation(String type) {
/* 223 */     int size = size();
/* 224 */     return new Object[] { "This XML query result can't be used as ", type, " because for that it had to contain exactly 1 XML node, but it contains ", 
/*     */         
/* 226 */         Integer.valueOf(size), " nodes. That is, the constructing XML query has found ", (size == 0) ? "no matches." : "multiple matches." };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\NodeListModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */