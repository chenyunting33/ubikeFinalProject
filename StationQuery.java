import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import org.json.*;

public class StationQuery extends JFrame implements ActionListener {

    private JTextField nameField, lonField, latField, radiusField;
    private JRadioButton emptyDocksRadio, availableBikesRadio;
    private ButtonGroup searchGroup;
    private JTextArea resultArea;
    private JButton searchButton, exitButton;

    public StationQuery() {
        setTitle("Station Query");
        setSize(600, 600); // Adjusted size to fit the layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // Using null layout for precise positioning

        JLabel nameLabel = new JLabel("站點名稱:");
        nameLabel.setBounds(50, 30, 100, 30);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(160, 30, 150, 30);
        add(nameField);

        JLabel lonLabel = new JLabel("經度:");
        lonLabel.setBounds(50, 80, 100, 30);
        add(lonLabel);

        lonField = new JTextField();
        lonField.setBounds(160, 80, 150, 30);
        add(lonField);

        JLabel latLabel = new JLabel("緯度:");
        latLabel.setBounds(50, 130, 100, 30);
        add(latLabel);

        latField = new JTextField();
        latField.setBounds(160, 130, 150, 30);
        add(latField);

        JLabel radiusLabel = new JLabel("範圍（公里）:");
        radiusLabel.setBounds(50, 180, 100, 30);
        add(radiusLabel);

        radiusField = new JTextField();
        radiusField.setBounds(160, 180, 150, 30);
        add(radiusField);

        // Radio buttons for choosing search type
        emptyDocksRadio = new JRadioButton("僅顯示有空車位站點");
        emptyDocksRadio.setBounds(50, 230, 200, 30);
        emptyDocksRadio.setSelected(true); // Default selection
        add(emptyDocksRadio);

        availableBikesRadio = new JRadioButton("僅顯示有車站點");
        availableBikesRadio.setBounds(250, 230, 150, 30);
        add(availableBikesRadio);

        // Button group for radio buttons
        searchGroup = new ButtonGroup();
        searchGroup.add(emptyDocksRadio);
        searchGroup.add(availableBikesRadio);

        searchButton = new JButton("查询");
        searchButton.setBounds(100, 280, 100, 30);
        searchButton.addActionListener(this);
        add(searchButton);

        exitButton = new JButton("退出");
        exitButton.setBounds(250, 280, 100, 30);
        exitButton.addActionListener(this);
        add(exitButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBounds(50, 330, 400, 200);
        add(scrollPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String name = nameField.getText();
            double lon = Double.parseDouble(lonField.getText());
            double lat = Double.parseDouble(latField.getText());
            double radius = Double.parseDouble(radiusField.getText());

            // Determine which radio button is selected
            boolean searchEmptyDocks = emptyDocksRadio.isSelected();
            JSONArray stations = SearchStations(name, lon, lat, radius);
            displayStations(stations, searchEmptyDocks);
        } else if (e.getSource() == exitButton) {
        	 new MyFrame();
             dispose();
        }
    }

    private void displayStations(JSONArray stations, boolean searchEmptyDocks) {
        resultArea.setText("");
        for (int i = 0; i < stations.length(); i++) {
            JSONObject station = stations.getJSONObject(i);

            // Determine which field to display based on radio button selection
            String displayField = searchEmptyDocks ? "number of empty docks" : "number of bikes";

            // Get the corresponding field value
            int quantity = searchEmptyDocks ? calculateEmptyForStation(station.getString("StationUID")) :
                                              calculateBikesForStation(station.getString("StationUID"));

            // Skip stations that don't meet the criteria
            if ((searchEmptyDocks && quantity == 0) || (!searchEmptyDocks && quantity == 0)) {
                continue;
            }

            // Build station information string
            String stationInfo = "Station Name: " +
                    station.getJSONObject("StationName").getString("Zh_tw") + " \n(" +
                    station.getJSONObject("StationName").getString("En") + ")\n" +
                    "Station Address: " +
                    station.getJSONObject("StationAddress").getString("Zh_tw") + " \n(" +
                    station.getJSONObject("StationAddress").getString("En") + ")\n" +
                    "Bike Capacity: " + station.getInt("BikesCapacity") + "\n" +
                    displayField + ": " + quantity + "\n";

            // Append station information to the result area
            resultArea.append(stationInfo + "\n");
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StationQuery frame = new StationQuery();
            frame.setVisible(true);
        });
    }

    public static JSONArray SearchStations(Connection conn, String name, double lon, double lat, double radius) {
        String sql = "SELECT StationUID, StationID, AuthorityID, " +
                "JSON_EXTRACT(StationName, '$.Zh_tw') AS stationNameZhTw, " +
                "JSON_EXTRACT(StationName, '$.En') AS stationNameEn, " +
                "JSON_EXTRACT(StationPosition, '$.PositionLon') AS positionLon, " +
                "JSON_EXTRACT(StationPosition, '$.PositionLat') AS positionLat, " +
                "JSON_EXTRACT(StationAddress, '$.Zh_tw') AS stationAddressZhTw, " +
                "JSON_EXTRACT(StationAddress, '$.En') AS stationAddressEn, " +
                "BikesCapacity FROM ntustations " +
                "WHERE (JSON_EXTRACT(StationName, '$.Zh_tw') LIKE ? OR JSON_EXTRACT(StationName, '$.En') LIKE ?) " +
                "AND (6371 * acos(cos(radians(?)) * cos(radians(JSON_EXTRACT(StationPosition, '$.PositionLat'))) * " +
                "cos(radians(JSON_EXTRACT(StationPosition, '$.PositionLon')) - radians(?)) + " +
                "sin(radians(?)) * sin(radians(JSON_EXTRACT(StationPosition, '$.PositionLat'))))) < ?";
        return executeQuery(conn, sql, "%" + name + "%", "%" + name + "%", lat, lon, lat, radius);
    }

    private static JSONArray executeQuery(Connection conn, String sql, Object... params) {
        JSONArray results = new JSONArray();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                JSONObject station = new JSONObject();
                station.put("StationUID", rs.getString("StationUID"));
                station.put("StationID", rs.getString("StationID"));
                station.put("AuthorityID", rs.getString("AuthorityID"));

                JSONObject stationName = new JSONObject();
                stationName.put("Zh_tw", rs.getString("stationNameZhTw"));
                stationName.put("En", rs.getString("stationNameEn"));
                station.put("StationName", stationName);

                JSONObject stationPosition = new JSONObject();
                stationPosition.put("PositionLon", rs.getDouble("positionLon"));
                stationPosition.put("PositionLat", rs.getDouble("positionLat"));
                station.put("StationPosition", stationPosition);

                JSONObject stationAddress = new JSONObject();
                stationAddress.put("Zh_tw", rs.getString("stationAddressZhTw"));
                stationAddress.put("En", rs.getString("stationAddressEn"));
                station.put("StationAddress", stationAddress);

                station.put("BikesCapacity", rs.getInt("BikesCapacity"));

                results.put(station);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private static Connection connect() {
        // Modify with your MySQL connection details
        String url = "jdbc:mysql://localhost:3306/ubike";
        String user = "root";
        String password = "password";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static JSONArray SearchStations(String name, double lon, double lat, double radius) {
        Connection conn = connect();
        if (conn != null) {
            return StationQuery.SearchStations(conn, name, lon, lat, radius);
        }
        return new JSONArray();
    }

    private int calculateBikesForStation(String stationId) {
        int totalbikesCount = 0;
        int emptyDockCount = 0;
        try {
            Connection conn = connect();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return emptyDockCount;
            }

            String sql = "SELECT bike FROM docks WHERE StationUID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String dockData = rs.getString("bike");
                totalbikesCount+=1;
                if (dockData.isEmpty()) {
                    emptyDockCount += 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (totalbikesCount-emptyDockCount);
    }


    private static int calculateEmptyForStation(String stationId) {
        int emptyDockCount = 0;
        try {
            Connection conn = connect();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return emptyDockCount;
            }

            String sql = "SELECT bike FROM docks WHERE StationUID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String dockData = rs.getString("bike");

                if (dockData.isEmpty()) {
                    emptyDockCount += 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emptyDockCount;
    }
}
