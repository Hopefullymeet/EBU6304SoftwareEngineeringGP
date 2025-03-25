package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class TransactionVisualizationUI extends JFrame {

    private JFreeChart currentChart;
    private ChartPanel chartPanel;
    private JComboBox<String> chartTypeComboBox;
    private JTextField startDateField, endDateField;
    private Map<String, Double> currentData;

    private final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);

    private JPanel accountPanel, billingPanel, budgetPanel, viewTransactionPanel, helpCenterPanel;

    public TransactionVisualizationUI() {
        setTitle("Transaction Data Visualization");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        createSidebar();

        // 创建 headerPanel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel titleLabel = new JLabel("Transaction Data Visualization");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // 创建 contentPanel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(Color.WHITE);

        JPanel datePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        datePanel.setBorder(null);
        datePanel.setBackground(Color.WHITE);
        startDateField = createTextField();
        endDateField = createTextField();
        datePanel.add(new JLabel("Start Date (YYYY-MM):"));
        datePanel.add(startDateField);
        datePanel.add(new JLabel("End Date (YYYY-MM):"));
        datePanel.add(endDateField);

        JPanel chartTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chartTypePanel.setBorder(null);
        chartTypePanel.setBackground(Color.WHITE);
        chartTypeComboBox = new JComboBox<>(new String[]{"Line Chart", "Pie Chart"});
        chartTypePanel.add(chartTypeComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBorder(null);
        buttonPanel.setBackground(Color.WHITE);

        JButton generateChartButton = createStyledButton("Generate Chart");
        JButton exportImageButton = createStyledButton("Export as Image");
        JButton exportPDFButton = createStyledButton("Export as PDF");
        JButton exportCSVButton = createStyledButton("Export as CSV");

        generateChartButton.addActionListener(e -> generateChart());
        exportImageButton.addActionListener(e -> exportChartAsImage());
        exportPDFButton.addActionListener(e -> exportChartAsPDF());
        exportCSVButton.addActionListener(e -> exportDataAsCSV());

        // 添加鼠标悬停效果
        addHoverEffect(generateChartButton);
        addHoverEffect(exportImageButton);
        addHoverEffect(exportPDFButton);
        addHoverEffect(exportCSVButton);

        buttonPanel.add(generateChartButton);
        buttonPanel.add(exportImageButton);
        buttonPanel.add(exportPDFButton);
        buttonPanel.add(exportCSVButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        contentPanel.add(datePanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPanel.add(chartTypePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        contentPanel.add(buttonPanel, gbc);

        chartPanel = new ChartPanel(null);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chartPanel.setBackground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        contentPanel.add(chartPanel, gbc);

        // 将 headerPanel 和 contentPanel 添加到主窗口
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Create sidebar items
        JPanel accountPanel = createSidebarItem("Account");
        JPanel billingPanel = createSidebarItem("Billing & Subscriptions");
        JPanel budgetPanel = createSidebarItem("Budget");
        JPanel viewTransactionPanel = createSidebarItem("View Transaction");
        JPanel helpCenterPanel = createSidebarItem("Help Center");

        // Add click listeners
        accountPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new AccountView();
            }
        });

        billingPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new BillingView();
            }
        });

        budgetPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new BudgetView();
            }
        });

        viewTransactionPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new TransactionVisualizationUI();
            }
        });

        helpCenterPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new HelpCenterView();
            }
        });

        // Add sidebar items to panel
        sidebarPanel.add(accountPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(billingPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(budgetPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(viewTransactionPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(helpCenterPanel);

        // Add a glue component to push everything to the top
        sidebarPanel.add(Box.createVerticalGlue());

        // Add sidebar to main frame
        add(sidebarPanel, BorderLayout.WEST);
    }

    private JPanel createSidebarItem(String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.BLACK);
        panel.add(label, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
            }
        });

        return panel;
    }

    private void setActiveSidebarItem(JPanel activePanel) {
        // Reset all panels to inactive state
        accountPanel.setBackground(Color.WHITE);
        billingPanel.setBackground(Color.WHITE);
        budgetPanel.setBackground(Color.WHITE);
        viewTransactionPanel.setBackground(Color.WHITE);
        helpCenterPanel.setBackground(Color.WHITE);

        // Set the active panel
        activePanel.setBackground(PRIMARY_BLUE);
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(CONTENT_FONT);
        textField.setPreferredSize(new Dimension(200, 30));
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setOpaque(false);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBackground(PRIMARY_BLUE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_BLUE);
            }
        });
    }

    private void generateChart() {
        currentData = fetchData();
        String chartType = (String) chartTypeComboBox.getSelectedItem();
        currentChart = "Line Chart".equals(chartType) ? createLineChart() : createPieChart();
        chartPanel.setChart(currentChart);
    }

    private Map<String, Double> fetchData() {
        Map<String, Double> data = new HashMap<>();
        data.put("2023-01", 2000.0);
        data.put("2023-02", 2500.0);
        data.put("2023-03", 3000.0);
        return data;
    }

    private JFreeChart createLineChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : currentData.entrySet()) {
            dataset.addValue(entry.getValue(), "Transaction", entry.getKey());
        }
        return ChartFactory.createLineChart("Transaction Trend", "Month", "Amount", dataset);
    }

    private JFreeChart createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : currentData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        return ChartFactory.createPieChart("Transaction Distribution", dataset, true, true, false);
    }

    private void exportChartAsImage() {
        try {
            ChartUtils.saveChartAsPNG(new File("chart.png"), currentChart, 800, 600);
            JOptionPane.showMessageDialog(this, "Chart exported as image successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportChartAsPDF() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("chart.pdf"));
            document.open();
            Image image = Image.getInstance("chart.png");
            document.add(image);
            document.close();
            JOptionPane.showMessageDialog(this, "Chart exported as PDF successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportDataAsCSV() {
        try (FileWriter writer = new FileWriter("data.csv")) {
            writer.write("Month,Amount\n");
            for (Map.Entry<String, Double> entry : currentData.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
            JOptionPane.showMessageDialog(this, "Data exported as CSV successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TransactionVisualizationUI().setVisible(true));
    }
}