/*     */ package ch.qos.logback.core.joran.spi;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.joran.action.Action;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.util.OptionHelper;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public class SimpleRuleStore
/*     */   extends ContextAwareBase
/*     */   implements RuleStore
/*     */ {
/*  34 */   static String KLEENE_STAR = "*";
/*     */ 
/*     */   
/*  37 */   HashMap<ElementSelector, List<Action>> rules = new HashMap<ElementSelector, List<Action>>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleRuleStore(Context context) {
/*  43 */     setContext(context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRule(ElementSelector elementSelector, Action action) {
/*  51 */     action.setContext(this.context);
/*     */     
/*  53 */     List<Action> a4p = this.rules.get(elementSelector);
/*     */     
/*  55 */     if (a4p == null) {
/*  56 */       a4p = new ArrayList<Action>();
/*  57 */       this.rules.put(elementSelector, a4p);
/*     */     } 
/*     */     
/*  60 */     a4p.add(action);
/*     */   }
/*     */   
/*     */   public void addRule(ElementSelector elementSelector, String actionClassName) {
/*  64 */     Action action = null;
/*     */     
/*     */     try {
/*  67 */       action = (Action)OptionHelper.instantiateByClassName(actionClassName, Action.class, this.context);
/*  68 */     } catch (Exception e) {
/*  69 */       addError("Could not instantiate class [" + actionClassName + "]", e);
/*     */     } 
/*  71 */     if (action != null) {
/*  72 */       addRule(elementSelector, action);
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
/*     */   public List<Action> matchActions(ElementPath elementPath) {
/*     */     List<Action> actionList;
/*  86 */     if ((actionList = fullPathMatch(elementPath)) != null)
/*  87 */       return actionList; 
/*  88 */     if ((actionList = suffixMatch(elementPath)) != null)
/*  89 */       return actionList; 
/*  90 */     if ((actionList = prefixMatch(elementPath)) != null)
/*  91 */       return actionList; 
/*  92 */     if ((actionList = middleMatch(elementPath)) != null) {
/*  93 */       return actionList;
/*     */     }
/*  95 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   List<Action> fullPathMatch(ElementPath elementPath) {
/* 100 */     for (ElementSelector selector : this.rules.keySet()) {
/* 101 */       if (selector.fullPathMatch(elementPath))
/* 102 */         return this.rules.get(selector); 
/*     */     } 
/* 104 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   List<Action> suffixMatch(ElementPath elementPath) {
/* 109 */     int max = 0;
/* 110 */     ElementSelector longestMatchingElementSelector = null;
/*     */     
/* 112 */     for (ElementSelector selector : this.rules.keySet()) {
/* 113 */       if (isSuffixPattern(selector)) {
/* 114 */         int r = selector.getTailMatchLength(elementPath);
/* 115 */         if (r > max) {
/* 116 */           max = r;
/* 117 */           longestMatchingElementSelector = selector;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 122 */     if (longestMatchingElementSelector != null) {
/* 123 */       return this.rules.get(longestMatchingElementSelector);
/*     */     }
/* 125 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isSuffixPattern(ElementSelector p) {
/* 130 */     return (p.size() > 1 && p.get(0).equals(KLEENE_STAR));
/*     */   }
/*     */   
/*     */   List<Action> prefixMatch(ElementPath elementPath) {
/* 134 */     int max = 0;
/* 135 */     ElementSelector longestMatchingElementSelector = null;
/*     */     
/* 137 */     for (ElementSelector selector : this.rules.keySet()) {
/* 138 */       String last = selector.peekLast();
/* 139 */       if (isKleeneStar(last)) {
/* 140 */         int r = selector.getPrefixMatchLength(elementPath);
/*     */         
/* 142 */         if (r == selector.size() - 1 && r > max) {
/* 143 */           max = r;
/* 144 */           longestMatchingElementSelector = selector;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 149 */     if (longestMatchingElementSelector != null) {
/* 150 */       return this.rules.get(longestMatchingElementSelector);
/*     */     }
/* 152 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isKleeneStar(String last) {
/* 157 */     return KLEENE_STAR.equals(last);
/*     */   }
/*     */ 
/*     */   
/*     */   List<Action> middleMatch(ElementPath path) {
/* 162 */     int max = 0;
/* 163 */     ElementSelector longestMatchingElementSelector = null;
/*     */     
/* 165 */     for (ElementSelector selector : this.rules.keySet()) {
/* 166 */       String last = selector.peekLast();
/* 167 */       String first = null;
/* 168 */       if (selector.size() > 1) {
/* 169 */         first = selector.get(0);
/*     */       }
/* 171 */       if (isKleeneStar(last) && isKleeneStar(first)) {
/* 172 */         List<String> copyOfPartList = selector.getCopyOfPartList();
/* 173 */         if (copyOfPartList.size() > 2) {
/* 174 */           copyOfPartList.remove(0);
/* 175 */           copyOfPartList.remove(copyOfPartList.size() - 1);
/*     */         } 
/*     */         
/* 178 */         int r = 0;
/* 179 */         ElementSelector clone = new ElementSelector(copyOfPartList);
/* 180 */         if (clone.isContainedIn(path)) {
/* 181 */           r = clone.size();
/*     */         }
/* 183 */         if (r > max) {
/* 184 */           max = r;
/* 185 */           longestMatchingElementSelector = selector;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 190 */     if (longestMatchingElementSelector != null) {
/* 191 */       return this.rules.get(longestMatchingElementSelector);
/*     */     }
/* 193 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 198 */     String TAB = "  ";
/*     */     
/* 200 */     StringBuilder retValue = new StringBuilder();
/*     */     
/* 202 */     retValue.append("SimpleRuleStore ( ").append("rules = ").append(this.rules).append("  ").append(" )");
/*     */     
/* 204 */     return retValue.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\SimpleRuleStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */