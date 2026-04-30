## 열거형(enum)

- 상수들의 집합을 정의할 때 사용하는 특별한 타입
- 장점
    1. `final static` 상수보다 가독성이 좋음, 의미를 바로 이해 가능
    2. 타입 안정성 보장
    3. IDE 지원으로 자동완성 → 오타 방지 효과
    4. 확장 용이
- 언제 enum을 쓰고 언제 DB 테이블로 분리할까?
    - enum: 값이 고정적인 경우 (성별, 계절, 요일)
    - DB 테이블: 값이 추가, 변경될 수 있는 경우 (부서, 카테고리)

```java
// 요일을 표현하는 열거형
enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

public class EnumExample {
    public static void main(String[] args) {
        Day today = Day.MONDAY;

        // 출력
        System.out.println("오늘은 " + today);

        // switch문과 함께 활용
        switch (today) {
            case MONDAY:
                System.out.println("한 주의 시작입니다!");
                break;
            case FRIDAY:
                System.out.println("불금이에요!");
                break;
            case SUNDAY:
                System.out.println("휴일이네요~");
                break;
            default:
                System.out.println("평범한 날입니다.");
        }
    }
}
```

```java
// 주문 상태를 표현하는 열거형
enum OrderStatus {
    ORDERED,     // 주문 완료
    SHIPPED,     // 배송 중
    DELIVERED,   // 배송 완료
    CANCELED     // 주문 취소
}

class Order {
    private int id;
    private OrderStatus status;

    public Order(int id) {
        this.id = id;
        this.status = OrderStatus.ORDERED; // 기본 상태는 주문 완료
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void printStatus() {
        System.out.println("주문번호 " + id + "의 상태: " + status);
    }
}

public class EnumOrderExample {
    public static void main(String[] args) {
        Order order = new Order(1001);
        order.printStatus(); // 주문 완료

        order.setStatus(OrderStatus.SHIPPED);
        order.printStatus(); // 배송 중

        order.setStatus(OrderStatus.DELIVERED);
        order.printStatus(); // 배송 완료
    }
}
```

## BigDecimal

- 정확한 소수 계산을 위한 클래스
    - 소수를 2진수로 저장하면서 오차가 생기기 때문
    - 금융, 결제 등에서 사용
- 숫자를 문자열처럼 정밀하게 저장

```java
BigDecimal a = new BigDecimal("0.1"); // 문자열로 저장
BigDecimal b = new BigDecimal("0.2");
System.out.println(a.add(b));  // 0.3
```

- 연산은 메서드를 사용

```java
a.add(b);       // 덧셈
a.subtract(b);  // 뺄셈
a.multiply(b);  // 곱셈
a.divide(b);    // 나눗셈
```

## Inner Class

- 바깥 클래스의 자원에 쉽게 접근하기 위해 클래스 안에 정의하는 클래스
    - 객체 간 통신보다 클래스 내부에서 데이터를 다루는 것이 더 편하기 때문에 사용
- 생성자를 만들 필요 없이 바로 OuterClass에 접근 가능
    - `private` 멤버에도 접근 가능
- AWT, Swint, Android App 에서 많이 사용

```java
class OuterClass {

	public int pdata = 10;
	private int data = 30;

	// inner class (자원에 대한 접근을 편하게)
	class InnerClass {
		void msg() {
			System.out.println("outer class data : " + data);
	}
}
```

```java
public class Ex10_innerClass {
	public static void main(Stirng[] args) {
		OuterClass outobj = new OuterClass();
		
		OuterClass.InnerClass innerObj = outobj.new InnerClass();
		innerObj.msg(); // OuterClass에 대한 접근 용이
	
```

## AnonymousClass

- 익명 클래스 → 클래스를 따로 정의하지 않고 객체를 만드는 방법 (1회용 클래스)
- 장점: 코드량을 줄일 수 있음, 편리함
- 단점: 재사용성 없음
- 이벤트 처리, 스레드 객체(Runnable), 람다식, 스트림(Stream API) 에서 사용
- 추상 클래스, 인터페이스는 상속하고 구현하는 클래스가 반드시 필요 → 익명 클래스로 1회성으로 강제 구현이 가능

```java
// 추상 클래스 (기존 방식)

abstract class Person { // 독자적으로 new 불가 -> 누군가 강제로 구현하고 재정의 필요
	abstract void eat(); // 재정의 강제
}

class Man extends Person {
	@Override
	void eat() {
		System.out.println("Person의 eat 함수 구현");
	}
}
```

```java
// 익명 클래스

Person p = new Person() {
    @Override
    void eat() {
        System.out.println("1회용으로 바로 구현");
    }
};
```

```java
interface Eatable {
    void eat();
}

class Test {
    void method(Eatable e) {
        // Eatable 구현한 자식 객체의 주소 (다형성)
        e.eat();
    }
}

// 호출 시 익명 클래스로 바로 넘김
test.method(new Eatable() {
    public void eat() {
        System.out.println("1회용 구현");
    }
});
```

## AWT(Abstract Window Toolkit)

- 자바에서 GUI를 만들기 위한 클래스 라이브러리
    - Client-Server 프로그램용
        - 클라이언트(요청하는 쪽)와 서버(응답하는 쪽)로 나뉘어 동작하는 프로그램
- 컴포넌트: 이미 만들어진 UI가 있는 객체
    - ex) Button, Label, TextField

```java
class MyFrame extends Frame {
    public MyFrame(String title) {
        super(title);
    }
}
```

```java
public class Ex12_awt_Frame {
    public static void main(String[] args) {
        MyFrame my = new MyFrame("login");
        my.setSize(400, 350);
        my.setVisible(true);
        my.setLayout(new FlowLayout());

        Button btn = new Button("one button");
        my.add(btn);
    }
}
```

- 버튼 이벤트
    - 이벤트의 3요소: 소스(버튼), 행위(클릭), 감지기(핸들러)

```java
BtnClickHandle handler = new BtnClickHandler(); // 핸들러
btn.addAdctionlistener(handler) // click 이벤트 발생 시 호출
```

---

## Thread

- 프로세스 내부에서의 실제 실행 흐름 단위
- 프로세스는 최소 1개의 스레드를 가짐

### 메모리 구조

- Stack: 스레드마다 **개별 소유**
- Heap: 모든 스레드가 **공유**
    - 인스턴스 객체
    - static 변수

### 싱글 스레드 (기존 방식)

- Stack 1개
- 실행 흐름 1개
- 순차 실행
- JVM → OS → Stack 메모리 → `main()` 함수가 최초로 올라가서 실행

### 멀티 스레드

- Stack 여러 개
    - 스레드 간 Heap은 공유
- 실행 흐름 여러 개
- 동시에 실행 (정확히는 CPU 스케줄링 기반 병행 실행)
    - 여러 스레드가 CPU를 점유할 수 있는 상태가 됨

### 스레드 생성 방법

1. `Thread` 클래스 상속
    - `start()` 호출 시 새로운 스레드 생성
    - 내부적으로 `run()` 실행
    
    ```java
    class Task extends Thread {
        @Override
        public void run() {
            // 실행 코드
        }
    }
    
    Task t = new Task();
    t.start();
    ```
    
2. `Runnable` 인터페이스로 구현 (권장)
    - `Runnable`은 스레드 아님
    - 실행 로직만 정의
    - `Thread`가 실행 담당
    
    ```java
    class Task implements Runnable {
        @Override
        public void run() {
            // 실행 코드
        }
    }
    
    Thread th = new Thread(new Task());
    th.start(); // 새로운 스레드 생성 후 run() 실행
    ```
    

### **Thread가 추상클래스가 아닌 이유**

- `new Thread()`로 직접 생성 가능해야 함
- 추상클래스면 인스턴스 생성 불가

### Thread 제어

#### join()

- 다른 스레드가 끝날 때까지 대기

#### interrupt()

- 실행 중인 스레드에 종료 요청

#### isInterrupted()

- 인터럽트 요청 여부 확인

---

## 동기화(Synchronization)

- 멀티 스레드에서 가장 중요한 문제 → 공유 자원을 어떻게 처리하는가
- Vector, ArrayList 에서 언급되었던 개념
    
    
    |  | Vector | ArrayList |
    | --- | --- | --- |
    | Lock | O | X / 선택적 |
    | 동기화 보장 | O | X / 선택적 |
    | 성능 | X | O |

### Synchronized

- 하나의 스레드만 접근 가능하도록 제한 (Lock)
    - 객체, 함수(메서드) 단위 Lock 가능

```java
class Room {
    synchronized void openDoor(String name) {
        System.out.println(name + " 입장");
    }
}
```

```java
Class User Extends Thread {
	Room room;
	String who;
	
	User(String name, Room room){
		this.who = name;
		this.room = room;
	}
	
	@Override
	public void run() {
		room.openDoor(this.who);
	}
```
