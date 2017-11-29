package math.fermat.perfectnumber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CalculateMsgWrapper2 {
	private CalculateMsg Body;

	/**
	 * @return the body
	 */
	public CalculateMsg getBody() {
		return Body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(CalculateMsg body) {
		Body = body;
	}
}
