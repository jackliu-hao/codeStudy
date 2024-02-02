package freemarker.ext.jdom;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._TemplateAPI;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/** @deprecated */
@Deprecated
public class NodeListModel implements TemplateHashModel, TemplateMethodModel, TemplateCollectionModel, TemplateSequenceModel, TemplateScalarModel {
   private static final AttributeXMLOutputter OUTPUT = new AttributeXMLOutputter();
   private static final NodeListModel EMPTY = new NodeListModel((List)null, false);
   private static final Map XPATH_CACHE = new WeakHashMap();
   private static final NamedNodeOperator NAMED_CHILDREN_OP = new NamedChildrenOp();
   private static final NamedNodeOperator NAMED_ATTRIBUTE_OP = new NamedAttributeOp();
   private static final NodeOperator ALL_ATTRIBUTES_OP = new AllAttributesOp();
   private static final NodeOperator ALL_CHILDREN_OP = new AllChildrenOp();
   private static final Map OPERATIONS = createOperations();
   private static final Map SPECIAL_OPERATIONS = createSpecialOperations();
   private static final int SPECIAL_OPERATION_COPY = 0;
   private static final int SPECIAL_OPERATION_UNIQUE = 1;
   private static final int SPECIAL_OPERATION_FILTER_NAME = 2;
   private static final int SPECIAL_OPERATION_FILTER_TYPE = 3;
   private static final int SPECIAL_OPERATION_QUERY_TYPE = 4;
   private static final int SPECIAL_OPERATION_REGISTER_NAMESPACE = 5;
   private static final int SPECIAL_OPERATION_PLAINTEXT = 6;
   private final List nodes;
   private final Map namespaces;

   public NodeListModel(Document document) {
      this.nodes = document == null ? Collections.EMPTY_LIST : Collections.singletonList(document);
      this.namespaces = new HashMap();
   }

   public NodeListModel(Element element) {
      this.nodes = element == null ? Collections.EMPTY_LIST : Collections.singletonList(element);
      this.namespaces = new HashMap();
   }

   private NodeListModel(Object object, Map namespaces) {
      this.nodes = object == null ? Collections.EMPTY_LIST : Collections.singletonList(object);
      this.namespaces = namespaces;
   }

   public NodeListModel(List nodes) {
      this(nodes, true);
   }

   public NodeListModel(List nodes, boolean copy) {
      this.nodes = (List)(copy && nodes != null ? new ArrayList(nodes) : (nodes == null ? Collections.EMPTY_LIST : nodes));
      this.namespaces = new HashMap();
   }

   private NodeListModel(List nodes, Map namespaces) {
      this.nodes = nodes == null ? Collections.EMPTY_LIST : nodes;
      this.namespaces = namespaces;
   }

   private static final NodeListModel createNodeListModel(List list, Map namespaces) {
      if (list != null && !list.isEmpty()) {
         return list.size() == 1 ? new NodeListModel(list.get(0), namespaces) : new NodeListModel(list, namespaces);
      } else {
         return namespaces.isEmpty() ? EMPTY : new NodeListModel(Collections.EMPTY_LIST, namespaces);
      }
   }

   public boolean isEmpty() {
      return this.nodes.isEmpty();
   }

   public String getAsString() throws TemplateModelException {
      if (this.isEmpty()) {
         return "";
      } else {
         StringWriter sw = new StringWriter(this.nodes.size() * 128);

         try {
            Iterator i = this.nodes.iterator();

            while(i.hasNext()) {
               Object node = i.next();
               if (node instanceof Element) {
                  OUTPUT.output((Element)node, sw);
               } else if (node instanceof Attribute) {
                  OUTPUT.output((Attribute)node, sw);
               } else if (node instanceof String) {
                  sw.write(OUTPUT.escapeElementEntities(node.toString()));
               } else if (node instanceof Text) {
                  OUTPUT.output((Text)node, sw);
               } else if (node instanceof Document) {
                  OUTPUT.output((Document)node, sw);
               } else if (node instanceof ProcessingInstruction) {
                  OUTPUT.output((ProcessingInstruction)node, sw);
               } else if (node instanceof Comment) {
                  OUTPUT.output((Comment)node, sw);
               } else if (node instanceof CDATA) {
                  OUTPUT.output((CDATA)node, sw);
               } else if (node instanceof DocType) {
                  OUTPUT.output((DocType)node, sw);
               } else {
                  if (!(node instanceof EntityRef)) {
                     throw new TemplateModelException(node.getClass().getName() + " is not a core JDOM class");
                  }

                  OUTPUT.output((EntityRef)node, sw);
               }
            }
         } catch (IOException var4) {
            throw new TemplateModelException(var4.getMessage());
         }

         return sw.toString();
      }
   }

