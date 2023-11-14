package com.wanted.teamV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.req.SpendCreateReqDto;
import com.wanted.teamV.dto.req.SpendUpdateReqDto;
import com.wanted.teamV.dto.res.SpendInfoResDto;
import com.wanted.teamV.dto.res.SpendListResDto;
import com.wanted.teamV.entity.Spend;
import com.wanted.teamV.service.SpendService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class SpendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpendService spendService;

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
    @DisplayName("지출 내역 생성 - 성공")
    public void createSpend() throws Exception {
        //given
        SpendCreateReqDto request = SpendCreateReqDto.builder()
                .categoryId(1L)
                .amount(25000)
                .memo("저녁")
                .date(LocalDateTime.now())
                .build();

        //when & then
        mockMvc.perform(post("/spends")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("지출 내역 수정 - 성공")
    public void updateSpend() throws Exception {
        //given
        Long spendId = 1L;

        SpendUpdateReqDto request = SpendUpdateReqDto.builder()
                .categoryId(2L)
                .amount(30000)
                .memo("저녁")
                .date(LocalDateTime.now())
                .isExcluded(true)
                .build();

        //when & then
        mockMvc.perform(patch("/spends/{spendId}", spendId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("지출 내역 상세 조회 - 성공")
    public void getSpendDetail() throws Exception {
        //given
        Long spendId = 1L;

        SpendInfoResDto response = SpendInfoResDto.builder()
                .member("mockUser")
                .category("식품")
                .amount(10000)
                .memo("편의점")
                .date(LocalDateTime.now())
                .isExcluded(true)
                .build();

        when(spendService.getSpendDetail(anyLong(), anyLong())).thenReturn(response);

        //when & then
        mockMvc.perform(get("/spends/spend/{spendId}", spendId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(print());
    }

    @Test
    @DisplayName("지출 내역 목록 조회 - 성공")
    public void getSpends() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        Long categoryId = 1L;
        Integer min = 1000;
        Integer max = 5000;

        SpendInfoResDto spend1 = SpendInfoResDto.builder()
                .member("mockUser")
                .category("식품")
                .amount(20000)
                .memo("저녁")
                .date(LocalDateTime.now())
                .isExcluded(true)
                .build();

        SpendInfoResDto spend2 = SpendInfoResDto.builder()
                .member("mockUser")
                .category("식품")
                .amount(10000)
                .memo("편의점")
                .date(LocalDateTime.now())
                .isExcluded(true)
                .build();

        SpendListResDto response = SpendListResDto.builder()
                .spendList(List.of(spend1, spend2))
                .categoryTotal(Map.of("식품", 30000.0))
                .allSpendsTotal(30000.0)
                .build();

        when(spendService.getAllSpends(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), anyLong(), any(Integer.class), any(Integer.class))).thenReturn(response);

        //when & then
        mockMvc.perform(get("/spends")
                        .header("Authorization", "Bearer " + token)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("categoryId", String.valueOf(categoryId))
                        .param("min", String.valueOf(min))
                        .param("max", String.valueOf(max))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response))) // 예상 응답과 일치하는지 확인
                .andDo(print());
    }

    @Test
    @DisplayName("지출 내역 삭제 - 성공")
    public void deleteSpend() throws Exception {
        //given
        Long spendId = 1L;

        //when & then
        mockMvc.perform(delete("/spends/{spendId}", spendId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"))
                .andDo(print());
    }

    @Test
    @DisplayName("합계 제외 지출 내역 - 성공")
    public void getExcludingTotal() throws Exception {
        //when & then
        mockMvc.perform(get("/spends/total")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
