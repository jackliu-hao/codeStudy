package org.antlr.v4.runtime.tree.pattern;

import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParseTreeMatch {
   private final ParseTree tree;
   private final ParseTreePattern pattern;
   private final MultiMap<String, ParseTree> labels;
   private final ParseTree mismatchedNode;

   public ParseTreeMatch(ParseTree tree, ParseTreePattern pattern, MultiMap<String, ParseTree> labels, ParseTree mismatchedNode) {
      if (tree == null) {
         throw new IllegalArgumentException("tree cannot be null");
      } else if (pattern == null) {
         throw new IllegalArgumentException("pattern cannot be null");
      } else if (labels == null) {
         throw new IllegalArgumentException("labels cannot be null");
      } else {
         this.tree = tree;
         this.pattern = pattern;
         this.labels = labels;
         this.mismatchedNode = mismatchedNode;
      }
   }

   public ParseTree get(String label) {
      List<ParseTree> parseTrees = (List)this.labels.get(label);
      return parseTrees != null && parseTrees.size() != 0 ? (ParseTree)parseTrees.get(parseTrees.size() - 1) : null;
   }

   public List<ParseTree> getAll(String label) {
      List<ParseTree> nodes = (List)this.labels.get(label);
      return nodes == null ? Collections.emptyList() : nodes;
   }

   public MultiMap<String, ParseTree> getLabels() {
      return this.labels;
   }

   public ParseTree getMismatchedNode() {
      return this.mismatchedNode;
   }

   public boolean succeeded() {
      return this.mismatchedNode == null;
   }

   public ParseTreePattern getPattern() {
      return this.pattern;
   }

   public ParseTree getTree() {
      return this.tree;
   }

   public String toString() {
      return String.format("Match %s; found %d labels", this.succeeded() ? "succeeded" : "failed", this.getLabels().size());
   }
}
