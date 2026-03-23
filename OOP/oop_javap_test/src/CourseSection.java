import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specific section/offering of a course.
 * Manages enrollment, capacity, and section-specific information.
 */
public class CourseSection {
    private String sectionId;
    private String courseCode;
    private int capacity;
    private List<String> enrolledStudents;
    private String semester;
    private String instructor;
    
    /**
     * Constructor for CourseSection
     * @param sectionId Unique section identifier (e.g., "CSE3063-01")
     * @param courseCode Course code this section belongs to
     * @param capacity Maximum number of students that can enroll
     * @param semester Semester offering (e.g., "Fall2024")
     * @param instructor Instructor teaching this section
     */
    public CourseSection(String sectionId, String courseCode, int capacity, 
                        String semester, String instructor) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.capacity = capacity;
        this.semester = semester;
        this.instructor = instructor;
        this.enrolledStudents = new ArrayList<>();
    }
    
    /**
     * Default constructor for JSON deserialization
     */
    public CourseSection() {
        this.enrolledStudents = new ArrayList<>();
    }
    
    /**
     * Attempts to enroll a student in this section
     * @param studentId ID of the student to enroll
     * @return true if enrollment successful, false if section is full or student already enrolled
     */
    public boolean enrollStudent(String studentId) {
        if (isFull()) {
            return false;
        }
        
        if (enrolledStudents.contains(studentId)) {
            return false; // Student already enrolled
        }
        
        enrolledStudents.add(studentId);
        return true;
    }
    
    /**
     * Removes a student from this section
     * @param studentId ID of the student to remove
     * @return true if student was removed, false if student was not enrolled
     */
    public boolean dropStudent(String studentId) {
        return enrolledStudents.remove(studentId);
    }
    
    /**
     * Checks if the section is at full capacity
     * @return true if section is full, false otherwise
     */
    public boolean isFull() {
        return enrolledStudents.size() >= capacity;
    }
    
    /**
     * Gets the number of available spots in the section
     * @return Number of available enrollment spots
     */
    public int getAvailableSpots() {
        return capacity - enrolledStudents.size();
    }
    
    /**
     * Checks if a specific student is enrolled in this section
     * @param studentId ID of the student to check
     * @return true if student is enrolled, false otherwise
     */
    public boolean isStudentEnrolled(String studentId) {
        return enrolledStudents.contains(studentId);
    }
    
    /**
     * Gets the current enrollment count
     * @return Number of currently enrolled students
     */
    public int getCurrentEnrollment() {
        return enrolledStudents.size();
    }
    
    /**
     * Gets enrollment percentage
     * @return Percentage of capacity filled (0.0 to 100.0)
     */
    public double getEnrollmentPercentage() {
        if (capacity == 0) return 0.0;
        return (double) enrolledStudents.size() / capacity * 100.0;
    }
    
    // Getters and Setters
    public String getSectionId() {
        return sectionId;
    }
    
    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }
    
    public String getCourseCode() {
        return courseCode;
    }
    
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public List<String> getEnrolledStudents() {
        return new ArrayList<>(enrolledStudents);
    }
    
    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents != null ? 
            new ArrayList<>(enrolledStudents) : new ArrayList<>();
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public String getInstructor() {
        return instructor;
    }
    
    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }
    
    @Override
    public String toString() {
        return String.format("CourseSection{sectionId='%s', courseCode='%s', enrollment=%d/%d, semester='%s'}", 
                           sectionId, courseCode, enrolledStudents.size(), capacity, semester);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CourseSection section = (CourseSection) obj;
        return sectionId != null ? sectionId.equals(section.sectionId) : section.sectionId == null;
    }
    
    @Override
    public int hashCode() {
        return sectionId != null ? sectionId.hashCode() : 0;
    }
}