package com.wanted.teamV.controller;

import com.wanted.teamV.component.AuthenticationPrincipal;
import com.wanted.teamV.dto.LoginMember;
import com.wanted.teamV.dto.req.BudgetUpdateReqDto;
import com.wanted.teamV.dto.res.BudgetInfoResDto;
import com.wanted.teamV.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @PutMapping
    public ResponseEntity<BudgetInfoResDto> updateBudget(
            @AuthenticationPrincipal LoginMember loginMember,
            @RequestBody BudgetUpdateReqDto request
    ) {
        BudgetInfoResDto response = budgetService.updateBudget(request.categoryId(), loginMember.id(), request.budget());
        return ResponseEntity.ok(response);
    }
}
