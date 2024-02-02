/*      */ package freemarker.ext.jdom;
/*      */ 
/*      */ import freemarker.template.ObjectWrapper;
/*      */ import freemarker.template.SimpleHash;
/*      */ import freemarker.template.SimpleScalar;
/*      */ import freemarker.template.Template;
/*      */ import freemarker.template.TemplateCollectionModel;
/*      */ import freemarker.template.TemplateHashModel;
/*      */ import freemarker.template.TemplateMethodModel;
/*      */ import freemarker.template.TemplateModel;
/*      */ import freemarker.template.TemplateModelException;
/*      */ import freemarker.template.TemplateModelIterator;
/*      */ import freemarker.template.TemplateScalarModel;
/*      */ import freemarker.template.TemplateSequenceModel;
/*      */ import freemarker.template._TemplateAPI;
/*      */ import java.io.FileReader;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.io.Writer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.WeakHashMap;
/*      */ import org.jaxen.Context;
/*      */ import org.jaxen.JaxenException;
/*      */ import org.jaxen.NamespaceContext;
/*      */ import org.jaxen.jdom.JDOMXPath;
/*      */ import org.jdom.Attribute;
/*      */ import org.jdom.CDATA;
/*      */ import org.jdom.Comment;
/*      */ import org.jdom.DocType;
/*      */ import org.jdom.Document;
/*      */ import org.jdom.Element;
/*      */ import org.jdom.EntityRef;
/*      */ import org.jdom.Namespace;
/*      */ import org.jdom.ProcessingInstruction;
/*      */ import org.jdom.Text;
/*      */ import org.jdom.input.SAXBuilder;
/*      */ import org.jdom.output.XMLOutputter;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @Deprecated
/*      */ public class NodeListModel
/*      */   implements TemplateHashModel, TemplateMethodModel, TemplateCollectionModel, TemplateSequenceModel, TemplateScalarModel
/*      */ {
/*   96 */   private static final AttributeXMLOutputter OUTPUT = new AttributeXMLOutputter();
/*      */   
/*   98 */   private static final NodeListModel EMPTY = new NodeListModel(null, false);
/*      */ 
/*      */   
/*  101 */   private static final Map XPATH_CACHE = new WeakHashMap<>();
/*      */   
/*  103 */   private static final NamedNodeOperator NAMED_CHILDREN_OP = new NamedChildrenOp();
/*  104 */   private static final NamedNodeOperator NAMED_ATTRIBUTE_OP = new NamedAttributeOp();
/*  105 */   private static final NodeOperator ALL_ATTRIBUTES_OP = new AllAttributesOp();
/*  106 */   private static final NodeOperator ALL_CHILDREN_OP = new AllChildrenOp();
/*  107 */   private static final Map OPERATIONS = createOperations();
/*  108 */   private static final Map SPECIAL_OPERATIONS = createSpecialOperations();
/*      */   
/*      */   private static final int SPECIAL_OPERATION_COPY = 0;
/*      */   
/*      */   private static final int SPECIAL_OPERATION_UNIQUE = 1;
/*      */   
/*      */   private static final int SPECIAL_OPERATION_FILTER_NAME = 2;
/*      */   
/*      */   private static final int SPECIAL_OPERATION_FILTER_TYPE = 3;
/*      */   
/*      */   private static final int SPECIAL_OPERATION_QUERY_TYPE = 4;
/*      */   private static final int SPECIAL_OPERATION_REGISTER_NAMESPACE = 5;
/*      */   private static final int SPECIAL_OPERATION_PLAINTEXT = 6;
/*      */   private final List nodes;
/*      */   private final Map namespaces;
/*      */   
/*      */   public NodeListModel(Document document) {
/*  125 */     this.nodes = (document == null) ? Collections.EMPTY_LIST : Collections.<Document>singletonList(document);
/*  126 */     this.namespaces = new HashMap<>();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NodeListModel(Element element) {
/*  133 */     this.nodes = (element == null) ? Collections.EMPTY_LIST : Collections.<Element>singletonList(element);
/*  134 */     this.namespaces = new HashMap<>();
/*      */   }
/*      */   
/*      */   private NodeListModel(Object object, Map namespaces) {
/*  138 */     this.nodes = (object == null) ? Collections.EMPTY_LIST : Collections.<Object>singletonList(object);
/*  139 */     this.namespaces = namespaces;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NodeListModel(List nodes) {
/*  149 */     this(nodes, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NodeListModel(List<?> nodes, boolean copy) {
/*  161 */     this.nodes = (copy && nodes != null) ? new ArrayList(nodes) : ((nodes == null) ? Collections.EMPTY_LIST : nodes);
/*  162 */     this.namespaces = new HashMap<>();
/*      */   }
/*      */   
/*      */   private NodeListModel(List nodes, Map namespaces) {
/*  166 */     this.nodes = (nodes == null) ? Collections.EMPTY_LIST : nodes;
/*  167 */     this.namespaces = namespaces;
/*      */   }
/*      */   
/*      */   private static final NodeListModel createNodeListModel(List list, Map namespaces) {
/*  171 */     if (list == null || list.isEmpty()) {
/*  172 */       if (namespaces.isEmpty()) {
/*  173 */         return EMPTY;
/*      */       }
/*  175 */       return new NodeListModel(Collections.EMPTY_LIST, namespaces);
/*      */     } 
/*      */     
/*  178 */     if (list.size() == 1) return new NodeListModel(list.get(0), namespaces); 
/*  179 */     return new NodeListModel(list, namespaces);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEmpty() {
/*  187 */     return this.nodes.isEmpty();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getAsString() throws TemplateModelException {
/*  200 */     if (isEmpty()) {
/*  201 */       return "";
/*      */     }
/*  203 */     StringWriter sw = new StringWriter(this.nodes.size() * 128);
/*      */     try {
/*  205 */       for (Iterator i = this.nodes.iterator(); i.hasNext(); ) {
/*  206 */         Object node = i.next();
/*  207 */         if (node instanceof Element) {
/*  208 */           OUTPUT.output((Element)node, sw); continue;
/*  209 */         }  if (node instanceof Attribute) {
/*  210 */           OUTPUT.output((Attribute)node, sw); continue;
/*  211 */         }  if (node instanceof String) {
/*  212 */           sw.write(OUTPUT.escapeElementEntities(node.toString())); continue;
/*  213 */         }  if (node instanceof Text) {
/*  214 */           OUTPUT.output((Text)node, sw); continue;
/*  215 */         }  if (node instanceof Document) {
/*  216 */           OUTPUT.output((Document)node, sw); continue;
/*  217 */         }  if (node instanceof ProcessingInstruction) {
/*  218 */           OUTPUT.output((ProcessingInstruction)node, sw); continue;
/*  219 */         }  if (node instanceof Comment) {
/*  220 */           OUTPUT.output((Comment)node, sw); continue;
/*  221 */         }  if (node instanceof CDATA) {
/*  222 */           OUTPUT.output((CDATA)node, sw); continue;
/*  223 */         }  if (node instanceof DocType) {
/*  224 */           OUTPUT.output((DocType)node, sw); continue;
/*  225 */         }  if (node instanceof EntityRef) {
/*  226 */           OUTPUT.output((EntityRef)node, sw); continue;
/*      */         } 
/*  228 */         throw new TemplateModelException(node.getClass().getName() + " is not a core JDOM class");
/*      */       } 
/*  230 */     } catch (IOException e) {
/*  231 */       throw new TemplateModelException(e.getMessage());
/*      */     } 
/*  233 */     return sw.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateModel get(String key) throws TemplateModelException {
/*  355 */     if (isEmpty()) {
/*  356 */       return (TemplateModel)EMPTY;
/*      */     }
/*  358 */     if (key == null || key.length() == 0) {
/*  359 */       throw new TemplateModelException("Invalid key [" + key + "]");
/*      */     }
/*  361 */     NodeOperator op = null;
/*  362 */     NamedNodeOperator nop = null;
/*  363 */     String name = null;
/*      */     
/*  365 */     switch (key.charAt(0)) {
/*      */       
/*      */       case '@':
/*  368 */         if (key.length() != 2 || key.charAt(1) != '*') {
/*      */           
/*  370 */           nop = NAMED_ATTRIBUTE_OP;
/*  371 */           name = key.substring(1);
/*      */           break;
/*      */         } 
/*  374 */         op = ALL_ATTRIBUTES_OP;
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case '*':
/*  380 */         if (key.length() == 1) {
/*  381 */           op = ALL_CHILDREN_OP;
/*      */           break;
/*      */         } 
/*  384 */         throw new TemplateModelException("Invalid key [" + key + "]");
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case '_':
/*      */       case 'x':
/*  391 */         op = (NodeOperator)OPERATIONS.get(key);
/*  392 */         if (op == null) {
/*      */           
/*  394 */           Integer specop = (Integer)SPECIAL_OPERATIONS.get(key);
/*  395 */           if (specop != null) {
/*  396 */             switch (specop.intValue()) {
/*      */               
/*      */               case 0:
/*  399 */                 synchronized (this.namespaces) {
/*  400 */                   return (TemplateModel)new NodeListModel(this.nodes, (Map)((HashMap)this.namespaces).clone());
/*      */                 } 
/*      */               
/*      */               case 1:
/*  404 */                 return (TemplateModel)new NodeListModel(removeDuplicates(this.nodes), this.namespaces);
/*      */               case 2:
/*  406 */                 return (TemplateModel)new NameFilter();
/*      */               case 3:
/*  408 */                 return (TemplateModel)new TypeFilter();
/*      */               case 4:
/*  410 */                 return getType();
/*      */               case 5:
/*  412 */                 return (TemplateModel)new RegisterNamespace();
/*      */               case 6:
/*  414 */                 return (TemplateModel)getPlainText();
/*      */             } 
/*      */           
/*      */           }
/*      */         } 
/*      */         break;
/*      */     } 
/*      */     
/*  422 */     if (op == null && nop == null) {
/*  423 */       nop = NAMED_CHILDREN_OP;
/*  424 */       name = key;
/*      */     } 
/*      */     
/*  427 */     List list = null;
/*  428 */     if (op != null) {
/*  429 */       list = evaluateElementOperation(op, this.nodes);
/*      */     } else {
/*  431 */       String localName = name;
/*  432 */       Namespace namespace = Namespace.NO_NAMESPACE;
/*  433 */       int colon = name.indexOf(':');
/*  434 */       if (colon != -1) {
/*  435 */         localName = name.substring(colon + 1);
/*  436 */         String nsPrefix = name.substring(0, colon);
/*  437 */         synchronized (this.namespaces) {
/*  438 */           namespace = (Namespace)this.namespaces.get(nsPrefix);
/*      */         } 
/*  440 */         if (namespace == null) {
/*  441 */           if (nsPrefix.equals("xml")) {
/*  442 */             namespace = Namespace.XML_NAMESPACE;
/*      */           } else {
/*  444 */             throw new TemplateModelException("Unregistered namespace prefix '" + nsPrefix + "'");
/*      */           } 
/*      */         }
/*      */       } 
/*  448 */       list = evaluateNamedElementOperation(nop, localName, namespace, this.nodes);
/*      */     } 
/*  450 */     return (TemplateModel)createNodeListModel(list, this.namespaces);
/*      */   }
/*      */   private TemplateModel getType() {
/*      */     char code;
/*  454 */     if (this.nodes.size() == 0)
/*  455 */       return (TemplateModel)new SimpleScalar(""); 
/*  456 */     Object firstNode = this.nodes.get(0);
/*      */     
/*  458 */     if (firstNode instanceof Element) {
/*  459 */       code = 'e';
/*  460 */     } else if (firstNode instanceof Text || firstNode instanceof String) {
/*  461 */       code = 'x';
/*  462 */     } else if (firstNode instanceof Attribute) {
/*  463 */       code = 'a';
/*  464 */     } else if (firstNode instanceof EntityRef) {
/*  465 */       code = 'n';
/*  466 */     } else if (firstNode instanceof Document) {
/*  467 */       code = 'd';
/*  468 */     } else if (firstNode instanceof DocType) {
/*  469 */       code = 't';
/*  470 */     } else if (firstNode instanceof Comment) {
/*  471 */       code = 'c';
/*  472 */     } else if (firstNode instanceof ProcessingInstruction) {
/*  473 */       code = 'p';
/*      */     } else {
/*  475 */       code = '?';
/*  476 */     }  return (TemplateModel)new SimpleScalar(new String(new char[] { code }));
/*      */   }
/*      */ 
/*      */   
/*      */   private SimpleScalar getPlainText() throws TemplateModelException {
/*  481 */     List list = evaluateElementOperation((TextOp)OPERATIONS.get("_text"), this.nodes);
/*  482 */     StringBuilder buf = new StringBuilder();
/*  483 */     for (Iterator it = list.iterator(); it.hasNext();) {
/*  484 */       buf.append(it.next());
/*      */     }
/*  486 */     return new SimpleScalar(buf.toString());
/*      */   }
/*      */ 
/*      */   
/*      */   public TemplateModelIterator iterator() {
/*  491 */     return new TemplateModelIterator()
/*      */       {
/*  493 */         private final Iterator it = NodeListModel.this.nodes.iterator();
/*      */ 
/*      */         
/*      */         public TemplateModel next() {
/*  497 */           return this.it.hasNext() ? (TemplateModel)new NodeListModel(this.it.next(), NodeListModel.this.namespaces) : null;
/*      */         }
/*      */ 
/*      */         
/*      */         public boolean hasNext() {
/*  502 */           return this.it.hasNext();
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateModel get(int i) throws TemplateModelException {
/*      */     try {
/*  514 */       return (TemplateModel)new NodeListModel(this.nodes.get(i), this.namespaces);
/*  515 */     } catch (IndexOutOfBoundsException e) {
/*  516 */       throw new TemplateModelException("Index out of bounds: " + e.getMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public int size() {
/*  522 */     return this.nodes.size();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object exec(List<String> arguments) throws TemplateModelException {
/*  545 */     if (arguments == null || arguments.size() != 1) {
/*  546 */       throw new TemplateModelException("Exactly one argument required for execute() on NodeTemplate");
/*      */     }
/*  548 */     String xpathString = arguments.get(0);
/*  549 */     JDOMXPathEx xpath = null;
/*      */     try {
/*  551 */       synchronized (XPATH_CACHE) {
/*  552 */         xpath = (JDOMXPathEx)XPATH_CACHE.get(xpathString);
/*  553 */         if (xpath == null) {
/*  554 */           xpath = new JDOMXPathEx(xpathString);
/*  555 */           XPATH_CACHE.put(xpathString, xpath);
/*      */         } 
/*      */       } 
/*  558 */       return createNodeListModel(xpath.selectNodes(this.nodes, this.namespaces), this.namespaces);
/*  559 */     } catch (Exception e) {
/*  560 */       throw new TemplateModelException("Could not evaulate XPath expression " + xpathString, e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerNamespace(String prefix, String uri) {
/*  590 */     synchronized (this.namespaces) {
/*  591 */       this.namespaces.put(prefix, Namespace.getNamespace(prefix, uri));
/*      */     } 
/*      */   }
/*      */   
/*      */   private static interface NodeOperator {
/*      */     List operate(Object param1Object) throws TemplateModelException;
/*      */   }
/*      */   
/*      */   private static interface NamedNodeOperator {
/*      */     List operate(Object param1Object, String param1String, Namespace param1Namespace) throws TemplateModelException;
/*      */   }
/*      */   
/*      */   private static final class AllChildrenOp
/*      */     implements NodeOperator {
/*      */     private AllChildrenOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  608 */       if (node instanceof Element)
/*  609 */         return ((Element)node).getChildren(); 
/*  610 */       if (node instanceof Document) {
/*  611 */         Element root = ((Document)node).getRootElement();
/*  612 */         return (root == null) ? Collections.EMPTY_LIST : Collections.<Element>singletonList(root);
/*      */       } 
/*      */ 
/*      */       
/*  616 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static final class NamedChildrenOp
/*      */     implements NamedNodeOperator
/*      */   {
/*      */     private NamedChildrenOp() {}
/*      */     
/*      */     public List operate(Object node, String localName, Namespace namespace) {
/*  627 */       if (node instanceof Element)
/*  628 */         return ((Element)node).getChildren(localName, namespace); 
/*  629 */       if (node instanceof Document) {
/*  630 */         Element root = ((Document)node).getRootElement();
/*  631 */         if (root != null && root
/*  632 */           .getName().equals(localName) && root
/*  633 */           .getNamespaceURI().equals(namespace.getURI())) {
/*  634 */           return Collections.singletonList(root);
/*      */         }
/*  636 */         return Collections.EMPTY_LIST;
/*      */       } 
/*      */ 
/*      */       
/*  640 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class AllAttributesOp
/*      */     implements NodeOperator
/*      */   {
/*      */     private AllAttributesOp() {}
/*      */ 
/*      */     
/*      */     public List operate(Object node) {
/*  653 */       if (!(node instanceof Element)) {
/*  654 */         return null;
/*      */       }
/*  656 */       return ((Element)node).getAttributes();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static final class NamedAttributeOp
/*      */     implements NamedNodeOperator
/*      */   {
/*      */     private NamedAttributeOp() {}
/*      */     
/*      */     public List operate(Object node, String localName, Namespace namespace) {
/*  667 */       Attribute attr = null;
/*  668 */       if (node instanceof Element) {
/*  669 */         Element element = (Element)node;
/*  670 */         attr = element.getAttribute(localName, namespace);
/*  671 */       } else if (node instanceof ProcessingInstruction) {
/*  672 */         ProcessingInstruction pi = (ProcessingInstruction)node;
/*  673 */         if ("target".equals(localName))
/*  674 */         { attr = new Attribute("target", pi.getTarget()); }
/*  675 */         else if ("data".equals(localName))
/*  676 */         { attr = new Attribute("data", pi.getData()); }
/*      */         else
/*  678 */         { attr = new Attribute(localName, pi.getValue(localName)); } 
/*  679 */       } else if (node instanceof DocType) {
/*  680 */         DocType doctype = (DocType)node;
/*  681 */         if ("publicId".equals(localName)) {
/*  682 */           attr = new Attribute("publicId", doctype.getPublicID());
/*  683 */         } else if ("systemId".equals(localName)) {
/*  684 */           attr = new Attribute("systemId", doctype.getSystemID());
/*  685 */         } else if ("elementName".equals(localName)) {
/*  686 */           attr = new Attribute("elementName", doctype.getElementName());
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/*  691 */         return null;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  697 */       return (attr == null) ? Collections.EMPTY_LIST : Collections.<Attribute>singletonList(attr);
/*      */     } }
/*      */   
/*      */   private static final class NameOp implements NodeOperator {
/*      */     private NameOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  704 */       if (node instanceof Element)
/*  705 */         return Collections.singletonList(((Element)node).getName()); 
/*  706 */       if (node instanceof Attribute)
/*  707 */         return Collections.singletonList(((Attribute)node).getName()); 
/*  708 */       if (node instanceof EntityRef)
/*  709 */         return Collections.singletonList(((EntityRef)node).getName()); 
/*  710 */       if (node instanceof ProcessingInstruction)
/*  711 */         return Collections.singletonList(((ProcessingInstruction)node).getTarget()); 
/*  712 */       if (node instanceof DocType) {
/*  713 */         return Collections.singletonList(((DocType)node).getPublicID());
/*      */       }
/*  715 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class QNameOp
/*      */     implements NodeOperator
/*      */   {
/*      */     private QNameOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  725 */       if (node instanceof Element)
/*  726 */         return Collections.singletonList(((Element)node).getQualifiedName()); 
/*  727 */       if (node instanceof Attribute) {
/*  728 */         return Collections.singletonList(((Attribute)node).getQualifiedName());
/*      */       }
/*      */       
/*  731 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class NamespaceUriOp implements NodeOperator {
/*      */     private NamespaceUriOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  739 */       if (node instanceof Element)
/*  740 */         return Collections.singletonList(((Element)node).getNamespace().getURI()); 
/*  741 */       if (node instanceof Attribute) {
/*  742 */         return Collections.singletonList(((Attribute)node).getNamespace().getURI());
/*      */       }
/*      */       
/*  745 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class NamespacePrefixOp implements NodeOperator {
/*      */     private NamespacePrefixOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  753 */       if (node instanceof Element)
/*  754 */         return Collections.singletonList(((Element)node).getNamespace().getPrefix()); 
/*  755 */       if (node instanceof Attribute) {
/*  756 */         return Collections.singletonList(((Attribute)node).getNamespace().getPrefix());
/*      */       }
/*      */       
/*  759 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class CanonicalNameOp implements NodeOperator {
/*      */     private CanonicalNameOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  767 */       if (node instanceof Element) {
/*  768 */         Element element = (Element)node;
/*  769 */         return Collections.singletonList(element.getNamespace().getURI() + element.getName());
/*  770 */       }  if (node instanceof Attribute) {
/*  771 */         Attribute attribute = (Attribute)node;
/*  772 */         return Collections.singletonList(attribute.getNamespace().getURI() + attribute.getName());
/*      */       } 
/*      */ 
/*      */       
/*  776 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final Element getParent(Object node) {
/*  783 */     if (node instanceof Element)
/*  784 */       return ((Element)node).getParent(); 
/*  785 */     if (node instanceof Attribute)
/*  786 */       return ((Attribute)node).getParent(); 
/*  787 */     if (node instanceof Text)
/*  788 */       return ((Text)node).getParent(); 
/*  789 */     if (node instanceof ProcessingInstruction)
/*  790 */       return ((ProcessingInstruction)node).getParent(); 
/*  791 */     if (node instanceof Comment)
/*  792 */       return ((Comment)node).getParent(); 
/*  793 */     if (node instanceof EntityRef) {
/*  794 */       return ((EntityRef)node).getParent();
/*      */     }
/*      */ 
/*      */     
/*  798 */     return null;
/*      */   }
/*      */   
/*      */   private static final class ParentOp implements NodeOperator {
/*      */     private ParentOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  805 */       Element parent = NodeListModel.getParent(node);
/*  806 */       return (parent == null) ? Collections.EMPTY_LIST : Collections.<Element>singletonList(parent);
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class AncestorOp implements NodeOperator { private AncestorOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  813 */       Element parent = NodeListModel.getParent(node);
/*  814 */       if (parent == null) return Collections.EMPTY_LIST; 
/*  815 */       LinkedList<Element> list = new LinkedList();
/*      */       while (true) {
/*  817 */         list.addFirst(parent);
/*  818 */         parent = parent.getParent();
/*  819 */         if (parent == null)
/*  820 */           return list; 
/*      */       } 
/*      */     } }
/*      */   
/*      */   private static final class AncestorOrSelfOp implements NodeOperator { private AncestorOrSelfOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  827 */       Element parent = NodeListModel.getParent(node);
/*  828 */       if (parent == null) return Collections.singletonList(node); 
/*  829 */       LinkedList<Object> list = new LinkedList();
/*  830 */       list.addFirst(node);
/*      */       while (true) {
/*  832 */         list.addFirst(parent);
/*  833 */         parent = parent.getParent();
/*  834 */         if (parent == null)
/*  835 */           return list; 
/*      */       } 
/*      */     } }
/*      */   
/*      */   private static class DescendantOp implements NodeOperator { private DescendantOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  842 */       LinkedList<Element> list = new LinkedList();
/*  843 */       if (node instanceof Element) {
/*  844 */         addChildren((Element)node, list);
/*  845 */       } else if (node instanceof Document) {
/*  846 */         Element root = ((Document)node).getRootElement();
/*  847 */         list.add(root);
/*  848 */         addChildren(root, list);
/*      */       }
/*      */       else {
/*      */         
/*  852 */         return null;
/*      */       } 
/*      */       
/*  855 */       return list;
/*      */     }
/*      */     
/*      */     private void addChildren(Element element, List<Element> list) {
/*  859 */       List children = element.getChildren();
/*  860 */       Iterator<Element> it = children.iterator();
/*  861 */       while (it.hasNext()) {
/*  862 */         Element child = it.next();
/*  863 */         list.add(child);
/*  864 */         addChildren(child, list);
/*      */       } 
/*      */     } }
/*      */   
/*      */   private static final class DescendantOrSelfOp extends DescendantOp {
/*      */     private DescendantOrSelfOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  872 */       LinkedList<Object> list = (LinkedList)super.operate(node);
/*  873 */       list.addFirst(node);
/*  874 */       return list;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class DocumentOp implements NodeOperator { private DocumentOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  881 */       Document doc = null;
/*  882 */       if (node instanceof Element) {
/*  883 */         doc = ((Element)node).getDocument();
/*  884 */       } else if (node instanceof Attribute) {
/*  885 */         Element parent = ((Attribute)node).getParent();
/*  886 */         doc = (parent == null) ? null : parent.getDocument();
/*  887 */       } else if (node instanceof Text) {
/*  888 */         Element parent = ((Text)node).getParent();
/*  889 */         doc = (parent == null) ? null : parent.getDocument();
/*  890 */       } else if (node instanceof Document) {
/*  891 */         doc = (Document)node;
/*  892 */       } else if (node instanceof ProcessingInstruction) {
/*  893 */         doc = ((ProcessingInstruction)node).getDocument();
/*  894 */       } else if (node instanceof EntityRef) {
/*  895 */         doc = ((EntityRef)node).getDocument();
/*  896 */       } else if (node instanceof Comment) {
/*  897 */         doc = ((Comment)node).getDocument();
/*      */       }
/*      */       else {
/*      */         
/*  901 */         return null;
/*      */       } 
/*      */       
/*  904 */       return (doc == null) ? Collections.EMPTY_LIST : Collections.<Document>singletonList(doc);
/*      */     } }
/*      */   
/*      */   private static final class DocTypeOp implements NodeOperator {
/*      */     private DocTypeOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  911 */       if (node instanceof Document) {
/*  912 */         DocType doctype = ((Document)node).getDocType();
/*  913 */         return (doctype == null) ? Collections.EMPTY_LIST : Collections.<DocType>singletonList(doctype);
/*      */       } 
/*      */ 
/*      */       
/*  917 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class ContentOp implements NodeOperator {
/*      */     private ContentOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  925 */       if (node instanceof Element)
/*  926 */         return ((Element)node).getContent(); 
/*  927 */       if (node instanceof Document) {
/*  928 */         return ((Document)node).getContent();
/*      */       }
/*      */       
/*  931 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class TextOp implements NodeOperator {
/*      */     private TextOp() {}
/*      */     
/*      */     public List operate(Object node) {
/*  939 */       if (node instanceof Element)
/*  940 */         return Collections.singletonList(((Element)node).getTextTrim()); 
/*  941 */       if (node instanceof Attribute)
/*  942 */         return Collections.singletonList(((Attribute)node).getValue()); 
/*  943 */       if (node instanceof CDATA)
/*  944 */         return Collections.singletonList(((CDATA)node).getText()); 
/*  945 */       if (node instanceof Comment)
/*  946 */         return Collections.singletonList(((Comment)node).getText()); 
/*  947 */       if (node instanceof ProcessingInstruction) {
/*  948 */         return Collections.singletonList(((ProcessingInstruction)node).getData());
/*      */       }
/*      */       
/*  951 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final List evaluateElementOperation(NodeOperator op, List nodes) throws TemplateModelException {
/*  958 */     int s = nodes.size();
/*  959 */     List[] lists = new List[s];
/*  960 */     int l = 0;
/*      */     
/*  962 */     int i = 0;
/*  963 */     Iterator it = nodes.iterator();
/*  964 */     while (it.hasNext()) {
/*  965 */       List list = op.operate(it.next());
/*  966 */       if (list != null) {
/*  967 */         lists[i++] = list;
/*  968 */         l += list.size();
/*      */       } 
/*      */     } 
/*      */     
/*  972 */     List retval = new ArrayList(l);
/*  973 */     for (int j = 0; j < s; j++) {
/*  974 */       if (lists[j] != null) {
/*  975 */         retval.addAll(lists[j]);
/*      */       }
/*      */     } 
/*  978 */     return retval;
/*      */   }
/*      */ 
/*      */   
/*      */   private static final List evaluateNamedElementOperation(NamedNodeOperator op, String localName, Namespace namespace, List nodes) throws TemplateModelException {
/*  983 */     int s = nodes.size();
/*  984 */     List[] lists = new List[s];
/*  985 */     int l = 0;
/*      */     
/*  987 */     int i = 0;
/*  988 */     Iterator it = nodes.iterator();
/*  989 */     while (it.hasNext()) {
/*  990 */       List list = op.operate(it.next(), localName, namespace);
/*  991 */       lists[i++] = list;
/*  992 */       l += list.size();
/*      */     } 
/*      */     
/*  995 */     List retval = new ArrayList(l);
/*  996 */     for (int j = 0; j < s; j++)
/*  997 */       retval.addAll(lists[j]); 
/*  998 */     return retval;
/*      */   }
/*      */   
/*      */   private static final List removeDuplicates(List list) {
/* 1002 */     int s = list.size();
/* 1003 */     ArrayList<Object> ulist = new ArrayList(s);
/* 1004 */     Set<Object> set = new HashSet(s * 4 / 3, 0.75F);
/* 1005 */     Iterator it = list.iterator();
/* 1006 */     while (it.hasNext()) {
/* 1007 */       Object o = it.next();
/* 1008 */       if (set.add(o))
/* 1009 */         ulist.add(o); 
/*      */     } 
/* 1011 */     ulist.trimToSize();
/* 1012 */     return ulist;
/*      */   }
/*      */   
/*      */   private static final Map createOperations() {
/* 1016 */     Map<Object, Object> map = new HashMap<>();
/*      */     
/* 1018 */     map.put("_ancestor", new AncestorOp());
/* 1019 */     map.put("_ancestorOrSelf", new AncestorOrSelfOp());
/* 1020 */     map.put("_attributes", ALL_ATTRIBUTES_OP);
/* 1021 */     map.put("_children", ALL_CHILDREN_OP);
/* 1022 */     map.put("_cname", new CanonicalNameOp());
/* 1023 */     map.put("_content", new ContentOp());
/* 1024 */     map.put("_descendant", new DescendantOp());
/* 1025 */     map.put("_descendantOrSelf", new DescendantOrSelfOp());
/* 1026 */     map.put("_document", new DocumentOp());
/* 1027 */     map.put("_doctype", new DocTypeOp());
/* 1028 */     map.put("_name", new NameOp());
/* 1029 */     map.put("_nsprefix", new NamespacePrefixOp());
/* 1030 */     map.put("_nsuri", new NamespaceUriOp());
/* 1031 */     map.put("_parent", new ParentOp());
/* 1032 */     map.put("_qname", new QNameOp());
/* 1033 */     map.put("_text", new TextOp());
/*      */     
/* 1035 */     return map;
/*      */   }
/*      */   
/*      */   private static final Map createSpecialOperations() {
/* 1039 */     Map<Object, Object> map = new HashMap<>();
/*      */     
/* 1041 */     Integer copy = Integer.valueOf(0);
/* 1042 */     Integer unique = Integer.valueOf(1);
/* 1043 */     Integer fname = Integer.valueOf(2);
/* 1044 */     Integer ftype = Integer.valueOf(3);
/* 1045 */     Integer type = Integer.valueOf(4);
/* 1046 */     Integer regns = Integer.valueOf(5);
/* 1047 */     Integer plaintext = Integer.valueOf(6);
/*      */     
/* 1049 */     map.put("_copy", copy);
/* 1050 */     map.put("_unique", unique);
/* 1051 */     map.put("_fname", fname);
/* 1052 */     map.put("_ftype", ftype);
/* 1053 */     map.put("_type", type);
/* 1054 */     map.put("_registerNamespace", regns);
/* 1055 */     map.put("_plaintext", plaintext);
/*      */ 
/*      */     
/* 1058 */     map.put("x_copy", copy);
/* 1059 */     map.put("x_unique", unique);
/* 1060 */     map.put("x_fname", fname);
/* 1061 */     map.put("x_ftype", ftype);
/* 1062 */     map.put("x_type", type);
/*      */     
/* 1064 */     return map;
/*      */   }
/*      */   private final class RegisterNamespace implements TemplateMethodModel { private RegisterNamespace() {}
/*      */     
/*      */     public boolean isEmpty() {
/* 1069 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Object exec(List<String> arguments) throws TemplateModelException {
/* 1075 */       if (arguments.size() != 2) {
/* 1076 */         throw new TemplateModelException("_registerNamespace(prefix, uri) requires two arguments");
/*      */       }
/* 1078 */       NodeListModel.this.registerNamespace(arguments.get(0), arguments.get(1));
/*      */       
/* 1080 */       return TemplateScalarModel.EMPTY_STRING;
/*      */     } }
/*      */   
/*      */   private final class NameFilter implements TemplateMethodModel { private NameFilter() {}
/*      */     
/*      */     public boolean isEmpty() {
/* 1086 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     public Object exec(List<?> arguments) {
/* 1091 */       Set names = new HashSet(arguments);
/* 1092 */       List list = new LinkedList(NodeListModel.this.nodes);
/* 1093 */       Iterator it = list.iterator();
/* 1094 */       while (it.hasNext()) {
/* 1095 */         Object node = it.next();
/* 1096 */         String name = null;
/* 1097 */         if (node instanceof Element) {
/* 1098 */           name = ((Element)node).getName();
/* 1099 */         } else if (node instanceof Attribute) {
/* 1100 */           name = ((Attribute)node).getName();
/* 1101 */         } else if (node instanceof ProcessingInstruction) {
/* 1102 */           name = ((ProcessingInstruction)node).getTarget();
/* 1103 */         } else if (node instanceof EntityRef) {
/* 1104 */           name = ((EntityRef)node).getName();
/* 1105 */         } else if (node instanceof DocType) {
/* 1106 */           name = ((DocType)node).getPublicID();
/*      */         } 
/* 1108 */         if (name == null || !names.contains(name))
/* 1109 */           it.remove(); 
/*      */       } 
/* 1111 */       return NodeListModel.createNodeListModel(list, NodeListModel.this.namespaces);
/*      */     } }
/*      */   
/*      */   private final class TypeFilter implements TemplateMethodModel { private TypeFilter() {}
/*      */     
/*      */     public boolean isEmpty() {
/* 1117 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Object exec(List<String> arguments) throws TemplateModelException {
/* 1123 */       if (arguments == null || arguments.size() == 0)
/* 1124 */         throw new TemplateModelException("_type expects exactly one argument"); 
/* 1125 */       String arg = arguments.get(0);
/* 1126 */       boolean invert = (arg.indexOf('!') != -1);
/*      */ 
/*      */ 
/*      */       
/* 1130 */       boolean a = (invert != ((arg.indexOf('a') == -1)));
/* 1131 */       boolean c = (invert != ((arg.indexOf('c') == -1)));
/* 1132 */       boolean d = (invert != ((arg.indexOf('d') == -1)));
/* 1133 */       boolean e = (invert != ((arg.indexOf('e') == -1)));
/* 1134 */       boolean n = (invert != ((arg.indexOf('n') == -1)));
/* 1135 */       boolean p = (invert != ((arg.indexOf('p') == -1)));
/* 1136 */       boolean t = (invert != ((arg.indexOf('t') == -1)));
/* 1137 */       boolean x = (invert != ((arg.indexOf('x') == -1)));
/*      */       
/* 1139 */       LinkedList list = new LinkedList(NodeListModel.this.nodes);
/* 1140 */       Iterator it = list.iterator();
/* 1141 */       while (it.hasNext()) {
/* 1142 */         Object node = it.next();
/* 1143 */         if ((node instanceof Element && e) || (node instanceof Attribute && a) || (node instanceof String && x) || (node instanceof Text && x) || (node instanceof ProcessingInstruction && p) || (node instanceof Comment && c) || (node instanceof EntityRef && n) || (node instanceof Document && d) || (node instanceof DocType && t))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1152 */           it.remove(); } 
/*      */       } 
/* 1154 */       return NodeListModel.createNodeListModel(list, NodeListModel.this.namespaces);
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void main(String[] args) throws Exception {
/* 1169 */     SAXBuilder builder = new SAXBuilder();
/* 1170 */     Document document = builder.build(System.in);
/* 1171 */     SimpleHash model = new SimpleHash((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 1172 */     model.put("document", new NodeListModel(document));
/* 1173 */     FileReader fr = new FileReader(args[0]);
/* 1174 */     Template template = new Template(args[0], fr);
/* 1175 */     Writer w = new OutputStreamWriter(System.out);
/* 1176 */     template.process(model, w);
/* 1177 */     w.flush();
/* 1178 */     w.close();
/*      */   }
/*      */   
/*      */   private static final class AttributeXMLOutputter extends XMLOutputter { private AttributeXMLOutputter() {}
/*      */     
/*      */     public void output(Attribute attribute, Writer out) throws IOException {
/* 1184 */       out.write(" ");
/* 1185 */       out.write(attribute.getQualifiedName());
/* 1186 */       out.write("=");
/*      */       
/* 1188 */       out.write("\"");
/* 1189 */       out.write(escapeAttributeEntities(attribute.getValue()));
/* 1190 */       out.write("\"");
/*      */     } }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class JDOMXPathEx
/*      */     extends JDOMXPath
/*      */   {
/*      */     JDOMXPathEx(String path) throws JaxenException {
/* 1199 */       super(path);
/*      */     }
/*      */ 
/*      */     
/*      */     public List selectNodes(Object object, Map namespaces) throws JaxenException {
/* 1204 */       Context context = getContext(object);
/* 1205 */       context.getContextSupport().setNamespaceContext(new NamespaceContextImpl(namespaces));
/* 1206 */       return selectNodesForContext(context);
/*      */     }
/*      */     
/*      */     private static final class NamespaceContextImpl
/*      */       implements NamespaceContext
/*      */     {
/*      */       private final Map namespaces;
/*      */       
/*      */       NamespaceContextImpl(Map namespaces) {
/* 1215 */         this.namespaces = namespaces;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public String translateNamespacePrefixToUri(String prefix) {
/* 1221 */         if (prefix.length() == 0) {
/* 1222 */           return prefix;
/*      */         }
/* 1224 */         synchronized (this.namespaces) {
/* 1225 */           Namespace ns = (Namespace)this.namespaces.get(prefix);
/* 1226 */           return (ns == null) ? null : ns.getURI();
/*      */         } 
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jdom\NodeListModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */