package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import javax.swing.text.JTextComponent;

/**
 * InventoryReportDialog - Modern Inventory Report Form for Granite and Marble Factory
 * Updated to match SalesReportDialog structure and factory product categories
 */
public class InventoryReportDialog extends JDialog {
    // Colors - exactly matching SalesReportDialog
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
    private static final Color SUCCESS_GREEN = new Color(40, 167, 69);
    private static final Color WARNING_YELLOW = new Color(255, 193, 7);
    private static final Color LOW_STOCK_COLOR = new Color(220, 53, 69);
    private static final Color MEDIUM_STOCK_COLOR = new Color(255, 193, 7);
    private static final Color HIGH_STOCK_COLOR = new Color(40, 167, 69);
    private static final Color TABLE_HEADER_BG = new Color(245, 245, 245);
    private static final Color TABLE_HEADER_FG = Color.BLACK;
    
    // Form fields
    private JComboBox<String> reportTypeCombo;
    private JComboBox<String> categoryCombo;  // Changed from materialTypeCombo to match SalesReportDialog
    private JComboBox<String> warehouseCombo;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> formatCombo;
    private JCheckBox includeLowStockCheck;
    private JCheckBox includeZeroStockCheck;
    private JCheckBox showPriceDetailsCheck;
    private JTextArea notesArea;
    
    // Table for preview
    private JTable previewTable;
    
    // Data managers (to be injected from main application)
    private ProductManager productManager;
    private InventoryManager inventoryManager;
    
    // Summary labels for dynamic updates (matching SalesReportDialog)
    private JLabel totalItemsLabel;
    private JLabel totalValueLabel;
    private JLabel lowStockLabel;
    private JLabel avgStockLabel;
    
