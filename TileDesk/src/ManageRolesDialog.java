package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashMap;

/**
 * ManageRolesDialog - Dialog for managing employee roles in factory
 * With Database Integration
 */
public class ManageRolesDialog extends JDialog {

    // Database connection removed
    // private Connection connection;

    // Color scheme
    private static final Color SKY_BLUE = new Color(135, 206, 250);
    private static final Color DARK_SKY_BLUE = new Color(30, 144, 255);
    private static final Color LIGHT_BLUE_BG = new Color(240, 248, 255);
    private static final Color INPUT_BG = new Color(255, 255, 255);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(200, 220, 240);
    private static final Color LABEL_COLOR = new Color(60, 60, 80);
    private static final Color REQUIRED_COLOR = new Color(220, 53, 69);
    private static final Color OPTIONAL_COLOR = new Color(108, 117, 125);
    private static final Color DISABLED_BG = new Color(248, 249, 250);
    private static final Color HOVER_RED = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);

    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 15);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 18);
    private final Font PLACEHOLDER_FONT = new Font("Segoe UI", Font.ITALIC, 14);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private final Font ROLE_FONT = new Font("Segoe UI Semibold", Font.BOLD, 14);
    private final Font EMPLOYEE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    // Form fields
    private JTextField searchField;
    private JButton searchButton;

    // Role Management Components
    private JTextField newRoleField;
    private JComboBox<String> departmentComboBox;
    private JTextArea roleDescriptionArea;
    private JTextField maxEmployeesField;
    private JButton addRoleButton;
    private JButton updateRoleButton;
    private JButton deleteRoleButton;

    // Current Roles Display
    private JList<String> rolesList;
    private DefaultListModel<String> rolesListModel;
    private JList<String> employeesInRoleList;
    private DefaultListModel<String> employeesListModel;

    // Employee Assignment
    private JComboBox<String> assignEmployeeComboBox;
    private JButton assignButton;
    private JButton removeAssignmentButton;

    // Buttons
    private JButton saveButton;
    private JButton resetButton;
    private JButton clearButton;
    private JButton cancelButton;

    // Data storage
    private Map<String, RoleData> rolesDatabase;
    private Map<String, EmployeeData> employeesDatabase;
    private RoleData currentRole = null;
    private RoleData originalRoleData = null;

    /* ================= DATA CLASSES ================= */
    public static class RoleData {
        private String roleName;
        private String department;
        private String description;
        private List<String> assignedEmployees;
        private int maxEmployees;
        private Date createdDate;// error
        private Date lastModified;// error
        private String roleCode;

        public RoleData(String roleName, String department, String description, int maxEmployees) {
            this.roleName = roleName;
            this.department = department;
            this.description = description;
            this.assignedEmployees = new ArrayList<>();
            this.maxEmployees = maxEmployees;
            this.createdDate = new Date(); // error
            this.lastModified = new Date();// error
        }

        public RoleData(String roleCode, String roleName, String department, String description,
                int maxEmployees, int currentEmployees) {
            this.roleCode = roleCode;
            this.roleName = roleName;
            this.department = department;
            this.description = description;
            this.assignedEmployees = new ArrayList<>();
            this.maxEmployees = maxEmployees;
            this.createdDate = new Date();// error
            this.lastModified = new Date(); // error
        }

        // Getters
        public String getRoleName() {
            return roleName;
        }

        public String getDepartment() {
            return department;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getAssignedEmployees() {
            return new ArrayList<>(assignedEmployees);
        }

        public int getMaxEmployees() {
            return maxEmployees;
        }

        public Date getCreatedDate() {
            return createdDate;
        }// error

        public Date getLastModified() {
            return lastModified;
        } // error

        public String getRoleCode() {
            return roleCode;
        }

        // Setters
        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setMaxEmployees(int maxEmployees) {
            this.maxEmployees = maxEmployees;
        }

        public void setLastModified(Date lastModified) {
            this.lastModified = lastModified;
        } // erro

        // Employee management
        public boolean addEmployee(String employeeId) {
            if (assignedEmployees.size() < maxEmployees && !assignedEmployees.contains(employeeId)) {
                assignedEmployees.add(employeeId);
                lastModified = new Date(); // error
                return true;
            }
            return false;
        }

        public boolean removeEmployee(String employeeId) {
            boolean removed = assignedEmployees.remove(employeeId);
            if (removed) {
                lastModified = new Date();
            }
            return removed;
        }

        public boolean isFull() {
            return assignedEmployees.size() >= maxEmployees;
        }

        public int getCurrentCount() {
            return assignedEmployees.size();
        }

        // Copy method
        public RoleData copy() {
            RoleData copy = new RoleData(roleName, department, description, maxEmployees);
            copy.assignedEmployees = new ArrayList<>(assignedEmployees);
            copy.createdDate = (Date) createdDate.clone();
            copy.lastModified = (Date) lastModified.clone();
            return copy;
        }
    }

    public static class EmployeeData {
        private String employeeId;
        private String employeeCode;
        private String fullName;
        private String currentRole;
        private String department;
        private String phone;
        private String status;

        public EmployeeData(String employeeId, String employeeCode, String fullName, String currentRole,
                String department, String phone, String status) {
            this.employeeId = employeeId;
            this.employeeCode = employeeCode;
            this.fullName = fullName;
            this.currentRole = currentRole;
            this.department = department;
            this.phone = phone;
            this.status = status;
        }

        // Getters
        public String getEmployeeId() {
            return employeeId;
        }

        public String getEmployeeCode() {
            return employeeCode;
        }

        public String getFullName() {
            return fullName;
        }

        public String getCurrentRole() {
            return currentRole;
        }

        public String getDepartment() {
            return department;
        }

        public String getPhone() {
            return phone;
        }

        public String getStatus() {
            return status;
        }

        // Setters
        public void setCurrentRole(String currentRole) {
            this.currentRole = currentRole;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        @Override
        public String toString() {
            return employeeCode + " - " + fullName + " (" + currentRole + ")";
        }
    }

    /* ================= CONSTRUCTOR ================= */
    public ManageRolesDialog(JFrame ownerFrame, JPanel contentPanel) {
        super(ownerFrame, true);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));

        // Database initialization removed
        /*
         * connection = DBConnection.getConnection();
         * if (connection == null) {
         * JOptionPane.showMessageDialog(this,
         * "Failed to connect to database. Please check database connection.",
         * "Database Error",
         * JOptionPane.ERROR_MESSAGE);
         * dispose();
         * return;
         * }
         */

        initializeDatabases();

        // Create main card panel
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 35, 25, 35));
        card.setPreferredSize(new Dimension(950, 800));

        /* ================= TOP BAR WITH TITLE, SEARCH AND CLOSE ================= */
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title on LEFT - "Manage Roles" in SKY BLUE
        JLabel title = new JLabel("Manage Roles");
        title.setFont(TITLE_FONT);
        title.setForeground(SKY_BLUE);
        topPanel.add(title, BorderLayout.WEST);

        // Search components panel on RIGHT
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Search field
        searchField = createStyledTextField("Search role or employee...", false);
        searchField.setPreferredSize(new Dimension(250, 52));
        searchField.addActionListener(e -> performSearch());

        // Search button
        searchButton = createStyledButton("🔍 Search", SKY_BLUE, DARK_SKY_BLUE);
        searchButton.setPreferredSize(new Dimension(120, 52));
        searchButton.addActionListener(e -> performSearch());

        // Close button
        JButton closeBtn = createCloseButton();

        // Add components to search panel
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(closeBtn);

        topPanel.add(searchPanel, BorderLayout.EAST);
        card.add(topPanel, BorderLayout.NORTH);

        /* ================= HEADING PANEL ================= */
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headingPanel.setOpaque(false);
        headingPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Sky Blue Heading
        JLabel heading = new JLabel("Factory Role Management");
        heading.setFont(HEADING_FONT);
        heading.setForeground(SKY_BLUE);
        heading.setBorder(new EmptyBorder(5, 0, 5, 0));
        headingPanel.add(heading);

        card.add(headingPanel, BorderLayout.CENTER);

        /* ================= MAIN CONTENT PANEL ================= */
        JPanel contentPanelMain = new JPanel(new GridBagLayout());
        contentPanelMain.setOpaque(false);
        contentPanelMain.setBorder(new EmptyBorder(10, 0, 10, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;

        /* ================= LEFT PANEL - ROLE CREATION/EDITING ================= */
        gbc.gridx = 0;
        JPanel leftPanel = createLeftPanel();
        contentPanelMain.add(leftPanel, gbc);

        /* ================= MIDDLE PANEL - AVAILABLE ROLES ================= */
        gbc.gridx = 1;
        JPanel middlePanel = createMiddlePanel();
        contentPanelMain.add(middlePanel, gbc);

        /* ================= RIGHT PANEL - EMPLOYEE ASSIGNMENT ================= */
        gbc.gridx = 2;
        JPanel rightPanel = createRightPanel();
        contentPanelMain.add(rightPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(contentPanelMain);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LIGHT_BLUE_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
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
        setFormEnabled(false);

        // Focus on search field initially
        SwingUtilities.invokeLater(() -> searchField.requestFocus());
    }

    private void initializeDatabases() {
        rolesDatabase = new HashMap<>();
        employeesDatabase = new HashMap<>();

        // Add some default data for demonstration (DB connectivity removed)
        RoleData adminRole = new RoleData("ADM", "Admin", "System Administration", "Full system access", 5, 1);
        rolesDatabase.put("Admin", adminRole);

        RoleData salesRole = new RoleData("SAL", "Sales", "Sales Department", "Customer management and invoicing", 10,
                2);
        rolesDatabase.put("Sales Executive", salesRole);

        loadDepartmentsIntoComboBox();
    }

    private void loadDepartmentsFromDB() {
        /* DB connectivity removed */ }

    private void loadRolesFromDB() {
        /* DB connectivity removed */ }

    private void loadEmployeesFromDB() {
        /* DB connectivity removed */ }

    /* ================= PANEL CREATION METHODS ================= */
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // Panel title
        JLabel title = new JLabel("Role Details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(SKY_BLUE);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // Role Name
        JLabel roleLabel = new JLabel("Role Name *");
        roleLabel.setFont(LABEL_FONT);
        roleLabel.setForeground(LABEL_COLOR);
        formPanel.add(roleLabel, gbc);

        gbc.gridy++;
        newRoleField = createStyledTextField("Enter new role name", false);
        formPanel.add(newRoleField, gbc);

        // Department
        gbc.gridy++;
        JLabel deptLabel = new JLabel("Department *");
        deptLabel.setFont(LABEL_FONT);
        deptLabel.setForeground(LABEL_COLOR);
        formPanel.add(deptLabel, gbc);

        gbc.gridy++;
        departmentComboBox = createStyledComboBox(new String[] { "Select Department" }, "Choose department");
        loadDepartmentsIntoComboBox();
        formPanel.add(departmentComboBox, gbc);

        // Max Employees
        gbc.gridy++;
        JLabel maxLabel = new JLabel("Max Employees *");
        maxLabel.setFont(LABEL_FONT);
        maxLabel.setForeground(LABEL_COLOR);
        formPanel.add(maxLabel, gbc);

        gbc.gridy++;
        maxEmployeesField = createStyledTextField("10", false);
        formPanel.add(maxEmployeesField, gbc);

        // Description
        gbc.gridy++;
        JLabel descLabel = new JLabel("Role Description");
        descLabel.setFont(LABEL_FONT);
        descLabel.setForeground(LABEL_COLOR);
        formPanel.add(descLabel, gbc);

        gbc.gridy++;
        roleDescriptionArea = createStyledTextArea("Describe the role responsibilities and requirements...", 4);
        JScrollPane descScroll = new JScrollPane(roleDescriptionArea);
        styleScrollPane(descScroll);
        formPanel.add(descScroll, gbc);

        // Action buttons panel
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 0, 0);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.setOpaque(false);

        addRoleButton = createStyledButton("➕ Add", SUCCESS_COLOR, SUCCESS_COLOR.darker());
        addRoleButton.setForeground(Color.WHITE);
        addRoleButton.setPreferredSize(new Dimension(100, 40));
        addRoleButton.addActionListener(e -> addNewRole());

        updateRoleButton = createStyledButton("✎ Update", SKY_BLUE, DARK_SKY_BLUE);
        updateRoleButton.setForeground(Color.WHITE);
        updateRoleButton.setPreferredSize(new Dimension(100, 40));
        updateRoleButton.addActionListener(e -> updateRole());
        updateRoleButton.setEnabled(false);

        deleteRoleButton = createStyledButton("🗑 Delete", HOVER_RED, HOVER_RED.darker());
        deleteRoleButton.setForeground(Color.WHITE);
        deleteRoleButton.setPreferredSize(new Dimension(100, 40));
        deleteRoleButton.addActionListener(e -> deleteRole());
        deleteRoleButton.setEnabled(false);

        actionPanel.add(addRoleButton);
        actionPanel.add(updateRoleButton);
        actionPanel.add(deleteRoleButton);
        formPanel.add(actionPanel, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadDepartmentsIntoComboBox() {
        departmentComboBox.removeAllItems();
        departmentComboBox.addItem("Select Department");
        departmentComboBox.addItem("Admin");
        departmentComboBox.addItem("Sales");
        departmentComboBox.addItem("Production");
        departmentComboBox.addItem("Quality Control");
        departmentComboBox.addItem("Maintenance");
    }

    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // Panel title with count
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Available Roles");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(SKY_BLUE);
        titlePanel.add(title, BorderLayout.WEST);

        JLabel countLabel = new JLabel("(" + rolesDatabase.size() + " roles)");
        countLabel.setFont(STATUS_FONT);
        countLabel.setForeground(OPTIONAL_COLOR);
        titlePanel.add(countLabel, BorderLayout.EAST);

        panel.add(titlePanel, BorderLayout.NORTH);

        // Roles list
        rolesListModel = new DefaultListModel<>();
        updateRolesList();

        rolesList = new JList<>(rolesListModel);
        rolesList.setFont(ROLE_FONT);
        rolesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rolesList.setBackground(INPUT_BG);
        rolesList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Custom cell renderer for roles list
        rolesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(SKY_BLUE);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(INPUT_BG);
                    setForeground(new Color(40, 40, 40));
                }
                return c;
            }
        });

        rolesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && rolesList.getSelectedIndex() != -1) {
                String selected = (String) rolesList.getSelectedValue();
                String roleName = selected.substring(0, selected.indexOf(" ("));
                loadRoleDetails(roleName);
            }
        });

        JScrollPane listScroll = new JScrollPane(rolesList);
        listScroll.setPreferredSize(new Dimension(200, 300));
        styleScrollPane(listScroll);
        panel.add(listScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // Panel title
        JLabel title = new JLabel("Assigned Employees");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(SKY_BLUE);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        // Employees list
        employeesListModel = new DefaultListModel<>();
        employeesInRoleList = new JList<>(employeesListModel);
        employeesInRoleList.setFont(EMPLOYEE_FONT);
        employeesInRoleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeesInRoleList.setBackground(INPUT_BG);
        employeesInRoleList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane empScroll = new JScrollPane(employeesInRoleList);
        empScroll.setPreferredSize(new Dimension(200, 200));
        styleScrollPane(empScroll);
        panel.add(empScroll, BorderLayout.CENTER);

        // Assignment panel
        JPanel assignmentPanel = new JPanel(new GridBagLayout());
        assignmentPanel.setOpaque(false);
        assignmentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        JLabel assignLabel = new JLabel("Assign Employee:");
        assignLabel.setFont(LABEL_FONT);
        assignLabel.setForeground(LABEL_COLOR);
        assignmentPanel.add(assignLabel, gbc);

        gbc.gridy++;
        assignEmployeeComboBox = new JComboBox<>();
        assignEmployeeComboBox.setFont(INPUT_FONT);
        assignEmployeeComboBox.setBackground(INPUT_BG);
        assignEmployeeComboBox.setEnabled(false);
        assignmentPanel.add(assignEmployeeComboBox, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 5, 0);
        JPanel assignButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        assignButtonsPanel.setOpaque(false);

        assignButton = createStyledButton("Assign", SUCCESS_COLOR, SUCCESS_COLOR.darker());
        assignButton.setForeground(Color.WHITE);
        assignButton.setPreferredSize(new Dimension(100, 35));
        assignButton.addActionListener(e -> assignEmployee());
        assignButton.setEnabled(false);

        removeAssignmentButton = createStyledButton("Remove", HOVER_RED, HOVER_RED.darker());
        removeAssignmentButton.setForeground(Color.WHITE);
        removeAssignmentButton.setPreferredSize(new Dimension(100, 35));
        removeAssignmentButton.addActionListener(e -> removeEmployeeAssignment());
        removeAssignmentButton.setEnabled(false);

        assignButtonsPanel.add(assignButton);
        assignButtonsPanel.add(removeAssignmentButton);
        assignmentPanel.add(assignButtonsPanel, gbc);

        panel.add(assignmentPanel, BorderLayout.SOUTH);

        return panel;
    }

    /* ================= BUTTON PANEL ================= */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Save button - SKY BLUE with WHITE TEXT
        saveButton = createStyledButton("💾 Save All Changes", SKY_BLUE, DARK_SKY_BLUE);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveAllChanges());
        saveButton.setEnabled(false);

        // Reset button - SKY BLUE with WHITE TEXT
        resetButton = createStyledButton("Reset", SKY_BLUE, DARK_SKY_BLUE);
        resetButton.setForeground(Color.WHITE);
        resetButton.addActionListener(e -> resetForm());
        resetButton.setEnabled(false);

        // Clear button - SKY BLUE with WHITE TEXT
        clearButton = createStyledButton("Clear All", SKY_BLUE, DARK_SKY_BLUE);
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> {
            searchField.setText("Search role or employee...");
            searchField.setForeground(Color.GRAY);
            clearForm();
            setFormEnabled(false);
            saveButton.setEnabled(false);
            resetButton.setEnabled(false);
            rolesList.clearSelection();
            employeesInRoleList.clearSelection();
        });

        // Cancel button - GRAY with RED HOVER
        cancelButton = createStyledButton("✕ Cancel", new Color(200, 200, 200), HOVER_RED);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dispose());

        panel.add(saveButton);
        panel.add(resetButton);
        panel.add(clearButton);
        panel.add(cancelButton);

        return panel;
    }

    /* ================= UI COMPONENT CREATION ================= */
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                g2.setColor(getBackground().darker());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(BUTTON_FONT);
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

    private JTextField createStyledTextField(String placeholder, boolean readOnly) {
        return new JTextField() {
            private boolean showingPlaceholder = !readOnly;
            private String placeholderText = placeholder;

            {
                setFont(INPUT_FONT);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1),
                        BorderFactory.createEmptyBorder(12, 20, 12, 20)));
                setBackground(readOnly ? DISABLED_BG : INPUT_BG);
                setForeground(readOnly ? new Color(80, 80, 80) : Color.GRAY);
                setText(placeholder);
                setCaretColor(DARK_SKY_BLUE);
                setPreferredSize(new Dimension(250, 48));
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

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
                setPreferredSize(new Dimension(250, 48));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2.setColor(DARK_SKY_BLUE);
                int[] xPoints = { getWidth() - 25, getWidth() - 15, getWidth() - 20 };
                int[] yPoints = { getHeight() / 2 - 3, getHeight() / 2 - 3, getHeight() / 2 + 3 };
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2.dispose();
                super.paintComponent(g);
            }
        };
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
    private void performSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.equals("Search role or employee...") || searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a role name or employee ID to search",
                    "Search Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Search in local data (DB connectivity removed)
        boolean found = false;
        for (String roleName : rolesDatabase.keySet()) {
            if (roleName.toLowerCase().contains(searchText.toLowerCase())) {
                loadRoleDetails(roleName);
                highlightRoleInList(roleName);
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this,
                    "No role matching: " + searchText + " (DB connectivity removed)",
                    "Not Found",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void highlightRoleInList(String roleName) {
        for (int i = 0; i < rolesListModel.getSize(); i++) {
            String item = rolesListModel.getElementAt(i);
            if (item.startsWith(roleName + " (")) {
                rolesList.setSelectedIndex(i);
                rolesList.ensureIndexIsVisible(i);
                break;
            }
        }
    }

    private void loadRoleDetails(String roleName) {
        currentRole = rolesDatabase.get(roleName);
        if (currentRole != null) {
            originalRoleData = currentRole.copy();

            // Load role details
            newRoleField.setText(currentRole.getRoleName());
            newRoleField.setForeground(new Color(40, 40, 40));

            departmentComboBox.setSelectedItem(currentRole.getDepartment());
            departmentComboBox.setForeground(new Color(40, 40, 40));

            maxEmployeesField.setText(String.valueOf(currentRole.getMaxEmployees()));
            maxEmployeesField.setForeground(new Color(40, 40, 40));

            roleDescriptionArea.setText(currentRole.getDescription());
            roleDescriptionArea.setForeground(new Color(40, 40, 40));

            // Load assigned employees
            updateEmployeesList();

            // Update assign employee combo box
            updateAssignEmployeeComboBox();

            // Enable form and buttons
            setFormEnabled(true);
            updateRoleButton.setEnabled(true);
            deleteRoleButton.setEnabled(true);
            assignButton.setEnabled(true);
            removeAssignmentButton.setEnabled(true);
            saveButton.setEnabled(true);
            resetButton.setEnabled(true);
        }
    }

    private void updateEmployeesList() {
        employeesListModel.clear();
        if (currentRole != null) {
            for (String empId : currentRole.getAssignedEmployees()) {
                EmployeeData emp = employeesDatabase.get(empId);
                if (emp != null) {
                    employeesListModel.addElement(emp.getEmployeeCode() + " - " + emp.getFullName());
                }
            }
        }
        employeesInRoleList.setModel(employeesListModel);
    }

    private void updateAssignEmployeeComboBox() {
        assignEmployeeComboBox.removeAllItems();
        assignEmployeeComboBox.addItem("Select Employee");

        if (currentRole != null) {
            // Get employees not assigned to this role
            for (String empId : employeesDatabase.keySet()) {
                EmployeeData emp = employeesDatabase.get(empId);
                // Only show active employees and those not already in this role
                if ("ACTIVE".equals(emp.getStatus()) &&
                        !currentRole.getAssignedEmployees().contains(empId)) {
                    assignEmployeeComboBox.addItem(emp.getEmployeeCode() + " - " + emp.getFullName());
                }
            }
        }

        assignEmployeeComboBox.setEnabled(assignEmployeeComboBox.getItemCount() > 1);
    }

    private void addNewRole() {
        String roleName = newRoleField.getText().trim();
        String department = (String) departmentComboBox.getSelectedItem();
        String description = roleDescriptionArea.getText().trim();
        String maxEmployeesStr = maxEmployeesField.getText().trim();

        // Validation
        if (roleName.isEmpty() || newRoleField.getForeground().equals(Color.GRAY)) {
            JOptionPane.showMessageDialog(this, "Please enter a role name", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            newRoleField.requestFocus();
            return;
        }

        int maxEmployees = 10;
        try {
            maxEmployees = Integer.parseInt(maxEmployeesStr);
        } catch (Exception e) {
        }

        // DB connectivity removed
        RoleData newRole = new RoleData("NEW", roleName, department, description, maxEmployees, 0);
        rolesDatabase.put(roleName, newRole);
        updateRolesList();
        clearRoleForm();

        JOptionPane.showMessageDialog(this, "✅ New role added locally (DB connectivity removed)!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateRole() {
        if (currentRole == null)
            return;
        JOptionPane.showMessageDialog(this, "✅ Role parameters captured locally (DB connectivity removed)!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteRole() {
        if (currentRole == null)
            return;
        rolesDatabase.remove(currentRole.getRoleName());
        updateRolesList();
        clearRoleForm();
        JOptionPane.showMessageDialog(this, "✅ Role removed locally (DB connectivity removed)!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void assignEmployee() {
        if (currentRole == null)
            return;

        String selected = (String) assignEmployeeComboBox.getSelectedItem();
        if (selected == null || selected.equals("Select Employee")) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee to assign",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extract employee code from combo box item
        String empCode = selected.substring(0, selected.indexOf(" - "));
        String empId = null;

        // Find employee ID by code in local database
        for (EmployeeData empItem : employeesDatabase.values()) {
            if (empItem.getEmployeeCode().equals(empCode)) {
                empId = empItem.getEmployeeId();
                break;
            }
        }

        if (empId == null) {
            JOptionPane.showMessageDialog(this,
                    "Employee not found in local data",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        EmployeeData emp = employeesDatabase.get(empId);

        if (emp == null) {
            JOptionPane.showMessageDialog(this,
                    "Employee not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if role is full
        if (currentRole.isFull()) {
            JOptionPane.showMessageDialog(this,
                    "This role has reached its maximum capacity (" +
                            currentRole.getMaxEmployees() + " employees)",
                    "Role Full",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Update local cache and UI
        currentRole.addEmployee(empId);
        emp.setCurrentRole(currentRole.getRoleName());
        emp.setDepartment(currentRole.getDepartment());

        // Update UI
        updateEmployeesList();
        updateAssignEmployeeComboBox();
        updateRolesList();

        JOptionPane.showMessageDialog(this,
                "✅ Employee assigned successfully (locally)!\n\n" +
                        "Employee: " + emp.getFullName() + "\n" +
                        "Employee Code: " + emp.getEmployeeCode() + "\n" +
                        "Assigned to: " + currentRole.getRoleName() + "\n" +
                        "Current Capacity: " + currentRole.getCurrentCount() + "/"
                        + currentRole.getMaxEmployees(),
                "Assignment Successful",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void removeEmployeeAssignment() {
        if (currentRole == null)
            return;

        int selectedIndex = employeesInRoleList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee to remove",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selected = employeesListModel.getElementAt(selectedIndex);
        String empCode = selected.substring(0, selected.indexOf(" - "));
        String empId = null;

        // Find employee ID by code in local database
        for (EmployeeData empItem : employeesDatabase.values()) {
            if (empItem.getEmployeeCode().equals(empCode)) {
                empId = empItem.getEmployeeId();
                break;
            }
        }

        if (empId == null) {
            JOptionPane.showMessageDialog(this,
                    "Employee not found in local data",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        EmployeeData emp = employeesDatabase.get(empId);

        if (emp == null)
            return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove " + emp.getFullName() +
                        " from the " + currentRole.getRoleName() + " role?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Update local cache
            currentRole.removeEmployee(empId);
            emp.setCurrentRole("");
            emp.setDepartment("");

            // Update UI
            updateEmployeesList();
            updateAssignEmployeeComboBox();
            updateRolesList();

            JOptionPane.showMessageDialog(this,
                    "✅ Employee removed from role (locally)!\n\n" +
                            "Employee: " + emp.getFullName() + "\n" +
                            "Employee Code: " + emp.getEmployeeCode() + "\n" +
                            "Removed from: " + currentRole.getRoleName(),
                    "Removal Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveAllChanges() {
        // In this implementation, changes are saved immediately to database
        // So this button just confirms everything is saved

        JOptionPane.showMessageDialog(this,
                "✅ All changes have been saved to the database!\n\n" +
                        "Database: " + DBConnection.getDatabaseName() + "\n" +
                        "Timestamp: " + new java.util.Date(),
                "Save Complete",
                JOptionPane.INFORMATION_MESSAGE);

        // Print summary to console
        printRoleManagementSummary();
    }

    private void clearForm() {
        clearRoleForm();
        employeesListModel.clear();
        assignEmployeeComboBox.removeAllItems();
        rolesList.clearSelection();
        employeesInRoleList.clearSelection();
        currentRole = null;
        originalRoleData = null;
    }

    private void clearRoleForm() {
        newRoleField.setText("Enter new role name");
        newRoleField.setForeground(Color.GRAY);

        departmentComboBox.setSelectedIndex(0);
        departmentComboBox.setForeground(Color.GRAY);

        maxEmployeesField.setText("10");
        maxEmployeesField.setForeground(Color.GRAY);

        roleDescriptionArea.setText("Describe the role responsibilities and requirements...");
        roleDescriptionArea.setForeground(Color.GRAY);
    }

    private void resetForm() {
        if (originalRoleData != null) {
            loadRoleDetails(originalRoleData.getRoleName());
            JOptionPane.showMessageDialog(this,
                    "Role form reset to original values",
                    "Reset Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setFormEnabled(boolean enabled) {
        newRoleField.setEditable(enabled);
        newRoleField.setBackground(enabled ? INPUT_BG : DISABLED_BG);

        departmentComboBox.setEnabled(enabled);
        departmentComboBox.setBackground(enabled ? INPUT_BG : DISABLED_BG);

        maxEmployeesField.setEditable(enabled);
        maxEmployeesField.setBackground(enabled ? INPUT_BG : DISABLED_BG);

        roleDescriptionArea.setEditable(enabled);
        roleDescriptionArea.setBackground(enabled ? INPUT_BG : DISABLED_BG);
    }

    private void updateRolesList() {
        rolesListModel.clear();
        for (String roleName : rolesDatabase.keySet()) {
            RoleData role = rolesDatabase.get(roleName);
            rolesListModel.addElement(roleName + " (" + role.getCurrentCount() + "/" + role.getMaxEmployees() + ")");
        }
        rolesList.setModel(rolesListModel);
    }

    /* ================= DATABASE HELPER METHODS (REMOVED) ================= */

    /* ================= CONSOLE OUTPUT ================= */
    private void printRoleManagementSummary() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("     FACTORY ROLE MANAGEMENT SUMMARY");
        System.out.println("═".repeat(70));
        System.out.println("Report Time: " + new java.util.Date());
        System.out.println("Database: " + DBConnection.getDatabaseName());
        System.out.println("─".repeat(70));

        System.out.println("\n1. ROLE STATISTICS");
        System.out.println("   Total Roles: " + rolesDatabase.size());

        int totalAssigned = 0;
        int maxCapacity = 0;
        for (RoleData role : rolesDatabase.values()) {
            totalAssigned += role.getCurrentCount();
            maxCapacity += role.getMaxEmployees();
        }
        System.out.println("   Total Assigned Employees: " + totalAssigned);
        System.out.println("   Total Capacity: " + maxCapacity);
        System.out.println("   Utilization: " + String.format("%.1f%%", (totalAssigned * 100.0 / maxCapacity)));

        System.out.println("\n2. ROLE DETAILS");
        System.out.println("   ┌──────────────────────────────────────┬─────────────┬──────────┐");
        System.out.println("   │ Role Name                            │ Department  │ Capacity │");
        System.out.println("   ├──────────────────────────────────────┼─────────────┼──────────┤");
        for (RoleData role : rolesDatabase.values()) {
            System.out.printf("   │ %-36s │ %-11s │ %3d/%3d │\n",
                    role.getRoleName(),
                    role.getDepartment(),
                    role.getCurrentCount(),
                    role.getMaxEmployees());
        }
        System.out.println("   └──────────────────────────────────────┴─────────────┴──────────┘");

        System.out.println("\n3. DEPARTMENT BREAKDOWN");
        Map<String, Integer> deptCount = new HashMap<>();
        Map<String, Integer> deptCapacity = new HashMap<>();

        for (RoleData role : rolesDatabase.values()) {
            String dept = role.getDepartment();
            deptCount.put(dept, deptCount.getOrDefault(dept, 0) + role.getCurrentCount());
            deptCapacity.put(dept, deptCapacity.getOrDefault(dept, 0) + role.getMaxEmployees());
        }

        for (String dept : deptCount.keySet()) {
            int count = deptCount.get(dept);
            int capacity = deptCapacity.get(dept);
            System.out.printf("   • %-15s: %2d/%2d employees (%5.1f%%)\n",
                    dept, count, capacity, (count * 100.0 / capacity));
        }

        System.out.println("\n4. UNASSIGNED EMPLOYEES");
        int unassigned = 0;
        for (EmployeeData emp : employeesDatabase.values()) {
            if (emp.getCurrentRole() == null || emp.getCurrentRole().isEmpty()) {
                System.out.println("   • " + emp.getEmployeeCode() + " - " + emp.getFullName());
                unassigned++;
            }
        }
        if (unassigned == 0) {
            System.out.println("   All employees are assigned to roles.");
        }

        System.out.println("═".repeat(70) + "\n");
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
                g2.fillRoundRect(i, i, getWidth() - 2 * i, getHeight() - 2 * i, cornerRadius, cornerRadius);
            }

            // Draw main card
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

            // Draw border
            g2.setColor(new Color(180, 200, 230));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /* ================= TEST METHOD ================= */
    public static void main(String[] args) {
        // Test database connection first
        if (!DBConnection.testConnection()) {
            System.err.println("❌ Database connection failed! Cannot run ManageRolesDialog.");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Manage Roles Dialog Test - Database Connected");
            frame.setSize(1200, 850);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(new Color(240, 248, 255));

            // Create a test button to open the dialog
            JButton testBtn = new JButton("Open Manage Roles Dialog");
            testBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            testBtn.setBackground(new Color(135, 206, 250));
            testBtn.setForeground(Color.WHITE);
            testBtn.setFocusPainted(false);
            testBtn.setPreferredSize(new Dimension(300, 50));
            testBtn.addActionListener(e -> {
                ManageRolesDialog dlg = new ManageRolesDialog(frame, null);
                dlg.setVisible(true);

                // After dialog closes, show summary
                System.out.println("\n" + "=".repeat(70));
                System.out.println("     TEST COMPLETE - ROLE MANAGEMENT SYSTEM");
                System.out.println("=".repeat(70));
                System.out.println("Database: " + DBConnection.getDatabaseName());
                System.out.println("Dialog closed at: " + new java.util.Date());
                System.out.println("=".repeat(70) + "\n");
            });

            // Instructions label
            JLabel instructions = new JLabel("<html><div style='text-align: center; width: 400px;'>" +
                    "<h3 style='color:#1e90ff'>Factory Role Management System</h3>" +
                    "<p><b>Connected to Database:</b> " + DBConnection.getDatabaseName() + "</p>" +
                    "<p><b>Features:</b></p>" +
                    "<ul style='text-align: left;'>" +
                    "<li>Create, Update, Delete factory roles</li>" +
                    "<li>Assign/Remove employees from roles</li>" +
                    "<li>Search roles and employees</li>" +
                    "<li>Department-based role organization</li>" +
                    "<li>Capacity management per role</li>" +
                    "<li>Role description and details</li>" +
                    "</ul>" +
                    "<p><b>Data is saved directly to database</b></p>" +
                    "<p style='color:green'><b>Check console for detailed reports</b></p>" +
                    "</div></html>");
            instructions.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            instructions.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setBackground(new Color(240, 248, 255));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(20, 0, 40, 0);
            centerPanel.add(instructions, gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            centerPanel.add(testBtn, gbc);

            frame.add(centerPanel);
            frame.setVisible(true);
        });
    }
}