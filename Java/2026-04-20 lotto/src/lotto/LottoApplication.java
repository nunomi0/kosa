package lotto;

import java.util.Scanner;

public class LottoApplication {

	private Scanner sc = new Scanner(System.in);
	private LottoMachine lottoMachine = new LottoMachine();
	private LottoFileRepository lottoFileRepository = new LottoFileRepository();
	
	public void run() {
		while (true) {
			System.out.println("메뉴를 입력하세요. (1: 로또 생성, 2: 저장 로또 확인, 3: 종료)");
	        int menu = inputMenu(1, 3);
			
			switch (menu) {
				case 1: {
					handleCreateLotto();
					break;
				}
				case 2: {
					handleShowLottoList();
					break;
				}
				case 3: {
					return;
				}
			}
		}
	}
	
	private int inputMenu(int min, int max) {
	    while (true) {
	        if (!sc.hasNextInt()) {
	            sc.next();
	            System.out.println("숫자를 입력하세요.");
	            continue;
	        }

	        int value = sc.nextInt();
	        if (value >= min && value <= max) return value;
	        System.out.println(min + "~" + max + " 범위로 입력하세요.");
	    }
	}
	
	private void handleCreateLotto() {
		Lotto lotto = lottoMachine.makeLotto();
		System.out.println("로또를 발행했습니다. " + lotto.toString());
		System.out.println("저장하시겠습니까? (1: 네, 2: 아니요)");
		int save = inputMenu(1, 2);
		if (save==1) lottoFileRepository.save(lotto);
		return;
	}
	
	private void handleShowLottoList() {
		System.out.println("저장된 로또를 조회합니다.");
		lottoFileRepository.readAll();
		return;
	}
	
}
