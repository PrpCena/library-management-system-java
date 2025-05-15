package com.prpcena.library.model; // Adjust package name

import java.util.Objects;
import java.util.UUID; // For generating a unique member ID

/**
 * Represents a library member.
 * Member ID is immutable once generated. Other details can be updated.
 */
public class Member {
    private final String memberId; // Immutable unique ID
    private String name;
    private String contactInfo; // e.g., email or phone

    public Member(String name, String contactInfo) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be null or empty.");
        }
        this.memberId = UUID.randomUUID().toString(); // Generate a unique ID
        this.name = name;
        this.contactInfo = contactInfo;
    }

    // Getters
    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    // Setters for mutable fields
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be null or empty.");
        }
        this.name = name;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId='" + memberId + '\'' +
                ", name='" + name + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                '}';
    }

    // MemberId is the unique identifier
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Member member = (Member) o;
        return Objects.equals(memberId, member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }
}