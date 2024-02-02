package freemarker.core;

import freemarker.template.EmptyMap;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.utility.ObjectFactory;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class UnifiedCall extends TemplateElement implements DirectiveCallPlace {
   private Expression nameExp;
   private Map<String, ? extends Expression> namedArgs;
   private List<? extends Expression> positionalArgs;
   private List<String> bodyParameterNames;
   boolean legacySyntax;
   private transient volatile SoftReference sortedNamedArgsCache;
   private CustomDataHolder customDataHolder;

   UnifiedCall(Expression nameExp, Map<String, ? extends Expression> namedArgs, TemplateElements children, List<String> bodyParameterNames) {
      this.nameExp = nameExp;
      this.namedArgs = namedArgs;
      this.setChildren(children);
      this.bodyParameterNames = bodyParameterNames;
   }

   UnifiedCall(Expression nameExp, List<? extends Expression> positionalArgs, TemplateElements children, List<String> bodyParameterNames) {
      this.nameExp = nameExp;
      this.positionalArgs = positionalArgs;
      this.setChildren(children);
      this.bodyParameterNames = bodyParameterNames;
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      TemplateModel tm = this.nameExp.eval(env);
      if (tm == Macro.DO_NOTHING_MACRO) {
         return null;
      } else {
         if (tm instanceof Macro) {
            Macro macro = (Macro)tm;
            if (macro.isFunction() && !this.legacySyntax) {
               throw new _MiscTemplateException(env, new Object[]{"Routine ", new _DelayedJQuote(macro.getName()), " is a function, not a directive. Functions can only be called from expressions, like in ${f()}, ${x + f()} or ", "<@someDirective someParam=f() />", "."});
            }

            env.invokeMacro(macro, this.namedArgs, this.positionalArgs, this.bodyParameterNames, this);
         } else {
            boolean isDirectiveModel = tm instanceof TemplateDirectiveModel;
            if (!isDirectiveModel && !(tm instanceof TemplateTransformModel)) {
               if (tm == null) {
                  throw InvalidReferenceException.getInstance(this.nameExp, env);
               }

               throw new NonUserDefinedDirectiveLikeException(this.nameExp, tm, env);
            }

            Object args;
            if (this.namedArgs != null && !this.namedArgs.isEmpty()) {
               args = new HashMap();
               Iterator it = this.namedArgs.entrySet().iterator();

               while(it.hasNext()) {
                  Map.Entry entry = (Map.Entry)it.next();
                  String key = (String)entry.getKey();
                  Expression valueExp = (Expression)entry.getValue();
                  TemplateModel value = valueExp.eval(env);
                  ((Map)args).put(key, value);
               }
            } else {
               args = EmptyMap.instance;
            }

            if (isDirectiveModel) {
               env.visit((TemplateElement[])this.getChildBuffer(), (TemplateDirectiveModel)tm, (Map)args, this.bodyParameterNames);
            } else {
               env.visitAndTransform(this.getChildBuffer(), (TemplateTransformModel)tm, (Map)args);
            }
         }

         return null;
      }
   }

   protected String dump(boolean canonical) {
      StringBuilder sb = new StringBuilder();
      if (canonical) {
         sb.append('<');
      }

      sb.append('@');
      _MessageUtil.appendExpressionAsUntearable(sb, this.nameExp);
      boolean nameIsInParen = sb.charAt(sb.length() - 1) == ')';
      int i;
      if (this.positionalArgs != null) {
         for(i = 0; i < this.positionalArgs.size(); ++i) {
            Expression argExp = (Expression)this.positionalArgs.get(i);
            if (i != 0) {
               sb.append(',');
            }

            sb.append(' ');
            sb.append(argExp.getCanonicalForm());
         }
      } else {
         List entries = this.getSortedNamedArgs();

         for(int i = 0; i < entries.size(); ++i) {
            Map.Entry entry = (Map.Entry)entries.get(i);
            Expression argExp = (Expression)entry.getValue();
            sb.append(' ');
            sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference((String)entry.getKey()));
            sb.append('=');
            _MessageUtil.appendExpressionAsUntearable(sb, argExp);
         }
      }

      if (this.bodyParameterNames != null && !this.bodyParameterNames.isEmpty()) {
         sb.append("; ");

         for(i = 0; i < this.bodyParameterNames.size(); ++i) {
            if (i != 0) {
               sb.append(", ");
            }

            sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference((String)this.bodyParameterNames.get(i)));
         }
      }

      if (canonical) {
         if (this.getChildCount() == 0) {
            sb.append("/>");
         } else {
            sb.append('>');
            sb.append(this.getChildrenCanonicalForm());
            sb.append("</@");
            if (!nameIsInParen && (this.nameExp instanceof Identifier || this.nameExp instanceof Dot && ((Dot)this.nameExp).onlyHasIdentifiers())) {
               sb.append(this.nameExp.getCanonicalForm());
            }

            sb.append('>');
         }
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      return "@";
   }

   int getParameterCount() {
      return 1 + (this.positionalArgs != null ? this.positionalArgs.size() : 0) + (this.namedArgs != null ? this.namedArgs.size() * 2 : 0) + (this.bodyParameterNames != null ? this.bodyParameterNames.size() : 0);
   }

   Object getParameterValue(int idx) {
      if (idx == 0) {
         return this.nameExp;
      } else {
         int base = 1;
         int positionalArgsSize = this.positionalArgs != null ? this.positionalArgs.size() : 0;
         if (idx - base < positionalArgsSize) {
            return this.positionalArgs.get(idx - base);
         } else {
            base += positionalArgsSize;
            int namedArgsSize = this.namedArgs != null ? this.namedArgs.size() : 0;
            if (idx - base < namedArgsSize * 2) {
               Map.Entry namedArg = (Map.Entry)this.getSortedNamedArgs().get((idx - base) / 2);
               return (idx - base) % 2 == 0 ? namedArg.getKey() : namedArg.getValue();
            } else {
               base += namedArgsSize * 2;
               int bodyParameterNamesSize = this.bodyParameterNames != null ? this.bodyParameterNames.size() : 0;
               if (idx - base < bodyParameterNamesSize) {
                  return this.bodyParameterNames.get(idx - base);
               } else {
                  throw new IndexOutOfBoundsException();
               }
            }
         }
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx == 0) {
         return ParameterRole.CALLEE;
      } else {
         int base = 1;
         int positionalArgsSize = this.positionalArgs != null ? this.positionalArgs.size() : 0;
         if (idx - base < positionalArgsSize) {
            return ParameterRole.ARGUMENT_VALUE;
         } else {
            base += positionalArgsSize;
            int namedArgsSize = this.namedArgs != null ? this.namedArgs.size() : 0;
            if (idx - base < namedArgsSize * 2) {
               return (idx - base) % 2 == 0 ? ParameterRole.ARGUMENT_NAME : ParameterRole.ARGUMENT_VALUE;
            } else {
               base += namedArgsSize * 2;
               int bodyParameterNamesSize = this.bodyParameterNames != null ? this.bodyParameterNames.size() : 0;
               if (idx - base < bodyParameterNamesSize) {
                  return ParameterRole.TARGET_LOOP_VARIABLE;
               } else {
                  throw new IndexOutOfBoundsException();
               }
            }
         }
      }
   }

   private List getSortedNamedArgs() {
      Reference ref = this.sortedNamedArgsCache;
      List res;
      if (ref != null) {
         res = (List)ref.get();
         if (res != null) {
            return res;
         }
      }

      res = MiscUtil.sortMapOfExpressions(this.namedArgs);
      this.sortedNamedArgsCache = new SoftReference(res);
      return res;
   }

   public Object getOrCreateCustomData(Object providerIdentity, ObjectFactory objectFactory) throws CallPlaceCustomDataInitializationException {
      CustomDataHolder customDataHolder = this.customDataHolder;
      if (customDataHolder == null) {
         synchronized(this) {
            customDataHolder = this.customDataHolder;
            if (customDataHolder == null || customDataHolder.providerIdentity != providerIdentity) {
               customDataHolder = this.createNewCustomData(providerIdentity, objectFactory);
               this.customDataHolder = customDataHolder;
            }
         }
      }

      if (customDataHolder.providerIdentity != providerIdentity) {
         synchronized(this) {
            customDataHolder = this.customDataHolder;
            if (customDataHolder == null || customDataHolder.providerIdentity != providerIdentity) {
               customDataHolder = this.createNewCustomData(providerIdentity, objectFactory);
               this.customDataHolder = customDataHolder;
            }
         }
      }

      return customDataHolder.customData;
   }

   private CustomDataHolder createNewCustomData(Object provierIdentity, ObjectFactory objectFactory) throws CallPlaceCustomDataInitializationException {
      Object customData;
      try {
         customData = objectFactory.createObject();
      } catch (Exception var6) {
         throw new CallPlaceCustomDataInitializationException("Failed to initialize custom data for provider identity " + StringUtil.tryToString(provierIdentity) + " via factory " + StringUtil.tryToString(objectFactory), var6);
      }

      if (customData == null) {
         throw new NullPointerException("ObjectFactory.createObject() has returned null");
      } else {
         CustomDataHolder customDataHolder = new CustomDataHolder(provierIdentity, customData);
         return customDataHolder;
      }
   }

   public boolean isNestedOutputCacheable() {
      return this.isChildrenOutputCacheable();
   }

   boolean isNestedBlockRepeater() {
      return true;
   }

   boolean isShownInStackTrace() {
      return true;
   }

   private static class CustomDataHolder {
      private final Object providerIdentity;
      private final Object customData;

      public CustomDataHolder(Object providerIdentity, Object customData) {
         this.providerIdentity = providerIdentity;
         this.customData = customData;
      }
   }
}
