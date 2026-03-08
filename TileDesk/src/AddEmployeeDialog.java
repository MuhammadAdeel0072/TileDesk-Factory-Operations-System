package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * AddEmployeeDialog - Modern Employee Addition Form with Enhanced UI
 * Updated to connect with tile_factory_db database
 */
public class AddEmployeeDialog extends JDialog {
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
    
    // Form fields
    private JTextField empIdField;
    private JTextField fullNameField;
    private JComboBox<String> roleComboBox;
    private JTextField phoneField;
    private JComboBox<String> statusComboBox;
    private JTextField dateField;
    private JTextField cnicField;
    private JTextField salaryField;
    private JComboBox<String> departmentComboBox;
    private JTextArea addressArea;
    private JTextArea notesArea;
    
    // Date formatter
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Fonts (larger for better visibility)
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.BOLD, 15);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 16);
    private final Font PLACEHOLDER_FONT = new Font("Segoe UI", Font.ITALIC, 14);
    private final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    // Save status
    private boolean saveSuccessful = false;
    private EmployeeData employeeData = null;
    
    // Button references for hover effects
    private JButton saveButton;
    private JButton cancelButton;
    private JButton regenButton;
    
    /* ================= DATA CLASS ================= */
    public static class EmployeeData {
        private final String employeeId;
        private final String fullName;
        private final String role;
        private final String phone;
        private final String status;
        private final String joinDate;
        private final String cnic;
        private final String salary;
        private final String department;
        private final String address;
        private final String notes;
        
        public EmployeeData(String employeeId, String fullName, String role, String phone, 
                          String status, String joinDate, String cnic, String salary,
                          String department, String address, String notes) {
            this.employeeId = employeeId;
            this.fullName = fullName;
            this.role = role;
            this.phone = phone;
            this.status = status;
            this.joinDate = joinDate;
            this.cnic = cnic;
            this.salary = salary;
            this.department = department;
            this.address = address;
            this.notes = notes;
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
        public String getDepartment() { return department; }
        public String getAddress() { return address; }
        public String getNotes() { return notes; }
    }
    
    /* ================= CONSTRUCTOR ================= */
    public AddEmployeeDialog(JFrame ownerFrame, JPanel contentPanel) {
        super(ownerFrame, true);
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Initialize data
        loadDefaultRolesAndDepartments();
        
        // Main card panel
        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30, 35, 30, 35));
        card.setPreferredSize(new Dimension(900, 800));
        
        /* ================= TOP BAR ================= */
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 25, 0));
        
        // Title on left
        JLabel title = new JLabel("Add New Employee");
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
        setSize(900, 800);
        
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
        generateEmployeeId();
        
        // Load data
        SwingUtilities.invokeLater(() -> {
            loadDefaultRolesAndDepartments();
        });
        
        // Set focus to first field
        SwingUtilities.invokeLater(() -> fullNameField.requestFocus());
    }
    
    /* ================= DATA METHODS ================= */
    private void initializeDatabaseConnection() {
        // DB connectivity removed
    }
    
    private void loadRolesAndDepartments() {
        loadDefaultRolesAndDepartments();
    }
    
    private void loadDefaultRolesAndDepartments() {
        String[] defaultRoles = {"Select Role", "Factory Manager", "Production Supervisor", 
                               "Quality Control Inspector", "Machine Operator", 
                               "Maintenance Technician", "Warehouse Manager", 
                               "Sales Executive", "HR Manager", "Security Officer"};
        if (roleComboBox != null) {
            roleComboBox.setModel(new DefaultComboBoxModel<>(defaultRoles));
        }
        
        String[] defaultDepartments = {"Select Department", "Production", "Quality Control", 
                                     "Maintenance", "Warehouse", "Administration", 
                                     "Sales", "HR", "Security"};
        if (departmentComboBox != null) {
            departmentComboBox.setModel(new DefaultComboBoxModel<>(defaultDepartments));
        }
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Employee ID (Required)
        addLabel(panel, gbc, "Employee ID", true);
        gbc.gridy++;
        empIdField = createStyledTextField("EMP-0000", true);
        empIdField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(empIdField, gbc);
        
        // Full Name (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Full Name", true);
        gbc.gridy++;
        fullNameField = createStyledTextField("Enter employee's full name", false);
        panel.add(fullNameField, gbc);
        
        // Department (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Department", true);
        gbc.gridy++;
        String[] departments = {"Select Department"}; // Will be populated from database
        departmentComboBox = createStyledComboBox(departments, "Choose department");
        panel.add(departmentComboBox, gbc);
        
        // Role (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Role / Designation", true);
        gbc.gridy++;
        String[] roles = {"Select Role"}; // Will be populated from database
        roleComboBox = createStyledComboBox(roles, "Choose employee role");
        panel.add(roleComboBox, gbc);
        
        // Phone Number (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Phone Number", true);
        gbc.gridy++;
        phoneField = createStyledTextField("Enter 11-digit phone number", false);
        phoneField.setInputVerifier(new PhoneVerifier());
        panel.add(phoneField, gbc);
        
        // Status (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Employment Status", true);
        gbc.gridy++;
        String[] statuses = {"ACTIVE", "ON_LEAVE", "PROBATION", "RESIGNED", "TERMINATED"};
        statusComboBox = createStyledComboBox(statuses, "Select status");
        panel.add(statusComboBox, gbc);
        
        // Date of Joining (Required)
        gbc.gridy++;
        addLabel(panel, gbc, "Date of Joining", true);
        gbc.gridy++;
        dateField = createStyledTextField(LocalDate.now().format(dateFormatter), false);
        dateField.setToolTipText("Format: YYYY-MM-DD");
        panel.add(dateField, gbc);
        
        // CNIC (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "CNIC", false);
        gbc.gridy++;
        cnicField = createStyledTextField("Optional: 12345-6789012-3", false);
        panel.add(cnicField, gbc);
        
        // Salary (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Monthly Salary", false);
        gbc.gridy++;
        salaryField = createStyledTextField("Optional: Enter amount", false);
        panel.add(salaryField, gbc);
        
        // Address (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Address", false);
        gbc.gridy++;
        addressArea = createStyledTextArea("Optional: Enter complete address", 3);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        styleScrollPane(addressScroll);
        panel.add(addressScroll, gbc);
        
        // Notes (Optional)
        gbc.gridy++;
        addLabel(panel, gbc, "Additional Notes", false);
        gbc.gridy++;
        notesArea = createStyledTextArea("Optional: Any additional information", 2);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        styleScrollPane(notesScroll);
        panel.add(notesScroll, gbc);
        
        // Status indicator
        gbc.gridy++;
        gbc.insets = new Insets(25, 10, 5, 10);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusPanel.setOpaque(false);
        
        JLabel requiredLabel = createStatusLabel("● Required", REQUIRED_COLOR);
        JLabel optionalLabel = createStatusLabel("● Optional", OPTIONAL_COLOR);
        
        statusPanel.add(requiredLabel);
        statusPanel.add(optionalLabel);
        panel.add(statusPanel, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 0, 0, 0));
        
        // Save button
        saveButton = createStyledButton("💾 Save Employee", SKY_BLUE, SKY_BLUE.darker());
        saveButton.addActionListener(e -> onSave());
        
        // Cancel button
        cancelButton = createStyledButton("✕ Cancel", new Color(200, 200, 200), HOVER_RED);
        cancelButton.addActionListener(e -> dispose());
        
        // Regenerate ID button
        regenButton = createStyledButton("🔄 Regenerate ID", new Color(180, 220, 240), DARK_SKY_BLUE);
        regenButton.addActionListener(e -> generateEmployeeId());
        regenButton.setToolTipText("Generate new Employee ID");
        
        panel.add(saveButton);
        panel.add(cancelButton);
        panel.add(regenButton);
        
        // Make Save the default button
        getRootPane().setDefaultButton(saveButton);
        
        return panel;
    }
    
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
        button.setPreferredSize(new Dimension(200, 55));
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
                        if (index == -1 && ((String)value).startsWith("Select")) {
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
        
        // Custom scrollbar
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
    
    private void generateEmployeeId() {
        generateRandomEmployeeId();
    }
    
    private void generateRandomEmployeeId() {
        Random rand = new Random();
        int randomNum = 1000 + rand.nextInt(9000);
        String empId = String.format("EMP-%04d", randomNum);
        if (empIdField != null) {
            empIdField.setText(empId);
            empIdField.setForeground(new Color(40, 40, 40));
        }
    }
    
    private void onSave() {
        // Validate required fields
        if (fullNameField.getForeground().equals(Color.GRAY) || fullNameField.getText().trim().isEmpty()) {
            showError("Full Name is required");
            fullNameField.requestFocus();
            return;
        }
        
        if (departmentComboBox.getSelectedIndex() == 0 || departmentComboBox.getSelectedItem().toString().startsWith("Select")) {
            showError("Please select a Department");
            departmentComboBox.requestFocus();
            return;
        }
        
        if (roleComboBox.getSelectedIndex() == 0 || roleComboBox.getSelectedItem().toString().startsWith("Select")) {
            showError("Please select a Role");
            roleComboBox.requestFocus();
            return;
        }
        
        if (phoneField.getForeground().equals(Color.GRAY) || phoneField.getText().trim().isEmpty()) {
            showError("Phone Number is required");
            phoneField.requestFocus();
            return;
        }
        
        if (dateField.getForeground().equals(Color.GRAY) || dateField.getText().trim().isEmpty()) {
            showError("Date of Joining is required");
            dateField.requestFocus();
            return;
        }
        
        // kolektat data
        String fullName = fullNameField.getForeground().equals(Color.GRAY) ? "" : fullNameField.getText().trim();
        String phone = phoneField.getForeground().equals(Color.GRAY) ? "" : phoneField.getText().trim();
        String date = dateField.getForeground().equals(Color.GRAY) ? "" : dateField.getText().trim();
        String cnic = cnicField.getForeground().equals(Color.GRAY) ? "" : cnicField.getText().trim();
        String salary = salaryField.getForeground().equals(Color.GRAY) ? "" : salaryField.getText().trim();
        String address = addressArea.getForeground().equals(Color.GRAY) ? "" : addressArea.getText().trim();
        String notes = notesArea.getForeground().equals(Color.GRAY) ? "" : notesArea.getText().trim();
        
        employeeData = new EmployeeData(
            empIdField.getText(),
            fullName,
            (String) roleComboBox.getSelectedItem(),
            phone,
            (String) statusComboBox.getSelectedItem(),
            date,
            cnic,
            salary,
            (String) departmentComboBox.getSelectedItem(),
            address,
            notes
        );
        
        // Save (local only)
        saveSuccessful = true;
        printEmployeeData();
        
        JOptionPane.showMessageDialog(this, 
            "✅ Employee data captured locally (DB connectivity removed)!\n\n" +
            "Name: " + employeeData.getFullName() + "\n" +
            "ID: " + employeeData.getEmployeeId(),
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    }
    
    private boolean saveToDatabase() {
        return true; 
    }
    
    private int getRoleId(String roleName) {
        return -1;
    }
    
    private int getDepartmentId(String departmentName) {
        return -1;
    }
    
    private void updateRoleEmployeeCount(int roleId) {
    }
    
    private void printEmployeeData() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("           EMPLOYEE DATA SAVED");
        System.out.println("═".repeat(50));
        System.out.println("Employee ID    : " + employeeData.getEmployeeId());
        System.out.println("Full Name      : " + employeeData.getFullName());
        System.out.println("Department     : " + employeeData.getDepartment());
        System.out.println("Role           : " + employeeData.getRole());
        System.out.println("Phone          : " + employeeData.getPhone());
        System.out.println("Status         : " + employeeData.getStatus());
        System.out.println("Join Date      : " + employeeData.getJoinDate());
        System.out.println("CNIC           : " + (employeeData.getCnic().isEmpty() ? "Not provided" : employeeData.getCnic()));
        System.out.println("Salary         : " + (employeeData.getSalary().isEmpty() ? "Not provided" : employeeData.getSalary()));
        System.out.println("Address        : " + (employeeData.getAddress().isEmpty() ? "Not provided" : employeeData.getAddress()));
        System.out.println("Notes          : " + (employeeData.getNotes().isEmpty() ? "None" : employeeData.getNotes()));
        System.out.println("═".repeat(50) + "\n");
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            "❌ " + message + "\n\nPlease fill in all required fields marked with *", 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    /* ================= INPUT VERIFIER ================= */
    private static class PhoneVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            JTextField field = (JTextField) input;
            String text = field.getText().trim();
            
            // Allow empty for placeholder
            if (text.isEmpty() || field.getForeground().equals(Color.GRAY)) {
                return true;
            }
            
            // Check if it's a valid phone number
            return text.matches("\\d{11}") || text.matches("\\d{4}-\\d{7}");
        }
    }
    
    /* ================= PUBLIC ACCESSORS ================= */
    public boolean isSaveSuccessful() { return saveSuccessful; }
    public EmployeeData getEmployeeData() { return employeeData; }
    
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
    
    /* ================= TEST ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Add Employee Dialog Test");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(new Color(240, 248, 255));
            
            // Create a test button to open the dialog
            JButton testBtn = new JButton("Open Add Employee Dialog");
            testBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            testBtn.setBackground(new Color(135, 206, 250));
            testBtn.setForeground(Color.WHITE);
            testBtn.setFocusPainted(false);
            testBtn.setPreferredSize(new Dimension(300, 50));
            testBtn.addActionListener(e -> {
                AddEmployeeDialog dlg = new AddEmployeeDialog(frame, null);
                dlg.setVisible(true);
                
                if (dlg.isSaveSuccessful()) {
                    EmployeeData emp = dlg.getEmployeeData();
                    System.out.println("Employee saved: " + emp.getFullName() + " (" + emp.getEmployeeId() + ")");
                }
            });
            
            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setBackground(new Color(240, 248, 255));
            centerPanel.add(testBtn);
            
            frame.add(centerPanel);
            frame.setVisible(true);
        });
    }
}