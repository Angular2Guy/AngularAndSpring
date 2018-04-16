package ch.xxx.trader.clients;

public class  Tuple <X,Y> {
	private final X x;
	private final Y y;
	
	public Tuple(X x,Y y) {
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public Y getY() {
		return y;
	}
		
}
