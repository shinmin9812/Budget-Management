package com.wanted.teamV.repository;

import com.wanted.teamV.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByAccount(String account);

    Optional<Member> findByAccount(String account);

    default Member getByAccount(String account) {
        return findByAccount(account).orElseThrow();
    }
}
