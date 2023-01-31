package hopfield;

import java.util.Random;

public class Network {
	private int [][] pobudzenie;
	private Random random;
	
	public Network(int cols, int rows) {
		pobudzenie = new int [cols * rows][cols * rows];
		random = new Random();
	}
	
	public void learn(int [] data) {
		for(int i = 0; i < data.length; i++) {
			for(int j = 0; j < data.length; j++) {
				if(i != j) {
					if(data[i] == data[j]) {
						pobudzenie[i][j] += 1;
					} else {
						pobudzenie[i][j] += -1;
					}
				}
			}
		}
	}
	
	public int [] testAsync(int [] data) {
		int [] out = new int [data.length];
		System.arraycopy(data, 0, out, 0, data.length);

		int index = random.nextInt(data.length);
		
		for(int i = 0; i < data.length; i++) {
			out[index] += data[i] * pobudzenie[index][i];
		}
		
		out[index] = getOutput(out[index]);
		return out;
	}
	
	public int [] testSync(int [] data) {
		int [] out = new int [data.length];

		for(int i = 0; i < data.length; i++) {
			for(int j = 0; j < data.length; j++) {
				out[i] += data[j] * pobudzenie[i][j];
			}
		}

		for(int i = 0; i < data.length; i++) {
			out[i] = getOutput(out[i]);
		}
		return out;
	}
	
	private int getOutput(int value) {
		int result = 0;
		
		if(value > 0) {
			result = 1;
		} else if(value == 0) {
			result = value;
		}
		
		return result;
	}
}
