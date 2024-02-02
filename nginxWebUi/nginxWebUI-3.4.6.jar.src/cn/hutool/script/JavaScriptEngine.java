/*     */ package cn.hutool.script;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import javax.script.Bindings;
/*     */ import javax.script.Compilable;
/*     */ import javax.script.CompiledScript;
/*     */ import javax.script.Invocable;
/*     */ import javax.script.ScriptContext;
/*     */ import javax.script.ScriptEngineFactory;
/*     */ import javax.script.ScriptException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JavaScriptEngine
/*     */   extends FullSupportScriptEngine
/*     */ {
/*     */   public JavaScriptEngine() {
/*  20 */     super(ScriptUtil.createJsEngine());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JavaScriptEngine instance() {
/*  29 */     return new JavaScriptEngine();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
/*  35 */     return ((Invocable)this.engine).invokeMethod(thiz, name, args);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
/*  40 */     return ((Invocable)this.engine).invokeFunction(name, args);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getInterface(Class<T> clasz) {
/*  45 */     return ((Invocable)this.engine).getInterface(clasz);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getInterface(Object thiz, Class<T> clasz) {
/*  50 */     return ((Invocable)this.engine).getInterface(thiz, clasz);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public CompiledScript compile(String script) throws ScriptException {
/*  56 */     return ((Compilable)this.engine).compile(script);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompiledScript compile(Reader script) throws ScriptException {
/*  61 */     return ((Compilable)this.engine).compile(script);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object eval(String script, ScriptContext context) throws ScriptException {
/*  67 */     return this.engine.eval(script, context);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object eval(Reader reader, ScriptContext context) throws ScriptException {
/*  72 */     return this.engine.eval(reader, context);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object eval(String script) throws ScriptException {
/*  77 */     return this.engine.eval(script);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object eval(Reader reader) throws ScriptException {
/*  82 */     return this.engine.eval(reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object eval(String script, Bindings n) throws ScriptException {
/*  87 */     return this.engine.eval(script, n);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object eval(Reader reader, Bindings n) throws ScriptException {
/*  92 */     return this.engine.eval(reader, n);
/*     */   }
/*     */ 
/*     */   
/*     */   public void put(String key, Object value) {
/*  97 */     this.engine.put(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object get(String key) {
/* 102 */     return this.engine.get(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public Bindings getBindings(int scope) {
/* 107 */     return this.engine.getBindings(scope);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBindings(Bindings bindings, int scope) {
/* 112 */     this.engine.setBindings(bindings, scope);
/*     */   }
/*     */ 
/*     */   
/*     */   public Bindings createBindings() {
/* 117 */     return this.engine.createBindings();
/*     */   }
/*     */ 
/*     */   
/*     */   public ScriptContext getContext() {
/* 122 */     return this.engine.getContext();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContext(ScriptContext context) {
/* 127 */     this.engine.setContext(context);
/*     */   }
/*     */ 
/*     */   
/*     */   public ScriptEngineFactory getFactory() {
/* 132 */     return this.engine.getFactory();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\script\JavaScriptEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */