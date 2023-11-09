package com.wanted.teamV.service.impl;

import com.wanted.teamV.component.AuthTokenCreator;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.res.MemberTokenResDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenCreator authTokenCreator;

    @Override
    public ResponseEntity<Void> join(MemberJoinReqDto memberJoinReqDto) {
        validateUniqueAccount(memberJoinReqDto.account());

        String encryptedPassword = passwordEncoder.encode(memberJoinReqDto.password());

        Member member = Member.builder()
                .account(memberJoinReqDto.account())
                .password(encryptedPassword)
                .build();

        memberRepository.save(member);

        return ResponseEntity.ok().build();
    }

    @Override
    public MemberTokenResDto login(MemberLoginReqDto memberLoginReqDto) {
        Member member = memberRepository.getByAccount(memberLoginReqDto.account());

        if (!passwordEncoder.matches(memberLoginReqDto.password(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = authTokenCreator.createAuthToken(member.getId());
        return new MemberTokenResDto(accessToken);
    }

    @Override
    public Long extractUserId(String accessToken) {
        return authTokenCreator.extractPayload(accessToken);
    }

    private void validateUniqueAccount(String account) {
        if (memberRepository.existsByAccount(account)) {
            throw new CustomException(ErrorCode.DUPLICATE_ACCOUNT);
        }
    }
}
