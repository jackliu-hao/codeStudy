/*    */ package ch.qos.logback.core.joran.conditional;
/*    */ 
/*    */ import ch.qos.logback.core.spi.ContextAwareBase;
/*    */ import ch.qos.logback.core.spi.PropertyContainer;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.codehaus.commons.compiler.CompileException;
/*    */ import org.codehaus.janino.ClassBodyEvaluator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PropertyEvalScriptBuilder
/*    */   extends ContextAwareBase
/*    */ {
/* 29 */   private static String SCRIPT_PREFIX = "public boolean evaluate() { return ";
/* 30 */   private static String SCRIPT_SUFFIX = "; }";
/*    */   
/*    */   final PropertyContainer localPropContainer;
/*    */   
/*    */   Map<String, String> map;
/*    */ 
/*    */   
/*    */   PropertyEvalScriptBuilder(PropertyContainer localPropContainer) {
/* 38 */     this.map = new HashMap<String, String>();
/*    */     this.localPropContainer = localPropContainer;
/*    */   }
/*    */   
/*    */   public Condition build(String script) throws IllegalAccessException, CompileException, InstantiationException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
/* 43 */     ClassBodyEvaluator cbe = new ClassBodyEvaluator();
/* 44 */     cbe.setImplementedInterfaces(new Class[] { Condition.class });
/* 45 */     cbe.setExtendedClass(PropertyWrapperForScripts.class);
/* 46 */     cbe.setParentClassLoader(ClassBodyEvaluator.class.getClassLoader());
/* 47 */     cbe.cook(SCRIPT_PREFIX + script + SCRIPT_SUFFIX);
/*    */     
/* 49 */     Class<?> clazz = cbe.getClazz();
/* 50 */     Condition instance = (Condition)clazz.newInstance();
/* 51 */     Method setMapMethod = clazz.getMethod("setPropertyContainers", new Class[] { PropertyContainer.class, PropertyContainer.class });
/* 52 */     setMapMethod.invoke(instance, new Object[] { this.localPropContainer, this.context });
/*    */     
/* 54 */     return instance;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\conditional\PropertyEvalScriptBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */