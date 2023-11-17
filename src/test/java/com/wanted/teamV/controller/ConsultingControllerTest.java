package com.wanted.teamV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.res.TodayAmountInfoResDto;
import com.wanted.teamV.dto.res.TodayRecommendResDto;
import com.wanted.teamV.service.ConsultingService;
import com.wanted.teamV.type.RecommendSentence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class ConsultingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConsultingService consultingService;

    private String token;

    @BeforeEach
    public void commonSetup() throws Exception {
        // 회원가입
        MemberJoinReqDto joinReqDto = new MemberJoinReqDto("mockUser", "mockUser1234!");
        MvcResult result = mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinReqDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        // 로그인
        MemberLoginReqDto loginReqDto = new MemberLoginReqDto("mockUser", "mockUser1234!");
        result = mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReqDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        response = result.getResponse().getContentAsString();
        token = JsonPath.parse(response).read("$.accessToken");
    }

    @Test
    @DisplayName("오늘의 지출 추천 - 성공")
    public void recommendTodaySpend() throws Exception {
        //given
        TodayRecommendResDto response = TodayRecommendResDto.builder()
                .availableTodaySpend(35000.0)
                .categoryTodaySpend(Map.of("식품", 20000.0, "교통", 10000.0))
                .sentence(RecommendSentence.GOOD.getContent())
                .build();

        //when
        when(consultingService.recommendTodaySpend(anyLong())).thenReturn(response);

        //then
        mockMvc.perform(get("/consults/today-recommend")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableTodaySpend").value(35000.0))
                .andExpect(jsonPath("$.categoryTodaySpend.식품").value(20000.0))
                .andExpect(jsonPath("$.categoryTodaySpend.교통").value(10000.0))
                .andDo(print());
    }

    @Test
    @DisplayName("오늘의 지출 안내 - 성공")
    public void getTodaySpend() throws Exception {
        //given
        TodayAmountInfoResDto response = TodayAmountInfoResDto.builder()
                .todayAllSpends(25000.0)
                .todaySpendByCategory(Map.of("식품", 12000.0, "교통", 8000.0, "금융", 5000.0))
                .riskPercentageByCategory(Map.of("식품", 80.0, "교통", 200.0, "금융", 100.0))
                .build();

        //when
        when(consultingService.getTodaySpend(anyLong())).thenReturn(response);

        //then
        mockMvc.perform(get("/consults/today-spend")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todayAllSpends").value(25000.0))
                .andExpect(jsonPath("$.todaySpendByCategory.식품").value(12000.0))
                .andExpect(jsonPath("$.todaySpendByCategory.교통").value(8000.0))
                .andExpect(jsonPath("$.todaySpendByCategory.금융").value(5000.0))
                .andExpect(jsonPath("$.riskPercentageByCategory.식품").value(80.0))
                .andExpect(jsonPath("$.riskPercentageByCategory.교통").value(200.0))
                .andExpect(jsonPath("$.riskPercentageByCategory.금융").value(100.0))
                .andDo(print());

    }
}
