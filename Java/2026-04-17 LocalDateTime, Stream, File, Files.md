## 복습

### 컬렉션

- ArrayList 대신 List를 써서 다형성을 활용하자
    
    → 나중에 구현체를 바꿔도 선언부를 수정할 필요 없음
    
    - List → 인터페이스 (다형성 O)
    - ArrayList → 구현 클래스

```java
// 권장 ✅
List<String> list = new ArrayList<>();

// 비권장 ❌
ArrayList<String> list = new ArrayList<>();
```

---

## 날짜/시간 API

### 1. Date

- 가장 오래된 클래스
- mutable (값이 바뀔 수 있음)
- 연도 기준이 1900년
- 월이 0부터 시작

```java
import java.util.Date;

Date now = new Date();           // 현재 시간
System.out.println(now);         // Sun Apr 26 14:30:00 KST 2026

long time = now.getTime();       // 1970-01-01부터의 milliseconds
```

### 2. Calendar

- 추상 클래스
    - new로 직접 객체를 만들 수 없으므로 getInstance() 제공됨 (팩토리 메서드 패턴)
        - 객체 생성을 별도의 메서드로 위임
        - Java가 알아서 지역에 맞는 객체를 골라줌
- mutable
- 월이 0부터 시작

```java
import java.util.Calendar;

Calendar cal = Calendar.getInstance();   // 현재 시간

int year  = cal.get(Calendar.YEAR);      // 2026 (정상!)
int month = cal.get(Calendar.MONTH);     // 3 (4월인데... 여전히 0부터 시작 😡)
int day   = cal.get(Calendar.DAY_OF_MONTH); // 26

// 날짜 더하기/빼기 가능
cal.add(Calendar.DATE, 7);               // 7일 후
cal.add(Calendar.MONTH, -1);             // 1달 전

// 특정 날짜 설정
cal.set(2026, Calendar.DECEMBER, 25);    // 2026-12-25
```

### 3. Local date time (권장)

- `java.time` 패키지에 있는 신세대 API
- immutable → 스레드 안전
- 월이 1부터 시작
- 날짜, 시간 연산 가능

| 클래스 | 용도 | 예시 |
| --- | --- | --- |
| `LocalDate` | 날짜만 | `2026-04-26` |
| `LocalTime` | 시간만 | `14:30:00` |
| `LocalDateTime` | 날짜 + 시간 | `2026-04-26T14:30:00` |

```java
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

LocalDateTime now = LocalDateTime.now();
// 2026-04-26T14:30:00.123

int year  = now.getYear();          // 2026
int month = now.getMonthValue();    // 4 (드디어 1부터! 🎉)
int day   = now.getDayOfMonth();    // 26

// 특정 날짜 생성
LocalDateTime birthday = LocalDateTime.of(2026, 12, 25, 0, 0);

// 날짜 연산 (immutable이라 새 객체 반환)
LocalDateTime nextWeek = now.plusDays(7);
LocalDateTime lastMonth = now.minusMonths(1);
LocalDateTime nextYear = now.plusYears(1);

// 비교
boolean isBefore = now.isBefore(birthday);  // true
boolean isAfter  = now.isAfter(birthday);   // false
```

- `DateTimeFormatter` 로 포맷팅 가능

```java
import java.time.format.DateTimeFormatter;

LocalDateTime now = LocalDateTime.now();
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");
String formatted = now.format(fmt);   // "2026년 04월 26일 14:30"

// 문자열 → LocalDateTime
LocalDateTime parsed = LocalDateTime.parse("2026-04-26T14:30:00");
```

---

## I/O

## Stream

- 단방향으로 데이터가 흐르는 것
    
    ex) 빨대, 중간 매개체
    
- Java API (I/O) 클래스 활용
    - 추상 클래스(InputStream, OutputStream)을 상속해서 재정의한 후 사용
- 입력 스트림: 데이터가 들어오는 것
- 출력 스트림: 데이터가 나가는 것
- 바이트 스트림: 그림, 멀티미디어, 문자 등 모든 종류의 데이터를 입출력할 때 사용
- 문자 스트림: 문자만 입출력할 때 사용(Char)

