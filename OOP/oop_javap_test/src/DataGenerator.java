import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Utility class to generate sample data for the Course Registration System.
 * Creates students, advisors, courses, and course sections with realistic data.
 */
public class DataGenerator {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Random random = new Random();
    
    public static void main(String[] args) {
        System.out.println("Generating sample data for Course Registration System...");
        
        try {
            generateStudents();
            generateAdvisors();
            generateCourses();
            generateCourseSections();
            generateSampleRegistrationRequests();
            
            System.out.println("Sample data generation completed successfully!");
            System.out.println("Files created:");
            System.out.println("- students.json (10 students)");
            System.out.println("- advisors.json (2 advisors)");
            System.out.println("- courses.json (15 courses)");
            System.out.println("- course_sections.json (13 sections)");
            System.out.println("- registration_requests.json (sample requests)");
            
        } catch (IOException e) {
            System.err.println("Error generating sample data: " + e.getMessage());
        }
    }
    
    /**
     * Generates 10 third-year students with varied academic backgrounds
     */
    private static void generateStudents() throws IOException {
        List<Student> students = new ArrayList<>();
        
        String[] firstNames = {"John", "Jane", "Michael", "Sarah", "David", "Emily", "James", "Lisa", "Robert", "Maria"};
        String[] lastNames = {"Smith", "Johnson", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson", "Thomas"};
        
        for (int i = 0; i < 10; i++) {
            String firstName = firstNames[i];
            String lastName = lastNames[i];
            String userId = String.format("15011900%d", i + 1);
            String username = (firstName + "." + lastName).toLowerCase();
            String password = "student" + (i + 1);
            
            double gpa = 2.0 + (random.nextDouble() * 2.0); // GPA between 2.0 and 4.0
            String advisorId = i < 5 ? "ADV001" : "ADV002"; // Distribute students between advisors
            
            Student student = new Student(userId, firstName + " " + lastName, username, password, 3, gpa, advisorId);
            
            // Add some completed courses (prerequisites)
            addCompletedCourses(student);
            
            students.add(student);
        }
        
        // Save to JSON file
        FileWriter writer = new FileWriter("students.json");
        gson.toJson(students, writer);
        writer.close();
    }
    
    /**
     * Generates 2 advisors for the department
     */
    private static void generateAdvisors() throws IOException {
        List<Advisor> advisors = new ArrayList<>();
        
        // Advisor 1
        Advisor advisor1 = new Advisor("ADV001", "Dr. Alice Johnson", "dr.johnson", "advisor123", 
                                     "Computer Engineering", "Dr.", "B-201", "EMP001");
        advisor1.setAdviseeList(Arrays.asList("150119001", "150119002", "150119003", "150119004", "150119005"));
        advisor1.setEmail("alice.johnson@university.edu");
        advisor1.setPhoneNumber("555-0101");
        advisor1.setAdvisorType("Academic");
        
        // Advisor 2
        Advisor advisor2 = new Advisor("ADV002", "Prof. Bob Smith", "prof.smith", "advisor456", 
                                     "Computer Engineering", "Prof.", "B-203", "EMP002");
        advisor2.setAdviseeList(Arrays.asList("150119006", "150119007", "150119008", "150119009", "150119010"));
        advisor2.setEmail("bob.smith@university.edu");
        advisor2.setPhoneNumber("555-0102");
        advisor2.setAdvisorType("Academic");
        
        advisors.add(advisor1);
        advisors.add(advisor2);
        
        // Save to JSON file
        FileWriter writer = new FileWriter("advisors.json");
        gson.toJson(advisors, writer);
        writer.close();
    }
    
    /**
     * Generates 10 courses with prerequisites and varied difficulty levels
     */
    private static void generateCourses() throws IOException {
        List<Course> courses = new ArrayList<>();
        
        // 1st year courses (no prerequisites)
        courses.add(new Course("CSE1141", "Computer Programming I", 4, 1, 
                              new ArrayList<>(), "Computer Engineering"));
        
        courses.add(new Course("MATH1051", "Calculus I", 4, 1, 
                              new ArrayList<>(), "Mathematics"));
        
        // 1st year with prerequisites
        courses.add(new Course("CSE1142", "Computer Programming II", 4, 1, 
                              Arrays.asList("CSE1141"), "Computer Engineering"));
        
        courses.add(new Course("MATH1052", "Calculus II", 4, 1, 
                              Arrays.asList("MATH1051"), "Mathematics"));
        
        // 2nd year courses with prerequisites
        courses.add(new Course("CSE2023", "Data Structures and Algorithms", 4, 2, 
                              Arrays.asList("CSE1142"), "Computer Engineering"));
        
        courses.add(new Course("CSE2033", "System Programming", 3, 2, 
                              Arrays.asList("CSE1142"), "Computer Engineering"));
        
        courses.add(new Course("MATH2059", "Discrete Mathematics", 3, 2, 
                              Arrays.asList("MATH1051"), "Mathematics"));
        
        // 3rd year courses (current level) with multiple prerequisites
        courses.add(new Course("CSE3063", "Object-Oriented Software Design", 4, 3, 
                              Arrays.asList("CSE1142", "CSE2023"), "Computer Engineering"));
        
        courses.add(new Course("CSE3033", "Operating Systems", 4, 3, 
                              Arrays.asList("CSE2033", "CSE2023"), "Computer Engineering"));
        
        courses.add(new Course("CSE3055", "Database Systems", 4, 3, 
                              Arrays.asList("CSE2023"), "Computer Engineering"));
        
        courses.add(new Course("CSE3043", "Software Engineering", 3, 3, 
                              Arrays.asList("CSE2023", "CSE3063"), "Computer Engineering"));
        
        courses.add(new Course("CSE3011", "Computer Networks", 3, 3, 
                              Arrays.asList("CSE2033"), "Computer Engineering"));
        
        // 4th year courses (advanced) with complex prerequisites
        courses.add(new Course("CSE4074", "Machine Learning", 3, 4, 
                              Arrays.asList("CSE3055", "CSE2023", "MATH2059"), "Computer Engineering"));
        
        courses.add(new Course("CSE4088", "Advanced Computer Networks", 4, 4, 
                              Arrays.asList("CSE3033", "CSE3011"), "Computer Engineering"));
        
        courses.add(new Course("CSE4099", "Senior Project", 4, 4, 
                              Arrays.asList("CSE3063", "CSE3043"), "Computer Engineering"));
        
        // Save to JSON file
        FileWriter writer = new FileWriter("courses.json");
        gson.toJson(courses, writer);
        writer.close();
    }
    
    /**
     * Generates course sections for the available courses
     */
    private static void generateCourseSections() throws IOException {
        List<CourseSection> sections = new ArrayList<>();
        
        String[] instructors = {"Dr. Wilson", "Prof. Brown", "Dr. Garcia", "Prof. Lee", "Dr. Martinez", "Dr. Thompson"};
        String semester = "Fall2024";
        
        // Available courses for current semester (3rd year students can take 3rd and some 4th year courses)
        String[] availableCourses = {
            // 3rd year courses (main focus)
            "CSE3063", "CSE3033", "CSE3055", "CSE3043", "CSE3011",
            // 4th year courses (for advanced students)
            "CSE4074", "CSE4088",
            // Some 2nd year courses (for students who need to catch up)
            "CSE2023", "CSE2033", "MATH2059",
            // 1st year courses (remedial or elective)
            "CSE1141", "CSE1142"
        };
        
        for (int i = 0; i < availableCourses.length; i++) {
            String courseCode = availableCourses[i];
            String sectionId = courseCode + "-01";
            String instructor = instructors[i % instructors.length];
            
            int capacity;
            // Set different capacities based on course level and popularity
            if (courseCode.startsWith("CSE1")) {
                capacity = 45; // Large capacity for foundational courses
            } else if (courseCode.startsWith("CSE2")) {
                capacity = 35; // Medium-large capacity for 2nd year courses
            } else if (courseCode.startsWith("CSE3")) {
                capacity = 30; // Medium capacity for 3rd year courses
            } else {
                capacity = 20; // Smaller capacity for advanced courses
            }
            
            CourseSection section = new CourseSection(sectionId, courseCode, capacity, semester, instructor);
            sections.add(section);
        }
        
        // Add multiple sections for popular courses
        sections.add(new CourseSection("CSE3063-02", "CSE3063", 30, semester, "Dr. Johnson"));
        sections.add(new CourseSection("CSE2023-02", "CSE2023", 35, semester, "Prof. Davis"));
        
        // Save to JSON file
        FileWriter writer = new FileWriter("course_sections.json");
        gson.toJson(sections, writer);
        writer.close();
    }
    
    /**
     * Adds completed courses to a student's record to satisfy prerequisites
     */
    private static void addCompletedCourses(Student student) {
        // All 3rd year students should have completed 1st and 2nd year courses
        
        // 1st year courses
        student.addCompletedCourse("CSE1141", "Computer Programming I", 4, getRandomGrade(), "Fall2022", 2022);
        student.addCompletedCourse("CSE1142", "Computer Programming II", 4, getRandomGrade(), "Spring2023", 2023);
        
        // Basic math and science courses
        student.addCompletedCourse("MATH1051", "Calculus I", 4, getRandomGrade(), "Fall2022", 2022);
        student.addCompletedCourse("PHYS1101", "Physics I", 3, getRandomGrade(), "Spring2023", 2023);
        student.addCompletedCourse("MATH1052", "Calculus II", 4, getRandomGrade(), "Spring2023", 2023);
        
        // 2nd year courses
        student.addCompletedCourse("CSE2023", "Data Structures and Algorithms", 4, getRandomGrade(), "Fall2023", 2023);
        student.addCompletedCourse("CSE2033", "System Programming", 3, getRandomGrade(), "Spring2024", 2024);
        student.addCompletedCourse("MATH2059", "Discrete Mathematics", 3, getRandomGrade(), "Fall2023", 2023);
        
        // Additional courses based on performance
        if (student.getGpa() > 3.0) {
            // High-performing students might have taken extra courses
            if (random.nextBoolean()) {
                student.addCompletedCourse("CSE2043", "Software Engineering Principles", 3, getRandomGrade(), "Spring2024", 2024);
            }
            if (random.nextBoolean()) {
                student.addCompletedCourse("ENG1001", "Technical Writing", 3, getRandomGrade(), "Fall2023", 2023);
            }
        }
        
        // General education requirements
        student.addCompletedCourse("HIST1101", "World History", 3, getRandomGrade(), "Fall2022", 2022);
        student.addCompletedCourse("LIT1201", "Literature", 3, getRandomGrade(), "Spring2023", 2023);
        
        // Set transcript student ID if not already set
        if (student.getTranscript() != null) {
            student.getTranscript().setStudentId(student.getUserId());
        }
    }
    
    /**
     * Generates a random grade based on realistic distribution
     */
    private static String getRandomGrade() {
        String[] grades = {"AA", "BA", "BB", "CB", "CC", "DC", "DD"};
        double[] probabilities = {0.15, 0.25, 0.25, 0.20, 0.10, 0.04, 0.01}; // Realistic grade distribution
        
        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;
        
        for (int i = 0; i < grades.length; i++) {
            cumulativeProbability += probabilities[i];
            if (randomValue <= cumulativeProbability) {
                return grades[i];
            }
        }
        
        return "BB"; // Default fallback
    }
    
    /**
     * Generates sample registration requests for demonstration
     */
    private static void generateSampleRegistrationRequests() throws IOException {
        List<RegistrationRequest> requests = new ArrayList<>();
        
        // Create a few sample registration requests
        String[] reasons = {
            "Required for my major curriculum",
            "Interested in this subject area for future career",
            "Prerequisite for advanced courses I plan to take",
            "Fits well with my current schedule",
            "Recommended by my advisor for academic growth"
        };
        
        // Request 1: Student wants to take CSE3063
        RegistrationRequest req1 = new RegistrationRequest(
            "REQ001", "150119001", "CSE3063-01", "CSE3063", 
            "Object-Oriented Software Design", reasons[0]
        );
        requests.add(req1);
        
        // Request 2: Student wants to take CSE3055
        RegistrationRequest req2 = new RegistrationRequest(
            "REQ002", "150119003", "CSE3055-01", "CSE3055", 
            "Database Systems", reasons[1]
        );
        requests.add(req2);
        
        // Request 3: Student wants to take CSE4074 (advanced course)
        RegistrationRequest req3 = new RegistrationRequest(
            "REQ003", "150119005", "CSE4074-01", "CSE4074", 
            "Machine Learning", reasons[2]
        );
        requests.add(req3);
        
        // Save to JSON file
        FileWriter writer = new FileWriter("registration_requests.json");
        gson.toJson(requests, writer);
        writer.close();
    }
}