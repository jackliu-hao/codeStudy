/*     */ package cn.hutool.script;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Reader;
/*     */ import javax.script.Bindings;
/*     */ import javax.script.Compilable;
/*     */ import javax.script.CompiledScript;
/*     */ import javax.script.Invocable;
/*     */ import javax.script.ScriptContext;
/*     */ import javax.script.ScriptEngine;
/*     */ import javax.script.ScriptEngineFactory;
/*     */ import javax.script.ScriptEngineManager;
/*     */ import javax.script.ScriptException;
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
/*     */ public class FullSupportScriptEngine
/*     */   implements ScriptEngine, Compilable, Invocable
/*     */ {
/*     */   ScriptEngine engine;
/*     */   
/*     */   public FullSupportScriptEngine(ScriptEngine engine) {
/*  33 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FullSupportScriptEngine(String nameOrExtOrMime) {
/*  42 */     ScriptEngineManager manager = new ScriptEngineManager();
/*  43 */     ScriptEngine engine = manager.getEngineByName(nameOrExtOrMime);
/*  44 */     if (null == engine) {
/*  45 */       engine = manager.getEngineByExtension(nameOrExtOrMime);
/*     */     }
/*  47 */     if (null == engine) {
/*  48 */       engine = manager.getEngineByMimeType(nameOrExtOrMime);
/*     */     }
/*  50 */     if (null == engine) {
/*  51 */       throw new NullPointerException(StrUtil.format("Script for [{}] not support !", new Object[] { nameOrExtOrMime }));
/*     */     }
/*  53 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
/*  59 */     return ((Invocable)this.engine).invokeMethod(thiz, name, args);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
/*  64 */     return ((Invocable)this.engine).invokeFunction(name, args);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getInterface(Class<T> clasz) {
/*  69 */     return ((Invocable)this.engine).getInterface(clasz);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getInterface(Object thiz, Class<T> clasz) {
/*  74 */     return ((Invocable)this.engine).getInterface(thiz, clasz);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public CompiledScript compile(String script) throws ScriptException {
/*  80 */     return ((Compilable)this.engine).compile(script);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompiledScript compile(Reader script) throws ScriptException {
/*  85 */     return ((Compilable)this.engine).compile(script);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object eval(String script, ScriptContext context) throws ScriptException {
/*  91 */     return this.engine.eval(script, context);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object eval(Reader reader, ScriptContext context) throws ScriptException {
/*  96 */     return this.engine.eval(reader, context);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object eval(String script) throws ScriptException {
/* 101 */     return this.engine.eval(script);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object eval(Reader reader) throws ScriptException {
/* 106 */     return this.engine.eval(reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object eval(String script, Bindings n) throws ScriptException {
/* 111 */     return this.engine.eval(script, n);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object eval(Reader reader, Bindings n) throws ScriptException {
/* 116 */     return this.engine.eval(reader, n);
/*     */   }
/*     */ 
/*     */   
/*     */   public void put(String key, Object value) {
/* 121 */     this.engine.put(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object get(String key) {
/* 126 */     return this.engine.get(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public Bindings getBindings(int scope) {
/* 131 */     return this.engine.getBindings(scope);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBindings(Bindings bindings, int scope) {
/* 136 */     this.engine.setBindings(bindings, scope);
/*     */   }
/*     */ 
/*     */   
/*     */   public Bindings createBindings() {
/* 141 */     return this.engine.createBindings();
/*     */   }
/*     */ 
/*     */   
/*     */   public ScriptContext getContext() {
/* 146 */     return this.engine.getContext();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContext(ScriptContext context) {
/* 151 */     this.engine.setContext(context);
/*     */   }
/*     */ 
/*     */   
/*     */   public ScriptEngineFactory getFactory() {
/* 156 */     return this.engine.getFactory();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\script\FullSupportScriptEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */