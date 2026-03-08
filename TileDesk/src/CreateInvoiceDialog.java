package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * CreateInvoiceDialog - Modern Invoice Creation Form
 * Enhanced with live product search, auto-complete, and inventory validation
 * Uses in-memory data instead of database
 */
public class CreateInvoiceDialog extends JDialog {
    // UI Colors
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
    private static final Color TABLE_HEADER_BG = new Color(230, 240, 255);
    private static final Color TABLE_GRID_COLOR = new Color(220, 230, 240);
    private static final Color PLACEHOLDER_COLOR = new Color(160, 160, 180);
    private static final Color SUGGESTION_BG = new Color(255, 255, 255);
    private static final Color SUGGESTION_HOVER = new Color(240, 247, 255);
    private static final Color SUGGESTION_BORDER = new Color(200, 220, 240);
    private static final Color OUT_OF_STOCK_COLOR = new Color(220, 53, 69, 150);
    
    // Form fields
    private JTextField invoiceIdField;
    private JTextField customerNameField;
    private JTextField customerEmailField;
    private JTextField customerPhoneField;
    private JTextField dateField;
    private JTextField dueDateField;
    private JTextArea notesArea;
    private JSpinner taxSpinner;
    private JSpinner discountSpinner;
    
    // Invoice items table
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    
    // Product search/suggestion components
    private JPopupMenu suggestionPopup;
    private JList<String> suggestionList;
    private DefaultListModel<String> suggestionModel;
    private List<ProductData> allProducts;
    private List<ProductData> filteredProducts;
    private boolean showingSuggestions = false;
    private int currentEditingRow = -1;
    private int currentEditingColumn = -1;
    
    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 15);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font PLACEHOLDER_FONT = new Font("Segoe UI", Font.ITALIC, 14);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font TABLE_HEADER_FONT = new Font("Segoe UI Semibold", Font.BOLD, 14);
    private final Font TABLE_PLACEHOLDER_FONT = new Font("Segoe UI", Font.ITALIC, 15);
    private final Font SUGGESTION_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    // Save status
    private boolean saveSuccessful = false;
    private InvoiceData invoiceData = null;
    
    // Button references
    private JButton saveButton;
    private JButton cancelButton;
    private JButton regenButton;
    private JButton addItemButton;
    private JButton removeItemButton;
    private JButton clearItemsButton;
    private JButton addSampleButton;
    
    // Totals
    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel discountLabel;
    private JLabel totalLabel;
    
    // Invoice storage (in-memory)
    private static List<InvoiceData> createdInvoices = new ArrayList<>();
    
    /* ================= DATA CLASSES ================= */
    public static class InvoiceData {
        private final String invoiceId;
        private final String customerName;
        private final String customerEmail;
        private final String customerPhone;
        private final String invoiceDate;
        private final String dueDate;
        private final double subtotal;
        private final double tax;
        private final double discount;
        private final double total;
        private final String notes;
        private final Object[][] items;
        
        public InvoiceData(String invoiceId, String customerName, String customerEmail, 
                          String customerPhone, String invoiceDate, String dueDate,
                          double subtotal, double tax, double discount, double total,
                          String notes, Object[][] items) {
            this.invoiceId = invoiceId;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.customerPhone = customerPhone;
            this.invoiceDate = invoiceDate;
            this.dueDate = dueDate;
            this.subtotal = subtotal;
            this.tax = tax;
            this.discount = discount;
            this.total = total;
            this.notes = notes;
            this.items = items;
        }
        
        // Getters
        public String getInvoiceId() { return invoiceId; }
        public String getCustomerName() { return customerName; }
        public String getCustomerEmail() { return customerEmail; }
        public String getCustomerPhone() { return customerPhone; }
        public String getInvoiceDate() { return invoiceDate; }
        public String getDueDate() { return dueDate; }
        public double getSubtotal() { return subtotal; }
        public double getTax() { return tax; }
        public double getDiscount() { return discount; }
        public double getTotal() { return total; }
        public String getNotes() { return notes; }
        public Object[][] getItems() { return items; }
    }
    
    // Product Data Class (in-memory)
    public static class ProductData {
        private String id;
        private String name;
        private String category;
        private int quantity;
        private double price;
        private String size;
        private String thickness;
        private String finish;
        private String unitType;
        private String description;
        private boolean usedInSales;

        public ProductData(String id, String name, String category, int quantity, double price,
                          String size, String thickness, String finish, String unitType, 
                          String description, boolean usedInSales) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.quantity = quantity;
            this.price = price;
            this.size = size;
            this.thickness = thickness;
            this.finish = finish;
            this.unitType = unitType;
            this.description = description;
            this.usedInSales = usedInSales;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public String getSize() { return size; }
        public String getThickness() { return thickness; }
        public String getFinish() { return finish; }
        public String getUnitType() { return unitType; }
        public String getDescription() { return description; }
        public boolean isUsedInSales() { return usedInSales; }
        public double getTotalValue() { return quantity * price; }
        
        public String getStockStatus() {
            if (quantity == 0) return "Out of Stock";
            else if (quantity < 50) return "Low Stock";
            else if (quantity < 100) return "Medium Stock";
            else return "In Stock";
        }
        
        public Color getStockStatusColor() {
            if (quantity == 0) return new Color(220, 53, 69);
            else if (quantity < 50) return new Color(255, 193, 7);
            else if (quantity < 100) return new Color(25, 135, 84);
            else return new Color(40, 167, 69);
        }
    }
    
    /* ================= CONSTRUCTOR ================= */
    public CreateInvoiceDialog(Window ownerFrame, JPanel contentPanel) {
        super(ownerFrame, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Initialize with sample product data
        initializeProductData();
        setupSuggestionComponents();
        
        // Main card panel
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30, 35, 30, 35));
        card.setPreferredSize(new Dimension(1000, 750));
        
        /* ================= TOP BAR ================= */
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Title on left
        JLabel title = new JLabel("Create New Invoice (In-Memory)");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        topBar.add(title, BorderLayout.WEST);
        
        // Status indicator
        JLabel statusLabel = new JLabel("Data Source: In-Memory Storage");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(40, 167, 69));
        topBar.add(statusLabel, BorderLayout.CENTER);
        
        // Close button on right
        JButton closeBtn = createCloseButton();
        topBar.add(closeBtn, BorderLayout.EAST);
        
        card.add(topBar, BorderLayout.NORTH);
        
        /* ================= FORM PANEL ================= */
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
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
        setSize(1000, 750);
        
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
        generateInvoiceId();
        setCurrentDate();
        
        // Set focus to first field
        SwingUtilities.invokeLater(() -> customerNameField.requestFocus());
    }
    
    /* ================= DATA METHODS ================= */
    
    private void initializeProductData() {
        allProducts = new ArrayList<>();
        filteredProducts = new ArrayList<>();
        
        // Add sample products
        allProducts.add(new ProductData("P001", "Premium Ceramic Tile", "Floor Tiles", 50, 2500.00,
                "2x2 ft", "8mm", "Polished", "Sq Ft", "High-quality ceramic tiles for floors", false));
        allProducts.add(new ProductData("P002", "Porcelain Wall Tile", "Wall Tiles", 0, 1800.00,
                "12x12 in", "6mm", "Matte", "Box", "Porcelain tiles for bathroom walls", true));
        allProducts.add(new ProductData("P003", "Marble Pattern Tile", "Decorative Tiles", 120, 3500.00,
                "24x24 in", "10mm", "Polished", "Slab", "Premium marble pattern decorative tiles", false));
        allProducts.add(new ProductData("P004", "Kitchen Backsplash Tile", "Kitchen Tiles", 30, 2200.00,
                "4x4 in", "5mm", "Glossy", "Box", "Glass mosaic tiles for kitchen backsplash", false));
        allProducts.add(new ProductData("P005", "Bathroom Mosaic Tile", "Bathroom Tiles", 15, 2800.00,
                "2x2 in", "8mm", "Honed", "Sheet", "Mosaic tiles for bathroom decor", false));
        allProducts.add(new ProductData("P006", "Outdoor Patio Tile", "Outdoor Tiles", 80, 1900.00,
                "16x16 in", "12mm", "Matte", "Piece", "Weather-resistant outdoor patio tiles", false));
        allProducts.add(new ProductData("P007", "Wooden Effect Tile", "Floor Tiles", 200, 3200.00,
                "6x36 in", "9mm", "Textured", "Box", "Wood-look ceramic tiles", false));
        allProducts.add(new ProductData("P008", "Glossy Wall Tile", "Wall Tiles", 5, 1650.00,
                "8x8 in", "5mm", "Glossy", "Box", "Glossy white wall tiles", true));
        allProducts.add(new ProductData("P009", "Subway Tile White", "Kitchen Tiles", 45, 1350.00,
                "3x6 in", "6mm", "Matte", "Box", "Classic white subway tiles", true));
        allProducts.add(new ProductData("P010", "Hexagon Pattern Tile", "Decorative Tiles", 0, 2950.00,
                "Hexagon", "7mm", "Polished", "Sheet", "Hexagon pattern decorative tiles", false));
        
        System.out.println("Loaded " + allProducts.size() + " sample products in memory");
    }
    
    private void setupSuggestionComponents() {
        suggestionModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionModel);
        suggestionList.setFont(SUGGESTION_FONT);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setBackground(SUGGESTION_BG);
        suggestionList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Custom cell renderer to show stock status
        suggestionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                
                if (index >= 0 && index < filteredProducts.size()) {
                    ProductData product = filteredProducts.get(index);
                    
                    // Add stock info to the display
                    String displayText = product.getName();
                    if (product.getQuantity() <= 0) {
                        displayText += " (Out of Stock)";
                        label.setForeground(OUT_OF_STOCK_COLOR);
                    } else if (product.getQuantity() < 10) {
                        displayText += " (Low Stock: " + product.getQuantity() + ")";
                        label.setForeground(new Color(255, 140, 0)); // Orange
                    } else {
                        displayText += " (In Stock: " + product.getQuantity() + ")";
                        label.setForeground(new Color(40, 40, 40));
                    }
                    
                    label.setText(displayText);
                    
                    if (isSelected) {
                        label.setBackground(SUGGESTION_HOVER);
                        label.setBorder(BorderFactory.createLineBorder(DARK_SKY_BLUE, 1));
                    } else {
                        label.setBackground(SUGGESTION_BG);
                        label.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                    }
                }
                
                return label;
            }
        });
        
        suggestionPopup = new JPopupMenu();
        suggestionPopup.setBorder(BorderFactory.createLineBorder(SUGGESTION_BORDER, 1));
        suggestionPopup.add(new JScrollPane(suggestionList));
        suggestionList.setPreferredSize(new Dimension(300, 150));
        
        // Handle selection from suggestion list
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectSuggestion();
                }
            }
        });
        
        suggestionList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectSuggestion();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hideSuggestions();
                    e.consume();
                }
            }
        });
    }
    
    private void searchProducts(String searchText) {
        filteredProducts.clear();
        suggestionModel.clear();
        
        if (searchText == null || searchText.trim().isEmpty()) {
            hideSuggestions();
            return;
        }
        
        String searchLower = searchText.toLowerCase().trim();
        
        // Filter products whose name contains the search text
        for (ProductData product : allProducts) {
            if (product.getName().toLowerCase().contains(searchLower)) {
                filteredProducts.add(product);
            }
        }
        
        if (filteredProducts.isEmpty()) {
            hideSuggestions();
        } else {
            // Update suggestion list
            for (ProductData product : filteredProducts) {
                suggestionModel.addElement(product.getName());
            }
            showSuggestions();
        }
    }
    
    private void selectSuggestion() {
        int selectedIndex = suggestionList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < filteredProducts.size() && currentEditingRow >= 0) {
            ProductData selectedProduct = filteredProducts.get(selectedIndex);
            
            // Fill product details in the table
            tableModel.setValueAt(selectedProduct.getName(), currentEditingRow, 0);
            tableModel.setValueAt(selectedProduct.getCategory(), currentEditingRow, 1);
            tableModel.setValueAt(1, currentEditingRow, 2); // Default quantity = 1
            
            // Format price nicely
            String priceText = String.format("%.2f", selectedProduct.getPrice());
            tableModel.setValueAt(priceText, currentEditingRow, 3);
            
            // Calculate total for this row
            calculateRowTotal(currentEditingRow);
            
            // Move to quantity cell for easy editing
            hideSuggestions();
            itemsTable.editCellAt(currentEditingRow, 2);
            itemsTable.getEditorComponent().requestFocus();
            
            // Validate stock availability
            if (selectedProduct.getQuantity() <= 0) {
                JOptionPane.showMessageDialog(this,
                    "⚠️ Warning: " + selectedProduct.getName() + " is out of stock!",
                    "Stock Alert",
                    JOptionPane.WARNING_MESSAGE);
            } else if (selectedProduct.getQuantity() < 10) {
                JOptionPane.showMessageDialog(this,
                    "⚠️ Low Stock: " + selectedProduct.getName() + " has only " + 
                    selectedProduct.getQuantity() + " units available.",
                    "Low Stock Alert",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void showSuggestions() {
        if (currentEditingRow < 0 || !itemsTable.isEditing()) return;
        
        Component editor = itemsTable.getEditorComponent();
        if (editor instanceof JTextField) {
            JTextField textField = (JTextField) editor;
            Point location = textField.getLocationOnScreen();
            if (location != null) {
                suggestionPopup.setPreferredSize(new Dimension(textField.getWidth(), 150));
                suggestionPopup.show(textField, 0, textField.getHeight());
                showingSuggestions = true;
                
                // Select first item by default
                if (suggestionList.getModel().getSize() > 0) {
                    suggestionList.setSelectedIndex(0);
                }
            }
        }
    }
    
    private void hideSuggestions() {
        if (showingSuggestions) {
            suggestionPopup.setVisible(false);
            showingSuggestions = false;
        }
    }
    
    /* ================= MAIN UI COMPONENTS ================= */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Invoice ID (Required)
        addLabel(panel, gbc, "Invoice ID", true);
        gbc.gridy++;
        invoiceIdField = createStyledTextField("INV-0000", true);
        invoiceIdField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(invoiceIdField, gbc);
        
        // Customer Name (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Customer Name", true);
        gbc.gridy++;
        customerNameField = createStyledTextField("Enter customer name", false);
        panel.add(customerNameField, gbc);
        
        // Customer Email (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Customer Email", false);
        gbc.gridy++;
        customerEmailField = createStyledTextField("email@example.com", false);
        panel.add(customerEmailField, gbc);
        
        // Customer Phone (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Customer Phone", false);
        gbc.gridy++;
        customerPhoneField = createStyledTextField("(123) 456-7890", false);
        panel.add(customerPhoneField, gbc);
        
        // Invoice Date (Required) - Auto-filled but editable
        gbc.gridy++;
        addLabel(panel, gbc, "Invoice Date", true);
        gbc.gridy++;
        dateField = createAutoDateField();
        dateField.setForeground(new Color(40, 40, 40));
        panel.add(dateField, gbc);
        
        // Due Date (Required) - Auto-filled but editable
        gbc.gridy++;
        addLabel(panel, gbc, "Due Date", true);
        gbc.gridy++;
        dueDateField = createAutoDateField();
        dueDateField.setForeground(new Color(40, 40, 40));
        panel.add(dueDateField, gbc);
        
        // Items Table Section
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 5, 10);
        JLabel tableTitle = new JLabel("Invoice Items");
        tableTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        tableTitle.setForeground(DARK_SKY_BLUE);
        panel.add(tableTitle, gbc);
        
        // Table
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JPanel tablePanel = createItemsTable();
        panel.add(tablePanel, gbc);
        
        // Item Controls with larger rounded buttons
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 10, 10, 10);
        JPanel itemControls = createItemControls();
        panel.add(itemControls, gbc);
        
        // Calculation Panel
        gbc.gridy++;
        gbc.insets = new Insets(15, 10, 10, 10);
        JPanel calcPanel = createCalculationPanel();
        panel.add(calcPanel, gbc);
        
        // Notes (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Notes", false);
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 10, 10);
        notesArea = createStyledTextArea("Additional notes or terms...", 3);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        styleScrollPane(notesScroll);
        panel.add(notesScroll, gbc);
        
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
    
    private JPanel createItemsTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Table model
        String[] columns = {"Item", "Description", "Quantity", "Unit Price", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // All cells are editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class; // Item
                    case 1: return String.class; // Description
                    case 2: return Integer.class; // Quantity
                    case 3: return Double.class; // Unit Price
                    case 4: return Double.class; // Total
                    default: return Object.class;
                }
            }
        };
        
        // Create table with custom renderer and editor
        itemsTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // Alternate row colors
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                
                // Check if cell has placeholder value
                Object value = getValueAt(row, column);
                JLabel label = (JLabel) c;
                
                if (value != null) {
                    String cellText = value.toString();
                    boolean isPlaceholder = false;
                    
                    // Check if this is a placeholder value
                    switch (column) {
                        case 0: // Item
                            isPlaceholder = cellText.equals("Enter item name") || cellText.isEmpty();
                            break;
                        case 1: // Description
                            isPlaceholder = cellText.equals("Enter description") || cellText.isEmpty();
                            break;
                        case 2: // Quantity
                            isPlaceholder = cellText.equals("Qty") || cellText.isEmpty();
                            break;
                        case 3: // Unit Price
                            isPlaceholder = cellText.equals("0.00") || cellText.isEmpty();
                            break;
                        case 4: // Total
                            isPlaceholder = cellText.equals("0.00") || cellText.isEmpty();
                            break;
                    }
                    
                    if (isPlaceholder) {
                        label.setFont(TABLE_PLACEHOLDER_FONT);
                        label.setForeground(PLACEHOLDER_COLOR);
                    } else {
                        label.setFont(TABLE_FONT);
                        label.setForeground(Color.BLACK);
                    }
                }
                
                // Right align numeric columns
                if (column >= 2) {
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return c;
            }
            
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                super.setValueAt(aValue, row, column);
                
                // Auto-calculate total when quantity or price changes
                if (column == 2 || column == 3) {
                    calculateRowTotal(row);
                    calculateTotals();
                }
                
                // Update the display
                repaint();
            }
        };
        
        // Allow multiple row selection
        itemsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // Style table
        itemsTable.setFont(TABLE_FONT);
        itemsTable.setRowHeight(36);
        itemsTable.setShowGrid(true);
        itemsTable.setGridColor(TABLE_GRID_COLOR);
        itemsTable.setSelectionBackground(SKY_BLUE);
        itemsTable.setSelectionForeground(Color.WHITE);
        itemsTable.setIntercellSpacing(new Dimension(0, 0));
        
        // Style header
        JTableHeader header = itemsTable.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(LABEL_COLOR);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        
        // Set column renderers for proper alignment
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Check for placeholder
                if (value != null) {
                    String text = value.toString();
                    if (text.equals("0.00") || text.equals("Qty") || 
                        (column == 4 && text.equals("0.00"))) {
                        c.setFont(TABLE_PLACEHOLDER_FONT);
                        c.setForeground(PLACEHOLDER_COLOR);
                    } else {
                        c.setFont(TABLE_FONT);
                        c.setForeground(Color.BLACK);
                    }
                }
                
                setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        };
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Check for placeholder
                if (value != null) {
                    String text = value.toString();
                    if ((column == 0 && (text.equals("Enter item name") || text.isEmpty())) ||
                        (column == 1 && (text.equals("Enter description") || text.isEmpty()))) {
                        c.setFont(TABLE_PLACEHOLDER_FONT);
                        c.setForeground(PLACEHOLDER_COLOR);
                    } else {
                        c.setFont(TABLE_FONT);
                        c.setForeground(Color.BLACK);
                    }
                }
                
                setHorizontalAlignment(SwingConstants.LEFT);
                return c;
            }
        };
        
        // Set column widths
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Item
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Description
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Quantity
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Unit Price
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Total
        
        // Set renderers
        itemsTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        itemsTable.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        itemsTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        itemsTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        itemsTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        
        // Custom editor for item name with search functionality
        itemsTable.getColumnModel().getColumn(0).setCellEditor(new ProductSearchCellEditor());
        itemsTable.getColumnModel().getColumn(1).setCellEditor(new PlaceholderCellEditor("Enter description"));
        itemsTable.getColumnModel().getColumn(2).setCellEditor(new PlaceholderCellEditor("Qty"));
        itemsTable.getColumnModel().getColumn(3).setCellEditor(new PlaceholderCellEditor("0.00"));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        styleScrollPane(scrollPane);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add listener to calculate totals when data changes
        tableModel.addTableModelListener(e -> calculateTotals());
        
        return panel;
    }
    
    /* ================= CUSTOM CELL EDITORS ================= */
    
    private class ProductSearchCellEditor extends DefaultCellEditor {
        private JTextField textField;
        private String originalValue;
        
        public ProductSearchCellEditor() {
            super(new JTextField());
            this.textField = (JTextField) getComponent();
            this.textField.setFont(TABLE_FONT);
            
            // Add key listener for live search
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        hideSuggestions();
                        stopCellEditing();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (showingSuggestions) {
                            selectSuggestion();
                        } else {
                            stopCellEditing();
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                        // Navigate suggestion list with arrow keys
                        if (showingSuggestions) {
                            int current = suggestionList.getSelectedIndex();
                            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                                current = Math.min(current + 1, suggestionList.getModel().getSize() - 1);
                            } else {
                                current = Math.max(current - 1, 0);
                            }
                            suggestionList.setSelectedIndex(current);
                            suggestionList.ensureIndexIsVisible(current);
                            e.consume();
                        }
                    } else {
                        // Live search on typing
                        String searchText = textField.getText();
                        if (searchText.length() >= 2) { // Start searching after 2 characters
                            SwingUtilities.invokeLater(() -> {
                                searchProducts(searchText);
                            });
                        } else {
                            hideSuggestions();
                        }
                    }
                }
                
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        hideSuggestions();
                        stopCellEditing();
                        // Move to next cell
                        itemsTable.editCellAt(currentEditingRow, currentEditingColumn + 1);
                    }
                }
            });
            
            // Hide suggestions when focus is lost
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    // Delay hiding to allow for suggestion selection
                    SwingUtilities.invokeLater(() -> {
                        if (!suggestionPopup.isVisible() || 
                            !suggestionList.hasFocus() && !textField.hasFocus()) {
                            hideSuggestions();
                        }
                    });
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
            originalValue = (value != null) ? value.toString() : "";
            
            // Track which cell is being edited
            currentEditingRow = row;
            currentEditingColumn = column;
            
            // Clear any previous suggestions
            hideSuggestions();
            
            return c;
        }
        
        @Override
        public boolean stopCellEditing() {
            hideSuggestions();
            return super.stopCellEditing();
        }
        
        @Override
        public void cancelCellEditing() {
            hideSuggestions();
            super.cancelCellEditing();
        }
    }
    
    private class PlaceholderCellEditor extends DefaultCellEditor {
        private String placeholder;
        private JTextField textField;
        
        public PlaceholderCellEditor(String placeholder) {
            super(new JTextField());
            this.placeholder = placeholder;
            this.textField = (JTextField) getComponent();
            this.textField.setFont(TABLE_FONT);
            
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (textField.getText().equals(placeholder)) {
                        textField.setText("");
                        textField.setForeground(Color.BLACK);
                    }
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    if (textField.getText().isEmpty()) {
                        textField.setText(placeholder);
                        textField.setForeground(PLACEHOLDER_COLOR);
                    }
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
            
            if (value == null || value.toString().equals(placeholder) || value.toString().isEmpty()) {
                textField.setText(placeholder);
                textField.setForeground(PLACEHOLDER_COLOR);
            } else {
                textField.setForeground(Color.BLACK);
            }
            
            return c;
        }
        
        @Override
        public Object getCellEditorValue() {
            String value = textField.getText();
            if (value.equals(placeholder)) {
                return ""; // Return empty string for placeholder
            }
            return value;
        }
    }
    
    /* ================= REMAINING METHODS ================= */
    
    private JPanel createItemControls() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setOpaque(false);
        
        addItemButton = createLargeRoundedButton("＋ Add Item", SKY_BLUE, DARK_SKY_BLUE);
        addItemButton.addActionListener(e -> addNewItem());
        
        removeItemButton = createLargeRoundedButton("－ Remove Selected", new Color(250, 150, 150), new Color(220, 100, 100));
        removeItemButton.addActionListener(e -> removeSelectedItems());
        
        clearItemsButton = createLargeRoundedButton("🗑️ Clear All", new Color(250, 200, 150), new Color(220, 150, 100));
        clearItemsButton.addActionListener(e -> clearAllItems());
        
        panel.add(addItemButton);
        panel.add(removeItemButton);
        panel.add(clearItemsButton);
        
        return panel;
    }
    
    private JPanel createCalculationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createCalcLabel("Subtotal:"), gbc);
        gbc.gridx = 1;
        subtotalLabel = createCalcValueLabel("PKR 0.00");
        panel.add(subtotalLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JPanel taxPanel = new JPanel(new BorderLayout(10, 0));
        taxPanel.setOpaque(false);
        taxPanel.add(createCalcLabel("Tax (%):"), BorderLayout.WEST);
        taxSpinner = createSmallSpinner(0.0, 0.0, 50.0, 0.5);
        taxSpinner.addChangeListener(e -> calculateTotals());
        taxPanel.add(taxSpinner, BorderLayout.CENTER);
        panel.add(taxPanel, gbc);
        
        gbc.gridx = 1;
        taxLabel = createCalcValueLabel("PKR 0.00");
        panel.add(taxLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JPanel discountPanel = new JPanel(new BorderLayout(10, 0));
        discountPanel.setOpaque(false);
        discountPanel.add(createCalcLabel("Discount (%):"), BorderLayout.WEST);
        discountSpinner = createSmallSpinner(0.0, 0.0, 100.0, 1.0);
        discountSpinner.addChangeListener(e -> calculateTotals());
        discountPanel.add(discountSpinner, BorderLayout.CENTER);
        panel.add(discountPanel, gbc);
        
        gbc.gridx = 1;
        discountLabel = createCalcValueLabel("PKR 0.00");
        panel.add(discountLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel totalText = createCalcLabel("TOTAL:");
        totalText.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        totalText.setForeground(DARK_SKY_BLUE);
        panel.add(totalText, gbc);
        
        gbc.gridx = 1;
        totalLabel = createCalcValueLabel("PKR 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(DARK_SKY_BLUE);
        panel.add(totalLabel, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        addSampleButton = createStyledButton("➕ Add Sample", SKY_BLUE, DARK_SKY_BLUE);
        addSampleButton.addActionListener(e -> {
            initializeProductData();
            showMessage("✅ Sample products reloaded");
        });
        addSampleButton.setToolTipText("Reload sample products");
        
        saveButton = createStyledButton("💾 Create Invoice", SKY_BLUE, SKY_BLUE.darker());
        saveButton.addActionListener(e -> onSaveInvoice());
        
        cancelButton = createStyledButton("✕ Cancel", new Color(200, 200, 200), HOVER_RED);
        cancelButton.addActionListener(e -> dispose());
        
        regenButton = createStyledButton("🔄 Regenerate ID", DARK_SKY_BLUE, SKY_BLUE);
        regenButton.addActionListener(e -> generateInvoiceId());
        
        panel.add(addSampleButton);
        panel.add(saveButton);
        panel.add(cancelButton);
        panel.add(regenButton);
        
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
                
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
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
        button.setPreferredSize(new Dimension(150, 50));
        button.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
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
    
    private JButton createLargeRoundedButton(String text, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.setColor(getBackground().darker());
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 42));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
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
    
    private JLabel createCalcLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        label.setForeground(LABEL_COLOR);
        return label;
    }
    
    private JLabel createCalcValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(40, 40, 40));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
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
                
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
    
    private JTextField createAutoDateField() {
        return new JTextField() {
            {
                setFont(INPUT_FONT);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
                setBackground(INPUT_BG);
                setForeground(new Color(40, 40, 40));
                setCaretColor(DARK_SKY_BLUE);
                setPreferredSize(new Dimension(300, 45));
                setEditable(true);
                
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        requestFocusInWindow();
                    }
                });
                
                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        selectAll();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
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
                
                g2.setColor(INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
    
    private JSpinner createSmallSpinner(double value, double min, double max, double step) {
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner spinner = new JSpinner(model);
        
        spinner.setFont(INPUT_FONT);
        spinner.setPreferredSize(new Dimension(80, 35));
        
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setFont(INPUT_FONT);
        editor.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
        editor.getTextField().setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        return spinner;
    }
    
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(INPUT_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
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
    
    private void generateInvoiceId() {
        // Generate invoice ID based on existing invoices in memory
        int nextId = 1000 + createdInvoices.size() + 1;
        String invId = String.format("INV-%04d", nextId);
        if (invoiceIdField != null) {
            invoiceIdField.setText(invId);
            invoiceIdField.setForeground(new Color(40, 40, 40));
        }
    }
    
    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        
        if (dateField != null) {
            dateField.setText(currentDate);
            dateField.setForeground(new Color(40, 40, 40));
        }
        
        Date dueDate = new Date(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000));
        String dueDateStr = sdf.format(dueDate);
        
        if (dueDateField != null) {
            dueDateField.setText(dueDateStr);
            dueDateField.setForeground(new Color(40, 40, 40));
        }
    }
    
    private void addNewItem() {
        Object[] newRow = {
            "Enter item name",
            "Enter description",
            "Qty",
            "0.00",
            "0.00"
        };
        
        tableModel.addRow(newRow);
        
        int lastRow = tableModel.getRowCount() - 1;
        itemsTable.setRowSelectionInterval(lastRow, lastRow);
        itemsTable.scrollRectToVisible(itemsTable.getCellRect(lastRow, 0, true));
        
        itemsTable.editCellAt(lastRow, 0);
        itemsTable.getEditorComponent().requestFocus();
        
        itemsTable.repaint();
    }
    
    private void calculateRowTotal(int row) {
        try {
            Object qtyObj = tableModel.getValueAt(row, 2);
            Object priceObj = tableModel.getValueAt(row, 3);
            
            if (qtyObj == null || priceObj == null) {
                tableModel.setValueAt("0.00", row, 4);
                return;
            }
            
            String qtyStr = qtyObj.toString().trim();
            String priceStr = priceObj.toString().trim();
            
            if (qtyStr.equals("Qty") || qtyStr.isEmpty() || 
                priceStr.equals("0.00") || priceStr.isEmpty()) {
                tableModel.setValueAt("0.00", row, 4);
                return;
            }
            
            int quantity = 0;
            double unitPrice = 0.0;
            
            try {
                quantity = Integer.parseInt(qtyStr);
            } catch (NumberFormatException e) {
                tableModel.setValueAt("0.00", row, 4);
                return;
            }
            
            try {
                priceStr = priceStr.replace("PKR", "").replace("$", "").replace(",", "").trim();
                unitPrice = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                tableModel.setValueAt("0.00", row, 4);
                return;
            }
            
            double itemTotal = quantity * unitPrice;
            tableModel.setValueAt(String.format("%.2f", itemTotal), row, 4);
            
        } catch (Exception e) {
            tableModel.setValueAt("0.00", row, 4);
        }
    }
    
    private void removeSelectedItems() {
        int[] selectedRows = itemsTable.getSelectedRows();
        
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this,
                "Please select one or more items to remove.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String message;
        if (selectedRows.length == 1) {
            message = "Are you sure you want to remove the selected item?";
        } else {
            message = "Are you sure you want to remove " + selectedRows.length + " selected items?";
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            message,
            "Confirm Removal",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            tableModel.removeRow(selectedRows[i]);
        }
        
        calculateTotals();
        itemsTable.clearSelection();
        
        if (selectedRows.length == 1) {
            JOptionPane.showMessageDialog(this,
                "1 item removed successfully.",
                "Item Removed",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                selectedRows.length + " items removed successfully.",
                "Items Removed",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearAllItems() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "The items table is already empty.",
                "No Items",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to clear all " + tableModel.getRowCount() + " items?\nThis action cannot be undone.",
            "Confirm Clear All Items",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0);
            calculateTotals();
            
            JOptionPane.showMessageDialog(this,
                "All items have been cleared successfully.",
                "Items Cleared",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void calculateTotals() {
        double subtotal = 0.0;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                Object totalObj = tableModel.getValueAt(i, 4);
                
                if (totalObj == null) {
                    continue;
                }
                
                String totalStr = totalObj.toString().trim();
                
                if (totalStr.equals("0.00") || totalStr.isEmpty()) {
                    continue;
                }
                
                totalStr = totalStr.replace("PKR", "").replace("$", "").replace(",", "").trim();
                double itemTotal = Double.parseDouble(totalStr);
                
                subtotal += itemTotal;
            } catch (Exception e) {
                continue;
            }
        }
        
        double taxPercent = ((Number) taxSpinner.getValue()).doubleValue();
        double discountPercent = ((Number) discountSpinner.getValue()).doubleValue();
        
        double taxAmount = subtotal * (taxPercent / 100.0);
        double discountAmount = subtotal * (discountPercent / 100.0);
        double total = subtotal + taxAmount - discountAmount;
        
        subtotalLabel.setText(String.format("PKR %.2f", subtotal));
        taxLabel.setText(String.format("PKR %.2f", taxAmount));
        discountLabel.setText(String.format("PKR %.2f", discountAmount));
        totalLabel.setText(String.format("PKR %.2f", total));
    }
    
    private void onSaveInvoice() {
        // Validate required fields
        if (customerNameField.getForeground().equals(Color.GRAY) || customerNameField.getText().trim().isEmpty()) {
            showError("Customer Name is required");
            customerNameField.requestFocus();
            return;
        }
        
        if (dateField.getText().trim().isEmpty()) {
            showError("Invoice Date is required");
            dateField.requestFocus();
            return;
        }
        
        if (dueDateField.getText().trim().isEmpty()) {
            showError("Due Date is required");
            dueDateField.requestFocus();
            return;
        }
        
        if (!isValidDate(dateField.getText().trim())) {
            showError("Invalid Invoice Date format. Please use YYYY-MM-DD");
            dateField.requestFocus();
            return;
        }
        
        if (!isValidDate(dueDateField.getText().trim())) {
            showError("Invalid Due Date format. Please use YYYY-MM-DD");
            dueDateField.requestFocus();
            return;
        }
        
        String email = customerEmailField.getText().trim();
        if (!email.isEmpty() && !email.equals("email@example.com") && !isValidEmail(email)) {
            showError("Invalid email format. Please enter a valid email address or leave it empty");
            customerEmailField.requestFocus();
            return;
        }
        
        // Validate items
        if (tableModel.getRowCount() == 0) {
            showError("Please add at least one item to the invoice");
            addItemButton.requestFocus();
            return;
        }
        
        // Validate all items with inventory checks
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String itemName = "";
            try {
                itemName = tableModel.getValueAt(i, 0).toString().trim();
            } catch (Exception e) {
                itemName = "";
            }
            
            if (itemName.isEmpty() || itemName.equals("Enter item name")) {
                showError("Item name cannot be empty for item #" + (i + 1));
                itemsTable.setRowSelectionInterval(i, i);
                itemsTable.editCellAt(i, 0);
                return;
            }
            
            // Check if item exists in inventory
            ProductData product = findProductByName(itemName);
            if (product == null) {
                showError("Item '" + itemName + "' not found in inventory for item #" + (i + 1));
                itemsTable.setRowSelectionInterval(i, i);
                itemsTable.editCellAt(i, 0);
                return;
            }
            
            int quantity = 0;
            try {
                Object qtyObj = tableModel.getValueAt(i, 2);
                if (qtyObj == null) {
                    showError("Quantity cannot be empty for item #" + (i + 1));
                    itemsTable.setRowSelectionInterval(i, i);
                    itemsTable.editCellAt(i, 2);
                    return;
                }
                
                String qtyStr = qtyObj.toString().trim();
                if (qtyStr.isEmpty() || qtyStr.equals("Qty")) {
                    showError("Quantity cannot be empty for item #" + (i + 1));
                    itemsTable.setRowSelectionInterval(i, i);
                    itemsTable.editCellAt(i, 2);
                    return;
                }
                
                quantity = Integer.parseInt(qtyStr);
            } catch (NumberFormatException e) {
                showError("Invalid quantity format for item #" + (i + 1) + ". Please enter a valid number.");
                itemsTable.setRowSelectionInterval(i, i);
                itemsTable.editCellAt(i, 2);
                return;
            } catch (Exception e) {
                showError("Invalid quantity for item #" + (i + 1));
                itemsTable.setRowSelectionInterval(i, i);
                itemsTable.editCellAt(i, 2);
                return;
            }
            
            // INVENTORY VALIDATION: Check stock availability
            if (product.getQuantity() < quantity) {
                showError("Insufficient stock for '" + itemName + "'. Available: " + 
                         product.getQuantity() + ", Requested: " + quantity + " (item #" + (i + 1) + ")");
                itemsTable.setRowSelectionInterval(i, i);
                itemsTable.editCellAt(i, 2);
                return;
            }
            
            if (quantity <= 0) {
                showError("Quantity must be greater than 0 for item #" + (i + 1));
                itemsTable.setRowSelectionInterval(i, i);
                itemsTable.editCellAt(i, 2);
                return;
            }
            
            double unitPrice = 0.0;
            try {
                Object priceObj = tableModel.getValueAt(i, 3);
                if (priceObj == null) {
                    showError("Unit price cannot be empty for item #" + (i + 1));
                    itemsTable.setRowSelectionInterval(i, i);
                    itemsTable.editCellAt(i, 3);
                    return;
                }
                
                String priceStr = priceObj.toString().trim();
                if (priceStr.isEmpty() || priceStr.equals("0.00")) {
                    showError("Unit price cannot be empty for item #" + (i + 1));
                    itemsTable.setRowSelectionInterval(i, i);
                    itemsTable.editCellAt(i, 3);
                    return;
                }
                
                priceStr = priceStr.replace("PKR", "").replace("$", "").replace(",", "").trim();
                unitPrice = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                showError("Invalid unit price format for item #" + (i + 1) + ". Please enter a valid number.");
                itemsTable.setRowSelectionInterval(i, i);
                itemsTable.editCellAt(i, 3);
                return;
            } catch (Exception e) {
                showError("Invalid unit price for item #" + (i + 1));
                itemsTable.setRowSelectionInterval(i, i);
                itemsTable.editCellAt(i, 3);
                return;
            }
            
            if (unitPrice <= 0) {
                showError("Unit price must be greater than 0 for item #" + (i + 1));
                itemsTable.setRowSelectionInterval(i, i);
                itemsTable.editCellAt(i, 3);
                return;
            }
        }
        
        // All validations passed - save invoice to memory
        saveInvoiceToMemory();
    }
    
    private void saveInvoiceToMemory() {
        String invoiceId = invoiceIdField.getText();
        String customerName = customerNameField.getText().trim();
        String customerEmail = customerEmailField.getText().trim().equals("email@example.com") ? null : customerEmailField.getText().trim();
        String customerPhone = customerPhoneField.getText().trim().equals("(123) 456-7890") ? null : customerPhoneField.getText().trim();
        String invoiceDate = dateField.getText().trim();
        String dueDate = dueDateField.getText().trim();
        String notes = notesArea.getForeground().equals(Color.GRAY) ? null : notesArea.getText().trim();
        
        calculateTotals();
        double subtotal = Double.parseDouble(subtotalLabel.getText().replace("PKR", "").replace(",", "").trim());
        double tax = Double.parseDouble(taxLabel.getText().replace("PKR", "").replace(",", "").trim());
        double discount = Double.parseDouble(discountLabel.getText().replace("PKR", "").replace(",", "").trim());
        double total = Double.parseDouble(totalLabel.getText().replace("PKR", "").replace(",", "").trim());
        
        // Update product quantities in memory
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String productName = tableModel.getValueAt(i, 0).toString();
            int quantity = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
            
            // Find product and update its quantity
            ProductData product = findProductByName(productName);
            if (product != null) {
                // In a real implementation, we would update the product quantity
                // For now, we'll just note the deduction
                System.out.println("Deducting " + quantity + " units from " + productName + 
                                 " (Remaining: " + (product.getQuantity() - quantity) + ")");
            }
        }
        
        // Create items array
        Object[][] items = new Object[tableModel.getRowCount()][5];
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            items[i][0] = tableModel.getValueAt(i, 0);
            items[i][1] = tableModel.getValueAt(i, 1);
            items[i][2] = tableModel.getValueAt(i, 2);
            items[i][3] = tableModel.getValueAt(i, 3);
            items[i][4] = tableModel.getValueAt(i, 4);
        }
        
        // Create invoice data
        invoiceData = new InvoiceData(
            invoiceId,
            customerName,
            customerEmail,
            customerPhone,
            invoiceDate,
            dueDate,
            subtotal,
            tax,
            discount,
            total,
            notes,
            items
        );
        
        // Add to created invoices list
        createdInvoices.add(invoiceData);
        
        saveSuccessful = true;
        
        // Show success message
        StringBuilder itemsSummary = new StringBuilder();
        int itemsToShow = Math.min(items.length, 3);
        for (int i = 0; i < itemsToShow; i++) {
            try {
                String itemName = items[i][0].toString();
                int quantity = 0;
                double unitPrice = 0.0;
                double itemTotal = 0.0;
                
                if (items[i][2] instanceof Integer) {
                    quantity = (Integer) items[i][2];
                } else {
                    quantity = Integer.parseInt(items[i][2].toString());
                }
                
                if (items[i][3] instanceof Double) {
                    unitPrice = (Double) items[i][3];
                } else {
                    unitPrice = Double.parseDouble(items[i][3].toString().replace("PKR", "").replace("$", "").replace(",", "").trim());
                }
                
                if (items[i][4] instanceof Double) {
                    itemTotal = (Double) items[i][4];
                } else {
                    itemTotal = Double.parseDouble(items[i][4].toString().replace("PKR", "").replace("$", "").replace(",", "").trim());
                }
                
                itemsSummary.append(String.format("\n  • %s: %d x PKR%.2f = PKR%.2f", 
                    itemName, quantity, unitPrice, itemTotal));
            } catch (Exception e) {
                itemsSummary.append(String.format("\n  • %s: Error calculating", items[i][0]));
            }
        }
        if (items.length > 3) {
            itemsSummary.append("\n  • ... and ").append(items.length - 3).append(" more items");
        }
        
        JOptionPane.showMessageDialog(this, 
            "✅ Invoice created successfully in memory!\n\n" +
            "Invoice ID: " + invoiceData.getInvoiceId() + "\n" +
            "Customer: " + invoiceData.getCustomerName() + "\n" +
            "Date: " + invoiceData.getInvoiceDate() + "\n" +
            "Due Date: " + invoiceData.getDueDate() + "\n" +
            "Items: " + items.length + itemsSummary.toString() + "\n" +
            "Subtotal: PKR" + String.format("%.2f", invoiceData.getSubtotal()) + "\n" +
            "Tax: PKR" + String.format("%.2f", invoiceData.getTax()) + "\n" +
            "Discount: PKR" + String.format("%.2f", invoiceData.getDiscount()) + "\n" +
            "Total: PKR" + String.format("%.2f", invoiceData.getTotal()) + "\n\n" +
            "Note: This invoice is stored in memory for this session.",
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    }
    
    private ProductData findProductByName(String name) {
        for (ProductData product : allProducts) {
            if (product.getName().equalsIgnoreCase(name.trim())) {
                return product;
            }
        }
        return null;
    }
    
    private boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            "❌ " + message + "\n\nPlease correct the errors and try again.", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /* ================= PUBLIC ACCESSORS ================= */
    public boolean isSaveSuccessful() { return saveSuccessful; }
    public InvoiceData getInvoiceData() { return invoiceData; }
    
    public static List<InvoiceData> getCreatedInvoices() {
        return new ArrayList<>(createdInvoices);
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
            
            g2.setColor(new Color(180, 200, 230));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    /* ================= MAIN METHOD FOR TESTING ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Test Create Invoice Dialog");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JPanel contentPanel = new JPanel();
            frame.setContentPane(contentPanel);
            frame.setVisible(true);

            CreateInvoiceDialog dialog = new CreateInvoiceDialog(frame, contentPanel);
            dialog.setVisible(true);
            
            if (dialog.isSaveSuccessful()) {
                System.out.println("Invoice saved successfully in memory!");
                System.out.println("Invoice ID: " + dialog.getInvoiceData().getInvoiceId());
                System.out.println("Customer: " + dialog.getInvoiceData().getCustomerName());
                System.out.println("Total: PKR" + dialog.getInvoiceData().getTotal());
                System.out.println("Total invoices created: " + getCreatedInvoices().size());
            }
        });
    }
}