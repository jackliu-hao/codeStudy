package org.codehaus.plexus.util.dag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Vertex implements Cloneable, Serializable {
   private String label = null;
   List children = new ArrayList();
   List parents = new ArrayList();

   public Vertex(String label) {
      this.label = label;
   }

   public String getLabel() {
      return this.label;
   }

   public void addEdgeTo(Vertex vertex) {
      this.children.add(vertex);
   }

   public void removeEdgeTo(Vertex vertex) {
      this.children.remove(vertex);
   }

   public void addEdgeFrom(Vertex vertex) {
      this.parents.add(vertex);
   }

   public void removeEdgeFrom(Vertex vertex) {
      this.parents.remove(vertex);
   }

   public List getChildren() {
      return this.children;
   }

   public List getChildLabels() {
      List retValue = new ArrayList(this.children.size());
      Iterator iter = this.children.iterator();

      while(iter.hasNext()) {
         Vertex vertex = (Vertex)iter.next();
         retValue.add(vertex.getLabel());
      }

      return retValue;
   }

   public List getParents() {
      return this.parents;
   }

   public List getParentLabels() {
      List retValue = new ArrayList(this.parents.size());
      Iterator iter = this.parents.iterator();

      while(iter.hasNext()) {
         Vertex vertex = (Vertex)iter.next();
         retValue.add(vertex.getLabel());
      }

      return retValue;
   }

   public boolean isLeaf() {
      return this.children.size() == 0;
   }

   public boolean isRoot() {
      return this.parents.size() == 0;
   }

   public boolean isConnected() {
      return this.isRoot() || this.isLeaf();
   }

   public Object clone() throws CloneNotSupportedException {
      Object retValue = super.clone();
      return retValue;
   }

   public String toString() {
      return "Vertex{label='" + this.label + "'" + "}";
   }
}
