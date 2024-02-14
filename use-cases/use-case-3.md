# Use Case 3: Produce a Report on the Salary of Employees in a Department (Internal)

## Characteristic Information

### Goal in Context
As a ***department manager*** I want ***to produce a report on the salary of employees in my department*** so that ***I can support financial reporting for my department.***

### Scope
Department

### Level
Primary task

### Preconditions
- Database contains current employee salary data
- Database contains current employee department data

### Success End Condition
A report is available for the department manager

### Failed End Condition
No report is produced

### Primary Actor
Department Manager

### Trigger
A request for employee salary information is sent to the department manager

## Main Success Scenario
1. A request is sent to the department manager for employee salary information
2. The department manager runs a report on the salary of all employees in their department
3. The department manager provides the report to whoever requested it

## Extensions
2. **Department manager is not the manager of the selected department**
   1. The system refuses the request
   2. The department manager informs the requesting party that the report cannot be produced 

## Sub-variations
None

## Schedule
**Due Date**: Release 1.0