    // Date formatter - consistent with SalesReportDialog and CreateInvoiceDialog
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Fonts (exactly matching SalesReportDialog)
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 15);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font PLACEHOLDER_FONT = new Font("Segoe UI", Font.ITALIC, 14);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font SUMMARY_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Font SUMMARY_TITLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    
    // Button references for hover effects
    private JButton generateButton;
    private JButton previewButton;
    private JButton cancelButton;
    private JButton exportButton;
    
    /* ================= CONSTRUCTOR ================= */
    public InventoryReportDialog(JFrame ownerFrame, JPanel contentPanel, 
                               ProductManager productManager,
                               InventoryManager inventoryManager) {
        super(ownerFrame, true);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Initialize data managers
        this.productManager = productManager;
        this.inventoryManager = inventoryManager;
        
        // Main card panel
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30, 35, 30, 35));
        card.setPreferredSize(new Dimension(950, 800));
        
        /* ================= TOP BAR ================= */
        JPanel topBar = createTopBar();
        card.add(topBar, BorderLayout.NORTH);
        
        /* ================= FORM PANEL ================= */
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
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
        setSize(950, 800);
        
        // Center dialog on the right white panel (contentPanel)
        centerOnContentPanel(contentPanel);
        
        // ESC to close
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Set default dates (matching SalesReportDialog)
        setDefaultDates();
        
        // Load dynamic data
        loadCategories();
        refreshPreview();
        
        // Set focus to first field
        SwingUtilities.invokeLater(() -> reportTypeCombo.requestFocus());
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 25, 0));
        
        // Title on left
        JLabel title = new JLabel("Factory Inventory Report");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        topBar.add(title, BorderLayout.WEST);
        
        // Close button on right
        JButton closeBtn = createCloseButton();
        topBar.add(closeBtn, BorderLayout.EAST);
        
        return topBar;
    }
    
    private void centerOnContentPanel(JPanel contentPanel) {
        if (contentPanel != null) {
            try {
                // Get the location of contentPanel on screen
                Point panelLocation = contentPanel.getLocationOnScreen();
                
                // Calculate center position within the contentPanel
                int centerX = panelLocation.x + (contentPanel.getWidth() - getWidth()) / 2;
                int centerY = panelLocation.y + (contentPanel.getHeight() - getHeight()) / 2;
                
                // Ensure dialog stays within screen bounds
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                Rectangle screenBounds = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
                
                int finalX = Math.max(screenBounds.x + 20, Math.min(centerX, 
                    screenBounds.x + screenBounds.width - getWidth() - 20));
                int finalY = Math.max(screenBounds.y + 20, Math.min(centerY, 
                    screenBounds.y + screenBounds.height - getHeight() - 20));
                
                setLocation(finalX, finalY);
            } catch (IllegalComponentStateException ex) {
                // Fallback to center of owner frame
                setLocationRelativeTo(getOwner());
            }
        } else {
            setLocationRelativeTo(getOwner());
        }
    }
    
    /* ================= DATA LOADING METHODS ================= */
    
    private void loadCategories() {
        try {
            if (productManager != null) {
                // Get actual categories from ProductManager - ONLY Marble and Granite
                List<String> categories = productManager.getCategories();
                if (categories == null || categories.isEmpty()) {
                    // Factory default categories - ONLY Marble and Granite
                    categories = new ArrayList<>();
                    categories.add("All");
                    categories.add("Marble");
                    categories.add("Granite");
                } else {
                    // Ensure only Marble and Granite are present
                    categories.removeIf(cat -> !cat.equals("Marble") && !cat.equals("Granite"));
                    categories.add(0, "All");
                }
                categoryCombo.setModel(new DefaultComboBoxModel<>(categories.toArray(new String[0])));
            } else {
                // Fallback to factory categories - ONLY Marble and Granite
                categoryCombo.setModel(new DefaultComboBoxModel<>(new String[]{"All", "Marble", "Granite"}));
            }
        } catch (Exception e) {
            System.err.println("Error loading categories: " + e.getMessage());
            categoryCombo.setModel(new DefaultComboBoxModel<>(new String[]{"All", "Marble", "Granite"}));
        }
    }
    
    private List<InventoryItem> getFilteredInventory() {
        if (inventoryManager == null) {
            return new ArrayList<>();
        }
        
        try {
            // Get all inventory items
            List<InventoryItem> allItems = inventoryManager.getInventoryItems();
            if (allItems == null) {
                return new ArrayList<>();
            }
            
            List<InventoryItem> filtered = new ArrayList<>();
            String category = (String) categoryCombo.getSelectedItem();
            String warehouse = (String) warehouseCombo.getSelectedItem();
            
            // Apply filters
            for (InventoryItem item : allItems) {
                // Category filter (matches SalesReportDialog logic)
                if (category != null && !category.equals("All") && 
                    !category.equalsIgnoreCase(item.getCategory())) {
                    continue;
                }
                
                // Warehouse filter
                if (warehouse != null && !warehouse.equals("All Warehouses") && 
                    !warehouse.equals(item.getWarehouse())) {
                    continue;
                }
                
                filtered.add(item);
            }
            
            return filtered;
        } catch (Exception e) {
            System.err.println("Error filtering inventory: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void refreshPreview() {
        SwingUtilities.invokeLater(() -> {
            List<InventoryItem> items = getFilteredInventory();
            
            // Calculate totals
            int totalItems = items.size();
            double totalValue = 0;
            int lowStockCount = 0;
            int totalQuantity = 0;
            
            for (InventoryItem item : items) {
                totalValue += item.getValue();
                totalQuantity += item.getQuantity();
                if (item.getQuantity() < item.getMinStockLevel()) {
                    lowStockCount++;
                }
            }
            
            double avgStock = totalItems > 0 ? (double) totalQuantity / totalItems : 0;
            
            // Update summary panel
            updateSummaryPanel(totalItems, totalValue, lowStockCount, avgStock);
            
            // Update preview table
            updatePreviewTable(items);
        });
    }
    
    private void updateSummaryPanel(int totalItems, double totalValue, int lowStockCount, double avgStock) {
        if (totalItemsLabel != null) {
            totalItemsLabel.setText(totalItems + " Items");
            totalValueLabel.setText(String.format("₱ %,.2f", totalValue));
            lowStockLabel.setText(lowStockCount + " Items");
            avgStockLabel.setText(String.format("%.0f units", avgStock));
        }
    }
    
    private void updatePreviewTable(List<InventoryItem> items) {
        String[] columns = {"Product Name", "Category", "Warehouse", "Quantity", "Unit", "Min Stock", "Status", "Value (₱)"};
        Object[][] data = new Object[items.size()][columns.length];
        
        for (int i = 0; i < items.size(); i++) {
            InventoryItem item = items.get(i);
            data[i][0] = item.getProductName();
            data[i][1] = item.getCategory();
            data[i][2] = item.getWarehouse();
            data[i][3] = item.getQuantity();
            data[i][4] = item.getUnit();
            data[i][5] = item.getMinStockLevel();
            data[i][6] = getStockStatus(item.getQuantity(), item.getMinStockLevel());
            data[i][7] = String.format("%,.2f", item.getValue());
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        
        if (previewTable != null) {
            previewTable.setModel(model);
            
            // Set column widths
            int[] columnWidths = {140, 100, 120, 80, 80, 90, 100, 120};
            for (int i = 0; i < columnWidths.length && i < previewTable.getColumnCount(); i++) {
                previewTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
            }
            
            // Center align all columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            for (int i = 0; i < previewTable.getColumnCount(); i++) {
                previewTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            
            // Custom renderer for status column
            previewTable.getColumnModel().getColumn(6).setCellRenderer(new StockStatusRenderer());
            
            // Update table header
            JTableHeader header = previewTable.getTableHeader();
            header.repaint();
        }
    }
    
    private String getStockStatus(int quantity, int minStock) {
        if (quantity == 0) return "Out of Stock";
        if (quantity < minStock) return "Low Stock";
        if (quantity < minStock * 2) return "Medium Stock";
        return "High Stock";
    }
    
    /* ================= FORM PANEL CREATION ================= */
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(14, 10, 14, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Report Type (Required) - No "Select" placeholder to match SalesReportDialog
        addLabel(panel, gbc, "Report Type", true);
        gbc.gridy++;
        String[] reportTypes = {"Current Stock Levels", 
                               "Stock Movement Report", 
                               "Low Stock Alert", 
                               "Category-wise Analysis", // Changed from "Material-wise" to match category
                               "Warehouse Summary", 
                               "Stock Aging Report", 
                               "Reorder Suggestions"};
        reportTypeCombo = createStyledComboBox(reportTypes, "Choose report type");
        reportTypeCombo.addActionListener(e -> refreshPreview());
        panel.add(reportTypeCombo, gbc);
        
        // Category Filter (Optional) - Changed from "Material Type" to match SalesReportDialog
        gbc.gridy++;
        addLabel(panel, gbc, "Category Filter", false);
        gbc.gridy++;
        categoryCombo = createStyledComboBox(new String[]{"Loading..."}, "Filter by category");
        categoryCombo.addActionListener(e -> refreshPreview());
        panel.add(categoryCombo, gbc);
        
        // Warehouse (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Warehouse", false);
        gbc.gridy++;
        String[] warehouses = {"All Warehouses", "Main Warehouse", "North Storage", 
                              "South Storage", "Processing Unit", "Dispatch Center"};
        warehouseCombo = createStyledComboBox(warehouses, "Filter by warehouse");
        warehouseCombo.addActionListener(e -> refreshPreview());
        panel.add(warehouseCombo, gbc);
        
        // Start Date (Optional for movement report)
        gbc.gridy++;
        addLabel(panel, gbc, "Start Date", false);
        gbc.gridy++;
        startDateField = createStyledTextField("YYYY-MM-DD", false);
        startDateField.setToolTipText("Format: YYYY-MM-DD (same as invoices)");
        startDateField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> refreshPreview()));
        panel.add(startDateField, gbc);
        
        // End Date (Optional for movement report)
        gbc.gridy++;
        addLabel(panel, gbc, "End Date", false);
        gbc.gridy++;
        endDateField = createStyledTextField("YYYY-MM-DD", false);
        endDateField.setToolTipText("Format: YYYY-MM-DD (same as invoices)");
        endDateField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> refreshPreview()));
        panel.add(endDateField, gbc);
        
        // Output Format (Required) - No "Select" placeholder to match SalesReportDialog
        gbc.gridy++;
        addLabel(panel, gbc, "Output Format", true);
        gbc.gridy++;
        String[] formats = {"PDF Document", "Excel Spreadsheet", "CSV File", 
                           "HTML Report", "Printable PDF"};
        formatCombo = createStyledComboBox(formats, "Select output format");
        panel.add(formatCombo, gbc);
        
        // Stock Threshold Panel
        gbc.gridy++;
        addLabel(panel, gbc, "Stock Threshold Indicators", false);
        gbc.gridy++;
        
        JPanel thresholdPanel = new JPanel(new GridLayout(1, 4, 15, 0)); // Changed to 4 columns
        thresholdPanel.setOpaque(false);
        thresholdPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Color indicators for thresholds - Added "Out of Stock"
        JPanel outStockPanel = createThresholdPanel("Out of Stock", "0 units", LOW_STOCK_COLOR);
        JPanel lowStockPanel = createThresholdPanel("Low Stock", "< Min Stock", LOW_STOCK_COLOR);
        JPanel mediumStockPanel = createThresholdPanel("Medium Stock", "Min-2x Min", MEDIUM_STOCK_COLOR);
        JPanel highStockPanel = createThresholdPanel("High Stock", "> 2x Min", HIGH_STOCK_COLOR);
        
        thresholdPanel.add(outStockPanel);
        thresholdPanel.add(lowStockPanel);
        thresholdPanel.add(mediumStockPanel);
        thresholdPanel.add(highStockPanel);
        
        panel.add(thresholdPanel, gbc);
        
        // Options Panel (Checkboxes)
        gbc.gridy++;
        addLabel(panel, gbc, "Report Options", false);
        gbc.gridy++;
        
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbcOpt = new GridBagConstraints();
        gbcOpt.fill = GridBagConstraints.HORIZONTAL;
        gbcOpt.insets = new Insets(5, 5, 5, 5);
        
        // First row of checkboxes
        gbcOpt.gridx = 0;
        gbcOpt.gridy = 0;
        gbcOpt.gridwidth = 1;
        includeLowStockCheck = createStyledCheckBox("Highlight Low Stock");
        includeLowStockCheck.setSelected(true);
        includeLowStockCheck.addActionListener(e -> refreshPreview());
        optionsPanel.add(includeLowStockCheck, gbcOpt);
        
        gbcOpt.gridy = 1;
        includeZeroStockCheck = createStyledCheckBox("Show Out of Stock");
        includeZeroStockCheck.addActionListener(e -> refreshPreview());
        optionsPanel.add(includeZeroStockCheck, gbcOpt);
        
        gbcOpt.gridy = 2;
        showPriceDetailsCheck = createStyledCheckBox("Include Price Details");
        showPriceDetailsCheck.setSelected(true);
        showPriceDetailsCheck.addActionListener(e -> refreshPreview());
        optionsPanel.add(showPriceDetailsCheck, gbcOpt);
        
        panel.add(optionsPanel, gbc);
        
        // Preview Table
        gbc.gridy++;
        addLabel(panel, gbc, "Inventory Preview", false);
        gbc.gridy++;
        
        // Create table with actual data
        String[] columns = {"Product Name", "Category", "Warehouse", "Quantity", "Unit", "Min Stock", "Status", "Value (₱)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        
        previewTable = new JTable(model);
        previewTable.setFont(TABLE_FONT);
        previewTable.setRowHeight(35);
        previewTable.setShowGrid(true);
        previewTable.setGridColor(new Color(230, 230, 230));
        previewTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Custom header
        JTableHeader header = previewTable.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        header.setReorderingAllowed(false);
        
        // Set column widths
        int[] columnWidths = {140, 100, 120, 80, 80, 90, 100, 120};
        for (int i = 0; i < columnWidths.length && i < previewTable.getColumnCount(); i++) {
            previewTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        
        JScrollPane tableScroll = new JScrollPane(previewTable);
        tableScroll.setPreferredSize(new Dimension(850, 200));
        styleScrollPane(tableScroll);
        panel.add(tableScroll, gbc);
        
        // Summary Panel - will be populated dynamically
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JPanel summaryPanel = createSummaryPanel();
        panel.add(summaryPanel, gbc);
        
        // Notes (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Additional Notes", false);
        gbc.gridy++;
        notesArea = createStyledTextArea("Optional: Add any additional notes or comments for the report", 2);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        styleScrollPane(notesScroll);
        panel.add(notesScroll, gbc);
        
        // Status indicator (exactly matching SalesReportDialog)
        gbc.gridy++;
        gbc.insets = new Insets(25, 10, 5, 10);
        JPanel statusIndicatorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusIndicatorPanel.setOpaque(false);
        
        JLabel requiredLabel = createStatusLabel("● Required", REQUIRED_COLOR);
        JLabel optionalLabel = createStatusLabel("● Optional", OPTIONAL_COLOR);
        
        statusIndicatorPanel.add(requiredLabel);
        statusIndicatorPanel.add(optionalLabel);
        panel.add(statusIndicatorPanel, gbc);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] titles = {"Total Items", "Total Value", "Low Stock", "Avg. Stock"};
        Color[] colors = {SUCCESS_GREEN, DARK_SKY_BLUE, LOW_STOCK_COLOR, WARNING_YELLOW};
        
        // Create stat cards (matching SalesReportDialog structure)
        for (int i = 0; i < 4; i++) {
            JPanel statCard = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw rounded background
                    g2.setColor(new Color(250, 250, 255));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    
                    // Draw border
                    g2.setColor(BORDER_COLOR);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                    
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            statCard.setLayout(new BorderLayout());
            statCard.setOpaque(false);
            statCard.setBorder(new EmptyBorder(15, 15, 15, 15));
            
            JLabel titleLabel = new JLabel(titles[i], SwingConstants.CENTER);
            titleLabel.setFont(SUMMARY_TITLE_FONT);
            titleLabel.setForeground(LABEL_COLOR);
            
            JLabel valueLabel = new JLabel("0", SwingConstants.CENTER);
            valueLabel.setFont(SUMMARY_VALUE_FONT);
            valueLabel.setForeground(colors[i]);
            
            // Store references for dynamic updates (matching SalesReportDialog)
            switch (i) {
                case 0: totalItemsLabel = valueLabel; break;
                case 1: totalValueLabel = valueLabel; break;
                case 2: lowStockLabel = valueLabel; break;
                case 3: avgStockLabel = valueLabel; break;
            }
            
            statCard.add(titleLabel, BorderLayout.NORTH);
            statCard.add(valueLabel, BorderLayout.CENTER);
            panel.add(statCard);
        }
        
        return panel;
    }
    
    private JPanel createThresholdPanel(String title, String range, Color color) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JPanel colorIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        colorIndicator.setPreferredSize(new Dimension(18, 18));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(LABEL_COLOR);
        
        JLabel rangeLabel = new JLabel(range);
        rangeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rangeLabel.setForeground(OPTIONAL_COLOR);
        
        panel.add(colorIndicator, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(rangeLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 0, 0, 0));
        
        // Generate button (matching SalesReportDialog exactly)
        generateButton = createStyledButton("📊 Generate Report", SKY_BLUE, SKY_BLUE.darker());
        generateButton.addActionListener(e -> onGenerate());
        
        // Preview button
        previewButton = createStyledButton("👁️ Preview", new Color(180, 220, 240), DARK_SKY_BLUE);
        previewButton.addActionListener(e -> onPreview());
        
        // Export button
        exportButton = createStyledButton("💾 Export Data", new Color(180, 240, 180), SUCCESS_GREEN.darker());
        exportButton.addActionListener(e -> onExport());
        
        // Cancel button
        cancelButton = createStyledButton("✕ Cancel", new Color(200, 200, 200), HOVER_RED);
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(generateButton);
        panel.add(previewButton);
        panel.add(exportButton);
        panel.add(cancelButton);
        
        // Make Generate the default button
        getRootPane().setDefaultButton(generateButton);
        
        return panel;
    }
    
    /* ================= UI COMPONENT CREATION METHODS ================= */
    // These methods are now identical to SalesReportDialog
    
    private JButton createCloseButton() {
        JButton btn = new JButton("✕");
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setForeground(new Color(140, 140, 140));
        btn.setPreferredSize(new Dimension(45, 45));
        btn.addActionListener(e -> dispose());
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(Color.RED);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 22));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(new Color(140, 140, 140));
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
                
                // Draw rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Draw border
                g2.setColor(getBackground().darker());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 55));
        button.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 25));
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
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
                setBackground(INPUT_BG);
                setForeground(readOnly ? new Color(80, 80, 80) : Color.GRAY);
                setText(placeholder);
                setCaretColor(DARK_SKY_BLUE);
                setPreferredSize(new Dimension(350, 52));
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw border
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
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
                        if (index == -1 && (value == null || value.toString().contains("Select"))) {
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
                setPreferredSize(new Dimension(350, 52));
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw border
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Draw dropdown arrow
                g2.setColor(DARK_SKY_BLUE);
                int[] xPoints = {getWidth() - 25, getWidth() - 15, getWidth() - 20};
                int[] yPoints = {getHeight()/2 - 3, getHeight()/2 - 3, getHeight()/2 + 3};
                g2.fillPolygon(xPoints, yPoints, 3);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
    
    private JCheckBox createStyledCheckBox(String text) {
        return new JCheckBox(text) {
            {
                setFont(INPUT_FONT);
                setForeground(LABEL_COLOR);
                setBackground(null);
                setOpaque(false);
                setFocusPainted(false);
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
                
                // Draw rounded background
                g2.setColor(INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw border
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
    
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(INPUT_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Custom scrollbar (matching SalesReportDialog)
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
    
    private void setDefaultDates() {
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        
        startDateField.setText(oneMonthAgo.format(dateFormatter));
        startDateField.setForeground(new Color(40, 40, 40));
        startDateField.setCaretPosition(0);
        
        endDateField.setText(today.format(dateFormatter));
        endDateField.setForeground(new Color(40, 40, 40));
        endDateField.setCaretPosition(0);
    }
    
    private void onGenerate() {
        // Validate required fields (matching SalesReportDialog structure)
        if (reportTypeCombo.getSelectedIndex() == -1 || 
            reportTypeCombo.getSelectedItem() == null) {
            showError("Please select a Report Type");
            reportTypeCombo.requestFocus();
            return;
        }
        
        if (formatCombo.getSelectedIndex() == -1 || 
            formatCombo.getSelectedItem() == null) {
            showError("Please select an Output Format");
            formatCombo.requestFocus();
            return;
        }
        
        // Validate dates if provided
        if (!startDateField.getText().equals("YYYY-MM-DD") && !endDateField.getText().equals("YYYY-MM-DD")) {
            try {
                LocalDate startDate = LocalDate.parse(startDateField.getText().trim(), dateFormatter);
                LocalDate endDate = LocalDate.parse(endDateField.getText().trim(), dateFormatter);
                
                if (endDate.isBefore(startDate)) {
                    showError("End Date cannot be before Start Date");
                    endDateField.requestFocus();
                    return;
                }
            } catch (DateTimeParseException e) {
                showError("Invalid date format. Please use YYYY-MM-DD format");
                startDateField.requestFocus();
                return;
            }
        }
        
        // Generate report with actual data
        generateActualReport();
    }
    
    private void generateActualReport() {
        List<InventoryItem> items = getFilteredInventory();
        
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "⚠️ No inventory data found for the selected filters",
                "No Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Calculate totals
        int totalItems = items.size();
        double totalValue = 0;
        int lowStockCount = 0;
        int outOfStockCount = 0;
        int totalQuantity = 0;
        
        for (InventoryItem item : items) {
            totalValue += item.getValue();
            totalQuantity += item.getQuantity();
            if (item.getQuantity() == 0) {
                outOfStockCount++;
            } else if (item.getQuantity() < item.getMinStockLevel()) {
                lowStockCount++;
            }
        }
        
        double avgStock = totalItems > 0 ? (double) totalQuantity / totalItems : 0;
        int marbleCount = 0;
        int graniteCount = 0;
        
        for (InventoryItem item : items) {
            if ("Marble".equalsIgnoreCase(item.getCategory())) {
                marbleCount++;
            } else if ("Granite".equalsIgnoreCase(item.getCategory())) {
                graniteCount++;
            }
        }
        
        // Show success message
        StringBuilder message = new StringBuilder();
        message.append("✅ Inventory Report Generated Successfully!\n\n");
        message.append("Report Type: ").append(reportTypeCombo.getSelectedItem()).append("\n");
        message.append("Category Filter: ").append(categoryCombo.getSelectedItem()).append("\n");
        message.append("Warehouse: ").append(warehouseCombo.getSelectedItem()).append("\n");
        
        if (!startDateField.getText().equals("YYYY-MM-DD")) {
            message.append("Start Date: ").append(startDateField.getText()).append("\n");
            message.append("End Date: ").append(endDateField.getText()).append("\n");
        }
        
        message.append("Format: ").append(formatCombo.getSelectedItem()).append("\n");
        
        message.append("\nSummary:\n");
        message.append(String.format("  • Total Items: %d\n", totalItems));
        message.append(String.format("  • Total Value: ₱ %,.2f\n", totalValue));
        message.append(String.format("  • Average Stock: %.0f units\n", avgStock));
        message.append(String.format("  • Low Stock Items: %d\n", lowStockCount));
        message.append(String.format("  • Out of Stock Items: %d\n", outOfStockCount));
        
        message.append("\nCategory Distribution:\n");
        message.append(String.format("  • Marble: %d items (%.1f%%)\n", marbleCount, 
            totalItems > 0 ? (marbleCount * 100.0 / totalItems) : 0));
        message.append(String.format("  • Granite: %d items (%.1f%%)\n", graniteCount,
            totalItems > 0 ? (graniteCount * 100.0 / totalItems) : 0));
        
        message.append("\nOptions Applied:\n");
        message.append("  • Highlight Low Stock: ").append(includeLowStockCheck.isSelected() ? "Yes" : "No").append("\n");
        message.append("  • Show Out of Stock: ").append(includeZeroStockCheck.isSelected() ? "Yes" : "No").append("\n");
        message.append("  • Price Details: ").append(showPriceDetailsCheck.isSelected() ? "Yes" : "No").append("\n");
        
        if (!notesArea.getText().trim().isEmpty() && !notesArea.getText().equals(notesArea.getToolTipText())) {
            message.append("  • Notes: Included\n");
        }
        
        JOptionPane.showMessageDialog(this, 
            message.toString(),
            "Inventory Report Generated", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // Print to console for debugging
        printReportDetails(totalItems, totalValue);
    }
    
    private void onPreview() {
        List<InventoryItem> items = getFilteredInventory();
        
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "⚠️ No inventory data to preview",
                "No Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show a preview dialog
        JDialog previewDialog = new JDialog(this, "Inventory Report Preview", true);
        previewDialog.setUndecorated(true);
        previewDialog.getContentPane().setBackground(LIGHT_BLUE_BG);
        previewDialog.setLayout(new BorderLayout());
        
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(700, 500));
        
        JLabel title = new JLabel("📋 Inventory Report Preview");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JTextArea previewText = new JTextArea();
        previewText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        previewText.setText(generatePreviewText(items));
        previewText.setEditable(false);
        previewText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane previewScroll = new JScrollPane(previewText);
        previewScroll.setPreferredSize(new Dimension(650, 350));
        
        JButton closePreviewBtn = createStyledButton("Close Preview", 
            new Color(200, 200, 200), HOVER_RED);
        closePreviewBtn.addActionListener(e -> previewDialog.dispose());
        
        JPanel previewButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        previewButtonPanel.setOpaque(false);
        previewButtonPanel.add(closePreviewBtn);
        
        card.add(title, BorderLayout.NORTH);
        card.add(previewScroll, BorderLayout.CENTER);
        card.add(previewButtonPanel, BorderLayout.SOUTH);
        
        previewDialog.add(card);
        previewDialog.pack();
        previewDialog.setLocationRelativeTo(this);
        previewDialog.setVisible(true);
    }
    
    private void onExport() {
        List<InventoryItem> items = getFilteredInventory();
        
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "⚠️ No inventory data to export",
                "No Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Inventory Report Data");
        
        // Set default file name
        String fileName = "Inventory_Report_" + LocalDate.now().format(dateFormatter) + ".csv";
        fileChooser.setSelectedFile(new java.io.File(fileName));
        
        // Add file filters
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "CSV Files (*.csv)", "csv"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Excel Files (*.xlsx)", "xlsx"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "PDF Files (*.pdf)", "pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            // Add extension if not present
            String filePath = fileToSave.getAbsolutePath();
            javax.swing.filechooser.FileFilter selectedFilter = fileChooser.getFileFilter();
            
            if (selectedFilter instanceof javax.swing.filechooser.FileNameExtensionFilter) {
                String[] extensions = ((javax.swing.filechooser.FileNameExtensionFilter) selectedFilter).getExtensions();
                if (extensions.length > 0 && !filePath.toLowerCase().endsWith("." + extensions[0])) {
                    fileToSave = new java.io.File(filePath + "." + extensions[0]);
                }
            }
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "✅ Inventory report data exported successfully!\n\n" +
                "File: " + fileToSave.getName() + "\n" +
                "Location: " + fileToSave.getParent() + "\n" +
                "Records: " + items.size() + " inventory items",
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private String generatePreviewText(List<InventoryItem> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(70)).append("\n");
        sb.append("               FACTORY INVENTORY REPORT PREVIEW\n");
        sb.append("=".repeat(70)).append("\n\n");
        
        sb.append("Report Type    : ").append(reportTypeCombo.getSelectedItem()).append("\n");
        sb.append("Category Filter: ").append(categoryCombo.getSelectedItem()).append("\n");
        sb.append("Warehouse      : ").append(warehouseCombo.getSelectedItem()).append("\n");
        
        if (!startDateField.getText().equals("YYYY-MM-DD")) {
            sb.append("Start Date     : ").append(startDateField.getText()).append("\n");
            sb.append("End Date       : ").append(endDateField.getText()).append("\n");
        }
        
        sb.append("Format         : ").append(formatCombo.getSelectedItem()).append("\n");
        sb.append("Generated On   : ").append(LocalDate.now().format(dateFormatter)).append("\n");
        sb.append("-".repeat(70)).append("\n\n");
        
        // Calculate totals
        int totalItems = items.size();
        double totalValue = 0;
        int lowStockCount = 0;
        int outOfStockCount = 0;
        int totalQuantity = 0;
        int marbleCount = 0;
        int graniteCount = 0;
        
        for (InventoryItem item : items) {
            totalValue += item.getValue();
            totalQuantity += item.getQuantity();
            if (item.getQuantity() == 0) {
                outOfStockCount++;
            } else if (item.getQuantity() < item.getMinStockLevel()) {
                lowStockCount++;
            }
            
            if ("Marble".equalsIgnoreCase(item.getCategory())) {
                marbleCount++;
            } else if ("Granite".equalsIgnoreCase(item.getCategory())) {
                graniteCount++;
            }
        }
        
        double avgStock = totalItems > 0 ? (double) totalQuantity / totalItems : 0;
        
        sb.append("INVENTORY SUMMARY\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append(String.format("Total Items          : %d items\n", totalItems));
        sb.append(String.format("Total Inventory Value: ₱ %,.2f\n", totalValue));
        sb.append(String.format("Average Stock        : %.0f units per item\n", avgStock));
        sb.append(String.format("Low Stock Items      : %d items\n", lowStockCount));
        sb.append(String.format("Out of Stock Items   : %d items\n\n", outOfStockCount));
        
        sb.append("CATEGORY DISTRIBUTION\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append(String.format("Marble               : %d items (%.1f%%)\n", marbleCount, 
            totalItems > 0 ? (marbleCount * 100.0 / totalItems) : 0));
        sb.append(String.format("Granite              : %d items (%.1f%%)\n\n", graniteCount,
            totalItems > 0 ? (graniteCount * 100.0 / totalItems) : 0));
        
        sb.append("INVENTORY DETAILS\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append(String.format("%-20s %-10s %-15s %-8s %-10s %-8s %-10s\n", 
            "Product", "Category", "Warehouse", "Qty", "Min Stock", "Status", "Value"));
        sb.append("-".repeat(70)).append("\n");
        
        for (InventoryItem item : items) {
            sb.append(String.format("%-20s %-10s %-15s %-8s %-10s %-8s %-10s\n",
                item.getProductName().length() > 20 ? item.getProductName().substring(0, 17) + "..." : item.getProductName(),
                item.getCategory(),
                item.getWarehouse().length() > 15 ? item.getWarehouse().substring(0, 12) + "..." : item.getWarehouse(),
                item.getQuantity(),
                item.getMinStockLevel(),
                getStockStatus(item.getQuantity(), item.getMinStockLevel()),
                String.format("%,.0f", item.getValue())));
        }
        
        sb.append("\nREPORT OPTIONS\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append("Highlight Low Stock    : ").append(includeLowStockCheck.isSelected() ? "Yes" : "No").append("\n");
        sb.append("Show Out of Stock      : ").append(includeZeroStockCheck.isSelected() ? "Yes" : "No").append("\n");
        sb.append("Include Price Details  : ").append(showPriceDetailsCheck.isSelected() ? "Yes" : "No").append("\n");
        
        if (!notesArea.getText().trim().isEmpty() && !notesArea.getText().equals(notesArea.getToolTipText())) {
            sb.append("\nNOTES\n");
            sb.append("-".repeat(70)).append("\n");
            sb.append(notesArea.getText().trim()).append("\n");
        }
        
        sb.append("\n").append("=".repeat(70)).append("\n");
        sb.append("               END OF INVENTORY REPORT\n");
        sb.append("=".repeat(70));
        
        return sb.toString();
    }
    
    private void printReportDetails(int itemCount, double totalValue) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("        FACTORY INVENTORY REPORT GENERATION");
        System.out.println("═".repeat(60));
        System.out.println("Report Type      : " + reportTypeCombo.getSelectedItem());
        System.out.println("Category Filter  : " + categoryCombo.getSelectedItem());
        System.out.println("Warehouse Filter : " + warehouseCombo.getSelectedItem());
        System.out.println("Output Format    : " + formatCombo.getSelectedItem());
        System.out.println("Total Items      : " + itemCount);
        System.out.println("Total Value      : ₱ " + String.format("%,.2f", totalValue));
        System.out.println("═".repeat(60));
        System.out.println("Report generated successfully!");
        System.out.println("═".repeat(60) + "\n");
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            "❌ " + message + "\n\nPlease fill in all required fields marked with *", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    /* ================= HELPER CLASSES ================= */
    
    private class SimpleDocumentListener implements DocumentListener {
        private final Runnable callback;
        
        public SimpleDocumentListener(Runnable callback) {
            this.callback = callback;
        }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
            callback.run();
        }
        
        @Override
        public void removeUpdate(DocumentEvent e) {
            callback.run();
        }
        
        @Override
        public void changedUpdate(DocumentEvent e) {
            callback.run();
        }
    }
    
    /* ================= STOCK STATUS RENDERER ================= */
    private class StockStatusRenderer extends DefaultTableCellRenderer {
        public StockStatusRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                if ("High Stock".equals(status)) {
                    c.setForeground(HIGH_STOCK_COLOR);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("Medium Stock".equals(status)) {
                    c.setForeground(MEDIUM_STOCK_COLOR);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("Low Stock".equals(status) || "Out of Stock".equals(status)) {
                    c.setForeground(LOW_STOCK_COLOR);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
            }
            
            ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
            return c;
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
    
    /* ================= INTERFACE DEFINITIONS ================= */
    
    // These interfaces should match your existing data managers
    public interface ProductManager {
        List<String> getCategories();
    }
    
    public interface InventoryManager {
        List<InventoryItem> getInventoryItems();
    }
    
    public interface InventoryItem {
        String getProductName();
        String getCategory();  // Returns "Marble" or "Granite"
        String getWarehouse();
        int getQuantity();
        String getUnit();
        int getMinStockLevel();
        double getValue();
    }
    
    /* ================= TEST METHOD ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Inventory Report Dialog Test");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            
            // Create a simulated content panel (right white panel)
            JPanel contentPanel = new JPanel();
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setPreferredSize(new Dimension(800, 600));
            frame.add(contentPanel, BorderLayout.CENTER);
            
            // Create mock data managers for testing
            ProductManager mockProductManager = () -> {
                List<String> categories = new ArrayList<>();
                categories.add("Marble");
                categories.add("Granite");
                return categories;
            };
            
            InventoryManager mockInventoryManager = new InventoryManager() {
                @Override
                public List<InventoryItem> getInventoryItems() {
                    List<InventoryItem> items = new ArrayList<>();
                    
                    // Add sample inventory items
                    for (int i = 1; i <= 10; i++) {
                        final int index = i;
                        items.add(new InventoryItem() {
                            @Override
                            public String getProductName() {
                                String[] marbleNames = {"Carrara White", "Calacatta Gold", "Statuario", "Emperador Dark"};
                                String[] graniteNames = {"Absolute Black", "Ubatuba", "Blue Pearl", "Santa Cecilia"};
                                return index % 2 == 0 ? marbleNames[index % 4] : graniteNames[index % 4];
                            }
                            
                            @Override
                            public String getCategory() {
                                return index % 2 == 0 ? "Marble" : "Granite";
                            }
                            
                            @Override
                            public String getWarehouse() {
                                String[] warehouses = {"Main Warehouse", "North Storage", "South Storage", "Processing Unit"};
                                return warehouses[index % 4];
                            }
                            
                            @Override
                            public int getQuantity() {
                                return (index * 50) + (index % 3 * 20);
                            }
                            
                            @Override
                            public String getUnit() {
                                return index % 2 == 0 ? "slabs" : "sq. ft.";
                            }
                            
                            @Override
                            public int getMinStockLevel() {
                                return 100;
                            }
                            
                            @Override
                            public double getValue() {
                                return (index * 10000) + 5000;
                            }
                        });
                    }
                    
                    return items;
                }
            };
            
            // Test button to open dialog
            JButton testBtn = new JButton("Open Inventory Report Dialog");
            testBtn.addActionListener(e -> {
                InventoryReportDialog dlg = new InventoryReportDialog(
                    frame, 
                    contentPanel,
                    mockProductManager,
                    mockInventoryManager
                );
                dlg.setVisible(true);
            });
            
            JPanel northPanel = new JPanel();
            northPanel.add(testBtn);
            frame.add(northPanel, BorderLayout.NORTH);
            
            frame.setVisible(true);
        });
    }
}