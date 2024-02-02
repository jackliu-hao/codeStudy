/*      */ package com.sun.jna.platform;
/*      */ 
/*      */ import com.sun.jna.Memory;
/*      */ import com.sun.jna.Native;
/*      */ import com.sun.jna.NativeLong;
/*      */ import com.sun.jna.Platform;
/*      */ import com.sun.jna.Pointer;
/*      */ import com.sun.jna.platform.unix.X11;
/*      */ import com.sun.jna.platform.win32.GDI32;
/*      */ import com.sun.jna.platform.win32.Kernel32;
/*      */ import com.sun.jna.platform.win32.Psapi;
/*      */ import com.sun.jna.platform.win32.User32;
/*      */ import com.sun.jna.platform.win32.Win32Exception;
/*      */ import com.sun.jna.platform.win32.WinDef;
/*      */ import com.sun.jna.platform.win32.WinGDI;
/*      */ import com.sun.jna.platform.win32.WinNT;
/*      */ import com.sun.jna.platform.win32.WinUser;
/*      */ import com.sun.jna.ptr.ByteByReference;
/*      */ import com.sun.jna.ptr.IntByReference;
/*      */ import com.sun.jna.ptr.PointerByReference;
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.AlphaComposite;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dialog;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GraphicsDevice;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.AWTEventListener;
/*      */ import java.awt.event.ComponentEvent;
/*      */ import java.awt.event.ComponentListener;
/*      */ import java.awt.event.ContainerEvent;
/*      */ import java.awt.event.HierarchyEvent;
/*      */ import java.awt.event.HierarchyListener;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.geom.Area;
/*      */ import java.awt.geom.PathIterator;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.Raster;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JLayeredPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.RootPaneContainer;
/*      */ import javax.swing.SwingUtilities;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class WindowUtils
/*      */ {
/*  163 */   private static final Logger LOG = Logger.getLogger(WindowUtils.class.getName());
/*      */   
/*      */   private static final String TRANSPARENT_OLD_BG = "transparent-old-bg";
/*      */   
/*      */   private static final String TRANSPARENT_OLD_OPAQUE = "transparent-old-opaque";
/*      */   
/*      */   private static final String TRANSPARENT_ALPHA = "transparent-alpha";
/*  170 */   public static final Shape MASK_NONE = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class HeavyweightForcer
/*      */     extends Window
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final boolean packed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HeavyweightForcer(Window parent) {
/*  192 */       super(parent);
/*  193 */       pack();
/*  194 */       this.packed = true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isVisible() {
/*  202 */       return this.packed;
/*      */     }
/*      */ 
/*      */     
/*      */     public Rectangle getBounds() {
/*  207 */       return getOwner().getBounds();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected static class RepaintTrigger
/*      */     extends JComponent
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */     
/*      */     protected class Listener
/*      */       extends WindowAdapter
/*      */       implements ComponentListener, HierarchyListener, AWTEventListener
/*      */     {
/*      */       public void windowOpened(WindowEvent e) {
/*  223 */         WindowUtils.RepaintTrigger.this.repaint();
/*      */       }
/*      */ 
/*      */       
/*      */       public void componentHidden(ComponentEvent e) {}
/*      */ 
/*      */       
/*      */       public void componentMoved(ComponentEvent e) {}
/*      */ 
/*      */       
/*      */       public void componentResized(ComponentEvent e) {
/*  234 */         WindowUtils.RepaintTrigger.this.setSize(WindowUtils.RepaintTrigger.this.getParent().getSize());
/*  235 */         WindowUtils.RepaintTrigger.this.repaint();
/*      */       }
/*      */ 
/*      */       
/*      */       public void componentShown(ComponentEvent e) {
/*  240 */         WindowUtils.RepaintTrigger.this.repaint();
/*      */       }
/*      */ 
/*      */       
/*      */       public void hierarchyChanged(HierarchyEvent e) {
/*  245 */         WindowUtils.RepaintTrigger.this.repaint();
/*      */       }
/*      */ 
/*      */       
/*      */       public void eventDispatched(AWTEvent e) {
/*  250 */         if (e instanceof MouseEvent) {
/*  251 */           Component src = ((MouseEvent)e).getComponent();
/*  252 */           if (src != null && 
/*  253 */             SwingUtilities.isDescendingFrom(src, WindowUtils.RepaintTrigger.this.content)) {
/*  254 */             MouseEvent me = SwingUtilities.convertMouseEvent(src, (MouseEvent)e, WindowUtils.RepaintTrigger.this.content);
/*  255 */             Component c = SwingUtilities.getDeepestComponentAt(WindowUtils.RepaintTrigger.this.content, me.getX(), me.getY());
/*  256 */             if (c != null) {
/*  257 */               WindowUtils.RepaintTrigger.this.setCursor(c.getCursor());
/*      */             }
/*      */           } 
/*      */         } 
/*      */       }
/*      */     }
/*      */     
/*  264 */     private final Listener listener = createListener(); private final JComponent content;
/*      */     private Rectangle dirty;
/*      */     
/*      */     public RepaintTrigger(JComponent content) {
/*  268 */       this.content = content;
/*      */     }
/*      */ 
/*      */     
/*      */     public void addNotify() {
/*  273 */       super.addNotify();
/*  274 */       Window w = SwingUtilities.getWindowAncestor(this);
/*  275 */       setSize(getParent().getSize());
/*  276 */       w.addComponentListener(this.listener);
/*  277 */       w.addWindowListener(this.listener);
/*  278 */       Toolkit.getDefaultToolkit().addAWTEventListener(this.listener, 48L);
/*      */     }
/*      */ 
/*      */     
/*      */     public void removeNotify() {
/*  283 */       Toolkit.getDefaultToolkit().removeAWTEventListener(this.listener);
/*  284 */       Window w = SwingUtilities.getWindowAncestor(this);
/*  285 */       w.removeComponentListener(this.listener);
/*  286 */       w.removeWindowListener(this.listener);
/*  287 */       super.removeNotify();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected void paintComponent(Graphics g) {
/*  293 */       Rectangle bounds = g.getClipBounds();
/*  294 */       if (this.dirty == null || !this.dirty.contains(bounds)) {
/*  295 */         if (this.dirty == null) {
/*  296 */           this.dirty = bounds;
/*      */         } else {
/*      */           
/*  299 */           this.dirty = this.dirty.union(bounds);
/*      */         } 
/*  301 */         this.content.repaint(this.dirty);
/*      */       } else {
/*      */         
/*  304 */         this.dirty = null;
/*      */       } 
/*      */     }
/*      */     
/*      */     protected Listener createListener() {
/*  309 */       return new Listener();
/*      */     }
/*      */   }
/*      */   
/*      */   public static abstract class NativeWindowUtils {
/*      */     protected abstract class TransparentContentPane
/*      */       extends JPanel implements AWTEventListener {
/*      */       private static final long serialVersionUID = 1L;
/*      */       private boolean transparent;
/*      */       
/*      */       public TransparentContentPane(Container oldContent) {
/*  320 */         super(new BorderLayout());
/*  321 */         add(oldContent, "Center");
/*  322 */         setTransparent(true);
/*  323 */         if (oldContent instanceof JPanel) {
/*  324 */           ((JComponent)oldContent).setOpaque(false);
/*      */         }
/*      */       }
/*      */       
/*      */       public void addNotify() {
/*  329 */         super.addNotify();
/*  330 */         Toolkit.getDefaultToolkit().addAWTEventListener(this, 2L);
/*      */       }
/*      */       
/*      */       public void removeNotify() {
/*  334 */         Toolkit.getDefaultToolkit().removeAWTEventListener(this);
/*  335 */         super.removeNotify();
/*      */       }
/*      */       public void setTransparent(boolean transparent) {
/*  338 */         this.transparent = transparent;
/*  339 */         setOpaque(!transparent);
/*  340 */         setDoubleBuffered(!transparent);
/*  341 */         repaint();
/*      */       }
/*      */       
/*      */       public void eventDispatched(AWTEvent e) {
/*  345 */         if (e.getID() == 300 && 
/*  346 */           SwingUtilities.isDescendingFrom(((ContainerEvent)e).getChild(), this)) {
/*  347 */           Component child = ((ContainerEvent)e).getChild();
/*  348 */           WindowUtils.NativeWindowUtils.this.setDoubleBuffered(child, false);
/*      */         } 
/*      */       }
/*      */       
/*      */       public void paint(Graphics gr) {
/*  353 */         if (this.transparent) {
/*  354 */           Rectangle r = gr.getClipBounds();
/*  355 */           int w = r.width;
/*  356 */           int h = r.height;
/*  357 */           if (getWidth() > 0 && getHeight() > 0) {
/*  358 */             BufferedImage buf = new BufferedImage(w, h, 3);
/*      */ 
/*      */             
/*  361 */             Graphics2D g = buf.createGraphics();
/*  362 */             g.setComposite(AlphaComposite.Clear);
/*  363 */             g.fillRect(0, 0, w, h);
/*  364 */             g.dispose();
/*      */             
/*  366 */             g = buf.createGraphics();
/*  367 */             g.translate(-r.x, -r.y);
/*  368 */             super.paint(g);
/*  369 */             g.dispose();
/*      */             
/*  371 */             paintDirect(buf, r);
/*      */           } 
/*      */         } else {
/*      */           
/*  375 */           super.paint(gr);
/*      */         } 
/*      */       }
/*      */ 
/*      */       
/*      */       protected abstract void paintDirect(BufferedImage param2BufferedImage, Rectangle param2Rectangle);
/*      */     }
/*      */ 
/*      */     
/*      */     protected Window getWindow(Component c) {
/*  385 */       return (c instanceof Window) ? (Window)c : 
/*  386 */         SwingUtilities.getWindowAncestor(c);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void whenDisplayable(Component w, final Runnable action) {
/*  393 */       if (w.isDisplayable() && (!WindowUtils.Holder.requiresVisible || w.isVisible())) {
/*  394 */         action.run();
/*      */       }
/*  396 */       else if (WindowUtils.Holder.requiresVisible) {
/*  397 */         getWindow(w).addWindowListener(new WindowAdapter()
/*      */             {
/*      */               public void windowOpened(WindowEvent e) {
/*  400 */                 e.getWindow().removeWindowListener(this);
/*  401 */                 action.run();
/*      */               }
/*      */               
/*      */               public void windowClosed(WindowEvent e) {
/*  405 */                 e.getWindow().removeWindowListener(this);
/*      */               }
/*      */             });
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  412 */         w.addHierarchyListener(new HierarchyListener()
/*      */             {
/*      */               public void hierarchyChanged(HierarchyEvent e) {
/*  415 */                 if ((e.getChangeFlags() & 0x2L) != 0L && e
/*  416 */                   .getComponent().isDisplayable()) {
/*  417 */                   e.getComponent().removeHierarchyListener(this);
/*  418 */                   action.run();
/*      */                 } 
/*      */               }
/*      */             });
/*      */       } 
/*      */     }
/*      */     
/*      */     protected Raster toRaster(Shape mask) {
/*  426 */       Raster raster = null;
/*  427 */       if (mask != WindowUtils.MASK_NONE) {
/*  428 */         Rectangle bounds = mask.getBounds();
/*  429 */         if (bounds.width > 0 && bounds.height > 0) {
/*  430 */           BufferedImage clip = new BufferedImage(bounds.x + bounds.width, bounds.y + bounds.height, 12);
/*      */ 
/*      */ 
/*      */           
/*  434 */           Graphics2D g = clip.createGraphics();
/*  435 */           g.setColor(Color.black);
/*  436 */           g.fillRect(0, 0, bounds.x + bounds.width, bounds.y + bounds.height);
/*  437 */           g.setColor(Color.white);
/*  438 */           g.fill(mask);
/*  439 */           raster = clip.getRaster();
/*      */         } 
/*      */       } 
/*  442 */       return raster;
/*      */     }
/*      */     
/*      */     protected Raster toRaster(Component c, Icon mask) {
/*  446 */       Raster raster = null;
/*  447 */       if (mask != null) {
/*      */         
/*  449 */         Rectangle bounds = new Rectangle(0, 0, mask.getIconWidth(), mask.getIconHeight());
/*  450 */         BufferedImage clip = new BufferedImage(bounds.width, bounds.height, 2);
/*      */ 
/*      */         
/*  453 */         Graphics2D g = clip.createGraphics();
/*  454 */         g.setComposite(AlphaComposite.Clear);
/*  455 */         g.fillRect(0, 0, bounds.width, bounds.height);
/*  456 */         g.setComposite(AlphaComposite.SrcOver);
/*  457 */         mask.paintIcon(c, g, 0, 0);
/*  458 */         raster = clip.getAlphaRaster();
/*      */       } 
/*  460 */       return raster;
/*      */     }
/*      */     
/*      */     protected Shape toShape(Raster raster) {
/*  464 */       final Area area = new Area(new Rectangle(0, 0, 0, 0));
/*  465 */       RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput()
/*      */           {
/*      */             public boolean outputRange(int x, int y, int w, int h) {
/*  468 */               area.add(new Area(new Rectangle(x, y, w, h)));
/*  469 */               return true;
/*      */             }
/*      */           });
/*  472 */       return area;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setWindowAlpha(Window w, float alpha) {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isWindowAlphaSupported() {
/*  485 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
/*  491 */       GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*  492 */       GraphicsDevice dev = env.getDefaultScreenDevice();
/*  493 */       return dev.getDefaultConfiguration();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setWindowTransparent(Window w, boolean transparent) {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void setDoubleBuffered(Component root, boolean buffered) {
/*  506 */       if (root instanceof JComponent) {
/*  507 */         ((JComponent)root).setDoubleBuffered(buffered);
/*      */       }
/*  509 */       if (root instanceof JRootPane && buffered) {
/*  510 */         ((JRootPane)root).setDoubleBuffered(true);
/*      */       }
/*  512 */       else if (root instanceof Container) {
/*  513 */         Component[] kids = ((Container)root).getComponents();
/*  514 */         for (int i = 0; i < kids.length; i++) {
/*  515 */           setDoubleBuffered(kids[i], buffered);
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     protected void setLayersTransparent(Window w, boolean transparent) {
/*  522 */       Color bg = transparent ? new Color(0, 0, 0, 0) : null;
/*  523 */       if (w instanceof RootPaneContainer) {
/*  524 */         RootPaneContainer rpc = (RootPaneContainer)w;
/*  525 */         JRootPane root = rpc.getRootPane();
/*  526 */         JLayeredPane lp = root.getLayeredPane();
/*  527 */         Container c = root.getContentPane();
/*  528 */         JComponent content = (c instanceof JComponent) ? (JComponent)c : null;
/*      */         
/*  530 */         if (transparent) {
/*  531 */           lp.putClientProperty("transparent-old-opaque", Boolean.valueOf(lp.isOpaque()));
/*  532 */           lp.setOpaque(false);
/*  533 */           root.putClientProperty("transparent-old-opaque", Boolean.valueOf(root.isOpaque()));
/*  534 */           root.setOpaque(false);
/*  535 */           if (content != null) {
/*  536 */             content.putClientProperty("transparent-old-opaque", Boolean.valueOf(content.isOpaque()));
/*  537 */             content.setOpaque(false);
/*      */           } 
/*  539 */           root.putClientProperty("transparent-old-bg", root
/*  540 */               .getParent().getBackground());
/*      */         } else {
/*      */           
/*  543 */           lp.setOpaque(Boolean.TRUE.equals(lp.getClientProperty("transparent-old-opaque")));
/*  544 */           lp.putClientProperty("transparent-old-opaque", (Object)null);
/*  545 */           root.setOpaque(Boolean.TRUE.equals(root.getClientProperty("transparent-old-opaque")));
/*  546 */           root.putClientProperty("transparent-old-opaque", (Object)null);
/*  547 */           if (content != null) {
/*  548 */             content.setOpaque(Boolean.TRUE.equals(content.getClientProperty("transparent-old-opaque")));
/*  549 */             content.putClientProperty("transparent-old-opaque", (Object)null);
/*      */           } 
/*  551 */           bg = (Color)root.getClientProperty("transparent-old-bg");
/*  552 */           root.putClientProperty("transparent-old-bg", (Object)null);
/*      */         } 
/*      */       } 
/*  555 */       w.setBackground(bg);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void setMask(Component c, Raster raster) {
/*  562 */       throw new UnsupportedOperationException("Window masking is not available");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void setWindowMask(Component w, Raster raster) {
/*  571 */       if (w.isLightweight())
/*  572 */         throw new IllegalArgumentException("Component must be heavyweight: " + w); 
/*  573 */       setMask(w, raster);
/*      */     }
/*      */ 
/*      */     
/*      */     public void setWindowMask(Component w, Shape mask) {
/*  578 */       setWindowMask(w, toRaster(mask));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setWindowMask(Component w, Icon mask) {
/*  586 */       setWindowMask(w, toRaster(w, mask));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void setForceHeavyweightPopups(Window w, boolean force) {
/*  595 */       if (!(w instanceof WindowUtils.HeavyweightForcer)) {
/*  596 */         Window[] owned = w.getOwnedWindows();
/*  597 */         for (int i = 0; i < owned.length; i++) {
/*  598 */           if (owned[i] instanceof WindowUtils.HeavyweightForcer) {
/*  599 */             if (force)
/*      */               return; 
/*  601 */             owned[i].dispose();
/*      */           } 
/*      */         } 
/*  604 */         Boolean b = Boolean.valueOf(System.getProperty("jna.force_hw_popups", "true"));
/*  605 */         if (force && b.booleanValue()) {
/*  606 */           new WindowUtils.HeavyweightForcer(w);
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected BufferedImage getWindowIcon(WinDef.HWND hwnd) {
/*  625 */       throw new UnsupportedOperationException("This platform is not supported, yet.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Dimension getIconSize(WinDef.HICON hIcon) {
/*  641 */       throw new UnsupportedOperationException("This platform is not supported, yet.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected List<DesktopWindow> getAllWindows(boolean onlyVisibleWindows) {
/*  662 */       throw new UnsupportedOperationException("This platform is not supported, yet.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected String getWindowTitle(WinDef.HWND hwnd) {
/*  678 */       throw new UnsupportedOperationException("This platform is not supported, yet.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected String getProcessFilePath(WinDef.HWND hwnd) {
/*  695 */       throw new UnsupportedOperationException("This platform is not supported, yet.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Rectangle getWindowLocationAndSize(WinDef.HWND hwnd) {
/*  710 */       throw new UnsupportedOperationException("This platform is not supported, yet.");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static class Holder
/*      */   {
/*      */     public static boolean requiresVisible;
/*      */     
/*      */     public static final WindowUtils.NativeWindowUtils INSTANCE;
/*      */ 
/*      */     
/*      */     static {
/*  723 */       if (Platform.isWindows()) {
/*  724 */         INSTANCE = new WindowUtils.W32WindowUtils();
/*      */       }
/*  726 */       else if (Platform.isMac()) {
/*  727 */         INSTANCE = new WindowUtils.MacWindowUtils();
/*      */       }
/*  729 */       else if (Platform.isX11()) {
/*  730 */         INSTANCE = new WindowUtils.X11WindowUtils();
/*      */         
/*  732 */         requiresVisible = System.getProperty("java.version").matches("^1\\.4\\..*");
/*      */       } else {
/*      */         
/*  735 */         String os = System.getProperty("os.name");
/*  736 */         throw new UnsupportedOperationException("No support for " + os);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private static NativeWindowUtils getInstance() {
/*  742 */     return Holder.INSTANCE;
/*      */   }
/*      */   private static class W32WindowUtils extends NativeWindowUtils { private W32WindowUtils() {}
/*      */     
/*      */     private WinDef.HWND getHWnd(Component w) {
/*  747 */       WinDef.HWND hwnd = new WinDef.HWND();
/*  748 */       hwnd.setPointer(Native.getComponentPointer(w));
/*  749 */       return hwnd;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isWindowAlphaSupported() {
/*  758 */       return Boolean.getBoolean("sun.java2d.noddraw");
/*      */     }
/*      */ 
/*      */     
/*      */     private boolean usingUpdateLayeredWindow(Window w) {
/*  763 */       if (w instanceof RootPaneContainer) {
/*  764 */         JRootPane root = ((RootPaneContainer)w).getRootPane();
/*  765 */         return (root.getClientProperty("transparent-old-bg") != null);
/*      */       } 
/*  767 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void storeAlpha(Window w, byte alpha) {
/*  774 */       if (w instanceof RootPaneContainer) {
/*  775 */         JRootPane root = ((RootPaneContainer)w).getRootPane();
/*  776 */         Byte b = (alpha == -1) ? null : Byte.valueOf(alpha);
/*  777 */         root.putClientProperty("transparent-alpha", b);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private byte getAlpha(Window w) {
/*  783 */       if (w instanceof RootPaneContainer) {
/*  784 */         JRootPane root = ((RootPaneContainer)w).getRootPane();
/*  785 */         Byte b = (Byte)root.getClientProperty("transparent-alpha");
/*  786 */         if (b != null) {
/*  787 */           return b.byteValue();
/*      */         }
/*      */       } 
/*  790 */       return -1;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setWindowAlpha(final Window w, final float alpha) {
/*  795 */       if (!isWindowAlphaSupported()) {
/*  796 */         throw new UnsupportedOperationException("Set sun.java2d.noddraw=true to enable transparent windows");
/*      */       }
/*  798 */       whenDisplayable(w, new Runnable()
/*      */           {
/*      */             public void run() {
/*  801 */               WinDef.HWND hWnd = WindowUtils.W32WindowUtils.this.getHWnd(w);
/*  802 */               User32 user = User32.INSTANCE;
/*  803 */               int flags = user.GetWindowLong(hWnd, -20);
/*  804 */               byte level = (byte)((int)(255.0F * alpha) & 0xFF);
/*  805 */               if (WindowUtils.W32WindowUtils.this.usingUpdateLayeredWindow(w)) {
/*      */ 
/*      */                 
/*  808 */                 WinUser.BLENDFUNCTION blend = new WinUser.BLENDFUNCTION();
/*  809 */                 blend.SourceConstantAlpha = level;
/*  810 */                 blend.AlphaFormat = 1;
/*  811 */                 user.UpdateLayeredWindow(hWnd, null, null, null, null, null, 0, blend, 2);
/*      */ 
/*      */               
/*      */               }
/*  815 */               else if (alpha == 1.0F) {
/*  816 */                 flags &= 0xFFF7FFFF;
/*  817 */                 user.SetWindowLong(hWnd, -20, flags);
/*      */               } else {
/*      */                 
/*  820 */                 flags |= 0x80000;
/*  821 */                 user.SetWindowLong(hWnd, -20, flags);
/*  822 */                 user.SetLayeredWindowAttributes(hWnd, 0, level, 2);
/*      */               } 
/*      */               
/*  825 */               WindowUtils.W32WindowUtils.this.setForceHeavyweightPopups(w, (alpha != 1.0F));
/*  826 */               WindowUtils.W32WindowUtils.this.storeAlpha(w, level);
/*      */             }
/*      */           });
/*      */     }
/*      */ 
/*      */     
/*      */     private class W32TransparentContentPane
/*      */       extends WindowUtils.NativeWindowUtils.TransparentContentPane
/*      */     {
/*      */       private static final long serialVersionUID = 1L;
/*      */       private WinDef.HDC memDC;
/*      */       private WinDef.HBITMAP hBitmap;
/*      */       private Pointer pbits;
/*      */       private Dimension bitmapSize;
/*      */       
/*      */       public W32TransparentContentPane(Container content) {
/*  842 */         super(content);
/*      */       }
/*      */       private void disposeBackingStore() {
/*  845 */         GDI32 gdi = GDI32.INSTANCE;
/*  846 */         if (this.hBitmap != null) {
/*  847 */           gdi.DeleteObject((WinNT.HANDLE)this.hBitmap);
/*  848 */           this.hBitmap = null;
/*      */         } 
/*  850 */         if (this.memDC != null) {
/*  851 */           gdi.DeleteDC(this.memDC);
/*  852 */           this.memDC = null;
/*      */         } 
/*      */       }
/*      */       
/*      */       public void removeNotify() {
/*  857 */         super.removeNotify();
/*  858 */         disposeBackingStore();
/*      */       }
/*      */       
/*      */       public void setTransparent(boolean transparent) {
/*  862 */         super.setTransparent(transparent);
/*  863 */         if (!transparent) {
/*  864 */           disposeBackingStore();
/*      */         }
/*      */       }
/*      */ 
/*      */       
/*      */       protected void paintDirect(BufferedImage buf, Rectangle bounds) {
/*  870 */         Window win = SwingUtilities.getWindowAncestor(this);
/*  871 */         GDI32 gdi = GDI32.INSTANCE;
/*  872 */         User32 user = User32.INSTANCE;
/*  873 */         int x = bounds.x;
/*  874 */         int y = bounds.y;
/*  875 */         Point origin = SwingUtilities.convertPoint(this, x, y, win);
/*  876 */         int w = bounds.width;
/*  877 */         int h = bounds.height;
/*  878 */         int ww = win.getWidth();
/*  879 */         int wh = win.getHeight();
/*  880 */         WinDef.HDC screenDC = user.GetDC(null);
/*  881 */         WinNT.HANDLE oldBitmap = null;
/*      */         try {
/*  883 */           if (this.memDC == null) {
/*  884 */             this.memDC = gdi.CreateCompatibleDC(screenDC);
/*      */           }
/*  886 */           if (this.hBitmap == null || !win.getSize().equals(this.bitmapSize)) {
/*  887 */             if (this.hBitmap != null) {
/*  888 */               gdi.DeleteObject((WinNT.HANDLE)this.hBitmap);
/*  889 */               this.hBitmap = null;
/*      */             } 
/*  891 */             WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
/*  892 */             bmi.bmiHeader.biWidth = ww;
/*  893 */             bmi.bmiHeader.biHeight = wh;
/*  894 */             bmi.bmiHeader.biPlanes = 1;
/*  895 */             bmi.bmiHeader.biBitCount = 32;
/*  896 */             bmi.bmiHeader.biCompression = 0;
/*  897 */             bmi.bmiHeader.biSizeImage = ww * wh * 4;
/*  898 */             PointerByReference ppbits = new PointerByReference();
/*  899 */             this.hBitmap = gdi.CreateDIBSection(this.memDC, bmi, 0, ppbits, null, 0);
/*      */ 
/*      */             
/*  902 */             this.pbits = ppbits.getValue();
/*  903 */             this.bitmapSize = new Dimension(ww, wh);
/*      */           } 
/*  905 */           oldBitmap = gdi.SelectObject(this.memDC, (WinNT.HANDLE)this.hBitmap);
/*  906 */           Raster raster = buf.getData();
/*  907 */           int[] pixel = new int[4];
/*  908 */           int[] bits = new int[w];
/*  909 */           for (int row = 0; row < h; row++) {
/*  910 */             for (int col = 0; col < w; col++) {
/*  911 */               raster.getPixel(col, row, pixel);
/*  912 */               int alpha = (pixel[3] & 0xFF) << 24;
/*  913 */               int red = pixel[2] & 0xFF;
/*  914 */               int green = (pixel[1] & 0xFF) << 8;
/*  915 */               int blue = (pixel[0] & 0xFF) << 16;
/*  916 */               bits[col] = alpha | red | green | blue;
/*      */             } 
/*  918 */             int v = wh - origin.y + row - 1;
/*  919 */             this.pbits.write(((v * ww + origin.x) * 4), bits, 0, bits.length);
/*      */           } 
/*  921 */           WinUser.SIZE winSize = new WinUser.SIZE();
/*  922 */           winSize.cx = win.getWidth();
/*  923 */           winSize.cy = win.getHeight();
/*  924 */           WinDef.POINT winLoc = new WinDef.POINT();
/*  925 */           winLoc.x = win.getX();
/*  926 */           winLoc.y = win.getY();
/*  927 */           WinDef.POINT srcLoc = new WinDef.POINT();
/*  928 */           WinUser.BLENDFUNCTION blend = new WinUser.BLENDFUNCTION();
/*  929 */           WinDef.HWND hWnd = WindowUtils.W32WindowUtils.this.getHWnd(win);
/*      */           
/*  931 */           ByteByReference bref = new ByteByReference();
/*  932 */           IntByReference iref = new IntByReference();
/*  933 */           byte level = WindowUtils.W32WindowUtils.this.getAlpha(win);
/*      */           
/*      */           try {
/*  936 */             if (user.GetLayeredWindowAttributes(hWnd, null, bref, iref) && (iref
/*  937 */               .getValue() & 0x2) != 0) {
/*  938 */               level = bref.getValue();
/*      */             }
/*      */           }
/*  941 */           catch (UnsatisfiedLinkError unsatisfiedLinkError) {}
/*      */           
/*  943 */           blend.SourceConstantAlpha = level;
/*  944 */           blend.AlphaFormat = 1;
/*  945 */           user.UpdateLayeredWindow(hWnd, screenDC, winLoc, winSize, this.memDC, srcLoc, 0, blend, 2);
/*      */         } finally {
/*      */           
/*  948 */           user.ReleaseDC(null, screenDC);
/*  949 */           if (this.memDC != null && oldBitmap != null) {
/*  950 */             gdi.SelectObject(this.memDC, oldBitmap);
/*      */           }
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setWindowTransparent(final Window w, final boolean transparent) {
/*  962 */       if (!(w instanceof RootPaneContainer)) {
/*  963 */         throw new IllegalArgumentException("Window must be a RootPaneContainer");
/*      */       }
/*  965 */       if (!isWindowAlphaSupported()) {
/*  966 */         throw new UnsupportedOperationException("Set sun.java2d.noddraw=true to enable transparent windows");
/*      */       }
/*      */       
/*  969 */       boolean isTransparent = (w.getBackground() != null && w.getBackground().getAlpha() == 0);
/*  970 */       if (transparent == isTransparent)
/*      */         return; 
/*  972 */       whenDisplayable(w, new Runnable()
/*      */           {
/*      */             public void run() {
/*  975 */               User32 user = User32.INSTANCE;
/*  976 */               WinDef.HWND hWnd = WindowUtils.W32WindowUtils.this.getHWnd(w);
/*  977 */               int flags = user.GetWindowLong(hWnd, -20);
/*  978 */               JRootPane root = ((RootPaneContainer)w).getRootPane();
/*  979 */               JLayeredPane lp = root.getLayeredPane();
/*  980 */               Container content = root.getContentPane();
/*  981 */               if (content instanceof WindowUtils.W32WindowUtils.W32TransparentContentPane) {
/*  982 */                 ((WindowUtils.W32WindowUtils.W32TransparentContentPane)content).setTransparent(transparent);
/*      */               }
/*  984 */               else if (transparent) {
/*  985 */                 WindowUtils.W32WindowUtils.W32TransparentContentPane w32content = new WindowUtils.W32WindowUtils.W32TransparentContentPane(content);
/*      */                 
/*  987 */                 root.setContentPane(w32content);
/*  988 */                 lp.add(new WindowUtils.RepaintTrigger(w32content), JLayeredPane.DRAG_LAYER);
/*      */               } 
/*      */               
/*  991 */               if (transparent && !WindowUtils.W32WindowUtils.this.usingUpdateLayeredWindow(w)) {
/*  992 */                 flags |= 0x80000;
/*  993 */                 user.SetWindowLong(hWnd, -20, flags);
/*      */               }
/*  995 */               else if (!transparent && WindowUtils.W32WindowUtils.this.usingUpdateLayeredWindow(w)) {
/*  996 */                 flags &= 0xFFF7FFFF;
/*  997 */                 user.SetWindowLong(hWnd, -20, flags);
/*      */               } 
/*  999 */               WindowUtils.W32WindowUtils.this.setLayersTransparent(w, transparent);
/* 1000 */               WindowUtils.W32WindowUtils.this.setForceHeavyweightPopups(w, transparent);
/* 1001 */               WindowUtils.W32WindowUtils.this.setDoubleBuffered(w, !transparent);
/*      */             }
/*      */           });
/*      */     }
/*      */ 
/*      */     
/*      */     public void setWindowMask(Component w, Shape mask) {
/* 1008 */       if (mask instanceof Area && ((Area)mask).isPolygonal()) {
/* 1009 */         setMask(w, (Area)mask);
/*      */       } else {
/*      */         
/* 1012 */         super.setWindowMask(w, mask);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private void setWindowRegion(final Component w, final WinDef.HRGN hrgn) {
/* 1018 */       whenDisplayable(w, new Runnable()
/*      */           {
/*      */             public void run() {
/* 1021 */               GDI32 gdi = GDI32.INSTANCE;
/* 1022 */               User32 user = User32.INSTANCE;
/* 1023 */               WinDef.HWND hWnd = WindowUtils.W32WindowUtils.this.getHWnd(w);
/*      */               try {
/* 1025 */                 user.SetWindowRgn(hWnd, hrgn, true);
/* 1026 */                 WindowUtils.W32WindowUtils.this.setForceHeavyweightPopups(WindowUtils.W32WindowUtils.this.getWindow(w), (hrgn != null));
/*      */               } finally {
/*      */                 
/* 1029 */                 gdi.DeleteObject((WinNT.HANDLE)hrgn);
/*      */               } 
/*      */             }
/*      */           });
/*      */     }
/*      */ 
/*      */     
/*      */     private void setMask(Component w, Area area) {
/* 1037 */       GDI32 gdi = GDI32.INSTANCE;
/* 1038 */       PathIterator pi = area.getPathIterator(null);
/* 1039 */       int mode = (pi.getWindingRule() == 1) ? 2 : 1;
/*      */       
/* 1041 */       float[] coords = new float[6];
/* 1042 */       List<WinDef.POINT> points = new ArrayList<WinDef.POINT>();
/* 1043 */       int size = 0;
/* 1044 */       List<Integer> sizes = new ArrayList<Integer>();
/* 1045 */       while (!pi.isDone()) {
/* 1046 */         int type = pi.currentSegment(coords);
/* 1047 */         if (type == 0) {
/* 1048 */           size = 1;
/* 1049 */           points.add(new WinDef.POINT((int)coords[0], (int)coords[1]));
/*      */         }
/* 1051 */         else if (type == 1) {
/* 1052 */           size++;
/* 1053 */           points.add(new WinDef.POINT((int)coords[0], (int)coords[1]));
/*      */         }
/* 1055 */         else if (type == 4) {
/* 1056 */           sizes.add(Integer.valueOf(size));
/*      */         } else {
/*      */           
/* 1059 */           throw new RuntimeException("Area is not polygonal: " + area);
/*      */         } 
/* 1061 */         pi.next();
/*      */       } 
/* 1063 */       WinDef.POINT[] lppt = (WinDef.POINT[])(new WinDef.POINT()).toArray(points.size());
/* 1064 */       WinDef.POINT[] pts = points.<WinDef.POINT>toArray(new WinDef.POINT[points.size()]);
/* 1065 */       for (int i = 0; i < lppt.length; i++) {
/* 1066 */         (lppt[i]).x = (pts[i]).x;
/* 1067 */         (lppt[i]).y = (pts[i]).y;
/*      */       } 
/* 1069 */       int[] counts = new int[sizes.size()];
/* 1070 */       for (int j = 0; j < counts.length; j++) {
/* 1071 */         counts[j] = ((Integer)sizes.get(j)).intValue();
/*      */       }
/* 1073 */       WinDef.HRGN hrgn = gdi.CreatePolyPolygonRgn(lppt, counts, counts.length, mode);
/* 1074 */       setWindowRegion(w, hrgn);
/*      */     }
/*      */ 
/*      */     
/*      */     protected void setMask(Component w, Raster raster) {
/* 1079 */       GDI32 gdi = GDI32.INSTANCE;
/*      */       
/* 1081 */       final WinDef.HRGN region = (raster != null) ? gdi.CreateRectRgn(0, 0, 0, 0) : null;
/* 1082 */       if (region != null) {
/* 1083 */         final WinDef.HRGN tempRgn = gdi.CreateRectRgn(0, 0, 0, 0);
/*      */         try {
/* 1085 */           RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput()
/*      */               {
/*      */                 public boolean outputRange(int x, int y, int w, int h) {
/* 1088 */                   GDI32 gdi = GDI32.INSTANCE;
/* 1089 */                   gdi.SetRectRgn(tempRgn, x, y, x + w, y + h);
/* 1090 */                   return (gdi.CombineRgn(region, region, tempRgn, 2) != 0);
/*      */                 }
/*      */               });
/*      */         } finally {
/*      */           
/* 1095 */           gdi.DeleteObject((WinNT.HANDLE)tempRgn);
/*      */         } 
/*      */       } 
/* 1098 */       setWindowRegion(w, region);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BufferedImage getWindowIcon(WinDef.HWND hwnd) {
/* 1104 */       WinDef.DWORDByReference hIconNumber = new WinDef.DWORDByReference();
/*      */       
/* 1106 */       WinDef.LRESULT result = User32.INSTANCE.SendMessageTimeout(hwnd, 127, new WinDef.WPARAM(1L), new WinDef.LPARAM(0L), 2, 500, hIconNumber);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1111 */       if (result.intValue() == 0)
/*      */       {
/* 1113 */         result = User32.INSTANCE.SendMessageTimeout(hwnd, 127, new WinDef.WPARAM(0L), new WinDef.LPARAM(0L), 2, 500, hIconNumber);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1118 */       if (result.intValue() == 0)
/*      */       {
/* 1120 */         result = User32.INSTANCE.SendMessageTimeout(hwnd, 127, new WinDef.WPARAM(2L), new WinDef.LPARAM(0L), 2, 500, hIconNumber);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1125 */       if (result.intValue() == 0) {
/*      */ 
/*      */         
/* 1128 */         result = new WinDef.LRESULT(User32.INSTANCE.GetClassLongPtr(hwnd, -14).intValue());
/* 1129 */         hIconNumber.getValue().setValue(result.intValue());
/*      */       } 
/* 1131 */       if (result.intValue() == 0) {
/*      */ 
/*      */         
/* 1134 */         result = new WinDef.LRESULT(User32.INSTANCE.GetClassLongPtr(hwnd, -34).intValue());
/* 1135 */         hIconNumber.getValue().setValue(result.intValue());
/*      */       } 
/* 1137 */       if (result.intValue() == 0) {
/* 1138 */         return null;
/*      */       }
/*      */ 
/*      */       
/* 1142 */       WinDef.HICON hIcon = new WinDef.HICON(new Pointer(hIconNumber.getValue().longValue()));
/* 1143 */       Dimension iconSize = getIconSize(hIcon);
/* 1144 */       if (iconSize.width == 0 || iconSize.height == 0) {
/* 1145 */         return null;
/*      */       }
/* 1147 */       int width = iconSize.width;
/* 1148 */       int height = iconSize.height;
/* 1149 */       short depth = 24;
/*      */       
/* 1151 */       byte[] lpBitsColor = new byte[width * height * 24 / 8];
/* 1152 */       Memory memory1 = new Memory(lpBitsColor.length);
/* 1153 */       byte[] lpBitsMask = new byte[width * height * 24 / 8];
/* 1154 */       Memory memory2 = new Memory(lpBitsMask.length);
/* 1155 */       WinGDI.BITMAPINFO bitmapInfo = new WinGDI.BITMAPINFO();
/* 1156 */       WinGDI.BITMAPINFOHEADER hdr = new WinGDI.BITMAPINFOHEADER();
/*      */       
/* 1158 */       bitmapInfo.bmiHeader = hdr;
/* 1159 */       hdr.biWidth = width;
/* 1160 */       hdr.biHeight = height;
/* 1161 */       hdr.biPlanes = 1;
/* 1162 */       hdr.biBitCount = 24;
/* 1163 */       hdr.biCompression = 0;
/* 1164 */       hdr.write();
/* 1165 */       bitmapInfo.write();
/*      */       
/* 1167 */       WinDef.HDC hDC = User32.INSTANCE.GetDC(null);
/* 1168 */       WinGDI.ICONINFO iconInfo = new WinGDI.ICONINFO();
/* 1169 */       User32.INSTANCE.GetIconInfo(hIcon, iconInfo);
/* 1170 */       iconInfo.read();
/* 1171 */       GDI32.INSTANCE.GetDIBits(hDC, iconInfo.hbmColor, 0, height, (Pointer)memory1, bitmapInfo, 0);
/*      */       
/* 1173 */       memory1.read(0L, lpBitsColor, 0, lpBitsColor.length);
/* 1174 */       GDI32.INSTANCE.GetDIBits(hDC, iconInfo.hbmMask, 0, height, (Pointer)memory2, bitmapInfo, 0);
/*      */       
/* 1176 */       memory2.read(0L, lpBitsMask, 0, lpBitsMask.length);
/* 1177 */       BufferedImage image = new BufferedImage(width, height, 2);
/*      */ 
/*      */ 
/*      */       
/* 1181 */       int x = 0, y = height - 1; int i;
/* 1182 */       for (i = 0; i < lpBitsColor.length; i += 3) {
/* 1183 */         int b = lpBitsColor[i] & 0xFF;
/* 1184 */         int g = lpBitsColor[i + 1] & 0xFF;
/* 1185 */         int r = lpBitsColor[i + 2] & 0xFF;
/* 1186 */         int a = 255 - lpBitsMask[i] & 0xFF;
/* 1187 */         int argb = a << 24 | r << 16 | g << 8 | b;
/* 1188 */         image.setRGB(x, y, argb);
/* 1189 */         x = (x + 1) % width;
/* 1190 */         if (x == 0) {
/* 1191 */           y--;
/*      */         }
/*      */       } 
/* 1194 */       User32.INSTANCE.ReleaseDC(null, hDC);
/*      */       
/* 1196 */       return image;
/*      */     }
/*      */ 
/*      */     
/*      */     public Dimension getIconSize(WinDef.HICON hIcon) {
/* 1201 */       WinGDI.ICONINFO iconInfo = new WinGDI.ICONINFO();
/*      */       try {
/* 1203 */         if (!User32.INSTANCE.GetIconInfo(hIcon, iconInfo))
/* 1204 */           return new Dimension(); 
/* 1205 */         iconInfo.read();
/*      */         
/* 1207 */         WinGDI.BITMAP bmp = new WinGDI.BITMAP();
/* 1208 */         if (iconInfo.hbmColor != null && iconInfo.hbmColor
/* 1209 */           .getPointer() != Pointer.NULL) {
/* 1210 */           int nWrittenBytes = GDI32.INSTANCE.GetObject((WinNT.HANDLE)iconInfo.hbmColor, bmp
/* 1211 */               .size(), bmp.getPointer());
/* 1212 */           bmp.read();
/* 1213 */           if (nWrittenBytes > 0)
/* 1214 */             return new Dimension(bmp.bmWidth.intValue(), bmp.bmHeight
/* 1215 */                 .intValue()); 
/* 1216 */         } else if (iconInfo.hbmMask != null && iconInfo.hbmMask
/* 1217 */           .getPointer() != Pointer.NULL) {
/* 1218 */           int nWrittenBytes = GDI32.INSTANCE.GetObject((WinNT.HANDLE)iconInfo.hbmMask, bmp
/* 1219 */               .size(), bmp.getPointer());
/* 1220 */           bmp.read();
/* 1221 */           if (nWrittenBytes > 0)
/* 1222 */             return new Dimension(bmp.bmWidth.intValue(), bmp.bmHeight.intValue() / 2); 
/*      */         } 
/*      */       } finally {
/* 1225 */         if (iconInfo.hbmColor != null && iconInfo.hbmColor
/* 1226 */           .getPointer() != Pointer.NULL)
/* 1227 */           GDI32.INSTANCE.DeleteObject((WinNT.HANDLE)iconInfo.hbmColor); 
/* 1228 */         if (iconInfo.hbmMask != null && iconInfo.hbmMask
/* 1229 */           .getPointer() != Pointer.NULL) {
/* 1230 */           GDI32.INSTANCE.DeleteObject((WinNT.HANDLE)iconInfo.hbmMask);
/*      */         }
/*      */       } 
/* 1233 */       return new Dimension();
/*      */     }
/*      */ 
/*      */     
/*      */     public List<DesktopWindow> getAllWindows(final boolean onlyVisibleWindows) {
/* 1238 */       final List<DesktopWindow> result = new LinkedList<DesktopWindow>();
/*      */       
/* 1240 */       WinUser.WNDENUMPROC lpEnumFunc = new WinUser.WNDENUMPROC()
/*      */         {
/*      */           public boolean callback(WinDef.HWND hwnd, Pointer arg1)
/*      */           {
/*      */             try {
/* 1245 */               boolean visible = (!onlyVisibleWindows || User32.INSTANCE.IsWindowVisible(hwnd));
/* 1246 */               if (visible) {
/* 1247 */                 String title = WindowUtils.W32WindowUtils.this.getWindowTitle(hwnd);
/* 1248 */                 String filePath = WindowUtils.W32WindowUtils.this.getProcessFilePath(hwnd);
/* 1249 */                 Rectangle locAndSize = WindowUtils.W32WindowUtils.this.getWindowLocationAndSize(hwnd);
/* 1250 */                 result.add(new DesktopWindow(hwnd, title, filePath, locAndSize));
/*      */               }
/*      */             
/* 1253 */             } catch (Exception e) {
/*      */               
/* 1255 */               e.printStackTrace();
/*      */             } 
/*      */             
/* 1258 */             return true;
/*      */           }
/*      */         };
/*      */       
/* 1262 */       if (!User32.INSTANCE.EnumWindows(lpEnumFunc, null)) {
/* 1263 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */       }
/* 1265 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String getWindowTitle(WinDef.HWND hwnd) {
/* 1271 */       int requiredLength = User32.INSTANCE.GetWindowTextLength(hwnd) + 1;
/* 1272 */       char[] title = new char[requiredLength];
/* 1273 */       int length = User32.INSTANCE.GetWindowText(hwnd, title, title.length);
/*      */ 
/*      */       
/* 1276 */       return Native.toString(Arrays.copyOfRange(title, 0, length));
/*      */     }
/*      */ 
/*      */     
/*      */     public String getProcessFilePath(WinDef.HWND hwnd) {
/* 1281 */       char[] filePath = new char[2048];
/* 1282 */       IntByReference pid = new IntByReference();
/* 1283 */       User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);
/*      */       
/* 1285 */       WinNT.HANDLE process = Kernel32.INSTANCE.OpenProcess(1040, false, pid
/* 1286 */           .getValue());
/* 1287 */       if (process == null) {
/* 1288 */         if (Kernel32.INSTANCE.GetLastError() != 5) {
/* 1289 */           throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */         }
/*      */         
/* 1292 */         return "";
/*      */       } 
/*      */ 
/*      */       
/*      */       try {
/* 1297 */         int length = Psapi.INSTANCE.GetModuleFileNameExW(process, null, filePath, filePath.length);
/*      */         
/* 1299 */         if (length == 0) {
/* 1300 */           if (Kernel32.INSTANCE.GetLastError() != 6) {
/* 1301 */             throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */           }
/*      */           
/* 1304 */           return "";
/*      */         } 
/*      */         
/* 1307 */         return Native.toString(filePath).trim();
/*      */       } finally {
/* 1309 */         Kernel32.INSTANCE.CloseHandle(process);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public Rectangle getWindowLocationAndSize(WinDef.HWND hwnd) {
/* 1315 */       WinDef.RECT lpRect = new WinDef.RECT();
/* 1316 */       if (!User32.INSTANCE.GetWindowRect(hwnd, lpRect)) {
/* 1317 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */       }
/* 1319 */       return new Rectangle(lpRect.left, lpRect.top, Math.abs(lpRect.right - lpRect.left), 
/* 1320 */           Math.abs(lpRect.bottom - lpRect.top));
/*      */     } }
/*      */   
/*      */   private static class MacWindowUtils extends NativeWindowUtils {
/*      */     private static final String WDRAG = "apple.awt.draggableWindowBackground";
/*      */     
/*      */     public boolean isWindowAlphaSupported() {
/* 1327 */       return true;
/*      */     }
/*      */     private MacWindowUtils() {}
/*      */     private OSXMaskingContentPane installMaskingPane(Window w) {
/*      */       OSXMaskingContentPane content;
/* 1332 */       if (w instanceof RootPaneContainer) {
/*      */         
/* 1334 */         RootPaneContainer rpc = (RootPaneContainer)w;
/* 1335 */         Container oldContent = rpc.getContentPane();
/* 1336 */         if (oldContent instanceof OSXMaskingContentPane) {
/* 1337 */           content = (OSXMaskingContentPane)oldContent;
/*      */         } else {
/*      */           
/* 1340 */           content = new OSXMaskingContentPane(oldContent);
/*      */           
/* 1342 */           rpc.setContentPane(content);
/*      */         } 
/*      */       } else {
/*      */         
/* 1346 */         Component oldContent = (w.getComponentCount() > 0) ? w.getComponent(0) : null;
/* 1347 */         if (oldContent instanceof OSXMaskingContentPane) {
/* 1348 */           content = (OSXMaskingContentPane)oldContent;
/*      */         } else {
/*      */           
/* 1351 */           content = new OSXMaskingContentPane(oldContent);
/* 1352 */           w.add(content);
/*      */         } 
/*      */       } 
/* 1355 */       return content;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setWindowTransparent(Window w, boolean transparent) {
/* 1369 */       boolean isTransparent = (w.getBackground() != null && w.getBackground().getAlpha() == 0);
/* 1370 */       if (transparent != isTransparent) {
/* 1371 */         setBackgroundTransparent(w, transparent, "setWindowTransparent");
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void fixWindowDragging(Window w, String context) {
/* 1378 */       if (w instanceof RootPaneContainer) {
/* 1379 */         JRootPane p = ((RootPaneContainer)w).getRootPane();
/* 1380 */         Boolean oldDraggable = (Boolean)p.getClientProperty("apple.awt.draggableWindowBackground");
/* 1381 */         if (oldDraggable == null) {
/* 1382 */           p.putClientProperty("apple.awt.draggableWindowBackground", Boolean.FALSE);
/* 1383 */           if (w.isDisplayable()) {
/* 1384 */             WindowUtils.LOG.log(Level.WARNING, "{0}(): To avoid content dragging, {1}() must be called before the window is realized, or apple.awt.draggableWindowBackground must be set to Boolean.FALSE before the window is realized.  If you really want content dragging, set apple.awt.draggableWindowBackground on the window''s root pane to Boolean.TRUE before calling {2}() to hide this message.", new Object[] { context, context, context });
/*      */           }
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setWindowAlpha(final Window w, final float alpha) {
/* 1401 */       if (w instanceof RootPaneContainer) {
/* 1402 */         JRootPane p = ((RootPaneContainer)w).getRootPane();
/* 1403 */         p.putClientProperty("Window.alpha", Float.valueOf(alpha));
/* 1404 */         fixWindowDragging(w, "setWindowAlpha");
/*      */       } 
/* 1406 */       whenDisplayable(w, new Runnable()
/*      */           {
/*      */             
/*      */             public void run()
/*      */             {
/*      */               try {
/* 1412 */                 Method getPeer = w.getClass().getMethod("getPeer", new Class[0]);
/* 1413 */                 Object peer = getPeer.invoke(w, new Object[0]);
/* 1414 */                 Method setAlpha = peer.getClass().getMethod("setAlpha", new Class[] { float.class });
/* 1415 */                 setAlpha.invoke(peer, new Object[] { Float.valueOf(this.val$alpha) });
/*      */               }
/* 1417 */               catch (Exception exception) {}
/*      */             }
/*      */           });
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected void setWindowMask(Component w, Raster raster) {
/* 1425 */       if (raster != null) {
/* 1426 */         setWindowMask(w, toShape(raster));
/*      */       } else {
/*      */         
/* 1429 */         setWindowMask(w, new Rectangle(0, 0, w.getWidth(), w
/* 1430 */               .getHeight()));
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void setWindowMask(Component c, Shape shape) {
/* 1436 */       if (c instanceof Window) {
/* 1437 */         Window w = (Window)c;
/* 1438 */         OSXMaskingContentPane content = installMaskingPane(w);
/* 1439 */         content.setMask(shape);
/* 1440 */         setBackgroundTransparent(w, (shape != WindowUtils.MASK_NONE), "setWindowMask");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private static class OSXMaskingContentPane
/*      */       extends JPanel
/*      */     {
/*      */       private static final long serialVersionUID = 1L;
/*      */       
/*      */       private Shape shape;
/*      */ 
/*      */       
/*      */       public OSXMaskingContentPane(Component oldContent) {
/* 1455 */         super(new BorderLayout());
/* 1456 */         if (oldContent != null) {
/* 1457 */           add(oldContent, "Center");
/*      */         }
/*      */       }
/*      */       
/*      */       public void setMask(Shape shape) {
/* 1462 */         this.shape = shape;
/* 1463 */         repaint();
/*      */       }
/*      */ 
/*      */       
/*      */       public void paint(Graphics graphics) {
/* 1468 */         Graphics2D g = (Graphics2D)graphics.create();
/* 1469 */         g.setComposite(AlphaComposite.Clear);
/* 1470 */         g.fillRect(0, 0, getWidth(), getHeight());
/* 1471 */         g.dispose();
/* 1472 */         if (this.shape != null) {
/* 1473 */           g = (Graphics2D)graphics.create();
/* 1474 */           g.setClip(this.shape);
/* 1475 */           super.paint(g);
/* 1476 */           g.dispose();
/*      */         } else {
/*      */           
/* 1479 */           super.paint(graphics);
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private void setBackgroundTransparent(Window w, boolean transparent, String context) {
/* 1486 */       JRootPane rp = (w instanceof RootPaneContainer) ? ((RootPaneContainer)w).getRootPane() : null;
/* 1487 */       if (transparent) {
/* 1488 */         if (rp != null) {
/* 1489 */           rp.putClientProperty("transparent-old-bg", w.getBackground());
/*      */         }
/* 1491 */         w.setBackground(new Color(0, 0, 0, 0));
/*      */       
/*      */       }
/* 1494 */       else if (rp != null) {
/* 1495 */         Color bg = (Color)rp.getClientProperty("transparent-old-bg");
/*      */ 
/*      */ 
/*      */         
/* 1499 */         if (bg != null) {
/* 1500 */           bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), bg.getAlpha());
/*      */         }
/* 1502 */         w.setBackground(bg);
/* 1503 */         rp.putClientProperty("transparent-old-bg", (Object)null);
/*      */       } else {
/*      */         
/* 1506 */         w.setBackground((Color)null);
/*      */       } 
/*      */       
/* 1509 */       fixWindowDragging(w, context);
/*      */     } }
/*      */   
/*      */   private static class X11WindowUtils extends NativeWindowUtils {
/*      */     private boolean didCheck;
/*      */     
/*      */     private static X11.Pixmap createBitmap(X11.Display dpy, X11.Window win, Raster raster) {
/* 1516 */       X11 x11 = X11.INSTANCE;
/* 1517 */       Rectangle bounds = raster.getBounds();
/* 1518 */       int width = bounds.x + bounds.width;
/* 1519 */       int height = bounds.y + bounds.height;
/* 1520 */       X11.Pixmap pm = x11.XCreatePixmap(dpy, (X11.Drawable)win, width, height, 1);
/* 1521 */       X11.GC gc = x11.XCreateGC(dpy, (X11.Drawable)pm, new NativeLong(0L), null);
/* 1522 */       if (gc == null) {
/* 1523 */         return null;
/*      */       }
/* 1525 */       x11.XSetForeground(dpy, gc, new NativeLong(0L));
/* 1526 */       x11.XFillRectangle(dpy, (X11.Drawable)pm, gc, 0, 0, width, height);
/* 1527 */       final List<Rectangle> rlist = new ArrayList<Rectangle>();
/*      */       try {
/* 1529 */         RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput()
/*      */             {
/*      */               public boolean outputRange(int x, int y, int w, int h) {
/* 1532 */                 rlist.add(new Rectangle(x, y, w, h));
/* 1533 */                 return true;
/*      */               }
/*      */             });
/*      */         
/* 1537 */         X11.XRectangle[] rects = (X11.XRectangle[])(new X11.XRectangle()).toArray(rlist.size());
/* 1538 */         for (int i = 0; i < rects.length; i++) {
/* 1539 */           Rectangle r = rlist.get(i);
/* 1540 */           (rects[i]).x = (short)r.x;
/* 1541 */           (rects[i]).y = (short)r.y;
/* 1542 */           (rects[i]).width = (short)r.width;
/* 1543 */           (rects[i]).height = (short)r.height;
/*      */           
/* 1545 */           Pointer p = rects[i].getPointer();
/* 1546 */           p.setShort(0L, (short)r.x);
/* 1547 */           p.setShort(2L, (short)r.y);
/* 1548 */           p.setShort(4L, (short)r.width);
/* 1549 */           p.setShort(6L, (short)r.height);
/* 1550 */           rects[i].setAutoSynch(false);
/*      */         } 
/*      */         
/* 1553 */         int UNMASKED = 1;
/* 1554 */         x11.XSetForeground(dpy, gc, new NativeLong(1L));
/* 1555 */         x11.XFillRectangles(dpy, (X11.Drawable)pm, gc, rects, rects.length);
/*      */       } finally {
/*      */         
/* 1558 */         x11.XFreeGC(dpy, gc);
/*      */       } 
/* 1560 */       return pm;
/*      */     }
/*      */ 
/*      */     
/* 1564 */     private long[] alphaVisualIDs = new long[0]; private static final long OPAQUE = 4294967295L;
/*      */     private static final String OPACITY = "_NET_WM_WINDOW_OPACITY";
/*      */     
/*      */     public boolean isWindowAlphaSupported() {
/* 1568 */       return ((getAlphaVisualIDs()).length > 0);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static long getVisualID(GraphicsConfiguration config) {
/*      */       try {
/* 1577 */         Object o = config.getClass().getMethod("getVisual", (Class[])null).invoke(config, (Object[])null);
/* 1578 */         return ((Number)o).longValue();
/*      */       }
/* 1580 */       catch (Exception e) {
/*      */         
/* 1582 */         e.printStackTrace();
/* 1583 */         return -1L;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
/* 1590 */       if (isWindowAlphaSupported()) {
/*      */         
/* 1592 */         GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 1593 */         GraphicsDevice[] devices = env.getScreenDevices();
/* 1594 */         for (int i = 0; i < devices.length; i++) {
/*      */           
/* 1596 */           GraphicsConfiguration[] configs = devices[i].getConfigurations();
/* 1597 */           for (int j = 0; j < configs.length; j++) {
/* 1598 */             long visualID = getVisualID(configs[j]);
/* 1599 */             long[] ids = getAlphaVisualIDs();
/* 1600 */             for (int k = 0; k < ids.length; k++) {
/* 1601 */               if (visualID == ids[k]) {
/* 1602 */                 return configs[j];
/*      */               }
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/* 1608 */       return super.getAlphaCompatibleGraphicsConfiguration();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private synchronized long[] getAlphaVisualIDs() {
/* 1616 */       if (this.didCheck) {
/* 1617 */         return this.alphaVisualIDs;
/*      */       }
/* 1619 */       this.didCheck = true;
/* 1620 */       X11 x11 = X11.INSTANCE;
/* 1621 */       X11.Display dpy = x11.XOpenDisplay(null);
/* 1622 */       if (dpy == null)
/* 1623 */         return this.alphaVisualIDs; 
/* 1624 */       X11.XVisualInfo info = null;
/*      */       try {
/* 1626 */         int screen = x11.XDefaultScreen(dpy);
/* 1627 */         X11.XVisualInfo template = new X11.XVisualInfo();
/* 1628 */         template.screen = screen;
/* 1629 */         template.depth = 32;
/* 1630 */         template.c_class = 4;
/* 1631 */         NativeLong mask = new NativeLong(14L);
/*      */ 
/*      */         
/* 1634 */         IntByReference pcount = new IntByReference();
/* 1635 */         info = x11.XGetVisualInfo(dpy, mask, template, pcount);
/* 1636 */         if (info != null) {
/* 1637 */           List<X11.VisualID> list = new ArrayList<X11.VisualID>();
/*      */           
/* 1639 */           X11.XVisualInfo[] infos = (X11.XVisualInfo[])info.toArray(pcount.getValue()); int i;
/* 1640 */           for (i = 0; i < infos.length; i++) {
/*      */             
/* 1642 */             X11.Xrender.XRenderPictFormat format = X11.Xrender.INSTANCE.XRenderFindVisualFormat(dpy, (infos[i]).visual);
/*      */             
/* 1644 */             if (format.type == 1 && format.direct.alphaMask != 0)
/*      */             {
/* 1646 */               list.add((infos[i]).visualid);
/*      */             }
/*      */           } 
/* 1649 */           this.alphaVisualIDs = new long[list.size()];
/* 1650 */           for (i = 0; i < this.alphaVisualIDs.length; i++) {
/* 1651 */             this.alphaVisualIDs[i] = ((Number)list.get(i)).longValue();
/*      */           }
/* 1653 */           return this.alphaVisualIDs;
/*      */         } 
/*      */       } finally {
/*      */         
/* 1657 */         if (info != null) {
/* 1658 */           x11.XFree(info.getPointer());
/*      */         }
/* 1660 */         x11.XCloseDisplay(dpy);
/*      */       } 
/* 1662 */       return this.alphaVisualIDs;
/*      */     }
/*      */ 
/*      */     
/*      */     private static X11.Window getContentWindow(Window w, X11.Display dpy, X11.Window win, Point offset) {
/* 1667 */       if ((w instanceof Frame && !((Frame)w).isUndecorated()) || (w instanceof Dialog && 
/* 1668 */         !((Dialog)w).isUndecorated())) {
/* 1669 */         X11 x11 = X11.INSTANCE;
/* 1670 */         X11.WindowByReference rootp = new X11.WindowByReference();
/* 1671 */         X11.WindowByReference parentp = new X11.WindowByReference();
/* 1672 */         PointerByReference childrenp = new PointerByReference();
/* 1673 */         IntByReference countp = new IntByReference();
/* 1674 */         x11.XQueryTree(dpy, win, rootp, parentp, childrenp, countp);
/* 1675 */         Pointer p = childrenp.getValue();
/* 1676 */         int[] ids = p.getIntArray(0L, countp.getValue());
/* 1677 */         int arrayOfInt1[] = ids, i = arrayOfInt1.length; byte b = 0; if (b < i) { int id = arrayOfInt1[b];
/*      */           
/* 1679 */           X11.Window child = new X11.Window(id);
/* 1680 */           X11.XWindowAttributes xwa = new X11.XWindowAttributes();
/* 1681 */           x11.XGetWindowAttributes(dpy, child, xwa);
/* 1682 */           offset.x = -xwa.x;
/* 1683 */           offset.y = -xwa.y;
/* 1684 */           win = child; }
/*      */ 
/*      */         
/* 1687 */         if (p != null) {
/* 1688 */           x11.XFree(p);
/*      */         }
/*      */       } 
/* 1691 */       return win;
/*      */     }
/*      */     
/*      */     private static X11.Window getDrawable(Component w) {
/* 1695 */       int id = (int)Native.getComponentID(w);
/* 1696 */       if (id == 0)
/* 1697 */         return null; 
/* 1698 */       return new X11.Window(id);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setWindowAlpha(final Window w, final float alpha) {
/* 1706 */       if (!isWindowAlphaSupported()) {
/* 1707 */         throw new UnsupportedOperationException("This X11 display does not provide a 32-bit visual");
/*      */       }
/* 1709 */       Runnable action = new Runnable()
/*      */         {
/*      */           public void run() {
/* 1712 */             X11 x11 = X11.INSTANCE;
/* 1713 */             X11.Display dpy = x11.XOpenDisplay(null);
/* 1714 */             if (dpy == null)
/*      */               return; 
/*      */             try {
/* 1717 */               X11.Window win = WindowUtils.X11WindowUtils.getDrawable(w);
/* 1718 */               if (alpha == 1.0F) {
/* 1719 */                 x11.XDeleteProperty(dpy, win, x11
/* 1720 */                     .XInternAtom(dpy, "_NET_WM_WINDOW_OPACITY", false));
/*      */               }
/*      */               else {
/*      */                 
/* 1724 */                 int opacity = (int)((long)(alpha * 4.2949673E9F) & 0xFFFFFFFFFFFFFFFFL);
/* 1725 */                 IntByReference patom = new IntByReference(opacity);
/* 1726 */                 x11.XChangeProperty(dpy, win, x11
/* 1727 */                     .XInternAtom(dpy, "_NET_WM_WINDOW_OPACITY", false), X11.XA_CARDINAL, 32, 0, patom
/*      */ 
/*      */ 
/*      */                     
/* 1731 */                     .getPointer(), 1);
/*      */               } 
/*      */             } finally {
/*      */               
/* 1735 */               x11.XCloseDisplay(dpy);
/*      */             } 
/*      */           }
/*      */         };
/* 1739 */       whenDisplayable(w, action);
/*      */     }
/*      */     private class X11TransparentContentPane extends WindowUtils.NativeWindowUtils.TransparentContentPane { private static final long serialVersionUID = 1L; private Memory buffer;
/*      */       private int[] pixels;
/*      */       private final int[] pixel;
/*      */       
/*      */       public X11TransparentContentPane(Container oldContent) {
/* 1746 */         super(oldContent);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1751 */         this.pixel = new int[4];
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected void paintDirect(BufferedImage buf, Rectangle bounds) {
/* 1757 */         Window window = SwingUtilities.getWindowAncestor(this);
/* 1758 */         X11 x11 = X11.INSTANCE;
/* 1759 */         X11.Display dpy = x11.XOpenDisplay(null);
/* 1760 */         X11.Window win = WindowUtils.X11WindowUtils.getDrawable(window);
/* 1761 */         Point offset = new Point();
/* 1762 */         win = WindowUtils.X11WindowUtils.getContentWindow(window, dpy, win, offset);
/* 1763 */         X11.GC gc = x11.XCreateGC(dpy, (X11.Drawable)win, new NativeLong(0L), null);
/*      */         
/* 1765 */         Raster raster = buf.getData();
/* 1766 */         int w = bounds.width;
/* 1767 */         int h = bounds.height;
/* 1768 */         if (this.buffer == null || this.buffer.size() != (w * h * 4)) {
/* 1769 */           this.buffer = new Memory((w * h * 4));
/* 1770 */           this.pixels = new int[w * h];
/*      */         } 
/* 1772 */         for (int y = 0; y < h; y++) {
/* 1773 */           for (int x = 0; x < w; x++) {
/* 1774 */             raster.getPixel(x, y, this.pixel);
/* 1775 */             int alpha = this.pixel[3] & 0xFF;
/* 1776 */             int red = this.pixel[2] & 0xFF;
/* 1777 */             int green = this.pixel[1] & 0xFF;
/* 1778 */             int blue = this.pixel[0] & 0xFF;
/*      */ 
/*      */             
/* 1781 */             this.pixels[y * w + x] = alpha << 24 | blue << 16 | green << 8 | red;
/*      */           } 
/*      */         } 
/* 1784 */         X11.XWindowAttributes xwa = new X11.XWindowAttributes();
/* 1785 */         x11.XGetWindowAttributes(dpy, win, xwa);
/*      */         
/* 1787 */         X11.XImage image = x11.XCreateImage(dpy, xwa.visual, 32, 2, 0, (Pointer)this.buffer, w, h, 32, w * 4);
/*      */         
/* 1789 */         this.buffer.write(0L, this.pixels, 0, this.pixels.length);
/* 1790 */         offset.x += bounds.x;
/* 1791 */         offset.y += bounds.y;
/* 1792 */         x11.XPutImage(dpy, (X11.Drawable)win, gc, image, 0, 0, offset.x, offset.y, w, h);
/*      */         
/* 1794 */         x11.XFree(image.getPointer());
/* 1795 */         x11.XFreeGC(dpy, gc);
/* 1796 */         x11.XCloseDisplay(dpy);
/*      */       } }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setWindowTransparent(final Window w, final boolean transparent) {
/* 1803 */       if (!(w instanceof RootPaneContainer)) {
/* 1804 */         throw new IllegalArgumentException("Window must be a RootPaneContainer");
/*      */       }
/* 1806 */       if (!isWindowAlphaSupported()) {
/* 1807 */         throw new UnsupportedOperationException("This X11 display does not provide a 32-bit visual");
/*      */       }
/*      */       
/* 1810 */       if (!w.getGraphicsConfiguration().equals(getAlphaCompatibleGraphicsConfiguration())) {
/* 1811 */         throw new IllegalArgumentException("Window GraphicsConfiguration '" + w.getGraphicsConfiguration() + "' does not support transparency");
/*      */       }
/*      */       
/* 1814 */       boolean isTransparent = (w.getBackground() != null && w.getBackground().getAlpha() == 0);
/* 1815 */       if (transparent == isTransparent)
/*      */         return; 
/* 1817 */       whenDisplayable(w, new Runnable()
/*      */           {
/*      */             public void run() {
/* 1820 */               JRootPane root = ((RootPaneContainer)w).getRootPane();
/* 1821 */               JLayeredPane lp = root.getLayeredPane();
/* 1822 */               Container content = root.getContentPane();
/* 1823 */               if (content instanceof WindowUtils.X11WindowUtils.X11TransparentContentPane) {
/* 1824 */                 ((WindowUtils.X11WindowUtils.X11TransparentContentPane)content).setTransparent(transparent);
/*      */               }
/* 1826 */               else if (transparent) {
/* 1827 */                 WindowUtils.X11WindowUtils.X11TransparentContentPane x11content = new WindowUtils.X11WindowUtils.X11TransparentContentPane(content);
/*      */                 
/* 1829 */                 root.setContentPane(x11content);
/* 1830 */                 lp.add(new WindowUtils.RepaintTrigger(x11content), JLayeredPane.DRAG_LAYER);
/*      */               } 
/*      */               
/* 1833 */               WindowUtils.X11WindowUtils.this.setLayersTransparent(w, transparent);
/* 1834 */               WindowUtils.X11WindowUtils.this.setForceHeavyweightPopups(w, transparent);
/* 1835 */               WindowUtils.X11WindowUtils.this.setDoubleBuffered(w, !transparent);
/*      */             }
/*      */           });
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void setWindowShape(final Window w, final PixmapSource src) {
/* 1845 */       Runnable action = new Runnable()
/*      */         {
/*      */           public void run() {
/* 1848 */             X11 x11 = X11.INSTANCE;
/* 1849 */             X11.Display dpy = x11.XOpenDisplay(null);
/* 1850 */             if (dpy == null) {
/*      */               return;
/*      */             }
/* 1853 */             X11.Pixmap pm = null;
/*      */             try {
/* 1855 */               X11.Window win = WindowUtils.X11WindowUtils.getDrawable(w);
/* 1856 */               pm = src.getPixmap(dpy, win);
/* 1857 */               X11.Xext ext = X11.Xext.INSTANCE;
/* 1858 */               ext.XShapeCombineMask(dpy, win, 0, 0, 0, (pm == null) ? X11.Pixmap.None : pm, 0);
/*      */             
/*      */             }
/*      */             finally {
/*      */               
/* 1863 */               if (pm != null) {
/* 1864 */                 x11.XFreePixmap(dpy, pm);
/*      */               }
/* 1866 */               x11.XCloseDisplay(dpy);
/*      */             } 
/* 1868 */             WindowUtils.X11WindowUtils.this.setForceHeavyweightPopups(WindowUtils.X11WindowUtils.this.getWindow(w), (pm != null));
/*      */           }
/*      */         };
/* 1871 */       whenDisplayable(w, action);
/*      */     }
/*      */ 
/*      */     
/*      */     protected void setMask(Component w, final Raster raster) {
/* 1876 */       setWindowShape(getWindow(w), new PixmapSource()
/*      */           {
/*      */             public X11.Pixmap getPixmap(X11.Display dpy, X11.Window win) {
/* 1879 */               return (raster != null) ? WindowUtils.X11WindowUtils.createBitmap(dpy, win, raster) : null;
/*      */             }
/*      */           });
/*      */     }
/*      */     
/*      */     private X11WindowUtils() {}
/*      */     
/*      */     private static interface PixmapSource {
/*      */       X11.Pixmap getPixmap(X11.Display param2Display, X11.Window param2Window); }
/*      */   }
/*      */   
/*      */   public static void setWindowMask(Window w, Shape mask) {
/* 1891 */     getInstance().setWindowMask(w, mask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setComponentMask(Component c, Shape mask) {
/* 1900 */     getInstance().setWindowMask(c, mask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setWindowMask(Window w, Icon mask) {
/* 1909 */     getInstance().setWindowMask(w, mask);
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean isWindowAlphaSupported() {
/* 1914 */     return getInstance().isWindowAlphaSupported();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
/* 1922 */     return getInstance().getAlphaCompatibleGraphicsConfiguration();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setWindowAlpha(Window w, float alpha) {
/* 1940 */     getInstance().setWindowAlpha(w, Math.max(0.0F, Math.min(alpha, 1.0F)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setWindowTransparent(Window w, boolean transparent) {
/* 1956 */     getInstance().setWindowTransparent(w, transparent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage getWindowIcon(WinDef.HWND hwnd) {
/* 1969 */     return getInstance().getWindowIcon(hwnd);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Dimension getIconSize(WinDef.HICON hIcon) {
/* 1981 */     return getInstance().getIconSize(hIcon);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<DesktopWindow> getAllWindows(boolean onlyVisibleWindows) {
/* 1999 */     return getInstance().getAllWindows(onlyVisibleWindows);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getWindowTitle(WinDef.HWND hwnd) {
/* 2012 */     return getInstance().getWindowTitle(hwnd);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getProcessFilePath(WinDef.HWND hwnd) {
/* 2026 */     return getInstance().getProcessFilePath(hwnd);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Rectangle getWindowLocationAndSize(WinDef.HWND hwnd) {
/* 2038 */     return getInstance().getWindowLocationAndSize(hwnd);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\WindowUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */