## RDBMS (Relatonal Database Management System)

- 관계형 데이터베이스 관리 시스템
    - 데이터를 행/열 기반의 테이블로 관리하는 시스템
- 특징
    - 테이블 간 키(Key)를 이용하여 관계를 형성
        - Primary Key: 한 행을 유일하게 식별
        - Foreign Key: 다른 테이블의 PK를 참조
    - SQL(Structured Query Language) 사용
        - 데이터를 조작하고 조회하는 표준 언어
    - 데이터 무결성 유지
        - 제약 조건을 통해 데이터의 정확성 보장
    - 트랜잭션 원칙 준수
        - ACID (원자성, 일관성, 고립성, 지속성)
- 장점
    - 데이터 정합성 보장
    - 중복 최소화
    - 강력한 쿼리 기능
    - 데이터 보안
- 단점
    - 확장성 제한
    - 복잡한 스키마
    - 고비용
- 종류
    - MySQL, PostgreSQL, Oracle Database, Microsoft SQL Server, MariaDB

## Oracle

- 구조
    - User = Schema
    - 각 사용자마다 자기 테이블 존재
    - MySQL은 Database 안에 Table 구조 → 계정은 DB 접근 권한만 가짐
- Shared Pool 사용
    - Shared Pool 안에는 Library Cache가 있고 SQL이 저장됨
    - Oracle은 SQL을 파싱 결과(실행 계획) 기준으로 재사용
    - 조건
        - SQL 텍스트 동일
        - 바인드 변수 사용 여부 중요
    
    ```java
    -- 비효율 (Hard Parse 증가)
    SELECT * FROM emp WHERE empno = 100;
    SELECT * FROM emp WHERE empno = 200;
    
    -- 효율 (Soft Parse)
    SELECT * FROM emp WHERE empno = :empno;
    ```
    
- 버전
    - 버전 숫자 = 출시 연도
    - g: grid, c: cloud
    
    | **버전** | **분류** | **주요 특징 및 현재 상태** |
    | --- | --- | --- |
    | **11g R2** | **과거 표준** | 가장 오랫동안 쓰인 버전. 현재 기술 지원 종료(Sustaining Support만 가능). |
    | **12c / 18c** | **과거 버전** | Multi-tenant(Cloud) 아키텍처 도입. 현재 모든 기술 지원 종료. |
    | **19c** | **LTS (표준)** | **현재 기업체 실무 표준.** 가장 안정적이며 2029년(유료 시 2032년)까지 지원. |
    | **21c** | **Innovation** | 블록체인 테이블, JavaScript 실행 등 신기능 테스트용. 지원 기간이 짧음. |
    | **23ai** | **LTS (최신)** | **최신 장기 지원 버전.** AI Vector Search(벡터 검색), JSON Relational duality 등 혁신적 기능 포함. |
    | **26ai** | **최신** | **2026년 최신 릴리스.** AI 및 클라우드 네이티브 기능이 더욱 강화된 버전. |

## SQL

- `DDL` : 구조 생성
    - `create`, `alter`, `drop(truncate)`
    
    ```sql
    -- 테이블 생성
    create table member (
        id number primary key,
        name varchar2(50),
        age number
    );
    
    -- 컬럼 추가
    alter table member add email varchar2(100);
    
    -- 테이블 삭제
    drop table member;
    
    -- 데이터만 삭제 (구조 유지)
    truncate table member;
    ```
    
- `DML` : 데이터 조작
    - `insert`, `update`, `delete`, `select`
    
    ```sql
    -- 데이터 삽입
    insert into member (id, name, age) values (1, 'KIM', 20);
    
    -- 데이터 수정
    update member set age = 30 where id = 1;
    
    -- 데이터 삭제
    delete from member where id = 1;
    
    -- 데이터 조회
    select * from member;
    ```
    
- `DCL` : 권한 조작
    - `grant`, `revoke`
    
    ```sql
    -- 권한 부여
    grant select, insert on member to user1;
    
    -- 권한 회수
    revoke insert on member from user1;
    ```
    
- `TCL` : 트랜젝션 관리
    - `commit`, `rollback`

## 트랜잭션 (commit/rollback)

- 오라클은 `commit` 전까지 실제 반영 X
- `rollback` 하면 마지막 커밋 시점으로 되돌아감
- JDBC에서는 auto commit 지원

## Null

- `NULL`은 0도 아니고 빈 문자열도 아님
- `NULL`과의 모든 연산 결과는 `NULL`
    
    ex) `100 + NULL = NULL`
    
- `NULL`처리 함수
    - `nvl(expr, value)` : NULL이면 value 반환
    - `nvl2(expr, A, B)` : NULL 여부에 따라 A 또는 B
- `NULL` 비교
    - `is null`, `is not null`

## 문자열 검색

- `LIKE`
    - `%`: 0개 이상의 모든 문자
    - `_`: 한 글자
- `%A%`처럼 앞에 와일드카드가 오면 인덱스를 타기 어려워질 수 있음
- 패턴이 복잡한 경우 정규 표현식 사용
    - `REGEXP_LIKE()`

```sql
select *
from emp
where ename like '%A%';

select *
from emp
where regexp_like(ename, '[A-C]');
```

## 정렬

- `order by 컬럼명`
    - 문자열, 날짜 숫자 정렬 가능
    - `asc`: 오름차순 (default)
    - `desc`: 내림차순
- DB에서 정렬은 비용이 많이 드는 작업
- SQL 실행 순서

```sql
select       3
from         1
where        2
order by     4
```

## 합집합

- `union`→ 중복 제거
    - 대응되는 컬럼 수와 타입이 동일해야 함
- `union all` → 중복 허용

```sql
select empno, ename, job, sal from emp
union
select deptno, dname, loc, null from dept;
```

## 기본 함수

### 문자열 함수

- `lower()`: 소문자 변환
- `concat()`: 문자열 결합
    
    ```sql
    select concat(’a’,’b’) from dual;
    ```
    
- `substr()`: 부분 문자열 추출
    
    ```sql
    select substr(’ABCDE’, 2, 3) from dual; -- BCD
    ```
    
- `lpad()`, `rpad()`: 좌/우 패딩
    
    ```sql
    select lpad(’ABC’, 10, ‘*’) from dual; -- ABC*******
    select rpad(’ABC’, 10, ‘*’) from dual; -- *******ABC
    ```
    
- `trim()`: 공백 또는 특정 문자 제거
    
    ```sql
    select rtrim(’MILLER’, ’ER’) from dual;
    ```
    
- `replace()`: 문자 치환
    
    ```sql
    select ename, replace(ename, ‘a’, ‘b’) from emp;
    ```
    

### 숫자 함수

- `round()`: 반올림
    
    ```sql
    select round(12.345, 0) from dual; -- 12 (정수만 남기기)
    select round(12.567, 0) from dual; -- 13
    select round(12.345, 1) from dual; -- 12.3
    select round(12.564, 1) from dual; -- 12.6
    select round(12.345, -1) from dual; -- 10
    select round(15.345, -1) from dual; -- 20
    ```
    
- `trunc()`: 절삭
    
    ```sql
    select trunc(12.345,0) from dual;   -- 12 (정수만 남기기)
    select trunc(12.567,0) from dual;   -- 12
    select trunc(12.345,1) from dual;   -- 12.3
    select trunc(12.564,1) from dual;   -- 12.5
    select trunc(12.345,-1) from dual;  -- 10
    select trunc(15.345,-1) from dual;  -- 10
    ```
    
- `mod()`: 나머지
    
    ```sql
    select 12/10 from dual; -- 1.2
    select mod(12, 10) from dual; -- 2
    select mod(0,0) from dual; -- 0
    ```
    

### 날짜/변환 함수

- 오라클의 날짜는 숫자처럼 계산이 가능
    - `Date + Number -> Date`
    - `Date - Number -> Date`
    - `Date - Date -> Number`
- `to_char()`: 숫자/날짜 → 포맷된 문자열 변환
- `to_date()`: 문자열 → 날짜 변환
- `to_number()`: 문자열 숫자 → 숫자 변환

```sql
select to_char(sysdate, 'YYYY-MM-DD HH24:MI:SS') from dual;
select to_date('2025-01-01') + 100 from dual;
```
