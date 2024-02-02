package org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.DoubleKeyMap;
import org.antlr.v4.runtime.misc.MurmurHash;

public abstract class PredictionContext {
   public static final EmptyPredictionContext EMPTY = new EmptyPredictionContext();
   public static final int EMPTY_RETURN_STATE = Integer.MAX_VALUE;
   private static final int INITIAL_HASH = 1;
   public static int globalNodeCount = 0;
   public final int id;
   public final int cachedHashCode;

   protected PredictionContext(int cachedHashCode) {
      this.id = globalNodeCount++;
      this.cachedHashCode = cachedHashCode;
   }

   public static PredictionContext fromRuleContext(ATN atn, RuleContext outerContext) {
      if (outerContext == null) {
         outerContext = RuleContext.EMPTY;
      }

      if (((RuleContext)outerContext).parent != null && outerContext != RuleContext.EMPTY) {
         PredictionContext parent = EMPTY;
         PredictionContext parent = fromRuleContext(atn, ((RuleContext)outerContext).parent);
         ATNState state = (ATNState)atn.states.get(((RuleContext)outerContext).invokingState);
         RuleTransition transition = (RuleTransition)state.transition(0);
         return SingletonPredictionContext.create(parent, transition.followState.stateNumber);
      } else {
         return EMPTY;
      }
   }

   public abstract int size();

   public abstract PredictionContext getParent(int var1);

   public abstract int getReturnState(int var1);

   public boolean isEmpty() {
      return this == EMPTY;
   }

   public boolean hasEmptyPath() {
      return this.getReturnState(this.size() - 1) == Integer.MAX_VALUE;
   }

   public final int hashCode() {
      return this.cachedHashCode;
   }

   public abstract boolean equals(Object var1);

   protected static int calculateEmptyHashCode() {
      int hash = MurmurHash.initialize(1);
      hash = MurmurHash.finish(hash, 0);
      return hash;
   }

   protected static int calculateHashCode(PredictionContext parent, int returnState) {
      int hash = MurmurHash.initialize(1);
      hash = MurmurHash.update(hash, parent);
      hash = MurmurHash.update(hash, returnState);
      hash = MurmurHash.finish(hash, 2);
      return hash;
   }

   protected static int calculateHashCode(PredictionContext[] parents, int[] returnStates) {
      int hash = MurmurHash.initialize(1);
      PredictionContext[] arr$ = parents;
      int len$ = parents.length;

      int i$;
      for(i$ = 0; i$ < len$; ++i$) {
         PredictionContext parent = arr$[i$];
         hash = MurmurHash.update(hash, parent);
      }

      int[] arr$ = returnStates;
      len$ = returnStates.length;

      for(i$ = 0; i$ < len$; ++i$) {
         int returnState = arr$[i$];
         hash = MurmurHash.update(hash, returnState);
      }

      hash = MurmurHash.finish(hash, 2 * parents.length);
      return hash;
   }

   public static PredictionContext merge(PredictionContext a, PredictionContext b, boolean rootIsWildcard, DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
      assert a != null && b != null;

      if (a != b && !((PredictionContext)a).equals(b)) {
         if (a instanceof SingletonPredictionContext && b instanceof SingletonPredictionContext) {
            return mergeSingletons((SingletonPredictionContext)a, (SingletonPredictionContext)b, rootIsWildcard, mergeCache);
         } else {
            if (rootIsWildcard) {
               if (a instanceof EmptyPredictionContext) {
                  return (PredictionContext)a;
               }

               if (b instanceof EmptyPredictionContext) {
                  return (PredictionContext)b;
               }
            }

            if (a instanceof SingletonPredictionContext) {
               a = new ArrayPredictionContext((SingletonPredictionContext)a);
            }

            if (b instanceof SingletonPredictionContext) {
               b = new ArrayPredictionContext((SingletonPredictionContext)b);
            }

            return mergeArrays((ArrayPredictionContext)a, (ArrayPredictionContext)b, rootIsWildcard, mergeCache);
         }
      } else {
         return (PredictionContext)a;
      }
   }

