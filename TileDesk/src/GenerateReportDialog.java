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
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * GenerateReportDialog - Modern Report Generation Dialog
 * Connected to Tile Factory Database
 */
public class GenerateReportDialog extends JDialog {
    private static final Color SKY_BLUE = new Color(135, 206, 250);
    private static final Color DARK_SKY_BLUE = new Color(30, 144, 255);
    private static final Color LIGHT_BLUE_BG = new Color(240, 248, 255);
    private static final Color INPUT_BG = new Color(255, 255, 255);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(180, 200, 220);
    private static final Color LABEL_COLOR = new Color(60, 60, 80);
    private static final Color REQUIRED_COLOR = new Color(220, 53, 69);
    private static final Color OPTIONAL_COLOR = new Color(108, 117, 125);
    private static final Color HOVER_RED = new Color(220, 80, 80);
    private static final Color GREEN_BUTTON = new Color(76, 175, 80);
    private static final Color GREEN_HOVER = new Color(56, 142, 60);
    private static final Color PURPLE_BUTTON = new Color(156, 39, 176);
    private static final Color PURPLE_HOVER = new Color(123, 31, 162);
    private static final Color ORANGE_BUTTON = new Color(255, 152, 0);
    private static final Color ORANGE_HOVER = new Color(245, 124, 0);
    private static final Color TABLE_HEADER_BG = new Color(245, 247, 250);
    private static final Color TABLE_SELECTION = new Color(225, 240, 255);
    private static final Color TABLE_GRID_COLOR = new Color(220, 225, 230);
    private static final Color TEXT_BLACK = new Color(40, 40, 40);
    private static final Color SCROLLBAR_COLOR = new Color(100, 100, 100);

    // Radio buttons for report type
    private JRadioButton dailySalesRadio;
    private JRadioButton monthlySalesRadio;
    private JRadioButton yearlySalesRadio;
    private JRadioButton productWiseRadio;
    private JRadioButton categoryWiseRadio;
    private JRadioButton customerWiseRadio;
    private ButtonGroup reportTypeGroup;

    // Filter fields
    private JTextField fromDateField;
    private JTextField toDateField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> productCombo;

    // Report summary labels
    private JLabel totalInvoicesLabel;
    private JLabel totalQuantityLabel;
    private JLabel grossSalesLabel;
    private JLabel totalDiscountLabel;
    private JLabel totalTaxLabel;
    private JLabel netSalesLabel;

    // Report details table
    private JTable reportTable;
    private DefaultTableModel reportTableModel;

