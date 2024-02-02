/*     */ package freemarker.ext.dom;
/*     */ 
/*     */ import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.AdapterTemplateModel;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNodeModel;
/*     */ import freemarker.template.TemplateNodeModelEx;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.net.MalformedURLException;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.CharacterData;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.DocumentType;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.ProcessingInstruction;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class NodeModel
/*     */   implements TemplateNodeModelEx, TemplateHashModel, TemplateSequenceModel, AdapterTemplateModel, WrapperTemplateModel, _UnexpectedTypeErrorExplainerTemplateModel
/*     */ {
/*  89 */   private static final Logger LOG = Logger.getLogger("freemarker.dom");
/*     */   
/*  91 */   private static final Object STATIC_LOCK = new Object();
/*     */   
/*     */   private static DocumentBuilderFactory docBuilderFactory;
/*     */   
/*  95 */   private static final Map xpathSupportMap = Collections.synchronizedMap(new WeakHashMap<>());
/*     */   private static XPathSupport jaxenXPathSupport;
/*     */   private static ErrorHandler errorHandler;
/*     */   static Class xpathSupportClass;
/*     */   final Node node;
/*     */   private TemplateSequenceModel children;
/*     */   private NodeModel parent;
/*     */   
/*     */   static {
/*     */     try {
/* 105 */       useDefaultXPathSupport();
/* 106 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/* 109 */     if (xpathSupportClass == null && LOG.isWarnEnabled()) {
/* 110 */       LOG.warn("No XPath support is available. If you need it, add Apache Xalan or Jaxen as dependency.");
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
/*     */   @Deprecated
/*     */   public static void setDocumentBuilderFactory(DocumentBuilderFactory docBuilderFactory) {
/* 134 */     synchronized (STATIC_LOCK) {
/* 135 */       NodeModel.docBuilderFactory = docBuilderFactory;
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
/*     */   public static DocumentBuilderFactory getDocumentBuilderFactory() {
/* 147 */     synchronized (STATIC_LOCK) {
/* 148 */       if (docBuilderFactory == null) {
/* 149 */         DocumentBuilderFactory newFactory = DocumentBuilderFactory.newInstance();
/* 150 */         newFactory.setNamespaceAware(true);
/* 151 */         newFactory.setIgnoringElementContentWhitespace(true);
/* 152 */         docBuilderFactory = newFactory;
/*     */       } 
/* 154 */       return docBuilderFactory;
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
/*     */   @Deprecated
/*     */   public static void setErrorHandler(ErrorHandler errorHandler) {
/* 169 */     synchronized (STATIC_LOCK) {
/* 170 */       NodeModel.errorHandler = errorHandler;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ErrorHandler getErrorHandler() {
/* 180 */     synchronized (STATIC_LOCK) {
/* 181 */       return errorHandler;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static NodeModel parse(InputSource is, boolean removeComments, boolean removePIs) throws SAXException, IOException, ParserConfigurationException {
/*     */     Document doc;
/* 215 */     DocumentBuilder builder = getDocumentBuilderFactory().newDocumentBuilder();
/* 216 */     ErrorHandler errorHandler = getErrorHandler();
/* 217 */     if (errorHandler != null) builder.setErrorHandler(errorHandler);
/*     */     
/*     */     try {
/* 220 */       doc = builder.parse(is);
/* 221 */     } catch (MalformedURLException e) {
/*     */       
/* 223 */       if (is.getSystemId() == null && is.getCharacterStream() == null && is.getByteStream() == null) {
/* 224 */         throw new MalformedURLException("The SAX InputSource has systemId == null && characterStream == null && byteStream == null. This is often because it was created with a null InputStream or Reader, which is often because the XML file it should point to was not found. (The original exception was: " + e + ")");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 230 */       throw e;
/*     */     } 
/*     */     
/* 233 */     if (removeComments && removePIs) {
/* 234 */       simplify(doc);
/*     */     } else {
/* 236 */       if (removeComments) {
/* 237 */         removeComments(doc);
/*     */       }
/* 239 */       if (removePIs) {
/* 240 */         removePIs(doc);
/*     */       }
/* 242 */       mergeAdjacentText(doc);
/*     */     } 
/* 244 */     return wrap(doc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static NodeModel parse(InputSource is) throws SAXException, IOException, ParserConfigurationException {
/* 252 */     return parse(is, true, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static NodeModel parse(File f, boolean removeComments, boolean removePIs) throws SAXException, IOException, ParserConfigurationException {
/* 262 */     DocumentBuilder builder = getDocumentBuilderFactory().newDocumentBuilder();
/* 263 */     ErrorHandler errorHandler = getErrorHandler();
/* 264 */     if (errorHandler != null) builder.setErrorHandler(errorHandler); 
/* 265 */     Document doc = builder.parse(f);
/* 266 */     if (removeComments && removePIs) {
/* 267 */       simplify(doc);
/*     */     } else {
/* 269 */       if (removeComments) {
/* 270 */         removeComments(doc);
/*     */       }
/* 272 */       if (removePIs) {
/* 273 */         removePIs(doc);
/*     */       }
/* 275 */       mergeAdjacentText(doc);
/*     */     } 
/* 277 */     return wrap(doc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static NodeModel parse(File f) throws SAXException, IOException, ParserConfigurationException {
/* 285 */     return parse(f, true, true);
/*     */   }
/*     */   
/*     */   protected NodeModel(Node node) {
/* 289 */     this.node = node;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Node getNode() {
/* 297 */     return this.node;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/* 302 */     if (key.startsWith("@@")) {
/* 303 */       if (key.equals(AtAtKey.TEXT.getKey()))
/* 304 */         return (TemplateModel)new SimpleScalar(getText(this.node)); 
/* 305 */       if (key.equals(AtAtKey.NAMESPACE.getKey())) {
/* 306 */         String nsURI = this.node.getNamespaceURI();
/* 307 */         return (nsURI == null) ? null : (TemplateModel)new SimpleScalar(nsURI);
/* 308 */       }  if (key.equals(AtAtKey.LOCAL_NAME.getKey())) {
/* 309 */         String localName = this.node.getLocalName();
/* 310 */         if (localName == null) {
/* 311 */           localName = getNodeName();
/*     */         }
/* 313 */         return (TemplateModel)new SimpleScalar(localName);
/* 314 */       }  if (key.equals(AtAtKey.MARKUP.getKey())) {
/* 315 */         StringBuilder buf = new StringBuilder();
/* 316 */         NodeOutputter nu = new NodeOutputter(this.node);
/* 317 */         nu.outputContent(this.node, buf);
/* 318 */         return (TemplateModel)new SimpleScalar(buf.toString());
/* 319 */       }  if (key.equals(AtAtKey.NESTED_MARKUP.getKey())) {
/* 320 */         StringBuilder buf = new StringBuilder();
/* 321 */         NodeOutputter nu = new NodeOutputter(this.node);
/* 322 */         nu.outputContent(this.node.getChildNodes(), buf);
/* 323 */         return (TemplateModel)new SimpleScalar(buf.toString());
/* 324 */       }  if (key.equals(AtAtKey.QNAME.getKey())) {
/* 325 */         String qname = getQualifiedName();
/* 326 */         return (qname != null) ? (TemplateModel)new SimpleScalar(qname) : null;
/*     */       } 
/*     */       
/* 329 */       if (AtAtKey.containsKey(key)) {
/* 330 */         throw new TemplateModelException("\"" + key + "\" is not supported for an XML node of type \"" + 
/* 331 */             getNodeType() + "\".");
/*     */       }
/* 333 */       throw new TemplateModelException("Unsupported @@ key: " + key);
/*     */     } 
/*     */ 
/*     */     
/* 337 */     XPathSupport xps = getXPathSupport();
/* 338 */     if (xps == null) {
/* 339 */       throw new TemplateModelException("No XPath support is available (add Apache Xalan or Jaxen as dependency). This is either malformed, or an XPath expression: " + key);
/*     */     }
/*     */ 
/*     */     
/* 343 */     return xps.executeQuery(this.node, key);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateNodeModel getParentNode() {
/* 349 */     if (this.parent == null) {
/* 350 */       Node parentNode = this.node.getParentNode();
/* 351 */       if (parentNode == null && 
/* 352 */         this.node instanceof Attr) {
/* 353 */         parentNode = ((Attr)this.node).getOwnerElement();
/*     */       }
/*     */       
/* 356 */       this.parent = wrap(parentNode);
/*     */     } 
/* 358 */     return (TemplateNodeModel)this.parent;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateNodeModelEx getPreviousSibling() throws TemplateModelException {
/* 363 */     return wrap(this.node.getPreviousSibling());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateNodeModelEx getNextSibling() throws TemplateModelException {
/* 368 */     return wrap(this.node.getNextSibling());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateSequenceModel getChildNodes() {
/* 373 */     if (this.children == null) {
/* 374 */       this.children = (TemplateSequenceModel)new NodeListModel(this.node.getChildNodes(), this);
/*     */     }
/* 376 */     return this.children;
/*     */   }
/*     */ 
/*     */   
/*     */   public final String getNodeType() throws TemplateModelException {
/* 381 */     short nodeType = this.node.getNodeType();
/* 382 */     switch (nodeType) { case 2:
/* 383 */         return "attribute";
/* 384 */       case 4: return "text";
/* 385 */       case 8: return "comment";
/* 386 */       case 11: return "document_fragment";
/* 387 */       case 9: return "document";
/* 388 */       case 10: return "document_type";
/* 389 */       case 1: return "element";
/* 390 */       case 6: return "entity";
/* 391 */       case 5: return "entity_reference";
/* 392 */       case 12: return "notation";
/* 393 */       case 7: return "pi";
/* 394 */       case 3: return "text"; }
/*     */     
/* 396 */     throw new TemplateModelException("Unknown node type: " + nodeType + ". This should be impossible!");
/*     */   }
/*     */   
/*     */   public TemplateModel exec(List<String> args) throws TemplateModelException {
/* 400 */     if (args.size() != 1) {
/* 401 */       throw new TemplateModelException("Expecting exactly one arguments");
/*     */     }
/* 403 */     String query = args.get(0);
/*     */     
/* 405 */     XPathSupport xps = getXPathSupport();
/* 406 */     if (xps == null) {
/* 407 */       throw new TemplateModelException("No XPath support available");
/*     */     }
/* 409 */     return xps.executeQuery(this.node, query);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int size() {
/* 417 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public final TemplateModel get(int i) {
/* 422 */     return (i == 0) ? (TemplateModel)this : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getNodeNamespace() {
/* 427 */     int nodeType = this.node.getNodeType();
/* 428 */     if (nodeType != 2 && nodeType != 1) {
/* 429 */       return null;
/*     */     }
/* 431 */     String result = this.node.getNamespaceURI();
/* 432 */     if (result == null && nodeType == 1) {
/* 433 */       result = "";
/* 434 */     } else if ("".equals(result) && nodeType == 2) {
/* 435 */       result = null;
/*     */     } 
/* 437 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 442 */     return this.node.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 447 */     if (other == null) return false; 
/* 448 */     return (other.getClass() == getClass() && ((NodeModel)other).node
/* 449 */       .equals(this.node));
/*     */   }
/*     */   
/*     */   public static NodeModel wrap(Node node) {
/* 453 */     if (node == null) {
/* 454 */       return null;
/*     */     }
/* 456 */     NodeModel result = null;
/* 457 */     switch (node.getNodeType()) { case 9:
/* 458 */         result = new DocumentModel((Document)node); break;
/* 459 */       case 1: result = new ElementModel((Element)node); break;
/* 460 */       case 2: result = new AttributeNodeModel((Attr)node); break;
/*     */       case 3: case 4:
/*     */       case 8:
/* 463 */         result = new CharacterDataNodeModel((CharacterData)node); break;
/* 464 */       case 7: result = new PINodeModel((ProcessingInstruction)node); break;
/* 465 */       case 10: result = new DocumentTypeModel((DocumentType)node); break; }
/*     */     
/* 467 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void removeComments(Node parent) {
/* 476 */     Node child = parent.getFirstChild();
/* 477 */     while (child != null) {
/* 478 */       Node nextSibling = child.getNextSibling();
/* 479 */       if (child.getNodeType() == 8) {
/* 480 */         parent.removeChild(child);
/* 481 */       } else if (child.hasChildNodes()) {
/* 482 */         removeComments(child);
/*     */       } 
/* 484 */       child = nextSibling;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void removePIs(Node parent) {
/* 494 */     Node child = parent.getFirstChild();
/* 495 */     while (child != null) {
/* 496 */       Node nextSibling = child.getNextSibling();
/* 497 */       if (child.getNodeType() == 7) {
/* 498 */         parent.removeChild(child);
/* 499 */       } else if (child.hasChildNodes()) {
/* 500 */         removePIs(child);
/*     */       } 
/* 502 */       child = nextSibling;
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
/*     */   public static void mergeAdjacentText(Node parent) {
/* 517 */     mergeAdjacentText(parent, new StringBuilder(0));
/*     */   }
/*     */   
/*     */   private static void mergeAdjacentText(Node parent, StringBuilder collectorBuf) {
/* 521 */     Node child = parent.getFirstChild();
/* 522 */     while (child != null) {
/* 523 */       Node next = child.getNextSibling();
/* 524 */       if (child instanceof org.w3c.dom.Text) {
/* 525 */         boolean atFirstText = true;
/* 526 */         while (next instanceof org.w3c.dom.Text) {
/* 527 */           if (atFirstText) {
/* 528 */             collectorBuf.setLength(0);
/* 529 */             collectorBuf.ensureCapacity(child.getNodeValue().length() + next.getNodeValue().length());
/* 530 */             collectorBuf.append(child.getNodeValue());
/* 531 */             atFirstText = false;
/*     */           } 
/* 533 */           collectorBuf.append(next.getNodeValue());
/*     */           
/* 535 */           parent.removeChild(next);
/*     */           
/* 537 */           next = child.getNextSibling();
/*     */         } 
/* 539 */         if (!atFirstText && collectorBuf.length() != 0) {
/* 540 */           ((CharacterData)child).setData(collectorBuf.toString());
/*     */         }
/*     */       } else {
/* 543 */         mergeAdjacentText(child, collectorBuf);
/*     */       } 
/* 545 */       child = next;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void simplify(Node parent) {
/* 555 */     simplify(parent, new StringBuilder(0));
/*     */   }
/*     */   
/*     */   private static void simplify(Node parent, StringBuilder collectorTextChildBuff) {
/* 559 */     Node collectorTextChild = null;
/* 560 */     Node child = parent.getFirstChild();
/* 561 */     while (child != null) {
/* 562 */       Node next = child.getNextSibling();
/* 563 */       if (child.hasChildNodes()) {
/* 564 */         if (collectorTextChild != null) {
/*     */           
/* 566 */           if (collectorTextChildBuff.length() != 0) {
/* 567 */             ((CharacterData)collectorTextChild).setData(collectorTextChildBuff.toString());
/* 568 */             collectorTextChildBuff.setLength(0);
/*     */           } 
/* 570 */           collectorTextChild = null;
/*     */         } 
/*     */         
/* 573 */         simplify(child, collectorTextChildBuff);
/*     */       } else {
/* 575 */         int type = child.getNodeType();
/* 576 */         if (type == 3 || type == 4) {
/* 577 */           if (collectorTextChild != null) {
/* 578 */             if (collectorTextChildBuff.length() == 0) {
/* 579 */               collectorTextChildBuff.ensureCapacity(collectorTextChild
/* 580 */                   .getNodeValue().length() + child.getNodeValue().length());
/* 581 */               collectorTextChildBuff.append(collectorTextChild.getNodeValue());
/*     */             } 
/* 583 */             collectorTextChildBuff.append(child.getNodeValue());
/* 584 */             parent.removeChild(child);
/*     */           } else {
/* 586 */             collectorTextChild = child;
/* 587 */             collectorTextChildBuff.setLength(0);
/*     */           } 
/* 589 */         } else if (type == 8) {
/* 590 */           parent.removeChild(child);
/* 591 */         } else if (type == 7) {
/* 592 */           parent.removeChild(child);
/* 593 */         } else if (collectorTextChild != null) {
/*     */           
/* 595 */           if (collectorTextChildBuff.length() != 0) {
/* 596 */             ((CharacterData)collectorTextChild).setData(collectorTextChildBuff.toString());
/* 597 */             collectorTextChildBuff.setLength(0);
/*     */           } 
/* 599 */           collectorTextChild = null;
/*     */         } 
/*     */       } 
/* 602 */       child = next;
/*     */     } 
/*     */     
/* 605 */     if (collectorTextChild != null)
/*     */     {
/* 607 */       if (collectorTextChildBuff.length() != 0) {
/* 608 */         ((CharacterData)collectorTextChild).setData(collectorTextChildBuff.toString());
/* 609 */         collectorTextChildBuff.setLength(0);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   NodeModel getDocumentNodeModel() {
/* 615 */     if (this.node instanceof Document) {
/* 616 */       return this;
/*     */     }
/* 618 */     return wrap(this.node.getOwnerDocument());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void useDefaultXPathSupport() {
/* 627 */     synchronized (STATIC_LOCK) {
/* 628 */       xpathSupportClass = null;
/* 629 */       jaxenXPathSupport = null;
/*     */       
/*     */       try {
/* 632 */         useXalanXPathSupport();
/* 633 */       } catch (ClassNotFoundException classNotFoundException) {
/*     */       
/* 635 */       } catch (Exception e) {
/* 636 */         LOG.debug("Failed to use Xalan XPath support.", e);
/* 637 */       } catch (IllegalAccessError e) {
/* 638 */         LOG.debug("Failed to use Xalan internal XPath support.", e);
/*     */       } 
/*     */       
/* 641 */       if (xpathSupportClass == null) {
/* 642 */         try { useSunInternalXPathSupport(); }
/* 643 */         catch (Exception e)
/* 644 */         { LOG.debug("Failed to use Sun internal XPath support.", e); }
/* 645 */         catch (IllegalAccessError e)
/*     */         
/* 647 */         { LOG.debug("Failed to use Sun internal XPath support. Tip: On Java 9+, you may need Xalan or Jaxen+Saxpath.", e); }
/*     */       
/*     */       }
/*     */       
/* 651 */       if (xpathSupportClass == null) {
/* 652 */         try { useJaxenXPathSupport(); }
/* 653 */         catch (ClassNotFoundException classNotFoundException)
/*     */         {  }
/* 655 */         catch (Exception|IllegalAccessError e)
/* 656 */         { LOG.debug("Failed to use Jaxen XPath support.", e); }
/*     */       
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void useJaxenXPathSupport() throws Exception {
/* 666 */     Class.forName("org.jaxen.dom.DOMXPath");
/* 667 */     Class<?> c = Class.forName("freemarker.ext.dom.JaxenXPathSupport");
/* 668 */     jaxenXPathSupport = (XPathSupport)c.newInstance();
/* 669 */     synchronized (STATIC_LOCK) {
/* 670 */       xpathSupportClass = c;
/*     */     } 
/* 672 */     LOG.debug("Using Jaxen classes for XPath support");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void useXalanXPathSupport() throws Exception {
/* 680 */     Class.forName("org.apache.xpath.XPath");
/* 681 */     Class<?> c = Class.forName("freemarker.ext.dom.XalanXPathSupport");
/* 682 */     synchronized (STATIC_LOCK) {
/* 683 */       xpathSupportClass = c;
/*     */     } 
/* 685 */     LOG.debug("Using Xalan classes for XPath support");
/*     */   }
/*     */   
/*     */   public static void useSunInternalXPathSupport() throws Exception {
/* 689 */     Class.forName("com.sun.org.apache.xpath.internal.XPath");
/* 690 */     Class<?> c = Class.forName("freemarker.ext.dom.SunInternalXalanXPathSupport");
/* 691 */     synchronized (STATIC_LOCK) {
/* 692 */       xpathSupportClass = c;
/*     */     } 
/* 694 */     LOG.debug("Using Sun's internal Xalan classes for XPath support");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setXPathSupportClass(Class<?> cl) {
/* 703 */     if (cl != null && !XPathSupport.class.isAssignableFrom(cl)) {
/* 704 */       throw new RuntimeException("Class " + cl.getName() + " does not implement freemarker.ext.dom.XPathSupport");
/*     */     }
/*     */     
/* 707 */     synchronized (STATIC_LOCK) {
/* 708 */       xpathSupportClass = cl;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class getXPathSupportClass() {
/* 717 */     synchronized (STATIC_LOCK) {
/* 718 */       return xpathSupportClass;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static String getText(Node node) {
/* 723 */     String result = "";
/* 724 */     if (node instanceof org.w3c.dom.Text || node instanceof org.w3c.dom.CDATASection) {
/* 725 */       result = ((CharacterData)node).getData();
/* 726 */     } else if (node instanceof Element) {
/* 727 */       NodeList children = node.getChildNodes();
/* 728 */       for (int i = 0; i < children.getLength(); i++) {
/* 729 */         result = result + getText(children.item(i));
/*     */       }
/* 731 */     } else if (node instanceof Document) {
/* 732 */       result = getText(((Document)node).getDocumentElement());
/*     */     } 
/* 734 */     return result;
/*     */   }
/*     */   
/*     */   XPathSupport getXPathSupport() {
/* 738 */     if (jaxenXPathSupport != null) {
/* 739 */       return jaxenXPathSupport;
/*     */     }
/* 741 */     XPathSupport xps = null;
/* 742 */     Document doc = this.node.getOwnerDocument();
/* 743 */     if (doc == null) {
/* 744 */       doc = (Document)this.node;
/*     */     }
/* 746 */     synchronized (doc) {
/* 747 */       WeakReference<XPathSupport> ref = (WeakReference)xpathSupportMap.get(doc);
/* 748 */       if (ref != null) {
/* 749 */         xps = ref.get();
/*     */       }
/* 751 */       if (xps == null && xpathSupportClass != null) {
/*     */         try {
/* 753 */           xps = xpathSupportClass.newInstance();
/* 754 */           xpathSupportMap.put(doc, new WeakReference<>(xps));
/* 755 */         } catch (Exception e) {
/* 756 */           LOG.error("Error instantiating xpathSupport class", e);
/*     */         } 
/*     */       }
/*     */     } 
/* 760 */     return xps;
/*     */   }
/*     */ 
/*     */   
/*     */   String getQualifiedName() throws TemplateModelException {
/* 765 */     return getNodeName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAdaptedObject(Class hint) {
/* 770 */     return this.node;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getWrappedObject() {
/* 775 */     return this.node;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object[] explainTypeError(Class[] expectedClasses) {
/* 780 */     for (int i = 0; i < expectedClasses.length; i++) {
/* 781 */       Class<?> expectedClass = expectedClasses[i];
/* 782 */       if (TemplateDateModel.class.isAssignableFrom(expectedClass) || TemplateNumberModel.class
/* 783 */         .isAssignableFrom(expectedClass) || TemplateBooleanModel.class
/* 784 */         .isAssignableFrom(expectedClass)) {
/* 785 */         return new Object[] { "XML node values are always strings (text), that is, they can't be used as number, date/time/datetime or boolean without explicit conversion (such as someNode?number, someNode?datetime.xs, someNode?date.xs, someNode?time.xs, someNode?boolean)." };
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 793 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\NodeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */