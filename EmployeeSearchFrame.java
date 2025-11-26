import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class EmployeeSearchFrame extends JFrame {

    private JPanel contentPane;
    private JTextField txtDatabase;
    private DefaultListModel<String> department = new DefaultListModel<>();
    private DefaultListModel<String> project = new DefaultListModel<>();
    private JList<String> lstDepartment;
    private JList<String> lstProject;
    private JTextArea textAreaEmployee;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                EmployeeSearchFrame frame = new EmployeeSearchFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public EmployeeSearchFrame() {
        setTitle("Employee Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 520, 420);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblDb = new JLabel("Database:");
        lblDb.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblDb.setBounds(20, 20, 80, 20);
        contentPane.add(lblDb);

        txtDatabase = new JTextField();
        txtDatabase.setBounds(100, 20, 200, 20);
        contentPane.add(txtDatabase);

        JButton btnFill = new JButton("Fill");
        btnFill.setBounds(320, 20, 80, 23);
        contentPane.add(btnFill);

        JLabel lblDept = new JLabel("Department");
        lblDept.setBounds(40, 60, 150, 20);
        contentPane.add(lblDept);

        JLabel lblProj = new JLabel("Project");
        lblProj.setBounds(280, 60, 150, 20);
        contentPane.add(lblProj);

        lstDepartment = new JList<>(department);
        lstDepartment.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollDept = new JScrollPane(lstDepartment);
        scrollDept.setBounds(20, 80, 200, 80);
        contentPane.add(scrollDept);

        lstProject = new JList<>(project);
        lstProject.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollProj = new JScrollPane(lstProject);
        scrollProj.setBounds(260, 80, 200, 80);
        contentPane.add(scrollProj);

        JCheckBox chkNotDept = new JCheckBox("Not");
        chkNotDept.setBounds(80, 170, 80, 20);
        contentPane.add(chkNotDept);

        JCheckBox chkNotProj = new JCheckBox("Not");
        chkNotProj.setBounds(310, 170, 80, 20);
        contentPane.add(chkNotProj);

        JLabel lblEmp = new JLabel("Employees");
        lblEmp.setBounds(40, 200, 200, 20);
        contentPane.add(lblEmp);

        textAreaEmployee = new JTextArea();
        JScrollPane scrollEmp = new JScrollPane(textAreaEmployee);
        scrollEmp.setBounds(20, 220, 440, 100);
        contentPane.add(scrollEmp);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(80, 330, 100, 25);
        contentPane.add(btnSearch);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(300, 330, 100, 25);
        contentPane.add(btnClear);

        // Fill Button Logic
        btnFill.addActionListener((ActionEvent e) -> {
            department.clear();
            project.clear();

            try {
                Connection conn = DBConnection.connect(txtDatabase.getText());

                PreparedStatement stmtDept = conn.prepareStatement("SELECT DNAME FROM DEPARTMENT");
                ResultSet rsDept = stmtDept.executeQuery();
                while (rsDept.next())
                    department.addElement(rsDept.getString(1));

                PreparedStatement stmtProj = conn.prepareStatement("SELECT PNAME FROM PROJECT");
                ResultSet rsProj = stmtProj.executeQuery();
                while (rsProj.next())
                    project.addElement(rsProj.getString(1));

                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Failed to load database.");
            }
        });

        // Search Button Logic
        btnSearch.addActionListener((ActionEvent e) -> {
            textAreaEmployee.setText("");

            try {
                Connection conn = DBConnection.connect(txtDatabase.getText());

                ArrayList<String> dept = new ArrayList<>(lstDepartment.getSelectedValuesList());
                ArrayList<String> proj = new ArrayList<>(lstProject.getSelectedValuesList());
                boolean notDept = chkNotDept.isSelected();
                boolean notProj = chkNotProj.isSelected();

                String query = buildQuery(dept, proj, notDept, notProj);
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    textAreaEmployee.append(rs.getString("FNAME") + " " + rs.getString("LNAME") + "\n");
                }

                conn.close();

            } catch (Exception ex) {
                textAreaEmployee.setText("Error searching employees.");
            }
        });

        // Clear Button Logic
        btnClear.addActionListener((ActionEvent e) -> {
            textAreaEmployee.setText("");
            lstDepartment.clearSelection();
            lstProject.clearSelection();
            chkNotDept.setSelected(false);
            chkNotProj.setSelected(false);
        });
    }

    private String buildQuery(
            ArrayList<String> dept,
            ArrayList<String> proj,
            boolean notDept,
            boolean notProj) {

        StringBuilder q = new StringBuilder(
            "SELECT DISTINCT E.FNAME, E.LNAME " +
            "FROM EMPLOYEE E " +
            "LEFT JOIN WORKS_ON W ON E.SSN = W.ESSN " +
            "LEFT JOIN PROJECT P ON W.PNO = P.PNUMBER WHERE 1=1 ");

        if (!dept.isEmpty()) {
            q.append(" AND E.DNO ")
                .append(notDept ? "NOT" : "")
                .append(" IN (SELECT DNUMBER FROM DEPARTMENT WHERE DNAME IN (");

            for (int i = 0; i < dept.size(); i++) {
                q.append("'" + dept.get(i) + "'");
                if (i < dept.size() - 1) q.append(",");
            }
            q.append(")) ");
        }

        if (!proj.isEmpty()) {
            q.append(" AND P.PNAME ")
                .append(notProj ? "NOT" : "")
                .append(" IN (");

            for (int i = 0; i < proj.size(); i++) {
                q.append("'" + proj.get(i) + "'");
                if (i < proj.size() - 1) q.append(",");
            }
            q.append(") ");
        }

        return q.toString();
    }
}
