package com.hospital.model;

public class User {
   
    private String username;

    private String password;

    private String role;

    private int linkedId;
    private AccountStatus status;

    public User() { 


    }

    public User(String username, String password, String role, int linkedId) {
        
        this(username, password, role, linkedId, AccountStatus.ACTIVE);
    }

    public User(String username, String password, String role, int linkedId, AccountStatus status) {
      
        this.username = username;
        this.password = password;
        this.role = role;
        this.linkedId = linkedId;
        this.status = status;
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

    public String getRole() {
         return role; 
    }

    public void setRole(String role) {
         this.role = role; 
     }

    public int getLinkedId() { 
        return linkedId;
    }

    public void setLinkedId(int linkedId) { 
        this.linkedId = linkedId; 
    }

    public AccountStatus getStatus() { 
        return status;
    }
    public void setStatus(AccountStatus status) 
    {
         this.status = status;
        
    }

    public String getStatusName() { 
        return status == null ? null : status.name(); 
    }

}
