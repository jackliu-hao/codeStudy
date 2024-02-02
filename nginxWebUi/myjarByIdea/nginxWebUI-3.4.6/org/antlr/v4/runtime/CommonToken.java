package org.antlr.v4.runtime;

import java.io.Serializable;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;

public class CommonToken implements WritableToken, Serializable {
   protected static final Pair<TokenSource, CharStream> EMPTY_SOURCE = new Pair((Object)null, (Object)null);
   protected int type;
   protected int line;
   protected int charPositionInLine = -1;
   protected int channel = 0;
   protected Pair<TokenSource, CharStream> source;
   protected String text;
   protected int index = -1;
   protected int start;
   protected int stop;

   public CommonToken(int type) {
      this.type = type;
      this.source = EMPTY_SOURCE;
   }

   public CommonToken(Pair<TokenSource, CharStream> source, int type, int channel, int start, int stop) {
      this.source = source;
      this.type = type;
      this.channel = channel;
      this.start = start;
      this.stop = stop;
      if (source.a != null) {
         this.line = ((TokenSource)source.a).getLine();
         this.charPositionInLine = ((TokenSource)source.a).getCharPositionInLine();
      }

   }

   public CommonToken(int type, String text) {
      this.type = type;
      this.channel = 0;
      this.text = text;
      this.source = EMPTY_SOURCE;
   }

   public CommonToken(Token oldToken) {
      this.type = oldToken.getType();
      this.line = oldToken.getLine();
      this.index = oldToken.getTokenIndex();
      this.charPositionInLine = oldToken.getCharPositionInLine();
      this.channel = oldToken.getChannel();
      this.start = oldToken.getStartIndex();
      this.stop = oldToken.getStopIndex();
      if (oldToken instanceof CommonToken) {
         this.text = ((CommonToken)oldToken).text;
         this.source = ((CommonToken)oldToken).source;
      } else {
         this.text = oldToken.getText();
         this.source = new Pair(oldToken.getTokenSource(), oldToken.getInputStream());
      }

   }

   public int getType() {
      return this.type;
   }

   public void setLine(int line) {
      this.line = line;
   }

   public String getText() {
      if (this.text != null) {
         return this.text;
      } else {
         CharStream input = this.getInputStream();
         if (input == null) {
            return null;
         } else {
            int n = input.size();
            return this.start < n && this.stop < n ? input.getText(Interval.of(this.start, this.stop)) : "<EOF>";
         }
      }
   }

   public void setText(String text) {
      this.text = text;
   }

   public int getLine() {
      return this.line;
   }

   public int getCharPositionInLine() {
      return this.charPositionInLine;
   }

   public void setCharPositionInLine(int charPositionInLine) {
      this.charPositionInLine = charPositionInLine;
   }

   public int getChannel() {
      return this.channel;
   }

   public void setChannel(int channel) {
      this.channel = channel;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getStartIndex() {
      return this.start;
   }

   public void setStartIndex(int start) {
      this.start = start;
   }

   public int getStopIndex() {
      return this.stop;
   }

   public void setStopIndex(int stop) {
      this.stop = stop;
   }

   public int getTokenIndex() {
      return this.index;
   }

   public void setTokenIndex(int index) {
      this.index = index;
   }

   public TokenSource getTokenSource() {
      return (TokenSource)this.source.a;
   }

   public CharStream getInputStream() {
      return (CharStream)this.source.b;
   }

   public String toString() {
      String channelStr = "";
      if (this.channel > 0) {
         channelStr = ",channel=" + this.channel;
      }

      String txt = this.getText();
      if (txt != null) {
         txt = txt.replace("\n", "\\n");
         txt = txt.replace("\r", "\\r");
         txt = txt.replace("\t", "\\t");
      } else {
         txt = "<no text>";
      }

      return "[@" + this.getTokenIndex() + "," + this.start + ":" + this.stop + "='" + txt + "',<" + this.type + ">" + channelStr + "," + this.line + ":" + this.getCharPositionInLine() + "]";
   }
}
