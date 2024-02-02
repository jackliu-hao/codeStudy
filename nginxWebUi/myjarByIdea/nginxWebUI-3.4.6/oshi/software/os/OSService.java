package oshi.software.os;

import oshi.annotation.concurrent.Immutable;

@Immutable
public class OSService {
   private final String name;
   private final int processID;
   private final State state;

   public OSService(String name, int processID, State state) {
      this.name = name;
      this.processID = processID;
      this.state = state;
   }

   public String getName() {
      return this.name;
   }

   public int getProcessID() {
      return this.processID;
   }

   public State getState() {
      return this.state;
   }

   public static enum State {
      RUNNING,
      STOPPED,
      OTHER;
   }
}
