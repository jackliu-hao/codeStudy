package freemarker.ext.rhino;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class RhinoFunctionModel extends RhinoScriptableModel implements TemplateMethodModelEx {
   private final Scriptable fnThis;

   public RhinoFunctionModel(Function function, Scriptable fnThis, BeansWrapper wrapper) {
      super(function, wrapper);
      this.fnThis = fnThis;
   }

   public Object exec(List arguments) throws TemplateModelException {
      Context cx = Context.getCurrentContext();
      Object[] args = arguments.toArray();
      BeansWrapper wrapper = this.getWrapper();

      for(int i = 0; i < args.length; ++i) {
         args[i] = wrapper.unwrap((TemplateModel)args[i]);
      }

      return wrapper.wrap(((Function)this.getScriptable()).call(cx, ScriptableObject.getTopLevelScope(this.fnThis), this.fnThis, args));
   }
}
