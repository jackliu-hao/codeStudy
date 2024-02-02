package ch.qos.logback.classic.pattern;

public abstract class ThrowableHandlingConverter extends ClassicConverter {
   boolean handlesThrowable() {
      return true;
   }
}
