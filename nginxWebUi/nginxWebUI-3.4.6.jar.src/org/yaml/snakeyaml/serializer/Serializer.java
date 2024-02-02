/*     */ package org.yaml.snakeyaml.serializer;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.yaml.snakeyaml.DumperOptions;
/*     */ import org.yaml.snakeyaml.comments.CommentLine;
/*     */ import org.yaml.snakeyaml.emitter.Emitable;
/*     */ import org.yaml.snakeyaml.events.AliasEvent;
/*     */ import org.yaml.snakeyaml.events.CommentEvent;
/*     */ import org.yaml.snakeyaml.events.DocumentEndEvent;
/*     */ import org.yaml.snakeyaml.events.DocumentStartEvent;
/*     */ import org.yaml.snakeyaml.events.Event;
/*     */ import org.yaml.snakeyaml.events.ImplicitTuple;
/*     */ import org.yaml.snakeyaml.events.MappingEndEvent;
/*     */ import org.yaml.snakeyaml.events.MappingStartEvent;
/*     */ import org.yaml.snakeyaml.events.ScalarEvent;
/*     */ import org.yaml.snakeyaml.events.SequenceEndEvent;
/*     */ import org.yaml.snakeyaml.events.SequenceStartEvent;
/*     */ import org.yaml.snakeyaml.events.StreamEndEvent;
/*     */ import org.yaml.snakeyaml.events.StreamStartEvent;
/*     */ import org.yaml.snakeyaml.nodes.AnchorNode;
/*     */ import org.yaml.snakeyaml.nodes.MappingNode;
/*     */ import org.yaml.snakeyaml.nodes.Node;
/*     */ import org.yaml.snakeyaml.nodes.NodeId;
/*     */ import org.yaml.snakeyaml.nodes.NodeTuple;
/*     */ import org.yaml.snakeyaml.nodes.ScalarNode;
/*     */ import org.yaml.snakeyaml.nodes.SequenceNode;
/*     */ import org.yaml.snakeyaml.nodes.Tag;
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
/*     */ public final class Serializer
/*     */ {
/*     */   private final Emitable emitter;
/*     */   private final Resolver resolver;
/*     */   private boolean explicitStart;
/*     */   private boolean explicitEnd;
/*     */   private DumperOptions.Version useVersion;
/*     */   private Map<String, String> useTags;
/*     */   private Set<Node> serializedNodes;
/*     */   private Map<Node, String> anchors;
/*     */   private AnchorGenerator anchorGenerator;
/*     */   private Boolean closed;
/*     */   private Tag explicitRoot;
/*     */   
/*     */   public Serializer(Emitable emitter, Resolver resolver, DumperOptions opts, Tag rootTag) {
/*  65 */     this.emitter = emitter;
/*  66 */     this.resolver = resolver;
/*  67 */     this.explicitStart = opts.isExplicitStart();
/*  68 */     this.explicitEnd = opts.isExplicitEnd();
/*  69 */     if (opts.getVersion() != null) {
/*  70 */       this.useVersion = opts.getVersion();
/*     */     }
/*  72 */     this.useTags = opts.getTags();
/*  73 */     this.serializedNodes = new HashSet<>();
/*  74 */     this.anchors = new HashMap<>();
/*  75 */     this.anchorGenerator = opts.getAnchorGenerator();
/*  76 */     this.closed = null;
/*  77 */     this.explicitRoot = rootTag;
/*     */   }
/*     */   
/*     */   public void open() throws IOException {
/*  81 */     if (this.closed == null)
/*  82 */     { this.emitter.emit((Event)new StreamStartEvent(null, null));
/*  83 */       this.closed = Boolean.FALSE; }
/*  84 */     else { if (Boolean.TRUE.equals(this.closed)) {
/*  85 */         throw new SerializerException("serializer is closed");
/*     */       }
/*  87 */       throw new SerializerException("serializer is already opened"); }
/*     */   
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/*  92 */     if (this.closed == null)
/*  93 */       throw new SerializerException("serializer is not opened"); 
/*  94 */     if (!Boolean.TRUE.equals(this.closed)) {
/*  95 */       this.emitter.emit((Event)new StreamEndEvent(null, null));
/*  96 */       this.closed = Boolean.TRUE;
/*     */       
/*  98 */       this.serializedNodes.clear();
/*  99 */       this.anchors.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void serialize(Node node) throws IOException {
/* 104 */     if (this.closed == null)
/* 105 */       throw new SerializerException("serializer is not opened"); 
/* 106 */     if (this.closed.booleanValue()) {
/* 107 */       throw new SerializerException("serializer is closed");
/*     */     }
/* 109 */     this.emitter.emit((Event)new DocumentStartEvent(null, null, this.explicitStart, this.useVersion, this.useTags));
/*     */     
/* 111 */     anchorNode(node);
/* 112 */     if (this.explicitRoot != null) {
/* 113 */       node.setTag(this.explicitRoot);
/*     */     }
/* 115 */     serializeNode(node, null);
/* 116 */     this.emitter.emit((Event)new DocumentEndEvent(null, null, this.explicitEnd));
/* 117 */     this.serializedNodes.clear();
/* 118 */     this.anchors.clear();
/*     */   }
/*     */   
/*     */   private void anchorNode(Node node) {
/* 122 */     if (node.getNodeId() == NodeId.anchor) {
/* 123 */       node = ((AnchorNode)node).getRealNode();
/*     */     }
/* 125 */     if (this.anchors.containsKey(node)) {
/* 126 */       String anchor = this.anchors.get(node);
/* 127 */       if (null == anchor) {
/* 128 */         anchor = this.anchorGenerator.nextAnchor(node);
/* 129 */         this.anchors.put(node, anchor);
/*     */       } 
/*     */     } else {
/* 132 */       SequenceNode seqNode; List<Node> list; MappingNode mnode; List<NodeTuple> map; this.anchors.put(node, (node.getAnchor() != null) ? this.anchorGenerator.nextAnchor(node) : null);
/* 133 */       switch (node.getNodeId()) {
/*     */         case sequence:
/* 135 */           seqNode = (SequenceNode)node;
/* 136 */           list = seqNode.getValue();
/* 137 */           for (Node item : list) {
/* 138 */             anchorNode(item);
/*     */           }
/*     */           break;
/*     */         case mapping:
/* 142 */           mnode = (MappingNode)node;
/* 143 */           map = mnode.getValue();
/* 144 */           for (NodeTuple object : map) {
/* 145 */             Node key = object.getKeyNode();
/* 146 */             Node value = object.getValueNode();
/* 147 */             anchorNode(key);
/* 148 */             anchorNode(value);
/*     */           } 
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void serializeNode(Node node, Node parent) throws IOException {
/* 157 */     if (node.getNodeId() == NodeId.anchor) {
/* 158 */       node = ((AnchorNode)node).getRealNode();
/*     */     }
/* 160 */     String tAlias = this.anchors.get(node);
/* 161 */     if (this.serializedNodes.contains(node)) {
/* 162 */       this.emitter.emit((Event)new AliasEvent(tAlias, null, null));
/*     */     } else {
/* 164 */       ScalarNode scalarNode; Tag detectedTag, defaultTag; ImplicitTuple tuple; ScalarEvent event; SequenceNode seqNode; boolean implicitS; List<Node> list; this.serializedNodes.add(node);
/* 165 */       switch (node.getNodeId()) {
/*     */         case scalar:
/* 167 */           scalarNode = (ScalarNode)node;
/* 168 */           serializeComments(node.getBlockComments());
/* 169 */           detectedTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), true);
/* 170 */           defaultTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), false);
/* 171 */           tuple = new ImplicitTuple(node.getTag().equals(detectedTag), node.getTag().equals(defaultTag));
/*     */           
/* 173 */           event = new ScalarEvent(tAlias, node.getTag().getValue(), tuple, scalarNode.getValue(), null, null, scalarNode.getScalarStyle());
/*     */           
/* 175 */           this.emitter.emit((Event)event);
/* 176 */           serializeComments(node.getInLineComments());
/* 177 */           serializeComments(node.getEndComments());
/*     */           return;
/*     */         case sequence:
/* 180 */           seqNode = (SequenceNode)node;
/* 181 */           serializeComments(node.getBlockComments());
/* 182 */           implicitS = node.getTag().equals(this.resolver.resolve(NodeId.sequence, null, true));
/*     */           
/* 184 */           this.emitter.emit((Event)new SequenceStartEvent(tAlias, node.getTag().getValue(), implicitS, null, null, seqNode.getFlowStyle()));
/*     */           
/* 186 */           list = seqNode.getValue();
/* 187 */           for (Node item : list) {
/* 188 */             serializeNode(item, node);
/*     */           }
/* 190 */           this.emitter.emit((Event)new SequenceEndEvent(null, null));
/* 191 */           serializeComments(node.getInLineComments());
/* 192 */           serializeComments(node.getEndComments());
/*     */           return;
/*     */       } 
/* 195 */       serializeComments(node.getBlockComments());
/* 196 */       Tag implicitTag = this.resolver.resolve(NodeId.mapping, null, true);
/* 197 */       boolean implicitM = node.getTag().equals(implicitTag);
/* 198 */       MappingNode mnode = (MappingNode)node;
/* 199 */       List<NodeTuple> map = mnode.getValue();
/* 200 */       if (mnode.getTag() != Tag.COMMENT) {
/* 201 */         this.emitter.emit((Event)new MappingStartEvent(tAlias, mnode.getTag().getValue(), implicitM, null, null, mnode.getFlowStyle()));
/*     */         
/* 203 */         for (NodeTuple row : map) {
/* 204 */           Node key = row.getKeyNode();
/* 205 */           Node value = row.getValueNode();
/* 206 */           serializeNode(key, (Node)mnode);
/* 207 */           serializeNode(value, (Node)mnode);
/*     */         } 
/* 209 */         this.emitter.emit((Event)new MappingEndEvent(null, null));
/* 210 */         serializeComments(node.getInLineComments());
/* 211 */         serializeComments(node.getEndComments());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void serializeComments(List<CommentLine> comments) throws IOException {
/* 218 */     if (comments == null) {
/*     */       return;
/*     */     }
/* 221 */     for (CommentLine line : comments) {
/* 222 */       CommentEvent commentEvent = new CommentEvent(line.getCommentType(), line.getValue(), line.getStartMark(), line.getEndMark());
/*     */       
/* 224 */       this.emitter.emit((Event)commentEvent);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\serializer\Serializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */