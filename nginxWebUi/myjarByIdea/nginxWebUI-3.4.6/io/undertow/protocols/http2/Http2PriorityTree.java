package io.undertow.protocols.http2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Http2PriorityTree {
   private final Http2PriorityNode rootNode = new Http2PriorityNode(0, 0);
   private final Map<Integer, Http2PriorityNode> nodesByID = new HashMap();
   private int[] evictionQueue;
   private int evictionQueuePosition;

   public Http2PriorityTree() {
      this.nodesByID.put(0, this.rootNode);
      this.evictionQueue = new int[10];
   }

   public void registerStream(int streamId, int dependency, int weighting, boolean exclusive) {
      Http2PriorityNode node = new Http2PriorityNode(streamId, weighting);
      Http2PriorityNode existing;
      if (exclusive) {
         existing = (Http2PriorityNode)this.nodesByID.get(dependency);
         if (existing != null) {
            existing.exclusive(node);
         }
      } else {
         existing = (Http2PriorityNode)this.nodesByID.get(dependency);
         if (existing != null) {
            existing.addDependent(node);
         }
      }

      this.nodesByID.put(streamId, node);
   }

   public void streamRemoved(int streamId) {
      Http2PriorityNode node = (Http2PriorityNode)this.nodesByID.get(streamId);
      if (node != null) {
         if (!node.hasDependents()) {
            int toEvict = this.evictionQueue[this.evictionQueuePosition];
            this.evictionQueue[this.evictionQueuePosition++] = streamId;
            Http2PriorityNode nodeToEvict = (Http2PriorityNode)this.nodesByID.get(toEvict);
            if (nodeToEvict != null && !nodeToEvict.hasDependents()) {
               this.nodesByID.remove(toEvict);
            }
         }

      }
   }

   public Comparator<Integer> comparator() {
      return new Comparator<Integer>() {
         public int compare(Integer o1, Integer o2) {
            Http2PriorityNode n1 = (Http2PriorityNode)Http2PriorityTree.this.nodesByID.get(o1);
            Http2PriorityNode n2 = (Http2PriorityNode)Http2PriorityTree.this.nodesByID.get(o2);
            if (n1 == null && n2 == null) {
               return 0;
            } else if (n1 == null) {
               return -1;
            } else if (n2 == null) {
               return 1;
            } else {
               double d1 = Http2PriorityTree.this.createWeightingProportion(n1);
               double d2 = Http2PriorityTree.this.createWeightingProportion(n2);
               return Double.compare(d1, d2);
            }
         }
      };
   }

   private double createWeightingProportion(Http2PriorityNode n1) {
      double ret = 1.0;

      Http2PriorityNode parent;
      for(Http2PriorityNode node = n1; node != null; node = parent) {
         parent = node.parent;
         if (parent != null) {
            ret *= (double)node.weighting / (double)parent.totalWeights;
         }
      }

      return ret;
   }

   public void priorityFrame(int streamId, int streamDependency, int weight, boolean exlusive) {
      Http2PriorityNode existing = (Http2PriorityNode)this.nodesByID.get(streamId);
      if (existing != null) {
         int dif = weight - existing.weighting;
         Http2PriorityNode var10000 = existing.parent;
         var10000.totalWeights += dif;
         existing.weighting = weight;
         Http2PriorityNode newParent;
         if (exlusive) {
            newParent = (Http2PriorityNode)this.nodesByID.get(streamDependency);
            if (newParent != null) {
               existing.parent.removeDependent(existing);
               newParent.exclusive(existing);
            }
         } else if (existing.parent.streamId != streamDependency) {
            newParent = (Http2PriorityNode)this.nodesByID.get(streamDependency);
            if (newParent != null) {
               newParent.addDependent(existing);
            }
         }

      }
   }

   private static class Http2PriorityNode {
      private Http2PriorityNode parent;
      private final int streamId;
      int weighting;
      int totalWeights;
      private Http2PriorityNode[] dependents = null;

      Http2PriorityNode(int streamId, int weighting) {
         this.streamId = streamId;
         this.weighting = weighting;
      }

      void removeDependent(Http2PriorityNode node) {
         if (this.dependents != null) {
            this.totalWeights -= node.weighting;
            boolean found = false;

            int i;
            for(i = 0; i < this.dependents.length - 1; ++i) {
               if (this.dependents[i] == node) {
                  found = true;
               }

               if (found) {
                  this.dependents[i] = this.dependents[i + i];
               }

               if (this.dependents[i] == null) {
                  break;
               }
            }

            if (found) {
               this.dependents[i + 1] = null;
            }

         }
      }

      boolean hasDependents() {
         return this.dependents != null && this.dependents[0] != null;
      }

      public void addDependent(Http2PriorityNode node) {
         if (this.dependents == null) {
            this.dependents = new Http2PriorityNode[5];
         }

         int i = 0;

         boolean found;
         for(found = false; i < this.dependents.length; ++i) {
            if (this.dependents[i] == null) {
               found = true;
               break;
            }
         }

         if (!found) {
            Http2PriorityNode[] old = this.dependents;
            this.dependents = new Http2PriorityNode[this.dependents.length + 5];
            System.arraycopy(old, 0, this.dependents, 0, old.length);
            ++i;
         }

         this.dependents[i] = node;
         node.parent = this;
         this.totalWeights += node.weighting;
      }

      public void exclusive(Http2PriorityNode node) {
         if (this.dependents == null) {
            this.dependents = new Http2PriorityNode[5];
         }

         Http2PriorityNode[] var2 = this.dependents;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Http2PriorityNode i = var2[var4];
            if (i != null) {
               node.addDependent(i);
            }
         }

         this.dependents[0] = node;

         for(int i = 1; i < this.dependents.length; ++i) {
            this.dependents[i] = null;
         }

         this.totalWeights = node.weighting;
      }
   }
}
