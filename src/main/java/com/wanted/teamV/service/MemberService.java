package com.wanted.teamV.service;

import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.res.MemberTokenResDto;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity<?> join(MemberJoinReqDto memberJoinReqDto);

    MemberTokenResDto login(MemberLoginReqDto memberLoginReqDto);

    Long extractUserId(String accessToken);
}
