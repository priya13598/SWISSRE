package com.swissre.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.swissre.writer.EmployeeItemWriter;

import com.swissre.model.Employee;

@Configuration
public class EmployeeJob {

	@Autowired
	private JobBuilderFactory  jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private EmployeeItemWriter firstItemWriter;
	
	@Value("${file.path}")
	private String filePath;
	
	
	@Bean
	public Job CsvReadJob()
	{
		return jobBuilderFactory.get("SWISS_EMPLOYEE_JOB")
				.incrementer(new RunIdIncrementer())
				.flow(CsvReadStep())
				.end()
				.build();
	}
	
	private Step CsvReadStep()
	{
		return stepBuilderFactory.get("SWISS_EMPLOYEE_STEP")
				.<Employee,Employee>chunk(1000)
				.reader(flatFileItemReader())
				.writer(firstItemWriter)
				.build();
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<Employee> flatFileItemReader()
	{
		FlatFileItemReader<Employee> flatFileItemReader = new FlatFileItemReader<Employee>();
		flatFileItemReader.setResource(new ClassPathResource(filePath));
		
		DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<Employee>();
		
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames("Id","firstName","lastName","salary","managerId");
		lineTokenizer.setDelimiter(",");
		
		BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Employee.class);
		
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		
		
		flatFileItemReader.setLineMapper(lineMapper);
		flatFileItemReader.setLinesToSkip(1);
		
		return flatFileItemReader;
	}
	
}