   public TemplateModel get(String key) throws TemplateModelException {
      if (this.isEmpty()) {
         return EMPTY;
      } else if (key != null && key.length() != 0) {
         NodeOperator op = null;
         NamedNodeOperator nop = null;
         String name = null;
         Integer specop;
         switch (key.charAt(0)) {
            case '*':
               if (key.length() != 1) {
                  throw new TemplateModelException("Invalid key [" + key + "]");
               }

               op = ALL_CHILDREN_OP;
               break;
            case '@':
               if (key.length() == 2 && key.charAt(1) == '*') {
                  op = ALL_ATTRIBUTES_OP;
               } else {
                  nop = NAMED_ATTRIBUTE_OP;
                  name = key.substring(1);
               }
               break;
            case '_':
            case 'x':
               op = (NodeOperator)OPERATIONS.get(key);
               if (op == null) {
                  specop = (Integer)SPECIAL_OPERATIONS.get(key);
                  if (specop != null) {
                     switch (specop) {
                        case 0:
                           synchronized(this.namespaces) {
                              return new NodeListModel(this.nodes, (Map)((HashMap)this.namespaces).clone());
                           }
                        case 1:
                           return new NodeListModel(removeDuplicates(this.nodes), this.namespaces);
                        case 2:
                           return new NameFilter();
                        case 3:
                           return new TypeFilter();
                        case 4:
                           return this.getType();
                        case 5:
                           return new RegisterNamespace();
                        case 6:
                           return this.getPlainText();
                     }
                  }
               }
         }

         if (op == null && nop == null) {
            nop = NAMED_CHILDREN_OP;
            name = key;
         }

         specop = null;
         List list;
         if (op != null) {
            list = evaluateElementOperation(op, this.nodes);
         } else {
            String localName = name;
            Namespace namespace = Namespace.NO_NAMESPACE;
            int colon = name.indexOf(58);
            if (colon != -1) {
               localName = name.substring(colon + 1);
               String nsPrefix = name.substring(0, colon);
               synchronized(this.namespaces) {
                  namespace = (Namespace)this.namespaces.get(nsPrefix);
               }

               if (namespace == null) {
                  if (!nsPrefix.equals("xml")) {
                     throw new TemplateModelException("Unregistered namespace prefix '" + nsPrefix + "'");
                  }

                  namespace = Namespace.XML_NAMESPACE;
               }
            }

            list = evaluateNamedElementOperation(nop, localName, namespace, this.nodes);
         }

         return createNodeListModel(list, this.namespaces);
      } else {
         throw new TemplateModelException("Invalid key [" + key + "]");
      }
   }

   private TemplateModel getType() {
      if (this.nodes.size() == 0) {
         return new SimpleScalar("");
      } else {
         Object firstNode = this.nodes.get(0);
         char code;
         if (firstNode instanceof Element) {
            code = 'e';
         } else if (!(firstNode instanceof Text) && !(firstNode instanceof String)) {
            if (firstNode instanceof Attribute) {
               code = 'a';
            } else if (firstNode instanceof EntityRef) {
               code = 'n';
            } else if (firstNode instanceof Document) {
               code = 'd';
            } else if (firstNode instanceof DocType) {
               code = 't';
            } else if (firstNode instanceof Comment) {
               code = 'c';
            } else if (firstNode instanceof ProcessingInstruction) {
               code = 'p';
            } else {
               code = '?';
            }
         } else {
            code = 'x';
         }

         return new SimpleScalar(new String(new char[]{code}));
      }
   }

   private SimpleScalar getPlainText() throws TemplateModelException {
      List list = evaluateElementOperation((TextOp)OPERATIONS.get("_text"), this.nodes);
      StringBuilder buf = new StringBuilder();
      Iterator it = list.iterator();

      while(it.hasNext()) {
         buf.append(it.next());
      }

      return new SimpleScalar(buf.toString());
   }

   public TemplateModelIterator iterator() {
      return new TemplateModelIterator() {
         private final Iterator it;

         {
            this.it = NodeListModel.this.nodes.iterator();
         }

         public TemplateModel next() {
            return this.it.hasNext() ? new NodeListModel(this.it.next(), NodeListModel.this.namespaces) : null;
         }

         public boolean hasNext() {
            return this.it.hasNext();
         }
      };
   }

   public TemplateModel get(int i) throws TemplateModelException {
      try {
         return new NodeListModel(this.nodes.get(i), this.namespaces);
      } catch (IndexOutOfBoundsException var3) {
         throw new TemplateModelException("Index out of bounds: " + var3.getMessage());
      }
   }

   public int size() {
      return this.nodes.size();
   }

   public Object exec(List arguments) throws TemplateModelException {
      if (arguments != null && arguments.size() == 1) {
         String xpathString = (String)arguments.get(0);
         JDOMXPathEx xpath = null;

         try {
            synchronized(XPATH_CACHE) {
               xpath = (JDOMXPathEx)XPATH_CACHE.get(xpathString);
               if (xpath == null) {
                  xpath = new JDOMXPathEx(xpathString);
                  XPATH_CACHE.put(xpathString, xpath);
               }
            }

            return createNodeListModel(xpath.selectNodes(this.nodes, this.namespaces), this.namespaces);
         } catch (Exception var7) {
            throw new TemplateModelException("Could not evaulate XPath expression " + xpathString, var7);
         }
      } else {
         throw new TemplateModelException("Exactly one argument required for execute() on NodeTemplate");
      }
   }

   public void registerNamespace(String prefix, String uri) {
      synchronized(this.namespaces) {
         this.namespaces.put(prefix, Namespace.getNamespace(prefix, uri));
      }
   }

   private static final Element getParent(Object node) {
      if (node instanceof Element) {
         return ((Element)node).getParent();
      } else if (node instanceof Attribute) {
         return ((Attribute)node).getParent();
      } else if (node instanceof Text) {
         return ((Text)node).getParent();
      } else if (node instanceof ProcessingInstruction) {
         return ((ProcessingInstruction)node).getParent();
      } else if (node instanceof Comment) {
         return ((Comment)node).getParent();
      } else {
         return node instanceof EntityRef ? ((EntityRef)node).getParent() : null;
      }
   }

   private static final List evaluateElementOperation(NodeOperator op, List nodes) throws TemplateModelException {
      int s = nodes.size();
      List[] lists = new List[s];
      int l = 0;
      int i = 0;
      Iterator it = nodes.iterator();

      while(it.hasNext()) {
         List list = op.operate(it.next());
         if (list != null) {
            lists[i++] = list;
            l += list.size();
         }
      }

      List retval = new ArrayList(l);

      for(int i = 0; i < s; ++i) {
         if (lists[i] != null) {
            retval.addAll(lists[i]);
         }
      }

      return retval;
   }

   private static final List evaluateNamedElementOperation(NamedNodeOperator op, String localName, Namespace namespace, List nodes) throws TemplateModelException {
      int s = nodes.size();
      List[] lists = new List[s];
      int l = 0;
      int i = 0;

      List list;
      for(Iterator it = nodes.iterator(); it.hasNext(); l += list.size()) {
         list = op.operate(it.next(), localName, namespace);
         lists[i++] = list;
      }

      List retval = new ArrayList(l);

      for(int i = 0; i < s; ++i) {
         retval.addAll(lists[i]);
      }

      return retval;
   }

   private static final List removeDuplicates(List list) {
      int s = list.size();
      ArrayList ulist = new ArrayList(s);
      Set set = new HashSet(s * 4 / 3, 0.75F);
      Iterator it = list.iterator();

      while(it.hasNext()) {
         Object o = it.next();
         if (set.add(o)) {
            ulist.add(o);
         }
      }

      ulist.trimToSize();
      return ulist;
   }

   private static final Map createOperations() {
      Map map = new HashMap();
      map.put("_ancestor", new AncestorOp());
      map.put("_ancestorOrSelf", new AncestorOrSelfOp());
      map.put("_attributes", ALL_ATTRIBUTES_OP);
      map.put("_children", ALL_CHILDREN_OP);
      map.put("_cname", new CanonicalNameOp());
      map.put("_content", new ContentOp());
      map.put("_descendant", new DescendantOp());
      map.put("_descendantOrSelf", new DescendantOrSelfOp());
      map.put("_document", new DocumentOp());
      map.put("_doctype", new DocTypeOp());
      map.put("_name", new NameOp());
      map.put("_nsprefix", new NamespacePrefixOp());
      map.put("_nsuri", new NamespaceUriOp());
      map.put("_parent", new ParentOp());
      map.put("_qname", new QNameOp());
      map.put("_text", new TextOp());
      return map;
   }

   private static final Map createSpecialOperations() {
      Map map = new HashMap();
      Integer copy = 0;
      Integer unique = 1;
      Integer fname = 2;
      Integer ftype = 3;
      Integer type = 4;
      Integer regns = 5;
      Integer plaintext = 6;
      map.put("_copy", copy);
      map.put("_unique", unique);
      map.put("_fname", fname);
      map.put("_ftype", ftype);
      map.put("_type", type);
      map.put("_registerNamespace", regns);
      map.put("_plaintext", plaintext);
      map.put("x_copy", copy);
      map.put("x_unique", unique);
      map.put("x_fname", fname);
      map.put("x_ftype", ftype);
      map.put("x_type", type);
      return map;
   }

   /** @deprecated */
   @Deprecated
   public static void main(String[] args) throws Exception {
      SAXBuilder builder = new SAXBuilder();
      Document document = builder.build(System.in);
      SimpleHash model = new SimpleHash(_TemplateAPI.SAFE_OBJECT_WRAPPER);
      model.put("document", new NodeListModel(document));
      FileReader fr = new FileReader(args[0]);
      Template template = new Template(args[0], fr);
      Writer w = new OutputStreamWriter(System.out);
      template.process(model, w);
      w.flush();
      w.close();
   }

   // $FF: synthetic method
   NodeListModel(Object x0, Map x1, Object x2) {
      this(x0, x1);
   }

   private static final class JDOMXPathEx extends JDOMXPath {
      JDOMXPathEx(String path) throws JaxenException {
         super(path);
      }

      public List selectNodes(Object object, Map namespaces) throws JaxenException {
         Context context = this.getContext(object);
         context.getContextSupport().setNamespaceContext(new NamespaceContextImpl(namespaces));
         return this.selectNodesForContext(context);
      }

      private static final class NamespaceContextImpl implements NamespaceContext {
         private final Map namespaces;

         NamespaceContextImpl(Map namespaces) {
            this.namespaces = namespaces;
         }

         public String translateNamespacePrefixToUri(String prefix) {
            if (prefix.length() == 0) {
               return prefix;
            } else {
               synchronized(this.namespaces) {
                  Namespace ns = (Namespace)this.namespaces.get(prefix);
                  return ns == null ? null : ns.getURI();
               }
            }
         }
      }
   }

   private static final class AttributeXMLOutputter extends XMLOutputter {
      private AttributeXMLOutputter() {
      }

      public void output(Attribute attribute, Writer out) throws IOException {
         out.write(" ");
         out.write(attribute.getQualifiedName());
         out.write("=");
         out.write("\"");
         out.write(this.escapeAttributeEntities(attribute.getValue()));
         out.write("\"");
      }

      // $FF: synthetic method
      AttributeXMLOutputter(Object x0) {
         this();
      }
   }

   private final class TypeFilter implements TemplateMethodModel {
      private TypeFilter() {
      }

      public boolean isEmpty() {
         return false;
      }

      public Object exec(List arguments) throws TemplateModelException {
         if (arguments != null && arguments.size() != 0) {
            String arg = (String)arguments.get(0);
            boolean invert = arg.indexOf(33) != -1;
            boolean a = invert != (arg.indexOf(97) == -1);
            boolean c = invert != (arg.indexOf(99) == -1);
            boolean d = invert != (arg.indexOf(100) == -1);
            boolean e = invert != (arg.indexOf(101) == -1);
            boolean n = invert != (arg.indexOf(110) == -1);
            boolean p = invert != (arg.indexOf(112) == -1);
            boolean t = invert != (arg.indexOf(116) == -1);
            boolean x = invert != (arg.indexOf(120) == -1);
            LinkedList list = new LinkedList(NodeListModel.this.nodes);
            Iterator it = list.iterator();

            while(true) {
               Object node;
               do {
                  if (!it.hasNext()) {
                     return NodeListModel.createNodeListModel(list, NodeListModel.this.namespaces);
                  }

                  node = it.next();
               } while((!(node instanceof Element) || !e) && (!(node instanceof Attribute) || !a) && (!(node instanceof String) || !x) && (!(node instanceof Text) || !x) && (!(node instanceof ProcessingInstruction) || !p) && (!(node instanceof Comment) || !c) && (!(node instanceof EntityRef) || !n) && (!(node instanceof Document) || !d) && (!(node instanceof DocType) || !t));

               it.remove();
            }
         } else {
            throw new TemplateModelException("_type expects exactly one argument");
         }
      }

