/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class Http2PriorityTree
/*     */ {
/*     */   private final Http2PriorityNode rootNode;
/*  35 */   private final Map<Integer, Http2PriorityNode> nodesByID = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] evictionQueue;
/*     */ 
/*     */ 
/*     */   
/*     */   private int evictionQueuePosition;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Http2PriorityTree() {
/*  49 */     this.rootNode = new Http2PriorityNode(0, 0);
/*  50 */     this.nodesByID.put(Integer.valueOf(0), this.rootNode);
/*  51 */     this.evictionQueue = new int[10];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerStream(int streamId, int dependency, int weighting, boolean exclusive) {
/*  61 */     Http2PriorityNode node = new Http2PriorityNode(streamId, weighting);
/*  62 */     if (exclusive) {
/*  63 */       Http2PriorityNode existing = this.nodesByID.get(Integer.valueOf(dependency));
/*  64 */       if (existing != null) {
/*  65 */         existing.exclusive(node);
/*     */       }
/*     */     } else {
/*  68 */       Http2PriorityNode existing = this.nodesByID.get(Integer.valueOf(dependency));
/*  69 */       if (existing != null) {
/*  70 */         existing.addDependent(node);
/*     */       }
/*     */     } 
/*  73 */     this.nodesByID.put(Integer.valueOf(streamId), node);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void streamRemoved(int streamId) {
/*  82 */     Http2PriorityNode node = this.nodesByID.get(Integer.valueOf(streamId));
/*  83 */     if (node == null) {
/*     */       return;
/*     */     }
/*  86 */     if (!node.hasDependents()) {
/*     */       
/*  88 */       int toEvict = this.evictionQueue[this.evictionQueuePosition];
/*  89 */       this.evictionQueue[this.evictionQueuePosition++] = streamId;
/*  90 */       Http2PriorityNode nodeToEvict = this.nodesByID.get(Integer.valueOf(toEvict));
/*     */ 
/*     */       
/*  93 */       if (nodeToEvict != null && !nodeToEvict.hasDependents()) {
/*  94 */         this.nodesByID.remove(Integer.valueOf(toEvict));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Comparator<Integer> comparator() {
/* 105 */     return new Comparator<Integer>()
/*     */       {
/*     */         public int compare(Integer o1, Integer o2) {
/* 108 */           Http2PriorityTree.Http2PriorityNode n1 = (Http2PriorityTree.Http2PriorityNode)Http2PriorityTree.this.nodesByID.get(o1);
/* 109 */           Http2PriorityTree.Http2PriorityNode n2 = (Http2PriorityTree.Http2PriorityNode)Http2PriorityTree.this.nodesByID.get(o2);
/* 110 */           if (n1 == null && n2 == null) {
/* 111 */             return 0;
/*     */           }
/* 113 */           if (n1 == null) {
/* 114 */             return -1;
/*     */           }
/* 116 */           if (n2 == null) {
/* 117 */             return 1;
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 122 */           double d1 = Http2PriorityTree.this.createWeightingProportion(n1);
/* 123 */           double d2 = Http2PriorityTree.this.createWeightingProportion(n2);
/* 124 */           return Double.compare(d1, d2);
/*     */         }
/*     */       };
/*     */   }
/*     */   private double createWeightingProportion(Http2PriorityNode n1) {
/* 129 */     double ret = 1.0D;
/* 130 */     Http2PriorityNode node = n1;
/* 131 */     while (node != null) {
/* 132 */       Http2PriorityNode parent = node.parent;
/* 133 */       if (parent != null) {
/* 134 */         ret *= node.weighting / parent.totalWeights;
/*     */       }
/* 136 */       node = parent;
/*     */     } 
/* 138 */     return ret;
/*     */   }
/*     */   
/*     */   public void priorityFrame(int streamId, int streamDependency, int weight, boolean exlusive) {
/* 142 */     Http2PriorityNode existing = this.nodesByID.get(Integer.valueOf(streamId));
/* 143 */     if (existing == null) {
/*     */       return;
/*     */     }
/* 146 */     int dif = weight - existing.weighting;
/* 147 */     existing.parent.totalWeights += dif;
/* 148 */     existing.weighting = weight;
/* 149 */     if (exlusive) {
/* 150 */       Http2PriorityNode newParent = this.nodesByID.get(Integer.valueOf(streamDependency));
/* 151 */       if (newParent != null) {
/* 152 */         existing.parent.removeDependent(existing);
/* 153 */         newParent.exclusive(existing);
/*     */       } 
/* 155 */     } else if (existing.parent.streamId != streamDependency) {
/* 156 */       Http2PriorityNode newParent = this.nodesByID.get(Integer.valueOf(streamDependency));
/* 157 */       if (newParent != null) {
/* 158 */         newParent.addDependent(existing);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Http2PriorityNode
/*     */   {
/*     */     private Http2PriorityNode parent;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final int streamId;
/*     */ 
/*     */ 
/*     */     
/*     */     int weighting;
/*     */ 
/*     */ 
/*     */     
/*     */     int totalWeights;
/*     */ 
/*     */ 
/*     */     
/* 186 */     private Http2PriorityNode[] dependents = null;
/*     */     
/*     */     Http2PriorityNode(int streamId, int weighting) {
/* 189 */       this.streamId = streamId;
/* 190 */       this.weighting = weighting;
/*     */     }
/*     */ 
/*     */     
/*     */     void removeDependent(Http2PriorityNode node) {
/* 195 */       if (this.dependents == null) {
/*     */         return;
/*     */       }
/* 198 */       this.totalWeights -= node.weighting;
/* 199 */       boolean found = false;
/*     */       int i;
/* 201 */       for (i = 0; i < this.dependents.length - 1; i++) {
/* 202 */         if (this.dependents[i] == node) {
/* 203 */           found = true;
/*     */         }
/* 205 */         if (found) {
/* 206 */           this.dependents[i] = this.dependents[i + i];
/*     */         }
/* 208 */         if (this.dependents[i] == null) {
/*     */           break;
/*     */         }
/*     */       } 
/* 212 */       if (found) {
/* 213 */         this.dependents[i + 1] = null;
/*     */       }
/*     */     }
/*     */     
/*     */     boolean hasDependents() {
/* 218 */       return (this.dependents != null && this.dependents[0] != null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void addDependent(Http2PriorityNode node) {
/* 223 */       if (this.dependents == null) {
/* 224 */         this.dependents = new Http2PriorityNode[5];
/*     */       }
/* 226 */       int i = 0;
/* 227 */       boolean found = false;
/* 228 */       for (; i < this.dependents.length; i++) {
/* 229 */         if (this.dependents[i] == null) {
/* 230 */           found = true;
/*     */           break;
/*     */         } 
/*     */       } 
/* 234 */       if (!found) {
/* 235 */         Http2PriorityNode[] old = this.dependents;
/* 236 */         this.dependents = new Http2PriorityNode[this.dependents.length + 5];
/* 237 */         System.arraycopy(old, 0, this.dependents, 0, old.length);
/* 238 */         i++;
/*     */       } 
/* 240 */       this.dependents[i] = node;
/* 241 */       node.parent = this;
/* 242 */       this.totalWeights += node.weighting;
/*     */     }
/*     */     
/*     */     public void exclusive(Http2PriorityNode node) {
/* 246 */       if (this.dependents == null) {
/* 247 */         this.dependents = new Http2PriorityNode[5];
/*     */       }
/*     */       
/* 250 */       for (Http2PriorityNode http2PriorityNode : this.dependents) {
/* 251 */         if (http2PriorityNode != null) {
/* 252 */           node.addDependent(http2PriorityNode);
/*     */         }
/*     */       } 
/* 255 */       this.dependents[0] = node;
/* 256 */       for (int i = 1; i < this.dependents.length; i++) {
/* 257 */         this.dependents[i] = null;
/*     */       }
/* 259 */       this.totalWeights = node.weighting;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2PriorityTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */