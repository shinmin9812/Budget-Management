package com.wanted.teamV.controller;

import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.res.MemberTokenResDto;
import com.wanted.teamV.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping()
    public ResponseEntity<String> join(
            @Valid @RequestBody MemberJoinReqDto memberJoinReqDto
    ) {
        memberService.join(memberJoinReqDto);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/login")
    public ResponseEntity<MemberTokenResDto> login(
            @RequestBody MemberLoginReqDto memberLoginReqDto
    ) {
        MemberTokenResDto memberTokenResDto = memberService.login(memberLoginReqDto);
        return ResponseEntity.ok(memberTokenResDto);
    }
}
