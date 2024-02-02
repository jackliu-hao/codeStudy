package freemarker.core;

class FlowControlException extends RuntimeException {
   FlowControlException() {
   }

   FlowControlException(String message) {
      super(message);
   }
}
