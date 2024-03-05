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

        int retries = 15;
        for (int i=0; i<retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait for db to start
                Thread.sleep(5000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "password");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database, attempt " + (i+1) + "/" + retries);
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

            // Continue - add employee details: employee number, first & last name
            Employee emp = new Employee();
            emp.emp_no = emp_rset.getInt("emp_no");
            emp.first_name = emp_rset.getString("first_name");
            emp.last_name = emp_rset.getString("last_name");

            // Repeat for department, manager and salary details
            // Job Title
            strSelect = "SELECT title FROM titles WHERE emp_no = " + ID + " AND to_date = '9999-01-01'";
            ResultSet title_rset = stmt.executeQuery(strSelect);
            if (!title_rset.next())
                { return null; }

            emp.title = title_rset.getString("title");

            // Salary
            strSelect = "SELECT salary FROM salaries WHERE emp_no = " + ID + " AND to_date = '9999-01-01'";
            ResultSet salary_rset = stmt.executeQuery(strSelect);
            if (!salary_rset.next())
                { return null; }

            emp.salary = salary_rset.getInt("salary");


            // Department
            // Use SQL subqueries to select through multiple tables - fetches department number and name
            strSelect = "SELECT * FROM (" +
                    "SELECT emp_no, dept_no FROM dept_emp WHERE emp_no = " + ID + " AND to_date = '9999-01-01'" +
                    ") as de, departments " +
                    "WHERE de.dept_no=departments.dept_no";
            ResultSet dept_rset = stmt.executeQuery(strSelect);
            if (!dept_rset.next())
                { return null; }

            // Get department details
            String temp_dept_no = dept_rset.getString("dept_no");
            String temp_dept_name = dept_rset.getString("dept_name");
            // Need to manually fetch department, in case its manager is this employee
            //      otherwise getDepartment() will call getEmployee() infinitely recursively
            /* Department dept = getDepartment(temp_dept_no); */

            strSelect = "SELECT emp_no, dept_no FROM dept_manager WHERE dept_no = '" + temp_dept_no + "' AND to_date = '9999-01-01'";
            ResultSet mgr_rset = stmt.executeQuery(strSelect);
            if (!mgr_rset.next()) {
                throw new RuntimeException("Department manager returned no result from database");
            }

            // No point checking twice - assign manager as self, then check and reassign if not
            // This custom database check prevents infinite recursion from the getDepartment() method
            emp.manager = emp;
            if (mgr_rset.getInt("emp_no") != ID) {
                emp.manager = getEmployee(mgr_rset.getInt("emp_no"));
            }


            // Assign department to employee
            emp.department = new Department(
                    temp_dept_no,
                    temp_dept_name,
                    emp.manager
            );


            // Close all result sets
            emp_rset.close();
            title_rset.close();
            salary_rset.close();
            dept_rset.close();
            mgr_rset.close();

            return emp;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }



    public Department getDepartment(String dept_no) {
        try {
            // Get department details
            Statement stmt = con.createStatement();


            // Get department details
            String strSelect = "SELECT * FROM departments WHERE dept_no = '" + dept_no + "'";
            ResultSet dept_rset = stmt.executeQuery(strSelect);
            if (!dept_rset.next()) {
                throw new RuntimeException("Department number returned no result from database");
            }


            // Get department manager
            strSelect = "SELECT emp_no, dept_no FROM dept_manager " +
                    "WHERE dept_no = '" + dept_no + "' AND to_date = '9999-01-01'";
            ResultSet mgr_rset = stmt.executeQuery(strSelect);
            if (!mgr_rset.next()) {
                throw new RuntimeException("Department manager returned no result from database");
            }


            // Get manager details
            strSelect = "SELECT * FROM employees WHERE emp_no = " + mgr_rset.getInt("emp_no");
            ResultSet emp_rset = stmt.executeQuery(strSelect);
            if (!emp_rset.next()) {
                throw new RuntimeException("Manager employee number returned no result from database");
            }



            Employee mgr = new Employee();
            mgr.emp_no = emp_rset.getInt("emp_no");
            mgr.first_name = emp_rset.getString("first_name");
            mgr.last_name = emp_rset.getString("last_name");


            Department dept = new Department(
                    dept_rset.getString("dept_no"),
                    dept_rset.getString("dept_name"),
                    mgr
            );


            dept.setManager(mgr);


            // Close all result sets
            dept_rset.close();
            mgr_rset.close();
            emp_rset.close();

            return dept;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get department details");
            return null;
        }
    }



    public void displayEmployee(Employee emp) {
        if (emp == null) { return; }


        String mgr_string;
        if (emp.manager == null) {
            mgr_string = "Is the manager";
        } else {
            mgr_string = emp.manager.first_name + " " + emp.manager.last_name;
        }

        System.out.println(
                "Employee:\n\tID: " + emp.emp_no
                        + "\n\tName: " + emp.first_name + " " + emp.last_name
                        + "\n\tJob Title: " + emp.title
                        + "\n\tSalary: £" + emp.salary
                        + "\n\tDepartment: " + emp.department.getName()
                        + "\n\tManager: " + mgr_string
                        + "\n"
        );
    }



    public void displayDepartment(Department dept) {
        if (dept == null) { return; }
        System.out.println(
                "Department:\n\tDepartment Number: " + dept.getNo()
                        + "\n\tName: " + dept.getName()
                        + "\n\tManager: " + dept.getManager().first_name + " " + dept.getManager().last_name
                        + "\n"
        );
    }



    public void displayEmployeeSalariesByDept(String dept_no) {
        String strSelect = "";
        if (!dept_no.isBlank()) {
            strSelect = "SELECT employees.emp_no, first_name, last_name, salary " +
                    "FROM employees, dept_emp, salaries " +
                    "WHERE employees.emp_no = salaries.emp_no " +
                    "AND employees.emp_no = dept_emp.emp_no " +
                    "AND dept_emp.dept_no = '" + dept_no + "' " +
                    "AND salaries.to_date = '9999-01-01'";
        }
        displayEmployeeSalaries(strSelect);
    }


    public void displayEmployeeSalariesByDept(Employee dept_manager) {
        if (dept_manager == null || dept_manager.department == null) {
            System.out.println("Error displaying employee salaries: Department manager does not exist or has no department");
            return;
        }

        try {
            // Get department ID
            Statement stmt = con.createStatement();

            ResultSet dept_rset = stmt.executeQuery("SELECT dept_no " +
                    "FROM departments " +
                    "WHERE dept_name='" + dept_manager.department.getName() + "'");

            if (!dept_rset.next()) {
                throw new RuntimeException("Department name returned no result from database");
            }

            String temp_dept_no = dept_rset.getString("dept_no");
            System.out.println("DeptNo: " + temp_dept_no);
            displayEmployeeSalariesByDept(temp_dept_no);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get department ID");
        }
    }


    public void displayEmployeeSalariesByRole(String role) {
        if (role.isBlank()) {
            System.out.println("Error displaying employee salaries: No role provided");
            return;
        }
        String strSelect = "SELECT employees.emp_no, first_name, last_name, salary " +
                "FROM employees, salaries, titles " +
                "WHERE employees.emp_no=salaries.emp_no " +
                "AND employees.emp_no=titles.emp_no " +
                "AND salaries.to_date = '9999-01-01' " +
                "AND titles.title = '" + role + "' " +
                "AND titles.to_date = '9999-01-01' ";

        displayEmployeeSalaries(strSelect);
    }

    public void displayEmployeeSalaries() {
        displayEmployeeSalaries("SELECT employees.emp_no, first_name, last_name, salary " +
                "FROM employees, salaries " +
                "WHERE employees.emp_no=salaries.emp_no " +
                "AND salaries.to_date = '9999-01-01'");
    }



    private void displayEmployeeSalaries(String query) {
        // Escapes if wrong inputs
        if (query == null) {
            return;
        }
        if (query.isBlank()) {
            System.out.println("displayEmployeeSalaries(String query): query is blank");
            return;
        }

        try {
            // Create SQL statement
            Statement stmt = con.createStatement();

            // Execute
            ResultSet emp_rset = stmt.executeQuery(query);

            // Return if no data
            if (!emp_rset.next()) {
                System.out.println("displayEmployeeSalaries(String query): ResultSet returned no results");
                return;
            }

            // For every employee, display their ID, name and salary
            System.out.println("ID\t\tName\t\t\t Salary");

            do {
                System.out.printf("%-8.7s%1.1s. %-12.11s £%d%n", emp_rset.getString("emp_no"), emp_rset.getString("first_name"), emp_rset.getString("last_name"), emp_rset.getInt("salary"));
            } while (emp_rset.next());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("displayEmployeeDetails(String query): Failed to get employee details");
        }
    }





    public static void main(String[] args) {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();

        Employee e = a.getEmployee(111939);
        a.displayEmployee(e);
        a.displayDepartment(e.department);

        // Disconnect from database
        a.disconnect();
    }
}