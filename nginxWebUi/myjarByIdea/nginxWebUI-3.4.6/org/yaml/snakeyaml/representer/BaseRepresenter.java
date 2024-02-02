package org.yaml.snakeyaml.representer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public abstract class BaseRepresenter {
   protected final Map<Class<?>, Represent> representers = new HashMap();
   protected Represent nullRepresenter;
   protected final Map<Class<?>, Represent> multiRepresenters = new LinkedHashMap();
   protected DumperOptions.ScalarStyle defaultScalarStyle = null;
   protected DumperOptions.FlowStyle defaultFlowStyle;
   protected final Map<Object, Node> representedObjects;
   protected Object objectToRepresent;
   private PropertyUtils propertyUtils;
   private boolean explicitPropertyUtils;

   public BaseRepresenter() {
      this.defaultFlowStyle = DumperOptions.FlowStyle.AUTO;
      this.representedObjects = new IdentityHashMap<Object, Node>() {
         private static final long serialVersionUID = -5576159264232131854L;

         public Node put(Object key, Node value) {
            return (Node)super.put(key, new AnchorNode(value));
         }
      };
      this.explicitPropertyUtils = false;
   }

   public Node represent(Object data) {
      Node node = this.representData(data);
      this.representedObjects.clear();
      this.objectToRepresent = null;
      return node;
   }

   protected final Node representData(Object data) {
      this.objectToRepresent = data;
      Node node;
      if (this.representedObjects.containsKey(this.objectToRepresent)) {
         node = (Node)this.representedObjects.get(this.objectToRepresent);
         return node;
      } else if (data == null) {
         node = this.nullRepresenter.representData((Object)null);
         return node;
      } else {
         Class<?> clazz = data.getClass();
         Represent representer;
         if (this.representers.containsKey(clazz)) {
            representer = (Represent)this.representers.get(clazz);
            node = representer.representData(data);
         } else {
            Iterator i$ = this.multiRepresenters.keySet().iterator();

            while(i$.hasNext()) {
               Class<?> repr = (Class)i$.next();
               if (repr != null && repr.isInstance(data)) {
                  Represent representer = (Represent)this.multiRepresenters.get(repr);
                  node = representer.representData(data);
                  return node;
               }
            }

            if (this.multiRepresenters.containsKey((Object)null)) {
               representer = (Represent)this.multiRepresenters.get((Object)null);
               node = representer.representData(data);
            } else {
               representer = (Represent)this.representers.get((Object)null);
               node = representer.representData(data);
            }
         }

         return node;
      }
   }

   protected Node representScalar(Tag tag, String value, DumperOptions.ScalarStyle style) {
      if (style == null) {
         style = this.defaultScalarStyle;
      }

      Node node = new ScalarNode(tag, value, (Mark)null, (Mark)null, style);
      return node;
   }

   protected Node representScalar(Tag tag, String value) {
      return this.representScalar(tag, value, (DumperOptions.ScalarStyle)null);
   }

   protected Node representSequence(Tag tag, Iterable<?> sequence, DumperOptions.FlowStyle flowStyle) {
      int size = 10;
      if (sequence instanceof List) {
         size = ((List)sequence).size();
      }

      List<Node> value = new ArrayList(size);
      SequenceNode node = new SequenceNode(tag, value, flowStyle);
      this.representedObjects.put(this.objectToRepresent, node);
      DumperOptions.FlowStyle bestStyle = DumperOptions.FlowStyle.FLOW;

      Node nodeItem;
      for(Iterator i$ = sequence.iterator(); i$.hasNext(); value.add(nodeItem)) {
         Object item = i$.next();
         nodeItem = this.representData(item);
         if (!(nodeItem instanceof ScalarNode) || !((ScalarNode)nodeItem).isPlain()) {
            bestStyle = DumperOptions.FlowStyle.BLOCK;
         }
      }

      if (flowStyle == DumperOptions.FlowStyle.AUTO) {
         if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
            node.setFlowStyle(this.defaultFlowStyle);
         } else {
            node.setFlowStyle(bestStyle);
         }
      }

      return node;
   }

   protected Node representMapping(Tag tag, Map<?, ?> mapping, DumperOptions.FlowStyle flowStyle) {
      List<NodeTuple> value = new ArrayList(mapping.size());
      MappingNode node = new MappingNode(tag, value, flowStyle);
      this.representedObjects.put(this.objectToRepresent, node);
      DumperOptions.FlowStyle bestStyle = DumperOptions.FlowStyle.FLOW;

      Node nodeKey;
      Node nodeValue;
      for(Iterator i$ = mapping.entrySet().iterator(); i$.hasNext(); value.add(new NodeTuple(nodeKey, nodeValue))) {
         Map.Entry<?, ?> entry = (Map.Entry)i$.next();
         nodeKey = this.representData(entry.getKey());
         nodeValue = this.representData(entry.getValue());
         if (!(nodeKey instanceof ScalarNode) || !((ScalarNode)nodeKey).isPlain()) {
            bestStyle = DumperOptions.FlowStyle.BLOCK;
         }

         if (!(nodeValue instanceof ScalarNode) || !((ScalarNode)nodeValue).isPlain()) {
            bestStyle = DumperOptions.FlowStyle.BLOCK;
         }
      }

      if (flowStyle == DumperOptions.FlowStyle.AUTO) {
         if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
            node.setFlowStyle(this.defaultFlowStyle);
         } else {
            node.setFlowStyle(bestStyle);
         }
      }

      return node;
   }

   public void setDefaultScalarStyle(DumperOptions.ScalarStyle defaultStyle) {
      this.defaultScalarStyle = defaultStyle;
   }

   public DumperOptions.ScalarStyle getDefaultScalarStyle() {
      return this.defaultScalarStyle == null ? DumperOptions.ScalarStyle.PLAIN : this.defaultScalarStyle;
   }

   public void setDefaultFlowStyle(DumperOptions.FlowStyle defaultFlowStyle) {
      this.defaultFlowStyle = defaultFlowStyle;
   }

   public DumperOptions.FlowStyle getDefaultFlowStyle() {
      return this.defaultFlowStyle;
   }

   public void setPropertyUtils(PropertyUtils propertyUtils) {
      this.propertyUtils = propertyUtils;
      this.explicitPropertyUtils = true;
   }

   public final PropertyUtils getPropertyUtils() {
      if (this.propertyUtils == null) {
         this.propertyUtils = new PropertyUtils();
      }

      return this.propertyUtils;
   }

   public final boolean isExplicitPropertyUtils() {
      return this.explicitPropertyUtils;
   }
}
