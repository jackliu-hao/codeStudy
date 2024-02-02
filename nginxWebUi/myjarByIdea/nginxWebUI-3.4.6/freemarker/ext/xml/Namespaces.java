package freemarker.ext.xml;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import java.util.HashMap;
import java.util.List;

class Namespaces implements TemplateMethodModel, Cloneable {
   private HashMap namespaces = new HashMap();
   private boolean shared;

   Namespaces() {
      this.namespaces.put("", "");
      this.namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
      this.shared = false;
   }

   public Object clone() {
      try {
         Namespaces clone = (Namespaces)super.clone();
         clone.namespaces = (HashMap)this.namespaces.clone();
         clone.shared = false;
         return clone;
      } catch (CloneNotSupportedException var2) {
         throw new Error();
      }
   }

   public String translateNamespacePrefixToUri(String prefix) {
      synchronized(this.namespaces) {
         return (String)this.namespaces.get(prefix);
      }
   }

   public Object exec(List arguments) throws TemplateModelException {
      if (arguments.size() != 2) {
         throw new TemplateModelException("_registerNamespace(prefix, uri) requires two arguments");
      } else {
         this.registerNamespace((String)arguments.get(0), (String)arguments.get(1));
         return TemplateScalarModel.EMPTY_STRING;
      }
   }

   void registerNamespace(String prefix, String uri) {
      synchronized(this.namespaces) {
         this.namespaces.put(prefix, uri);
      }
   }

   void markShared() {
      if (!this.shared) {
         this.shared = true;
      }

   }

   boolean isShared() {
      return this.shared;
   }
}
