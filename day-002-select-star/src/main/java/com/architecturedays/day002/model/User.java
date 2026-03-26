package com.architecturedays.day002.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String photoBase64;

    @Column(length = 5000)
    private String bio;

    public User() {}

    public User(String email, String name, String photoBase64, String bio) {
        this.email = email;
        this.name = name;
        this.photoBase64 = photoBase64;
        this.bio = bio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhotoBase64() { return photoBase64; }
    public void setPhotoBase64(String photoBase64) { this.photoBase64 = photoBase64; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
