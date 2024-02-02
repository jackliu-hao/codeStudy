package freemarker.core;

import freemarker.cache.AndMatcher;
import freemarker.cache.ConditionalTemplateConfigurationFactory;
import freemarker.cache.FileExtensionMatcher;
import freemarker.cache.FileNameGlobMatcher;
import freemarker.cache.FirstMatchTemplateConfigurationFactory;
import freemarker.cache.MergingTemplateConfigurationFactory;
import freemarker.cache.NotMatcher;
import freemarker.cache.OrMatcher;
import freemarker.cache.PathGlobMatcher;
import freemarker.cache.PathRegexMatcher;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;
import freemarker.template.utility.WriteProtectable;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class _ObjectBuilderSettingEvaluator {
   private static final String INSTANCE_FIELD_NAME = "INSTANCE";
   private static final String BUILD_METHOD_NAME = "build";
   private static final String BUILDER_CLASS_POSTFIX = "Builder";
   private static Map<String, String> SHORTHANDS;
   private static final Object VOID = new Object();
   private final String src;
   private final Class expectedClass;
   private final boolean allowNull;
   private final _SettingEvaluationEnvironment env;
   private int pos;
   private boolean modernMode = false;

   private _ObjectBuilderSettingEvaluator(String src, int pos, Class expectedClass, boolean allowNull, _SettingEvaluationEnvironment env) {
      this.src = src;
      this.pos = pos;
      this.expectedClass = expectedClass;
      this.allowNull = allowNull;
      this.env = env;
   }

   public static Object eval(String src, Class expectedClass, boolean allowNull, _SettingEvaluationEnvironment env) throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      return (new _ObjectBuilderSettingEvaluator(src, 0, expectedClass, allowNull, env)).eval();
   }

   public static int configureBean(String argumentListSrc, int posAfterOpenParen, Object bean, _SettingEvaluationEnvironment env) throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      return (new _ObjectBuilderSettingEvaluator(argumentListSrc, posAfterOpenParen, bean.getClass(), true, env)).configureBean(bean);
   }

   private Object eval() throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      this.skipWS();

      Object value;
      try {
         value = this.ensureEvaled(this.fetchValue(false, true, false, true));
      } catch (LegacyExceptionWrapperSettingEvaluationExpression var3) {
         var3.rethrowLegacy();
         value = null;
      }

      this.skipWS();
      if (this.pos != this.src.length()) {
         throw new _ObjectBuilderSettingEvaluationException("end-of-expression", this.src, this.pos);
      } else if (value == null && !this.allowNull) {
         throw new _ObjectBuilderSettingEvaluationException("Value can't be null.");
      } else if (value != null && !this.expectedClass.isInstance(value)) {
         throw new _ObjectBuilderSettingEvaluationException("The resulting object (of class " + value.getClass() + ") is not a(n) " + this.expectedClass.getName() + ".");
      } else {
         return value;
      }
   }

   private int configureBean(Object bean) throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      PropertyAssignmentsExpression propAssignments = new PropertyAssignmentsExpression(bean);
      this.fetchParameterListInto(propAssignments);
      this.skipWS();
      propAssignments.eval();
      return this.pos;
   }

   private Object ensureEvaled(Object value) throws _ObjectBuilderSettingEvaluationException {
      return value instanceof SettingExpression ? ((SettingExpression)value).eval() : value;
   }

   private Object fetchBuilderCall(boolean optional, boolean topLevel) throws _ObjectBuilderSettingEvaluationException {
      int startPos = this.pos;
      BuilderCallExpression exp = new BuilderCallExpression();
      exp.canBeStaticField = true;
      String fetchedClassName = this.fetchClassName(optional);
      if (fetchedClassName == null) {
         if (!optional) {
            throw new _ObjectBuilderSettingEvaluationException("class name", this.src, this.pos);
         } else {
            return VOID;
         }
      } else {
         exp.className = shorthandToFullQualified(fetchedClassName);
         if (!fetchedClassName.equals(exp.className)) {
            this.modernMode = true;
            exp.canBeStaticField = false;
         }

         this.skipWS();
         char openParen = this.fetchOptionalChar("(");
         if (openParen == 0 && !topLevel) {
            if (fetchedClassName.indexOf(46) == -1) {
               this.pos = startPos;
               return VOID;
            }

            exp.mustBeStaticField = true;
         }

         if (openParen != 0) {
            this.fetchParameterListInto(exp);
            exp.canBeStaticField = false;
         }

         return exp;
      }
   }

   private void fetchParameterListInto(ExpressionWithParameters exp) throws _ObjectBuilderSettingEvaluationException {
      this.modernMode = true;
      this.skipWS();
      if (this.fetchOptionalChar(")") != ')') {
         do {
            this.skipWS();
            Object paramNameOrValue = this.fetchValue(false, false, true, false);
            if (paramNameOrValue != VOID) {
               this.skipWS();
               if (paramNameOrValue instanceof Name) {
                  exp.namedParamNames.add(((Name)paramNameOrValue).name);
                  this.skipWS();
                  this.fetchRequiredChar("=");
                  this.skipWS();
                  Object paramValue = this.fetchValue(false, false, true, true);
                  exp.namedParamValues.add(this.ensureEvaled(paramValue));
               } else {
                  if (!exp.namedParamNames.isEmpty()) {
                     throw new _ObjectBuilderSettingEvaluationException("Positional parameters must precede named parameters");
                  }

                  if (!exp.getAllowPositionalParameters()) {
                     throw new _ObjectBuilderSettingEvaluationException("Positional parameters not supported here");
                  }

                  exp.positionalParamValues.add(this.ensureEvaled(paramNameOrValue));
               }

               this.skipWS();
            }
         } while(this.fetchRequiredChar(",)") == ',');
      }

   }

   private Object fetchValue(boolean optional, boolean topLevel, boolean resultCoerced, boolean resolveVariables) throws _ObjectBuilderSettingEvaluationException {
      if (this.pos < this.src.length()) {
         Object val = this.fetchNumberLike(true, resultCoerced);
         if (val != VOID) {
            return val;
         }

         val = this.fetchStringLiteral(true);
         if (val != VOID) {
            return val;
         }

         val = this.fetchListLiteral(true);
         if (val != VOID) {
            return val;
         }

         val = this.fetchMapLiteral(true);
         if (val != VOID) {
            return val;
         }

         val = this.fetchBuilderCall(true, topLevel);
         if (val != VOID) {
            return val;
         }

         String name = this.fetchSimpleName(true);
         if (name != null) {
            val = this.keywordToValueOrVoid(name);
            if (val != VOID) {
               return val;
            }

            if (resolveVariables) {
               throw new _ObjectBuilderSettingEvaluationException("Can't resolve variable reference: " + name);
            }

            return new Name(name);
         }
      }

      if (optional) {
         return VOID;
      } else {
         throw new _ObjectBuilderSettingEvaluationException("value or name", this.src, this.pos);
      }
   }

   private boolean isKeyword(String name) {
      return this.keywordToValueOrVoid(name) != VOID;
   }

   private Object keywordToValueOrVoid(String name) {
      if (name.equals("true")) {
         return Boolean.TRUE;
      } else if (name.equals("false")) {
         return Boolean.FALSE;
      } else {
         return name.equals("null") ? null : VOID;
      }
   }

   private String fetchSimpleName(boolean optional) throws _ObjectBuilderSettingEvaluationException {
      char c = this.pos < this.src.length() ? this.src.charAt(this.pos) : 0;
      if (!this.isIdentifierStart(c)) {
         if (optional) {
            return null;
         } else {
            throw new _ObjectBuilderSettingEvaluationException("class name", this.src, this.pos);
         }
      } else {
         int startPos;
         for(startPos = this.pos++; this.pos != this.src.length(); ++this.pos) {
            c = this.src.charAt(this.pos);
            if (!this.isIdentifierMiddle(c)) {
               break;
            }
         }

         return this.src.substring(startPos, this.pos);
      }
   }

   private String fetchClassName(boolean optional) throws _ObjectBuilderSettingEvaluationException {
      int startPos = this.pos;
      StringBuilder sb = new StringBuilder();

      while(true) {
         String className = this.fetchSimpleName(true);
         if (className == null) {
            if (!optional) {
               throw new _ObjectBuilderSettingEvaluationException("name", this.src, this.pos);
            }

            this.pos = startPos;
            return null;
         }

         sb.append(className);
         this.skipWS();
         if (this.pos >= this.src.length() || this.src.charAt(this.pos) != '.') {
            className = sb.toString();
            if (this.isKeyword(className)) {
               this.pos = startPos;
               return null;
            } else {
               return className;
            }
         }

         sb.append('.');
         ++this.pos;
         this.skipWS();
      }
   }

   private Object fetchNumberLike(boolean optional, boolean resultCoerced) throws _ObjectBuilderSettingEvaluationException {
      int startPos = this.pos;
      boolean isVersion = false;

      for(boolean hasDot = false; this.pos != this.src.length(); ++this.pos) {
         char c = this.src.charAt(this.pos);
         if (c == '.') {
            if (hasDot) {
               isVersion = true;
            } else {
               hasDot = true;
            }
         } else if (!this.isASCIIDigit(c) && c != '-') {
            break;
         }
      }

      if (startPos == this.pos) {
         if (optional) {
            return VOID;
         } else {
            throw new _ObjectBuilderSettingEvaluationException("number-like", this.src, this.pos);
         }
      } else {
         String numStr = this.src.substring(startPos, this.pos);
         if (isVersion) {
            try {
               return new Version(numStr);
            } catch (IllegalArgumentException var10) {
               throw new _ObjectBuilderSettingEvaluationException("Malformed version number: " + numStr, var10);
            }
         } else {
            String typePostfix;
            for(typePostfix = null; this.pos != this.src.length(); ++this.pos) {
               char c = this.src.charAt(this.pos);
               if (!Character.isLetter(c)) {
                  break;
               }

               if (typePostfix == null) {
                  typePostfix = String.valueOf(c);
               } else {
                  typePostfix = typePostfix + c;
               }
            }

            try {
               if (numStr.endsWith(".")) {
                  throw new NumberFormatException("A number can't end with a dot");
               } else if (!numStr.startsWith(".") && !numStr.startsWith("-.") && !numStr.startsWith("+.")) {
                  if (typePostfix == null) {
                     if (numStr.indexOf(46) == -1) {
                        BigInteger biNum = new BigInteger(numStr);
                        int bitLength = biNum.bitLength();
                        if (bitLength <= 31) {
                           return biNum.intValue();
                        } else {
                           return bitLength <= 63 ? biNum.longValue() : biNum;
                        }
                     } else {
                        return resultCoerced ? new BigDecimal(numStr) : Double.valueOf(numStr);
                     }
                  } else if (typePostfix.equalsIgnoreCase("l")) {
                     return Long.valueOf(numStr);
                  } else if (typePostfix.equalsIgnoreCase("bi")) {
                     return new BigInteger(numStr);
                  } else if (typePostfix.equalsIgnoreCase("bd")) {
                     return new BigDecimal(numStr);
                  } else if (typePostfix.equalsIgnoreCase("d")) {
                     return Double.valueOf(numStr);
                  } else if (typePostfix.equalsIgnoreCase("f")) {
                     return Float.valueOf(numStr);
                  } else {
                     throw new _ObjectBuilderSettingEvaluationException("Unrecognized number type postfix: " + typePostfix);
                  }
               } else {
                  throw new NumberFormatException("A number can't start with a dot");
               }
            } catch (NumberFormatException var11) {
               throw new _ObjectBuilderSettingEvaluationException("Malformed number: " + numStr, var11);
            }
         }
      }
   }

   private Object fetchStringLiteral(boolean optional) throws _ObjectBuilderSettingEvaluationException {
      int startPos = this.pos;
      char q = 0;
      boolean afterEscape = false;
      boolean raw = false;

      while(true) {
         if (this.pos == this.src.length()) {
            if (q != 0) {
               throw new _ObjectBuilderSettingEvaluationException(String.valueOf(q), this.src, this.pos);
            }
            break;
         }

         char c = this.src.charAt(this.pos);
         if (q == 0) {
            if (c == 'r' && this.pos + 1 < this.src.length()) {
               raw = true;
               c = this.src.charAt(this.pos + 1);
            }

            if (c == '\'') {
               q = '\'';
            } else {
               if (c != '"') {
                  break;
               }

               q = '"';
            }

            if (raw) {
               ++this.pos;
            }
         } else if (!afterEscape) {
            if (c == '\\' && !raw) {
               afterEscape = true;
            } else {
               if (c == q) {
                  break;
               }

               if (c == '{') {
                  char prevC = this.src.charAt(this.pos - 1);
                  if (prevC == '$' || prevC == '#') {
                     throw new _ObjectBuilderSettingEvaluationException("${...} and #{...} aren't allowed here.");
                  }
               }
            }
         } else {
            afterEscape = false;
         }

         ++this.pos;
      }

      if (startPos == this.pos) {
         if (optional) {
            return VOID;
         } else {
            throw new _ObjectBuilderSettingEvaluationException("string literal", this.src, this.pos);
         }
      } else {
         String sInside = this.src.substring(startPos + (raw ? 2 : 1), this.pos);

         try {
            ++this.pos;
            return raw ? sInside : StringUtil.FTLStringLiteralDec(sInside);
         } catch (ParseException var8) {
            throw new _ObjectBuilderSettingEvaluationException("Malformed string literal: " + sInside, var8);
         }
      }
   }

   private Object fetchListLiteral(boolean optional) throws _ObjectBuilderSettingEvaluationException {
      if (this.pos != this.src.length() && this.src.charAt(this.pos) == '[') {
         ++this.pos;
         ListExpression listExp = new ListExpression();

         while(true) {
            this.skipWS();
            if (this.fetchOptionalChar("]") != 0) {
               return listExp;
            }

            if (listExp.itemCount() != 0) {
               this.fetchRequiredChar(",");
               this.skipWS();
            }

            listExp.addItem(this.fetchValue(false, false, false, true));
            this.skipWS();
         }
      } else if (!optional) {
         throw new _ObjectBuilderSettingEvaluationException("[", this.src, this.pos);
      } else {
         return VOID;
      }
   }

   private Object fetchMapLiteral(boolean optional) throws _ObjectBuilderSettingEvaluationException {
      if (this.pos != this.src.length() && this.src.charAt(this.pos) == '{') {
         ++this.pos;
         MapExpression mapExp = new MapExpression();

         while(true) {
            this.skipWS();
            if (this.fetchOptionalChar("}") != 0) {
               return mapExp;
            }

            if (mapExp.itemCount() != 0) {
               this.fetchRequiredChar(",");
               this.skipWS();
            }

            Object key = this.fetchValue(false, false, false, true);
            this.skipWS();
            this.fetchRequiredChar(":");
            this.skipWS();
            Object value = this.fetchValue(false, false, false, true);
            mapExp.addItem(new KeyValuePair(key, value));
            this.skipWS();
         }
      } else if (!optional) {
         throw new _ObjectBuilderSettingEvaluationException("{", this.src, this.pos);
      } else {
         return VOID;
      }
   }

   private void skipWS() {
      while(this.pos != this.src.length()) {
         char c = this.src.charAt(this.pos);
         if (!Character.isWhitespace(c)) {
            return;
         }

         ++this.pos;
      }

   }

   private char fetchOptionalChar(String expectedChars) throws _ObjectBuilderSettingEvaluationException {
      return this.fetchChar(expectedChars, true);
   }

   private char fetchRequiredChar(String expectedChars) throws _ObjectBuilderSettingEvaluationException {
      return this.fetchChar(expectedChars, false);
   }

   private char fetchChar(String expectedChars, boolean optional) throws _ObjectBuilderSettingEvaluationException {
      char c = this.pos < this.src.length() ? this.src.charAt(this.pos) : 0;
      if (expectedChars.indexOf(c) != -1) {
         ++this.pos;
         return c;
      } else if (optional) {
         return '\u0000';
      } else {
         StringBuilder sb = new StringBuilder();

         for(int i = 0; i < expectedChars.length(); ++i) {
            if (i != 0) {
               sb.append(" or ");
            }

            sb.append(StringUtil.jQuote(expectedChars.substring(i, i + 1)));
         }

         throw new _ObjectBuilderSettingEvaluationException(sb.toString(), this.src, this.pos);
      }
   }

   private boolean isASCIIDigit(char c) {
      return c >= '0' && c <= '9';
   }

   private boolean isIdentifierStart(char c) {
      return Character.isLetter(c) || c == '_' || c == '$';
   }

   private boolean isIdentifierMiddle(char c) {
      return this.isIdentifierStart(c) || this.isASCIIDigit(c);
   }

   private static synchronized String shorthandToFullQualified(String className) {
      if (SHORTHANDS == null) {
         SHORTHANDS = new HashMap();
         addWithSimpleName(SHORTHANDS, DefaultObjectWrapper.class);
         addWithSimpleName(SHORTHANDS, BeansWrapper.class);
         addWithSimpleName(SHORTHANDS, SimpleObjectWrapper.class);
         addWithSimpleName(SHORTHANDS, TemplateConfiguration.class);
         addWithSimpleName(SHORTHANDS, PathGlobMatcher.class);
         addWithSimpleName(SHORTHANDS, FileNameGlobMatcher.class);
         addWithSimpleName(SHORTHANDS, FileExtensionMatcher.class);
         addWithSimpleName(SHORTHANDS, PathRegexMatcher.class);
         addWithSimpleName(SHORTHANDS, AndMatcher.class);
         addWithSimpleName(SHORTHANDS, OrMatcher.class);
         addWithSimpleName(SHORTHANDS, NotMatcher.class);
         addWithSimpleName(SHORTHANDS, ConditionalTemplateConfigurationFactory.class);
         addWithSimpleName(SHORTHANDS, MergingTemplateConfigurationFactory.class);
         addWithSimpleName(SHORTHANDS, FirstMatchTemplateConfigurationFactory.class);
         addWithSimpleName(SHORTHANDS, HTMLOutputFormat.class);
         addWithSimpleName(SHORTHANDS, XHTMLOutputFormat.class);
         addWithSimpleName(SHORTHANDS, XMLOutputFormat.class);
         addWithSimpleName(SHORTHANDS, RTFOutputFormat.class);
         addWithSimpleName(SHORTHANDS, PlainTextOutputFormat.class);
         addWithSimpleName(SHORTHANDS, UndefinedOutputFormat.class);
         addWithSimpleName(SHORTHANDS, DefaultTruncateBuiltinAlgorithm.class);
         addWithSimpleName(SHORTHANDS, Locale.class);
         SHORTHANDS.put("TimeZone", "freemarker.core._TimeZone");
         SHORTHANDS.put("markup", "freemarker.core._Markup");
         addWithSimpleName(SHORTHANDS, Configuration.class);
      }

      String fullClassName = (String)SHORTHANDS.get(className);
      return fullClassName == null ? className : fullClassName;
   }

   private static void addWithSimpleName(Map map, Class<?> pClass) {
      map.put(pClass.getSimpleName(), pClass.getName());
   }

   private void setJavaBeanProperties(Object bean, List namedParamNames, List namedParamValues) throws _ObjectBuilderSettingEvaluationException {
      if (!namedParamNames.isEmpty()) {
         Class cl = bean.getClass();

         HashMap beanPropSetters;
         int i;
         Method beanPropSetter;
         try {
            PropertyDescriptor[] propDescs = Introspector.getBeanInfo(cl).getPropertyDescriptors();
            beanPropSetters = new HashMap(propDescs.length * 4 / 3, 1.0F);

            for(i = 0; i < propDescs.length; ++i) {
               PropertyDescriptor propDesc = propDescs[i];
               beanPropSetter = propDesc.getWriteMethod();
               if (beanPropSetter != null) {
                  beanPropSetters.put(propDesc.getName(), beanPropSetter);
               }
            }
         } catch (Exception var13) {
            throw new _ObjectBuilderSettingEvaluationException("Failed to inspect " + cl.getName() + " class", var13);
         }

         TemplateHashModel beanTM = null;

         for(i = 0; i < namedParamNames.size(); ++i) {
            String name = (String)namedParamNames.get(i);
            if (!beanPropSetters.containsKey(name)) {
               throw new _ObjectBuilderSettingEvaluationException("The " + cl.getName() + " class has no writeable JavaBeans property called " + StringUtil.jQuote(name) + ".");
            }

            beanPropSetter = (Method)beanPropSetters.put(name, (Object)null);
            if (beanPropSetter == null) {
               throw new _ObjectBuilderSettingEvaluationException("JavaBeans property " + StringUtil.jQuote(name) + " is set twice.");
            }

            try {
               TemplateModel m;
               if (beanTM == null) {
                  m = this.env.getObjectWrapper().wrap(bean);
                  if (!(m instanceof TemplateHashModel)) {
                     throw new _ObjectBuilderSettingEvaluationException("The " + cl.getName() + " class is not a wrapped as TemplateHashModel.");
                  }

                  beanTM = (TemplateHashModel)m;
               }

               m = beanTM.get(beanPropSetter.getName());
               if (m == null) {
                  throw new _ObjectBuilderSettingEvaluationException("Can't find " + beanPropSetter + " as FreeMarker method.");
               }

               if (!(m instanceof TemplateMethodModelEx)) {
                  throw new _ObjectBuilderSettingEvaluationException(StringUtil.jQuote(beanPropSetter.getName()) + " wasn't a TemplateMethodModelEx.");
               }

               List args = new ArrayList();
               args.add(this.env.getObjectWrapper().wrap(namedParamValues.get(i)));
               ((TemplateMethodModelEx)m).exec(args);
            } catch (Exception var12) {
               throw new _ObjectBuilderSettingEvaluationException("Failed to set " + StringUtil.jQuote(name), var12);
            }
         }

      }
   }

   private static class LegacyExceptionWrapperSettingEvaluationExpression extends _ObjectBuilderSettingEvaluationException {
      public LegacyExceptionWrapperSettingEvaluationExpression(Throwable cause) {
         super("Legacy operation failed", cause);
         if (!(cause instanceof ClassNotFoundException) && !(cause instanceof InstantiationException) && !(cause instanceof IllegalAccessException)) {
            throw new IllegalArgumentException();
         }
      }

      public void rethrowLegacy() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
         Throwable cause = this.getCause();
         if (cause instanceof ClassNotFoundException) {
            throw (ClassNotFoundException)cause;
         } else if (cause instanceof InstantiationException) {
            throw (InstantiationException)cause;
         } else if (cause instanceof IllegalAccessException) {
            throw (IllegalAccessException)cause;
         } else {
            throw new BugException();
         }
      }
   }

   private class PropertyAssignmentsExpression extends ExpressionWithParameters {
      private final Object bean;

      public PropertyAssignmentsExpression(Object bean) {
         super(null);
         this.bean = bean;
      }

      Object eval() throws _ObjectBuilderSettingEvaluationException {
         _ObjectBuilderSettingEvaluator.this.setJavaBeanProperties(this.bean, this.namedParamNames, this.namedParamValues);
         return this.bean;
      }

      protected boolean getAllowPositionalParameters() {
         return false;
      }
   }

   private class BuilderCallExpression extends ExpressionWithParameters {
      private String className;
      private boolean canBeStaticField;
      private boolean mustBeStaticField;

      private BuilderCallExpression() {
         super(null);
      }

      Object eval() throws _ObjectBuilderSettingEvaluationException {
         if (this.mustBeStaticField) {
            if (!this.canBeStaticField) {
               throw new BugException();
            } else {
               return this.getStaticFieldValue(this.className);
            }
         } else if (!_ObjectBuilderSettingEvaluator.this.modernMode) {
            try {
               try {
                  return ClassUtil.forName(this.className).newInstance();
               } catch (InstantiationException var8) {
                  throw new LegacyExceptionWrapperSettingEvaluationExpression(var8);
               } catch (IllegalAccessException var9) {
                  throw new LegacyExceptionWrapperSettingEvaluationExpression(var9);
               } catch (ClassNotFoundException var10) {
                  throw new LegacyExceptionWrapperSettingEvaluationExpression(var10);
               }
            } catch (LegacyExceptionWrapperSettingEvaluationExpression var13) {
               if (this.canBeStaticField && this.className.indexOf(46) != -1) {
                  try {
                     return this.getStaticFieldValue(this.className);
                  } catch (_ObjectBuilderSettingEvaluationException var7) {
                     throw var13;
                  }
               } else {
                  throw var13;
               }
            }
         } else {
            Class cl;
            boolean clIsBuilderClass;
            try {
               cl = ClassUtil.forName(this.className + "Builder");
               clIsBuilderClass = true;
            } catch (ClassNotFoundException var16) {
               clIsBuilderClass = false;

               try {
                  cl = ClassUtil.forName(this.className);
               } catch (Exception var15) {
                  boolean failedToGetAsStaticField;
                  if (this.canBeStaticField) {
                     try {
                        return this.getStaticFieldValue(this.className);
                     } catch (_ObjectBuilderSettingEvaluationException var14) {
                        failedToGetAsStaticField = true;
                     }
                  } else {
                     failedToGetAsStaticField = false;
                  }

                  throw new _ObjectBuilderSettingEvaluationException("Failed to get class " + StringUtil.jQuote(this.className) + (failedToGetAsStaticField ? " (also failed to resolve name as static field)" : "") + ".", var15);
               }
            }

            if (!clIsBuilderClass && this.hasNoParameters()) {
               try {
                  Field f = cl.getField("INSTANCE");
                  if ((f.getModifiers() & 9) == 9) {
                     return f.get((Object)null);
                  }
               } catch (NoSuchFieldException var11) {
               } catch (Exception var12) {
                  throw new _ObjectBuilderSettingEvaluationException("Error when trying to access " + StringUtil.jQuote(this.className) + "." + "INSTANCE", var12);
               }
            }

            Object constructorResult = this.callConstructor(cl);
            _ObjectBuilderSettingEvaluator.this.setJavaBeanProperties(constructorResult, this.namedParamNames, this.namedParamValues);
            Object result;
            if (clIsBuilderClass) {
               result = this.callBuild(constructorResult);
            } else {
               if (constructorResult instanceof WriteProtectable) {
                  ((WriteProtectable)constructorResult).writeProtect();
               }

               result = constructorResult;
            }

            return result;
         }
      }

      private Object getStaticFieldValue(String dottedName) throws _ObjectBuilderSettingEvaluationException {
         int lastDotIdx = dottedName.lastIndexOf(46);
         if (lastDotIdx == -1) {
            throw new IllegalArgumentException();
         } else {
            String className = _ObjectBuilderSettingEvaluator.shorthandToFullQualified(dottedName.substring(0, lastDotIdx));
            String fieldName = dottedName.substring(lastDotIdx + 1);

            Class cl;
            try {
               cl = ClassUtil.forName(className);
            } catch (Exception var10) {
               throw new _ObjectBuilderSettingEvaluationException("Failed to get field's parent class, " + StringUtil.jQuote(className) + ".", var10);
            }

            Field field;
            try {
               field = cl.getField(fieldName);
            } catch (Exception var9) {
               throw new _ObjectBuilderSettingEvaluationException("Failed to get field " + StringUtil.jQuote(fieldName) + " from class " + StringUtil.jQuote(className) + ".", var9);
            }

            if ((field.getModifiers() & 8) == 0) {
               throw new _ObjectBuilderSettingEvaluationException("Referred field isn't static: " + field);
            } else if ((field.getModifiers() & 1) == 0) {
               throw new _ObjectBuilderSettingEvaluationException("Referred field isn't public: " + field);
            } else if (field.getName().equals("INSTANCE")) {
               throw new _ObjectBuilderSettingEvaluationException("The INSTANCE field is only accessible through pseudo-constructor call: " + className + "()");
            } else {
               try {
                  return field.get((Object)null);
               } catch (Exception var8) {
                  throw new _ObjectBuilderSettingEvaluationException("Failed to get field value: " + field, var8);
               }
            }
         }
      }

      private Object callConstructor(Class cl) throws _ObjectBuilderSettingEvaluationException {
         if (this.hasNoParameters()) {
            try {
               return cl.newInstance();
            } catch (Exception var6) {
               throw new _ObjectBuilderSettingEvaluationException("Failed to call " + cl.getName() + " 0-argument constructor", var6);
            }
         } else {
            BeansWrapper ow = _ObjectBuilderSettingEvaluator.this.env.getObjectWrapper();
            List tmArgs = new ArrayList(this.positionalParamValues.size());

            for(int i = 0; i < this.positionalParamValues.size(); ++i) {
               try {
                  tmArgs.add(ow.wrap(this.positionalParamValues.get(i)));
               } catch (TemplateModelException var8) {
                  throw new _ObjectBuilderSettingEvaluationException("Failed to wrap arg #" + (i + 1), var8);
               }
            }

            try {
               return ow.newInstance(cl, tmArgs);
            } catch (Exception var7) {
               throw new _ObjectBuilderSettingEvaluationException("Failed to call " + cl.getName() + " constructor", var7);
            }
         }
      }

      private Object callBuild(Object constructorResult) throws _ObjectBuilderSettingEvaluationException {
         Class cl = constructorResult.getClass();

         Method buildMethod;
         try {
            buildMethod = constructorResult.getClass().getMethod("build", (Class[])null);
         } catch (NoSuchMethodException var6) {
            throw new _ObjectBuilderSettingEvaluationException("The " + cl.getName() + " builder class must have a public " + "build" + "() method", var6);
         } catch (Exception var7) {
            throw new _ObjectBuilderSettingEvaluationException("Failed to get the build() method of the " + cl.getName() + " builder class", var7);
         }

         try {
            return buildMethod.invoke(constructorResult, (Object[])null);
         } catch (Exception var8) {
            Object cause;
            if (var8 instanceof InvocationTargetException) {
               cause = ((InvocationTargetException)var8).getTargetException();
            } else {
               cause = var8;
            }

            throw new _ObjectBuilderSettingEvaluationException("Failed to call build() method on " + cl.getName() + " instance", (Throwable)cause);
         }
      }

      private boolean hasNoParameters() {
         return this.positionalParamValues.isEmpty() && this.namedParamValues.isEmpty();
      }

      protected boolean getAllowPositionalParameters() {
         return true;
      }

      // $FF: synthetic method
      BuilderCallExpression(Object x1) {
         this();
      }
   }

   private static class KeyValuePair {
      private final Object key;
      private final Object value;

      public KeyValuePair(Object key, Object value) {
         this.key = key;
         this.value = value;
      }
   }

   private class MapExpression extends SettingExpression {
      private List<KeyValuePair> items;

      private MapExpression() {
         super(null);
         this.items = new ArrayList();
      }

      void addItem(KeyValuePair item) {
         this.items.add(item);
      }

      public int itemCount() {
         return this.items.size();
      }

      Object eval() throws _ObjectBuilderSettingEvaluationException {
         LinkedHashMap res = new LinkedHashMap(this.items.size() * 4 / 3, 1.0F);
         Iterator var2 = this.items.iterator();

         while(var2.hasNext()) {
            KeyValuePair item = (KeyValuePair)var2.next();
            Object key = _ObjectBuilderSettingEvaluator.this.ensureEvaled(item.key);
            if (key == null) {
               throw new _ObjectBuilderSettingEvaluationException("Map can't use null as key.");
            }

            res.put(key, _ObjectBuilderSettingEvaluator.this.ensureEvaled(item.value));
         }

         return res;
      }

      // $FF: synthetic method
      MapExpression(Object x1) {
         this();
      }
   }

   private class ListExpression extends SettingExpression {
      private List<Object> items;

      private ListExpression() {
         super(null);
         this.items = new ArrayList();
      }

      void addItem(Object item) {
         this.items.add(item);
      }

      public int itemCount() {
         return this.items.size();
      }

      Object eval() throws _ObjectBuilderSettingEvaluationException {
         ArrayList res = new ArrayList(this.items.size());
         Iterator var2 = this.items.iterator();

         while(var2.hasNext()) {
            Object item = var2.next();
            res.add(_ObjectBuilderSettingEvaluator.this.ensureEvaled(item));
         }

         return res;
      }

      // $FF: synthetic method
      ListExpression(Object x1) {
         this();
      }
   }

   private abstract class ExpressionWithParameters extends SettingExpression {
      protected List positionalParamValues;
      protected List namedParamNames;
      protected List namedParamValues;

      private ExpressionWithParameters() {
         super(null);
         this.positionalParamValues = new ArrayList();
         this.namedParamNames = new ArrayList();
         this.namedParamValues = new ArrayList();
      }

      protected abstract boolean getAllowPositionalParameters();

      // $FF: synthetic method
      ExpressionWithParameters(Object x1) {
         this();
      }
   }

   private abstract static class SettingExpression {
      private SettingExpression() {
      }

      abstract Object eval() throws _ObjectBuilderSettingEvaluationException;

      // $FF: synthetic method
      SettingExpression(Object x0) {
         this();
      }
   }

   private static class Name {
      private final String name;

      public Name(String name) {
         this.name = name;
      }
   }
}
