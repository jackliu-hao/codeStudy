package org.codehaus.plexus.util.dag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DAG implements Cloneable, Serializable {
   private Map vertexMap = new HashMap();
   private List vertexList = new ArrayList();

   public List getVerticies() {
      return this.vertexList;
   }

   public Set getLabels() {
      Set retValue = this.vertexMap.keySet();
      return retValue;
   }

   public Vertex addVertex(String label) {
      Vertex retValue = null;
      if (this.vertexMap.containsKey(label)) {
         retValue = (Vertex)this.vertexMap.get(label);
      } else {
         retValue = new Vertex(label);
         this.vertexMap.put(label, retValue);
         this.vertexList.add(retValue);
      }

      return retValue;
   }

   public void addEdge(String from, String to) throws CycleDetectedException {
      Vertex v1 = this.addVertex(from);
      Vertex v2 = this.addVertex(to);
      this.addEdge(v1, v2);
   }

   public void addEdge(Vertex from, Vertex to) throws CycleDetectedException {
      from.addEdgeTo(to);
      to.addEdgeFrom(from);
      List cycle = CycleDetector.introducesCycle(to);
      if (cycle != null) {
         this.removeEdge(from, to);
         String msg = "Edge between '" + from + "' and '" + to + "' introduces to cycle in the graph";
         throw new CycleDetectedException(msg, cycle);
      }
   }

   public void removeEdge(String from, String to) {
      Vertex v1 = this.addVertex(from);
      Vertex v2 = this.addVertex(to);
      this.removeEdge(v1, v2);
   }

   public void removeEdge(Vertex from, Vertex to) {
      from.removeEdgeTo(to);
      to.removeEdgeFrom(from);
   }

   public Vertex getVertex(String label) {
      Vertex retValue = (Vertex)this.vertexMap.get(label);
      return retValue;
   }

   public boolean hasEdge(String label1, String label2) {
      Vertex v1 = this.getVertex(label1);
      Vertex v2 = this.getVertex(label2);
      boolean retValue = v1.getChildren().contains(v2);
      return retValue;
   }

   public List getChildLabels(String label) {
      Vertex vertex = this.getVertex(label);
      return vertex.getChildLabels();
   }

   public List getParentLabels(String label) {
      Vertex vertex = this.getVertex(label);
      return vertex.getParentLabels();
   }

   public Object clone() throws CloneNotSupportedException {
      Object retValue = super.clone();
      return retValue;
   }

   public boolean isConnected(String label) {
      Vertex vertex = this.getVertex(label);
      boolean retValue = vertex.isConnected();
      return retValue;
   }

   public List getSuccessorLabels(String label) {
      Vertex vertex = this.getVertex(label);
      Object retValue;
      if (vertex.isLeaf()) {
         retValue = new ArrayList(1);
         ((List)retValue).add(label);
      } else {
         retValue = TopologicalSorter.sort(vertex);
      }

      return (List)retValue;
   }
}