      // $FF: synthetic method
      TypeFilter(Object x1) {
         this();
      }
   }

   private final class NameFilter implements TemplateMethodModel {
      private NameFilter() {
      }

      public boolean isEmpty() {
         return false;
      }

      public Object exec(List arguments) {
         Set names = new HashSet(arguments);
         List list = new LinkedList(NodeListModel.this.nodes);
         Iterator it = list.iterator();

         while(true) {
            String name;
            do {
               if (!it.hasNext()) {
                  return NodeListModel.createNodeListModel(list, NodeListModel.this.namespaces);
               }

               Object node = it.next();
               name = null;
               if (node instanceof Element) {
                  name = ((Element)node).getName();
               } else if (node instanceof Attribute) {
                  name = ((Attribute)node).getName();
               } else if (node instanceof ProcessingInstruction) {
                  name = ((ProcessingInstruction)node).getTarget();
               } else if (node instanceof EntityRef) {
                  name = ((EntityRef)node).getName();
               } else if (node instanceof DocType) {
                  name = ((DocType)node).getPublicID();
               }
            } while(name != null && names.contains(name));

            it.remove();
         }
      }

      // $FF: synthetic method
      NameFilter(Object x1) {
         this();
      }
   }

   private final class RegisterNamespace implements TemplateMethodModel {
      private RegisterNamespace() {
      }

      public boolean isEmpty() {
         return false;
      }

      public Object exec(List arguments) throws TemplateModelException {
         if (arguments.size() != 2) {
            throw new TemplateModelException("_registerNamespace(prefix, uri) requires two arguments");
         } else {
            NodeListModel.this.registerNamespace((String)arguments.get(0), (String)arguments.get(1));
            return TemplateScalarModel.EMPTY_STRING;
         }
      }

      // $FF: synthetic method
      RegisterNamespace(Object x1) {
         this();
      }
   }

   private static final class TextOp implements NodeOperator {
      private TextOp() {
      }

      public List operate(Object node) {
         if (node instanceof Element) {
            return Collections.singletonList(((Element)node).getTextTrim());
         } else if (node instanceof Attribute) {
            return Collections.singletonList(((Attribute)node).getValue());
         } else if (node instanceof CDATA) {
            return Collections.singletonList(((CDATA)node).getText());
         } else if (node instanceof Comment) {
            return Collections.singletonList(((Comment)node).getText());
         } else {
            return node instanceof ProcessingInstruction ? Collections.singletonList(((ProcessingInstruction)node).getData()) : null;
         }
      }

      // $FF: synthetic method
      TextOp(Object x0) {
         this();
      }
   }

   private static final class ContentOp implements NodeOperator {
      private ContentOp() {
      }

      public List operate(Object node) {
         if (node instanceof Element) {
            return ((Element)node).getContent();
         } else {
            return node instanceof Document ? ((Document)node).getContent() : null;
         }
      }

      // $FF: synthetic method
      ContentOp(Object x0) {
         this();
      }
   }

   private static final class DocTypeOp implements NodeOperator {
      private DocTypeOp() {
      }

      public List operate(Object node) {
         if (node instanceof Document) {
            DocType doctype = ((Document)node).getDocType();
            return doctype == null ? Collections.EMPTY_LIST : Collections.singletonList(doctype);
         } else {
            return null;
         }
      }

      // $FF: synthetic method
      DocTypeOp(Object x0) {
         this();
      }
   }

   private static final class DocumentOp implements NodeOperator {
      private DocumentOp() {
      }

      public List operate(Object node) {
         Document doc = null;
         if (node instanceof Element) {
            doc = ((Element)node).getDocument();
         } else {
            Element parent;
            if (node instanceof Attribute) {
               parent = ((Attribute)node).getParent();
               doc = parent == null ? null : parent.getDocument();
            } else if (node instanceof Text) {
               parent = ((Text)node).getParent();
               doc = parent == null ? null : parent.getDocument();
            } else if (node instanceof Document) {
               doc = (Document)node;
            } else if (node instanceof ProcessingInstruction) {
               doc = ((ProcessingInstruction)node).getDocument();
            } else if (node instanceof EntityRef) {
               doc = ((EntityRef)node).getDocument();
            } else {
               if (!(node instanceof Comment)) {
                  return null;
               }

               doc = ((Comment)node).getDocument();
            }
         }

         return doc == null ? Collections.EMPTY_LIST : Collections.singletonList(doc);
      }

