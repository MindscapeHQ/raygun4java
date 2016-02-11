package com.mindscapehq.raygun4java.core.messages;

public class RaygunIdentifier
{
  private Boolean isAnonymous;

  private String email;

  private String fullName;

  private String firstName;

  private String uuid;

  private String identifier;

  /**
   * Set the current user's info to be transmitted - any parameters can be null if the data is not available or
   * you do not wish to send it.
   * @param firstName The user's first name
   * @param fullName The user's full name - if setting the first name you should set this too
   * @param emailAddress User's email address
   * @param uuid Device identifier - if this is null we will attempt to generate it automatically (legacy behavior).
   * @param isAnonymous Whether this user data represents an anonymous user
   * @param uniqueUserIdentifier Unique identifier for this user. Set this to the internal identifier you use to look up users,
   *                   or a correlation ID for anonymous users if you have one. It doesn't have to be unique, but we will treat
   *                   any duplicated values as the same user. If you use their email address here, pass it in as the 'emailAddress' parameter too.
   */
  public RaygunIdentifier(String uniqueUserIdentifier, String firstName, String fullName, String emailAddress, String uuid, Boolean isAnonymous)
  {
    this.setFirstName(firstName);
    this.setFullName(fullName);
    this.setEmail(emailAddress);
    this.setUuid(uuid);
    this.setIsAnonymous(isAnonymous);
    this.setIdentifier(uniqueUserIdentifier);
  }

  /**
   * Creates a representation of the current user info by specifying just their unique identifier. This can be an email address
   * or an internal unique ID. If it is an email address you should also set Email. All other properties are optional
   * with this overload/
   * @param uniqueUserIdentifier The email address or unique ID representing the current user.
   */
  public RaygunIdentifier(String uniqueUserIdentifier)
  {
    this.setIdentifier(uniqueUserIdentifier);
  }

  public Boolean getIsAnonymous() {
    return isAnonymous;
  }
	
  public void setIsAnonymous(Boolean isAnonymous) {
    this.isAnonymous = isAnonymous;
  }

  public String getEmail() {
    return email;
  }
	
  public void setEmail(String email) {
    this.email = email;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }  
}
