package cn.hutool.script;

import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.StrUtil;
import java.lang.invoke.SerializedLambda;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptUtil {
   private static final ScriptEngineManager MANAGER = new ScriptEngineManager();
   private static final WeakConcurrentMap<String, ScriptEngine> CACHE = new WeakConcurrentMap();

   public static ScriptEngine getScript(String nameOrExtOrMime) {
      return (ScriptEngine)CACHE.computeIfAbsent(nameOrExtOrMime, () -> {
         return createScript(nameOrExtOrMime);
      });
   }

   public static ScriptEngine createScript(String nameOrExtOrMime) {
      ScriptEngine engine = MANAGER.getEngineByName(nameOrExtOrMime);
      if (null == engine) {
         engine = MANAGER.getEngineByExtension(nameOrExtOrMime);
      }

      if (null == engine) {
         engine = MANAGER.getEngineByMimeType(nameOrExtOrMime);
      }

      if (null == engine) {
         throw new NullPointerException(StrUtil.format("Script for [{}] not support !", new Object[]{nameOrExtOrMime}));
      } else {
         return engine;
      }
   }

   public static JavaScriptEngine getJavaScriptEngine() {
      return new JavaScriptEngine();
   }

   public static ScriptEngine getJsEngine() {
      return getScript("js");
   }

   public static ScriptEngine createJsEngine() {
      return createScript("js");
   }

   public static ScriptEngine getPythonEngine() {
      System.setProperty("python.import.site", "false");
      return getScript("python");
   }

   public static ScriptEngine createPythonEngine() {
      System.setProperty("python.import.site", "false");
      return createScript("python");
   }

   public static ScriptEngine getLuaEngine() {
      return getScript("lua");
   }

   public static ScriptEngine createLuaEngine() {
      return createScript("lua");
   }

   public static ScriptEngine getGroovyEngine() {
      return getScript("groovy");
   }

   public static ScriptEngine createGroovyEngine() {
      return createScript("groovy");
   }

   public static Invocable evalInvocable(String script) throws ScriptRuntimeException {
      ScriptEngine jsEngine = getJsEngine();

      Object eval;
      try {
         eval = jsEngine.eval(script);
      } catch (ScriptException var4) {
         throw new ScriptRuntimeException(var4);
      }

      if (eval instanceof Invocable) {
         return (Invocable)eval;
      } else if (jsEngine instanceof Invocable) {
         return (Invocable)jsEngine;
      } else {
         throw new ScriptRuntimeException("Script is not invocable !");
      }
   }

   public static Object eval(String script) throws ScriptRuntimeException {
      try {
         return getJsEngine().eval(script);
      } catch (ScriptException var2) {
         throw new ScriptRuntimeException(var2);
      }
   }

   public static Object eval(String script, ScriptContext context) throws ScriptRuntimeException {
      try {
         return getJsEngine().eval(script, context);
      } catch (ScriptException var3) {
         throw new ScriptRuntimeException(var3);
      }
   }

   public static Object eval(String script, Bindings bindings) throws ScriptRuntimeException {
      try {
         return getJsEngine().eval(script, bindings);
      } catch (ScriptException var3) {
         throw new ScriptRuntimeException(var3);
      }
   }

   public static Object invoke(String script, String func, Object... args) {
      Invocable eval = evalInvocable(script);

      try {
         return eval.invokeFunction(func, args);
      } catch (NoSuchMethodException | ScriptException var5) {
         throw new ScriptRuntimeException(var5);
      }
   }

   public static CompiledScript compile(String script) throws ScriptRuntimeException {
      try {
         return compile(getJsEngine(), script);
      } catch (ScriptException var2) {
         throw new ScriptRuntimeException(var2);
      }
   }

   public static CompiledScript compile(ScriptEngine engine, String script) throws ScriptException {
      if (engine instanceof Compilable) {
         Compilable compEngine = (Compilable)engine;
         return compEngine.compile(script);
      } else {
         return null;
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "lambda$getScript$f42252b3$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/script/ScriptUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;)Ljavax/script/ScriptEngine;")) {
               return () -> {
                  return createScript(nameOrExtOrMime);
               };
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
