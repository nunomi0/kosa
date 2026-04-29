package lotto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.Random;

@Data
@AllArgsConstructor
public class LottoMachine {

	public Lotto makeLotto() {
		while (true) {
			boolean[] numbersMap = selectRandomNumbers();
			int[] lottoNumbers = convertMapToArray(numbersMap);
			if (!isValid(lottoNumbers))
				return new Lotto(lottoNumbers);
		}
	}

	private boolean[] selectRandomNumbers() {
		boolean[] numbersMap = new boolean[Lotto.MAX_NUMBER + 1];
		int count = 0;

		while (count < Lotto.LOTTO_SIZE) {
			int randomNumber = generateRandomNumber();

			if (!numbersMap[randomNumber]) {
				numbersMap[randomNumber] = true;
				count++;
			}
		}
		return numbersMap;
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(Lotto.MAX_NUMBER) + 1;
	}

	private int[] convertMapToArray(boolean[] numbersMap) {
		int[] lottoNumbers = new int[Lotto.LOTTO_SIZE];
		int index = 0;
		for (int i = Lotto.MIN_NUMBER; i < Lotto.MAX_NUMBER + 1; i++) {
			if (numbersMap[i])
				lottoNumbers[index++] = i;
		}
		return lottoNumbers;
	}
	
	private boolean isValid(int[] lottoNumbers) {
	    return hasConsecutiveDiff(lottoNumbers, 1) || hasConsecutiveDiff(lottoNumbers, 7);
	}

	private boolean hasConsecutiveDiff(int[] lottoNumbers, int diff) {
	    int count = 1;
	    for (int i = 1; i < Lotto.LOTTO_SIZE; i++) {
	        if (lottoNumbers[i - 1] + diff == lottoNumbers[i]) count++;
	        else count = 1;
	        if (count >= 3) return true;
	    }
	    return false;
	}

}