      // $FF: synthetic method
      DocumentOp(Object x0) {
         this();
      }
   }

   private static final class DescendantOrSelfOp extends DescendantOp {
      private DescendantOrSelfOp() {
         super(null);
      }

      public List operate(Object node) {
         LinkedList list = (LinkedList)super.operate(node);
         list.addFirst(node);
         return list;
      }

      // $FF: synthetic method
      DescendantOrSelfOp(Object x0) {
         this();
      }
   }

   private static class DescendantOp implements NodeOperator {
      private DescendantOp() {
      }

      public List operate(Object node) {
         LinkedList list = new LinkedList();
         if (node instanceof Element) {
            this.addChildren((Element)node, list);
         } else {
            if (!(node instanceof Document)) {
               return null;
            }

            Element root = ((Document)node).getRootElement();
            list.add(root);
            this.addChildren(root, list);
         }

         return list;
      }

      private void addChildren(Element element, List list) {
         List children = element.getChildren();
         Iterator it = children.iterator();

         while(it.hasNext()) {
            Element child = (Element)it.next();
            list.add(child);
            this.addChildren(child, list);
         }

      }

      // $FF: synthetic method
      DescendantOp(Object x0) {
         this();
      }
   }

   private static final class AncestorOrSelfOp implements NodeOperator {
      private AncestorOrSelfOp() {
      }

      public List operate(Object node) {
         Element parent = NodeListModel.getParent(node);
         if (parent == null) {
            return Collections.singletonList(node);
         } else {
            LinkedList list = new LinkedList();
            list.addFirst(node);

            do {
               list.addFirst(parent);
               parent = parent.getParent();
            } while(parent != null);

            return list;
         }
      }

      // $FF: synthetic method
      AncestorOrSelfOp(Object x0) {
         this();
      }
   }

   private static final class AncestorOp implements NodeOperator {
      private AncestorOp() {
      }

      public List operate(Object node) {
         Element parent = NodeListModel.getParent(node);
         if (parent == null) {
            return Collections.EMPTY_LIST;
         } else {
            LinkedList list = new LinkedList();

            do {
               list.addFirst(parent);
               parent = parent.getParent();
            } while(parent != null);

            return list;
         }
      }

      // $FF: synthetic method
      AncestorOp(Object x0) {
         this();
      }
   }

   private static final class ParentOp implements NodeOperator {
      private ParentOp() {
      }

      public List operate(Object node) {
         Element parent = NodeListModel.getParent(node);
         return parent == null ? Collections.EMPTY_LIST : Collections.singletonList(parent);
      }

      // $FF: synthetic method
      ParentOp(Object x0) {
         this();
      }
   }

   private static final class CanonicalNameOp implements NodeOperator {
      private CanonicalNameOp() {
      }

      public List operate(Object node) {
         if (node instanceof Element) {
            Element element = (Element)node;
            return Collections.singletonList(element.getNamespace().getURI() + element.getName());
         } else if (node instanceof Attribute) {
            Attribute attribute = (Attribute)node;
            return Collections.singletonList(attribute.getNamespace().getURI() + attribute.getName());
         } else {
            return null;
         }
      }

      // $FF: synthetic method
      CanonicalNameOp(Object x0) {
         this();
      }
   }

   private static final class NamespacePrefixOp implements NodeOperator {
      private NamespacePrefixOp() {
      }

      public List operate(Object node) {
         if (node instanceof Element) {
            return Collections.singletonList(((Element)node).getNamespace().getPrefix());
         } else {
            return node instanceof Attribute ? Collections.singletonList(((Attribute)node).getNamespace().getPrefix()) : null;
         }
      }

      // $FF: synthetic method
      NamespacePrefixOp(Object x0) {
         this();
      }
   }

   private static final class NamespaceUriOp implements NodeOperator {
      private NamespaceUriOp() {
      }

