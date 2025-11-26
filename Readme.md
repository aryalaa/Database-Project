Database Project – Employee Search Application
Group Members: Aadarsha Aryal, Murari Raman Upreti, Sean Rose Vincent
Course: CSCI 4055 – Database Management Systems

DESCRIPTION
-----------
This Java Swing program connects to a MySQL database that uses the COMPANY schema.
It loads all Department and Project names and allows the user to search for employees
based on selected departments and/or projects. The "Not" checkboxes invert the search
to show employees who are NOT in the selected department or NOT working on selected projects.

TOOLS USED
-----------
• Java 17 (OpenJDK)
• Swing for GUI
• MySQL Server 9.5.0
• MySQL Workbench for testing
• JDBC Connector: mysql-connector-j-9.5.0.jar

HOW TO RUN
-----------
1. Place the MySQL Connector/J jar file in the same folder as the .java files.
2. Compile:
   javac -cp .:mysql-connector-j-9.5.0.jar DBConnection.java EmployeeSearchFrame.java
   
3. Run:
   java -cp .:mysql-connector-j-9.5.0.jar EmployeeSearchFrame

4. In the GUI:
   • Enter database name: companydb
   • Click "Fill"
   • Select Departments and Projects
   • Use "Not" checkboxes if needed
   • Click "Search"

DATABASE INFORMATION
--------------------
Database name: companydb
Schema: COMPANY (Department, Project, Employee, Works_on)
SQL file included: companydb.sql
