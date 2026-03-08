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
 * EmployeeReportDialog - Modern Employee Report Form with Database Integration
 * Integrated with actual tile_factory_db database
 */
public class EmployeeReportDialog extends JDialog {
    // Colors - exactly matching other dialogs
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
    private static final Color ACTIVE_COLOR = new Color(40, 167, 69);
    private static final Color LEAVE_COLOR = new Color(255, 193, 7);
    private static final Color RESIGNED_COLOR = new Color(108, 117, 125);
    private static final Color TABLE_HEADER_BG = new Color(245, 245, 245);
    private static final Color TABLE_HEADER_FG = Color.BLACK;
    
    // Form fields
    private JComboBox<String> reportTypeCombo;
    private JComboBox<String> departmentCombo;
    private JComboBox<String> statusCombo;
    private JComboBox<String> roleCombo;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> formatCombo;
    private JTextField minSalaryField;
    private JTextField maxSalaryField;
    private JCheckBox includeContactCheck;
    private JCheckBox includeSalaryCheck;
    private JCheckBox includeAttendanceCheck;
    private JTextArea notesArea;
    
    // Table for preview
    private JTable previewTable;
    
    // Database connection removed
    // private Connection connection;
    
    // Summary labels for dynamic updates (matching other dialogs)
    private JLabel totalEmployeesLabel;
    private JLabel activeStaffLabel;
    private JLabel avgSalaryLabel;
    private JLabel departmentsLabel;
    
