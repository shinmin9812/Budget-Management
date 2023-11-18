package com.wanted.teamV.component;

import com.wanted.teamV.entity.Category;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.entity.Spend;
import com.wanted.teamV.repository.CategoryRepository;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.repository.SpendRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DummyData {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final SpendRepository spendRepository;

    @PostConstruct
    public void generateDummyData() {
        generateSpendDummyData();
    }

    private void generateSpendDummyData() {
        List<Member> memberList = memberRepository.findAll();
        List<Category> categoryList = categoryRepository.findAll();
        LocalDateTime current = LocalDateTime.now();
        int currentYear = current.getYear();
        int currentMonth = current.getMonthValue();

        Random rand = new Random();

        for (int i = 0; i < 50; i++) {
            YearMonth currentYearMonth = YearMonth.of(currentYear, currentMonth);

            int ranMember = rand.nextInt(memberList.size());
            int ranCategory = rand.nextInt(categoryList.size());
            int ranAmount = rand.nextInt(50000) + 1000;
            int ranMonth = rand.nextInt(currentMonth) + 1;
            int ranDay = rand.nextInt(currentYearMonth.lengthOfMonth()) + 1;

            Spend spend = Spend.builder()
                    .member(memberList.get(ranMember))
                    .category(categoryList.get(ranCategory))
                    .amount(ranAmount)
                    .memo(ranMemo(ranCategory))
                    .date(LocalDateTime.of(currentYear, ranMonth, ranDay, 0, 0, 0))
                    .build();

            spendRepository.save(spend);
        }
    }

    private String ranMemo(int ranCategory) {
        switch (ranCategory) {
            case 0:
                return "식비";
            case 1:
                return "교통";
            case 2:
                return "금융";
            case 3:
                return "야구";
            case 4:
                return "쇼핑";
            case 5:
                return "생활";
            case 6:
                return "주거/통신";
            case 7:
                return "의료/건강";
        }
        return "default";
    }
}
