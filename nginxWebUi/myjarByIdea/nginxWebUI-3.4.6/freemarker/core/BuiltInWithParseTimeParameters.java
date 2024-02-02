package freemarker.core;

import java.util.List;

abstract class BuiltInWithParseTimeParameters extends SpecialBuiltIn {
   abstract void bindToParameters(List<Expression> var1, Token var2, Token var3) throws ParseException;

   public String getCanonicalForm() {
      StringBuilder buf = new StringBuilder();
      buf.append(super.getCanonicalForm());
      buf.append("(");
      List<Expression> args = this.getArgumentsAsList();
      int size = args.size();

      for(int i = 0; i < size; ++i) {
         if (i != 0) {
            buf.append(", ");
         }

         Expression arg = (Expression)args.get(i);
         buf.append(arg.getCanonicalForm());
      }

      buf.append(")");
      return buf.toString();
   }

   String getNodeTypeSymbol() {
      return super.getNodeTypeSymbol() + "(...)";
   }

   int getParameterCount() {
      return super.getParameterCount() + this.getArgumentsCount();
   }

   Object getParameterValue(int idx) {
      int superParamCnt = super.getParameterCount();
      if (idx < superParamCnt) {
         return super.getParameterValue(idx);
      } else {
         int argIdx = idx - superParamCnt;
         return this.getArgumentParameterValue(argIdx);
      }
   }

   ParameterRole getParameterRole(int idx) {
      int superParamCnt = super.getParameterCount();
      if (idx < superParamCnt) {
         return super.getParameterRole(idx);
      } else if (idx - superParamCnt < this.getArgumentsCount()) {
         return ParameterRole.ARGUMENT_VALUE;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected final ParseException newArgumentCountException(String ordinalityDesc, Token openParen, Token closeParen) {
      return new ParseException("?" + this.key + "(...) " + ordinalityDesc + " parameters", this.getTemplate(), openParen.beginLine, openParen.beginColumn, closeParen.endLine, closeParen.endColumn);
   }

   protected final void checkLocalLambdaParamCount(LocalLambdaExpression localLambdaExp, int expectedParamCount) throws ParseException {
      int actualParamCount = localLambdaExp.getLambdaParameterList().getParameters().size();
      if (actualParamCount != expectedParamCount) {
         throw new ParseException("?" + this.key + "(...) parameter lambda expression must declare exactly " + expectedParamCount + " parameter" + (expectedParamCount > 1 ? "s" : "") + ", but it declared " + actualParamCount + ".", localLambdaExp);
      }
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      Expression clone = super.deepCloneWithIdentifierReplaced_inner(replacedIdentifier, replacement, replacementState);
      this.cloneArguments(clone, replacedIdentifier, replacement, replacementState);
      return clone;
   }

   protected abstract List<Expression> getArgumentsAsList();

   protected abstract int getArgumentsCount();

   protected abstract Expression getArgumentParameterValue(int var1);

   protected abstract void cloneArguments(Expression var1, String var2, Expression var3, Expression.ReplacemenetState var4);

   protected boolean isLocalLambdaParameterSupported() {
      return false;
   }
}
