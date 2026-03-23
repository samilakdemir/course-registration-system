import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student in the course registration system.
 * Extends Person class and adds student-specific functionality.
 */
public class Student extends Person {
    private int year;
    private double gpa;
    private String advisorId;
    private List<String> enrolledCourses;
    private List<CompletedCourse> completedCourses;
    private Transcript transcript;
    
    // Constants
    public static final int MAX_COURSES = 5;
    public static final double MIN_GPA = 0.0;
    public static final double MAX_GPA = 4.0;
    
    /**
     * Constructor for Student
     * @param userId Unique student identifier
     * @param name Full name of the student
     * @param username Login username
     * @param password Login password
     * @param year Academic year (1-4)
     * @param gpa Current GPA
     * @param advisorId ID of assigned advisor
     */
    public Student(String userId, String name, String username, String password,
                   int year, double gpa, String advisorId) {
        super(userId, name, username, password);
        this.year = year;
        this.gpa = gpa;
        this.advisorId = advisorId;
        this.enrolledCourses = new ArrayList<>();
        this.completedCourses = new ArrayList<>();
        this.transcript = new Transcript(userId);
    }
    
    /**
     * Default constructor for JSON deserialization
     */
    public Student() {
        super();
        this.enrolledCourses = new ArrayList<>();
        this.completedCourses = new ArrayList<>();
        this.transcript = new Transcript();
    }
    