    // Date formatter - consistent with other dialogs
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Fonts (exactly matching other dialogs)
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
    public EmployeeReportDialog(JFrame ownerFrame, JPanel contentPanel) {
        super(ownerFrame, true);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Database initialization removed
        /*
        try {
            this.connection = DBConnection.getConnection();
        } catch (Exception e) {
            System.err.println("Failed to get database connection: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to connect to database. Please check your connection settings.",
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        */
        
        // Main card panel - Same size as other dialogs
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
        
        // Set default dates
        setDefaultDates();
        
        // Load dynamic data from database
        loadDepartments();
        loadRoles();
        refreshPreview();
        
        // Set focus to first field
        SwingUtilities.invokeLater(() -> reportTypeCombo.requestFocus());
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 25, 0));
        
        // Title on left
        JLabel title = new JLabel("Factory Employee Report");
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
    
    /* ================= DATABASE METHODS ================= */
    
    private List<String> getDepartmentsFromDB() {
        List<String> departments = new ArrayList<>();
        departments.add("All Departments"); // Add "All Departments" option first
        
        // Add default departments as fallback (DB connectivity removed)
        departments.add("Production");
        departments.add("Quality Control");
        departments.add("Maintenance");
        departments.add("Warehouse");
        departments.add("Administration");
        departments.add("Sales");
        departments.add("HR");
        departments.add("Research & Development");
        departments.add("Shipping");
        departments.add("Security");
        
        return departments;
    }
    
    private List<String> getRolesFromDB() {
        List<String> roles = new ArrayList<>();
        roles.add("All Roles"); // Add "All Roles" option first
        
        // Add default roles as fallback (DB connectivity removed)
        roles.add("Factory Manager");
        roles.add("Production Supervisor");
        roles.add("Quality Control Inspector");
        roles.add("Machine Operator");
        roles.add("Maintenance Technician");
        roles.add("Warehouse Manager");
        roles.add("Sales Executive");
        roles.add("HR Manager");
        roles.add("Security Officer");
        roles.add("Research Specialist");
        
        return roles;
    }
    
    private List<EmployeeData> getEmployeesFromDB() {
        List<EmployeeData> employees = new ArrayList<>();
        // DB connectivity removed
        return employees;
    }
    
    private void loadDepartments() {
        List<String> departments = getDepartmentsFromDB();
        departmentCombo.setModel(new DefaultComboBoxModel<>(departments.toArray(new String[0])));
    }
    
    private void loadRoles() {
        List<String> roles = getRolesFromDB();
        roleCombo.setModel(new DefaultComboBoxModel<>(roles.toArray(new String[0])));
    }
    
    private List<EmployeeData> getFilteredEmployees() {
        List<EmployeeData> allEmployees = getEmployeesFromDB();
        if (allEmployees.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<EmployeeData> filtered = new ArrayList<>();
        String department = (String) departmentCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();
        String role = (String) roleCombo.getSelectedItem();
        
        // Parse salary range
        double minSalary = 0;
        double maxSalary = Double.MAX_VALUE;
        
        try {
            if (minSalaryField.getText() != null && !minSalaryField.getText().trim().isEmpty()) {
                minSalary = Double.parseDouble(minSalaryField.getText().trim());
            }
        } catch (NumberFormatException e) {
            minSalary = 0;
        }
        
        try {
            if (maxSalaryField.getText() != null && !maxSalaryField.getText().trim().isEmpty()) {
                maxSalary = Double.parseDouble(maxSalaryField.getText().trim());
            }
        } catch (NumberFormatException e) {
            maxSalary = Double.MAX_VALUE;
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
        for (EmployeeData employee : allEmployees) {
            // Department filter
            if (department != null && !department.equals("All Departments") && 
                !department.equals(employee.department)) {
                continue;
            }
            
            // Status filter
            if (status != null && !status.equals("All Status") && 
                !status.equals(employee.status)) {
                continue;
            }
            
            // Role filter
            if (role != null && !role.equals("All Roles") && 
                !role.equals(employee.role)) {
                continue;
            }
            
            // Salary filter
            if (employee.salary < minSalary || employee.salary > maxSalary) {
                continue;
            }
            
            // Date filter (for joining date)
            if (startDate != null && employee.joiningDate.isBefore(startDate)) {
                continue;
            }
            if (endDate != null && employee.joiningDate.isAfter(endDate)) {
                continue;
            }
            
            filtered.add(employee);
        }
        
        return filtered;
    }
    
    /* ================= DATA MANAGEMENT ================= */
    
    private void refreshPreview() {
        SwingUtilities.invokeLater(() -> {
            List<EmployeeData> employees = getFilteredEmployees();
            
            // Calculate totals
            int totalEmployees = employees.size();
            int activeStaff = 0;
            double totalSalary = 0;
            int departmentsCount = 0;
            List<String> uniqueDepartments = new ArrayList<>();
            
            for (EmployeeData employee : employees) {
                if ("ACTIVE".equalsIgnoreCase(employee.status)) {
                    activeStaff++;
                }
                totalSalary += employee.salary;
                if (employee.department != null && !uniqueDepartments.contains(employee.department)) {
                    uniqueDepartments.add(employee.department);
                }
            }
            
            departmentsCount = uniqueDepartments.size();
            double avgSalary = totalEmployees > 0 ? totalSalary / totalEmployees : 0;
            
            // Update summary panel
            updateSummaryPanel(totalEmployees, activeStaff, avgSalary, departmentsCount);
            
            // Update preview table
            updatePreviewTable(employees);
        });
    }
    
    private void updateSummaryPanel(int totalEmployees, int activeStaff, double avgSalary, int departmentsCount) {
        if (totalEmployeesLabel != null) {
            totalEmployeesLabel.setText(totalEmployees + " Employees");
            activeStaffLabel.setText(activeStaff + " Active");
            avgSalaryLabel.setText(String.format("PKR %,.0f", avgSalary));
            departmentsLabel.setText(departmentsCount + " Depts");
        }
    }
    
    private void updatePreviewTable(List<EmployeeData> employees) {
        String[] columns = {"Emp ID", "Name", "Department", "Role", "Status", "Salary (PKR)", "Joining Date"};
        Object[][] data = new Object[employees.size()][columns.length];
        
        for (int i = 0; i < employees.size(); i++) {
            EmployeeData employee = employees.get(i);
            data[i][0] = employee.empId;
            data[i][1] = employee.name;
            data[i][2] = employee.department;
            data[i][3] = employee.role;
            data[i][4] = formatStatus(employee.status);
            data[i][5] = String.format("%,.0f", employee.salary);
            data[i][6] = employee.joiningDate.format(dateFormatter);
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
            int[] columnWidths = {100, 140, 120, 140, 100, 120, 120};
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
            previewTable.getColumnModel().getColumn(4).setCellRenderer(new EmployeeStatusRenderer());
            
            // Update table header
            JTableHeader header = previewTable.getTableHeader();
            header.repaint();
        }
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
        
        // Report Type (Required) - No "Select" placeholder to match other dialogs
        addLabel(panel, gbc, "Report Type", true);
        gbc.gridy++;
        String[] reportTypes = {"Employee Directory", 
                               "Department-wise Report", 
                               "Salary Report", 
                               "Attendance Summary", 
                               "Performance Review", 
                               "Joining Report", 
                               "Resignation Report", 
                               "Leave Balance Report", 
                               "Training Report"};
        reportTypeCombo = createStyledComboBox(reportTypes, "Choose report type");
        reportTypeCombo.addActionListener(e -> refreshPreview());
        panel.add(reportTypeCombo, gbc);
        
        // Department (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Department", false);
        gbc.gridy++;
        departmentCombo = createStyledComboBox(new String[]{"Loading..."}, "Filter by department");
        departmentCombo.addActionListener(e -> refreshPreview());
        panel.add(departmentCombo, gbc);
        
        // Employee Status (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Employee Status", false);
        gbc.gridy++;
        String[] statuses = {"All Status", "Active", "On Leave", "Probation", 
                            "Resigned", "Terminated", "Struck Off"};
        statusCombo = createStyledComboBox(statuses, "Filter by status");
        statusCombo.addActionListener(e -> refreshPreview());
        panel.add(statusCombo, gbc);
        
        // Role/Designation (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Role/Designation", false);
        gbc.gridy++;
        roleCombo = createStyledComboBox(new String[]{"Loading..."}, "Filter by role");
        roleCombo.addActionListener(e -> refreshPreview());
        panel.add(roleCombo, gbc);
        
        // Start Date (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Start Date", false);
        gbc.gridy++;
        startDateField = createStyledTextField("YYYY-MM-DD", false);
        startDateField.setToolTipText("Format: YYYY-MM-DD (same as other reports)");
        startDateField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> refreshPreview()));
        panel.add(startDateField, gbc);
        
        // End Date (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "End Date", false);
        gbc.gridy++;
        endDateField = createStyledTextField("YYYY-MM-DD", false);
        endDateField.setToolTipText("Format: YYYY-MM-DD (same as other reports)");
        endDateField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> refreshPreview()));
        panel.add(endDateField, gbc);
        
        // Output Format (Required) - No "Select" placeholder
        gbc.gridy++;
        addLabel(panel, gbc, "Output Format", true);
        gbc.gridy++;
        String[] formats = {"PDF Document", "Excel Spreadsheet", "CSV File", 
                           "HTML Report", "Printable PDF"};
        formatCombo = createStyledComboBox(formats, "Select output format");
        panel.add(formatCombo, gbc);
        
        // Salary Range (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Salary Range (PKR)", false);
        gbc.gridy++;
        
        JPanel salaryPanel = new JPanel(new GridBagLayout());
        salaryPanel.setOpaque(false);
        salaryPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        GridBagConstraints gbcSalary = new GridBagConstraints();
        gbcSalary.fill = GridBagConstraints.HORIZONTAL;
        gbcSalary.insets = new Insets(0, 5, 0, 5);
        
        // Min Salary
        gbcSalary.gridx = 0;
        gbcSalary.gridy = 0;
        gbcSalary.weightx = 0.0;
        JLabel minLabel = new JLabel("Min:");
        minLabel.setFont(LABEL_FONT);
        minLabel.setForeground(LABEL_COLOR);
        salaryPanel.add(minLabel, gbcSalary);
        
        gbcSalary.gridx = 1;
        gbcSalary.weightx = 1.0;
        minSalaryField = createStyledTextField("0", false);
        minSalaryField.setPreferredSize(new Dimension(120, 52));
        minSalaryField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> refreshPreview()));
        salaryPanel.add(minSalaryField, gbcSalary);
        
        // Spacer
        gbcSalary.gridx = 2;
        gbcSalary.weightx = 0.2;
        salaryPanel.add(Box.createHorizontalStrut(20), gbcSalary);
        
        // Max Salary
        gbcSalary.gridx = 3;
        gbcSalary.weightx = 0.0;
        JLabel maxLabel = new JLabel("Max:");
        maxLabel.setFont(LABEL_FONT);
        maxLabel.setForeground(LABEL_COLOR);
        salaryPanel.add(maxLabel, gbcSalary);
        
        gbcSalary.gridx = 4;
        gbcSalary.weightx = 1.0;
        maxSalaryField = createStyledTextField("", false);
        maxSalaryField.setPreferredSize(new Dimension(120, 52));
        maxSalaryField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> refreshPreview()));
        salaryPanel.add(maxSalaryField, gbcSalary);
        
        panel.add(salaryPanel, gbc);
        
        // Status Indicators Panel
        gbc.gridy++;
        addLabel(panel, gbc, "Status Indicators", false);
        gbc.gridy++;
        
        JPanel statusPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Color indicators for employee statuses
        JPanel activePanel = createStatusIndicatorPanel("Active", ACTIVE_COLOR);
        JPanel leavePanel = createStatusIndicatorPanel("On Leave/Probation", LEAVE_COLOR);
        JPanel resignedPanel = createStatusIndicatorPanel("Resigned/Terminated", RESIGNED_COLOR);
        
        statusPanel.add(activePanel);
        statusPanel.add(leavePanel);
        statusPanel.add(resignedPanel);
        
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
        includeContactCheck = createStyledCheckBox("Include Contact Info");
        includeContactCheck.setSelected(true);
        includeContactCheck.addActionListener(e -> refreshPreview());
        optionsPanel.add(includeContactCheck, gbcOpt);
        
        gbcOpt.gridy = 1;
        includeSalaryCheck = createStyledCheckBox("Include Salary Details");
        includeSalaryCheck.addActionListener(e -> refreshPreview());
        optionsPanel.add(includeSalaryCheck, gbcOpt);
        
        gbcOpt.gridy = 2;
        includeAttendanceCheck = createStyledCheckBox("Include Attendance");
        includeAttendanceCheck.addActionListener(e -> refreshPreview());
        optionsPanel.add(includeAttendanceCheck, gbcOpt);
        
        panel.add(optionsPanel, gbc);
        
        // Preview Table
        gbc.gridy++;
        addLabel(panel, gbc, "Employee Preview", false);
        gbc.gridy++;
        
        // Create table with actual data
        String[] columns = {"Emp ID", "Name", "Department", "Role", "Status", "Salary (PKR)", "Joining Date"};
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
        int[] columnWidths = {100, 140, 120, 140, 100, 120, 120};
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
        
        // Status indicator (exactly matching other dialogs)
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
        
        String[] titles = {"Total Employees", "Active Staff", "Avg. Salary", "Departments"};
        Color[] colors = {DARK_SKY_BLUE, SUCCESS_GREEN, WARNING_YELLOW, new Color(111, 66, 193)};
        
        // Create stat cards (matching other dialogs structure)
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
            
            // Store references for dynamic updates (matching other dialogs)
            switch (i) {
                case 0: totalEmployeesLabel = valueLabel; break;
                case 1: activeStaffLabel = valueLabel; break;
                case 2: avgSalaryLabel = valueLabel; break;
                case 3: departmentsLabel = valueLabel; break;
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
        
        JLabel descriptionLabel = new JLabel(getStatusDescription(title));
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionLabel.setForeground(OPTIONAL_COLOR);
        
        panel.add(colorIndicator, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(descriptionLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private String getStatusDescription(String status) {
        switch (status) {
            case "Active": return "Currently working";
            case "On Leave/Probation": return "Temporary status";
            case "Resigned/Terminated": return "Left the company";
            default: return "";
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 0, 0, 0));
        
        // Generate button (exactly matching other dialogs)
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
    // These methods are now identical to other dialogs
    
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
        
        // Custom scrollbar (matching other dialogs)
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
        LocalDate threeMonthsAgo = today.minusMonths(3);
        
        startDateField.setText(threeMonthsAgo.format(dateFormatter));
        startDateField.setForeground(new Color(40, 40, 40));
        ((javax.swing.text.JTextComponent) startDateField).setCaretPosition(0);
        
        endDateField.setText(today.format(dateFormatter));
        endDateField.setForeground(new Color(40, 40, 40));
        ((javax.swing.text.JTextComponent) endDateField).setCaretPosition(0);
    }
    
    private void onGenerate() {
        // Validate required fields (matching other dialogs structure)
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
        
        // Validate salary range
        double minSalary;
        double maxSalary;
        
        try {
            minSalary = minSalaryField.getText().trim().isEmpty() ? 0 : 
                Double.parseDouble(minSalaryField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Invalid minimum salary. Please enter a numeric value");
            minSalaryField.requestFocus();
            return;
        }
        
        try {
            maxSalary = maxSalaryField.getText().trim().isEmpty() ? Double.MAX_VALUE : 
                Double.parseDouble(maxSalaryField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Invalid maximum salary. Please enter a numeric value");
            maxSalaryField.requestFocus();
            return;
        }
        
        if (maxSalary < minSalary) {
            showError("Maximum salary must be greater than or equal to minimum salary");
            maxSalaryField.requestFocus();
            return;
        }
        
        // Generate report with actual data
        generateActualReport();
    }
    
    private void generateActualReport() {
        JOptionPane.showMessageDialog(this, 
            "✅ Employee Report parameters captured locally (DB connectivity removed)!",
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void saveReportToDatabase(String reportType, String department, 
                                      int totalEmployees, int activeStaff, 
                                      double avgSalary, double totalSalary) {
        // DB connectivity removed
    }
    
    private void onPreview() {
        List<EmployeeData> employees = getFilteredEmployees();
        
        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "⚠️ No employee data to preview",
                "No Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show a preview dialog
        JDialog previewDialog = new JDialog(this, "Employee Report Preview", true);
        previewDialog.setUndecorated(true);
        previewDialog.getContentPane().setBackground(LIGHT_BLUE_BG);
        previewDialog.setLayout(new BorderLayout());
        
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(700, 500));
        
        JLabel title = new JLabel("📋 Employee Report Preview");
        title.setFont(TITLE_FONT);
        title.setForeground(DARK_SKY_BLUE);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JTextArea previewText = new JTextArea();
        previewText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        previewText.setText(generatePreviewText(employees));
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
        List<EmployeeData> employees = getFilteredEmployees();
        
        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "⚠️ No employee data to export",
                "No Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Employee Report Data");
        
        // Set default file name
        String fileName = "Employee_Report_" + LocalDate.now().format(dateFormatter) + ".csv";
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
            exportToCSV(fileToSave, employees);
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "✅ Employee report data exported successfully!\n\n" +
                "File: " + fileToSave.getName() + "\n" +
                "Location: " + fileToSave.getParent() + "\n" +
                "Records: " + employees.size() + " employees",
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void exportToCSV(java.io.File file, List<EmployeeData> employees) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
            // Write CSV header
            writer.println("Employee Code,Full Name,Department,Role,Status,Salary,Joining Date,Phone,Email,Address");
            
            // Write data
            for (EmployeeData employee : employees) {
                writer.println(String.format("%s,%s,%s,%s,%s,%.2f,%s,%s,%s,%s",
                    employee.empId,
                    escapeCSV(employee.name),
                    escapeCSV(employee.department != null ? employee.department : ""),
                    escapeCSV(employee.role != null ? employee.role : ""),
                    formatStatus(employee.status),
                    employee.salary,
                    employee.joiningDate.format(dateFormatter),
                    escapeCSV(employee.phone != null ? employee.phone : ""),
                    escapeCSV(employee.email != null ? employee.email : ""),
                    escapeCSV(employee.address != null ? employee.address : "")));
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
    
    private String generatePreviewText(List<EmployeeData> employees) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(70)).append("\n");
        sb.append("               FACTORY EMPLOYEE REPORT PREVIEW\n");
        sb.append("=".repeat(70)).append("\n\n");
        
        sb.append("Report Type    : ").append(reportTypeCombo.getSelectedItem()).append("\n");
        sb.append("Department     : ").append(departmentCombo.getSelectedItem()).append("\n");
        sb.append("Status Filter  : ").append(statusCombo.getSelectedItem()).append("\n");
        sb.append("Role Filter    : ").append(roleCombo.getSelectedItem()).append("\n");
        
        if (!startDateField.getText().equals("YYYY-MM-DD")) {
            sb.append("Start Date     : ").append(startDateField.getText()).append("\n");
            sb.append("End Date       : ").append(endDateField.getText()).append("\n");
        }
        
        sb.append("Format         : ").append(formatCombo.getSelectedItem()).append("\n");
        sb.append("Generated On   : ").append(LocalDate.now().format(dateFormatter)).append("\n");
        sb.append("-".repeat(70)).append("\n\n");
        
        // Calculate totals
        int totalEmployees = employees.size();
        int activeStaff = 0;
        double totalSalary = 0;
        int departmentsCount = 0;
        List<String> uniqueDepartments = new ArrayList<>();
        
        for (EmployeeData employee : employees) {
            if ("ACTIVE".equalsIgnoreCase(employee.status)) {
                activeStaff++;
            }
            totalSalary += employee.salary;
            if (employee.department != null && !uniqueDepartments.contains(employee.department)) {
                uniqueDepartments.add(employee.department);
            }
        }
        
        departmentsCount = uniqueDepartments.size();
        double avgSalary = totalEmployees > 0 ? totalSalary / totalEmployees : 0;
        double activeRate = totalEmployees > 0 ? (activeStaff * 100.0 / totalEmployees) : 0;
        
        sb.append("FACTORY HR OVERVIEW\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append(String.format("Total Employees        : %d\n", totalEmployees));
        sb.append(String.format("Active Staff           : %d (%.1f%%)\n", activeStaff, activeRate));
        sb.append(String.format("Average Salary         : PKR %,.2f\n", avgSalary));
        sb.append(String.format("Total Monthly Payroll  : PKR %,.2f\n", totalSalary));
        sb.append(String.format("Departments            : %d\n\n", departmentsCount));
        
        sb.append("EMPLOYEE DETAILS\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append(String.format("%-10s %-20s %-15s %-15s %-10s %-12s\n", 
            "Emp ID", "Name", "Department", "Role", "Status", "Salary"));
        sb.append("-".repeat(70)).append("\n");
        
        for (EmployeeData employee : employees) {
            sb.append(String.format("%-10s %-20s %-15s %-15s %-10s %-12s\n",
                employee.empId,
                employee.name.length() > 20 ? employee.name.substring(0, 17) + "..." : employee.name,
                employee.department != null && employee.department.length() > 15 ? 
                    employee.department.substring(0, 12) + "..." : (employee.department != null ? employee.department : "N/A"),
                employee.role != null && employee.role.length() > 15 ? 
                    employee.role.substring(0, 12) + "..." : (employee.role != null ? employee.role : "N/A"),
                formatStatus(employee.status),
                String.format("%,.0f", employee.salary)));
        }
        
        sb.append("\nREPORT OPTIONS\n");
        sb.append("-".repeat(70)).append("\n");
        sb.append("Include Contact Info   : ").append(includeContactCheck.isSelected() ? "Yes" : "No").append("\n");
        sb.append("Include Salary Details : ").append(includeSalaryCheck.isSelected() ? "Yes" : "No").append("\n");
        sb.append("Include Attendance     : ").append(includeAttendanceCheck.isSelected() ? "Yes" : "No").append("\n");
        
        if (!notesArea.getText().trim().isEmpty() && !notesArea.getText().equals(notesArea.getToolTipText())) {
            sb.append("\nNOTES\n");
            sb.append("-".repeat(70)).append("\n");
            sb.append(notesArea.getText().trim()).append("\n");
        }
        
        sb.append("\n").append("=".repeat(70)).append("\n");
        sb.append("               END OF EMPLOYEE REPORT\n");
        sb.append("=".repeat(70));
        
        return sb.toString();
    }
    
    private void printReportDetails(int totalEmployees, double totalSalary) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("        FACTORY EMPLOYEE REPORT GENERATION");
        System.out.println("═".repeat(60));
        System.out.println("Report Type      : " + reportTypeCombo.getSelectedItem());
        System.out.println("Department Filter: " + departmentCombo.getSelectedItem());
        System.out.println("Status Filter    : " + statusCombo.getSelectedItem());
        System.out.println("Output Format    : " + formatCombo.getSelectedItem());
        System.out.println("Total Employees  : " + totalEmployees);
        System.out.println("Total Payroll    : PKR " + String.format("%,.2f", totalSalary));
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
    
    /* ================= EMPLOYEE STATUS RENDERER ================= */
    private class EmployeeStatusRenderer extends DefaultTableCellRenderer {
        public EmployeeStatusRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                if ("Active".equalsIgnoreCase(status)) {
                    c.setForeground(ACTIVE_COLOR);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("On Leave".equalsIgnoreCase(status) || "Probation".equalsIgnoreCase(status)) {
                    c.setForeground(LEAVE_COLOR);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("Resigned".equalsIgnoreCase(status) || "Terminated".equalsIgnoreCase(status) || "Struck Off".equalsIgnoreCase(status)) {
                    c.setForeground(RESIGNED_COLOR);
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
    private static class EmployeeData {
        String empId;
        String name;
        String department;
        String role;
        String status;
        double salary;
        LocalDate joiningDate;
        String phone;
        String email;
        String address;
    }
    
    /* ================= TEST METHOD ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Employee Report Dialog Test");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            
            // Create a simulated content panel (right white panel)
            JPanel contentPanel = new JPanel();
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setPreferredSize(new Dimension(800, 600));
            frame.add(contentPanel, BorderLayout.CENTER);
            
            // Test button to open dialog
            JButton testBtn = new JButton("Open Employee Report Dialog");
            testBtn.addActionListener(e -> {
                EmployeeReportDialog dlg = new EmployeeReportDialog(frame, contentPanel);
                dlg.setVisible(true);
            });
            
            JPanel northPanel = new JPanel();
            northPanel.add(testBtn);
            frame.add(northPanel, BorderLayout.NORTH);
            
            frame.setVisible(true);
        });
    }
}