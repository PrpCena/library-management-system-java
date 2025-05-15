// src/main/java/com/yourusername/library/repository/InMemoryMemberRepository.java
package com.prpcena.library.repository; // Adjust package name

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prpcena.library.model.Member;

public class InMemoryMemberRepository implements MemberRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryMemberRepository.class);
    private final Map<String, Member> members = new LinkedHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Member save(Member member) {
        if (member == null || member.getMemberId() == null || member.getMemberId().trim().isEmpty()) {
            logger.error("Attempted to save a null member or member with null/empty ID.");
            throw new IllegalArgumentException("Member and Member ID cannot be null or empty.");
        }
        lock.writeLock().lock();
        try {
            members.put(member.getMemberId(), member);
            logger.info("Saved/Updated member with ID: {}", member.getMemberId());
            return member;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Member> findById(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            logger.warn("Attempted to find member with null or empty ID.");
            return Optional.empty();
        }
        lock.readLock().lock();
        try {
            Member member = members.get(memberId);
            if (member != null) {
                logger.debug("Found member with ID: {}", memberId);
            } else {
                logger.debug("No member found with ID: {}", memberId);
            }
            return Optional.ofNullable(member);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Member> findAll() {
        lock.readLock().lock();
        try {
            logger.debug("Retrieving all members. Total count: {}", members.size());
            return new ArrayList<>(members.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean deleteById(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            logger.warn("Attempted to delete member with null or empty ID.");
            return false;
        }
        lock.writeLock().lock();
        try {
            Member removedMember = members.remove(memberId);
            if (removedMember != null) {
                logger.info("Deleted member with ID: {}", memberId);
                return true;
            } else {
                logger.info("No member found with ID {} to delete.", memberId);
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}