| 방향 \ 종류 | **Byte 기반** | **Char 기반** |
| --- | --- | --- |
| **입력** (읽기) | `InputStream` | `Reader` |
| **출력** (쓰기) | `OutputStream` | `Writer` |

```java
// InputStream
int read()                          // 1바이트 읽기 (-1이면 끝)
int read(byte[] b)                  // 배열에 한꺼번에 읽기
void close()                        // 스트림 닫기

// OutputStream
void write(int b)                   // 1바이트 쓰기
void write(byte[] b)                // 배열을 한꺼번에 쓰기
void flush()                        // 버퍼 비우기 (강제 출력)
void close()                        // 스트림 닫기

// Reader
int read()                          // 1문자 읽기
int read(char[] cbuf)

// Writer
void write(int c)                   // 1문자 쓰기
void write(String str)              // 문자열 쓰기
```

- 입출력 대상별 클래스 정리

| 대상 | Byte 기반 (입력 / 출력) | Char 기반 (입력 / 출력) |
| --- | --- | --- |
| **메모리** (Array, Collection) | `ByteArrayInputStream` / `ByteArrayOutputStream` | `CharArrayReader` / `CharArrayWriter` |
| **파일** | `FileInputStream` / `FileOutputStream` | `FileReader` / `FileWriter` |
| **네트워크** (프로세스 간) | `SocketInputStream` 등 (Socket이 내부 보유) |  |

### 1. OutputStream (바이트 출력 스트림)

- 출력 스트림을 더 이상 사용하지 않을 때에는 close() 메소드를 호출해서 출력 스트림이 사용했던 메모리를 해제하는 것이 좋음
    
    ```java
    public void close() throws IOException {
        flush();              // 1. 버퍼에 남은 데이터를 디스크로 강제 출력
        nativeClose();        // 2. OS에 파일 핸들 반환
        releaseResources();   // 3. 관련 자원 정리
    }
    ```
    
    - close() 호출하지 않으면 → 자원 누수, 락, 데이터 손실, 메모리 점유, 네트워크 자원 점유 문제 생길 수 있음
    - 스트림이 점유하는 OS 자원은 자바 밖에 있어서 힙 메모리만 청소하는 GC가 관리할 수 없음
- 종류
    - FileOutputStream
        - 주어진 파일을 생성할 수 없으면 IOException을 발생시킴 → 예외처리 필요
    - PrintStream
        - 사람이 읽는 문자열로 출력
        - `System.out`
    - BufferedOutputStream
        - 내부 버퍼에 바이트를 모아뒀다가, **버퍼가 차면** 한 번에 출력
        - 매번 `write()` 호출 시 OS 시스템 콜 발생 → 느림 → 성능 향상을 위해 `BufferedOutputStream`으로 감싸서 사용 (시스템 콜 횟수를 줄인다)
        - 데코레이터 패턴
    - DataOutputStream
        - 자바의 기본 데이터 타입(int, long, double, boolean...)을 바이트로 변환해서 그대로 저장 → 나중에 `DataInputStream`으로 그대로 복원할 수 있음.

```java
void write(int b)                   // 1바이트 쓰기
void write(byte[] b)                // 배열을 한꺼번에 쓰기
void flush()                        // 버퍼 비우기 (강제 출력)
void close()                        // 스트림 닫기
```

### 2. InputStream (바이트 입력 스트림)

- FileInputStream
    - 주어진 파일이 존재하지 않을 경우 FileNotFountException 발생
- BufferedInputStream
- DataInputStream

```java
int read()                          // 1바이트 읽기 (-1이면 끝)
int read(byte[] b)                  // 배열에 한꺼번에 읽기
void close()                        // 스트림 닫기
```

### 3. Writer (문자 입력 스트림)

- FileWriter
- BufferedWriter
- PrintWriter
- OutputStreamWriter

```java
// Writer
void write(int c)                   // 1문자 쓰기
void write(String str)              // 문자열 쓰기
```

### 4. Reader (문자 출력 스트림)

- FileReader
- BufferedReader
- InputStreamReader

```java
// Reader
int read()                          // 1문자 읽기
int read(char[] cbuf)
```

## 보조 스트림

- 다른 스트림과 연결되어 여러 가지 편리한 기능을 제공해주는 스트림
- 자체적으로 입출력 수행 불가
- 다른 보조 스트림과 연결되어 스트림 체인으로 구성 가능

```java
InputStream is = new FileInputStream("...");
InputStreamReader reader = new InputStreamReader(is); // 보조 스트림
BufferedReader br = new BufferedReader(reader);
```

### 문자 변환 스트림

- 바이트 스트림(InuputStream, OutputStream)에서 입출력할 데이터가 문자라면 문자 스트림(Reader, Writer)으로 변환해서 사용
    - 문자로 바로 입출력 가능
    - 문자셋 종류 지정 가능

```java
// InputStream -> Reader
InputStream is = new FileInputStream("C:/Temp/test.txt");
Reader reader = new InputStreamReader(is);
```

```java
// OutputStream -> Writer
OutputStream os = jew FileOutputStream("C:/Temp/test.txt");
Writer write = new OutputStreamWriter(os, "UTF-8");
```

### 성능 향상 스트림

- 프로그램이 입출력 소스(하드 디스크, 네트워크)와 직접 작업하지 않고, 중간에 메모리 버퍼와 작업 → 실행 성능 향상

```java
BufferedInputStream bis = new BufferedInputStream(바이트 입력 스트림);
BufferedOutputStream bos = new BufferedOutputStream(바이트 출력 스트림);
BufferedReader br = new BufferedReader(문자 입력 스트림);
BufferedWriter bw = new BufferedWriter(문자 출력 스트림);
```

### 기본 타입 스트림

- 기본 타입(boolean, char, short, int, long, loat, double) 입출력 가능

```java
// 쓰기
FileOutputStream fos = new FileOutputStream("data.bin");
DataOutputStream dos = new DataOutputStream(fos);

dos.writeInt(100);
dos.writeDouble(3.14);
dos.writeBoolean(true);
dos.writeUTF("안녕하세요");
dos.close();

// 읽기
FileInputStream fis = new FileInputStream("data.bin");
DataInputStream dis = new DataInputStream(fis);

int i = dis.readInt();        // 100
double d = dis.readDouble();  // 3.14
boolean b = dis.readBoolean();// true
String s = dis.readUTF();     // "안녕하세요"
dis.close();
```

- 메소드
    
    
    - DataOutputStream (쓰기)
    
    | 메소드 | 설명 |
    | --- | --- |
    | `writeBoolean(boolean v)` | boolean 값 쓰기 (1바이트) |
    | `writeByte(int v)` | byte 값 쓰기 (1바이트) |
    | `writeShort(int v)` | short 값 쓰기 (2바이트) |
    | `writeChar(int v)` | char 값 쓰기 (2바이트) |
    | `writeInt(int v)` | int 값 쓰기 (4바이트) |
    | `writeLong(long v)` | long 값 쓰기 (8바이트) |
    | `writeFloat(float v)` | float 값 쓰기 (4바이트) |
    | `writeDouble(double v)` | double 값 쓰기 (8바이트) |
    | `writeUTF(String s)` | 문자열을 UTF-8로 쓰기 |
    | `writeChars(String s)` | 문자열의 각 char를 쓰기 |
    
    - DataInputStream (읽기)
        
        
        | 메소드 | 설명 |
        | --- | --- |
        | `readBoolean()` | boolean 값 읽기 |
        | `readByte()` | byte 값 읽기 |
        | `readShort()` | short 값 읽기 |
        | `readChar()` | char 값 읽기 |
        | `readInt()` | int 값 읽기 |
        | `readLong()` | long 값 읽기 |
        | `readFloat()` | float 값 읽기 |
        | `readDouble()` | double 값 읽기 |
        | `readUTF()` | UTF-8 문자열 읽기 |

### 프린트 스트림

- `System.out.println()` 에서 `out`이 PrintStream 타입
- 메소드
    - print(): 줄바꿈 없이 문자 출력
    - println(): 줄바꿈 추가
    - printf(): 형식화된 문자열(format string) 출력

```java
// PrintWriter로 파일에 형식화된 출력
FileWriter fw = new FileWriter("result.txt");
PrintWriter pw = new PrintWriter(fw);

pw.println("이름: 홍길동");
pw.printf("점수: %d점, 평균: %.2f%n", 95, 87.65);
pw.close();
```

### 객체 스트림

- 객체 자체를 통째로 읽고 쓰는 보조 스트림
- 직렬화(Serialization): 객체의 필드값을 일렬로 늘어선 바이트로 변경
- 역직렬화(Deserialization): 직렬화된 바이트를 객체의 필드값으로 복원하는 것
- Serialize 인터페이스를 통해 직렬화, 역직렬화 가능
    - 메소드가 없는 마커 인터페이스로, "이 클래스는 직렬화 가능하다"는 표시를 해줌
- `serialVersionUID` 필드를 명시적으로 선언하면 클래스 버전 관리에 유리
    - 직렬화할 때 사용딘 클래스와 역직렬화할 때 사용된 클래스는 기본적으로 동일한 클래스여야 함
    - `serialVersionUID` : 객체의 버전을 식별하는 고유 번호
    - `serialVersionUID`를 명시하지 않으면 JVM이 클래스 구조(필드, 메소드 등)를 기반으로 자동 계산 → 클래스를 조금만 수정해도 UID가 바뀜
- 직렬화 되지 않는 것
    - `transient` 키워드가 붙은 필드
    - `static` 필드 (인스턴스가 아닌 클래스 소속이므로)

```java
class Student implements Serializable {
    private static final long serialVersionUID = 1L;  // 버전 번호
    String name;
    int age;
    transient String password;  // transient 키워드: 직렬화 제외
}
```

```java
// 객체 저장 (직렬화)
Student s = new Student("홍길동", 20);

FileOutputStream fos = new FileOutputStream("student.dat");
ObjectOutputStream oos = new ObjectOutputStream(fos);
oos.writeObject(s);
oos.close();

// 객체 읽기 (역직렬화)
FileInputStream fis = new FileInputStream("student.dat");
ObjectInputStream ois = new ObjectInputStream(fis);
Student s2 = (Student) ois.readObject();  // 캐스팅 필수
ois.close();
```

---

## File/Files 클래스

### File 클래스 (java.io.File)

- 파일이나 디렉토리의 경로와 메타 정보를 다루는 클래스
- 파일 시스템 조작에 사용
- 파일 내용을 읽거나 쓰지는 않음

```java
import java.io.File;

public class FileExample {
    public static void main(String[] args) throws Exception {
        File file = new File("test.txt"); // 객체 생성 필요
        
        // 파일 생성
        if (!file.exists()) {
            file.createNewFile();
        }
        
        // 정보 조회
        System.out.println("이름: " + file.getName());
        System.out.println("경로: " + file.getAbsolutePath());
        System.out.println("크기: " + file.length() + " bytes");
        
        // 디렉토리 목록
        File dir = new File("C:/temp");
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                System.out.println(f.getName());
            }
        }
    }
}
```

### Files **클래스 (java.nio.file.Files)**

- Java 7부터 제공
- 정적 메소드(static)로 구성 → 객체 만들 필요 X
    - 운영체제의 파일 시스템에게 파일 작업을 수행하도록 위임
        - 자바는 운영체제(OS)에게 "이 파일 좀 지워줘"라고 요청 → 실제로 파일을 지우는 건 OS의 파일 시스템

```java
import java.nio.file.*;
import java.util.List;

public class FilesExample {
    public static void main(String[] args) throws Exception {
        Path path = Paths.get("test.txt");
        
        // 쓰기
        Files.writeString(path, "Hello\n자바");
        
        // 읽기
        String content = Files.readString(path);
        System.out.println(content);
        
        // 줄 단위 읽기
        List<String> lines = Files.readAllLines(path);
        
        // 복사
        Files.copy(path, Paths.get("backup.txt"),
                   StandardCopyOption.REPLACE_EXISTING);
        
        // 디렉토리 탐색
        Files.walk(Paths.get("."))
             .filter(p -> p.toString().endsWith(".java"))
             .forEach(System.out::println);
        
        // 삭제
        Files.deleteIfExists(Paths.get("backup.txt"));
    }
}
```

```java
import java.io.FileReader;
import java.io.FileWriter;

public class Ex05_Reader_Writer {
	public static void main(String[] args) {
		FileReader fr = null;
		FileWriter fw = null;
		
		try {
			fr = new FileReader("Ex01_Stream.java"); // read
			fw = new FileWriter("copy_Ex01.txt); // FileWriter 생성자도 파일이 없으면 create
			
			int data = 0;
			while ((data = fr.read()) != -1) {
				// System.out.println((char)data);
				if (data!='\n' && data!='\r' && data!=' '){ // 압축 버전 만들기
					fw.write(data);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	
	}
	
```

### File vs Files

| 구분 | `File` (java.io) | `Files` (java.nio.file) |
| --- | --- | --- |
| 도입 | Java 1.0 | Java 7 |
| 형태 | 인스턴스 클래스 | 정적 유틸리티 클래스 |
| 경로 표현 | `File` 객체 자체 | `Path` 객체 사용 |
| 사용 방식 | `file.delete()` | `Files.delete(path)` |
| 파일 읽기/쓰기 | ❌ (스트림 필요) | ✅ (메소드로 직접) |
| 실패 처리 | boolean 반환 | 명확한 예외 발생 |
| 심볼릭 링크 | 지원 미흡 | 완벽 지원 |
| Stream API | ❌ | ✅ (`lines()`, `walk()`) |
| 성능 | 보통 | 우수 |

## 파일 압축 버전 만들기 예제

```java
import java.io.FileReader; // 문자 스트림
import java.io.FileWriter;

public class Ex05_Reader_Writer {
    public static void main(String[] args) {
        FileReader fr = null;
        FileWriter fw = null;
        
        try {
            fr = new FileReader("Ex01_Stream.java");   // 읽을 파일
            fw = new FileWriter("copy_Ex01.txt");      // 쓸 파일 (없으면 생성)
            
            int data = 0;
            while ((data = fr.read()) != -1) {
                // System.out.println((char)data);
                if (data != '\n' && data != '\r' && data != ' ') {  // 압축 버전 만들기
                    fw.write(data); // 읽은 문자가 줄바꿈도 아니고, 캐리지 리턴도 아니고, 공백도 아닐 때만 파일에 쓴다
                }
            }
            
            fr.close();
            fw.close();
            
            System.out.println("파일 복사 완료!");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

| 단계 | 동작 |
| --- | --- |
| 1 | `FileReader`로 원본 파일 열기 |
| 2 | `FileWriter`로 새 파일 만들기 |
| 3 | 한 글자씩 읽기 (`read()`) |
| 4 | 공백/줄바꿈이 아니면 쓰기 (`write()`) |
| 5 | `-1` (EOF)까지 반복 |
| 6 | 스트림 닫기 |
- `\r`을 검사하는 이유
    - Windows에서 만든 텍스트 파일을 읽을 때 줄바꿈이 `\r\n`으로 되어있기 때문
    - `\n`만 검사하면 `\r`이 그대로 남아서 결과 파일에 이상한 문자가 섞일 수 있음

| 기호 | 이름 | 의미 | 시각적 동작 |
| --- | --- | --- | --- |
| `\r` | 캐리지 리턴 | 커서를 줄 맨 앞으로 | ←(같은 줄에서 처음으로) |
| `\n` | 라인 피드 | 커서를 다음 줄로 | ↓ (아래로) |
| `\r\n` | CRLF | 진짜 줄바꿈 (Windows) | ↙ (다음 줄 처음) |
