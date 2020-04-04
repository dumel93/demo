package com.example.demo.service;

import com.example.demo.model.Employee;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class EmployeeService {

    private static final String urlJSON = "employees.json";
    private static final String urlCSV = "employees.csv";

    //start app
    public static void main(String[] args) {
        parseFromJSON();
        parseFromCSV();
    }

    private static void parseFromCSV() {
        BufferedReader br = null;
        String csvSplitBy = "    ";
        List<Employee> employees = new ArrayList<>();
        // create an instance of BufferedReader
        try {
            br = new BufferedReader(new FileReader(EmployeeService.urlCSV));
            // read the first line from the text file
            String line = br.readLine();
            // skip headers
            line = br.readLine();
            // loop until all lines are read
            while (line != null) {

                // use string.split to load a string array with the values from
                // each line of the file, using a space as the delimiter
                String[] attributes = line.split(csvSplitBy);
                Employee employee = createEmployeeFromCSVDates(attributes);
                // adding employee into ArrayList
                employees.add(employee);
                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }
            // main method: show Salaries of employees group by job
            System.out.println("parse from CSV: \n");
            showSalariesByJob(employees);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @SuppressWarnings("unchecked")
    private static void parseFromJSON() {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try {

            Object obj = jsonParser.parse(new FileReader(EmployeeService.urlJSON));
            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONObject jsonObject = (JSONObject) obj;
            // A JSON array. JSONObject supports java.util.List interface.
            JSONArray employees = (JSONArray) jsonObject.get("employees");
            ArrayList<Employee> employeeList = new ArrayList<>();
            //Iterate over employee array and add to list of Employees
            employees.forEach(employee -> employeeList.add(createEmployeeFromJSONObject((JSONObject) employee)));

            // main method: show Salaries of employees group by job
            System.out.println("parse from json: \n");
            showSalariesByJob(employeeList);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void showSalariesByJob(List<Employee> employeeList) {
        // create hashMap grouping by job , key: job, value = list of employees
        Map<String, List<Employee>> mapGroupByJob = employeeList.stream().collect(
                groupingBy(Employee::getJob));
        Map<String, BigDecimal> resultMap = new HashMap<>();
        // create new hashMap based on previous one: key: job , value: sum of salary of list of employees grouped by job
        mapGroupByJob.forEach((key, value) -> resultMap.put(key, value.stream()
                .map(Employee::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add)));

        resultMap.entrySet().forEach(System.out::println);
        System.out.println();
    }

    private static Employee createEmployeeFromJSONObject(JSONObject employeeJSON) {

        Employee employee = new Employee();

        //Get employee id
        Long id = (Long) employeeJSON.get("id");
        employee.setId(id);

        //Get employee name
        String name = (String) employeeJSON.get("name");
        employee.setName(name);

        //Get employee surname
        String surname = (String) employeeJSON.get("surname");
        employee.setSurname(surname);

        //Get employee job
        String job = (String) employeeJSON.get("job");
        employee.setJob(job);

        //Get employee salary
        String salary = (String) employeeJSON.get("salary");
        BigDecimal money = new BigDecimal(salary.replaceAll(",", "."));
        employee.setSalary(money);

        return employee;

    }

    private static Employee createEmployeeFromCSVDates(String[] metadata) {

        List<String> stringList = Arrays.asList(metadata);
        // only string data excludes id and skip get(0)
        List<String> subStringList = stringList.subList(2, 6);
        List<String> sublist = new ArrayList<>();
        for (String word : subStringList) {
            // fix string values
            String word2 = word.replaceAll("  ", "").replaceAll(";", "").replaceAll("\"", "");
            sublist.add(word2);
        }
        // fix id
        Long id = Long.valueOf(metadata[1].replaceAll(";", "").replaceAll(" ", ""));
        String name = sublist.get(0);
        String surname = sublist.get(1);
        String job = sublist.get(2);
        String salary = sublist.get(3);
        BigDecimal money = new BigDecimal(salary.replaceAll(",", "."));

        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setSurname(surname);
        employee.setJob(job);
        employee.setSalary(money);

        // create and return employee of this metadata
        return employee;
    }
}


