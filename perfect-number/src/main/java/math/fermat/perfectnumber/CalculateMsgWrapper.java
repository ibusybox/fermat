package math.fermat.perfectnumber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CalculateMsgWrapper {
	private CalculateMsg body;
	public CalculateMsg getBody() {
		return body;
	}
	public void setBody(CalculateMsg body) {
		this.body = body;
	}
}
