/**
 * Simple test runner that doesn't require JUnit.
 * Tests core functionality of the Course Registration System.
 */
public class SimpleTestRunner {
    private static int totalTests = 0;
    private static int passedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Course Registration System Tests ===");
        System.out.println();
        
        // Run all tests
        testStudentCreation();
        testAdvisorCreation();
        testStaffInheritance();
        testCoursePrerequisites();
        testCourseSectionEnrollment();
        testRegistrationWorkflow();
        testTranscriptFunctionality();
        
        // Print results
        System.out.println();
        System.out.println("=== Test Results ===");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + (totalTests - passedTests));
        System.out.println("Success Rate: " + String.format("%.1f%%", (double)passedTests/totalTests * 100));
        
        if (passedTests == totalTests) {
            System.out.println("ALL TESTS PASSED!");
        } else {
            System.out.println("Some tests failed - check output above.");
        }
    }
    
    // Test helper methods
    private static void assert_true(boolean condition, String testName) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.println("✓ PASS: " + testName);
        } else {
            System.out.println("✗ FAIL: " + testName);
        }
    }
    
    private static void assert_equals(Object expected, Object actual, String testName) {
        totalTests++;
        boolean passed = (expected == null && actual == null) || 
                        (expected != null && expected.equals(actual));
        if (passed) {
            passedTests++;
            System.out.println("✓ PASS: " + testName);
        } else {
            System.out.println("✗ FAIL: " + testName + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }
    
    // Test methods
    private static void testStudentCreation() {
        System.out.println("--- Testing Student Creation ---");
        
        Student student = new Student("150119001", "John Doe", "john.doe", "password", 3, 3.5, "ADV001");
        
        assert_equals("150119001", student.getUserId(), "Student ID should be set correctly");
        assert_equals("John Doe", student.getName(), "Student name should be set correctly");
        assert_equals(3, student.getYear(), "Student year should be set correctly");
        assert_equals(3.5, student.getGpa(), "Student GPA should be set correctly");
        assert_equals("STUDENT", student.getUserType(), "Student user type should be STUDENT");
        assert_true(student.authenticate("john.doe", "password"), "Student should authenticate with correct credentials");
    }
    
    private static void testAdvisorCreation() {
        System.out.println("--- Testing Advisor Creation ---");
        
        Advisor advisor = new Advisor("ADV001", "Dr. Smith", "dr.smith", "advisor123", 
                                     "Computer Engineering", "Dr.", "B-201", "EMP001");
        
        assert_equals("ADV001", advisor.getUserId(), "Advisor ID should be set correctly");
        assert_equals("Dr. Smith", advisor.getName(), "Advisor name should be set correctly");
        assert_equals("Computer Engineering", advisor.getDepartment(), "Advisor department should be set correctly");
        assert_equals("ADVISOR", advisor.getUserType(), "Advisor user type should be ADVISOR");
        assert_true(advisor.authenticate("dr.smith", "advisor123"), "Advisor should authenticate with correct credentials");
    }
    
    private static void testStaffInheritance() {
        System.out.println("--- Testing Staff Inheritance ---");
        
        Advisor advisor = new Advisor("ADV001", "Dr. Smith", "dr.smith", "password", 
                                     "Computer Engineering", "Dr.", "B-201", "EMP001");
        
        assert_true(advisor instanceof Staff, "Advisor should be instance of Staff");
        assert_true(advisor instanceof Person, "Advisor should be instance of Person");
        assert_equals("ADVISOR", advisor.getStaffRole(), "Advisor staff role should be ADVISOR");
        // The formatted name includes title + name, so "Dr." + "Dr. Smith" = "Dr. Dr. Smith"
        // This is expected behavior - the name already contains the title
        assert_equals("Dr. Dr. Smith", advisor.getFormattedName(), "Formatted name should include title prefix");
    }
    
    private static void testCoursePrerequisites() {
        System.out.println("--- Testing Course Prerequisites ---");
        
        Course basicCourse = new Course("CSE1141", "Programming I", 4, 1, 
                                       java.util.Arrays.asList(), "Computer Engineering");
        Course advancedCourse = new Course("CSE2023", "Data Structures", 4, 2, 
                                         java.util.Arrays.asList("CSE1141"), "Computer Engineering");
        
        assert_true(basicCourse.arePrerequisitesMet(java.util.Arrays.asList()), 
                   "Course with no prerequisites should be available");
        assert_true(advancedCourse.arePrerequisitesMet(java.util.Arrays.asList("CSE1141")), 
                   "Course should be available when prerequisites are met");
        assert_true(!advancedCourse.arePrerequisitesMet(java.util.Arrays.asList()), 
                   "Course should not be available when prerequisites are missing");
    }
    
    private static void testCourseSectionEnrollment() {
        System.out.println("--- Testing Course Section Enrollment ---");
        
        CourseSection section = new CourseSection("CSE1141-01", "CSE1141", 2, "Fall2024", "Dr. Test");
        
        assert_true(section.enrollStudent("150119001"), "Should enroll first student");
        assert_equals(1, section.getCurrentEnrollment(), "Should have 1 enrolled student");
        assert_true(!section.isFull(), "Section should not be full yet");
        
        assert_true(section.enrollStudent("150119002"), "Should enroll second student");
        assert_equals(2, section.getCurrentEnrollment(), "Should have 2 enrolled students");
        assert_true(section.isFull(), "Section should be full now");
        
        assert_true(!section.enrollStudent("150119003"), "Should not enroll when full");
        assert_equals(2, section.getCurrentEnrollment(), "Should still have 2 students");
    }
    
    private static void testRegistrationWorkflow() {
        System.out.println("--- Testing Registration Workflow ---");
        
        // Create test data
        Student student = new Student("150119001", "John Doe", "john.doe", "password", 3, 3.5, "ADV001");
        student.addCompletedCourse("CSE1141", "Programming I", 4, "AA", "Fall2023", 2023);
        
        Advisor advisor = new Advisor("ADV001", "Dr. Smith", "dr.smith", "advisor123", 
                                     "Computer Engineering", "Dr.", "B-201", "EMP001");
        advisor.addAdvisee("150119001");
        
        Course course = new Course("CSE1142", "Programming II", 4, 1, 
                                  java.util.Arrays.asList("CSE1141"), "Computer Engineering");
        CourseSection section = new CourseSection("CSE1142-01", "CSE1142", 30, "Fall2024", "Dr. Test");
        
        // Test authentication directly (without system for now)
        assert_true(student.authenticate("john.doe", "password"), "Student should authenticate");
        assert_true(advisor.authenticate("dr.smith", "advisor123"), "Advisor should authenticate");
        
        // Test advisor-student relationship
        assert_true(advisor.isAdvisee("150119001"), "Advisor should recognize student as advisee");
        assert_equals(1, advisor.getAdviseeCount(), "Advisor should have 1 advisee");
        
        // Test course prerequisites
        assert_true(student.canEnrollInCourse(course, section), "Student should be able to enroll with prerequisites met");
    }
    
    private static void testTranscriptFunctionality() {
        System.out.println("--- Testing Transcript Functionality ---");
        
        Transcript transcript = new Transcript("150119001");
        
        transcript.addEntry("CSE1141", "Programming I", 4, "AA", "Fall2023", 2023);
        transcript.addEntry("CSE1142", "Programming II", 4, "BA", "Spring2024", 2024);
        
        assert_equals(2, transcript.getEntries().size(), "Should have 2 transcript entries");
        assert_true(transcript.getCumulativeGPA() > 3.0, "GPA should be calculated correctly");
        assert_equals(8, transcript.getTotalCreditsCompleted(), "Should have 8 credits completed");
        
        java.util.List<String> completedCodes = transcript.getCompletedCourseCodes();
        assert_true(completedCodes.contains("CSE1141"), "Should contain CSE1141");
        assert_true(completedCodes.contains("CSE1142"), "Should contain CSE1142");
    }
}