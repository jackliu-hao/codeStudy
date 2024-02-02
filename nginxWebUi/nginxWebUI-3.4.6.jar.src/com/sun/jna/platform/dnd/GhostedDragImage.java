/*     */ package com.sun.jna.platform.dnd;
/*     */ 
/*     */ import com.sun.jna.platform.WindowUtils;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.geom.Area;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.Timer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GhostedDragImage
/*     */ {
/*     */   private static final float DEFAULT_ALPHA = 0.5F;
/*     */   private Window dragImage;
/*     */   private Point origin;
/*     */   private static final int SLIDE_INTERVAL = 33;
/*     */   
/*     */   public GhostedDragImage(Component dragSource, final Icon icon, Point initialScreenLoc, final Point cursorOffset) {
/*  68 */     Window parent = (dragSource instanceof Window) ? (Window)dragSource : SwingUtilities.getWindowAncestor(dragSource);
/*     */     
/*  70 */     GraphicsConfiguration gc = parent.getGraphicsConfiguration();
/*  71 */     this.dragImage = new Window(JOptionPane.getRootFrame(), gc)
/*     */       {
/*     */         public void paint(Graphics g) {
/*  74 */           icon.paintIcon(this, g, 0, 0);
/*     */         } private static final long serialVersionUID = 1L;
/*     */         public Dimension getPreferredSize() {
/*  77 */           return new Dimension(icon.getIconWidth(), icon.getIconHeight());
/*     */         }
/*     */         public Dimension getMinimumSize() {
/*  80 */           return getPreferredSize();
/*     */         }
/*     */         public Dimension getMaximumSize() {
/*  83 */           return getPreferredSize();
/*     */         }
/*     */       };
/*  86 */     this.dragImage.setFocusableWindowState(false);
/*  87 */     this.dragImage.setName("###overrideRedirect###");
/*  88 */     Icon dragIcon = new Icon() {
/*     */         public int getIconHeight() {
/*  90 */           return icon.getIconHeight();
/*     */         }
/*     */         public int getIconWidth() {
/*  93 */           return icon.getIconWidth();
/*     */         }
/*     */         public void paintIcon(Component c, Graphics g, int x, int y) {
/*  96 */           g = g.create();
/*  97 */           Area area = new Area(new Rectangle(x, y, getIconWidth(), getIconHeight()));
/*     */           
/*  99 */           area.subtract(new Area(new Rectangle(x + cursorOffset.x - 1, y + cursorOffset.y - 1, 3, 3)));
/* 100 */           g.setClip(area);
/* 101 */           icon.paintIcon(c, g, x, y);
/* 102 */           g.dispose();
/*     */         }
/*     */       };
/*     */     
/* 106 */     this.dragImage.pack();
/* 107 */     WindowUtils.setWindowMask(this.dragImage, dragIcon);
/* 108 */     WindowUtils.setWindowAlpha(this.dragImage, 0.5F);
/* 109 */     move(initialScreenLoc);
/* 110 */     this.dragImage.setVisible(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAlpha(float alpha) {
/* 117 */     WindowUtils.setWindowAlpha(this.dragImage, alpha);
/*     */   }
/*     */ 
/*     */   
/*     */   public void dispose() {
/* 122 */     this.dragImage.dispose();
/* 123 */     this.dragImage = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void move(Point screenLocation) {
/* 130 */     if (this.origin == null) {
/* 131 */       this.origin = screenLocation;
/*     */     }
/* 133 */     this.dragImage.setLocation(screenLocation.x, screenLocation.y);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void returnToOrigin() {
/* 139 */     final Timer timer = new Timer(33, null);
/* 140 */     timer.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent e) {
/* 142 */             Point location = GhostedDragImage.this.dragImage.getLocationOnScreen();
/* 143 */             Point dst = new Point(GhostedDragImage.this.origin);
/* 144 */             int dx = (dst.x - location.x) / 2;
/* 145 */             int dy = (dst.y - location.y) / 2;
/* 146 */             if (dx != 0 || dy != 0) {
/* 147 */               location.translate(dx, dy);
/* 148 */               GhostedDragImage.this.move(location);
/*     */             } else {
/*     */               
/* 151 */               timer.stop();
/* 152 */               GhostedDragImage.this.dispose();
/*     */             } 
/*     */           }
/*     */         });
/* 156 */     timer.setInitialDelay(0);
/* 157 */     timer.start();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\dnd\GhostedDragImage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */