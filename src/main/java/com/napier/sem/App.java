package com.napier.sem;

import java.sql.*;

public class App
{
    /**
     * Connection to MySQL database
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect() {
        try {
            // Load database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Couldn't load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i=0; i<retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait for db to start
                Thread.sleep(20000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "password");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database, attempt " + i);
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }



    public void disconnect() {
        if (con == null) { return; }

        try {
            // Close connection
            con.close();
        } catch (Exception e) {
            System.out.println("Error closing connection to database");
        }
    }



    public Employee getEmployee(int ID) {
        try {
            // Get employee details

            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT emp_no, first_name, last_name FROM employees WHERE emp_no = " + ID;
            // Execute SQL statement
            ResultSet emp_rset = stmt.executeQuery(strSelect);
            // Check data is returned - return null if not
            if (!emp_rset.next())
                { return null; }

            // Continue - add employee details
            Employee emp = new Employee();
            emp.emp_no = emp_rset.getInt("emp_no");
            emp.first_name = emp_rset.getString("first_name");
            emp.last_name = emp_rset.getString("last_name");

            // Repeat for department, manager and salary details
            // Ensure dates are correct
            strSelect = "SELECT title FROM titles WHERE emp_no = " + ID + " AND to_date = '9999-01-01'";
            ResultSet title_rset = stmt.executeQuery(strSelect);
            if (!title_rset.next())
                { return null; }

            emp.title = title_rset.getString("title");


            strSelect = "SELECT salary FROM salaries WHERE emp_no = " + ID + " AND to_date = '9999-01-01'";
            ResultSet salary_rset = stmt.executeQuery(strSelect);
            if (!salary_rset.next())
                { return null; }

            emp.salary = salary_rset.getInt("salary");


            // Will fetch employee department(s?) and manager(s?)...
            return emp;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }



    public void displayEmployee(Employee emp) {
        if (emp == null) { return; }
        System.out.println(
                "Employee:\n\tID: " + emp.emp_no
                + "\n\tName: " + emp.first_name + " " + emp.last_name
                + "\n\tJob Title: " + emp.title
                + "\n\tSalary: £" + emp.salary
                + "\n\tDepartment: " + emp.dept_name
                + "\n\tManager: " + emp.manager
                + "\n"
        );
    }



    public Employee[] getAllEmployees() {
        try {
            // Get employee details
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT * FROM employees";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid
            // Check one is returned
            Employee[] employees = new Employee[100];
            int i = 0;
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                employees[i] = emp;
                i++;
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }



    public void displayEmployeeSalaries(Employee[] employees) {
        if (employees == null) { return; }

        System.out.println("ID\t\tName\tSalary\t");
        for (Employee emp: employees) {
            System.out.printf("%-8s%-1s. %-14s%-8d\n", emp.emp_no, emp.first_name, emp.last_name, emp.salary);
        }
    }



    public static void main(String[] args)
    {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();

        // Get employee
        Employee emp = a.getEmployee(255530);
        // Display results
        a.displayEmployee(emp);


        // Disconnect from database
        a.disconnect();
    }
}