/*     */ package com.sun.jna.platform.dnd;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Point;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.awt.dnd.DropTarget;
/*     */ import java.awt.dnd.DropTargetContext;
/*     */ import java.awt.dnd.DropTargetDragEvent;
/*     */ import java.awt.dnd.DropTargetDropEvent;
/*     */ import java.awt.dnd.DropTargetEvent;
/*     */ import java.awt.dnd.DropTargetListener;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
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
/*     */ public abstract class DropHandler
/*     */   implements DropTargetListener
/*     */ {
/* 102 */   private static final Logger LOG = Logger.getLogger(DropHandler.class.getName());
/*     */   
/*     */   private int acceptedActions;
/*     */   
/*     */   private List<DataFlavor> acceptedFlavors;
/*     */   
/*     */   private DropTarget dropTarget;
/*     */   
/*     */   private boolean active = true;
/*     */   
/*     */   private DropTargetPainter painter;
/*     */   
/*     */   private String lastAction;
/*     */   
/*     */   public DropHandler(Component c, int acceptedActions) {
/* 117 */     this(c, acceptedActions, new DataFlavor[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DropHandler(Component c, int acceptedActions, DataFlavor[] acceptedFlavors) {
/* 128 */     this(c, acceptedActions, acceptedFlavors, null);
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
/*     */   public DropHandler(Component c, int acceptedActions, DataFlavor[] acceptedFlavors, DropTargetPainter painter) {
/* 141 */     this.acceptedActions = acceptedActions;
/* 142 */     this.acceptedFlavors = Arrays.asList(acceptedFlavors);
/* 143 */     this.painter = painter;
/* 144 */     this.dropTarget = new DropTarget(c, acceptedActions, this, this.active);
/*     */   }
/*     */   
/*     */   protected DropTarget getDropTarget() {
/* 148 */     return this.dropTarget;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 154 */     return this.active;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setActive(boolean active) {
/* 161 */     this.active = active;
/* 162 */     if (this.dropTarget != null) {
/* 163 */       this.dropTarget.setActive(active);
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
/*     */   protected int getDropActionsForFlavors(DataFlavor[] dataFlavors) {
/* 177 */     return this.acceptedActions;
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
/*     */   protected int getDropAction(DropTargetEvent e) {
/* 196 */     int currentAction = 0;
/* 197 */     int sourceActions = 0;
/* 198 */     Point location = null;
/* 199 */     DataFlavor[] flavors = new DataFlavor[0];
/* 200 */     if (e instanceof DropTargetDragEvent) {
/* 201 */       DropTargetDragEvent ev = (DropTargetDragEvent)e;
/* 202 */       currentAction = ev.getDropAction();
/* 203 */       sourceActions = ev.getSourceActions();
/* 204 */       flavors = ev.getCurrentDataFlavors();
/* 205 */       location = ev.getLocation();
/*     */     }
/* 207 */     else if (e instanceof DropTargetDropEvent) {
/* 208 */       DropTargetDropEvent ev = (DropTargetDropEvent)e;
/* 209 */       currentAction = ev.getDropAction();
/* 210 */       sourceActions = ev.getSourceActions();
/* 211 */       flavors = ev.getCurrentDataFlavors();
/* 212 */       location = ev.getLocation();
/*     */     } 
/* 214 */     if (isSupported(flavors)) {
/* 215 */       int availableActions = getDropActionsForFlavors(flavors);
/* 216 */       currentAction = getDropAction(e, currentAction, sourceActions, availableActions);
/* 217 */       if (currentAction != 0 && 
/* 218 */         canDrop(e, currentAction, location)) {
/* 219 */         return currentAction;
/*     */       }
/*     */     } 
/*     */     
/* 223 */     return 0;
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
/*     */   protected int getDropAction(DropTargetEvent e, int currentAction, int sourceActions, int acceptedActions) {
/* 244 */     boolean modifiersActive = modifiersActive(currentAction);
/* 245 */     if ((currentAction & acceptedActions) == 0 && !modifiersActive) {
/*     */       
/* 247 */       int action = acceptedActions & sourceActions;
/* 248 */       currentAction = action;
/*     */     }
/* 250 */     else if (modifiersActive) {
/* 251 */       int action = currentAction & acceptedActions & sourceActions;
/* 252 */       if (action != currentAction) {
/* 253 */         currentAction = action;
/*     */       }
/*     */     } 
/* 256 */     return currentAction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean modifiersActive(int dropAction) {
/* 267 */     int mods = DragHandler.getModifiers();
/* 268 */     if (mods == -1) {
/* 269 */       if (dropAction == 1073741824 || dropAction == 1)
/*     */       {
/* 271 */         return true;
/*     */       }
/*     */ 
/*     */       
/* 275 */       return false;
/*     */     } 
/* 277 */     return (mods != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   private void describe(String type, DropTargetEvent e) {
/* 282 */     if (LOG.isLoggable(Level.FINE)) {
/* 283 */       StringBuilder msgBuilder = new StringBuilder();
/* 284 */       msgBuilder.append("drop: ");
/* 285 */       msgBuilder.append(type);
/* 286 */       if (e instanceof DropTargetDragEvent) {
/* 287 */         DropTargetContext dtc = e.getDropTargetContext();
/* 288 */         DropTarget dt = dtc.getDropTarget();
/* 289 */         DropTargetDragEvent ev = (DropTargetDragEvent)e;
/* 290 */         msgBuilder.append(": src=");
/* 291 */         msgBuilder.append(DragHandler.actionString(ev.getSourceActions()));
/* 292 */         msgBuilder.append(" tgt=");
/* 293 */         msgBuilder.append(DragHandler.actionString(dt.getDefaultActions()));
/* 294 */         msgBuilder.append(" act=");
/* 295 */         msgBuilder.append(DragHandler.actionString(ev.getDropAction()));
/*     */       }
/* 297 */       else if (e instanceof DropTargetDropEvent) {
/* 298 */         DropTargetContext dtc = e.getDropTargetContext();
/* 299 */         DropTarget dt = dtc.getDropTarget();
/* 300 */         DropTargetDropEvent ev = (DropTargetDropEvent)e;
/* 301 */         msgBuilder.append(": src=");
/* 302 */         msgBuilder.append(DragHandler.actionString(ev.getSourceActions()));
/* 303 */         msgBuilder.append(" tgt=");
/* 304 */         msgBuilder.append(DragHandler.actionString(dt.getDefaultActions()));
/* 305 */         msgBuilder.append(" act=");
/* 306 */         msgBuilder.append(DragHandler.actionString(ev.getDropAction()));
/*     */       } 
/* 308 */       String msg = msgBuilder.toString();
/* 309 */       if (!msg.equals(this.lastAction)) {
/* 310 */         LOG.log(Level.FINE, msg);
/* 311 */         this.lastAction = msg;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int acceptOrReject(DropTargetDragEvent e) {
/* 322 */     int action = getDropAction(e);
/* 323 */     if (action != 0) {
/*     */ 
/*     */       
/* 326 */       e.acceptDrag(action);
/*     */     } else {
/*     */       
/* 329 */       e.rejectDrag();
/*     */     } 
/* 331 */     return action;
/*     */   }
/*     */ 
/*     */   
/*     */   public void dragEnter(DropTargetDragEvent e) {
/* 336 */     describe("enter(tgt)", e);
/* 337 */     int action = acceptOrReject(e);
/* 338 */     paintDropTarget(e, action, e.getLocation());
/*     */   }
/*     */ 
/*     */   
/*     */   public void dragOver(DropTargetDragEvent e) {
/* 343 */     describe("over(tgt)", e);
/* 344 */     int action = acceptOrReject(e);
/* 345 */     paintDropTarget(e, action, e.getLocation());
/*     */   }
/*     */ 
/*     */   
/*     */   public void dragExit(DropTargetEvent e) {
/* 350 */     describe("exit(tgt)", e);
/* 351 */     paintDropTarget(e, 0, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void dropActionChanged(DropTargetDragEvent e) {
/* 356 */     describe("change(tgt)", e);
/* 357 */     int action = acceptOrReject(e);
/* 358 */     paintDropTarget(e, action, e.getLocation());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drop(DropTargetDropEvent e) {
/* 367 */     describe("drop(tgt)", e);
/* 368 */     int action = getDropAction(e);
/* 369 */     if (action != 0) {
/* 370 */       e.acceptDrop(action);
/*     */       try {
/* 372 */         drop(e, action);
/*     */         
/* 374 */         e.dropComplete(true);
/* 375 */       } catch (Exception ex) {
/* 376 */         e.dropComplete(false);
/*     */       } 
/*     */     } else {
/* 379 */       e.rejectDrop();
/*     */     } 
/* 381 */     paintDropTarget(e, 0, e.getLocation());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isSupported(DataFlavor[] flavors) {
/* 391 */     Set<DataFlavor> set = new HashSet<DataFlavor>(Arrays.asList(flavors));
/* 392 */     set.retainAll(this.acceptedFlavors);
/* 393 */     return !set.isEmpty();
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
/*     */   protected void paintDropTarget(DropTargetEvent e, int action, Point location) {
/* 410 */     if (this.painter != null) {
/* 411 */       this.painter.paintDropTarget(e, action, location);
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
/*     */   protected boolean canDrop(DropTargetEvent e, int action, Point location) {
/* 426 */     return true;
/*     */   }
/*     */   
/*     */   protected abstract void drop(DropTargetDropEvent paramDropTargetDropEvent, int paramInt) throws UnsupportedFlavorException, IOException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\dnd\DropHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */