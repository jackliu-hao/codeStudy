/*     */ package ch.qos.logback.core.joran.spi;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.joran.event.InPlayListener;
/*     */ import ch.qos.logback.core.joran.event.SaxEvent;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.spi.PropertyContainer;
/*     */ import ch.qos.logback.core.util.OptionHelper;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Stack;
/*     */ import org.xml.sax.Locator;
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
/*     */ public class InterpretationContext
/*     */   extends ContextAwareBase
/*     */   implements PropertyContainer
/*     */ {
/*     */   Stack<Object> objectStack;
/*     */   Map<String, Object> objectMap;
/*     */   Map<String, String> propertiesMap;
/*     */   Interpreter joranInterpreter;
/*  47 */   final List<InPlayListener> listenerList = new ArrayList<InPlayListener>();
/*  48 */   DefaultNestedComponentRegistry defaultNestedComponentRegistry = new DefaultNestedComponentRegistry();
/*     */   
/*     */   public InterpretationContext(Context context, Interpreter joranInterpreter) {
/*  51 */     this.context = context;
/*  52 */     this.joranInterpreter = joranInterpreter;
/*  53 */     this.objectStack = new Stack();
/*  54 */     this.objectMap = new HashMap<String, Object>(5);
/*  55 */     this.propertiesMap = new HashMap<String, String>(5);
/*     */   }
/*     */   
/*     */   public DefaultNestedComponentRegistry getDefaultNestedComponentRegistry() {
/*  59 */     return this.defaultNestedComponentRegistry;
/*     */   }
/*     */   
/*     */   public Map<String, String> getCopyOfPropertyMap() {
/*  63 */     return new HashMap<String, String>(this.propertiesMap);
/*     */   }
/*     */   
/*     */   void setPropertiesMap(Map<String, String> propertiesMap) {
/*  67 */     this.propertiesMap = propertiesMap;
/*     */   }
/*     */   
/*     */   String updateLocationInfo(String msg) {
/*  71 */     Locator locator = this.joranInterpreter.getLocator();
/*     */     
/*  73 */     if (locator != null) {
/*  74 */       return msg + locator.getLineNumber() + ":" + locator.getColumnNumber();
/*     */     }
/*  76 */     return msg;
/*     */   }
/*     */ 
/*     */   
/*     */   public Locator getLocator() {
/*  81 */     return this.joranInterpreter.getLocator();
/*     */   }
/*     */   
/*     */   public Interpreter getJoranInterpreter() {
/*  85 */     return this.joranInterpreter;
/*     */   }
/*     */   
/*     */   public Stack<Object> getObjectStack() {
/*  89 */     return this.objectStack;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  93 */     return this.objectStack.isEmpty();
/*     */   }
/*     */   
/*     */   public Object peekObject() {
/*  97 */     return this.objectStack.peek();
/*     */   }
/*     */   
/*     */   public void pushObject(Object o) {
/* 101 */     this.objectStack.push(o);
/*     */   }
/*     */   
/*     */   public Object popObject() {
/* 105 */     return this.objectStack.pop();
/*     */   }
/*     */   
/*     */   public Object getObject(int i) {
/* 109 */     return this.objectStack.get(i);
/*     */   }
/*     */   
/*     */   public Map<String, Object> getObjectMap() {
/* 113 */     return this.objectMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addSubstitutionProperty(String key, String value) {
/* 121 */     if (key == null || value == null) {
/*     */       return;
/*     */     }
/*     */     
/* 125 */     value = value.trim();
/* 126 */     this.propertiesMap.put(key, value);
/*     */   }
/*     */   
/*     */   public void addSubstitutionProperties(Properties props) {
/* 130 */     if (props == null) {
/*     */       return;
/*     */     }
/* 133 */     for (Object keyObject : props.keySet()) {
/* 134 */       String key = (String)keyObject;
/* 135 */       String val = props.getProperty(key);
/* 136 */       addSubstitutionProperty(key, val);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getProperty(String key) {
/* 145 */     String v = this.propertiesMap.get(key);
/* 146 */     if (v != null) {
/* 147 */       return v;
/*     */     }
/* 149 */     return this.context.getProperty(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public String subst(String value) {
/* 154 */     if (value == null) {
/* 155 */       return null;
/*     */     }
/* 157 */     return OptionHelper.substVars(value, this, (PropertyContainer)this.context);
/*     */   }
/*     */   
/*     */   public boolean isListenerListEmpty() {
/* 161 */     return this.listenerList.isEmpty();
/*     */   }
/*     */   
/*     */   public void addInPlayListener(InPlayListener ipl) {
/* 165 */     if (this.listenerList.contains(ipl)) {
/* 166 */       addWarn("InPlayListener " + ipl + " has been already registered");
/*     */     } else {
/* 168 */       this.listenerList.add(ipl);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean removeInPlayListener(InPlayListener ipl) {
/* 173 */     return this.listenerList.remove(ipl);
/*     */   }
/*     */   
/*     */   void fireInPlay(SaxEvent event) {
/* 177 */     for (InPlayListener ipl : this.listenerList)
/* 178 */       ipl.inPlay(event); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\InterpretationContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */