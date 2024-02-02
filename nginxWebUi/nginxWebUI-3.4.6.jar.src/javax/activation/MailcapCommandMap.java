/*     */ package javax.activation;
/*     */ 
/*     */ import com.sun.activation.registries.LogSupport;
/*     */ import com.sun.activation.registries.MailcapFile;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class MailcapCommandMap
/*     */   extends CommandMap
/*     */ {
/* 127 */   private static MailcapFile defDB = null;
/*     */ 
/*     */   
/*     */   private MailcapFile[] DB;
/*     */   
/*     */   private static final int PROG = 0;
/*     */ 
/*     */   
/*     */   public MailcapCommandMap() {
/* 136 */     List dbv = new ArrayList(5);
/* 137 */     MailcapFile mf = null;
/* 138 */     dbv.add(null);
/*     */     
/* 140 */     LogSupport.log("MailcapCommandMap: load HOME");
/*     */     try {
/* 142 */       String user_home = System.getProperty("user.home");
/*     */       
/* 144 */       if (user_home != null) {
/* 145 */         String path = user_home + File.separator + ".mailcap";
/* 146 */         mf = loadFile(path);
/* 147 */         if (mf != null)
/* 148 */           dbv.add(mf); 
/*     */       } 
/* 150 */     } catch (SecurityException ex) {}
/*     */     
/* 152 */     LogSupport.log("MailcapCommandMap: load SYS");
/*     */     
/*     */     try {
/* 155 */       String system_mailcap = System.getProperty("java.home") + File.separator + "lib" + File.separator + "mailcap";
/*     */       
/* 157 */       mf = loadFile(system_mailcap);
/* 158 */       if (mf != null)
/* 159 */         dbv.add(mf); 
/* 160 */     } catch (SecurityException ex) {}
/*     */     
/* 162 */     LogSupport.log("MailcapCommandMap: load JAR");
/*     */     
/* 164 */     loadAllResources(dbv, "META-INF/mailcap");
/*     */     
/* 166 */     LogSupport.log("MailcapCommandMap: load DEF");
/* 167 */     synchronized (MailcapCommandMap.class) {
/*     */       
/* 169 */       if (defDB == null) {
/* 170 */         defDB = loadResource("/META-INF/mailcap.default");
/*     */       }
/*     */     } 
/* 173 */     if (defDB != null) {
/* 174 */       dbv.add(defDB);
/*     */     }
/* 176 */     this.DB = new MailcapFile[dbv.size()];
/* 177 */     this.DB = dbv.<MailcapFile>toArray(this.DB);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MailcapFile loadResource(String name) {
/* 184 */     InputStream clis = null;
/*     */     try {
/* 186 */       clis = SecuritySupport.getResourceAsStream(getClass(), name);
/* 187 */       if (clis != null) {
/* 188 */         MailcapFile mf = new MailcapFile(clis);
/* 189 */         if (LogSupport.isLoggable()) {
/* 190 */           LogSupport.log("MailcapCommandMap: successfully loaded mailcap file: " + name);
/*     */         }
/* 192 */         return mf;
/*     */       } 
/* 194 */       if (LogSupport.isLoggable()) {
/* 195 */         LogSupport.log("MailcapCommandMap: not loading mailcap file: " + name);
/*     */       }
/*     */     }
/* 198 */     catch (IOException e) {
/* 199 */       if (LogSupport.isLoggable())
/* 200 */         LogSupport.log("MailcapCommandMap: can't load " + name, e); 
/* 201 */     } catch (SecurityException sex) {
/* 202 */       if (LogSupport.isLoggable())
/* 203 */         LogSupport.log("MailcapCommandMap: can't load " + name, sex); 
/*     */     } finally {
/*     */       try {
/* 206 */         if (clis != null)
/* 207 */           clis.close(); 
/* 208 */       } catch (IOException ex) {}
/*     */     } 
/* 210 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadAllResources(List v, String name) {
/* 217 */     boolean anyLoaded = false;
/*     */     try {
/*     */       URL[] urls;
/* 220 */       ClassLoader cld = null;
/*     */       
/* 222 */       cld = SecuritySupport.getContextClassLoader();
/* 223 */       if (cld == null)
/* 224 */         cld = getClass().getClassLoader(); 
/* 225 */       if (cld != null) {
/* 226 */         urls = SecuritySupport.getResources(cld, name);
/*     */       } else {
/* 228 */         urls = SecuritySupport.getSystemResources(name);
/* 229 */       }  if (urls != null) {
/* 230 */         if (LogSupport.isLoggable())
/* 231 */           LogSupport.log("MailcapCommandMap: getResources"); 
/* 232 */         for (int i = 0; i < urls.length; i++) {
/* 233 */           URL url = urls[i];
/* 234 */           InputStream clis = null;
/* 235 */           if (LogSupport.isLoggable());
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
/*     */         }
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
/*     */       }
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
/*     */     }
/* 269 */     catch (Exception ex) {
/* 270 */       if (LogSupport.isLoggable()) {
/* 271 */         LogSupport.log("MailcapCommandMap: can't load " + name, ex);
/*     */       }
/*     */     } 
/*     */     
/* 275 */     if (!anyLoaded) {
/* 276 */       if (LogSupport.isLoggable())
/* 277 */         LogSupport.log("MailcapCommandMap: !anyLoaded"); 
/* 278 */       MailcapFile mf = loadResource("/" + name);
/* 279 */       if (mf != null) {
/* 280 */         v.add(mf);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private MailcapFile loadFile(String name) {
/* 288 */     MailcapFile mtf = null;
/*     */     
/*     */     try {
/* 291 */       mtf = new MailcapFile(name);
/* 292 */     } catch (IOException e) {}
/*     */ 
/*     */     
/* 295 */     return mtf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailcapCommandMap(String fileName) throws IOException {
/* 306 */     this();
/*     */     
/* 308 */     if (LogSupport.isLoggable())
/* 309 */       LogSupport.log("MailcapCommandMap: load PROG from " + fileName); 
/* 310 */     if (this.DB[0] == null) {
/* 311 */       this.DB[0] = new MailcapFile(fileName);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailcapCommandMap(InputStream is) {
/* 323 */     this();
/*     */     
/* 325 */     LogSupport.log("MailcapCommandMap: load PROG");
/* 326 */     if (this.DB[0] == null) {
/*     */       try {
/* 328 */         this.DB[0] = new MailcapFile(is);
/* 329 */       } catch (IOException ex) {}
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized CommandInfo[] getPreferredCommands(String mimeType) {
/* 349 */     List cmdList = new ArrayList();
/* 350 */     if (mimeType != null)
/* 351 */       mimeType = mimeType.toLowerCase(); 
/*     */     int i;
/* 353 */     for (i = 0; i < this.DB.length; i++) {
/* 354 */       if (this.DB[i] != null) {
/*     */         
/* 356 */         Map cmdMap = this.DB[i].getMailcapList(mimeType);
/* 357 */         if (cmdMap != null) {
/* 358 */           appendPrefCmdsToList(cmdMap, cmdList);
/*     */         }
/*     */       } 
/*     */     } 
/* 362 */     for (i = 0; i < this.DB.length; i++) {
/* 363 */       if (this.DB[i] != null) {
/*     */         
/* 365 */         Map cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
/* 366 */         if (cmdMap != null)
/* 367 */           appendPrefCmdsToList(cmdMap, cmdList); 
/*     */       } 
/*     */     } 
/* 370 */     CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
/* 371 */     cmdInfos = (CommandInfo[])cmdList.toArray((Object[])cmdInfos);
/*     */     
/* 373 */     return cmdInfos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void appendPrefCmdsToList(Map cmdHash, List cmdList) {
/* 380 */     Iterator verb_enum = cmdHash.keySet().iterator();
/*     */     
/* 382 */     while (verb_enum.hasNext()) {
/* 383 */       String verb = verb_enum.next();
/* 384 */       if (!checkForVerb(cmdList, verb)) {
/* 385 */         List cmdList2 = (List)cmdHash.get(verb);
/* 386 */         String className = cmdList2.get(0);
/* 387 */         cmdList.add(new CommandInfo(verb, className));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean checkForVerb(List cmdList, String verb) {
/* 397 */     Iterator ee = cmdList.iterator();
/* 398 */     while (ee.hasNext()) {
/* 399 */       String enum_verb = ((CommandInfo)ee.next()).getCommandName();
/*     */       
/* 401 */       if (enum_verb.equals(verb))
/* 402 */         return true; 
/*     */     } 
/* 404 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized CommandInfo[] getAllCommands(String mimeType) {
/* 415 */     List cmdList = new ArrayList();
/* 416 */     if (mimeType != null)
/* 417 */       mimeType = mimeType.toLowerCase(); 
/*     */     int i;
/* 419 */     for (i = 0; i < this.DB.length; i++) {
/* 420 */       if (this.DB[i] != null) {
/*     */         
/* 422 */         Map cmdMap = this.DB[i].getMailcapList(mimeType);
/* 423 */         if (cmdMap != null) {
/* 424 */           appendCmdsToList(cmdMap, cmdList);
/*     */         }
/*     */       } 
/*     */     } 
/* 428 */     for (i = 0; i < this.DB.length; i++) {
/* 429 */       if (this.DB[i] != null) {
/*     */         
/* 431 */         Map cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
/* 432 */         if (cmdMap != null)
/* 433 */           appendCmdsToList(cmdMap, cmdList); 
/*     */       } 
/*     */     } 
/* 436 */     CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
/* 437 */     cmdInfos = (CommandInfo[])cmdList.toArray((Object[])cmdInfos);
/*     */     
/* 439 */     return cmdInfos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void appendCmdsToList(Map typeHash, List cmdList) {
/* 446 */     Iterator verb_enum = typeHash.keySet().iterator();
/*     */     
/* 448 */     while (verb_enum.hasNext()) {
/* 449 */       String verb = verb_enum.next();
/* 450 */       List cmdList2 = (List)typeHash.get(verb);
/* 451 */       Iterator cmd_enum = cmdList2.iterator();
/*     */       
/* 453 */       while (cmd_enum.hasNext()) {
/* 454 */         String cmd = cmd_enum.next();
/* 455 */         cmdList.add(new CommandInfo(verb, cmd));
/*     */       } 
/*     */     } 
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
/*     */   public synchronized CommandInfo getCommand(String mimeType, String cmdName) {
/* 470 */     if (mimeType != null)
/* 471 */       mimeType = mimeType.toLowerCase(); 
/*     */     int i;
/* 473 */     for (i = 0; i < this.DB.length; i++) {
/* 474 */       if (this.DB[i] != null) {
/*     */         
/* 476 */         Map cmdMap = this.DB[i].getMailcapList(mimeType);
/* 477 */         if (cmdMap != null) {
/*     */           
/* 479 */           List v = (List)cmdMap.get(cmdName);
/* 480 */           if (v != null) {
/* 481 */             String cmdClassName = v.get(0);
/*     */             
/* 483 */             if (cmdClassName != null) {
/* 484 */               return new CommandInfo(cmdName, cmdClassName);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 490 */     for (i = 0; i < this.DB.length; i++) {
/* 491 */       if (this.DB[i] != null) {
/*     */         
/* 493 */         Map cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
/* 494 */         if (cmdMap != null) {
/*     */           
/* 496 */           List v = (List)cmdMap.get(cmdName);
/* 497 */           if (v != null) {
/* 498 */             String cmdClassName = v.get(0);
/*     */             
/* 500 */             if (cmdClassName != null)
/* 501 */               return new CommandInfo(cmdName, cmdClassName); 
/*     */           } 
/*     */         } 
/*     */       } 
/* 505 */     }  return null;
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
/*     */   public synchronized void addMailcap(String mail_cap) {
/* 519 */     LogSupport.log("MailcapCommandMap: add to PROG");
/* 520 */     if (this.DB[0] == null) {
/* 521 */       this.DB[0] = new MailcapFile();
/*     */     }
/* 523 */     this.DB[0].appendToMailcap(mail_cap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized DataContentHandler createDataContentHandler(String mimeType) {
/* 534 */     if (LogSupport.isLoggable()) {
/* 535 */       LogSupport.log("MailcapCommandMap: createDataContentHandler for " + mimeType);
/*     */     }
/* 537 */     if (mimeType != null)
/* 538 */       mimeType = mimeType.toLowerCase(); 
/*     */     int i;
/* 540 */     for (i = 0; i < this.DB.length; i++) {
/* 541 */       if (this.DB[i] != null) {
/*     */         
/* 543 */         if (LogSupport.isLoggable())
/* 544 */           LogSupport.log("  search DB #" + i); 
/* 545 */         Map cmdMap = this.DB[i].getMailcapList(mimeType);
/* 546 */         if (cmdMap != null) {
/* 547 */           List v = (List)cmdMap.get("content-handler");
/* 548 */           if (v != null) {
/* 549 */             String name = v.get(0);
/* 550 */             DataContentHandler dch = getDataContentHandler(name);
/* 551 */             if (dch != null) {
/* 552 */               return dch;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 558 */     for (i = 0; i < this.DB.length; i++) {
/* 559 */       if (this.DB[i] != null) {
/*     */         
/* 561 */         if (LogSupport.isLoggable())
/* 562 */           LogSupport.log("  search fallback DB #" + i); 
/* 563 */         Map cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
/* 564 */         if (cmdMap != null) {
/* 565 */           List v = (List)cmdMap.get("content-handler");
/* 566 */           if (v != null) {
/* 567 */             String name = v.get(0);
/* 568 */             DataContentHandler dch = getDataContentHandler(name);
/* 569 */             if (dch != null)
/* 570 */               return dch; 
/*     */           } 
/*     */         } 
/*     */       } 
/* 574 */     }  return null;
/*     */   }
/*     */   
/*     */   private DataContentHandler getDataContentHandler(String name) {
/* 578 */     if (LogSupport.isLoggable())
/* 579 */       LogSupport.log("    got content-handler"); 
/* 580 */     if (LogSupport.isLoggable())
/* 581 */       LogSupport.log("      class " + name); 
/*     */     try {
/* 583 */       ClassLoader cld = null;
/*     */       
/* 585 */       cld = SecuritySupport.getContextClassLoader();
/* 586 */       if (cld == null)
/* 587 */         cld = getClass().getClassLoader(); 
/* 588 */       Class cl = null;
/*     */       try {
/* 590 */         cl = cld.loadClass(name);
/* 591 */       } catch (Exception ex) {
/*     */         
/* 593 */         cl = Class.forName(name);
/*     */       } 
/* 595 */       if (cl != null)
/* 596 */         return (DataContentHandler)cl.newInstance(); 
/* 597 */     } catch (IllegalAccessException e) {
/* 598 */       if (LogSupport.isLoggable())
/* 599 */         LogSupport.log("Can't load DCH " + name, e); 
/* 600 */     } catch (ClassNotFoundException e) {
/* 601 */       if (LogSupport.isLoggable())
/* 602 */         LogSupport.log("Can't load DCH " + name, e); 
/* 603 */     } catch (InstantiationException e) {
/* 604 */       if (LogSupport.isLoggable())
/* 605 */         LogSupport.log("Can't load DCH " + name, e); 
/*     */     } 
/* 607 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String[] getMimeTypes() {
/* 617 */     List mtList = new ArrayList();
/*     */     
/* 619 */     for (int i = 0; i < this.DB.length; i++) {
/* 620 */       if (this.DB[i] != null) {
/*     */         
/* 622 */         String[] ts = this.DB[i].getMimeTypes();
/* 623 */         if (ts != null)
/* 624 */           for (int j = 0; j < ts.length; j++) {
/*     */             
/* 626 */             if (!mtList.contains(ts[j])) {
/* 627 */               mtList.add(ts[j]);
/*     */             }
/*     */           }  
/*     */       } 
/*     */     } 
/* 632 */     String[] mts = new String[mtList.size()];
/* 633 */     mts = mtList.<String>toArray(mts);
/*     */     
/* 635 */     return mts;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String[] getNativeCommands(String mimeType) {
/* 653 */     List cmdList = new ArrayList();
/* 654 */     if (mimeType != null) {
/* 655 */       mimeType = mimeType.toLowerCase();
/*     */     }
/* 657 */     for (int i = 0; i < this.DB.length; i++) {
/* 658 */       if (this.DB[i] != null) {
/*     */         
/* 660 */         String[] arrayOfString = this.DB[i].getNativeCommands(mimeType);
/* 661 */         if (arrayOfString != null)
/* 662 */           for (int j = 0; j < arrayOfString.length; j++) {
/*     */             
/* 664 */             if (!cmdList.contains(arrayOfString[j])) {
/* 665 */               cmdList.add(arrayOfString[j]);
/*     */             }
/*     */           }  
/*     */       } 
/*     */     } 
/* 670 */     String[] cmds = new String[cmdList.size()];
/* 671 */     cmds = cmdList.<String>toArray(cmds);
/*     */     
/* 673 */     return cmds;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\MailcapCommandMap.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */