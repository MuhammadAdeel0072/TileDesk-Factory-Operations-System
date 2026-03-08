package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;

/**
 * ViewProductDialog - Premium UI/UX with Sky Blue Titles and Beautiful Grid
 * Layout
 * Enhanced HCI principles with smooth animations and interactive feedback
 */
public class ViewProductDialog extends JDialog {

    // Premium Color Palette
    private static final Color SKY_BLUE = new Color(135, 206, 235); // Sky blue for titles
    private static final Color DEEP_SKY_BLUE = new Color(0, 149, 218); // Deep sky blue
    private static final Color CARD_BG = new Color(248, 250, 252);
    private static final Color PRODUCT_CARD_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);
    private static final Color PRIMARY_LIGHT = new Color(37, 99, 235, 30);
    private static final Color SUCCESS_COLOR = new Color(22, 163, 74);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color DANGER_COLOR = new Color(220, 38, 38);
    private static final Color DARK_TEXT = new Color(17, 24, 39);
    private static final Color MEDIUM_TEXT = new Color(75, 85, 99);
    private static final Color LIGHT_TEXT = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Color SEARCH_BG = new Color(249, 250, 251);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 20);

    // Enhanced card dimensions
    private static final int CARD_WIDTH = 320;
    private static final int CARD_HEIGHT = 420;
    private static final int ARC = 20;

    // UI Components
    private List<ProductData> productList = new ArrayList<>();
    private List<ProductData> filteredList = new ArrayList<>();
    private JPanel productsPanel;
    private JTextField searchField;
    private JComboBox<String> categoryFilterCombo;
    private JComboBox<String> stockFilterCombo;
    private JLabel statusBarLabel;
    private Timer statusTimer;

    // Statistics labels
    private JLabel totalProductsValueLabel;
    private JLabel inventoryValueLabel;
    private JLabel lowStockValueLabel;
    private JLabel showingValueLabel;

    // Enhanced Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font CARD_SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font CARD_TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font STAT_LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font STAT_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 20);

    // Statistics
    private double totalInventoryValue = 0;
    private int totalProductsCount = 0;
    private int lowStockCount = 0;

    // PKR Currency Formatter
    private static final NumberFormat PKR_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "PK"));
    static {
        PKR_FORMAT.setMaximumFractionDigits(0);
    }

    // Enhanced Product Data Class
    public static class ProductData {
        private String id;
        private String name;
        private String category;
        private int quantity;
        private double price;
        private String size;
        private String thickness;
        private String finish;
        private String unitType;
        private String description;
        private boolean usedInSales;
        private String imageUrl;
        private int reorderLevel;

        public ProductData(String id, String name, String category, int quantity, double price,
                String size, String thickness, String finish, String unitType,
                String description, boolean usedInSales) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.quantity = quantity;
            this.price = price;
            this.size = size;
            this.thickness = thickness;
            this.finish = finish;
            this.unitType = unitType;
            this.description = description;
            this.usedInSales = usedInSales;
            this.reorderLevel = 50;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public String getSize() {
            return size;
        }

        public String getThickness() {
            return thickness;
        }

        public String getFinish() {
            return finish;
        }

        public String getUnitType() {
            return unitType;
        }

        public String getDescription() {
            return description;
        }

        public boolean isUsedInSales() {
            return usedInSales;
        }

        public double getTotalValue() {
            return quantity * price;
        }

        public int getReorderLevel() {
            return reorderLevel;
        }

        public boolean needsReorder() {
            return quantity <= reorderLevel;
        }

        public String getStockStatus() {
            if (quantity == 0)
                return "Out of Stock";
            else if (quantity < reorderLevel)
                return "Low Stock";
            else if (quantity < reorderLevel * 2)
                return "Medium Stock";
            else
                return "In Stock";
        }

        public Color getStockStatusColor() {
            if (quantity == 0)
                return DANGER_COLOR;
            else if (quantity < reorderLevel)
                return WARNING_COLOR;
            else if (quantity < reorderLevel * 2)
                return PRIMARY_COLOR;
            else
                return SUCCESS_COLOR;
        }

        public String getStockStatusIcon() {
            if (quantity == 0)
                return "🔴";
            else if (quantity < reorderLevel)
                return "🟡";
            else
                return "🟢";
        }
    }

    public ViewProductDialog(JFrame ownerFrame) {
        super(ownerFrame, true);
        setTitle("Marble & Granite Inventory Management - Premium Edition");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setUndecorated(true);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        Rectangle screenBounds = gd.getDefaultConfiguration().getBounds();

        // Enhanced gradient background
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 248, 252), 0, h, new Color(235, 240, 248));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainContainer.setBorder(new EmptyBorder(20, 30, 20, 30));

        mainContainer.add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(createSearchFilterPanel(), BorderLayout.NORTH);

        // Enhanced products grid panel
        productsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
        };
        productsPanel.setLayout(new GridBagLayout());
        productsPanel.setOpaque(false);
        productsPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(50);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(centerPanel, BorderLayout.CENTER);

        mainContainer.add(createStatusBar(), BorderLayout.SOUTH);

        setContentPane(mainContainer);
        setSize(screenBounds.width, screenBounds.height);
        setLocationRelativeTo(null);

        loadSampleProducts();
        setupKeyboardShortcuts();
        refreshProductGrid();
        showTemporaryStatus("✨ Welcome to Premium Inventory Management", SUCCESS_COLOR);
    }

    private void loadSampleProducts() {
        productList.clear();

        productList.add(new ProductData("MAR-1001", "Premium White Marble", "Marble", 250, 1250.00,
                "24x24 in", "12mm", "Polished", "Sq Ft",
                "Premium quality white marble from Italy with elegant veining. Perfect for luxury flooring and wall cladding.",
                false));

        productList.add(new ProductData("GRA-2001", "Black Galaxy Granite", "Granite", 85, 2800.00,
                "48x96 in", "20mm", "Honed", "Slab",
                "Exclusive black granite with golden and silver sparkling spots. Ideal for kitchen countertops.",
                true));

        productList.add(new ProductData("MAR-1002", "Carrara White Marble", "Marble", 45, 1850.00,
                "24x48 in", "18mm", "Honed", "Sq Ft",
                "Classic Carrara marble with subtle gray veining. Perfect for bathrooms and fireplaces.", true));

        productList.add(new ProductData("GRA-2002", "Absolute Black Granite", "Granite", 0, 3200.00,
                "36x72 in", "30mm", "Polished", "Slab",
                "Deepest black granite with minimal grain. Premium choice for modern designs.", false));

        productList.add(new ProductData("MAR-1003", "Emperador Brown Marble", "Marble", 120, 1650.00,
                "24x24 in", "16mm", "Glossy", "Sq Ft",
                "Rich brown marble with light veining. Ideal for living rooms and lobbies.", true));

        productList.add(new ProductData("GRA-2003", "Tan Brown Granite", "Granite", 150, 2100.00,
                "48x72 in", "20mm", "Leathered", "Slab",
                "Rich brown granite with black and tan speckles. Perfect for outdoor kitchens.", false));

        productList.add(new ProductData("MAR-1004", "Statuario Marble", "Marble", 15, 4500.00,
                "24x48 in", "20mm", "Polished", "Sq Ft",
                "Ultra-premium Statuario marble with dramatic veining. Limited stock available.", true));

        productList.add(new ProductData("GRA-2004", "Steel Grey Granite", "Granite", 380, 1450.00,
                "24x24 in", "12mm", "Flamed", "Sq Ft",
                "Durable grey granite with consistent color. Ideal for commercial spaces.", false));

        productList.add(new ProductData("MAR-1005", "Calacatta Gold Marble", "Marble", 35, 3800.00,
                "12x24 in", "16mm", "Polished", "Box",
                "Luxurious Calacatta marble with bold gold veining. Perfect for accent walls.", true));

        productList.add(new ProductData("GRA-2005", "Kashmir White Granite", "Granite", 220, 1550.00,
                "24x48 in", "18mm", "Polished", "Slab",
                "Elegant white granite with subtle grey and burgundy flecks. Excellent durability.", false));

        calculateStatistics();
        filteredList.clear();
        filteredList.addAll(productList);
        updateCategoryFilter();
    }

    private void calculateStatistics() {
        totalInventoryValue = 0;
        lowStockCount = 0;
        totalProductsCount = productList.size();

        for (ProductData p : productList) {
            totalInventoryValue += p.getTotalValue();
            if (p.getQuantity() < 50)
                lowStockCount++;
        }
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setOpaque(false);

        JLabel titleLabel = new JLabel("💎 Marble & Granite Inventory");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(DEEP_SKY_BLUE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Premium stone inventory management system • 10 products available");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(MEDIUM_TEXT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleSection.add(titleLabel);
        titleSection.add(Box.createVerticalStrut(8));
        titleSection.add(subtitleLabel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonsPanel.setOpaque(false);

        JButton refreshButton = createStyledButton("🔄 Refresh", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> {
            loadSampleProducts();
            filterProducts();
            showTemporaryStatus("✅ Inventory refreshed", SUCCESS_COLOR);
        });

        JButton exportBtn = createStyledButton("📊 Export", new Color(75, 85, 99));
        exportBtn.addActionListener(e -> showTemporaryStatus("📎 Export coming soon", PRIMARY_COLOR));

        JButton closeBtn = createStyledButton("✕ Close", DANGER_COLOR);
        closeBtn.addActionListener(e -> dispose());

        buttonsPanel.add(refreshButton);
        buttonsPanel.add(exportBtn);
        buttonsPanel.add(closeBtn);

        headerPanel.add(titleSection, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 42));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private JPanel createSearchFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel topRow = new JPanel(new BorderLayout(15, 0));
        topRow.setOpaque(false);

        // Enhanced search container
        JPanel searchContainer = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        searchContainer.setBackground(SEARCH_BG);
        searchContainer.setBorder(new EmptyBorder(10, 15, 10, 15));
        searchContainer.setOpaque(false);

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Dialog", Font.PLAIN, 18));

        searchField = new JTextField();
        searchField.setFont(INPUT_FONT);
        searchField.setBorder(null);
        searchField.setBackground(SEARCH_BG);
        searchField.setOpaque(false);
        searchField.setForeground(DARK_TEXT);
        searchField.setCaretColor(PRIMARY_COLOR);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private Timer timer = new Timer(300, e -> filterProducts());
            {
                timer.setRepeats(false);
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                timer.restart();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                timer.restart();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                timer.restart();
            }
        });

        searchContainer.add(searchIcon, BorderLayout.WEST);
        searchContainer.add(searchField, BorderLayout.CENTER);

        // Quick filter chips
        JPanel filterChipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterChipsPanel.setOpaque(false);

        String[] quickFilters = { "All", "Low Stock", "Out of Stock", "Premium" };
        for (String filter : quickFilters) {
            filterChipsPanel.add(createFilterChip(filter));
        }

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);
        leftPanel.add(searchContainer, BorderLayout.NORTH);
        leftPanel.add(filterChipsPanel, BorderLayout.SOUTH);

        topRow.add(leftPanel, BorderLayout.WEST);

        // Advanced filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filterPanel.setOpaque(false);

        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        categoryPanel.setOpaque(false);
        JLabel catLabel = new JLabel("📋 Category:");
        catLabel.setFont(STAT_LABEL_FONT);
        categoryPanel.add(catLabel);

        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.setFont(INPUT_FONT);
        categoryFilterCombo.setPreferredSize(new Dimension(160, 38));
        categoryFilterCombo.addActionListener(e -> filterProducts());
        categoryPanel.add(categoryFilterCombo);

        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        stockPanel.setOpaque(false);
        JLabel stockLabel = new JLabel("📊 Stock:");
        stockLabel.setFont(STAT_LABEL_FONT);
        stockPanel.add(stockLabel);

        stockFilterCombo = new JComboBox<>(new String[] {
                "All Stock", "Low Stock (<50)", "Out of Stock (0)", "In Stock (≥100)", "Needs Reorder"
        });
        stockFilterCombo.setFont(INPUT_FONT);
        stockFilterCombo.setPreferredSize(new Dimension(160, 38));
        stockFilterCombo.addActionListener(e -> filterProducts());
        stockPanel.add(stockFilterCombo);

        filterPanel.add(categoryPanel);
        filterPanel.add(stockPanel);

        topRow.add(filterPanel, BorderLayout.CENTER);
        panel.add(topRow, BorderLayout.NORTH);
        panel.add(createStatisticsPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JButton createFilterChip(String text) {
        JButton chip = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chip.setBackground(Color.WHITE);
        chip.setForeground(MEDIUM_TEXT);
        chip.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        chip.setFocusPainted(false);
        chip.setContentAreaFilled(false);
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.setPreferredSize(new Dimension(110, 34));

        chip.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                chip.setBackground(SKY_BLUE);
                chip.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                chip.setBackground(Color.WHITE);
                chip.setForeground(MEDIUM_TEXT);
            }
        });

        chip.addActionListener(e -> {
            switch (text) {
                case "Low Stock":
                    stockFilterCombo.setSelectedItem("Low Stock (<50)");
                    break;
                case "Out of Stock":
                    stockFilterCombo.setSelectedItem("Out of Stock (0)");
                    break;
                case "Premium":
                    filterPremiumProducts();
                    break;
                default:
                    stockFilterCombo.setSelectedItem("All Stock");
                    categoryFilterCombo.setSelectedIndex(0);
                    searchField.setText("");
            }
        });

        return chip;
    }

    private void filterPremiumProducts() {
        filteredList.clear();
        for (ProductData product : productList) {
            if (product.getPrice() > 3000) {
                filteredList.add(product);
            }
        }
        updateStatsLabels();
        refreshProductGrid();
        showTemporaryStatus("💰 Showing " + filteredList.size() + " premium products", PRIMARY_COLOR);
    }

    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        statsPanel.add(createStatCard("📦 Total Products", String.valueOf(totalProductsCount), MEDIUM_TEXT, "total"));
        statsPanel.add(createStatCard("💰 Inventory Value", formatPKR(totalInventoryValue), SUCCESS_COLOR, "value"));
        statsPanel.add(createStatCard("⚠️ Low Stock", String.valueOf(lowStockCount), WARNING_COLOR, "lowstock"));
        statsPanel.add(createStatCard("📋 Showing", String.valueOf(filteredList.size()), PRIMARY_COLOR, "showing"));

        return statsPanel;
    }

    private JLabel createStatCard(String label, String value, Color valueColor, String type) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2d.setColor(new Color(220, 220, 220));
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(STAT_LABEL_FONT);
        labelLbl.setForeground(LIGHT_TEXT);
        labelLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(STAT_VALUE_FONT);
        valueLbl.setForeground(valueColor);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        switch (type) {
            case "total":
                totalProductsValueLabel = valueLbl;
                break;
            case "value":
                inventoryValueLabel = valueLbl;
                break;
            case "lowstock":
                lowStockValueLabel = valueLbl;
                break;
            case "showing":
                showingValueLabel = valueLbl;
                break;
        }

        card.add(labelLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLbl);

        JLabel wrapper = new JLabel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(240, 244, 248));
        statusBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        statusBarLabel = new JLabel("🔌 Connected to: Sample Data (10 products)");
        statusBarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.add(statusBarLabel, BorderLayout.WEST);

        JLabel keyHints = new JLabel("F5: Refresh • Ctrl+F: Search • ESC: Close");
        keyHints.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        keyHints.setForeground(LIGHT_TEXT);
        statusBar.add(keyHints, BorderLayout.EAST);

        return statusBar;
    }

    private void updateCategoryFilter() {
        if (categoryFilterCombo == null)
            return;

        Set<String> categories = new HashSet<>();
        categories.add("All Categories");

        for (ProductData product : productList) {
            categories.add(product.getCategory());
        }

        categoryFilterCombo.removeAllItems();
        for (String category : categories) {
            categoryFilterCombo.addItem(category);
        }
    }

    private void filterProducts() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedCategory = (String) categoryFilterCombo.getSelectedItem();
        String selectedStockFilter = (String) stockFilterCombo.getSelectedItem();

        filteredList.clear();

        for (ProductData product : productList) {
            boolean matches = true;

            if (!searchText.isEmpty()) {
                if (!product.getName().toLowerCase().contains(searchText) &&
                        !product.getId().toLowerCase().contains(searchText) &&
                        !product.getCategory().toLowerCase().contains(searchText) &&
                        !product.getDescription().toLowerCase().contains(searchText)) {
                    matches = false;
                }
            }

            if (matches && selectedCategory != null && !selectedCategory.equals("All Categories")) {
                if (!product.getCategory().equals(selectedCategory)) {
                    matches = false;
                }
            }

            if (matches && selectedStockFilter != null) {
                switch (selectedStockFilter) {
                    case "Low Stock (<50)":
                        if (product.getQuantity() >= 50)
                            matches = false;
                        break;
                    case "Out of Stock (0)":
                        if (product.getQuantity() > 0)
                            matches = false;
                        break;
                    case "In Stock (≥100)":
                        if (product.getQuantity() < 100)
                            matches = false;
                        break;
                    case "Needs Reorder":
                        if (!product.needsReorder())
                            matches = false;
                        break;
                }
            }

            if (matches) {
                filteredList.add(product);
            }
        }

        updateStatsLabels();
        refreshProductGrid();
        showTemporaryStatus("Found " + filteredList.size() + " product(s)", PRIMARY_COLOR);
    }

    private void updateStatsLabels() {
        if (totalProductsValueLabel != null) {
            totalProductsValueLabel.setText(String.valueOf(totalProductsCount));
        }
        if (inventoryValueLabel != null) {
            inventoryValueLabel.setText(formatPKR(totalInventoryValue));
        }
        if (lowStockValueLabel != null) {
            lowStockValueLabel.setText(String.valueOf(lowStockCount));
        }
        if (showingValueLabel != null) {
            showingValueLabel.setText(String.valueOf(filteredList.size()));
        }
    }

    private void refreshProductGrid() {
        productsPanel.removeAll();

        if (filteredList.isEmpty()) {
            showEmptyState();
        } else {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 15, 15, 15);
            gbc.fill = GridBagConstraints.BOTH;

            int columnCount = 4;
            int productCount = filteredList.size();

            for (int i = 0; i < productCount; i++) {
                ProductData product = filteredList.get(i);

                gbc.gridx = i % columnCount;
                gbc.gridy = i / columnCount;
                gbc.weightx = 1.0;
                gbc.weighty = 0;

                productsPanel.add(createProductCard(product), gbc);
            }

            if (productCount > 0) {
                gbc.gridx = 0;
                gbc.gridy = (productCount + columnCount - 1) / columnCount;
                gbc.gridwidth = columnCount;
                gbc.weighty = 1.0;
                JPanel filler = new JPanel();
                filler.setOpaque(false);
                productsPanel.add(filler, gbc);
            }
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private void showEmptyState() {
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setOpaque(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel iconLabel = new JLabel("🔍");
        iconLabel.setFont(new Font("Dialog", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("No Products Found");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(MEDIUM_TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("Try adjusting your search or filter criteria");
        descLabel.setFont(SUBTITLE_FONT);
        descLabel.setForeground(LIGHT_TEXT);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton clearBtn = createStyledButton("Clear All Filters", PRIMARY_COLOR);
        clearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            categoryFilterCombo.setSelectedIndex(0);
            stockFilterCombo.setSelectedIndex(0);
        });

        content.add(iconLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(descLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(clearBtn);

        emptyPanel.add(content);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        productsPanel.add(emptyPanel, gbc);
    }

    private JPanel createProductCard(ProductData product) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow effect
                g2d.setColor(SHADOW_COLOR);
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, ARC, ARC);

                // Card background
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, ARC, ARC);

                // Border
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, ARC, ARC);
            }
        };

        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Header with ID and stock indicator
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel idLabel = new JLabel(product.getId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        idLabel.setForeground(LIGHT_TEXT);

        JLabel stockIndicator = new JLabel(product.getStockStatusIcon() + " " + product.getStockStatus());
        stockIndicator.setFont(new Font("Segoe UI", Font.BOLD, 12));
        stockIndicator.setForeground(product.getStockStatusColor());

        headerPanel.add(idLabel, BorderLayout.WEST);
        headerPanel.add(stockIndicator, BorderLayout.EAST);

        // Product name and category
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        namePanel.setBorder(new EmptyBorder(5, 0, 15, 0));

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(CARD_TITLE_FONT);
        nameLabel.setForeground(SKY_BLUE); // Sky blue title!
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel categoryLabel = new JLabel("📦 " + product.getCategory());
        categoryLabel.setFont(CARD_SUBTITLE_FONT);
        categoryLabel.setForeground(MEDIUM_TEXT);
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        namePanel.add(nameLabel);
        namePanel.add(Box.createVerticalStrut(5));
        namePanel.add(categoryLabel);

        // Details section
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        addDetailLine(detailsPanel, "💰 Price", formatPKR(product.getPrice()));
        addDetailLine(detailsPanel, "📦 Quantity", String.valueOf(product.getQuantity()));
        addDetailLine(detailsPanel, "📏 Size", product.getSize());
        addDetailLine(detailsPanel, "📐 Thickness", product.getThickness());
        addDetailLine(detailsPanel, "✨ Finish", product.getFinish());
        addDetailLine(detailsPanel, "💵 Total Value", formatPKR(product.getTotalValue()));

        // Reorder badge
        if (product.needsReorder()) {
            JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            badgePanel.setOpaque(false);
            badgePanel.setBorder(new EmptyBorder(10, 0, 0, 0));

            JLabel badge = new JLabel("⚠️ Reorder Needed");
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            badge.setForeground(WARNING_COLOR);
            badgePanel.add(badge);
            detailsPanel.add(badgePanel);
        }

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(namePanel, BorderLayout.CENTER);
        card.add(detailsPanel, BorderLayout.SOUTH);

        // Enhanced hover effect with animation
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(240, 248, 255));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showProductDetails(product);
            }
        });

        return card;
    }

    private void addDetailLine(JPanel panel, String label, String value) {
        JPanel line = new JPanel(new BorderLayout());
        line.setOpaque(false);
        line.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(CARD_TEXT_FONT);
        labelLbl.setForeground(MEDIUM_TEXT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valueLbl.setForeground(DARK_TEXT);
        valueLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        line.add(labelLbl, BorderLayout.WEST);
        line.add(valueLbl, BorderLayout.EAST);

        panel.add(line);
    }

    private void showProductDetails(ProductData product) {
        JDialog detailDialog = new JDialog(this, "Product Details: " + product.getName(), true);
        detailDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        contentPanel.setBackground(Color.WHITE);

        // Header with status
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel(product.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(SKY_BLUE);

        JLabel statusBadge = new JLabel(" " + product.getStockStatusIcon() + " " + product.getStockStatus() + " ");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setBackground(product.getStockStatusColor());
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(6, 12, 6, 12));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusBadge, BorderLayout.EAST);

        contentPanel.add(headerPanel);

        // ID and Category
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        idPanel.setOpaque(false);
        JLabel idLabel = new JLabel("ID: " + product.getId() + "  •  Category: " + product.getCategory());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idLabel.setForeground(MEDIUM_TEXT);
        idPanel.add(idLabel);
        contentPanel.add(idPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Details grid
        JPanel detailsGrid = new JPanel(new GridLayout(0, 2, 20, 15));
        detailsGrid.setOpaque(false);

        addDetailRow(detailsGrid, "💰 Price per unit", formatPKR(product.getPrice()));
        addDetailRow(detailsGrid, "📦 Quantity in stock", String.valueOf(product.getQuantity()));
        addDetailRow(detailsGrid, "📊 Total value", formatPKR(product.getTotalValue()));
        addDetailRow(detailsGrid, "📏 Size", product.getSize());
        addDetailRow(detailsGrid, "📐 Thickness", product.getThickness());
        addDetailRow(detailsGrid, "✨ Finish", product.getFinish());
        addDetailRow(detailsGrid, "📦 Unit type", product.getUnitType());
        addDetailRow(detailsGrid, "⚠️ Reorder level", String.valueOf(product.getReorderLevel()));

        contentPanel.add(detailsGrid);
        contentPanel.add(Box.createVerticalStrut(20));

        // Description
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            JPanel descPanel = new JPanel(new BorderLayout());
            descPanel.setOpaque(false);
            descPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(15, 15, 15, 15)));

            JLabel descLabel = new JLabel("<html><b>Description:</b><br/>" + product.getDescription() + "</html>");
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            descPanel.add(descLabel, BorderLayout.CENTER);

            contentPanel.add(descPanel);
        }

        contentPanel.add(Box.createVerticalStrut(20));

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton closeBtn = createStyledButton("✓ Close", PRIMARY_COLOR);
        closeBtn.addActionListener(e -> detailDialog.dispose());
        buttonPanel.add(closeBtn);

        contentPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        detailDialog.add(scrollPane, BorderLayout.CENTER);
        detailDialog.setSize(550, 600);
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setVisible(true);
    }

    private void addDetailRow(JPanel grid, String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelLbl.setForeground(MEDIUM_TEXT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valueLbl.setForeground(DARK_TEXT);

        panel.add(labelLbl, BorderLayout.WEST);
        panel.add(valueLbl, BorderLayout.EAST);

        grid.add(panel);
    }

    private void setupKeyboardShortcuts() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        actionMap.put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        actionMap.put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSampleProducts();
                filterProducts();
                showTemporaryStatus("✅ Refreshed", SUCCESS_COLOR);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "focusSearch");
        actionMap.put("focusSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.requestFocus();
                searchField.selectAll();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK), "clearFilters");
        actionMap.put("clearFilters", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                categoryFilterCombo.setSelectedIndex(0);
                stockFilterCombo.setSelectedIndex(0);
                showTemporaryStatus("🧹 Filters cleared", MEDIUM_TEXT);
            }
        });
    }

    private void showTemporaryStatus(String message, Color color) {
        if (statusTimer != null && statusTimer.isRunning()) {
            statusTimer.stop();
        }

        if (statusBarLabel != null) {
            statusBarLabel.setText("🔔 " + message);
            statusBarLabel.setForeground(color);

            statusTimer = new Timer(3000, e -> {
                statusBarLabel.setText("🔌 Connected to: Sample Data (10 products)");
                statusBarLabel.setForeground(LIGHT_TEXT);
            });
            statusTimer.setRepeats(false);
            statusTimer.start();
        }
    }

    public static String formatPKR(double amount) {
        return "Rs. " + String.format("%,.0f", amount);
    }

    // Custom scrollbar styling
    private class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(156, 163, 175);
            this.trackColor = CARD_BG;
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
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 8, 8);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
    }

    // Main method
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tile Factory - Product Inventory Premium Edition");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JButton openBtn = new JButton("💎 Open Premium Inventory") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    super.paintComponent(g);
                }
            };
            openBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            openBtn.setBackground(DEEP_SKY_BLUE);
            openBtn.setForeground(Color.WHITE);
            openBtn.setFocusPainted(false);
            openBtn.setContentAreaFilled(false);
            openBtn.setBorderPainted(false);
            openBtn.setPreferredSize(new Dimension(280, 70));

            openBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    openBtn.setBackground(SKY_BLUE);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    openBtn.setBackground(DEEP_SKY_BLUE);
                }
            });

            openBtn.addActionListener(e -> {
                ViewProductDialog dialog = new ViewProductDialog(frame);
                dialog.setVisible(true);
            });

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(new Color(245, 248, 252));
            panel.add(openBtn);

            frame.add(panel);
            frame.setVisible(true);
        });
    }
}