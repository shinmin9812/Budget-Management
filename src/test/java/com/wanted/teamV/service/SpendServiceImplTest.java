package com.wanted.teamV.service;

import com.wanted.teamV.repository.CategoryRepository;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.repository.SpendRepository;
import com.wanted.teamV.service.impl.SpendServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SpendServiceImplTest {

    @InjectMocks
    private SpendServiceImpl spendService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SpendRepository spendRepository;
}
