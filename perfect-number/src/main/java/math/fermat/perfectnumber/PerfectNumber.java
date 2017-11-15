package math.fermat.perfectnumber;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.huawei.services.runtime.Context;
import com.huawei.services.runtime.RuntimeLogger;


public class PerfectNumber {

	/**
	 * webapp触发启动计算给定的数n是否为完美数
	 * 此函数的触发器为HTTP，由webapp调用
	 * @param n specified number
	 */
	public String kickoff(PerfectNumberKickoff kickoff, Context context) {
		RuntimeLogger logger = context.getLogger();
		
		String clientId = UUID.randomUUID().toString();
		
		//create result queue
		logger.log("start to create result queue for client id " + clientId);
		if (! createRequestQuque(clientId) ) {
			logger.log("create result queue failure.");
			return "";
		}
		
		//start calculate
		PerfectNumberMessage message = new PerfectNumberMessage();
		message.setNumber(kickoff.getN());
		message.setMoreFactors(true);
		message.setClientId(clientId);
		
		logger.log("put message to calculate queue.");
		if ( ! put2CalculateQueue(message) ) {
			logger.log("put message to calculate queue failure.");
			return "";
		}
		
		logger.log("Perfect number check for: " + kickoff.getN() + " will be starting, clientId = " + clientId);
		return clientId;
	}
	
	
	/**
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	public String factor(PerfectNumberMessage message, Context context) {
		RuntimeLogger logger = context.getLogger();
		
		int segment = Integer.valueOf(context.getUserData("segment"));
		List<Long> factors = factor2(message.getNumber(), segment, logger);
		if (factors.size() >=   segment - 1) {
			//未分解完成，需要继续分解因子，将每个factor构造一个Message放到队列中
		} else {
			//分解完成，将消息放到结果中
		}
		return "";
	}
	
	/**
	 * webapp 查询计算结果
	 * @param message
	 * @param context
	 * @return
	 */
	public String result(PerfectNumberQuery query, Context context) {
		return "";
	}
	
	//不包含自身的因素，但是包含1
	private List<Long> factor2(long x, int segment, RuntimeLogger logger) {
		List<Long> factors = new ArrayList<Long>();
		long i = 1;
		for (; i*i<x && factors.size()<segment; i++) {
			if (x%i == 0) {
				factors.add(i);
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
