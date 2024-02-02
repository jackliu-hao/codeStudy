/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.antlr.v4.runtime.atn.ATN;
/*     */ import org.antlr.v4.runtime.atn.ATNSimulator;
/*     */ import org.antlr.v4.runtime.atn.ParseInfo;
/*     */ import org.antlr.v4.runtime.misc.Utils;
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
/*     */ public abstract class Recognizer<Symbol, ATNInterpreter extends ATNSimulator>
/*     */ {
/*     */   public static final int EOF = -1;
/*  48 */   private static final Map<Vocabulary, Map<String, Integer>> tokenTypeMapCache = new WeakHashMap<Vocabulary, Map<String, Integer>>();
/*     */   
/*  50 */   private static final Map<String[], Map<String, Integer>> ruleIndexMapCache = (Map)new WeakHashMap<String, Map<String, Integer>>();
/*     */ 
/*     */ 
/*     */   
/*  54 */   private List<ANTLRErrorListener> _listeners = new CopyOnWriteArrayList<ANTLRErrorListener>()
/*     */     {
/*     */     
/*     */     };
/*     */   
/*     */   protected ATNInterpreter _interp;
/*     */   
/*  61 */   private int _stateNumber = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public abstract String[] getTokenNames();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String[] getRuleNames();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Vocabulary getVocabulary() {
/*  82 */     return VocabularyImpl.fromTokenNames(getTokenNames());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Integer> getTokenTypeMap() {
/*  91 */     Vocabulary vocabulary = getVocabulary();
/*  92 */     synchronized (tokenTypeMapCache) {
/*  93 */       Map<String, Integer> result = tokenTypeMapCache.get(vocabulary);
/*  94 */       if (result == null) {
/*  95 */         result = new HashMap<String, Integer>();
/*  96 */         for (int i = 0; i < (getATN()).maxTokenType; i++) {
/*  97 */           String literalName = vocabulary.getLiteralName(i);
/*  98 */           if (literalName != null) {
/*  99 */             result.put(literalName, Integer.valueOf(i));
/*     */           }
/*     */           
/* 102 */           String symbolicName = vocabulary.getSymbolicName(i);
/* 103 */           if (symbolicName != null) {
/* 104 */             result.put(symbolicName, Integer.valueOf(i));
/*     */           }
/*     */         } 
/*     */         
/* 108 */         result.put("EOF", Integer.valueOf(-1));
/* 109 */         result = Collections.unmodifiableMap(result);
/* 110 */         tokenTypeMapCache.put(vocabulary, result);
/*     */       } 
/*     */       
/* 113 */       return result;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Integer> getRuleIndexMap() {
/* 123 */     String[] ruleNames = getRuleNames();
/* 124 */     if (ruleNames == null) {
/* 125 */       throw new UnsupportedOperationException("The current recognizer does not provide a list of rule names.");
/*     */     }
/*     */     
/* 128 */     synchronized (ruleIndexMapCache) {
/* 129 */       Map<String, Integer> result = ruleIndexMapCache.get(ruleNames);
/* 130 */       if (result == null) {
/* 131 */         result = Collections.unmodifiableMap(Utils.toMap(ruleNames));
/* 132 */         ruleIndexMapCache.put(ruleNames, result);
/*     */       } 
/*     */       
/* 135 */       return result;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getTokenType(String tokenName) {
/* 140 */     Integer ttype = getTokenTypeMap().get(tokenName);
/* 141 */     if (ttype != null) return ttype.intValue(); 
/* 142 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSerializedATN() {
/* 153 */     throw new UnsupportedOperationException("there is no serialized ATN");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getGrammarFileName();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ATN getATN();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ATNInterpreter getInterpreter() {
/* 174 */     return this._interp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseInfo getParseInfo() {
/* 183 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInterpreter(ATNInterpreter interpreter) {
/* 193 */     this._interp = interpreter;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getErrorHeader(RecognitionException e) {
/* 198 */     int line = e.getOffendingToken().getLine();
/* 199 */     int charPositionInLine = e.getOffendingToken().getCharPositionInLine();
/* 200 */     return "line " + line + ":" + charPositionInLine;
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
/*     */   
/*     */   @Deprecated
/*     */   public String getTokenErrorDisplay(Token t) {
/* 218 */     if (t == null) return "<no token>"; 
/* 219 */     String s = t.getText();
/* 220 */     if (s == null) {
/* 221 */       if (t.getType() == -1) {
/* 222 */         s = "<EOF>";
/*     */       } else {
/*     */         
/* 225 */         s = "<" + t.getType() + ">";
/*     */       } 
/*     */     }
/* 228 */     s = s.replace("\n", "\\n");
/* 229 */     s = s.replace("\r", "\\r");
/* 230 */     s = s.replace("\t", "\\t");
/* 231 */     return "'" + s + "'";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addErrorListener(ANTLRErrorListener listener) {
/* 238 */     if (listener == null) {
/* 239 */       throw new NullPointerException("listener cannot be null.");
/*     */     }
/*     */     
/* 242 */     this._listeners.add(listener);
/*     */   }
/*     */   
/*     */   public void removeErrorListener(ANTLRErrorListener listener) {
/* 246 */     this._listeners.remove(listener);
/*     */   }
/*     */   
/*     */   public void removeErrorListeners() {
/* 250 */     this._listeners.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<? extends ANTLRErrorListener> getErrorListeners() {
/* 255 */     return this._listeners;
/*     */   }
/*     */   
/*     */   public ANTLRErrorListener getErrorListenerDispatch() {
/* 259 */     return new ProxyErrorListener(getErrorListeners());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean sempred(RuleContext _localctx, int ruleIndex, int actionIndex) {
/* 265 */     return true;
/*     */   }
/*     */   
/*     */   public boolean precpred(RuleContext localctx, int precedence) {
/* 269 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {}
/*     */   
/*     */   public final int getState() {
/* 276 */     return this._stateNumber;
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
/*     */   public final void setState(int atnState) {
/* 288 */     this._stateNumber = atnState;
/*     */   }
/*     */   
/*     */   public abstract IntStream getInputStream();
/*     */   
/*     */   public abstract void setInputStream(IntStream paramIntStream);
/*     */   
/*     */   public abstract TokenFactory<?> getTokenFactory();
/*     */   
/*     */   public abstract void setTokenFactory(TokenFactory<?> paramTokenFactory);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\Recognizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */