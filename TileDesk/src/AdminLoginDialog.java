package src;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class AdminLoginDialog extends JDialog {

    private RoundedTextField usernameField;
    private RoundedPasswordField passwordField;
    private RoundedButton loginButton, clearButton;
    private JCheckBox showPasswordCB;
    private JCheckBox rememberCB;
    private JLabel statusLabel;

    private boolean maximized = false;
    private Point initialClick;

    private final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private final Color GRADIENT_BLUE = new Color(0, 200, 255);

    private ShadowPanel loginPanel; // for animation

    // credentials (for demo)
    private final String CORRECT_USER = "Admin";
    private final String CORRECT_PASS = "admin123";

    // login attempts / lockout
    private int failedAttempts = 0;
    private final int MAX_ATTEMPTS = 3;
    private final int LOCKOUT_SECONDS = 15;
    private Timer lockoutTimer;

    public AdminLoginDialog(Frame owner) {
        super(owner, "Admin Login", true);
        setSize(800, 450);
        setLocationRelativeTo(owner);
        setUndecorated(true);
        setResizable(false); // not resizable
        setLayout(new BorderLayout());

        // ===== Top Bar =====
        add(createTopBar(), BorderLayout.NORTH);

        // ===== Main Panel =====
        JPanel mainPanel = new JPanel(null) { // null layout for custom placement & animation
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                // left gradient (left panel background)
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_BLUE, 0, getHeight(), GRADIENT_BLUE);
                g2.setPaint(gp);
                int mid = getWidth() / 2;
                g2.fillRect(0, 0, mid, getHeight());

                // ensure right side (where the login panel sits) is painted white
                // this removes the thin blue line that could appear behind rounded panel edges
                g2.setColor(Color.WHITE);
                g2.fillRect(mid, 0, getWidth() - mid, getHeight());
            }
        };
        mainPanel.setBounds(0,0,800,450);

        // ===== Left Panel =====
        int leftWidth = 200; // decreased width as requested
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // keep transparent - gradient is drawn by parent
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setBounds(0,0,leftWidth,450);

        JLabel brand = new JLabel("Tiles Factory");
        // use a font with a clear 'g'
        brand.setFont(new Font("Segoe UI", Font.BOLD, 28));
        brand.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Admin Control System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.WHITE);

        GridBagConstraints lb = new GridBagConstraints();
        lb.gridy = 0;
        lb.insets = new Insets(10, 10, 6, 10);
        lb.anchor = GridBagConstraints.NORTHWEST;
        leftPanel.add(brand, lb);
        lb.gridy = 1;
        lb.insets = new Insets(0, 12, 6, 10);
        leftPanel.add(subtitle, lb);

        mainPanel.add(leftPanel);

        // ===== Login Panel =====
        loginPanel = new ShadowPanel();
        loginPanel.setBackground(Color.WHITE);
        // reduced top padding to bring the content up so the whole panel is more visible
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        loginPanel.setLayout(new GridBagLayout());

        // start the panel slightly off-screen to the left for the slide-in animation
        int startX = -500;
        int panelWidth = 420;
        int panelHeight = 360;
        int finalX = leftWidth + 80; // moves to the right of left panel with small gap
        int finalY = 30; // moved upward so full panel is more visible
        loginPanel.setBounds(startX, finalY, panelWidth, panelHeight);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Headline - use Tahoma for a clearer lowercase 'g'
        JLabel headline = new JLabel("Admin Login");
        headline.setFont(new Font("Tahoma", Font.BOLD, 28)); // clearer 'g'
        headline.setForeground(PRIMARY_BLUE); // match left sky panel color
        headline.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0,0,18,0);
        loginPanel.add(headline, gbc);

        // Username
        gbc.gridy++;
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginPanel.add(userLabel, gbc);

        gbc.gridy++;
        usernameField = new RoundedTextField();
        // Do NOT prefill username — keep empty so user types it
        usernameField.setText("");
        usernameField.setToolTipText("Enter your admin username");
        loginPanel.add(usernameField, gbc);

        // Password
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginPanel.add(passLabel, gbc);

        gbc.gridy++;
        passwordField = new RoundedPasswordField();
        // Do NOT prefill password — keep empty so user types it
        passwordField.setText("");
        passwordField.setToolTipText("Enter your password");
        loginPanel.add(passwordField, gbc);

        // Show password & Remember me
        gbc.gridy++;
        JPanel opts = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        opts.setBackground(Color.WHITE);
        showPasswordCB = new JCheckBox("Show password");
        showPasswordCB.setBackground(Color.WHITE);
        showPasswordCB.setFocusable(false);
        showPasswordCB.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCB.isSelected() ? (char)0 : '\u2022');
        });

        rememberCB = new JCheckBox("Remember me");
        rememberCB.setBackground(Color.WHITE);
        rememberCB.setFocusable(false);
        rememberCB.setToolTipText("Remember me (demo only: stored in memory during session).");

        opts.add(showPasswordCB);
        opts.add(rememberCB);
        loginPanel.add(opts, gbc);

        // Buttons
        loginButton = new RoundedButton("Login", PRIMARY_BLUE);
        clearButton = new RoundedButton("Clear", PRIMARY_BLUE);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(100, 42));
        clearButton.setPreferredSize(new Dimension(100, 42));
        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);

        gbc.gridy++;
        gbc.insets = new Insets(14, 0, 6, 0);
        loginPanel.add(buttonPanel, gbc);

        // Status / Footer
        gbc.gridy++;
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(statusLabel, gbc);

        mainPanel.add(loginPanel);
        add(mainPanel, BorderLayout.CENTER);

        // Actions
        loginButton.addActionListener(e -> handleLogin());
        clearButton.addActionListener(e -> clearFields());

        // keyboard actions: Enter => login, Esc => close
        getRootPane().setDefaultButton(loginButton);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Enter on password triggers login as well
        passwordField.addActionListener(e -> handleLogin());

        // ===== Animate the panel (slide-in) =====
        Timer timer = new Timer(10, null);
        timer.addActionListener(new ActionListener() {
            int x = startX;
            @Override
            public void actionPerformed(ActionEvent e) {
                x += 20;
                if (x >= finalX) x = finalX;
                loginPanel.setBounds(x, finalY, panelWidth, panelHeight);
                if (x == finalX) timer.stop();
            }
        });
        timer.setInitialDelay(200);
        timer.start();

        // initialize lockout timer (disabled until needed)
        lockoutTimer = new Timer(1000, new ActionListener() {
            int remaining = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                remaining--;
                statusLabel.setText("Too many attempts. Try again in " + remaining + "s");
                if (remaining <= 0) {
                    lockoutTimer.stop();
                    loginButton.setEnabled(true);
                    statusLabel.setText("You may try logging in again.");
                    failedAttempts = 0;
                }
            }
            public void setRemaining(int secs) { this.remaining = secs; }
        });

        // small polish
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
    }

    // ===== Shadow Panel =====
    static class ShadowPanel extends JPanel {
        private final int shadowSize = 8;

        public ShadowPanel() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // drop shadow
            g2.setColor(new Color(0,0,0,50));
            for(int i=0;i<shadowSize;i++) {
                g2.drawRoundRect(i,i,getWidth()-i*2-1,getHeight()-i*2-1,18,18);
            }

            // panel - ensure fill covers the top edge so underlying gradient doesn't show as a thin line
            g2.setColor(getBackground());
            g2.fillRoundRect(0,0,getWidth()-shadowSize,getHeight()-shadowSize,18,18);

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // ===== Rounded Inputs & Buttons =====
    static class RoundedTextField extends JTextField {
    private Color borderColor = new Color(200,200,200);
    private String placeholder = "Enter username";

    public RoundedTextField() {
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setPreferredSize(new Dimension(260,36));
        setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
        setOpaque(false);

        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e){
                borderColor = new Color(0,123,255);
                repaint();
            }
            public void focusLost(FocusEvent e){
                borderColor = new Color(200,200,200);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);

        // border
        g2.setStroke(new BasicStroke(1 + (hasFocus()?1:0)));
        g2.setColor(borderColor);
        g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,18,18);

        super.paintComponent(g2);

        // placeholder text
        if(getText().isEmpty() && !hasFocus()){
            g2.setColor(Color.GRAY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            g2.drawString(placeholder, 12, getHeight()/2 + 5);
        }

        g2.dispose();
    }
}


    static class RoundedPasswordField extends JPasswordField {
    private Color borderColor = new Color(200,200,200);
    private String placeholder = "Enter password";

    public RoundedPasswordField() {
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setPreferredSize(new Dimension(260,36));
        setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
        setOpaque(false);
        setEchoChar('\u2022');

        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e){
                borderColor = new Color(0,123,255);
                repaint();
            }
            public void focusLost(FocusEvent e){
                borderColor = new Color(200,200,200);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);

        // border
        g2.setStroke(new BasicStroke(1 + (hasFocus()?1:0)));
        g2.setColor(borderColor);
        g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,18,18);

        super.paintComponent(g2);

        // placeholder
        if(getPassword().length == 0 && !hasFocus()){
            g2.setColor(Color.GRAY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            g2.drawString(placeholder, 12, getHeight()/2 + 5);
        }

        g2.dispose();
    }
}


    static class RoundedButton extends JButton {
        private Color bg;
        private final Color originalBg;
        private final Color hoverBg;
        public RoundedButton(String text, Color bg){
            super(text);
            this.bg = bg;
            this.originalBg = bg;
            this.hoverBg = bg.darker();
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){ RoundedButton.this.bg = hoverBg; repaint(); }
                public void mouseExited(MouseEvent e){ RoundedButton.this.bg = originalBg; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // ===== Top Bar & Window Buttons =====
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_BLUE);
        topBar.setPreferredSize(new Dimension(0, 40));

        JLabel title = new JLabel("  Admin Login");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        controls.setOpaque(false);

        // MINIMIZE REMOVED - only the close button remains as requested
        JButton closeBtn = createWindowButton("X");
        closeBtn.setForeground(Color.WHITE);

        closeBtn.addActionListener(e -> dispose());

        // Only add close button
        controls.add(closeBtn);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(controls, BorderLayout.EAST);

        // draggable top bar
        topBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e){ initialClick = e.getPoint(); }
        });
        topBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e){
                setLocation(getX() + e.getX() - initialClick.x, getY() + e.getY() - initialClick.y);
            }
        });

        return topBar;
    }

    private JButton createWindowButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY_BLUE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void handleLogin() {
        if (!loginButton.isEnabled()) return;

        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if(user.isEmpty()||pass.isEmpty()){
            JOptionPane.showMessageDialog(this,"Please enter all fields","Missing fields", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Basic username validation (letters, numbers, ., -, _ allowed)
        if(!user.matches("[\\w.\\-]+")){
            JOptionPane.showMessageDialog(this,"Username contains invalid characters","Invalid username", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // compare case-insensitive for username, exact for password
        if(user.equalsIgnoreCase(CORRECT_USER) && pass.equals(CORRECT_PASS)){
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("Login Successful. Welcome, " + CORRECT_USER + "!");

            // Open AdminDashboardDialog after closing this dialog
            // Dispose first to close modal dialog, then show dashboard
            dispose();

            SwingUtilities.invokeLater(() -> {
                try {
                    AdminDashboardDialog dashboard = new AdminDashboardDialog();
                    dashboard.setVisible(true);
                } catch (Exception ex) {
                    // If AdminDashboardDialog not available, show an error message
                    JOptionPane.showMessageDialog(null, "Login succeeded but dashboard failed to open:\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

        } else {
            failedAttempts++;
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Invalid credentials. Attempt " + failedAttempts + " of " + MAX_ATTEMPTS);
            if(failedAttempts >= MAX_ATTEMPTS){
                loginButton.setEnabled(false);
                // start lockout countdown
                if(lockoutTimer.isRunning()) lockoutTimer.stop();
                Timer countdown = new Timer(1000, null);
                final int[] remaining = {LOCKOUT_SECONDS};
                countdown.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        remaining[0]--;
                        statusLabel.setText("Too many attempts. Try again in " + remaining[0] + "s");
                        if (remaining[0] <= 0) {
                            countdown.stop();
                            loginButton.setEnabled(true);
                            statusLabel.setForeground(Color.DARK_GRAY);
                            statusLabel.setText("You may try logging in again.");
                            failedAttempts = 0;
                        }
                    }
                });
                statusLabel.setText("Too many attempts. Try again in " + LOCKOUT_SECONDS + "s");
                countdown.setInitialDelay(1000);
                countdown.start();
            }
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
        statusLabel.setText(" ");
    }

    public static void main(String[] args){
        // enable anti-aliased text globally for better clarity (helps the 'g' glyph)
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            AdminLoginDialog dialog = new AdminLoginDialog(null);
            dialog.setVisible(true);
        });
    }
}