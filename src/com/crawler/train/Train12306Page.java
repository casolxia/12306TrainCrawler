package com.crawler.train;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crawler.CrawlerService;
import com.crawler.util.BeanUtil;
import com.crawler.vo.TrainCodeVO;

public class Train12306Page  implements PageProcessor {

	private Site site = Site
			.me()
			.setCycleRetryTimes(5)
			.setRetryTimes(5)
			.setSleepTime(500)
			.setTimeOut(3 * 60 * 1000)
			.setUserAgent(
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
			.addHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
			.setCharset("utf-8");

	private static Logger logger = LoggerFactory.getLogger(Train12306Page.class);
	
	private CrawlerService crawlerService = (CrawlerService) BeanUtil.getBean("crawlerService");
	
	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		String data = page.getRawText();
		JSONObject jsonObject = null;
		data = data.substring(16);
		try{
         jsonObject = JSONObject.parseObject(data);
		}catch(Exception e){
			logger.info("data json error");
		}
		if(jsonObject==null)return;
 		List<TrainCodeVO> list = new ArrayList<>();
		for(String dates :jsonObject.keySet()){
			logger.info("日期："+dates);
	 		int days = 0;
	 		try {
	 			days = crawlerService.existTrainDates(dates);
			} catch (Exception e) {
				e.printStackTrace();
			}
	 		if(days == 1)
	 		{
		 		System.out.println("days==1");
	 			continue;
	 		}
	 		logger.info("不存在，添加此日期***********："+dates);
	 		JSONObject trainType = null;
	 		trainType = (JSONObject) jsonObject.get(dates);//得到每日车次类型数据json对象
	 		System.out.println(trainType+"--------"+trainType);
	 		for(String type :trainType.keySet()){
		 		System.out.println(dates+"--------"+type);
		 		JSONArray trainDetail = null;
		 		trainDetail = (JSONArray) trainType.get(type);//得到每日车次类型数据json对象
		 		for(Object detail :trainDetail){
			 		JSONObject trainNumObject = null;
			 		trainNumObject = JSONObject.parseObject(detail.toString());
			 		String train_no = trainNumObject.getString("train_no");
			 		String station_train = trainNumObject.getString("station_train_code");
			 		String train_num = station_train.split("\\(")[0];
			 		String start_s = station_train.split("\\(")[1].split("-")[0];
			 		String end_s = station_train.split("\\(")[1].split("-")[1].split("\\)")[0];
			 		TrainCodeVO vo = new TrainCodeVO();
			 		vo.setStart_(start_s);
			 		vo.setEnd_(end_s);
			 		vo.setTrainCode(train_no);
			 		vo.setTrainnum(train_num);
			 		vo.setDates(dates);
			 		list.add(vo);
		 		}

	 		}
		}
		page.putField("train", list);//火车数据

	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/conf/applicationContext.xml");
    	ctx.registerShutdownHook();
		String url = "https://kyfw.12306.cn/otn/resources/js/query/train_list.js?scriptVersion=1.0";
		Spider spider = new Spider(new Train12306Page());
		spider.addUrl(url)
		.setScheduler(
				new QueueScheduler()
						.setDuplicateRemover(new HashSetDuplicateRemover()))
						.addPipeline(new Train12306Pipeline())
		.thread(1);
		spider.run();
	}
}
