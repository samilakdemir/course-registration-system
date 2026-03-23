import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an academic transcript for a student.
 * Manages completed courses, GPA calculation, and academic progress tracking.
 */
public class Transcript {
    private String studentId;
    private List<TranscriptEntry> entries;
    private double cumulativeGPA;
    private int totalCreditsCompleted;
    private int totalCreditsAttempted;
    private String academicStanding;
    
    // Grade point values
    private static final Map<String, Double> GRADE_POINTS = new HashMap<>();
    static {
        GRADE_POINTS.put("AA", 4.0);
        GRADE_POINTS.put("BA", 3.5);
        GRADE_POINTS.put("BB", 3.0);
        GRADE_POINTS.put("CB", 2.5);
        GRADE_POINTS.put("CC", 2.0);
        GRADE_POINTS.put("DC", 1.5);
        GRADE_POINTS.put("DD", 1.0);
        GRADE_POINTS.put("FF", 0.0);
        GRADE_POINTS.put("FD", 0.0);
        GRADE_POINTS.put("NA", 0.0);
    }
    
    /**
     * Constructor for Transcript
     * @param studentId ID of the student this transcript belongs to
     */
    public Transcript(String studentId) {
        this.studentId = studentId;
        this.entries = new ArrayList<>();
        this.cumulativeGPA = 0.0;
        this.totalCreditsCompleted = 0;
        this.totalCreditsAttempted = 0;
        this.academicStanding = "Good Standing";
    }
    
    /**
     * Default constructor for JSON deserialization
     */
    public Transcript() {
        this.entries = new ArrayList<>();
    }
    
    /**
     * Adds a course entry to the transcript
     * @param courseCode Course code
     * @param courseName Course name
     * @param credits Number of credits
     * @param grade Letter grade received
     * @param semester Semester taken
     * @param year Academic year
     */
    public void addEntry(String courseCode, String courseName, int credits, 
                        String grade, String semester, int year) {
        TranscriptEntry entry = new TranscriptEntry(courseCode, courseName, 
                                                   credits, grade, semester, year);
        entries.add(entry);
        recalculateGPA();
    }
    
    /**
     * Adds a transcript entry directly
     * @param entry TranscriptEntry to add
     */
    public void addEntry(TranscriptEntry entry) {
        entries.add(entry);
        recalculateGPA();
    }
    
    /**
     * Recalculates the cumulative GPA based on all entries
     */
    private void recalculateGPA() {
        double totalGradePoints = 0.0;
        totalCreditsAttempted = 0;
        totalCreditsCompleted = 0;
        
        for (TranscriptEntry entry : entries) {
            int credits = entry.getCredits();
            String grade = entry.getGrade();
            
            totalCreditsAttempted += credits;
            
            if (GRADE_POINTS.containsKey(grade)) {
                double gradePoint = GRADE_POINTS.get(grade);
                totalGradePoints += gradePoint * credits;
                
                // Count as completed if passing grade (not FF, FD, or NA)
                if (!grade.equals("FF") && !grade.equals("FD") && !grade.equals("NA")) {
                    totalCreditsCompleted += credits;
                }
            }
        }
        
        cumulativeGPA = totalCreditsAttempted > 0 ? totalGradePoints / totalCreditsAttempted : 0.0;
        updateAcademicStanding();
    }
    
    /**
     * Updates academic standing based on GPA and credit progress
     */
    private void updateAcademicStanding() {
        if (cumulativeGPA >= 3.5) {
            academicStanding = "Dean's List";
        } else if (cumulativeGPA >= 3.0) {
            academicStanding = "Good Standing";
        } else if (cumulativeGPA >= 2.0) {
            academicStanding = "Satisfactory";
        } else if (cumulativeGPA >= 1.5) {
            academicStanding = "Academic Warning";
        } else {
            academicStanding = "Academic Probation";
        }
    }
    
    /**
     * Gets entries for a specific semester
     * @param semester Semester to filter by
     * @param year Academic year to filter by
     * @return List of transcript entries for the specified semester
     */
    public List<TranscriptEntry> getEntriesBySemester(String semester, int year) {
        List<TranscriptEntry> semesterEntries = new ArrayList<>();
        for (TranscriptEntry entry : entries) {
            if (entry.getSemester().equals(semester) && entry.getYear() == year) {
                semesterEntries.add(entry);
            }
        }
        return semesterEntries;
    }
    
