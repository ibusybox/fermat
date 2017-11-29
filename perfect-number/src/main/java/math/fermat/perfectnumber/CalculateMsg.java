package math.fermat.perfectnumber;

import java.math.BigDecimal;

public class CalculateMsg {
	private BigDecimal number;
	private BigDecimal[] factors;
	private boolean moreFactors;
	private String calcId;
	public BigDecimal getNumber() {
		return number;
	}
	public void setNumber(BigDecimal number) {
		this.number = number;
	}
	public BigDecimal[] getFactors() {
		return factors;
	}
	public void setFactors(BigDecimal[] factors) {
		this.factors = factors;
	}
	public boolean isMoreFactors() {
		return moreFactors;
	}
	public void setMoreFactors(boolean moreFactors) {
		this.moreFactors = moreFactors;
	}
	public String getCalcId() {
		return calcId;
	}
	public void setCalcId(String clientId) {
		this.calcId = clientId;
	}
}
