package freemarker.core;

import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;

class BuiltInsForLoopVariables {
   static class item_cycleBI extends BuiltInForLoopVariable {
      TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
         return new BIMethod(iterCtx);
      }

      private class BIMethod implements TemplateMethodModelEx {
         private final IteratorBlock.IterationContext iterCtx;

         private BIMethod(IteratorBlock.IterationContext iterCtx) {
            this.iterCtx = iterCtx;
         }

         public Object exec(List args) throws TemplateModelException {
            item_cycleBI.this.checkMethodArgCount(args, 1, Integer.MAX_VALUE);
            return args.get(this.iterCtx.getIndex() % args.size());
         }

         // $FF: synthetic method
         BIMethod(IteratorBlock.IterationContext x1, Object x2) {
            this(x1);
         }
      }
   }

   static class item_parity_capBI extends BuiltInForLoopVariable {
      private static final SimpleScalar ODD = new SimpleScalar("Odd");
      private static final SimpleScalar EVEN = new SimpleScalar("Even");

      TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
         return iterCtx.getIndex() % 2 == 0 ? ODD : EVEN;
      }
   }

   static class item_parityBI extends BuiltInForLoopVariable {
      private static final SimpleScalar ODD = new SimpleScalar("odd");
      private static final SimpleScalar EVEN = new SimpleScalar("even");

      TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
         return iterCtx.getIndex() % 2 == 0 ? ODD : EVEN;
      }
   }

   static class is_even_itemBI extends BooleanBuiltInForLoopVariable {
      protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
         return iterCtx.getIndex() % 2 != 0;
      }
   }

   static class is_odd_itemBI extends BooleanBuiltInForLoopVariable {
      protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
         return iterCtx.getIndex() % 2 == 0;
      }
   }

   static class is_firstBI extends BooleanBuiltInForLoopVariable {
      protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
         return iterCtx.getIndex() == 0;
      }
   }

   static class is_lastBI extends BooleanBuiltInForLoopVariable {
      protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
         return !iterCtx.hasNext();
      }
   }

   static class has_nextBI extends BooleanBuiltInForLoopVariable {
      protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
         return iterCtx.hasNext();
      }
   }

   abstract static class BooleanBuiltInForLoopVariable extends BuiltInForLoopVariable {
      final TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
         return this.calculateBooleanResult(iterCtx, env) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }

      protected abstract boolean calculateBooleanResult(IteratorBlock.IterationContext var1, Environment var2);
   }

   static class counterBI extends BuiltInForLoopVariable {
      TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
         return new SimpleNumber(iterCtx.getIndex() + 1);
      }
   }

   static class indexBI extends BuiltInForLoopVariable {
      TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
         return new SimpleNumber(iterCtx.getIndex());
      }
   }
}
