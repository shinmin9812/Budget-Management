package com.wanted.teamV.controller;

import com.wanted.teamV.component.AuthenticationPrincipal;
import com.wanted.teamV.dto.LoginMember;
import com.wanted.teamV.dto.req.SpendCreateReqDto;
import com.wanted.teamV.dto.req.SpendUpdateReqDto;
import com.wanted.teamV.dto.res.SpendInfoResDto;
import com.wanted.teamV.dto.res.SpendListResDto;
import com.wanted.teamV.service.SpendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spends")
public class SpendController {

    private final SpendService spendService;

    @PostMapping
    public ResponseEntity<SpendInfoResDto> createSpend(
            @AuthenticationPrincipal LoginMember loginMember,
            @RequestBody SpendCreateReqDto request
    ) {
        SpendInfoResDto response = spendService.createSpend(loginMember.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{spendId}")
    public ResponseEntity<SpendInfoResDto> updateSpend(
            @AuthenticationPrincipal LoginMember loginMember,
            @PathVariable Long spendId,
            @RequestBody SpendUpdateReqDto request
    ) {
        SpendInfoResDto response = spendService.updateSpend(loginMember.id(), spendId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/spend/{spendId}")
    public ResponseEntity<SpendInfoResDto> getSpendDetail(
            @AuthenticationPrincipal LoginMember loginMember,
            @PathVariable Long spendId
    ) {
        SpendInfoResDto response = spendService.getSpendDetail(loginMember.id(), spendId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SpendListResDto> getSpends(
            @AuthenticationPrincipal LoginMember loginMember,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "min", required = false) Integer min,
            @RequestParam(value = "max", required = false) Integer max
    ) {
        SpendListResDto response = spendService.getAllSpends(loginMember.id(), startDate, endDate, categoryId, min, max);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{spendId}")
    public ResponseEntity<String> deleteSpend(
            @AuthenticationPrincipal LoginMember loginMember,
            @PathVariable Long spendId
    ) {
        spendService.deleteSpend(loginMember.id(), spendId);
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getExcludingTotal(
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        Double result = spendService.getExcludingTotal(loginMember.id());
        return ResponseEntity.ok(result);
    }
}
