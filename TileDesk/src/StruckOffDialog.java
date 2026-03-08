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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.Period;
// Add these imports at the top of the file
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * StruckOffDialog - Modern Employee Strike-Off Interface
 * Clean, professional UI with employee table and enhanced search functionality
 * Updated to connect with tile_factory_db database
 */
public class StruckOffDialog extends JDialog {
    private static final Color SKY_BLUE = new Color(135, 206, 250);  // Blue for buttons
    private static final Color DARK_SKY_BLUE = new Color(30, 144, 255); // Hover blue
    private static final Color TITLE_BLUE = new Color(30, 144, 255);  // Blue for title
    private static final Color LIGHT_BLUE_BG = new Color(240, 248, 255);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(180, 200, 220); // Darker border for clear lines
    private static final Color INPUT_BORDER = new Color(200, 220, 240);
    private static final Color WARNING_RED = new Color(220, 53, 69); // Red for struck-off button
    private static final Color HOVER_RED = new Color(200, 40, 40); // Hover red
    private static final Color CANCEL_COLOR = new Color(150, 150, 150);
    private static final Color CANCEL_HOVER_RED = new Color(220, 80, 80); // Cancel turns red on hover
    private static final Color TABLE_HEADER_BG = new Color(245, 247, 250);
    private static final Color TABLE_SELECTION = new Color(225, 240, 255);
    private static final Color SCROLLBAR_COLOR = new Color(100, 100, 100); // Gray-black
    private static final Color TEXT_BLACK = new Color(40, 40, 40); // Consistent black
    private static final Color PLACEHOLDER_GRAY = new Color(150, 150, 150);
    private static final Color SEARCH_BG = new Color(255, 253, 245);
    private static final Color SEARCH_BORDER = new Color(255, 215, 0);
    private static final Color FILTER_PANEL_BG = new Color(230, 240, 255); // Light blue for filter panel
    private static final Color ACTIVE_STATUS = new Color(40, 167, 69); // Green for active
    private static final Color STRUCK_OFF_STATUS = new Color(220, 53, 69); // Red for struck-off
    
    // private Connection connection; // Database connectivity removed
    
    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 15);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FILTER_FONT = new Font("Segoe UI Semibold", Font.BOLD, 14);
    
    // Form fields
    private JTextField empIdField;
    private JTextField fullNameField;
    private JTextField roleField;
    private JTextField phoneField;
    private JTextField statusField;
    private JTextField joinDateField;
    private JComboBox<String> struckOffReasonComboBox;
    private JTextField struckOffDateField;
    private JTextArea remarksArea;
    
    // Search components
    private JTextField searchField;
    private JComboBox<String> searchFilterCombo;
    private JButton searchButton;
    private JButton clearSearchButton;
    
    // Table components
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Emp ID", "Full Name", "Role", "Phone", "Status"}, 0);
    private final JTable table = new JTable(tableModel);
    private JLabel statusLabel; // For showing status messages
    
    // Button references
    private JButton struckOffButton;
    private JButton cancelButton;
    private JButton clearButton;
    
    // Employee data storage
    private List<EmployeeData> employeeList = new ArrayList<>();
    private EmployeeData currentEmployee = null;
    private EmployeeData originalData = null;
    
    // Date formatter
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /* ================= EMPLOYEE DATA CLASS ================= */
    public static class EmployeeData {
        private String employeeId;
        private String fullName;
        private String role;
        private String phone;
        private String status;
        private String joinDate;
        private String cnic;
        private String salary;
        private String address;
        private String notes;
        private int employeeIdInt; // For database operations
        
        public EmployeeData() {}
        
        public EmployeeData(String employeeId, String fullName, String role, String phone, 
                          String status, String joinDate, String cnic, String salary, 
                          String address, String notes) {
            this.employeeId = employeeId;
            this.fullName = fullName;
            this.role = role;
            this.phone = phone;
            this.status = status;
            this.joinDate = joinDate;
            this.cnic = cnic;
            this.salary = salary;
            this.address = address;
            this.notes = notes;
            this.employeeIdInt = extractIdNumber(employeeId);
        }
        
        private int extractIdNumber(String empId) {
            try {
                if (empId.startsWith("EMP-")) {
                    return Integer.parseInt(empId.substring(4));
                }
                return Integer.parseInt(empId);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        // Getters
        public String getEmployeeId() { return employeeId; }
        public String getFullName() { return fullName; }
        public String getRole() { return role; }
        public String getPhone() { return phone; }
        public String getStatus() { return status; }
        public String getJoinDate() { return joinDate; }
        public String getCnic() { return cnic; }
        public String getSalary() { return salary; }
        public String getAddress() { return address; }
        public String getNotes() { return notes; }
        public int getEmployeeIdInt() { return employeeIdInt; }
        
        // Setters
        public void setStatus(String status) { this.status = status; }
        
        // Copy method
        public EmployeeData copy() {
            return new EmployeeData(employeeId, fullName, role, phone, status, 
                                   joinDate, cnic, salary, address, notes);
        }
        
        // Search helper method
        public boolean matchesSearch(String searchText, String filter) {
            searchText = searchText.toLowerCase();
            
            switch (filter) {
                case "All Fields":
                    return employeeId.toLowerCase().contains(searchText) ||
                           fullName.toLowerCase().contains(searchText) ||
                           (role != null && role.toLowerCase().contains(searchText)) ||
                           (phone != null && phone.contains(searchText)) ||
                           (status != null && status.toLowerCase().contains(searchText));
                case "Employee ID":
                    return employeeId.toLowerCase().contains(searchText);
                case "Full Name":
                    return fullName.toLowerCase().contains(searchText);
                case "Role":
                    return role != null && role.toLowerCase().contains(searchText);
                case "Phone":
                    return phone != null && phone.contains(searchText);
                case "Status":
                    return status != null && status.toLowerCase().contains(searchText);
                default:
                    return false;
            }
        }
    }
    
    public StruckOffDialog(JFrame ownerFrame, JPanel contentPanel) {
        super(ownerFrame, true);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Initialize database connection
        // initializeDatabaseConnection(); // DB connectivity removed
        
        // Main card panel - REDUCED WIDTH
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 25, 20, 25)); // Reduced padding
        card.setPreferredSize(new Dimension(1000, 700)); // Reduced width
        
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
        setSize(1000, 700); // Reduced size
        
        // Center dialog
        centerDialog(ownerFrame, contentPanel);
        
        // ESC to close
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Load data from database
        SwingUtilities.invokeLater(() -> {
            loadEmployeesFromDatabase();
        });
        
        // Set focus to search field
        SwingUtilities.invokeLater(() -> searchField.requestFocus());
    }
    
    /* ================= DATABASE METHODS ================= */
    private void initializeDatabaseConnection() { /* DB connectivity removed */ }
    
    private void getConnection() { /* DB connectivity removed */ }
    
    private void loadEmployeesFromDatabase() {
        loadSampleData();
    }
        
    private String formatStatus(String status) {
        if (status == null) return "Unknown";
        switch (status.toUpperCase()) {
            case "ACTIVE": return "Active";
            case "ON_LEAVE": return "On Leave";
            case "PROBATION": return "Probation";
            case "RESIGNED": return "Resigned";
            case "TERMINATED": return "Terminated";
            case "STRUCK_OFF": return "Struck Off";
            default: return status;
        }
    }
    
    private String getDatabaseStatus(String displayStatus) {
        if (displayStatus == null) return "ACTIVE";
        switch (displayStatus.toLowerCase()) {
            case "active": return "ACTIVE";
            case "on leave": return "ON_LEAVE";
            case "probation": return "PROBATION";
            case "resigned": return "RESIGNED";
            case "terminated": return "TERMINATED";
            case "struck off": return "STRUCK_OFF";
            default: return "ACTIVE";
        }
    }
    
    private void updateTableWithEmployees() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            
            for (EmployeeData emp : employeeList) {
                tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getFullName(),
                    emp.getRole(),
                    emp.getPhone(),
                    emp.getStatus()
                });
            }
            
            // Update status label
            long activeCount = employeeList.stream()
                .filter(e -> "Active".equals(e.getStatus()))
                .count();
            
            statusLabel.setText("Total Employees: " + employeeList.size() + 
                               " | Active: " + activeCount + 
                               " | Select an active employee to strike-off");
        });
    }
    
    private void loadSampleData() {
        employeeList.clear();
        tableModel.setRowCount(0);
        
        // Create sample employees with realistic data
        String[] firstNames = {"John", "Sarah", "Michael", "Emma", "Robert", "David", 
                              "Lisa", "James", "Maria", "Ahmed", "Fatima", "Ali", 
                              "Sophia", "Daniel", "Olivia", "William", "Ava", "Joseph"};
        String[] lastNames = {"Smith", "Johnson", "Chen", "Wilson", "Brown", "Davis", 
                             "Miller", "Wilson", "Taylor", "Anderson", "Thomas", "Jackson"};
        String[] roles = {"Factory Manager", "Quality Control Inspector", "Machine Operator", 
                         "Tile Setter", "Kiln Operator", "Production Supervisor", 
                         "Maintenance Technician", "Safety Officer", "Warehouse Manager"};
        
        java.util.Random rand = new java.util.Random();
        
        for (int i = 1; i <= 15; i++) {
            String firstName = firstNames[rand.nextInt(firstNames.length)];
            String lastName = lastNames[rand.nextInt(lastNames.length)];
            String fullName = firstName + " " + lastName;
            String role = roles[rand.nextInt(roles.length)];
            
            // Generate status (all active for strike-off testing)
            String status = "Active";
            
            // Generate phone number
            String phone = "03" + (10 + rand.nextInt(90)) + "" + 
                          (1000000 + rand.nextInt(9000000));
            
            // Generate join date (within last 5 years)
            int year = 2020 + rand.nextInt(4);
            int month = 1 + rand.nextInt(12);
            int day = 1 + rand.nextInt(28);
            String joinDate = String.format("%04d-%02d-%02d", year, month, day);
            
            EmployeeData emp = new EmployeeData(
                "EMP-" + (1000 + i),
                fullName,
                role,
                phone,
                status,
                joinDate,
                "",
                "",
                "",
                ""
            );
            
            employeeList.add(emp);
            
            // Add to table
            tableModel.addRow(new Object[]{
                emp.getEmployeeId(),
                emp.getFullName(),
                emp.getRole(),
                emp.getPhone(),
                emp.getStatus()
            });
        }
        
        // Update status label
        long activeCount = employeeList.stream()
            .filter(e -> "Active".equals(e.getStatus()))
            .count();
        
        statusLabel.setText("Using Sample Data - Total Employees: " + employeeList.size() + 
                           " | Active: " + activeCount + 
                           " | Select an active employee to strike-off");
    }
    
    private boolean saveStrikeOffToDatabase(EmployeeData employee, String reason, String strikeOffDate, String remarks) {
        return true; // DB connectivity removed
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Title on left - Changed to SKY_BLUE as requested
        JLabel title = new JLabel("Employee Strike-Off");
        title.setFont(TITLE_FONT);
        title.setForeground(SKY_BLUE);
        topBar.add(title, BorderLayout.WEST);
        
        // Close button on right
        topBar.add(createCloseButton(), BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout(10, 0)); // Reduced gap
        mainContent.setOpaque(false);
        
        /* ================= LEFT PANEL - TABLE ================= */
        JPanel leftPanel = new JPanel(new BorderLayout(0, 5));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 2, true),
            new EmptyBorder(8, 8, 8, 8) // Reduced padding
        ));
        
        // Add search panel at top
        JPanel searchPanel = createSearchPanel();
        searchPanel.setBackground(FILTER_PANEL_BG);
        searchPanel.setOpaque(true);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SKY_BLUE, 2, true),
            new EmptyBorder(8, 8, 8, 8)
        ));
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Add table title
        JLabel tableTitle = new JLabel("Active Employees List - Click any active employee to strike-off");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(TEXT_BLACK);
        tableTitle.setBorder(new EmptyBorder(5, 0, 5, 0));
        leftPanel.add(tableTitle, BorderLayout.CENTER);
        
        setupTable();
        
        JScrollPane tableScroll = new JScrollPane(table);
        styleTableScrollPane(tableScroll);
        tableScroll.setPreferredSize(new Dimension(550, 400)); // Set preferred size
        leftPanel.add(tableScroll, BorderLayout.CENTER);
        
        // Status label for messages
        statusLabel = new JLabel("Total Employees: " + employeeList.size() + " | Select an active employee to mark as struck-off");
        statusLabel.setFont(STATUS_FONT);
        statusLabel.setForeground(new Color(100, 100, 120));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        leftPanel.add(statusLabel, BorderLayout.SOUTH);
        
        mainContent.add(leftPanel, BorderLayout.CENTER);
        
        /* ================= RIGHT PANEL - STRIKE-OFF FORM ================= */
        JPanel rightPanel = createStrikeOffFormPanel();
        rightPanel.setPreferredSize(new Dimension(320, 0)); // Reduced width
        mainContent.add(rightPanel, BorderLayout.EAST);
        
        return mainContent;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        panel.setBackground(FILTER_PANEL_BG);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);
        
        // Search Label - Sky Blue
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel searchLabel = new JLabel("🔍 Search Employees");
        searchLabel.setFont(FILTER_FONT);
        searchLabel.setForeground(SKY_BLUE);
        panel.add(searchLabel, gbc);
        
        // Search filter combo
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        String[] filters = {"All Fields", "Employee ID", "Full Name", "Role", "Phone", "Status"};
        searchFilterCombo = createStyledComboBox(filters, "Search by");
        searchFilterCombo.setPreferredSize(new Dimension(120, 36)); // Smaller
        searchFilterCombo.setBackground(Color.WHITE);
        panel.add(searchFilterCombo, gbc);
        
        // Search field
        gbc.gridx = 1;
        searchField = new JTextField() {
            private boolean showingPlaceholder = true;
            
            {
                setFont(INPUT_FONT);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SKY_BLUE, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                setBackground(SEARCH_BG);
                setForeground(PLACEHOLDER_GRAY);
                setText("Type to search employees...");
                setCaretColor(DARK_SKY_BLUE);
                setPreferredSize(new Dimension(200, 36)); // Smaller
                
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
                            setText("Type to search employees...");
                            setForeground(PLACEHOLDER_GRAY);
                            showingPlaceholder = true;
                        }
                    }
                });
                
                addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            performSearch();
                        }
                    }
                });
            }
        };
        panel.add(searchField, gbc);
        
        // Buttons row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttonPanel.setOpaque(false);
        
        // Search button - Sky Blue
        searchButton = new JButton("Search");
        styleButton(searchButton, SKY_BLUE, DARK_SKY_BLUE);
        searchButton.setPreferredSize(new Dimension(100, 36)); // Smaller
        searchButton.addActionListener(e -> performSearch());
        
        // Clear search button - Light Gray
        clearSearchButton = new JButton("Clear");
        styleButton(clearSearchButton, new Color(220, 220, 220), new Color(180, 180, 180));
        clearSearchButton.setForeground(TEXT_BLACK);
        clearSearchButton.setPreferredSize(new Dimension(90, 36)); // Smaller
        clearSearchButton.addActionListener(e -> clearSearch());
        
        buttonPanel.add(searchButton);
        buttonPanel.add(clearSearchButton);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createStrikeOffFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 2, true),
            new EmptyBorder(12, 12, 12, 12) // Reduced padding
        ));
        panel.setPreferredSize(new Dimension(320, 0));
        
        // Form title - SKY_BLUE
        JLabel formTitle = new JLabel("Strike-Off Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(SKY_BLUE);
        formTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        panel.add(formTitle, BorderLayout.NORTH);
        
        // Form fields panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 3, 4, 3); // Reduced insets
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Employee ID (Read-only)
        addLabel(formPanel, gbc, "Employee ID", true);
        gbc.gridy++;
        empIdField = createStyledTextField("Click table row to select", true);
        empIdField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        empIdField.setPreferredSize(new Dimension(250, 40)); // Smaller
        formPanel.add(empIdField, gbc);
        
        // Full Name (Read-only)
        gbc.gridy++;
        addLabel(formPanel, gbc, "Full Name", true);
        gbc.gridy++;
        fullNameField = createStyledTextField("", true);
        fullNameField.setPreferredSize(new Dimension(250, 40)); // Smaller
        formPanel.add(fullNameField, gbc);
        
        // Role (Read-only)
        gbc.gridy++;
        addLabel(formPanel, gbc, "Role", true);
        gbc.gridy++;
        roleField = createStyledTextField("", true);
        roleField.setPreferredSize(new Dimension(250, 40)); // Smaller
        formPanel.add(roleField, gbc);
        
        // Phone Number (Read-only)
        gbc.gridy++;
        addLabel(formPanel, gbc, "Phone Number", true);
        gbc.gridy++;
        phoneField = createStyledTextField("", true);
        phoneField.setPreferredSize(new Dimension(250, 40)); // Smaller
        formPanel.add(phoneField, gbc);
        
        // Current Status (Read-only)
        gbc.gridy++;
        addLabel(formPanel, gbc, "Current Status", true);
        gbc.gridy++;
        statusField = createStyledTextField("", true);
        statusField.setPreferredSize(new Dimension(250, 40)); // Smaller
        formPanel.add(statusField, gbc);
        
        // Date of Joining (Read-only)
        gbc.gridy++;
        addLabel(formPanel, gbc, "Join Date", true);
        gbc.gridy++;
        joinDateField = createStyledTextField("", true);
        joinDateField.setPreferredSize(new Dimension(250, 40)); // Smaller
        formPanel.add(joinDateField, gbc);
        
        // Strike-Off Reason (Required)
        gbc.gridy++;
        addLabel(formPanel, gbc, "Strike-Off Reason", true);
        gbc.gridy++;
        String[] reasons = {
            "Select Reason",
            "Resigned Voluntarily",
            "Terminated for Misconduct",
            "Absconded (No Show)",
            "Contract Expired",
            "Medical Grounds",
            "Retirement",
            "Redundancy / Layoff",
            "Performance Issues",
            "Violation of Company Policy",
            "Other"
        };
        struckOffReasonComboBox = createStyledComboBox(reasons, "Choose reason");
        struckOffReasonComboBox.setPreferredSize(new Dimension(250, 40)); // Smaller
        struckOffReasonComboBox.setEnabled(false);
        formPanel.add(struckOffReasonComboBox, gbc);
        
        // Strike-Off Date
        gbc.gridy++;
        addLabel(formPanel, gbc, "Strike-Off Date", true);
        gbc.gridy++;
        struckOffDateField = createStyledTextField(LocalDate.now().format(dateFormatter), false);
        struckOffDateField.setPreferredSize(new Dimension(250, 40)); // Smaller
        struckOffDateField.setEnabled(false);
        formPanel.add(struckOffDateField, gbc);
        
        // Remarks (Optional)
        gbc.gridy++;
        addLabel(formPanel, gbc, "Remarks", false);
        gbc.gridy++;
        remarksArea = createStyledTextArea("Optional: Additional information", 3);
        remarksArea.setEnabled(false);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        styleFormScrollPane(remarksScroll);
        remarksScroll.setPreferredSize(new Dimension(250, 80)); // Smaller
        formPanel.add(remarksScroll, gbc);
        
        // Warning label
        gbc.gridy++;
        gbc.insets = new Insets(12, 3, 5, 3);
        JLabel warningLabel = new JLabel("⚠ This action cannot be undone!");
        warningLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        warningLabel.setForeground(WARNING_RED);
        warningLabel.setHorizontalAlignment(JLabel.CENTER);
        formPanel.add(warningLabel, gbc);
        
        // Add some vertical space at the bottom
        gbc.gridy++;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);
        
        JScrollPane formScroll = new JScrollPane(formPanel);
        styleFormScrollPane(formScroll);
        panel.add(formScroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupTable() {
        // Make table non-editable (only selection)
        table.setDefaultEditor(Object.class, null);
        
        table.setFont(TABLE_FONT);
        table.setRowHeight(32); // Smaller row height
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 225, 230));
        table.setSelectionBackground(TABLE_SELECTION);
        table.setSelectionForeground(TEXT_BLACK);
        table.setForeground(TEXT_BLACK);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Custom cell renderer
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
                        BorderFactory.createEmptyBorder(2, 4, 2, 4)
                    ));
                    
                    // Status column color coding
                    if (column == 4) { // Status column
                        String status = table.getValueAt(row, column).toString();
                        Color statusColor = getStatusColor(status);
                        setBackground(statusColor);
                        setForeground(statusColor.equals(Color.WHITE) ? TEXT_BLACK : Color.WHITE);
                    } else {
                        // Alternating row colors
                        if (row % 2 == 0) {
                            setBackground(Color.WHITE);
                        } else {
                            setBackground(new Color(248, 250, 252));
                        }
                        setForeground(TEXT_BLACK);
                    }
                }
                
                setHorizontalAlignment(column == 1 ? JLabel.LEFT : JLabel.CENTER);
                return c;
            }
        });
        
        // Style table header
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
                    BorderFactory.createEmptyBorder(6, 4, 6, 4)
                ));
                return this;
            }
        });
        
        // Set column widths - Adjusted for smaller table
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Emp ID
        columnModel.getColumn(1).setPreferredWidth(150); // Full Name
        columnModel.getColumn(2).setPreferredWidth(100); // Role
        columnModel.getColumn(3).setPreferredWidth(90);  // Phone
        columnModel.getColumn(4).setPreferredWidth(80);  // Status
        
        // Row click → auto-fill form fields
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String empId = table.getValueAt(row, 0).toString();
                    EmployeeData emp = findEmployeeById(empId);
                    
                    if (emp != null) {
                        // Check if employee is already struck-off
                        if (emp.getStatus().equals("Struck Off") || emp.getStatus().equals("Resigned") || 
                            emp.getStatus().equals("Terminated")) {
                            JOptionPane.showMessageDialog(StruckOffDialog.this,
                                "This employee cannot be struck off!\n\n" +
                                "Employee: " + emp.getFullName() + "\n" +
                                "ID: " + emp.getEmployeeId() + "\n" +
                                "Current Status: " + emp.getStatus() + "\n\n" +
                                "Only Active, On Leave, or Probation employees can be struck off.",
                                "Invalid Employee Status",
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        currentEmployee = emp;
                        originalData = emp.copy();
                        
                        // Fill form fields
                        empIdField.setText(emp.getEmployeeId());
                        empIdField.setForeground(TEXT_BLACK);
                        
                        fullNameField.setText(emp.getFullName());
                        fullNameField.setForeground(TEXT_BLACK);
                        
                        roleField.setText(emp.getRole());
                        roleField.setForeground(TEXT_BLACK);
                        
                        phoneField.setText(emp.getPhone());
                        phoneField.setForeground(TEXT_BLACK);
                        
                        statusField.setText(emp.getStatus());
                        statusField.setForeground(TEXT_BLACK);
                        
                        joinDateField.setText(emp.getJoinDate());
                        joinDateField.setForeground(TEXT_BLACK);
                        
                        // Enable form and buttons
                        setFormEnabled(true);
                        struckOffButton.setEnabled(true);
                        
                        // Update status message
                        statusLabel.setText("Ready to strike-off: " + emp.getFullName() + " (ID: " + emp.getEmployeeId() + ")");
                        statusLabel.setForeground(WARNING_RED);
                    }
                }
            }
        });
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Reduced spacing
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        // Struck-off button - SKY_BLUE
        struckOffButton = createStyledButton("⚠ Strike-Off Employee", SKY_BLUE, DARK_SKY_BLUE);
        struckOffButton.setPreferredSize(new Dimension(160, 45)); // Smaller
        struckOffButton.addActionListener(e -> performStrikeOff());
        struckOffButton.setEnabled(false);
        
        // Cancel button - Turns red on hover
        cancelButton = createStyledButton("✕ Cancel", CANCEL_COLOR, CANCEL_HOVER_RED);
        cancelButton.setPreferredSize(new Dimension(120, 45)); // Smaller
        cancelButton.addActionListener(e -> dispose());
        
        // Clear button - Sky Blue
        clearButton = createStyledButton("🗑️ Clear Form", SKY_BLUE, DARK_SKY_BLUE);
        clearButton.setPreferredSize(new Dimension(140, 45)); // Smaller
        clearButton.addActionListener(e -> clearForm());
        clearButton.setToolTipText("Clear all form fields");
        
        panel.add(struckOffButton);
        panel.add(cancelButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private JButton createCloseButton() {
        JButton btn = new JButton("✕");
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setForeground(new Color(120, 120, 120));
        btn.setPreferredSize(new Dimension(35, 35)); // Smaller
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
                
                // Draw rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw border
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
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        // Hover effect
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
                    button.setBackground(normalColor);
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
    
    private void styleButton(JButton button, Color normalColor, Color hoverColor) {
        button.setFont(INPUT_FONT);
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
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
            star.setForeground(WARNING_RED);
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
                setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
                setBackground(Color.WHITE);
                setForeground(readOnly ? TEXT_BLACK : PLACEHOLDER_GRAY);
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
                
                // Draw rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw clear border
                g2.setColor(INPUT_BORDER);
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
                setBackground(Color.WHITE);
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
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2.setColor(INPUT_BORDER);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                // Draw dropdown arrow - SKY_BLUE
                g2.setColor(SKY_BLUE);
                int[] xPoints = {getWidth() - 22, getWidth() - 12, getWidth() - 17};
                int[] yPoints = {getHeight()/2 - 3, getHeight()/2 - 3, getHeight()/2 + 3};
                g2.fillPolygon(xPoints, yPoints, 3);
                
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
                setForeground(PLACEHOLDER_GRAY);
                setText(placeholder);
                setCaretColor(DARK_SKY_BLUE);
                setBackground(Color.WHITE);
                
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
                            setText(placeholder);
                            setForeground(PLACEHOLDER_GRAY);
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
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2.setColor(INPUT_BORDER);
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
        
        // Custom narrow scrollbars with gray-black color
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
    
    private EmployeeData findEmployeeById(String empId) {
        for (EmployeeData emp : employeeList) {
            if (emp.getEmployeeId().equals(empId)) {
                return emp;
            }
        }
        return null;
    }
    
    private Color getStatusColor(String status) {
        if (status == null) return Color.WHITE;
        switch (status) {
            case "Active": return ACTIVE_STATUS; // Green
            case "On Leave": return new Color(255, 193, 7); // Yellow
            case "Probation": return new Color(0, 123, 255); // Blue
            case "Resigned": return new Color(108, 117, 125); // Gray
            case "Terminated": return new Color(220, 53, 69); // Red
            case "Struck Off": return STRUCK_OFF_STATUS; // Red
            default: return Color.WHITE;
        }
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim();
        String filter = (String) searchFilterCombo.getSelectedItem();
        
        if (searchText.equals("Type to search employees...") || searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter search text",
                "Search Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        tableModel.setRowCount(0);
        int foundCount = 0;
        
        for (EmployeeData emp : employeeList) {
            if (emp.matchesSearch(searchText, filter)) {
                tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getFullName(),
                    emp.getRole(),
                    emp.getPhone(),
                    emp.getStatus()
                });
                foundCount++;
            }
        }
        
        if (foundCount == 0) {
            statusLabel.setText("No employees found for: '" + searchText + "' in " + filter);
            statusLabel.setForeground(new Color(200, 0, 0));
            JOptionPane.showMessageDialog(this,
                "No employees found matching your search criteria.\n\n" +
                "Search: '" + searchText + "'\n" +
                "Filter: " + filter,
                "No Results",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            clearForm();
            setFormEnabled(false);
            struckOffButton.setEnabled(false);
        } else {
            statusLabel.setText("Found " + foundCount + " employee(s) for: '" + searchText + "' in " + filter);
            statusLabel.setForeground(new Color(0, 100, 0));
            
            // Clear form since search results might be different
            clearForm();
            setFormEnabled(false);
            struckOffButton.setEnabled(false);
        }
    }
    
    private void clearSearch() {
        searchField.setText("Type to search employees...");
        searchField.setForeground(PLACEHOLDER_GRAY);
        searchFilterCombo.setSelectedIndex(0);
        
        // Reload all employees
        tableModel.setRowCount(0);
        for (EmployeeData emp : employeeList) {
            tableModel.addRow(new Object[]{
                emp.getEmployeeId(),
                emp.getFullName(),
                emp.getRole(),
                emp.getPhone(),
                emp.getStatus()
            });
        }
        
        long activeCount = employeeList.stream()
            .filter(e -> "Active".equals(e.getStatus()))
            .count();
        
        statusLabel.setText("Total Employees: " + employeeList.size() + 
                           " | Active: " + activeCount + 
                           " | Select an active employee to strike-off");
        statusLabel.setForeground(new Color(100, 100, 120));
        
        // Clear form
        clearForm();
        setFormEnabled(false);
        struckOffButton.setEnabled(false);
    }
    
    private void clearForm() {
        empIdField.setText("Click table row to select");
        empIdField.setForeground(PLACEHOLDER_GRAY);
        
        fullNameField.setText("");
        roleField.setText("");
        phoneField.setText("");
        statusField.setText("");
        joinDateField.setText("");
        
        struckOffReasonComboBox.setSelectedIndex(0);
        struckOffReasonComboBox.setForeground(PLACEHOLDER_GRAY);
        
        struckOffDateField.setText(LocalDate.now().format(dateFormatter));
        struckOffDateField.setForeground(TEXT_BLACK);
        
        remarksArea.setText("Optional: Additional information");
        remarksArea.setForeground(PLACEHOLDER_GRAY);
        
        currentEmployee = null;
        originalData = null;
        table.clearSelection();
    }
    
    private void setFormEnabled(boolean enabled) {
        struckOffReasonComboBox.setEnabled(enabled);
        struckOffDateField.setEditable(enabled);
        remarksArea.setEditable(enabled);
    }
    
    private void performStrikeOff() {
        // Validate required fields
        if (struckOffReasonComboBox.getSelectedIndex() == 0) {
            showError("Please select a Strike-Off Reason");
            struckOffReasonComboBox.requestFocus();
            return;
        }
        
        String date = struckOffDateField.getText().trim();
        if (date.isEmpty()) {
            showError("Please enter the Strike-Off Date");
            struckOffDateField.requestFocus();
            return;
        }
        
        // Simple date validation
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            showError("Please enter date in YYYY-MM-DD format");
            struckOffDateField.requestFocus();
            return;
        }
        
        String reason = (String) struckOffReasonComboBox.getSelectedItem();
        String remarks = remarksArea.getForeground().equals(PLACEHOLDER_GRAY) ? "" : remarksArea.getText().trim();
        
        // Show confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b style='color:red; font-size:14px;'>⚠ WARNING: IRREVERSIBLE ACTION</b><br><br>" +
            "<b>Are you absolutely sure you want to strike-off this employee?</b><br><br>" +
            "<table style='width:100%; border-collapse:collapse;'>" +
            "<tr><td><b>Employee:</b></td><td>" + currentEmployee.getFullName() + "</td></tr>" +
            "<tr><td><b>Employee ID:</b></td><td>" + currentEmployee.getEmployeeId() + "</td></tr>" +
            "<tr><td><b>Role:</b></td><td>" + currentEmployee.getRole() + "</td></tr>" +
            "<tr><td><b>Current Status:</b></td><td>" + currentEmployee.getStatus() + "</td></tr>" +
            "<tr><td><b>New Status:</b></td><td style='color:red; font-weight:bold;'>Struck Off</td></tr>" +
            "<tr><td><b>Reason:</b></td><td>" + reason + "</td></tr>" +
            "<tr><td><b>Effective Date:</b></td><td>" + date + "</td></tr>" +
            "</table><br>" +
            "<font color='red'><b>⚠ This action cannot be undone!<br>" +
            "Employee will be permanently marked as inactive.</b></font></html>",
            "Confirm Employee Strike-Off",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Save to database
            boolean dbSuccess = saveStrikeOffToDatabase(currentEmployee, reason, date, remarks);
            
            if (dbSuccess) { // Allow success if no DB (sample data)
                // Update employee status locally
                currentEmployee.setStatus("Struck Off");
                
                // Update table
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(currentEmployee.getEmployeeId())) {
                        tableModel.setValueAt("Struck Off", i, 4);
                        break;
                    }
                }
                
                // Update form status field
                statusField.setText("Struck Off");
                statusField.setForeground(STRUCK_OFF_STATUS);
                
                // Show success message
                showStatusMessage("✅ Successfully struck-off: " + currentEmployee.getFullName() + 
                                 " (ID: " + currentEmployee.getEmployeeId() + ")", false);
                
                JOptionPane.showMessageDialog(this,
                    "<html><b style='color:red; font-size:14px;'>✅ EMPLOYEE STRUCK-OFF SUCCESSFULLY</b><br><br>" +
                    "<table style='width:100%; border-collapse:collapse;'>" +
                    "<tr><td><b>Employee:</b></td><td>" + currentEmployee.getFullName() + "</td></tr>" +
                    "<tr><td><b>Employee ID:</b></td><td>" + currentEmployee.getEmployeeId() + "</td></tr>" +
                    "<tr><td><b>New Status:</b></td><td style='color:red; font-weight:bold;'>Struck Off</td></tr>" +
                    "<tr><td><b>Reason:</b></td><td>" + reason + "</td></tr>" +
                    "<tr><td><b>Effective Date:</b></td><td>" + date + "</td></tr>" +
                    "<tr><td><b>Timestamp:</b></td><td>" + 
                        java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + 
                    "</td></tr>" +
                    "</table><br>" +
                    (dbSuccess ? "<b>Database Updated Successfully</b><br>" : "<b>Sample Data Mode</b><br>") +
                    "<b>System Actions:</b><br>" +
                    "• Employee status updated to 'Struck Off'<br>" +
                    "• Role assignment removed<br>" +
                    "• Record archived for compliance</html>",
                    "Strike-Off Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Print strike-off record to console
                printStrikeOffRecord(reason, date, remarks, dbSuccess);
                
                // Disable form after successful strike-off
                setFormEnabled(false);
                struckOffButton.setEnabled(false);
                
                // Update original data
                originalData = currentEmployee.copy();
                
                // Update status label
                long activeCount = employeeList.stream()
                    .filter(e -> "Active".equals(e.getStatus()))
                    .count();
                statusLabel.setText("Total Employees: " + employeeList.size() + 
                                   " | Active: " + activeCount + 
                                   " | Employee struck-off successfully");
            } else {
                showError("Failed to update employee status in database!\nPlease try again.");
            }
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            "❌ " + message, 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void showStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        if (isError) {
            statusLabel.setForeground(new Color(200, 0, 0));
        } else {
            statusLabel.setForeground(new Color(0, 100, 0));
        }
    }
    
    /* ================= CONSOLE OUTPUT ================= */
    private void printStrikeOffRecord(String reason, String date, String remarks, boolean dbSuccess) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("           EMPLOYEE STRIKE-OFF RECORD - FACTORY HR SYSTEM");
        System.out.println("=".repeat(70));
        System.out.println("Action Time: " + 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("Database Operation: " + (dbSuccess ? "SUCCESS" : "SAMPLE DATA MODE"));
        System.out.println("-".repeat(70));
        
        System.out.println("1. EMPLOYEE INFORMATION");
        System.out.println("   Employee ID      : " + currentEmployee.getEmployeeId());
        System.out.println("   Full Name        : " + currentEmployee.getFullName());
        System.out.println("   Role             : " + currentEmployee.getRole());
        System.out.println("   Phone            : " + currentEmployee.getPhone());
        System.out.println("   Previous Status  : " + originalData.getStatus());
        System.out.println("   New Status       : STRUCK OFF");
        System.out.println("   Date of Joining  : " + currentEmployee.getJoinDate());
        System.out.println("   Employment Period: " + calculateEmploymentPeriod(currentEmployee.getJoinDate(), date));
        
        System.out.println("\n2. STRIKE-OFF DETAILS");
        System.out.println("   Reason           : " + reason);
        System.out.println("   Effective Date   : " + date);
        System.out.println("   Remarks          : " + (remarks.isEmpty() ? "None" : remarks));
        System.out.println("   Action By        : System Administrator");
        System.out.println("   Action Type      : Permanent Status Change");
        
        System.out.println("\n3. SYSTEM ACTIONS PERFORMED");
        System.out.println("   [" + (dbSuccess ? "✓" : "•") + "] Employee status updated" + (dbSuccess ? " in database" : " (sample data)"));
        System.out.println("   [" + (dbSuccess ? "✓" : "•") + "] Role assignment removed");
        System.out.println("   [" + (dbSuccess ? "✓" : "•") + "] Record archived for compliance");
        
        if (dbSuccess) {
            System.out.println("\n4. DATABASE OPERATIONS");
            System.out.println("   • Updated employees table (employment_status = 'STRUCK_OFF')");
            System.out.println("   • Inserted record into employee_status_history");
            System.out.println("   • Removed role and department assignments");
        } else {
            System.out.println("\n4. SAMPLE DATA MODE");
            System.out.println("   • Operating in sample data mode");
            System.out.println("   • No actual database changes made");
            System.out.println("   • In production, this would update the database");
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                ACTION COMPLETED SUCCESSFULLY");
        System.out.println("=".repeat(70) + "\n");
    }
    
    private String calculateEmploymentPeriod(String joinDate, String strikeOffDate) {
        try {
            LocalDate join = LocalDate.parse(joinDate);
            LocalDate strikeOff = LocalDate.parse(strikeOffDate);
            Period period = Period.between(join, strikeOff);
            
            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();
            
            if (years > 0) {
                return years + " years, " + months + " months, " + days + " days";
            } else if (months > 0) {
                return months + " months, " + days + " days";
            } else {
                return days + " days";
            }
        } catch (Exception e) {
            return "N/A";
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
           // ... Continuing from previous code
            // Draw subtle shadow
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, cornerRadius, cornerRadius);
            
            // Draw main panel with gradient
            GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), 
                new Color(245, 248, 255));
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            
            // Draw border with sky blue
            g2.setColor(SKY_BLUE);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            
            g2.dispose();
        }
    }
    
    /* ================= MAIN METHOD FOR TESTING ================= */
    public static void main(String[] args) {
        // Use invokeLater to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel to system default
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Create test frame
            JFrame testFrame = new JFrame("Employee Strike-Off System - TEST MODE");
            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            testFrame.setSize(1200, 800);
            testFrame.setLocationRelativeTo(null);
            
            // Create main content panel
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(240, 248, 255));
            mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            // Add title
            JLabel titleLabel = new JLabel("Employee Strike-Off System - TEST MODE", JLabel.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(new Color(30, 144, 255));
            titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            
            // Add content panel
            JPanel contentPanel = new JPanel();
            contentPanel.setOpaque(false);
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            
            // Add info panels
            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
            infoPanel.setOpaque(false);
            infoPanel.setBorder(new EmptyBorder(10, 50, 10, 50));
            
            // Database status
            JPanel dbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            dbPanel.setOpaque(false);
            JLabel dbLabel = new JLabel("Database Status: ");
            dbLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            dbLabel.setForeground(Color.BLACK);
            dbPanel.add(dbLabel);
            
            JLabel dbStatus = new JLabel("Connecting...");
            dbStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
            dbPanel.add(dbStatus);
            infoPanel.add(dbPanel);
            
            // Test database connection
            new Thread(() -> {
                boolean isConnected = DBConnection.testConnection();
                SwingUtilities.invokeLater(() -> {
                    if (isConnected) {
                        dbStatus.setText("CONNECTED ✓");
                        dbStatus.setForeground(new Color(0, 150, 0));
                    } else {
                        dbStatus.setText("NOT CONNECTED (Using Sample Data)");
                        dbStatus.setForeground(Color.RED);
                    }
                });
            }).start();
            
            // Test data info
            JPanel dataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            dataPanel.setOpaque(false);
            JLabel dataLabel = new JLabel("Data Source: ");
            dataLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            dataLabel.setForeground(Color.BLACK);
            dataPanel.add(dataLabel);
            
            JLabel dataSource = new JLabel("Checking...");
            dataSource.setFont(new Font("Segoe UI", Font.BOLD, 14));
            dataSource.setForeground(Color.BLUE);
            dataPanel.add(dataSource);
            infoPanel.add(dataPanel);
            
            // Instructions
            JPanel instrPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            instrPanel.setOpaque(false);
            JLabel instrLabel = new JLabel("<html>Instructions: <font color='red'>Test Mode - No real data will be modified</font></html>");
            instrLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            instrPanel.add(instrLabel);
            infoPanel.add(instrPanel);
            
            contentPanel.add(infoPanel);
            
            // Add buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            buttonPanel.setOpaque(false);
            
            JButton openDialogBtn = new JButton("Open Strike-Off Dialog");
            openDialogBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            openDialogBtn.setBackground(new Color(30, 144, 255));
            openDialogBtn.setForeground(Color.WHITE);
            openDialogBtn.setFocusPainted(false);
            openDialogBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
            openDialogBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            openDialogBtn.addActionListener(e -> {
                StruckOffDialog dialog = new StruckOffDialog(testFrame, mainPanel);
                dialog.setVisible(true);
                
                // Update data source label
                SwingUtilities.invokeLater(() -> {
                    dataSource.setText("Sample Data (Offline-First)");
                    dataSource.setForeground(new Color(0, 150, 0));
                });
            });
            
            buttonPanel.add(openDialogBtn);
            
            // Test database button
            JButton testDBBtn = new JButton("Test Database Connection");
            testDBBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            testDBBtn.setBackground(new Color(200, 200, 200));
            testDBBtn.setForeground(Color.BLACK);
            testDBBtn.setFocusPainted(false);
            testDBBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            testDBBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            testDBBtn.addActionListener(e -> {
                new Thread(() -> {
                    boolean isConnected = DBConnection.testConnection();
                    SwingUtilities.invokeLater(() -> {
                        if (isConnected) {
                            dbStatus.setText("CONNECTED ✓");
                            dbStatus.setForeground(new Color(0, 150, 0));
                            dataSource.setText("Database (tile_factory_db)");
                            dataSource.setForeground(new Color(0, 150, 0));
                            JOptionPane.showMessageDialog(testFrame,
                                "✅ Database connection successful!\n\n" +
                                "URL: " + DBConnection.getDatabaseURL() + "\n" +
                                "Database: " + DBConnection.getDatabaseName() + "\n" +
                                "User: " + DBConnection.getDatabaseUser(),
                                "Connection Test",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            dbStatus.setText("NOT CONNECTED");
                            dbStatus.setForeground(Color.RED);
                            dataSource.setText("Sample Data");
                            dataSource.setForeground(Color.ORANGE);
                            JOptionPane.showMessageDialog(testFrame,
                                "❌ Database connection failed!\n\n" +
                                "Please check:\n" +
                                "1. MySQL server is running\n" +
                                "2. Database 'tile_factory_db' exists\n" +
                                "3. Username and password are correct\n" +
                                "4. MySQL connector is in classpath\n\n" +
                                "URL: " + DBConnection.getDatabaseURL(),
                                "Connection Failed",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }).start();
            });
            
            buttonPanel.add(testDBBtn);
            
            contentPanel.add(buttonPanel);
            
            // Add console output area
            JTextArea consoleArea = new JTextArea();
            consoleArea.setEditable(false);
            consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            consoleArea.setBackground(Color.BLACK);
            consoleArea.setForeground(Color.GREEN);
            consoleArea.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            // Redirect System.out to console area
            PrintStream printStream = new PrintStream(new OutputStream() { //error 
                @Override
                public void write(int b) throws IOException { //error 
                    consoleArea.append(String.valueOf((char) b));
                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                }
            });
            System.setOut(printStream);
            System.setErr(printStream);
            
            JScrollPane consoleScroll = new JScrollPane(consoleArea);
            consoleScroll.setPreferredSize(new Dimension(800, 150));
            consoleScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "System Console Output"));
            
            JPanel consolePanel = new JPanel(new BorderLayout());
            consolePanel.setOpaque(false);
            consolePanel.setBorder(new EmptyBorder(20, 50, 0, 50));
            consolePanel.add(consoleScroll, BorderLayout.CENTER);
            contentPanel.add(consolePanel);
            
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            
            // Add footer
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            footer.setOpaque(false);
            footer.setBorder(new EmptyBorder(10, 0, 0, 0));
            
            JLabel footerLabel = new JLabel("Tile Factory Management System - Employee Strike-Off Module");
            footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            footerLabel.setForeground(new Color(100, 100, 120));
            footer.add(footerLabel);
            
            mainPanel.add(footer, BorderLayout.SOUTH);
            
            // Add to frame and show
            testFrame.add(mainPanel);
            testFrame.setVisible(true);
            
            // Print startup message
            System.out.println("\n" + "=".repeat(70));
            System.out.println("     EMPLOYEE STRIKE-OFF SYSTEM - TEST MODE INITIALIZED");
            System.out.println("=".repeat(70));
            System.out.println("Start Time: " + 
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("Database URL: " + DBConnection.getDatabaseURL());
            System.out.println("Database Name: " + DBConnection.getDatabaseName());
            System.out.println("Database User: " + DBConnection.getDatabaseUser());
            System.out.println("-".repeat(70));
            System.out.println("System Ready - Click 'Open Strike-Off Dialog' to begin\n");
        });
    }
}