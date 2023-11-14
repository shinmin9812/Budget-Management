package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.req.SpendCreateReqDto;
import com.wanted.teamV.dto.req.SpendUpdateReqDto;
import com.wanted.teamV.dto.res.SpendInfoResDto;
import com.wanted.teamV.dto.res.SpendListResDto;
import com.wanted.teamV.entity.Category;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.entity.Spend;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.CategoryRepository;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.repository.SpendRepository;
import com.wanted.teamV.service.SpendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SpendServiceImpl implements SpendService {

    private final SpendRepository spendRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public SpendInfoResDto createSpend(Long memberId, SpendCreateReqDto createReqDto) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findById(createReqDto.categoryId()).orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Spend spend = Spend.builder()
                .member(member)
                .category(category)
                .amount(createReqDto.amount())
                .memo(createReqDto.memo())
                .date(createReqDto.date())
                .build();

        spendRepository.save(spend);

        return SpendInfoResDto.builder()
                .member(spend.getMember().getAccount())
                .category(spend.getCategory().getName())
                .amount(spend.getAmount())
                .memo(spend.getMemo())
                .date(spend.getDate())
                .isExcluded(spend.getIsExcluded())
                .build();
    }

    @Override
    public SpendInfoResDto updateSpend(Long memberId, Long spendId, SpendUpdateReqDto updateReqDto) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Spend spend = spendRepository.findById(spendId).orElseThrow(() -> new CustomException(ErrorCode.SPEND_NOT_FOUND));
        Category category = categoryRepository.findById(updateReqDto.categoryId()).orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if (spend.getMember().getId() != memberId) throw new CustomException(ErrorCode.NO_AUTHORIZATION);

        spendRepository.updateSpend(memberId, spendId, updateReqDto.categoryId(), updateReqDto.amount(), updateReqDto.memo(),
                updateReqDto.date(), updateReqDto.isExcluded());

        return SpendInfoResDto.builder()
                .member(member.getAccount())
                .category(category.getName())
                .amount(updateReqDto.amount())
                .memo(updateReqDto.memo())
                .date(updateReqDto.date())
                .isExcluded(updateReqDto.isExcluded())
                .build();
    }

    @Override
    public SpendInfoResDto getSpendDetail(Long memberId, Long spendId) {
        Spend spend = spendRepository.findById(spendId).orElseThrow(() -> new CustomException(ErrorCode.SPEND_NOT_FOUND));

        if (spend.getMember().getId() != memberId) throw new CustomException(ErrorCode.NO_AUTHORIZATION);

        return SpendInfoResDto.builder()
                .member(spend.getMember().getAccount())
                .category(spend.getCategory().getName())
                .amount(spend.getAmount())
                .memo(spend.getMemo())
                .date(spend.getDate())
                .isExcluded(spend.getIsExcluded())
                .build();
    }

    @Override
    public SpendListResDto getAllSpends(Long memberId, LocalDateTime startDate, LocalDateTime endDate, Long categoryId, Integer min, Integer max) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        if ((min != null && max == null) || (min == null && max != null) || (min != null && min < 0)) {
            throw new CustomException(ErrorCode.INVALID_AMOUNT_RANGE);
        }

        List<Spend> spends = new ArrayList<>();

        if (categoryId == null && (min == null && max == null)) {
            spends = spendRepository.findSpendsByDateBetween(memberId, startDate, endDate);
        } else if (categoryId == null && min != null && max != null) {
            spends = spendRepository.findSpendsByAmountBetweenAndDateBetween(memberId, min, max, startDate, endDate);
        } else if (categoryId != null && min == null && max == null) {
            spends = spendRepository.findSpendsByCategoryAndDateBetween(memberId, categoryId, startDate, endDate);
        } else if (categoryId != null && min != null && max != null) {
            spends = spendRepository.findSpendsByCategoryAndAmountBetweenAndDateBetween(memberId, categoryId, min, max, startDate, endDate);
        }

        List<SpendInfoResDto> lists = convertToSpendInfoResDto(spends);
        Map<String, Double> categoryTotal = getSumSpendsByCategory(spends);
        Double total = getSumSpends(spends);

        return SpendListResDto.builder()
                .spendList(lists)
                .categoryTotal(categoryTotal)
                .allSpendsTotal(total)
                .build();
    }

    public Map<String, Double> getSumSpendsByCategory(List<Spend> spends) {
        return spends.stream()
                .filter(spend -> spend.getIsExcluded() == true)
                .collect(Collectors.groupingBy(spend -> spend.getCategory().getName(),
                        Collectors.summingDouble(Spend::getAmount)));
    }

    public Double getSumSpends(List<Spend> spends) {
        return spends.stream()
                .filter(spend -> spend.getIsExcluded() == true)
                .mapToDouble(Spend::getAmount)
                .sum();
    }

    public List<SpendInfoResDto> convertToSpendInfoResDto(List<Spend> spends) {
        return spends.stream().map(this::mapToSpendInfoResDto).collect(Collectors.toList());
    }

    private SpendInfoResDto mapToSpendInfoResDto(Spend spend) {
        return SpendInfoResDto.builder()
                .member(spend.getMember().getAccount())
                .category(spend.getCategory().getName())
                .amount(spend.getAmount())
                .memo(spend.getMemo())
                .date(spend.getDate())
                .isExcluded(spend.getIsExcluded())
                .build();
    }

    @Override
    public void deleteSpend(Long memberId, Long spendId) {
        Spend spend = spendRepository.findById(spendId).orElseThrow(() -> new CustomException(ErrorCode.SPEND_NOT_FOUND));

        if (spend.getMember().getId() != memberId) throw new CustomException(ErrorCode.NO_AUTHORIZATION);

        spendRepository.delete(spend);
    }

    @Override
    public Double getExcludingTotal(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return spendRepository.getSumAmountByMember(memberId);
    }
}
