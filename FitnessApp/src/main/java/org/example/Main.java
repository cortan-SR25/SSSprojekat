package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import org.example.models.*;

import static java.sql.DriverManager.getConnection;

class FitnessApp extends JFrame {

    public FitnessApp() {
        setTitle("Fitness App");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new ExercisePanel());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FitnessApp().setVisible(true));
    }
}

class ExercisePanel extends JPanel {

    JComboBox<ComboItem> equipmentBox = new JComboBox<>();
    JComboBox<ComboItem> machineBox = new JComboBox<>();
    JComboBox<ComboItem> trainerBox = new JComboBox<>();

    DefaultTableModel tableModel = new DefaultTableModel();

    public ExercisePanel() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField noteField = new JTextField();
        JTextField videoField = new JTextField();

        JButton saveBtn = new JButton("Save Exercise");

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Note:"));
        form.add(noteField);
        form.add(new JLabel("Video URL:"));
        form.add(videoField);
        form.add(new JLabel("Equipment:"));
        form.add(equipmentBox);
        form.add(new JLabel("Machine:"));
        form.add(machineBox);
        form.add(new JLabel("Trainer:"));
        form.add(trainerBox);
        form.add(new JLabel());
        form.add(saveBtn);

        add(form, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        tableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Trainer"});
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadComboData();
        loadExercises();

        saveBtn.addActionListener(e -> {
            ComboItem eq = (ComboItem) equipmentBox.getSelectedItem();
            ComboItem mach = (ComboItem) machineBox.getSelectedItem();
            ComboItem tr = (ComboItem) trainerBox.getSelectedItem();

            DBUtil.insertExercise(
                    nameField.getText(),
                    noteField.getText(),
                    videoField.getText(),
                    eq != null ? eq.id : null,
                    mach != null ? mach.id : null,
                    tr.id
            );

            loadExercises();
        });
    }

    private void loadComboData() {
        equipmentBox.addItem(null);
        machineBox.addItem(null);

        for (ComboItem item : DBUtil.getEquipment()) equipmentBox.addItem(item);
        for (ComboItem item : DBUtil.getMachines()) machineBox.addItem(item);
        for (ComboItem item : DBUtil.getTrainers()) trainerBox.addItem(item);
    }

    private void loadExercises() {
        tableModel.setRowCount(0);
        for (Vector<String> row : DBUtil.getExercises()) {
            tableModel.addRow(row);
        }
    }
}

class ComboItem {
    int id;
    String name;

    public ComboItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String toString() {
        return name;
    }
}

class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/fitness_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void insertExercise(String name, String note, String video,
                                      Integer equipmentId, Integer machineId, int trainerId) {
        String sql = "INSERT INTO exercises(name, note, video_url, equipment_id, machine_id, trainer_id) VALUES(?,?,?,?,?,?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, note);
            stmt.setString(3, video);

            if (equipmentId == null) stmt.setNull(4, Types.INTEGER);
            else stmt.setInt(4, equipmentId);

            if (machineId == null) stmt.setNull(5, Types.INTEGER);
            else stmt.setInt(5, machineId);

            stmt.setInt(6, trainerId);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Saved!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static java.util.List<ComboItem> getEquipment() {
        return getSimpleList("equipment");
    }

    public static java.util.List<ComboItem> getMachines() {
        return getSimpleList("machines");
    }

    public static java.util.List<ComboItem> getTrainers() {
        return getSimpleList("users");
    }

    private static java.util.List<ComboItem> getSimpleList(String table) {
        java.util.List<ComboItem> list = new java.util.ArrayList<>();
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT id, name FROM " + table);
            while (rs.next()) {
                list.add(new ComboItem(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static java.util.List<Vector<String>> getExercises() {
        java.util.List<Vector<String>> list = new java.util.ArrayList<>();

        String sql = "SELECT e.id, e.name, t.name as trainer FROM exercises e JOIN users t ON e.trainer_id = t.id";

        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("trainer"));
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}