   public static PredictionContext mergeSingletons(SingletonPredictionContext a, SingletonPredictionContext b, boolean rootIsWildcard, DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
      PredictionContext rootMerge;
      if (mergeCache != null) {
         rootMerge = (PredictionContext)mergeCache.get(a, b);
         if (rootMerge != null) {
            return rootMerge;
         }

         rootMerge = (PredictionContext)mergeCache.get(b, a);
         if (rootMerge != null) {
            return rootMerge;
         }
      }

      rootMerge = mergeRoot(a, b, rootIsWildcard);
      if (rootMerge != null) {
         if (mergeCache != null) {
            mergeCache.put(a, b, rootMerge);
         }

         return rootMerge;
      } else {
         PredictionContext singleParent;
         if (a.returnState == b.returnState) {
            singleParent = merge(a.parent, b.parent, rootIsWildcard, mergeCache);
            if (singleParent == a.parent) {
               return a;
            } else if (singleParent == b.parent) {
               return b;
            } else {
               PredictionContext a_ = SingletonPredictionContext.create(singleParent, a.returnState);
               if (mergeCache != null) {
                  mergeCache.put(a, b, a_);
               }

               return a_;
            }
         } else {
            singleParent = null;
            if (a == b || a.parent != null && a.parent.equals(b.parent)) {
               singleParent = a.parent;
            }

            int[] payloads;
            PredictionContext[] parents;
            ArrayPredictionContext a_;
            if (singleParent != null) {
               payloads = new int[]{a.returnState, b.returnState};
               if (a.returnState > b.returnState) {
                  payloads[0] = b.returnState;
                  payloads[1] = a.returnState;
               }

               parents = new PredictionContext[]{singleParent, singleParent};
               a_ = new ArrayPredictionContext(parents, payloads);
               if (mergeCache != null) {
                  mergeCache.put(a, b, a_);
               }

               return a_;
            } else {
               payloads = new int[]{a.returnState, b.returnState};
               parents = new PredictionContext[]{a.parent, b.parent};
               if (a.returnState > b.returnState) {
                  payloads[0] = b.returnState;
                  payloads[1] = a.returnState;
                  parents = new PredictionContext[]{b.parent, a.parent};
               }

               a_ = new ArrayPredictionContext(parents, payloads);
               if (mergeCache != null) {
                  mergeCache.put(a, b, a_);
               }

               return a_;
            }
         }
      }
   }

   public static PredictionContext mergeRoot(SingletonPredictionContext a, SingletonPredictionContext b, boolean rootIsWildcard) {
      if (rootIsWildcard) {
         if (a == EMPTY) {
            return EMPTY;
         }

         if (b == EMPTY) {
            return EMPTY;
         }
      } else {
         if (a == EMPTY && b == EMPTY) {
            return EMPTY;
         }

         int[] payloads;
         PredictionContext[] parents;
         ArrayPredictionContext joined;
         if (a == EMPTY) {
            payloads = new int[]{b.returnState, Integer.MAX_VALUE};
            parents = new PredictionContext[]{b.parent, null};
            joined = new ArrayPredictionContext(parents, payloads);
            return joined;
         }

         if (b == EMPTY) {
            payloads = new int[]{a.returnState, Integer.MAX_VALUE};
            parents = new PredictionContext[]{a.parent, null};
            joined = new ArrayPredictionContext(parents, payloads);
            return joined;
         }
      }

      return null;
   }

