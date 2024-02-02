package freemarker.ext.dom;

import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.log.Logger;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNodeModelEx;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateSequenceModel;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class NodeModel implements TemplateNodeModelEx, TemplateHashModel, TemplateSequenceModel, AdapterTemplateModel, WrapperTemplateModel, _UnexpectedTypeErrorExplainerTemplateModel {
   private static final Logger LOG = Logger.getLogger("freemarker.dom");
   private static final Object STATIC_LOCK = new Object();
   private static DocumentBuilderFactory docBuilderFactory;
   private static final Map xpathSupportMap = Collections.synchronizedMap(new WeakHashMap());
   private static XPathSupport jaxenXPathSupport;
   private static ErrorHandler errorHandler;
   static Class xpathSupportClass;
   final Node node;
   private TemplateSequenceModel children;
   private NodeModel parent;

   /** @deprecated */
   @Deprecated
   public static void setDocumentBuilderFactory(DocumentBuilderFactory docBuilderFactory) {
      synchronized(STATIC_LOCK) {
         NodeModel.docBuilderFactory = docBuilderFactory;
      }
   }

   public static DocumentBuilderFactory getDocumentBuilderFactory() {
      synchronized(STATIC_LOCK) {
         if (docBuilderFactory == null) {
            DocumentBuilderFactory newFactory = DocumentBuilderFactory.newInstance();
            newFactory.setNamespaceAware(true);
            newFactory.setIgnoringElementContentWhitespace(true);
            docBuilderFactory = newFactory;
         }

         return docBuilderFactory;
      }
   }

   /** @deprecated */
   @Deprecated
   public static void setErrorHandler(ErrorHandler errorHandler) {
      synchronized(STATIC_LOCK) {
         NodeModel.errorHandler = errorHandler;
      }
   }

   public static ErrorHandler getErrorHandler() {
      synchronized(STATIC_LOCK) {
         return errorHandler;
      }
   }

   public static NodeModel parse(InputSource is, boolean removeComments, boolean removePIs) throws SAXException, IOException, ParserConfigurationException {
      DocumentBuilder builder = getDocumentBuilderFactory().newDocumentBuilder();
      ErrorHandler errorHandler = getErrorHandler();
      if (errorHandler != null) {
         builder.setErrorHandler(errorHandler);
      }

      Document doc;
      try {
         doc = builder.parse(is);
      } catch (MalformedURLException var7) {
         if (is.getSystemId() == null && is.getCharacterStream() == null && is.getByteStream() == null) {
            throw new MalformedURLException("The SAX InputSource has systemId == null && characterStream == null && byteStream == null. This is often because it was created with a null InputStream or Reader, which is often because the XML file it should point to was not found. (The original exception was: " + var7 + ")");
         }

         throw var7;
      }

      if (removeComments && removePIs) {
         simplify(doc);
      } else {
         if (removeComments) {
            removeComments(doc);
         }

         if (removePIs) {
            removePIs(doc);
         }

         mergeAdjacentText(doc);
      }

      return wrap(doc);
   }

   public static NodeModel parse(InputSource is) throws SAXException, IOException, ParserConfigurationException {
      return parse(is, true, true);
   }

   public static NodeModel parse(File f, boolean removeComments, boolean removePIs) throws SAXException, IOException, ParserConfigurationException {
      DocumentBuilder builder = getDocumentBuilderFactory().newDocumentBuilder();
      ErrorHandler errorHandler = getErrorHandler();
      if (errorHandler != null) {
         builder.setErrorHandler(errorHandler);
      }

      Document doc = builder.parse(f);
      if (removeComments && removePIs) {
         simplify(doc);
      } else {
         if (removeComments) {
            removeComments(doc);
         }

         if (removePIs) {
            removePIs(doc);
         }

         mergeAdjacentText(doc);
      }

      return wrap(doc);
   }

   public static NodeModel parse(File f) throws SAXException, IOException, ParserConfigurationException {
      return parse(f, true, true);
   }

   protected NodeModel(Node node) {
      this.node = node;
   }

   public Node getNode() {
      return this.node;
   }

   public TemplateModel get(String key) throws TemplateModelException {
      if (key.startsWith("@@")) {
         if (key.equals(AtAtKey.TEXT.getKey())) {
            return new SimpleScalar(getText(this.node));
         } else {
            String qname;
            if (key.equals(AtAtKey.NAMESPACE.getKey())) {
               qname = this.node.getNamespaceURI();
               return qname == null ? null : new SimpleScalar(qname);
            } else if (key.equals(AtAtKey.LOCAL_NAME.getKey())) {
               qname = this.node.getLocalName();
               if (qname == null) {
                  qname = this.getNodeName();
               }

               return new SimpleScalar(qname);
            } else {
               NodeOutputter nu;
               StringBuilder buf;
               if (key.equals(AtAtKey.MARKUP.getKey())) {
                  buf = new StringBuilder();
                  nu = new NodeOutputter(this.node);
                  nu.outputContent(this.node, buf);
                  return new SimpleScalar(buf.toString());
               } else if (key.equals(AtAtKey.NESTED_MARKUP.getKey())) {
                  buf = new StringBuilder();
                  nu = new NodeOutputter(this.node);
                  nu.outputContent(this.node.getChildNodes(), buf);
                  return new SimpleScalar(buf.toString());
               } else if (key.equals(AtAtKey.QNAME.getKey())) {
                  qname = this.getQualifiedName();
                  return qname != null ? new SimpleScalar(qname) : null;
               } else if (AtAtKey.containsKey(key)) {
                  throw new TemplateModelException("\"" + key + "\" is not supported for an XML node of type \"" + this.getNodeType() + "\".");
               } else {
                  throw new TemplateModelException("Unsupported @@ key: " + key);
               }
            }
         }
      } else {
         XPathSupport xps = this.getXPathSupport();
         if (xps == null) {
            throw new TemplateModelException("No XPath support is available (add Apache Xalan or Jaxen as dependency). This is either malformed, or an XPath expression: " + key);
         } else {
            return xps.executeQuery(this.node, key);
         }
      }
   }

   public TemplateNodeModel getParentNode() {
      if (this.parent == null) {
         Node parentNode = this.node.getParentNode();
         if (parentNode == null && this.node instanceof Attr) {
            parentNode = ((Attr)this.node).getOwnerElement();
         }

         this.parent = wrap((Node)parentNode);
      }

      return this.parent;
   }

   public TemplateNodeModelEx getPreviousSibling() throws TemplateModelException {
      return wrap(this.node.getPreviousSibling());
   }

   public TemplateNodeModelEx getNextSibling() throws TemplateModelException {
      return wrap(this.node.getNextSibling());
   }

   public TemplateSequenceModel getChildNodes() {
      if (this.children == null) {
         this.children = new NodeListModel(this.node.getChildNodes(), this);
      }

      return this.children;
   }

   public final String getNodeType() throws TemplateModelException {
      short nodeType = this.node.getNodeType();
      switch (nodeType) {
         case 1:
            return "element";
         case 2:
            return "attribute";
         case 3:
            return "text";
         case 4:
            return "text";
         case 5:
            return "entity_reference";
         case 6:
            return "entity";
         case 7:
            return "pi";
         case 8:
            return "comment";
         case 9:
            return "document";
         case 10:
            return "document_type";
         case 11:
            return "document_fragment";
         case 12:
            return "notation";
         default:
            throw new TemplateModelException("Unknown node type: " + nodeType + ". This should be impossible!");
      }
   }

   public TemplateModel exec(List args) throws TemplateModelException {
      if (args.size() != 1) {
         throw new TemplateModelException("Expecting exactly one arguments");
      } else {
         String query = (String)args.get(0);
         XPathSupport xps = this.getXPathSupport();
         if (xps == null) {
            throw new TemplateModelException("No XPath support available");
         } else {
            return xps.executeQuery(this.node, query);
         }
      }
   }

   public final int size() {
      return 1;
   }

   public final TemplateModel get(int i) {
      return i == 0 ? this : null;
   }

   public String getNodeNamespace() {
      int nodeType = this.node.getNodeType();
      if (nodeType != 2 && nodeType != 1) {
         return null;
      } else {
         String result = this.node.getNamespaceURI();
         if (result == null && nodeType == 1) {
            result = "";
         } else if ("".equals(result) && nodeType == 2) {
            result = null;
         }

         return result;
      }
   }

   public final int hashCode() {
      return this.node.hashCode();
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else {
         return other.getClass() == this.getClass() && ((NodeModel)other).node.equals(this.node);
      }
   }

   public static NodeModel wrap(Node node) {
      if (node == null) {
         return null;
      } else {
         NodeModel result = null;
         switch (node.getNodeType()) {
            case 1:
               result = new ElementModel((Element)node);
               break;
            case 2:
               result = new AttributeNodeModel((Attr)node);
               break;
            case 3:
            case 4:
            case 8:
               result = new CharacterDataNodeModel((CharacterData)node);
            case 5:
            case 6:
            default:
               break;
            case 7:
               result = new PINodeModel((ProcessingInstruction)node);
               break;
            case 9:
               result = new DocumentModel((Document)node);
               break;
            case 10:
               result = new DocumentTypeModel((DocumentType)node);
         }

         return (NodeModel)result;
      }
   }

   public static void removeComments(Node parent) {
      Node nextSibling;
      for(Node child = parent.getFirstChild(); child != null; child = nextSibling) {
         nextSibling = child.getNextSibling();
         if (child.getNodeType() == 8) {
            parent.removeChild(child);
         } else if (child.hasChildNodes()) {
            removeComments(child);
         }
      }

   }

   public static void removePIs(Node parent) {
      Node nextSibling;
      for(Node child = parent.getFirstChild(); child != null; child = nextSibling) {
         nextSibling = child.getNextSibling();
         if (child.getNodeType() == 7) {
            parent.removeChild(child);
         } else if (child.hasChildNodes()) {
            removePIs(child);
         }
      }

   }

   public static void mergeAdjacentText(Node parent) {
      mergeAdjacentText(parent, new StringBuilder(0));
   }

   private static void mergeAdjacentText(Node parent, StringBuilder collectorBuf) {
      Node next;
      for(Node child = parent.getFirstChild(); child != null; child = next) {
         next = child.getNextSibling();
         if (!(child instanceof Text)) {
            mergeAdjacentText(child, collectorBuf);
         } else {
            boolean atFirstText;
            for(atFirstText = true; next instanceof Text; next = child.getNextSibling()) {
               if (atFirstText) {
                  collectorBuf.setLength(0);
                  collectorBuf.ensureCapacity(child.getNodeValue().length() + next.getNodeValue().length());
                  collectorBuf.append(child.getNodeValue());
                  atFirstText = false;
               }

               collectorBuf.append(next.getNodeValue());
               parent.removeChild(next);
            }

            if (!atFirstText && collectorBuf.length() != 0) {
               ((CharacterData)child).setData(collectorBuf.toString());
            }
         }
      }

   }

   public static void simplify(Node parent) {
      simplify(parent, new StringBuilder(0));
   }

   private static void simplify(Node parent, StringBuilder collectorTextChildBuff) {
      Node collectorTextChild = null;

      Node next;
      for(Node child = parent.getFirstChild(); child != null; child = next) {
         next = child.getNextSibling();
         if (child.hasChildNodes()) {
            if (collectorTextChild != null) {
               if (collectorTextChildBuff.length() != 0) {
                  ((CharacterData)collectorTextChild).setData(collectorTextChildBuff.toString());
                  collectorTextChildBuff.setLength(0);
               }

               collectorTextChild = null;
            }

            simplify(child, collectorTextChildBuff);
         } else {
            int type = child.getNodeType();
            if (type != 3 && type != 4) {
               if (type == 8) {
                  parent.removeChild(child);
               } else if (type == 7) {
                  parent.removeChild(child);
               } else if (collectorTextChild != null) {
                  if (collectorTextChildBuff.length() != 0) {
                     ((CharacterData)collectorTextChild).setData(collectorTextChildBuff.toString());
                     collectorTextChildBuff.setLength(0);
                  }

                  collectorTextChild = null;
               }
            } else if (collectorTextChild != null) {
               if (collectorTextChildBuff.length() == 0) {
                  collectorTextChildBuff.ensureCapacity(collectorTextChild.getNodeValue().length() + child.getNodeValue().length());
                  collectorTextChildBuff.append(collectorTextChild.getNodeValue());
               }

               collectorTextChildBuff.append(child.getNodeValue());
               parent.removeChild(child);
            } else {
               collectorTextChild = child;
               collectorTextChildBuff.setLength(0);
            }
         }
      }

      if (collectorTextChild != null && collectorTextChildBuff.length() != 0) {
         ((CharacterData)collectorTextChild).setData(collectorTextChildBuff.toString());
         collectorTextChildBuff.setLength(0);
      }

   }

   NodeModel getDocumentNodeModel() {
      return this.node instanceof Document ? this : wrap(this.node.getOwnerDocument());
   }

   public static void useDefaultXPathSupport() {
      synchronized(STATIC_LOCK) {
         xpathSupportClass = null;
         jaxenXPathSupport = null;

         try {
            useXalanXPathSupport();
         } catch (ClassNotFoundException var7) {
         } catch (Exception var8) {
            LOG.debug("Failed to use Xalan XPath support.", var8);
         } catch (IllegalAccessError var9) {
            LOG.debug("Failed to use Xalan internal XPath support.", var9);
         }

         if (xpathSupportClass == null) {
            try {
               useSunInternalXPathSupport();
            } catch (Exception var5) {
               LOG.debug("Failed to use Sun internal XPath support.", var5);
            } catch (IllegalAccessError var6) {
               LOG.debug("Failed to use Sun internal XPath support. Tip: On Java 9+, you may need Xalan or Jaxen+Saxpath.", var6);
            }
         }

         if (xpathSupportClass == null) {
            try {
               useJaxenXPathSupport();
            } catch (ClassNotFoundException var3) {
            } catch (IllegalAccessError | Exception var4) {
               LOG.debug("Failed to use Jaxen XPath support.", var4);
            }
         }

      }
   }

   public static void useJaxenXPathSupport() throws Exception {
      Class.forName("org.jaxen.dom.DOMXPath");
      Class c = Class.forName("freemarker.ext.dom.JaxenXPathSupport");
      jaxenXPathSupport = (XPathSupport)c.newInstance();
      synchronized(STATIC_LOCK) {
         xpathSupportClass = c;
      }

      LOG.debug("Using Jaxen classes for XPath support");
   }

   public static void useXalanXPathSupport() throws Exception {
      Class.forName("org.apache.xpath.XPath");
      Class c = Class.forName("freemarker.ext.dom.XalanXPathSupport");
      synchronized(STATIC_LOCK) {
         xpathSupportClass = c;
      }

      LOG.debug("Using Xalan classes for XPath support");
   }

   public static void useSunInternalXPathSupport() throws Exception {
      Class.forName("com.sun.org.apache.xpath.internal.XPath");
      Class c = Class.forName("freemarker.ext.dom.SunInternalXalanXPathSupport");
      synchronized(STATIC_LOCK) {
         xpathSupportClass = c;
      }

      LOG.debug("Using Sun's internal Xalan classes for XPath support");
   }

   public static void setXPathSupportClass(Class cl) {
      if (cl != null && !XPathSupport.class.isAssignableFrom(cl)) {
         throw new RuntimeException("Class " + cl.getName() + " does not implement freemarker.ext.dom.XPathSupport");
      } else {
         synchronized(STATIC_LOCK) {
            xpathSupportClass = cl;
         }
      }
   }

   public static Class getXPathSupportClass() {
      synchronized(STATIC_LOCK) {
         return xpathSupportClass;
      }
   }

   private static String getText(Node node) {
      String result = "";
      if (!(node instanceof Text) && !(node instanceof CDATASection)) {
         if (node instanceof Element) {
            NodeList children = node.getChildNodes();

            for(int i = 0; i < children.getLength(); ++i) {
               result = result + getText(children.item(i));
            }
         } else if (node instanceof Document) {
            result = getText(((Document)node).getDocumentElement());
         }
      } else {
         result = ((CharacterData)node).getData();
      }

      return result;
   }

   XPathSupport getXPathSupport() {
      if (jaxenXPathSupport != null) {
         return jaxenXPathSupport;
      } else {
         XPathSupport xps = null;
         Document doc = this.node.getOwnerDocument();
         if (doc == null) {
            doc = (Document)this.node;
         }

         synchronized(doc) {
            WeakReference ref = (WeakReference)xpathSupportMap.get(doc);
            if (ref != null) {
               xps = (XPathSupport)ref.get();
            }

            if (xps == null && xpathSupportClass != null) {
               try {
                  xps = (XPathSupport)xpathSupportClass.newInstance();
                  xpathSupportMap.put(doc, new WeakReference(xps));
               } catch (Exception var7) {
                  LOG.error("Error instantiating xpathSupport class", var7);
               }
            }

            return xps;
         }
      }
   }

   String getQualifiedName() throws TemplateModelException {
      return this.getNodeName();
   }

   public Object getAdaptedObject(Class hint) {
      return this.node;
   }

   public Object getWrappedObject() {
      return this.node;
   }

   public Object[] explainTypeError(Class[] expectedClasses) {
      for(int i = 0; i < expectedClasses.length; ++i) {
         Class expectedClass = expectedClasses[i];
         if (TemplateDateModel.class.isAssignableFrom(expectedClass) || TemplateNumberModel.class.isAssignableFrom(expectedClass) || TemplateBooleanModel.class.isAssignableFrom(expectedClass)) {
            return new Object[]{"XML node values are always strings (text), that is, they can't be used as number, date/time/datetime or boolean without explicit conversion (such as someNode?number, someNode?datetime.xs, someNode?date.xs, someNode?time.xs, someNode?boolean)."};
         }
      }

      return null;
   }

   static {
      try {
         useDefaultXPathSupport();
      } catch (Exception var1) {
      }

      if (xpathSupportClass == null && LOG.isWarnEnabled()) {
         LOG.warn("No XPath support is available. If you need it, add Apache Xalan or Jaxen as dependency.");
      }

   }
}
