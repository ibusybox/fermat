package math.fermat.perfectnumber;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.cloud.dms.ApiUtils;
import com.cloud.dms.ResponseMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.services.runtime.Context;
import com.huawei.services.runtime.RuntimeLogger;


public class PerfectNumber {

	/**
	 * webapp触发启动计算给定的数n是否为完美数
	 * 此函数的触发器为HTTP，由webapp调用
	 * @param n specified number
	 */
	public Response kickoff(PerfectNumberKickoff kickoff, Context context) {
		String dmsEndpoint = context.getUserData("dmsEndpoint");
		String region = context.getUserData("region");
		String queueId = context.getUserData("calcQueueId");
		String serviceName = "dms";
		String projectId = context.getUserData("projectId");
		String ak = context.getUserData("ak");
		String sk = context.getUserData("sk");
		
		RuntimeLogger logger = context.getLogger();
		Response response = new Response(); 
		
		String clientId = UUID.randomUUID().toString();
		
		//start calculate
		CalculateMsg message = new CalculateMsg();
		message.setNumber(new BigDecimal(kickoff.getN()));
		message.setMoreFactors(true);
		message.setCalcId(clientId);
		CalculateMsgWrapper messageCalc = new CalculateMsgWrapper();
		messageCalc.setBody(message);
		
		
		logger.log("put message to calculate queue.");
		try {
			if ( ! put2Queue(new CalculateMsgWrapper[]{messageCalc}, queueId, projectId, dmsEndpoint + "/v1.0/", serviceName, region, ak, sk) ) {
				logger.log("put message to calculate queue failure.");
				response.setErrorCode(500);
				response.setErrorMsg("put message to calculate queue failure.");
				return response;
			}
		} catch (JsonProcessingException e) {
			logger.log("send message to calculate queue got exception: " + e.getMessage());
			response.setErrorCode(500);
			response.setErrorMsg("send message to calculate queue got exception: " + e.getMessage());
			return response;
		}
		
		logger.log("Perfect number check for: " + kickoff.getN() + " will be starting, clientId = " + clientId);
		response.setErrorCode(0);
		response.setErrorMsg("Perfect number check for: " + kickoff.getN() + " will be starting, clientId = " + clientId);
		//TODO: save to database
		return response;
	}
	
	
	private boolean put2Queue(CalculateMsgWrapper[] messages, String qId, String projectId, String dmsUrl, String serviceName, String region, String ak, String sk) throws JsonProcessingException {
		CalculateMsgWrapperIn in = new CalculateMsgWrapperIn();
		in.setMessages(messages);
		ObjectMapper mapper = new ObjectMapper();
		String messageJson = mapper.writeValueAsString(in);
		ResponseMessage response = ApiUtils.sendMessages(messageJson, qId, projectId, dmsUrl, serviceName, region, ak, sk);
		return response.isSuccess();
	}


