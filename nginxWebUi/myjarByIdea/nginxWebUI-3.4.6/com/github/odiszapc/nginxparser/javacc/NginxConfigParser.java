package com.github.odiszapc.nginxparser.javacc;

import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxComment;
import com.github.odiszapc.nginxparser.NgxConfig;
import com.github.odiszapc.nginxparser.NgxEntry;
import com.github.odiszapc.nginxparser.NgxIfBlock;
import com.github.odiszapc.nginxparser.NgxParam;
import com.github.odiszapc.nginxparser.NgxToken;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NginxConfigParser implements NginxConfigParserConstants {
   private boolean isDebug;
   public NginxConfigParserTokenManager token_source;
   SimpleCharStream jj_input_stream;
   public Token token;
   public Token jj_nt;
   private int jj_ntk;
   private Token jj_scanpos;
   private Token jj_lastpos;
   private int jj_la;
   private int jj_gen;
   private final int[] jj_la1;
   private static int[] jj_la1_0;
   private final JJCalls[] jj_2_rtns;
   private boolean jj_rescan;
   private int jj_gc;
   private final LookaheadSuccess jj_ls;
   private List<int[]> jj_expentries;
   private int[] jj_expentry;
   private int jj_kind;
   private int[] jj_lasttokens;
   private int jj_endpos;

   public void setDebug(boolean val) {
      this.isDebug = val;
   }

   private void debug(String message) {
      this.debug(message, false);
   }

   private void debugLn(String message) {
      this.debug(message, true);
   }

   private void debug(String message, boolean appendLineEnding) {
      if (this.isDebug) {
         System.out.print(message);
         if (appendLineEnding) {
            System.out.println();
         }
      }

   }

   public final NgxConfig parse() throws ParseException {
      NgxConfig config = this.statements();
      if ("" != null) {
         return config;
      } else {
         throw new Error("Missing return statement in function");
      }
   }

   public final NgxConfig statements() throws ParseException {
      NgxConfig config = new NgxConfig();

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 13:
            case 14:
            case 15:
            case 16:
               Object curEntry;
               if (this.jj_2_1(4)) {
                  curEntry = this.param();
               } else {
                  switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                     case 13:
                     case 14:
                     case 15:
                        curEntry = this.block();
                        break;
                     case 16:
                        curEntry = this.comment();
                        break;
                     default:
                        this.jj_la1[1] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                  }
               }

               config.addEntry((NgxEntry)curEntry);
               break;
            default:
               this.jj_la1[0] = this.jj_gen;
               if ("" != null) {
                  return config;
               }

               throw new Error("Missing return statement in function");
         }
      }
   }

   public final NgxBlock block() throws ParseException {
      String blockName = "";
      NgxBlock block = new NgxBlock();
      NgxToken curToken = this.id();
      blockName = blockName + this.token.image + " ";
      block.getTokens().add(curToken);

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 13:
            case 14:
            case 15:
               curToken = this.id();
               blockName = blockName + this.token.image + " ";
               block.getTokens().add(curToken);
               break;
            default:
               this.jj_la1[2] = this.jj_gen;
               this.debugLn("BLOCK=" + blockName + "{");
               this.jj_consume_token(7);

               while(true) {
                  switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                     case 10:
                     case 13:
                     case 14:
                     case 15:
                     case 16:
                        Object curEntry;
                        if (this.jj_2_2(4)) {
                           curEntry = this.param();
                        } else {
                           switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                              case 10:
                                 curEntry = this.if_block();
                                 break;
                              case 11:
                              case 12:
                              default:
                                 this.jj_la1[4] = this.jj_gen;
                                 this.jj_consume_token(-1);
                                 throw new ParseException();
                              case 13:
                              case 14:
                              case 15:
                                 curEntry = this.block();
                                 break;
                              case 16:
                                 curEntry = this.comment();
                           }
                        }

                        block.addEntry((NgxEntry)curEntry);
                        break;
                     case 11:
                     case 12:
                     default:
                        this.jj_la1[3] = this.jj_gen;
                        this.jj_consume_token(8);
                        this.debugLn("}");
                        if ("" != null) {
                           return block;
                        }

                        throw new Error("Missing return statement in function");
                  }
               }
         }
      }
   }

   public final NgxBlock if_block() throws ParseException {
      NgxIfBlock block = new NgxIfBlock();
      NgxEntry curEntry = null;
      this.jj_consume_token(10);
      this.debug(this.token.image + " ");
      block.addValue(new NgxToken(this.token.image));
      this.jj_consume_token(11);
      this.debug(this.token.image + " ");
      block.addValue(new NgxToken(this.token.image));
      this.jj_consume_token(7);
      this.debugLn(this.token.image);

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 13:
            case 14:
            case 15:
            case 16:
               if (this.jj_2_3(4)) {
                  curEntry = this.param();
               } else {
                  switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                     case 13:
                     case 14:
                     case 15:
                        curEntry = this.block();
                        break;
                     case 16:
                        curEntry = this.comment();
                        break;
                     default:
                        this.jj_la1[6] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                  }
               }

               block.addEntry((NgxEntry)curEntry);
               break;
            default:
               this.jj_la1[5] = this.jj_gen;
               this.jj_consume_token(8);
               this.debugLn(this.token.image);
               if ("" != null) {
                  return block;
               }

               throw new Error("Missing return statement in function");
         }
      }
   }

   public final NgxParam param() throws ParseException {
      NgxParam param = new NgxParam();
      NgxToken curToken = null;
      curToken = this.id();
      this.debug("KEY=" + this.token.image + ", VALUE=");
      param.getTokens().add(curToken);

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 12:
            case 13:
            case 14:
            case 15:
               curToken = this.value();
               this.debug(this.token.image + " | ");
               param.getTokens().add(curToken);
               break;
            default:
               this.jj_la1[7] = this.jj_gen;
               this.debugLn("");
               this.jj_consume_token(9);
               if ("" != null) {
                  return param;
               }

               throw new Error("Missing return statement in function");
         }
      }
   }

   public final NgxComment comment() throws ParseException {
      this.jj_consume_token(16);
      this.debugLn("COMMENT=" + this.token.image);
      if ("" != null) {
         return new NgxComment(this.token.image);
      } else {
         throw new Error("Missing return statement in function");
      }
   }

   public final NgxToken id() throws ParseException {
      NgxToken val = null;
      switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
         case 13:
            this.jj_consume_token(13);
            break;
         case 14:
            this.jj_consume_token(14);
            break;
         case 15:
            this.jj_consume_token(15);
            break;
         default:
            this.jj_la1[8] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

      if ("" != null) {
         return new NgxToken(this.token.image);
      } else {
         throw new Error("Missing return statement in function");
      }
   }

   public final NgxToken value() throws ParseException {
      NgxToken val = null;
      switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
         case 12:
            this.jj_consume_token(12);
            break;
         case 13:
            this.jj_consume_token(13);
            break;
         case 14:
            this.jj_consume_token(14);
            break;
         case 15:
            this.jj_consume_token(15);
            break;
         default:
            this.jj_la1[9] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

      if ("" != null) {
         return new NgxToken(this.token.image);
      } else {
         throw new Error("Missing return statement in function");
      }
   }

   private boolean jj_2_1(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_1();
         return var2;
      } catch (LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(0, xla);
      }

      return var3;
   }

   private boolean jj_2_2(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_2();
         return var2;
      } catch (LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(1, xla);
      }

      return var3;
   }

   private boolean jj_2_3(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_3();
         return var2;
      } catch (LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(2, xla);
      }

      return var3;
   }

   private boolean jj_3R_8() {
      return this.jj_3R_9();
   }

   private boolean jj_3R_7() {
      Token xsp = this.jj_scanpos;
      if (this.jj_scan_token(13)) {
         this.jj_scanpos = xsp;
         if (this.jj_scan_token(14)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(15)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean jj_3_2() {
      return this.jj_3R_6();
   }

   private boolean jj_3R_9() {
      Token xsp = this.jj_scanpos;
      if (this.jj_scan_token(12)) {
         this.jj_scanpos = xsp;
         if (this.jj_scan_token(13)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(14)) {
               this.jj_scanpos = xsp;
               if (this.jj_scan_token(15)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean jj_3_3() {
      return this.jj_3R_6();
   }

   private boolean jj_3_1() {
      return this.jj_3R_6();
   }

   private boolean jj_3R_6() {
      if (this.jj_3R_7()) {
         return true;
      } else {
         Token xsp;
         do {
            xsp = this.jj_scanpos;
         } while(!this.jj_3R_8());

         this.jj_scanpos = xsp;
         return this.jj_scan_token(9);
      }
   }

   private static void jj_la1_init_0() {
      jj_la1_0 = new int[]{122880, 122880, 57344, 123904, 123904, 122880, 122880, 61440, 57344, 61440};
   }

   public NginxConfigParser(InputStream stream) {
      this(stream, (String)null);
   }

   public NginxConfigParser(InputStream stream, String encoding) {
      this.isDebug = false;
      this.jj_la1 = new int[10];
      this.jj_2_rtns = new JJCalls[3];
      this.jj_rescan = false;
      this.jj_gc = 0;
      this.jj_ls = new LookaheadSuccess();
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.jj_lasttokens = new int[100];

      try {
         this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }

      this.token_source = new NginxConfigParserTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 10; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   public void ReInit(InputStream stream) {
      this.ReInit(stream, (String)null);
   }

   public void ReInit(InputStream stream, String encoding) {
      try {
         this.jj_input_stream.ReInit(stream, encoding, 1, 1);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }

      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 10; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   public NginxConfigParser(Reader stream) {
      this.isDebug = false;
      this.jj_la1 = new int[10];
      this.jj_2_rtns = new JJCalls[3];
      this.jj_rescan = false;
      this.jj_gc = 0;
      this.jj_ls = new LookaheadSuccess();
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.jj_lasttokens = new int[100];
      this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
      this.token_source = new NginxConfigParserTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 10; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   public void ReInit(Reader stream) {
      this.jj_input_stream.ReInit((Reader)stream, 1, 1);
      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 10; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   public NginxConfigParser(NginxConfigParserTokenManager tm) {
      this.isDebug = false;
      this.jj_la1 = new int[10];
      this.jj_2_rtns = new JJCalls[3];
      this.jj_rescan = false;
      this.jj_gc = 0;
      this.jj_ls = new LookaheadSuccess();
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.jj_lasttokens = new int[100];
      this.token_source = tm;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 10; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   public void ReInit(NginxConfigParserTokenManager tm) {
      this.token_source = tm;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 10; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   private Token jj_consume_token(int kind) throws ParseException {
      Token oldToken;
      if ((oldToken = this.token).next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      if (this.token.kind != kind) {
         this.token = oldToken;
         this.jj_kind = kind;
         throw this.generateParseException();
      } else {
         ++this.jj_gen;
         if (++this.jj_gc > 100) {
            this.jj_gc = 0;

            for(int i = 0; i < this.jj_2_rtns.length; ++i) {
               for(JJCalls c = this.jj_2_rtns[i]; c != null; c = c.next) {
                  if (c.gen < this.jj_gen) {
                     c.first = null;
                  }
               }
            }
         }

         return this.token;
      }
   }

   private boolean jj_scan_token(int kind) {
      if (this.jj_scanpos == this.jj_lastpos) {
         --this.jj_la;
         if (this.jj_scanpos.next == null) {
            this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
         } else {
            this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
         }
      } else {
         this.jj_scanpos = this.jj_scanpos.next;
      }

      if (this.jj_rescan) {
         int i = 0;

         Token tok;
         for(tok = this.token; tok != null && tok != this.jj_scanpos; tok = tok.next) {
            ++i;
         }

         if (tok != null) {
            this.jj_add_error_token(kind, i);
         }
      }

      if (this.jj_scanpos.kind != kind) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         throw this.jj_ls;
      } else {
         return false;
      }
   }

   public final Token getNextToken() {
      if (this.token.next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      ++this.jj_gen;
      return this.token;
   }

   public final Token getToken(int index) {
      Token t = this.token;

      for(int i = 0; i < index; ++i) {
         if (t.next != null) {
            t = t.next;
         } else {
            t = t.next = this.token_source.getNextToken();
         }
      }

      return t;
   }

   private int jj_ntk_f() {
      return (this.jj_nt = this.token.next) == null ? (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind) : (this.jj_ntk = this.jj_nt.kind);
   }

   private void jj_add_error_token(int kind, int pos) {
      if (pos < 100) {
         if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
         } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];

            for(int i = 0; i < this.jj_endpos; ++i) {
               this.jj_expentry[i] = this.jj_lasttokens[i];
            }

            Iterator<?> it = this.jj_expentries.iterator();

            label41:
            while(true) {
               int[] oldentry;
               do {
                  if (!it.hasNext()) {
                     break label41;
                  }

                  oldentry = (int[])((int[])it.next());
               } while(oldentry.length != this.jj_expentry.length);

               for(int i = 0; i < this.jj_expentry.length; ++i) {
                  if (oldentry[i] != this.jj_expentry[i]) {
                     continue label41;
                  }
               }

               this.jj_expentries.add(this.jj_expentry);
               break;
            }

            if (pos != 0) {
               this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
            }
         }

      }
   }

   public ParseException generateParseException() {
      this.jj_expentries.clear();
      boolean[] la1tokens = new boolean[19];
      if (this.jj_kind >= 0) {
         la1tokens[this.jj_kind] = true;
         this.jj_kind = -1;
      }

      int i;
      int j;
      for(i = 0; i < 10; ++i) {
         if (this.jj_la1[i] == this.jj_gen) {
            for(j = 0; j < 32; ++j) {
               if ((jj_la1_0[i] & 1 << j) != 0) {
                  la1tokens[j] = true;
               }
            }
         }
      }

      for(i = 0; i < 19; ++i) {
         if (la1tokens[i]) {
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
         }
      }

      this.jj_endpos = 0;
      this.jj_rescan_token();
      this.jj_add_error_token(0, 0);
      int[][] exptokseq = new int[this.jj_expentries.size()][];

      for(j = 0; j < this.jj_expentries.size(); ++j) {
         exptokseq[j] = (int[])this.jj_expentries.get(j);
      }

      return new ParseException(this.token, exptokseq, tokenImage);
   }

   public final void enable_tracing() {
   }

   public final void disable_tracing() {
   }

   private void jj_rescan_token() {
      this.jj_rescan = true;

      for(int i = 0; i < 3; ++i) {
         try {
            JJCalls p = this.jj_2_rtns[i];

            do {
               if (p.gen > this.jj_gen) {
                  this.jj_la = p.arg;
                  this.jj_lastpos = this.jj_scanpos = p.first;
                  switch (i) {
                     case 0:
                        this.jj_3_1();
                        break;
                     case 1:
                        this.jj_3_2();
                        break;
                     case 2:
                        this.jj_3_3();
                  }
               }

               p = p.next;
            } while(p != null);
         } catch (LookaheadSuccess var3) {
         }
      }

      this.jj_rescan = false;
   }

   private void jj_save(int index, int xla) {
      JJCalls p;
      for(p = this.jj_2_rtns[index]; p.gen > this.jj_gen; p = p.next) {
         if (p.next == null) {
            p = p.next = new JJCalls();
            break;
         }
      }

      p.gen = this.jj_gen + xla - this.jj_la;
      p.first = this.token;
      p.arg = xla;
   }

   static {
      jj_la1_init_0();
   }

   static final class JJCalls {
      int gen;
      Token first;
      int arg;
      JJCalls next;
   }

   private static final class LookaheadSuccess extends Error {
      private LookaheadSuccess() {
      }

      // $FF: synthetic method
      LookaheadSuccess(Object x0) {
         this();
      }
   }
}
