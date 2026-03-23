import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a course registration request that requires advisor approval.
 * Manages the workflow between student request and advisor approval.
 */
public class RegistrationRequest {
    private String requestId;
    private String studentId;
    private String sectionId;
    private String courseCode;
    private String courseName;
    private RequestStatus status;
    private String requestDate;
    private String approvalDate;
    private String advisorId;
    private String advisorComments;
    private String studentReason;
    
    /**
     * Enum for registration request status
     */
    public enum RequestStatus {
        PENDING("Pending Advisor Approval"),
        APPROVED("Approved by Advisor"),
        REJECTED("Rejected by Advisor"),
        ENROLLED("Successfully Enrolled"),
        EXPIRED("Request Expired");
        
        private final String description;
        
        RequestStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Constructor for creating a new registration request
     * @param requestId Unique request identifier
     * @param studentId ID of requesting student
     * @param sectionId Section ID to register for
     * @param courseCode Course code
     * @param courseName Course name
     * @param studentReason Reason provided by student
     */
    public RegistrationRequest(String requestId, String studentId, String sectionId, 
                             String courseCode, String courseName, String studentReason) {
        this.requestId = requestId;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.studentReason = studentReason;
        this.status = RequestStatus.PENDING;
        this.requestDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.advisorComments = "";
    }
    
    /**
     * Default constructor for JSON deserialization
     */
    public RegistrationRequest() {}
    
    /**
     * Approves the registration request
     * @param advisorId ID of approving advisor
     * @param comments Optional comments from advisor
     */
    public void approve(String advisorId, String comments) {
        this.status = RequestStatus.APPROVED;
        this.advisorId = advisorId;
        this.advisorComments = comments != null ? comments : "";
        this.approvalDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    /**
     * Rejects the registration request
     * @param advisorId ID of rejecting advisor
     * @param reason Reason for rejection
     */
    public void reject(String advisorId, String reason) {
        this.status = RequestStatus.REJECTED;
        this.advisorId = advisorId;
        this.advisorComments = reason != null ? reason : "No reason provided";
        this.approvalDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    /**
     * Marks the request as enrolled (after successful registration)
     */
    public void markAsEnrolled() {
        if (status == RequestStatus.APPROVED) {
            this.status = RequestStatus.ENROLLED;
        }
    }
    
    /**
     * Checks if the request is pending approval
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }
    
    /**
     * Checks if the request has been approved
     * @return true if status is APPROVED
     */
    public boolean isApproved() {
        return status == RequestStatus.APPROVED;
    }
    
    /**
     * Checks if the request has been rejected
     * @return true if status is REJECTED
     */
    public boolean isRejected() {
        return status == RequestStatus.REJECTED;
    }
    
    /**
     * Gets formatted display string for the request
     * @return Formatted request information
     */
    public String getDisplayString() {
        StringBuilder display = new StringBuilder();
        display.append(String.format("Request ID: %s\n", requestId));
        display.append(String.format("Course: %s - %s\n", courseCode, courseName));
        display.append(String.format("Section: %s\n", sectionId));
        display.append(String.format("Student ID: %s\n", studentId));
        display.append(String.format("Status: %s\n", status.getDescription()));
        display.append(String.format("Request Date: %s\n", requestDate));
        
        // Don't show student reason in display
        
        if (approvalDate != null) {
            display.append(String.format("Decision Date: %s\n", approvalDate));
        }
        
        if (advisorComments != null && !advisorComments.trim().isEmpty()) {
            display.append(String.format("Advisor Comments: %s\n", advisorComments));
        }
        
        return display.toString();
    }
    
    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
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
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    public String getRequestDate() {
        return requestDate;
    }
    
    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }
    
    public String getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(String approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public String getAdvisorId() {
        return advisorId;
    }
    
    public void setAdvisorId(String advisorId) {
        this.advisorId = advisorId;
    }
    
    public String getAdvisorComments() {
        return advisorComments;
    }
    
    public void setAdvisorComments(String advisorComments) {
        this.advisorComments = advisorComments;
    }
    
    public String getStudentReason() {
        return studentReason;
    }
    
    public void setStudentReason(String studentReason) {
        this.studentReason = studentReason;
    }
    
    @Override
    public String toString() {
        return String.format("RegistrationRequest{requestId='%s', student='%s', course='%s', status='%s'}", 
                           requestId, studentId, courseCode, status);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RegistrationRequest request = (RegistrationRequest) obj;
        return requestId != null ? requestId.equals(request.requestId) : request.requestId == null;
    }
    
    @Override
    public int hashCode() {
        return requestId != null ? requestId.hashCode() : 0;
    }
}