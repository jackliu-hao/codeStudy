package freemarker.ext.beans;

import freemarker.log.Logger;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class StaticModel implements TemplateHashModelEx {
   private static final Logger LOG = Logger.getLogger("freemarker.beans");
   private final Class<?> clazz;
   private final BeansWrapper wrapper;
   private final Map<String, Object> map = new HashMap();

   StaticModel(Class<?> clazz, BeansWrapper wrapper) throws TemplateModelException {
      this.clazz = clazz;
      this.wrapper = wrapper;
      this.populate();
   }

   public TemplateModel get(String key) throws TemplateModelException {
      Object model = this.map.get(key);
      if (model instanceof TemplateModel) {
         return (TemplateModel)model;
      } else if (model instanceof Field) {
         try {
            return this.wrapper.readField((Object)null, (Field)model);
         } catch (IllegalAccessException var4) {
            throw new TemplateModelException("Illegal access for field " + key + " of class " + this.clazz.getName());
         }
      } else {
         throw new TemplateModelException("No such key: " + key + " in class " + this.clazz.getName());
      }
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public int size() {
      return this.map.size();
   }

   public TemplateCollectionModel keys() throws TemplateModelException {
      return (TemplateCollectionModel)this.wrapper.getOuterIdentity().wrap(this.map.keySet());
   }

   public TemplateCollectionModel values() throws TemplateModelException {
      return (TemplateCollectionModel)this.wrapper.getOuterIdentity().wrap(this.map.values());
   }

   private void populate() throws TemplateModelException {
      if (!Modifier.isPublic(this.clazz.getModifiers())) {
         throw new TemplateModelException("Can't wrap the non-public class " + this.clazz.getName());
      } else if (this.wrapper.getExposureLevel() != 3) {
         ClassMemberAccessPolicy effClassMemberAccessPolicy = this.wrapper.getClassIntrospector().getEffectiveMemberAccessPolicy().forClass(this.clazz);
         Field[] fields = this.clazz.getFields();
         Field[] var3 = fields;
         int i = fields.length;

         for(int var5 = 0; var5 < i; ++var5) {
            Field field = var3[var5];
            int mod = field.getModifiers();
            if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && effClassMemberAccessPolicy.isFieldExposed(field)) {
               if (Modifier.isFinal(mod)) {
                  try {
                     this.map.put(field.getName(), this.wrapper.readField((Object)null, field));
                  } catch (IllegalAccessException var10) {
                  }
               } else {
                  this.map.put(field.getName(), field);
               }
            }
         }

         if (this.wrapper.getExposureLevel() < 2) {
            Method[] methods = this.clazz.getMethods();

            for(i = 0; i < methods.length; ++i) {
               Method method = methods[i];
               int mod = method.getModifiers();
               if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && effClassMemberAccessPolicy.isMethodExposed(method)) {
                  String name = method.getName();
                  Object obj = this.map.get(name);
                  OverloadedMethods overloadedMethods;
                  if (obj instanceof Method) {
                     overloadedMethods = new OverloadedMethods(this.wrapper.is2321Bugfixed());
                     overloadedMethods.addMethod((Method)obj);
                     overloadedMethods.addMethod(method);
                     this.map.put(name, overloadedMethods);
                  } else if (obj instanceof OverloadedMethods) {
                     overloadedMethods = (OverloadedMethods)obj;
                     overloadedMethods.addMethod(method);
                  } else {
                     if (obj != null && LOG.isInfoEnabled()) {
                        LOG.info("Overwriting value [" + obj + "] for  key '" + name + "' with [" + method + "] in static model for " + this.clazz.getName());
                     }

                     this.map.put(name, method);
                  }
               }
            }

            Iterator<Map.Entry<String, Object>> entries = this.map.entrySet().iterator();

            while(entries.hasNext()) {
               Map.Entry<String, Object> entry = (Map.Entry)entries.next();
               Object value = entry.getValue();
               if (value instanceof Method) {
                  Method method = (Method)value;
                  entry.setValue(new SimpleMethodModel((Object)null, method, method.getParameterTypes(), this.wrapper));
               } else if (value instanceof OverloadedMethods) {
                  entry.setValue(new OverloadedMethodsModel((Object)null, (OverloadedMethods)value, this.wrapper));
               }
            }
         }

      }
   }
}
