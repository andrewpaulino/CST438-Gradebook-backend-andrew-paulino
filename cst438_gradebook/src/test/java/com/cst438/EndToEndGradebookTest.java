package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;


@SpringBootTest
public class EndToEndGradebookTest {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedrivers/chromedriver.exe";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_COURSE_TITLE = "Test Course";
	public static final String TEST_STUDENT_NAME = "Test";

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addAssignmentTest() throws Exception {

//		Database setup:  create course		
		Course c = new Course();
		c.setCourse_id(99999);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("Fall");
		c.setYear(2021);
		c.setTitle(TEST_COURSE_TITLE);
		courseRepository.save(c);


		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		

		try {
	        // Click on "Add Assignment" button
	        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/a[2]")).click();
	        
	        // Enter assignment name
	        driver.findElement(By.xpath("//*[@id=\"mui-12\"]")).sendKeys("Test Assignment - FOR E2E");
	        
	        // Enter course ID
	        driver.findElement(By.xpath("//*[@id=\"mui-13\"]")).sendKeys("99999");
	        
	        // Enter due date
	        driver.findElement(By.xpath("//*[@id=\"date\"]")).sendKeys("04-01-2020");
	        
	        // Click on "Create Assignment" button
	        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/form/div[1]/button")).click();
	        
	        // Wait 8 seconds for redirection
			Thread.sleep(SLEEP_DURATION * 8);
			
			// Check if title exist after redirection, which indicates assignment created successfully
			WebElement element = driver.findElement(By.xpath("//*[@id='root']/div/div/h4"));
			assertEquals("Assignment(s) ready to grade:", element.getText());


	        // Close the browser
	        driver.close();
		} catch (Exception ex) {
			throw ex;
		} finally {
			List<Assignment> assignments = assignmentRepository.findNeedGradingByEmail(TEST_INSTRUCTOR_EMAIL);
			
			
			System.out.println("Deleting assignment");

			for (Assignment a: assignments) {
				System.out.println("Deleting assignment" + a.getName() + " " + "Test Assignment - FOR E2E" == a.getName());
				if ("Test Assignment - FOR E2E".equals(a.getName())) {
					assignmentRepository.deleteById(a.getId());
				}
				
				
			}
			System.out.println("Deleting course");
			courseRepository.delete(c);
			
			driver.quit();
		}

	}
}
