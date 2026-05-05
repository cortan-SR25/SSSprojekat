package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Objects;
import java.util.Vector;

public class FitnessApp extends JFrame {

    CardLayout layout = new CardLayout();
    JPanel mainPanel = new JPanel(layout);
    public static String id;
    private TrainerPanel trainerPanel;

    public FitnessApp() {
        setTitle("Fitness App");
        setSize(800, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel.add(new LoginPanel(this), "login");
        mainPanel.add(new AdminPanel(this), "admin");
        //mainPanel.add(new TrainerPanel(this), "trainer");

        add(mainPanel);
        layout.show(mainPanel, "login");
    }

    public void showTrainer(int trainerId) {

        if (trainerPanel != null) {
            mainPanel.remove(trainerPanel); // očisti stari
        }

        trainerPanel = new TrainerPanel(this, Integer.parseInt(FitnessApp.id));
        mainPanel.add(trainerPanel, "trainer");

        layout.show(mainPanel, "trainer");
    }

    public void showAdmin() { layout.show(mainPanel, "admin"); }
    //public void showTrainer() { layout.show(mainPanel, "trainer"); }
    public void showLogin() { layout.show(mainPanel, "login"); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FitnessApp().setVisible(true));
    }
}

// =====================
// LOGIN PANEL
// =====================
class LoginPanel extends JPanel {

    public LoginPanel(FitnessApp app) {

        if (FitnessApp.id != null){
            FitnessApp.id = null;
        }

        setLayout(new GridLayout(4, 2, 10, 10));

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        add(new JLabel("Username:")); add(username);
        add(new JLabel("Password:")); add(password);
        add(new JLabel()); add(loginBtn);

        loginBtn.addActionListener(e -> {
            String role = DBUtil.loginRole(username.getText(), new String(password.getPassword()));
            System.out.println(role);
            if (role.equals("ADMIN")) {
                JOptionPane.showMessageDialog(this, "Login success");
                app.showAdmin();
            } else if (role.equals("TRAINER")){
                JOptionPane.showMessageDialog(this, "Login success");
                app.showTrainer(Integer.parseInt(FitnessApp.id));
            }
            else {
                JOptionPane.showMessageDialog(this, "Access denied");
            }
        });
    }
}

// =====================
// ADMIN PANEL
// =====================
class AdminPanel extends JPanel {

    FitnessApp app;
    DefaultTableModel eqModel = new DefaultTableModel();
    DefaultTableModel machModel = new DefaultTableModel();

    JTable eqTable = new JTable(eqModel);
    JTable machTable = new JTable(machModel);

    public AdminPanel(FitnessApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> app.showLogin());
        topBar.add(logoutBtn);

        add(topBar, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1));
        center.add(createEquipmentPanel());
        center.add(createMachinePanel());

        add(center, BorderLayout.CENTER);
    }

    private JPanel createEquipmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextField nameField = new JTextField();
        JButton addBtn = new JButton("Add Equipment");
        JButton deleteBtn = new JButton("Delete Selected");

        JPanel top = new JPanel(new GridLayout(1, 3));
        top.add(nameField);
        top.add(addBtn);
        top.add(deleteBtn);

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        model.setColumnIdentifiers(new String[]{"Name"});

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // LOAD
        Runnable load = () -> {
            model.setRowCount(0);
            for (Vector<String> row : DBUtil.getSimple("equipment")) {
                model.addRow(row);
            }
        };

        load.run();

        // ADD
        addBtn.addActionListener(e -> {
            if (!nameField.getText().trim().isEmpty()) {
                DBUtil.insertEquipment(nameField.getText());
                nameField.setText("");
                load.run();
            } else {
                JOptionPane.showMessageDialog(panel, "Enter name");
            }
        });

        // DELETE
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(panel, "Delete selected?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                    DBUtil.delete("equipment", id);
                    load.run();
                }
            }
        });

        return panel;
    }

    private JPanel createMachinePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextField nameField = new JTextField();
        JButton addBtn = new JButton("Add Machine");
        JButton deleteBtn = new JButton("Delete Selected");

        JPanel top = new JPanel(new GridLayout(1, 3));
        top.add(nameField);
        top.add(addBtn);
        top.add(deleteBtn);

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        model.setColumnIdentifiers(new String[]{"Name"});

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // LOAD
        Runnable load = () -> {
            model.setRowCount(0);
            for (Vector<String> row : DBUtil.getSimple("machines")) {
                model.addRow(row);
            }
        };

        load.run();

        // ADD
        addBtn.addActionListener(e -> {
            if (!nameField.getText().trim().isEmpty()) {
                DBUtil.insertMachine(nameField.getText());
                nameField.setText("");
                load.run();
            } else {
                JOptionPane.showMessageDialog(panel, "Enter name");
            }
        });

        // DELETE
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(panel, "Delete selected?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                    DBUtil.delete("machines", id);
                    load.run();
                }
            }
        });

        return panel;
    }
}

    // =====================
// TRAINER PANEL
// =====================
    class TrainerPanel extends JPanel {

        // static int trainerId;

        DefaultTableModel exModel = new DefaultTableModel();
        DefaultTableModel trModel = new DefaultTableModel();
        DefaultTableModel linkModel = new DefaultTableModel();

        JTable exTable = new JTable(exModel);
        JTable trTable = new JTable(trModel);
        JTable linkTable = new JTable(linkModel);

        JTextField exName = new JTextField();
        JTextField exUrl = new JTextField();
        JTextField exNote = new JTextField();
        JTextField trName = new JTextField();

        private JComboBox<Item> trainingBox;
        private JComboBox<Item> exerciseBox;

        private void loadDropdowns(JComboBox<Item> machineBox, JComboBox<Item> equipmentBox) {
            machineBox.removeAllItems();
            equipmentBox.removeAllItems();

            machineBox.addItem(null);
            equipmentBox.addItem(null);

            for (Item m : DBUtil.getItems("machines")) {
                machineBox.addItem(m);
            }

            for (Item e : DBUtil.getItems("equipment")) {
                equipmentBox.addItem(e);
            }
        }

        public void refreshLinks() {
            loadLinkDropdowns(trainingBox, exerciseBox);
            loadLinks();
        }

        private void loadLinkDropdowns(JComboBox<Item> trainingBox, JComboBox<Item> exerciseBox) {
            trainingBox.removeAllItems();
            exerciseBox.removeAllItems();

            for (Item t : DBUtil.getItemsByTrainer("trainings", Integer.parseInt(FitnessApp.id))) {
                trainingBox.addItem(t);
            }

            for (Item e : DBUtil.getItemsByTrainer("exercises", Integer.parseInt(FitnessApp.id))) {
                exerciseBox.addItem(e);
            }
        }

        public TrainerPanel(FitnessApp app, int trainerId) {
            setLayout(new BorderLayout());

            JButton logout = new JButton("Logout");
            logout.addActionListener(e -> app.showLogin());
            add(logout, BorderLayout.NORTH);

            JTabbedPane tabs = new JTabbedPane();
            tabs.add("Exercises", exercisePanel());
            tabs.add("Trainings", trainingPanel());
            tabs.add("Training Exercises", linkPanel());

            add(tabs, BorderLayout.CENTER);
            loadExercises();
            loadTrainings();
            loadLinks();

        }

        static class Item {
            int id;
            String name;

            public Item(int id, String name) {
                this.id = id;
                this.name = name;
            }

            public String toString() {
                return (name != null) ? name : "-- none --";
            }
        }

        // =====================
        // EXERCISES
        // =====================
        private JPanel exercisePanel(){
            JPanel p = new JPanel(new BorderLayout());

            exModel.setColumnIdentifiers(new String[]{"Name","URL","Note","Machine","Equipment"});

            JComboBox<Item> machineBox = new JComboBox<>();
            JComboBox<Item> equipmentBox = new JComboBox<>();


            JButton add = new JButton("Add Exercise");
            JButton del = new JButton("Delete");

            loadDropdowns(machineBox, equipmentBox);

            JPanel top = new JPanel(new GridLayout(1,5));
            top.add(exName); top.add(exUrl); top.add(exNote);
            top.add(machineBox); top.add(equipmentBox); top.add(add); top.add(del);

            p.add(top, BorderLayout.NORTH);
            p.add(new JScrollPane(exTable), BorderLayout.CENTER);

           loadExercises();

            add.addActionListener(e->{
                Item machine = (Item) machineBox.getSelectedItem();
                Item equipment = (Item) equipmentBox.getSelectedItem();

                Integer machineId = (machine != null && machine.id != -1) ? machine.id : null;
                Integer equipmentId = (equipment != null && equipment.id != -1) ? equipment.id : null;

                DBUtil.insertExercise(exName.getText() ,exUrl.getText(),
                        exNote.getText(), machineId, equipmentId);
                loadExercises();
                refreshLinks();
            });

            del.addActionListener(e->{
                int r = exTable.getSelectedRow();
                if(r!=-1){
                    int id = Integer.parseInt(exModel.getValueAt(r,0).toString());
                    DBUtil.delete("exercises", id);
                    loadExercises();
                    refreshLinks();
                }
            });

            return p;
        }

        // =====================
        // TRAININGS
        // =====================
        private JPanel trainingPanel(){
            JPanel p = new JPanel(new BorderLayout());

            trModel.setColumnIdentifiers(new String[]{"Name"});

            JButton add = new JButton("Add Training");
            JButton del = new JButton("Delete");

            JPanel top = new JPanel(new GridLayout(1,3));
            top.add(trName); top.add(add); top.add(del);

            p.add(top, BorderLayout.NORTH);
            p.add(new JScrollPane(trTable), BorderLayout.CENTER);

            loadTrainings();

            add.addActionListener(e->{
                DBUtil.insertTraining(trName.getText());
                loadTrainings();
                refreshLinks();
            });

            del.addActionListener(e->{
                int r = trTable.getSelectedRow();
                if(r!=-1){
                    int id = Integer.parseInt(trModel.getValueAt(r,0).toString());
                    DBUtil.delete("trainings", id);
                    loadTrainings();
                    refreshLinks();
                }
            });

            return p;
        }

        // =====================
        // LINK (Vezbe u trening)
        // =====================
        private JPanel linkPanel(){
            JPanel p = new JPanel(new BorderLayout());

            JTextField repsField = new JTextField();
            JTextField setsField = new JTextField();
            JTextField durationField = new JTextField();
            trainingBox = new JComboBox<>();
            exerciseBox = new JComboBox<>();

            JPanel inputPanel = new JPanel(new GridLayout(1,6));
            inputPanel.add(new JLabel("Reps"));
            inputPanel.add(repsField);
            inputPanel.add(new JLabel("Sets"));
            inputPanel.add(setsField);
            inputPanel.add(new JLabel("Duration"));
            inputPanel.add(durationField);
            inputPanel.add(trainingBox);
            inputPanel.add(exerciseBox);

            loadLinkDropdowns(trainingBox, exerciseBox);

            p.add(inputPanel, BorderLayout.NORTH);

            linkModel.setColumnIdentifiers(new String[]{"Training","Exercise","Reps","Sets","Duration"});

            JButton add = new JButton("Add Exercise To Training");
            JButton del = new JButton("Remove");

            p.add(new JScrollPane(linkTable), BorderLayout.CENTER);

            JPanel bottom = new JPanel();
            bottom.add(add); bottom.add(del);
            p.add(bottom, BorderLayout.SOUTH);

            loadLinks();

            add.addActionListener(e -> {
                Item tr = (Item) trainingBox.getSelectedItem();
                Item ex = (Item) exerciseBox.getSelectedItem();

                if (tr != null && ex != null) {
                    try {

                        int reps = Integer.parseInt(repsField.getText());
                        int sets = Integer.parseInt(setsField.getText());
                        int duration = Integer.parseInt(durationField.getText());

                        DBUtil.addExerciseToTraining(tr.id, ex.id, reps, sets, duration);
                        loadLinks();

                    } catch (Exception exx) {
                        JOptionPane.showMessageDialog(p, "Invalid input");
                    }
                }
            });

            del.addActionListener(e->{
                int r = linkTable.getSelectedRow();
                if(r!=-1){
                    int id = Integer.parseInt(linkModel.getValueAt(r,0).toString());
                    DBUtil.delete("links", id);
                    loadLinks();
                }
            });

            return p;
        }

        private void loadExercises(){ exModel.setRowCount(0); for(Vector<String> r: DBUtil.getExercises(Integer.parseInt(FitnessApp.id))) exModel.addRow(r);}
        private void loadTrainings(){ trModel.setRowCount(0); for(Vector<String> r: DBUtil.getTrainings(Integer.parseInt(FitnessApp.id))) trModel.addRow(r);}
        private void loadLinks(){ linkModel.setRowCount(0); for(Vector<String> r: DBUtil.getLinks(Integer.parseInt(FitnessApp.id))) linkModel.addRow(r);}
    }

    // =====================
