package src;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * UpdateProductDialog - Modern Product Update Interface
 * Now uses in-memory data instead of database
 */
public class UpdateProductDialog extends JDialog {
    private static final Color SKY_BLUE = new Color(135, 206, 250);
    private static final Color DARK_SKY_BLUE = new Color(30, 144, 255);
    private static final Color TITLE_BLUE = new Color(30, 144, 255);
    private static final Color LIGHT_BLUE_BG = new Color(240, 248, 255);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(180, 200, 220);
    private static final Color INPUT_BORDER = new Color(200, 220, 240);
    private static final Color UPDATE_GREEN = new Color(40, 167, 69);
    private static final Color HOVER_GREEN = new Color(30, 147, 59);
    private static final Color CANCEL_COLOR = new Color(150, 150, 150);
    private static final Color CANCEL_HOVER_RED = new Color(220, 80, 80);
    private static final Color TABLE_HEADER_BG = new Color(245, 247, 250);
    private static final Color TABLE_SELECTION = new Color(225, 240, 255);
    private static final Color SCROLLBAR_COLOR = new Color(100, 100, 100);
    private static final Color TEXT_BLACK = new Color(40, 40, 40);
    private static final Color PLACEHOLDER_GRAY = new Color(150, 150, 150);
    private static final Color ERROR_RED = new Color(220, 53, 69);
    private static final Color SUCCESS_GREEN = new Color(40, 167, 69);
    
