import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class SearchRecord extends JFrame implements ActionListener {

    private JButton queryButton, exitButton;
    private JTable resultTable;
    private String userid;

    // 建構子，接受使用者ID作為參數
    public SearchRecord() {

    	setLayout(null);
    	setVisible(true);
        this.userid = DatabaseHelper.signinData.userid;

        // 設定視窗標題
        setTitle("租還車紀錄查詢");
        setSize(1000, 600); // 設定視窗大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 設定關閉操作
        setLocationRelativeTo(null); // 視窗居中
        setLayout(null); // 使用空布局進行精確定位

        // 查詢按鈕
        queryButton = new JButton("查詢");
        queryButton.setBounds(100, 30, 100, 30); // 設定按鈕位置和大小
        queryButton.addActionListener(this); // 添加事件監聽器
        add(queryButton);

        // 退出按鈕
        exitButton = new JButton("退出");
        exitButton.setBounds(250, 30, 100, 30); // 設定按鈕位置和大小
        exitButton.addActionListener(this); // 添加事件監聽器
        add(exitButton);

        // 顯示結果的表格
        resultTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBounds(50, 80, 900, 450); // 設定表格位置和大小
        add(scrollPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == queryButton) {
            // 查詢所有租還車紀錄
            queryRentalRecords();
        } else if (e.getSource() == exitButton) {
            new MyFrame(); // 退出應用程式
            dispose();
        }
    }

    // 查詢租還車紀錄的方法
    private void queryRentalRecords() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 連接到資料庫
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike", "root", "password");

            // 查詢資料的 SQL 語句
            String sql = "SELECT RentTime, RentStationUID, ReturnTime, ReturnStationUID, BikeUID, cost FROM records WHERE userid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid); // 設定查詢參數
            rs = pstmt.executeQuery();

            // 獲取元資料
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 設置表格模型
            DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
            model.setRowCount(0); // 清空表格數據
            model.setColumnCount(0); // 清空表格列

            // 設置表頭
            Vector<String> columnNames = new Vector<>();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }
            model.setColumnIdentifiers(columnNames);

            // 填充表格數據
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    row.add(rs.getObject(columnIndex));
                }
                data.add(row);
            }
            for (Vector<Object> rowData : data) {
                model.addRow(rowData);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "查詢失敗。", "錯誤", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}

