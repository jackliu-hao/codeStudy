package org.codehaus.plexus.util.dag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CycleDetector {
   private static final Integer NOT_VISTITED = new Integer(0);
   private static final Integer VISITING = new Integer(1);
   private static final Integer VISITED = new Integer(2);

   public static List hasCycle(DAG graph) {
      List verticies = graph.getVerticies();
      Map vertexStateMap = new HashMap();
      List retValue = null;
      Iterator iter = verticies.iterator();

      while(iter.hasNext()) {
         Vertex vertex = (Vertex)iter.next();
         if (isNotVisited(vertex, vertexStateMap)) {
            retValue = introducesCycle(vertex, vertexStateMap);
            if (retValue != null) {
               break;
            }
         }
      }

      return retValue;
   }

   public static List introducesCycle(Vertex vertex, Map vertexStateMap) {
      LinkedList cycleStack = new LinkedList();
      boolean hasCycle = dfsVisit(vertex, cycleStack, vertexStateMap);
      if (hasCycle) {
         String label = (String)cycleStack.getFirst();
         int pos = cycleStack.lastIndexOf(label);
         List cycle = cycleStack.subList(0, pos + 1);
         Collections.reverse(cycle);
         return cycle;
      } else {
         return null;
      }
   }

   public static List introducesCycle(Vertex vertex) {
      Map vertexStateMap = new HashMap();
      return introducesCycle(vertex, vertexStateMap);
   }

   private static boolean isNotVisited(Vertex vertex, Map vertexStateMap) {
      if (!vertexStateMap.containsKey(vertex)) {
         return true;
      } else {
         Integer state = (Integer)vertexStateMap.get(vertex);
         return NOT_VISTITED.equals(state);
      }
   }

   private static boolean isVisiting(Vertex vertex, Map vertexStateMap) {
      Integer state = (Integer)vertexStateMap.get(vertex);
      return VISITING.equals(state);
   }

   private static boolean dfsVisit(Vertex vertex, LinkedList cycle, Map vertexStateMap) {
      cycle.addFirst(vertex.getLabel());
      vertexStateMap.put(vertex, VISITING);
      List verticies = vertex.getChildren();
      Iterator iter = verticies.iterator();

      while(iter.hasNext()) {
         Vertex v = (Vertex)iter.next();
         if (isNotVisited(v, vertexStateMap)) {
            boolean hasCycle = dfsVisit(v, cycle, vertexStateMap);
            if (hasCycle) {
               return true;
            }
         } else if (isVisiting(v, vertexStateMap)) {
            cycle.addFirst(v.getLabel());
            return true;
         }
      }

      vertexStateMap.put(vertex, VISITED);
      cycle.removeFirst();
      return false;
   }
}
