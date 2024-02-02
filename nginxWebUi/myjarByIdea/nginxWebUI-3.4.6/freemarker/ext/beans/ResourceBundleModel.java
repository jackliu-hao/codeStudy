package freemarker.ext.beans;

import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

public class ResourceBundleModel extends BeanModel implements TemplateMethodModelEx {
   static final ModelFactory FACTORY = new ModelFactory() {
      public TemplateModel create(Object object, ObjectWrapper wrapper) {
         return new ResourceBundleModel((ResourceBundle)object, (BeansWrapper)wrapper);
      }
   };
   private Hashtable formats = null;

   public ResourceBundleModel(ResourceBundle bundle, BeansWrapper wrapper) {
      super(bundle, wrapper);
   }

   protected TemplateModel invokeGenericGet(Map keyMap, Class clazz, String key) throws TemplateModelException {
      try {
         return this.wrap(((ResourceBundle)this.object).getObject(key));
      } catch (MissingResourceException var5) {
         throw new _TemplateModelException(var5, new Object[]{"No ", new _DelayedJQuote(key), " key in the ResourceBundle. Note that conforming to the ResourceBundle Java API, this is an error and not just a missing sub-variable (a null)."});
      }
   }

   public boolean isEmpty() {
      return !((ResourceBundle)this.object).getKeys().hasMoreElements() && super.isEmpty();
   }

   public int size() {
      return this.keySet().size();
   }

   protected Set keySet() {
      Set set = super.keySet();
      Enumeration e = ((ResourceBundle)this.object).getKeys();

      while(e.hasMoreElements()) {
         set.add(e.nextElement());
      }

      return set;
   }

   public Object exec(List arguments) throws TemplateModelException {
      if (arguments.size() < 1) {
         throw new TemplateModelException("No message key was specified");
      } else {
         Iterator it = arguments.iterator();
         String key = this.unwrap((TemplateModel)it.next()).toString();

         try {
            if (!it.hasNext()) {
               return this.wrap(((ResourceBundle)this.object).getObject(key));
            } else {
               int args = arguments.size() - 1;
               Object[] params = new Object[args];

               for(int i = 0; i < args; ++i) {
                  params[i] = this.unwrap((TemplateModel)it.next());
               }

               return new StringModel(this.format(key, params), this.wrapper);
            }
         } catch (MissingResourceException var7) {
            throw new TemplateModelException("No such key: " + key);
         } catch (Exception var8) {
            throw new TemplateModelException(var8.getMessage());
         }
      }
   }

   public String format(String key, Object[] params) throws MissingResourceException {
      if (this.formats == null) {
         this.formats = new Hashtable();
      }

      MessageFormat format = null;
      format = (MessageFormat)this.formats.get(key);
      if (format == null) {
         format = new MessageFormat(((ResourceBundle)this.object).getString(key));
         format.setLocale(this.getBundle().getLocale());
         this.formats.put(key, format);
      }

      synchronized(format) {
         return format.format(params);
      }
   }

   public ResourceBundle getBundle() {
      return (ResourceBundle)this.object;
   }
}