    // Form fields
    private JTextField productIdField;
    private JTextField nameField;
    private JComboBox<String> categoryCombo;
    private JTextField quantityField;
    private JTextField priceField;
    private JTextField sizeField;
    private JTextField thicknessField;
    private JComboBox<String> finishCombo;
    private JComboBox<String> unitTypeCombo;
    private JTextArea descriptionArea;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Product ID", "Product Name", "Category", "Quantity", "Price (PKR)"}, 0);
    private final JTable table = new JTable(tableModel);
    private JLabel statusLabel;
    
    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 15);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    
    // Button references
    private JButton updateButton;
    private JButton cancelButton;
    private JButton clearButton;
    private JButton refreshButton;
    private JButton addSampleButton;
    
    // Form state
    private boolean showingProductIdPlaceholder = true;
    private boolean showingNamePlaceholder = true;
    private boolean showingQuantityPlaceholder = true;
    private boolean showingPricePlaceholder = true;
    private boolean showingSizePlaceholder = true;
    private boolean showingThicknessPlaceholder = true;
    private boolean showingDescriptionPlaceholder = true;
    
    // Category validation
    private Set<String> validCategories = new HashSet<>();
    
    // In-memory product storage
    private final List<Product> products = new ArrayList<>();
    
    // Product class to store data
    private static class Product {
        String id;
        String name;
        String category;
        double price;
        int quantity;
        String size;
        String thickness;
        String finish;
        String unitType;
        String description;
        
        Product(String id, String name, String category, double price, int quantity,
                String size, String thickness, String finish, String unitType, String description) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.quantity = quantity;
            this.size = size;
            this.thickness = thickness;
            this.finish = finish;
            this.unitType = unitType;
            this.description = description;
        }
    }
    
    public UpdateProductDialog(JFrame ownerFrame, JPanel contentPanel) {
        super(ownerFrame, true);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Initialize with sample data
        initializeSampleData();
        
        // Main card panel
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setPreferredSize(new Dimension(850, 750));
        
        /* ================= TOP BAR ================= */
        JPanel topBar = createTopBar();
        card.add(topBar, BorderLayout.NORTH);
        
        /* ================= MAIN CONTENT ================= */
        JPanel mainContent = createMainContent();
        card.add(mainContent, BorderLayout.CENTER);
        
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
        centerDialog(ownerFrame, contentPanel);
        
        // ESC to close
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Set focus and load data
        SwingUtilities.invokeLater(() -> {
            productIdField.requestFocus();
            loadCategories();
            loadProducts();
        });
    }
    
    /**
     * Initialize with sample data
     */
    private void initializeSampleData() {
        products.clear();
        
        // Add exactly 9 sample products
        products.add(new Product("P001", "Premium Ceramic Tile", "Floor Tiles", 2500.00, 500,
                "2x2 ft", "8mm", "Polished", "Sq Ft", "High-quality ceramic tiles for floors"));
        products.add(new Product("P002", "Porcelain Wall Tile", "Wall Tiles", 1800.00, 1200,
                "12x12 in", "6mm", "Matte", "Box", "Porcelain tiles for bathroom walls"));
        products.add(new Product("P003", "Marble Pattern Tile", "Decorative Tiles", 3500.00, 450,
                "24x24 in", "10mm", "Polished", "Slab", "Premium marble pattern decorative tiles"));
        products.add(new Product("P004", "Kitchen Backsplash Tile", "Kitchen Tiles", 2200.00, 300,
                "4x4 in", "5mm", "Glossy", "Box", "Glass mosaic tiles for kitchen backsplash"));
        products.add(new Product("P005", "Bathroom Mosaic Tile", "Bathroom Tiles", 2800.00, 150,
                "2x2 in", "8mm", "Honed", "Sheet", "Mosaic tiles for bathroom decor"));
        products.add(new Product("P006", "Outdoor Patio Tile", "Outdoor Tiles", 1900.00, 800,
                "16x16 in", "12mm", "Matte", "Piece", "Weather-resistant outdoor patio tiles"));
        products.add(new Product("P007", "Wooden Effect Tile", "Floor Tiles", 3200.00, 600,
                "6x36 in", "9mm", "Textured", "Box", "Wood-look ceramic tiles"));
        products.add(new Product("P008", "Glossy Wall Tile", "Wall Tiles", 1650.00, 2000,
                "8x8 in", "5mm", "Glossy", "Box", "Glossy white wall tiles"));
        products.add(new Product("P009", "Subway Tile White", "Kitchen Tiles", 1350.00, 1500,
                "3x6 in", "6mm", "Matte", "Box", "Classic white subway tiles"));
    }
    
    /**
     * Load all distinct categories from memory
     */
    private void loadCategories() {
        validCategories.clear();
        categoryCombo.removeAllItems();
        categoryCombo.addItem("Select Category");
        
        for (Product product : products) {
            if (!validCategories.contains(product.category)) {
                validCategories.add(product.category);
                categoryCombo.addItem(product.category);
            }
        }
        
        System.out.println("Loaded " + validCategories.size() + " categories from memory");
    }
    
    /**
     * Load all products from memory into table
     */
    private void loadProducts() {
        tableModel.setRowCount(0);
        int count = 0;
        
        for (Product product : products) {
            // Format price with PKR prefix
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String formattedPrice = "PKR " + df.format(product.price);
            
            tableModel.addRow(new Object[]{product.id, product.name, product.category, 
                                          product.quantity, formattedPrice});
            count++;
        }
        
        showStatusMessage("✅ Loaded " + count + " products from memory", false);
    }
    
    /**
     * Load complete product details for editing
     */
    private void loadProductDetails(String productId) {
        if (productId == null || productId.isEmpty()) return;
        
        Product product = getProductById(productId);
        if (product != null) {
            // Load all fields from product object
            nameField.setText(product.name);
            nameField.setForeground(TEXT_BLACK);
            showingNamePlaceholder = false;
            
            // Set category
            categoryCombo.setSelectedItem(product.category);
            categoryCombo.setEnabled(false); // Category cannot be changed
            
            quantityField.setText(String.valueOf(product.quantity));
            quantityField.setForeground(TEXT_BLACK);
            showingQuantityPlaceholder = false;
            
            priceField.setText(String.valueOf(product.price));
            priceField.setForeground(TEXT_BLACK);
            showingPricePlaceholder = false;
            
            sizeField.setText(product.size);
            sizeField.setForeground(TEXT_BLACK);
            showingSizePlaceholder = false;
            
            thicknessField.setText(product.thickness);
            thicknessField.setForeground(TEXT_BLACK);
            showingThicknessPlaceholder = false;
            
            // Set finish type
            if (product.finish != null) {
                finishCombo.setSelectedItem(product.finish);
            }
            
            // Set unit type
            if (product.unitType != null) {
                unitTypeCombo.setSelectedItem(product.unitType);
            }
            
            // Set description
            if (product.description != null && !product.description.isEmpty()) {
                descriptionArea.setText(product.description);
                descriptionArea.setForeground(TEXT_BLACK);
                showingDescriptionPlaceholder = false;
            }
            
            showStatusMessage("✅ Loaded details for: " + productId, false);
        } else {
            showStatusMessage("❌ Product not found: " + productId, true);
        }
    }
    
    /**
     * Get product by ID
     */
    private Product getProductById(String productId) {
        for (Product product : products) {
            if (product.id.equals(productId)) {
                return product;
            }
        }
        return null;
    }
    
    /**
     * Update product in memory
     */
    private boolean updateProductInMemory(String productId, String name, String category,
                                         String size, String thickness, String finish,
                                         String unitType, int quantity, double price,
                                         String description) {
        Product product = getProductById(productId);
        if (product == null) return false;
        
        // Update product details
        product.name = name;
        // Note: category is not updated as per business rules
        product.size = size;
        product.thickness = thickness;
        product.finish = finish;
        product.unitType = unitType;
        product.quantity = quantity;
        product.price = price;
        product.description = description;
        
        System.out.println("Product updated in memory: " + productId);
        return true;
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Title on left
        JLabel title = new JLabel("Update Product Details");
        title.setFont(TITLE_FONT);
        title.setForeground(TITLE_BLUE);
        topBar.add(title, BorderLayout.WEST);
        
        // Close button on right
        topBar.add(createCloseButton(), BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout(15, 0));
        mainContent.setOpaque(false);
        
        /* ================= LEFT PANEL - TABLE ================= */
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 2, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel tableTitle = new JLabel("Factory Products - Click any row to edit");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(new Color(80, 80, 100));
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        leftPanel.add(tableTitle, BorderLayout.NORTH);
        
        setupTable();
        
        JScrollPane tableScroll = new JScrollPane(table);
        styleTableScrollPane(tableScroll);
        leftPanel.add(tableScroll, BorderLayout.CENTER);
        
        statusLabel = new JLabel("Select a product from the table to edit");
        statusLabel.setFont(STATUS_FONT);
        statusLabel.setForeground(new Color(100, 100, 120));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        leftPanel.add(statusLabel, BorderLayout.SOUTH);
        
        mainContent.add(leftPanel, BorderLayout.CENTER);
        
        /* ================= RIGHT PANEL - EDIT FORM ================= */
        JPanel rightPanel = createEditFormPanel();
        mainContent.add(rightPanel, BorderLayout.EAST);
        
        return mainContent;
    }
    
    private JPanel createEditFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(320, 0));
        
        JLabel formTitle = new JLabel("Edit Product Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(TITLE_BLUE);
        formTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(formTitle, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Product ID
        addLabel(formPanel, gbc, "Product ID", true);
        gbc.gridy++;
        productIdField = createStyledTextField("Select from table", true);
        productIdField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        productIdField.setEditable(false);
        productIdField.setBackground(new Color(250, 250, 250));
        formPanel.add(productIdField, gbc);
        
        // Product Name
        gbc.gridy++;
        addLabel(formPanel, gbc, "Product Name", true);
        gbc.gridy++;
        nameField = createEditableTextField("Enter product name");
        formPanel.add(nameField, gbc);
        
        // Category
        gbc.gridy++;
        addLabel(formPanel, gbc, "Category", true);
        gbc.gridy++;
        categoryCombo = createComboBox(new String[]{"Select Category"});
        formPanel.add(categoryCombo, gbc);
        
        // Tile Size
        gbc.gridy++;
        addLabel(formPanel, gbc, "Tile Size", false);
        gbc.gridy++;
        sizeField = createEditableTextField("e.g., 2x2 ft, 12x12 in");
        formPanel.add(sizeField, gbc);
        
        // Thickness
        gbc.gridy++;
        addLabel(formPanel, gbc, "Thickness", false);
        gbc.gridy++;
        thicknessField = createEditableTextField("e.g., 8mm, 10mm");
        formPanel.add(thicknessField, gbc);
        
        // Finish Type
        gbc.gridy++;
        addLabel(formPanel, gbc, "Finish Type", false);
        gbc.gridy++;
        finishCombo = createComboBox(new String[]{"Select", "Polished", "Matte", "Honed", "Glossy", "Custom"});
        formPanel.add(finishCombo, gbc);
        
        // Unit Type
        gbc.gridy++;
        addLabel(formPanel, gbc, "Unit Type", true);
        gbc.gridy++;
        unitTypeCombo = createComboBox(new String[]{"Select", "Sq Ft", "Slab", "Box", "Piece", "Custom"});
        formPanel.add(unitTypeCombo, gbc);
        
        // Quantity
        gbc.gridy++;
        addLabel(formPanel, gbc, "Quantity", true);
        gbc.gridy++;
        quantityField = createEditableTextField("Enter quantity");
        formPanel.add(quantityField, gbc);
        
        // Price
        gbc.gridy++;
        addLabel(formPanel, gbc, "Price per Unit (PKR)", true);
        gbc.gridy++;
        priceField = createEditableTextField("Enter price in PKR");
        formPanel.add(priceField, gbc);
        
        // Description
        gbc.gridy++;
        addLabel(formPanel, gbc, "Description", false);
        gbc.gridy++;
        descriptionArea = createTextArea("Product description (optional)");
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(280, 80));
        formPanel.add(descScroll, gbc);
        
        gbc.gridy++;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);
        
        JScrollPane formScroll = new JScrollPane(formPanel);
        styleFormScrollPane(formScroll);
        panel.add(formScroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupTable() {
        table.setDefaultEditor(Object.class, null);
        table.setFont(TABLE_FONT);
        table.setRowHeight(36);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 225, 230));
        table.setSelectionBackground(TABLE_SELECTION);
        table.setSelectionForeground(TEXT_BLACK);
        table.setForeground(TEXT_BLACK);
        table.setFillsViewportHeight(true);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DARK_SKY_BLUE, 2),
                        BorderFactory.createEmptyBorder(2, 4, 2, 4)
                    ));
                    setBackground(TABLE_SELECTION);
                } else {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(240, 242, 245), 1),
                        BorderFactory.createEmptyBorder(3, 5, 3, 5)
                    ));
                    
                    if (row % 2 == 0) {
                        setBackground(Color.WHITE);
                    } else {
                        setBackground(new Color(248, 250, 252));
                    }
                }
                
                setHorizontalAlignment(JLabel.CENTER);
                setForeground(TEXT_BLACK);
                return c;
            }
        });
        
        JTableHeader header = table.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setForeground(TEXT_BLACK);
        header.setBackground(TABLE_HEADER_BG);
        header.setReorderingAllowed(false);
        
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(TABLE_HEADER_FONT);
                setForeground(TEXT_BLACK);
                setBackground(TABLE_HEADER_BG);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(200, 210, 220)),
                    BorderFactory.createEmptyBorder(8, 5, 8, 5)
                ));
                return this;
            }
        });
        
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(120);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(120);
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String id = table.getValueAt(row, 0).toString();
                    productIdField.setText(id);
                    productIdField.setForeground(TEXT_BLACK);
                    showingProductIdPlaceholder = false;
                    
                    loadProductDetails(id);
                    
                    statusLabel.setText("Editing: " + id);
                    statusLabel.setForeground(new Color(0, 100, 0));
                }
            }
        });
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 0, 0, 0));
        
        // Add Sample button
        addSampleButton = createStyledButton("➕ Add Sample", SKY_BLUE, DARK_SKY_BLUE);
        addSampleButton.addActionListener(e -> {
            initializeSampleData();
            loadProducts();
            loadCategories();
            showStatusMessage("✅ Sample products added", false);
        });
        addSampleButton.setToolTipText("Add sample products to memory");
        
        // Refresh button
        refreshButton = createStyledButton("🔄 Refresh", SKY_BLUE, DARK_SKY_BLUE);
        refreshButton.addActionListener(e -> {
            loadProducts();
            loadCategories();
            showStatusMessage("✅ Data refreshed", false);
        });
        refreshButton.setToolTipText("Reload data from memory");
        
        // Update button
        updateButton = createStyledButton("💾 Update Product", UPDATE_GREEN, HOVER_GREEN);
        updateButton.addActionListener(e -> updateProduct());
        
        // Clear button
        clearButton = createStyledButton("🗑️ Clear Form", SKY_BLUE, DARK_SKY_BLUE);
        clearButton.addActionListener(e -> clearForm());
        
        // Cancel button
        cancelButton = createStyledButton("✕ Cancel", CANCEL_COLOR, CANCEL_HOVER_RED);
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(addSampleButton);
        panel.add(refreshButton);
        panel.add(updateButton);
        panel.add(clearButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private JButton createCloseButton() {
        JButton btn = new JButton("✕");
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setForeground(new Color(120, 120, 120));
        btn.setPreferredSize(new Dimension(40, 40));
        btn.addActionListener(e -> dispose());
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(Color.RED);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 22));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(new Color(120, 120, 120));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
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
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(getBackground().darker());
                g2.setStroke(new BasicStroke(2.0f));
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
        button.setPreferredSize(new Dimension(150, 52));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button == cancelButton) {
                    button.setBackground(CANCEL_HOVER_RED);
                } else {
                    button.setBackground(hoverColor);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (button == cancelButton) {
                    button.setBackground(new Color(180, 60, 60));
                } else {
                    button.setBackground(normalColor.darker());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button == cancelButton) {
                    button.setBackground(CANCEL_HOVER_RED);
                } else {
                    button.setBackground(hoverColor);
                }
            }
        });
        
        return button;
    }
    
    private void addLabel(JPanel panel, GridBagConstraints gbc, String text, boolean required) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_BLACK);
        labelPanel.add(label);
        
        if (required) {
            JLabel star = new JLabel(" *");
            star.setFont(LABEL_FONT);
            star.setForeground(ERROR_RED);
            labelPanel.add(star);
        }
        
        panel.add(labelPanel, gbc);
        gbc.gridy++;
    }
    
    private JTextField createEditableTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(INPUT_BORDER);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        field.setFont(INPUT_FONT);
        field.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        field.setBackground(Color.WHITE);
        field.setForeground(PLACEHOLDER_GRAY);
        field.setText(placeholder);
        field.setCaretColor(DARK_SKY_BLUE);
        field.setPreferredSize(new Dimension(280, 40));
        field.putClientProperty("placeholder", placeholder);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String currentText = field.getText();
                String placeholderText = (String) field.getClientProperty("placeholder");
                
                if (currentText.equals(placeholderText)) {
                    field.setText("");
                    field.setForeground(TEXT_BLACK);
                }
                field.selectAll();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                String currentText = field.getText();
                String placeholderText = (String) field.getClientProperty("placeholder");
                
                if (currentText.isEmpty()) {
                    field.setText(placeholderText);
                    field.setForeground(PLACEHOLDER_GRAY);
                }
            }
        });
        
        return field;
    }
    
    private JTextField createStyledTextField(String placeholder, boolean readOnly) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(INPUT_BORDER);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        field.setFont(INPUT_FONT);
        field.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        field.setBackground(Color.WHITE);
        field.setForeground(readOnly ? TEXT_BLACK : PLACEHOLDER_GRAY);
        field.setText(placeholder);
        field.setCaretColor(DARK_SKY_BLUE);
        field.setPreferredSize(new Dimension(280, 40));
        field.setEditable(!readOnly);
        
        return field;
    }
    
    private JComboBox<String> createComboBox(String[] items) {
        return new JComboBox<String>(items) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(INPUT_BORDER);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
    
    private JTextArea createTextArea(String placeholder) {
        JTextArea area = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(INPUT_BORDER);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        area.setFont(INPUT_FONT);
        area.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        area.setBackground(Color.WHITE);
        area.setForeground(PLACEHOLDER_GRAY);
        area.setText(placeholder);
        area.setCaretColor(DARK_SKY_BLUE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.putClientProperty("placeholder", placeholder);
        
        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String currentText = area.getText();
                String placeholderText = (String) area.getClientProperty("placeholder");
                
                if (currentText.equals(placeholderText)) {
                    area.setText("");
                    area.setForeground(TEXT_BLACK);
                    showingDescriptionPlaceholder = false;
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                String currentText = area.getText();
                String placeholderText = (String) area.getClientProperty("placeholder");
                
                if (currentText.isEmpty()) {
                    area.setText(placeholderText);
                    area.setForeground(PLACEHOLDER_GRAY);
                    showingDescriptionPlaceholder = true;
                }
            }
        });
        
        return area;
    }
    
    private void styleTableScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 220, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setPreferredSize(new Dimension(10, 0));
        vertical.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = SCROLLBAR_COLOR;
                this.trackColor = new Color(245, 245, 245);
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
                return button;
            }
        });
        
        JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
        horizontal.setPreferredSize(new Dimension(0, 10));
        horizontal.setUI(vertical.getUI());
    }
    
    private void styleFormScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LIGHT_BLUE_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }
    
    private void centerDialog(JFrame ownerFrame, JPanel contentPanel) {
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
    }
    
    /**
     * Validate form fields
     */
    private boolean validateForm() {
        String id = productIdField.getText().trim();
        String name = nameField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String qty = quantityField.getText().trim();
        String price = priceField.getText().trim();
        String unitType = (String) unitTypeCombo.getSelectedItem();
        
        // Check required fields
        if (showingProductIdPlaceholder || id.isEmpty()) {
            showErrorMessage("Please select a product from the table", "Validation Error");
            return false;
        }
        
        if (showingNamePlaceholder || name.isEmpty()) {
            showErrorMessage("Product Name is required", "Validation Error");
            nameField.requestFocus();
            return false;
        }
        
        if (category == null || category.equals("Select Category")) {
            showErrorMessage("Please select a Category", "Validation Error");
            categoryCombo.requestFocus();
            return false;
        }
        
        if (unitType == null || unitType.equals("Select")) {
            showErrorMessage("Please select a Unit Type", "Validation Error");
            unitTypeCombo.requestFocus();
            return false;
        }
        
        if (showingQuantityPlaceholder || qty.isEmpty()) {
            showErrorMessage("Quantity is required", "Validation Error");
            quantityField.requestFocus();
            return false;
        }
        
        if (showingPricePlaceholder || price.isEmpty()) {
            showErrorMessage("Price is required", "Validation Error");
            priceField.requestFocus();
            return false;
        }
        
        // Validate numeric fields
        try {
            int quantity = Integer.parseInt(qty);
            if (quantity < 0) {
                showErrorMessage("Quantity cannot be negative", "Validation Error");
                quantityField.requestFocus();
                return false;
            }
            
            double priceValue = Double.parseDouble(price);
            if (priceValue <= 0) {
                showErrorMessage("Price must be greater than 0", "Validation Error");
                priceField.requestFocus();
                return false;
            }
            
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid number format in quantity or price", "Validation Error");
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if product exists
     */
    private boolean productExists(String productId) {
        return getProductById(productId) != null;
    }
    
    /**
     * Update product in memory
     */
    private void updateProduct() {
        if (!validateForm()) {
            return;
        }
        
        String id = productIdField.getText().trim();
        String name = nameField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String size = showingSizePlaceholder ? "" : sizeField.getText().trim();
        String thickness = showingThicknessPlaceholder ? "" : thicknessField.getText().trim();
        String finish = (String) finishCombo.getSelectedItem();
        String unitType = (String) unitTypeCombo.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText().trim());
        double price = Double.parseDouble(priceField.getText().trim());
        String description = showingDescriptionPlaceholder ? "" : descriptionArea.getText().trim();
        
        // Check if product exists
        if (!productExists(id)) {
            showErrorMessage("Product not found: " + id, "Not Found");
            return;
        }
        
        // Confirm update
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><div style='width:350px;'>" +
            "<b style='color:#28a745;'>Confirm Product Update</b><br><br>" +
            "<b>Product ID:</b> " + id + "<br>" +
            "<b>Name:</b> " + name + "<br>" +
            "<b>Category:</b> " + category + "<br>" +
            "<b>Quantity:</b> " + quantity + "<br>" +
            "<b>Price:</b> PKR " + String.format("%,.2f", price) + "<br><br>" +
            "Update this product in memory?" +
            "</div></html>",
            "Confirm Update",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            showStatusMessage("⏸️ Update cancelled", false);
            return;
        }
        
        // Update in memory
        boolean success = updateProductInMemory(id, name, category, size, thickness,
                                               finish, unitType, quantity, price, description);
        
        if (success) {
            // Refresh table
            loadProducts();
            
            // Show success message
            showStatusMessage("✅ Product updated successfully: " + id, false);
            
            JOptionPane.showMessageDialog(this,
                "<html><div style='width:300px;'>" +
                "<b style='color:#28a745;'>✅ Update Successful!</b><br><br>" +
                "Product <b>" + id + "</b> has been updated in memory.<br>" +
                "All changes have been saved for this session." +
                "</div></html>",
                "Update Complete",
                JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
        } else {
            showErrorMessage("Failed to update product", "Error");
        }
    }
    
    private void clearForm() {
        productIdField.setText("Select from table");
        productIdField.setForeground(PLACEHOLDER_GRAY);
        showingProductIdPlaceholder = true;
        
        nameField.setText("Enter product name");
        nameField.setForeground(PLACEHOLDER_GRAY);
        showingNamePlaceholder = true;
        
        categoryCombo.setSelectedIndex(0);
        categoryCombo.setEnabled(true);
        
        sizeField.setText("e.g., 2x2 ft, 12x12 in");
        sizeField.setForeground(PLACEHOLDER_GRAY);
        showingSizePlaceholder = true;
        
        thicknessField.setText("e.g., 8mm, 10mm");
        thicknessField.setForeground(PLACEHOLDER_GRAY);
        showingThicknessPlaceholder = true;
        
        finishCombo.setSelectedIndex(0);
        unitTypeCombo.setSelectedIndex(0);
        
        quantityField.setText("Enter quantity");
        quantityField.setForeground(PLACEHOLDER_GRAY);
        showingQuantityPlaceholder = true;
        
        priceField.setText("Enter price in PKR");
        priceField.setForeground(PLACEHOLDER_GRAY);
        showingPricePlaceholder = true;
        
        descriptionArea.setText("Product description (optional)");
        descriptionArea.setForeground(PLACEHOLDER_GRAY);
        showingDescriptionPlaceholder = true;
        
        table.clearSelection();
        statusLabel.setText("Select a product from the table to edit");
        statusLabel.setForeground(new Color(100, 100, 120));
        
        nameField.requestFocus();
    }
    
    private void showStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? ERROR_RED : SUCCESS_GREEN);
    }
    
    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        showStatusMessage("❌ " + message, true);
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
            
            g2.setColor(new Color(0, 0, 0, 15));
            for (int i = 0; i < 3; i++) {
                g2.fillRoundRect(i, i, getWidth()-2*i, getHeight()-2*i, cornerRadius, cornerRadius);
            }
            
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            g2.setColor(new Color(180, 200, 220));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    /* ================= TEST ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Tile Factory - Product Update");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(new Color(240, 248, 255));
            
            JButton testBtn = new JButton("Open Product Update (In-Memory)");
            testBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            testBtn.setBackground(new Color(30, 144, 255));
            testBtn.setForeground(Color.WHITE);
            testBtn.setFocusPainted(false);
            testBtn.setPreferredSize(new Dimension(300, 50));
            testBtn.addActionListener(e -> {
                UpdateProductDialog dlg = new UpdateProductDialog(frame, null);
                dlg.setVisible(true);
            });
            
            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setBackground(new Color(240, 248, 255));
            centerPanel.add(testBtn);
            
            frame.add(centerPanel);
            frame.setVisible(true);
        });
    }
}