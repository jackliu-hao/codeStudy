package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PluginExecution extends ConfigurationContainer implements Serializable, Cloneable {
   private String id = "default";
   private String phase;
   private int priority = 0;
   private List<String> goals;
   public static final String DEFAULT_EXECUTION_ID = "default";

   public void addGoal(String string) {
      this.getGoals().add(string);
   }

   public PluginExecution clone() {
      try {
         PluginExecution copy = (PluginExecution)super.clone();
         if (this.goals != null) {
            copy.goals = new ArrayList();
            copy.goals.addAll(this.goals);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public List<String> getGoals() {
      if (this.goals == null) {
         this.goals = new ArrayList();
      }

      return this.goals;
   }

   public String getId() {
      return this.id;
   }

   public String getPhase() {
      return this.phase;
   }

   public int getPriority() {
      return this.priority;
   }

   public void removeGoal(String string) {
      this.getGoals().remove(string);
   }

   public void setGoals(List<String> goals) {
      this.goals = goals;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setPhase(String phase) {
      this.phase = phase;
   }

   public void setPriority(int priority) {
      this.priority = priority;
   }

   public String toString() {
      return this.getId();
   }
}
