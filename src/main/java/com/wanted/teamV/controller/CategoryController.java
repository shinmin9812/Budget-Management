package com.wanted.teamV.controller;

import com.wanted.teamV.component.AuthenticationPrincipal;
import com.wanted.teamV.dto.LoginMember;
import com.wanted.teamV.entity.Category;
import com.wanted.teamV.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping()
    public ResponseEntity<List<Category>> getCategories(
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }
}
