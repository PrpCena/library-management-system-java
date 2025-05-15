package com.prpcena.library.model; // Adjust package name

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class MemberTest {
    @Test
    void testMemberCreation_Valid() {
        Member member = new Member("John Doe", "john.doe@example.com");
        assertNotNull(member.getMemberId(), "Member ID should be generated.");
        assertEquals("John Doe", member.getName());
        assertEquals("john.doe@example.com", member.getContactInfo());
    }

    @Test
    void testMemberCreation_NullName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Member(null, "contact"));
    }

    @Test
    void testSetName_Valid() {
        Member member = new Member("Initial Name", "contact");
        member.setName("Updated Name");
        assertEquals("Updated Name", member.getName());
    }

    @Test
    void testSetName_Null_ThrowsException() {
        Member member = new Member("Initial Name", "contact");
        assertThrows(IllegalArgumentException.class, () -> member.setName(null));
    }

    @Test
    void testEqualsAndHashCode_BasedOnMemberId() {
        // Note: Because memberId is randomly generated, we can't easily create two
        // "equal" new members.
        // This test primarily checks that a member is equal to itself and not null.
        // True equality testing for distinct but "same" members would require setting
        // ID or a different strategy.
        Member member1 = new Member("Alice", "alice@example.com");
        Member member2 = new Member("Bob", "bob@example.com"); // Will have a different ID

        assertEquals(member1, member1, "A member should be equal to itself.");
        assertNotEquals(member1, member2,
                "Two newly created distinct members should not be equal due to different IDs.");
        assertNotNull(member1.hashCode());
        assertNotEquals(member1.hashCode(), member2.hashCode());
    }
}