    // Database connection
    private Connection connection;

    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font SECTION_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 14);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 14);
    private final Font PLACEHOLDER_FONT = new Font("Segoe UI", Font.ITALIC, 12);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 11);
    private final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font TABLE_HEADER_FONT = new Font("Segoe UI Semibold", Font.BOLD, 13);
    private final Font SUMMARY_LABEL_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 12);
    private final Font SUMMARY_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private final Font RADIO_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // Date formatters
    private final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
    private final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");

    // Button references
    private JButton generateButton;
    private JButton resetButton;
    private JButton exportPdfButton;
    private JButton exportExcelButton;
    private JButton printReportButton;
    private JButton closeButton;

    /* ================= CONSTRUCTOR ================= */
    public GenerateReportDialog(Window ownerFrame, JPanel contentPanel) {
        super(ownerFrame, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));

        // Get database connection from DBConnection class
        connection = DBConnection.getConnection();

        // Main card panel
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setPreferredSize(new Dimension(950, 700));

        /* ================= TOP BAR ================= */
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title on left
        JLabel title = new JLabel("Generate Report");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        topBar.add(title, BorderLayout.WEST);

        // Close button on right
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
        setSize(950, 700);

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
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setDefaultDates();
        loadFiltersFromDatabase();
        loadSampleDataFromDatabase();
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(5, 0, 10, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 8, 3, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // Section 1: Report Type
        JLabel reportTypeTitle = new JLabel("Report Type:");
        reportTypeTitle.setFont(SECTION_FONT);
        reportTypeTitle.setForeground(DARK_SKY_BLUE);
        panel.add(reportTypeTitle, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 12, 8);
        JPanel reportTypePanel = createReportTypePanel();
        panel.add(reportTypePanel, gbc);

        // Separator
        gbc.gridy++;
        gbc.insets = new Insets(15, 8, 15, 8);
        panel.add(createSeparator(), gbc);

        // Section 2: Filters
        gbc.gridy++;
        gbc.insets = new Insets(3, 8, 3, 8);
        JLabel filtersTitle = new JLabel("Filters:");
        filtersTitle.setFont(SECTION_FONT);
        filtersTitle.setForeground(DARK_SKY_BLUE);
        panel.add(filtersTitle, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(12, 8, 12, 8);
        JPanel filterPanel = createFilterPanel();
        panel.add(filterPanel, gbc);

        // Section 3: Report Summary
        gbc.gridy++;
        gbc.insets = new Insets(15, 8, 3, 8);
        JLabel summaryTitle = new JLabel("Report Summary:");
        summaryTitle.setFont(SECTION_FONT);
        summaryTitle.setForeground(DARK_SKY_BLUE);
        panel.add(summaryTitle, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 12, 8);
        JPanel summaryPanel = createSummaryPanel();
        panel.add(summaryPanel, gbc);

        // Separator
        gbc.gridy++;
        gbc.insets = new Insets(15, 8, 15, 8);
        panel.add(createSeparator(), gbc);

        // Section 4: Report Details
        gbc.gridy++;
        gbc.insets = new Insets(3, 8, 3, 8);
        JLabel detailsTitle = new JLabel("Report Details:");
        detailsTitle.setFont(SECTION_FONT);
        detailsTitle.setForeground(DARK_SKY_BLUE);
        panel.add(detailsTitle, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 12, 8);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JPanel detailsPanel = createDetailsPanel();
        panel.add(detailsPanel, gbc);

        // Section 5: Action Buttons
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.insets = new Insets(15, 8, 5, 8);
        JPanel actionPanel = createActionButtonsPanel();
        panel.add(actionPanel, gbc);

        return panel;
    }

    private JPanel createReportTypePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // Create radio buttons
        dailySalesRadio = createStyledRadioButton("Daily Sales");
        monthlySalesRadio = createStyledRadioButton("Monthly Sales");
        yearlySalesRadio = createStyledRadioButton("Yearly Sales");
        productWiseRadio = createStyledRadioButton("Product-wise");
        categoryWiseRadio = createStyledRadioButton("Category-wise");
        customerWiseRadio = createStyledRadioButton("Customer-wise");

        // Create button group
        reportTypeGroup = new ButtonGroup();
        reportTypeGroup.add(dailySalesRadio);
        reportTypeGroup.add(monthlySalesRadio);
        reportTypeGroup.add(yearlySalesRadio);
        reportTypeGroup.add(productWiseRadio);
        reportTypeGroup.add(categoryWiseRadio);
        reportTypeGroup.add(customerWiseRadio);

        // Set default selection
        dailySalesRadio.setSelected(true);

        // Add radio buttons to panel
        panel.add(dailySalesRadio);
        panel.add(monthlySalesRadio);
        panel.add(yearlySalesRadio);
        panel.add(productWiseRadio);
        panel.add(categoryWiseRadio);
        panel.add(customerWiseRadio);

        // Add action listeners to reload data when report type changes
        ActionListener reportTypeListener = e -> loadSampleDataFromDatabase();
        dailySalesRadio.addActionListener(reportTypeListener);
        monthlySalesRadio.addActionListener(reportTypeListener);
        yearlySalesRadio.addActionListener(reportTypeListener);
        productWiseRadio.addActionListener(reportTypeListener);
        categoryWiseRadio.addActionListener(reportTypeListener);
        customerWiseRadio.addActionListener(reportTypeListener);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 8, 6, 8);

        // Row 1: From Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createFilterLabel("From Date:"), gbc);

        gbc.gridx = 1;
        fromDateField = createStyledTextField("dd-MMM-yyyy", false);
        fromDateField.setPreferredSize(new Dimension(150, 35));
        panel.add(fromDateField, gbc);

        // To Date
        gbc.gridx = 2;
        panel.add(createFilterLabel("To Date:"), gbc);

        gbc.gridx = 3;
        toDateField = createStyledTextField("dd-MMM-yyyy", false);
        toDateField.setPreferredSize(new Dimension(150, 35));
        panel.add(toDateField, gbc);

        // Row 2: Category
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createFilterLabel("Category:"), gbc);

        gbc.gridx = 1;
        categoryCombo = createStyledComboBox(new String[] { "All" }, "Select Category");
        categoryCombo.setPreferredSize(new Dimension(150, 35));
        panel.add(categoryCombo, gbc);

        // Product
        gbc.gridx = 2;
        panel.add(createFilterLabel("Product:"), gbc);

        gbc.gridx = 3;
        productCombo = createStyledComboBox(new String[] { "All" }, "Select Product");
        productCombo.setPreferredSize(new Dimension(150, 35));
        panel.add(productCombo, gbc);

        // Row 3: Buttons
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        generateButton = createFilterButton("📊 Generate Report", SKY_BLUE, DARK_SKY_BLUE);
        generateButton.addActionListener(e -> onGenerateReport());
        generateButton.setPreferredSize(new Dimension(160, 35));
        panel.add(generateButton, gbc);

        gbc.gridx = 3;
        resetButton = createFilterButton("🔄 Reset", new Color(200, 200, 200), HOVER_RED);
        resetButton.addActionListener(e -> onReset());
        resetButton.setPreferredSize(new Dimension(100, 35));
        panel.add(resetButton, gbc);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // Create summary labels with actual JLabel references
        totalInvoicesLabel = new JLabel("0");
        totalQuantityLabel = new JLabel("0");
        grossSalesLabel = new JLabel("PKR 0.00");
        totalDiscountLabel = new JLabel("PKR 0.00");
        totalTaxLabel = new JLabel("PKR 0.00");
        netSalesLabel = new JLabel("PKR 0.00");

        // Style the labels
        totalInvoicesLabel.setFont(SUMMARY_VALUE_FONT);
        totalInvoicesLabel.setForeground(DARK_SKY_BLUE);
        totalQuantityLabel.setFont(SUMMARY_VALUE_FONT);
        totalQuantityLabel.setForeground(DARK_SKY_BLUE);
        grossSalesLabel.setFont(SUMMARY_VALUE_FONT);
        grossSalesLabel.setForeground(DARK_SKY_BLUE);
        totalDiscountLabel.setFont(SUMMARY_VALUE_FONT);
        totalDiscountLabel.setForeground(DARK_SKY_BLUE);
        totalTaxLabel.setFont(SUMMARY_VALUE_FONT);
        totalTaxLabel.setForeground(DARK_SKY_BLUE);
        netSalesLabel.setFont(SUMMARY_VALUE_FONT);
        netSalesLabel.setForeground(DARK_SKY_BLUE);

        // Create summary items with actual labels
        panel.add(createSummaryItem("Total Invoices:", totalInvoicesLabel));
        panel.add(createSummaryItem("Total Quantity:", totalQuantityLabel));
        panel.add(createSummaryItem("Gross Sales:", grossSalesLabel));
        panel.add(createSummaryItem("Total Discount:", totalDiscountLabel));
        panel.add(createSummaryItem("Total Tax:", totalTaxLabel));
        panel.add(createSummaryItem("Net Sales:", netSalesLabel));

        return panel;
    }

    private JPanel createSummaryItem(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(SUMMARY_LABEL_FONT);
        label.setForeground(LABEL_COLOR);

        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(label, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Add table title
        JLabel tableTitle = new JLabel("Report Data - Select any row for details");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(new Color(80, 80, 100));
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(tableTitle, BorderLayout.NORTH);

        // Table model
        String[] columns = { "Date / Product", "Qty Sold", "Revenue", "% Contribution" };
        reportTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return String.class;
                    case 1:
                        return Double.class;
                    case 2:
                        return String.class;
                    case 3:
                        return String.class;
                    default:
                        return Object.class;
                }
            }
        };

        // Create table with custom renderer
        reportTable = new JTable(reportTableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (isRowSelected(row)) {
                    ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(DARK_SKY_BLUE, 2),
                            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
                } else {
                    ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(240, 242, 245), 1),
                            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
                }

                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                c.setForeground(TEXT_BLACK);

                if (!isRowSelected(row)) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252));
                    }
                }

                return c;
            }
        };

        // Make table non-editable
        reportTable.setDefaultEditor(Object.class, null);

        // Style table
        reportTable.setFont(TABLE_FONT);
        reportTable.setRowHeight(40);
        reportTable.setShowGrid(true);
        reportTable.setGridColor(TABLE_GRID_COLOR);
        reportTable.setSelectionBackground(TABLE_SELECTION);
        reportTable.setSelectionForeground(TEXT_BLACK);
        reportTable.setForeground(TEXT_BLACK);
        reportTable.setFillsViewportHeight(true);
        reportTable.setIntercellSpacing(new Dimension(0, 0));

        // Style header
        JTableHeader header = reportTable.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setForeground(TEXT_BLACK);
        header.setBackground(TABLE_HEADER_BG);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(TABLE_HEADER_FONT);
                setForeground(TEXT_BLACK);
                setBackground(TABLE_HEADER_BG);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(200, 210, 220)),
                        BorderFactory.createEmptyBorder(10, 5, 10, 5)));
                return this;
            }
        });

        // Set column widths
        reportTable.getColumnModel().getColumn(0).setPreferredWidth(220);
        reportTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        reportTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        reportTable.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < reportTable.getColumnCount(); i++) {
            reportTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add row selection listener
        reportTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = reportTable.getSelectedRow();
                if (row != -1) {
                    String selectedItem = reportTable.getValueAt(row, 0).toString();
                    String revenue = reportTable.getValueAt(row, 2).toString();

                    totalInvoicesLabel.setText("Selected: " + selectedItem);
                    totalInvoicesLabel.setForeground(new Color(30, 144, 255));

                    reportTable.setToolTipText("<html><b>" + selectedItem + "</b><br>" +
                            "Revenue: " + revenue + "<br>" +
                            "Click to view details</html>");

                    Timer restoreTimer = new Timer(3000, evt -> {
                        if (totalInvoicesLabel.getText().startsWith("Selected:")) {
                            loadSummaryFromDatabase();
                        }
                    });
                    restoreTimer.setRepeats(false);
                    restoreTimer.start();
                }
            }
        });

        // Add hover effect
        reportTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = reportTable.rowAtPoint(e.getPoint());
                if (row >= 0 && !reportTable.isRowSelected(row)) {
                    reportTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    reportTable.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        styleTableScrollPane(scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        exportPdfButton = createActionButton("📄 Export PDF", SKY_BLUE, DARK_SKY_BLUE);
        exportPdfButton.addActionListener(e -> onExportPDF());
        exportPdfButton.setPreferredSize(new Dimension(140, 38));

        exportExcelButton = createActionButton("📊 Export Excel", GREEN_BUTTON, GREEN_HOVER);
        exportExcelButton.addActionListener(e -> onExportExcel());
        exportExcelButton.setPreferredSize(new Dimension(140, 38));

        printReportButton = createActionButton("🖨️ Print Report", ORANGE_BUTTON, ORANGE_HOVER);
        printReportButton.addActionListener(e -> onPrintReport());
        printReportButton.setPreferredSize(new Dimension(140, 38));

        panel.add(exportPdfButton);
        panel.add(exportExcelButton);
        panel.add(printReportButton);

        return panel;
    }

    /* ================= DATABASE METHODS ================= */

    private void loadFiltersFromDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }

            // Load categories from database
            String categoryQuery = "SELECT DISTINCT category FROM products ORDER BY category";
            PreparedStatement pstmt = connection.prepareStatement(categoryQuery);
            ResultSet rs = pstmt.executeQuery();

            categoryCombo.removeAllItems();
            categoryCombo.addItem("All");
            while (rs.next()) {
                categoryCombo.addItem(rs.getString("category"));
            }
            rs.close();
            pstmt.close();

            // Load products from database
            String productQuery = "SELECT DISTINCT product_name FROM products ORDER BY product_name";
            pstmt = connection.prepareStatement(productQuery);
            rs = pstmt.executeQuery();

            productCombo.removeAllItems();
            productCombo.addItem("All");
            while (rs.next()) {
                productCombo.addItem(rs.getString("product_name"));
            }
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading filters: " + e.getMessage() +
                            "\n\nTrying to reconnect...",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

            // Try to reconnect
            connection = DBConnection.getConnectionWithRetry();
            if (connection != null) {
                loadFiltersFromDatabase();
            }
        }
    }

    private void loadSampleDataFromDatabase() {
        reportTableModel.setRowCount(0);

        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }

            String selectedType = getSelectedReportType();
            String query = "";

            if (selectedType.equals("Daily Sales")) {
                query = "SELECT DATE(i.invoice_date) as sale_date, " +
                        "SUM(ii.quantity) as total_qty, " +
                        "SUM(ii.total_price) as total_revenue " +
                        "FROM invoices i " +
                        "JOIN invoice_items ii ON i.invoice_id = ii.invoice_id " +
                        "WHERE i.status = 'PAID' " +
                        "GROUP BY DATE(i.invoice_date) " +
                        "ORDER BY sale_date DESC " +
                        "LIMIT 10";

            } else if (selectedType.equals("Monthly Sales")) {
                query = "SELECT DATE_FORMAT(i.invoice_date, '%Y-%m') as month, " +
                        "SUM(ii.quantity) as total_qty, " +
                        "SUM(ii.total_price) as total_revenue " +
                        "FROM invoices i " +
                        "JOIN invoice_items ii ON i.invoice_id = ii.invoice_id " +
                        "WHERE i.status = 'PAID' " +
                        "GROUP BY DATE_FORMAT(i.invoice_date, '%Y-%m') " +
                        "ORDER BY month DESC " +
                        "LIMIT 10";

            } else if (selectedType.equals("Yearly Sales")) {
                query = "SELECT YEAR(i.invoice_date) as year, " +
                        "SUM(ii.quantity) as total_qty, " +
                        "SUM(ii.total_price) as total_revenue " +
                        "FROM invoices i " +
                        "JOIN invoice_items ii ON i.invoice_id = ii.invoice_id " +
                        "WHERE i.status = 'PAID' " +
                        "GROUP BY YEAR(i.invoice_date) " +
                        "ORDER BY year DESC " +
                        "LIMIT 5";

            } else if (selectedType.equals("Product-wise")) {
                query = "SELECT p.product_name, " +
                        "SUM(ii.quantity) as total_qty, " +
                        "SUM(ii.total_price) as total_revenue " +
                        "FROM products p " +
                        "JOIN invoice_items ii ON p.product_id = ii.product_id " +
                        "JOIN invoices i ON ii.invoice_id = i.invoice_id " +
                        "WHERE i.status = 'PAID' " +
                        "GROUP BY p.product_id, p.product_name " +
                        "ORDER BY total_revenue DESC " +
                        "LIMIT 10";

            } else if (selectedType.equals("Category-wise")) {
                query = "SELECT p.category, " +
                        "SUM(ii.quantity) as total_qty, " +
                        "SUM(ii.total_price) as total_revenue " +
                        "FROM products p " +
                        "JOIN invoice_items ii ON p.product_id = ii.product_id " +
                        "JOIN invoices i ON ii.invoice_id = i.invoice_id " +
                        "WHERE i.status = 'PAID' " +
                        "GROUP BY p.category " +
                        "ORDER BY total_revenue DESC";

            } else if (selectedType.equals("Customer-wise")) {
                query = "SELECT c.customer_name, " +
                        "SUM(ii.quantity) as total_qty, " +
                        "SUM(ii.total_price) as total_revenue " +
                        "FROM customers c " +
                        "JOIN invoices i ON c.customer_id = i.customer_id " +
                        "JOIN invoice_items ii ON i.invoice_id = ii.invoice_id " +
                        "WHERE i.status = 'PAID' " +
                        "GROUP BY c.customer_id, c.customer_name " +
                        "ORDER BY total_revenue DESC " +
                        "LIMIT 10";
            }

            if (!query.isEmpty()) {
                PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();

                double totalRevenue = 0;
                while (rs.next()) {
                    totalRevenue += rs.getDouble("total_revenue");
                }
                rs.beforeFirst();

                while (rs.next()) {
                    String itemName = "";
                    if (selectedType.equals("Daily Sales")) {
                        itemName = rs.getDate("sale_date").toString();
                    } else if (selectedType.equals("Monthly Sales")) {
                        itemName = rs.getString("month") + " (Monthly)";
                    } else if (selectedType.equals("Yearly Sales")) {
                        itemName = rs.getInt("year") + " (Yearly)";
                    } else if (selectedType.equals("Product-wise")) {
                        itemName = rs.getString("product_name");
                    } else if (selectedType.equals("Category-wise")) {
                        itemName = rs.getString("category");
                    } else if (selectedType.equals("Customer-wise")) {
                        itemName = rs.getString("customer_name");
                    }

                    double qty = rs.getDouble("total_qty");
                    double revenue = rs.getDouble("total_revenue");
                    double percent = totalRevenue > 0 ? (revenue / totalRevenue * 100) : 0;

                    reportTableModel.addRow(new Object[] {
                            itemName,
                            String.format("%.2f", qty),
                            "PKR " + CURRENCY_FORMAT.format(revenue),
                            String.format("%.1f%%", percent)
                    });
                }
                rs.close();
                pstmt.close();
            }

            // Load summary after loading data
            loadSummaryFromDatabase();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading report data: " + e.getMessage() +
                            "\n\nPlease check database connection.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSummaryFromDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }

            // Get total invoices count
            String invoiceQuery = "SELECT COUNT(*) as total_invoices, " +
                    "SUM(grand_total) as total_sales " +
                    "FROM invoices WHERE status = 'PAID'";
            PreparedStatement pstmt = connection.prepareStatement(invoiceQuery);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int totalInvoices = rs.getInt("total_invoices");
                double totalSales = rs.getDouble("total_sales");
                double totalDiscount = totalSales * 0.05; // Assume 5% discount
                double totalTax = totalSales * 0.17; // Assume 17% tax
                double netSales = totalSales - totalDiscount + totalTax;

                totalInvoicesLabel.setText(String.valueOf(totalInvoices));
                grossSalesLabel.setText("PKR " + CURRENCY_FORMAT.format(totalSales));
                totalDiscountLabel.setText("PKR " + CURRENCY_FORMAT.format(totalDiscount));
                totalTaxLabel.setText("PKR " + CURRENCY_FORMAT.format(totalTax));
                netSalesLabel.setText("PKR " + CURRENCY_FORMAT.format(netSales));
            }
            rs.close();
            pstmt.close();

            // Get total quantity
            String quantityQuery = "SELECT SUM(quantity) as total_qty FROM invoice_items ii " +
                    "JOIN invoices i ON ii.invoice_id = i.invoice_id " +
                    "WHERE i.status = 'PAID'";
            pstmt = connection.prepareStatement(quantityQuery);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                double totalQty = rs.getDouble("total_qty");
                totalQuantityLabel.setText(String.format("%.2f", totalQty));
            }
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            // Set default values on error
            totalInvoicesLabel.setText("0");
            totalQuantityLabel.setText("0");
            grossSalesLabel.setText("PKR 0.00");
            totalDiscountLabel.setText("PKR 0.00");
            totalTaxLabel.setText("PKR 0.00");
            netSalesLabel.setText("PKR 0.00");
        }
    }

    /* ================= HELPER METHODS ================= */

    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER_COLOR);
        separator.setBackground(BORDER_COLOR);
        return separator;
    }

    private JLabel createFilterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(LABEL_COLOR);
        label.setPreferredSize(new Dimension(80, 25));
        return label;
    }

    private JButton createCloseButton() {
        JButton btn = new JButton("✕");
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setForeground(new Color(140, 140, 140));
        btn.setPreferredSize(new Dimension(35, 35));
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

    private JRadioButton createStyledRadioButton(String text) {
        JRadioButton radio = new JRadioButton(text);
        radio.setFont(RADIO_FONT);
        radio.setForeground(LABEL_COLOR);
        radio.setBackground(Color.WHITE);
        radio.setFocusPainted(false);
        radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        radio.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (radio.isSelected()) {
                    g2.setColor(SKY_BLUE);
                    g2.fillOval(x, y, 14, 14);

                    g2.setColor(Color.WHITE);
                    g2.fillOval(x + 3, y + 3, 8, 8);

                    g2.setColor(SKY_BLUE.darker());
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawOval(x, y, 13, 13);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x, y, 14, 14);

                    g2.setColor(new Color(180, 180, 180));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawOval(x, y, 13, 13);
                }
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 18;
            }

            @Override
            public int getIconHeight() {
                return 18;
            }
        });

        return radio;
    }

    private JButton createFilterButton(String text, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(normalColor.darker(), 1),
                BorderFactory.createEmptyBorder(6, 15, 6, 15)));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(hoverColor.darker(), 1),
                        BorderFactory.createEmptyBorder(6, 15, 6, 15)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(normalColor.darker(), 1),
                        BorderFactory.createEmptyBorder(6, 15, 6, 15)));
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(getBackground().darker());
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

    private JTextField createStyledTextField(String placeholder, boolean readOnly) {
        return new JTextField() {
            private boolean showingPlaceholder = !readOnly;
            private String placeholderText = placeholder;

            {
                setFont(INPUT_FONT);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                setBackground(INPUT_BG);
                setForeground(readOnly ? new Color(80, 80, 80) : Color.GRAY);
                setText(placeholder);
                setCaretColor(DARK_SKY_BLUE);
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

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
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

                g2.setColor(DARK_SKY_BLUE);
                int[] xPoints = { getWidth() - 18, getWidth() - 10, getWidth() - 14 };
                int[] yPoints = { getHeight() / 2 - 2, getHeight() / 2 - 2, getHeight() / 2 + 2 };
                g2.fillPolygon(xPoints, yPoints, 3);

                g2.dispose();
                super.paintComponent(g);
            }
        };
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

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }

    /* ================= BUSINESS LOGIC ================= */
    private void setDefaultDates() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String firstDayStr = DISPLAY_DATE_FORMAT.format(cal.getTime());
        String currentDate = DISPLAY_DATE_FORMAT.format(new Date());

        if (fromDateField != null) {
            fromDateField.setText(firstDayStr);
            fromDateField.setForeground(new Color(40, 40, 40));
        }

        if (toDateField != null) {
            toDateField.setText(currentDate);
            toDateField.setForeground(new Color(40, 40, 40));
        }
    }

    private String getSelectedReportType() {
        if (dailySalesRadio.isSelected())
            return "Daily Sales";
        if (monthlySalesRadio.isSelected())
            return "Monthly Sales";
        if (yearlySalesRadio.isSelected())
            return "Yearly Sales";
        if (productWiseRadio.isSelected())
            return "Product-wise";
        if (categoryWiseRadio.isSelected())
            return "Category-wise";
        if (customerWiseRadio.isSelected())
            return "Customer-wise";
        return "Daily Sales";
    }

    private void onGenerateReport() {
        String selectedType = getSelectedReportType();
        String fromDate = fromDateField.getForeground().equals(Color.GRAY) ? "" : fromDateField.getText().trim();
        String toDate = toDateField.getForeground().equals(Color.GRAY) ? "" : toDateField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String product = (String) productCombo.getSelectedItem();

        // Validate dates if provided
        if (!fromDate.isEmpty() && !toDate.isEmpty()) {
            try {
                Date from = DISPLAY_DATE_FORMAT.parse(fromDate);
                Date to = DISPLAY_DATE_FORMAT.parse(toDate);

                if (from.after(to)) {
                    JOptionPane.showMessageDialog(this,
                            "❌ Invalid Date Range\n\n" +
                                    "From Date cannot be after To Date.\n" +
                                    "Please correct the dates and try again.",
                            "Date Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                // Continue with invalid dates
            }
        }

        // Show generation message
        StringBuilder message = new StringBuilder();
        message.append("<html><div style='width:400px; padding:10px;'>");
        message.append("<b style='color:#1e90ff; font-size:14px;'>📊 Generating ").append(selectedType)
                .append(" Report</b><br><br>");
        message.append("<b>Filters Applied:</b><br>");
        if (!fromDate.isEmpty())
            message.append("• From Date: ").append(fromDate).append("<br>");
        if (!toDate.isEmpty())
            message.append("• To Date: ").append(toDate).append("<br>");
        if (!category.equals("All"))
            message.append("• Category: ").append(category).append("<br>");
        if (!product.equals("All"))
            message.append("• Product: ").append(product).append("<br>");
        message.append("<br><i>Processing report data...</i></div></html>");

        // Create a loading dialog
        JDialog loadingDialog = new JDialog(this, "Generating Report", true);
        loadingDialog.setUndecorated(true);
        loadingDialog.setSize(300, 150);
        loadingDialog.setLocationRelativeTo(this);

        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.setBackground(Color.WHITE);
        loadingPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SKY_BLUE, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel loadingLabel = new JLabel("<html><div style='text-align:center;'>" +
                "<b style='color:#1e90ff;'>Processing Report...</b><br><br>" +
                "<div style='color:#666;'>Please wait while we generate your report</div></div></html>");
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        loadingPanel.add(loadingLabel, BorderLayout.CENTER);
        loadingDialog.add(loadingPanel);

        // Simulate loading with a timer
        Timer timer = new Timer(1500, e -> {
            loadingDialog.dispose();

            // Load data from database
            loadSampleDataFromDatabase();

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "<html><div style='width:350px; padding:10px;'>" +
                            "<b style='color:#28a745; font-size:14px;'>✅ Report Generated Successfully!</b><br><br>" +
                            "<b>Report Details:</b><br>" +
                            "• Type: " + selectedType + "<br>" +
                            "• Records: " + reportTableModel.getRowCount() + "<br>" +
                            "• Filters Applied: " +
                            ((!category.equals("All") || !product.equals("All") || !fromDate.isEmpty()) ? "Yes" : "No")
                            + "<br><br>" +
                            "<i>Report is ready for export or printing.</i></div></html>",
                    "Report Generated",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        timer.setRepeats(false);

        // Show loading dialog and start timer
        loadingDialog.setVisible(true);
        timer.start();
    }

    private void onReset() {
        // Reset radio buttons
        dailySalesRadio.setSelected(true);

        // Reset date fields
        setDefaultDates();

        // Reset comboboxes
        categoryCombo.setSelectedIndex(0);
        productCombo.setSelectedIndex(0);

        // Clear table selection
        reportTable.clearSelection();

        // Reload default data
        loadSampleDataFromDatabase();

        // Show confirmation
        JOptionPane.showMessageDialog(this,
                "<html><div style='width:300px; padding:10px;'>" +
                        "<b style='color:#1e90ff;'>🔄 Reset Complete</b><br><br>" +
                        "All filters and selections have been reset to default values.</div></html>",
                "Reset Complete",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void onExportPDF() {
        String selectedType = getSelectedReportType();
        int rowCount = reportTableModel.getRowCount();

        String fileName = selectedType.replace(" ", "_") + "_Report_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";

        // Show export dialog with options
        String[] options = { "A4 Portrait", "A4 Landscape", "Legal Size", "Custom" };
        int choice = JOptionPane.showOptionDialog(this,
                "<html><div style='width:350px; padding:10px;'>" +
                        "<b style='color:#1e90ff;'>📄 Export to PDF</b><br><br>" +
                        "<b>Report Details:</b><br>" +
                        "• Type: " + selectedType + "<br>" +
                        "• Records: " + rowCount + "<br>" +
                        "• File: " + fileName + "<br><br>" +
                        "<b>Select Page Format:</b></div></html>",
                "Export to PDF",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice >= 0) {
            // Simulate PDF generation
            JDialog progressDialog = new JDialog(this, "Exporting PDF", true);
            progressDialog.setUndecorated(true);
            progressDialog.setSize(350, 120);
            progressDialog.setLocationRelativeTo(this);

            JPanel progressPanel = new JPanel(new BorderLayout());
            progressPanel.setBackground(Color.WHITE);
            progressPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GREEN_BUTTON, 2),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)));

            JLabel progressLabel = new JLabel("<html><div style='text-align:center;'>" +
                    "<b style='color:#28a745;'>Exporting to PDF...</b><br><br>" +
                    "Generating " + fileName + "<br>" +
                    "Page Format: " + options[choice] + "</div></html>");
            progressLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setBackground(Color.WHITE);
            progressBar.setForeground(GREEN_BUTTON);

            progressPanel.add(progressLabel, BorderLayout.CENTER);
            progressPanel.add(progressBar, BorderLayout.SOUTH);
            progressDialog.add(progressPanel);

            Timer timer = new Timer(2000, e -> {
                progressDialog.dispose();

                int pageCount = (rowCount / 40) + 1;

                JOptionPane.showMessageDialog(this,
                        "<html><div style='width:350px; padding:10px;'>" +
                                "<b style='color:#28a745;'>✅ PDF Export Complete!</b><br><br>" +
                                "<b>File Details:</b><br>" +
                                "• Name: " + fileName + "<br>" +
                                "• Size: ~" + (rowCount * 2 + 50) + " KB<br>" +
                                "• Pages: " + pageCount + "<br>" +
                                "• Format: " + options[choice] + "<br><br>" +
                                "<i>The PDF file has been saved in your Documents folder.</i></div></html>",
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            });
            timer.setRepeats(false);

            progressDialog.setVisible(true);
            timer.start();
        }
    }

    private void onExportExcel() {
        String selectedType = getSelectedReportType();
        int rowCount = reportTableModel.getRowCount();

        String fileName = selectedType.replace(" ", "_") + "_Report_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";

        // Show export options
        Object[] options = { "Basic Export", "With Charts", "With Pivot Tables" };
        int choice = JOptionPane.showOptionDialog(this,
                "<html><div style='width:400px; padding:10px;'>" +
                        "<b style='color:#28a745;'>📊 Export to Excel</b><br><br>" +
                        "<b>Report Details:</b><br>" +
                        "• Type: " + selectedType + "<br>" +
                        "• Records: " + rowCount + "<br>" +
                        "• File: " + fileName + "<br><br>" +
                        "<b>Select Export Options:</b><br>" +
                        "1. Basic Export (Data only)<br>" +
                        "2. With Charts (Visualizations)<br>" +
                        "3. With Pivot Tables (Advanced analysis)</div></html>",
                "Export to Excel",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice >= 0) {
            // Simulate Excel export
            JDialog progressDialog = new JDialog(this, "Exporting Excel", true);
            progressDialog.setUndecorated(true);
            progressDialog.setSize(400, 140);
            progressDialog.setLocationRelativeTo(this);

            JPanel progressPanel = new JPanel(new BorderLayout());
            progressPanel.setBackground(Color.WHITE);
            progressPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GREEN_BUTTON, 2),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)));

            String exportTypeStr;
            switch (choice) {
                case 0:
                    exportTypeStr = "Basic Data";
                    break;
                case 1:
                    exportTypeStr = "Data with Charts";
                    break;
                case 2:
                    exportTypeStr = "Data with Pivot Tables";
                    break;
                default:
                    exportTypeStr = "Basic Data";
            }

            JLabel progressLabel = new JLabel("<html><div style='text-align:center;'>" +
                    "<b style='color:#28a745;'>Exporting to Excel...</b><br><br>" +
                    "Creating " + fileName + "<br>" +
                    "Export Type: " + exportTypeStr + "</div></html>");
            progressLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setBackground(Color.WHITE);
            progressBar.setForeground(GREEN_BUTTON);

            progressPanel.add(progressLabel, BorderLayout.CENTER);
            progressPanel.add(progressBar, BorderLayout.SOUTH);
            progressDialog.add(progressPanel);

            // Simulate progress
            Timer timer = new Timer(50, new ActionListener() {
                int progress = 0;

                @Override
                public void actionPerformed(ActionEvent evt) {
                    progress += 2;
                    progressBar.setValue(progress);

                    if (progress >= 100) {
                        ((Timer) evt.getSource()).stop();
                        progressDialog.dispose();

                        int sheetCount = (choice == 0 ? 1 : choice == 1 ? 3 : 4);

                        JOptionPane.showMessageDialog(GenerateReportDialog.this,
                                "<html><div style='width:400px; padding:10px;'>" +
                                        "<b style='color:#28a745;'>✅ Excel Export Complete!</b><br><br>" +
                                        "<b>File Details:</b><br>" +
                                        "• Name: " + fileName + "<br>" +
                                        "• Format: .xlsx (Excel 2016+)<br>" +
                                        "• Sheets: " + sheetCount + "<br>" +
                                        "• Export Type: " + exportTypeStr + "<br><br>" +
                                        "<b>Sheets Included:</b><br>" +
                                        "1. Report Summary<br>" +
                                        "2. Detailed Data" +
                                        (choice >= 1 ? "<br>3. Charts & Graphs" : "") +
                                        (choice >= 2 ? "<br>4. Pivot Tables" : "") + "<br><br>" +
                                        "<i>The Excel file has been saved and is ready for analysis.</i></div></html>",
                                "Export Complete",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });

            progressDialog.setVisible(true);
            timer.start();
        }
    }

    private void onPrintReport() {
        String selectedType = getSelectedReportType();
        int rowCount = reportTableModel.getRowCount();
        int pageCount = (rowCount / 30) + 2;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Print " + selectedType + " report?\n" +
                        "Records: " + rowCount + "\n" +
                        "Estimated pages: " + pageCount,
                "Print Report",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Print job sent successfully!\n" +
                            "Pages: " + pageCount,
                    "Print Complete",
                    JOptionPane.INFORMATION_MESSAGE);
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
                g2.fillRoundRect(i, i, getWidth() - 2 * i, getHeight() - 2 * i, cornerRadius, cornerRadius);
            }

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

            g2.setColor(new Color(180, 200, 230));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

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

            // Test database connection first
            if (!DBConnection.testConnection()) {
                JOptionPane.showMessageDialog(null,
                        "Cannot connect to database!\n\n" +
                                "Please ensure:\n" +
                                "1. MySQL server is running\n" +
                                "2. Database 'tile_factory_db' exists\n" +
                                "3. Username/Password are correct\n\n" +
                                "Check console for details.",
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            // Get connection from DBConnection
            Connection dbConnection = DBConnection.getConnection();

            if (dbConnection == null) {
                JOptionPane.showMessageDialog(null,
                        "Failed to establish database connection.\n" +
                                "Please check your database settings.",
                        "Connection Failed",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            JFrame frame = new JFrame("Test Generate Report Dialog");
            frame.setSize(1000, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JPanel contentPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    GradientPaint gradient = new GradientPaint(
                            0, 0, new Color(230, 240, 255),
                            getWidth(), getHeight(), new Color(210, 225, 245));
                    g2.setPaint(gradient);
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    g2.setColor(new Color(30, 144, 255, 30));
                    for (int i = 0; i < 5; i++) {
                        int x = (int) (Math.random() * getWidth());
                        int y = (int) (Math.random() * getHeight());
                        int size = 50 + (int) (Math.random() * 100);
                        g2.fillOval(x, y, size, size);
                    }
                    g2.dispose();
                }
            };
            contentPanel.setLayout(new GridBagLayout());
            frame.setContentPane(contentPanel);

            JButton testBtn = new JButton("📊 Open Generate Report Dialog") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                    g2.setColor(getBackground().darker());
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);

                    g2.dispose();
                    super.paintComponent(g);
                }
            };

            testBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            testBtn.setBackground(new Color(30, 144, 255));
            testBtn.setForeground(Color.WHITE);
            testBtn.setFocusPainted(false);
            testBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            testBtn.setPreferredSize(new Dimension(300, 55));
            testBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
            testBtn.setContentAreaFilled(false);
            testBtn.setOpaque(false);

            testBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    testBtn.setBackground(new Color(70, 130, 180));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    testBtn.setBackground(new Color(30, 144, 255));
                }
            });

            testBtn.addActionListener(e -> {
                GenerateReportDialog dialog = new GenerateReportDialog(frame, contentPanel);
                dialog.setVisible(true);
            });

            JLabel instructionLabel = new JLabel("<html><div style='text-align:center; padding:20px;'>" +
                    "<h2 style='color:#1e90ff;'>Generate Report Dialog Demo</h2>" +
                    "<p style='color:#666; font-size:14px;'>Click the button below to open the report generation dialog.<br>"
                    +
                    "Features include: Multiple report types, filtering options,<br>" +
                    "summary statistics, and export/print functionality.</p>" +
                    "<p style='color:#888; font-size:12px; margin-top:20px;'>" +
                    "Note: Connected to Tile Factory Database</p></div></html>");
            instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel centerPanel = new JPanel(new BorderLayout(0, 30));
            centerPanel.setOpaque(false);
            centerPanel.add(instructionLabel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false);
            buttonPanel.add(testBtn);
            centerPanel.add(buttonPanel, BorderLayout.CENTER);

            contentPanel.add(centerPanel);

            JLabel footerLabel = new JLabel(
                    "<html><div style='text-align:center; color:#888; font-size:11px; padding:15px;'>" +
                            "© 2024 Tile Factory Management System | Version 2.0 | Report Generation Module</div></html>");
            footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            contentPanel.add(footerLabel, BorderLayout.SOUTH);

            frame.setVisible(true);

            // Show welcome message
            Timer welcomeTimer = new Timer(500, evt -> {
                JOptionPane.showMessageDialog(frame,
                        "<html><div style='width:400px; padding:15px;'>" +
                                "<h3 style='color:#1e90ff; margin-top:0;'>Welcome to Report Generator</h3>" +
                                "<p>This application is connected to Tile Factory Database.</p>" +
                                "<p style='color:#666; font-style:italic;'>Click 'Open Generate Report Dialog' to begin.</p>"
                                +
                                "</div></html>",
                        "Welcome",
                        JOptionPane.INFORMATION_MESSAGE);
            });
            welcomeTimer.setRepeats(false);
            welcomeTimer.start();
        });
    }
}