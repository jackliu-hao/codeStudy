package freemarker.core;

import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._TemplateAPI;
import java.util.HashSet;
import java.util.Set;

final class AddConcatExpression extends Expression {
   private final Expression left;
   private final Expression right;

   AddConcatExpression(Expression left, Expression right) {
      this.left = left;
      this.right = right;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      return _eval(env, this, this.left, this.left.eval(env), this.right, this.right.eval(env));
   }

   static TemplateModel _eval(Environment env, TemplateObject parent, Expression leftExp, TemplateModel leftModel, Expression rightExp, TemplateModel rightModel) throws TemplateModelException, TemplateException, NonStringException {
      if (leftModel instanceof TemplateNumberModel && rightModel instanceof TemplateNumberModel) {
         Number first = EvalUtil.modelToNumber((TemplateNumberModel)leftModel, leftExp);
         Number second = EvalUtil.modelToNumber((TemplateNumberModel)rightModel, rightExp);
         return _evalOnNumbers(env, parent, first, second);
      } else if (leftModel instanceof TemplateSequenceModel && rightModel instanceof TemplateSequenceModel) {
         return new ConcatenatedSequence((TemplateSequenceModel)leftModel, (TemplateSequenceModel)rightModel);
      } else {
         boolean hashConcatPossible = leftModel instanceof TemplateHashModel && rightModel instanceof TemplateHashModel;

         try {
            Object leftOMOrStr = EvalUtil.coerceModelToStringOrMarkup(leftModel, leftExp, hashConcatPossible, (String)null, env);
            if (leftOMOrStr == null) {
               return _eval_concatenateHashes(leftModel, rightModel);
            } else {
               Object rightOMOrStr = EvalUtil.coerceModelToStringOrMarkup(rightModel, rightExp, hashConcatPossible, (String)null, env);
               if (rightOMOrStr == null) {
                  return _eval_concatenateHashes(leftModel, rightModel);
               } else {
                  TemplateMarkupOutputModel rightMO;
                  if (leftOMOrStr instanceof String) {
                     if (rightOMOrStr instanceof String) {
                        return new SimpleScalar(((String)leftOMOrStr).concat((String)rightOMOrStr));
                     } else {
                        rightMO = (TemplateMarkupOutputModel)rightOMOrStr;
                        return EvalUtil.concatMarkupOutputs(parent, rightMO.getOutputFormat().fromPlainTextByEscaping((String)leftOMOrStr), rightMO);
                     }
                  } else {
                     rightMO = (TemplateMarkupOutputModel)leftOMOrStr;
                     return rightOMOrStr instanceof String ? EvalUtil.concatMarkupOutputs(parent, rightMO, rightMO.getOutputFormat().fromPlainTextByEscaping((String)rightOMOrStr)) : EvalUtil.concatMarkupOutputs(parent, rightMO, (TemplateMarkupOutputModel)rightOMOrStr);
                  }
               }
            }
         } catch (NonStringOrTemplateOutputException var10) {
            if (hashConcatPossible) {
               return _eval_concatenateHashes(leftModel, rightModel);
            } else {
               throw var10;
            }
         }
      }
   }

   private static TemplateModel _eval_concatenateHashes(TemplateModel leftModel, TemplateModel rightModel) throws TemplateModelException {
      if (leftModel instanceof TemplateHashModelEx && rightModel instanceof TemplateHashModelEx) {
         TemplateHashModelEx leftModelEx = (TemplateHashModelEx)leftModel;
         TemplateHashModelEx rightModelEx = (TemplateHashModelEx)rightModel;
         if (leftModelEx.size() == 0) {
            return rightModelEx;
         } else {
            return (TemplateModel)(rightModelEx.size() == 0 ? leftModelEx : new ConcatenatedHashEx(leftModelEx, rightModelEx));
         }
      } else {
         return new ConcatenatedHash((TemplateHashModel)leftModel, (TemplateHashModel)rightModel);
      }
   }

