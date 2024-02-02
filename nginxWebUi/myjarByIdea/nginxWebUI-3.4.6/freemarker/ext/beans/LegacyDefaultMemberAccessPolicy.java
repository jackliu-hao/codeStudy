package freemarker.ext.beans;

import freemarker.template.utility.ClassUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public final class LegacyDefaultMemberAccessPolicy implements MemberAccessPolicy {
   public static final LegacyDefaultMemberAccessPolicy INSTANCE = new LegacyDefaultMemberAccessPolicy();
   private static final String UNSAFE_METHODS_PROPERTIES = "unsafeMethods.properties";
   private static final Set<Method> UNSAFE_METHODS = createUnsafeMethodsSet();
   private static final BlacklistClassMemberAccessPolicy CLASS_MEMBER_ACCESS_POLICY_INSTANCE = new BlacklistClassMemberAccessPolicy();

   private static Set<Method> createUnsafeMethodsSet() {
      try {
         Properties props = ClassUtil.loadProperties(BeansWrapper.class, "unsafeMethods.properties");
         Set<Method> set = new HashSet(props.size() * 4 / 3, 1.0F);
         Iterator var2 = props.keySet().iterator();

         while(var2.hasNext()) {
            Object key = var2.next();

            try {
               set.add(parseMethodSpec((String)key));
            } catch (NoSuchMethodException | ClassNotFoundException var5) {
               if (ClassIntrospector.DEVELOPMENT_MODE) {
                  throw var5;
               }
            }
         }

         return set;
      } catch (Exception var6) {
         throw new RuntimeException("Could not load unsafe method set", var6);
      }
   }

   private static Method parseMethodSpec(String methodSpec) throws ClassNotFoundException, NoSuchMethodException {
      int brace = methodSpec.indexOf(40);
      int dot = methodSpec.lastIndexOf(46, brace);
      Class<?> clazz = ClassUtil.forName(methodSpec.substring(0, dot));
      String methodName = methodSpec.substring(dot + 1, brace);
      String argSpec = methodSpec.substring(brace + 1, methodSpec.length() - 1);
      StringTokenizer tok = new StringTokenizer(argSpec, ",");
      int argcount = tok.countTokens();
      Class<?>[] argTypes = new Class[argcount];

      for(int i = 0; i < argcount; ++i) {
         String argClassName = tok.nextToken();
         argTypes[i] = ClassUtil.resolveIfPrimitiveTypeName(argClassName);
         if (argTypes[i] == null) {
            argTypes[i] = ClassUtil.forName(argClassName);
         }
      }

      return clazz.getMethod(methodName, argTypes);
   }

   private LegacyDefaultMemberAccessPolicy() {
   }

   public ClassMemberAccessPolicy forClass(Class<?> containingClass) {
      return CLASS_MEMBER_ACCESS_POLICY_INSTANCE;
   }

   public boolean isToStringAlwaysExposed() {
      return true;
   }

   private static class BlacklistClassMemberAccessPolicy implements ClassMemberAccessPolicy {
      private BlacklistClassMemberAccessPolicy() {
      }

      public boolean isMethodExposed(Method method) {
         return !LegacyDefaultMemberAccessPolicy.UNSAFE_METHODS.contains(method);
      }

      public boolean isConstructorExposed(Constructor<?> constructor) {
         return true;
      }

      public boolean isFieldExposed(Field field) {
         return true;
      }

      // $FF: synthetic method
      BlacklistClassMemberAccessPolicy(Object x0) {
         this();
      }
   }
}