    /**
     * Gets all completed course codes (for prerequisite checking)
     * Only includes courses that were passed (not failed)
     * @return List of completed course codes
     */
    public List<String> getCompletedCourseCodes() {
        List<String> completedCodes = new ArrayList<>();
        
        // Use a map to track the highest grade for each course (in case of retakes)
        Map<String, String> courseGrades = new HashMap<>();
        
        for (TranscriptEntry entry : entries) {
            String courseCode = entry.getCourseCode();
            String grade = entry.getGrade();
            
            // Update if this is a better grade or first attempt
            if (!courseGrades.containsKey(courseCode) || 
                isHigherGrade(grade, courseGrades.get(courseCode))) {
                courseGrades.put(courseCode, grade);
            }
        }
        
        // Only include courses with passing grades
        for (Map.Entry<String, String> courseGrade : courseGrades.entrySet()) {
            String grade = courseGrade.getValue();
            if (isPassingGrade(grade)) {
                completedCodes.add(courseGrade.getKey());
            }
        }
        
        return completedCodes;
    }
    
    /**
     * Checks if a grade is considered passing
     * @param grade The letter grade
     * @return true if the grade is passing
     */
    private boolean isPassingGrade(String grade) {
        return !grade.equals("FF") && !grade.equals("FD") && !grade.equals("NA") && 
               GRADE_POINTS.getOrDefault(grade, 0.0) >= 1.0; // DD and above are passing
    }
    
    /**
     * Compares two grades to determine which is higher
     * @param grade1 First grade
     * @param grade2 Second grade
     * @return true if grade1 is higher than grade2
     */
    private boolean isHigherGrade(String grade1, String grade2) {
        double points1 = GRADE_POINTS.getOrDefault(grade1, 0.0);
        double points2 = GRADE_POINTS.getOrDefault(grade2, 0.0);
        return points1 > points2;
    }
    
    /**
     * Gets the best grade received for a specific course
     * @param courseCode Course code to check
     * @return Best grade received, or null if course not taken
     */
    public String getBestGradeForCourse(String courseCode) {
        String bestGrade = null;
        double bestPoints = -1.0;
        
        for (TranscriptEntry entry : entries) {
            if (entry.getCourseCode().equals(courseCode)) {
                double points = GRADE_POINTS.getOrDefault(entry.getGrade(), 0.0);
                if (points > bestPoints) {
                    bestPoints = points;
                    bestGrade = entry.getGrade();
                }
            }
        }
        
        return bestGrade;
    }
    
    /**
     * Checks if a student has attempted a course (regardless of grade)
     * @param courseCode Course code to check
     * @return true if course has been attempted
     */
    public boolean hasAttemptedCourse(String courseCode) {
        return entries.stream().anyMatch(entry -> entry.getCourseCode().equals(courseCode));
    }
    
    /**
     * Gets number of attempts for a specific course
     * @param courseCode Course code to check
     * @return Number of times the course was attempted
     */
    public int getCourseAttempts(String courseCode) {
        return (int) entries.stream().filter(entry -> entry.getCourseCode().equals(courseCode)).count();
    }
    
    /**
     * Calculates semester GPA for a specific semester
     * @param semester Semester to calculate GPA for
     * @param year Academic year
     * @return GPA for the specified semester
     */
    public double getSemesterGPA(String semester, int year) {
        List<TranscriptEntry> semesterEntries = getEntriesBySemester(semester, year);
        if (semesterEntries.isEmpty()) {
            return 0.0;
        }
        
        double totalGradePoints = 0.0;
        int totalCredits = 0;
        
        for (TranscriptEntry entry : semesterEntries) {
            int credits = entry.getCredits();
            String grade = entry.getGrade();
            
            if (GRADE_POINTS.containsKey(grade)) {
                totalGradePoints += GRADE_POINTS.get(grade) * credits;
                totalCredits += credits;
            }
        }
        
        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }
    
    /**
     * Gets the grade point value for a letter grade
     * @param grade Letter grade
     * @return Grade point value
     */
    public static double getGradePoint(String grade) {
        return GRADE_POINTS.getOrDefault(grade, 0.0);
    }
    
    /**
     * Checks if student is eligible for graduation
     * @param requiredCredits Total credits required for graduation
     * @return true if eligible for graduation
     */
    public boolean isEligibleForGraduation(int requiredCredits) {
        return totalCreditsCompleted >= requiredCredits && cumulativeGPA >= 2.0;
    }
    
    /**
     * Gets academic progress as percentage
     * @param requiredCredits Total credits required for graduation
     * @return Progress percentage (0.0 to 100.0)
     */
    public double getProgressPercentage(int requiredCredits) {
        if (requiredCredits <= 0) return 0.0;
        return Math.min(100.0, (double) totalCreditsCompleted / requiredCredits * 100.0);
    }
    
    /**
     * Generates a formatted transcript display
     * @return Formatted transcript string
     */
    public String generateTranscriptDisplay() {
        StringBuilder transcript = new StringBuilder();
        transcript.append("═══════════════════════════════════════════════════════════════\n");
        transcript.append("                        ACADEMIC TRANSCRIPT                       \n");
        transcript.append("═══════════════════════════════════════════════════════════════\n");
        transcript.append(String.format("Student ID: %s\n", studentId));
        transcript.append(String.format("Cumulative GPA: %.2f\n", cumulativeGPA));
        transcript.append(String.format("Academic Standing: %s\n", academicStanding));
        transcript.append(String.format("Credits Completed: %d\n", totalCreditsCompleted));
        transcript.append(String.format("Credits Attempted: %d\n", totalCreditsAttempted));
        transcript.append("═══════════════════════════════════════════════════════════════\n");
        
        // Group entries by semester and year
        Map<String, List<TranscriptEntry>> semesterGroups = new HashMap<>();
        for (TranscriptEntry entry : entries) {
            String semesterKey = entry.getSemester() + " " + entry.getYear();
            semesterGroups.computeIfAbsent(semesterKey, k -> new ArrayList<>()).add(entry);
        }
        
        // Display each semester
        for (String semesterKey : semesterGroups.keySet()) {
            List<TranscriptEntry> semesterEntries = semesterGroups.get(semesterKey);
            if (!semesterEntries.isEmpty()) {
                TranscriptEntry firstEntry = semesterEntries.get(0);
                double semesterGPA = getSemesterGPA(firstEntry.getSemester(), firstEntry.getYear());
                
                transcript.append(String.format("\n%s (GPA: %.2f)\n", semesterKey, semesterGPA));
                transcript.append("───────────────────────────────────────────────────────────────\n");
                transcript.append(String.format("%-12s %-35s %-7s %-5s\n", 
                                               "Course", "Title", "Credits", "Grade"));
                transcript.append("───────────────────────────────────────────────────────────────\n");
                
                for (TranscriptEntry entry : semesterEntries) {
                    transcript.append(String.format("%-12s %-35s %-7d %-5s\n",
                                                   entry.getCourseCode(),
                                                   entry.getCourseName().length() > 35 ? 
                                                   entry.getCourseName().substring(0, 32) + "..." : 
                                                   entry.getCourseName(),
                                                   entry.getCredits(),
                                                   entry.getGrade()));
                }
            }
        }
        
        transcript.append("═══════════════════════════════════════════════════════════════\n");
        return transcript.toString();
    }
    
    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public List<TranscriptEntry> getEntries() {
        return new ArrayList<>(entries);
    }
    
    public void setEntries(List<TranscriptEntry> entries) {
        this.entries = entries != null ? new ArrayList<>(entries) : new ArrayList<>();
        recalculateGPA();
    }
    
    public double getCumulativeGPA() {
        return cumulativeGPA;
    }
    
    public int getTotalCreditsCompleted() {
        return totalCreditsCompleted;
    }
    
    public int getTotalCreditsAttempted() {
        return totalCreditsAttempted;
    }
    
    public String getAcademicStanding() {
        return academicStanding;
    }
    
    @Override
    public String toString() {
        return String.format("Transcript{studentId='%s', gpa=%.2f, credits=%d/%d, standing='%s'}", 
                           studentId, cumulativeGPA, totalCreditsCompleted, 
                           totalCreditsAttempted, academicStanding);
    }
    
    /**
     * Inner class representing a single transcript entry
     */
    public static class TranscriptEntry {
        private String courseCode;
        private String courseName;
        private int credits;
        private String grade;
        private String semester;
        private int year;
        
        public TranscriptEntry(String courseCode, String courseName, int credits, 
                              String grade, String semester, int year) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.credits = credits;
            this.grade = grade;
            this.semester = semester;
            this.year = year;
        }
        
        public TranscriptEntry() {}
        
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
        
        public int getYear() {
            return year;
        }
        
        public void setYear(int year) {
            this.year = year;
        }
        
        @Override
        public String toString() {
            return String.format("TranscriptEntry{courseCode='%s', courseName='%s', credits=%d, grade='%s', semester='%s %d'}", 
                               courseCode, courseName, credits, grade, semester, year);
        }
    }
}