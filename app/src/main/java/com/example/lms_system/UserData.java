package com.example.lms_system;

public class UserData {
    String Name,Email,IdNumber;

    public UserData(){}

    public UserData(String name, String email, String idNumber) {
        Name = name;
        Email = email;
        IdNumber = idNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getIdNumber() {
        return IdNumber;
    }

    public void setIdNumber(String idNumber) {
        IdNumber = idNumber;
    }
}
