package freemarker.ext.beans;

import freemarker.core.CollectionAndSequence;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.util.ModelFactory;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.log.Logger;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.StringUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanModel implements TemplateHashModelEx, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport {
   private static final Logger LOG = Logger.getLogger("freemarker.beans");
   protected final Object object;
   protected final BeansWrapper wrapper;
   static final TemplateModel UNKNOWN = new SimpleScalar("UNKNOWN");
   static final ModelFactory FACTORY = new ModelFactory() {
      public TemplateModel create(Object object, ObjectWrapper wrapper) {
         return new BeanModel(object, (BeansWrapper)wrapper);
      }
   };
   private HashMap<Object, TemplateModel> memberCache;

   public BeanModel(Object object, BeansWrapper wrapper) {
      this(object, wrapper, true);
   }

   BeanModel(Object object, BeansWrapper wrapper, boolean inrospectNow) {
      this.object = object;
      this.wrapper = wrapper;
      if (inrospectNow && object != null) {
         wrapper.getClassIntrospector().get(object.getClass());
      }

   }

   public TemplateModel get(String key) throws TemplateModelException {
      Class<?> clazz = this.object.getClass();
      Map<Object, Object> classInfo = this.wrapper.getClassIntrospector().get(clazz);
      TemplateModel retval = null;

      try {
         if (this.wrapper.isMethodsShadowItems()) {
            Object fd = classInfo.get(key);
            if (fd != null) {
               retval = this.invokeThroughDescriptor(fd, classInfo);
            } else {
               retval = this.invokeGenericGet(classInfo, clazz, key);
            }
         } else {
            TemplateModel model = this.invokeGenericGet(classInfo, clazz, key);
            TemplateModel nullModel = this.wrapper.wrap((Object)null);
            if (model != nullModel && model != UNKNOWN) {
               return model;
            }

            Object fd = classInfo.get(key);
            if (fd != null) {
               retval = this.invokeThroughDescriptor(fd, classInfo);
               if (retval == UNKNOWN && model == nullModel) {
                  retval = nullModel;
               }
            }
         }

         if (retval == UNKNOWN) {
            if (this.wrapper.isStrict()) {
               throw new InvalidPropertyException("No such bean property: " + key);
            }

            if (LOG.isDebugEnabled()) {
               this.logNoSuchKey(key, classInfo);
            }

            retval = this.wrapper.wrap((Object)null);
         }

         return retval;
      } catch (TemplateModelException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new _TemplateModelException(var9, new Object[]{"An error has occurred when reading existing sub-variable ", new _DelayedJQuote(key), "; see cause exception! The type of the containing value was: ", new _DelayedFTLTypeDescription(this)});
      }
   }

   private void logNoSuchKey(String key, Map<?, ?> keyMap) {
      LOG.debug("Key " + StringUtil.jQuoteNoXSS(key) + " was not found on instance of " + this.object.getClass().getName() + ". Introspection information for the class is: " + keyMap);
   }

   protected boolean hasPlainGetMethod() {
      return this.wrapper.getClassIntrospector().get(this.object.getClass()).get(ClassIntrospector.GENERIC_GET_KEY) != null;
   }

   private TemplateModel invokeThroughDescriptor(Object desc, Map<Object, Object> classInfo) throws IllegalAccessException, InvocationTargetException, TemplateModelException {
      Object cachedModel;
      synchronized(this) {
         cachedModel = this.memberCache != null ? (TemplateModel)this.memberCache.get(desc) : null;
      }

      if (cachedModel != null) {
         return (TemplateModel)cachedModel;
      } else {
         TemplateModel resultModel = UNKNOWN;
         if (desc instanceof FastPropertyDescriptor) {
            FastPropertyDescriptor pd = (FastPropertyDescriptor)desc;
            Method indexedReadMethod = pd.getIndexedReadMethod();
            if (indexedReadMethod != null) {
               if (!this.wrapper.getPreferIndexedReadMethod() && pd.getReadMethod() != null) {
                  resultModel = this.wrapper.invokeMethod(this.object, pd.getReadMethod(), (Object[])null);
               } else {
                  resultModel = cachedModel = new SimpleMethodModel(this.object, indexedReadMethod, ClassIntrospector.getArgTypes(classInfo, indexedReadMethod), this.wrapper);
               }
            } else {
               resultModel = this.wrapper.invokeMethod(this.object, pd.getReadMethod(), (Object[])null);
            }
         } else if (desc instanceof Field) {
            resultModel = this.wrapper.readField(this.object, (Field)desc);
         } else if (desc instanceof Method) {
            Method method = (Method)desc;
            resultModel = cachedModel = new SimpleMethodModel(this.object, method, ClassIntrospector.getArgTypes(classInfo, method), this.wrapper);
         } else if (desc instanceof OverloadedMethods) {
            resultModel = cachedModel = new OverloadedMethodsModel(this.object, (OverloadedMethods)desc, this.wrapper);
         }

         if (cachedModel != null) {
            synchronized(this) {
               if (this.memberCache == null) {
                  this.memberCache = new HashMap();
               }

               this.memberCache.put(desc, cachedModel);
            }
         }

         return (TemplateModel)resultModel;
      }
   }

   void clearMemberCache() {
      synchronized(this) {
         this.memberCache = null;
      }
   }

   protected TemplateModel invokeGenericGet(Map classInfo, Class<?> clazz, String key) throws IllegalAccessException, InvocationTargetException, TemplateModelException {
      Method genericGet = (Method)classInfo.get(ClassIntrospector.GENERIC_GET_KEY);
      return genericGet == null ? UNKNOWN : this.wrapper.invokeMethod(this.object, genericGet, new Object[]{key});
   }

   protected TemplateModel wrap(Object obj) throws TemplateModelException {
      return this.wrapper.getOuterIdentity().wrap(obj);
   }

   protected Object unwrap(TemplateModel model) throws TemplateModelException {
      return this.wrapper.unwrap(model);
   }

   public boolean isEmpty() {
      if (this.object instanceof String) {
         return ((String)this.object).length() == 0;
      } else if (this.object instanceof Collection) {
         return ((Collection)this.object).isEmpty();
      } else if (this.object instanceof Iterator && this.wrapper.is2324Bugfixed()) {
         return !((Iterator)this.object).hasNext();
      } else if (this.object instanceof Map) {
         return ((Map)this.object).isEmpty();
      } else {
         return this.object == null || Boolean.FALSE.equals(this.object);
      }
   }

   public Object getAdaptedObject(Class<?> hint) {
      return this.object;
   }

   public Object getWrappedObject() {
      return this.object;
   }

   public int size() {
      return this.wrapper.getClassIntrospector().keyCount(this.object.getClass());
   }

   public TemplateCollectionModel keys() {
      return new CollectionAndSequence(new SimpleSequence(this.keySet(), this.wrapper));
   }

   public TemplateCollectionModel values() throws TemplateModelException {
      List<Object> values = new ArrayList(this.size());
      TemplateModelIterator it = this.keys().iterator();

      while(it.hasNext()) {
         String key = ((TemplateScalarModel)it.next()).getAsString();
         values.add(this.get(key));
      }

      return new CollectionAndSequence(new SimpleSequence(values, this.wrapper));
   }

   String getAsClassicCompatibleString() {
      if (this.object == null) {
         return "null";
      } else {
         String s = this.object.toString();
         return s != null ? s : "null";
      }
   }

   public String toString() {
      return this.object.toString();
   }

   protected Set keySet() {
      return this.wrapper.getClassIntrospector().keySet(this.object.getClass());
   }

   public TemplateModel getAPI() throws TemplateModelException {
      return this.wrapper.wrapAsAPI(this.object);
   }
}
