package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

/**
 * SalesReportDialog - Modern Sales Report Form with Database Integration
 * Integrated with actual tile_factory_db database
 */
public class SalesReportDialog extends JDialog {
    // Colors - matching other dialogs
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
    private static final Color PAID_COLOR = new Color(40, 167, 69);
    private static final Color PENDING_COLOR = new Color(255, 193, 7);
    private static final Color CANCELLED_COLOR = new Color(220, 53, 69);
    private static final Color TABLE_HEADER_BG = new Color(245, 245, 245);
    private static final Color TABLE_HEADER_FG = Color.BLACK;
    
    // Form fields
    private JComboBox<String> reportTypeCombo;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> formatCombo;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> customerCombo;
    private JTextField minAmountField;
    private JTextField maxAmountField;
    private JCheckBox includeSummaryCheck;
    private JCheckBox includeChartsCheck;
    private JCheckBox emailReportCheck;
    private JTextField emailField;
    private JTextArea notesArea;
    
    // Table for preview
    private JTable previewTable;
    
    // private Connection connection; // Database connectivity removed
    
    // Summary labels for dynamic updates
    private JLabel totalSalesLabel;
    private JLabel totalQuantityLabel;
    private JLabel avgSaleLabel;
    private JLabel completedOrdersLabel;
    
