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
import java.util.List;

/**
 * DeleteProductDialog - Real factory product deletion module
 * Uses in-memory data instead of database
 */
public class DeleteProductDialog extends JDialog {
    private static final Color SKY_BLUE = new Color(135, 206, 250);
    private static final Color DARK_SKY_BLUE = new Color(30, 144, 255);
    private static final Color TITLE_BLUE = new Color(30, 144, 255);
    private static final Color LIGHT_BLUE_BG = new Color(240, 248, 255);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(180, 200, 220);
    private static final Color INPUT_BORDER = new Color(200, 220, 240);
    private static final Color DELETE_RED = new Color(220, 53, 69);
    private static final Color HOVER_RED = new Color(200, 35, 51);
    private static final Color CANCEL_COLOR = new Color(150, 150, 150);
    private static final Color CANCEL_HOVER_RED = new Color(220, 80, 80);
    private static final Color TABLE_HEADER_BG = new Color(245, 247, 250);
    private static final Color TABLE_SELECTION = new Color(225, 240, 255);
    private static final Color SCROLLBAR_COLOR = new Color(100, 100, 100);
    private static final Color TEXT_BLACK = new Color(40, 40, 40);
    private static final Color PLACEHOLDER_GRAY = new Color(150, 150, 150);
    
    // Form fields
    private JTextField deleteIdField;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Product ID", "Product Name", "Category", "Price (PKR)", "Quantity", "Status"}, 0);
    private final JTable table = new JTable(tableModel);
    private JLabel statusLabel;
    
    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    
    // In-memory product storage
    private final List<Product> products = new ArrayList<>();
    
    // Product class to store data
    private static class Product {
        String id;
        String name;
        String category;
        double price;
        int quantity;
        boolean usedInSales;
        
