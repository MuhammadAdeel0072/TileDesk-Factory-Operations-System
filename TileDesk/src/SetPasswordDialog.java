package src;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * SetPasswordDialog - Modern Password Change Form (Same UI/UX as AddProductDialog)
 */
public class SetPasswordDialog extends JDialog {
    // Color palette matching AddProductDialog
    private static final Color SKY_BLUE = new Color(135, 206, 250);
    private static final Color DARK_SKY_BLUE = new Color(30, 144, 255);
    private static final Color LIGHT_BLUE_BG = new Color(240, 248, 255);
    private static final Color INPUT_BG = new Color(255, 255, 255);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(200, 220, 240);
    private static final Color LABEL_COLOR = new Color(60, 60, 80);
    private static final Color REQUIRED_COLOR = new Color(220, 53, 69);
    private static final Color OPTIONAL_COLOR = new Color(108, 117, 125);
    private static final Color HOVER_RED = new Color(220, 80, 80);
    
    // Fonts matching AddProductDialog but with bolder labels
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);  // Increased from 15 to 16
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font PLACEHOLDER_FONT = new Font("Segoe UI", Font.ITALIC, 14);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font PANEL_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    
    // Form fields for both panels
    private JTextField currentUserField;
    private JPasswordField currentPassField;
    private JTextField newUserField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;
    
    // Card layout for multi-step form
    private CardLayout cardLayout;
    private JPanel mainCardPanel;
    
    // Button references
    private JButton nextButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton backButton;
    
    // Dummy credentials
    private String currentUsername = "admin";
    private String currentPassword = "admin123";
    
    /* ================= CONSTRUCTOR ================= */
    public SetPasswordDialog(Window ownerFrame, JPanel contentPanel) {
        super(ownerFrame, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Main card panel (rounded with shadow)
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 35, 25, 35));  // Reduced padding
        card.setPreferredSize(new Dimension(700, 550));   // Adjusted size
        
        /* ================= TOP BAR ================= */
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 15, 0));  // Reduced bottom padding
        
        // Title on left (blue like AddProductDialog)
        JLabel title = new JLabel("🔐 Change Password");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        topBar.add(title, BorderLayout.WEST);
        
        // Close button on right (red X with hover effect)
        JButton closeBtn = createCloseButton();
        topBar.add(closeBtn, BorderLayout.EAST);
        
        card.add(topBar, BorderLayout.NORTH);
        
        /* ================= MAIN CONTENT PANEL ================= */
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setOpaque(false);
        mainCardPanel.setBorder(new EmptyBorder(5, 0, 10, 0));  // Reduced padding
        
        // Create both panels
        mainCardPanel.add(createVerificationPanel(), "VERIFY");
        mainCardPanel.add(createNewPasswordPanel(), "NEW_PASSWORD");
        
        card.add(mainCardPanel, BorderLayout.CENTER);
        
        /* ================= BUTTON PANEL ================= */
        JPanel buttonPanel = createButtonPanel();
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add card to dialog
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(LIGHT_BLUE_BG);
        getContentPane().add(card);
        
        pack();
        setSize(700, 550);
        
        // Center dialog
        if (contentPanel != null) {
            try {
                Point p = contentPanel.getLocationOnScreen();
                int cx = p.x + (contentPanel.getWidth() - getWidth()) / 2;
                int cy = p.y + (contentPanel.getHeight() - getHeight()) / 2;
                setLocation(Math.max(20, cx), Math.max(20, cy));
            } catch (IllegalComponentStateException ex) {
                setLocationRelativeTo(ownerFrame);
            }
        } else {
            setLocationRelativeTo(ownerFrame);
        }
        
        // ESC to close
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Set focus to first field
        SwingUtilities.invokeLater(() -> currentUserField.requestFocus());
    }
    
    private JPanel createVerificationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(5, 0, 10, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 10, 8, 10);  // Reduced insets
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Panel title
        JLabel panelTitle = new JLabel("Verify Current Credentials");
        panelTitle.setFont(PANEL_TITLE_FONT);
        panelTitle.setForeground(DARK_SKY_BLUE);
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 20, 10);  // Reduced bottom margin
        panel.add(panelTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // Current Username (Required) - Increased and bold label
        addLabel(panel, gbc, "Current Username", true);
        gbc.gridy++;
        currentUserField = createStyledTextField("Enter current username", false);
        panel.add(currentUserField, gbc);
        
        // Current Password (Required) - Increased and bold label
        gbc.gridy++;
        addLabel(panel, gbc, "Current Password", true);
        gbc.gridy++;
        currentPassField = createStyledPasswordField("Enter current password");
        panel.add(currentPassField, gbc);
        
        // Info text
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);  // Reduced top margin
        JLabel infoLabel = new JLabel("Enter your current login credentials to proceed");
        infoLabel.setFont(PLACEHOLDER_FONT);
        infoLabel.setForeground(OPTIONAL_COLOR);
        panel.add(infoLabel, gbc);
        
        // Add filler panel to push content up
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);
        
        return panel;
    }
    
    private JPanel createNewPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(5, 0, 10, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 10, 8, 10);  // Reduced insets
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Panel title
        JLabel panelTitle = new JLabel("Set New Credentials");
        panelTitle.setFont(PANEL_TITLE_FONT);
        panelTitle.setForeground(DARK_SKY_BLUE);
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 20, 10);  // Reduced bottom margin
        panel.add(panelTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // New Username (Required) - Increased and bold label
        addLabel(panel, gbc, "New Username", true);
        gbc.gridy++;
        newUserField = createStyledTextField("Enter new username", false);
        panel.add(newUserField, gbc);
        
        // New Password (Required) - Increased and bold label
        gbc.gridy++;
        addLabel(panel, gbc, "New Password", true);
        gbc.gridy++;
        newPassField = createStyledPasswordField("Enter new password");
        panel.add(newPassField, gbc);
        
        // Confirm Password (Required) - Increased and bold label
        gbc.gridy++;
        addLabel(panel, gbc, "Confirm Password", true);
        gbc.gridy++;
        confirmPassField = createStyledPasswordField("Confirm new password");
        panel.add(confirmPassField, gbc);
        
        // Password requirements
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);  // Reduced top margin
        JPanel reqPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        reqPanel.setOpaque(false);
        
        JLabel reqLabel = createStatusLabel("● Must be at least 8 characters", OPTIONAL_COLOR);
        JLabel reqLabel2 = createStatusLabel("● Should include letters and numbers", OPTIONAL_COLOR);
        
        reqPanel.add(reqLabel);
        reqPanel.add(reqLabel2);
        panel.add(reqPanel, gbc);
        
        // Add filler panel to push content up
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));  // Reduced spacing
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));  // Reduced top padding
        
        // Next button (blue) - shown on verification panel
        nextButton = createStyledButton("➡️ Next Step", DARK_SKY_BLUE, SKY_BLUE);  // Blue -> Lighter blue on hover
        nextButton.addActionListener(e -> verifyCredentials());
        
        // Save button (blue) - shown on new password panel
        saveButton = createStyledButton("💾 Save Changes", DARK_SKY_BLUE, SKY_BLUE);  // Blue -> Lighter blue on hover
        saveButton.addActionListener(e -> saveNewCredentials());
        saveButton.setVisible(false);
        
        // Back button (light gray) - shown on new password panel
        backButton = createStyledButton("⬅️ Back", new Color(200, 200, 200), new Color(180, 180, 180));
        backButton.addActionListener(e -> {
            cardLayout.show(mainCardPanel, "VERIFY");
            nextButton.setVisible(true);
            saveButton.setVisible(false);
            backButton.setVisible(false);
            getRootPane().setDefaultButton(nextButton);
        });
        backButton.setVisible(false);
        
        // Cancel button
        cancelButton = createStyledButton("✕ Cancel", new Color(200, 200, 200), HOVER_RED);
        cancelButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel? All changes will be lost.",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) dispose();
        });
        
        panel.add(nextButton);
        panel.add(saveButton);
        panel.add(backButton);
        panel.add(cancelButton);
        
        // Make Next the default button initially
        getRootPane().setDefaultButton(nextButton);
        
        return panel;
    }
    
    private JButton createCloseButton() {
        JButton btn = new JButton("✕");
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setForeground(new Color(140, 140, 140));
        btn.setPreferredSize(new Dimension(40, 40));
        btn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to close?",
                "Confirm Close",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) dispose();
        });
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(Color.RED);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(new Color(140, 140, 140));
            }
        });
        
        return btn;
    }
    
    private JButton createStyledButton(String text, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor;
                if (getModel().isPressed()) {
                    bgColor = normalColor.darker();
                } else if (getModel().isRollover()) {
                    bgColor = hoverColor;
                } else {
                    bgColor = normalColor;
                }
                
                // Draw rounded background
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw border
                g2.setColor(bgColor.darker());
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 45));  // Slightly smaller
        button.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        return button;
    }
    
    private void addLabel(JPanel panel, GridBagConstraints gbc, String text, boolean required) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);  // Using increased bold font
        label.setForeground(LABEL_COLOR);
        labelPanel.add(label);
        
        if (required) {
            JLabel star = new JLabel(" *");
            star.setFont(LABEL_FONT);  // Same increased font
            star.setForeground(REQUIRED_COLOR);
            labelPanel.add(star);
        }
        
        panel.add(labelPanel, gbc);
        gbc.gridy++;
    }
    
    private JLabel createStatusLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(STATUS_FONT);
        label.setForeground(color);
        return label;
    }
    
    private JTextField createStyledTextField(String placeholder, boolean readOnly) {
        return new JTextField() {
            private boolean showingPlaceholder = !readOnly;
            private String placeholderText = placeholder;
            
            {
                setFont(INPUT_FONT);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)  // Reduced padding
                ));
                setBackground(INPUT_BG);
                setForeground(readOnly ? new Color(80, 80, 80) : Color.GRAY);
                setText(placeholder);
                setCaretColor(DARK_SKY_BLUE);
                setPreferredSize(new Dimension(300, 40));  // Reduced height
                setEditable(!readOnly);
                
                if (!readOnly) {
                    addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            if (showingPlaceholder) {
                                setText("");
                                setForeground(new Color(40, 40, 40));
                                showingPlaceholder = false;
                            }
                        }
                        
                        @Override
                        public void focusLost(FocusEvent e) {
                            if (getText().isEmpty()) {
                                setText(placeholderText);
                                setForeground(Color.GRAY);
                                showingPlaceholder = true;
                            }
                        }
                    });
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Draw border
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
    
    private JPasswordField createStyledPasswordField(String placeholder) {
        return new JPasswordField() {
            private boolean showingPlaceholder = true;
            private String placeholderText = placeholder;
            
            {
                setFont(INPUT_FONT);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)  // Reduced padding
                ));
                setBackground(INPUT_BG);
                setForeground(Color.GRAY);
                setEchoChar((char) 0);
                setText(placeholder);
                setCaretColor(DARK_SKY_BLUE);
                setPreferredSize(new Dimension(300, 40));  // Reduced height
                
                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (showingPlaceholder) {
                            setText("");
                            setForeground(new Color(40, 40, 40));
                            setEchoChar('•');
                            showingPlaceholder = false;
                        }
                    }
                    
                    @Override
                    public void focusLost(FocusEvent e) {
                        if (getPassword().length == 0) {
                            setText(placeholderText);
                            setForeground(Color.GRAY);
                            setEchoChar((char) 0);
                            showingPlaceholder = true;
                        }
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Draw border
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
    
    /* ================= BUSINESS LOGIC ================= */
    
    private void verifyCredentials() {
        String username = currentUserField.getForeground().equals(Color.GRAY) ? "" : currentUserField.getText().trim();
        String password = new String(currentPassField.getPassword());
        
        if (currentPassField.getForeground().equals(Color.GRAY) || password.isEmpty()) {
            showError("Current password is required");
            currentPassField.requestFocus();
            return;
        }
        
        if (username.isEmpty()) {
            showError("Current username is required");
            currentUserField.requestFocus();
            return;
        }
        
        // Verify against stored credentials
        if (username.equals(currentUsername) && password.equals(currentPassword)) {
            // Switch to new password panel
            cardLayout.show(mainCardPanel, "NEW_PASSWORD");
            nextButton.setVisible(false);
            saveButton.setVisible(true);
            backButton.setVisible(true);
            getRootPane().setDefaultButton(saveButton);
            
            // Auto-fill new username field with current username
            newUserField.setText(currentUsername);
            newUserField.setForeground(new Color(40, 40, 40));
            newPassField.requestFocus();
        } else {
            showError("Invalid username or password");
            currentPassField.setText("");
            currentPassField.requestFocus();
        }
    }
    
    private void saveNewCredentials() {
        String newUsername = newUserField.getForeground().equals(Color.GRAY) ? "" : newUserField.getText().trim();
        String newPassword = new String(newPassField.getPassword());
        String confirmPassword = new String(confirmPassField.getPassword());
        
        // Validate all fields
        if (newUsername.isEmpty()) {
            showError("New username is required");
            newUserField.requestFocus();
            return;
        }
        
        if (newPassword.isEmpty() || newPassField.getForeground().equals(Color.GRAY)) {
            showError("New password is required");
            newPassField.requestFocus();
            return;
        }
        
        if (confirmPassword.isEmpty() || confirmPassField.getForeground().equals(Color.GRAY)) {
            showError("Please confirm your password");
            confirmPassField.requestFocus();
            return;
        }
        
        // Validate password length
        if (newPassword.length() < 8) {
            showError("Password must be at least 8 characters long");
            newPassField.requestFocus();
            return;
        }
        
        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match");
            confirmPassField.setText("");
            confirmPassField.requestFocus();
            return;
        }
        
        // Check if credentials are different
        if (newUsername.equals(currentUsername) && newPassword.equals(currentPassword)) {
            showError("New credentials must be different from current credentials");
            return;
        }
        
        // Update credentials
        currentUsername = newUsername;
        currentPassword = newPassword;
        
        // Show success message
        JOptionPane.showMessageDialog(this, 
            "✅ Credentials updated successfully!\n\n" +
            "Username: " + currentUsername + "\n" +
            "Password has been changed",
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            "❌ " + message, 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    /* ================= CUSTOM PANEL ================= */
    private static class RoundedCardPanel extends JPanel {
        private final int cornerRadius;
        
        RoundedCardPanel(int cornerRadius) {
            super(new BorderLayout());
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw subtle shadow (exactly like AddProductDialog)
            g2.setColor(new Color(0, 0, 0, 15));
            for (int i = 0; i < 3; i++) {
                g2.fillRoundRect(i, i, getWidth()-2*i, getHeight()-2*i, cornerRadius, cornerRadius);
            }
            
            // Draw main card
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // Draw border
            g2.setColor(new Color(180, 200, 230));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    /* ================= TEST METHOD ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Password Dialog");
            frame.setSize(1000, 700);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JPanel contentPanel = new JPanel();
            frame.setContentPane(contentPanel);
            frame.setVisible(true);

            SetPasswordDialog dialog = new SetPasswordDialog(frame, contentPanel);
            dialog.setVisible(true);
        });
    }
}