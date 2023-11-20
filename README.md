# 예산 관리 어플리케이션

본 서비스는 사용자들이 개인 재무를 관리하고 지출을 추적하는 데 도움을 주는 애플리케이션입니다. 이 앱은 사용자들이 예산을 설정하고 지출을 모니터링하여 재무 목표를 달성하는 데 도움이 됩니다.

<br/>

## Table of Contents

- [개요](#개요)
- [Skils](#skils)
- [프로젝트 진행 및 이슈 관리](#프로젝트-진행-및-이슈-관리)
- [ERD](#erd)
- [API Reference](#api-reference)
- [Running Tests](#running-tests)
- [구현과정(설계 및 의도)](#구현과정(설계-및-의도))
- [TIL 및 회고](#til-및-회고)
- [References](#references)

<br/>

## 개요

사용자의 카테고리 별 예산을 설정 또는 추천해주고, 지출 내역을 통해 각종 통계자료를 확인할 수 있는 서비스

<br/>

## Skils

언어 및
프레임워크: ![Static Badge](https://img.shields.io/badge/JAVA-17-blue) ![Static Badge](https://img.shields.io/badge/SpringBoot-3.1.5-green)<br/>
데이터베이스: ![Static Badge](https://img.shields.io/badge/MySQL--red) ![Static Badge](https://img.shields.io/badge/Redis--red)<br/>
테스트 데이터베이스: ![Static Badge](https://img.shields.io/badge/H2--red)

<br/>

## 프로젝트 진행 및 이슈 관리

<img src="https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white">
<img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white">
<br/>

### 요구사항 분석

[**요구사항 분석**](https://wonwonjung.notion.site/18b5d24fb06f4577acdc23aeb483031b?pvs=4)

### 이슈 관리 - Github

![스크린샷 2023-11-19 081528](https://github.com/shinmin9812/Budget-Management/assets/83534757/ede4b67d-cfff-4216-8759-bc566215bc3c)

## ERD

![스크린샷 2023-11-19 081704](https://github.com/shinmin9812/Budget-Management/assets/83534757/a4e5d946-aa7f-42b5-923a-09e8e17f7c9d)

## API Reference

<details>
<summary>Member</summary>

#### 회원가입

POST /members

| 바디       | 타입     | 설명   |
|:---------|:-------|:-----|
| account  | string | 계정   |
| password | string | 비밀번호 |

#### Response

    HTTP/1.1 200
    Content-Type: application/json
    
    OK

#### 로그인

POST /members/login

| 바디       | 타입     | 설명   |
|:---------|:-------|:-----|
| account  | string | 계정   |
| password | string | 비밀번호 |

#### Response

    HTTP/1.1 200
    Content-Type: application/json

    {
        "accessToken": "12412fd12fdksr.142fdadafs.rea2r23r23f"
    }

</details>

<details>
<summary>Category</summary>

#### 카테고리 목록 조회

GET /categories

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    [
      {
        "id": 1,
        "name": "식품"
      },
      {
        "id": 2,
        "name": "금융"
      },...
    ]
```

</details>

<details>
<summary>Budget</summary>

#### 예산 설정

PUT /budgets

| 바디         | 타입   | 설명       |
|:-----------|:-----|:---------|
| categoryId | Long | 카테고리 아이디 |
| budget     | int  | 예산       |

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    {
      "id": 5,
      "category": "쇼핑",
      "member": "test",
      "budget": 50000
    }
```

#### 예산 설계(추천)

GET /budgets/recommend

| 쿼리 파라미터 | 타입  | 설명    |
|:--------|:----|:------|
| money   | int | 예산 총액 |

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    {
      "총액": 100000.0,
      "식품": 24500.0,
      "금융": 15000.0,
      ...,
      "잔액": 500.0
    }
```

</details>

<details>
<summary>Spend</summary>

#### 지출 내역 생성

POST /spends

| 바디         | 타입            | 설명       |
|:-----------|:--------------|:---------|
| categoryId | Long          | 카테고리 아이디 |
| amount     | int           | 지출 금액    |
| memo       | String        | 지출 메모    |
| date       | LocalDateTime | 지출 일자    |

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    {
      "member": "test1",
      "category": "금융",
      "amount": 50000,
      "memo": "출금",
      "date": "2023-11-15T00:00:00",
      "isExcluded": true
    }
```

#### 지출 내역 수정

PATCH /spends/{spendId}

| 바디         | 타입            | 설명       |
|:-----------|:--------------|:---------|
| categoryId | Long          | 카테고리 아이디 |
| amount     | int           | 지출 금액    |
| memo       | String        | 지출 메모    |
| date       | LocalDateTime | 지출 일자    |
| isExcluded | Boolean       | 합계 제외 여부 |

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    {
      "member": "test",
      "category": "금융",
      "amount": 50000,
      "memo": "출금",
      "date": "2023-11-16T00:00:00",
      "isExcluded": true
    }
```

#### 지출 내역 상세 조회

GET /spends/spend/{spendId}

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    {
      "member": "test",
      "category": "금융",
      "amount": 50000,
      "memo": "출금",
      "date": "2023-11-16T00:00:00",
      "isExcluded": true
    }
```

#### 지출 내역 목록 조회

GET /spends

| 쿼리 파라미터    | 타입            | 설명       |
|:-----------|:--------------|:---------|
| startDate  | LocalDateTime | 시작 일자    |
| endDate    | LocalDateTime | 종료 일자    |
| categoryId | Long          | 카테고리 아이디 |
| min        | Integer       | 최소 지출 금액 |
| max        | Integer       | 최대 지출 금액 |

#### Request

```javascript
spends ? startDate = 2023 - 11 - 14T00
:
00
:
00 & endDate = 2023 - 12 - 17
T00:00
:
00 & categoryId = 2 & min = 0 & max = 15000
```

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    {
      "spendList": [
          {
            "member": "test",
            "category": "교통",
            "amount": 1500,
            "memo": "버스",
            "date": "2023-11-19T00:00:00",
            "isExcluded": true
          }
      ],
      "categoryTotal": {
          "교통": 1500.0
      },
      "allSpendsTotal": 1500.0
    }
```

#### 지출 내역 삭제

DELETE /spends/{spendId}

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    OK
```

#### 합계 제외 지출 금액

GET /spends/total

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    439450.0
```

</details>

<details>
<summary>Consulting</summary>

#### 오늘의 지출 추천

GET /consults/today-recommend

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    {
      "availableTodaySpend": 153900.0,
      "categoryTodaySpend": {
        "교통": 5200.0,
        "식품": 35000.0,
        "생활": 50000.0,
        "의료/건강": 9000.0,
        "금융": 26800.0,
        "여가": 15000.0,
        "쇼핑": 5000.0,
        "주거/통신": 8000.0
      },
      "sentence": "절약하면서 잘 쓰고 있어요! 좋아요!"
    }
```

#### 오늘의 지출 정보

GET /consults/today-spend

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    {
      "todaySpendByCategory": {},
      "todayAllSpends": 0.0,
      "riskPercentageByCategory": {
        "교통": 0.0,
        "식품": 0.0,
        "생활": 0.0,
        "의료/건강": 0.0,
        "금융": 0.0,
        "여가": 0.0,
        "쇼핑": 0.0,
        "주거/통신": 0.0
      }
    }
```

</details>

<details>
<summary>Statistics</summary>

#### 월별 통계

GET /statistics/monthly

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    {
      "availableTodaySpend": 153900.0,
      "categoryTodaySpend": {
        "교통": 5200.0,
        "식품": 35000.0,
        "생활": 50000.0,
        "의료/건강": 9000.0,
        "금융": 26800.0,
        "여가": 15000.0,
        "쇼핑": 5000.0,
        "주거/통신": 8000.0
      },
      "sentence": "절약하면서 잘 쓰고 있어요! 좋아요!"
    }
```

#### 오늘의 지출 정보

GET /consults/today-spend

#### Response

```http
    HTTP/1.1 200
    Content-Type: application/json

    {
      "todaySpendByCategory": {
          "교통": 1300.0,
          "식품": 20000.0,
          "금융": 15000.0,
          "쇼핑": 5000.0
       },
      "todayAllSpends": 41300.0,
      "riskPercentageByCategory": {
        "교통": 1300.0,
        "식품": 20000.0,
        "생활": 0.0,
        "의료/건강": 0.0,
        "금융": 15000.0,
        "여가": 0.0,
        "쇼핑": 5000.0,
        "주거/통신": 0.0
      }
    }
```

</details>

<br/>

## Running Tests

> ![Static Badge](https://img.shields.io/badge/Test_Passed-27/27-green)
![스크린샷 2023-11-19 081554](https://github.com/shinmin9812/Budget-Management/assets/83534757/7a3b661f-78cd-4ccb-a7fc-f8734fd7deb1)

<br/>

## 구현과정(설계 및 의도)

[요구사항 분석](https://wonwonjung.notion.site/18b5d24fb06f4577acdc23aeb483031b?pvs=4)

<details>
<summary>Budget</summary>

- 예산 설정 - 예산을 설정한 데이터가 있다면 수정, 그렇지 않다면 새로운 예산 데이터를 저장함.
- 예산 설계(추천) - 모든 멤버들의 카테고리 별 예산 비율을 구하고 사용자가 원하는 총 금액에 비율을 곱해서 카테고리 별로 예산을 설계함.

</details>

<details>
<summary>Spend</summary>

- 지출 내역 목록 조회 - 검색 시작일자와 종료일자를 제외한 특정 카테고리, 최소 금액, 최대 금액 값 존재에 따라 총 4가지 쿼리문으로 구분
    - 합계는 합계 제외 처리한 지출 내역을 제외한 나머지 내역의 합계로 설정

</details>

<details>
<summary>Consulting</summary>

- 오늘의 지출 추천
    - 사용자가 설정한 예산 목록을 참조하여 사용자의 총 예산을 구하고 어제까지 사용한 총액을 구해 사용 가능한 오늘 총액(availableTodaySpend)을 구함.
    - 사용자가 설정한 카테고리 별 예산 목록과 어제까지 사용한 카테고리 별 예산을 구해서 나온 차액을 남은 날짜로 나눠 오늘 사용할 수 있는 카테고리 별 예산(categoryAmount)을 구함.
    - 사용자가 설정한 총 예산을 어제까지 사용한 총 예산으로 나눈 뒤 그 비율에 따라 5단계(VERY_BAD, BAD, NORMAL, GOOD, EXCELLENT)로 나눠 소비 습관을 잘 지치고 있는지 한 줄로
      표현함.

- 오늘의 지출 정보
    - 오늘 사용한 지출 내역을 조회해 사용한 총액(allSpendTotal), 카테고리 별 지출 내역(categoryTotal)을 구함.
    - 사용자가 설정한 카테고리 별 예산을 조회해 하루의 예산을 구하고 오늘 사용한 카테고리 별 지출 내역과 비교해 위험도를 구함.

</details>

<details>
<summary>Statistics</summary>

- 월별 통계
    - 오늘을 기준으로 2달 ~ 1달 사이의 지출과 1달 ~ 오늘 사이의 지출을 비교해 총액과 카테고리 별 지출 비율을 구함.
- 일자 통계
    - 오늘 요일과 지난 주의 요일의 지출을 비교해 지출 비율을 구함.

</details>

<br/>

## TIL 및 회고

<details>
<summary>@PostConstruct</summary>

- 통계 API 구현을 위해 통계 자료를 더미 데이터로 구현해 서버를 실행할 때마다 더미 데이터가 생성되도록 @PostConstruct 어노테이션을 사용해서 구현
- 그러나 테스트 코드 실행 시, ApplicationContext가 발생하면서 테스트를 진행할 수 없었음. <br>

&rarr; **@Profile** 어노테이션을 설정해 특정 프로필을 활성화할때만 더미 데이터를 생성하도록 변경함.

</details>

<details>
<summary>LocalDateTime 역직렬화</summary>

- 지출 구현 중 LocalDateTime으로 저장한 date를 DB에서 불러올 때 `Java 8 date/time type java.time.LocalDateTime not supported by default` 오류가 발생함.
- LocalDateTime 타입을 역직렬화를 하지 못해서 발생한 오류 <br>

&rarr; `implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310'`를 gradle에 추가, DTO 파일의 date 위에 `@JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)`어노테이션을 추가함.

</details>

<br/>

## References

- [@PostConstruct를 막아라](https://hyewoncc.github.io/post-construct-profile/)
- [스프링 Java 8 LocalDateTime 직렬화 역직렬화 오류](https://velog.io/@sago_mungcci/%EC%8A%A4%ED%94%84%EB%A7%81-Java-8-LocalDateTime-%EC%A7%81%EB%A0%AC%ED%99%94-%EC%97%AD%EC%A7%81%EB%A0%AC%ED%99%94-%EC%98%A4%EB%A5%98)


