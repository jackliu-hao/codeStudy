package freemarker.template.utility;

import freemarker.core.Environment;
import freemarker.core.Macro;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core._CoreAPI;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BooleanModel;
import freemarker.ext.beans.CollectionModel;
import freemarker.ext.beans.DateModel;
import freemarker.ext.beans.EnumerationModel;
import freemarker.ext.beans.IteratorModel;
import freemarker.ext.beans.MapModel;
import freemarker.ext.beans.NumberModel;
import freemarker.ext.beans.OverloadedMethodsModel;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.ext.beans.StringModel;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNodeModelEx;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ClassUtil {
   private static final Map<String, Class<?>> PRIMITIVE_CLASSES_BY_NAME = new HashMap();

   private ClassUtil() {
   }

   public static Class forName(String className) throws ClassNotFoundException {
      try {
         ClassLoader ctcl = Thread.currentThread().getContextClassLoader();
         if (ctcl != null) {
            return Class.forName(className, true, ctcl);
         }
      } catch (ClassNotFoundException var2) {
      } catch (SecurityException var3) {
      }

      return Class.forName(className);
   }

   public static Class<?> resolveIfPrimitiveTypeName(String typeName) {
      return (Class)PRIMITIVE_CLASSES_BY_NAME.get(typeName);
   }

   public static Class<?> getArrayClass(Class<?> elementType, int dimensions) {
      return dimensions == 0 ? elementType : Array.newInstance(elementType, new int[dimensions]).getClass();
   }

   public static String getShortClassName(Class pClass) {
      return getShortClassName(pClass, false);
   }

   public static String getShortClassName(Class pClass, boolean shortenFreeMarkerClasses) {
      if (pClass == null) {
         return null;
      } else if (pClass.isArray()) {
         return getShortClassName(pClass.getComponentType()) + "[]";
      } else {
         String cn = pClass.getName();
         if (!cn.startsWith("java.lang.") && !cn.startsWith("java.util.")) {
            if (shortenFreeMarkerClasses) {
               if (cn.startsWith("freemarker.template.")) {
                  return "f.t" + cn.substring(19);
               }

               if (cn.startsWith("freemarker.ext.beans.")) {
                  return "f.e.b" + cn.substring(20);
               }

               if (cn.startsWith("freemarker.core.")) {
                  return "f.c" + cn.substring(15);
               }

               if (cn.startsWith("freemarker.ext.")) {
                  return "f.e" + cn.substring(14);
               }

               if (cn.startsWith("freemarker.")) {
                  return "f" + cn.substring(10);
               }
            }

            return cn;
         } else {
            return cn.substring(10);
         }
      }
   }

   public static String getShortClassNameOfObject(Object obj) {
      return getShortClassNameOfObject(obj, false);
   }

   public static String getShortClassNameOfObject(Object obj, boolean shortenFreeMarkerClasses) {
      return obj == null ? "Null" : getShortClassName(obj.getClass(), shortenFreeMarkerClasses);
   }

   private static Class getPrimaryTemplateModelInterface(TemplateModel tm) {
      if (tm instanceof BeanModel) {
         if (tm instanceof CollectionModel) {
            return TemplateSequenceModel.class;
         } else if (!(tm instanceof IteratorModel) && !(tm instanceof EnumerationModel)) {
            if (tm instanceof MapModel) {
               return TemplateHashModelEx.class;
            } else if (tm instanceof NumberModel) {
               return TemplateNumberModel.class;
            } else if (tm instanceof BooleanModel) {
               return TemplateBooleanModel.class;
            } else if (tm instanceof DateModel) {
               return TemplateDateModel.class;
            } else if (tm instanceof StringModel) {
               Object wrapped = ((BeanModel)tm).getWrappedObject();
               return wrapped instanceof String ? TemplateScalarModel.class : (tm instanceof TemplateHashModelEx ? TemplateHashModelEx.class : null);
            } else {
               return null;
            }
         } else {
            return TemplateCollectionModel.class;
         }
      } else if (!(tm instanceof SimpleMethodModel) && !(tm instanceof OverloadedMethodsModel)) {
         return tm instanceof TemplateCollectionModel && _CoreAPI.isLazilyGeneratedSequenceModel((TemplateCollectionModel)tm) ? TemplateSequenceModel.class : null;
      } else {
         return TemplateMethodModelEx.class;
      }
   }

   private static void appendTemplateModelTypeName(StringBuilder sb, Set typeNamesAppended, Class cl) {
      int initalLength = sb.length();
      if (TemplateNodeModelEx.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "extended node");
      } else if (TemplateNodeModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "node");
      }

      if (TemplateDirectiveModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "directive");
      } else if (TemplateTransformModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "transform");
      }

      if (TemplateSequenceModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "sequence");
      } else if (TemplateCollectionModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, TemplateCollectionModelEx.class.isAssignableFrom(cl) ? "extended_collection" : "collection");
      } else if (TemplateModelIterator.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "iterator");
      }

      if (TemplateMethodModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "method");
      }

      if (Environment.Namespace.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "namespace");
      } else if (TemplateHashModelEx.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "extended_hash");
      } else if (TemplateHashModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "hash");
      }

      if (TemplateNumberModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "number");
      }

      if (TemplateDateModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "date_or_time_or_datetime");
      }

      if (TemplateBooleanModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "boolean");
      }

      if (TemplateScalarModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "string");
      }

      if (TemplateMarkupOutputModel.class.isAssignableFrom(cl)) {
         appendTypeName(sb, typeNamesAppended, "markup_output");
      }

      if (sb.length() == initalLength) {
         appendTypeName(sb, typeNamesAppended, "misc_template_model");
      }

   }

   private static Class getUnwrappedClass(TemplateModel tm) {
      Object unwrapped;
      try {
         if (tm instanceof WrapperTemplateModel) {
            unwrapped = ((WrapperTemplateModel)tm).getWrappedObject();
         } else if (tm instanceof AdapterTemplateModel) {
            unwrapped = ((AdapterTemplateModel)tm).getAdaptedObject(Object.class);
         } else {
            unwrapped = null;
         }
      } catch (Throwable var3) {
         unwrapped = null;
      }

      return unwrapped != null ? unwrapped.getClass() : null;
   }

   private static void appendTypeName(StringBuilder sb, Set typeNamesAppended, String name) {
      if (!typeNamesAppended.contains(name)) {
         if (sb.length() != 0) {
            sb.append("+");
         }

         sb.append(name);
         typeNamesAppended.add(name);
      }

   }

   public static String getFTLTypeDescription(TemplateModel tm) {
      if (tm == null) {
         return "Null";
      } else {
         Set typeNamesAppended = new HashSet();
         StringBuilder sb = new StringBuilder();
         Class primaryInterface = getPrimaryTemplateModelInterface(tm);
         if (primaryInterface != null) {
            appendTemplateModelTypeName(sb, typeNamesAppended, primaryInterface);
         }

         if (tm instanceof Macro) {
            appendTypeName(sb, typeNamesAppended, ((Macro)tm).isFunction() ? "function" : "macro");
         }

         appendTemplateModelTypeName(sb, typeNamesAppended, tm.getClass());
         Class unwrappedClass = getUnwrappedClass(tm);
         String javaClassName;
         if (unwrappedClass != null) {
            javaClassName = getShortClassName(unwrappedClass, true);
         } else {
            javaClassName = null;
         }

         sb.append(" (");
         String modelClassName = getShortClassName(tm.getClass(), true);
         if (javaClassName == null) {
            sb.append("wrapper: ");
            sb.append(modelClassName);
         } else {
            sb.append(javaClassName);
            sb.append(" wrapped into ");
            sb.append(modelClassName);
         }

         sb.append(")");
         return sb.toString();
      }
   }

   public static Class primitiveClassToBoxingClass(Class primitiveClass) {
      if (primitiveClass == Integer.TYPE) {
         return Integer.class;
      } else if (primitiveClass == Boolean.TYPE) {
         return Boolean.class;
      } else if (primitiveClass == Long.TYPE) {
         return Long.class;
      } else if (primitiveClass == Double.TYPE) {
         return Double.class;
      } else if (primitiveClass == Character.TYPE) {
         return Character.class;
      } else if (primitiveClass == Float.TYPE) {
         return Float.class;
      } else if (primitiveClass == Byte.TYPE) {
         return Byte.class;
      } else if (primitiveClass == Short.TYPE) {
         return Short.class;
      } else {
         return primitiveClass == Void.TYPE ? Void.class : primitiveClass;
      }
   }

   public static Class boxingClassToPrimitiveClass(Class boxingClass) {
      if (boxingClass == Integer.class) {
         return Integer.TYPE;
      } else if (boxingClass == Boolean.class) {
         return Boolean.TYPE;
      } else if (boxingClass == Long.class) {
         return Long.TYPE;
      } else if (boxingClass == Double.class) {
         return Double.TYPE;
      } else if (boxingClass == Character.class) {
         return Character.TYPE;
      } else if (boxingClass == Float.class) {
         return Float.TYPE;
      } else if (boxingClass == Byte.class) {
         return Byte.TYPE;
      } else if (boxingClass == Short.class) {
         return Short.TYPE;
      } else {
         return boxingClass == Void.class ? Void.TYPE : boxingClass;
      }
   }

   public static boolean isNumerical(Class type) {
      return Number.class.isAssignableFrom(type) || type.isPrimitive() && type != Boolean.TYPE && type != Character.TYPE && type != Void.TYPE;
   }

   public static InputStream getReasourceAsStream(Class<?> baseClass, String resource, boolean optional) throws IOException {
      InputStream ins;
      try {
         ins = baseClass.getResourceAsStream(resource);
      } catch (Exception var6) {
         URL url = baseClass.getResource(resource);
         ins = url != null ? url.openStream() : null;
      }

      if (!optional) {
         checkInputStreamNotNull(ins, baseClass, resource);
      }

      return ins;
   }

   public static InputStream getReasourceAsStream(ClassLoader classLoader, String resource, boolean optional) throws IOException {
      InputStream ins;
      try {
         ins = classLoader.getResourceAsStream(resource);
      } catch (Exception var6) {
         URL url = classLoader.getResource(resource);
         ins = url != null ? url.openStream() : null;
      }

      if (ins == null && !optional) {
         throw new IOException("Class-loader resource not found (shown quoted): " + StringUtil.jQuote(resource) + ". The base ClassLoader was: " + classLoader);
      } else {
         return ins;
      }
   }

   public static Properties loadProperties(Class<?> baseClass, String resource) throws IOException {
      Properties props = new Properties();
      InputStream ins = null;

      try {
         try {
            ins = baseClass.getResourceAsStream(resource);
         } catch (Exception var31) {
            throw new MaybeZipFileClosedException();
         }

         checkInputStreamNotNull(ins, baseClass, resource);

         try {
            props.load(ins);
         } catch (Exception var29) {
            throw new MaybeZipFileClosedException();
         } finally {
            try {
               ins.close();
            } catch (Exception var28) {
            }

            ins = null;
         }
      } catch (MaybeZipFileClosedException var32) {
         URL url = baseClass.getResource(resource);
         ins = url != null ? url.openStream() : null;
         checkInputStreamNotNull(ins, baseClass, resource);
         props.load(ins);
      } finally {
         if (ins != null) {
            try {
               ins.close();
            } catch (Exception var27) {
            }
         }

      }

      return props;
   }

   private static void checkInputStreamNotNull(InputStream ins, Class<?> baseClass, String resource) throws IOException {
      if (ins == null) {
         throw new IOException("Class-loader resource not found (shown quoted): " + StringUtil.jQuote(resource) + ". The base class was " + baseClass.getName() + ".");
      }
   }

   static {
      PRIMITIVE_CLASSES_BY_NAME.put("boolean", Boolean.TYPE);
      PRIMITIVE_CLASSES_BY_NAME.put("byte", Byte.TYPE);
      PRIMITIVE_CLASSES_BY_NAME.put("char", Character.TYPE);
      PRIMITIVE_CLASSES_BY_NAME.put("short", Short.TYPE);
      PRIMITIVE_CLASSES_BY_NAME.put("int", Integer.TYPE);
      PRIMITIVE_CLASSES_BY_NAME.put("long", Long.TYPE);
      PRIMITIVE_CLASSES_BY_NAME.put("float", Float.TYPE);
      PRIMITIVE_CLASSES_BY_NAME.put("double", Double.TYPE);
   }

   private static class MaybeZipFileClosedException extends Exception {
      private MaybeZipFileClosedException() {
      }

      // $FF: synthetic method
      MaybeZipFileClosedException(Object x0) {
         this();
      }
   }
}