    /**
     * Checks if student can enroll in a specific course section
     * @param course The course to check
     * @param section The section to enroll in
     * @return true if enrollment is possible, false otherwise
     */
    public boolean canEnrollInCourse(Course course, CourseSection section) {
        // Check if student has reached maximum course limit
        if (enrolledCourses.size() >= MAX_COURSES) {
            return false;
        }
        
        // Check if already enrolled in this course section
        if (enrolledCourses.contains(section.getSectionId())) {
            return false;
        }
        
        // Check if already enrolled in another section of the same course
        for (String enrolledSectionId : enrolledCourses) {
            String enrolledCourseCode = extractCourseCodeFromSection(enrolledSectionId);
            if (enrolledCourseCode.equals(course.getCourseCode())) {
                return false;
            }
        }
        
        // Check if student already passed this course
        List<String> completedCourseCodes = getCompletedCourseCodes();
        if (completedCourseCodes.contains(course.getCourseCode())) {
            return false; // Cannot retake a passed course
        }
        
        // Check prerequisites
        if (!course.arePrerequisitesMet(completedCourseCodes)) {
            return false;
        }
        
        // Check if section is full
        if (section.isFull()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets detailed reasons why student cannot enroll in a course
     * @param course The course to check
     * @param section The section to enroll in
     * @return List of reasons preventing enrollment
     */
    public List<String> getEnrollmentBlockingReasons(Course course, CourseSection section) {
        List<String> reasons = new ArrayList<>();
        
        if (enrolledCourses.size() >= MAX_COURSES) {
            reasons.add("Maximum course limit reached (" + MAX_COURSES + ")");
        }
        
        if (enrolledCourses.contains(section.getSectionId())) {
            reasons.add("Already enrolled in this section");
        }
        
        // Check if already enrolled in another section of the same course
        for (String enrolledSectionId : enrolledCourses) {
            String enrolledCourseCode = extractCourseCodeFromSection(enrolledSectionId);
            if (enrolledCourseCode.equals(course.getCourseCode())) {
                reasons.add("Already enrolled in another section of this course");
                break;
            }
        }
        
        // Check if student already passed this course
        List<String> completedCourseCodes = getCompletedCourseCodes();
        if (completedCourseCodes.contains(course.getCourseCode())) {
            reasons.add("Course already completed - cannot retake passed courses");
        }
        
        // Check prerequisites
        if (!course.arePrerequisitesMet(completedCourseCodes)) {
            List<String> missing = course.getMissingPrerequisites(completedCourseCodes);
            reasons.add("Missing prerequisites: " + String.join(", ", missing));
        }
        
        if (section.isFull()) {
            reasons.add("Section is full");
        }
        
        return reasons;
    }
    
    /**
     * Enrolls student in a course section
     * @param sectionId Section ID to enroll in
     * @return true if enrollment successful, false otherwise
     */
    public boolean enrollInCourse(String sectionId) {
        if (enrolledCourses.size() >= MAX_COURSES) {
            return false;
        }
        
        if (enrolledCourses.contains(sectionId)) {
            return false;
        }
        
        enrolledCourses.add(sectionId);
        return true;
    }
    
    /**
     * Drops a course section
     * @param sectionId Section ID to drop
     * @return true if drop successful, false if not enrolled
     */
    public boolean dropCourse(String sectionId) {
        return enrolledCourses.remove(sectionId);
    }
    
    /**
     * Gets list of completed course codes for prerequisite checking
     * @return List of completed course codes
     */
    public List<String> getCompletedCourseCodes() {
        // Use transcript if available, otherwise fall back to legacy list
        if (transcript != null && !transcript.getEntries().isEmpty()) {
            return transcript.getCompletedCourseCodes();
        }
        
        // Legacy method
        List<String> codes = new ArrayList<>();
        for (CompletedCourse completed : completedCourses) {
            codes.add(completed.getCourseCode());
        }
        return codes;
    }
    
    /**
     * Gets the student's academic transcript
     * @return Academic transcript
     */
    public Transcript getTranscript() {
        return transcript;
    }
    
    /**
     * Sets the student's academic transcript
     * @param transcript Academic transcript
     */
    public void setTranscript(Transcript transcript) {
        this.transcript = transcript;
        if (transcript != null) {
            this.gpa = transcript.getCumulativeGPA();
        }
    }
    
    /**
     * Adds a completed course to student's record and transcript
     * @param courseCode Course code
     * @param courseName Course name
     * @param credits Number of credits
     * @param grade Letter grade received
     * @param semester Semester completed
     * @param year Academic year
     */
    public void addCompletedCourse(String courseCode, String courseName, int credits, 
                                  String grade, String semester, int year) {
        // Add to legacy completed courses list (for backward compatibility)
        completedCourses.add(new CompletedCourse(courseCode, grade, semester));
        
        // Add to transcript
        transcript.addEntry(courseCode, courseName, credits, grade, semester, year);
        
        // Update GPA from transcript
        this.gpa = transcript.getCumulativeGPA();
    }
    
    /**
     * Adds a completed course to student's record (legacy method)
     * @param courseCode Course code
     * @param grade Letter grade received
     * @param semester Semester completed
     */
    public void addCompletedCourse(String courseCode, String grade, String semester) {
        // For legacy compatibility, assume 3 credits and extract year from semester
        int year = extractYearFromSemester(semester);
        String courseName = "Course " + courseCode; // Default name
        addCompletedCourse(courseCode, courseName, 3, grade, semester, year);
    }
    
    /**
     * Extracts year from semester string (e.g., "Fall2023" -> 2023)
     * @param semester Semester string
     * @return Academic year
     */
    private int extractYearFromSemester(String semester) {
        try {
            return Integer.parseInt(semester.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 2024; // Default year
        }
    }
    
    /**
     * Gets the number of courses student can still enroll in
     * @return Number of available enrollment slots
     */
    public int getAvailableEnrollmentSlots() {
        return MAX_COURSES - enrolledCourses.size();
    }
    
    /**
     * Checks if student is on track academically
     * @return true if GPA is acceptable and course load is reasonable
     */
    public boolean isAcademicStatusGood() {
        return gpa >= 2.0; // Minimum GPA requirement
    }
    
    /**
     * Extracts course code from section ID
     * @param sectionId Full section ID (e.g., "CSE3063-01")
     * @return Course code (e.g., "CSE3063")
     */
    private String extractCourseCodeFromSection(String sectionId) {
        if (sectionId.contains("-")) {
            return sectionId.substring(0, sectionId.lastIndexOf("-"));
        }
        return sectionId;
    }
    
    @Override
    public String getUserType() {
        return "STUDENT";
    }
    
    // Getters and Setters
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public double getGpa() {
        return gpa;
    }
    
    public void setGpa(double gpa) {
        if (gpa >= MIN_GPA && gpa <= MAX_GPA) {
            this.gpa = gpa;
        }
    }
    
    public String getAdvisorId() {
        return advisorId;
    }
    
    public void setAdvisorId(String advisorId) {
        this.advisorId = advisorId;
    }
    
    public List<String> getEnrolledCourses() {
        return new ArrayList<>(enrolledCourses);
    }
    
    public void setEnrolledCourses(List<String> enrolledCourses) {
        this.enrolledCourses = enrolledCourses != null ? 
            new ArrayList<>(enrolledCourses) : new ArrayList<>();
    }
    
    public List<CompletedCourse> getCompletedCourses() {
        return new ArrayList<>(completedCourses);
    }
    
    public void setCompletedCourses(List<CompletedCourse> completedCourses) {
        this.completedCourses = completedCourses != null ? 
            new ArrayList<>(completedCourses) : new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return String.format("Student{userId='%s', name='%s', year=%d, gpa=%.2f, enrolledCourses=%d}", 
                           getUserId(), getName(), year, gpa, enrolledCourses.size());
    }
    
    /**
     * Inner class to represent completed courses
     */
    public static class CompletedCourse {
        private String courseCode;
        private String grade;
        private String semester;
        
        public CompletedCourse(String courseCode, String grade, String semester) {
            this.courseCode = courseCode;
            this.grade = grade;
            this.semester = semester;
        }
        
        public CompletedCourse() {}
        
        // Getters and Setters
        public String getCourseCode() {
            return courseCode;
        }
        
        public void setCourseCode(String courseCode) {
            this.courseCode = courseCode;
        }
        
        public String getGrade() {
            return grade;
        }
        
        public void setGrade(String grade) {
            this.grade = grade;
        }
        
        public String getSemester() {
            return semester;
        }
        
        public void setSemester(String semester) {
            this.semester = semester;
        }
        
        @Override
        public String toString() {
            return String.format("CompletedCourse{courseCode='%s', grade='%s', semester='%s'}", 
                               courseCode, grade, semester);
        }
    }
}