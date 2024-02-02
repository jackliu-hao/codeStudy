package org.yaml.snakeyaml.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.CommentToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.util.ArrayStack;

public class ParserImpl implements Parser {
   private static final Map<String, String> DEFAULT_TAGS = new HashMap();
   protected final Scanner scanner;
   private Event currentEvent;
   private final ArrayStack<Production> states;
   private final ArrayStack<Mark> marks;
   private Production state;
   private VersionTagsTuple directives;

   public ParserImpl(StreamReader reader) {
      this((Scanner)(new ScannerImpl(reader)));
   }

   public ParserImpl(StreamReader reader, boolean emitComments) {
      this((Scanner)(new ScannerImpl(reader)).setEmitComments(emitComments));
   }

   public ParserImpl(Scanner scanner) {
      this.scanner = scanner;
      this.currentEvent = null;
      this.directives = new VersionTagsTuple((DumperOptions.Version)null, new HashMap(DEFAULT_TAGS));
      this.states = new ArrayStack(100);
      this.marks = new ArrayStack(10);
      this.state = new ParseStreamStart();
   }

   public boolean checkEvent(Event.ID choice) {
      this.peekEvent();
      return this.currentEvent != null && this.currentEvent.is(choice);
   }

   public Event peekEvent() {
      if (this.currentEvent == null && this.state != null) {
         this.currentEvent = this.state.produce();
      }

      return this.currentEvent;
   }

   public Event getEvent() {
      this.peekEvent();
      Event value = this.currentEvent;
      this.currentEvent = null;
      return value;
   }

   private CommentEvent produceCommentEvent(CommentToken token) {
      Mark startMark = token.getStartMark();
      Mark endMark = token.getEndMark();
      String value = token.getValue();
      CommentType type = token.getCommentType();
      return new CommentEvent(type, value, startMark, endMark);
   }

   private VersionTagsTuple processDirectives() {
      DumperOptions.Version yamlVersion = null;
      HashMap<String, String> tagHandles = new HashMap();

      while(this.scanner.checkToken(Token.ID.Directive)) {
         DirectiveToken token = (DirectiveToken)this.scanner.getToken();
         List value;
         if (token.getName().equals("YAML")) {
            if (yamlVersion != null) {
               throw new ParserException((String)null, (Mark)null, "found duplicate YAML directive", token.getStartMark());
            }

            value = token.getValue();
            Integer major = (Integer)value.get(0);
            if (major != 1) {
               throw new ParserException((String)null, (Mark)null, "found incompatible YAML document (version 1.* is required)", token.getStartMark());
            }

            Integer minor = (Integer)value.get(1);
            switch (minor) {
               case 0:
                  yamlVersion = DumperOptions.Version.V1_0;
                  break;
               default:
                  yamlVersion = DumperOptions.Version.V1_1;
            }
         } else if (token.getName().equals("TAG")) {
            value = token.getValue();
            String handle = (String)value.get(0);
            String prefix = (String)value.get(1);
            if (tagHandles.containsKey(handle)) {
               throw new ParserException((String)null, (Mark)null, "duplicate tag handle " + handle, token.getStartMark());
            }

            tagHandles.put(handle, prefix);
         }
      }

      if (yamlVersion != null || !tagHandles.isEmpty()) {
         Iterator i$ = DEFAULT_TAGS.keySet().iterator();

         while(i$.hasNext()) {
            String key = (String)i$.next();
            if (!tagHandles.containsKey(key)) {
               tagHandles.put(key, DEFAULT_TAGS.get(key));
            }
         }

         this.directives = new VersionTagsTuple(yamlVersion, tagHandles);
      }

      return this.directives;
   }

   private Event parseFlowNode() {
      return this.parseNode(false, false);
   }

   private Event parseBlockNodeOrIndentlessSequence() {
      return this.parseNode(true, true);
   }

