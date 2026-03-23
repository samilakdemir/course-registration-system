/**
 * Demonstration program showing how the prerequisite system works.
 * This shows various scenarios for course registration and prerequisites.
 */
public class PrerequisiteDemo {
    
    public static void main(String[] args) {
        System.out.println("=== COURSE REGISTRATION PREREQUISITE DEMO ===\n");
        
        // Create a student
        Student student = new Student("150119999", "Demo Student", "demo.student", "password", 
                                     3, 3.0, "ADV001");
        
        // Create courses with prerequisite chain
        Course prog1 = new Course("CSE1141", "Programming I", 4, 1, 
                                 java.util.Arrays.asList(), "Computer Engineering");
        
        Course prog2 = new Course("CSE1142", "Programming II", 4, 1, 
                                 java.util.Arrays.asList("CSE1141"), "Computer Engineering");
        
        Course dataStruct = new Course("CSE2023", "Data Structures", 4, 2, 
                                      java.util.Arrays.asList("CSE1142"), "Computer Engineering");
        
        Course oop = new Course("CSE3063", "Object-Oriented Design", 4, 3, 
                               java.util.Arrays.asList("CSE1142", "CSE2023"), "Computer Engineering");
        
        // Create sections
        CourseSection prog1Section = new CourseSection("CSE1141-01", "CSE1141", 30, "Fall2024", "Dr. A");
        CourseSection prog2Section = new CourseSection("CSE1142-01", "CSE1142", 30, "Fall2024", "Dr. B");
        CourseSection dataSection = new CourseSection("CSE2023-01", "CSE2023", 30, "Fall2024", "Dr. C");
        CourseSection oopSection = new CourseSection("CSE3063-01", "CSE3063", 30, "Fall2024", "Dr. D");
        
        System.out.println("SCENARIO 1: New student with no completed courses");
        System.out.println("=".repeat(50));
        
        testEnrollment(student, prog1, prog1Section, "Programming I (no prerequisites)");
        testEnrollment(student, prog2, prog2Section, "Programming II (requires Programming I)");
        testEnrollment(student, dataStruct, dataSection, "Data Structures (requires Programming II)");
        testEnrollment(student, oop, oopSection, "OOP Design (requires Programming II + Data Structures)");
        
        System.out.println("\nSCENARIO 2: After completing Programming I");
        System.out.println("=".repeat(50));
        
        student.addCompletedCourse("CSE1141", "Programming I", 4, "AA", "Fall2023", 2023);
        System.out.println("✓ Completed: Programming I (Grade: AA)");
        
        testEnrollment(student, prog1, prog1Section, "Programming I (already completed)");
        testEnrollment(student, prog2, prog2Section, "Programming II (prerequisite now met)");
        testEnrollment(student, dataStruct, dataSection, "Data Structures (still missing Programming II)");
        
        System.out.println("\nSCENARIO 3: After completing Programming II");
        System.out.println("=".repeat(50));
        
        student.addCompletedCourse("CSE1142", "Programming II", 4, "BA", "Spring2024", 2024);
        System.out.println("✓ Completed: Programming II (Grade: BA)");
        
        testEnrollment(student, prog2, prog2Section, "Programming II (already completed)");
        testEnrollment(student, dataStruct, dataSection, "Data Structures (prerequisite now met)");
        testEnrollment(student, oop, oopSection, "OOP Design (still missing Data Structures)");
        
        System.out.println("\nSCENARIO 4: After completing Data Structures");
        System.out.println("=".repeat(50));
        
        student.addCompletedCourse("CSE2023", "Data Structures", 4, "BB", "Fall2024", 2024);
        System.out.println("✓ Completed: Data Structures (Grade: BB)");
        
        testEnrollment(student, oop, oopSection, "OOP Design (all prerequisites now met)");
        
        System.out.println("\nSCENARIO 5: Failed course and retake");
        System.out.println("=".repeat(50));
        
        // Create a new student for this scenario
        Student retakeStudent = new Student("150119998", "Retake Student", "retake.student", "password", 
                                           3, 2.5, "ADV001");
        
        // Student fails Programming I
        retakeStudent.addCompletedCourse("CSE1141", "Programming I", 4, "FF", "Fall2023", 2023);
        System.out.println("✗ Failed: Programming I (Grade: FF)");
        
        testEnrollment(retakeStudent, prog2, prog2Section, "Programming II (prerequisite failed)");
        
        // Student retakes and passes Programming I
        retakeStudent.addCompletedCourse("CSE1141", "Programming I", 4, "CB", "Spring2024", 2024);
        System.out.println("✓ Retook: Programming I (Grade: CB)");
        
        testEnrollment(retakeStudent, prog2, prog2Section, "Programming II (prerequisite now passed)");
        
        System.out.println("\nSCENARIO 6: Course load limit");
        System.out.println("=".repeat(50));
        
        // Enroll student in maximum courses
        for (int i = 0; i < Student.MAX_COURSES; i++) {
            student.enrollInCourse("FILLER" + i + "-01");
        }
        System.out.println("Student enrolled in " + Student.MAX_COURSES + " courses (maximum allowed)");
        
        testEnrollment(student, prog1, prog1Section, "Any course (at course limit)");
        
        System.out.println("\n=== DEMO COMPLETE ===");
    }
    
    /**
     * Tests enrollment and shows the result
     */
    private static void testEnrollment(Student student, Course course, CourseSection section, String description) {
        boolean canEnroll = student.canEnrollInCourse(course, section);
        System.out.printf("%-50s: ", description);
        
        if (canEnroll) {
            System.out.println("✓ CAN REGISTER");
        } else {
            System.out.println("✗ CANNOT REGISTER");
            java.util.List<String> reasons = student.getEnrollmentBlockingReasons(course, section);
            for (String reason : reasons) {
                System.out.println("  → " + reason);
            }
        }
    }
}