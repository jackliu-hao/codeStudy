/*     */ package org.codehaus.plexus.util.dag;
/*     */ 
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
/*     */ public class TopologicalSorter
/*     */ {
/*  32 */   private static final Integer NOT_VISTITED = new Integer(0);
/*     */   
/*  34 */   private static final Integer VISITING = new Integer(1);
/*     */   
/*  36 */   private static final Integer VISITED = new Integer(2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List sort(DAG graph) {
/*  45 */     return dfs(graph);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static List sort(Vertex vertex) {
/*  51 */     LinkedList retValue = new LinkedList();
/*     */     
/*  53 */     Map vertexStateMap = new HashMap();
/*     */     
/*  55 */     dfsVisit(vertex, vertexStateMap, retValue);
/*     */     
/*  57 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static List dfs(DAG graph) {
/*  63 */     List verticies = graph.getVerticies();
/*     */ 
/*     */     
/*  66 */     LinkedList retValue = new LinkedList();
/*     */     
/*  68 */     Map vertexStateMap = new HashMap();
/*     */     
/*  70 */     for (Iterator iter = verticies.iterator(); iter.hasNext(); ) {
/*     */       
/*  72 */       Vertex vertex = iter.next();
/*     */       
/*  74 */       if (isNotVisited(vertex, vertexStateMap))
/*     */       {
/*  76 */         dfsVisit(vertex, vertexStateMap, retValue);
/*     */       }
/*     */     } 
/*     */     
/*  80 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isNotVisited(Vertex vertex, Map vertexStateMap) {
/*  90 */     if (!vertexStateMap.containsKey(vertex))
/*     */     {
/*  92 */       return true;
/*     */     }
/*  94 */     Integer state = (Integer)vertexStateMap.get(vertex);
/*     */     
/*  96 */     return NOT_VISTITED.equals(state);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void dfsVisit(Vertex vertex, Map vertexStateMap, LinkedList list) {
/* 102 */     vertexStateMap.put(vertex, VISITING);
/*     */     
/* 104 */     List verticies = vertex.getChildren();
/*     */     
/* 106 */     for (Iterator iter = verticies.iterator(); iter.hasNext(); ) {
/*     */       
/* 108 */       Vertex v = iter.next();
/*     */       
/* 110 */       if (isNotVisited(v, vertexStateMap))
/*     */       {
/* 112 */         dfsVisit(v, vertexStateMap, list);
/*     */       }
/*     */     } 
/*     */     
/* 116 */     vertexStateMap.put(vertex, VISITED);
/*     */     
/* 118 */     list.add(vertex.getLabel());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\dag\TopologicalSorter.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */