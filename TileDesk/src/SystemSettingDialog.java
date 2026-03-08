package src;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * SystemSettingDialog - Modern System Settings Form (Same UI/UX as AddProductDialog)
 */
public class SystemSettingDialog extends JDialog {
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
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    
    // Fonts matching AddProductDialog but with bolder labels
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font PLACEHOLDER_FONT = new Font("Segoe UI", Font.ITALIC, 14);
    private final Font SECTION_FONT = new Font("Segoe UI Semibold", Font.BOLD, 18);
    
    // Form fields
    private JCheckBox autoBackupCheck;
    private JSpinner backupDaysSpinner;
    private JTextField backupPathField;
    private JButton browseButton;
    private JCheckBox darkModeCheck;
    private JComboBox<String> fontSizeCombo;
    private JSpinner timeoutSpinner;
    private JCheckBox autoUpdateCheck;
    private JComboBox<String> themeCombo;
    private JCheckBox notificationsCheck;
    private JCheckBox soundCheck;
    
    // Button references
    private JButton saveButton;
    private JButton resetButton;
    private JButton cancelButton;
    
    /* ================= CONSTRUCTOR ================= */
    public SystemSettingDialog(Window ownerFrame, JPanel contentPanel) {
        super(ownerFrame, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Main card panel (rounded with shadow)
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 35, 25, 35));
        card.setPreferredSize(new Dimension(750, 650));
        
        /* ================= TOP BAR ================= */
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Title on left (blue like AddProductDialog)
        JLabel title = new JLabel("⚙️ System Settings");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        topBar.add(title, BorderLayout.WEST);
        
        // Close button on right (red X with hover effect)
        JButton closeBtn = createCloseButton();
        topBar.add(closeBtn, BorderLayout.EAST);
        
        card.add(topBar, BorderLayout.NORTH);
        
        /* ================= MAIN CONTENT PANEL ================= */
        JPanel mainPanel = createSettingsPanel();
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LIGHT_BLUE_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        card.add(scrollPane, BorderLayout.CENTER);
        
        /* ================= BUTTON PANEL ================= */
        JPanel buttonPanel = createButtonPanel();
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add card to dialog
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(LIGHT_BLUE_BG);
        getContentPane().add(card);
        
        pack();
        setSize(750, 650);
        
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
        