   static TemplateModel _evalOnNumbers(Environment env, TemplateObject parent, Number first, Number second) throws TemplateException {
      ArithmeticEngine ae = EvalUtil.getArithmeticEngine(env, parent);
      return new SimpleNumber(ae.add(first, second));
   }

   boolean isLiteral() {
      return this.constantValue != null || this.left.isLiteral() && this.right.isLiteral();
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new AddConcatExpression(this.left.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.right.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
   }

   public String getCanonicalForm() {
      return this.left.getCanonicalForm() + " + " + this.right.getCanonicalForm();
   }

   String getNodeTypeSymbol() {
      return "+";
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      return idx == 0 ? this.left : this.right;
   }

   ParameterRole getParameterRole(int idx) {
      return ParameterRole.forBinaryOperatorOperand(idx);
   }

   private static final class ConcatenatedHashEx extends ConcatenatedHash implements TemplateHashModelEx {
      private CollectionAndSequence keys;
      private CollectionAndSequence values;

      ConcatenatedHashEx(TemplateHashModelEx left, TemplateHashModelEx right) {
         super(left, right);
      }

      public int size() throws TemplateModelException {
         this.initKeys();
         return this.keys.size();
      }

      public TemplateCollectionModel keys() throws TemplateModelException {
         this.initKeys();
         return this.keys;
      }

      public TemplateCollectionModel values() throws TemplateModelException {
         this.initValues();
         return this.values;
      }

      private void initKeys() throws TemplateModelException {
         if (this.keys == null) {
            HashSet keySet = new HashSet();
            SimpleSequence keySeq = new SimpleSequence(32, _TemplateAPI.SAFE_OBJECT_WRAPPER);
            addKeys(keySet, keySeq, (TemplateHashModelEx)this.left);
            addKeys(keySet, keySeq, (TemplateHashModelEx)this.right);
            this.keys = new CollectionAndSequence(keySeq);
         }

      }

      private static void addKeys(Set keySet, SimpleSequence keySeq, TemplateHashModelEx hash) throws TemplateModelException {
         TemplateModelIterator it = hash.keys().iterator();

         while(it.hasNext()) {
            TemplateScalarModel tsm = (TemplateScalarModel)it.next();
            if (keySet.add(tsm.getAsString())) {
               keySeq.add(tsm);
            }
         }

      }

      private void initValues() throws TemplateModelException {
         if (this.values == null) {
            SimpleSequence seq = new SimpleSequence(this.size(), _TemplateAPI.SAFE_OBJECT_WRAPPER);
            int ln = this.keys.size();

            for(int i = 0; i < ln; ++i) {
               seq.add(this.get(((TemplateScalarModel)this.keys.get(i)).getAsString()));
            }

            this.values = new CollectionAndSequence(seq);
         }

      }
   }

   private static class ConcatenatedHash implements TemplateHashModel {
      protected final TemplateHashModel left;
      protected final TemplateHashModel right;

      ConcatenatedHash(TemplateHashModel left, TemplateHashModel right) {
         this.left = left;
         this.right = right;
      }

      public TemplateModel get(String key) throws TemplateModelException {
         TemplateModel model = this.right.get(key);
         return model != null ? model : this.left.get(key);
      }

      public boolean isEmpty() throws TemplateModelException {
         return this.left.isEmpty() && this.right.isEmpty();
      }
   }

   private static final class ConcatenatedSequence implements TemplateSequenceModel {
      private final TemplateSequenceModel left;
      private final TemplateSequenceModel right;

      ConcatenatedSequence(TemplateSequenceModel left, TemplateSequenceModel right) {
         this.left = left;
         this.right = right;
      }

      public int size() throws TemplateModelException {
         return this.left.size() + this.right.size();
      }

      public TemplateModel get(int i) throws TemplateModelException {
         int ls = this.left.size();
         return i < ls ? this.left.get(i) : this.right.get(i - ls);
      }
   }
}
