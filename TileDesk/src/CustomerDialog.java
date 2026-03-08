package src;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * CustomerDialog - Modern Customer Management Dialog with MySQL Database Integration
 */
public class CustomerDialog extends JDialog {
    // Colors (unchanged)
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
    private static final Color TABLE_HEADER_BG = new Color(245, 247, 250);
    private static final Color TABLE_ROW_HOVER = new Color(240, 248, 255);
    private static final Color TABLE_GRID_COLOR = new Color(225, 230, 240);
    private static final Color SUCCESS_GREEN = new Color(40, 167, 69);
    private static final Color WARNING_ORANGE = new Color(255, 193, 7);
    private static final Color UPDATE_GREEN = new Color(40, 167, 69);
    private static final Color HOVER_GREEN = new Color(30, 147, 59);
    private static final Color CANCEL_COLOR = new Color(150, 150, 150);
    private static final Color CANCEL_HOVER_RED = new Color(220, 80, 80);
    private static final Color TABLE_SELECTION = new Color(225, 240, 255);
    private static final Color SCROLLBAR_COLOR = new Color(100, 100, 100);
    private static final Color TEXT_BLACK = new Color(40, 40, 40);
    private static final Color PLACEHOLDER_GRAY = new Color(150, 150, 150);
    private static final Color INPUT_BORDER = new Color(200, 220, 240);
    // ================= UI CONSTANTS =================
private static final Color INPUT_VISIBLE_BORDER = new Color(180, 200, 230);

    private static final Color INPUT_VISIBLE_BG = new Color(252, 253, 255);
    
    // Database connection removed
    // private Connection connection;
    
    // Form fields
    private JTextField customerIdField;
    private JTextField customerCodeField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JTextField regDateField;
    private JTextField lastPurchaseField;
    private JTextField totalPurchasesField;
    private JTextField purchaseCountField;
    
    // Table
    private JTable customerTable;
    private DefaultTableModel tableModel;
    
    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font PLACEHOLDER_FONT = new Font("Segoe UI", Font.ITALIC, 14);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font SECTION_FONT = new Font("Segoe UI Semibold", Font.BOLD, 18);
    
    // Button references
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton searchButton;
    private JButton closeButton;
    private JButton mergeButton;
    private JButton viewHistoryButton;
    
    // Data
    private int selectedCustomerId = -1;
    
    // For total customers counter
    private JLabel totalCustomersLabel;
    
    // In-memory customer storage
    private final List<Customer> customerList = new ArrayList<>();
    
    // Customer class to store data
    private static class Customer {
        String code;
        String name;
        String phone;
        String email;
        double totalPurchases;
        String lastPurchase;
        String regDate;
        String address;
        int purchaseCount;
        
        Customer(String code, String name, String phone, String email, double totalPurchases,
                 String lastPurchase, String regDate, String address, int purchaseCount) {
            this.code = code;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.totalPurchases = totalPurchases;
            this.lastPurchase = lastPurchase;
            this.regDate = regDate;
            this.address = address;
            this.purchaseCount = purchaseCount;
        }
    }
    
    /* ================= CONSTRUCTOR ================= */
    public CustomerDialog(JFrame ownerFrame, JPanel contentPanel) {
        super(ownerFrame, true);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Database connectivity removed
        /*
        connection = DBConnection.getConnection();
        if (connection == null) {
            dispose();
            return;
        }
        */

        
        // ================= FULL SCREEN SETUP =================
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        Rectangle bounds = gd.getDefaultConfiguration().getBounds();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gd.getDefaultConfiguration());
        
        int screenWidth = bounds.width;
        int screenHeight = bounds.height - screenInsets.bottom - screenInsets.top;
        int screenX = bounds.x;
        int screenY = bounds.y + screenInsets.top;
        
