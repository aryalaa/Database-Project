/**
 * Author: Lon Smith, Ph.D.
 * Modified by: Aadarsha Aryal
 * Description: Completed version with full database functionality.
 */

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

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

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtDatabase;
    private JList<String> lstDepartment;
    private DefaultListModel<String> department = new DefaultListModel<String>();
    private JList<String> lstProject;
    private DefaultListModel<String> project = new DefaultListModel<String>();
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
        setBounds(100, 100, 450, 347);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Database:");
        lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel.setBounds(21, 23, 59, 14);
        contentPane.add(lblNewLabel);

        txtDatabase = new JTextField("companydb");
        txtDatabase.setBounds(90, 20, 193, 20);
        contentPane.add(txtDatabase);
        txtDatabase.setColumns(10);

        JButton btnDBFill = new JButton("Fill");
        btnDBFill.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btnDBFill.setBounds(307, 19, 68, 23);
        contentPane.add(btnDBFill);

        JLabel lblDepartment = new JLabel("Department");
        lblDepartment.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblDepartment.setBounds(52, 63, 89, 14);
        contentPane.add(lblDepartment);

        JLabel lblProject = new JLabel("Project");
        lblProject.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblProject.setBounds(255, 63, 47, 14);
        contentPane.add(lblProject);

        // DEPARTMENT LIST SCROLLABLE
        lstDepartment = new JList<>(department);
        lstDepartment.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollDept = new JScrollPane(lstDepartment);
        scrollDept.setBounds(36, 84, 172, 60);
        contentPane.add(scrollDept);

        // PROJECT LIST SCROLLABLE
        lstProject = new JList<>(project);
        lstProject.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollProj = new JScrollPane(lstProject);
        scrollProj.setBounds(225, 84, 150, 60);
        contentPane.add(scrollProj);

        JCheckBox chckbxNotDept = new JCheckBox("Not");
        chckbxNotDept.setBounds(71, 150, 59, 23);
        contentPane.add(chckbxNotDept);

        JCheckBox chckbxNotProject = new JCheckBox("Not");
        chckbxNotProject.setBounds(270, 150, 59, 23);
        contentPane.add(chckbxNotProject);

        JLabel lblEmployee = new JLabel("Employee");
        lblEmployee.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblEmployee.setBounds(52, 179, 89, 14);
        contentPane.add(lblEmployee);

        // EMPLOYEE LIST SCROLLABLE
        textAreaEmployee = new JTextArea();
        textAreaEmployee.setEditable(false);
        JScrollPane scrollEmp = new JScrollPane(textAreaEmployee);
        scrollEmp.setBounds(36, 197, 339, 68);
        contentPane.add(scrollEmp);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(80, 276, 89, 23);
        contentPane.add(btnSearch);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(236, 276, 89, 23);
        contentPane.add(btnClear);

        /* ---------------- FILL BUTTON ---------------- */
        btnDBFill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fillData();
            }
        });

        /* ---------------- SEARCH BUTTON ---------------- */
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchEmployees(chckbxNotDept.isSelected(), chckbxNotProject.isSelected());
            }
        });

        /* ---------------- CLEAR BUTTON ---------------- */
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textAreaEmployee.setText("");
                lstDepartment.clearSelection();
                lstProject.clearSelection();
                chckbxNotDept.setSelected(false);
                chckbxNotProject.setSelected(false);
            }
        });
    }

    /* ---------------- LOAD DEPARTMENTS & PROJECTS ---------------- */
    private void fillData() {
        department.clear();
        project.clear();

        try (Connection conn = DBConnection.connect(txtDatabase.getText().trim())) {

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT DNAME FROM DEPARTMENT;");
            while (rs.next()) {
                department.addElement(rs.getString("DNAME"));
            }

            rs = st.executeQuery("SELECT PNAME FROM PROJECT;");
            while (rs.next()) {
                project.addElement(rs.getString("PNAME"));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load database.");
        }
    }

    /* ---------------- SEARCH FUNCTION ---------------- */
    private void searchEmployees(boolean notDept, boolean notProj) {

        textAreaEmployee.setText("");

        try (Connection conn = DBConnection.connect(txtDatabase.getText().trim())) {

            StringBuilder query = new StringBuilder(
                "SELECT DISTINCT CONCAT(e.FNAME, ' ', e.LNAME) AS NAME " +
                "FROM EMPLOYEE e " +
                "JOIN DEPARTMENT d ON e.DNO = d.DNUMBER " +
                "JOIN WORKS_ON w ON e.SSN = w.ESSN " +
                "JOIN PROJECT p ON w.PNO = p.PNUMBER WHERE 1=1 "
            );

            var depts = lstDepartment.getSelectedValuesList();
            if (!depts.isEmpty()) {
                query.append(notDept ? " AND d.DNAME NOT IN (" : " AND d.DNAME IN (");
                for (int i = 0; i < depts.size(); i++) {
                    query.append("'").append(depts.get(i)).append("'");
                    if (i < depts.size() - 1) query.append(",");
                }
                query.append(")");
            }

            var projs = lstProject.getSelectedValuesList();
            if (!projs.isEmpty()) {
                query.append(notProj ? " AND p.PNAME NOT IN (" : " AND p.PNAME IN (");
                for (int i = 0; i < projs.size(); i++) {
                    query.append("'").append(projs.get(i)).append("'");
                    if (i < projs.size() - 1) query.append(",");
                }
                query.append(")");
            }

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query.toString());

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append(rs.getString("NAME")).append("\n");
            }

            textAreaEmployee.setText(sb.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error.");
        }
    }
}
