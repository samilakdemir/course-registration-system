import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing staff members in the university system.
 * Extends Person and serves as base class for Advisor, Lecturer, and other staff roles.
 */
public abstract class Staff extends Person {
    protected String department;
    protected String title;
    protected String officeLocation;
    protected String employeeId;
    protected String phoneNumber;
    protected String email;
    protected List<String> responsibilities;
    
    /**
     * Constructor for Staff
     * @param userId Unique user identifier
     * @param name Full name of the staff member
     * @param username Login username
     * @param password Login password
     * @param department Department affiliation
     * @param title Academic/professional title
     * @param officeLocation Office location
     * @param employeeId Employee identification number
     */
    public Staff(String userId, String name, String username, String password,
                 String department, String title, String officeLocation, String employeeId) {
        super(userId, name, username, password);
        this.department = department;
        this.title = title;
        this.officeLocation = officeLocation;
        this.employeeId = employeeId;
        this.responsibilities = new ArrayList<>();
    }
    
    /**
     * Default constructor for JSON deserialization
     */
    public Staff() {
        super();
        this.responsibilities = new ArrayList<>();
    }
    
    /**
     * Gets the staff member's role/position (to be implemented by subclasses)
     * @return String representing the staff role
     */
    public abstract String getStaffRole();
    
    /**
     * Adds a responsibility to the staff member's duties
     * @param responsibility Description of the responsibility
     */
    public void addResponsibility(String responsibility) {
        if (responsibility != null && !responsibility.trim().isEmpty()) {
            responsibilities.add(responsibility.trim());
        }
    }
    
    /**
     * Removes a responsibility from the staff member's duties
     * @param responsibility Responsibility to remove
     * @return true if responsibility was removed, false if not found
     */
    public boolean removeResponsibility(String responsibility) {
        return responsibilities.remove(responsibility);
    }
    
    /**
     * Gets formatted display name with title
     * @return Formatted name (e.g., "Dr. John Smith")
     */
    public String getFormattedName() {
        if (title != null && !title.isEmpty()) {
            return title + " " + getName();
        }
        return getName();
    }
    
    /**
     * Gets full contact information
     * @return Formatted contact information
     */
    public String getContactInfo() {
        StringBuilder contact = new StringBuilder();
        contact.append("Name: ").append(getFormattedName()).append("\n");
        contact.append("Department: ").append(department != null ? department : "N/A").append("\n");
        contact.append("Office: ").append(officeLocation != null ? officeLocation : "N/A").append("\n");
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            contact.append("Phone: ").append(phoneNumber).append("\n");
        }
        
        if (email != null && !email.isEmpty()) {
            contact.append("Email: ").append(email).append("\n");
        }
        
        return contact.toString();
    }
    
    /**
     * Checks if staff member belongs to a specific department
     * @param departmentName Department to check
     * @return true if staff member is in the specified department
     */
    public boolean isInDepartment(String departmentName) {
        return department != null && department.equalsIgnoreCase(departmentName);
    }
    
    /**
     * Gets office hours (placeholder for future implementation)
     * @return Office hours string
     */
    public String getOfficeHours() {
        // This could be extended in future iterations
        return "By appointment";
    }
    
    @Override
    public String getUserType() {
        return "STAFF";
    }
    
    // Getters and Setters
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getOfficeLocation() {
        return officeLocation;
    }
    
    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public List<String> getResponsibilities() {
        return new ArrayList<>(responsibilities);
    }
    
    public void setResponsibilities(List<String> responsibilities) {
        this.responsibilities = responsibilities != null ? 
            new ArrayList<>(responsibilities) : new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return String.format("Staff{userId='%s', name='%s', title='%s', department='%s', role='%s'}", 
                           getUserId(), getFormattedName(), title, department, getStaffRole());
    }
}