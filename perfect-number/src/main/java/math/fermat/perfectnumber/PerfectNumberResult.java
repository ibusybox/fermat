package math.fermat.perfectnumber;

public class PerfectNumberResult {
	private long n;
	private String clientId;
	private long[] factors;
	private String eulerExpression;
	public long getN() {
		return n;
	}
	public void setN(long n) {
		this.n = n;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public long[] getFactors() {
		return factors;
	}
	public void setFactors(long[] factors) {
		this.factors = factors;
	}
	public String getEulerExpression() {
		return eulerExpression;
	}
	public void setEulerExpression(String eulerExpression) {
		this.eulerExpression = eulerExpression;
	}
}