    // Date formatter - consistent with CreateInvoiceDialog
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Fonts (matching InventoryReportDialog)
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
    public SalesReportDialog(JFrame ownerFrame, JPanel contentPanel) {
        super(ownerFrame, true);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Initialize database connection
        /* DB connectivity removed */
        
        // Main card panel
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30, 35, 30, 35));
        card.setPreferredSize(new Dimension(950, 800));
        
        /* ================= TOP BAR ================= */
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 25, 0));
        
        // Title on left
        JLabel title = new JLabel("Sales Report Generator");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        topBar.add(title, BorderLayout.WEST);
        
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
        
        // Set default dates
        setDefaultDates();
        
        // Load dynamic data from database
        loadCategories();
        loadCustomers();
        refreshPreview();
        
        // Set focus to first field
        SwingUtilities.invokeLater(() -> reportTypeCombo.requestFocus());
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
    
    /* ================= DATABASE METHODS ================= */
    
    private List<String> getCategoriesFromDB() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.add("Marble");
        categories.add("Granite");
        categories.add("Tile");
        categories.add("Slab");
        return categories;
    }
    
    private List<String> getCustomersFromDB() {
        List<String> customers = new ArrayList<>();
        customers.add("All Customers");
        customers.add("Ali Traders");
        customers.add("Construction Co.");
        customers.add("Walk-in Customer");
        return customers;
    }
    
    private List<InvoiceData> getInvoicesFromDB() {
        return new ArrayList<>(); // DB connectivity removed
    }
    
    private void loadCategories() {
        List<String> categories = getCategoriesFromDB();
        categoryCombo.setModel(new DefaultComboBoxModel<>(categories.toArray(new String[0])));
    }
    
    private void loadCustomers() {
        List<String> customers = getCustomersFromDB();
        customerCombo.setModel(new DefaultComboBoxModel<>(customers.toArray(new String[0])));
    }
    
    private List<InvoiceData> getFilteredInvoices() {
        List<InvoiceData> allInvoices = getInvoicesFromDB();
        if (allInvoices.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<InvoiceData> filtered = new ArrayList<>();
        
        String category = (String) categoryCombo.getSelectedItem();
        String customer = (String) customerCombo.getSelectedItem();
        
        // Parse amount range
        double minAmount = 0;
        double maxAmount = Double.MAX_VALUE;
        
        try {
            if (minAmountField.getText() != null && !minAmountField.getText().trim().isEmpty()) {
                minAmount = Double.parseDouble(minAmountField.getText().trim());
            }
        } catch (NumberFormatException e) {
            minAmount = 0;
        }
        
        try {
            if (maxAmountField.getText() != null && !maxAmountField.getText().trim().isEmpty()) {
                maxAmount = Double.parseDouble(maxAmountField.getText().trim());
            }
        } catch (NumberFormatException e) {
            maxAmount = Double.MAX_VALUE;
        }
        
        // Parse date range
        LocalDate startDate = null;
        LocalDate endDate = null;
        
        try {
            if (startDateField.getText() != null && !startDateField.getText().trim().isEmpty()) {
                startDate = LocalDate.parse(startDateField.getText().trim(), dateFormatter);
            }
        } catch (DateTimeParseException e) {
            // Invalid date format, ignore filter
        }
        
        try {
            if (endDateField.getText() != null && !endDateField.getText().trim().isEmpty()) {
                endDate = LocalDate.parse(endDateField.getText().trim(), dateFormatter);
            }
        } catch (DateTimeParseException e) {
            // Invalid date format, ignore filter
        }
        
        // Apply filters
        for (InvoiceData invoice : allInvoices) {
            // Date filter
            if (startDate != null && invoice.date.isBefore(startDate)) {
                continue;
            }
            if (endDate != null && invoice.date.isAfter(endDate)) {
                continue;
            }
            
            // Category filter
            if (category != null && !category.equals("All") && 
                !category.equalsIgnoreCase(invoice.category)) {
                continue;
            }
            
            // Customer filter
            if (customer != null && !customer.equals("All Customers") && 
                !customer.equals(invoice.customerName)) {
                continue;
            }
            
            // Amount filter
            if (invoice.totalAmount < minAmount || invoice.totalAmount > maxAmount) {
                continue;
            }
            
            filtered.add(invoice);
        }
        
        return filtered;
    }
    
    /* ================= DATA MANAGEMENT ================= */
    
    private void refreshPreview() {
        SwingUtilities.invokeLater(() -> {
            List<InvoiceData> invoices = getFilteredInvoices();
            
            // Calculate totals
            double totalSales = 0;
            double totalQuantity = 0;
            int completedOrders = 0;
            
            for (InvoiceData invoice : invoices) {
                totalSales += invoice.totalAmount;
                totalQuantity += invoice.quantity;
                if ("PAID".equalsIgnoreCase(invoice.status)) {
                    completedOrders++;
                }
            }
            
            int totalOrders = invoices.size();
            double avgSale = totalOrders > 0 ? totalSales / totalOrders : 0;
            
            // Update summary panel
            updateSummaryPanel(totalSales, totalQuantity, avgSale, totalOrders, completedOrders);
            
            // Update preview table
            updatePreviewTable(invoices);
        });
    }
    
    private void updateSummaryPanel(double totalSales, double totalQuantity, double avgSale, 
                                   int totalOrders, int completedOrders) {
        if (totalSalesLabel != null) {
            totalSalesLabel.setText(String.format("PKR %,.2f", totalSales));
            totalQuantityLabel.setText(String.format("%.0f Items", totalQuantity));
            avgSaleLabel.setText(String.format("PKR %,.2f", avgSale));
            completedOrdersLabel.setText(String.format("%d of %d", completedOrders, totalOrders));
        }
    }
    
    private void updatePreviewTable(List<InvoiceData> invoices) {
        String[] columns = {"Date", "Invoice #", "Customer", "Product", "Quantity", "Amount (PKR)", "Status"};
        Object[][] data = new Object[invoices.size()][columns.length];
        
        for (int i = 0; i < invoices.size(); i++) {
            InvoiceData invoice = invoices.get(i);
            data[i][0] = invoice.date.format(dateFormatter);
            data[i][1] = invoice.invoiceNumber;
            data[i][2] = invoice.customerName;
            data[i][3] = invoice.productName;
            data[i][4] = String.format("%.2f", invoice.quantity);
            data[i][5] = String.format("%,.2f", invoice.totalAmount);
            data[i][6] = formatStatus(invoice.status);
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
            int[] columnWidths = {100, 100, 140, 120, 80, 100, 100};
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
            previewTable.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());
            
            // Update table header
            JTableHeader header = previewTable.getTableHeader();
            header.repaint();
        }
    }
    
    private String formatStatus(String status) {
        if (status == null) return "";
        switch (status.toUpperCase()) {
            case "PAID": return "Paid";
            case "PENDING": return "Pending";
            case "CANCELLED": return "Cancelled";
            case "DRAFT": return "Draft";
            default: return status;
        }
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
        
        // Report Type (Required)
        addLabel(panel, gbc, "Report Type", true);
        gbc.gridy++;
        String[] reportTypes = {"Daily Sales", "Weekly Sales", 
                               "Monthly Sales", "Quarterly Sales", "Yearly Sales", 
                               "Custom Period", "Product-wise Sales", "Customer-wise Sales"};
        reportTypeCombo = createStyledComboBox(reportTypes, "Choose report type");
        reportTypeCombo.addActionListener(e -> refreshPreview());
        panel.add(reportTypeCombo, gbc);
        
        // Start Date (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Start Date", true);
        gbc.gridy++;
        startDateField = createStyledTextField("YYYY-MM-DD", false);
        startDateField.setToolTipText("Format: YYYY-MM-DD (same as invoices)");
        startDateField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> refreshPreview()));
        panel.add(startDateField, gbc);
        
        // End Date (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "End Date", true);
        gbc.gridy++;
        endDateField = createStyledTextField("YYYY-MM-DD", false);
        endDateField.setToolTipText("Format: YYYY-MM-DD (same as invoices)");
        endDateField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> refreshPreview()));
        panel.add(endDateField, gbc);
        
        // Output Format (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Output Format", true);
        gbc.gridy++;
        String[] formats = {"PDF Document", "Excel Spreadsheet", "CSV File", 
                           "HTML Report", "Printable PDF"};
        formatCombo = createStyledComboBox(formats, "Select output format");
        panel.add(formatCombo, gbc);
        
        // Category Filter (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Category Filter", false);
        gbc.gridy++;
        categoryCombo = createStyledComboBox(new String[]{"Loading..."}, "Filter by category");
        categoryCombo.addActionListener(e -> refreshPreview());
        panel.add(categoryCombo, gbc);
        
        // Customer Filter (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Customer Filter", false);
        gbc.gridy++;
        customerCombo = createStyledComboBox(new String[]{"Loading..."}, "Filter by customer");
        customerCombo.addActionListener(e -> refreshPreview());
        panel.add(customerCombo, gbc);
        
        // Amount Range (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Amount Range (PKR)", false);
        gbc.gridy++;
        
        JPanel amountPanel = new JPanel(new GridBagLayout());
        amountPanel.setOpaque(false);
        amountPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        GridBagConstraints gbcAmount = new GridBagConstraints();
        gbcAmount.fill = GridBagConstraints.HORIZONTAL;
        gbcAmount.insets = new Insets(0, 5, 0, 5);
        
        // Min Amount
        gbcAmount.gridx = 0;
        gbcAmount.gridy = 0;
        gbcAmount.weightx = 0.0;
        JLabel minLabel = new JLabel("Min:");
        minLabel.setFont(LABEL_FONT);
        minLabel.setForeground(LABEL_COLOR);
        amountPanel.add(minLabel, gbcAmount);
        
        gbcAmount.gridx = 1;
        gbcAmount.weightx = 1.0;
        minAmountField = createStyledTextField("0", false);
        minAmountField.setPreferredSize(new Dimension(120, 52));
        minAmountField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> refreshPreview()));
        amountPanel.add(minAmountField, gbcAmount);
        
        // Spacer
        gbcAmount.gridx = 2;
        gbcAmount.weightx = 0.2;
        amountPanel.add(Box.createHorizontalStrut(20), gbcAmount);
        
        // Max Amount
        gbcAmount.gridx = 3;
        gbcAmount.weightx = 0.0;
        JLabel maxLabel = new JLabel("Max:");
        maxLabel.setFont(LABEL_FONT);
        maxLabel.setForeground(LABEL_COLOR);
        amountPanel.add(maxLabel, gbcAmount);
        
        gbcAmount.gridx = 4;
        gbcAmount.weightx = 1.0;
        maxAmountField = createStyledTextField("", false);
        maxAmountField.setPreferredSize(new Dimension(120, 52));
        maxAmountField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> refreshPreview()));
        amountPanel.add(maxAmountField, gbcAmount);
        
        panel.add(amountPanel, gbc);
        
        // Status Indicators Panel
        gbc.gridy++;
        addLabel(panel, gbc, "Payment Status Indicators", false);
        gbc.gridy++;
        
        JPanel statusPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Only show statuses that exist in invoices
        JPanel paidPanel = createStatusIndicatorPanel("Paid", PAID_COLOR);
        JPanel pendingPanel = createStatusIndicatorPanel("Pending", PENDING_COLOR);
        JPanel cancelledPanel = createStatusIndicatorPanel("Cancelled", CANCELLED_COLOR);
        
        statusPanel.add(paidPanel);
        statusPanel.add(pendingPanel);
        statusPanel.add(cancelledPanel);
        
        panel.add(statusPanel, gbc);
        
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
        includeSummaryCheck = createStyledCheckBox("Include Summary");
        includeSummaryCheck.setSelected(true);
        optionsPanel.add(includeSummaryCheck, gbcOpt);
        
        gbcOpt.gridy = 1;
        includeChartsCheck = createStyledCheckBox("Include Charts");
        optionsPanel.add(includeChartsCheck, gbcOpt);
        
        gbcOpt.gridy = 2;
        emailReportCheck = createStyledCheckBox("Email Report");
        // Email functionality disabled as per requirements
        emailReportCheck.setEnabled(false);
        emailReportCheck.setToolTipText("Email functionality not implemented");
        optionsPanel.add(emailReportCheck, gbcOpt);
        
        panel.add(optionsPanel, gbc);
        
        // Preview Table
        gbc.gridy++;
        addLabel(panel, gbc, "Report Preview", false);
        gbc.gridy++;
        
        // Create table with actual data
        String[] columns = {"Date", "Invoice #", "Customer", "Product", "Quantity", "Amount (PKR)", "Status"};
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
        int[] columnWidths = {100, 100, 140, 120, 80, 100, 100};
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
        
        // Status indicator
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
        
        String[] titles = {"Total Sales", "Total Quantity", "Avg. Sale", "Completed Orders"};
        Color[] colors = {SUCCESS_GREEN, DARK_SKY_BLUE, WARNING_YELLOW, new Color(111, 66, 193)};
        
        // Create stat cards
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
            
            // Store references for dynamic updates
            switch (i) {
                case 0: totalSalesLabel = valueLabel; break;
                case 1: totalQuantityLabel = valueLabel; break;
                case 2: avgSaleLabel = valueLabel; break;
                case 3: completedOrdersLabel = valueLabel; break;
            }
            
            statCard.add(titleLabel, BorderLayout.NORTH);
            statCard.add(valueLabel, BorderLayout.CENTER);
            panel.add(statCard);
        }
        
        return panel;
    }
    
    private JPanel createStatusIndicatorPanel(String title, Color color) {
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
        
        JLabel descriptionLabel = new JLabel(getPaymentStatusDescription(title));
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionLabel.setForeground(OPTIONAL_COLOR);
        
        panel.add(colorIndicator, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(descriptionLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private String getPaymentStatusDescription(String status) {
        switch (status) {
            case "Paid": return "Payment completed";
            case "Pending": return "Payment pending";
            case "Cancelled": return "Order cancelled";
            default: return "";
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 0, 0, 0));
        
        // Generate button
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
        
        // Custom scrollbar (matching InventoryReportDialog)
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
        ((javax.swing.text.JTextComponent) startDateField).setCaretPosition(0);
        
        endDateField.setText(today.format(dateFormatter));
        endDateField.setForeground(new Color(40, 40, 40));
        ((javax.swing.text.JTextComponent) endDateField).setCaretPosition(0);
    }
    
    private void onGenerate() {
        // Validate required fields
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
        
        // Validate dates
        LocalDate startDate;
        LocalDate endDate;
        
        try {
            startDate = LocalDate.parse(startDateField.getText().trim(), dateFormatter);
        } catch (DateTimeParseException e) {
            showError("Invalid Start Date format. Please use YYYY-MM-DD format");
            startDateField.requestFocus();
            return;
        }
        
        try {
            endDate = LocalDate.parse(endDateField.getText().trim(), dateFormatter);
        } catch (DateTimeParseException e) {
            showError("Invalid End Date format. Please use YYYY-MM-DD format");
            endDateField.requestFocus();
            return;
        }
        
        if (endDate.isBefore(startDate)) {
            showError("End Date cannot be before Start Date");
            endDateField.requestFocus();
            return;
        }
        
        // Validate amount range
        double minAmount;
        double maxAmount;
        
        try {
            minAmount = minAmountField.getText().trim().isEmpty() ? 0 : 
                Double.parseDouble(minAmountField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Invalid minimum amount. Please enter a numeric value");
            minAmountField.requestFocus();
            return;
        }
        
        try {
            maxAmount = maxAmountField.getText().trim().isEmpty() ? Double.MAX_VALUE : 
                Double.parseDouble(maxAmountField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Invalid maximum amount. Please enter a numeric value");
            maxAmountField.requestFocus();
            return;
        }
        
        if (maxAmount < minAmount) {
            showError("Maximum amount must be greater than or equal to minimum amount");
            maxAmountField.requestFocus();
            return;
        }
        
        // Generate report with actual data
        generateActualReport();
    }
    
    private void generateActualReport() {
        List<InvoiceData> invoices = getFilteredInvoices();
        
        if (invoices.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "⚠️ No sales data found for the selected filters",
                "No Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Calculate totals
        double totalSales = 0;
        double totalQuantity = 0;
        int completedOrders = 0;
        
        for (InvoiceData invoice : invoices) {
            totalSales += invoice.totalAmount;
            totalQuantity += invoice.quantity;
            if ("PAID".equalsIgnoreCase(invoice.status)) {
                completedOrders++;
            }
        }
        
        int totalOrders = invoices.size();
        double avgSale = totalOrders > 0 ? totalSales / totalOrders : 0;
        double completionRate = totalOrders > 0 ? (completedOrders * 100.0 / totalOrders) : 0;
        
        // Show success message
        StringBuilder message = new StringBuilder();
        message.append("✅ Sales Report Generated Successfully!\n\n");
        message.append("Report Type: ").append(reportTypeCombo.getSelectedItem()).append("\n");
        message.append("Period: ").append(startDateField.getText()).append(" to ").append(endDateField.getText()).append("\n");
        message.append("Format: ").append(formatCombo.getSelectedItem()).append("\n");
        
        message.append("\nSummary:\n");
        message.append(String.format("  • Total Sales: PKR %,.2f\n", totalSales));
        message.append(String.format("  • Total Items: %.0f\n", totalQuantity));
        message.append(String.format("  • Average Sale: PKR %,.2f\n", avgSale));
        message.append(String.format("  • Orders: %d transactions\n", totalOrders));
        message.append(String.format("  • Completion Rate: %.1f%%\n", completionRate));
        
        message.append("\nFilters Applied:\n");
        message.append("  • Category: ").append(categoryCombo.getSelectedItem()).append("\n");
        message.append("  • Customer: ").append(customerCombo.getSelectedItem()).append("\n");
        
        String minAmount = minAmountField.getText().trim().isEmpty() ? "0" : minAmountField.getText();
        String maxAmount = maxAmountField.getText().trim().isEmpty() ? "No Limit" : maxAmountField.getText();
        message.append("  • Amount Range: PKR ").append(minAmount).append(" - PKR ").append(maxAmount).append("\n");
        
        if (includeSummaryCheck.isSelected()) {
            message.append("  • Include Summary: Yes\n");
        }
        
        if (includeChartsCheck.isSelected()) {
            message.append("  • Include Charts: Yes\n");
        }
        
        if (!notesArea.getText().trim().isEmpty() && !notesArea.getText().equals(notesArea.getToolTipText())) {
            message.append("  • Notes: Included\n");
        }
        
        JOptionPane.showMessageDialog(this, 
            message.toString(),
            "Report Generated", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // Print to console for debugging
        printReportDetails(totalOrders, totalSales);
        
        // Optionally, you can save the report to database using stored procedure
        // saveReportToDatabase(reportTypeCombo.getSelectedItem().toString(), startDateField.getText(), 
        //                     endDateField.getText(), totalSales, totalQuantity, totalOrders);
    }
    
    private void saveReportToDatabase(String reportType, String startDate, String endDate, 
                                      double totalSales, double totalQuantity, int totalOrders) {
        /* DB connectivity removed */
    }
    
    private void onPreview() {
        List<InvoiceData> invoices = getFilteredInvoices();
        
        if (invoices.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "⚠️ No sales data found for the selected filters",
                "No Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show a preview dialog
        JDialog previewDialog = new JDialog(this, "Report Preview", true);
        previewDialog.setUndecorated(true);
        previewDialog.getContentPane().setBackground(LIGHT_BLUE_BG);
        previewDialog.setLayout(new BorderLayout());
        
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(700, 500));
        
        JLabel title = new JLabel("📋 Report Preview");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JTextArea previewText = new JTextArea();
        previewText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        previewText.setText(generatePreviewText(invoices));
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
        List<InvoiceData> invoices = getFilteredInvoices();
        
        if (invoices.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "⚠️ No sales data to export",
                "No Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Sales Report Data");
        
        // Set default file name
        String fileName = "Sales_Report_" + LocalDate.now().format(dateFormatter) + ".csv";
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
            
            // Export data to CSV
            exportToCSV(fileToSave, invoices);
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "✅ Sales report data exported successfully!\n\n" +
                "File: " + fileToSave.getName() + "\n" +
                "Location: " + fileToSave.getParent() + "\n" +
                "Records: " + invoices.size() + " transactions",
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void exportToCSV(java.io.File file, List<InvoiceData> invoices) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
            // Write CSV header
            writer.println("Date,Invoice No,Customer,Product,Category,Quantity,Total Amount,Status");
            
            // Write data
            for (InvoiceData invoice : invoices) {
                writer.println(String.format("%s,%s,%s,%s,%s,%.2f,%.2f,%s",
                    invoice.date.format(dateFormatter),
                    invoice.invoiceNumber,
                    escapeCSV(invoice.customerName),
                    escapeCSV(invoice.productName),
                    invoice.category,
                    invoice.quantity,
                    invoice.totalAmount,
                    formatStatus(invoice.status)));
            }
            
            writer.flush();
        } catch (java.io.IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error exporting file: " + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private String generatePreviewText(List<InvoiceData> invoices) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(70)).append("\n");
        sb.append("               SALES REPORT PREVIEW\n");
        sb.append("=".repeat(70)).append("\n\n");
        
        sb.append("Report Type    : ").append(reportTypeCombo.getSelectedItem()).append("\n");
        sb.append("Period         : ").append(startDateField.getText()).append(" to ").append(endDateField.getText()).append("\n");
        sb.append("Format         : ").append(formatCombo.getSelectedItem()).append("\n");
        sb.append("Generated On   : ").append(LocalDate.now().format(dateFormatter)).append("\n");
        sb.append("-".repeat(70)).append("\n\n");
        
        // Calculate totals
        double totalSales = 0;
        double totalQuantity = 0;
        int completedOrders = 0;
        
        for (InvoiceData invoice : invoices) {
            totalSales += invoice.totalAmount;
            totalQuantity += invoice.quantity;
            if ("PAID".equalsIgnoreCase(invoice.status)) {
                completedOrders++;
            }
        }
        
        int totalOrders = invoices.size();
        double avgSale = totalOrders > 0 ? totalSales / totalOrders : 0;
        double completionRate = totalOrders > 0 ? (completedOrders * 100.0 / totalOrders) : 0;
        
        sb.append("SUMMARY\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append(String.format("Total Sales     : PKR %,.2f\n", totalSales));
        sb.append(String.format("Total Quantity  : %.0f Items\n", totalQuantity));
        sb.append(String.format("Average Sale    : PKR %,.2f\n", avgSale));
        sb.append(String.format("Orders Count    : %d Transactions\n", totalOrders));
        sb.append(String.format("Completion Rate : %.1f%% (%d of %d)\n\n", completionRate, completedOrders, totalOrders));
        
        sb.append("FILTERS APPLIED\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append("Category        : ").append(categoryCombo.getSelectedItem()).append("\n");
        sb.append("Customer        : ").append(customerCombo.getSelectedItem()).append("\n");
        
        String minAmount = minAmountField.getText().trim().isEmpty() ? "0" : minAmountField.getText();
        String maxAmount = maxAmountField.getText().trim().isEmpty() ? "No Limit" : maxAmountField.getText();
        sb.append("Amount Range    : PKR ").append(minAmount).append(" - PKR ").append(maxAmount).append("\n\n");
        
        sb.append("TRANSACTION DETAILS\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append(String.format("%-12s %-10s %-20s %-15s %-8s %-10s %-8s\n", 
            "Date", "Invoice", "Customer", "Product", "Qty", "Amount", "Status"));
        sb.append("-".repeat(70)).append("\n");
        
        for (InvoiceData invoice : invoices) {
            sb.append(String.format("%-12s %-10s %-20s %-15s %-8s %-10s %-8s\n",
                invoice.date.format(dateFormatter),
                invoice.invoiceNumber,
                invoice.customerName.length() > 20 ? invoice.customerName.substring(0, 17) + "..." : invoice.customerName,
                invoice.productName.length() > 15 ? invoice.productName.substring(0, 12) + "..." : invoice.productName,
                String.format("%.2f", invoice.quantity),
                String.format("%,.2f", invoice.totalAmount),
                formatStatus(invoice.status)));
        }
        
        sb.append("\nREPORT OPTIONS\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append("Include Summary : ").append(includeSummaryCheck.isSelected() ? "Yes" : "No").append("\n");
        sb.append("Include Charts  : ").append(includeChartsCheck.isSelected() ? "Yes" : "No").append("\n");
        
        if (!notesArea.getText().trim().isEmpty() && !notesArea.getText().equals(notesArea.getToolTipText())) {
            sb.append("\nNOTES\n");
            sb.append("-".repeat(70)).append("\n");
            sb.append(notesArea.getText().trim()).append("\n");
        }
        
        sb.append("\n").append("=".repeat(70)).append("\n");
        sb.append("               END OF REPORT PREVIEW\n");
        sb.append("=".repeat(70));
        
        return sb.toString();
    }
    
    private void printReportDetails(int transactionCount, double totalSales) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("        SALES REPORT GENERATION DETAILS");
        System.out.println("═".repeat(60));
        System.out.println("Report Type      : " + reportTypeCombo.getSelectedItem());
        System.out.println("Period           : " + startDateField.getText() + " to " + endDateField.getText());
        System.out.println("Output Format    : " + formatCombo.getSelectedItem());
        System.out.println("Category Filter  : " + categoryCombo.getSelectedItem());
        System.out.println("Customer Filter  : " + customerCombo.getSelectedItem());
        System.out.println("Include Summary  : " + (includeSummaryCheck.isSelected() ? "Yes" : "No"));
        System.out.println("Include Charts   : " + (includeChartsCheck.isSelected() ? "Yes" : "No"));
        System.out.println("Transactions     : " + transactionCount);
        System.out.println("Total Sales      : PKR " + String.format("%,.2f", totalSales));
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
    
    private class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private final Runnable callback;
        
        public SimpleDocumentListener(Runnable callback) {
            this.callback = callback;
        }
        
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            callback.run();
        }
        
        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            callback.run();
        }
        
        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            callback.run();
        }
    }
    
    /* ================= STATUS RENDERER ================= */
    private class StatusRenderer extends DefaultTableCellRenderer {
        public StatusRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                if ("Paid".equalsIgnoreCase(status)) {
                    c.setForeground(PAID_COLOR);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("Pending".equalsIgnoreCase(status)) {
                    c.setForeground(PENDING_COLOR);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("Cancelled".equalsIgnoreCase(status)) {
                    c.setForeground(CANCELLED_COLOR);
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
    
    /* ================= DATA CLASS ================= */
    private static class InvoiceData {
        LocalDate date;
        String invoiceNumber;
        String customerName;
        String productName;
        String category;
        double quantity;
        double totalAmount;
        String status;
    }
    
    /* ================= TEST METHOD ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Sales Report Dialog Test");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            
            // Create a simulated content panel (right white panel)
            JPanel contentPanel = new JPanel();
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setPreferredSize(new Dimension(800, 600));
            frame.add(contentPanel, BorderLayout.CENTER);
            
            // Test button to open dialog
            JButton testBtn = new JButton("Open Sales Report Dialog");
            testBtn.addActionListener(e -> {
                SalesReportDialog dlg = new SalesReportDialog(frame, contentPanel);
                dlg.setVisible(true);
            });
            
            JPanel northPanel = new JPanel();
            northPanel.add(testBtn);
            frame.add(northPanel, BorderLayout.NORTH);
            
            frame.setVisible(true);
        });
    }
}