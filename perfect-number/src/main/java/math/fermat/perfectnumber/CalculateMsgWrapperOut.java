package math.fermat.perfectnumber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CalculateMsgWrapperOut {
	private CalculateMsgWrapper2[] Messages;

	/**
	 * @return the messages
	 */
	public CalculateMsgWrapper2[] getMessages() {
		return Messages;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(CalculateMsgWrapper2[] messages) {
		Messages = messages;
	}
}
