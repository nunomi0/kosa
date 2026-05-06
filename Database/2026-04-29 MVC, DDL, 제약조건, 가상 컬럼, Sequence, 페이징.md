## MVC

### Model

- POJO(Plain Old Java Object)
    - 프레임워크나 라이브러리에 의존하지 않는 순수 자바 객체
- 특징
    - 특정 상속 없음 (`extends`, `implements` 강제 없음)
    - 어노테이션 없이도 사용 가능
    - getter setter만 있는 단순 객체
    - 어디서든 재사용 가능

### DTO (Data Transfer Object)

- 데이터를 전달하기 위한 객체
    - Table 하나 당 Class 하나
        - Join 결과를 담는 DTO 생성 가능
        - 
- Controller ↔ Service ↔ DAO 사이 데이터 전달
- 특징
    - 필드 + getter/setter
    - 비즈니스 로직 없음
    - 값만 담음
- VO (Value Object)
    - 값 자체를 표현하는 객체
- Domain
    - 비즈니스 로직을 포함하는 핵심 객체
- DTO를 만들고 싶지 않은 경우 Map, List 사용 가능

```java
public class UserDTO {
    private String name;
    private int age;

    // getter, setter
}
```

### DAO (Data Access Object)

- DB 접근을 담당하는 객체
- DB ↔ 애플리케이션 연결
- 특징
    - DB 연결
    - CRUD 수행
    - SQL 포함

```java
public class UserDAO {

    public UserDTO findById(int id) {
        // DB 조회
        // ResultSet → DTO 변환
        return userDTO;
    }

    public void save(UserDTO user) {
        // INSERT 실행
    }
}
```

---

## DDL (Data Definition Language)

- 데이터베이스 구조(스키마)를 정의하거나 변경하는 SQL

### 1. CREATE

- 테이블 생성

```sql
CREATE TABLE emp (
    empno NUMBER,
    ename VARCHAR2(20)
);
```

### 2. ALTER

- 테이블 구조 변경
- 자동 commit 되어 rollback 불가
- 구조 바꿀 때 기존 데이터에 영향을 줌

```sql
-- 컬럼 추가
ALTER TABLE emp ADD sal NUMBER;
ALTER TABLE emp ADD (job VARCHAR2(20), hiredate DATE);

-- 컬럼 수정
ALTER TABLE emp MODIFY sal NUMBER(10);

--- 컬럼 이름 변경
ALTER TABLE emp RENAME COLUMN sal TO salary;

--- 컬럼 삭제 (데이터 사라짐)
ALTER TABLE emp DROP COLUMN sal;

-- 제약조건 추가
ALTER TABLE emp
ADD CONSTRAINT pk_emp PRIMARY KEY(empno);

-- 제약조건 삭제
ALTER TABLE emp
DROP CONSTRAINT pk_emp;

-- NOT NULL 추가
ALTER TABLE emp
MODIFY ename VARCHAR2(20) NOT NULL;

-- 테이블 이름 변경
RENAME emp TO emp_new;
```

### 3. DROP

- 테이블 삭제

```sql
DROP TABLE emp;
```

### 4. TRUNCATE

- 데이터 초기화
- 구조는 유지
- DELETE 보다 빠름
- ROLLBACK 불가

```sql
TRUNCATE TABLE emp;
```

---

## 제약조건 (constraint)

### Primary Key

- not null, unique (유일 값 보장)
- 조회가 많음 → index default)
- pk는 테이블 당 1개만 가능
    - 여러 개의 칼럼을 묶어서 복합 키(composite key) 1개로도 pk 가능
- 참조 무결성
    - 존재하지 않는 부모 값 입력 불가
- 삭제 제한
    - 부모 데이터가 자식에서 사용 중이면 삭제 불가
        
        → `CASCADE` 사용: 부모 삭제 시 자식도 같이 삭제
        
        ```sql
        ON DELETE CASCADE
        ```
        
- 제약 생성 및 확인 방법
    1. 테이블 생성 시 pk 추가
        - pk 이름 없이 생성 시 Oracle이 자동으로 이름 생성 → 불편
            - 에러 메시지 의미 없음
            - 삭제, 수정 불편
            - 가독성 떨어짐
        
        ```sql
        -- pk 이름 지정
        CREATE TABLE emp (
            empno NUMBER,
            CONSTRAINT pk_emp PRIMARY KEY(empno)
        );
        
        -- 권장 X
        CREATE TABLE temp (
            id NUMBER PRIMARY KEY
        );
        ```
        
    2. 테이블 생성 후 pk 추가
        - 데이터 조건 만족해야 함
            - NULL 없음
            - 중복 없음
        
        ```sql
        ALTER TABLE emp
        ADD CONSTRAINT pk_emp PRIMARY KEY(empno);
        ```
        
    3. 테이블 제약조건 확인
        
        ```sql
        SELECT *
        FROM user_constraints
        WHERE table_name = 'EMP';
        ```
        

