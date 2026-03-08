package src;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import src.CreateInvoiceDialog.InvoiceData;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * SalesMenuDialog
 *
 * This file upgrades the UI/UX of the original SalesMenuDialog to match the look-and-feel
 * of ProductMenuDialog while preserving the original logic (menu items and what they do).
 *
 * UI improvements (no logic changes):
 * - Starts maximized to cover the full screen.
 * - Left blue panel has rounded corners and a subtle border.
 * - Menu boxes, fonts and paddings scale proportionally as the window size changes.
 * - A single moving accent indicator animates to hovered/selected menu boxes.
 * - Opening Create Invoice dialog remains modal and centered over the right content area.
 *
 * Placeholder modal dialog CreateInvoiceDialog is included so this file compiles standalone.
 */
public class SalesMenuDialog extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color GRADIENT_BLUE = new Color(0, 200, 255);
    // Menu box colors: translucent whites that read well over the blue left panel
    private static final Color MENU_BOX_BG = new Color(255, 255, 255, 30);
    private static final Color MENU_BOX_HOVER = new Color(255, 255, 255, 80);
    private static final Color CONTENT_OFFWHITE = new Color(250, 251, 253);
    private static final Color SHADOW = new Color(0, 0, 0, 30);
    private static final Color ACCENT = new Color(10, 90, 200);

    // Base metrics (used to compute scaling)
    private final int BASE_WIDTH = 1000;
    private final int BASE_LEFT_WIDTH = 320;
    private final int BASE_MENU_BOX_HEIGHT = 36;
    private final int BASE_TOPBAR_HEIGHT = 48;
    private final int BASE_CORNER_ARC = 18;

    // Scaled values (updated on resize)
    private int menuBoxHeight = BASE_MENU_BOX_HEIGHT;
    private int cornerArc = BASE_CORNER_ARC;
    private Font menuFont = new Font("Tahoma", Font.BOLD, 17);
    private Font headingFont = new Font("Tahoma", Font.BOLD, 36);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font noteFont = new Font("Segoe UI", Font.PLAIN, 14);

    private final RoundedPanel leftPanel;
    private final JPanel contentPanel;
    private final JPanel menuGrid;
    private Point dragOffset;
    private boolean maximized = false;
    private Rectangle previousBounds;
    // private Connection connection; // Database connection removed


    // Menu items
    private final MenuBox[] menuBoxes;

    // Indicator for hovered/selected menu
    private int indicatorY = -1;
    private int indicatorTargetY = -1;
    private int indicatorHeight = 0;
    private Timer indicatorTimer;

    public SalesMenuDialog() {
        super("Sales Dashboard");
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // create top bar and left panel
        JPanel topBar = createTopBar();

        leftPanel = createLeftGradientPanel();

        String[] menuItems = {"Create Invoice", "Record Sales", "Generate Report"};
        menuBoxes = new MenuBox[menuItems.length];
        menuGrid = createMenuGrid(menuItems);

        JPanel leftInner = new JPanel(new BorderLayout());
        leftInner.setOpaque(false);
        leftInner.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel brand = new JLabel("Tiles Factory");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Sales Management");
        subtitle.setForeground(new Color(255, 255, 255, 200));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel brandBox = new JPanel(new GridLayout(2, 1));
        brandBox.setOpaque(false);
        brandBox.add(brand);
        brandBox.add(subtitle);
        brandBox.setBorder(new EmptyBorder(0, 0, 12, 0));
        leftInner.add(brandBox, BorderLayout.NORTH);
        leftInner.add(menuGrid, BorderLayout.CENTER);
        leftPanel.add(leftInner, BorderLayout.CENTER);

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(CONTENT_OFFWHITE);
        addWelcomeContent();

        JPanel root = new JPanel(new BorderLayout());
        root.add(topBar, BorderLayout.NORTH);
        root.add(leftPanel, BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);
        getContentPane().add(root);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                enforceLeftProportion();
                rescaleUi();
                // reposition indicator to currently selected or first item
                SwingUtilities.invokeLater(() -> {
                    MenuBox sel = findSelectedBox();
                    if (sel != null) moveIndicatorTo(sel, true);
                });
            }
        });

        // Start maximized to cover full screen
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle screenBounds = env.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        setBounds(screenBounds);
        maximized = true;

        // ensure left proportion and scaling after shown
        SwingUtilities.invokeLater(() -> {
            enforceLeftProportion();
            rescaleUi();
            if (menuBoxes.length > 0) {
                menuBoxes[0].setSelected(true);
                moveIndicatorTo(menuBoxes[0], true);
            }
        });
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(0, BASE_TOPBAR_HEIGHT));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JLabel title = new JLabel(" Sales Dashboard");
        title.setFont(titleFont);
        title.setForeground(new Color(60, 60, 60));
        topBar.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        controls.setOpaque(false);

        JButton back = new JButton("\u25C0 Back");
        styleTopButton(back);
        back.setToolTipText("Back to Admin Dashboard");
        back.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    AdminDashboardDialog dash = new AdminDashboardDialog();
                    dash.setLocation(this.getLocation());
                    dash.setVisible(true);
                } catch (Throwable ex) {
                    JOptionPane.showMessageDialog(null, "Failed to open Admin Dashboard:\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        JButton minBtn = createTopButton("\u2014");
        JButton maxBtn = createTopButton("▢");
        JButton closeBtn = createTopButton("X");
        closeBtn.setForeground(Color.RED);

        minBtn.addActionListener(e -> setState(Frame.ICONIFIED));
        maxBtn.addActionListener(e -> toggleMaximize());
        closeBtn.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "Close Sales Dashboard?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) dispose();
        });

        controls.add(back);
        controls.add(minBtn);
        controls.add(maxBtn);
        controls.add(closeBtn);
        topBar.add(controls, BorderLayout.EAST);

        // Draggable top bar
        MouseAdapter drag = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragOffset = e.getPoint(); }
            @Override public void mouseDragged(MouseEvent e) {
                Point p = e.getLocationOnScreen();
                setLocation(p.x - dragOffset.x, p.y - dragOffset.y);
            }
        };
        topBar.addMouseListener(drag);
        topBar.addMouseMotionListener(drag);
        return topBar;
    }
    private void styleTopButton(JButton b) {
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        b.setBackground(new Color(255,255,255));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JButton createTopButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setBackground(new Color(0,0,0,0));
        btn.setOpaque(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
    private void showInContentPanel(JPanel panel) {
    contentPanel.removeAll();

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;

    contentPanel.add(panel, gbc);
    contentPanel.revalidate();
    contentPanel.repaint();
}

    private RoundedPanel createLeftGradientPanel() {
        RoundedPanel left = new RoundedPanel(BASE_CORNER_ARC) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D rr = new RoundRectangle2D.Double(0,0,getWidth(),getHeight(), getArc(), getArc());
                g2.setClip(rr);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_BLUE, 0, getHeight(), GRADIENT_BLUE);
                g2.setPaint(gp);
                g2.fill(rr);

                // subtle top-left highlight / border
                g2.setPaint(new Color(255,255,255,30));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rr);

                // draw moving indicator (accent) on the right side inside rounded area
                if (indicatorY >= 0 && indicatorHeight > 0) {
                    g2.setPaint(ACCENT);
                    int indW = Math.max(6, Math.round(6f * scaleFactor()));
                    int x = getWidth() - 14 - indW; // inset from right
                    g2.fillRoundRect(x, indicatorY, indW, indicatorHeight, indW, indW);
                }

                // subtle footer shadow
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(0, getHeight()-6, Math.max(0, getWidth()), 6, getArc(), getArc());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(BASE_LEFT_WIDTH, 0));
        return left;
    }

    private JPanel createMenuGrid(String[] items) {
        // increase vertical gap between boxes so they're visually separated
        JPanel grid = new JPanel(new GridLayout(items.length, 1, 0, Math.max(12, Math.round(12 * scaleFactor()))));
        grid.setOpaque(false);

        for (int i = 0; i < items.length; i++) {
            MenuBox box = new MenuBox(items[i]);
            menuBoxes[i] = box;
            grid.add(box);
        }
        return grid;
    }

    private class MenuBox extends JPanel {
        private final JLabel label;
        private boolean selected = false;
        private boolean hover = false;

        MenuBox(String text) {
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(8, 14, 8, 14));
            label = new JLabel(text);
            label.setFont(menuFont);
            // text color should be white on the left blue panel
            label.setForeground(Color.WHITE);
            add(label, BorderLayout.WEST);
            setPreferredSize(new Dimension(200, menuBoxHeight));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(text);

            // track hover state and repaint without changing the component border/shape
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); moveIndicatorTo(MenuBox.this, false); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); MenuBox sel = findSelectedBox(); if (sel != null) moveIndicatorTo(sel, false); }
                @Override public void mouseClicked(MouseEvent e) { onClick(text); setSelected(true); }
            });

            setFocusable(true);
            addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) { onClick(text); setSelected(true); }
                }
            });
        }

        void setSelected(boolean sel) {
            if (sel) {
                // deselect others
                for (MenuBox mb : menuBoxes) if (mb != this) mb.selected = false;
            }
            this.selected = sel;
            repaint();
        }

        boolean isSelected() { return selected; }

        @Override protected void paintComponent(Graphics g) {
            int arc = Math.max(12, Math.round(cornerArc * scaleFactor()));
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // choose background based on hover/selection without changing border style
            Color fill = selected ? MENU_BOX_HOVER : (hover ? MENU_BOX_HOVER : MENU_BOX_BG);

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            // consistent border that doesn't change shape on hover
            Color borderColor = new Color(255, 255, 255, 140);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, Math.max(0, getWidth()-1), Math.max(0, getHeight()-1), arc, arc);

            // keep a very subtle bottom gloss (non-intrusive)
            g2.setColor(new Color(255, 255, 255, 12));
            int glossH = Math.max(4, Math.round(4 * scaleFactor()));
            g2.fillRoundRect(2, 2, Math.max(0, getWidth()-4), glossH, arc, arc);

            // ensure font scaled and text color preserved
            label.setFont(menuFont);
            label.setForeground(Color.WHITE);
            g2.dispose();
            super.paintComponent(g);
        }

        private void onClick(String text) {

     if ("Create Invoice".equalsIgnoreCase(text)) {

    // Make sure contentPanel is your right side panel where you want the dialog to appear
    CreateInvoiceDialog dialog = new CreateInvoiceDialog(SalesMenuDialog.this, contentPanel);
    dialog.setVisible(true);

    return;
}

   if ("Record Sales".equalsIgnoreCase(text)) {

    RecordSalesDialog dialog = new RecordSalesDialog(SalesMenuDialog.this, contentPanel);
    dialog.setVisible(true);

    return;
}


   if ("Generate Report".equalsIgnoreCase(text)) {
    // DB connectivity removed
    GenerateReportDialog dialog = new GenerateReportDialog(SalesMenuDialog.this, contentPanel);
    dialog.setVisible(true);
    return;
}


    // placeholder for other items (if any)
    showPlaceholder(text);
}

    }

    private MenuBox findSelectedBox() {
        for (MenuBox mb : menuBoxes) if (mb != null && mb.isSelected()) return mb;
        for (MenuBox mb : menuBoxes) if (mb != null) return mb; // fallback
        return null;
    }
    private void createDatabaseConnection() {
        // Database connectivity removed
    }
    private void addWelcomeContent() {
        contentPanel.removeAll();
        JLabel lbl = new JLabel("Welcome to Sales Dashboard");
        lbl.setFont(headingFont);
        lbl.setForeground(PRIMARY_BLUE);
        contentPanel.add(lbl);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPlaceholder(String title) {
        contentPanel.removeAll();
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CONTENT_OFFWHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        JLabel h = new JLabel(title);
        h.setFont(new Font("Tahoma", Font.BOLD, Math.max(20, Math.round(28 * scaleFactor()))));
        h.setForeground(PRIMARY_BLUE);
        gbc.gridy = 0;
        card.add(h, gbc);
        JLabel note = new JLabel("This is a placeholder for the \"" + title + "\" screen.");
        note.setFont(noteFont);
        note.setForeground(new Color(90, 90, 90));
        gbc.gridy = 1;
        card.add(note, gbc);
        contentPanel.add(card);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void enforceLeftProportion() {
        int total = getContentPane().getWidth();
        if (total <= 0) return;
        int leftWidth = Math.max(220, (int) (total * 0.30));
        leftPanel.setPreferredSize(new Dimension(leftWidth, getContentPane().getHeight()));
        leftPanel.revalidate();
    }

    private void toggleMaximize() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = env.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        if (!maximized) {
            previousBounds = getBounds();
            setBounds(bounds);
        } else {
            if (previousBounds != null) setBounds(previousBounds);
            else {
                setSize(BASE_WIDTH, 650);
                setLocationRelativeTo(null);
            }
        }
        maximized = !maximized;
        enforceLeftProportion();
        rescaleUi();
    }

    // Move indicator to a particular MenuBox (animated)
    private void moveIndicatorTo(MenuBox box, boolean immediate) {
        if (box == null) return;
        Rectangle b = SwingUtilities.convertRectangle(box.getParent(), box.getBounds(), leftPanel);
        int pad = Math.max(6, Math.round(6 * scaleFactor()));
        indicatorTargetY = b.y + pad;
        indicatorHeight = Math.max(20, b.height - 2 * pad);

        if (immediate) {
            indicatorY = indicatorTargetY;
            leftPanel.repaint();
            return;
        }

        if (indicatorTimer != null && indicatorTimer.isRunning()) indicatorTimer.stop();
        indicatorTimer = new Timer(12, null);
        indicatorTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (indicatorY < 0) indicatorY = indicatorTargetY;
                int dy = indicatorTargetY - indicatorY;
                if (Math.abs(dy) <= 2) {
                    indicatorY = indicatorTargetY;
                    indicatorTimer.stop();
                } else {
                    indicatorY += Math.max(-8, Math.min(8, dy));
                }
                leftPanel.repaint();
            }
        });
        indicatorTimer.start();
    }

    private float scaleFactor() {
        int w = getWidth();
        if (w <= 0) w = BASE_WIDTH;
        float s = (float) w / (float) BASE_WIDTH;
        s = Math.max(0.75f, Math.min(1.8f, s));
        return s;
    }

    private void rescaleUi() {
        float s = scaleFactor();
        menuBoxHeight = Math.max(28, Math.round(BASE_MENU_BOX_HEIGHT * s));
        cornerArc = Math.max(8, Math.round(BASE_CORNER_ARC * s));
        menuFont = new Font("Tahoma", Font.BOLD, Math.max(12, Math.round(17 * s)));
        headingFont = new Font("Tahoma", Font.BOLD, Math.max(20, Math.round(36 * s)));
        titleFont = new Font("Segoe UI", Font.BOLD, Math.max(12, Math.round(14 * s)));
        noteFont = new Font("Segoe UI", Font.PLAIN, Math.max(12, Math.round(14 * s)));

        // Update menu boxes
        for (MenuBox mb : menuBoxes) {
            if (mb == null) continue;
            mb.setPreferredSize(new Dimension(mb.getPreferredSize().width, menuBoxHeight));
            mb.label.setFont(menuFont);
            mb.revalidate();
            mb.repaint();
        }

        leftPanel.setArc(cornerArc);
        leftPanel.revalidate();
        leftPanel.repaint();

        // Update content children fonts
        for (Component c : contentPanel.getComponents()) {
            c.revalidate();
            c.repaint();
        }
    }

    // RoundedPanel (supports arc)
    private abstract static class RoundedPanel extends JPanel {
        private int arc;
        RoundedPanel(int arc) {
            super(new BorderLayout());
            this.arc = arc;
            setOpaque(false);
        }
        public int getArc() { return arc; }
        public void setArc(int arc) { this.arc = arc; repaint(); }
    }

    // ---------------- Placeholder modal dialogs ----------------
    // Minimal modal dialog that centers over the contentPanel area and blocks the owner



    // CreateInvoiceDialog placeholder (matches usage in onClick)

    // ---------------- Main ----------------

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            SalesMenuDialog dlg = new SalesMenuDialog();
            dlg.setVisible(true);
        });
    }
}