   public static PredictionContext mergeArrays(ArrayPredictionContext a, ArrayPredictionContext b, boolean rootIsWildcard, DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
      if (mergeCache != null) {
         PredictionContext previous = (PredictionContext)mergeCache.get(a, b);
         if (previous != null) {
            return previous;
         }

         previous = (PredictionContext)mergeCache.get(b, a);
         if (previous != null) {
            return previous;
         }
      }

      int i = 0;
      int j = 0;
      int k = 0;
      int[] mergedReturnStates = new int[a.returnStates.length + b.returnStates.length];

      PredictionContext[] mergedParents;
      for(mergedParents = new PredictionContext[a.returnStates.length + b.returnStates.length]; i < a.returnStates.length && j < b.returnStates.length; ++k) {
         PredictionContext a_parent = a.parents[i];
         PredictionContext b_parent = b.parents[j];
         if (a.returnStates[i] == b.returnStates[j]) {
            int payload = a.returnStates[i];
            boolean both$ = payload == Integer.MAX_VALUE && a_parent == null && b_parent == null;
            boolean ax_ax = a_parent != null && b_parent != null && a_parent.equals(b_parent);
            if (!both$ && !ax_ax) {
               PredictionContext mergedParent = merge(a_parent, b_parent, rootIsWildcard, mergeCache);
               mergedParents[k] = mergedParent;
               mergedReturnStates[k] = payload;
            } else {
               mergedParents[k] = a_parent;
               mergedReturnStates[k] = payload;
            }

            ++i;
            ++j;
         } else if (a.returnStates[i] < b.returnStates[j]) {
            mergedParents[k] = a_parent;
            mergedReturnStates[k] = a.returnStates[i];
            ++i;
         } else {
            mergedParents[k] = b_parent;
            mergedReturnStates[k] = b.returnStates[j];
            ++j;
         }
      }

      int p;
      if (i < a.returnStates.length) {
         for(p = i; p < a.returnStates.length; ++p) {
            mergedParents[k] = a.parents[p];
            mergedReturnStates[k] = a.returnStates[p];
            ++k;
         }
      } else {
         for(p = j; p < b.returnStates.length; ++p) {
            mergedParents[k] = b.parents[p];
            mergedReturnStates[k] = b.returnStates[p];
            ++k;
         }
      }

      if (k < mergedParents.length) {
         if (k == 1) {
            PredictionContext a_ = SingletonPredictionContext.create(mergedParents[0], mergedReturnStates[0]);
            if (mergeCache != null) {
               mergeCache.put(a, b, a_);
            }

            return a_;
         }

         mergedParents = (PredictionContext[])Arrays.copyOf(mergedParents, k);
         mergedReturnStates = Arrays.copyOf(mergedReturnStates, k);
      }

      PredictionContext M = new ArrayPredictionContext(mergedParents, mergedReturnStates);
      if (M.equals(a)) {
         if (mergeCache != null) {
            mergeCache.put(a, b, a);
         }

         return a;
      } else if (M.equals(b)) {
         if (mergeCache != null) {
            mergeCache.put(a, b, b);
         }

         return b;
      } else {
         combineCommonParents(mergedParents);
         if (mergeCache != null) {
            mergeCache.put(a, b, M);
         }

         return M;
      }
   }

   protected static void combineCommonParents(PredictionContext[] parents) {
      Map<PredictionContext, PredictionContext> uniqueParents = new HashMap();

      int p;
      for(p = 0; p < parents.length; ++p) {
         PredictionContext parent = parents[p];
         if (!uniqueParents.containsKey(parent)) {
            uniqueParents.put(parent, parent);
         }
      }

      for(p = 0; p < parents.length; ++p) {
         parents[p] = (PredictionContext)uniqueParents.get(parents[p]);
      }

   }

   public static String toDOTString(PredictionContext context) {
      if (context == null) {
         return "";
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append("digraph G {\n");
         buf.append("rankdir=LR;\n");
         List<PredictionContext> nodes = getAllContextNodes(context);
         Collections.sort(nodes, new Comparator<PredictionContext>() {
            public int compare(PredictionContext o1, PredictionContext o2) {
               return o1.id - o2.id;
            }
         });
         Iterator i$ = nodes.iterator();

         while(true) {
            PredictionContext current;
            String s;
            while(i$.hasNext()) {
               current = (PredictionContext)i$.next();
               if (current instanceof SingletonPredictionContext) {
                  String s = String.valueOf(current.id);
                  buf.append("  s").append(s);
                  s = String.valueOf(current.getReturnState(0));
                  if (current instanceof EmptyPredictionContext) {
                     s = "$";
                  }

                  buf.append(" [label=\"").append(s).append("\"];\n");
               } else {
                  ArrayPredictionContext arr = (ArrayPredictionContext)current;
                  buf.append("  s").append(arr.id);
                  buf.append(" [shape=box, label=\"");
                  buf.append("[");
                  boolean first = true;
                  int[] arr$ = arr.returnStates;
                  int len$ = arr$.length;

                  for(int i$ = 0; i$ < len$; ++i$) {
                     int inv = arr$[i$];
                     if (!first) {
                        buf.append(", ");
                     }

                     if (inv == Integer.MAX_VALUE) {
                        buf.append("$");
                     } else {
                        buf.append(inv);
                     }

                     first = false;
                  }

                  buf.append("]");
                  buf.append("\"];\n");
               }
            }

            i$ = nodes.iterator();

            while(true) {
               do {
                  if (!i$.hasNext()) {
                     buf.append("}\n");
                     return buf.toString();
                  }

                  current = (PredictionContext)i$.next();
               } while(current == EMPTY);

               for(int i = 0; i < current.size(); ++i) {
                  if (current.getParent(i) != null) {
                     s = String.valueOf(current.id);
                     buf.append("  s").append(s);
                     buf.append("->");
                     buf.append("s");
                     buf.append(current.getParent(i).id);
                     if (current.size() > 1) {
                        buf.append(" [label=\"parent[" + i + "]\"];\n");
                     } else {
                        buf.append(";\n");
                     }
                  }
               }
            }
         }
      }
   }

   public static PredictionContext getCachedContext(PredictionContext context, PredictionContextCache contextCache, IdentityHashMap<PredictionContext, PredictionContext> visited) {
      if (context.isEmpty()) {
         return context;
      } else {
         PredictionContext existing = (PredictionContext)visited.get(context);
         if (existing != null) {
            return existing;
         } else {
            existing = contextCache.get(context);
            if (existing != null) {
               visited.put(context, existing);
               return existing;
            } else {
               boolean changed = false;
               PredictionContext[] parents = new PredictionContext[context.size()];

               for(int i = 0; i < parents.length; ++i) {
                  PredictionContext parent = getCachedContext(context.getParent(i), contextCache, visited);
                  if (changed || parent != context.getParent(i)) {
                     if (!changed) {
                        parents = new PredictionContext[context.size()];

                        for(int j = 0; j < context.size(); ++j) {
                           parents[j] = context.getParent(j);
                        }

                        changed = true;
                     }

                     parents[i] = parent;
                  }
               }

               if (!changed) {
                  contextCache.add(context);
                  visited.put(context, context);
                  return context;
               } else {
                  Object updated;
                  if (parents.length == 0) {
                     updated = EMPTY;
                  } else if (parents.length == 1) {
                     updated = SingletonPredictionContext.create(parents[0], context.getReturnState(0));
                  } else {
                     ArrayPredictionContext arrayPredictionContext = (ArrayPredictionContext)context;
                     updated = new ArrayPredictionContext(parents, arrayPredictionContext.returnStates);
                  }

                  contextCache.add((PredictionContext)updated);
                  visited.put(updated, updated);
                  visited.put(context, updated);
                  return (PredictionContext)updated;
               }
            }
         }
      }
   }

   public static List<PredictionContext> getAllContextNodes(PredictionContext context) {
      List<PredictionContext> nodes = new ArrayList();
      Map<PredictionContext, PredictionContext> visited = new IdentityHashMap();
      getAllContextNodes_(context, nodes, visited);
      return nodes;
   }

   public static void getAllContextNodes_(PredictionContext context, List<PredictionContext> nodes, Map<PredictionContext, PredictionContext> visited) {
      if (context != null && !visited.containsKey(context)) {
         visited.put(context, context);
         nodes.add(context);

         for(int i = 0; i < context.size(); ++i) {
            getAllContextNodes_(context.getParent(i), nodes, visited);
         }

      }
   }

   public String toString(Recognizer<?, ?> recog) {
      return this.toString();
   }

   public String[] toStrings(Recognizer<?, ?> recognizer, int currentState) {
      return this.toStrings(recognizer, EMPTY, currentState);
   }

   public String[] toStrings(Recognizer<?, ?> recognizer, PredictionContext stop, int currentState) {
      List<String> result = new ArrayList();
      int perm = 0;

      while(true) {
         int offset = 0;
         boolean last = true;
         PredictionContext p = this;
         int stateNumber = currentState;
         StringBuilder localBuffer = new StringBuilder();
         localBuffer.append("[");

         label60: {
            while(!p.isEmpty() && p != stop) {
               int index = 0;
               if (p.size() > 0) {
                  int bits;
                  for(bits = 1; 1 << bits < p.size(); ++bits) {
                  }

                  int mask = (1 << bits) - 1;
                  index = perm >> offset & mask;
                  last &= index >= p.size() - 1;
                  if (index >= p.size()) {
                     break label60;
                  }

                  offset += bits;
               }

               if (recognizer != null) {
                  if (localBuffer.length() > 1) {
                     localBuffer.append(' ');
                  }

                  ATN atn = recognizer.getATN();
                  ATNState s = (ATNState)atn.states.get(stateNumber);
                  String ruleName = recognizer.getRuleNames()[s.ruleIndex];
                  localBuffer.append(ruleName);
               } else if (p.getReturnState(index) != Integer.MAX_VALUE && !p.isEmpty()) {
                  if (localBuffer.length() > 1) {
                     localBuffer.append(' ');
                  }

                  localBuffer.append(p.getReturnState(index));
               }

               stateNumber = p.getReturnState(index);
               p = p.getParent(index);
            }

            localBuffer.append("]");
            result.add(localBuffer.toString());
            if (last) {
               return (String[])result.toArray(new String[result.size()]);
            }
         }

         ++perm;
      }
   }
}
