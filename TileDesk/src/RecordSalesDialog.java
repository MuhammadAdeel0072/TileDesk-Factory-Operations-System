package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ExecutionException;

/**
 * RecordSalesDialog - Modern Sales Records Viewer Dialog
 * DATABASE-FREE VERSION with all UI/UX preserved
 */
public class RecordSalesDialog extends JDialog {
    private static final Color SKY_BLUE = new Color(135, 206, 250);
    private static final Color DARK_SKY_BLUE = new Color(30, 144, 255);
    private static final Color LIGHT_BLUE_BG = new Color(240, 248, 255);
    private static final Color INPUT_BG = new Color(255, 255, 255);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(180, 200, 220);
    private static final Color LABEL_COLOR = new Color(40, 40, 40);
    private static final Color REQUIRED_COLOR = new Color(220, 53, 69);
    private static final Color OPTIONAL_COLOR = new Color(108, 117, 125);
    private static final Color HOVER_RED = new Color(220, 80, 80);
    private static final Color TABLE_HEADER_BG = new Color(245, 247, 250);
    private static final Color TABLE_SELECTION = new Color(225, 240, 255);
    private static final Color TABLE_GRID_COLOR = new Color(220, 230, 240);
    private static final Color GREEN_BUTTON = new Color(76, 175, 80);
    private static final Color GREEN_HOVER = new Color(56, 142, 60);
    private static final Color ORANGE_BUTTON = new Color(255, 152, 0);
    private static final Color ORANGE_HOVER = new Color(245, 124, 0);
    private static final Color TEXT_BLACK = new Color(40, 40, 40);
    private static final Color PLACEHOLDER_GRAY = new Color(150, 150, 150);
    
    // Filter fields
    private JTextField fromDateField;
    private JTextField toDateField;
    private JComboBox<String> productCombo;
    private JComboBox<String> categoryCombo;
    private JTextField customerField;
    
    // Tables
    private JTable salesTable;
    private DefaultTableModel salesTableModel;
    private JTable invoiceItemsTable;
    private DefaultTableModel itemsTableModel;
    
    // Summary labels
    private JLabel invoiceNoLabel;
    private JLabel customerLabel;
    private JLabel dateLabel;
    private JLabel subTotalLabel;
    private JLabel discountLabel;
    private JLabel taxLabel;
    private JLabel grandTotalLabel;
    
    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private final Font SECTION_FONT = new Font("Segoe UI Semibold", Font.BOLD, 18);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 15);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font PLACEHOLDER_FONT = new Font("Segoe UI", Font.ITALIC, 14);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private final Font SUMMARY_LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 14);
    private final Font SUMMARY_VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    // Button references
    private JButton searchButton;
    private JButton resetButton;
    private JButton viewInvoiceButton;
    private JButton printButton;
    private JButton exportPdfButton;
    private JButton closeButton;
    
    // Data structures for thread safety
    private final Object dataLock = new Object();
    private List<SalesRecord> salesRecords = new ArrayList<>();
    private List<InvoiceItem> currentInvoiceItems = new ArrayList<>();
    private String currentSelectedInvoice = null;
    
    // Data model classes
    private static class SalesRecord {
        String invoiceNo;
        String date;
        String customer;
        List<InvoiceItem> items;
        double subTotal;
        double discount;
        double tax;
        double grandTotal;
        int itemCount;
        
        SalesRecord(String invoiceNo, String date, String customer, List<InvoiceItem> items,
                   double subTotal, double discount, double tax, double grandTotal) {
            this.invoiceNo = invoiceNo;
            this.date = date;
            this.customer = customer;
            this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
            this.subTotal = subTotal;
            this.discount = discount;
            this.tax = tax;
            this.grandTotal = grandTotal;
            this.itemCount = items != null ? items.size() : 0;
        }
    }
    
    private static class InvoiceItem {
        String product;
        String category;
        double quantity;
        double price;
        double total;
        
        InvoiceItem(String product, String category, double quantity, double price, double total) {
            this.product = product;
            this.category = category;
            this.quantity = quantity;
            this.price = price;
            this.total = total;
        }
    }
    
    /* ================= CONSTRUCTOR ================= */
    public RecordSalesDialog(Window ownerFrame, JPanel contentPanel) {
        super(ownerFrame, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Initialize sample data
        initializeSampleData();
        
        // Main card panel
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setPreferredSize(new Dimension(1050, 780));
        
        /* ================= TOP BAR ================= */
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel title = new JLabel("Record Sales");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        topBar.add(title, BorderLayout.WEST);
        
        JButton closeBtn = createCloseButton();
        topBar.add(closeBtn, BorderLayout.EAST);
        
        card.add(topBar, BorderLayout.NORTH);
        
        /* ================= MAIN CONTENT PANEL ================= */
        JPanel mainPanel = createMainPanel();
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LIGHT_BLUE_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        card.add(scrollPane, BorderLayout.CENTER);
        
        // Add card to dialog
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(LIGHT_BLUE_BG);
        getContentPane().add(card);
        
        pack();
        setSize(1050, 780);
        
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
        setDefaultDates();
        
        // Load data on EDT
        SwingUtilities.invokeLater(() -> {
            loadSalesData();
            loadComboData();
            if (salesTableModel.getRowCount() > 0) {
                salesTable.setRowSelectionInterval(0, 0);
                updateInvoiceSummary();
            }
        });
    }
    
    /* ================= DATA INITIALIZATION ================= */
    private void initializeSampleData() {
        synchronized (dataLock) {
            salesRecords.clear();
            
            // Sample invoice items
            List<InvoiceItem> inv21Items = Arrays.asList(
                new InvoiceItem("Porcelain Tile White", "Tile", 40.00, 250.00, 10000.00),
                new InvoiceItem("Absolute Black Granite", "Granite", 20.00, 320.00, 6400.00),
                new InvoiceItem("Carrara White Marble", "Marble", 15.00, 130.00, 1950.00)
            );
            salesRecords.add(new SalesRecord("INV-0021", "12-Jan-26", "Ali", inv21Items, 
                16400.00, 820.00, 2770.00, 18350.00));
            
            List<InvoiceItem> inv22Items = Arrays.asList(
                new InvoiceItem("Ceramic Mosaic Pattern", "Tile", 30.00, 280.00, 8400.00),
                new InvoiceItem("Calacatta Gold Marble", "Marble", 25.00, 168.00, 4200.00)
            );
            salesRecords.add(new SalesRecord("INV-0022", "13-Jan-26", "Ahmad", inv22Items,
                12600.00, 630.00, 2130.00, 12600.00));
            
            List<InvoiceItem> inv23Items = Arrays.asList(
                new InvoiceItem("Uba Tuba Green", "Granite", 50.00, 124.00, 6200.00)
            );
            salesRecords.add(new SalesRecord("INV-0023", "13-Jan-26", "Walk-in", inv23Items,
                6200.00, 310.00, 1047.00, 6200.00));
            
            List<InvoiceItem> inv24Items = Arrays.asList(
                new InvoiceItem("Vitrified Floor Tile", "Tile", 60.00, 350.00, 21000.00),
                new InvoiceItem("Statuario Venato", "Marble", 20.00, 175.00, 3500.00)
            );
            salesRecords.add(new SalesRecord("INV-0024", "14-Jan-26", "Sara", inv24Items,
                24500.00, 1225.00, 4138.75, 24500.00));
            
            List<InvoiceItem> inv25Items = Arrays.asList(
                new InvoiceItem("Blue Pearl Granite", "Granite", 35.00, 320.00, 11200.00),
                new InvoiceItem("Porcelain Tile White", "Tile", 20.00, 230.00, 4600.00)
            );
            salesRecords.add(new SalesRecord("INV-0025", "15-Jan-26", "Kamran", inv25Items,
                15800.00, 790.00, 2669.00, 15800.00));
        }
    }
    
    private void loadComboData() {
        // Load categories
        Set<String> categories = new HashSet<>();
        categories.add("All");
        synchronized (dataLock) {
            for (SalesRecord record : salesRecords) {
                for (InvoiceItem item : record.items) {
                    if (item.category != null && !item.category.trim().isEmpty()) {
                        categories.add(item.category.trim());
                    }
                }
            }
        }
        
        if (categoryCombo != null && categories.size() > 0) {
            List<String> sortedCategories = new ArrayList<>(categories);
            Collections.sort(sortedCategories);
            categoryCombo.setModel(new DefaultComboBoxModel<>(sortedCategories.toArray(new String[0])));
            categoryCombo.setSelectedIndex(0);
        }
        
        // Load products
        Set<String> products = new HashSet<>();
        products.add("All");
        synchronized (dataLock) {
            for (SalesRecord record : salesRecords) {
                for (InvoiceItem item : record.items) {
                    if (item.product != null && !item.product.trim().isEmpty()) {
                        products.add(item.product.trim());
                    }
                }
            }
        }
        
        if (productCombo != null && products.size() > 0) {
            List<String> sortedProducts = new ArrayList<>(products);
            Collections.sort(sortedProducts);
            productCombo.setModel(new DefaultComboBoxModel<>(sortedProducts.toArray(new String[0])));
            productCombo.setSelectedIndex(0);
        }
    }
    
    /* ================= 1️⃣ TABLE SELECTION SAFETY ================= */
    private void updateInvoiceSummary() {
        SwingUtilities.invokeLater(() -> {
            int selectedRow = salesTable.getSelectedRow();
            if (selectedRow < 0 || selectedRow >= salesTableModel.getRowCount()) {
                // No row selected or invalid index - reset to default
                resetInvoiceSummary();
                return;
            }
            
            try {
                String invoiceNo = (String) salesTableModel.getValueAt(selectedRow, 0);
                String date = (String) salesTableModel.getValueAt(selectedRow, 1);
                String customer = (String) salesTableModel.getValueAt(selectedRow, 2);
                
                // Update invoice details
                invoiceNoLabel.setText(invoiceNo != null ? invoiceNo : "INV-0000");
                customerLabel.setText(customer != null ? customer : "Not selected");
                dateLabel.setText(date != null ? date : "Not selected");
                
                // Update invoice items
                updateInvoiceItems(invoiceNo);
            } catch (Exception e) {
                // Defensive: If anything goes wrong, reset summary
                resetInvoiceSummary();
                System.err.println("Error updating invoice summary: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void resetInvoiceSummary() {
        invoiceNoLabel.setText("INV-0000");
        customerLabel.setText("Not selected");
        dateLabel.setText("Not selected");
        
        // Clear items table
        itemsTableModel.setRowCount(0);
        currentInvoiceItems.clear();
        
        // Reset totals
        subTotalLabel.setText("0.00");
        discountLabel.setText("0.00");
        taxLabel.setText("0.00");
        grandTotalLabel.setText("0.00");
    }
    
    /* ================= 2️⃣ INVOICE SUMMARY CONSISTENCY ================= */
    private void updateInvoiceItems(String invoiceNo) {
        SwingUtilities.invokeLater(() -> {
            // Clear existing items
            itemsTableModel.setRowCount(0);
            currentInvoiceItems.clear();
            
            if (invoiceNo == null || invoiceNo.trim().isEmpty()) {
                return;
            }
            
            // Find the sales record for this invoice
            SalesRecord record = null;
            synchronized (dataLock) {
                for (SalesRecord sr : salesRecords) {
                    if (sr.invoiceNo.equals(invoiceNo)) {
                        record = sr;
                        break;
                    }
                }
            }
            
            if (record == null) {
                // Invoice not found - use safe defaults
                addDefaultInvoiceItems();
                return;
            }
            
            // Store current items for totals calculation
            currentInvoiceItems = new ArrayList<>(record.items);
            currentSelectedInvoice = invoiceNo;
            
            // Add items to table
            for (InvoiceItem item : record.items) {
                itemsTableModel.addRow(new Object[]{
                    item.product != null ? item.product : "Unknown",
                    item.category != null ? item.category : "Unknown",
                    formatNumber(item.quantity),
                    formatCurrency(item.price),
                    formatCurrency(item.total)
                });
            }
            
            // Update totals
            updateTotalsFromRecord(record);
        });
    }
    
    private void updateTotalsFromRecord(SalesRecord record) {
        // Ensure numeric consistency
        double subTotal = Math.max(0, record.subTotal);
        double discount = Math.max(0, Math.min(record.discount, subTotal)); // Discount can't exceed subtotal
        double tax = Math.max(0, record.tax);
        double grandTotal = Math.max(0, record.grandTotal);
        
        // Calculate expected grand total for verification
        double expectedGrandTotal = subTotal - discount + tax;
        
        // If there's a mismatch, log it and use calculated value
        if (Math.abs(grandTotal - expectedGrandTotal) > 0.01) {
            System.err.println("Invoice " + record.invoiceNo + 
                             ": Grand total mismatch. Recorded: " + grandTotal + 
                             ", Calculated: " + expectedGrandTotal);
            grandTotal = expectedGrandTotal;
        }
        
        // Update labels
        subTotalLabel.setText(formatCurrency(subTotal));
        discountLabel.setText(formatCurrency(discount));
        taxLabel.setText(formatCurrency(tax));
        grandTotalLabel.setText(formatCurrency(grandTotal));
    }
    
    private void addDefaultInvoiceItems() {
        // Safe default items for unknown invoices
        itemsTableModel.addRow(new Object[]{"Sample Product", "Tile", "10", "100.00", "1,000.00"});
        currentInvoiceItems.add(new InvoiceItem("Sample Product", "Tile", 10, 100.00, 1000.00));
        
        subTotalLabel.setText("1,000.00");
        discountLabel.setText("0.00");
        taxLabel.setText("0.00");
        grandTotalLabel.setText("1,000.00");
    }
    
    /* ================= 3️⃣ SEARCH LOGIC ================= */
    private void onSearch() {
        // Use SwingWorker for background search to prevent UI blocking
        new SwingWorker<List<SalesRecord>, Void>() {
            @Override
            protected List<SalesRecord> doInBackground() throws Exception {
                return performSearch();
            }
            
            @Override
            protected void done() {
                try {
                    List<SalesRecord> filtered = get();
                    updateSalesTable(filtered);
                    
                    if (filtered.isEmpty()) {
                        // No results found - safely reset everything
                        SwingUtilities.invokeLater(() -> {
                            salesTable.clearSelection();
                            resetInvoiceSummary();
                            JOptionPane.showMessageDialog(RecordSalesDialog.this,
                                "No sales records found matching the search criteria.",
                                "No Results",
                                JOptionPane.INFORMATION_MESSAGE);
                        });
                    } else {
                        // Select first row if available
                        SwingUtilities.invokeLater(() -> {
                            if (salesTableModel.getRowCount() > 0) {
                                salesTable.setRowSelectionInterval(0, 0);
                                updateInvoiceSummary();
                            }
                        });
                    }
                } catch (InterruptedException | ExecutionException e) {
                    // Handle search error
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(RecordSalesDialog.this,
                            "Error performing search: " + e.getMessage(),
                            "Search Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    
    private List<SalesRecord> performSearch() {
        List<SalesRecord> results = new ArrayList<>();
        
        try {
            // Safely read filter values
            String fromDate = getFilterTextSafely(fromDateField);
            String toDate = getFilterTextSafely(toDateField);
            String product = getComboValueSafely(productCombo);
            String category = getComboValueSafely(categoryCombo);
            String customer = getFilterTextSafely(customerField);
            
            // Parse dates if provided (safely)
            java.util.Date fromDateObj = null;
            java.util.Date toDateObj = null;
            
            if (!fromDate.isEmpty() && !fromDate.equals("dd-MMM-yyyy")) {
                try {
                    fromDateObj = new SimpleDateFormat("dd-MMM-yyyy").parse(fromDate);
                } catch (Exception e) {
                    System.err.println("Invalid from date format: " + fromDate);
                }
            }
            
            if (!toDate.isEmpty() && !toDate.equals("dd-MMM-yyyy")) {
                try {
                    toDateObj = new SimpleDateFormat("dd-MMM-yyyy").parse(toDate);
                } catch (Exception e) {
                    System.err.println("Invalid to date format: " + toDate);
                }
            }
            
            // Filter records
            synchronized (dataLock) {
                for (SalesRecord record : salesRecords) {
                    if (matchesFilter(record, fromDateObj, toDateObj, product, category, customer)) {
                        results.add(record);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error in search logic: " + e.getMessage());
            e.printStackTrace();
            // Return empty results on error
        }
        
        return results;
    }
    
    private boolean matchesFilter(SalesRecord record, java.util.Date fromDate, java.util.Date toDate, 
                                 String product, String category, String customer) {
        try {
            // Date filter
            if (fromDate != null || toDate != null) {
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MMM-yy");
                java.util.Date recordDate = displayFormat.parse(record.date);
                if (fromDate != null && recordDate.before(fromDate)) return false;
                if (toDate != null && recordDate.after(toDate)) return false;
            }
            
            // Product filter
            if (product != null && !product.equals("All") && !product.isEmpty()) {
                boolean productFound = false;
                for (InvoiceItem item : record.items) {
                    if (item.product != null && item.product.toLowerCase().contains(product.toLowerCase())) {
                        productFound = true;
                        break;
                    }
                }
                if (!productFound) return false;
            }
            
            // Category filter
            if (category != null && !category.equals("All") && !category.isEmpty()) {
                boolean categoryFound = false;
                for (InvoiceItem item : record.items) {
                    if (item.category != null && item.category.equalsIgnoreCase(category)) {
                        categoryFound = true;
                        break;
                    }
                }
                if (!categoryFound) return false;
            }
            
            // Customer filter
            if (customer != null && !customer.isEmpty() && !customer.equals("Search by Name / Phone")) {
                if (record.customer == null || !record.customer.toLowerCase().contains(customer.toLowerCase())) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            // If parsing fails, include the record to be safe
            System.err.println("Filter matching error for record: " + record.invoiceNo + " - " + e.getMessage());
            return true;
        }
    }
    
    private String getFilterTextSafely(JTextField field) {
        if (field == null) return "";
        String text = field.getText();
        if (text == null) return "";
        
        // Check if text is placeholder
        Color fg = field.getForeground();
        if (fg.equals(PLACEHOLDER_GRAY) || 
            text.equals("dd-MMM-yyyy") || 
            text.equals("Search by Name / Phone")) {
            return "";
        }
        
        return text.trim();
    }
    
    private String getComboValueSafely(JComboBox<String> combo) {
        if (combo == null) return "";
        Object value = combo.getSelectedItem();
        return value != null ? value.toString() : "";
    }
    
    private void updateSalesTable(List<SalesRecord> records) {
        SwingUtilities.invokeLater(() -> {
            salesTableModel.setRowCount(0);
            
            for (SalesRecord record : records) {
                salesTableModel.addRow(new Object[]{
                    record.invoiceNo,
                    record.date,
                    record.customer,
                    record.itemCount,
                    formatCurrency(record.grandTotal)
                });
            }
        });
    }
    
    /* ================= 4️⃣ RESET BEHAVIOR (Hard Reset) ================= */
    private void onReset() {
        // Reset is idempotent - can be called multiple times safely
        SwingUtilities.invokeLater(() -> {
            // Reset filter fields
            setDefaultDates();
            
            if (productCombo != null) {
                productCombo.setSelectedIndex(0);
            }
            
            if (categoryCombo != null) {
                categoryCombo.setSelectedIndex(0);
            }
            
            if (customerField != null) {
                customerField.setText("Search by Name / Phone");
                customerField.setForeground(PLACEHOLDER_GRAY);
            }
            
            // Reload all data
            initializeSampleData();
            loadSalesData();
            loadComboData();
            
            // Auto-select first row if available
            if (salesTableModel.getRowCount() > 0) {
                salesTable.setRowSelectionInterval(0, 0);
                updateInvoiceSummary();
            } else {
                resetInvoiceSummary();
            }
            
            JOptionPane.showMessageDialog(this,
                "Filters have been reset to default values.\n" +
                "Showing all " + salesRecords.size() + " sales records.",
                "Reset Complete",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    /* ================= 5️⃣ SAMPLE DATA ================= */
    private void loadSalesData() {
        SwingUtilities.invokeLater(() -> {
            salesTableModel.setRowCount(0);
            
            synchronized (dataLock) {
                for (SalesRecord record : salesRecords) {
                    salesTableModel.addRow(new Object[]{
                        record.invoiceNo,
                        record.date,
                        record.customer,
                        record.itemCount,
                        formatCurrency(record.grandTotal)
                    });
                }
            }
        });
    }
    
    /* ================= 6️⃣ CURRENCY & TOTALS INTEGRITY ================= */
    private String formatCurrency(double amount) {
        // Ensure amount is numeric and valid
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            amount = 0.0;
        }
        
        // Format with thousand separators and 2 decimal places
        return String.format("%,.2f", Math.max(0, amount));
    }
    
    private String formatNumber(double number) {
        // Format quantity with appropriate decimal places
        if (number == (int) number) {
            return String.format("%,d", (int) number);
        } else {
            return String.format("%,.2f", number);
        }
    }
    
    /* ================= 7️⃣ BUTTON BEHAVIOR GUARANTEES ================= */
    private void onViewInvoice() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= salesTableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this,
                "Please select an invoice to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String invoiceNo = (String) salesTableModel.getValueAt(selectedRow, 0);
            String customer = (String) salesTableModel.getValueAt(selectedRow, 2);
            Object totalObj = salesTableModel.getValueAt(selectedRow, 4);
            double total = 0.0;
            
            if (totalObj instanceof Double) {
                total = (Double) totalObj;
            } else if (totalObj instanceof String) {
                try {
                    total = Double.parseDouble(((String) totalObj).replace(",", ""));
                } catch (NumberFormatException e) {
                    total = 0.0;
                }
            }
            
            // Show invoice details
            StringBuilder details = new StringBuilder();
            details.append("📄 Invoice Details:\n\n");
            details.append("Invoice No: ").append(invoiceNo).append("\n");
            details.append("Customer: ").append(customer).append("\n");
            details.append("Total: PKR ").append(formatCurrency(total)).append("\n");
            details.append("Items: ").append(currentInvoiceItems.size()).append("\n\n");
            
            if (!currentInvoiceItems.isEmpty()) {
                details.append("Line Items:\n");
                for (InvoiceItem item : currentInvoiceItems) {
                    details.append("  • ").append(item.product)
                           .append(" (").append(item.category).append(") - ")
                           .append(formatNumber(item.quantity)).append(" units @ PKR ")
                           .append(formatCurrency(item.price)).append(" = PKR ")
                           .append(formatCurrency(item.total)).append("\n");
                }
            }
            
            JOptionPane.showMessageDialog(this,
                details.toString(),
                "View Invoice - " + invoiceNo,
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error displaying invoice details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onPrint() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= salesTableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this,
                "Please select an invoice to print.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String invoiceNo = (String) salesTableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Print invoice " + invoiceNo + "?\n\n" +
            "Make sure your printer is connected and ready.",
            "Confirm Print",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Simulate print job
            JOptionPane.showMessageDialog(this,
                "🖨️ Print job sent for invoice " + invoiceNo + "\n" +
                "Please check your printer for the output.",
                "Print Sent",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void onExportPDF() {
        int selectedRow = salesTable.getSelectedRow();
        
        if (selectedRow < 0 || selectedRow >= salesTableModel.getRowCount()) {
            // If no invoice selected, offer to export all
            int choice = JOptionPane.showConfirmDialog(this,
                "No specific invoice selected.\n" +
                "Would you like to export ALL sales records to PDF?",
                "Export All Records",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this,
                    "📊 Exporting ALL " + salesRecords.size() + " sales records to PDF...\n" +
                    "File will be saved as: Sales_Report_" + 
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".pdf",
                    "Export Started",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }
        
        String invoiceNo = (String) salesTableModel.getValueAt(selectedRow, 0);
        String[] options = {"Export This Invoice", "Export All Filtered Records", "Cancel"};
        
        int choice = JOptionPane.showOptionDialog(this,
            "Select export option for invoice " + invoiceNo + ":",
            "Export to PDF",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == 0) { // Export This Invoice
            JOptionPane.showMessageDialog(this,
                "📊 Exporting invoice " + invoiceNo + " to PDF...\n" +
                "File will be saved as: Invoice_" + invoiceNo + "_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".pdf",
                "Export Started",
                JOptionPane.INFORMATION_MESSAGE);
        } else if (choice == 1) { // Export All Filtered Records
            int recordCount = salesTableModel.getRowCount();
            JOptionPane.showMessageDialog(this,
                "📊 Exporting " + recordCount + " filtered records to PDF...\n" +
                "File will be saved as: Sales_Report_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".pdf",
                "Export Started",
                JOptionPane.INFORMATION_MESSAGE);
        }
        // Choice 2 is Cancel - do nothing
    }
    
    /* ================= REMAINING UI METHODS (unchanged) ================= */
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        JLabel sectionTitle = new JLabel("Filter Records");
        sectionTitle.setFont(SECTION_FONT);
        sectionTitle.setForeground(DARK_SKY_BLUE);
        panel.add(sectionTitle, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(15, 10, 15, 10);
        JPanel filterPanel = createFilterPanel();
        panel.add(filterPanel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 5, 10);
        JLabel salesTitle = new JLabel("Sales Records");
        salesTitle.setFont(SECTION_FONT);
        salesTitle.setForeground(DARK_SKY_BLUE);
        panel.add(salesTitle, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 15, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.6;
        JPanel salesTablePanel = createSalesTablePanel();
        panel.add(salesTablePanel, gbc);
        
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.insets = new Insets(20, 10, 5, 10);
        JLabel invoiceTitle = new JLabel("Selected Invoice Summary");
        invoiceTitle.setFont(SECTION_FONT);
        invoiceTitle.setForeground(DARK_SKY_BLUE);
        panel.add(invoiceTitle, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 10, 10);
        JPanel invoicePanel = createInvoiceSummaryPanel();
        panel.add(invoicePanel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JPanel actionPanel = createActionButtonsPanel();
        panel.add(actionPanel, gbc);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createFilterLabel("From Date:"), gbc);
        
        gbc.gridx = 1;
        fromDateField = createStyledTextField("dd-MMM-yyyy", false);
        fromDateField.setPreferredSize(new Dimension(180, 42));
        panel.add(fromDateField, gbc);
        
        gbc.gridx = 2;
        panel.add(createFilterLabel("To Date:"), gbc);
        
        gbc.gridx = 3;
        toDateField = createStyledTextField("dd-MMM-yyyy", false);
        toDateField.setPreferredSize(new Dimension(180, 42));
        panel.add(toDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createFilterLabel("Product:"), gbc);
        
        gbc.gridx = 1;
        String[] products = {"All"}; // Will be populated from data
        productCombo = createStyledComboBox(products, "Select Product");
        productCombo.setPreferredSize(new Dimension(180, 42));
        panel.add(productCombo, gbc);
        
        gbc.gridx = 2;
        panel.add(createFilterLabel("Category:"), gbc);
        
        gbc.gridx = 3;
        String[] categories = {"All"}; // Will be populated from data
        categoryCombo = createStyledComboBox(categories, "Select Category");
        categoryCombo.setPreferredSize(new Dimension(180, 42));
        panel.add(categoryCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createFilterLabel("Customer:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        customerField = createStyledTextField("Search by Name / Phone", false);
        customerField.setPreferredSize(new Dimension(650, 42));
        panel.add(customerField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 3; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        searchButton = createFilterButton("🔍 Search", SKY_BLUE, DARK_SKY_BLUE);
        searchButton.addActionListener(e -> onSearch());
        panel.add(searchButton, gbc);
        
        gbc.gridx = 3;
        resetButton = createFilterButton("🔄 Reset", new Color(150, 150, 150), HOVER_RED);
        resetButton.addActionListener(e -> onReset());
        panel.add(resetButton, gbc);
        
        return panel;
    }
    
    private JPanel createSalesTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        String[] columns = {"Invoice No", "Date", "Customer", "Items", "Total (PKR)"};
        salesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;
                    case 1: return String.class;
                    case 2: return String.class;
                    case 3: return Integer.class;
                    case 4: return Double.class;
                    default: return Object.class;
                }
            }
        };
        
        salesTable = new JTable(salesTableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    
                    if (isRowSelected(row)) {
                        label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(DARK_SKY_BLUE, 2),
                            BorderFactory.createEmptyBorder(2, 4, 2, 4)
                        ));
                        label.setBackground(TABLE_SELECTION);
                        label.setForeground(TEXT_BLACK);
                    } else {
                        label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(240, 242, 245), 1),
                            BorderFactory.createEmptyBorder(3, 5, 3, 5)
                        ));
                        
                        if (row % 2 == 0) {
                            label.setBackground(Color.WHITE);
                        } else {
                            label.setBackground(new Color(248, 250, 252));
                        }
                        label.setForeground(TEXT_BLACK);
                    }
                    
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                }
                
                return c;
            }
        };
        
        salesTable.setFont(TABLE_FONT);
        salesTable.setRowHeight(38);
        salesTable.setShowGrid(true);
        salesTable.setGridColor(new Color(220, 225, 230));
        salesTable.setSelectionBackground(TABLE_SELECTION);
        salesTable.setSelectionForeground(TEXT_BLACK);
        salesTable.setForeground(TEXT_BLACK);
        salesTable.setFillsViewportHeight(true);
        salesTable.setIntercellSpacing(new Dimension(1, 1));
        
        salesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateInvoiceSummary();
            }
        });
        
        JTableHeader header = salesTable.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_BLACK);
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
        
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        salesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        salesTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < salesTable.getColumnCount(); i++) {
            salesTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        styleScrollPane(scrollPane);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInvoiceSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 200));
        
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        detailsPanel.setPreferredSize(new Dimension(220, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 10, 15);
        
        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(createSummaryLabel("Invoice No:"), gbc);
        
        gbc.gridx = 1;
        invoiceNoLabel = createSummaryValueLabel("INV-0000");
        invoiceNoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        detailsPanel.add(invoiceNoLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        detailsPanel.add(createSummaryLabel("Customer:"), gbc);
        
        gbc.gridx = 1;
        customerLabel = createSummaryValueLabel("Not selected");
        customerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        detailsPanel.add(customerLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(createSummaryLabel("Date:"), gbc);
        
        gbc.gridx = 1;
        dateLabel = createSummaryValueLabel("Not selected");
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        detailsPanel.add(dateLabel, gbc);
        
        panel.add(detailsPanel, BorderLayout.WEST);
        
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setOpaque(false);
        itemsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        String[] itemColumns = {"Product", "Category", "Qty (sqft)", "Price", "Total"};
        itemsTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        invoiceItemsTable = new JTable(itemsTableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    
                    if (isRowSelected(row)) {
                        label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(DARK_SKY_BLUE, 2),
                            BorderFactory.createEmptyBorder(2, 4, 2, 4)
                        ));
                        label.setBackground(TABLE_SELECTION);
                        label.setForeground(TEXT_BLACK);
                    } else {
                        label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(240, 242, 245), 1),
                            BorderFactory.createEmptyBorder(3, 5, 3, 5)
                        ));
                        
                        if (row % 2 == 0) {
                            label.setBackground(Color.WHITE);
                        } else {
                            label.setBackground(new Color(248, 250, 252));
                        }
                        label.setForeground(TEXT_BLACK);
                    }
                    
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                }
                
                return c;
            }
        };
        
        invoiceItemsTable.setFont(TABLE_FONT);
        invoiceItemsTable.setRowHeight(35);
        invoiceItemsTable.setShowGrid(true);
        invoiceItemsTable.setGridColor(new Color(220, 225, 230));
        invoiceItemsTable.setSelectionBackground(TABLE_SELECTION);
        invoiceItemsTable.setSelectionForeground(TEXT_BLACK);
        invoiceItemsTable.setFillsViewportHeight(true);
        
        JTableHeader itemsHeader = invoiceItemsTable.getTableHeader();
        itemsHeader.setFont(TABLE_HEADER_FONT);
        itemsHeader.setBackground(TABLE_HEADER_BG);
        itemsHeader.setForeground(TEXT_BLACK);
        itemsHeader.setReorderingAllowed(false);
        
        itemsHeader.setDefaultRenderer(new DefaultTableCellRenderer() {
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
        
        invoiceItemsTable.getColumnModel().getColumn(0).setPreferredWidth(180);
        invoiceItemsTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        invoiceItemsTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        invoiceItemsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        invoiceItemsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < invoiceItemsTable.getColumnCount(); i++) {
            invoiceItemsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane itemsScroll = new JScrollPane(invoiceItemsTable);
        itemsScroll.setBorder(null);
        styleScrollPane(itemsScroll);
        
        itemsPanel.add(itemsScroll, BorderLayout.CENTER);
        
        panel.add(itemsPanel, BorderLayout.CENTER);
        
        JPanel totalsPanel = new JPanel(new GridBagLayout());
        totalsPanel.setOpaque(false);
        totalsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        totalsPanel.setPreferredSize(new Dimension(200, 0));
        
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 8, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        totalsPanel.add(createSummaryLabel("Sub Total:"), gbc);
        
        gbc.gridx = 1;
        subTotalLabel = createSummaryValueLabel("0.00");
        subTotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalsPanel.add(subTotalLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        totalsPanel.add(createSummaryLabel("Discount:"), gbc);
        
        gbc.gridx = 1;
        discountLabel = createSummaryValueLabel("0.00");
        discountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalsPanel.add(discountLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        totalsPanel.add(createSummaryLabel("Tax:"), gbc);
        
        gbc.gridx = 1;
        taxLabel = createSummaryValueLabel("0.00");
        taxLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalsPanel.add(taxLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER_COLOR);
        totalsPanel.add(separator, gbc);
        
        gbc.gridy = 4; gbc.gridwidth = 1;
        JLabel grandTotalText = createSummaryLabel("Grand Total:");
        grandTotalText.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        grandTotalText.setForeground(DARK_SKY_BLUE);
        totalsPanel.add(grandTotalText, gbc);
        
        gbc.gridx = 1;
        grandTotalLabel = createSummaryValueLabel("0.00");
        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        grandTotalLabel.setForeground(DARK_SKY_BLUE);
        grandTotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalsPanel.add(grandTotalLabel, gbc);
        
        panel.add(totalsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createActionButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        viewInvoiceButton = createActionButton("📄 View Invoice", SKY_BLUE, DARK_SKY_BLUE);
        viewInvoiceButton.addActionListener(e -> onViewInvoice());
        
        printButton = createActionButton("🖨️ Print", ORANGE_BUTTON, ORANGE_HOVER);
        printButton.addActionListener(e -> onPrint());
        
        exportPdfButton = createActionButton("📊 Export PDF", GREEN_BUTTON, GREEN_HOVER);
        exportPdfButton.addActionListener(e -> onExportPDF());
        
        panel.add(viewInvoiceButton);
        panel.add(printButton);
        panel.add(exportPdfButton);
        
        return panel;
    }
    
    // Helper UI methods (unchanged from original)
    private JLabel createFilterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_BLACK);
        label.setPreferredSize(new Dimension(100, 35));
        return label;
    }
    
    private JLabel createSummaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUMMARY_LABEL_FONT);
        label.setForeground(TEXT_BLACK);
        return label;
    }
    
    private JLabel createSummaryValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUMMARY_VALUE_FONT);
        label.setForeground(TEXT_BLACK);
        return label;
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
    
    private JButton createFilterButton(String text, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(getBackground().darker());
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 42));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
    
    private JButton createActionButton(String text, Color normalColor, Color hoverColor) {
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
        
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 48));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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
    
    private JTextField createStyledTextField(String placeholder, boolean readOnly) {
        return new JTextField() {
            private boolean showingPlaceholder = !readOnly;
            private String placeholderText = placeholder;
            
            {
                setFont(INPUT_FONT);
                setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                setBackground(INPUT_BG);
                setForeground(readOnly ? new Color(80, 80, 80) : PLACEHOLDER_GRAY);
                setText(placeholder);
                setCaretColor(DARK_SKY_BLUE);
                setEditable(!readOnly);
                
                if (!readOnly) {
                    addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            if (showingPlaceholder) {
                                setText("");
                                setForeground(TEXT_BLACK);
                                showingPlaceholder = false;
                            }
                        }
                        
                        @Override
                        public void focusLost(FocusEvent e) {
                            if (getText().isEmpty()) {
                                setText(placeholderText);
                                setForeground(PLACEHOLDER_GRAY);
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(new Color(200, 220, 240));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
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
                setForeground(PLACEHOLDER_GRAY);
                setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value,
                            int index, boolean isSelected, boolean cellHasFocus) {
                        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (index == -1 && value.toString().startsWith("Select")) {
                            setForeground(PLACEHOLDER_GRAY);
                        } else if (isSelected) {
                            setBackground(TABLE_SELECTION);
                            setForeground(TEXT_BLACK);
                        } else {
                            setBackground(INPUT_BG);
                            setForeground(TEXT_BLACK);
                        }
                        return c;
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(new Color(200, 220, 240));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                g2.setColor(DARK_SKY_BLUE);
                int[] xPoints = {getWidth() - 22, getWidth() - 14, getWidth() - 18};
                int[] yPoints = {getHeight()/2 - 3, getHeight()/2 - 3, getHeight()/2 + 3};
                g2.fillPolygon(xPoints, yPoints, 3);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
    
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setPreferredSize(new Dimension(10, 0));
        vertical.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100);
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
    
    private void setDefaultDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String currentDate = sdf.format(new java.util.Date());
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String firstDayStr = sdf.format(cal.getTime());
        
        if (fromDateField != null) {
            fromDateField.setText(firstDayStr);
            fromDateField.setForeground(TEXT_BLACK);
        }
        
        if (toDateField != null) {
            toDateField.setText(currentDate);
            toDateField.setForeground(TEXT_BLACK);
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
    
    /* ================= MAIN METHOD FOR TESTING ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Test Record Sales Dialog");
            frame.setSize(1200, 850);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JPanel contentPanel = new JPanel();
            contentPanel.setBackground(new Color(240, 248, 255));
            frame.setContentPane(contentPanel);
            
            JButton testBtn = new JButton("Open Record Sales Dialog");
            testBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            testBtn.setBackground(new Color(30, 144, 255));
            testBtn.setForeground(Color.WHITE);
            testBtn.setFocusPainted(false);
            testBtn.setPreferredSize(new Dimension(300, 50));
            testBtn.addActionListener(e -> {
                RecordSalesDialog dlg = new RecordSalesDialog(frame, contentPanel);
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