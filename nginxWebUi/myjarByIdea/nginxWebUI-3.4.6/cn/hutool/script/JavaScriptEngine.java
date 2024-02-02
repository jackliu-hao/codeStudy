package cn.hutool.script;

import java.io.Reader;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

public class JavaScriptEngine extends FullSupportScriptEngine {
   public JavaScriptEngine() {
      super(ScriptUtil.createJsEngine());
   }

   public static JavaScriptEngine instance() {
      return new JavaScriptEngine();
   }

   public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
      return ((Invocable)this.engine).invokeMethod(thiz, name, args);
   }

   public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
      return ((Invocable)this.engine).invokeFunction(name, args);
   }

   public <T> T getInterface(Class<T> clasz) {
      return ((Invocable)this.engine).getInterface(clasz);
   }

   public <T> T getInterface(Object thiz, Class<T> clasz) {
      return ((Invocable)this.engine).getInterface(thiz, clasz);
   }

   public CompiledScript compile(String script) throws ScriptException {
      return ((Compilable)this.engine).compile(script);
   }

   public CompiledScript compile(Reader script) throws ScriptException {
      return ((Compilable)this.engine).compile(script);
   }

   public Object eval(String script, ScriptContext context) throws ScriptException {
      return this.engine.eval(script, context);
   }

   public Object eval(Reader reader, ScriptContext context) throws ScriptException {
      return this.engine.eval(reader, context);
   }

   public Object eval(String script) throws ScriptException {
      return this.engine.eval(script);
   }

   public Object eval(Reader reader) throws ScriptException {
      return this.engine.eval(reader);
   }

   public Object eval(String script, Bindings n) throws ScriptException {
      return this.engine.eval(script, n);
   }

   public Object eval(Reader reader, Bindings n) throws ScriptException {
      return this.engine.eval(reader, n);
   }

   public void put(String key, Object value) {
      this.engine.put(key, value);
   }

   public Object get(String key) {
      return this.engine.get(key);
   }

   public Bindings getBindings(int scope) {
      return this.engine.getBindings(scope);
   }

   public void setBindings(Bindings bindings, int scope) {
      this.engine.setBindings(bindings, scope);
   }

   public Bindings createBindings() {
      return this.engine.createBindings();
   }

   public ScriptContext getContext() {
      return this.engine.getContext();
   }

   public void setContext(ScriptContext context) {
      this.engine.setContext(context);
   }

   public ScriptEngineFactory getFactory() {
      return this.engine.getFactory();
   }
}
