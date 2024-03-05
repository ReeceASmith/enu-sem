package com.napier.sem;

public class Department {
    private String dept_no;
    private String dept_name;
    private Employee manager;



    public Department(String dept_no, String dept_name, Employee manager) {
        this.dept_no = dept_no;
        this.dept_name = dept_name;
        this.manager = manager;
    }

    public String getNo() {
        return dept_no;
    }

    public String getName() {
        return dept_name;
    }

    public Employee getManager() {
        return manager;
    }

    public void setNo(String dept_no) {
        this.dept_no = dept_no;
    }

    public void setName(String dept_name) {
        this.dept_name = dept_name;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }
}
