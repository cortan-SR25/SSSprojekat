package org.example.models;

import java.util.Date;

public class Trainer extends User{
    public String name;
    public String surname;
    public Date dateOfBirth;
    public String CV;
    public boolean licenseApproved;
    public Gender gender;

    public Trainer() {
    }

    public Trainer(int id, String username, String password, String name, String surname, Date dateOfBirth, String CV, boolean licenseApproved, Gender gender) {
        super(id, username, password);
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.CV = CV;
        this.licenseApproved = licenseApproved;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCV() {
        return CV;
    }

    public void setCV(String CV) {
        this.CV = CV;
    }

    public boolean isLicenseApproved() {
        return licenseApproved;
    }

    public void setLicenseApproved(boolean licenseApproved) {
        this.licenseApproved = licenseApproved;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
