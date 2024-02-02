package javax.mail.internet;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.LineOutputStream;
import com.sun.mail.util.PropUtil;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessageAware;
import javax.mail.MessageContext;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.MultipartDataSource;

public class MimeMultipart extends Multipart {
   protected DataSource ds;
   protected boolean parsed;
   private boolean complete;
   private String preamble;
   private boolean ignoreMissingEndBoundary;
   private boolean ignoreMissingBoundaryParameter;
   private boolean ignoreExistingBoundaryParameter;
   private boolean allowEmpty;
   private boolean bmparse;

   public MimeMultipart() {
      this("mixed");
   }

   public MimeMultipart(String subtype) {
      this.ds = null;
      this.parsed = true;
      this.complete = true;
      this.preamble = null;
      this.ignoreMissingEndBoundary = true;
      this.ignoreMissingBoundaryParameter = true;
      this.ignoreExistingBoundaryParameter = false;
      this.allowEmpty = false;
      this.bmparse = true;
      String boundary = UniqueValue.getUniqueBoundaryValue();
      ContentType cType = new ContentType("multipart", subtype, (ParameterList)null);
      cType.setParameter("boundary", boundary);
      this.contentType = cType.toString();
   }

   public MimeMultipart(DataSource ds) throws MessagingException {
      this.ds = null;
      this.parsed = true;
      this.complete = true;
      this.preamble = null;
      this.ignoreMissingEndBoundary = true;
      this.ignoreMissingBoundaryParameter = true;
      this.ignoreExistingBoundaryParameter = false;
      this.allowEmpty = false;
      this.bmparse = true;
      if (ds instanceof MessageAware) {
         MessageContext mc = ((MessageAware)ds).getMessageContext();
         this.setParent(mc.getPart());
      }

      if (ds instanceof MultipartDataSource) {
         this.setMultipartDataSource((MultipartDataSource)ds);
      } else {
         this.parsed = false;
         this.ds = ds;
         this.contentType = ds.getContentType();
      }
   }

   public synchronized void setSubType(String subtype) throws MessagingException {
      ContentType cType = new ContentType(this.contentType);
      cType.setSubType(subtype);
      this.contentType = cType.toString();
   }

   public synchronized int getCount() throws MessagingException {
      this.parse();
      return super.getCount();
   }

   public synchronized BodyPart getBodyPart(int index) throws MessagingException {
      this.parse();
      return super.getBodyPart(index);
   }

   public synchronized BodyPart getBodyPart(String CID) throws MessagingException {
      this.parse();
      int count = this.getCount();

      for(int i = 0; i < count; ++i) {
         MimeBodyPart part = (MimeBodyPart)this.getBodyPart(i);
         String s = part.getContentID();
         if (s != null && s.equals(CID)) {
            return part;
         }
      }

      return null;
   }

   public boolean removeBodyPart(BodyPart part) throws MessagingException {
      this.parse();
      return super.removeBodyPart(part);
   }

   public void removeBodyPart(int index) throws MessagingException {
      this.parse();
      super.removeBodyPart(index);
   }

   public synchronized void addBodyPart(BodyPart part) throws MessagingException {
      this.parse();
      super.addBodyPart(part);
   }

   public synchronized void addBodyPart(BodyPart part, int index) throws MessagingException {
      this.parse();
      super.addBodyPart(part, index);
   }

   public synchronized boolean isComplete() throws MessagingException {
      this.parse();
      return this.complete;
   }

   public synchronized String getPreamble() throws MessagingException {
      this.parse();
      return this.preamble;
   }

   public synchronized void setPreamble(String preamble) throws MessagingException {
      this.preamble = preamble;
   }

   protected synchronized void updateHeaders() throws MessagingException {
      this.parse();

      for(int i = 0; i < this.parts.size(); ++i) {
         ((MimeBodyPart)this.parts.elementAt(i)).updateHeaders();
      }

   }

   public synchronized void writeTo(OutputStream os) throws IOException, MessagingException {
      this.parse();
      String boundary = "--" + (new ContentType(this.contentType)).getParameter("boundary");
      LineOutputStream los = new LineOutputStream(os);
      if (this.preamble != null) {
         byte[] pb = ASCIIUtility.getBytes(this.preamble);
         los.write(pb);
         if (pb.length > 0 && pb[pb.length - 1] != 13 && pb[pb.length - 1] != 10) {
            los.writeln();
         }
      }

      if (this.parts.size() == 0) {
         this.allowEmpty = PropUtil.getBooleanSystemProperty("mail.mime.multipart.allowempty", false);
         if (!this.allowEmpty) {
            throw new MessagingException("Empty multipart: " + this.contentType);
         }

         los.writeln(boundary);
         los.writeln();
      } else {
         for(int i = 0; i < this.parts.size(); ++i) {
            los.writeln(boundary);
            ((MimeBodyPart)this.parts.elementAt(i)).writeTo(os);
            los.writeln();
         }
      }

      los.writeln(boundary + "--");
   }

   protected synchronized void parse() throws MessagingException {
      if (!this.parsed) {
         this.ignoreMissingEndBoundary = PropUtil.getBooleanSystemProperty("mail.mime.multipart.ignoremissingendboundary", true);
         this.ignoreMissingBoundaryParameter = PropUtil.getBooleanSystemProperty("mail.mime.multipart.ignoremissingboundaryparameter", true);
         this.ignoreExistingBoundaryParameter = PropUtil.getBooleanSystemProperty("mail.mime.multipart.ignoreexistingboundaryparameter", false);
         this.allowEmpty = PropUtil.getBooleanSystemProperty("mail.mime.multipart.allowempty", false);
         this.bmparse = PropUtil.getBooleanSystemProperty("mail.mime.multipart.bmparse", true);
         if (this.bmparse) {
            this.parsebm();
         } else {
            InputStream in = null;
            SharedInputStream sin = null;
            long start = 0L;
            long end = 0L;

            try {
               in = this.ds.getInputStream();
               if (!(in instanceof ByteArrayInputStream) && !(in instanceof BufferedInputStream) && !(in instanceof SharedInputStream)) {
                  in = new BufferedInputStream((InputStream)in);
               }
            } catch (Exception var36) {
               throw new MessagingException("No inputstream from datasource", var36);
            }

            if (in instanceof SharedInputStream) {
               sin = (SharedInputStream)in;
            }

            ContentType cType = new ContentType(this.contentType);
            String boundary = null;
            if (!this.ignoreExistingBoundaryParameter) {
               String bp = cType.getParameter("boundary");
               if (bp != null) {
                  boundary = "--" + bp;
               }
            }

            if (boundary == null && !this.ignoreMissingBoundaryParameter && !this.ignoreExistingBoundaryParameter) {
               throw new MessagingException("Missing boundary parameter");
            } else {
               try {
                  LineInputStream lin = new LineInputStream((InputStream)in);
                  StringBuffer preamblesb = null;
                  String lineSeparator = null;

                  String line;
                  int bl;
                  while((line = lin.readLine()) != null) {
                     int i;
                     for(i = line.length() - 1; i >= 0; --i) {
                        bl = line.charAt(i);
                        if (bl != 32 && bl != 9) {
                           break;
                        }
                     }

                     line = line.substring(0, i + 1);
                     if (boundary != null) {
                        if (line.equals(boundary)) {
                           break;
                        }

                        if (line.length() == boundary.length() + 2 && line.startsWith(boundary) && line.endsWith("--")) {
                           line = null;
                           break;
                        }
                     } else if (line.length() > 2 && line.startsWith("--") && (line.length() <= 4 || !allDashes(line))) {
                        boundary = line;
                        break;
                     }

                     if (line.length() > 0) {
                        if (lineSeparator == null) {
                           try {
                              lineSeparator = System.getProperty("line.separator", "\n");
                           } catch (SecurityException var35) {
                              lineSeparator = "\n";
                           }
                        }

                        if (preamblesb == null) {
                           preamblesb = new StringBuffer(line.length() + 2);
                        }

                        preamblesb.append(line).append(lineSeparator);
                     }
                  }

                  if (preamblesb != null) {
                     this.preamble = preamblesb.toString();
                  }

                  if (line == null) {
                     if (this.allowEmpty) {
                        return;
                     }

                     throw new MessagingException("Missing start boundary");
                  }

                  byte[] bndbytes = ASCIIUtility.getBytes(boundary);
                  bl = bndbytes.length;

                  MimeBodyPart part;
                  for(boolean done = false; !done; super.addBodyPart(part)) {
                     InternetHeaders headers = null;
                     if (sin != null) {
                        start = sin.getPosition();

                        while((line = lin.readLine()) != null && line.length() > 0) {
                        }

                        if (line == null) {
                           if (!this.ignoreMissingEndBoundary) {
                              throw new MessagingException("missing multipart end boundary");
                           }

                           this.complete = false;
                           break;
                        }
                     } else {
                        headers = this.createInternetHeaders((InputStream)in);
                     }

                     if (!((InputStream)in).markSupported()) {
                        throw new MessagingException("Stream doesn't support mark");
                     }

                     ByteArrayOutputStream buf = null;
                     if (sin == null) {
                        buf = new ByteArrayOutputStream();
                     } else {
                        end = sin.getPosition();
                     }

                     boolean bol = true;
                     int eol1 = -1;
                     int eol2 = -1;

                     label539:
                     while(true) {
                        if (bol) {
                           ((InputStream)in).mark(bl + 4 + 1000);

                           int i;
                           for(i = 0; i < bl && ((InputStream)in).read() == (bndbytes[i] & 255); ++i) {
                           }

                           if (i == bl) {
                              int b2 = ((InputStream)in).read();
                              if (b2 == 45 && ((InputStream)in).read() == 45) {
                                 this.complete = true;
                                 done = true;
                                 break;
                              }

                              while(true) {
                                 if (b2 != 32 && b2 != 9) {
                                    if (b2 == 10) {
                                       break label539;
                                    }

                                    if (b2 == 13) {
                                       ((InputStream)in).mark(1);
                                       if (((InputStream)in).read() != 10) {
                                          ((InputStream)in).reset();
                                       }
                                       break label539;
                                    }
                                    break;
                                 }

                                 b2 = ((InputStream)in).read();
                              }
                           }

                           ((InputStream)in).reset();
                           if (buf != null && eol1 != -1) {
                              buf.write(eol1);
                              if (eol2 != -1) {
                                 buf.write(eol2);
                              }

                              eol2 = -1;
                              eol1 = -1;
                           }
                        }

                        int b;
                        if ((b = ((InputStream)in).read()) < 0) {
                           if (!this.ignoreMissingEndBoundary) {
                              throw new MessagingException("missing multipart end boundary");
                           }

                           this.complete = false;
                           done = true;
                           break;
                        }

                        if (b != 13 && b != 10) {
                           bol = false;
                           if (buf != null) {
                              buf.write(b);
                           }
                        } else {
                           bol = true;
                           if (sin != null) {
                              end = sin.getPosition() - 1L;
                           }

                           eol1 = b;
                           if (b == 13) {
                              ((InputStream)in).mark(1);
                              if ((b = ((InputStream)in).read()) == 10) {
                                 eol2 = b;
                              } else {
                                 ((InputStream)in).reset();
                              }
                           }
                        }
                     }

                     if (sin != null) {
                        part = this.createMimeBodyPartIs(sin.newStream(start, end));
                     } else {
                        part = this.createMimeBodyPart(headers, buf.toByteArray());
                     }
                  }
               } catch (IOException var37) {
                  throw new MessagingException("IO Error", var37);
               } finally {
                  try {
                     ((InputStream)in).close();
                  } catch (IOException var34) {
                  }

               }

               this.parsed = true;
            }
         }
      }
   }

   private synchronized void parsebm() throws MessagingException {
      if (!this.parsed) {
         InputStream in = null;
         SharedInputStream sin = null;
         long start = 0L;
         long end = 0L;

         try {
            in = this.ds.getInputStream();
            if (!(in instanceof ByteArrayInputStream) && !(in instanceof BufferedInputStream) && !(in instanceof SharedInputStream)) {
               in = new BufferedInputStream((InputStream)in);
            }
         } catch (Exception var42) {
            throw new MessagingException("No inputstream from datasource", var42);
         }

         if (in instanceof SharedInputStream) {
            sin = (SharedInputStream)in;
         }

         ContentType cType = new ContentType(this.contentType);
         String boundary = null;
         if (!this.ignoreExistingBoundaryParameter) {
            String bp = cType.getParameter("boundary");
            if (bp != null) {
               boundary = "--" + bp;
            }
         }

         if (boundary == null && !this.ignoreMissingBoundaryParameter && !this.ignoreExistingBoundaryParameter) {
            throw new MessagingException("Missing boundary parameter");
         } else {
            try {
               LineInputStream lin = new LineInputStream((InputStream)in);
               StringBuffer preamblesb = null;
               String lineSeparator = null;

               String line;
               int bl;
               while((line = lin.readLine()) != null) {
                  int i;
                  for(i = line.length() - 1; i >= 0; --i) {
                     bl = line.charAt(i);
                     if (bl != 32 && bl != 9) {
                        break;
                     }
                  }

                  line = line.substring(0, i + 1);
                  if (boundary != null) {
                     if (line.equals(boundary)) {
                        break;
                     }

                     if (line.length() == boundary.length() + 2 && line.startsWith(boundary) && line.endsWith("--")) {
                        line = null;
                        break;
                     }
                  } else if (line.length() > 2 && line.startsWith("--") && (line.length() <= 4 || !allDashes(line))) {
                     boundary = line;
                     break;
                  }

                  if (line.length() > 0) {
                     if (lineSeparator == null) {
                        try {
                           lineSeparator = System.getProperty("line.separator", "\n");
                        } catch (SecurityException var41) {
                           lineSeparator = "\n";
                        }
                     }

                     if (preamblesb == null) {
                        preamblesb = new StringBuffer(line.length() + 2);
                     }

                     preamblesb.append(line).append(lineSeparator);
                  }
               }

               if (preamblesb != null) {
                  this.preamble = preamblesb.toString();
               }

               if (line == null) {
                  if (this.allowEmpty) {
                     return;
                  }

                  throw new MessagingException("Missing start boundary");
               }

               byte[] bndbytes = ASCIIUtility.getBytes(boundary);
               bl = bndbytes.length;
               int[] bcs = new int[256];
               int i = 0;

               label642:
               while(true) {
                  if (i >= bl) {
                     int[] gss = new int[bl];

                     label637:
                     for(int i = bl; i > 0; --i) {
                        int j;
                        for(j = bl - 1; j >= i; --j) {
                           if (bndbytes[j] != bndbytes[j - i]) {
                              continue label637;
                           }

                           gss[j - 1] = i;
                        }

                        while(j > 0) {
                           --j;
                           gss[j] = i;
                        }
                     }

                     gss[bl - 1] = 1;
                     boolean done = false;

                     while(true) {
                        if (done) {
                           break label642;
                        }

                        InternetHeaders headers = null;
                        if (sin != null) {
                           start = sin.getPosition();

                           while((line = lin.readLine()) != null && line.length() > 0) {
                           }

                           if (line == null) {
                              if (!this.ignoreMissingEndBoundary) {
                                 throw new MessagingException("missing multipart end boundary");
                              }

                              this.complete = false;
                              break label642;
                           }
                        } else {
                           headers = this.createInternetHeaders((InputStream)in);
                        }

                        if (!((InputStream)in).markSupported()) {
                           throw new MessagingException("Stream doesn't support mark");
                        }

                        ByteArrayOutputStream buf = null;
                        if (sin == null) {
                           buf = new ByteArrayOutputStream();
                        } else {
                           end = sin.getPosition();
                        }

                        byte[] inbuf = new byte[bl];
                        byte[] previnbuf = new byte[bl];
                        int inSize = false;
                        int prevSize = 0;
                        boolean first = true;

                        byte eolLen;
                        int inSize;
                        label608:
                        while(true) {
                           ((InputStream)in).mark(bl + 4 + 1000);
                           eolLen = 0;
                           inSize = readFully((InputStream)in, inbuf, 0, bl);
                           if (inSize < bl) {
                              if (!this.ignoreMissingEndBoundary) {
                                 throw new MessagingException("missing multipart end boundary");
                              }

                              if (sin != null) {
                                 end = sin.getPosition();
                              }

                              this.complete = false;
                              done = true;
                              break;
                           }

                           int i;
                           for(i = bl - 1; i >= 0 && inbuf[i] == bndbytes[i]; --i) {
                           }

                           int b2;
                           if (i < 0) {
                              eolLen = 0;
                              if (!first) {
                                 int b = previnbuf[prevSize - 1];
                                 if (b == 13 || b == 10) {
                                    eolLen = 1;
                                    if (b == 10 && prevSize >= 2) {
                                       b = previnbuf[prevSize - 2];
                                       if (b == 13) {
                                          eolLen = 2;
                                       }
                                    }
                                 }
                              }

                              if (first || eolLen > 0) {
                                 if (sin != null) {
                                    end = sin.getPosition() - (long)bl - (long)eolLen;
                                 }

                                 b2 = ((InputStream)in).read();
                                 if (b2 == 45 && ((InputStream)in).read() == 45) {
                                    this.complete = true;
                                    done = true;
                                    break;
                                 }

                                 while(true) {
                                    if (b2 != 32 && b2 != 9) {
                                       if (b2 == 10) {
                                          break label608;
                                       }

                                       if (b2 == 13) {
                                          ((InputStream)in).mark(1);
                                          if (((InputStream)in).read() != 10) {
                                             ((InputStream)in).reset();
                                          }
                                          break label608;
                                       }
                                       break;
                                    }

                                    b2 = ((InputStream)in).read();
                                 }
                              }

                              i = 0;
                           }

                           b2 = Math.max(i + 1 - bcs[inbuf[i] & 127], gss[i]);
                           if (b2 < 2) {
                              if (sin == null && prevSize > 1) {
                                 buf.write(previnbuf, 0, prevSize - 1);
                              }

                              ((InputStream)in).reset();
                              this.skipFully((InputStream)in, 1L);
                              if (prevSize >= 1) {
                                 previnbuf[0] = previnbuf[prevSize - 1];
                                 previnbuf[1] = inbuf[0];
                                 prevSize = 2;
                              } else {
                                 previnbuf[0] = inbuf[0];
                                 prevSize = 1;
                              }
                           } else {
                              if (prevSize > 0 && sin == null) {
                                 buf.write(previnbuf, 0, prevSize);
                              }

                              prevSize = b2;
                              ((InputStream)in).reset();
                              this.skipFully((InputStream)in, (long)b2);
                              byte[] tmp = inbuf;
                              inbuf = previnbuf;
                              previnbuf = tmp;
                           }

                           first = false;
                        }

                        MimeBodyPart part;
                        if (sin != null) {
                           part = this.createMimeBodyPartIs(sin.newStream(start, end));
                        } else {
                           if (prevSize - eolLen > 0) {
                              buf.write(previnbuf, 0, prevSize - eolLen);
                           }

                           if (!this.complete && inSize > 0) {
                              buf.write(inbuf, 0, inSize);
                           }

                           part = this.createMimeBodyPart(headers, buf.toByteArray());
                        }

                        super.addBodyPart(part);
                     }
                  }

                  bcs[bndbytes[i] & 255] = i + 1;
                  ++i;
               }
            } catch (IOException var43) {
               throw new MessagingException("IO Error", var43);
            } finally {
               try {
                  ((InputStream)in).close();
               } catch (IOException var40) {
               }

            }

            this.parsed = true;
         }
      }
   }

   private static boolean allDashes(String s) {
      for(int i = 0; i < s.length(); ++i) {
         if (s.charAt(i) != '-') {
            return false;
         }
      }

      return true;
   }

   private static int readFully(InputStream in, byte[] buf, int off, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else {
         int total;
         int bsize;
         for(total = 0; len > 0; len -= bsize) {
            bsize = in.read(buf, off, len);
            if (bsize <= 0) {
               break;
            }

            off += bsize;
            total += bsize;
         }

         return total > 0 ? total : -1;
      }
   }

   private void skipFully(InputStream in, long offset) throws IOException {
      while(offset > 0L) {
         long cur = in.skip(offset);
         if (cur <= 0L) {
            throw new EOFException("can't skip");
         }

         offset -= cur;
      }

   }

   protected InternetHeaders createInternetHeaders(InputStream is) throws MessagingException {
      return new InternetHeaders(is);
   }

   protected MimeBodyPart createMimeBodyPart(InternetHeaders headers, byte[] content) throws MessagingException {
      return new MimeBodyPart(headers, content);
   }

   protected MimeBodyPart createMimeBodyPart(InputStream is) throws MessagingException {
      return new MimeBodyPart(is);
   }

   private MimeBodyPart createMimeBodyPartIs(InputStream is) throws MessagingException {
      MimeBodyPart var2;
      try {
         var2 = this.createMimeBodyPart(is);
      } finally {
         try {
            is.close();
         } catch (IOException var9) {
         }

      }

      return var2;
   }
}
