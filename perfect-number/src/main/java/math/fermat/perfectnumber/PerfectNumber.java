package math.fermat.perfectnumber;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.cloud.dms.ApiUtils;
import com.cloud.dms.ResponseMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.huawei.services.runtime.Context;
import com.huawei.services.runtime.RuntimeLogger;


public class PerfectNumber {

	/**
	 * webapp触发启动计算给定的数n是否为完美数
	 * 此函数的触发器为HTTP，由webapp调用
	 * @param n specified number
	 */
	public String kickoff(PerfectNumberKickoff kickoff, Context context) {
		String dmsEndpoint = context.getUserData("dmsEndpoint");
		String region = context.getUserData("region");
		String queueId = context.getUserData("resultQueueId");
		String serviceName = "dms";
		String projectId = context.getUserData("projectId");
		String ak = context.getUserData("ak");
		String sk = context.getUserData("sk");
		
		RuntimeLogger logger = context.getLogger();
		
		String clientId = UUID.randomUUID().toString();
		
		//start calculate
		CalculateMsg message = new CalculateMsg();
		message.setNumber(kickoff.getN());
		message.setMoreFactors(true);
		message.setClientId(clientId);
		CalculateMsgWrapper messageCalc = new CalculateMsgWrapper();
		messageCalc.setBody(message);
		messageCalc.setTags(new String[]{clientId});
		
		
		logger.log("put message to calculate queue.");
		try {
			if ( ! put2CalculateQueue(clientId, new CalculateMsgWrapper[]{messageCalc}, queueId, projectId, dmsEndpoint + "/v1.0/", serviceName, region, ak, sk) ) {
				logger.log("put message to calculate queue failure.");
				return "";
			}
		} catch (JsonProcessingException e) {
			logger.log("send message to calculate queue got exception: " + e.getMessage());
			return "";
		}
		
		logger.log("Perfect number check for: " + kickoff.getN() + " will be starting, clientId = " + clientId);
		return clientId;
	}
	
	
	private boolean put2CalculateQueue(String clientId, CalculateMsgWrapper[] messages, String qId, String projectId, String dmsUrl, String serviceName, String region, String ak, String sk) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String messageJson = mapper.writeValueAsString(messages);
		ResponseMessage response = ApiUtils.sendMessages(messageJson, qId, projectId, dmsUrl, serviceName, region, ak, sk);
		return response.isSuccess();
	}


	/**
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	public String factor(CalculateMsgWrapper message, Context context) {
		String dmsEndpoint = context.getUserData("dmsEndpoint");
		String region = context.getUserData("region");
		String queueId = context.getUserData("resultQueueId");
		String serviceName = "dms";
		String projectId = context.getUserData("projectId");
		String ak = context.getUserData("ak");
		String sk = context.getUserData("sk");
		
		RuntimeLogger logger = context.getLogger();
		
		int segment = Integer.valueOf(context.getUserData("segment"));
		List<Long> factors = factor2(message.getBody().getNumber(), segment, logger);
		if (factors.size() >=   segment - 1) {
			//未分解完成，需要继续分解因子，将每个factor构造一个Message放到队列中
			CalculateMsgWrapper[] messages = constructCalculateMessage(factors, message.getBody().getClientId());
			try {
				put2CalculateQueue(message.getBody().getClientId(), messages, queueId, projectId, dmsEndpoint + "/v0.1/", serviceName, region, ak, sk);
			} catch (JsonProcessingException e) {
				logger.log("put message to calculate queue got exception, client id: " + message.getBody().getClientId() + ", exception: " + e.getMessage());
				return "";
			}
		} else {
			//分解完成，将消息放到结果中
			CalculateMsgWrapper[] messages = constructCalculateMessage(factors, message.getBody().getClientId());
			put2ResultQuque(message.getBody().getClientId(), messages, queueId, projectId, dmsEndpoint + "/v0.1/", serviceName, region, ak, sk);
		}
		return "";
	}
	
	private void put2ResultQuque(String clientId, CalculateMsgWrapper[] messages, String queueId,
			String projectId, String string, String serviceName, String region, String ak, String sk) {
		// TODO Auto-generated method stub
		
	}


	private CalculateMsgWrapper[] constructCalculateMessage(List<Long> factors, String clientId) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * webapp 查询计算结果
	 * @param message
	 * @param context
	 * @return
	 */
	public String result(PerfectNumberQuery query, Context context) {
		String dmsEndpoint = context.getUserData("dmsEndpoint");
		String region = context.getUserData("region");
		String queueId = context.getUserData("resultQueueId");
		String groupId = context.getUserData("resultQueueGroupId");
		String serviceName = "dms";
		String projectId = context.getUserData("projectId");
		String ak = context.getUserData("ak");
		String sk = context.getUserData("sk");
		
		//10 message each time
		List<CalculateMsgWrapper> messages = new ArrayList<CalculateMsgWrapper>();
		ResponseMessage response = ApiUtils.consumeMessages(queueId, groupId, 10, projectId, dmsEndpoint + "/v1.0/", serviceName, region, ak, sk, query.getClientId());
		boolean hasMore = retriveMessage(response, messages);
		while(hasMoreResultMsg(response)) {
			response = ApiUtils.consumeMessages(queueId, groupId, 10, projectId, dmsEndpoint + "/v1.0/", serviceName, region, ak, sk, query.getClientId());
			hasMore = retriveMessage(response);
		}
		return "";
	}
	
	//不包含自身的因素，但是包含1
	private List<Long> factor2(long x, int segment, RuntimeLogger logger) {
		List<Long> factors = new ArrayList<Long>();
		long i = 1;
		for (; i*i<x && factors.size()<segment; i++) {
			if (x%i == 0) {
				if (i < x) {
					factors.add(i);
				}
				long next = x/i;
				if (next > i && next < x) {
					factors.add(next);
				}
			}
		}
		return factors;
	}

	public static void main(String args[]) {
	}
}
