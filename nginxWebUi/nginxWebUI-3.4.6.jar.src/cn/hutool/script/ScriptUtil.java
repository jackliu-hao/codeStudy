/*     */ package cn.hutool.script;
/*     */ 
/*     */ import cn.hutool.core.map.WeakConcurrentMap;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import javax.script.Bindings;
/*     */ import javax.script.Compilable;
/*     */ import javax.script.CompiledScript;
/*     */ import javax.script.Invocable;
/*     */ import javax.script.ScriptContext;
/*     */ import javax.script.ScriptEngine;
/*     */ import javax.script.ScriptEngineManager;
/*     */ import javax.script.ScriptException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ScriptUtil
/*     */ {
/*  22 */   private static final ScriptEngineManager MANAGER = new ScriptEngineManager();
/*  23 */   private static final WeakConcurrentMap<String, ScriptEngine> CACHE = new WeakConcurrentMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScriptEngine getScript(String nameOrExtOrMime) {
/*  32 */     return (ScriptEngine)CACHE.computeIfAbsent(nameOrExtOrMime, () -> createScript(nameOrExtOrMime));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScriptEngine createScript(String nameOrExtOrMime) {
/*  43 */     ScriptEngine engine = MANAGER.getEngineByName(nameOrExtOrMime);
/*  44 */     if (null == engine) {
/*  45 */       engine = MANAGER.getEngineByExtension(nameOrExtOrMime);
/*     */     }
/*  47 */     if (null == engine) {
/*  48 */       engine = MANAGER.getEngineByMimeType(nameOrExtOrMime);
/*     */     }
/*  50 */     if (null == engine) {
/*  51 */       throw new NullPointerException(StrUtil.format("Script for [{}] not support !", new Object[] { nameOrExtOrMime }));
/*     */     }
/*  53 */     return engine;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JavaScriptEngine getJavaScriptEngine() {
/*  62 */     return new JavaScriptEngine();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScriptEngine getJsEngine() {
/*  72 */     return getScript("js");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScriptEngine createJsEngine() {
/*  82 */     return createScript("js");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScriptEngine getPythonEngine() {
/*  93 */     System.setProperty("python.import.site", "false");
/*  94 */     return getScript("python");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScriptEngine createPythonEngine() {
/* 105 */     System.setProperty("python.import.site", "false");
/* 106 */     return createScript("python");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScriptEngine getLuaEngine() {
/* 117 */     return getScript("lua");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScriptEngine createLuaEngine() {
/* 128 */     return createScript("lua");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScriptEngine getGroovyEngine() {
/* 139 */     return getScript("groovy");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScriptEngine createGroovyEngine() {
/* 150 */     return createScript("groovy");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Invocable evalInvocable(String script) throws ScriptRuntimeException {
/*     */     Object eval;
/* 167 */     ScriptEngine jsEngine = getJsEngine();
/*     */     
/*     */     try {
/* 170 */       eval = jsEngine.eval(script);
/* 171 */     } catch (ScriptException e) {
/* 172 */       throw new ScriptRuntimeException(e);
/*     */     } 
/* 174 */     if (eval instanceof Invocable)
/* 175 */       return (Invocable)eval; 
/* 176 */     if (jsEngine instanceof Invocable) {
/* 177 */       return (Invocable)jsEngine;
/*     */     }
/* 179 */     throw new ScriptRuntimeException("Script is not invocable !");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object eval(String script) throws ScriptRuntimeException {
/*     */     try {
/* 192 */       return getJsEngine().eval(script);
/* 193 */     } catch (ScriptException e) {
/* 194 */       throw new ScriptRuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object eval(String script, ScriptContext context) throws ScriptRuntimeException {
/*     */     try {
/* 209 */       return getJsEngine().eval(script, context);
/* 210 */     } catch (ScriptException e) {
/* 211 */       throw new ScriptRuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object eval(String script, Bindings bindings) throws ScriptRuntimeException {
/*     */     try {
/* 226 */       return getJsEngine().eval(script, bindings);
/* 227 */     } catch (ScriptException e) {
/* 228 */       throw new ScriptRuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object invoke(String script, String func, Object... args) {
/* 242 */     Invocable eval = evalInvocable(script);
/*     */     try {
/* 244 */       return eval.invokeFunction(func, args);
/* 245 */     } catch (ScriptException|NoSuchMethodException e) {
/* 246 */       throw new ScriptRuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CompiledScript compile(String script) throws ScriptRuntimeException {
/*     */     try {
/* 260 */       return compile(getJsEngine(), script);
/* 261 */     } catch (ScriptException e) {
/* 262 */       throw new ScriptRuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CompiledScript compile(ScriptEngine engine, String script) throws ScriptException {
/* 275 */     if (engine instanceof Compilable) {
/* 276 */       Compilable compEngine = (Compilable)engine;
/* 277 */       return compEngine.compile(script);
/*     */     } 
/* 279 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\script\ScriptUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */