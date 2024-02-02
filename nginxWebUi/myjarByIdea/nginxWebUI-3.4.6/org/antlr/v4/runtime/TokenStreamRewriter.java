package org.antlr.v4.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.misc.Interval;

public class TokenStreamRewriter {
   public static final String DEFAULT_PROGRAM_NAME = "default";
   public static final int PROGRAM_INIT_SIZE = 100;
   public static final int MIN_TOKEN_INDEX = 0;
   protected final TokenStream tokens;
   protected final Map<String, List<RewriteOperation>> programs;
   protected final Map<String, Integer> lastRewriteTokenIndexes;

   public TokenStreamRewriter(TokenStream tokens) {
      this.tokens = tokens;
      this.programs = new HashMap();
      this.programs.put("default", new ArrayList(100));
      this.lastRewriteTokenIndexes = new HashMap();
   }

   public final TokenStream getTokenStream() {
      return this.tokens;
   }

   public void rollback(int instructionIndex) {
      this.rollback("default", instructionIndex);
   }

   public void rollback(String programName, int instructionIndex) {
      List<RewriteOperation> is = (List)this.programs.get(programName);
      if (is != null) {
         this.programs.put(programName, is.subList(0, instructionIndex));
      }

   }

   public void deleteProgram() {
      this.deleteProgram("default");
   }

   public void deleteProgram(String programName) {
      this.rollback(programName, 0);
   }

   public void insertAfter(Token t, Object text) {
      this.insertAfter("default", t, text);
   }

   public void insertAfter(int index, Object text) {
      this.insertAfter("default", index, text);
   }

   public void insertAfter(String programName, Token t, Object text) {
      this.insertAfter(programName, t.getTokenIndex(), text);
   }

   public void insertAfter(String programName, int index, Object text) {
      this.insertBefore(programName, index + 1, text);
   }

   public void insertBefore(Token t, Object text) {
      this.insertBefore("default", t, text);
   }

   public void insertBefore(int index, Object text) {
      this.insertBefore("default", index, text);
   }

   public void insertBefore(String programName, Token t, Object text) {
      this.insertBefore(programName, t.getTokenIndex(), text);
   }

   public void insertBefore(String programName, int index, Object text) {
      RewriteOperation op = new InsertBeforeOp(index, text);
      List<RewriteOperation> rewrites = this.getProgram(programName);
      op.instructionIndex = rewrites.size();
      rewrites.add(op);
   }

   public void replace(int index, Object text) {
      this.replace("default", index, index, text);
   }

   public void replace(int from, int to, Object text) {
      this.replace("default", from, to, text);
   }

   public void replace(Token indexT, Object text) {
      this.replace("default", indexT, indexT, text);
   }

   public void replace(Token from, Token to, Object text) {
      this.replace("default", from, to, text);
   }

   public void replace(String programName, int from, int to, Object text) {
      if (from <= to && from >= 0 && to >= 0 && to < this.tokens.size()) {
         RewriteOperation op = new ReplaceOp(from, to, text);
         List<RewriteOperation> rewrites = this.getProgram(programName);
         op.instructionIndex = rewrites.size();
         rewrites.add(op);
      } else {
         throw new IllegalArgumentException("replace: range invalid: " + from + ".." + to + "(size=" + this.tokens.size() + ")");
      }
   }

   public void replace(String programName, Token from, Token to, Object text) {
      this.replace(programName, from.getTokenIndex(), to.getTokenIndex(), text);
   }

   public void delete(int index) {
      this.delete("default", index, index);
   }

   public void delete(int from, int to) {
      this.delete("default", from, to);
   }

   public void delete(Token indexT) {
      this.delete("default", indexT, indexT);
   }

   public void delete(Token from, Token to) {
      this.delete("default", from, to);
   }

   public void delete(String programName, int from, int to) {
      this.replace(programName, from, to, (Object)null);
   }

   public void delete(String programName, Token from, Token to) {
      this.replace(programName, from, to, (Object)null);
   }

   public int getLastRewriteTokenIndex() {
      return this.getLastRewriteTokenIndex("default");
   }

   protected int getLastRewriteTokenIndex(String programName) {
      Integer I = (Integer)this.lastRewriteTokenIndexes.get(programName);
      return I == null ? -1 : I;
   }

   protected void setLastRewriteTokenIndex(String programName, int i) {
      this.lastRewriteTokenIndexes.put(programName, i);
   }