        Product(String id, String name, String category, double price, int quantity, boolean usedInSales) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.quantity = quantity;
            this.usedInSales = usedInSales;
        }
    }
    
    // Constructor that matches your ProductMenuDialog call
    public DeleteProductDialog(JFrame ownerFrame, JPanel contentPanel) {
        super(ownerFrame, true);
        initializeUI(ownerFrame, contentPanel);
        
        // Initialize with sample data
        initializeSampleData();
        
        // Load product data
        loadProductData();
    }
    
    /* ================= DATA METHODS ================= */
    
    /**
     * Initialize with sample data
     */
    private void initializeSampleData() {
        products.clear();
        
        // Add sample products
        products.add(new Product("P001", "Premium Ceramic Tile", "Floor Tiles", 2500.00, 50, false));
        products.add(new Product("P002", "Porcelain Wall Tile", "Wall Tiles", 1800.00, 0, true));
        products.add(new Product("P003", "Marble Pattern Tile", "Decorative Tiles", 3500.00, 120, false));
        products.add(new Product("P004", "Kitchen Backsplash Tile", "Kitchen Tiles", 2200.00, 30, false));
        products.add(new Product("P005", "Bathroom Mosaic Tile", "Bathroom Tiles", 2800.00, 0, false));
        products.add(new Product("P006", "Outdoor Patio Tile", "Outdoor Tiles", 1900.00, 80, false));
        products.add(new Product("P007", "Wooden Effect Tile", "Floor Tiles", 3200.00, 15, false));
        products.add(new Product("P008", "Glossy Wall Tile", "Wall Tiles", 1650.00, 200, true));
        products.add(new Product("P009", "Subway Tile White", "Kitchen Tiles", 1350.00, 0, true));
        products.add(new Product("P010", "Hexagon Pattern Tile", "Decorative Tiles", 2950.00, 45, false));
    }
    
    /**
     * Load products from memory
     */
    private void loadProductData() {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        int rowCount = 0;
        DecimalFormat df = new DecimalFormat("#,##0.00");
        
        for (Product product : products) {
            String stockStatus = getStockStatus(product.quantity);
            
            tableModel.addRow(new Object[]{
                product.id,
                product.name,
                product.category,
                "PKR " + df.format(product.price),
                product.quantity,
                stockStatus
            });
            rowCount++;
        }
        
        if (rowCount == 0) {
            showStatusMessage("📝 No products found", false);
        } else {
            showStatusMessage("📋 Loaded " + rowCount + " products from memory", false);
        }
    }
    
    /**
     * Get stock status based on quantity
     */
    private String getStockStatus(int quantity) {
        if (quantity == 0) {
            return "Out of Stock";
        } else if (quantity < 20) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }
    
    /**
     * Check if product exists
     */
    private boolean checkProductExists(String productId) {
        return products.stream().anyMatch(p -> p.id.equals(productId));
    }
    
    /**
     * Check if product is used in sales
     */
    private boolean isProductUsedInSales(String productId) {
        return products.stream()
                .filter(p -> p.id.equals(productId))
                .findFirst()
                .map(p -> p.usedInSales)
                .orElse(false);
    }
    
    /**
     * Get product details
     */
    private Product getProductDetails(String productId) {
        return products.stream()
                .filter(p -> p.id.equals(productId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Delete product from memory
     */
    private boolean deleteProductFromMemory(String productId) {
        boolean removed = products.removeIf(p -> p.id.equals(productId));
        return removed;
    }
    
    /* ================= UI METHODS ================= */
    
    private void initializeUI(JFrame ownerFrame, JPanel contentPanel) {
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Main card panel
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 25, 20, 25));
        card.setPreferredSize(new Dimension(900, 700));
        
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
        setSize(900, 700);
        
        // Center dialog
        centerDialog(ownerFrame, contentPanel);
        
        // ESC to close
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Set focus to delete field
        SwingUtilities.invokeLater(() -> deleteIdField.requestFocus());
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel title = new JLabel("Delete Product");
        title.setFont(TITLE_FONT);
        title.setForeground(TITLE_BLUE);
        topBar.add(title, BorderLayout.WEST);
        
        topBar.add(createCloseButton(), BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);
        
        /* ================= TABLE PANEL ================= */
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 2, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel tableTitle = new JLabel("Products List - Click any row to select");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(new Color(80, 80, 100));
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        setupTable();
        
        JScrollPane tableScroll = new JScrollPane(table);
        styleTableScrollPane(tableScroll);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        
        statusLabel = new JLabel("Select a product from the table above");
        statusLabel.setFont(STATUS_FONT);
        statusLabel.setForeground(new Color(100, 100, 120));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        tablePanel.add(statusLabel, BorderLayout.SOUTH);
        
        mainContent.add(tablePanel, BorderLayout.CENTER);
        
        /* ================= DELETE INPUT PANEL ================= */
        mainContent.add(createDeletePanel(), BorderLayout.SOUTH);
        
        return mainContent;
    }
    
    private void setupTable() {
        table.setDefaultEditor(Object.class, null);
        table.setFont(TABLE_FONT);
        table.setRowHeight(38);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 225, 230));
        table.setSelectionBackground(TABLE_SELECTION);
        table.setSelectionForeground(TEXT_BLACK);
        table.setForeground(TEXT_BLACK);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
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
                } else {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(240, 242, 245), 1),
                        BorderFactory.createEmptyBorder(3, 5, 3, 5)
                    ));
                }
                
                setHorizontalAlignment(JLabel.CENTER);
                setForeground(TEXT_BLACK);
                
                // Color code based on stock status
                if (!isSelected) {
                    if (row % 2 == 0) {
                        setBackground(Color.WHITE);
                    } else {
                        setBackground(new Color(248, 250, 252));
                    }
                    
                    // Highlight low stock products
                    if (column == 5 && value != null) {
                        String status = value.toString();
                        if (status.equals("Out of Stock")) {
                            setForeground(new Color(220, 53, 69));
                        } else if (status.equals("Low Stock")) {
                            setForeground(new Color(255, 193, 7));
                        } else if (status.equals("In Stock")) {
                            setForeground(new Color(40, 167, 69));
                        }
                    }
                }
                
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
        columnModel.getColumn(0).setPreferredWidth(130);  // Product ID
        columnModel.getColumn(1).setPreferredWidth(240);  // Product Name
        columnModel.getColumn(2).setPreferredWidth(140);  // Category
        columnModel.getColumn(3).setPreferredWidth(150);  // Price
        columnModel.getColumn(4).setPreferredWidth(110);  // Quantity
        columnModel.getColumn(5).setPreferredWidth(120);  // Status
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    Object idValue = table.getValueAt(row, 0);
                    if (idValue != null) {
                        deleteIdField.setText(idValue.toString());
                        deleteIdField.setForeground(TEXT_BLACK);
                        String productName = table.getValueAt(row, 1).toString();
                        String category = table.getValueAt(row, 2).toString();
                        String quantity = table.getValueAt(row, 4).toString();
                        String status = table.getValueAt(row, 5).toString();
                        
                        statusLabel.setText("Selected: " + productName + " (ID: " + idValue + 
                                          ", Category: " + category + ", Stock: " + quantity + " - " + status + ")");
                        statusLabel.setForeground(new Color(0, 100, 0));
                    }
                }
            }
        });
    }
    
    private JPanel createDeletePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        JLabel label = new JLabel("Selected Product ID:");
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_BLACK);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        deleteIdField = createStyledTextField();
        panel.add(deleteIdField, gbc);
        
        return panel;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField("Click on a table row to select product") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(INPUT_BORDER);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        field.setFont(INPUT_FONT);
        field.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        field.setBackground(Color.WHITE);
        field.setForeground(PLACEHOLDER_GRAY);
        field.setPreferredSize(new Dimension(300, 48));
        field.setEditable(false);
        field.setFocusable(true);
        field.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        
        return field;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 0, 0, 0));
        
        JButton deleteButton = createStyledButton("🗑️ Delete Product", SKY_BLUE, DARK_SKY_BLUE);
        deleteButton.addActionListener(e -> deleteProduct());
        
        JButton cancelButton = createStyledButton("✕ Cancel", CANCEL_COLOR, CANCEL_HOVER_RED);
        cancelButton.addActionListener(e -> dispose());
        
        JButton clearButton = createStyledButton("🗑️ Clear Input", SKY_BLUE, DARK_SKY_BLUE);
        clearButton.addActionListener(e -> clearInput());
        clearButton.setToolTipText("Clear the selected product ID");
        
        JButton refreshButton = createStyledButton("🔄 Refresh", SKY_BLUE, DARK_SKY_BLUE);
        refreshButton.addActionListener(e -> {
            loadProductData();
            showStatusMessage("🔄 Product list refreshed", false);
        });
        refreshButton.setToolTipText("Refresh product list");
        
        JButton addSampleButton = createStyledButton("➕ Add Sample", SKY_BLUE, DARK_SKY_BLUE);
        addSampleButton.addActionListener(e -> {
            initializeSampleData();
            loadProductData();
            showStatusMessage("➕ Sample products added", false);
        });
        addSampleButton.setToolTipText("Add sample products");
        
        panel.add(deleteButton);
        panel.add(cancelButton);
        panel.add(clearButton);
        panel.add(refreshButton);
        panel.add(addSampleButton);
        
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
        button.setPreferredSize(new Dimension(180, 52));
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
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(hoverColor.darker());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverColor);
            }
        });
        
        return button;
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
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRect(thumbBounds.x + 1, thumbBounds.y + 2, 
                            thumbBounds.width - 2, thumbBounds.height - 4);
                g2.dispose();
            }
            
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, 
                            trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });
        
        JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
        horizontal.setPreferredSize(new Dimension(0, 10));
        horizontal.setUI(vertical.getUI());
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
    
    /* ================= BUSINESS LOGIC ================= */
    
    private void clearInput() {
        deleteIdField.setText("Click on a table row to select product");
        deleteIdField.setForeground(PLACEHOLDER_GRAY);
        table.clearSelection();
        statusLabel.setText("Select a product from the table above");
        statusLabel.setForeground(new Color(100, 100, 120));
        deleteIdField.requestFocus();
    }
    
    private void deleteProduct() {
        String productId = deleteIdField.getText().trim();
        
        if (productId.equals("Click on a table row to select product") || productId.isEmpty()) {
            showStatusMessage("❌ Please select a product first. Click on any row in the table.", true);
            JOptionPane.showMessageDialog(this,
                "❌ Please select a product first\n\n" +
                "Click on any row in the table to select a product for deletion.",
                "No Product Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if product exists
        Product product = getProductDetails(productId);
        if (product == null) {
            showStatusMessage("❌ Product not found: " + productId, true);
            JOptionPane.showMessageDialog(this,
                "❌ Product not found\n\n" +
                "No product found with ID: " + productId + "\n" +
                "Please check the ID and try again.",
                "Product Not Found",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if product has stock
        if (product.quantity > 0) {
            showStatusMessage("❌ Cannot delete: Product has stock in warehouse", true);
            JOptionPane.showMessageDialog(this,
                "❌ Cannot Delete Product\n\n" +
                "Product still has stock in warehouse:\n" +
                "• Product: " + product.name + "\n" +
                "• Stock Quantity: " + product.quantity + " units\n\n" +
                "Stock must be zero before deletion.\n" +
                "Consider selling or transferring stock first.",
                "Stock Not Zero",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if product is used in sales
        if (product.usedInSales) {
            showStatusMessage("❌ Cannot delete: Product is linked to sales records", true);
            JOptionPane.showMessageDialog(this,
                "❌ Cannot Delete Product\n\n" +
                "Product is linked to sales records:\n" +
                "• Product: " + product.name + "\n" +
                "• Category: " + product.category + "\n\n" +
                "Deleting would affect sales history.\n" +
                "Consider marking as discontinued instead.",
                "Used in Sales",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><div style='width:350px; padding:10px;'>" +
            "<b style='color:#dc3545; font-size:15px;'>⚠️ CONFIRM PRODUCT DELETION</b><br><br>" +
            "<table style='width:100%; border-collapse:collapse;'>" +
            "<tr><td style='padding:4px 0;'><b>Product ID:</b></td><td>" + product.id + "</td></tr>" +
            "<tr><td style='padding:4px 0;'><b>Name:</b></td><td>" + product.name + "</td></tr>" +
            "<tr><td style='padding:4px 0;'><b>Category:</b></td><td>" + product.category + "</td></tr>" +
            "<tr><td style='padding:4px 0;'><b>Price:</b></td><td>PKR " + String.format("%,.2f", product.price) + "</td></tr>" +
            "<tr><td style='padding:4px 0;'><b>Stock:</b></td><td>" + product.quantity + " units</td></tr>" +
            "</table><br>" +
            "<b style='color:#666;'>⚠️ This action cannot be undone!</b><br>" +
            "<small>The product will be permanently removed from memory.</small></div></html>",
            "Confirm Product Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Delete from memory
            boolean success = deleteProductFromMemory(productId);
            
            if (success) {
                // Refresh table
                loadProductData();
                showStatusMessage("✅ Successfully deleted: " + product.name + " (ID: " + product.id + ")", false);
                
                // Clear input
                clearInput();
                
                JOptionPane.showMessageDialog(this,
                    "✅ Product Deleted Successfully\n\n" +
                    product.name + " has been permanently removed from memory.\n" +
                    "This change is reflected in the current session only.",
                    "Deletion Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                showStatusMessage("❌ Failed to delete product", true);
            }
        } else {
            showStatusMessage("⏸️ Deletion cancelled for: " + product.name, false);
        }
    }
    
    private void showStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        if (isError) {
            statusLabel.setForeground(new Color(200, 0, 0));
        } else {
            statusLabel.setForeground(new Color(0, 100, 0));
        }
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
            
            JFrame frame = new JFrame("Tile Factory - Delete Product");
            frame.setSize(1100, 750);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(new Color(240, 248, 255));
            
            JButton testBtn = new JButton("Open Product Deletion Dialog");
            testBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            testBtn.setBackground(new Color(30, 144, 255));
            testBtn.setForeground(Color.WHITE);
            testBtn.setFocusPainted(false);
            testBtn.setPreferredSize(new Dimension(300, 50));
            testBtn.addActionListener(e -> {
                DeleteProductDialog dlg = new DeleteProductDialog(frame, null);
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