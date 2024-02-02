package org.antlr.v4.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.ParseInfo;
import org.antlr.v4.runtime.misc.Utils;

public abstract class Recognizer<Symbol, ATNInterpreter extends ATNSimulator> {
   public static final int EOF = -1;
   private static final Map<Vocabulary, Map<String, Integer>> tokenTypeMapCache = new WeakHashMap();
   private static final Map<String[], Map<String, Integer>> ruleIndexMapCache = new WeakHashMap();
   private List<ANTLRErrorListener> _listeners = new CopyOnWriteArrayList<ANTLRErrorListener>() {
      {
         this.add(ConsoleErrorListener.INSTANCE);
      }
   };
   protected ATNInterpreter _interp;
   private int _stateNumber = -1;

   /** @deprecated */
   @Deprecated
   public abstract String[] getTokenNames();

   public abstract String[] getRuleNames();

   public Vocabulary getVocabulary() {
      return VocabularyImpl.fromTokenNames(this.getTokenNames());
   }

   public Map<String, Integer> getTokenTypeMap() {
      Vocabulary vocabulary = this.getVocabulary();
      synchronized(tokenTypeMapCache) {
         Map<String, Integer> result = (Map)tokenTypeMapCache.get(vocabulary);
         if (result == null) {
            Map<String, Integer> result = new HashMap();

            for(int i = 0; i < this.getATN().maxTokenType; ++i) {
               String literalName = vocabulary.getLiteralName(i);
               if (literalName != null) {
                  result.put(literalName, i);
               }

               String symbolicName = vocabulary.getSymbolicName(i);
               if (symbolicName != null) {
                  result.put(symbolicName, i);
               }
            }

            result.put("EOF", -1);
            result = Collections.unmodifiableMap(result);
            tokenTypeMapCache.put(vocabulary, result);
         }

         return result;
      }
   }

   public Map<String, Integer> getRuleIndexMap() {
      String[] ruleNames = this.getRuleNames();
      if (ruleNames == null) {
         throw new UnsupportedOperationException("The current recognizer does not provide a list of rule names.");
      } else {
         synchronized(ruleIndexMapCache) {
            Map<String, Integer> result = (Map)ruleIndexMapCache.get(ruleNames);
            if (result == null) {
               result = Collections.unmodifiableMap(Utils.toMap(ruleNames));
               ruleIndexMapCache.put(ruleNames, result);
            }

            return result;
         }
      }
   }

   public int getTokenType(String tokenName) {
      Integer ttype = (Integer)this.getTokenTypeMap().get(tokenName);
      return ttype != null ? ttype : 0;
   }

   public String getSerializedATN() {
      throw new UnsupportedOperationException("there is no serialized ATN");
   }

   public abstract String getGrammarFileName();

   public abstract ATN getATN();

   public ATNInterpreter getInterpreter() {
      return this._interp;
   }

   public ParseInfo getParseInfo() {
      return null;
   }

   public void setInterpreter(ATNInterpreter interpreter) {
      this._interp = interpreter;
   }

   public String getErrorHeader(RecognitionException e) {
      int line = e.getOffendingToken().getLine();
      int charPositionInLine = e.getOffendingToken().getCharPositionInLine();
      return "line " + line + ":" + charPositionInLine;
   }

   /** @deprecated */
   @Deprecated
   public String getTokenErrorDisplay(Token t) {
      if (t == null) {
         return "<no token>";
      } else {
         String s = t.getText();
         if (s == null) {
            if (t.getType() == -1) {
               s = "<EOF>";
            } else {
               s = "<" + t.getType() + ">";
            }
         }

         s = s.replace("\n", "\\n");
         s = s.replace("\r", "\\r");
         s = s.replace("\t", "\\t");
         return "'" + s + "'";
      }
   }

   public void addErrorListener(ANTLRErrorListener listener) {
      if (listener == null) {
         throw new NullPointerException("listener cannot be null.");
      } else {
         this._listeners.add(listener);
      }
   }

   public void removeErrorListener(ANTLRErrorListener listener) {
      this._listeners.remove(listener);
   }

   public void removeErrorListeners() {
      this._listeners.clear();
   }

   public List<? extends ANTLRErrorListener> getErrorListeners() {
      return this._listeners;
   }

   public ANTLRErrorListener getErrorListenerDispatch() {
      return new ProxyErrorListener(this.getErrorListeners());
   }

   public boolean sempred(RuleContext _localctx, int ruleIndex, int actionIndex) {
      return true;
   }

   public boolean precpred(RuleContext localctx, int precedence) {
      return true;
   }

   public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
   }

   public final int getState() {
      return this._stateNumber;
   }

   public final void setState(int atnState) {
      this._stateNumber = atnState;
   }

   public abstract IntStream getInputStream();

   public abstract void setInputStream(IntStream var1);

   public abstract TokenFactory<?> getTokenFactory();

   public abstract void setTokenFactory(TokenFactory<?> var1);
}
