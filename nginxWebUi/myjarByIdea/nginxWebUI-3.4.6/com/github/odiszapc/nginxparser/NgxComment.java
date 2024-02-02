package com.github.odiszapc.nginxparser;

public class NgxComment extends NgxAbstractEntry {
   public NgxComment(String comment) {
      super();
      this.getTokens().add(new NgxToken(comment.substring(1)));
   }

   public String getValue() {
      return this.getName();
   }

   public String toString() {
      return "#" + this.getValue();
   }
}
