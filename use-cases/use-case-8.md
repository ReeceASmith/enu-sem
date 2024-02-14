# Use Case 8: Delete Employee Details

## Characteristic Information

### Goal in Context
As an ***HR Advisor*** I want ***to delete an employee's details*** so that ***the company is compliant with data retention legislation.***

### Scope
Organisation

### Level
Primary task

### Preconditions
None

### Success End Condition
Employee details are deleted

### Failed End Condition
Employee details are still contained within the database

### Primary Actor
HR Advisor

### Trigger
A request to remove an employee is sent to HR

## Main Success Scenario
1. A request is sent to HR to remove an employee
2. HR selects the employee to delete
3. HR deletes the employee from the database
4. HR confirms to the requesting party that the employee has been removed

## Extensions
2. **Selected employee does not exist in database**
    1. HR skips to **Main Success Scenario Step 4** 

## Sub-variations
None

## Schedule
**Due Date**: Release 1.0
