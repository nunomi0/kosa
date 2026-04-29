package lotto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.Arrays;

@Data
@AllArgsConstructor
public class Lotto {
	public static final int MIN_NUMBER = 1;
	public static final int MAX_NUMBER = 45;
	public static final int LOTTO_SIZE = 6;
	private int[] numbers;
	
    @Override
    public String toString() {
        return Arrays.toString(numbers);
    }
}
