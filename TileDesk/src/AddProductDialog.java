package src;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * AddProductDialog - Modern Tile Factory Product Addition Form
 */
public class AddProductDialog extends JDialog {
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
    
    // Tile factory categories - MODIFIED: Only Marble and Granite
    private static final String[] TILE_CATEGORIES = {
        "Select Category", 
        "Marble", "Granite"
    };
    
    // Tile sizes
    private static final String[] TILE_SIZES = {
        "Select Size",
        "2x2 ft", "2x4 ft", "4x4 ft", "12x12 in", "12x24 in",
        "24x24 in", "24x48 in", "Custom"
    };
    
    // Thickness options
    private static final String[] THICKNESS_OPTIONS = {
        "Select Thickness",
        "8mm", "10mm", "12mm", "16mm", "18mm",
        "20mm", "24mm", "30mm", "Custom"
    };
    
    // Finish types
    private static final String[] FINISH_TYPES = {
        "Select Finish",
        "Polished", "Matte", "Honed", "Glossy", "Textured",
        "Brushed", "Lappato", "Natural", "Custom"
    };
    
    // Unit types
    private static final String[] UNIT_TYPES = {
        "Select Unit",
        "Sq Ft", "Sq Meter", "Slab", "Box", "Piece"
    };
    
    // Form fields
    private JTextField productIdField;
    private JTextField nameField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> sizeCombo;
    private JComboBox<String> thicknessCombo;
    private JComboBox<String> finishCombo;
    private JComboBox<String> unitCombo;
    private JSpinner qtySpinner;
    private JTextField priceField;
    private JTextArea descriptionArea;
    private JTextField customSizeField;
    private JTextField customThicknessField;
    
    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 15);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font PLACEHOLDER_FONT = new Font("Segoe UI", Font.ITALIC, 14);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    // Save status
    private boolean saveSuccessful = false;
    private TileProductData productData = null;
    
    // Button references
    private JButton saveButton;
    private JButton cancelButton;
    private JButton regenButton;
    
    // Form panel reference
    private JPanel formPanel;
    
    /* ================= TILE PRODUCT DATA CLASS ================= */
    public static class TileProductData {
        private final String productId;
        private final String name;
        private final String category;
        private final String tileSize;
        private final String thickness;
        private final String finishType;
        private final String unitType;
        private final int quantity;
        private final double pricePerUnit;
        private final String description;
        
        public TileProductData(String productId, String name, String category, 
                              String tileSize, String thickness, String finishType,
                              String unitType, int quantity, double pricePerUnit, 
                              String description) {
            this.productId = productId;
            this.name = name;
            this.category = category;
            this.tileSize = tileSize;
            this.thickness = thickness;
            this.finishType = finishType;
            this.unitType = unitType;
            this.quantity = quantity;
            this.pricePerUnit = pricePerUnit;
            this.description = description;
        }
        
        // Getters
        public String getProductId() { return productId; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getTileSize() { return tileSize; }
        public String getThickness() { return thickness; }
        public String getFinishType() { return finishType; }
        public String getUnitType() { return unitType; }
        public int getQuantity() { return quantity; }
        public double getPricePerUnit() { return pricePerUnit; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() {
            return String.format("TileProduct[ID=%s, Name=%s, Category=%s, Size=%s, Unit=%s]", 
                productId, name, category, tileSize, unitType);
        }
    }
    
    /* ================= DATA METHODS ================= */
    
    /**
     * Stubs for database methods (connectivity removed)
     */
    private boolean saveProductToDatabase(TileProductData product) {
        return true; 
    }
    
    private boolean checkProductIdExists(String productId) {
        return false;
    }
    
    /* ================= CONSTRUCTOR ================= */
    public AddProductDialog(Window ownerFrame, JPanel contentPanel) {
        super(ownerFrame, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Main card panel
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30, 35, 30, 35));
        card.setPreferredSize(new Dimension(850, 750));
        
        /* ================= TOP BAR ================= */
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Title on left
        JLabel title = new JLabel("Add New Tile Product");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        topBar.add(title, BorderLayout.WEST);
        
        // Close button on right
        JButton closeBtn = createCloseButton();
        topBar.add(closeBtn, BorderLayout.EAST);
        
        card.add(topBar, BorderLayout.NORTH);
        
        /* ================= FORM PANEL ================= */
        formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LIGHT_BLUE_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        styleScrollPane(scrollPane);
        card.add(scrollPane, BorderLayout.CENTER);
        
        /* ================= BUTTON PANEL ================= */
        JPanel buttonPanel = createButtonPanel();
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add card to dialog
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(LIGHT_BLUE_BG);
        getContentPane().add(card);
        
        pack();
        setSize(850, 750);
        
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
        generateProductId();
        
        // Set focus to first field
        SwingUtilities.invokeLater(() -> nameField.requestFocus());
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Product ID (Required)
        addLabel(panel, gbc, "Product ID", true);
        gbc.gridy++;
        productIdField = createStyledTextField("TILE-0000", true);
        productIdField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(productIdField, gbc);
        
        // Product Name (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Product Name", true);
        gbc.gridy++;
        nameField = createStyledTextField("Enter tile product name", false);
        panel.add(nameField, gbc);
        
        // Category (Required) - MODIFIED: Only shows Marble and Granite
        gbc.gridy++;
        addLabel(panel, gbc, "Tile Category", true);
        gbc.gridy++;
        categoryCombo = createStyledComboBox(TILE_CATEGORIES, "Choose tile category");
        // Add listener to regenerate ID when category changes
        categoryCombo.addActionListener(e -> generateProductId());
        panel.add(categoryCombo, gbc);
        
        // Tile Size (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Tile Size", true);
        gbc.gridy++;
        sizeCombo = createStyledComboBox(TILE_SIZES, "Select tile size");
        sizeCombo.addActionListener(e -> handleSizeSelection());
        panel.add(sizeCombo, gbc);
        
        // Custom Size Field (hidden by default)
        gbc.gridy++;
        customSizeField = createStyledTextField("Enter custom size (e.g., 3x5 ft)", false);
        customSizeField.setVisible(false);
        panel.add(customSizeField, gbc);
        
        // Thickness (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Thickness", true);
        gbc.gridy++;
        thicknessCombo = createStyledComboBox(THICKNESS_OPTIONS, "Select thickness");
        thicknessCombo.addActionListener(e -> handleThicknessSelection());
        panel.add(thicknessCombo, gbc);
        
        // Custom Thickness Field (hidden by default)
        gbc.gridy++;
        customThicknessField = createStyledTextField("Enter custom thickness (e.g., 15mm)", false);
        customThicknessField.setVisible(false);
        panel.add(customThicknessField, gbc);
        
        // Finish Type (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Finish Type", true);
        gbc.gridy++;
        finishCombo = createStyledComboBox(FINISH_TYPES, "Select finish type");
        panel.add(finishCombo, gbc);
        
        // Unit Type (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Unit Type", true);
        gbc.gridy++;
        unitCombo = createStyledComboBox(UNIT_TYPES, "Select unit type");
        panel.add(unitCombo, gbc);
        
        // Quantity in Stock (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Quantity in Stock", true);
        gbc.gridy++;
        qtySpinner = createStyledSpinner();
        panel.add(qtySpinner, gbc);
        
        // Price per Unit (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Price per Unit", true);
        gbc.gridy++;
        priceField = createStyledTextField("Enter price per unit (e.g., 24.99)", false);
        priceField.setToolTipText("Price for the selected unit type (per Sq Ft, per Sq Meter, per Slab, etc.)");
        panel.add(priceField, gbc);
        
        // Description (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Description", false);
        gbc.gridy++;
        descriptionArea = createStyledTextArea("Optional: Enter product description (color, texture, application, etc.)", 4);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        styleScrollPane(descScroll);
        panel.add(descScroll, gbc);
        
        // Status indicator
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 5, 10);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusPanel.setOpaque(false);
        
        JLabel requiredLabel = createStatusLabel("● Required", REQUIRED_COLOR);
        JLabel optionalLabel = createStatusLabel("● Optional", OPTIONAL_COLOR);
        
        statusPanel.add(requiredLabel);
        statusPanel.add(optionalLabel);
        panel.add(statusPanel, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Save button (sky blue)
        saveButton = createStyledButton("💾 Save Tile Product", SKY_BLUE, SKY_BLUE.darker());
        saveButton.addActionListener(e -> onSave());
        
        // Cancel button
        cancelButton = createStyledButton("✕ Cancel", new Color(200, 200, 200), HOVER_RED);
        cancelButton.addActionListener(e -> dispose());
        
        // Regenerate ID button
        regenButton = createStyledButton("🔄 Regenerate ID", DARK_SKY_BLUE, SKY_BLUE);
        regenButton.addActionListener(e -> generateProductId());
        
        panel.add(saveButton);
        panel.add(cancelButton);
        panel.add(regenButton);
        
        // Make Save the default button
        getRootPane().setDefaultButton(saveButton);
        
        return panel;
    }
    
    private void handleSizeSelection() {
        String selected = (String) sizeCombo.getSelectedItem();
        if ("Custom".equals(selected)) {
            customSizeField.setVisible(true);
            SwingUtilities.invokeLater(() -> customSizeField.requestFocus());
        } else {
            customSizeField.setVisible(false);
        }
        formPanel.revalidate();
        formPanel.repaint();
        
        // Update scroll pane
        Container parent = formPanel.getParent();
        if (parent instanceof JViewport) {
            parent = parent.getParent();
            if (parent instanceof JScrollPane) {
                parent.revalidate();
                parent.repaint();
            }
        }
    }
    
    private void handleThicknessSelection() {
        String selected = (String) thicknessCombo.getSelectedItem();
        if ("Custom".equals(selected)) {
            customThicknessField.setVisible(true);
            SwingUtilities.invokeLater(() -> customThicknessField.requestFocus());
        } else {
            customThicknessField.setVisible(false);
        }
        formPanel.revalidate();
        formPanel.repaint();
        
        // Update scroll pane
        Container parent = formPanel.getParent();
        if (parent instanceof JViewport) {
            parent = parent.getParent();
            if (parent instanceof JScrollPane) {
                parent.revalidate();
                parent.repaint();
            }
        }
    }
    
    private JButton createCloseButton() {
        JButton btn = new JButton("✕");
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setForeground(new Color(140, 140, 140));
        btn.setPreferredSize(new Dimension(40, 40));
        btn.addActionListener(e -> dispose());
        
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
                
                // Draw rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw border
                g2.setColor(getBackground().darker());
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
        button.setPreferredSize(new Dimension(180, 50));
        button.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
        
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
        } else {
            JLabel optional = new JLabel(" (Optional)");
            optional.setFont(PLACEHOLDER_FONT);
            optional.setForeground(OPTIONAL_COLOR);
            labelPanel.add(optional);
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
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
                setBackground(INPUT_BG);
                setForeground(readOnly ? new Color(80, 80, 80) : Color.GRAY);
                setText(placeholder);
                setCaretColor(DARK_SKY_BLUE);
                setPreferredSize(new Dimension(300, 45));
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
                setPreferredSize(new Dimension(300, 45));
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
    
    private JSpinner createStyledSpinner() {
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 1000000, 1);
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
        spinner.setPreferredSize(new Dimension(300, 45));
        
        // Style the spinner editor
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setFont(INPUT_FONT);
        editor.getTextField().setBackground(INPUT_BG);
        editor.getTextField().setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
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
    
    private JTextArea createStyledTextArea(String placeholder, int rows) {
        return new JTextArea(rows, 30) {
            private boolean showingPlaceholder = true;
            
            {
                setFont(INPUT_FONT);
                setLineWrap(true);
                setWrapStyleWord(true);
                setForeground(Color.GRAY);
                setText(placeholder);
                setCaretColor(DARK_SKY_BLUE);
                
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
                            setText(placeholder);
                            setForeground(Color.GRAY);
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
    }
    
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LIGHT_BLUE_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Custom scrollbar - EXACTLY like original
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = SKY_BLUE;
                this.trackColor = new Color(240, 240, 245);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
    }
    
    /* ================= BUSINESS LOGIC ================= */
    
    private void generateProductId() {
        Random rand = new Random();
        int randomNum = 1000 + rand.nextInt(9000); // 1000-9999
        String categoryPrefix = "TILE";
        
        // Get selected category for prefix
        if (categoryCombo.getSelectedIndex() > 0) {
            String category = (String) categoryCombo.getSelectedItem();
            if ("Marble".equals(category)) {
                categoryPrefix = "MAR";
            } else if ("Granite".equals(category)) {
                categoryPrefix = "GRA";
            }
            // Removed other categories - only Marble and Granite now
        }
        
        String prodId = String.format("%s-%04d", categoryPrefix, randomNum);
        
        // Check if ID already exists in database
        if (checkProductIdExists(prodId)) {
            // If exists, generate a new one
            randomNum = 1000 + rand.nextInt(9000);
            prodId = String.format("%s-%04d", categoryPrefix, randomNum);
        }
        
        if (productIdField != null) {
            productIdField.setText(prodId);
            productIdField.setForeground(new Color(40, 40, 40));
        }
    }
    
    private void onSave() {
        // Validate required fields
        if (nameField.getForeground().equals(Color.GRAY) || nameField.getText().trim().isEmpty()) {
            showError("Product Name is required");
            nameField.requestFocus();
            return;
        }
        
        if (categoryCombo.getSelectedIndex() == 0) {
            showError("Please select a Tile Category");
            categoryCombo.requestFocus();
            return;
        }
        
        if (sizeCombo.getSelectedIndex() == 0) {
            showError("Tile Size is required");
            sizeCombo.requestFocus();
            return;
        }
        
        // Validate custom size if selected
        String tileSize = (String) sizeCombo.getSelectedItem();
        if ("Custom".equals(tileSize)) {
            if (customSizeField.getForeground().equals(Color.GRAY) || customSizeField.getText().trim().isEmpty()) {
                showError("Please enter custom size");
                customSizeField.requestFocus();
                return;
            }
            tileSize = customSizeField.getText().trim();
        }
        
        if (thicknessCombo.getSelectedIndex() == 0) {
            showError("Thickness is required");
            thicknessCombo.requestFocus();
            return;
        }
        
        // Validate custom thickness if selected
        String thickness = (String) thicknessCombo.getSelectedItem();
        if ("Custom".equals(thickness)) {
            if (customThicknessField.getForeground().equals(Color.GRAY) || customThicknessField.getText().trim().isEmpty()) {
                showError("Please enter custom thickness");
                customThicknessField.requestFocus();
                return;
            }
            thickness = customThicknessField.getText().trim();
        }
        
        if (finishCombo.getSelectedIndex() == 0) {
            showError("Finish Type is required");
            finishCombo.requestFocus();
            return;
        }
        
        if (unitCombo.getSelectedIndex() == 0) {
            showError("Unit Type is required");
            unitCombo.requestFocus();
            return;
        }
        
        // Validate price
        if (priceField.getForeground().equals(Color.GRAY) || priceField.getText().trim().isEmpty()) {
            showError("Price per Unit is required");
            priceField.requestFocus();
            return;
        }
        
        // Validate price format and value
        String priceText = priceField.getText().trim();
        double price;
        try {
            priceText = priceText.replaceAll("[,\\s]", "");
            priceText = priceText.replaceAll("[$€£₹]", "");
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                showError("Price must be greater than 0");
                priceField.requestFocus();
                return;
            }
            if (price > 1000000) { // Reasonable limit for tile price
                showError("Price seems too high. Please check the value.");
                priceField.requestFocus();
                return;
            }
        } catch (Exception e) {
            showError("Invalid price format. Please enter a valid number (e.g., 24.99 or 2499).");
            priceField.requestFocus();
            return;
        }
        
        // Validate quantity
        int quantity = (Integer) qtySpinner.getValue();
        if (quantity < 0) {
            showError("Quantity cannot be negative");
            qtySpinner.requestFocus();
            return;
        }
        
        // kolektat data
        String name = nameField.getForeground().equals(Color.GRAY) ? "" : nameField.getText().trim();
        String description = descriptionArea.getForeground().equals(Color.GRAY) ? "" : descriptionArea.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String finishType = (String) finishCombo.getSelectedItem();
        String unitType = (String) unitCombo.getSelectedItem();
        
        productData = new TileProductData(
            productIdField.getText(),
            name,
            category,
            tileSize,
            thickness,
            finishType,
            unitType,
            quantity,
            price,
            description
        );
        
        // Save local only
        saveSuccessful = true;
        
        // Show success message with tile-specific details
        DecimalFormat df = new DecimalFormat("#,##0.00");
        JOptionPane.showMessageDialog(this, 
            "✅ Tile Product data captured locally (DB connectivity removed)!\n\n" +
            "Name: " + productData.getName() + "\n" +
            "ID: " + productData.getProductId() + "\n" +
            "Category: " + productData.getCategory() + "\n\n" +
            "✅ Successfully saved locally!",
            "Tile Product Added", 
            JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            "❌ " + message + "\n\nPlease fill in all required fields marked with *", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    /* ================= PUBLIC ACCESSORS ================= */
    public boolean isSaveSuccessful() { return saveSuccessful; }
    public TileProductData getProductData() { return productData; }
    
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
            
            // Draw subtle shadow
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tile Factory - Test Dialog");
            frame.setSize(1000, 700);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JPanel contentPanel = new JPanel();
            frame.setContentPane(contentPanel);
            frame.setVisible(true);

            AddProductDialog dialog = new AddProductDialog(frame, contentPanel);
            dialog.setVisible(true);
        });
    }
}