// SESSION
// =====================
    class UserSession {
        int id;
        String role;
    }


// =====================
// DB UTIL
// =====================
class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/fitness_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String loginRole(String username, String password) {

        FitnessApp.id = null;

        String sql = "SELECT id FROM users WHERE username=? AND password=?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FitnessApp.id = String.valueOf(rs.getString("id"));
            }
            System.out.println(FitnessApp.id);
            if (FitnessApp.id != null) {
                if (Objects.equals(username, "admin")) {
                    return "ADMIN";
                } else {
                    return "TRAINER";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insertTraining(String name, String desc, int trainerId) {
        try (Connection c = getConnection(); PreparedStatement s = c.prepareStatement("INSERT INTO trainings(name,description,trainer_id) VALUES(?,?,?)")) {
            s.setString(1, name);
            s.setString(2, desc);
            s.setInt(3, trainerId);
            s.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTraining(int id, String name, String desc) {
        try (Connection c = getConnection(); PreparedStatement s = c.prepareStatement("UPDATE trainings SET name=?, description=? WHERE id=?")) {
            s.setString(1, name);
            s.setString(2, desc);
            s.setInt(3, id);
            s.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static java.util.List<Vector<String>> getTrainings(int trainerId){
//        java.util.List<Vector<String>> list = new java.util.ArrayList<>();
//        try(Connection c=getConnection(); PreparedStatement s=c.prepareStatement("SELECT * FROM trainings WHERE trainer_id=?")){
//            s.setInt(1,trainerId);
//            ResultSet rs=s.executeQuery();
//            while(rs.next()){
//                Vector<String> r=new Vector<>();
//                r.add(rs.getString("id"));
//                r.add(rs.getString("name"));
//                r.add(rs.getString("description"));
//                list.add(r);
//            }
//        }catch(Exception e){e.printStackTrace();}
//        return list;
//    }

    public static void insertMachine(String name) {
        String sql = "INSERT INTO machines(name) VALUES(?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertEquipment(String name) {
        String sql = "INSERT INTO equipment(name) VALUES(?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static java.util.List<Vector<String>> getSimple(String table) {
        java.util.List<Vector<String>> list = new java.util.ArrayList<>();

        String sql = "SELECT * FROM " + table;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("name"));
                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void insertExercise(String name, String url, String note, Integer machineId, Integer equipmentId){
        int trainerId = Integer.parseInt(FitnessApp.id);
        try(Connection c=getConnection(); PreparedStatement s=c.prepareStatement(
                "INSERT INTO exercises(name, trainer_id, video_url, note, machine_id, equipment_id) VALUES(?,?,?,?,?,?)")){
            s.setString(1,name); s.setInt(2,trainerId);
            s.setString(3,url); s.setString(4,note);
            if (machineId != null)
                s.setInt(5, machineId);
            else
                s.setNull(5, Types.INTEGER);

            if (equipmentId != null)
                s.setInt(6, equipmentId);
            else
                s.setNull(6, Types.INTEGER);s.executeUpdate();
        }catch(Exception e){e.printStackTrace();}
    }

    public static void insertTraining(String name){
        int trainerId = Integer.parseInt(FitnessApp.id);
        try(Connection c=getConnection(); PreparedStatement s=c.prepareStatement("INSERT INTO trainings(name, trainer_id) VALUES(?,?)")){
            s.setString(1,name); s.setInt(2,trainerId); s.executeUpdate();
        }catch(Exception e){e.printStackTrace();}
    }

    public static java.util.List<TrainerPanel.Item> getItems(String table) {
        java.util.List<TrainerPanel.Item> list = new java.util.ArrayList<>();

        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT id, name FROM " + table)) {

            while (rs.next()) {
                list.add(new TrainerPanel.Item(rs.getInt("id"), rs.getString("name")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public static void addExerciseToTraining(int trId, int exId, int reps, int sets, int duration){
        String sql = "INSERT INTO links(training_id, exercise_id, reps, sets, duration) VALUES(?,?,?,?,?)";

        try (Connection c = getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setInt(1, trId);
            s.setInt(2, exId);
            s.setInt(3, reps);
            s.setInt(4, sets);
            s.setInt(5, duration);

            s.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static java.util.List<Vector<String>> getExercises(int trainerId){
        java.util.List<Vector<String>> list = new java.util.ArrayList<>();

        String sql = """
        SELECT e.id, e.name, e.video_url, e.note, m.name, eq.name
        FROM exercises e
        LEFT JOIN machines m ON e.machine_id = m.id
        LEFT JOIN equipment eq ON e.equipment_id = eq.id
        WHERE e.trainer_id = ?
    """;

        try (Connection c = getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setInt(1, trainerId);
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString(1)); // ID
                row.add(rs.getString(2)); // Exercise name
                row.add(rs.getString(3)); // Video URL
                row.add(rs.getString(4)); // Note
                row.add(rs.getString(5)); // Machine name
                row.add(rs.getString(6)); // Equipment name
                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static java.util.List<Vector<String>> getTrainings(int trainerId){
        return getList("SELECT * FROM trainings WHERE trainer_id="+trainerId);
    }

    public static java.util.List<Vector<String>> getLinks(int trainerId){
        java.util.List<Vector<String>> list = new java.util.ArrayList<>();

        String sql = """
        SELECT te.id, t.name, e.name, te.reps, te.sets, te.duration
        FROM links te
        JOIN trainings t ON te.training_id = t.id
        JOIN exercises e ON te.exercise_id = e.id
        WHERE t.trainer_id = ?
    """;

        try (Connection c = getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setInt(1, trainerId);
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                Vector<String> r = new Vector<>();
                r.add(rs.getString(1)); // id
                r.add(rs.getString(2)); // training
                r.add(rs.getString(3)); // exercise
                r.add(rs.getString(4)); // reps
                r.add(rs.getString(5)); // sets
                r.add(rs.getString(6)); // duration
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void delete(String table, int id){
        try(Connection c=getConnection(); PreparedStatement s=c.prepareStatement("DELETE FROM "+table+" WHERE id=?")){
            s.setInt(1,id); s.executeUpdate();
        }catch(Exception e){e.printStackTrace();}
    }

    public static java.util.List<TrainerPanel.Item> getItemsByTrainer(String table, int trainerId) {
        java.util.List<TrainerPanel.Item> list = new java.util.ArrayList<>();

        String sql = "SELECT id, name FROM " + table + " WHERE trainer_id=?";

        try (Connection c = getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {

            s.setInt(1, trainerId);
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                list.add(new TrainerPanel.Item(rs.getInt("id"), rs.getString("name")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static java.util.List<Vector<String>> getList(String sql){
        java.util.List<Vector<String>> list=new java.util.ArrayList<>();
        try(Connection c=getConnection(); Statement s=c.createStatement()){
            ResultSet rs=s.executeQuery(sql);
            while(rs.next()){
                Vector<String> r=new Vector<>();
                r.add(rs.getString(1));
                r.add(rs.getString(2));
                if(rs.getMetaData().getColumnCount()>2) r.add(rs.getString(3));
                list.add(r);
            }
        }catch(Exception e){e.printStackTrace();}
        return list;
    }
}



