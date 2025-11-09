package com.base.CRUD_Employee;
public class EmployeeDAO {

    private Arquivo<Employee> arqEmployees;

    public EmployeeDAO() throws Exception {
        arqEmployees = new Arquivo<>("employees", Employee.class.getConstructor());
    }

    public Employee searchEmployee(int id) throws Exception {
        return arqEmployees.read(id);
    }

    public boolean insertEmployee(Employee employee) throws Exception {

        return arqEmployees.create(employee) > 0;
    }

    public boolean updateEmployee(Employee employee) throws Exception {
        return arqEmployees.update(employee);
    }

    public boolean deleteEmployee(int id) throws Exception {
        return arqEmployees.delete(id);
    }
}
