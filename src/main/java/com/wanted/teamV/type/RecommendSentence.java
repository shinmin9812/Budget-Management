package com.wanted.teamV.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RecommendSentence {
    EXCELLENT("훌륭해요! 지금처럼 아껴써봐요!!"),
    GOOD("절약하면서 잘 쓰고 있어요! 좋아요!"),
    NORMAL("계획에 맞게 잘 사용하고 있어요!"),
    BAD("조금 많이 쓰고 있어요! 조심하세요!"),
    VERY_BAD("너무 많이 쓰고 있어요! 아껴 쓰세요!");

    private final String content;

    public String getContent() {
        return this.content;
    }
}