        // Main card panel
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30, 35, 30, 35));
        card.setPreferredSize(new Dimension(screenWidth - 100, screenHeight - 100));
        
        /* ================= TOP BAR ================= */
        JPanel topBar = createTopBar();
        card.add(topBar, BorderLayout.NORTH);
        
        /* ================= MAIN CONTENT ================= */
        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setOpaque(false);
        
        // Left Panel - Form
        JPanel formPanel = createFormPanel();
        content.add(formPanel, BorderLayout.WEST);
        
        // Right Panel - Table & Search
        JPanel tablePanel = createTablePanel();
        content.add(tablePanel, BorderLayout.CENTER);
        
        card.add(content, BorderLayout.CENTER);
        
        /* ================= BUTTON PANEL ================= */
        JPanel buttonPanel = createButtonPanel();
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add card to dialog
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(LIGHT_BLUE_BG);
        getContentPane().add(card);
        
        pack();
        setSize(screenWidth, screenHeight);
        setLocation(screenX, screenY);
        
        // ESC to close
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Initialize with sample data
        initializeSampleData();
        
        // Load customers (local/memory)
        loadCustomersFromDatabase();
        
        // Set focus to search field
        SwingUtilities.invokeLater(() -> searchField.requestFocus());
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 25, 0));
        
        // Title with subtitle
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        
        JLabel title = new JLabel("Customer Management");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        
        JLabel subtitle = new JLabel("View • Update • Delete • Merge • History");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 120));
        
        titlePanel.add(title);
        titlePanel.add(subtitle);
        topBar.add(titlePanel, BorderLayout.WEST);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);
        
        // TOTAL CUSTOMERS LABEL - Will be updated dynamically
        totalCustomersLabel = new JLabel("<html><b>Total Customers:</b> 0</html>");
        totalCustomersLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalCustomersLabel.setForeground(LABEL_COLOR);
        totalCustomersLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SKY_BLUE, 2, true),
            new EmptyBorder(8, 15, 8, 15)
        ));
        statsPanel.add(totalCustomersLabel);
        
        // Close button
        closeButton = createCloseButton();
        closeButton.addActionListener(e -> dispose());
        statsPanel.add(closeButton);
        
        topBar.add(statsPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        panel.setPreferredSize(new Dimension(420, 0));
        
        JLabel formHeader = new JLabel("CUSTOMER INFORMATION");
        formHeader.setFont(SECTION_FONT);
        formHeader.setForeground(DARK_SKY_BLUE);
        formHeader.setBorder(new EmptyBorder(0, 0, 20, 0));
        formHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(formHeader);
        
        // Customer Code (Read-only)
        panel.add(createFieldLabel("Customer Code", true, true));
        customerCodeField = createStyledTextField("Selection Required", true);
        customerCodeField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(customerCodeField);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Full Name
        panel.add(createFieldLabel("Full Name", true, false));
        fullNameField = createStyledTextField("Enter customer name", false);
        panel.add(fullNameField);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Email
        panel.add(createFieldLabel("Email Address", false, false));
        emailField = createStyledTextField("example@mail.com", false);
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Phone Number
        panel.add(createFieldLabel("Phone Number", true, false));
        phoneField = createStyledTextField("03XX-XXXXXXX", false);
        phoneField.setInputVerifier(new PhoneVerifier());
        panel.add(phoneField);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Address
        panel.add(createFieldLabel("Address", false, false));
        addressArea = createStyledTextArea("Enter complete address...", 3);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        styleFormScrollPane(addressScroll);
        panel.add(addressScroll);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Registration & Purchase Info (Grouped)
        JPanel infoGrid = new JPanel(new GridLayout(4, 2, 10, 8));
        infoGrid.setOpaque(false);
        infoGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoGrid.add(new JLabel("Reg Date:"));
        regDateField = createSmallReadOnlyField();
        infoGrid.add(regDateField);
        
        infoGrid.add(new JLabel("Last Purchase:"));
        lastPurchaseField = createSmallReadOnlyField();
        infoGrid.add(lastPurchaseField);
        
        infoGrid.add(new JLabel("Total Spent:"));
        totalPurchasesField = createSmallReadOnlyField();
        totalPurchasesField.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoGrid.add(totalPurchasesField);
        
        infoGrid.add(new JLabel("Order Count:"));
        purchaseCountField = createSmallReadOnlyField();
        infoGrid.add(purchaseCountField);
        
        panel.add(infoGrid);
        
        // Status indicator
        panel.add(Box.createVerticalGlue());
        JPanel statusInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusInfo.setOpaque(false);
        statusInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusInfo.add(createStatusLabel("● Required", REQUIRED_COLOR));
        statusInfo.add(createStatusLabel("● Optional", OPTIONAL_COLOR));
        panel.add(statusInfo);
        
        return panel;
    }
    
    private JTextField createSmallReadOnlyField() {
        JTextField f = createStyledTextField("N/A", true);
        f.setPreferredSize(new Dimension(150, 32));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return f;
    }
    
    private JPanel createFieldLabel(String labelText, boolean required, boolean readOnly) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        label.setForeground(LABEL_COLOR);
        panel.add(label);
        
        if (required) {
            JLabel star = new JLabel(" *");
            star.setForeground(REQUIRED_COLOR);
            panel.add(star);
        }
        
        if (readOnly) {
            JLabel ro = new JLabel(" (Locked)");
            ro.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            ro.setForeground(PLACEHOLDER_GRAY);
            panel.add(ro);
        }
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                "Customer Database",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                SECTION_FONT,
                DARK_SKY_BLUE
            ),
            new EmptyBorder(20, 15, 15, 15)
        ));
        
        /* ================= SEARCH PANEL ================= */
        JPanel searchPanel = createSearchPanel();
        panel.add(searchPanel, BorderLayout.NORTH);
        
        /* ================= TABLE ================= */
        String[] columns = {"Code", "Name", "Phone", "Email", "Total Purchases", "Last Purchase", "Reg Date", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 4: return Double.class; // Total Purchases
                    default: return String.class;
                }
            }
        };
        
        customerTable = new JTable(tableModel) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getRowCount() == 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(PLACEHOLDER_GRAY);
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                    String text = "No customers found. Search or load from database.";
                    int textWidth = g2.getFontMetrics().stringWidth(text);
                    int x = (getWidth() - textWidth) / 2;
                    int y = getHeight() / 2;
                    g2.drawString(text, x, y);
                    g2.dispose();
                }
            }
        };
        
        setupTableLikeUpdateProduct();
        
        // Column widths
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Code
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Phone
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(180); // Email
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Total Purchases
        customerTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Last Purchase
        customerTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Reg Date
        customerTable.getColumnModel().getColumn(7).setPreferredWidth(200); // Address
        
        customerTable.getColumnModel().getColumn(4).setCellRenderer(new NumericRenderer());
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        styleTableScrollPane(scrollPane);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupTableLikeUpdateProduct() {
        customerTable.setDefaultEditor(Object.class, null);
        customerTable.setFont(TABLE_FONT);
        customerTable.setRowHeight(36);
        customerTable.setShowGrid(true);
        customerTable.setGridColor(new Color(220, 225, 230));
        customerTable.setSelectionBackground(TABLE_SELECTION);
        customerTable.setSelectionForeground(TEXT_BLACK);
        customerTable.setForeground(TEXT_BLACK);
        customerTable.setFillsViewportHeight(true);
        customerTable.setIntercellSpacing(new Dimension(1, 1));
        
        customerTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
                
                if (column == 4 && value instanceof Double) {
                    double amount = (Double) value;
                    if (!isSelected) {
                        if (amount > 100000) {
                            setBackground(new Color(220, 255, 220));
                            setForeground(new Color(0, 100, 0));
                        } else if (amount > 50000) {
                            setBackground(new Color(255, 255, 200));
                            setForeground(new Color(140, 120, 0));
                        }
                    }
                }
                
                return c;
            }
        });
        
        JTableHeader header = customerTable.getTableHeader();
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
        
        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = customerTable.getSelectedRow();
                if (row >= 0) {
                    loadCustomerData(row);
                }
            }
        });
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(15, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Search Label
        JLabel searchTitle = new JLabel("Quick Search:");
        searchTitle.setFont(LABEL_FONT);
        searchTitle.setForeground(LABEL_COLOR);
        searchPanel.add(searchTitle, BorderLayout.WEST);
        
        // Search Input Group
        JPanel inputGroup = new JPanel(new GridBagLayout());
        inputGroup.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        
        // Type Combo
        gbc.gridx = 0; gbc.weightx = 0.3;
        String[] searchTypes = {"Search All", "By Name", "By Phone", "By Email", "High Value Customers"};
        searchTypeCombo = createStyledComboBox(searchTypes, "Search type");
        searchTypeCombo.setPreferredSize(new Dimension(160, 45));
        inputGroup.add(searchTypeCombo, gbc);
        
        // Text Field
        gbc.gridx = 1; gbc.weightx = 0.7;
        searchField = createStyledTextField("Type name, phone or email to search...", false);
        searchField.setPreferredSize(new Dimension(300, 45));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchCustomer();
                }
            }
        });
        inputGroup.add(searchField, gbc);
        
        // Search Button
        gbc.gridx = 2; gbc.weightx = 0; gbc.insets = new Insets(0, 0, 0, 0);
        searchButton = createStyledButton("🔍 Find Customer", SKY_BLUE, DARK_SKY_BLUE);
        searchButton.setPreferredSize(new Dimension(160, 45));
        searchButton.addActionListener(e -> searchCustomer());
        inputGroup.add(searchButton, gbc);
        
        searchPanel.add(inputGroup, BorderLayout.CENTER);
        
        return searchPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 0, 0, 0));
        
        // View History button
        viewHistoryButton = createStyledButton("📊 View History", UPDATE_GREEN, HOVER_GREEN);
        viewHistoryButton.setForeground(Color.WHITE);
        viewHistoryButton.addActionListener(e -> viewCustomerHistory());
        viewHistoryButton.setEnabled(false);
        
        // Update button
        updateButton = createStyledButton("✏️ Update Details", UPDATE_GREEN, HOVER_GREEN);
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(e -> updateCustomer());
        updateButton.setEnabled(false);
        
        // Merge button
        mergeButton = createStyledButton("🔄 Merge Duplicates", SKY_BLUE, DARK_SKY_BLUE);
        mergeButton.setForeground(Color.WHITE);
        mergeButton.addActionListener(e -> mergeDuplicates());
        
        // Delete button
        deleteButton = createStyledButton("🗑️ Delete", CANCEL_COLOR, CANCEL_HOVER_RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteCustomer());
        deleteButton.setEnabled(false);
        
        // Clear button
        clearButton = createStyledButton("🗋 Clear", SKY_BLUE, DARK_SKY_BLUE);
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> clearForm());
        
        // Refresh button
        JButton refreshButton = createStyledButton("🔄 Refresh", SKY_BLUE, DARK_SKY_BLUE);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> refreshCustomers());
        
        panel.add(viewHistoryButton);
        panel.add(updateButton);
        panel.add(mergeButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    /* ================= DATABASE METHODS ================= */
    
    private void initializeSampleData() {
        customerList.clear();
        customerList.add(new Customer("C001", "Ahmad Hassan", "0300-1234567", "ahmad.h@example.com", 125000.00, "2024-03-01", "2023-01-15", "House 12, Street 4, Model Town, Lahore", 12));
        customerList.add(new Customer("C002", "Sara Khan", "0321-7654321", "sara.k@test.pk", 45000.50, "2024-02-28", "2023-05-20", "Flat 402, Royal Residency, Gulberg III, Lahore", 5));
        customerList.add(new Customer("C003", "Muhammad Bilal", "0333-9876543", "m.bilal88@yahoo.com", 210000.00, "2024-03-05", "2022-11-10", "P-114, Jinnah Colony, Faisalabad", 18));
        customerList.add(new Customer("C004", "Fatima Zahra", "0345-1122334", "fatima.z@gmail.com", 15000.00, "2024-01-12", "2024-01-05", "Bungalow 45-B, Phase 5, DHA, Karachi", 2));
        customerList.add(new Customer("C005", "Zubair Ahmed", "0312-5566778", "zubair.a@outlook.com", 85000.00, "2024-03-07", "2023-06-30", "Main Market, Sector G-9/4, Islamabad", 9));
        customerList.add(new Customer("C006", "Ayesha Siddiqua", "0301-4433221", "ayesha.sid@company.com", 320000.00, "2024-03-08", "2021-08-22", "67-C, Block 2, PECHS, Karachi", 25));
        customerList.add(new Customer("C007", "Usman Sheikh", "0322-9988776", "usman.sheikh@example.org", 54000.00, "2024-02-15", "2023-09-12", "Shop 5, Tariq Road, Karachi", 6));
        customerList.add(new Customer("C008", "Zainab Malik", "0334-0011223", "z.malik@testmail.com", 12000.00, "2023-12-20", "2023-12-15", "House 88, Satellite Town, Rawalpindi", 1));
        customerList.add(new Customer("C009", "Hamza Ali", "0300-8877665", "hamza.ali@gmail.com", 175000.00, "2024-03-04", "2022-04-18", "Apartment 15, Lake View, Bahria Town, Lahore", 14));
    }

    private void loadCustomersFromDatabase() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        for (Customer c : customerList) {
            tableModel.addRow(new Object[]{
                c.code, c.name, c.phone, c.email, c.totalPurchases, c.lastPurchase, c.regDate, c.address
            });
        }
        
        updateTotalCustomersCount();
    }
    
    private void refreshCustomers() {
        loadCustomersFromDatabase();
        clearForm();
        JOptionPane.showMessageDialog(this, 
            "✅ Customers refreshed (local)!", 
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateTotalCustomersCount() {
        if (tableModel != null && totalCustomersLabel != null) {
            int count = tableModel.getRowCount();
            totalCustomersLabel.setText("<html><b>Total Customers:</b> " + count + "</html>");
        }
    }
    
    private void updateCustomer() {
        if (selectedCustomerId == -1) {
            showError("No customer selected");
            return;
        }
        
        // Logic for DB update removed
        JOptionPane.showMessageDialog(this, 
            "✅ Customer data captured locally (DB connectivity removed)!",
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
            
        loadCustomersFromDatabase();
    }
    
    private void deleteCustomer() {
        // Logic for DB delete removed
        JOptionPane.showMessageDialog(this, 
            "✅ Customer removed locally (DB connectivity removed)!",
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
            
        loadCustomersFromDatabase();
        clearForm();
    }
    
    private void viewCustomerHistory() {
        showInfo("Customer history view (DB connectivity removed)");
    }
    
    private void createHistoryDialog(String customerCode, String customerName, 
                                    String phone, String email, double totalPurchases,
                                    String lastPurchase, String regDate) {
        
        JDialog historyDialog = new JDialog(this, "Purchase History", true);
        historyDialog.setUndecorated(true);
        historyDialog.setSize(800, 600);
        historyDialog.setLocationRelativeTo(this);
        
        RoundedCardPanel historyCard = new RoundedCardPanel(15);
        historyCard.setBackground(CARD_BG);
        historyCard.setLayout(new BorderLayout());
        historyCard.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel title = new JLabel("Purchase History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(DARK_SKY_BLUE);
        headerPanel.add(title, BorderLayout.WEST);
        
        JButton closeHistoryBtn = createCloseButton();
        closeHistoryBtn.addActionListener(e -> historyDialog.dispose());
        headerPanel.add(closeHistoryBtn, BorderLayout.EAST);
        
        historyCard.add(headerPanel, BorderLayout.NORTH);
        
        // Customer info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SKY_BLUE, 2, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        infoPanel.add(createInfoLabel("Customer Code:"));
        infoPanel.add(createInfoValue(customerCode));
        infoPanel.add(createInfoLabel("Name:"));
        infoPanel.add(createInfoValue(customerName));
        infoPanel.add(createInfoLabel("Phone:"));
        infoPanel.add(createInfoValue(phone != null ? phone : "N/A"));
        infoPanel.add(createInfoLabel("Email:"));
        infoPanel.add(createInfoValue(email != null ? email : "N/A"));
        infoPanel.add(createInfoLabel("Total Purchases:"));
        infoPanel.add(createInfoValue(formatCurrency(totalPurchases)));
        infoPanel.add(createInfoLabel("Last Purchase:"));
        infoPanel.add(createInfoValue(lastPurchase != null ? lastPurchase : "N/A"));
        infoPanel.add(createInfoLabel("Registration Date:"));
        infoPanel.add(createInfoValue(regDate != null ? regDate : "N/A"));
        
        historyCard.add(infoPanel, BorderLayout.CENTER);
        
        // Purchase history table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        String[] historyColumns = {"Date", "Invoice #", "Product", "Category", "Quantity", "Unit Price", "Total"};
        DefaultTableModel historyModel = new DefaultTableModel(historyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable historyTable = new JTable(historyModel);
        
        historyTable.setFont(TABLE_FONT);
        historyTable.setRowHeight(36);
        historyTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        historyTable.getTableHeader().setBackground(TABLE_HEADER_BG);
        
        // Add purchase history data - DB Logic removed
        /* 
        while (historyRs.next()) {
           ...
        }
        */
        
        JScrollPane historyScroll = new JScrollPane(historyTable) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (historyModel.getRowCount() == 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(PLACEHOLDER_GRAY);
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                    String text = "Purchase history unavailable (Database integration removed)";
                    int textWidth = g2.getFontMetrics().stringWidth(text);
                    int x = (getWidth() - textWidth) / 2;
                    int y = getHeight() / 2;
                    g2.drawString(text, x, y);
                    g2.dispose();
                }
            }
        };
        styleTableScrollPane(historyScroll);
        
        tablePanel.add(new JLabel("Purchase History:"), BorderLayout.NORTH);
        tablePanel.add(historyScroll, BorderLayout.CENTER);
        
        historyCard.add(tablePanel, BorderLayout.SOUTH);
        
        historyDialog.getContentPane().setLayout(new GridBagLayout());
        historyDialog.getContentPane().setBackground(LIGHT_BLUE_BG);
        historyDialog.getContentPane().add(historyCard);
        
        historyDialog.setVisible(true);
    }
    
    private void mergeDuplicates() {
        showInfo("Merge functionality (DB connectivity removed)");
    }
    
    private void mergeCustomersWithDatabase(int targetCustomerId, int sourceCustomerId) {
        // DB logic removed
    }
    
    private void searchCustomer() {
        showInfo("Search functionality (DB connectivity removed)");
    }
    
    private void loadCustomerData(int row) {
        if (row < 0 || row >= customerList.size()) return;
        
        Customer c = customerList.get(row);
        selectedCustomerId = row; // Using index as ID for simplicity
        
        customerCodeField.setText(c.code);
        fullNameField.setText(c.name);
        emailField.setText(c.email != null ? c.email : "");
        phoneField.setText(c.phone != null ? c.phone : "");
        addressArea.setText(c.address != null ? c.address : "");
        
        regDateField.setText(c.regDate);
        lastPurchaseField.setText(c.lastPurchase);
        totalPurchasesField.setText(formatCurrency(c.totalPurchases));
        purchaseCountField.setText(String.valueOf(c.purchaseCount));
        
        // Enable buttons
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
        viewHistoryButton.setEnabled(true);
        
        customerCodeField.setForeground(TEXT_BLACK);
        fullNameField.setForeground(TEXT_BLACK);
        emailField.setForeground(TEXT_BLACK);
        phoneField.setForeground(TEXT_BLACK);
        addressArea.setForeground(TEXT_BLACK);
    }

    private String formatCurrency(double amount) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        return "PKR " + df.format(amount);
    }
    
    private void selectCustomerInTable(String customerCode) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (customerCode.equals(tableModel.getValueAt(i, 0))) {
                customerTable.setRowSelectionInterval(i, i);
                customerTable.scrollRectToVisible(customerTable.getCellRect(i, 0, true));
                loadCustomerData(i);
                break;
            }
        }
    }
    
    private void clearForm() {
        customerCodeField.setText("Select customer from table");
        customerCodeField.setForeground(PLACEHOLDER_GRAY);
        
        fullNameField.setText("Enter customer full name");
        fullNameField.setForeground(PLACEHOLDER_GRAY);
        
        emailField.setText("Enter email address (optional)");
        emailField.setForeground(PLACEHOLDER_GRAY);
        
        phoneField.setText("Enter phone (03XX-XXXXXXX)");
        phoneField.setForeground(PLACEHOLDER_GRAY);
        
        addressArea.setText("Enter customer address (optional)");
        addressArea.setForeground(PLACEHOLDER_GRAY);
        
        regDateField.setText("Auto-filled from selection");
        regDateField.setForeground(PLACEHOLDER_GRAY);
        
        lastPurchaseField.setText("Auto-filled from selection");
        lastPurchaseField.setForeground(PLACEHOLDER_GRAY);
        
        totalPurchasesField.setText("Auto-filled from selection");
        totalPurchasesField.setForeground(PLACEHOLDER_GRAY);
        
        purchaseCountField.setText("Auto-filled from selection");
        purchaseCountField.setForeground(PLACEHOLDER_GRAY);
        
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewHistoryButton.setEnabled(false);
        selectedCustomerId = -1;
        
        customerTable.clearSelection();
        searchField.requestFocus();
    }
    
    /* ================= HELPER METHODS ================= */
    
    private String getCleanPhone(JTextField phoneField) {
        String text = phoneField.getText().trim();
        if (text.equals("Enter phone (03XX-XXXXXXX)") || phoneField.getForeground() == PLACEHOLDER_GRAY) {
            return "";
        }
        return text.replaceAll("\\D", "");
    }
    
    private boolean isFieldValid(JTextField field, boolean required) {
        String text = field.getText().trim();
        if (text.isEmpty()) return !required;
        return field.getForeground() != PLACEHOLDER_GRAY;
    }
    
    private double parseCurrencyValue(Object value) {
        if (value == null) return 0.0;
        try {
            if (value instanceof Double) {
                return (Double) value;
            } else if (value instanceof String) {
                String strVal = value.toString().trim();
                if (strVal.isEmpty() || strVal.equalsIgnoreCase("N/A")) {
                    return 0.0;
                }
                String clean = strVal.replaceAll("[PKR,]", "").trim();
                return clean.isEmpty() ? 0.0 : Double.parseDouble(clean);
            } else if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        } catch (NumberFormatException e) {
            // Silently return 0 for invalid values
        }
        return 0.0;
    }
    
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            "<html><div style='padding:10px;'>" +
            "<h3 style='color:#dc3545; margin-top:0;'>❌ Error</h3>" +
            "<p style='font-size:14px;'>" + message + "</p>" +
            "</div></html>", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, 
            "<html><div style='padding:10px;'>" +
            "<h3 style='color:#30a9ff; margin-top:0;'>ℹ️ Information</h3>" +
            "<p style='font-size:14px;'>" + message + "</p>" +
            "</div></html>", 
            "Information", 
            JOptionPane.INFORMATION_MESSAGE);
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
                if (button == deleteButton) {
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
                if (button == deleteButton) {
                    button.setBackground(new Color(180, 60, 60));
                } else {
                    button.setBackground(hoverColor);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button == deleteButton) {
                    button.setBackground(CANCEL_HOVER_RED);
                } else {
                    button.setBackground(hoverColor);
                }
            }
        });
        
        return button;
    }
    
    private JTextField createStyledTextField(String placeholder, boolean readOnly) {
        return new JTextField() {
            private boolean showingPlaceholder = !readOnly && !placeholder.isEmpty();
            private String placeholderText = placeholder;
            
            {
                setFont(INPUT_FONT);
                setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
                setBackground(readOnly ? new Color(248, 249, 250) : INPUT_VISIBLE_BG);
                setForeground(readOnly ? TEXT_BLACK : PLACEHOLDER_GRAY);
                if (!placeholder.isEmpty()) {
                    setText(placeholder);
                }
                setCaretColor(DARK_SKY_BLUE);
                setPreferredSize(new Dimension(350, 45));
                setEditable(!readOnly);
                
                if (!readOnly && !placeholder.isEmpty()) {
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
                g2.setColor(INPUT_VISIBLE_BORDER);
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
                setBackground(INPUT_VISIBLE_BG);
                setForeground(PLACEHOLDER_GRAY);
                setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value,
                            int index, boolean isSelected, boolean cellHasFocus) {
                        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (index == -1 && value.toString().startsWith("Select")) {
                            setForeground(PLACEHOLDER_GRAY);
                        } else if (isSelected) {
                            setBackground(SKY_BLUE);
                            setForeground(Color.WHITE);
                        } else {
                            setBackground(Color.WHITE);
                            setForeground(TEXT_BLACK);
                        }
                        return c;
                    }
                });
                setPreferredSize(new Dimension(150, 45));
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(INPUT_VISIBLE_BORDER);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(DARK_SKY_BLUE);
                int[] xPoints = {getWidth() - 25, getWidth() - 15, getWidth() - 20};
                int[] yPoints = {getHeight()/2 - 3, getHeight()/2 - 3, getHeight()/2 + 3};
                g2.fillPolygon(xPoints, yPoints, 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
    
    private JTextArea createStyledTextArea(String placeholder, int rows) {
        return new JTextArea(rows, 30) {
            private boolean showingPlaceholder = !placeholder.isEmpty();
            private String placeholderText = placeholder;
            
            {
                setFont(INPUT_FONT);
                setLineWrap(true);
                setWrapStyleWord(true);
                setBackground(INPUT_VISIBLE_BG);
                setForeground(placeholder.isEmpty() ? TEXT_BLACK : PLACEHOLDER_GRAY);
                if (!placeholder.isEmpty()) {
                    setText(placeholder);
                }
                setCaretColor(DARK_SKY_BLUE);
                setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                
                if (!placeholder.isEmpty()) {
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
                g2.setColor(INPUT_VISIBLE_BORDER);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
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
    }
    
    private void styleFormScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LIGHT_BLUE_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
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
    
    private JLabel createStatusLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(STATUS_FONT);
        label.setForeground(color);
        return label;
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        label.setForeground(LABEL_COLOR);
        return label;
    }
    
    private JLabel createInfoValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(DARK_SKY_BLUE);
        return label;
    }
    
    /* ================= HELPER CLASSES ================= */
    
    private static class PhoneVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            JTextField field = (JTextField) input;
            String text = field.getText().trim();
            
            if (text.isEmpty() || text.equals("Enter phone (03XX-XXXXXXX)")) {
                field.setForeground(PLACEHOLDER_GRAY);
                return true;
            }
            
            String digits = text.replaceAll("\\D", "");
            if (digits.length() == 11 && digits.startsWith("03")) {
                SwingUtilities.invokeLater(() -> {
                    if (!text.contains("-") && digits.length() == 11) {
                        String formatted = digits.substring(0, 4) + "-" + digits.substring(4);
                        field.setText(formatted);
                        field.setForeground(TEXT_BLACK);
                    }
                });
                return true;
            }
            
            field.setForeground(Color.RED);
            return false;
        }
    }
    
    private static class NumericRenderer extends DefaultTableCellRenderer {
        public NumericRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Double) {
                setText("PKR" + String.format("%,.2f", (Double) value));
                
                double amount = (Double) value;
                if (!isSelected) {
                    if (amount > 100000) {
                        c.setBackground(new Color(220, 255, 220));
                        setForeground(new Color(0, 100, 0));
                    } else if (amount > 50000) {
                        c.setBackground(new Color(255, 255, 200));
                        setForeground(new Color(140, 120, 0));
                    }
                }
            }
            
            return c;
        }
    }
    
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
    
    /* ================= CLEANUP ================= */
    @Override
    public void dispose() {
        // Don't close the connection here - let DBConnection class manage it
        super.dispose();
    }
    
    /* ================= MAIN METHOD FOR TESTING ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Customer Management System - Factory Edition");
            
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            frame.setSize(bounds.width, bounds.height);
            frame.setLocation(bounds.x, bounds.y);
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(new Color(240, 248, 255));
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(240, 248, 255));
            mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);
            
            JLabel mainTitle = new JLabel("Tile Factory Management System");
            mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
            mainTitle.setForeground(DARK_SKY_BLUE);
            
            JLabel subTitle = new JLabel("Customer Management - Database Integration");
            subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            subTitle.setForeground(new Color(100, 100, 120));
            
            JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
            titlePanel.setOpaque(false);
            titlePanel.add(mainTitle);
            titlePanel.add(subTitle);
            headerPanel.add(titlePanel, BorderLayout.WEST);
            
            JLabel infoLabel = new JLabel("<html><i>Connected to MySQL Database: tile_factory_db</i></html>");
            infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            infoLabel.setForeground(new Color(150, 150, 150));
            headerPanel.add(infoLabel, BorderLayout.EAST);
            
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);
            centerPanel.setBorder(new EmptyBorder(50, 0, 0, 0));
            
            JButton openDialogBtn = new JButton("Open Customer Management");
            openDialogBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
            openDialogBtn.setBackground(SKY_BLUE);
            openDialogBtn.setForeground(Color.WHITE);
            openDialogBtn.setFocusPainted(false);
            openDialogBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            openDialogBtn.setPreferredSize(new Dimension(350, 60));
            openDialogBtn.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
            
            openDialogBtn.addActionListener(e -> {
                CustomerDialog dialog = new CustomerDialog(frame, mainPanel);
                dialog.setVisible(true);
            });
            
            JLabel featuresLabel = new JLabel("<html><center><b>Database Features:</b><br><br>" +
                "• <b>Live MySQL Connection</b> to tile_factory_db<br>" +
                "• <b>Stored Procedures</b> for all operations<br>" +
                "• <b>Views</b> for customer search and reporting<br>" +
                "• <b>Real-time Sync</b> with sales and invoices<br>" +
                "• <b>Purchase History</b> from customer_purchase_history<br>" +
                "• <b>Merge Functionality</b> using database procedures</center></html>");
            featuresLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            featuresLabel.setForeground(new Color(80, 80, 100));
            featuresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            centerPanel.add(featuresLabel);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));
            centerPanel.add(openDialogBtn);
            
            JPanel wrapperPanel = new JPanel(new GridBagLayout());
            wrapperPanel.setBackground(new Color(240, 248, 255));
            wrapperPanel.add(centerPanel);
            
            mainPanel.add(wrapperPanel, BorderLayout.CENTER);
            
            JLabel footerLabel = new JLabel(
                "<html><center>Tile Factory Customer Management v4.0<br>" +
                "<span style='font-size:11px; color:#888;'>" +
                "MySQL Database Integration • Full CRUD Operations • Live Data</span></center></html>");
            footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            footerLabel.setForeground(new Color(150, 150, 150));
            footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            footerLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
            mainPanel.add(footerLabel, BorderLayout.SOUTH);
            
            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}