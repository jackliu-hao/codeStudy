package freemarker.core;

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._TemplateAPI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

final class ListLiteral extends Expression {
   final ArrayList<Expression> items;

   ListLiteral(ArrayList<Expression> items) {
      this.items = items;
      items.trimToSize();
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      SimpleSequence list = new SimpleSequence(this.items.size(), _TemplateAPI.SAFE_OBJECT_WRAPPER);

      TemplateModel tm;
      for(Iterator var3 = this.items.iterator(); var3.hasNext(); list.add(tm)) {
         Expression exp = (Expression)var3.next();
         tm = exp.eval(env);
         if (env == null || !env.isClassicCompatible()) {
            exp.assertNonNull(tm, env);
         }
      }

      return list;
   }

   List getValueList(Environment env) throws TemplateException {
      int size = this.items.size();
      switch (size) {
         case 0:
            return Collections.EMPTY_LIST;
         case 1:
            return Collections.singletonList(((Expression)this.items.get(0)).evalAndCoerceToPlainText(env));
         default:
            List result = new ArrayList(this.items.size());
            ListIterator iterator = this.items.listIterator();

            while(iterator.hasNext()) {
               Expression exp = (Expression)iterator.next();
               result.add(exp.evalAndCoerceToPlainText(env));
            }

            return result;
      }
   }

   List getModelList(Environment env) throws TemplateException {
      int size = this.items.size();
      switch (size) {
         case 0:
            return Collections.EMPTY_LIST;
         case 1:
            return Collections.singletonList(((Expression)this.items.get(0)).eval(env));
         default:
            List result = new ArrayList(this.items.size());
            ListIterator iterator = this.items.listIterator();

            while(iterator.hasNext()) {
               Expression exp = (Expression)iterator.next();
               result.add(exp.eval(env));
            }

            return result;
      }
   }

   public String getCanonicalForm() {
      StringBuilder buf = new StringBuilder("[");
      int size = this.items.size();

      for(int i = 0; i < size; ++i) {
         Expression value = (Expression)this.items.get(i);
         buf.append(value.getCanonicalForm());
         if (i != size - 1) {
            buf.append(", ");
         }
      }

      buf.append("]");
      return buf.toString();
   }

   String getNodeTypeSymbol() {
      return "[...]";
   }

   boolean isLiteral() {
      if (this.constantValue != null) {
         return true;
      } else {
         for(int i = 0; i < this.items.size(); ++i) {
            Expression exp = (Expression)this.items.get(i);
            if (!exp.isLiteral()) {
               return false;
            }
         }

         return true;
      }
   }

   TemplateSequenceModel evaluateStringsToNamespaces(Environment env) throws TemplateException {
      TemplateSequenceModel val = (TemplateSequenceModel)this.eval(env);
      SimpleSequence result = new SimpleSequence(val.size(), _TemplateAPI.SAFE_OBJECT_WRAPPER);

      for(int i = 0; i < this.items.size(); ++i) {
         Object itemExpr = this.items.get(i);
         if (itemExpr instanceof StringLiteral) {
            String s = ((StringLiteral)itemExpr).getAsString();

            try {
               Environment.Namespace ns = env.importLib((String)s, (String)null);
               result.add(ns);
            } catch (IOException var8) {
               throw new _MiscTemplateException((StringLiteral)itemExpr, new Object[]{"Couldn't import library ", new _DelayedJQuote(s), ": ", new _DelayedGetMessage(var8)});
            }
         } else {
            result.add(val.get(i));
         }
      }

      return result;
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      ArrayList clonedValues = (ArrayList)this.items.clone();
      ListIterator iter = clonedValues.listIterator();

      while(iter.hasNext()) {
         iter.set(((Expression)iter.next()).deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
      }

      return new ListLiteral(clonedValues);
   }

   int getParameterCount() {
      return this.items != null ? this.items.size() : 0;
   }

   Object getParameterValue(int idx) {
      this.checkIndex(idx);
      return this.items.get(idx);
   }

   ParameterRole getParameterRole(int idx) {
      this.checkIndex(idx);
      return ParameterRole.ITEM_VALUE;
   }

   private void checkIndex(int idx) {
      if (this.items == null || idx >= this.items.size()) {
         throw new IndexOutOfBoundsException();
      }
   }
}
