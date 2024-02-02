package org.codehaus.plexus.util.dag;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TopologicalSorter {
   private static final Integer NOT_VISTITED = new Integer(0);
   private static final Integer VISITING = new Integer(1);
   private static final Integer VISITED = new Integer(2);

   public static List sort(DAG graph) {
      return dfs(graph);
   }

   public static List sort(Vertex vertex) {
      LinkedList retValue = new LinkedList();
      Map vertexStateMap = new HashMap();
      dfsVisit(vertex, vertexStateMap, retValue);
      return retValue;
   }

   private static List dfs(DAG graph) {
      List verticies = graph.getVerticies();
      LinkedList retValue = new LinkedList();
      Map vertexStateMap = new HashMap();
      Iterator iter = verticies.iterator();

      while(iter.hasNext()) {
         Vertex vertex = (Vertex)iter.next();
         if (isNotVisited(vertex, vertexStateMap)) {
            dfsVisit(vertex, vertexStateMap, retValue);
         }
      }

      return retValue;
   }

   private static boolean isNotVisited(Vertex vertex, Map vertexStateMap) {
      if (!vertexStateMap.containsKey(vertex)) {
         return true;
      } else {
         Integer state = (Integer)vertexStateMap.get(vertex);
         return NOT_VISTITED.equals(state);
      }
   }

   private static void dfsVisit(Vertex vertex, Map vertexStateMap, LinkedList list) {
      vertexStateMap.put(vertex, VISITING);
      List verticies = vertex.getChildren();
      Iterator iter = verticies.iterator();

      while(iter.hasNext()) {
         Vertex v = (Vertex)iter.next();
         if (isNotVisited(v, vertexStateMap)) {
            dfsVisit(v, vertexStateMap, list);
         }
      }

      vertexStateMap.put(vertex, VISITED);
      list.add(vertex.getLabel());
   }
}
