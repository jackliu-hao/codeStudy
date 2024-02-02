package org.antlr.v4.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ErrorNodeImpl;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class ParserRuleContext extends RuleContext {
   public List<ParseTree> children;
   public Token start;
   public Token stop;
   public RecognitionException exception;

   public ParserRuleContext() {
   }

   public void copyFrom(ParserRuleContext ctx) {
      this.parent = ctx.parent;
      this.invokingState = ctx.invokingState;
      this.start = ctx.start;
      this.stop = ctx.stop;
   }

   public ParserRuleContext(ParserRuleContext parent, int invokingStateNumber) {
      super(parent, invokingStateNumber);
   }

   public void enterRule(ParseTreeListener listener) {
   }

   public void exitRule(ParseTreeListener listener) {
   }

   public TerminalNode addChild(TerminalNode t) {
      if (this.children == null) {
         this.children = new ArrayList();
      }

      this.children.add(t);
      return t;
   }

   public RuleContext addChild(RuleContext ruleInvocation) {
      if (this.children == null) {
         this.children = new ArrayList();
      }

      this.children.add(ruleInvocation);
      return ruleInvocation;
   }

   public void removeLastChild() {
      if (this.children != null) {
         this.children.remove(this.children.size() - 1);
      }

   }

   public TerminalNode addChild(Token matchedToken) {
      TerminalNodeImpl t = new TerminalNodeImpl(matchedToken);
      this.addChild((TerminalNode)t);
      t.parent = this;
      return t;
   }

   public ErrorNode addErrorNode(Token badToken) {
      ErrorNodeImpl t = new ErrorNodeImpl(badToken);
      this.addChild((TerminalNode)t);
      t.parent = this;
      return t;
   }

   public ParserRuleContext getParent() {
      return (ParserRuleContext)super.getParent();
   }

   public ParseTree getChild(int i) {
      return this.children != null && i >= 0 && i < this.children.size() ? (ParseTree)this.children.get(i) : null;
   }

   public <T extends ParseTree> T getChild(Class<? extends T> ctxType, int i) {
      if (this.children != null && i >= 0 && i < this.children.size()) {
         int j = -1;
         Iterator i$ = this.children.iterator();

         while(i$.hasNext()) {
            ParseTree o = (ParseTree)i$.next();
            if (ctxType.isInstance(o)) {
               ++j;
               if (j == i) {
                  return (ParseTree)ctxType.cast(o);
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public TerminalNode getToken(int ttype, int i) {
      if (this.children != null && i >= 0 && i < this.children.size()) {
         int j = -1;
         Iterator i$ = this.children.iterator();

         while(i$.hasNext()) {
            ParseTree o = (ParseTree)i$.next();
            if (o instanceof TerminalNode) {
               TerminalNode tnode = (TerminalNode)o;
               Token symbol = tnode.getSymbol();
               if (symbol.getType() == ttype) {
                  ++j;
                  if (j == i) {
                     return tnode;
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public List<TerminalNode> getTokens(int ttype) {
      if (this.children == null) {
         return Collections.emptyList();
      } else {
         List<TerminalNode> tokens = null;
         Iterator i$ = this.children.iterator();

         while(i$.hasNext()) {
            ParseTree o = (ParseTree)i$.next();
            if (o instanceof TerminalNode) {
               TerminalNode tnode = (TerminalNode)o;
               Token symbol = tnode.getSymbol();
               if (symbol.getType() == ttype) {
                  if (tokens == null) {
                     tokens = new ArrayList();
                  }

                  tokens.add(tnode);
               }
            }
         }

         if (tokens == null) {
            return Collections.emptyList();
         } else {
            return tokens;
         }
      }
   }

   public <T extends ParserRuleContext> T getRuleContext(Class<? extends T> ctxType, int i) {
      return (ParserRuleContext)this.getChild(ctxType, i);
   }

   public <T extends ParserRuleContext> List<T> getRuleContexts(Class<? extends T> ctxType) {
      if (this.children == null) {
         return Collections.emptyList();
      } else {
         List<T> contexts = null;
         Iterator i$ = this.children.iterator();

         while(i$.hasNext()) {
            ParseTree o = (ParseTree)i$.next();
            if (ctxType.isInstance(o)) {
               if (contexts == null) {
                  contexts = new ArrayList();
               }

               contexts.add(ctxType.cast(o));
            }
         }

         if (contexts == null) {
            return Collections.emptyList();
         } else {
            return contexts;
         }
      }
   }

   public int getChildCount() {
      return this.children != null ? this.children.size() : 0;
   }

   public Interval getSourceInterval() {
      if (this.start == null) {
         return Interval.INVALID;
      } else {
         return this.stop != null && this.stop.getTokenIndex() >= this.start.getTokenIndex() ? Interval.of(this.start.getTokenIndex(), this.stop.getTokenIndex()) : Interval.of(this.start.getTokenIndex(), this.start.getTokenIndex() - 1);
      }
   }

   public Token getStart() {
      return this.start;
   }

   public Token getStop() {
      return this.stop;
   }

   public String toInfoString(Parser recognizer) {
      List<String> rules = recognizer.getRuleInvocationStack(this);
      Collections.reverse(rules);
      return "ParserRuleContext" + rules + "{" + "start=" + this.start + ", stop=" + this.stop + '}';
   }
}
