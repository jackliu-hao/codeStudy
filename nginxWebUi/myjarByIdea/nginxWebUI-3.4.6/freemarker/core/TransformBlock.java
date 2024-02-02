package freemarker.core;

import freemarker.template.EmptyMap;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class TransformBlock extends TemplateElement {
   private Expression transformExpression;
   Map namedArgs;
   private transient volatile SoftReference sortedNamedArgsCache;

   TransformBlock(Expression transformExpression, Map namedArgs, TemplateElements children) {
      this.transformExpression = transformExpression;
      this.namedArgs = namedArgs;
      this.setChildren(children);
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      TemplateTransformModel ttm = env.getTransform(this.transformExpression);
      if (ttm == null) {
         TemplateModel tm = this.transformExpression.eval(env);
         throw new UnexpectedTypeException(this.transformExpression, tm, "transform", new Class[]{TemplateTransformModel.class}, env);
      } else {
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

         env.visitAndTransform(this.getChildBuffer(), ttm, (Map)args);
         return null;
      }
   }

   protected String dump(boolean canonical) {
      StringBuilder sb = new StringBuilder();
      if (canonical) {
         sb.append('<');
      }

      sb.append(this.getNodeTypeSymbol());
      sb.append(' ');
      sb.append(this.transformExpression);
      if (this.namedArgs != null) {
         Iterator it = this.getSortedNamedArgs().iterator();

         while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            sb.append(' ');
            sb.append(entry.getKey());
            sb.append('=');
            _MessageUtil.appendExpressionAsUntearable(sb, (Expression)entry.getValue());
         }
      }

      if (canonical) {
         sb.append(">");
         sb.append(this.getChildrenCanonicalForm());
         sb.append("</").append(this.getNodeTypeSymbol()).append('>');
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      return "#transform";
   }

   int getParameterCount() {
      return 1 + (this.namedArgs != null ? this.namedArgs.size() * 2 : 0);
   }

   Object getParameterValue(int idx) {
      if (idx == 0) {
         return this.transformExpression;
      } else if (this.namedArgs != null && idx - 1 < this.namedArgs.size() * 2) {
         Map.Entry namedArg = (Map.Entry)this.getSortedNamedArgs().get((idx - 1) / 2);
         return (idx - 1) % 2 == 0 ? namedArg.getKey() : namedArg.getValue();
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx == 0) {
         return ParameterRole.CALLEE;
      } else if (idx - 1 < this.namedArgs.size() * 2) {
         return (idx - 1) % 2 == 0 ? ParameterRole.ARGUMENT_NAME : ParameterRole.ARGUMENT_VALUE;
      } else {
         throw new IndexOutOfBoundsException();
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

   boolean isNestedBlockRepeater() {
      return false;
   }

   boolean isShownInStackTrace() {
      return true;
   }
}
