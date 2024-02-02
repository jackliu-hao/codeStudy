/*     */ package ch.qos.logback.core.joran.spi;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.joran.action.Action;
/*     */ import ch.qos.logback.core.joran.action.ImplicitAction;
/*     */ import ch.qos.logback.core.joran.event.BodyEvent;
/*     */ import ch.qos.logback.core.joran.event.EndEvent;
/*     */ import ch.qos.logback.core.joran.event.StartEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Stack;
/*     */ import java.util.Vector;
/*     */ import org.xml.sax.Attributes;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Interpreter
/*     */ {
/*  68 */   private static List<Action> EMPTY_LIST = new Vector<Action>(0);
/*     */ 
/*     */   
/*     */   private final RuleStore ruleStore;
/*     */ 
/*     */   
/*     */   private final InterpretationContext interpretationContext;
/*     */ 
/*     */   
/*     */   private final ArrayList<ImplicitAction> implicitActions;
/*     */ 
/*     */   
/*     */   private final CAI_WithLocatorSupport cai;
/*     */ 
/*     */   
/*     */   private ElementPath elementPath;
/*     */ 
/*     */   
/*     */   Locator locator;
/*     */   
/*     */   EventPlayer eventPlayer;
/*     */   
/*     */   Stack<List<Action>> actionListStack;
/*     */   
/*  92 */   ElementPath skip = null;
/*     */   
/*     */   public Interpreter(Context context, RuleStore rs, ElementPath initialElementPath) {
/*  95 */     this.cai = new CAI_WithLocatorSupport(context, this);
/*  96 */     this.ruleStore = rs;
/*  97 */     this.interpretationContext = new InterpretationContext(context, this);
/*  98 */     this.implicitActions = new ArrayList<ImplicitAction>(3);
/*  99 */     this.elementPath = initialElementPath;
/* 100 */     this.actionListStack = new Stack<List<Action>>();
/* 101 */     this.eventPlayer = new EventPlayer(this);
/*     */   }
/*     */   
/*     */   public EventPlayer getEventPlayer() {
/* 105 */     return this.eventPlayer;
/*     */   }
/*     */   
/*     */   public void setInterpretationContextPropertiesMap(Map<String, String> propertiesMap) {
/* 109 */     this.interpretationContext.setPropertiesMap(propertiesMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InterpretationContext getExecutionContext() {
/* 116 */     return getInterpretationContext();
/*     */   }
/*     */   
/*     */   public InterpretationContext getInterpretationContext() {
/* 120 */     return this.interpretationContext;
/*     */   }
/*     */ 
/*     */   
/*     */   public void startDocument() {}
/*     */   
/*     */   public void startElement(StartEvent se) {
/* 127 */     setDocumentLocator(se.getLocator());
/* 128 */     startElement(se.namespaceURI, se.localName, se.qName, se.attributes);
/*     */   }
/*     */ 
/*     */   
/*     */   private void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
/* 133 */     String tagName = getTagName(localName, qName);
/* 134 */     this.elementPath.push(tagName);
/*     */     
/* 136 */     if (this.skip != null) {
/*     */       
/* 138 */       pushEmptyActionList();
/*     */       
/*     */       return;
/*     */     } 
/* 142 */     List<Action> applicableActionList = getApplicableActionList(this.elementPath, atts);
/* 143 */     if (applicableActionList != null) {
/* 144 */       this.actionListStack.add(applicableActionList);
/* 145 */       callBeginAction(applicableActionList, tagName, atts);
/*     */     } else {
/*     */       
/* 148 */       pushEmptyActionList();
/* 149 */       String errMsg = "no applicable action for [" + tagName + "], current ElementPath  is [" + this.elementPath + "]";
/* 150 */       this.cai.addError(errMsg);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void pushEmptyActionList() {
/* 158 */     this.actionListStack.add(EMPTY_LIST);
/*     */   }
/*     */ 
/*     */   
/*     */   public void characters(BodyEvent be) {
/* 163 */     setDocumentLocator(be.locator);
/*     */     
/* 165 */     String body = be.getText();
/* 166 */     List<Action> applicableActionList = this.actionListStack.peek();
/*     */     
/* 168 */     if (body != null) {
/* 169 */       body = body.trim();
/* 170 */       if (body.length() > 0)
/*     */       {
/* 172 */         callBodyAction(applicableActionList, body);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void endElement(EndEvent endEvent) {
/* 178 */     setDocumentLocator(endEvent.locator);
/* 179 */     endElement(endEvent.namespaceURI, endEvent.localName, endEvent.qName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void endElement(String namespaceURI, String localName, String qName) {
/* 186 */     List<Action> applicableActionList = this.actionListStack.pop();
/*     */     
/* 188 */     if (this.skip != null) {
/* 189 */       if (this.skip.equals(this.elementPath)) {
/* 190 */         this.skip = null;
/*     */       }
/* 192 */     } else if (applicableActionList != EMPTY_LIST) {
/* 193 */       callEndAction(applicableActionList, getTagName(localName, qName));
/*     */     } 
/*     */ 
/*     */     
/* 197 */     this.elementPath.pop();
/*     */   }
/*     */   
/*     */   public Locator getLocator() {
/* 201 */     return this.locator;
/*     */   }
/*     */   
/*     */   public void setDocumentLocator(Locator l) {
/* 205 */     this.locator = l;
/*     */   }
/*     */   
/*     */   String getTagName(String localName, String qName) {
/* 209 */     String tagName = localName;
/*     */     
/* 211 */     if (tagName == null || tagName.length() < 1) {
/* 212 */       tagName = qName;
/*     */     }
/*     */     
/* 215 */     return tagName;
/*     */   }
/*     */   
/*     */   public void addImplicitAction(ImplicitAction ia) {
/* 219 */     this.implicitActions.add(ia);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   List<Action> lookupImplicitAction(ElementPath elementPath, Attributes attributes, InterpretationContext ec) {
/* 228 */     int len = this.implicitActions.size();
/*     */     
/* 230 */     for (int i = 0; i < len; i++) {
/* 231 */       ImplicitAction ia = this.implicitActions.get(i);
/*     */       
/* 233 */       if (ia.isApplicable(elementPath, attributes, ec)) {
/* 234 */         List<Action> actionList = new ArrayList<Action>(1);
/* 235 */         actionList.add(ia);
/*     */         
/* 237 */         return actionList;
/*     */       } 
/*     */     } 
/*     */     
/* 241 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   List<Action> getApplicableActionList(ElementPath elementPath, Attributes attributes) {
/* 248 */     List<Action> applicableActionList = this.ruleStore.matchActions(elementPath);
/*     */ 
/*     */     
/* 251 */     if (applicableActionList == null) {
/* 252 */       applicableActionList = lookupImplicitAction(elementPath, attributes, this.interpretationContext);
/*     */     }
/*     */     
/* 255 */     return applicableActionList;
/*     */   }
/*     */   
/*     */   void callBeginAction(List<Action> applicableActionList, String tagName, Attributes atts) {
/* 259 */     if (applicableActionList == null) {
/*     */       return;
/*     */     }
/*     */     
/* 263 */     Iterator<Action> i = applicableActionList.iterator();
/* 264 */     while (i.hasNext()) {
/* 265 */       Action action = i.next();
/*     */ 
/*     */       
/*     */       try {
/* 269 */         action.begin(this.interpretationContext, tagName, atts);
/* 270 */       } catch (ActionException e) {
/* 271 */         this.skip = this.elementPath.duplicate();
/* 272 */         this.cai.addError("ActionException in Action for tag [" + tagName + "]", e);
/* 273 */       } catch (RuntimeException e) {
/* 274 */         this.skip = this.elementPath.duplicate();
/* 275 */         this.cai.addError("RuntimeException in Action for tag [" + tagName + "]", e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void callBodyAction(List<Action> applicableActionList, String body) {
/* 281 */     if (applicableActionList == null) {
/*     */       return;
/*     */     }
/* 284 */     Iterator<Action> i = applicableActionList.iterator();
/*     */     
/* 286 */     while (i.hasNext()) {
/* 287 */       Action action = i.next();
/*     */       try {
/* 289 */         action.body(this.interpretationContext, body);
/* 290 */       } catch (ActionException ae) {
/* 291 */         this.cai.addError("Exception in end() methd for action [" + action + "]", ae);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void callEndAction(List<Action> applicableActionList, String tagName) {
/* 297 */     if (applicableActionList == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 302 */     Iterator<Action> i = applicableActionList.iterator();
/*     */     
/* 304 */     while (i.hasNext()) {
/* 305 */       Action action = i.next();
/*     */ 
/*     */       
/*     */       try {
/* 309 */         action.end(this.interpretationContext, tagName);
/* 310 */       } catch (ActionException ae) {
/*     */ 
/*     */         
/* 313 */         this.cai.addError("ActionException in Action for tag [" + tagName + "]", ae);
/* 314 */       } catch (RuntimeException e) {
/*     */         
/* 316 */         this.cai.addError("RuntimeException in Action for tag [" + tagName + "]", e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public RuleStore getRuleStore() {
/* 322 */     return this.ruleStore;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\Interpreter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */