/*     */ package org.yaml.snakeyaml.composer;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.yaml.snakeyaml.DumperOptions;
/*     */ import org.yaml.snakeyaml.LoaderOptions;
/*     */ import org.yaml.snakeyaml.comments.CommentEventsCollector;
/*     */ import org.yaml.snakeyaml.comments.CommentLine;
/*     */ import org.yaml.snakeyaml.comments.CommentType;
/*     */ import org.yaml.snakeyaml.error.Mark;
/*     */ import org.yaml.snakeyaml.error.YAMLException;
/*     */ import org.yaml.snakeyaml.events.AliasEvent;
/*     */ import org.yaml.snakeyaml.events.Event;
/*     */ import org.yaml.snakeyaml.events.MappingStartEvent;
/*     */ import org.yaml.snakeyaml.events.NodeEvent;
/*     */ import org.yaml.snakeyaml.events.ScalarEvent;
/*     */ import org.yaml.snakeyaml.events.SequenceStartEvent;
/*     */ import org.yaml.snakeyaml.nodes.MappingNode;
/*     */ import org.yaml.snakeyaml.nodes.Node;
/*     */ import org.yaml.snakeyaml.nodes.NodeId;
/*     */ import org.yaml.snakeyaml.nodes.NodeTuple;
/*     */ import org.yaml.snakeyaml.nodes.ScalarNode;
/*     */ import org.yaml.snakeyaml.nodes.SequenceNode;
/*     */ import org.yaml.snakeyaml.nodes.Tag;
/*     */ import org.yaml.snakeyaml.parser.Parser;
/*     */ import org.yaml.snakeyaml.resolver.Resolver;
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
/*     */ public class Composer
/*     */ {
/*     */   protected final Parser parser;
/*     */   private final Resolver resolver;
/*     */   private final Map<String, Node> anchors;
/*     */   private final Set<Node> recursiveNodes;
/*  61 */   private int nonScalarAliasesCount = 0;
/*     */   private final LoaderOptions loadingConfig;
/*     */   private final CommentEventsCollector blockCommentsCollector;
/*     */   private final CommentEventsCollector inlineCommentsCollector;
/*     */   
/*     */   public Composer(Parser parser, Resolver resolver) {
/*  67 */     this(parser, resolver, new LoaderOptions());
/*     */   }
/*     */   
/*     */   public Composer(Parser parser, Resolver resolver, LoaderOptions loadingConfig) {
/*  71 */     this.parser = parser;
/*  72 */     this.resolver = resolver;
/*  73 */     this.anchors = new HashMap<>();
/*  74 */     this.recursiveNodes = new HashSet<>();
/*  75 */     this.loadingConfig = loadingConfig;
/*  76 */     this.blockCommentsCollector = new CommentEventsCollector(parser, new CommentType[] { CommentType.BLANK_LINE, CommentType.BLOCK });
/*     */     
/*  78 */     this.inlineCommentsCollector = new CommentEventsCollector(parser, new CommentType[] { CommentType.IN_LINE });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean checkNode() {
/*  89 */     if (this.parser.checkEvent(Event.ID.StreamStart)) {
/*  90 */       this.parser.getEvent();
/*     */     }
/*     */     
/*  93 */     return !this.parser.checkEvent(Event.ID.StreamEnd);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Node getNode() {
/* 103 */     this.blockCommentsCollector.collectEvents();
/* 104 */     if (this.parser.checkEvent(Event.ID.StreamEnd)) {
/* 105 */       List<CommentLine> commentLines = this.blockCommentsCollector.consume();
/* 106 */       Mark startMark = ((CommentLine)commentLines.get(0)).getStartMark();
/* 107 */       List<NodeTuple> children = Collections.emptyList();
/* 108 */       MappingNode mappingNode = new MappingNode(Tag.COMMENT, false, children, startMark, null, DumperOptions.FlowStyle.BLOCK);
/* 109 */       mappingNode.setBlockComments(commentLines);
/* 110 */       return (Node)mappingNode;
/*     */     } 
/*     */     
/* 113 */     this.parser.getEvent();
/*     */     
/* 115 */     Node node = composeNode(null);
/*     */     
/* 117 */     this.blockCommentsCollector.collectEvents();
/* 118 */     if (!this.blockCommentsCollector.isEmpty()) {
/* 119 */       node.setEndComments(this.blockCommentsCollector.consume());
/*     */     }
/* 121 */     this.parser.getEvent();
/* 122 */     this.anchors.clear();
/* 123 */     this.recursiveNodes.clear();
/* 124 */     return node;
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
/*     */   public Node getSingleNode() {
/* 138 */     this.parser.getEvent();
/*     */     
/* 140 */     Node document = null;
/* 141 */     if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
/* 142 */       document = getNode();
/*     */     }
/*     */     
/* 145 */     if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
/* 146 */       Event event = this.parser.getEvent();
/* 147 */       Mark contextMark = (document != null) ? document.getStartMark() : null;
/* 148 */       throw new ComposerException("expected a single document in the stream", contextMark, "but found another document", event.getStartMark());
/*     */     } 
/*     */ 
/*     */     
/* 152 */     this.parser.getEvent();
/* 153 */     return document;
/*     */   }
/*     */   private Node composeNode(Node parent) {
/*     */     Node node;
/* 157 */     this.blockCommentsCollector.collectEvents();
/* 158 */     if (parent != null) {
/* 159 */       this.recursiveNodes.add(parent);
/*     */     }
/* 161 */     if (this.parser.checkEvent(Event.ID.Alias)) {
/* 162 */       AliasEvent event = (AliasEvent)this.parser.getEvent();
/* 163 */       String anchor = event.getAnchor();
/* 164 */       if (!this.anchors.containsKey(anchor)) {
/* 165 */         throw new ComposerException(null, null, "found undefined alias " + anchor, event.getStartMark());
/*     */       }
/*     */       
/* 168 */       node = this.anchors.get(anchor);
/* 169 */       if (!(node instanceof ScalarNode)) {
/* 170 */         this.nonScalarAliasesCount++;
/* 171 */         if (this.nonScalarAliasesCount > this.loadingConfig.getMaxAliasesForCollections()) {
/* 172 */           throw new YAMLException("Number of aliases for non-scalar nodes exceeds the specified max=" + this.loadingConfig.getMaxAliasesForCollections());
/*     */         }
/*     */       } 
/* 175 */       if (this.recursiveNodes.remove(node)) {
/* 176 */         node.setTwoStepsConstruction(true);
/*     */       }
/* 178 */       node.setBlockComments(this.blockCommentsCollector.consume());
/*     */     } else {
/* 180 */       NodeEvent event = (NodeEvent)this.parser.peekEvent();
/* 181 */       String anchor = event.getAnchor();
/*     */       
/* 183 */       if (this.parser.checkEvent(Event.ID.Scalar)) {
/* 184 */         node = composeScalarNode(anchor, this.blockCommentsCollector.consume());
/* 185 */       } else if (this.parser.checkEvent(Event.ID.SequenceStart)) {
/* 186 */         node = composeSequenceNode(anchor);
/*     */       } else {
/* 188 */         node = composeMappingNode(anchor);
/*     */       } 
/*     */     } 
/* 191 */     this.recursiveNodes.remove(parent);
/* 192 */     return node;
/*     */   }
/*     */   protected Node composeScalarNode(String anchor, List<CommentLine> blockComments) {
/*     */     Tag nodeTag;
/* 196 */     ScalarEvent ev = (ScalarEvent)this.parser.getEvent();
/* 197 */     String tag = ev.getTag();
/* 198 */     boolean resolved = false;
/*     */     
/* 200 */     if (tag == null || tag.equals("!")) {
/* 201 */       nodeTag = this.resolver.resolve(NodeId.scalar, ev.getValue(), ev.getImplicit().canOmitTagInPlainScalar());
/*     */       
/* 203 */       resolved = true;
/*     */     } else {
/* 205 */       nodeTag = new Tag(tag);
/*     */     } 
/* 207 */     ScalarNode scalarNode = new ScalarNode(nodeTag, resolved, ev.getValue(), ev.getStartMark(), ev.getEndMark(), ev.getScalarStyle());
/*     */     
/* 209 */     if (anchor != null) {
/* 210 */       scalarNode.setAnchor(anchor);
/* 211 */       this.anchors.put(anchor, scalarNode);
/*     */     } 
/* 213 */     scalarNode.setBlockComments(blockComments);
/* 214 */     scalarNode.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
/* 215 */     return (Node)scalarNode;
/*     */   }
/*     */   protected Node composeSequenceNode(String anchor) {
/*     */     Tag nodeTag;
/* 219 */     SequenceStartEvent startEvent = (SequenceStartEvent)this.parser.getEvent();
/* 220 */     String tag = startEvent.getTag();
/*     */ 
/*     */     
/* 223 */     boolean resolved = false;
/* 224 */     if (tag == null || tag.equals("!")) {
/* 225 */       nodeTag = this.resolver.resolve(NodeId.sequence, null, startEvent.getImplicit());
/* 226 */       resolved = true;
/*     */     } else {
/* 228 */       nodeTag = new Tag(tag);
/*     */     } 
/* 230 */     ArrayList<Node> children = new ArrayList<>();
/* 231 */     SequenceNode node = new SequenceNode(nodeTag, resolved, children, startEvent.getStartMark(), null, startEvent.getFlowStyle());
/*     */     
/* 233 */     if (startEvent.isFlow()) {
/* 234 */       node.setBlockComments(this.blockCommentsCollector.consume());
/*     */     }
/* 236 */     if (anchor != null) {
/* 237 */       node.setAnchor(anchor);
/* 238 */       this.anchors.put(anchor, node);
/*     */     } 
/* 240 */     while (!this.parser.checkEvent(Event.ID.SequenceEnd)) {
/* 241 */       this.blockCommentsCollector.collectEvents();
/* 242 */       if (this.parser.checkEvent(Event.ID.SequenceEnd)) {
/*     */         break;
/*     */       }
/* 245 */       children.add(composeNode((Node)node));
/*     */     } 
/* 247 */     if (startEvent.isFlow()) {
/* 248 */       node.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
/*     */     }
/* 250 */     Event endEvent = this.parser.getEvent();
/* 251 */     node.setEndMark(endEvent.getEndMark());
/* 252 */     this.inlineCommentsCollector.collectEvents();
/* 253 */     if (!this.inlineCommentsCollector.isEmpty()) {
/* 254 */       node.setInLineComments(this.inlineCommentsCollector.consume());
/*     */     }
/* 256 */     return (Node)node;
/*     */   }
/*     */   protected Node composeMappingNode(String anchor) {
/*     */     Tag nodeTag;
/* 260 */     MappingStartEvent startEvent = (MappingStartEvent)this.parser.getEvent();
/* 261 */     String tag = startEvent.getTag();
/*     */     
/* 263 */     boolean resolved = false;
/* 264 */     if (tag == null || tag.equals("!")) {
/* 265 */       nodeTag = this.resolver.resolve(NodeId.mapping, null, startEvent.getImplicit());
/* 266 */       resolved = true;
/*     */     } else {
/* 268 */       nodeTag = new Tag(tag);
/*     */     } 
/*     */     
/* 271 */     List<NodeTuple> children = new ArrayList<>();
/* 272 */     MappingNode node = new MappingNode(nodeTag, resolved, children, startEvent.getStartMark(), null, startEvent.getFlowStyle());
/*     */     
/* 274 */     if (startEvent.isFlow()) {
/* 275 */       node.setBlockComments(this.blockCommentsCollector.consume());
/*     */     }
/* 277 */     if (anchor != null) {
/* 278 */       node.setAnchor(anchor);
/* 279 */       this.anchors.put(anchor, node);
/*     */     } 
/* 281 */     while (!this.parser.checkEvent(Event.ID.MappingEnd)) {
/* 282 */       this.blockCommentsCollector.collectEvents();
/* 283 */       if (this.parser.checkEvent(Event.ID.MappingEnd)) {
/*     */         break;
/*     */       }
/* 286 */       composeMappingChildren(children, node);
/*     */     } 
/* 288 */     if (startEvent.isFlow()) {
/* 289 */       node.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
/*     */     }
/* 291 */     Event endEvent = this.parser.getEvent();
/* 292 */     node.setEndMark(endEvent.getEndMark());
/* 293 */     this.inlineCommentsCollector.collectEvents();
/* 294 */     if (!this.inlineCommentsCollector.isEmpty()) {
/* 295 */       node.setInLineComments(this.inlineCommentsCollector.consume());
/*     */     }
/* 297 */     return (Node)node;
/*     */   }
/*     */   
/*     */   protected void composeMappingChildren(List<NodeTuple> children, MappingNode node) {
/* 301 */     Node itemKey = composeKeyNode(node);
/* 302 */     if (itemKey.getTag().equals(Tag.MERGE)) {
/* 303 */       node.setMerged(true);
/*     */     }
/* 305 */     Node itemValue = composeValueNode(node);
/* 306 */     children.add(new NodeTuple(itemKey, itemValue));
/*     */   }
/*     */   
/*     */   protected Node composeKeyNode(MappingNode node) {
/* 310 */     return composeNode((Node)node);
/*     */   }
/*     */   
/*     */   protected Node composeValueNode(MappingNode node) {
/* 314 */     return composeNode((Node)node);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\composer\Composer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */