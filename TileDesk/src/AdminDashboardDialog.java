package src;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminDashboardDialog
 *
 * Improvements implemented:
 * 1) Maximize toggles to full screen (covers the whole screen) and restores properly.
 * 2) Left blue panel uses rounded corners for a nicer look.
 * 3) A single animated indicator moves to the hovered OR selected menu item.
 * 4) Responsive scaling: fonts and paddings scale when window size changes so boxes remain visually proportional.
 *
 * Companion minimal classes ProductMenuDialog.java and SalesMenuDialog.java are included to allow compiling/testing.
 */
public class AdminDashboardDialog extends JFrame {

    private final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private final Color GRADIENT_BLUE = new Color(0, 200, 255);
    private final Color MENU_BG = new Color(245, 246, 248);
    private final Color MENU_HOVER = new Color(235, 243, 255);
    private final Color CONTENT_OFFWHITE = new Color(250, 251, 253);
    private final Color ACCENT = new Color(10, 90, 200);
    private final Color BORDER = new Color(220, 220, 225);

    private final CardLayout contentCards = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentCards);
    private final List<RoundedMenuItem> menuItems = new ArrayList<>();

    // fonts will be updated on resize
    private Font menuFont = new Font("Tahoma", Font.BOLD, 16);
    private Font contentFont = new Font("Tahoma", Font.PLAIN, 14);
    private Font headingFont = new Font("Tahoma", Font.BOLD, 28);

    private boolean maximized = false;
    private Rectangle previousBounds;
    private Point dragOffset;

    private JLabel statusLabel;
    private RoundedPanel leftPanelPaint;   // now a rounded panel
    private JPanel menuGrid; // holds menu items in GridLayout
    private JButton collapseBtn;
    private JTextField searchField;
    private boolean collapsed = false;

    // indicator variables for the moving accent
    private int indicatorY = -1;
    private int indicatorTargetY = -1;
    private int indicatorHeight = 0;
    private Timer indicatorTimer;

    private int baseWindowWidth = 1100; // base size to compute scaling

    public AdminDashboardDialog() {
        super("Admin Dashboard");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext","true");

        JPanel topBar = createTopBar();
        leftPanelPaint = createLeftGradientPanel();
        contentPanel.setBackground(CONTENT_OFFWHITE);
        addContentCards();

        JPanel root = new JPanel(new BorderLayout());
        root.add(topBar, BorderLayout.NORTH);
        root.add(leftPanelPaint, BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);
        root.add(createStatusBar(), BorderLayout.SOUTH);
        getContentPane().add(root);

        // ensure left proportion and scale on resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                enforceLeftProportion();
                rescaleUi();
            }
        });

        setSize(baseWindowWidth, 700);
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> {
            enforceLeftProportion();
            animateWelcome();
            // initialize indicator on first (selected) item
            if (!menuItems.isEmpty()) moveIndicatorTo(menuItems.get(0), true);
        });

        // ESC to exit
        getRootPane().registerKeyboardAction(e -> {
            int option = JOptionPane.showConfirmDialog(this, "Exit application?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) dispose();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Ctrl+M to toggle menu collapse
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK), "toggleMenu");
        getRootPane().getActionMap().put("toggleMenu", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { toggleCollapse(); }
        });
    }

    // ---------------- UI BUILDERS ----------------

    private RoundedPanel createLeftGradientPanel() {
        RoundedPanel left = new RoundedPanel(24) {
            @Override
            protected void paintComponent(Graphics g) {
                // paint gradient inside rounded rect
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D rr = new RoundRectangle2D.Double(0,0,getWidth(),getHeight(), getArc(), getArc());
                g2.setClip(rr);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_BLUE, 0, getHeight(), GRADIENT_BLUE);
                g2.setPaint(gp);
                g2.fill(rr);

                // draw subtle border
                g2.setPaint(new Color(255,255,255,30));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rr);

                // draw moving indicator (on top of gradient)
                if (indicatorY >= 0 && indicatorHeight > 0) {
                    g2.setPaint(ACCENT);
                    int indW = 6;
                    int x = getWidth() - 14;
                    g2.fillRoundRect(x, indicatorY, indW, indicatorHeight, indW, indW);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        left.setOpaque(false);

        JPanel leftInner = new JPanel(new BorderLayout());
        leftInner.setOpaque(false);
        leftInner.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel brand = new JLabel("Tiles Factory");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel subtitle = new JLabel("Admin Control System");
        subtitle.setForeground(new Color(255, 255, 255, 220));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel brandBox = new JPanel(new GridLayout(2,1));
        brandBox.setOpaque(false);
        brandBox.add(brand);
        brandBox.add(subtitle);
        brandBox.setBorder(new EmptyBorder(0,0,8,0));
        leftInner.add(brandBox, BorderLayout.NORTH);

        String[] menus = {"Welcome", "Product", "Sales", "Customer", "Employees", "Reports", "Setting", "Logout"};
        menuGrid = new JPanel();
        menuGrid.setOpaque(false);
        menuGrid.setLayout(new GridLayout(menus.length, 1, 0, 10));
        menuGrid.setBorder(new EmptyBorder(6, 0, 6, 0));

        for (String m : menus) {
            RoundedMenuItem item = new RoundedMenuItem(m);
            item.setFont(menuFont);
            item.setTextColor(Color.WHITE);
            item.setToolTipText("Open " + m + " panel");
            item.addActionListener(ev -> onMenuSelected(m));
            menuItems.add(item);
            menuGrid.add(item);

            // when hovered, move indicator here
            item.addHoverListener(() -> moveIndicatorTo(item, false));
            // when hover exits, move back to selected item
            item.addHoverExitListener(() -> {
                RoundedMenuItem sel = menuItems.stream().filter(RoundedMenuItem::isSelected).findFirst().orElse(null);
                if (sel != null) moveIndicatorTo(sel, false);
            });
        }

        if (!menuItems.isEmpty()) {
            menuItems.get(0).setSelected(true);
        }

        leftInner.add(menuGrid, BorderLayout.CENTER);
        left.add(leftInner, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        collapseBtn = new JButton("\u25C0");
        collapseBtn.setToolTipText("Collapse / expand menu (Ctrl+M)");
        collapseBtn.setPreferredSize(new Dimension(36, 36));
        collapseBtn.setFocusPainted(false);
        collapseBtn.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        collapseBtn.setBackground(new Color(255,255,255,200));
        collapseBtn.setForeground(PRIMARY_BLUE);
        collapseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        collapseBtn.addActionListener(e -> toggleCollapse());
        bottom.add(collapseBtn, BorderLayout.WEST);
        bottom.setBorder(new EmptyBorder(12, 0, 0, 0));
        left.add(bottom, BorderLayout.SOUTH);

        // set a reasonable min preferred width
        left.setPreferredSize(new Dimension(Math.max(200, (int)(getWidth() * 0.30)), getHeight()));

        return left;
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(8,0));
        topBar.setPreferredSize(new Dimension(0, 44));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(230,230,230)));

        JLabel title = new JLabel("  Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(60,60,60));
        topBar.add(title, BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout(8,0));
        center.setOpaque(false);
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 34));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));
        searchField.setToolTipText("Filter menu (type to search)");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterMenu(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterMenu(searchField.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterMenu(searchField.getText()); }
        });
        center.add(searchField, BorderLayout.WEST);

        JLabel currentView = new JLabel(" ");
        currentView.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        currentView.setForeground(new Color(110,110,110));
        center.add(currentView, BorderLayout.CENTER);
        topBar.add(center, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        controls.setOpaque(false);

        JButton minBtn = createTopButton("\u2014", "Minimize");
        JButton maxBtn = createTopButton("▢", "Maximize / Restore");
        JButton closeBtn = createTopButton("X", "Close");
        closeBtn.setForeground(Color.RED);

        minBtn.addActionListener(e -> setState(Frame.ICONIFIED));
        maxBtn.addActionListener(e -> toggleMaximize());
        closeBtn.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) dispose();
        });

        controls.add(minBtn);
        controls.add(maxBtn);
        controls.add(closeBtn);
        topBar.add(controls, BorderLayout.EAST);

        MouseAdapter drag = new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dragOffset = e.getPoint(); }
            public void mouseDragged(MouseEvent e) {
                Point p = e.getLocationOnScreen();
                setLocation(p.x - dragOffset.x, p.y - dragOffset.y);
            }
        };
        topBar.addMouseListener(drag);
        topBar.addMouseMotionListener(drag);

        return topBar;
    }

    private JButton createTopButton(String label, String tooltip) {
        JButton btn = new JButton(label);
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
        btn.setBackground(new Color(0,0,0,0));
        btn.setOpaque(false);
        btn.setForeground(new Color(60,60,60));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setPreferredSize(new Dimension(0, 28));
        bar.setBackground(new Color(245, 245, 247));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(new EmptyBorder(4, 12, 4, 12));
        statusLabel.setForeground(new Color(80, 80, 80));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        right.setOpaque(false);
        JLabel tip = new JLabel("Tip: Press Ctrl+M to toggle menu");
        tip.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tip.setForeground(new Color(120, 120, 120));
        right.add(tip);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void addContentCards() {
        contentPanel.add(createWelcomePanel(), "Welcome");
        contentPanel.add(createSimpleCard("Product Management"), "Product");
        contentPanel.add(createSimpleCard("Sales Overview"), "Sales");
        contentPanel.add(createSimpleCard("Customer Records"), "Customer");
        contentPanel.add(createSimpleCard("Employee Directory"), "Employees");
        contentPanel.add(createSimpleCard("Reports & Analytics"), "Reports");
        contentPanel.add(createSimpleCard("Settings"), "Setting");
    }

    private JPanel createWelcomePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CONTENT_OFFWHITE);
        JLabel welcome = new JLabel("Welcome Admin");
        welcome.setFont(headingFont);
        welcome.setForeground(PRIMARY_BLUE);
        welcome.setBorder(new EmptyBorder(20,20,20,20));
        p.add(welcome);
        return p;
    }

    private JPanel createSimpleCard(String title) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(CONTENT_OFFWHITE);
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(menuFont.deriveFont(Font.BOLD, 22f));
        label.setForeground(new Color(60, 60, 60));
        label.setBorder(new EmptyBorder(20,20,20,20));
        container.add(label, BorderLayout.CENTER);

        JPanel meta = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        meta.setOpaque(false);
        JLabel small = new JLabel("Status: Active");
        small.setFont(contentFont);
        small.setForeground(new Color(100, 100, 100));
        meta.add(small);
        container.add(meta, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(container, BorderLayout.CENTER);
        wrapper.setBackground(CONTENT_OFFWHITE);
        return wrapper;
    }

    // ---------------- Menu interaction ----------------

    private void onMenuSelected(String menu) {

        if ("Logout".equalsIgnoreCase(menu)) {
            int option = JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) dispose();
            return;
        }

        // Product page - open external window but keep dashboard behavior stable
       if ("Product".equalsIgnoreCase(menu)) {
    // close Admin Dashboard
    this.dispose(); // closes current dashboard

    SwingUtilities.invokeLater(() -> {
        try {
            ProductMenuDialog productWindow = new ProductMenuDialog();
            productWindow.setLocationRelativeTo(null); // center
            productWindow.setVisible(true);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(null,
                "Unable to open Product window:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
    return;
}


        // Sales page - open external window
       // Sales page - open external window and close dashboard
if ("Sales".equalsIgnoreCase(menu)) {
    // close Admin Dashboard
    this.dispose();

    SwingUtilities.invokeLater(() -> {
        try {
            SalesMenuDialog salesWindow = new SalesMenuDialog();
            salesWindow.setLocationRelativeTo(null); // center
            salesWindow.setVisible(true);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(null,
                "Unable to open Sales window:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
    return;
}
// Customer page - open dialog centered on right content panel
if ("Customer".equalsIgnoreCase(menu)) {

    SwingUtilities.invokeLater(() -> {
        try {
            CustomerDialog dialog =
                new CustomerDialog(AdminDashboardDialog.this, contentPanel);
            dialog.setVisible(true);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this,
                "Unable to open Customer dialog:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    return;
}

 if ("Employees".equals(menu)) {

    // 1. Close Admin Dashboard
    this.dispose();

    // 2. Open Employee Dialog
    SwingUtilities.invokeLater(() -> {
        EmployeeDialog employeeDialog = new EmployeeDialog();
        employeeDialog.setLocationRelativeTo(null);
        employeeDialog.setVisible(true);
    });
    return;
}

if ("Reports".equalsIgnoreCase(menu)) {

    this.dispose();

    SwingUtilities.invokeLater(() -> {
        try {
            ReportDialog reportDialog = new ReportDialog();
            reportDialog.setLocationRelativeTo(null);
            reportDialog.setVisible(true);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(
                null,
                "Unable to open Reports window:\n" + ex.toString(),
                "Reports Error",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    });
    return;
}



// Setting page - open SettingDialog and close dashboard
if ("Setting".equalsIgnoreCase(menu)) {

    // close Admin Dashboard
    this.dispose();

    SwingUtilities.invokeLater(() -> {
        try {
            SettingDialog settingWindow = new SettingDialog();
            settingWindow.setLocationRelativeTo(null); // center on screen
            settingWindow.setVisible(true);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(null,
                "Unable to open Setting window:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    return;
}



        statusLabel.setText("Opening " + menu + "...");
        menuItems.forEach(it -> it.setSelected(it.getText().equalsIgnoreCase(menu)));
        // move indicator to newly selected item
        menuItems.stream().filter(RoundedMenuItem::isSelected).findFirst().ifPresent(it -> moveIndicatorTo(it, false));

        contentCards.show(contentPanel, menu);
        Timer t = new Timer(450, e -> statusLabel.setText("Ready"));
        t.setRepeats(false);
        t.start();
    }

    private void filterMenu(String q) {
        String text = q == null ? "" : q.trim().toLowerCase();
        menuItems.forEach(item -> {
            boolean match = item.getText().toLowerCase().contains(text);
            item.setVisible(match);
        });
        menuGrid.revalidate();
        menuGrid.repaint();
    }

    // ---------------- Collapse / Expand ----------------

    private Timer collapseTimer;
    private int collapseTarget;
    private int collapseStep;

    private void toggleCollapse() {
        if (collapseTimer != null && collapseTimer.isRunning()) return;
        int currentWidth = leftPanelPaint.getWidth();
        int total = getContentPane().getWidth();
        int expandedWidth = Math.max(200, (int)(total * 0.30));
        int collapsedWidth = 60;

        if (!collapsed) collapseTarget = collapsedWidth; else collapseTarget = expandedWidth;
        collapseStep = (collapseTarget - currentWidth) / 12;
        if (collapseStep == 0) collapseStep = collapseTarget > currentWidth ? 10 : -10;

        collapseTimer = new Timer(12, null);
        collapseTimer.addActionListener(new ActionListener() {
            int x = currentWidth;
            @Override
            public void actionPerformed(ActionEvent e) {
                x += collapseStep;
                boolean done = (collapseStep > 0 && x >= collapseTarget) || (collapseStep < 0 && x <= collapseTarget);
                if (done) {
                    x = collapseTarget;
                    collapseTimer.stop();
                    collapsed = !collapsed;
                    collapseBtn.setText(collapsed ? "\u25B6" : "\u25C0");
                }
                leftPanelPaint.setPreferredSize(new Dimension(Math.max(60, x), getHeight()));
                leftPanelPaint.revalidate();
            }
        });
        collapseTimer.start();
    }

    private void animateWelcome() {
        Component comp = contentPanel.getComponent(0);
        Point orig = comp.getLocation();
        final int offset = 16;
        comp.setLocation(orig.x + offset, orig.y);
        Timer t = new Timer(8, null);
        t.addActionListener(new ActionListener() {
            int steps = 18;
            int i = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                i++;
                int dx = (int)((double)offset * (1.0 - (double)i/steps));
                comp.setLocation(orig.x + dx, orig.y);
                if (i >= steps) {
                    comp.setLocation(orig.x, orig.y);
                    t.stop();
                }
            }
        });
        t.start();
    }

    private void toggleMaximize() {
        // Use screen bounds to cover the entire screen (primary monitor)
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle screenBounds = env.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());

        Rectangle fullBounds = new Rectangle(
                screenBounds.x,
                screenBounds.y,
                screenBounds.width,
                screenBounds.height
        );

        if (!maximized) {
            previousBounds = getBounds();
            // set to full screen bounds
            setBounds(fullBounds);
        } else {
            if (previousBounds != null) setBounds(previousBounds);
            else setSize(baseWindowWidth,700);
            setLocationRelativeTo(null);
        }
        maximized = !maximized;
        enforceLeftProportion();
    }

    private void enforceLeftProportion() {
        int totalWidth = getContentPane().getWidth();
        if (totalWidth <= 0) return;
        int leftWidth = collapsed ? 60 : Math.max(200, (int)(totalWidth * 0.30));
        leftPanelPaint.setPreferredSize(new Dimension(leftWidth, getHeight()));
        leftPanelPaint.revalidate();
        leftPanelPaint.repaint();
    }

    // ---------------- Indicator movement ----------------

    private void moveIndicatorTo(RoundedMenuItem item, boolean immediate) {
        if (item == null) return;
        // compute bounds relative to leftPanelPaint
        Rectangle itemBounds = SwingUtilities.convertRectangle(item.getParent(), item.getBounds(), leftPanelPaint);
        int targetY = itemBounds.y + 8; // some padding
        int targetH = Math.max(20, itemBounds.height - 16);
        indicatorTargetY = targetY;
        indicatorHeight = targetH;

        if (immediate) {
            indicatorY = indicatorTargetY;
            leftPanelPaint.repaint();
            return;
        }

        if (indicatorTimer != null && indicatorTimer.isRunning()) indicatorTimer.stop();

        indicatorTimer = new Timer(12, null);
        indicatorTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (indicatorY < 0) indicatorY = indicatorTargetY;
                int dy = (indicatorTargetY - indicatorY);
                if (Math.abs(dy) <= 2) {
                    indicatorY = indicatorTargetY;
                    indicatorTimer.stop();
                } else {
                    indicatorY += Math.max(-6, Math.min(6, dy));
                }
                leftPanelPaint.repaint();
            }
        });
        indicatorTimer.start();
    }

    private void rescaleUi() {
        int w = getWidth();
        if (w <= 0) return;
        float scale = (float) w / (float) baseWindowWidth;
        // clamp scale to a reasonable range
        scale = Math.max(0.75f, Math.min(1.8f, scale));

        menuFont = new Font("Tahoma", Font.BOLD, Math.max(12, Math.round(16 * scale)));
        contentFont = new Font("Tahoma", Font.PLAIN, Math.max(12, Math.round(14 * scale)));
        headingFont = new Font("Tahoma", Font.BOLD, Math.max(20, Math.round(28 * scale)));

        for (RoundedMenuItem it : menuItems) {
            it.setFont(menuFont);
            it.revalidate();
            it.repaint();
        }

        // update content panel children fonts (best-effort)
        for (Component c : contentPanel.getComponents()) {
            UIManager.put("Label.font", contentFont);
            c.revalidate();
            c.repaint();
        }

        leftPanelPaint.revalidate();
        leftPanelPaint.repaint();
    }

    // ---------------- Custom components ----------------

    // RoundedPanel with arc support
    private abstract static class RoundedPanel extends JPanel {
        private final int arc;

        RoundedPanel(int arc) {
            super(new BorderLayout());
            this.arc = arc;
            setOpaque(false);
        }

        public int getArc() { return arc; }
    }

    // Rounded menu item (card-like)
    private class RoundedMenuItem extends JPanel {
        private final String text;
        private boolean hover = false;
        private boolean selected = false;
        private final Color bg = new Color(255, 255, 255, 70);      // normal background
private final Color hoverBg = new Color(255, 255, 255, 120); // hover background
private final Color selBg = new Color(255, 255, 255, 180);   // selected background

        private Color textColor = Color.WHITE;
        private final List<ActionListener> listeners = new ArrayList<>();
        private final List<Runnable> hoverListeners = new ArrayList<>();
        private final List<Runnable> hoverExitListeners = new ArrayList<>();

        RoundedMenuItem(String text) {
            this.text = text;
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(14, 18, 14, 18));
            setToolTipText(text);
            setFocusable(true);
            setLayout(new BorderLayout());

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); fireHover(); }
                @Override
                public void mouseExited(MouseEvent e) { hover = false; repaint(); fireHoverExit(); }
                @Override
                public void mouseClicked(MouseEvent e) { performAction(); }
            });

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) performAction();
                }
            });

            // accessibility: allow focusing
            setFocusTraversalKeysEnabled(true);
        }

        private void fireHover() {
            for (Runnable r : hoverListeners) r.run();
        }
        private void fireHoverExit() {
            for (Runnable r : hoverExitListeners) r.run();
        }
        void addHoverListener(Runnable r) { hoverListeners.add(r); }
        void addHoverExitListener(Runnable r) { hoverExitListeners.add(r); }

        private void performAction() {
            menuItems.forEach(it -> it.setSelected(false));
            setSelected(true);
            ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, text);
            listeners.forEach(l -> l.actionPerformed(ae));
        }

        void addActionListener(ActionListener l) { listeners.add(l); }
        void setSelected(boolean sel) { this.selected = sel; repaint(); }
        boolean isSelected() { return selected; }
        String getText() { return text; }
        void setTextColor(Color c) { this.textColor = c; repaint(); }

        @Override
        public void setFont(Font f) { super.setFont(f); repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            int arc = 12;
            int w = getWidth();
            int h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color back = selected ? selBg : (hover ? hoverBg : bg);
            g2.setColor(back);
            g2.fillRoundRect(0,0,w,h,arc,arc);

            g2.setColor(new Color(255,255,255,90));  // stronger border
g2.setStroke(new BasicStroke(1.2f));
g2.drawRoundRect(0, 0, w-1, h-1, arc, arc);


            // draw text
            Font f = getFont() != null ? getFont() : menuFont;
            g2.setFont(f);
            FontMetrics fm = g2.getFontMetrics();
            int tx = 12;
            int ty = (h - fm.getHeight())/2 + fm.getAscent();
            g2.setColor(textColor);
            g2.drawString(text, tx, ty);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------------- Main ----------------

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            AdminDashboardDialog frame = new AdminDashboardDialog();
            frame.setVisible(true);
        });
    }
}