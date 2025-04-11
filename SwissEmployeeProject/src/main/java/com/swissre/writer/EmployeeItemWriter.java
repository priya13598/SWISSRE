package com.swissre.writer;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.swissre.model.Employee;
import com.swissre.service.EmployeeFiltration;

@Component
public class EmployeeItemWriter implements ItemWriter<Employee>{
	
	@Autowired
	private EmployeeFiltration employeeFiltration;
	
	Map<Integer,Employee> employeeMap = new HashMap<>();
	
	@Override
	public void write(List<? extends Employee> items) throws Exception {	
		employeeMap = items.stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
		employeeFiltration.getEmployeeData(employeeMap);
		
	}

	
}
