package src;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * EmployeeDialog
 * 
 * Employee Management Interface with similar design to ProductMenuDialog
 * Features: Add Employee, Update Employee Info, Struck-Off Employee, Manage Roles
 */
public class EmployeeDialog extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color GRADIENT_BLUE = new Color(0, 200, 255);
    private static final Color MENU_BOX_BG = new Color(255, 255, 255, 30);
    private static final Color MENU_BOX_HOVER = new Color(255, 255, 255, 80);
    private static final Color CONTENT_OFFWHITE = new Color(250, 251, 253);
    private static final Color SHADOW = new Color(0, 0, 0, 30);
    private static final Color ACCENT = new Color(10, 90, 200);

    // Base metrics for responsive scaling
    private final int BASE_WIDTH = 1000;
    private final int BASE_LEFT_WIDTH = 320;
    private final int BASE_MENU_BOX_HEIGHT = 36;
    private final int BASE_TOPBAR_HEIGHT = 48;
    private final int BASE_CORNER_ARC = 18;

    // Scaled values (updated on resize)
    private int menuBoxHeight = BASE_MENU_BOX_HEIGHT;
    private int cornerArc = BASE_CORNER_ARC;
    private Font menuFont = new Font("Tahoma", Font.BOLD, 15);
    private Font headingFont = new Font("Tahoma", Font.BOLD, 28); // Reduced from 32
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font noteFont = new Font("Segoe UI", Font.PLAIN, 14);

    private final RoundedPanel leftPanel;
    private final JPanel contentPanel;
    private final JPanel menuGrid;
    private Point dragOffset;
    private boolean maximized = false;
    private Rectangle previousBounds;

    // Menu items
    private final MenuBox[] menuBoxes;

    // Indicator for hovered/selected menu
    private int indicatorY = -1;
    private int indicatorTargetY = -1;
    private int indicatorHeight = 0;
    private Timer indicatorTimer;

    public EmployeeDialog() {
        super("Employee Management");
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Create top bar and left panel
        JPanel topBar = createTopBar();
        leftPanel = createLeftGradientPanel();

        // Employee management menu items
        String[] menuItemsText = {
            "Add Employee",
            "Update Employee Info", 
            "Struck-Off Employee",
            "Manage Roles"
        };

        menuBoxes = new MenuBox[menuItemsText.length];
        menuGrid = createMenuGrid(menuItemsText);

        JPanel leftInner = new JPanel(new BorderLayout());
        leftInner.setOpaque(false);
        leftInner.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Branding section
        JLabel brand = new JLabel("Tiles Factory");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel subtitle = new JLabel("Employee Management");
        subtitle.setForeground(new Color(255, 255, 255, 200));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel brandBox = new JPanel(new GridLayout(2, 1));
        brandBox.setOpaque(false);
        brandBox.add(brand);
        brandBox.add(subtitle);
        brandBox.setBorder(new EmptyBorder(0, 0, 12, 0));
        leftInner.add(brandBox, BorderLayout.NORTH);
        leftInner.add(menuGrid, BorderLayout.CENTER);
        leftPanel.add(leftInner, BorderLayout.CENTER);

        // Content panel
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(CONTENT_OFFWHITE);
        addWelcomeContent();

        // Root layout
        JPanel root = new JPanel(new BorderLayout());
        root.add(topBar, BorderLayout.NORTH);
        root.add(leftPanel, BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);
        getContentPane().add(root);

        // Handle resizing
        addComponentListener(new ComponentAdapter() {
            @Override 
            public void componentResized(ComponentEvent e) {
                enforceLeftProportion();
                rescaleUi();
                // Reposition indicator to currently selected item
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

        // Ensure proper scaling after showing
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

        JLabel title = new JLabel(" Employee Management");
        title.setFont(titleFont);
        title.setForeground(new Color(60, 60, 60));
        topBar.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        controls.setOpaque(false);

        // Back button to return to Admin Dashboard
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
                    JOptionPane.showMessageDialog(null, 
                        "Failed to open Admin Dashboard:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        // Window control buttons
        JButton minBtn = createTopButton("\u2014");
        JButton maxBtn = createTopButton("▢");
        JButton closeBtn = createTopButton("X");
        closeBtn.setForeground(Color.RED);

        minBtn.addActionListener(e -> setState(Frame.ICONIFIED));
        maxBtn.addActionListener(e -> toggleMaximize());
        closeBtn.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, 
                "Close Employee Management?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) dispose();
        });

        controls.add(back);
        controls.add(minBtn);
        controls.add(maxBtn);
        controls.add(closeBtn);
        topBar.add(controls, BorderLayout.EAST);

        // Make top bar draggable
        MouseAdapter drag = new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) { 
                dragOffset = e.getPoint(); 
            }
            
            @Override 
            public void mouseDragged(MouseEvent e) {
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
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        b.setBackground(new Color(255, 255, 255));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JButton createTopButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private RoundedPanel createLeftGradientPanel() {
        RoundedPanel left = new RoundedPanel(BASE_CORNER_ARC) {
            @Override 
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create rounded rectangle clip
                RoundRectangle2D rr = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), getArc(), getArc());
                g2.setClip(rr);
                
                // Draw gradient background
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_BLUE, 0, getHeight(), GRADIENT_BLUE);
                g2.setPaint(gp);
                g2.fill(rr);

                // Subtle border
                g2.setPaint(new Color(255, 255, 255, 30));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rr);

                // Draw moving indicator
                if (indicatorY >= 0 && indicatorHeight > 0) {
                    g2.setPaint(ACCENT);
                    int indW = Math.max(6, Math.round(6f * scaleFactor()));
                    int x = getWidth() - 14 - indW;
                    g2.fillRoundRect(x, indicatorY, indW, indicatorHeight, indW, indW);
                }

                // Subtle footer shadow
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(0, getHeight() - 6, Math.max(0, getWidth()), 6, getArc(), getArc());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(BASE_LEFT_WIDTH, 0));
        return left;
    }

    private JPanel createMenuGrid(String[] items) {
        // Increased vertical gap between menu items
        int verticalGap = Math.max(12, Math.round(12 * scaleFactor()));
        JPanel grid = new JPanel(new GridLayout(items.length, 1, 0, verticalGap));
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
            label.setForeground(Color.WHITE);
            add(label, BorderLayout.WEST);
            setPreferredSize(new Dimension(200, menuBoxHeight));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(text);

            // Mouse listeners for hover and click
            addMouseListener(new MouseAdapter() {
                @Override 
                public void mouseEntered(MouseEvent e) { 
                    hover = true; 
                    repaint(); 
                    moveIndicatorTo(MenuBox.this, false); 
                }
                
                @Override 
                public void mouseExited(MouseEvent e) { 
                    hover = false; 
                    repaint(); 
                    MenuBox sel = findSelectedBox(); 
                    if (sel != null) moveIndicatorTo(sel, false); 
                }
                
                @Override 
                public void mouseClicked(MouseEvent e) { 
                    onClick(text); 
                    setSelected(true); 
                }
            });

            // Keyboard support
            setFocusable(true);
            addKeyListener(new KeyAdapter() {
                @Override 
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
                        onClick(text); 
                        setSelected(true); 
                    }
                }
            });
        }

        void setSelected(boolean sel) {
            if (sel) {
                // Deselect other menu boxes
                for (MenuBox mb : menuBoxes) {
                    if (mb != this) mb.selected = false;
                }
            }
            this.selected = sel;
            repaint();
        }

        boolean isSelected() { 
            return selected; 
        }

        @Override 
        protected void paintComponent(Graphics g) {
            int arc = Math.max(12, Math.round(cornerArc * scaleFactor()));
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Choose background based on state
            Color fill = selected ? MENU_BOX_HOVER : (hover ? MENU_BOX_HOVER : MENU_BOX_BG);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            // Border
            Color borderColor = new Color(255, 255, 255, 140);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, Math.max(0, getWidth() - 1), Math.max(0, getHeight() - 1), arc, arc);

            // Subtle top gloss
            g2.setColor(new Color(255, 255, 255, 12));
            int glossH = Math.max(4, Math.round(4 * scaleFactor()));
            g2.fillRoundRect(2, 2, Math.max(0, getWidth() - 4), glossH, arc, arc);

            // Ensure font and color are set
            label.setFont(menuFont);
            label.setForeground(Color.WHITE);
            g2.dispose();
            super.paintComponent(g);
        }

        private void onClick(String text) {
            if ("Add Employee".equals(text)) {
                // Open AddEmployeeDialog
                SwingUtilities.invokeLater(() -> {
                    try {
                        // First check if AddEmployeeDialog exists
                        Class.forName("src.AddEmployeeDialog");
                        
                        AddEmployeeDialog dlg = new AddEmployeeDialog(EmployeeDialog.this, contentPanel);
                        dlg.setVisible(true);
                        
                        // Log that dialog was opened
                        System.out.println("Add Employee dialog opened");
                    } catch (ClassNotFoundException e) {
                        JOptionPane.showMessageDialog(EmployeeDialog.this,
                            "Add Employee functionality is not yet implemented.\n\n" +
                            "This feature is currently under development.",
                            "Feature Coming Soon",
                            JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(EmployeeDialog.this,
                            "Error opening Add Employee dialog: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
                return;
            }
            
            if ("Update Employee Info".equals(text)) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        UpdateEmployeeInfoDialog dlg = new UpdateEmployeeInfoDialog(EmployeeDialog.this, contentPanel);
                        dlg.setVisible(true);
                        
                        // Log that dialog was opened
                        System.out.println("Update Employee Info dialog opened");
                        
                        // Note: The UpdateEmployeeInfoDialog doesn't have isUpdateSuccessful() method
                        // You would need to add this functionality if needed
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(EmployeeDialog.this,
                            "Error opening Update Employee Info dialog: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
                return;
            }
            
            if ("Struck-Off Employee".equals(text)) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        StruckOffDialog dlg = new StruckOffDialog(EmployeeDialog.this, contentPanel);
                        dlg.setVisible(true);
                        
                        // Log that dialog was opened
                        System.out.println("Struck-Off Employee dialog opened");
                        
                        // Note: The StruckOffDialog doesn't have isStruckOffSuccessful() method
                        // You would need to add this functionality if needed
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(EmployeeDialog.this,
                            "Error opening Struck-Off Employee dialog: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
                return;
            }
            
            if ("Manage Roles".equals(text)) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        // First check if ManageRolesDialog exists
                        Class.forName("src.ManageRolesDialog");
                        
                        ManageRolesDialog dlg = new ManageRolesDialog(EmployeeDialog.this, contentPanel);
                        dlg.setVisible(true);
                        
                        // Log that dialog was opened
                        System.out.println("Manage Roles dialog opened");
                    } catch (ClassNotFoundException e) {
                        JOptionPane.showMessageDialog(EmployeeDialog.this,
                            "Manage Roles functionality is not yet implemented.\n\n" +
                            "This feature is currently under development.",
                            "Feature Coming Soon",
                            JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(EmployeeDialog.this,
                            "Error opening Manage Roles dialog: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
                return;
            }

            // Default behavior for other buttons
            showButtonContent(text);
        }
    }

    private void showButtonContent(String buttonText) {
        contentPanel.removeAll();
        
        JPanel contentCard = new JPanel(new GridBagLayout());
        contentCard.setBackground(CONTENT_OFFWHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Show the button name as main heading in BLACK color
        JLabel heading = new JLabel(buttonText);
        heading.setFont(headingFont);
        heading.setForeground(Color.BLACK); // Changed to black
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        contentCard.add(heading, gbc);
        
        // Add a brief description
        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 20, 20);
        String description = getButtonDescription(buttonText);
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, Math.max(14, Math.round(16 * scaleFactor()))));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentCard.add(descLabel, gbc);
        
        contentPanel.add(contentCard);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private String getButtonDescription(String buttonText) {
        switch (buttonText) {
            case "Add Employee":
                return "Add new employees to the system";
            case "Update Employee Info":
                return "Modify existing employee information";
            case "Struck-Off Employee":
                return "Remove employees from active records";
            case "Manage Roles":
                return "Configure employee roles and permissions";
            default:
                return "Employee management functionality";
        }
    }

    private MenuBox findSelectedBox() {
        for (MenuBox mb : menuBoxes) {
            if (mb != null && mb.isSelected()) return mb;
        }
        // Fallback to first menu item if none selected
        for (MenuBox mb : menuBoxes) {
            if (mb != null) return mb;
        }
        return null;
    }

    private void addWelcomeContent() {
        contentPanel.removeAll();
        
        JPanel welcomeCard = new JPanel(new GridBagLayout());
        welcomeCard.setBackground(CONTENT_OFFWHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Welcome title - centered and smaller
        JLabel title = new JLabel("Employee Management System");
        title.setFont(headingFont);
        title.setForeground(PRIMARY_BLUE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeCard.add(title, gbc);
        
        // Subtitle - centered
        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 20, 20);
        JLabel subtitle = new JLabel("Manage your workforce efficiently");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, Math.max(14, Math.round(16 * scaleFactor()))));
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeCard.add(subtitle, gbc);
        
        // NO STATS PANEL - Removed completely
        
        contentPanel.add(welcomeCard);
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
            if (previousBounds != null) {
                setBounds(previousBounds);
            } else {
                setSize(BASE_WIDTH, 650);
                setLocationRelativeTo(null);
            }
        }
        maximized = !maximized;
        enforceLeftProportion();
        rescaleUi();
    }

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

        if (indicatorTimer != null && indicatorTimer.isRunning()) {
            indicatorTimer.stop();
        }
        
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
        
        // Update metrics
        menuBoxHeight = Math.max(28, Math.round(BASE_MENU_BOX_HEIGHT * s));
        cornerArc = Math.max(8, Math.round(BASE_CORNER_ARC * s));
        menuFont = new Font("Tahoma", Font.BOLD, Math.max(12, Math.round(15 * s)));
        headingFont = new Font("Tahoma", Font.BOLD, Math.max(18, Math.round(28 * s))); // Adjusted scaling
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

        // Update left panel
        leftPanel.setArc(cornerArc);
        leftPanel.revalidate();
        leftPanel.repaint();

        // Update content panel
        for (Component c : contentPanel.getComponents()) {
            c.revalidate();
            c.repaint();
        }
    }

    // ================= INNER CLASSES =================

    // RoundedPanel with arc support
    private abstract static class RoundedPanel extends JPanel {
        private int arc;
        
        RoundedPanel(int arc) {
            super(new BorderLayout());
            this.arc = arc;
            setOpaque(false);
        }
        
        public int getArc() { 
            return arc; 
        }
        
        public void setArc(int arc) { 
            this.arc = arc; 
            repaint(); 
        }
    }
    // ================= MAIN METHOD =================

    public static void main(String[] args) {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> {
            EmployeeDialog dialog = new EmployeeDialog();
            dialog.setVisible(true);
        });
    }
}