# PL/SQL 고급 자원

- 메모리와 CPU를 많이 쓰는 자원
- Cursor, Procedure, Function, Trigger

## Cursor

- 여러 행 조회 결과를 한 행씩 꺼내서 처리하는 기능
- 행단위 데이터 처리 방법
- 여러 건의 데이터를 처리하는 방법 제공
- `SQL%`
    - 암시적 커서 속성
    
    | **속성** | **의미** |
    | --- | --- |
    | SQL%FOUND | 영향받은 행 있음 |
    | SQL%NOTFOUND | 영향받은 행 없음 |
    | SQL%ROWCOUNT | 영향받은 행 개수 |
    | SQL%ISOPEN | 커서 열림 여부 |

```sql
DECLARE
  CURSOR c1 IS
    SELECT empno, ename, sal
    FROM emp
    WHERE deptno = 30;

  vempno emp.empno%TYPE;
  vename emp.ename%TYPE;
  vsal emp.sal%TYPE;
BEGIN
  OPEN c1;

  LOOP
    FETCH c1 INTO vempno, vename, vsal;
    EXIT WHEN c1%NOTFOUND;

    DBMS_OUTPUT.PUT_LINE(vempno || '-' || vename || '-' || vsal);
  END LOOP;

  CLOSE c1;
END;
/
```

```sql
// OPEN, FETCH, CLOSE를 직접 쓰지 않아도 됨 -> Oracle이 자동으로 처리
DECLARE
  CURSOR emp_curr IS
    SELECT empno, ename
    FROM emp;
BEGIN
  FOR emp_record IN emp_curr
  LOOP
    DBMS_OUTPUT.PUT_LINE(emp_record.empno || '-' || emp_record.ename);
  END LOOP;
END;
/
```

```sql
DECLARE
  v_sal_total NUMBER(10,2) := 0;

  CURSOR emp_cursor IS
    SELECT empno, ename, sal
    FROM emp
    WHERE deptno = 20
      AND job = 'CLERK'
    ORDER BY empno;
BEGIN
  FOR emp_record IN emp_cursor
  LOOP
    v_sal_total := v_sal_total + emp_record.sal;

    DBMS_OUTPUT.PUT_LINE(
      RPAD(emp_record.empno, 6) ||
      RPAD(emp_record.ename, 12) ||
      LPAD(TO_CHAR(emp_record.sal, '$99,999,990.00'), 16)
    );
  END LOOP;

  DBMS_OUTPUT.PUT_LINE('20번 부서의 합 ' || v_sal_total);
END;
/
```

```sql
DECLARE
  result number := 0;

  CURSOR emp_curr IS
    SELECT empno, ename, sal, deptno, comm
    FROM emp;
BEGIN
  FOR vemp IN emp_curr
  LOOP
    IF vemp.deptno = 20 THEN
      result := vemp.sal + NVL(vemp.comm, 0);

      INSERT INTO cursor_table(empno, ename, sal, deptno, comm, totalsum)
      VALUES(vemp.empno, vemp.ename, vemp.sal, vemp.deptno, vemp.comm, result);

    ELSIF vemp.deptno = 10 THEN
      result := vemp.sal;

      INSERT INTO cursor_table(empno, ename, sal, deptno, comm, totalsum)
      VALUES(vemp.empno, vemp.ename, vemp.sal, vemp.deptno, vemp.comm, result);

    ELSE
      DBMS_OUTPUT.PUT_LINE('ETC');
    END IF;
  END LOOP;
END;
/
```

```sql
IF v_job_type = '정규직' THEN
  v_pay := NVL(v_monthly, 0);

ELSIF v_job_type = '시간직' THEN
  v_pay := NVL(v_hours, 0) * NVL(v_hourly, 0);

ELSIF v_job_type = '일용직' THEN
  v_pay := NVL(v_hourly, 0) + NVL(v_meal, 0);

ELSE
  v_pay := 0;
END IF;
```

## Procedure

- PL/SQL 코드를 DB 안에 저장하는 객체
    - 한 번 만들어두면 이름으로 호출 가능
- 장점
    1. 재사용: 같은 로직 계속 호출 가능
    2. 네트워크 감소: APP에서 SQL을 여러 번 보내지 않아도 됨
    3. 권한 관리: APP에 테이블 조작 권한 부여를 줄임
- 명명 규칙
    - sp: oracle에서 만든 것
    - usp: 개인이 만든 것

```sql
CREATE OR REPLACE PROCEDURE usp_emplist
IS
BEGIN
  UPDATE emp
  SET job = 'TTT'
  WHERE deptno = 30;
END;
/

EXEC usp_emplist;
```

```sql
CREATE OR REPLACE PROCEDURE usp_update_emp
(
  vempno emp.empno%TYPE
)
IS
BEGIN
  UPDATE emp
  SET sal = 0
  WHERE empno = vempno;
END;
/
```

```sql
CREATE OR REPLACE PROCEDURE usp_getemplist
(
  vempno emp.empno%TYPE
)
IS
  vname emp.ename%TYPE;
  vsal emp.sal%TYPE;
BEGIN
  SELECT ename, sal
  INTO vname, vsal
  FROM emp
  WHERE empno = vempno;

  DBMS_OUTPUT.PUT_LINE('이름은 : ' || vname);
  DBMS_OUTPUT.PUT_LINE('급여는 : ' || vsal);
END;
/
```

```sql
CREATE OR REPLACE PROCEDURE app_get_emplist
(
  vempno IN emp.empno%TYPE,
  vename OUT emp.ename%TYPE,
  vsal OUT emp.sal%TYPE
)
IS
BEGIN
  SELECT ename, sal
  INTO vename, vsal
  FROM emp
  WHERE empno = vempno;
END;
/
```

```sql
CREATE OR REPLACE PROCEDURE usp_EmpList
(
  p_sal IN number,
  p_cursor OUT SYS_REFCURSOR
)
IS
BEGIN
  OPEN p_cursor FOR
    SELECT empno, ename, sal
    FROM emp
    WHERE sal > p_sal;
END;
/
```

```sql
CREATE OR REPLACE PROCEDURE usp_insert_emp
(
  vempno IN emp.empno%TYPE,
  vename IN emp.ename%TYPE,
  vjob IN emp.job%TYPE,
  p_outmsg OUT VARCHAR2
)
IS
BEGIN
  INSERT INTO usp_emp(empno, ename, job)
  VALUES(vempno, vename, vjob);

  COMMIT;
  p_outmsg := 'success';

EXCEPTION
  WHEN OTHERS THEN
    p_outmsg := SQLERRM;
    ROLLBACK;
END;
/
```

## Function

- 값을 반환하는 PL/SQL 객체
    - 조회 보조 계산, 변환, 공통 비즈니스 규칙 등에 사용
- Procedure와 달리 RETURN이 있음
- SELECT 안에서 사용 가능

| **구분** | **Procedure** | **Function** |
| --- | --- | --- |
| 목적 | 작업 수행 | 값 반환 |
| RETURN | 필수 아님 | 필수 |
| SQL에서 사용 | 일반적으로 제한적 | SELECT 안에서 사용 가능 |

```sql
CREATE OR REPLACE FUNCTION f_max_sal
(
  s_deptno emp.deptno%TYPE
)
RETURN NUMBER
IS
  max_sal emp.sal%TYPE;
BEGIN
  SELECT MAX(sal)
  INTO max_sal
  FROM emp
  WHERE deptno = s_deptno;

  RETURN max_sal;
END;
/

SELECT *
FROM emp
WHERE sal = f_max_sal(10);
```

```sql
CREATE OR REPLACE FUNCTION f_callname
(
  vempno emp.empno%TYPE
)
RETURN VARCHAR2
IS
  v_name emp.ename%TYPE;
BEGIN
  SELECT ename || '님'
  INTO v_name
  FROM emp
  WHERE empno = vempno;

  RETURN v_name;
END;
/
```

```sql
CREATE OR REPLACE FUNCTION f_get_dname
(
  vempno emp.empno%TYPE
)
RETURN VARCHAR2
IS
  v_dname dept.dname%TYPE;
BEGIN
  SELECT dname
  INTO v_dname
  FROM dept
  WHERE deptno = (
    SELECT deptno
    FROM emp
    WHERE empno = vempno
  );

  RETURN v_dname;
END;
/
```

## Trigger

- 특정 테이블에 INSERT, UPDATE, DELETE가 발생했을 때 자동으로 실행되는 PL/SQL 객체
- 적용 예시
    - 변경 이력 저장
    - 감사 로그
    - 생성/수정일 자동 입력
    - 삭제 방지
    - 데이터 검증
    - 연관 테이블 자동 반영
    - 중요 데이터 백업

| **구분** | **실행 시점** |
| --- | --- |
| BEFORE | DML 실행 전 |
| AFTER | DML 실행 후 |