      public List operate(Object node) {
         if (node instanceof Element) {
            return Collections.singletonList(((Element)node).getNamespace().getURI());
         } else {
            return node instanceof Attribute ? Collections.singletonList(((Attribute)node).getNamespace().getURI()) : null;
         }
      }

      // $FF: synthetic method
      NamespaceUriOp(Object x0) {
         this();
      }
   }

   private static final class QNameOp implements NodeOperator {
      private QNameOp() {
      }

      public List operate(Object node) {
         if (node instanceof Element) {
            return Collections.singletonList(((Element)node).getQualifiedName());
         } else {
            return node instanceof Attribute ? Collections.singletonList(((Attribute)node).getQualifiedName()) : null;
         }
      }

      // $FF: synthetic method
      QNameOp(Object x0) {
         this();
      }
   }

   private static final class NameOp implements NodeOperator {
      private NameOp() {
      }

      public List operate(Object node) {
         if (node instanceof Element) {
            return Collections.singletonList(((Element)node).getName());
         } else if (node instanceof Attribute) {
            return Collections.singletonList(((Attribute)node).getName());
         } else if (node instanceof EntityRef) {
            return Collections.singletonList(((EntityRef)node).getName());
         } else if (node instanceof ProcessingInstruction) {
            return Collections.singletonList(((ProcessingInstruction)node).getTarget());
         } else {
            return node instanceof DocType ? Collections.singletonList(((DocType)node).getPublicID()) : null;
         }
      }

      // $FF: synthetic method
      NameOp(Object x0) {
         this();
      }
   }

   private static final class NamedAttributeOp implements NamedNodeOperator {
      private NamedAttributeOp() {
      }

      public List operate(Object node, String localName, Namespace namespace) {
         Attribute attr = null;
         if (node instanceof Element) {
            Element element = (Element)node;
            attr = element.getAttribute(localName, namespace);
         } else if (node instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction)node;
            if ("target".equals(localName)) {
               attr = new Attribute("target", pi.getTarget());
            } else if ("data".equals(localName)) {
               attr = new Attribute("data", pi.getData());
            } else {
               attr = new Attribute(localName, pi.getValue(localName));
            }
         } else {
            if (!(node instanceof DocType)) {
               return null;
            }

            DocType doctype = (DocType)node;
            if ("publicId".equals(localName)) {
               attr = new Attribute("publicId", doctype.getPublicID());
            } else if ("systemId".equals(localName)) {
               attr = new Attribute("systemId", doctype.getSystemID());
            } else if ("elementName".equals(localName)) {
               attr = new Attribute("elementName", doctype.getElementName());
            }
         }

         return attr == null ? Collections.EMPTY_LIST : Collections.singletonList(attr);
      }

      // $FF: synthetic method
      NamedAttributeOp(Object x0) {
         this();
      }
   }

   private static final class AllAttributesOp implements NodeOperator {
      private AllAttributesOp() {
      }

      public List operate(Object node) {
         return !(node instanceof Element) ? null : ((Element)node).getAttributes();
      }

      // $FF: synthetic method
      AllAttributesOp(Object x0) {
         this();
      }
   }

   private static final class NamedChildrenOp implements NamedNodeOperator {
      private NamedChildrenOp() {
      }

      public List operate(Object node, String localName, Namespace namespace) {
         if (node instanceof Element) {
            return ((Element)node).getChildren(localName, namespace);
         } else if (node instanceof Document) {
            Element root = ((Document)node).getRootElement();
            return root != null && root.getName().equals(localName) && root.getNamespaceURI().equals(namespace.getURI()) ? Collections.singletonList(root) : Collections.EMPTY_LIST;
         } else {
            return null;
         }
      }

      // $FF: synthetic method
      NamedChildrenOp(Object x0) {
         this();
      }
   }

   private static final class AllChildrenOp implements NodeOperator {
      private AllChildrenOp() {
      }

      public List operate(Object node) {
         if (node instanceof Element) {
            return ((Element)node).getChildren();
         } else if (node instanceof Document) {
            Element root = ((Document)node).getRootElement();
            return root == null ? Collections.EMPTY_LIST : Collections.singletonList(root);
         } else {
            return null;
         }
      }

      // $FF: synthetic method
      AllChildrenOp(Object x0) {
         this();
      }
   }

   private interface NamedNodeOperator {
      List operate(Object var1, String var2, Namespace var3) throws TemplateModelException;
   }

   private interface NodeOperator {
      List operate(Object var1) throws TemplateModelException;
   }
}
