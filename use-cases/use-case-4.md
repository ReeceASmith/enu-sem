# Use Case 4: Produce a Report on the Salary of Employees of a Particular Role

## Characteristic Information

### Goal in Context
As an ***HR Advisor*** I want ***to produce a report on the salary of employees of a given role*** so that ***I can support financial reporting of the organisation.***

### Scope
Organisation

### Level
Primary task

### Preconditions
- Database contains current employee salary data
- Database contains current employee role data

### Success End Condition
A report is available for HR to provide to whoever requested it

### Failed End Condition
No report is produced

### Primary Actor
HR Advisor

### Trigger
A request for employee salary information is sent to HR

## Main Success Scenario
1. A request is sent to HR for employee salary information
2. HR selects the role for which the report is requested
3. HR runs a report on the salary of all employees with the selected role
4. HR provides the report to whoever requested it

## Extensions
2. **Selected role does not exist in database**
    1. HR informs the requesting party the role does not exist

## Sub-variations
None

## Schedule
**Due Date**: Release 1.0
