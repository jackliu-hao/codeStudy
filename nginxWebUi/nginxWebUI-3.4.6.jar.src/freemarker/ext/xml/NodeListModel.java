/*     */ package freemarker.ext.xml;
/*     */ 
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateMethodModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNodeModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ public class NodeListModel
/*     */   implements TemplateHashModel, TemplateMethodModel, TemplateScalarModel, TemplateSequenceModel, TemplateNodeModel
/*     */ {
/*  74 */   private static final Logger LOG = Logger.getLogger("freemarker.xml");
/*     */   
/*  76 */   private static final Class DOM_NODE_CLASS = getClass("org.w3c.dom.Node");
/*  77 */   private static final Class DOM4J_NODE_CLASS = getClass("org.dom4j.Node");
/*  78 */   private static final Navigator DOM_NAVIGATOR = getNavigator("Dom");
/*  79 */   private static final Navigator DOM4J_NAVIGATOR = getNavigator("Dom4j");
/*  80 */   private static final Navigator JDOM_NAVIGATOR = getNavigator("Jdom");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static volatile boolean useJaxenNamespaces = true;
/*     */ 
/*     */ 
/*     */   
/*     */   private final Navigator navigator;
/*     */ 
/*     */ 
/*     */   
/*     */   private final List nodes;
/*     */ 
/*     */ 
/*     */   
/*     */   private Namespaces namespaces;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NodeListModel(Object nodes) {
/* 103 */     Object node = nodes;
/* 104 */     if (nodes instanceof Collection) {
/* 105 */       this.nodes = new ArrayList((Collection)nodes);
/* 106 */       node = this.nodes.isEmpty() ? null : this.nodes.get(0);
/* 107 */     } else if (nodes != null) {
/* 108 */       this.nodes = Collections.singletonList(nodes);
/*     */     } else {
/* 110 */       throw new IllegalArgumentException("nodes == null");
/*     */     } 
/* 112 */     if (DOM_NODE_CLASS != null && DOM_NODE_CLASS.isInstance(node)) {
/* 113 */       this.navigator = DOM_NAVIGATOR;
/* 114 */     } else if (DOM4J_NODE_CLASS != null && DOM4J_NODE_CLASS.isInstance(node)) {
/* 115 */       this.navigator = DOM4J_NAVIGATOR;
/*     */     } else {
/*     */       
/* 118 */       this.navigator = JDOM_NAVIGATOR;
/*     */     } 
/* 120 */     this.namespaces = createNamespaces();
/*     */   }
/*     */   
/*     */   private Namespaces createNamespaces() {
/* 124 */     if (useJaxenNamespaces) {
/*     */       try {
/* 126 */         return 
/* 127 */           (Namespaces)Class.forName("freemarker.ext.xml._JaxenNamespaces")
/* 128 */           .newInstance();
/* 129 */       } catch (Throwable t) {
/* 130 */         useJaxenNamespaces = false;
/*     */       } 
/*     */     }
/* 133 */     return new Namespaces();
/*     */   }
/*     */   
/*     */   private NodeListModel(Navigator navigator, List nodes, Namespaces namespaces) {
/* 137 */     this.navigator = navigator;
/* 138 */     this.nodes = nodes;
/* 139 */     this.namespaces = namespaces;
/*     */   }
/*     */   
/*     */   private NodeListModel deriveModel(List derivedNodes) {
/* 143 */     this.namespaces.markShared();
/* 144 */     return new NodeListModel(this.navigator, derivedNodes, this.namespaces);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 153 */     return this.nodes.size();
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
/*     */   public Object exec(List<String> arguments) throws TemplateModelException {
/* 166 */     if (arguments.size() != 1) {
/* 167 */       throw new TemplateModelException("Expecting exactly one argument - an XPath expression");
/*     */     }
/*     */     
/* 170 */     return deriveModel(this.navigator.applyXPath(this.nodes, arguments.get(0), this.namespaces));
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
/*     */   public String getAsString() throws TemplateModelException {
/* 186 */     StringWriter sw = new StringWriter(size() * 128);
/* 187 */     for (Iterator iter = this.nodes.iterator(); iter.hasNext(); ) {
/* 188 */       Object o = iter.next();
/* 189 */       if (o instanceof String) {
/* 190 */         sw.write((String)o); continue;
/*     */       } 
/* 192 */       this.navigator.getAsString(o, sw);
/*     */     } 
/*     */     
/* 195 */     return sw.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel get(int index) {
/* 206 */     return (TemplateModel)deriveModel(Collections.singletonList(this.nodes.get(index)));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/* 390 */     NodeOperator op = this.navigator.getOperator(key);
/* 391 */     String localName = null;
/* 392 */     String namespaceUri = "";
/*     */     
/* 394 */     if (op == null && key.length() > 0 && key.charAt(0) == '_') {
/* 395 */       if (key.equals("_unique"))
/* 396 */         return (TemplateModel)deriveModel(removeDuplicates(this.nodes)); 
/* 397 */       if (key.equals("_filterType") || key.equals("_ftype"))
/* 398 */         return (TemplateModel)new FilterByType(); 
/* 399 */       if (key.equals("_registerNamespace") && 
/* 400 */         this.namespaces.isShared()) {
/* 401 */         this.namespaces = (Namespaces)this.namespaces.clone();
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 406 */     if (op == null) {
/* 407 */       int colon = key.indexOf(':');
/* 408 */       if (colon == -1) {
/*     */         
/* 410 */         localName = key;
/*     */       } else {
/*     */         
/* 413 */         localName = key.substring(colon + 1);
/* 414 */         String prefix = key.substring(0, colon);
/* 415 */         namespaceUri = this.namespaces.translateNamespacePrefixToUri(prefix);
/* 416 */         if (namespaceUri == null) {
/* 417 */           throw new TemplateModelException("Namespace prefix " + prefix + " is not registered.");
/*     */         }
/*     */       } 
/* 420 */       if (localName.charAt(0) == '@') {
/* 421 */         op = this.navigator.getAttributeOperator();
/* 422 */         localName = localName.substring(1);
/*     */       } else {
/* 424 */         op = this.navigator.getChildrenOperator();
/*     */       } 
/*     */     } 
/* 427 */     List result = new ArrayList();
/* 428 */     for (Iterator iter = this.nodes.iterator(); iter.hasNext();) {
/*     */       try {
/* 430 */         op.process(iter.next(), localName, namespaceUri, result);
/* 431 */       } catch (RuntimeException e) {
/* 432 */         throw new TemplateModelException(e);
/*     */       } 
/*     */     } 
/* 435 */     return (TemplateModel)deriveModel(result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 444 */     return this.nodes.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerNamespace(String prefix, String uri) {
/* 454 */     if (this.namespaces.isShared()) {
/* 455 */       this.namespaces = (Namespaces)this.namespaces.clone();
/*     */     }
/* 457 */     this.namespaces.registerNamespace(prefix, uri);
/*     */   }
/*     */   
/*     */   private class FilterByType
/*     */     implements TemplateMethodModel {
/*     */     private FilterByType() {}
/*     */     
/*     */     public Object exec(List arguments) {
/* 465 */       List<Object> filteredNodes = new ArrayList();
/* 466 */       for (Iterator iter = arguments.iterator(); iter.hasNext(); ) {
/* 467 */         Object node = iter.next();
/* 468 */         if (arguments.contains(NodeListModel.this.navigator.getType(node))) {
/* 469 */           filteredNodes.add(node);
/*     */         }
/*     */       } 
/* 472 */       return NodeListModel.this.deriveModel(filteredNodes);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final List removeDuplicates(List list) {
/* 477 */     int s = list.size();
/* 478 */     ArrayList<Object> ulist = new ArrayList(s);
/* 479 */     Set<Object> set = new HashSet(s * 4 / 3, 0.75F);
/* 480 */     Iterator it = list.iterator();
/* 481 */     while (it.hasNext()) {
/* 482 */       Object o = it.next();
/* 483 */       if (set.add(o)) {
/* 484 */         ulist.add(o);
/*     */       }
/*     */     } 
/* 487 */     return ulist;
/*     */   }
/*     */   
/*     */   private static Class getClass(String className) {
/*     */     try {
/* 492 */       return ClassUtil.forName(className);
/* 493 */     } catch (Exception e) {
/* 494 */       if (LOG.isDebugEnabled()) {
/* 495 */         LOG.debug("Couldn't load class " + className, e);
/*     */       }
/* 497 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Navigator getNavigator(String navType) {
/*     */     try {
/* 503 */       return ClassUtil.forName("freemarker.ext.xml._" + navType + "Navigator")
/* 504 */         .newInstance();
/* 505 */     } catch (Throwable t) {
/* 506 */       if (LOG.isDebugEnabled()) {
/* 507 */         LOG.debug("Could not load navigator for " + navType, t);
/*     */       }
/* 509 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateSequenceModel getChildNodes() throws TemplateModelException {
/* 515 */     return (TemplateSequenceModel)get("_content");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getNodeName() throws TemplateModelException {
/* 520 */     return getUniqueText((NodeListModel)get("_name"), "name");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getNodeNamespace() throws TemplateModelException {
/* 525 */     return getUniqueText((NodeListModel)get("_nsuri"), "namespace");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getNodeType() throws TemplateModelException {
/* 530 */     return getUniqueText((NodeListModel)get("_type"), "type");
/*     */   }
/*     */   
/*     */   public TemplateNodeModel getParentNode() throws TemplateModelException {
/* 534 */     return (TemplateNodeModel)get("_parent");
/*     */   }
/*     */   
/*     */   private String getUniqueText(NodeListModel model, String property) throws TemplateModelException {
/* 538 */     String s1 = null;
/* 539 */     Set<String> s = null;
/* 540 */     for (Iterator<String> it = model.nodes.iterator(); it.hasNext(); ) {
/* 541 */       String s2 = it.next();
/* 542 */       if (s2 != null) {
/*     */         
/* 544 */         if (s1 == null) {
/* 545 */           s1 = s2;
/*     */           
/*     */           continue;
/*     */         } 
/* 549 */         if (!s1.equals(s2)) {
/* 550 */           if (s == null) {
/* 551 */             s = new HashSet();
/* 552 */             s.add(s1);
/*     */           } 
/* 554 */           s.add(s2);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 559 */     if (s == null) {
/* 560 */       return s1;
/*     */     }
/*     */     
/* 563 */     throw new TemplateModelException("Value for node " + property + " is ambiguos: " + s);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\xml\NodeListModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */