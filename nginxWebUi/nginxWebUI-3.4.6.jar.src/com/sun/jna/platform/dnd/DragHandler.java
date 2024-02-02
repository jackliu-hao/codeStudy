/*     */ package com.sun.jna.platform.dnd;
/*     */ 
/*     */ import com.sun.jna.Platform;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.dnd.DragGestureEvent;
/*     */ import java.awt.dnd.DragGestureListener;
/*     */ import java.awt.dnd.DragSource;
/*     */ import java.awt.dnd.DragSourceContext;
/*     */ import java.awt.dnd.DragSourceDragEvent;
/*     */ import java.awt.dnd.DragSourceDropEvent;
/*     */ import java.awt.dnd.DragSourceEvent;
/*     */ import java.awt.dnd.DragSourceListener;
/*     */ import java.awt.dnd.DragSourceMotionListener;
/*     */ import java.awt.dnd.DropTargetDragEvent;
/*     */ import java.awt.dnd.DropTargetDropEvent;
/*     */ import java.awt.dnd.DropTargetEvent;
/*     */ import java.awt.dnd.InvalidDnDOperationException;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JColorChooser;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.text.JTextComponent;
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
/*     */ public abstract class DragHandler
/*     */   implements DragSourceListener, DragSourceMotionListener, DragGestureListener
/*     */ {
/* 116 */   private static final Logger LOG = Logger.getLogger(DragHandler.class.getName());
/*     */ 
/*     */   
/* 119 */   public static final Dimension MAX_GHOST_SIZE = new Dimension(250, 250);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final float DEFAULT_GHOST_ALPHA = 0.5F;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int UNKNOWN_MODIFIERS = -1;
/*     */ 
/*     */ 
/*     */   
/* 132 */   public static final Transferable UNKNOWN_TRANSFERABLE = null;
/*     */ 
/*     */   
/*     */   protected static final int MOVE = 2;
/*     */ 
/*     */   
/*     */   protected static final int COPY = 1;
/*     */   
/*     */   protected static final int LINK = 1073741824;
/*     */   
/*     */   protected static final int NONE = 0;
/*     */   
/*     */   static final int MOVE_MASK = 64;
/*     */   
/* 146 */   static final boolean OSX = Platform.isMac();
/*     */   
/* 148 */   static final int COPY_MASK = OSX ? 512 : 128;
/*     */ 
/*     */   
/* 151 */   static final int LINK_MASK = OSX ? 768 : 192;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int KEY_MASK = 9152;
/*     */ 
/*     */ 
/*     */   
/* 160 */   private static int modifiers = -1;
/* 161 */   private static Transferable transferable = UNKNOWN_TRANSFERABLE;
/*     */ 
/*     */ 
/*     */   
/*     */   private int supportedActions;
/*     */ 
/*     */ 
/*     */   
/*     */   static int getModifiers() {
/* 170 */     return modifiers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Transferable getTransferable(DropTargetEvent e) {
/* 180 */     if (e instanceof DropTargetDragEvent) {
/*     */       try {
/* 182 */         return ((DropTargetDragEvent)e).getTransferable();
/* 183 */       } catch (Exception exception) {}
/*     */     
/*     */     }
/* 186 */     else if (e instanceof DropTargetDropEvent) {
/* 187 */       return ((DropTargetDropEvent)e).getTransferable();
/*     */     } 
/* 189 */     return transferable;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean fixCursor = true;
/*     */   private Component dragSource;
/*     */   private GhostedDragImage ghost;
/*     */   private Point imageOffset;
/* 197 */   private Dimension maxGhostSize = MAX_GHOST_SIZE;
/* 198 */   private float ghostAlpha = 0.5F;
/*     */   
/*     */   private String lastAction;
/*     */   
/*     */   private boolean moved;
/*     */ 
/*     */   
/*     */   protected DragHandler(Component dragSource, int actions) {
/* 206 */     this.dragSource = dragSource;
/* 207 */     this.supportedActions = actions;
/*     */     try {
/* 209 */       String alpha = System.getProperty("DragHandler.alpha");
/* 210 */       if (alpha != null) {
/*     */         try {
/* 212 */           this.ghostAlpha = Float.parseFloat(alpha);
/*     */         }
/* 214 */         catch (NumberFormatException numberFormatException) {}
/*     */       }
/* 216 */       String max = System.getProperty("DragHandler.maxDragImageSize");
/* 217 */       if (max != null) {
/* 218 */         String[] size = max.split("x");
/* 219 */         if (size.length == 2) {
/*     */           try {
/* 221 */             this
/* 222 */               .maxGhostSize = new Dimension(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
/*     */           }
/* 224 */           catch (NumberFormatException numberFormatException) {}
/*     */         }
/*     */       }
/*     */     
/* 228 */     } catch (SecurityException securityException) {}
/*     */     
/* 230 */     disableSwingDragSupport(dragSource);
/* 231 */     DragSource src = DragSource.getDefaultDragSource();
/* 232 */     src.createDefaultDragGestureRecognizer(dragSource, this.supportedActions, this);
/*     */   }
/*     */   
/*     */   private void disableSwingDragSupport(Component comp) {
/* 236 */     if (comp instanceof JTree) {
/* 237 */       ((JTree)comp).setDragEnabled(false);
/*     */     }
/* 239 */     else if (comp instanceof JList) {
/* 240 */       ((JList)comp).setDragEnabled(false);
/*     */     }
/* 242 */     else if (comp instanceof JTable) {
/* 243 */       ((JTable)comp).setDragEnabled(false);
/*     */     }
/* 245 */     else if (comp instanceof JTextComponent) {
/* 246 */       ((JTextComponent)comp).setDragEnabled(false);
/*     */     }
/* 248 */     else if (comp instanceof JColorChooser) {
/* 249 */       ((JColorChooser)comp).setDragEnabled(false);
/*     */     }
/* 251 */     else if (comp instanceof JFileChooser) {
/* 252 */       ((JFileChooser)comp).setDragEnabled(false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean canDrag(DragGestureEvent e) {
/* 263 */     int mods = e.getTriggerEvent().getModifiersEx() & 0x23C0;
/* 264 */     if (mods == 64)
/* 265 */       return ((this.supportedActions & 0x2) != 0); 
/* 266 */     if (mods == COPY_MASK)
/* 267 */       return ((this.supportedActions & 0x1) != 0); 
/* 268 */     if (mods == LINK_MASK)
/* 269 */       return ((this.supportedActions & 0x40000000) != 0); 
/* 270 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setModifiers(int mods) {
/* 277 */     modifiers = mods;
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
/*     */   protected abstract Transferable getTransferable(DragGestureEvent paramDragGestureEvent);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Icon getDragIcon(DragGestureEvent e, Point srcOffset) {
/* 299 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void dragStarted(DragGestureEvent e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dragGestureRecognized(DragGestureEvent e) {
/* 314 */     if ((e.getDragAction() & this.supportedActions) != 0 && 
/* 315 */       canDrag(e)) {
/* 316 */       setModifiers(e.getTriggerEvent().getModifiersEx() & 0x23C0);
/* 317 */       Transferable transferable = getTransferable(e);
/* 318 */       if (transferable == null)
/*     */         return; 
/*     */       try {
/* 321 */         Point srcOffset = new Point(0, 0);
/* 322 */         Icon icon = getDragIcon(e, srcOffset);
/* 323 */         Point origin = e.getDragOrigin();
/*     */         
/* 325 */         this.imageOffset = new Point(srcOffset.x - origin.x, srcOffset.y - origin.y);
/*     */         
/* 327 */         Icon dragIcon = scaleDragIcon(icon, this.imageOffset);
/* 328 */         Cursor cursor = null;
/* 329 */         if (dragIcon != null && DragSource.isDragImageSupported()) {
/* 330 */           GraphicsConfiguration gc = e.getComponent().getGraphicsConfiguration();
/* 331 */           e.startDrag(cursor, createDragImage(gc, dragIcon), this.imageOffset, transferable, this);
/*     */         }
/*     */         else {
/*     */           
/* 335 */           if (dragIcon != null) {
/* 336 */             Point screen = this.dragSource.getLocationOnScreen();
/* 337 */             screen.translate(origin.x, origin.y);
/* 338 */             Point cursorOffset = new Point(-this.imageOffset.x, -this.imageOffset.y);
/* 339 */             this
/* 340 */               .ghost = new GhostedDragImage(this.dragSource, dragIcon, getImageLocation(screen), cursorOffset);
/* 341 */             this.ghost.setAlpha(this.ghostAlpha);
/*     */           } 
/* 343 */           e.startDrag(cursor, transferable, this);
/*     */         } 
/* 345 */         dragStarted(e);
/* 346 */         this.moved = false;
/* 347 */         e.getDragSource().addDragSourceMotionListener(this);
/* 348 */         DragHandler.transferable = transferable;
/*     */       }
/* 350 */       catch (InvalidDnDOperationException ex) {
/* 351 */         if (this.ghost != null) {
/* 352 */           this.ghost.dispose();
/* 353 */           this.ghost = null;
/*     */         } 
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
/*     */   protected Icon scaleDragIcon(Icon icon, Point imageOffset) {
/* 379 */     return icon;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Image createDragImage(GraphicsConfiguration gc, Icon icon) {
/* 389 */     int w = icon.getIconWidth();
/* 390 */     int h = icon.getIconHeight();
/* 391 */     BufferedImage image = gc.createCompatibleImage(w, h, 3);
/* 392 */     Graphics2D g = (Graphics2D)image.getGraphics();
/* 393 */     g.setComposite(AlphaComposite.Clear);
/* 394 */     g.fillRect(0, 0, w, h);
/*     */     
/* 396 */     g.setComposite(AlphaComposite.getInstance(2, this.ghostAlpha));
/* 397 */     icon.paintIcon(this.dragSource, g, 0, 0);
/* 398 */     g.dispose();
/* 399 */     return image;
/*     */   }
/*     */ 
/*     */   
/*     */   private int reduce(int actions) {
/* 404 */     if ((actions & 0x2) != 0 && actions != 2) {
/* 405 */       return 2;
/*     */     }
/* 407 */     if ((actions & 0x1) != 0 && actions != 1) {
/* 408 */       return 1;
/*     */     }
/* 410 */     return actions;
/*     */   }
/*     */   
/*     */   protected Cursor getCursorForAction(int actualAction) {
/* 414 */     switch (actualAction) {
/*     */       case 2:
/* 416 */         return DragSource.DefaultMoveDrop;
/*     */       case 1:
/* 418 */         return DragSource.DefaultCopyDrop;
/*     */       case 1073741824:
/* 420 */         return DragSource.DefaultLinkDrop;
/*     */     } 
/* 422 */     return DragSource.DefaultMoveNoDrop;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getAcceptableDropAction(int targetActions) {
/* 431 */     return reduce(this.supportedActions & targetActions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getDropAction(DragSourceEvent ev) {
/* 439 */     if (ev instanceof DragSourceDragEvent) {
/* 440 */       DragSourceDragEvent e = (DragSourceDragEvent)ev;
/* 441 */       return e.getDropAction();
/*     */     } 
/* 443 */     if (ev instanceof DragSourceDropEvent) {
/* 444 */       return ((DragSourceDropEvent)ev).getDropAction();
/*     */     }
/* 446 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int adjustDropAction(DragSourceEvent ev) {
/* 455 */     int action = getDropAction(ev);
/* 456 */     if (ev instanceof DragSourceDragEvent) {
/* 457 */       DragSourceDragEvent e = (DragSourceDragEvent)ev;
/* 458 */       if (action == 0) {
/* 459 */         int mods = e.getGestureModifiersEx() & 0x23C0;
/* 460 */         if (mods == 0) {
/* 461 */           action = getAcceptableDropAction(e.getTargetActions());
/*     */         }
/*     */       } 
/*     */     } 
/* 465 */     return action;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void updateCursor(DragSourceEvent ev) {
/* 473 */     if (!this.fixCursor)
/*     */       return; 
/* 475 */     Cursor cursor = getCursorForAction(adjustDropAction(ev));
/* 476 */     ev.getDragSourceContext().setCursor(cursor);
/*     */   }
/*     */   
/*     */   static String actionString(int action) {
/* 480 */     switch (action) { case 2:
/* 481 */         return "MOVE";
/* 482 */       case 3: return "MOVE|COPY";
/* 483 */       case 1073741826: return "MOVE|LINK";
/* 484 */       case 1073741827: return "MOVE|COPY|LINK";
/* 485 */       case 1: return "COPY";
/* 486 */       case 1073741825: return "COPY|LINK";
/* 487 */       case 1073741824: return "LINK"; }
/* 488 */      return "NONE";
/*     */   }
/*     */ 
/*     */   
/*     */   private void describe(String type, DragSourceEvent e) {
/* 493 */     if (LOG.isLoggable(Level.FINE)) {
/* 494 */       StringBuilder msgBuilder = new StringBuilder();
/* 495 */       msgBuilder.append("drag: ");
/* 496 */       msgBuilder.append(type);
/* 497 */       DragSourceContext ds = e.getDragSourceContext();
/* 498 */       if (e instanceof DragSourceDragEvent) {
/* 499 */         DragSourceDragEvent ev = (DragSourceDragEvent)e;
/* 500 */         msgBuilder.append(": src=");
/* 501 */         msgBuilder.append(actionString(ds.getSourceActions()));
/* 502 */         msgBuilder.append(" usr=");
/* 503 */         msgBuilder.append(actionString(ev.getUserAction()));
/* 504 */         msgBuilder.append(" tgt=");
/* 505 */         msgBuilder.append(actionString(ev.getTargetActions()));
/* 506 */         msgBuilder.append(" act=");
/* 507 */         msgBuilder.append(actionString(ev.getDropAction()));
/* 508 */         msgBuilder.append(" mods=");
/* 509 */         msgBuilder.append(ev.getGestureModifiersEx());
/*     */       } else {
/*     */         
/* 512 */         msgBuilder.append(": e=");
/* 513 */         msgBuilder.append(e);
/*     */       } 
/* 515 */       String msg = msgBuilder.toString();
/* 516 */       if (!msg.equals(this.lastAction)) {
/* 517 */         LOG.log(Level.FINE, msg);
/* 518 */         this.lastAction = msg;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void dragDropEnd(DragSourceDropEvent e) {
/* 525 */     describe("end", e);
/* 526 */     setModifiers(-1);
/* 527 */     transferable = UNKNOWN_TRANSFERABLE;
/* 528 */     if (this.ghost != null) {
/* 529 */       if (e.getDropSuccess()) {
/* 530 */         this.ghost.dispose();
/*     */       } else {
/*     */         
/* 533 */         this.ghost.returnToOrigin();
/*     */       } 
/* 535 */       this.ghost = null;
/*     */     } 
/* 537 */     DragSource src = e.getDragSourceContext().getDragSource();
/* 538 */     src.removeDragSourceMotionListener(this);
/* 539 */     this.moved = false;
/*     */   }
/*     */   
/*     */   private Point getImageLocation(Point where) {
/* 543 */     where.translate(this.imageOffset.x, this.imageOffset.y);
/* 544 */     return where;
/*     */   }
/*     */ 
/*     */   
/*     */   public void dragEnter(DragSourceDragEvent e) {
/* 549 */     describe("enter", e);
/* 550 */     if (this.ghost != null) {
/* 551 */       this.ghost.move(getImageLocation(e.getLocation()));
/*     */     }
/* 553 */     updateCursor(e);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dragMouseMoved(DragSourceDragEvent e) {
/* 562 */     describe("move", e);
/* 563 */     if (this.ghost != null) {
/* 564 */       this.ghost.move(getImageLocation(e.getLocation()));
/*     */     }
/* 566 */     if (this.moved)
/* 567 */       updateCursor(e); 
/* 568 */     this.moved = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void dragOver(DragSourceDragEvent e) {
/* 573 */     describe("over", e);
/* 574 */     if (this.ghost != null) {
/* 575 */       this.ghost.move(getImageLocation(e.getLocation()));
/*     */     }
/* 577 */     updateCursor(e);
/*     */   }
/*     */ 
/*     */   
/*     */   public void dragExit(DragSourceEvent e) {
/* 582 */     describe("exit", e);
/*     */   }
/*     */ 
/*     */   
/*     */   public void dropActionChanged(DragSourceDragEvent e) {
/* 587 */     describe("change", e);
/* 588 */     setModifiers(e.getGestureModifiersEx() & 0x23C0);
/* 589 */     if (this.ghost != null) {
/* 590 */       this.ghost.move(getImageLocation(e.getLocation()));
/*     */     }
/* 592 */     updateCursor(e);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\dnd\DragHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */