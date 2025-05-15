// src/test/java/com/yourusername/library/repository/InMemoryMemberRepositoryTest.java
package com.prpcena.library.repository; // Adjust package name

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.prpcena.library.model.Member;

class InMemoryMemberRepositoryTest {
    private MemberRepository memberRepository;
    private Member member1;
    private Member member2; // Used for findAll and distinct member scenarios

    @BeforeEach
    void setUp() {
        memberRepository = new InMemoryMemberRepository();
        member1 = new Member("Alice Wonderland", "alice@example.com");
        // Create a second distinct member for tests that involve multiple members
        member2 = new Member("Bob The Builder", "bob@example.com");
    }

    @Test
    void save_NewMember_ShouldAddMember() {
        Member savedMember = memberRepository.save(member1);
        assertNotNull(savedMember);
        assertEquals(member1.getMemberId(), savedMember.getMemberId());
        assertEquals(1, memberRepository.findAll().size());
    }

    @Test
    void save_ExistingMember_ShouldUpdateMember() {
        memberRepository.save(member1); // Save initial version
        member1.setName("Alice In Chains"); // Modify the original member object
        Member updatedMember = memberRepository.save(member1); // Save again with the same ID

        assertNotNull(updatedMember);
        assertEquals(member1.getMemberId(), updatedMember.getMemberId());
        assertEquals("Alice In Chains", updatedMember.getName());
        assertEquals(1, memberRepository.findAll().size()); // Should still be 1 member
    }

    @Test
    void save_NullMember_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> memberRepository.save(null));
    }

    @Test
    void findById_ExistingId_ShouldReturnMember() {
        memberRepository.save(member1);
        Optional<Member> foundMember = memberRepository.findById(member1.getMemberId());
        assertTrue(foundMember.isPresent());
        assertEquals(member1.getName(), foundMember.get().getName());
    }

    @Test
    void findById_NonExistingId_ShouldReturnEmptyOptional() {
        Optional<Member> foundMember = memberRepository.findById("NONEXISTENT_ID");
        assertFalse(foundMember.isPresent());
    }

    @Test
    void findById_NullId_ShouldReturnEmptyOptional() {
        Optional<Member> foundMember = memberRepository.findById(null);
        assertFalse(foundMember.isPresent());
    }

    @Test
    void findAll_WhenMembersExist_ShouldReturnAllMembers() {
        memberRepository.save(member1);
        memberRepository.save(member2); // Save a second, distinct member
        List<Member> members = memberRepository.findAll();
        assertEquals(2, members.size());
        // Check if both member1 and member2 are in the list
        // This relies on Member.equals() being based on memberId
        assertTrue(members.stream().anyMatch(m -> m.getMemberId().equals(member1.getMemberId())));
        assertTrue(members.stream().anyMatch(m -> m.getMemberId().equals(member2.getMemberId())));
    }

    @Test
    void findAll_WhenNoMembersExist_ShouldReturnEmptyList() {
        List<Member> members = memberRepository.findAll();
        assertTrue(members.isEmpty());
    }

    @Test
    void deleteById_ExistingId_ShouldDeleteMemberAndReturnTrue() {
        memberRepository.save(member1);
        assertTrue(memberRepository.deleteById(member1.getMemberId()));
        assertFalse(memberRepository.findById(member1.getMemberId()).isPresent());
        assertEquals(0, memberRepository.findAll().size());
    }

    @Test
    void deleteById_NonExistingId_ShouldReturnFalse() {
        assertFalse(memberRepository.deleteById("NONEXISTENT_ID"));
    }

    @Test
    void deleteById_NullId_ShouldReturnFalse() {
        assertFalse(memberRepository.deleteById(null));
    }
}