// src/main/java/com/yourusername/library/repository/MemberRepository.java
package com.prpcena.library.repository; // Adjust package name

import java.util.List;
import java.util.Optional;

import com.prpcena.library.model.Member;

public interface MemberRepository {
    /**
     * Saves a new member or updates an existing one.
     * 
     * @param member The member to save.
     * @return The saved member.
     */
    Member save(Member member);

    /**
     * Finds a member by their ID.
     * 
     * @param memberId The ID of the member to find.
     * @return An Optional containing the member if found, or an empty Optional
     *         otherwise.
     */
    Optional<Member> findById(String memberId);

    /**
     * Retrieves all members.
     * 
     * @return A list of all members. If no members exist, an empty list is
     *         returned.
     */
    List<Member> findAll();

    /**
     * Deletes a member by their ID.
     * 
     * @param memberId The ID of the member to delete.
     * @return true if a member was deleted, false otherwise.
     */
    boolean deleteById(String memberId);

    // Optional: Add methods like findByName if needed later
}