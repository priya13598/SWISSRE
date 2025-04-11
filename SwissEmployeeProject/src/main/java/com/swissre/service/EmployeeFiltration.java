package com.swissre.service;

import java.util.*;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.swissre.model.Employee;

@Component
public class EmployeeFiltration {
	
	static Map<Integer,Employee> employeeMap = new HashMap<>();
	static Map<Integer,ArrayList<Employee>> subordinates = new HashMap<Integer, ArrayList<Employee>>();
	static Employee ceo = null;
	
	
	 public static void getEmployeeData(Map<Integer, Employee> map) {
		 employeeMap = map;
		 EmployeeFiltration.buildTree();
		 checkSalaryRules(employeeMap);
		 checkDepth(ceo);
	    }

	// Build tree
	 public static void buildTree()
	 {
		 for (Employee emp : employeeMap.values()) {
	         if (emp.getManagerId() != null) {
	           Employee e=  employeeMap.get(emp.getManagerId());
	           ArrayList<Employee> list = subordinates.getOrDefault(e.getId(), new ArrayList<>());
	           list.add(emp);
	           subordinates.put(e.getId(), list);
	         } else {
	             ceo = emp;
	         }
	     }
	 }
     
     
	 // Check salary conditions
	 private static void checkSalaryRules(Map<Integer, Employee> map) {
		 System.out.println("Generating Salary Conditions for Manager...");
		 
	        for (Employee manager : map.values()) {
	        	if(subordinates.getOrDefault(manager.getId(), new ArrayList<Employee>()).size() == 0) continue;

	            double avgSubSalary = subordinates.get(manager.getId()).stream().mapToDouble(e -> e.getSalary()).average().orElse(0);
	            double minRequired = avgSubSalary * 1.2;
	            double maxAllowed = avgSubSalary * 1.5;

	            if (manager.getSalary() < minRequired) {
	                System.out.printf("%s earns %.2f, should earn at least %.2f\n", manager.getFirstName(), manager.getSalary(), minRequired);
	            } else if (manager.getSalary() > maxAllowed) {
	                System.out.printf("%s earns %.2f, should earn at most %.2f\n", manager.getFirstName(), manager.getSalary(), maxAllowed);
	            }
	        }
	    }

	 // Check reporting line depth
	 private static void checkDepth(Employee ceo) {
		 System.out.println(" Generating Reporting Line...");
		 
	        Queue<Employee> queue = new LinkedList<>();
	        Map<Integer, Integer> depthMap = new HashMap<>();
	        queue.add(ceo);
	        depthMap.put(ceo.getId(), 0);

	        while (!queue.isEmpty()) {
	            Employee current = queue.poll();
	            int currentDepth = depthMap.get(current.getId());

	            if (currentDepth > 4) {
	                System.out.printf("%s has %d managers above (too many)\n", current.getFirstName(), currentDepth);
	            }

	            for (Employee sub : subordinates.getOrDefault(current.getId(), new ArrayList<>())) {
	                depthMap.put(sub.getId(), currentDepth + 1);
	                queue.add(sub);
	            }
	        }
	    }


}