   private Event parseNode(boolean block, boolean indentlessSequence) {
      Mark startMark = null;
      Mark endMark = null;
      Mark tagMark = null;
      Object event;
      if (this.scanner.checkToken(Token.ID.Alias)) {
         AliasToken token = (AliasToken)this.scanner.getToken();
         event = new AliasEvent(token.getValue(), token.getStartMark(), token.getEndMark());
         this.state = (Production)this.states.pop();
      } else {
         String anchor = null;
         TagTuple tagTokenTag = null;
         if (this.scanner.checkToken(Token.ID.Anchor)) {
            AnchorToken token = (AnchorToken)this.scanner.getToken();
            startMark = token.getStartMark();
            endMark = token.getEndMark();
            anchor = token.getValue();
            if (this.scanner.checkToken(Token.ID.Tag)) {
               TagToken tagToken = (TagToken)this.scanner.getToken();
               tagMark = tagToken.getStartMark();
               endMark = tagToken.getEndMark();
               tagTokenTag = tagToken.getValue();
            }
         } else if (this.scanner.checkToken(Token.ID.Tag)) {
            TagToken tagToken = (TagToken)this.scanner.getToken();
            startMark = tagToken.getStartMark();
            tagMark = startMark;
            endMark = tagToken.getEndMark();
            tagTokenTag = tagToken.getValue();
            if (this.scanner.checkToken(Token.ID.Anchor)) {
               AnchorToken token = (AnchorToken)this.scanner.getToken();
               endMark = token.getEndMark();
               anchor = token.getValue();
            }
         }

         String tag = null;
         String node;
         if (tagTokenTag != null) {
            String handle = tagTokenTag.getHandle();
            node = tagTokenTag.getSuffix();
            if (handle != null) {
               if (!this.directives.getTags().containsKey(handle)) {
                  throw new ParserException("while parsing a node", startMark, "found undefined tag handle " + handle, tagMark);
               }

               tag = (String)this.directives.getTags().get(handle) + node;
            } else {
               tag = node;
            }
         }

         if (startMark == null) {
            startMark = this.scanner.peekToken().getStartMark();
            endMark = startMark;
         }

         event = null;
         boolean implicit = tag == null || tag.equals("!");
         if (indentlessSequence && this.scanner.checkToken(Token.ID.BlockEntry)) {
            endMark = this.scanner.peekToken().getEndMark();
            event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.BLOCK);
            this.state = new ParseIndentlessSequenceEntryKey();
         } else if (this.scanner.checkToken(Token.ID.Scalar)) {
            ScalarToken token = (ScalarToken)this.scanner.getToken();
            endMark = token.getEndMark();
            ImplicitTuple implicitValues;
            if ((!token.getPlain() || tag != null) && !"!".equals(tag)) {
               if (tag == null) {
                  implicitValues = new ImplicitTuple(false, true);
               } else {
                  implicitValues = new ImplicitTuple(false, false);
               }
            } else {
               implicitValues = new ImplicitTuple(true, false);
            }

            event = new ScalarEvent(anchor, tag, implicitValues, token.getValue(), startMark, endMark, token.getStyle());
            this.state = (Production)this.states.pop();
         } else if (this.scanner.checkToken(Token.ID.Comment)) {
            event = this.produceCommentEvent((CommentToken)this.scanner.getToken());
         } else if (this.scanner.checkToken(Token.ID.FlowSequenceStart)) {
            endMark = this.scanner.peekToken().getEndMark();
            event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.FLOW);
            this.state = new ParseFlowSequenceFirstEntry();
         } else if (this.scanner.checkToken(Token.ID.FlowMappingStart)) {
            endMark = this.scanner.peekToken().getEndMark();
            event = new MappingStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.FLOW);
            this.state = new ParseFlowMappingFirstKey();
         } else if (block && this.scanner.checkToken(Token.ID.BlockSequenceStart)) {
            endMark = this.scanner.peekToken().getStartMark();
            event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.BLOCK);
            this.state = new ParseBlockSequenceFirstEntry();
         } else if (block && this.scanner.checkToken(Token.ID.BlockMappingStart)) {
            endMark = this.scanner.peekToken().getStartMark();
            event = new MappingStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.BLOCK);
            this.state = new ParseBlockMappingFirstKey();
         } else {
            if (anchor == null && tag == null) {
               if (block) {
                  node = "block";
               } else {
                  node = "flow";
               }

               Token token = this.scanner.peekToken();
               throw new ParserException("while parsing a " + node + " node", startMark, "expected the node content, but found '" + token.getTokenId() + "'", token.getStartMark());
            }

            event = new ScalarEvent(anchor, tag, new ImplicitTuple(implicit, false), "", startMark, endMark, DumperOptions.ScalarStyle.PLAIN);
            this.state = (Production)this.states.pop();
         }
      }

      return (Event)event;
   }

   private Event processEmptyScalar(Mark mark) {
      return new ScalarEvent((String)null, (String)null, new ImplicitTuple(true, false), "", mark, mark, DumperOptions.ScalarStyle.PLAIN);
   }

   static {
      DEFAULT_TAGS.put("!", "!");
      DEFAULT_TAGS.put("!!", "tag:yaml.org,2002:");
   }

   private class ParseFlowMappingEmptyValue implements Production {
      private ParseFlowMappingEmptyValue() {
      }

      public Event produce() {
         ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingKey(false);
         return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
      }

      // $FF: synthetic method
      ParseFlowMappingEmptyValue(Object x1) {
         this();
      }
   }

   private class ParseFlowMappingValue implements Production {
      private ParseFlowMappingValue() {
      }

      public Event produce() {
         Token token;
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            token = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingKey(false));
               return ParserImpl.this.parseFlowNode();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingKey(false);
               return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingKey(false);
            token = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(token.getStartMark());
         }
      }

      // $FF: synthetic method
      ParseFlowMappingValue(Object x1) {
         this();
      }
   }

   private class ParseFlowMappingKey implements Production {
      private boolean first = false;

      public ParseFlowMappingKey(boolean first) {
         this.first = first;
      }

      public Event produce() {
         Token token;
         if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
            if (!this.first) {
               if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                  token = ParserImpl.this.scanner.peekToken();
                  throw new ParserException("while parsing a flow mapping", (Mark)ParserImpl.this.marks.pop(), "expected ',' or '}', but got " + token.getTokenId(), token.getStartMark());
               }

               ParserImpl.this.scanner.getToken();
            }

            if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
               token = ParserImpl.this.scanner.getToken();
               if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
                  ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingValue());
                  return ParserImpl.this.parseFlowNode();
               }

               ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingValue();
               return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }

            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingEmptyValue());
               return ParserImpl.this.parseFlowNode();
            }
         }

         token = ParserImpl.this.scanner.getToken();
         Event event = new MappingEndEvent(token.getStartMark(), token.getEndMark());
         ParserImpl.this.marks.pop();
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowEndComment();
         }

         return event;
      }
   }

   private class ParseFlowMappingFirstKey implements Production {
      private ParseFlowMappingFirstKey() {
      }

      public Event produce() {
         Token token = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(token.getStartMark());
         return (ParserImpl.this.new ParseFlowMappingKey(true)).produce();
      }

      // $FF: synthetic method
      ParseFlowMappingFirstKey(Object x1) {
         this();
      }
   }

   private class ParseFlowSequenceEntryMappingEnd implements Production {
      private ParseFlowSequenceEntryMappingEnd() {
      }

      public Event produce() {
         ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntry(false);
         Token token = ParserImpl.this.scanner.peekToken();
         return new MappingEndEvent(token.getStartMark(), token.getEndMark());
      }

      // $FF: synthetic method
      ParseFlowSequenceEntryMappingEnd(Object x1) {
         this();
      }
   }

   private class ParseFlowSequenceEntryMappingValue implements Production {
      private ParseFlowSequenceEntryMappingValue() {
      }

      public Event produce() {
         Token token;
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            token = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntryMappingEnd());
               return ParserImpl.this.parseFlowNode();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingEnd();
               return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingEnd();
            token = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(token.getStartMark());
         }
      }

      // $FF: synthetic method
      ParseFlowSequenceEntryMappingValue(Object x1) {
         this();
      }
   }

   private class ParseFlowSequenceEntryMappingKey implements Production {
      private ParseFlowSequenceEntryMappingKey() {
      }

      public Event produce() {
         Token token = ParserImpl.this.scanner.getToken();
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntryMappingValue());
            return ParserImpl.this.parseFlowNode();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingValue();
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
         }
      }

      // $FF: synthetic method
      ParseFlowSequenceEntryMappingKey(Object x1) {
         this();
      }
   }

   private class ParseFlowEndComment implements Production {
      private ParseFlowEndComment() {
      }

      public Event produce() {
         Event event = ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
         }

         return event;
      }

      // $FF: synthetic method
      ParseFlowEndComment(Object x1) {
         this();
      }
   }

   private class ParseFlowSequenceEntry implements Production {
      private boolean first = false;

      public ParseFlowSequenceEntry(boolean first) {
         this.first = first;
      }

      public Event produce() {
         Token token;
         if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
            if (!this.first) {
               if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                  token = ParserImpl.this.scanner.peekToken();
                  throw new ParserException("while parsing a flow sequence", (Mark)ParserImpl.this.marks.pop(), "expected ',' or ']', but got " + token.getTokenId(), token.getStartMark());
               }

               ParserImpl.this.scanner.getToken();
            }

            if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
               token = ParserImpl.this.scanner.peekToken();
               Event event = new MappingStartEvent((String)null, (String)null, true, token.getStartMark(), token.getEndMark(), DumperOptions.FlowStyle.FLOW);
               ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingKey();
               return event;
            }

            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntry(false));
               return ParserImpl.this.parseFlowNode();
            }
         }

         token = ParserImpl.this.scanner.getToken();
         Event eventx = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
         ParserImpl.this.marks.pop();
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowEndComment();
         }

         return eventx;
      }
   }

   private class ParseFlowSequenceFirstEntry implements Production {
      private ParseFlowSequenceFirstEntry() {
      }

      public Event produce() {
         Token token = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(token.getStartMark());
         return (ParserImpl.this.new ParseFlowSequenceEntry(true)).produce();
      }

      // $FF: synthetic method
      ParseFlowSequenceFirstEntry(Object x1) {
         this();
      }
   }

   private class ParseBlockMappingValueComment implements Production {
      private ParseBlockMappingValueComment() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingKey());
            return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingKey();
            Token token = ParserImpl.this.scanner.getToken();
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
         }
      }

      // $FF: synthetic method
      ParseBlockMappingValueComment(Object x1) {
         this();
      }
   }

   private class ParseBlockMappingValue implements Production {
      private ParseBlockMappingValue() {
      }

      public Event produce() {
         Token token;
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            token = ParserImpl.this.scanner.getToken();
            if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
               ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingValueComment();
               return ParserImpl.this.state.produce();
            } else if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingKey());
               return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingKey();
               return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.Scalar)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingKey());
            return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingKey();
            token = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(token.getStartMark());
         }
      }

      // $FF: synthetic method
      ParseBlockMappingValue(Object x1) {
         this();
      }
   }

   private class ParseBlockMappingKey implements Production {
      private ParseBlockMappingKey() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else {
            Token token;
            if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
               token = ParserImpl.this.scanner.getToken();
               if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
                  ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingValue());
                  return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
               } else {
                  ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingValue();
                  return ParserImpl.this.processEmptyScalar(token.getEndMark());
               }
            } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
               token = ParserImpl.this.scanner.peekToken();
               throw new ParserException("while parsing a block mapping", (Mark)ParserImpl.this.marks.pop(), "expected <block end>, but found '" + token.getTokenId() + "'", token.getStartMark());
            } else {
               token = ParserImpl.this.scanner.getToken();
               Event event = new MappingEndEvent(token.getStartMark(), token.getEndMark());
               ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
               ParserImpl.this.marks.pop();
               return event;
            }
         }
      }

      // $FF: synthetic method
      ParseBlockMappingKey(Object x1) {
         this();
      }
   }

   private class ParseBlockMappingFirstKey implements Production {
      private ParseBlockMappingFirstKey() {
      }

      public Event produce() {
         Token token = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(token.getStartMark());
         return (ParserImpl.this.new ParseBlockMappingKey()).produce();
      }

      // $FF: synthetic method
      ParseBlockMappingFirstKey(Object x1) {
         this();
      }
   }

   private class ParseIndentlessSequenceEntryValue implements Production {
      BlockEntryToken token;

      public ParseIndentlessSequenceEntryValue(BlockEntryToken token) {
         this.token = token;
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.new ParseIndentlessSequenceEntryValue(this.token);
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseIndentlessSequenceEntryKey());
            return (ParserImpl.this.new ParseBlockNode()).produce();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseIndentlessSequenceEntryKey();
            return ParserImpl.this.processEmptyScalar(this.token.getEndMark());
         }
      }
   }

   private class ParseIndentlessSequenceEntryKey implements Production {
      private ParseIndentlessSequenceEntryKey() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
            BlockEntryToken token = (BlockEntryToken)ParserImpl.this.scanner.getToken();
            return (ParserImpl.this.new ParseIndentlessSequenceEntryValue(token)).produce();
         } else {
            Token tokenx = ParserImpl.this.scanner.peekToken();
            Event event = new SequenceEndEvent(tokenx.getStartMark(), tokenx.getEndMark());
            ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
            return event;
         }
      }

      // $FF: synthetic method
      ParseIndentlessSequenceEntryKey(Object x1) {
         this();
      }
   }

   private class ParseBlockSequenceEntryValue implements Production {
      BlockEntryToken token;

      public ParseBlockSequenceEntryValue(BlockEntryToken token) {
         this.token = token;
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.new ParseBlockSequenceEntryValue(this.token);
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.BlockEnd)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseBlockSequenceEntryKey());
            return (ParserImpl.this.new ParseBlockNode()).produce();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseBlockSequenceEntryKey();
            return ParserImpl.this.processEmptyScalar(this.token.getEndMark());
         }
      }
   }

   private class ParseBlockSequenceEntryKey implements Production {
      private ParseBlockSequenceEntryKey() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
            BlockEntryToken token = (BlockEntryToken)ParserImpl.this.scanner.getToken();
            return (ParserImpl.this.new ParseBlockSequenceEntryValue(token)).produce();
         } else {
            Token tokenx;
            if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
               tokenx = ParserImpl.this.scanner.peekToken();
               throw new ParserException("while parsing a block collection", (Mark)ParserImpl.this.marks.pop(), "expected <block end>, but found '" + tokenx.getTokenId() + "'", tokenx.getStartMark());
            } else {
               tokenx = ParserImpl.this.scanner.getToken();
               Event event = new SequenceEndEvent(tokenx.getStartMark(), tokenx.getEndMark());
               ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
               ParserImpl.this.marks.pop();
               return event;
            }
         }
      }

      // $FF: synthetic method
      ParseBlockSequenceEntryKey(Object x1) {
         this();
      }
   }

   private class ParseBlockSequenceFirstEntry implements Production {
      private ParseBlockSequenceFirstEntry() {
      }

      public Event produce() {
         Token token = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(token.getStartMark());
         return (ParserImpl.this.new ParseBlockSequenceEntryKey()).produce();
      }

      // $FF: synthetic method
      ParseBlockSequenceFirstEntry(Object x1) {
         this();
      }
   }

   private class ParseBlockNode implements Production {
      private ParseBlockNode() {
      }

      public Event produce() {
         return ParserImpl.this.parseNode(true, false);
      }

      // $FF: synthetic method
      ParseBlockNode(Object x1) {
         this();
      }
   }

   private class ParseDocumentContent implements Production {
      private ParseDocumentContent() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.DocumentEnd, Token.ID.StreamEnd)) {
            Event event = ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
            ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
            return event;
         } else {
            Production p = ParserImpl.this.new ParseBlockNode();
            return p.produce();
         }
      }

      // $FF: synthetic method
      ParseDocumentContent(Object x1) {
         this();
      }
   }

   private class ParseDocumentEnd implements Production {
      private ParseDocumentEnd() {
      }

      public Event produce() {
         Token token = ParserImpl.this.scanner.peekToken();
         Mark startMark = token.getStartMark();
         Mark endMark = startMark;
         boolean explicit = false;
         if (ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
            token = ParserImpl.this.scanner.getToken();
            endMark = token.getEndMark();
            explicit = true;
         }

         Event event = new DocumentEndEvent(startMark, endMark, explicit);
         ParserImpl.this.state = ParserImpl.this.new ParseDocumentStart();
         return event;
      }

      // $FF: synthetic method
      ParseDocumentEnd(Object x1) {
         this();
      }
   }

   private class ParseDocumentStart implements Production {
      private ParseDocumentStart() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else {
            while(ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
               ParserImpl.this.scanner.getToken();
            }

            if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
               return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
            } else {
               if (!ParserImpl.this.scanner.checkToken(Token.ID.StreamEnd)) {
                  Token tokenx = ParserImpl.this.scanner.peekToken();
                  Mark startMark = tokenx.getStartMark();
                  VersionTagsTuple tuple = ParserImpl.this.processDirectives();

                  while(ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
                     ParserImpl.this.scanner.getToken();
                  }

                  if (!ParserImpl.this.scanner.checkToken(Token.ID.StreamEnd)) {
                     if (!ParserImpl.this.scanner.checkToken(Token.ID.DocumentStart)) {
                        throw new ParserException((String)null, (Mark)null, "expected '<document start>', but found '" + ParserImpl.this.scanner.peekToken().getTokenId() + "'", ParserImpl.this.scanner.peekToken().getStartMark());
                     }

                     tokenx = ParserImpl.this.scanner.getToken();
                     Mark endMark = tokenx.getEndMark();
                     Event event = new DocumentStartEvent(startMark, endMark, true, tuple.getVersion(), tuple.getTags());
                     ParserImpl.this.states.push(ParserImpl.this.new ParseDocumentEnd());
                     ParserImpl.this.state = ParserImpl.this.new ParseDocumentContent();
                     return event;
                  }
               }

               StreamEndToken token = (StreamEndToken)ParserImpl.this.scanner.getToken();
               Event eventx = new StreamEndEvent(token.getStartMark(), token.getEndMark());
               if (!ParserImpl.this.states.isEmpty()) {
                  throw new YAMLException("Unexpected end of stream. States left: " + ParserImpl.this.states);
               } else if (!ParserImpl.this.marks.isEmpty()) {
                  throw new YAMLException("Unexpected end of stream. Marks left: " + ParserImpl.this.marks);
               } else {
                  ParserImpl.this.state = null;
                  return eventx;
               }
            }
         }
      }

      // $FF: synthetic method
      ParseDocumentStart(Object x1) {
         this();
      }
   }

   private class ParseImplicitDocumentStart implements Production {
      private ParseImplicitDocumentStart() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.StreamEnd)) {
            ParserImpl.this.directives = new VersionTagsTuple((DumperOptions.Version)null, ParserImpl.DEFAULT_TAGS);
            Token token = ParserImpl.this.scanner.peekToken();
            Mark startMark = token.getStartMark();
            Event event = new DocumentStartEvent(startMark, startMark, false, (DumperOptions.Version)null, (Map)null);
            ParserImpl.this.states.push(ParserImpl.this.new ParseDocumentEnd());
            ParserImpl.this.state = ParserImpl.this.new ParseBlockNode();
            return event;
         } else {
            Production p = ParserImpl.this.new ParseDocumentStart();
            return p.produce();
         }
      }

      // $FF: synthetic method
      ParseImplicitDocumentStart(Object x1) {
         this();
      }
   }

   private class ParseStreamStart implements Production {
      private ParseStreamStart() {
      }

      public Event produce() {
         StreamStartToken token = (StreamStartToken)ParserImpl.this.scanner.getToken();
         Event event = new StreamStartEvent(token.getStartMark(), token.getEndMark());
         ParserImpl.this.state = ParserImpl.this.new ParseImplicitDocumentStart();
         return event;
      }

      // $FF: synthetic method
      ParseStreamStart(Object x1) {
         this();
      }
   }
}
