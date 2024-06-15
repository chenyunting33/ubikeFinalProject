import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class MaintenanceReport extends JFrame implements ActionListener {

    private JTextField bikeNumberField, dockNumberField;
    private JTextArea descriptionArea;
    private JButton reportButton, exitButton;

    public MaintenanceReport() {
        setTitle("維修通報");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // Using null layout for precise positioning

        JLabel bikeNumberLabel = new JLabel("車號:");
        bikeNumberLabel.setBounds(50, 30, 100, 30);
        add(bikeNumberLabel);

        bikeNumberField = new JTextField();
        bikeNumberField.setBounds(160, 30, 150, 30);
        add(bikeNumberField);

        JLabel dockNumberLabel = new JLabel("車柱號:");
        dockNumberLabel.setBounds(50, 80, 100, 30);
        add(dockNumberLabel);

        dockNumberField = new JTextField();
        dockNumberField.setBounds(160, 80, 150, 30);
        add(dockNumberField);

        JLabel descriptionLabel = new JLabel("維修項目:");
        descriptionLabel.setBounds(50, 130, 100, 30);
        add(descriptionLabel);

        descriptionArea = new JTextArea();
        descriptionArea.setBounds(160, 130, 300, 150);
        add(descriptionArea);

        reportButton = new JButton("提交");
        reportButton.setBounds(100, 300, 100, 30);
        reportButton.addActionListener(this);
        add(reportButton);

        exitButton = new JButton("退出");
        exitButton.setBounds(250, 300, 100, 30);
        exitButton.addActionListener(this);
        add(exitButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == reportButton) {
            String bikeNumber = bikeNumberField.getText().trim();
            String dockNumber = dockNumberField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (bikeNumber.isEmpty() && dockNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "請輸入車號或車柱號。", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "請輸入維修項目描述。", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 提交维修通报信息到数据库
            submitReport(bikeNumber, dockNumber, description);
        } else if (e.getSource() == exitButton) {
            new MyFrame();
            dispose();
        }
    }

    private void submitReport(String bikeNumber, String dockNumber, String description) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // 连接到数据库
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike", "root", "password");

            // 插入数据的 SQL 语句
            String sql = "INSERT INTO reports (bike_number, dock_number, description) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bikeNumber);
            pstmt.setString(2, dockNumber);
            pstmt.setString(3, description);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "維修通報已提交。", "成功", JOptionPane.INFORMATION_MESSAGE);

            // 清空输入字段
            bikeNumberField.setText("");
            dockNumberField.setText("");
            descriptionArea.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "提交失敗。", "錯誤", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

//    public static void main(String[] args) {
//        // 运行数据库初始化脚本
//        initializeDatabase();
//
//        SwingUtilities.invokeLater(() -> {
//            MaintenanceReport frame = new MaintenanceReport();
//            frame.setVisible(true);
//        });
//    }

    private static void initializeDatabase() {
        Connection conn = null;
        Statement stmt = null;

        try {
            // 连接到数据库
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "password");
            stmt = conn.createStatement();

            // 创建数据库和表
            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS maintenance";
            stmt.executeUpdate(createDatabaseSQL);

            String useDatabaseSQL = "USE maintenance";
            stmt.executeUpdate(useDatabaseSQL);

            String createTableSQL = "CREATE TABLE IF NOT EXISTS reports (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "bike_number VARCHAR(50)," +
                    "dock_number VARCHAR(50)," +
                    "description TEXT," +
                    "report_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}

