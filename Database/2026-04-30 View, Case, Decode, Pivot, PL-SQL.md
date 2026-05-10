## View

- SQL 조회문을 저장한 가상 테이블
    - 실제 데이터를 저장하는 테이블이 아님
- 사용 이유
    1. 편리성
        - 복잡한 쿼리 + 서브 쿼리를 통한 조회를 단순화해서 재사용 가능
    2. 보안
        - 원본 테이블 전체를 노출하지 않아도 됨
- 읽기 전용으로 사용 (DML 작업 권장 X)

```sql
CREATE [OR REPLACE] [FORCE | NOFORCE] VIEW view_name
[(alias, alias, ...)]
AS subquery
[WITH CHECK OPTION [CONSTRAINT constraint_name]]
[WITH READ ONLY];
```

```sql
create view emp_dept_view
as
select e.empno,
       e.ename,
       d.dname
from emp e join dept d
on e.deptno = d.deptno;
```

```sql
select *
from emp_dept_view;
```

## Case

- 조건에 따라 다른 값을 반환하는 SQL 조건문
- Java의 `if`, `else`, `switch` 와 비슷함

```sql
case 컬럼
    when 값 then 결과
    else 결과
end
```

```sql
select ename,
       sal,
       case
           when sal >= 3000 then 'HIGH'
           when sal >= 2000 then 'MID'
           else 'LOW'
       end grade
from emp;
```

## Decode

- 특정 값에 따라 다른 결과를 반환하는 Oracle 전용 함수
    - Oracle의 옛날 방식 조건문 (레거시)

```sql
decode(
    컬럼,
    값1, 결과1,
    값2, 결과2,
    기본값
)
```

```sql
select ename,
       deptno,
       decode(
           deptno,
           10, '인사팀',
           20, '개발팀',
           '기타'
       ) dept_name
from emp;
```

## Pivot

- 행(Row) 형태의 데이터를 열(Column) 형태로 회전시켜 출력
- 집계 데이터를 표 형태로 보기 쉽게 만들 수 있음
- Oracle 11g 이상부터 지원

```sql
select *
from (
    select deptno, job, sal
    from emp
)
pivot(
    sum(sal)
    for job
    in (
        'MANAGER' as manager,
        'CLERK' as clerk
    )
);
```

```sql
-- 예시 데이터
DEPTNO JOB       SAL
10     MANAGER   2450
10     CLERK     1300
20     MANAGER   2975

-- 결과
DEPTNO MANAGER CLERK
10     2450    1300
20     2975    NULL
```

## PL-SQL

- Oracle’s Procedural Language extension to SQL
- Oracle DB에서 사용하는 절차형 SQL 언어
    - 변수 정의
    - 조건 처리 (if)
    - 반복 처리 (loop, while, for)
- 블록 구조로 동작 (begin~end)
    - 블록 종료 시 변수(메모리) 삭제됨
- Oracle 내부에 PL-SQL 전용 컴파일러와 실행 엔진 존재
- 구조
    - 선언부: 변수 정의
    - 실행부: 실제 로직
    - 예외부: 오류 처리
    - 종료부: `end`

```sql
-- 변수 선언
declare
  vno number;
  
-- 실행
begin
  vno := 100;
  dbms_output.put_line(vno);
  
-- 예외 처리
exception
  when others then
    dbms_output.put_line('error');
    
-- 종료
end;
```

### 변수 선언

- 값을 저장하는 메모리 공간
- DECLARE 영역에서 선언
- 선언하면서 값 저장 가능 (`:=`: 대입)

```sql
v_count number := 10;
```

- 사용자 입력 변수 받을 때는 `&` 사용

```sql
v_no number := '&NO';
```

- `%`: 특정 객체의 속성(attribute)이나 타입 정보를 참조
- `%TYPE`: 변수 타입 자동 참조
    - 특정 칼럼 타입을 그대로 사용

```sql
v_empno emp.empno%TYPE;
```

- `%ROWTYPE`: 행 전체를 변수로 저장

```sql
v_emp emp%ROWTYPE;

v_emp.empno
v_emp.ename
v_emp.sal

SELECT *
INTO v_emp
FROM emp
WHERE empno = 7788;
```

### 제어문

#### 조건문

- `if`
- `elsif`
- `else`

```sql
IF 조건 THEN
ELSIF 조건 THEN
ELSE
END IF;
```

- `case`

```sql
CASE 변수
    WHEN 값 THEN 결과
END
```

#### 반복문

- `loop`

```sql
LOOP
   실행문;
END LOOP;
```

- `while`

```sql
WHILE 조건 LOOP
   실행문;
END LOOP;
```

- `for`

```sql
FOR 변수 IN 시작..끝 LOOP
END LOOP;
```

### 예외 처리

- PL-SQL에서 오류 발생 시 프로그램이 비정상 종료되지 않도록 처리하는 기능

```sql
BEGIN
  실행문;
EXCEPTION
  WHEN 예외명 THEN
    처리문;
END;
/
```

```sql
exception
  when no_data_found then
    dbms_output.put_line('데이터 없음');
  when too_many_rows then
    dbms_output.put_line('행이 너무 많음');
  when others then
    dbms_output.put_line('기타 에러');
```

| **예외** | **발생 상황** |
| --- | --- |
| NO_DATA_FOUND | SELECT INTO 결과가 0행 |
| TOO_MANY_ROWS | SELECT INTO 결과가 2행 이상 |
| ZERO_DIVIDE | 0으로 나눔 |
| VALUE_ERROR | 변수 크기 초과, 타입 변환 오류 |
| DUP_VAL_ON_INDEX | PK, UNIQUE 중복 |
| OTHERS | 위에서 잡지 못한 나머지 오류 |
