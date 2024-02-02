package freemarker.template.utility;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._TemplateAPI;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/** @deprecated */
@Deprecated
public class DOMNodeModel implements TemplateHashModel {
   private static HashMap equivalenceTable = new HashMap();
   private Node node;
   private HashMap cache = new HashMap();

   public DOMNodeModel(Node node) {
      this.node = node;
   }

   public TemplateModel get(String key) throws TemplateModelException {
      TemplateModel result = null;
      if (equivalenceTable.containsKey(key)) {
         key = (String)equivalenceTable.get(key);
      }

      if (this.cache.containsKey(key)) {
         result = (TemplateModel)this.cache.get(key);
      }

      if (result == null) {
         if (!"attributes".equals(key)) {
            if (key.charAt(0) == '@') {
               if (!(this.node instanceof Element)) {
                  throw new TemplateModelException("Trying to get an attribute value for a non-element node");
               }

               String attValue = ((Element)this.node).getAttribute(key.substring(1));
               result = new SimpleScalar(attValue);
            } else if ("is_element".equals(key)) {
               result = this.node instanceof Element ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            } else if ("is_text".equals(key)) {
               result = this.node instanceof Text ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            } else if ("name".equals(key)) {
               result = new SimpleScalar(this.node.getNodeName());
            } else if ("children".equals(key)) {
               result = new NodeListTM(this.node.getChildNodes());
            } else {
               Node previous;
               if ("parent".equals(key)) {
                  previous = this.node.getParentNode();
                  result = previous == null ? null : new DOMNodeModel(previous);
               } else if ("ancestorByName".equals(key)) {
                  result = new AncestorByName();
               } else if ("nextSibling".equals(key)) {
                  previous = this.node.getNextSibling();
                  result = previous == null ? null : new DOMNodeModel(previous);
               } else if ("previousSibling".equals(key)) {
                  previous = this.node.getPreviousSibling();
                  result = previous == null ? null : new DOMNodeModel(previous);
               } else {
                  Element previous;
                  if ("nextSiblingElement".equals(key)) {
                     previous = nextSiblingElement(this.node);
                     result = previous == null ? null : new DOMNodeModel(previous);
                  } else if ("previousSiblingElement".equals(key)) {
                     previous = previousSiblingElement(this.node);
                     result = previous == null ? null : new DOMNodeModel(previous);
                  } else if ("nextElement".equals(key)) {
                     previous = nextElement(this.node);
                     result = previous == null ? null : new DOMNodeModel(previous);
                  } else if ("previousElement".equals(key)) {
                     previous = previousElement(this.node);
                     result = previous == null ? null : new DOMNodeModel(previous);
                  } else if ("text".equals(key)) {
                     result = new SimpleScalar(getText(this.node));
                  }
               }
            }
         } else {
            NamedNodeMap attributes = this.node.getAttributes();
            if (attributes != null) {
               SimpleHash hash = new SimpleHash(_TemplateAPI.SAFE_OBJECT_WRAPPER);

               for(int i = 0; i < attributes.getLength(); ++i) {
                  Attr att = (Attr)attributes.item(i);
                  hash.put(att.getName(), att.getValue());
               }

               result = hash;
            }
         }

         this.cache.put(key, result);
      }

      return (TemplateModel)result;
   }

   public boolean isEmpty() {
      return false;
   }

   private static String getText(Node node) {
      String result = "";
      if (node instanceof Text) {
         result = ((Text)node).getData();
      } else if (node instanceof Element) {
         NodeList children = node.getChildNodes();

         for(int i = 0; i < children.getLength(); ++i) {
            result = result + getText(children.item(i));
         }
      }

      return result;
   }

   private static Element nextSiblingElement(Node node) {
      Node next = node;

      do {
         if (next == null) {
            return null;
         }

         next = next.getNextSibling();
      } while(!(next instanceof Element));

      return (Element)next;
   }

   private static Element previousSiblingElement(Node node) {
      Node previous = node;

      do {
         if (previous == null) {
            return null;
         }

         previous = previous.getPreviousSibling();
      } while(!(previous instanceof Element));

      return (Element)previous;
   }

   private static Element nextElement(Node node) {
      if (node.hasChildNodes()) {
         NodeList children = node.getChildNodes();

         for(int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child instanceof Element) {
               return (Element)child;
            }
         }
      }

      Element nextSiblingElement = nextSiblingElement(node);
      if (nextSiblingElement != null) {
         return nextSiblingElement;
      } else {
         for(Node parent = node.getParentNode(); parent instanceof Element; parent = parent.getParentNode()) {
            Element next = nextSiblingElement(parent);
            if (next != null) {
               return next;
            }
         }

         return null;
      }
   }

   private static Element previousElement(Node node) {
      Element result = previousSiblingElement(node);
      if (result != null) {
         return result;
      } else {
         Node parent = node.getParentNode();
         return parent instanceof Element ? (Element)parent : null;
      }
   }

   void setParent(DOMNodeModel parent) {
      if (parent != null) {
         this.cache.put("parent", parent);
      }

   }

   String getNodeName() {
      return this.node.getNodeName();
   }

   static {
      equivalenceTable.put("*", "children");
      equivalenceTable.put("@*", "attributes");
   }

   class NodeListTM implements TemplateSequenceModel, TemplateMethodModel {
      private NodeList nodeList;
      private TemplateModel[] nodes;

      NodeListTM(NodeList nodeList) {
         this.nodeList = nodeList;
         this.nodes = new TemplateModel[nodeList.getLength()];
      }

      public TemplateModel get(int index) {
         DOMNodeModel result = (DOMNodeModel)this.nodes[index];
         if (result == null) {
            result = new DOMNodeModel(this.nodeList.item(index));
            this.nodes[index] = result;
            result.setParent(DOMNodeModel.this);
         }

         return result;
      }

      public int size() {
         return this.nodes.length;
      }

      public Object exec(List arguments) throws TemplateModelException {
         if (arguments.size() != 1) {
            throw new TemplateModelException("Expecting exactly one string argument here");
         } else if (!(DOMNodeModel.this.node instanceof Element)) {
            throw new TemplateModelException("Expecting element here.");
         } else {
            Element elem = (Element)DOMNodeModel.this.node;
            return DOMNodeModel.this.new NodeListTM(elem.getElementsByTagName((String)arguments.get(0)));
         }
      }
   }

   class AncestorByName implements TemplateMethodModel {
      public Object exec(List arguments) throws TemplateModelException {
         if (arguments.size() != 1) {
            throw new TemplateModelException("Expecting exactly one string argument here");
         } else {
            String nodeName = (String)arguments.get(0);

            for(DOMNodeModel ancestor = (DOMNodeModel)DOMNodeModel.this.get("parent"); ancestor != null; ancestor = (DOMNodeModel)ancestor.get("parent")) {
               if (nodeName.equals(ancestor.getNodeName())) {
                  return ancestor;
               }
            }

            return null;
         }
      }
   }
}
