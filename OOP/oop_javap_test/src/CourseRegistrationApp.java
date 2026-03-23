import java.util.List;
import java.util.Scanner;

/**
 * Main application class with command-line interface for the Course Registration System.
 * Handles user interactions and menu navigation with advisor approval workflow.
 */
public class CourseRegistrationApp {
    private CourseRegistrationSystem system;
    private Scanner scanner;
    private boolean running;
    
    /**
     * Constructor initializes the application
     */
    public CourseRegistrationApp() {
        this.system = new CourseRegistrationSystem();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }
    
    /**
     * Main entry point of the application
     */
    public static void main(String[] args) {
        CourseRegistrationApp app = new CourseRegistrationApp();
        app.run();
    }
    
    /**
     * Main application loop
     */
    public void run() {
        printWelcome();
        
        while (running) {
            if (system.getCurrentUser() == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
        
        scanner.close();
        System.out.println("Thank you for using the Course Registration System!");
    }
    
    /**
     * Prints welcome message
     */
    private void printWelcome() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║        Course Registration System           ║");
        System.out.println("║          Advisor Approval System            ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
    }
    
    /**
     * Shows login menu for unauthenticated users
     */
    private void showLoginMenu() {
        System.out.println("\n=== LOGIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Please select an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "2":
                running = false;
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    /**
     * Handles user login process
     */
    private void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        if (system.authenticateUser(username, password)) {
            Person user = system.getCurrentUser();
            System.out.println("\nLogin successful! Welcome, " + user.getName());
            System.out.println("User Type: " + user.getUserType());
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }
    
    /**
     * Shows main menu for authenticated users
     */
    private void showMainMenu() {
        Person currentUser = system.getCurrentUser();
        
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("Logged in as: " + currentUser.getName() + " (" + currentUser.getUserType() + ")");
        
        if (currentUser instanceof Student) {
            showStudentMenu();
        } else if (currentUser instanceof Advisor) {
            showAdvisorMenu();
        }
    }
    
    /**
     * Shows menu options for students
     */
    private void showStudentMenu() {
        System.out.println("\n=== STUDENT MENU ===");
        System.out.println("1. View Available Courses");
        System.out.println("2. Request Course Registration");
        System.out.println("3. View My Courses");
        System.out.println("4. View Registration Requests");
        System.out.println("5. Drop Course");
        System.out.println("6. View Academic Information");
        System.out.println("7. Logout");
        System.out.print("Please select an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                viewAvailableCourses();
                break;
            case "2":
                requestCourseRegistration();
                break;
            case "3":
                viewMyCourses();
                break;
            case "4":
                viewMyRegistrationRequests();
                break;
            case "5":
                dropCourse();
                break;
            case "6":
                viewAcademicInfo();
                break;
            case "7":
                system.logout();
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    /**
     * Shows menu options for advisors
     */
    private void showAdvisorMenu() {
        System.out.println("\n=== ADVISOR MENU ===");
        System.out.println("1. View My Advisees");
        System.out.println("2. Review Registration Requests");
        System.out.println("3. View All Courses");
        System.out.println("4. View Course Enrollments");
        System.out.println("5. Logout");
        System.out.print("Please select an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                viewAdvisees();
                break;
            case "2":
                reviewRegistrationRequests();
                break;
            case "3":
                viewAllCourses();
                break;
            case "4":
                viewCourseEnrollments();
                break;
            case "5":
                system.logout();
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    /**
     * Shows available courses for the current student
     */
    private void viewAvailableCourses() {
        Student student = (Student) system.getCurrentUser();
        List<CourseSection> availableSections = system.getAvailableSections(student.getUserId());
        
        if (availableSections.isEmpty()) {
            System.out.println("\nNo available courses found.");
            System.out.println("This could be because:");
            System.out.println("- You have already completed all available courses");
            System.out.println("- You don't meet the prerequisites for remaining courses");
            System.out.println("- You have reached the maximum course limit");
            System.out.println("- All sections are full");
            return;
        }
        
        System.out.println("\n=== AVAILABLE COURSES ===");
        System.out.printf("%-12s %-35s %-8s %-6s %-15s%n", 
                         "Section ID", "Course Name", "Credits", "Spots", "Semester");
        System.out.println("-".repeat(85));
        
        for (CourseSection section : availableSections) {
            Course course = system.getCourses().get(section.getCourseCode());
            if (course != null) {
                String courseName = course.getCourseName().length() > 35 ? 
                    course.getCourseName().substring(0, 32) + "..." : 
                    course.getCourseName();
                
                System.out.printf("%-12s %-35s %-8d %-6d %-15s%n",
                                section.getSectionId(),
                                courseName,
                                course.getCredits(),
                                section.getAvailableSpots(),
                                section.getSemester());
                
                // Show prerequisites on a separate line for clarity
                if (!course.getPrerequisites().isEmpty()) {
                    String prerequisites = String.join(", ", course.getPrerequisites());
                    System.out.printf("%-12s Prerequisites: %s%n", "", prerequisites);
                }
            }
        }
        
        // Show courses student cannot take and why
        showUnavailableCourses(student);
    }
    
    /**
     * Shows courses that are unavailable and the reasons why
     */
    private void showUnavailableCourses(Student student) {
        System.out.println("\n=== COURSES YOU CANNOT REGISTER FOR ===");
        
        boolean hasUnavailable = false;
        for (CourseSection section : system.getCourseSections().values()) {
            Course course = system.getCourses().get(section.getCourseCode());
            if (course != null && !student.canEnrollInCourse(course, section)) {
                List<String> reasons = student.getEnrollmentBlockingReasons(course, section);
                if (!reasons.isEmpty()) {
                    hasUnavailable = true;
                    System.out.printf("%-12s %-35s - %s%n",
                                    section.getSectionId(),
                                    course.getCourseName().length() > 35 ? 
                                    course.getCourseName().substring(0, 32) + "..." : 
                                    course.getCourseName(),
                                    String.join(", ", reasons));
                }
            }
        }
        
        if (!hasUnavailable) {
            System.out.println("All courses are available for registration!");
        }
    }
    
    /**
     * Handles course registration request for students
     */
    private void requestCourseRegistration() {
        Student student = (Student) system.getCurrentUser();
        
        if (student.getEnrolledCourses().size() >= Student.MAX_COURSES) {
            System.out.println("\nYou have reached the maximum course limit (" + Student.MAX_COURSES + ").");
            return;
        }
        
        viewAvailableCourses();
        
        System.out.print("\nEnter the Section ID to request registration for: ");
        String sectionId = scanner.nextLine().trim().toUpperCase();
        
        // Use default reason instead of asking
        String reason = "Course registration request";
        
        CourseRegistrationSystem.RegistrationResult result = 
            system.createRegistrationRequest(student.getUserId(), sectionId, reason);
        
        if (result.isSuccess()) {
            System.out.println("\n✓ " + result.getMessage());
            System.out.println("Your advisor will review this request and approve/reject it.");
            System.out.println("You can check the status in 'View Registration Requests' menu.");
        } else {
            System.out.println("\n✗ " + result.getMessage());
        }
    }
    
    /**
     * Shows student's registration requests and their status
     */
    private void viewMyRegistrationRequests() {
        Student student = (Student) system.getCurrentUser();
        List<RegistrationRequest> requests = system.getRequestsForStudent(student.getUserId());
        
        if (requests.isEmpty()) {
            System.out.println("\nYou have no registration requests.");
            return;
        }
        
        System.out.println("\n=== MY REGISTRATION REQUESTS ===");
        
        for (RegistrationRequest request : requests) {
            System.out.println("-".repeat(60));
            System.out.println(request.getDisplayString());
        }
    }
    
    /**
     * Shows courses the current student is enrolled in
     */
    private void viewMyCourses() {
        Student student = (Student) system.getCurrentUser();
        List<CourseSection> enrolledSections = system.getStudentEnrolledSections(student.getUserId());
        
        if (enrolledSections.isEmpty()) {
            System.out.println("\nYou are not enrolled in any courses.");
            return;
        }
        
        System.out.println("\n=== MY ENROLLED COURSES ===");
        System.out.printf("%-12s %-40s %-8s %-15s%n", 
                         "Section ID", "Course Name", "Credits", "Semester");
        System.out.println("-".repeat(80));
        
        int totalCredits = 0;
        for (CourseSection section : enrolledSections) {
            Course course = system.getCourses().get(section.getCourseCode());
            if (course != null) {
                System.out.printf("%-12s %-40s %-8d %-15s%n",
                                section.getSectionId(),
                                course.getCourseName(),
                                course.getCredits(),
                                section.getSemester());
                totalCredits += course.getCredits();
            }
        }
        
        System.out.println("-".repeat(80));
        System.out.println("Total Credits: " + totalCredits);
        System.out.println("Available Enrollment Slots: " + student.getAvailableEnrollmentSlots());
    }
    
    /**
     * Handles course dropping for students
     */
    private void dropCourse() {
        Student student = (Student) system.getCurrentUser();
        List<CourseSection> enrolledSections = system.getStudentEnrolledSections(student.getUserId());
        
        if (enrolledSections.isEmpty()) {
            System.out.println("\nYou are not enrolled in any courses to drop.");
            return;
        }
        
        viewMyCourses();
        
        System.out.print("\nEnter the Section ID to drop: ");
        String sectionId = scanner.nextLine().trim().toUpperCase();
        
        CourseRegistrationSystem.RegistrationResult result = 
            system.dropStudentFromCourse(student.getUserId(), sectionId);
        
        if (result.isSuccess()) {
            System.out.println("\n✓ " + result.getMessage());
        } else {
            System.out.println("\n✗ " + result.getMessage());
        }
    }
    
    /**
     * Shows academic information for the current student using Transcript
     */
    private void viewAcademicInfo() {
        Student student = (Student) system.getCurrentUser();
        Transcript transcript = student.getTranscript();
        
        if (transcript == null || transcript.getEntries().isEmpty()) {
            // Display basic information if no transcript available
            displayBasicAcademicInfo(student);
        } else {
            // Display full transcript
            System.out.println(transcript.generateTranscriptDisplay());
            
            // Additional information
            System.out.println("\n=== CURRENT ENROLLMENT STATUS ===");
            System.out.println("Current Enrollment: " + student.getEnrolledCourses().size() + "/" + Student.MAX_COURSES + " courses");
            System.out.println("Available Enrollment Slots: " + student.getAvailableEnrollmentSlots());
            
            // Show advisor information
            if (student.getAdvisorId() != null) {
                Advisor advisor = system.getAdvisors().get(student.getAdvisorId());
                if (advisor != null) {
                    System.out.println("Academic Advisor: " + advisor.getFormattedName() + " (" + advisor.getDepartment() + ")");
                }
            }
            
            // Academic progress information
            int requiredCredits = 128; // Typical bachelor's degree requirement
            double progress = transcript.getProgressPercentage(requiredCredits);
            System.out.println(String.format("Degree Progress: %.1f%% (%d/%d credits)", 
                                           progress, transcript.getTotalCreditsCompleted(), requiredCredits));
            
            if (transcript.isEligibleForGraduation(requiredCredits)) {
                System.out.println("✓ ELIGIBLE FOR GRADUATION");
            } else {
                int remainingCredits = requiredCredits - transcript.getTotalCreditsCompleted();
                System.out.println("Remaining Credits for Graduation: " + remainingCredits);
            }
        }
    }
    
    /**
     * Displays basic academic information when transcript is not available
     * @param student The student
     */
    private void displayBasicAcademicInfo(Student student) {
        System.out.println("\n=== ACADEMIC INFORMATION ===");
        System.out.println("Student ID: " + student.getUserId());
        System.out.println("Name: " + student.getName());
        System.out.println("Academic Year: " + student.getYear());
        System.out.println("Current GPA: " + String.format("%.2f", student.getGpa()));
        System.out.println("Academic Status: " + (student.isAcademicStatusGood() ? "Good Standing" : "Academic Warning"));
        
        // Show completed courses
        if (!student.getCompletedCourses().isEmpty()) {
            System.out.println("\n--- Completed Courses ---");
            System.out.printf("%-12s %-6s %-15s%n", "Course Code", "Grade", "Semester");
            System.out.println("-".repeat(35));
            
            for (Student.CompletedCourse completed : student.getCompletedCourses()) {
                System.out.printf("%-12s %-6s %-15s%n",
                                completed.getCourseCode(),
                                completed.getGrade(),
                                completed.getSemester());
            }
        }
        
        // Show current enrollment
        System.out.println("\nCurrent Enrollment: " + student.getEnrolledCourses().size() + "/" + Student.MAX_COURSES + " courses");
        
        // Show advisor information
        if (student.getAdvisorId() != null) {
            Advisor advisor = system.getAdvisors().get(student.getAdvisorId());
            if (advisor != null) {
                System.out.println("Academic Advisor: " + advisor.getFormattedName() + " (" + advisor.getDepartment() + ")");
            }
        }
    }
    
    /**
     * Shows pending registration requests for advisor to review
     */
    private void reviewRegistrationRequests() {
        Advisor advisor = (Advisor) system.getCurrentUser();
        List<RegistrationRequest> pendingRequests = system.getPendingRequestsForAdvisor(advisor.getUserId());
        
        if (pendingRequests.isEmpty()) {
            System.out.println("\nNo pending registration requests to review.");
            return;
        }
        
        System.out.println("\n=== PENDING REGISTRATION REQUESTS ===");
        
        for (int i = 0; i < pendingRequests.size(); i++) {
            RegistrationRequest request = pendingRequests.get(i);
            System.out.println("\n" + (i + 1) + ". " + "-".repeat(50));
            System.out.println(request.getDisplayString());
            
            // Show student information
            Student student = system.getStudents().get(request.getStudentId());
            if (student != null) {
                System.out.println(String.format("Student: %s (Year %d, GPA: %.2f)", 
                                 student.getName(), student.getYear(), student.getGpa()));
                System.out.println("Current Enrollment: " + student.getEnrolledCourses().size() + "/" + Student.MAX_COURSES + " courses");
            }
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.print("Enter request number to review (or 0 to return to menu): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 0) {
                return;
            }
            
            if (choice < 1 || choice > pendingRequests.size()) {
                System.out.println("Invalid request number.");
                return;
            }
            
            RegistrationRequest selectedRequest = pendingRequests.get(choice - 1);
            reviewSingleRequest(selectedRequest, advisor);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Reviews a single registration request
     */
    private void reviewSingleRequest(RegistrationRequest request, Advisor advisor) {
        System.out.println("\n=== REVIEWING REQUEST ===");
        System.out.println(request.getDisplayString());
        
        Student student = system.getStudents().get(request.getStudentId());
        if (student != null) {
            System.out.println(String.format("Student: %s (Year %d, GPA: %.2f)", 
                             student.getName(), student.getYear(), student.getGpa()));
            
            // Show academic standing
            System.out.println("Academic Status: " + (student.isAcademicStatusGood() ? "Good Standing" : "Academic Warning"));
            
            // Show current courses
            if (!student.getEnrolledCourses().isEmpty()) {
                System.out.println("Currently Enrolled Courses:");
                for (String sectionId : student.getEnrolledCourses()) {
                    CourseSection section = system.getCourseSections().get(sectionId);
                    if (section != null) {
                        Course course = system.getCourses().get(section.getCourseCode());
                        if (course != null) {
                            System.out.println("  - " + course.getCourseCode() + ": " + course.getCourseName());
                        }
                    }
                }
            }
        }
        
        System.out.println("\n1. Approve Request");
        System.out.println("2. Reject Request");
        System.out.println("3. Back to Request List");
        System.out.print("Please select an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                approveRequest(request, advisor);
                break;
            case "2":
                rejectRequest(request, advisor);
                break;
            case "3":
                return;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    /**
     * Approves a registration request
     */
    private void approveRequest(RegistrationRequest request, Advisor advisor) {
        // Use default comment instead of asking
        String comments = "Approved";
        
        CourseRegistrationSystem.RegistrationResult result = 
            system.approveRegistrationRequest(request.getRequestId(), advisor.getUserId(), comments);
        
        if (result.isSuccess()) {
            System.out.println("\n✓ " + result.getMessage());
        } else {
            System.out.println("\n✗ " + result.getMessage());
        }
    }
    
    /**
     * Rejects a registration request
     */
    private void rejectRequest(RegistrationRequest request, Advisor advisor) {
        System.out.print("Enter reason for rejection: ");
        String reason = scanner.nextLine().trim();
        
        if (reason.isEmpty()) {
            reason = "Request rejected";
        }
        
        CourseRegistrationSystem.RegistrationResult result = 
            system.rejectRegistrationRequest(request.getRequestId(), advisor.getUserId(), reason);
        
        if (result.isSuccess()) {
            System.out.println("\n✓ " + result.getMessage());
        } else {
            System.out.println("\n✗ " + result.getMessage());
        }
    }
    
    /**
     * Shows advisees for the current advisor
     */
    private void viewAdvisees() {
        Advisor advisor = (Advisor) system.getCurrentUser();
        List<String> adviseeIds = advisor.getAdviseeList();
        
        if (adviseeIds.isEmpty()) {
            System.out.println("\nYou have no assigned advisees.");
            return;
        }
        
        System.out.println("\n=== MY ADVISEES ===");
        System.out.printf("%-12s %-25s %-5s %-6s %-10s%n", 
                         "Student ID", "Name", "Year", "GPA", "Courses");
        System.out.println("-".repeat(65));
        
        for (String studentId : adviseeIds) {
            Student student = system.getStudents().get(studentId);
            if (student != null) {
                System.out.printf("%-12s %-25s %-5d %-6.2f %-10d%n",
                                student.getUserId(),
                                student.getName(),
                                student.getYear(),
                                student.getGpa(),
                                student.getEnrolledCourses().size());
            }
        }
        
        System.out.println("\nTotal Advisees: " + adviseeIds.size());
    }
    
    /**
     * Shows all available courses for advisors
     */
    private void viewAllCourses() {
        System.out.println("\n=== ALL COURSES ===");
        System.out.printf("%-12s %-40s %-8s %-5s %-20s%n", 
                         "Course Code", "Course Name", "Credits", "Year", "Prerequisites");
        System.out.println("-".repeat(90));
        
        for (Course course : system.getCourses().values()) {
            String prerequisites = course.getPrerequisites().isEmpty() ? 
                "None" : String.join(", ", course.getPrerequisites());
            
            System.out.printf("%-12s %-40s %-8d %-5d %-20s%n",
                            course.getCourseCode(),
                            course.getCourseName(),
                            course.getCredits(),
                            course.getYear(),
                            prerequisites.length() > 20 ? prerequisites.substring(0, 17) + "..." : prerequisites);
        }
    }
    
    /**
     * Shows course enrollment statistics for advisors
     */
    private void viewCourseEnrollments() {
        System.out.println("\n=== COURSE ENROLLMENT STATISTICS ===");
        System.out.printf("%-12s %-40s %-12s %-12s%n", 
                         "Section ID", "Course Name", "Enrolled", "Capacity %");
        System.out.println("-".repeat(80));
        
        for (CourseSection section : system.getCourseSections().values()) {
            Course course = system.getCourses().get(section.getCourseCode());
            if (course != null) {
                String enrollmentInfo = section.getCurrentEnrollment() + "/" + section.getCapacity();
                String courseName = course.getCourseName().length() > 40 ? 
                    course.getCourseName().substring(0, 37) + "..." : 
                    course.getCourseName();
                
                System.out.printf("%-12s %-40s %-12s %-12.1f%%%n",
                                section.getSectionId(),
                                courseName,
                                enrollmentInfo,
                                section.getEnrollmentPercentage());
            }
        }
    }
}