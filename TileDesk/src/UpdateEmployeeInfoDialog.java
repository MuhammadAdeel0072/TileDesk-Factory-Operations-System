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
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * UpdateEmployeeInfoDialog - Modern Employee Update Interface
 * Connected to Tile Factory Database
 */
public class UpdateEmployeeInfoDialog extends JDialog {
    private static final Color SKY_BLUE = new Color(135, 206, 250);  // Blue for buttons
    private static final Color DARK_SKY_BLUE = new Color(30, 144, 255); // Hover blue
    private static final Color LIGHT_BLUE_BG = new Color(240, 248, 255);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(180, 200, 220); // Darker border for clear lines
    private static final Color INPUT_BORDER = new Color(200, 220, 240);
    private static final Color CANCEL_COLOR = new Color(150, 150, 150);
    private static final Color CANCEL_HOVER_RED = new Color(220, 80, 80); // Cancel turns red on hover
    private static final Color TABLE_HEADER_BG = new Color(245, 247, 250);
    private static final Color TABLE_SELECTION = new Color(225, 240, 255);
    private static final Color SCROLLBAR_COLOR = new Color(100, 100, 100); // Gray-black
    private static final Color TEXT_BLACK = new Color(40, 40, 40); // Consistent black
    private static final Color PLACEHOLDER_GRAY = new Color(150, 150, 150);
    private static final Color SEARCH_BG = new Color(255, 253, 245);
    private static final Color FILTER_PANEL_BG = new Color(230, 240, 255); // Light blue for filter panel
    
    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 15);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FILTER_LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 14);
    
    // Form fields
    private JTextField empIdField;
    private JTextField fullNameField;
    private JComboBox<String> roleComboBox;
    private JTextField phoneField;
    private JComboBox<String> statusComboBox;
    private JTextField dateField;
    private JTextField cnicField;
    private JTextField salaryField;
    private JTextArea addressArea;
    private JTextArea notesArea;
    
    // Search components
    private JTextField searchField;
    private JComboBox<String> searchFilterCombo;
    private JButton searchButton;
    private JButton clearSearchButton;
    
    // Table components
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Emp ID", "Full Name", "Role", "Phone", "Status", "Salary"}, 0);
    private final JTable table = new JTable(tableModel);
    private JLabel statusLabel; // For showing status messages
    
    // Button references
    private JButton updateButton;
    private JButton cancelButton;
    private JButton clearButton;
    
    // Employee data storage
    private List<EmployeeData> employeeList = new ArrayList<>();
    private EmployeeData currentEmployee = null;
    private EmployeeData originalData = null;
    
    // Database connection
    private Connection connection;
    
    // Formatters
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");
    
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
        private String email;
        private String emergencyContact;
        private String emergencyName;
        private String bankAccount;
        private String bankName;
        private int employee_id; // Database primary key
        
        public EmployeeData() {}
        
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
        public String getEmail() { return email; }
        public String getEmergencyContact() { return emergencyContact; }
        public String getEmergencyName() { return emergencyName; }
        public String getBankAccount() { return bankAccount; }
        public String getBankName() { return bankName; }
        public int getEmployeeIdDB() { return employee_id; }
        
        // Setters for editable fields
        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setRole(String role) { this.role = role; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setStatus(String status) { this.status = status; }
        public void setCnic(String cnic) { this.cnic = cnic; }
        public void setSalary(String salary) { this.salary = salary; }
        public void setAddress(String address) { this.address = address; }
        public void setNotes(String notes) { this.notes = notes; }
        public void setEmail(String email) { this.email = email; }
        public void setEmployeeIdDB(int id) { this.employee_id = id; }
        
        // Copy method
        public EmployeeData copy() {
            EmployeeData copy = new EmployeeData();
            copy.employeeId = this.employeeId;
            copy.fullName = this.fullName;
            copy.role = this.role;
            copy.phone = this.phone;
            copy.status = this.status;
            copy.joinDate = this.joinDate;
            copy.cnic = this.cnic;
            copy.salary = this.salary;
            copy.address = this.address;
            copy.notes = this.notes;
            copy.email = this.email;
            copy.emergencyContact = this.emergencyContact;
            copy.emergencyName = this.emergencyName;
            copy.bankAccount = this.bankAccount;
            copy.bankName = this.bankName;
            copy.employee_id = this.employee_id;
            return copy;
        }
        
        // Search helper method
        public boolean matchesSearch(String searchText, String filter) {
            searchText = searchText.toLowerCase();
            
            switch (filter) {
                case "All Fields":
                    return employeeId.toLowerCase().contains(searchText) ||
                           fullName.toLowerCase().contains(searchText) ||
                           role.toLowerCase().contains(searchText) ||
                           phone.contains(searchText) ||
                           status.toLowerCase().contains(searchText) ||
                           salary.toLowerCase().contains(searchText);
                case "Employee ID":
                    return employeeId.toLowerCase().contains(searchText);
                case "Full Name":
                    return fullName.toLowerCase().contains(searchText);
                case "Role":
                    return role.toLowerCase().contains(searchText);
                case "Phone":
                    return phone.contains(searchText);
                case "Status":
                    return status.toLowerCase().contains(searchText);
                default:
                    return false;
            }
        }
    }
    
    public UpdateEmployeeInfoDialog(JFrame ownerFrame, JPanel contentPanel) {
        super(ownerFrame, true);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Get database connection
        connection = DBConnection.getConnection();
        
        // Main card panel - DECREASED WIDTH
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20)); // Reduced padding
        card.setPreferredSize(new Dimension(1000, 700)); // More compact
        
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
        setSize(1000, 700); // More compact
        
        // Center dialog
        centerDialog(ownerFrame, contentPanel);
        
        // ESC to close
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Initialize data from database
        loadEmployeeDataFromDatabase();
        loadRolesFromDatabase();
        
        // Set focus to search field
        SwingUtilities.invokeLater(() -> searchField.requestFocus());
    }
    
    /* ================= DATABASE METHODS ================= */
    
    private void loadEmployeeDataFromDatabase() {
        employeeList.clear();
        tableModel.setRowCount(0);
        
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            
            String query = "SELECT e.employee_id, e.employee_code, e.full_name, " +
                          "COALESCE(r.role_name, 'No Role') as role_name, " +
                          "e.phone_number, e.email, e.cnic, e.address, " +
                          "e.employment_status, e.date_of_joining, " +
                          "e.monthly_salary, e.notes, " +
                          "e.emergency_contact, e.emergency_contact_name, " +
                          "e.bank_account_number, e.bank_name, " +
                          "COALESCE(d.department_name, 'No Department') as department_name " +
                          "FROM employees e " +
                          "LEFT JOIN roles r ON e.role_id = r.role_id " +
                          "LEFT JOIN departments d ON e.department_id = d.department_id " +
                          "WHERE e.employment_status != 'STRUCK_OFF' " +
                          "ORDER BY e.employee_id DESC";
            
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                EmployeeData emp = new EmployeeData();
                emp.setEmployeeIdDB(rs.getInt("employee_id"));
                emp.employeeId = rs.getString("employee_code");
                emp.fullName = rs.getString("full_name");
                emp.role = rs.getString("role_name");
                emp.phone = rs.getString("phone_number");
                emp.email = rs.getString("email");
                emp.cnic = rs.getString("cnic");
                emp.address = rs.getString("address");
                emp.status = convertStatus(rs.getString("employment_status"));
                emp.joinDate = rs.getDate("date_of_joining").toString();
                emp.salary = CURRENCY_FORMAT.format(rs.getDouble("monthly_salary"));
                emp.notes = rs.getString("notes");
                emp.emergencyContact = rs.getString("emergency_contact");
                emp.emergencyName = rs.getString("emergency_contact_name");
                emp.bankAccount = rs.getString("bank_account_number");
                emp.bankName = rs.getString("bank_name");
                
                employeeList.add(emp);
                
                // Add to table
                tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getFullName(),
                    emp.getRole(),
                    emp.getPhone(),
                    emp.getStatus(),
                    "PKR " + emp.getSalary()
                });
                
                count++;
            }
            rs.close();
            pstmt.close();
            
            statusLabel.setText("Total Employees: " + count + " | Select an employee from the table to edit");
            
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading employee data: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            
            JOptionPane.showMessageDialog(this,
                "Error loading employee data: " + e.getMessage() + 
                "\n\nTrying to reconnect...",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            
            // Try to reconnect
            connection = DBConnection.getConnectionWithRetry();
            if (connection != null) {
                loadEmployeeDataFromDatabase();
            }
        }
    }
    
    private void loadRolesFromDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            
            String query = "SELECT role_name FROM roles WHERE status = 'ACTIVE' ORDER BY role_name";
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            roleComboBox.removeAllItems();
            roleComboBox.addItem("Select Role");
            
            while (rs.next()) {
                roleComboBox.addItem(rs.getString("role_name"));
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Add default roles if database fails
            String[] defaultRoles = {"Select Role", "Manager", "Supervisor", "Sales Executive", 
                                   "Accountant", "Store Keeper", "Production Worker", "Driver", 
                                   "HR Manager", "IT Support", "Quality Control"};
            roleComboBox.removeAllItems();
            for (String role : defaultRoles) {
                roleComboBox.addItem(role);
            }
        }
    }
    
    private String convertStatus(String dbStatus) {
        if (dbStatus == null) return "Active";
        
        switch (dbStatus) {
            case "ACTIVE": return "Active";
            case "ON_LEAVE": return "On Leave";
            case "PROBATION": return "Probation";
            case "RESIGNED": return "Resigned";
            case "TERMINATED": return "Terminated";
            case "STRUCK_OFF": return "Struck Off";
            default: return "Active";
        }
    }
    
    private String convertToDBStatus(String uiStatus) {
        switch (uiStatus) {
            case "Active": return "ACTIVE";
            case "On Leave": return "ON_LEAVE";
            case "Probation": return "PROBATION";
            case "Resigned": return "RESIGNED";
            case "Terminated": return "TERMINATED";
            case "Struck Off": return "STRUCK_OFF";
            default: return "ACTIVE";
        }
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Title on left - SKY_BLUE
        JLabel title = new JLabel("Update Employee Information");
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
            new EmptyBorder(8, 8, 8, 8)
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
        JLabel tableTitle = new JLabel("Employees List - Click any row to edit");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(TEXT_BLACK);
        tableTitle.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(tableTitle, BorderLayout.NORTH);
        
        setupTable();
        
        JScrollPane tableScroll = new JScrollPane(table);
        styleTableScrollPane(tableScroll);
        tableScroll.setPreferredSize(new Dimension(550, 400));
        titlePanel.add(tableScroll, BorderLayout.CENTER);
        leftPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Status label for messages
        statusLabel = new JLabel("Loading employees...");
        statusLabel.setFont(STATUS_FONT);
        statusLabel.setForeground(new Color(100, 100, 120));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        leftPanel.add(statusLabel, BorderLayout.SOUTH);
        
        mainContent.add(leftPanel, BorderLayout.CENTER);
        
        /* ================= RIGHT PANEL - EDIT FORM ================= */
        JPanel rightPanel = createEditFormPanel();
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
        searchLabel.setFont(FILTER_LABEL_FONT);
        searchLabel.setForeground(SKY_BLUE);
        panel.add(searchLabel, gbc);
        
        // Search filter combo
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        String[] filters = {"All Fields", "Employee ID", "Full Name", "Role", "Phone", "Status"};
        searchFilterCombo = createStyledComboBox(filters, "Search by");
        searchFilterCombo.setPreferredSize(new Dimension(120, 36));
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
                setText("Search employees...");
                setCaretColor(DARK_SKY_BLUE);
                setPreferredSize(new Dimension(200, 36));
                
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
                            setText("Search employees...");
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
        searchButton.setPreferredSize(new Dimension(100, 36));
        searchButton.addActionListener(e -> performSearch());
        
        // Clear search button - Light Gray
        clearSearchButton = new JButton("Clear");
        styleButton(clearSearchButton, new Color(220, 220, 220), new Color(180, 180, 180));
        clearSearchButton.setForeground(TEXT_BLACK);
        clearSearchButton.setPreferredSize(new Dimension(90, 36));
        clearSearchButton.addActionListener(e -> clearSearch());
        
        buttonPanel.add(searchButton);
        buttonPanel.add(clearSearchButton);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createEditFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 2, true),
            new EmptyBorder(12, 12, 12, 12)
        ));
        panel.setPreferredSize(new Dimension(320, 0));
        
        // Form title - SKY_BLUE
        JLabel formTitle = new JLabel("Edit Employee Details");
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
        gbc.insets = new Insets(4, 3, 4, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Employee ID (Read-only)
        addLabel(formPanel, gbc, "Employee ID", true);
        gbc.gridy++;
        empIdField = createStyledTextField("Click table row to select", true);
        empIdField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        empIdField.setPreferredSize(new Dimension(250, 40));
        formPanel.add(empIdField, gbc);
        
        // Full Name
        gbc.gridy++;
        addLabel(formPanel, gbc, "Full Name", true);
        gbc.gridy++;
        fullNameField = createStyledTextField("Enter employee's full name", false);
        fullNameField.setPreferredSize(new Dimension(250, 40));
        formPanel.add(fullNameField, gbc);
        
        // Role
        gbc.gridy++;
        addLabel(formPanel, gbc, "Role", true);
        gbc.gridy++;
        roleComboBox = createStyledComboBox(new String[]{"Select Role"}, "Choose employee role");
        roleComboBox.setPreferredSize(new Dimension(250, 40));
        formPanel.add(roleComboBox, gbc);
        
        // Phone Number
        gbc.gridy++;
        addLabel(formPanel, gbc, "Phone Number", true);
        gbc.gridy++;
        phoneField = createStyledTextField("Enter phone number", false);
        phoneField.setPreferredSize(new Dimension(250, 40));
        formPanel.add(phoneField, gbc);
        
        // Status
        gbc.gridy++;
        addLabel(formPanel, gbc, "Status", true);
        gbc.gridy++;
        String[] statuses = {"Active", "On Leave", "Probation", "Resigned", "Terminated"};
        statusComboBox = createStyledComboBox(statuses, "Select status");
        statusComboBox.setPreferredSize(new Dimension(250, 40));
        formPanel.add(statusComboBox, gbc);
        
        // Date of Joining (Read-only)
        gbc.gridy++;
        addLabel(formPanel, gbc, "Join Date", true);
        gbc.gridy++;
        dateField = createStyledTextField(LocalDate.now().format(dateFormatter), true);
        dateField.setPreferredSize(new Dimension(250, 40));
        formPanel.add(dateField, gbc);
        
        // CNIC (Optional)
        gbc.gridy++;
        addLabel(formPanel, gbc, "CNIC", false);
        gbc.gridy++;
        cnicField = createStyledTextField("Optional: CNIC number", false);
        cnicField.setPreferredSize(new Dimension(250, 40));
        formPanel.add(cnicField, gbc);
        
        // Salary
        gbc.gridy++;
        addLabel(formPanel, gbc, "Monthly Salary", true);
        gbc.gridy++;
        salaryField = createStyledTextField("Enter salary amount", false);
        salaryField.setPreferredSize(new Dimension(250, 40));
        formPanel.add(salaryField, gbc);
        
        // Address (Optional)
        gbc.gridy++;
        addLabel(formPanel, gbc, "Address", false);
        gbc.gridy++;
        addressArea = createStyledTextArea("Optional: Enter address", 3);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        styleFormScrollPane(addressScroll);
        addressScroll.setPreferredSize(new Dimension(250, 80));
        formPanel.add(addressScroll, gbc);
        
        // Notes (Optional)
        gbc.gridy++;
        addLabel(formPanel, gbc, "Notes", false);
        gbc.gridy++;
        notesArea = createStyledTextArea("Optional: Additional notes", 2);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        styleFormScrollPane(notesScroll);
        notesScroll.setPreferredSize(new Dimension(250, 60));
        formPanel.add(notesScroll, gbc);
        
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
        table.setRowHeight(32);
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
        columnModel.getColumn(5).setPreferredWidth(100); // Salary
        
        // Row click → auto-fill form fields
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String empId = table.getValueAt(row, 0).toString();
                    EmployeeData emp = findEmployeeById(empId);
                    
                    if (emp != null) {
                        currentEmployee = emp;
                        originalData = emp.copy();
                        
                        // Fill form fields
                        empIdField.setText(emp.getEmployeeId());
                        empIdField.setForeground(TEXT_BLACK);
                        
                        fullNameField.setText(emp.getFullName());
                        fullNameField.setForeground(TEXT_BLACK);
                        
                        // Set role
                        boolean roleFound = false;
                        for (int i = 0; i < roleComboBox.getItemCount(); i++) {
                            if (roleComboBox.getItemAt(i).equals(emp.getRole())) {
                                roleComboBox.setSelectedIndex(i);
                                roleFound = true;
                                break;
                            }
                        }
                        if (!roleFound) {
                            roleComboBox.setSelectedIndex(0);
                        }
                        
                        phoneField.setText(emp.getPhone());
                        phoneField.setForeground(TEXT_BLACK);
                        
                        // Set status
                        boolean statusFound = false;
                        for (int i = 0; i < statusComboBox.getItemCount(); i++) {
                            if (statusComboBox.getItemAt(i).equals(emp.getStatus())) {
                                statusComboBox.setSelectedIndex(i);
                                statusFound = true;
                                break;
                            }
                        }
                        if (!statusFound) {
                            statusComboBox.setSelectedIndex(0);
                        }
                        
                        dateField.setText(emp.getJoinDate());
                        
                        cnicField.setText(emp.getCnic() == null || emp.getCnic().isEmpty() ? 
                                         "Optional: CNIC number" : emp.getCnic());
                        cnicField.setForeground(emp.getCnic() == null || emp.getCnic().isEmpty() ? 
                                               PLACEHOLDER_GRAY : TEXT_BLACK);
                        
                        salaryField.setText(emp.getSalary());
                        salaryField.setForeground(TEXT_BLACK);
                        
                        addressArea.setText(emp.getAddress() == null || emp.getAddress().isEmpty() ? 
                                           "Optional: Enter address" : emp.getAddress());
                        addressArea.setForeground(emp.getAddress() == null || emp.getAddress().isEmpty() ? 
                                                 PLACEHOLDER_GRAY : TEXT_BLACK);
                        
                        notesArea.setText(emp.getNotes() == null || emp.getNotes().isEmpty() ? 
                                         "Optional: Additional notes" : emp.getNotes());
                        notesArea.setForeground(emp.getNotes() == null || emp.getNotes().isEmpty() ? 
                                               PLACEHOLDER_GRAY : TEXT_BLACK);
                        
                        // Enable form and buttons
                        setFormEnabled(true);
                        updateButton.setEnabled(true);
                        
                        // Update status message
                        statusLabel.setText("Editing: " + emp.getFullName() + " (ID: " + emp.getEmployeeId() + ")");
                        statusLabel.setForeground(new Color(0, 100, 0));
                    }
                }
            }
        });
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        // Update button - SKY_BLUE
        updateButton = createStyledButton("💾 Update Employee", SKY_BLUE, DARK_SKY_BLUE);
        updateButton.setPreferredSize(new Dimension(160, 45));
        updateButton.addActionListener(e -> performUpdate());
        updateButton.setEnabled(false);
        
        // Cancel button - Turns red on hover
        cancelButton = createStyledButton("✕ Cancel", CANCEL_COLOR, CANCEL_HOVER_RED);
        cancelButton.setPreferredSize(new Dimension(120, 45));
        cancelButton.addActionListener(e -> dispose());
        
        // Clear button - Sky Blue
        clearButton = createStyledButton("🗑️ Clear Form", SKY_BLUE, DARK_SKY_BLUE);
        clearButton.setPreferredSize(new Dimension(140, 45));
        clearButton.addActionListener(e -> clearForm());
        clearButton.setToolTipText("Clear all form fields");
        
        panel.add(updateButton);
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
        btn.setPreferredSize(new Dimension(35, 35));
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
            star.setForeground(new Color(220, 53, 69));
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
        switch (status) {
            case "Active": return new Color(40, 167, 69); // Green
            case "On Leave": return new Color(255, 193, 7); // Yellow
            case "Probation": return new Color(0, 123, 255); // Blue
            case "Resigned": return new Color(108, 117, 125); // Gray
            case "Terminated": return new Color(220, 53, 69); // Red
            default: return Color.WHITE;
        }
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim();
        String filter = (String) searchFilterCombo.getSelectedItem();
        
        if (searchText.equals("Search employees...") || searchText.isEmpty()) {
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
                    emp.getStatus(),
                    "PKR " + emp.getSalary()
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
        } else {
            statusLabel.setText("Found " + foundCount + " employee(s) for: '" + searchText + "' in " + filter);
            statusLabel.setForeground(new Color(0, 100, 0));
            
            // Clear form since search results might be different
            clearForm();
            setFormEnabled(false);
            updateButton.setEnabled(false);
        }
    }
    
    private void clearSearch() {
        searchField.setText("Search employees...");
        searchField.setForeground(PLACEHOLDER_GRAY);
        searchFilterCombo.setSelectedIndex(0);
        
        // Reload all employees
        loadEmployeeDataFromDatabase();
        
        // Clear form
        clearForm();
        setFormEnabled(false);
        updateButton.setEnabled(false);
    }
    
    private void clearForm() {
        empIdField.setText("Click table row to select");
        empIdField.setForeground(PLACEHOLDER_GRAY);
        
        fullNameField.setText("Enter employee's full name");
        fullNameField.setForeground(PLACEHOLDER_GRAY);
        
        roleComboBox.setSelectedIndex(0);
        roleComboBox.setForeground(PLACEHOLDER_GRAY);
        
        phoneField.setText("Enter phone number");
        phoneField.setForeground(PLACEHOLDER_GRAY);
        
        statusComboBox.setSelectedIndex(0);
        statusComboBox.setForeground(PLACEHOLDER_GRAY);
        
        dateField.setText(LocalDate.now().format(dateFormatter));
        
        cnicField.setText("Optional: CNIC number");
        cnicField.setForeground(PLACEHOLDER_GRAY);
        
        salaryField.setText("Enter salary amount");
        salaryField.setForeground(PLACEHOLDER_GRAY);
        
        addressArea.setText("Optional: Enter address");
        addressArea.setForeground(PLACEHOLDER_GRAY);
        
        notesArea.setText("Optional: Additional notes");
        notesArea.setForeground(PLACEHOLDER_GRAY);
        
        currentEmployee = null;
        originalData = null;
        table.clearSelection();
    }
    
    private void setFormEnabled(boolean enabled) {
        fullNameField.setEditable(enabled);
        roleComboBox.setEnabled(enabled);
        phoneField.setEditable(enabled);
        statusComboBox.setEnabled(enabled);
        cnicField.setEditable(enabled);
        salaryField.setEditable(enabled);
        addressArea.setEditable(enabled);
        notesArea.setEditable(enabled);
    }
    
    private void performUpdate() {
        // Validate required fields
        if (fullNameField.getForeground().equals(PLACEHOLDER_GRAY) || 
            fullNameField.getText().trim().isEmpty()) {
            showError("Full Name is required");
            fullNameField.requestFocus();
            return;
        }
        
        if (roleComboBox.getSelectedIndex() == 0 || 
            "Select Role".equals(roleComboBox.getSelectedItem())) {
            showError("Please select a Role");
            roleComboBox.requestFocus();
            return;
        }
        
        if (phoneField.getForeground().equals(PLACEHOLDER_GRAY) || 
            phoneField.getText().trim().isEmpty()) {
            showError("Phone Number is required");
            phoneField.requestFocus();
            return;
        }
        
        // Validate phone number format
        String phone = phoneField.getText().trim();
        if (!phone.matches("\\d{11}")) {
            showError("Phone number must be 11 digits");
            phoneField.requestFocus();
            return;
        }
        
        // Validate salary
        String salaryText = salaryField.getText().trim();
        if (salaryField.getForeground().equals(PLACEHOLDER_GRAY) || 
            salaryText.isEmpty()) {
            showError("Monthly Salary is required");
            salaryField.requestFocus();
            return;
        }
        
        try {
            double salary = Double.parseDouble(salaryText.replaceAll(",", ""));
            if (salary <= 0) {
                showError("Salary must be a positive number");
                salaryField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            showError("Invalid salary amount. Please enter a valid number");
            salaryField.requestFocus();
            return;
        }
        
        // Update current employee object
        currentEmployee.setFullName(fullNameField.getText().trim());
        currentEmployee.setRole((String) roleComboBox.getSelectedItem());
        currentEmployee.setPhone(phoneField.getText().trim());
        currentEmployee.setStatus((String) statusComboBox.getSelectedItem());
        
        // Handle optional fields
        String cnic = cnicField.getForeground().equals(PLACEHOLDER_GRAY) ? "" : cnicField.getText().trim();
        currentEmployee.setCnic(cnic);
        
        String salary = salaryField.getForeground().equals(PLACEHOLDER_GRAY) ? "" : 
                       String.format("%.0f", Double.parseDouble(salaryField.getText().trim().replaceAll(",", "")));
        currentEmployee.setSalary(salary);
        
        String address = addressArea.getForeground().equals(PLACEHOLDER_GRAY) ? "" : addressArea.getText().trim();
        currentEmployee.setAddress(address);
        
        String notes = notesArea.getForeground().equals(PLACEHOLDER_GRAY) ? "" : notesArea.getText().trim();
        currentEmployee.setNotes(notes);
        
        // Update database
        if (updateEmployeeInDatabase()) {
            // Update table
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(currentEmployee.getEmployeeId())) {
                    tableModel.setValueAt(currentEmployee.getFullName(), i, 1);
                    tableModel.setValueAt(currentEmployee.getRole(), i, 2);
                    tableModel.setValueAt(currentEmployee.getPhone(), i, 3);
                    tableModel.setValueAt(currentEmployee.getStatus(), i, 4);
                    tableModel.setValueAt("PKR " + currentEmployee.getSalary(), i, 5);
                    break;
                }
            }
            
            // Show success message
            showStatusMessage("✅ Successfully updated: " + currentEmployee.getFullName() + 
                             " (ID: " + currentEmployee.getEmployeeId() + ")", false);
            
            JOptionPane.showMessageDialog(this,
                "✅ Employee updated successfully in database!\n\n" +
                "Name: " + currentEmployee.getFullName() + "\n" +
                "ID: " + currentEmployee.getEmployeeId() + "\n" +
                "Role: " + currentEmployee.getRole() + "\n" +
                "Status: " + currentEmployee.getStatus() + "\n" +
                "Salary: PKR " + currentEmployee.getSalary(),
                "Update Complete",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Update original data for potential reset
            originalData = currentEmployee.copy();
        } else {
            showError("Failed to update employee in database. Please try again.");
        }
    }
    
    private boolean updateEmployeeInDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            
            // First, get role_id from role_name
            String roleIdQuery = "SELECT role_id FROM roles WHERE role_name = ? LIMIT 1";
            PreparedStatement roleStmt = connection.prepareStatement(roleIdQuery);
            roleStmt.setString(1, currentEmployee.getRole());
            ResultSet roleRs = roleStmt.executeQuery();
            
            Integer roleId = null;
            if (roleRs.next()) {
                roleId = roleRs.getInt("role_id");
            }
            roleRs.close();
            roleStmt.close();
            
            // Update employee in database using stored procedure
            String callProcedure = "CALL sp_update_employee(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            CallableStatement cstmt = connection.prepareCall(callProcedure);
            
            cstmt.setInt(1, currentEmployee.getEmployeeIdDB()); // employee_id
            cstmt.setString(2, currentEmployee.getFullName()); // full_name
            cstmt.setString(3, currentEmployee.getPhone()); // phone_number
            cstmt.setString(4, currentEmployee.getEmail() != null ? currentEmployee.getEmail() : ""); // email
            cstmt.setString(5, currentEmployee.getAddress()); // address
            cstmt.setInt(6, roleId != null ? roleId : -1); // role_id (use -1 if null)
            cstmt.setDouble(7, Double.parseDouble(currentEmployee.getSalary().replaceAll(",", ""))); // monthly_salary
            cstmt.setString(8, convertToDBStatus(currentEmployee.getStatus())); // employment_status
            cstmt.setString(9, currentEmployee.getNotes()); // notes
            
            cstmt.execute();
            cstmt.close();
            
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Database update error: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (NumberFormatException e) {
            showError("Invalid salary format");
            return false;
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
            
            // Draw main card with clear border
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // Draw clear border
            g2.setColor(new Color(180, 200, 220));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    /* ================= TEST METHOD ================= */
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
            
            JFrame frame = new JFrame("Update Employee Info Dialog Test");
            frame.setSize(1100, 750);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(new Color(240, 248, 255));
            
            JButton testBtn = new JButton("Open Update Employee Info Dialog");
            testBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            testBtn.setBackground(new Color(135, 206, 250));
            testBtn.setForeground(Color.WHITE);
            testBtn.setFocusPainted(false);
            testBtn.setPreferredSize(new Dimension(300, 50));
            testBtn.addActionListener(e -> {
                UpdateEmployeeInfoDialog dlg = new UpdateEmployeeInfoDialog(frame, null);
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