	/**
	 * 计算队列触发执行，分解因数
	 * @param message
	 * @param context
	 * @return
	 */
	public Response factor(CalculateMsgWrapperOut message, Context context) {
		String dmsEndpoint = context.getUserData("dmsEndpoint");
		String region = context.getUserData("region");
		String resultQueueId = context.getUserData("resultQueueId");
		String calcQueueId = context.getUserData("calcQueueId");
		String serviceName = "dms";
		String projectId = context.getUserData("projectId");
		String ak = context.getUserData("ak");
		String sk = context.getUserData("sk");
		
		RuntimeLogger logger = context.getLogger();
		Response response = new Response();
		
		CalculateMsg messageBody = message.getMessages()[0].getBody();
		
		int segment = Integer.valueOf(context.getUserData("segment"));
		List<BigDecimal> factors = factor2(messageBody.getNumber(), segment, logger);
		if (factors.size() >=   segment - 1) {
			//未分解完成，需要继续分解因子，将每个factor构造一个Message放到队列中
			CalculateMsgWrapper[] messages = constructCalculateMessage(factors, messageBody);
			try {
				if ( !put2Queue(messages, calcQueueId, projectId, dmsEndpoint + "/v0.1/", serviceName, region, ak, sk)) {
					String errorMsg = "put message to calculate quque failure, client id: " + messageBody.getCalcId(); 
					logger.log(errorMsg);
					response.setErrorCode(500);
					response.setErrorMsg(errorMsg);
				}
			} catch (JsonProcessingException e) {
				String errorMsg = "put message to calculate queue got exception, client id: " + messageBody.getCalcId() + ", exception: " + e.getMessage();
				logger.log(errorMsg);
				response.setErrorCode(500);
				response.setErrorMsg(errorMsg);
				return response;
			}
		} else {
			//分解完成，将消息放到结果中
			messageBody.setFactors(factors.toArray(new BigDecimal[]{}));
			messageBody.setMoreFactors(false);
			CalculateMsgWrapper msgWrapper = new CalculateMsgWrapper();
			msgWrapper.setBody(messageBody);
			try {
				put2Queue(new CalculateMsgWrapper[]{msgWrapper}, resultQueueId, projectId, dmsEndpoint + "/v0.1/", serviceName, region, ak, sk);
			} catch (JsonProcessingException e) {
				String errorMsg = "pute message to result queue got exception, client id: " + messageBody.getCalcId() + ", exception: " + e.getMessage(); 
				logger.log(errorMsg);
				response.setErrorCode(500);
				response.setErrorMsg(errorMsg);
			}
		}
		response.setErrorCode(0);
		return response;
	}
	
	private CalculateMsgWrapper[] constructCalculateMessage(List<BigDecimal> factors, CalculateMsg message) {
		List<CalculateMsgWrapper> wrappers = new ArrayList<CalculateMsgWrapper>();
		for (BigDecimal l : factors) {
			CalculateMsg m = new CalculateMsg();
			m.setCalcId(message.getCalcId());
			m.setNumber(l);
			m.setMoreFactors(true);
			CalculateMsgWrapper wrapper = new CalculateMsgWrapper();
			wrapper.setBody(m);
			wrappers.add(wrapper);
		}
		return wrappers.toArray(new CalculateMsgWrapper[]{});
	}


	/**
	 * 结果队列触发执行，归并结果，保存到数据库
	 * @param message
	 * @param context
	 * @return
	 */
	public String result(CalculateMsgWrapperOut message, Context context) {
		CalculateMsg messageBody = message.getMessages()[0].getBody();
		messageBody.getFactors();
		return "";
	}
	
	/**
	 * webapp触发查询结果
	 * @param context
	 * @return
	 */
	public String queryResult(PerfectNumberQuery query, Context context) {
		return "";
	}
	
	//不包含自身的因子，但是包含1
	private List<BigDecimal> factor2(BigDecimal x, int segment, RuntimeLogger logger) {
		List<BigDecimal> factors = new ArrayList<BigDecimal>();
		BigDecimal i = new BigDecimal("1");
		for (; i.multiply(i).compareTo(x) < 0 && factors.size()<segment; i = i.add(new BigDecimal("1"))) {
			if (x.remainder(i).intValue() == 0) {
				if (i.compareTo(x) < 0) {
					factors.add(i);
				}
				BigDecimal next = x.divide(i);
				if (next.compareTo(i) > 0 && next.compareTo(x) < 0) {
					factors.add(next);
				}
			}
		}
		return factors;
	}

	public static void main(String args[]) {
		BigDecimal start = new BigDecimal("2658455991569831744654692615953842176");
		List<BigDecimal> fs = new PerfectNumber().factor2(start, 100, null);
		BigDecimal sum = new BigDecimal(0);
		for (BigDecimal d : fs) {
			System.out.println(d);
			sum = sum.add(d);
		}
		System.out.println(sum);
	}
}
