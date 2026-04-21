## 요구사항 정의

요구사항 정의를 명확히 하기 위해서는 다음과 같은 과정과 고려가 필요하다. 도메인에 대한 분석과 상호 간의 이해를 바탕으로 원활한 커뮤니케이션을 할 수 있도록 해야한다.

1. 시스템 정의
2. 시나리오
3. 근거가 시나리오가 될 수 있어야 함

---

## 복습

### 상속

- 단일 상속 원칙, 계층적 상속을 원칙으로 함
- 상속을 하는 이유?
    - 재사용성 때문

### 객체지향 설계

- 초기 비용이 많이 드는 작업
- 재사용성을 위함

### 클래스의 관계

1. 상속: ~는 ~이다
2. 포함: ~는 ~를 가지고 있다

### 재정의

- 오버로딩: 하나의 이름으로 여러 가지 기능을 하는 함수
    - 파라미터 개수와 타입을 달리 해서 구현
- 오버라이딩: 상속 관계에서 재정의
    - 함수의 내용만 바꾸는 것
    - 부모가 갖고있는 메서드 이름을 자식에서도 똑같이 만들고 싶음
    - 문제점: 부모 메서드 접근 불가 → super() 호출
- final
    - final class, final 메서드 → 상속 불가

### 생성자

- 초기화를 보장하는 코드
    - 생성자에 필드를 파라미터로 넣는다
    - 생성자에 파라미터를 강제하면 객체를 만들 때 반드시 초기값을 넣어야만 한다 → 필드가 비어있는 상태로 쓰이는 것을 방지할 수 있다
    

---

## 접근 제어자

<aside>

private < default < protected < public

</aside>

| 제어자 | 같은 클래스 | 같은 패키지 | 다른 패키지 자식 | 완전 다른 곳 |
| --- | --- | --- | --- | --- |
| `private` | ✅ | ❌ | ❌ | ❌ |
| `default` | ✅ | ✅ | ❌ | ❌ |
| **`protected`** | ✅ | ✅ | ✅ (상속받으면) | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

### protected

- 같은 패키지에서
    - default 처럼 사용
    
    ```java
    package kr.or.kosa;
    
    public class Pclass {
    	private int p;
    	int d;
    	protected int pr; // 같은 패키지에서 default
    	public int pu;
    	
    	// 객체 입장에서는 default
    	protected voidm() {
    	
    	}
    ```
    
- 다른 패키지에서
    - 상속 없는 경우 → 닫혀 있음
    - 상속 있는 경우 (자식 클래스 내부) → public 처럼 쓸 수 있음

```java
class Dclass {
	private int p; // 가장 강력한 캡슐화
	int d; // default
	protected int pr;
	public int pu; // 가장 오픈
}

public class Ex07 {
	public static void main(String[] args) {
		Dclass dclass = new Dclass();
		dclass.d; // default
		dclass.pr; // protected
		dclass.pu;
		// dclass.p;
		
		Pclass pclass = new Pclass();
		
	}
}

```

protected 설계자 쓰는 이유

- 함수를 사용하려면 재정의 해라 → 강제성
    - public도 재정의는 가능하지만 강제성이 없음
- "이 메서드는 외부에서 함부로 쓰지 마. 근데 내 클래스를 상속 받은 자식이라면 써도 돼. 대신 네가 필요하면 재정의해서 써."

---

## 다형성(polymorphism)

- 사용 방법은 동일하지만 실행 결과가 다양하게 나오는 성질
- 같은 이름의 메서드나 연산이 상황에 따라 다르게 동작하는 것
    - 메소드를 오버라이딩해서 구현
- 부모 타입의 참조 변수는 자식 객체의 주소를 가질 수 있다
    - 부모는 자신의 자원만 접근 가능
    - 단, 자식이 부모의 함수를 재정의 했다면 부모가 자식을 볼 수 있음
        - 재정의 시 부모 자원으로 갔다가 → 자식 자원으로 떨어짐
    - 부모 생성자가 파라미터를 요구하면 자식은 `super()` 로 어떤 값을 넘길 지 알려줘야 함
        
        ```java
        class Animal {
            String name;
        
            Animal(String name) {   // 부모 생성자: 이름을 반드시 받아야 함
                this.name = name;
            }
        }
        
        class Dog extends Animal {
            Dog() {
                super("멍멍이");   // 부모에게 이름을 넘겨줌 ✅
            }
        }
        ```
        
    

### 다운 캐스팅(down casting)

- 부모 타입을 자식 타입으로 강제 변환
- 거의 쓰지 않음

```java
CapTv c = (CapTv)tv;
```

---

## 추상 클래스

- 클래스들의 공통적인 필드, 메소드를 추출해서 선언한 클래스
- 미완성 클래스(설계도)와 같음
    - 실행 블럭, 구현부가 없는 경우
    - 설계도의 의미는 미완성 코드에 있으므로, 반드시 미완성 코드를 구현하라는 강제성을 부여할 수 있음

추상 클래스는 `new` 연산자를 통해 스스로 객체 생성이 불가능하다

→ 반드시 상속을 통해서만 사용 가능

```java
abstract class Abclass {
	int pos;
	void run() {
		pos++;
	}
	abstract void stop(); // 실행 블록이 없다 -> 상속 관계 *반드시* 재정의 해라
}

class Child extends Abclass {
	
	@Override
	void stop() {
		// 구현
	}
}

public class Ex01_Abstract_Class {

	public static void main(String[] args) {
		Child child = new Child();
		child.run();
		child.run();
		System.out.println(child.pos);
		child.stop();
		
		Abclass ab = child; // 다형성
		ab.run();
		ab.stop(); // 부모 타입으로 접근하더라도 재정의 되었있다면 재정의 함수로 감
	}
}

```

## instanceof

- 변수가 참조하는 객체의 타입을 확인할 때 사용

```java
boolean result = 객체 instanceof 타입;
```

```java
if (tank instanceof Unit) {
	System.out.println(true);
}else {
	System.out.println(false);
}

if (tank instanceof Tank) {
	System.out.println(true);

}else {
	System.out.println(false);
}
```

- JavaScript: typeOf(), instanceof

---

## 디자인 패턴

- 오랜 시간 동안 좋은 코드만 모아 놓은 것
    - 객체 생성 관련
    
    → 디자인 패턴을 코드에 녹여내자
    
- 작은 코드에서는 디자인 패턴이 최악
    - 확장성을 고려한 코드이기 때문

### 싱글톤(Singleton)

- 객체를 하나만 만들고 모두가 그 객체를 공유해서 사용
    - ex) 직원 10명이 각자 프린터를 새로 사는 게 아니라 1대를 같이 씀
- 구현 방법
    - 설계도로 단 하나의 객체 만을 생성하고 `new`를 하지 못하게 막는다.
        - 생성자를 `private`으로 설정
        - `static` 메서드로 메모리에 올리기

```java
public class Singleton {
	private static Singleton p;
	
	private singleton() {
		// 외부에서 new 못하게 막는 것이 핵심
	}
	
	public static Singleton getInstance() {
		if (p==null) {
			p = new Singleton(); // 클래스 내부에서는 public과 private 구분이 없다
		}
		return p;
	}
```
