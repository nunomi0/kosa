## 복습

### 익명 클래스(Anonymous Class)

- 이름이 없는 클래스를 선언과 동시에 생성하는 방식

```java
Thread th3 = new Thread(new Runnable() {
    @Override
    public void run() {
        // 실행 코드
    }
});
th3.start();
```

- `Runnable`을 익명 클래스로 즉석 구현
- `Thread`에 전달
- `start()`로 실행

### ‘동시’의 정확한 의미

- 멀티 스레드의 ‘동시’는 CPU를 점유 가능한 상태로 여러 개 만들어 놓는 것을 의미함
- 싱글 코어 → 번갈아 실행 (컨텍스트 스위칭)
- 멀티 코어 → 실제 병렬 가능

### 병렬 처리가 무조건 좋을까?

- X → 오히려 느려질 수도 있음
- 느려지는 이유
    1. 스레드 생성 비용
    2. 컨텍스트 스위칭
    3. 동기화 비용
- 작업이 많고 독립적이고 오래 걸리는 작업일 때 유리
    
    ex) 파일 처리, 네트워크 요청, 이미지 처리
    

### join()

- 이 스레드 끝날 때 까지 기다림

```sql
t.start();
t.join(); // 대기
System.out.println("끝");
```

---

## 수업 내용

## Thread Priority

- priority가 높을수록 CPU를 점유할 수 있는 확률을 높임
- 실무적으로 차이는 없다
    - 멀티 코어에서는 더 영향 X (코어가 여러 개면 동시에 실행되기 때문)
- 특징
    - 보장 아님 (확률 기반)
    - OS 스케줄러가 최종 결정 (OS마다 다르게 동작)
    - JVM 구현 + OS 정책 영향 받음

→ 대신 Thread Pool, ExecuterServiece 사용

## Thread Pool

- 미리 생성해둔 스레드 집합
- 작업이 들어오면 새 스레드를 만들지 않고 기존 스레드를 재사용
- 스레드 개수를 재한해서 안정성 확보
- 큐 기반 관리

```java
[Client 요청]
      ↓
[Task Queue]
      ↓
[Thread Pool (n개 스레드)]
      ↓
[작업 실행]
```

### ExcecutorService

- Thread Pool을 관리하는 인터페이스 (자바 API)
- 생성 방식 종류
    - `Executors.newFixedThreadPool(n)` : 스레드 n개 고정
    - `Executors.newCachedThreadPool()` : 제한 없이 생성, queue 없음, 사용 안하면 60초 후 제거
    - `Executors.newSingleThreadExecutor()` : 스레드 1개 → 작업 순서 보장
    - `Executors.newScheduledThreadPool(n)` : 지연/주기 실행 → 배치에 사용

```java
ExecutorService executor = Executors.newFixedThreadPool(3);

executor.submit(() -> {
    System.out.println(Thread.currentThread().getName());
});

executor.shutdown();
```

| **메서드** | **설명** |
| --- | --- |
| submit() | 작업 실행 (비동기) |
| execute() | Runnable 실행 |
| shutdown() | 정상 종료 |
| shutdownNow() | 즉시 종료 |
| awaitTermination() | 종료 대기 |

## Demon Thread

- 백그라운드에서 보조 작업을 수행하는 스레드
- 일반(사용자) 스레드가 모두 종료되면 자동 종료
    - `finally` 블록이 실행되지 않을 수 있음

```java
public static void main(String[] args) {
	AutoSaveThread autosavethread = new AutoSaveThread();
	autosavethread.setDaemon(true);
	autosaveThread.start();
```

- `main thread`가 종료되면 `autoSaveThread`도 같이 종료
- 적합한 작업
    - GC, 로그 기록, 캐시 정리, 모니터링
- 부적합한 작업
    - DB 저장, 파일 저장, 트랜젝션 처리

## 프로그램 패러다임

- 자바에서 명령형, 선언형 프로그래밍 둘 다 가능
    - 선언형 프로그래밍 시 Stream API 사용

```java
프로그래밍 패러다임
 ├─ 명령형
 └─ 선언형
      ├─ 함수형
      └─ (SQL 같은 것)
```

### 명령형 프로그래밍 (Imperative)

- 무엇을 어떻게 할 것인가?
- 프로그램이 어떻게 실행될지 절차를 직접 명시하는 방식
    - 실행 순서 제어 (위에서 아래)
    - 상태 변경 (변수 값 변경)
    - 제어문 사용 (for, while, if)

```java
int sum = 0;
for (int i : list) {
    if (i > 10) {
        sum += i;
    }
}
```

### 선언형 프로그래밍(Declarative)

- 무엇을 할 것인가?
- 프로그램이 무엇을 원하는지 결과만 표현하고 실행 방법은 추상화하는 방식
    - 내부 처리 로직은 숨김
    - 상태 변화 최소화
    - 결과 중심 표현
- 선언형 코드는 내부적으로 명령형 코드 위에 만들어짐 → 선언형은 명령형 구현 위에 추상화된 형태로 동작

```java
int sum = 0;
for (int i : list) {
    if (i > 10) {
        sum += i;
    }
}
```

## 함수형 프로그래밍

- 함수를 중심으로 프로그램을 구성하는 방식
    - 선언형 프로그래밍의 한 종류
    - 데이터가 하나씩 stream 통로를 지나가면서 filter, maptoInt, sum을 순서대로 처리 → 최종 결과가 나옴
- 특징
    1. 순수 함수
        - 동일한 입력 → 항상 동일한 출력
        - 외부 상태에 영향 없음
    2. 상태 변경 없음 (Immutable)
        - 기존 데이터를 수정하지 않음
        - 새로운 값 생성
    3. 함수 자체를 값처럼 사용 (First-class)
        - 함수를 변수, 파라미터로 전달 가능

## 람다 표현식

- 함수의 하나의 식으로 표현한 것
- 자바에서 함수처럼 동작하는 코드를 간단하게 작성하는 문법
- 자바는 함수를 단독으로 사용할 수 없음 → 클래스/인터페이스 내부에 존재해야 함
    
    → 함수적 인터페이스의 추상 메서드만 가능
    
- 많이 쓰는 연습 필요

### 함수형 인터페이스

- 추상 메서드가 1개만 있는 인터페이스

```java
@FunctionalInterface
interface MyLambdaFunction {
    int max(int a, int b);
}
```

### 람다식이 필요한 이유 (장점)

- 코드량을 줄일 수 있음 → 가독성 높음

```java
// 익명 클래스

MyLambdaFunction f = new MyLambdaFunction() {
    @Override
    public int max(int a, int b) {
        return a > b ? a : b;
    }
};
```

```java
// 람다식

MyLambdaFunction f = (a, b) -> a > b ? a : b;
```

- 병렬 프로그래밍 가능

### 람다식 단점

- 무명함수 재사용 불가능
- 디버깅 어려움
- 재귀적 구현 어려움
    - 빌려쓰는 것이기 때문

### @FunctionalInterface

- 하나의 추상 메서드만 가지는 인터페이스임을 명시하는 어노테이션
    - 인터페이스 선언에 붙임
    
    ```java
    @FunctionalInterface
    interface MyFunc {
        void run();
    }
    ```
    
- 컴파일 시점에 함수형 인터페이스 조건(추상 메서드 1개)를 검사
    - 람다용 인터페이스에 메서드 추가되는 순간 바로 차단
    
    ```java
    // 람다는 내부적으로
    A a = () -> {};
    
    // 다음 코드를 의미하기 때문
    A a = new A() {
        public void run() {}
    };
    ```
    

## Stream

- Java 8에서 추가된 데이터 처리 API (파이프라인)
- 컬렉션, 배열 데이터를 표준화된 방식으로 처리
- 원본 데이터 변경 없음 (read-only)
- 일회용 (최종 연산 후 재사용 불가)
    - 사용 시 다시 생성해야 함
- 처리 단계
    
    ```java
    // 1. 생성
    String[] arr = {"dd", "aaa", "CC", "cc", "h"};
    
    // 2. 중간 연산
    long count = Arrays.stream(arr)
        .filter(s -> s.length() <= 2)
        .distinct()
        .sorted(String.CASE_INSENSITIVE_ORDER)
        .limit(3)
        .count();
    
    // 3. 최종 연산
    System.out.println(count);
    ```
    
    1. 스트림 생성 (Source)
        - Stream 객체를 생성하는 단계
    2. 중간 연산 (Intermediate)
        - 원본의 데이터를 별도의 데이터로 가공하기 위한 중간 연산
        - 반환 타입: Stream
        - 여러 개 연결 가능
        
        | **연산** | **설명** |
        | --- | --- |
        | filter | 조건 필터 |
        | map | 값 변환 |
        | distinct | 중복 제거 |
        | sorted | 정렬 |
        | limit | 개수 제한 |
        | skip | 일부 건너뜀 |
        | peek | 디버깅 출력 |
    3. 최종 연산 (Terminal)
        - 반환 타입: 값 or 컬렉션
        - Stream의 요소를 소모하면서 연산이 수행되기 때문에 1번만 처리 가능
        
        | **연산** | **설명** |
        | --- | --- |
        | count | 개수 |
        | forEach | 반복 |
        | collect | 리스트 변환 |
        | findFirst | 첫 요소 |
        | anyMatch | 조건 검사 |

---

## 면접 예상 질문

#### 1. 멀티 스레드 환경에서 공유 자원 관리는 어떻게 하는가?

- 동기화로 Race Condition 방지
- 이때 자바에서 Syncronized 키워드를 사용할 수 있음
    - 공유 자원에 Lock을 걸어 한 번에 하나의 스레드만 접근하도록 제어

#### 2. ArrayList는 동기화를 보장하나요?

- 기본적으로는 동기화 보장 X
- `Collections.synctronizedList()` 로 동기화 보장 가능

#### 3. 병렬 처리하면 성능이 좋아지는가?

- 항상 좋아지지는 않음
- CPU 연산이 많은 작업에서는 빨라짐
    - CPU bound: 대용량 배열 정렬, 이미지 처리, 수치 계산, 로그 분석 등
- 스레드 생성, context, switching, 동기화, 공유 자원 경쟁 문제가 생기면 오히려 느려질 수 있음
    - I/O bound: DB 조회, 네트워크 요청, 파일 입출력

#### 3. 웹에서 스레드를 많이 사용하는 곳?

- WAS(Web Application Server)
    
    → WAS는 요청마다 스레드를 할당해서 애플리케이션 로직을 실행하기 때문
    
    - 처리 흐름: 요청 → 스레드 풀에서 스레드 할당 → 로직 실행 → 응답 → 반환
    - 요청 수 ≈ 필요한 스레드 수
    - Tomcat은 스레드 풀 기반 처리를 함
    - WAS는 로직 실행(CPU), DB 조회(I/O), 외부 API 호출(네트워크)를 모두 담당 → 여러 스레드를 병렬로 사용
    - 용어 정리
        - 서버: 요청을 받아서 처리하고 응답을 주는 프로그램 또는 컴퓨터(요청 처리 주체)
        - 웹 서버: HTTP 요청을 받아 정적 리소스를 반환하는 서버
        - 애플리케이션: 사용자의 요청을 처리하는 실제 로직 코드 (비즈니스 로직 코드)
        - 웹 애플리케이션 서버: 웹 요청을 받아 코드를 실행하고 결과를 생성하는 서버 (동적 처리)

#### 4. 스레드란?

- 프로세스 내부의 실행 단위
    - 같은 메모리(heap) 공유
    - stack은 각자 별도

#### 5. 스레드 생성 방법?

1. Thread 상속
    - Java는 단일 상속만 가능 → Thread 상속 시 다른 클래스를 상속할 수 없다는 단점
    
    ```java
    class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("스레드 실행");
        }
    }
    
    public class Main {
        public static void main(String[] args) {
            MyThread t = new MyThread();
            t.start();
        }
    }
    ```
    
2. Runnable 구현 (권장)
    - Runnable은 인터페이스이기 때문에 다른 클래스를 상속하면서도 구현 가능 → 구조적으로 유연
    - 작업과 실행 주체 분리 가능
    
    ```java
    class MyTask implements Runnable {
        @Override
        public void run() {
            System.out.println("스레드 실행");
        }
    }
    
    public class Main {
        public static void main(String[] args) {
            Thread t = new Thread(new MyTask());
            t.start();
        }
    }
    ```
    

#### 6. 동기화란?

- 공유 자원에 대해 한 번에 하나의 스레드만 접근하도록 제어해서 데이터 일관성을 유지하는 것
    - Java에서는 `synchronized` 키워드로 구현
    
    ```java
    public synchronized void increase() {
        count++;
    }
    ```
    
- 여러 사람에게 자원이 동일한 상태로 보이는 것

#### 7. 스레드와 동기화는 무슨 관계가 있는가?

- 스레드가 동시에 작업하면 데이터가 덮어쓰기 되거나 잘못된 값이 들어갈 수 있음 → Race Condition → 공유 자원에 대해 동기화가 필요

#### 8. 멀티 스레드?

- 하나의 프로세스 내에서 여러 스레드를 사용하여 작업을 **동시에** 처리하는 것

#### 9. 멀티 스레드의 문제점?

1. Race Condition
2. Deadlock
3. Context Switching 비용으로 인한 성능 저하

#### 10. 데몬 스레드?

- 백그라운드에서 보조 역할을 하는 스레드
- 모든 일반 스레드 종료 시 데몬 스레드도 자동으로 함께 종료
- ex) GC, 로그 출력, 캐시 정리

#### 11. 스레드는 왜 추상(abstract) 클래스가 아닌가?

- Thread는 실행을 담당하는 클래스이며, Runnable 작업을 전달받아 실행할 수 있어야 함 → 객체를 직접 생성할 수 있어야 하므로 abstract 클래스가 아님
    - 기본 `run()` 메서드를 내부에 구현

#### 12. Thread 클래스 상속 시 `run()` 오버라이드가 필수가 아닌 이유?

- Thread 클래스에는 기본 `run()` 메서드가 구현되어 있음
- Runnable을 생성자로 전달하면 해당 `run()`에서 내부적으로 Runnable의 `run()`을 호출 → Thread를 상속할 경우 `run()` 오버라이드는 필수가 아님

```java
class Thread {
    private Runnable target;

    public Thread(Runnable target) {
        this.target = target;
    }

    public void run() {
        if (target != null) {
            target.run();
        }
    }
}
```

```java
Runnable task = () -> {
    System.out.println("작업 실행");
};

Thread t = new Thread(task);
t.start();
```
