package freemarker.core;

class BreakOrContinueException extends FlowControlException {
   static final BreakOrContinueException BREAK_INSTANCE = new BreakOrContinueException();
   static final BreakOrContinueException CONTINUE_INSTANCE = new BreakOrContinueException();

   private BreakOrContinueException() {
   }
}
