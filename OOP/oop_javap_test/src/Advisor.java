import java.util.ArrayList;
import java.util.List;

/**
 * Represents an advisor in the course registration system.
 * Extends Staff class and adds advisor-specific functionality.
 */
public class Advisor extends Staff {
    private List<String> adviseeList;
    private int maxAdvisees;
    private String advisorType; // e.g., "Academic", "Research", "Career"
    
    /**
     * Constructor for Advisor
     * @param userId Unique advisor identifier
     * @param name Full name of the advisor
     * @param username Login username
     * @param password Login password
     * @param department Department affiliation
     * @param title Academic title (e.g., "Dr.", "Prof.")
     * @param officeLocation Office location
     * @param employeeId Employee ID
     */
    public Advisor(String userId, String name, String username, String password,
                   String department, String title, String officeLocation, String employeeId) {
        super(userId, name, username, password, department, title, officeLocation, employeeId);
        this.adviseeList = new ArrayList<>();
        this.maxAdvisees = 20; // Default maximum
        this.advisorType = "Academic";
        
        // Add default advisor responsibilities
        addResponsibility("Academic advising and student guidance");
        addResponsibility("Course registration approval");
        addResponsibility("Academic progress monitoring");
    }
    
    /**
     * Legacy constructor for backward compatibility
     */
    public Advisor(String userId, String name, String username, String password,
                   String department, String title, String officeLocation) {
        this(userId, name, username, password, department, title, officeLocation, "EMP" + userId);
    }
    
    /**
     * Default constructor for JSON deserialization
     */
    public Advisor() {
        super();
        this.adviseeList = new ArrayList<>();
        this.maxAdvisees = 20;
        this.advisorType = "Academic";
    }
    
    @Override
    public String getStaffRole() {
        return "ADVISOR";
    }
    
    @Override
    public String getUserType() {
        return "ADVISOR";
    }
    
    /**
     * Adds a student to this advisor's advisee list
     * @param studentId ID of the student to add
     * @return true if student was added, false if already in list or at capacity
     */
    public boolean addAdvisee(String studentId) {
        if (adviseeList.contains(studentId)) {
            return false;
        }
        
        if (adviseeList.size() >= maxAdvisees) {
            return false; // At capacity
        }
        
        adviseeList.add(studentId);
        return true;
    }
    
    /**
     * Removes a student from this advisor's advisee list
     * @param studentId ID of the student to remove
     * @return true if student was removed, false if not in list
     */
    public boolean removeAdvisee(String studentId) {
        return adviseeList.remove(studentId);
    }
    
    /**
     * Checks if a student is advised by this advisor
     * @param studentId ID of the student to check
     * @return true if student is an advisee, false otherwise
     */
    public boolean isAdvisee(String studentId) {
        return adviseeList.contains(studentId);
    }
    
    /**
     * Gets the number of students this advisor is responsible for
     * @return Number of advisees
     */
    public int getAdviseeCount() {
        return adviseeList.size();
    }
    
    /**
     * Approves a student's course registration
     * @param studentId ID of the student
     * @param courseCode Course code to approve
     * @return true if approval granted, false otherwise
     */
    public boolean approveCourseRegistration(String studentId, String courseCode) {
        return isAdvisee(studentId);
    }
    
    /**
     * Provides academic guidance to a student
     * @param studentId ID of the student
     * @param currentGPA Student's current GPA
     * @param enrolledCredits Number of credits currently enrolled
     * @return Guidance message
     */
    public String provideAcademicGuidance(String studentId, double currentGPA, int enrolledCredits) {
        if (!isAdvisee(studentId)) {
            return "This student is not under your advisement.";
        }
        
        StringBuilder guidance = new StringBuilder();
        guidance.append("Academic Guidance for Student ").append(studentId).append(":\n");
        
        if (currentGPA < 2.0) {
            guidance.append("- WARNING: GPA below minimum requirement. Consider reducing course load.\n");
        } else if (currentGPA < 2.5) {
            guidance.append("- GPA needs improvement. Focus on core subjects.\n");
        } else if (currentGPA >= 3.5) {
            guidance.append("- Excellent academic performance. Consider advanced courses.\n");
        }
        
        if (enrolledCredits > 18) {
            guidance.append("- High credit load. Monitor academic performance carefully.\n");
        } else if (enrolledCredits < 12) {
            guidance.append("- Low credit load. Consider taking additional courses if possible.\n");
        }
        
        return guidance.toString();
    }
    
    /**
     * Checks if advisor can take on more advisees
     * @return true if can take more advisees, false if at capacity
     */
    public boolean canTakeMoreAdvisees() {
        return adviseeList.size() < maxAdvisees;
    }
    
    /**
     * Gets advisor's workload as a percentage
     * @return Workload percentage (0.0 to 100.0)
     */
    public double getWorkloadPercentage() {
        return (double) adviseeList.size() / maxAdvisees * 100.0;
    }
    
    /**
     * Gets advisor's specialization areas
     * @return List of specialization areas
     */
    public List<String> getSpecializations() {
        List<String> specializations = new ArrayList<>();
        
        // Based on department, add relevant specializations
        if (getDepartment() != null) {
            switch (getDepartment().toLowerCase()) {
                case "computer engineering":
                case "computer science":
                    specializations.add("Software Development");
                    specializations.add("Academic Planning");
                    specializations.add("Career Guidance");
                    break;
                default:
                    specializations.add("General Academic Advising");
                    break;
            }
        }
        
        return specializations;
    }
    
    // Getters and Setters
    public List<String> getAdviseeList() {
        return new ArrayList<>(adviseeList);
    }
    
    public void setAdviseeList(List<String> adviseeList) {
        this.adviseeList = adviseeList != null ? 
            new ArrayList<>(adviseeList) : new ArrayList<>();
    }
    
    public int getMaxAdvisees() {
        return maxAdvisees;
    }
    
    public void setMaxAdvisees(int maxAdvisees) {
        this.maxAdvisees = Math.max(1, maxAdvisees); // Ensure at least 1
    }
    
    public String getAdvisorType() {
        return advisorType;
    }
    
    public void setAdvisorType(String advisorType) {
        this.advisorType = advisorType;
    }
    
    @Override
    public String toString() {
        return String.format("Advisor{userId='%s', name='%s', department='%s', advisees=%d/%d}", 
                           getUserId(), getFormattedName(), getDepartment(), 
                           adviseeList.size(), maxAdvisees);
    }
}