package ch.qos.logback.classic.joran.action;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.JNDIUtil;
import ch.qos.logback.core.util.OptionHelper;
import javax.naming.Context;
import javax.naming.NamingException;
import org.xml.sax.Attributes;

public class InsertFromJNDIAction extends Action {
   public static final String ENV_ENTRY_NAME_ATTR = "env-entry-name";
   public static final String AS_ATTR = "as";

   public void begin(InterpretationContext ec, String name, Attributes attributes) {
      int errorCount = 0;
      String envEntryName = ec.subst(attributes.getValue("env-entry-name"));
      String asKey = ec.subst(attributes.getValue("as"));
      String scopeStr = attributes.getValue("scope");
      ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
      String lineColStr;
      if (OptionHelper.isEmpty(envEntryName)) {
         lineColStr = this.getLineColStr(ec);
         this.addError("[env-entry-name] missing, around " + lineColStr);
         ++errorCount;
      }

      if (OptionHelper.isEmpty(asKey)) {
         lineColStr = this.getLineColStr(ec);
         this.addError("[as] missing, around " + lineColStr);
         ++errorCount;
      }

      if (errorCount == 0) {
         try {
            Context ctx = JNDIUtil.getInitialContext();
            String envEntryValue = JNDIUtil.lookupString(ctx, envEntryName);
            if (OptionHelper.isEmpty(envEntryValue)) {
               this.addError("[" + envEntryName + "] has null or empty value");
            } else {
               this.addInfo("Setting variable [" + asKey + "] to [" + envEntryValue + "] in [" + scope + "] scope");
               ActionUtil.setProperty(ec, asKey, envEntryValue, scope);
            }
         } catch (NamingException var11) {
            this.addError("Failed to lookup JNDI env-entry [" + envEntryName + "]", var11);
         }

      }
   }

   public void end(InterpretationContext ec, String name) {
   }
}