        // Load current settings
        loadCurrentSettings();
    }
    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Database Settings Section
        JLabel dbSection = new JLabel("Database Settings");
        dbSection.setFont(SECTION_FONT);
        dbSection.setForeground(DARK_SKY_BLUE);
        gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 10, 15, 10);
        panel.add(dbSection, gbc);
        gbc.gridwidth = 1;
        
        // Auto Backup
        gbc.gridy++;
        gbc.insets = new Insets(8, 10, 8, 10);
        autoBackupCheck = createStyledCheckBox("Enable Auto Backup");
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        panel.add(autoBackupCheck, gbc);
        gbc.gridwidth = 1;
        
        // Backup Days
        gbc.gridy++;
        addLabel(panel, gbc, "Backup Frequency (days)", true);
        gbc.gridy++;
        backupDaysSpinner = createStyledSpinner(1, 365, 7, 1);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(backupDaysSpinner, gbc);
        gbc.gridwidth = 1;
        
        // Backup Path
        gbc.gridy++;
        gbc.gridx = 0;
        addLabel(panel, gbc, "Backup Path", true);
        gbc.gridy++;
        JPanel pathPanel = new JPanel(new BorderLayout(10, 0));
        pathPanel.setOpaque(false);
        backupPathField = createStyledTextField("C:/Backups/TilesFactory", false);
        backupPathField.setPreferredSize(new Dimension(300, 40));
        browseButton = createStyledButton("Browse", new Color(180, 180, 180), new Color(160, 160, 160));
        browseButton.setPreferredSize(new Dimension(100, 40));
        browseButton.addActionListener(e -> browseBackupPath());
        
        pathPanel.add(backupPathField, BorderLayout.CENTER);
        pathPanel.add(browseButton, BorderLayout.EAST);
        gbc.gridwidth = 2;
        panel.add(pathPanel, gbc);
        gbc.gridwidth = 1;
        
        // UI Settings Section
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = new Insets(25, 10, 15, 10);
        JLabel uiSection = new JLabel("User Interface Settings");
        uiSection.setFont(SECTION_FONT);
        uiSection.setForeground(DARK_SKY_BLUE);
        gbc.gridwidth = 3;
        panel.add(uiSection, gbc);
        gbc.gridwidth = 1;
        
        // Dark Mode
        gbc.gridy++;
        gbc.insets = new Insets(8, 10, 8, 10);
        darkModeCheck = createStyledCheckBox("Enable Dark Mode");
        gbc.gridwidth = 3;
        panel.add(darkModeCheck, gbc);
        gbc.gridwidth = 1;
        
        // Font Size
        gbc.gridy++;
        gbc.gridx = 0;
        addLabel(panel, gbc, "Font Size", true);
        gbc.gridy++;
        fontSizeCombo = createStyledComboBox(new String[]{"Small", "Medium", "Large"}, "Select font size");
        panel.add(fontSizeCombo, gbc);
        
        // Theme
        gbc.gridy++;
        gbc.gridx = 0;
        addLabel(panel, gbc, "Theme", true);
        gbc.gridy++;
        themeCombo = createStyledComboBox(new String[]{"Light", "Dark", "Blue", "Green", "Purple"}, "Select theme");
        panel.add(themeCombo, gbc);
        
        // Application Settings Section
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = new Insets(25, 10, 15, 10);
        JLabel appSection = new JLabel("Application Settings");
        appSection.setFont(SECTION_FONT);
        appSection.setForeground(DARK_SKY_BLUE);
        gbc.gridwidth = 3;
        panel.add(appSection, gbc);
        gbc.gridwidth = 1;
        
        // Session Timeout
        gbc.gridy++;
        gbc.insets = new Insets(8, 10, 8, 10);
        addLabel(panel, gbc, "Session Timeout (minutes)", true);
        gbc.gridy++;
        timeoutSpinner = createStyledSpinner(5, 180, 30, 5);
        panel.add(timeoutSpinner, gbc);
        
        // Auto Update
        gbc.gridy++;
        gbc.gridx = 0;
        autoUpdateCheck = createStyledCheckBox("Enable Auto Updates");
        gbc.gridwidth = 3;
        panel.add(autoUpdateCheck, gbc);
        gbc.gridwidth = 1;
        
        // Notification Settings Section
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = new Insets(25, 10, 15, 10);
        JLabel notifSection = new JLabel("Notification Settings");
        notifSection.setFont(SECTION_FONT);
        notifSection.setForeground(DARK_SKY_BLUE);
        gbc.gridwidth = 3;
        panel.add(notifSection, gbc);
        gbc.gridwidth = 1;
        
        // Notifications
        gbc.gridy++;
        gbc.insets = new Insets(8, 10, 8, 10);
        notificationsCheck = createStyledCheckBox("Enable Notifications");
        gbc.gridwidth = 3;
        panel.add(notificationsCheck, gbc);
        gbc.gridwidth = 1;
        
        // Sound
        gbc.gridy++;
        soundCheck = createStyledCheckBox("Enable Sound Notifications");
        gbc.gridwidth = 3;
        panel.add(soundCheck, gbc);
        gbc.gridwidth = 1;
        
        // Add filler to push content up
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        // Save button (blue)
        saveButton = createStyledButton("💾 Save Settings", DARK_SKY_BLUE, SKY_BLUE);
        saveButton.addActionListener(e -> saveSettings());
        
        // Reset button (orange)
        resetButton = createStyledButton("🔄 Reset to Default", new Color(255, 165, 0), new Color(255, 140, 0));
        resetButton.addActionListener(e -> resetToDefault());
        
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
        
        panel.add(saveButton);
        panel.add(resetButton);
        panel.add(cancelButton);
        
        // Make Save the default button
        getRootPane().setDefaultButton(saveButton);
        
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
        button.setPreferredSize(new Dimension(180, 45));
        button.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        return button;
    }
    
    private void addLabel(JPanel panel, GridBagConstraints gbc, String text, boolean required) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(LABEL_COLOR);
        labelPanel.add(label);
        
        if (required) {
            JLabel star = new JLabel(" *");
            star.setFont(LABEL_FONT);
            star.setForeground(REQUIRED_COLOR);
            labelPanel.add(star);
        }
        
        panel.add(labelPanel, gbc);
        gbc.gridy++;
    }
    
    private JTextField createStyledTextField(String placeholder, boolean readOnly) {
        return new JTextField() {
            private boolean showingPlaceholder = !readOnly;
            private String placeholderText = placeholder;
            
            {
                setFont(INPUT_FONT);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                setBackground(INPUT_BG);
                setForeground(readOnly ? new Color(80, 80, 80) : Color.GRAY);
                setText(placeholder);
                setCaretColor(DARK_SKY_BLUE);
                setPreferredSize(new Dimension(300, 40));
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
    
    private JCheckBox createStyledCheckBox(String text) {
        return new JCheckBox(text) {
            {
                setFont(LABEL_FONT);
                setForeground(LABEL_COLOR);
                setBackground(new Color(0, 0, 0, 0));
                setOpaque(false);
                setFocusPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                
                // Custom checkbox icon
                setIcon(new CheckBoxIcon(false));
                setSelectedIcon(new CheckBoxIcon(true));
                setRolloverIcon(new CheckBoxIcon(false));
                setRolloverSelectedIcon(new CheckBoxIcon(true));
                setPressedIcon(new CheckBoxIcon(false));
            }
        };
    }
    
    private class CheckBoxIcon implements Icon {
        private final boolean selected;
        private final int size = 20;
        
        CheckBoxIcon(boolean selected) {
            this.selected = selected;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw rounded checkbox
            g2.setColor(INPUT_BG);
            g2.fillRoundRect(x, y, size, size, 6, 6);
            
            // Draw border
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, size, size, 6, 6);
            
            if (selected) {
                // Draw checkmark
                g2.setColor(DARK_SKY_BLUE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 4, y + size/2, x + size/2, y + size - 4);
                g2.drawLine(x + size/2, y + size - 4, x + size - 4, y + 4);
            }
            
            g2.dispose();
        }
        
        @Override
        public int getIconWidth() { return size + 2; }
        
        @Override
        public int getIconHeight() { return size + 2; }
    }
    
    private JSpinner createStyledSpinner(int min, int max, int value, int step) {
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner spinner = new JSpinner(model) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background
                g2.setColor(INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Draw border
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        spinner.setFont(INPUT_FONT);
        spinner.setBackground(INPUT_BG);
        spinner.setPreferredSize(new Dimension(150, 40));
        
        // Style the spinner editor
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setFont(INPUT_FONT);
        editor.getTextField().setBackground(INPUT_BG);
        editor.getTextField().setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        editor.getTextField().setForeground(new Color(40, 40, 40));
        
        // Style spinner buttons
        Component[] comps = spinner.getComponents();
        for (Component comp : comps) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(SKY_BLUE);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                btn.setFocusPainted(false);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        btn.setBackground(DARK_SKY_BLUE);
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        btn.setBackground(SKY_BLUE);
                    }
                });
            }
        }
        
        return spinner;
    }
    
    private JComboBox<String> createStyledComboBox(String[] items, String placeholder) {
        return new JComboBox<String>(items) {
            {
                setFont(INPUT_FONT);
                setBackground(INPUT_BG);
                setForeground(Color.GRAY);
                setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value,
                            int index, boolean isSelected, boolean cellHasFocus) {
                        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (index == -1 && value.toString().startsWith("Select")) {
                            setForeground(Color.GRAY);
                        } else if (isSelected) {
                            setBackground(SKY_BLUE);
                            setForeground(Color.WHITE);
                        } else {
                            setBackground(INPUT_BG);
                            setForeground(new Color(40, 40, 40));
                        }
                        return c;
                    }
                });
                setPreferredSize(new Dimension(300, 40));
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
                
                // Draw dropdown arrow
                g2.setColor(DARK_SKY_BLUE);
                int[] xPoints = {getWidth() - 22, getWidth() - 14, getWidth() - 18};
                int[] yPoints = {getHeight()/2 - 3, getHeight()/2 - 3, getHeight()/2 + 3};
                g2.fillPolygon(xPoints, yPoints, 3);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
    
    /* ================= BUSINESS LOGIC ================= */
    
    private void loadCurrentSettings() {
        // Load default/saved settings
        autoBackupCheck.setSelected(true);
        backupDaysSpinner.setValue(7);
        backupPathField.setText("C:/Backups/TilesFactory");
        darkModeCheck.setSelected(false);
        fontSizeCombo.setSelectedItem("Medium");
        themeCombo.setSelectedItem("Light");
        timeoutSpinner.setValue(30);
        autoUpdateCheck.setSelected(true);
        notificationsCheck.setSelected(true);
        soundCheck.setSelected(true);
    }
    
    private void browseBackupPath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Backup Directory");
        
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            backupPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            backupPathField.setForeground(new Color(40, 40, 40));
        }
    }
    
    private void saveSettings() {
        // Validate settings
        String backupPath = backupPathField.getText().trim();
        if (autoBackupCheck.isSelected() && (backupPath.isEmpty() || backupPathField.getForeground().equals(Color.GRAY))) {
            showError("Backup path is required when auto backup is enabled");
            backupPathField.requestFocus();
            return;
        }
        
        // Save settings logic would go here (database, file, etc.)
        // For now, just show success message
        
        JOptionPane.showMessageDialog(this, 
            "✅ System settings saved successfully!\n\n" +
            "Settings will take effect after restarting the application.",
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    }
    
    private void resetToDefault() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset all settings to default values?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            loadCurrentSettings();
            JOptionPane.showMessageDialog(this,
                "✅ All settings have been reset to default values.",
                "Reset Complete",
                JOptionPane.INFORMATION_MESSAGE);
        }
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
            JFrame frame = new JFrame("Test System Settings Dialog");
            frame.setSize(1000, 700);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JPanel contentPanel = new JPanel();
            frame.setContentPane(contentPanel);
            frame.setVisible(true);

            SystemSettingDialog dialog = new SystemSettingDialog(frame, contentPanel);
            dialog.setVisible(true);
        });
    }
}