   protected List<RewriteOperation> getProgram(String name) {
      List<RewriteOperation> is = (List)this.programs.get(name);
      if (is == null) {
         is = this.initializeProgram(name);
      }

      return is;
   }

   private List<RewriteOperation> initializeProgram(String name) {
      List<RewriteOperation> is = new ArrayList(100);
      this.programs.put(name, is);
      return is;
   }

   public String getText() {
      return this.getText("default", Interval.of(0, this.tokens.size() - 1));
   }

   public String getText(String programName) {
      return this.getText(programName, Interval.of(0, this.tokens.size() - 1));
   }

   public String getText(Interval interval) {
      return this.getText("default", interval);
   }

   public String getText(String programName, Interval interval) {
      List<RewriteOperation> rewrites = (List)this.programs.get(programName);
      int start = interval.a;
      int stop = interval.b;
      if (stop > this.tokens.size() - 1) {
         stop = this.tokens.size() - 1;
      }

      if (start < 0) {
         start = 0;
      }

      if (rewrites != null && !rewrites.isEmpty()) {
         StringBuilder buf = new StringBuilder();
         Map<Integer, RewriteOperation> indexToOp = this.reduceToSingleOperationPerIndex(rewrites);
         int i = start;

         while(i <= stop && i < this.tokens.size()) {
            RewriteOperation op = (RewriteOperation)indexToOp.get(i);
            indexToOp.remove(i);
            Token t = this.tokens.get(i);
            if (op == null) {
               if (t.getType() != -1) {
                  buf.append(t.getText());
               }

               ++i;
            } else {
               i = op.execute(buf);
            }
         }

         if (stop == this.tokens.size() - 1) {
            Iterator i$ = indexToOp.values().iterator();

            while(i$.hasNext()) {
               RewriteOperation op = (RewriteOperation)i$.next();
               if (op.index >= this.tokens.size() - 1) {
                  buf.append(op.text);
               }
            }
         }

         return buf.toString();
      } else {
         return this.tokens.getText(interval);
      }
   }

   protected Map<Integer, RewriteOperation> reduceToSingleOperationPerIndex(List<RewriteOperation> rewrites) {
      int i;
      RewriteOperation op;
      List prevInserts;
      Iterator i$;
      InsertBeforeOp prevIop;
      ReplaceOp prevRop;
      List prevReplaces;
      Iterator i$;
      label149:
      for(i = 0; i < rewrites.size(); ++i) {
         op = (RewriteOperation)rewrites.get(i);
         if (op != null && op instanceof ReplaceOp) {
            ReplaceOp rop = (ReplaceOp)rewrites.get(i);
            prevInserts = this.getKindOfOps(rewrites, InsertBeforeOp.class, i);
            i$ = prevInserts.iterator();

            while(i$.hasNext()) {
               prevIop = (InsertBeforeOp)i$.next();
               if (prevIop.index == rop.index) {
                  rewrites.set(prevIop.instructionIndex, (Object)null);
                  rop.text = prevIop.text.toString() + (rop.text != null ? rop.text.toString() : "");
               } else if (prevIop.index > rop.index && prevIop.index <= rop.lastIndex) {
                  rewrites.set(prevIop.instructionIndex, (Object)null);
               }
            }

            prevReplaces = this.getKindOfOps(rewrites, ReplaceOp.class, i);
            i$ = prevReplaces.iterator();

            while(true) {
               while(true) {
                  if (!i$.hasNext()) {
                     continue label149;
                  }

                  prevRop = (ReplaceOp)i$.next();
                  if (prevRop.index >= rop.index && prevRop.lastIndex <= rop.lastIndex) {
                     rewrites.set(prevRop.instructionIndex, (Object)null);
                  } else {
                     boolean disjoint = prevRop.lastIndex < rop.index || prevRop.index > rop.lastIndex;
                     boolean same = prevRop.index == rop.index && prevRop.lastIndex == rop.lastIndex;
                     if (prevRop.text == null && rop.text == null && !disjoint) {
                        rewrites.set(prevRop.instructionIndex, (Object)null);
                        rop.index = Math.min(prevRop.index, rop.index);
                        rop.lastIndex = Math.max(prevRop.lastIndex, rop.lastIndex);
                        System.out.println("new rop " + rop);
                     } else if (!disjoint && !same) {
                        throw new IllegalArgumentException("replace op boundaries of " + rop + " overlap with previous " + prevRop);
                     }
                  }
               }
            }
         }
      }

      for(i = 0; i < rewrites.size(); ++i) {
         op = (RewriteOperation)rewrites.get(i);
         if (op != null && op instanceof InsertBeforeOp) {
            InsertBeforeOp iop = (InsertBeforeOp)rewrites.get(i);
            prevInserts = this.getKindOfOps(rewrites, InsertBeforeOp.class, i);
            i$ = prevInserts.iterator();

            while(i$.hasNext()) {
               prevIop = (InsertBeforeOp)i$.next();
               if (prevIop.index == iop.index) {
                  iop.text = this.catOpText(iop.text, prevIop.text);
                  rewrites.set(prevIop.instructionIndex, (Object)null);
               }
            }

            prevReplaces = this.getKindOfOps(rewrites, ReplaceOp.class, i);
            i$ = prevReplaces.iterator();

            while(i$.hasNext()) {
               prevRop = (ReplaceOp)i$.next();
               if (iop.index == prevRop.index) {
                  prevRop.text = this.catOpText(iop.text, prevRop.text);
                  rewrites.set(i, (Object)null);
               } else if (iop.index >= prevRop.index && iop.index <= prevRop.lastIndex) {
                  throw new IllegalArgumentException("insert op " + iop + " within boundaries of previous " + prevRop);
               }
            }
         }
      }

      Map<Integer, RewriteOperation> m = new HashMap();

      for(int i = 0; i < rewrites.size(); ++i) {
         RewriteOperation op = (RewriteOperation)rewrites.get(i);
         if (op != null) {
            if (m.get(op.index) != null) {
               throw new Error("should only be one op per index");
            }

            m.put(op.index, op);
         }
      }

      return m;
   }

