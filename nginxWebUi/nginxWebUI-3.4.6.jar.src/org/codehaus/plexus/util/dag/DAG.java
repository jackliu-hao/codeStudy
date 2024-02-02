/*     */ package org.codehaus.plexus.util.dag;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ public class DAG
/*     */   implements Cloneable, Serializable
/*     */ {
/*  45 */   private Map vertexMap = new HashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   private List vertexList = new ArrayList();
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
/*     */   public List getVerticies() {
/*  73 */     return this.vertexList;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Set getLabels() {
/*  79 */     Set retValue = this.vertexMap.keySet();
/*     */     
/*  81 */     return retValue;
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
/*     */   public Vertex addVertex(String label) {
/*  98 */     Vertex retValue = null;
/*     */ 
/*     */     
/* 101 */     if (this.vertexMap.containsKey(label)) {
/*     */       
/* 103 */       retValue = (Vertex)this.vertexMap.get(label);
/*     */     }
/*     */     else {
/*     */       
/* 107 */       retValue = new Vertex(label);
/*     */       
/* 109 */       this.vertexMap.put(label, retValue);
/*     */       
/* 111 */       this.vertexList.add(retValue);
/*     */     } 
/*     */     
/* 114 */     return retValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addEdge(String from, String to) throws CycleDetectedException {
/* 119 */     Vertex v1 = addVertex(from);
/*     */     
/* 121 */     Vertex v2 = addVertex(to);
/*     */     
/* 123 */     addEdge(v1, v2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addEdge(Vertex from, Vertex to) throws CycleDetectedException {
/* 129 */     from.addEdgeTo(to);
/*     */     
/* 131 */     to.addEdgeFrom(from);
/*     */     
/* 133 */     List cycle = CycleDetector.introducesCycle(to);
/*     */     
/* 135 */     if (cycle != null) {
/*     */ 
/*     */ 
/*     */       
/* 139 */       removeEdge(from, to);
/*     */       
/* 141 */       String msg = "Edge between '" + from + "' and '" + to + "' introduces to cycle in the graph";
/*     */       
/* 143 */       throw new CycleDetectedException(msg, cycle);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeEdge(String from, String to) {
/* 150 */     Vertex v1 = addVertex(from);
/*     */     
/* 152 */     Vertex v2 = addVertex(to);
/*     */     
/* 154 */     removeEdge(v1, v2);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeEdge(Vertex from, Vertex to) {
/* 159 */     from.removeEdgeTo(to);
/*     */     
/* 161 */     to.removeEdgeFrom(from);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Vertex getVertex(String label) {
/* 167 */     Vertex retValue = (Vertex)this.vertexMap.get(label);
/*     */     
/* 169 */     return retValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasEdge(String label1, String label2) {
/* 174 */     Vertex v1 = getVertex(label1);
/*     */     
/* 176 */     Vertex v2 = getVertex(label2);
/*     */     
/* 178 */     boolean retValue = v1.getChildren().contains(v2);
/*     */     
/* 180 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getChildLabels(String label) {
/* 190 */     Vertex vertex = getVertex(label);
/*     */     
/* 192 */     return vertex.getChildLabels();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getParentLabels(String label) {
/* 201 */     Vertex vertex = getVertex(label);
/*     */     
/* 203 */     return vertex.getParentLabels();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 213 */     Object retValue = super.clone();
/*     */     
/* 215 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConnected(String label) {
/* 226 */     Vertex vertex = getVertex(label);
/*     */     
/* 228 */     boolean retValue = vertex.isConnected();
/*     */     
/* 230 */     return retValue;
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
/*     */   public List getSuccessorLabels(String label) {
/*     */     List retValue;
/* 246 */     Vertex vertex = getVertex(label);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 251 */     if (vertex.isLeaf()) {
/*     */       
/* 253 */       retValue = new ArrayList(1);
/*     */       
/* 255 */       retValue.add(label);
/*     */     }
/*     */     else {
/*     */       
/* 259 */       retValue = TopologicalSorter.sort(vertex);
/*     */     } 
/*     */     
/* 262 */     return retValue;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\dag\DAG.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */