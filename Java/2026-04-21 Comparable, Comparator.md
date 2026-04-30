## Comparable 인터페이스

- 클래스의 기본 정렬 기준을 설정하는 인터페이스
- 자바에서 제공되는 정렬이 가능한 클래스들은 모두 Comparable 인터페이스를 구현하고 있음
- java.lang.Comparable
- compareTo 메서드를 오버라이드 해서 비교 기준을 정의
- ex) 학생을 학번 순으로 정렬하는 경우
    
    ```java
    import java.lang.Comparable; //패키지 import
    
    class Student implements Comparable<Student> { //제너릭스 주의!
    	String name; //이름
    	int id; //학번
    	double score; //학점
    	public Student(String name, int id, double score){
    		this.name = name;
    		this.id = id;
    		this.score = score;
    	}
    	public String toString(){ //출력용 toString오버라이드
    		return "이름: "+name+", 학번: "+id+", 학점: "+score;
    	}
    	@Override
    	public int compareTo(Student anotherStudent) { //오버라이딩
    		// TODO Auto-generated method stub
    		return Integer.compare(id, anotherStudent.id);
    		// return (id<anotherStudent.id)?-1:((id==anotherStudent.id)?0:1);
    	}
    }
    ```
    
    ```java
    pulic static int compare(int x, int y) {
    	return (x<y) ? -1 : ((x==y) ? 0 : 1);
    }
    ```
    

## Comparator 클래스

- 기본 정렬 기준과 다른 방식으로 정렬하고 싶을 때 사용하는 클래스
- java.util.Comparator
- 익명 클래스로 주로 사용
- ex) 성적 순 우선 정렬하는 경우
    
    ```java
    Arrays.sort(student, new Comparator<Student>(){
    	@Override
    	public int compare(Student s1, Student s2) {
    		double s1Score = s1.score;
    		double s2Score = s2.score;
    		if(s1Score == s2Score){ //학점이 같으면
    			return Double.compare(s1.id, s2.id); //학번 오름차순
    		}
    		return Double.compare(s2Score, s1Score);//학점 내림차순
    	}
    });
    
    ```
    

---

| 특징 | Comparable | Comparator |
| --- | --- | --- |
| 비교 기준 | 자기 자신 기준 (this) | 외부 기준 (다른 객체) |
| 메서드 | compareTo(T o) | compare(T o1, T o2) |
| 정렬 기준 개수 | 하나만 가능 | 여러 개 정의 가능 |
| 위치 | 정렬할 클래스 내부 | 별도의 클래스 또는 람다 표현식 사용 |
