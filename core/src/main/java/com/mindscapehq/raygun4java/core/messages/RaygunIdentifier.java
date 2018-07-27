package com.mindscapehq.raygun4java.core.messages;

public class RaygunIdentifier {
    private Boolean isAnonymous;

    private String email;

    private String fullName;

    private String firstName;

    private String uuid;

    private String identifier;

    /**
     * Creates a representation of the current user info by specifying just their unique identifier. This can be an email address
     * or an internal unique ID. If it is an email address you should also set Email. All other properties are optional
     * with this overload/
     *
     * @param uniqueUserIdentifier The email address or unique ID representing the current user.
     */
    public RaygunIdentifier(String uniqueUserIdentifier) {
        this.withIdentifier(uniqueUserIdentifier);
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    /**
     * @param isAnonymous Whether this user data represents an anonymous user
     * @return this
     */
    public RaygunIdentifier withAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
        return this;
    }

    public String getEmail() {
        return email;
    }

    /**
     * @param email User's email address
     * @return this
     */
    public RaygunIdentifier withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    /**
     *
     * @param fullName  The user's full name - if setting the first name you should set this too
     * @return
     */
    public RaygunIdentifier withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName The user's first name
     * @return this
     */
    public RaygunIdentifier withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid Device identifier - if this is null we will attempt to generate it automatically (legacy behavior).
     * @return this
     */
    public RaygunIdentifier withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier Unique identifier for this user. Set this to the internal identifier you use to look up users,
     *                   or a correlation ID for anonymous users if you have one. It doesn't have to be unique, but we will treat
     *                   any duplicated values as the same user. If you use their email address here, pass it in as the 'emailAddress' parameter too.
     * @return this
     */
    public RaygunIdentifier withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }
}
