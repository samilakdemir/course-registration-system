/**
 * Abstract base class for all users in the course registration system.
 * Provides common functionality for authentication and basic user information.
 */
public abstract class Person {
    protected String userId;
    protected String name;
    protected String username;
    protected String password;
    
    /**
     * Constructor for Person
     * @param userId Unique identifier for the person
     * @param name Full name of the person
     * @param username Login username
     * @param password Login password
     */
    public Person(String userId, String name, String username, String password) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
    }
    
    /**
     * Default constructor for JSON deserialization
     */
    public Person() {}
    
    /**
     * Authenticates user with provided credentials
     * @param username Provided username
     * @param password Provided password
     * @return true if credentials match, false otherwise
     */
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
    
    /**
     * Gets the user type (to be implemented by subclasses)
     * @return String representing the user type
     */
    public abstract String getUserType();
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return String.format("Person{userId='%s', name='%s', username='%s'}", 
                           userId, name, username);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return userId != null ? userId.equals(person.userId) : person.userId == null;
    }
    
    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}