/*     */ package org.codehaus.plexus.util.dag;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class Vertex
/*     */   implements Cloneable, Serializable
/*     */ {
/*  33 */   private String label = null;
/*     */   
/*  35 */   List children = new ArrayList();
/*     */   
/*  37 */   List parents = new ArrayList();
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
/*     */   public Vertex(String label) {
/*  49 */     this.label = label;
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
/*     */   public String getLabel() {
/*  62 */     return this.label;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addEdgeTo(Vertex vertex) {
/*  70 */     this.children.add(vertex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeEdgeTo(Vertex vertex) {
/*  79 */     this.children.remove(vertex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addEdgeFrom(Vertex vertex) {
/*  88 */     this.parents.add(vertex);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeEdgeFrom(Vertex vertex) {
/*  94 */     this.parents.remove(vertex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getChildren() {
/* 101 */     return this.children;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getChildLabels() {
/* 112 */     List retValue = new ArrayList(this.children.size());
/*     */     
/* 114 */     for (Iterator iter = this.children.iterator(); iter.hasNext(); ) {
/*     */       
/* 116 */       Vertex vertex = iter.next();
/*     */       
/* 118 */       retValue.add(vertex.getLabel());
/*     */     } 
/* 120 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getParents() {
/* 131 */     return this.parents;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getParentLabels() {
/* 142 */     List retValue = new ArrayList(this.parents.size());
/*     */     
/* 144 */     for (Iterator iter = this.parents.iterator(); iter.hasNext(); ) {
/*     */       
/* 146 */       Vertex vertex = iter.next();
/*     */       
/* 148 */       retValue.add(vertex.getLabel());
/*     */     } 
/* 150 */     return retValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLeaf() {
/* 161 */     return (this.children.size() == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRoot() {
/* 172 */     return (this.parents.size() == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConnected() {
/* 183 */     return (isRoot() || isLeaf());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 190 */     Object retValue = super.clone();
/*     */     
/* 192 */     return retValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 197 */     return "Vertex{label='" + this.label + "'" + "}";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\dag\Vertex.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */