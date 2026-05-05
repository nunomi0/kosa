## SQL, PL/SQL

### SQL

- 데이터 조회, 조작 언어
- 변수, 반복문, 제어문 사용 불가

### PL/SQL

- SQL + 프로그래밍
- 변수, 조건문, 반복문 사용 가능
    - `decode`
        - 값이 같으면 다른 값으로 바꿔주는 Oracle 함수
    
    ```sql
    decode(컬럼,
           값1, 결과1,
           값2, 결과2,
           기본값)
    ```
    
    ```sql
    select ename,
           decode(deptno,
                  10, 'ACCOUNTING',
                  20, 'RESEARCH',
                  30, 'SALES',
                  'ETC') as dept_name
    from emp;
    ```
    
    - `case`
        - 조건에 따라 값을 반환하는 SQL 표현식

## Group by

- 여러 행(row)을 특정 컬럼 기준으로 묶어서 집계 함수 결과를 계산할 때 사용하는 구문
- 같은 값을 가진 행들을 하나의 그룹으로 만듦
- 실행 순서
    
    ```sql
    select    5
    from      1
    where     2
    group by  3
    having    4
    order by  6
    ```
    

## 집계 함수

- 여러 행(row)의 값을 하나로 계산해서 결과를 반환하는 함수
- `count()`, `count(*)`, `sum()`, `avg()`, `max()`, `min()`
- 집계 함수 특징
    1. 집계 함수는 `group by` 절과 같이 사용
    2. 모든 집계 함수는 `null` 값을 무시
    3. `select` 절의 집계 함수 이외에 다른 컬럼이 오면 반드시 그 컬럼은 `group by` 절에 명시되어야 함
    
    ```sql
    select job, avg(sal), sum(sal)
    from emp
    group by job
    having avg(sal) >= 2000;
    ```
    
- 데이터가 DB에 있고 양이 많다면 Stream API보다 DB 집계 함수를 사용하는 것이 효율적
    - DB 엔진이 집계 연산에 최적화되어 있기 때문

## Join

- 여러 테이블을 연결해서 하나의 결과로 조회하는 연산

```sql
JOIN
 ├─ 조건 없음
 │    └─ 카테시안 곱
 │         └─ CROSS JOIN (문법)
 │
 └─ 조건 있음
      ├─ 결과 범위 기준
      │    ├─ INNER JOIN
      │    └─ OUTER JOIN
      │         ├─ LEFT
      │         ├─ RIGHT
      │         └─ FULL
      │
      ├─ 조건 형태 기준
      │    ├─ 등가조인 (=)
      │    └─ 비등가조인 (<, >, BETWEEN 등)
      │
      ├─ 조인 대상 기준
      │    └─ SELF JOIN
      │
      └─ 문법 방식
           └─ NATURAL JOIN
```

### 카테시안 곱(Cartesian Product)

- 두 테이블의 모든 행을 조합
- 조인 조건이 없을 때 발생
- `emp 행 수 × dept 행 수`

```sql
SELECT *
FROM A, B;

-- 또는
SELECT *
FROM A CROSS JOIN B;
```

### Inner Join

- 조인 조건을 만족하는 행만 반환
- 교집합 개념
- 기본 Join

```sql
SELECT e.ename, d.dname
FROM emp e
JOIN dept d
ON e.deptno = d.deptno;
```

### Outer Join

- 조건이 맞지 않아도 한쪽 또는 양쪽 데이터를 유지
- Left Outer Join
    - 왼쪽 유지
- Right Outer Join
    - 오른쪽 유지
- Full Outer Join
    - 양쪽 모두 유지

### 등가 조인 (Equi Join)

- `=` 조건을 사용하는 조인
- PK FK 관계에서 사용
- 대부분의 Join

```sql
// ANSI (권장)

select *
from emp e
join dept d on e.deptno = d.deptno;
```

```sql
// Oracle 구문 (구버전)

select *
from emp e, dept d
where e.deptno = d.deptno;
```

### 비등가 조인(Non-Equijoin)

- `=`이 아닌 비교 조건으로 조인
    - 범위 조건
    - 구간 매핑

```sql
select e.empno, e.sal, s.grade
from emp e join salgrade s
on e.sal between s.losal and s.hisal;
```

### Self Join

- 하나의 테이블을 두 번 사용해서 조인
- 같은 테이블을 서로 다른 역할로 사용
    - 계층 구조 표현 가능
    - 별칭 필수

```sql
SELECT e.ename AS 직원, m.ename AS 관리자
FROM emp e
JOIN emp m
ON e.mgr = m.empno;
```

### Natural Join

- 같은 이름 컬럼 자동 조인 → 예측 어려움

```sql
select *
from emp natural join dept;
```

## 서브쿼리(Subquery)

- 다른 SQL 안에 포함된 SQL
- 서브 쿼리 먼저 실행 → 결과를 외부 쿼리에 전달

```sql
서브쿼리 (위치 기준)
 ├─ SELECT → 스칼라 서브쿼리
 ├─ FROM → 인라인 뷰
 └─ WHERE → 일반 서브쿼리 (단일/다중행)
```

```sql
SELECT e.ename,
       
       -- 1) 스칼라 서브쿼리 (SELECT 절, 1값 반환)
       (SELECT d.dname 
        FROM dept d 
        WHERE d.deptno = e.deptno) AS dname,

       iv.avg_sal

FROM emp e

-- 2) 인라인 뷰 (FROM 절, 테이블처럼 사용)
JOIN (
    SELECT deptno, AVG(sal) AS avg_sal
    FROM emp
    GROUP BY deptno
) iv
ON e.deptno = iv.deptno

WHERE

-- 3) 다중행 서브쿼리
e.deptno IN (
    SELECT deptno 
    FROM dept 
    WHERE loc = 'NEW YORK'
)

AND

-- 4) 단일행 서브쿼리
e.sal > (
    SELECT AVG(sal) 
    FROM emp
)

AND

-- 5) 상관 서브쿼리 (외부 값 참조)
e.sal > (
    SELECT AVG(sal)
    FROM emp x
    WHERE x.deptno = e.deptno
);
```

### 스칼라 서브쿼리

- SELECT 절에서 사용
- 1행 1컬럼 반환

```sql
SELECT 컬럼
FROM 테이블
WHERE 컬럼 연산자 (서브쿼리);
```

### 인라인 뷰 (Inline View)

- FROM 절에 들어가는 서브쿼리
- 결과를 테이블처럼 사용
- 별칭 필수

```sql
SELECT *
FROM (
    SELECT deptno, AVG(sal) avg_sal
    FROM emp
    GROUP BY deptno
) t;
```

### 단일행 서브쿼리

- 결과가 1행 (컬럼 여러 개 가능)
- `=`, `>`, `<` 사용 가능

```sql
SELECT *
FROM emp
WHERE sal = (
    SELECT MAX(sal) FROM emp
);
```

### 다중행 서브쿼리

- 결과가 여러 행
- `IN`, `ANY`, `ALL` 사용

```sql
SELECT *
FROM emp
WHERE deptno IN (
    SELECT deptno FROM dept
);
```

### **WITH**

- 쿼리 안에서 이름을 붙인 임시 테이블 (CTE, Common Table Expression)
- 특징
    - 쿼리 가독성 ↑
    - 서브쿼리 재사용 가능
    - 복잡한 쿼리 단계별 분리 가능

```sql
WITH avg_table AS (
    SELECT deptno, AVG(sal) avg_sal
    FROM emp
    GROUP BY deptno
)
SELECT *
FROM avg_table
WHERE avg_sal > 2000;
```

```sql
처음엔 JOIN으로 해결
→ 복잡해지면 인라인 뷰
→ 자주 쓰면 VIEW
→ 쿼리 안에서 정리하려면 WITH
```

---

## 대량 INSERT 방식

1. values 반복
- 비효율적

```sql
INSERT INTO temp VALUES (...);
INSERT INTO temp VALUES (...);
```

1. select 기반 insert
- 여러 행 한 번에 삽입 가능
- 배치 표준 방식

```sql
INSERT INTO temp5(num)
SELECT id FROM temp4;
```

## CTAS (Create Table As Select)

- CREATE TABLE + SELECT를 한 번에 수행
    
    ```sql
    CREATE TABLE 테이블명 AS
    SELECT ...
    FROM ...
    WHERE ...;
    ```
    
1. 데이터를 포함해서 테이블을 생성하는 경우
    
    ```sql
    CREATE TABLE copyemp AS
    SELECT empno, ename, sal
    FROM emp
    WHERE deptno = 20;
    ```
    
2. 테이블 구조만 복사하는 경우
    
    ```sql
    CREATE TABLE copyemp2 AS
    SELECT *
    FROM emp
    WHERE 1=2;
    ```
    

---

## **DML 실패 원인 분석 (SELECT 정상, 쓰기 실패)**

### 상황

- `SELECT` 정상
- `INSERT / UPDATE / DELETE` 실패

### 경우 1: 트랜잭션 미종료

- COMMIT / ROLLBACK 안 해서 발생
    - 트랜잭션이 끝나지 않으면 락 유지
    - 다른 세션에서 DML 수행 불가
- 해결 방법

```sql
COMMIT;
-- 또는
ROLLBACK;
```

### 경우 2: **REDO LOG / ARCHIVE LOG 공간 부족**

| **로그 종류** | **저장 위치** |
| --- | --- |
| REDO LOG | Oracle이 관리하는 로그 파일 (DB 파일) |
| ARCHIVE LOG | OS 디렉토리 (archive log 경로) |
- Oracle은 모든 DML을 REDO LOG에 기록
- ARCHIVE MODE에서는 REDO LOG를 ARCHIVE LOG로 저장

```sql
-- archive log 경로
ARCHIVE LOG LIST;

-- redo log 위치
SELECT * FROM v$logfile;
```

- 디스크가 가득 차면 로그 기록 불가
- 해결 방법
    - 아카이브 로그 삭제
    - 디스크 용량 확보
    - 백업 수행
        - 데이터 + 로그를 안전하게 저장하는 작업
