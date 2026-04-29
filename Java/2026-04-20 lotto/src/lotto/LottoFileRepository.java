package lotto;

import java.io.FileWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LottoFileRepository {
	
	private static final String FILE_NAME = "lotto.txt";
	
	public void save(Lotto lotto) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))){
			writer.write(lotto.toString());
			writer.newLine();
			System.out.println("저장을 완료했습니다.");
		} catch (IOException e) {
			System.out.println("저장을 실패했습니다.");
		}
	}
	
	public void readAll() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
        	System.out.println("저장된 로또가 없습니다.");
        	return;
        }
        
        try (Scanner sc = new Scanner(file)) {
        	while (sc.hasNextLine()) System.out.println(sc.nextLine());
        } catch (IOException e) {
        	System.out.println("읽기에 실패했습니다.");
        }	
	}
}