   protected String catOpText(Object a, Object b) {
      String x = "";
      String y = "";
      if (a != null) {
         x = a.toString();
      }

      if (b != null) {
         y = b.toString();
      }

      return x + y;
   }

   protected <T extends RewriteOperation> List<? extends T> getKindOfOps(List<? extends RewriteOperation> rewrites, Class<T> kind, int before) {
      List<T> ops = new ArrayList();

      for(int i = 0; i < before && i < rewrites.size(); ++i) {
         RewriteOperation op = (RewriteOperation)rewrites.get(i);
         if (op != null && kind.isInstance(op)) {
            ops.add(kind.cast(op));
         }
      }

      return ops;
   }

   class ReplaceOp extends RewriteOperation {
      protected int lastIndex;

      public ReplaceOp(int from, int to, Object text) {
         super(from, text);
         this.lastIndex = to;
      }

      public int execute(StringBuilder buf) {
         if (this.text != null) {
            buf.append(this.text);
         }

         return this.lastIndex + 1;
      }

      public String toString() {
         return this.text == null ? "<DeleteOp@" + TokenStreamRewriter.this.tokens.get(this.index) + ".." + TokenStreamRewriter.this.tokens.get(this.lastIndex) + ">" : "<ReplaceOp@" + TokenStreamRewriter.this.tokens.get(this.index) + ".." + TokenStreamRewriter.this.tokens.get(this.lastIndex) + ":\"" + this.text + "\">";
      }
   }

   class InsertBeforeOp extends RewriteOperation {
      public InsertBeforeOp(int index, Object text) {
         super(index, text);
      }

      public int execute(StringBuilder buf) {
         buf.append(this.text);
         if (TokenStreamRewriter.this.tokens.get(this.index).getType() != -1) {
            buf.append(TokenStreamRewriter.this.tokens.get(this.index).getText());
         }

         return this.index + 1;
      }
   }

   public class RewriteOperation {
      protected int instructionIndex;
      protected int index;
      protected Object text;

      protected RewriteOperation(int index) {
         this.index = index;
      }

      protected RewriteOperation(int index, Object text) {
         this.index = index;
         this.text = text;
      }

      public int execute(StringBuilder buf) {
         return this.index;
      }

      public String toString() {
         String opName = this.getClass().getName();
         int $index = opName.indexOf(36);
         opName = opName.substring($index + 1, opName.length());
         return "<" + opName + "@" + TokenStreamRewriter.this.tokens.get(this.index) + ":\"" + this.text + "\">";
      }
   }
}