| **구분** | **조건** | **실행 횟수** |
| --- | --- | --- |
| 문장 레벨 | FOR EACH ROW 없음 | SQL 1번당 1번 |
| 행 레벨 | FOR EACH ROW 있음 | 변경된 행마다 1번 |

```sql
CREATE OR REPLACE TRIGGER 트리거명
BEFORE 또는 AFTER
INSERT 또는 UPDATE 또는 DELETE
ON 테이블명
FOR EACH ROW
BEGIN
  실행문;
END;
/
```

```sql
CREATE OR REPLACE TRIGGER tri_01
AFTER INSERT ON tri_emp
BEGIN
  DBMS_OUTPUT.PUT_LINE('신입사원 입사');
END;
/
```

```sql
CREATE OR REPLACE TRIGGER tri_02
AFTER UPDATE ON tri_emp
BEGIN
  DBMS_OUTPUT.PUT_LINE('신입사원 수정');
END;
/
```

```sql
CREATE OR REPLACE TRIGGER tri_03
AFTER DELETE ON tri_emp
BEGIN
  DBMS_OUTPUT.PUT_LINE('신입사원 삭제');
END;
/
```

```sql
CREATE OR REPLACE TRIGGER emp_audit_tr
AFTER INSERT OR UPDATE OR DELETE ON emp2
BEGIN
  IF INSERTING THEN
    INSERT INTO emp_audit
    VALUES(emp_audit_tr.nextval, USER, 'inserting', SYSDATE);

  ELSIF UPDATING THEN
    INSERT INTO emp_audit
    VALUES(emp_audit_tr.nextval, USER, 'updating', SYSDATE);

  ELSIF DELETING THEN
    INSERT INTO emp_audit
    VALUES(emp_audit_tr.nextval, USER, 'deleting', SYSDATE);
  END IF;
END;
/
```

| **DML** | :OLD | :NEW |
| --- | --- | --- |
| INSERT | 없음 | 새로 들어가는 값 |
| UPDATE | 변경 전 값 | 변경 후 값 |
| DELETE | 삭제 전 값 | 없음 |

```sql
CREATE OR REPLACE TRIGGER emp_audit_tr
AFTER INSERT OR UPDATE OR DELETE ON emp2
FOR EACH ROW
BEGIN
  IF INSERTING THEN
    INSERT INTO emp_audit
    VALUES(emp_audit_tr.nextval, USER, 'inserting', SYSDATE, :OLD.deptno, :NEW.deptno);

  ELSIF UPDATING THEN
    INSERT INTO emp_audit
    VALUES(emp_audit_tr.nextval, USER, 'updating', SYSDATE, :OLD.deptno, :NEW.deptno);

  ELSIF DELETING THEN
    INSERT INTO emp_audit
    VALUES(emp_audit_tr.nextval, USER, 'deleting', SYSDATE, :OLD.deptno, :NEW.deptno);
  END IF;
END;
/
```

```sql
CREATE OR REPLACE TRIGGER tri_order2
BEFORE INSERT ON tri_order
FOR EACH ROW
BEGIN
  IF :NEW.ord_code NOT IN ('desktop') THEN
    RAISE_APPLICATION_ERROR(-20002, '제품코드 오류');
  END IF;
END;
/
```

```sql
CREATE OR REPLACE TRIGGER tri_order2
BEFORE INSERT ON tri_order
FOR EACH ROW
BEGIN
  IF :NEW.ord_code NOT IN ('desktop') THEN
    RAISE_APPLICATION_ERROR(-20002, '제품코드 오류');
  END IF;
END;
/
```

```sql
CREATE OR REPLACE TRIGGER update_t_01
AFTER UPDATE ON t_01
FOR EACH ROW
BEGIN
  UPDATE t_02
  SET pname = :NEW.pname
  WHERE no = :OLD.no;
END;
/
```

```sql
DECLARE
  v_ename emp.ename%TYPE := '&p_ename';
  v_err_code NUMBER;
  v_err_msg VARCHAR2(255);
BEGIN
  DELETE emp
  WHERE ename = v_ename;

  IF SQL%NOTFOUND THEN
    RAISE_APPLICATION_ERROR(-20002, 'my no data found');
  END IF;

EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;

    v_err_code := SQLCODE;
    v_err_msg := SQLERRM;

    DBMS_OUTPUT.PUT_LINE('에러 번호 : ' || TO_CHAR(v_err_code));
    DBMS_OUTPUT.PUT_LINE('에러 내용 : ' || v_err_msg);
END;
/
```
