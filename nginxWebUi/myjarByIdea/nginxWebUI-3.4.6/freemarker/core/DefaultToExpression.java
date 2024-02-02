package freemarker.core;

import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.Constants;
import java.util.ArrayList;

class DefaultToExpression extends Expression {
   private static final TemplateCollectionModel EMPTY_COLLECTION = new SimpleCollection(new ArrayList(0));
   static final TemplateModel EMPTY_STRING_AND_SEQUENCE_AND_HASH = new EmptyStringAndSequenceAndHash();
   private final Expression lho;
   private final Expression rho;

   DefaultToExpression(Expression lho, Expression rho) {
      this.lho = lho;
      this.rho = rho;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateModel left;
      if (this.lho instanceof ParentheticalExpression) {
         boolean lastFIRE = env.setFastInvalidReferenceExceptions(true);

         try {
            left = this.lho.eval(env);
         } catch (InvalidReferenceException var8) {
            left = null;
         } finally {
            env.setFastInvalidReferenceExceptions(lastFIRE);
         }
      } else {
         left = this.lho.eval(env);
      }

      if (left != null) {
         return left;
      } else {
         return this.rho == null ? EMPTY_STRING_AND_SEQUENCE_AND_HASH : this.rho.eval(env);
      }
   }

   boolean isLiteral() {
      return false;
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new DefaultToExpression(this.lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho != null ? this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState) : null);
   }

   public String getCanonicalForm() {
      return this.rho == null ? this.lho.getCanonicalForm() + '!' : this.lho.getCanonicalForm() + '!' + this.rho.getCanonicalForm();
   }

   String getNodeTypeSymbol() {
      return "...!...";
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.lho;
         case 1:
            return this.rho;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      return ParameterRole.forBinaryOperatorOperand(idx);
   }

   private static class EmptyStringAndSequenceAndHash implements TemplateScalarModel, TemplateSequenceModel, TemplateHashModelEx2 {
      private EmptyStringAndSequenceAndHash() {
      }

      public String getAsString() {
         return "";
      }

      public TemplateModel get(int i) {
         return null;
      }

      public TemplateModel get(String s) {
         return null;
      }

      public int size() {
         return 0;
      }

      public boolean isEmpty() {
         return true;
      }

      public TemplateCollectionModel keys() {
         return DefaultToExpression.EMPTY_COLLECTION;
      }

      public TemplateCollectionModel values() {
         return DefaultToExpression.EMPTY_COLLECTION;
      }

      public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
         return Constants.EMPTY_KEY_VALUE_PAIR_ITERATOR;
      }

      // $FF: synthetic method
      EmptyStringAndSequenceAndHash(Object x0) {
         this();
      }
   }
}
