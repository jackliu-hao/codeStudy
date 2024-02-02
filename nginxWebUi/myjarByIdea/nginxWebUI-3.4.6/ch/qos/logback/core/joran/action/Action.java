package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.spi.ContextAwareBase;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

public abstract class Action extends ContextAwareBase {
   public static final String NAME_ATTRIBUTE = "name";
   public static final String KEY_ATTRIBUTE = "key";
   public static final String VALUE_ATTRIBUTE = "value";
   public static final String FILE_ATTRIBUTE = "file";
   public static final String CLASS_ATTRIBUTE = "class";
   public static final String PATTERN_ATTRIBUTE = "pattern";
   public static final String SCOPE_ATTRIBUTE = "scope";
   public static final String ACTION_CLASS_ATTRIBUTE = "actionClass";

   public abstract void begin(InterpretationContext var1, String var2, Attributes var3) throws ActionException;

   public void body(InterpretationContext ic, String body) throws ActionException {
   }

   public abstract void end(InterpretationContext var1, String var2) throws ActionException;

   public String toString() {
      return this.getClass().getName();
   }

   protected int getColumnNumber(InterpretationContext ic) {
      Interpreter ji = ic.getJoranInterpreter();
      Locator locator = ji.getLocator();
      return locator != null ? locator.getColumnNumber() : -1;
   }

   protected int getLineNumber(InterpretationContext ic) {
      Interpreter ji = ic.getJoranInterpreter();
      Locator locator = ji.getLocator();
      return locator != null ? locator.getLineNumber() : -1;
   }

   protected String getLineColStr(InterpretationContext ic) {
      return "line: " + this.getLineNumber(ic) + ", column: " + this.getColumnNumber(ic);
   }
}