### Unique

- 중복 허용 X, null 허용
    - null 중복은 체크하지 않음
    - 값 반드시 존재 + 중복 금지 → not null + unique
- 여러 칼럼에 대해 unique 가능
- create, alter에서 가능
- index 자동 생성

```sql
CREATE TABLE temp8 (
    id NUMBER CONSTRAINT pk_temp8_id PRIMARY KEY,
    name VARCHAR2(20) NOT NULL,
    jumin NVARCHAR2(6) CONSTRAINT uk_temp8 UNIQUE,
    addr VARCHAR2(50)
);
```

---

## 가상 컬럼 (조합 칼럼)

- 실제 데이터를 저장하지 않고 조회할 때 계산해서 값을 만들어내는 칼럼
- 무결성(integrity)을 보장하기 위한 방법

```sql
create table vtab (
	no1 number, // 값이 변경되면
	no2 number,
	no3 number GENERATED AlWAYS as (no1 + no2) VIRTUAL // 인지해서 테이블 다시 만든다
}
```

---

## Sequence

- 자동으로 증가하는 숫자를 만들어주는 DB 객체
    - 공유 객체라서 여러 곳에서 재사용 가능
- 주로 기본 키(PK) 값을 생성하기 위해 사용
- 애플리케이션 코드를 대체 → Oracle이 내부적으로 생성, 증가, 동시성 처리 관리
- 메모리에 cache 되면 sequence 값을 액세스 하는 효율성 향상

```sql
CREATE SEQUENCE board_num;

SELECT board_num.NEXTVAL FROM dual; -- 다음 번호 생성
SELECT board_num.CURRVAL FROM dual; -- 현재 번호 확인
```

```sql
create sequence 시퀀스명
[start with 시작번호]
[increment by 증가값]
[maxvalue 최대값 | nomaxvalue]
[minvalue 최소값 | nominvalue]
[cycle | nocycle]
[cache 숫자 | nocache];
```

## Rownum

- Oracle이 조회 결과에 임시로 붙여주는 행 번호
- 테이블에 실제 저장된 컬럼이 아님
- 조회할 때만 생성
- order by 전에 붙음 → Top N을 뽑고 싶으면 서브쿼리를 써야 함

```sql
select *
from (
    select rownum as num, e.*
    from (
        select empno, ename, sal
        from emp
        order by sal desc
    ) e
) n
where num <= 5;
```

- 페이징에서 많이 사용

## Rank

- 정렬 기준으로 순위를 매기는 윈도우 함수
    - 윈도우 함수는 where 보다 나중에 실행됨 → where에서 바로 못씀
    - 윈도우 함수: 행을 그룹으로 묶지 않으면서 다른 행들과 비교/계산할 수 있게 해주는 함수
        - rank(), dense_rank(), row_number() …
- 동점 처리 기능

```sql
select ename,
       sal,
       rank() over(order by sal desc) as rnk
from emp;
```

```sql
ENAME   SAL   RNK
KING    5000   1
SCOTT   3000   2
FORD    3000   2
JONES   2975   4
```

| **함수** | **동점 처리** | **순위 건너뜀** |
| --- | --- | --- |
| RANK | 같은 순위 | O |
| DENSE_RANK | 같은 순위 | X |
| ROW_NUMBER | 무조건 다름 | 없음 |

## Offset Fetch

- Oracle 12c부터 지원하는 페이징 문법

```sql
select *
from 테이블
order by 컬럼
offset 건너뛸개수 rows
fetch next 가져올개수 rows only;
```

```sql
-- ex) 앞 3개 건너 뛰고 다음 3개 조회
select *
from emp
order by sal desc
offset 3 rows
fetch next 3 rows only;
```

```sql
-- 1페이지
offset 0 rows
fetch next 10 rows only

-- 2페이지
offset 10 rows
fetch next 10 rows only

-- 3페이지
offset 20 rows
fetch next 10 rows only
```

- 실행 순서

```sql
1. FROM
2. WHERE
3. ORDER BY
4. OFFSET
5. FETCH
```
