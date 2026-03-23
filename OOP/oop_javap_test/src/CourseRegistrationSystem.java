import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Main controller class for the Course Registration System.
 * Handles authentication, business logic, and data persistence.
 */
public class CourseRegistrationSystem {
    private Map<String, Student> students;
    private Map<String, Advisor> advisors;
    private Map<String, Course> courses;
    private Map<String, CourseSection> courseSections;
    private Map<String, RegistrationRequest> registrationRequests;
    private Person currentUser;
    private Gson gson;
    
    // File paths for JSON data
    private static final String STUDENTS_FILE = "students.json";
    private static final String ADVISORS_FILE = "advisors.json";
    private static final String COURSES_FILE = "courses.json";
    private static final String SECTIONS_FILE = "course_sections.json";
    private static final String REQUESTS_FILE = "registration_requests.json";
    
    /**
     * Constructor initializes the system and loads data
     */
    public CourseRegistrationSystem() {
        this.students = new HashMap<>();
        this.advisors = new HashMap<>();
        this.courses = new HashMap<>();
        this.courseSections = new HashMap<>();
        this.registrationRequests = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.currentUser = null;
        
        loadAllData();
    }
    
    /**
     * Authenticates a user with username and password
     * @param username User's username
     * @param password User's password
     * @return true if authentication successful, false otherwise
     */
    public boolean authenticateUser(String username, String password) {
        // Check students
        for (Student student : students.values()) {
            if (student.authenticate(username, password)) {
                currentUser = student;
                return true;
            }
        }
        
        // Check advisors
        for (Advisor advisor : advisors.values()) {
            if (advisor.authenticate(username, password)) {
                currentUser = advisor;
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Gets the currently logged in user
     * @return Current user or null if not logged in
     */
    public Person getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Creates a registration request for advisor approval
     * @param studentId ID of the student
     * @param sectionId ID of the course section
     * @param studentReason Reason provided by student for taking the course
     * @return RegistrationResult with request creation status
     */
    public RegistrationResult createRegistrationRequest(String studentId, String sectionId, String studentReason) {
        Student student = students.get(studentId);
        if (student == null) {
            return new RegistrationResult(false, "Student not found.");
        }
        
        CourseSection section = courseSections.get(sectionId);
        if (section == null) {
            return new RegistrationResult(false, "Course section not found.");
        }
        
        Course course = courses.get(section.getCourseCode());
        if (course == null) {
            return new RegistrationResult(false, "Course not found.");
        }
        
        // Check basic eligibility (excluding advisor approval)
        if (!canStudentRequestCourse(student, course, section)) {
            List<String> reasons = getEnrollmentBlockingReasons(student, course, section);
            return new RegistrationResult(false, "Cannot request registration: " + String.join(", ", reasons));
        }
        
        // Check if student already has a pending request for this course
        for (RegistrationRequest existingRequest : registrationRequests.values()) {
            if (existingRequest.getStudentId().equals(studentId) && 
                existingRequest.getCourseCode().equals(course.getCourseCode()) && 
                existingRequest.isPending()) {
                return new RegistrationResult(false, "You already have a pending request for this course.");
            }
        }
        
        // Create new registration request
        String requestId = generateRequestId();
        RegistrationRequest request = new RegistrationRequest(
            requestId, studentId, sectionId, course.getCourseCode(), 
            course.getCourseName(), studentReason
        );
        
        registrationRequests.put(requestId, request);
        saveAllData();
        
        return new RegistrationResult(true, 
            String.format("Registration request submitted for %s - %s. Your advisor will review and approve/reject the request.", 
                         course.getCourseCode(), course.getCourseName()));
    }
    
    /**
     * Checks if student can request a course (similar to canEnroll but without advisor approval check)
     */
    private boolean canStudentRequestCourse(Student student, Course course, CourseSection section) {
        return student.canEnrollInCourse(course, section);
    }
    
    /**
     * Approves a registration request and enrolls the student
     * @param requestId ID of the registration request
     * @param advisorId ID of the approving advisor
     * @param comments Optional comments from advisor
     * @return RegistrationResult with approval status
     */
    public RegistrationResult approveRegistrationRequest(String requestId, String advisorId, String comments) {
        RegistrationRequest request = registrationRequests.get(requestId);
        if (request == null) {
            return new RegistrationResult(false, "Registration request not found.");
        }
        
        if (!request.isPending()) {
            return new RegistrationResult(false, "Request has already been processed.");
        }
        
        // Verify advisor is authorized to approve for this student
        Student student = students.get(request.getStudentId());
        if (student == null || !student.getAdvisorId().equals(advisorId)) {
            return new RegistrationResult(false, "You are not authorized to approve requests for this student.");
        }
        
        // Double-check that student can still enroll (conditions might have changed)
        CourseSection section = courseSections.get(request.getSectionId());
        Course course = courses.get(request.getCourseCode());
        
        if (section == null || course == null) {
            return new RegistrationResult(false, "Course or section no longer available.");
        }
        
        if (!student.canEnrollInCourse(course, section)) {
            List<String> reasons = getEnrollmentBlockingReasons(student, course, section);
            request.reject(advisorId, "Student no longer eligible: " + String.join(", ", reasons));
            saveAllData();
            return new RegistrationResult(false, "Student is no longer eligible for this course: " + String.join(", ", reasons));
        }
        
        // Approve the request
        request.approve(advisorId, comments);
        
        // Enroll the student
        if (section.enrollStudent(request.getStudentId()) && student.enrollInCourse(request.getSectionId())) {
            request.markAsEnrolled();
            saveAllData();
            return new RegistrationResult(true, 
                String.format("Request approved and student enrolled in %s - %s", 
                             course.getCourseCode(), course.getCourseName()));
        } else {
            return new RegistrationResult(false, "Approval successful but enrollment failed due to system error.");
        }
    }
    
    /**
     * Rejects a registration request
     * @param requestId ID of the registration request
     * @param advisorId ID of the rejecting advisor
     * @param reason Reason for rejection
     * @return RegistrationResult with rejection status
     */
    public RegistrationResult rejectRegistrationRequest(String requestId, String advisorId, String reason) {
        RegistrationRequest request = registrationRequests.get(requestId);
        if (request == null) {
            return new RegistrationResult(false, "Registration request not found.");
        }
        
        if (!request.isPending()) {
            return new RegistrationResult(false, "Request has already been processed.");
        }
        
        // Verify advisor is authorized to reject for this student
        Student student = students.get(request.getStudentId());
        if (student == null || !student.getAdvisorId().equals(advisorId)) {
            return new RegistrationResult(false, "You are not authorized to reject requests for this student.");
        }
        
        request.reject(advisorId, reason);
        saveAllData();
        
        return new RegistrationResult(true, "Registration request rejected.");
    }
    
    /**
     * Gets pending registration requests for a specific advisor
     * @param advisorId ID of the advisor
     * @return List of pending registration requests
     */
    public List<RegistrationRequest> getPendingRequestsForAdvisor(String advisorId) {
        List<RegistrationRequest> pendingRequests = new ArrayList<>();
        
        for (RegistrationRequest request : registrationRequests.values()) {
            if (request.isPending()) {
                Student student = students.get(request.getStudentId());
                if (student != null && student.getAdvisorId().equals(advisorId)) {
                    pendingRequests.add(request);
                }
            }
        }
        
        return pendingRequests;
    }
    
    /**
     * Gets registration requests for a specific student
     * @param studentId ID of the student
     * @return List of registration requests
     */
    public List<RegistrationRequest> getRequestsForStudent(String studentId) {
        List<RegistrationRequest> studentRequests = new ArrayList<>();
        
        for (RegistrationRequest request : registrationRequests.values()) {
            if (request.getStudentId().equals(studentId)) {
                studentRequests.add(request);
            }
        }
        
        return studentRequests;
    }
    
    /**
     * Generates a unique request ID
     * @return Unique request ID
     */
    private String generateRequestId() {
        return "REQ" + System.currentTimeMillis() + "_" + (registrationRequests.size() + 1);
    }
    
    /**
     * Drops a student from a course section
     * @param studentId ID of the student
     * @param sectionId ID of the course section
     * @return RegistrationResult with success status and message
     */
    public RegistrationResult dropStudentFromCourse(String studentId, String sectionId) {
        Student student = students.get(studentId);
        if (student == null) {
            return new RegistrationResult(false, "Student not found.");
        }
        
        CourseSection section = courseSections.get(sectionId);
        if (section == null) {
            return new RegistrationResult(false, "Course section not found.");
        }
        
        if (section.dropStudent(studentId) && student.dropCourse(sectionId)) {
            saveAllData();
            return new RegistrationResult(true, "Successfully dropped from course.");
        } else {
            return new RegistrationResult(false, "Drop failed - student not enrolled in this section.");
        }
    }
    
    /**
     * Gets reasons why a student cannot enroll in a course
     * @param student The student
     * @param course The course
     * @param section The course section
     * @return List of blocking reasons
     */
    private List<String> getEnrollmentBlockingReasons(Student student, Course course, CourseSection section) {
        return student.getEnrollmentBlockingReasons(course, section);
    }
    
    /**
     * Gets available course sections for a student
     * @param studentId ID of the student
     * @return List of available course sections
     */
    public List<CourseSection> getAvailableSections(String studentId) {
        Student student = students.get(studentId);
        if (student == null) {
            return new ArrayList<>();
        }
        
        List<CourseSection> availableSections = new ArrayList<>();
        
        for (CourseSection section : courseSections.values()) {
            Course course = courses.get(section.getCourseCode());
            if (course != null && student.canEnrollInCourse(course, section)) {
                availableSections.add(section);
            }
        }
        
        return availableSections;
    }
    
    /**
     * Gets a student's enrolled sections
     * @param studentId ID of the student
     * @return List of enrolled course sections
     */
    public List<CourseSection> getStudentEnrolledSections(String studentId) {
        Student student = students.get(studentId);
        if (student == null) {
            return new ArrayList<>();
        }
        
        List<CourseSection> enrolledSections = new ArrayList<>();
        
        for (String sectionId : student.getEnrolledCourses()) {
            CourseSection section = courseSections.get(sectionId);
            if (section != null) {
                enrolledSections.add(section);
            }
        }
        
        return enrolledSections;
    }
    
    /**
     * Loads all data from JSON files
     */
    private void loadAllData() {
        loadStudents();
        loadAdvisors();
        loadCourses();
        loadCourseSections();
        loadRegistrationRequests();
    }
    
    /**
     * Saves all data to JSON files
     */
    private void saveAllData() {
        saveStudents();
        saveAdvisors();
        saveCourses();
        saveCourseSections();
        saveRegistrationRequests();
    }
    
    /**
     * Loads students from JSON file
     */
    private void loadStudents() {
        try {
            File file = new File(STUDENTS_FILE);
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                Type listType = new TypeToken<List<Student>>(){}.getType();
                List<Student> studentList = gson.fromJson(reader, listType);
                reader.close();
                
                if (studentList != null) {
                    for (Student student : studentList) {
                        students.put(student.getUserId(), student);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
    }
    
    /**
     * Saves students to JSON file
     */
    private void saveStudents() {
        try {
            FileWriter writer = new FileWriter(STUDENTS_FILE);
            gson.toJson(new ArrayList<>(students.values()), writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving students: " + e.getMessage());
        }
    }
    
    /**
     * Loads advisors from JSON file
     */
    private void loadAdvisors() {
        try {
            File file = new File(ADVISORS_FILE);
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                Type listType = new TypeToken<List<Advisor>>(){}.getType();
                List<Advisor> advisorList = gson.fromJson(reader, listType);
                reader.close();
                
                if (advisorList != null) {
                    for (Advisor advisor : advisorList) {
                        advisors.put(advisor.getUserId(), advisor);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading advisors: " + e.getMessage());
        }
    }
    
    /**
     * Saves advisors to JSON file
     */
    private void saveAdvisors() {
        try {
            FileWriter writer = new FileWriter(ADVISORS_FILE);
            gson.toJson(new ArrayList<>(advisors.values()), writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving advisors: " + e.getMessage());
        }
    }
    
    /**
     * Loads courses from JSON file
     */
    private void loadCourses() {
        try {
            File file = new File(COURSES_FILE);
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                Type listType = new TypeToken<List<Course>>(){}.getType();
                List<Course> courseList = gson.fromJson(reader, listType);
                reader.close();
                
                if (courseList != null) {
                    for (Course course : courseList) {
                        courses.put(course.getCourseCode(), course);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
    }
    
    /**
     * Saves courses to JSON file
     */
    private void saveCourses() {
        try {
            FileWriter writer = new FileWriter(COURSES_FILE);
            gson.toJson(new ArrayList<>(courses.values()), writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving courses: " + e.getMessage());
        }
    }
    
    /**
     * Loads course sections from JSON file
     */
    private void loadCourseSections() {
        try {
            File file = new File(SECTIONS_FILE);
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                Type listType = new TypeToken<List<CourseSection>>(){}.getType();
                List<CourseSection> sectionList = gson.fromJson(reader, listType);
                reader.close();
                
                if (sectionList != null) {
                    for (CourseSection section : sectionList) {
                        courseSections.put(section.getSectionId(), section);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading course sections: " + e.getMessage());
        }
    }
    
    /**
     * Saves course sections to JSON file
     */
    private void saveCourseSections() {
        try {
            FileWriter writer = new FileWriter(SECTIONS_FILE);
            gson.toJson(new ArrayList<>(courseSections.values()), writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving course sections: " + e.getMessage());
        }
    }
    
    // Getters for accessing data (useful for testing and UI)
    public Map<String, Student> getStudents() {
        return new HashMap<>(students);
    }
    
    public Map<String, Advisor> getAdvisors() {
        return new HashMap<>(advisors);
    }
    
    public Map<String, Course> getCourses() {
        return new HashMap<>(courses);
    }
    
    public Map<String, CourseSection> getCourseSections() {
        return new HashMap<>(courseSections);
    }
    
    public Map<String, RegistrationRequest> getRegistrationRequests() {
        return new HashMap<>(registrationRequests);
    }
    
    /**
     * Loads registration requests from JSON file
     */
    private void loadRegistrationRequests() {
        try {
            File file = new File(REQUESTS_FILE);
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                Type listType = new TypeToken<List<RegistrationRequest>>(){}.getType();
                List<RegistrationRequest> requestList = gson.fromJson(reader, listType);
                reader.close();
                
                if (requestList != null) {
                    for (RegistrationRequest request : requestList) {
                        registrationRequests.put(request.getRequestId(), request);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading registration requests: " + e.getMessage());
        }
    }
    
    /**
     * Saves registration requests to JSON file
     */
    private void saveRegistrationRequests() {
        try {
            FileWriter writer = new FileWriter(REQUESTS_FILE);
            gson.toJson(new ArrayList<>(registrationRequests.values()), writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving registration requests: " + e.getMessage());
        }
    }
    
    /**
     * Inner class to represent registration results
     */
    public static class RegistrationResult {
        private boolean success;
        private String message;
        
        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return message;
        }
    }
}