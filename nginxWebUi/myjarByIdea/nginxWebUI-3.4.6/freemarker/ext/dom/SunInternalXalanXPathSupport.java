package freemarker.ext.dom;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XNull;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import freemarker.core.Environment;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

class SunInternalXalanXPathSupport implements XPathSupport {
   private XPathContext xpathContext = new XPathContext();
   private static final PrefixResolver CUSTOM_PREFIX_RESOLVER = new PrefixResolver() {
      public String getNamespaceForPrefix(String prefix, Node node) {
         return this.getNamespaceForPrefix(prefix);
      }

      public String getNamespaceForPrefix(String prefix) {
         return prefix.equals("D") ? Environment.getCurrentEnvironment().getDefaultNS() : Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
      }

      public String getBaseIdentifier() {
         return null;
      }

      public boolean handlesNullPrefixes() {
         return false;
      }
   };

   public synchronized TemplateModel executeQuery(Object context, String xpathQuery) throws TemplateModelException {
      if (!(context instanceof Node)) {
         if (context != null && !isNodeList(context)) {
            throw new TemplateModelException("Can't perform an XPath query against a " + context.getClass().getName() + ". Expecting a single org.w3c.dom.Node.");
         } else {
            int cnt = context != null ? ((List)context).size() : 0;
            throw new TemplateModelException((cnt != 0 ? "Xalan can't perform an XPath query against a Node Set (contains " + cnt + " node(s)). Expecting a single Node." : "Xalan can't perform an XPath query against an empty Node Set.") + " (There's no such restriction if you configure FreeMarker to use Jaxen for XPath.)");
         }
      } else {
         Node node = (Node)context;

         try {
            XPath xpath = new XPath(xpathQuery, (SourceLocator)null, CUSTOM_PREFIX_RESOLVER, 0, (ErrorListener)null);
            int ctxtNode = this.xpathContext.getDTMHandleFromNode(node);
            XObject xresult = xpath.execute(this.xpathContext, ctxtNode, CUSTOM_PREFIX_RESOLVER);
            if (!(xresult instanceof XNodeSet)) {
               if (xresult instanceof XBoolean) {
                  return ((XBoolean)xresult).bool() ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
               } else if (xresult instanceof XNull) {
                  return null;
               } else if (xresult instanceof XString) {
                  return new SimpleScalar(xresult.toString());
               } else if (xresult instanceof XNumber) {
                  return new SimpleNumber(((XNumber)xresult).num());
               } else {
                  throw new TemplateModelException("Cannot deal with type: " + xresult.getClass().getName());
               }
            } else {
               NodeListModel result = new NodeListModel(node);
               result.xpathSupport = this;
               NodeIterator nodeIterator = xresult.nodeset();

               Node n;
               do {
                  n = nodeIterator.nextNode();
                  if (n != null) {
                     result.add(n);
                  }
               } while(n != null);

               return (TemplateModel)(result.size() == 1 ? result.get(0) : result);
            }
         } catch (TransformerException var10) {
            throw new TemplateModelException(var10);
         }
      }
   }

   private static boolean isNodeList(Object context) {
      if (!(context instanceof List)) {
         return false;
      } else {
         List ls = (List)context;
         int ln = ls.size();

         for(int i = 0; i < ln; ++i) {
            if (!(ls.get(i) instanceof Node)) {
               return false;
            }
         }

         return true;
      }
   }
}
