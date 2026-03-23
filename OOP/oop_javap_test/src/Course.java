import java.util.ArrayList;
import java.util.List;

/**
 * Represents a course in the university system.
 * Contains course metadata, prerequisites, and academic information.
 */
public class Course {
    private String courseCode;
    private String courseName;
    private int credits;
    private int year;
    private List<String> prerequisites;
    private String department;
    
    /**
     * Constructor for Course
     * @param courseCode Unique course identifier (e.g., "CSE3063")
     * @param courseName Full name of the course
     * @param credits Number of credit hours
     * @param year Target academic year (1-4)
     * @param prerequisites List of prerequisite course codes
     * @param department Department offering the course
     */
    public Course(String courseCode, String courseName, int credits, int year, 
                  List<String> prerequisites, String department) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.year = year;
        this.prerequisites = prerequisites != null ? new ArrayList<>(prerequisites) : new ArrayList<>();
        this.department = department;
    }
    
    /**
     * Default constructor for JSON deserialization
     */
    public Course() {
        this.prerequisites = new ArrayList<>();
    }
    
    /**
     * Checks if a student has completed all prerequisites for this course
     * @param completedCourses List of course codes the student has completed
     * @return true if all prerequisites are met, false otherwise
     */
    public boolean arePrerequisitesMet(List<String> completedCourses) {
        if (prerequisites.isEmpty()) {
            return true;
        }
        
        for (String prerequisite : prerequisites) {
            if (!completedCourses.contains(prerequisite)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gets list of missing prerequisites for a student
     * @param completedCourses List of course codes the student has completed
     * @return List of missing prerequisite course codes
     */
    public List<String> getMissingPrerequisites(List<String> completedCourses) {
        List<String> missing = new ArrayList<>();
        for (String prerequisite : prerequisites) {
            if (!completedCourses.contains(prerequisite)) {
                missing.add(prerequisite);
            }
        }
        return missing;
    }
    
    /**
     * Checks if a student has completed all prerequisites for this course
     * This method now works with the Transcript system to check for passing grades
     * @param student The student to check prerequisites for
     * @return true if all prerequisites are met with passing grades, false otherwise
     */
    public boolean arePrerequisitesMet(Student student) {
        if (prerequisites.isEmpty()) {
            return true;
        }
        
        List<String> completedCodes = student.getCompletedCourseCodes();
        return arePrerequisitesMet(completedCodes);
    }
    
    /**
     * Gets list of missing prerequisites for a student
     * @param student The student to check
     * @return List of missing prerequisite course codes
     */
    public List<String> getMissingPrerequisites(Student student) {
        List<String> completedCodes = student.getCompletedCourseCodes();
        return getMissingPrerequisites(completedCodes);
    }
    
    /**
     * Checks if this course is available for a specific academic year
     * @param studentYear The student's current academic year
     * @return true if course is appropriate for the student's year
     */
    public boolean isAvailableForYear(int studentYear) {
        // Students can take courses for their year or lower years
        // But should be warned about taking higher year courses
        return this.year <= studentYear + 1; // Allow taking next year's courses
    }
    
    // Getters and Setters
    public String getCourseCode() {
        return courseCode;
    }
    
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public int getCredits() {
        return credits;
    }
    
    public void setCredits(int credits) {
        this.credits = credits;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public List<String> getPrerequisites() {
        return new ArrayList<>(prerequisites);
    }
    
    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites != null ? new ArrayList<>(prerequisites) : new ArrayList<>();
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    @Override
    public String toString() {
        return String.format("Course{courseCode='%s', courseName='%s', credits=%d, year=%d, prerequisites=%s}", 
                           courseCode, courseName, credits, year, prerequisites);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return courseCode != null ? courseCode.equals(course.courseCode) : course.courseCode == null;
    }
    
    @Override
    public int hashCode() {
        return courseCode != null ? courseCode.hashCode() : 0;
    }
}