package math.fermat.perfectnumber;

public class PerfectNumberMessage {
	private long number;
	private Long[] factors;
	private boolean moreFactors;
	private String clientId;
	public long getNumber() {
		return number;
	}
	public void setNumber(long number) {
		this.number = number;
	}
	public Long[] getFactors() {
		return factors;
	}
	public void setFactors(Long[] factors) {
		this.factors = factors;
	}
	public boolean isMoreFactors() {
		return moreFactors;
	}
	public void setMoreFactors(boolean moreFactors) {
		this.moreFactors = moreFactors;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
