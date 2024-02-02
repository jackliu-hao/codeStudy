/*     */ package org.codehaus.plexus.util.dag;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
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
/*     */ public class CycleDetector
/*     */ {
/*  33 */   private static final Integer NOT_VISTITED = new Integer(0);
/*     */   
/*  35 */   private static final Integer VISITING = new Integer(1);
/*     */   
/*  37 */   private static final Integer VISITED = new Integer(2);
/*     */ 
/*     */ 
/*     */   
/*     */   public static List hasCycle(DAG graph) {
/*  42 */     List verticies = graph.getVerticies();
/*     */     
/*  44 */     Map vertexStateMap = new HashMap();
/*     */     
/*  46 */     List retValue = null;
/*     */     
/*  48 */     for (Iterator iter = verticies.iterator(); iter.hasNext(); ) {
/*     */       
/*  50 */       Vertex vertex = iter.next();
/*     */       
/*  52 */       if (isNotVisited(vertex, vertexStateMap)) {
/*     */         
/*  54 */         retValue = introducesCycle(vertex, vertexStateMap);
/*     */         
/*  56 */         if (retValue != null) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  63 */     return retValue;
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
/*     */   public static List introducesCycle(Vertex vertex, Map vertexStateMap) {
/*  79 */     LinkedList cycleStack = new LinkedList();
/*     */     
/*  81 */     boolean hasCycle = dfsVisit(vertex, cycleStack, vertexStateMap);
/*     */     
/*  83 */     if (hasCycle) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  91 */       String label = cycleStack.getFirst();
/*     */       
/*  93 */       int pos = cycleStack.lastIndexOf(label);
/*     */       
/*  95 */       List cycle = cycleStack.subList(0, pos + 1);
/*     */       
/*  97 */       Collections.reverse(cycle);
/*     */       
/*  99 */       return cycle;
/*     */     } 
/*     */     
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List introducesCycle(Vertex vertex) {
/* 109 */     Map vertexStateMap = new HashMap();
/*     */     
/* 111 */     return introducesCycle(vertex, vertexStateMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isNotVisited(Vertex vertex, Map vertexStateMap) {
/* 122 */     if (!vertexStateMap.containsKey(vertex))
/*     */     {
/* 124 */       return true;
/*     */     }
/*     */     
/* 127 */     Integer state = (Integer)vertexStateMap.get(vertex);
/*     */     
/* 129 */     return NOT_VISTITED.equals(state);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isVisiting(Vertex vertex, Map vertexStateMap) {
/* 139 */     Integer state = (Integer)vertexStateMap.get(vertex);
/*     */     
/* 141 */     return VISITING.equals(state);
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean dfsVisit(Vertex vertex, LinkedList cycle, Map vertexStateMap) {
/* 146 */     cycle.addFirst(vertex.getLabel());
/*     */     
/* 148 */     vertexStateMap.put(vertex, VISITING);
/*     */     
/* 150 */     List verticies = vertex.getChildren();
/*     */     
/* 152 */     for (Iterator iter = verticies.iterator(); iter.hasNext(); ) {
/*     */       
/* 154 */       Vertex v = iter.next();
/*     */       
/* 156 */       if (isNotVisited(v, vertexStateMap)) {
/*     */         
/* 158 */         boolean hasCycle = dfsVisit(v, cycle, vertexStateMap);
/*     */         
/* 160 */         if (hasCycle)
/*     */         {
/* 162 */           return true; } 
/*     */         continue;
/*     */       } 
/* 165 */       if (isVisiting(v, vertexStateMap)) {
/*     */         
/* 167 */         cycle.addFirst(v.getLabel());
/*     */         
/* 169 */         return true;
/*     */       } 
/*     */     } 
/* 172 */     vertexStateMap.put(vertex, VISITED);
/*     */     
/* 174 */     cycle.removeFirst();
/*     */     
/* 176 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\dag\CycleDetector.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */