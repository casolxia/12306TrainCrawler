package com.crawler.train;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.crawler.vo.TrainVO;
import com.google.common.base.Splitter;

public class TrainDetail12306Page implements PageProcessor {

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

	private static Logger logger = LoggerFactory.getLogger(TrainDetail12306Page.class);
	
	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		String data = page.getRawText();
		JSONObject jsonObject = null;
		String url = page.getUrl().toString();
		String train_code = getParam(url,"train_no");
		String dates = getParam(url,"depart_date");
		try{
			jsonObject = JSONObject.parseObject(data);
		}catch(Exception e){
			//logger.info("data json error");
		}
		if(jsonObject==null)return;
		JSONObject trainData = (JSONObject) jsonObject.get("data");
		JSONArray trainDetailData = (JSONArray) trainData.get("data");
 		List<TrainVO> list = new ArrayList<>();
 		for(Object detail :trainDetailData){
 			TrainVO vo = new TrainVO();
 			JSONObject detailData = null;
			detailData = JSONObject.parseObject(detail.toString());
 			String arrive_time = detailData.getString("arrive_time");
 			String start_time = detailData.getString("start_time");
 			String stopover_time = detailData.getString("stopover_time");
 			String station_no = detailData.getString("station_no");
 			String isEnabled = detailData.getString("isEnabled");
 			String station_name = detailData.getString("station_name");
 			//System.out.println(arrive_time+"--"+start_time+"--"+stopover_time+"--"+station_name+"--"+station_no);
 			vo.setArriveTime(arrive_time);
 			vo.setStartTime(start_time);
 			vo.setStopoverTime(stopover_time);
 			vo.setStationNo(station_no);
 			vo.setStationName(station_name);
 			vo.setDates(dates);
 			vo.setTrainCode(train_code);
 			vo.setIsenabled(isEnabled);
 			list.add(vo);
 		}
 		page.putField("trainDetail", list);//火车数据
	}
	
	public  String getParam(String url, String name) {
	    String params = url.substring(url.indexOf("?") + 1, url.length());
	    Map<String, String> split = Splitter.on("&").withKeyValueSeparator("=").split(params);
	    return split.get(name);
	}

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/conf/applicationContext.xml");
    	ctx.registerShutdownHook();
		String train_cod ="4e0000G51022";
		String start_st ="WHN";
		String end_st ="BXP";
		String date_s ="2018-11-22";
    	String url = "https://kyfw.12306.cn/otn/czxx/queryByTrainNo?"
    			+ "train_no="+train_cod+"&from_station_telecode="+start_st+"&to_station_telecode="+end_st+"&depart_date="+date_s;
		Spider spider = new Spider(new TrainDetail12306Page());
		spider.addUrl(url)
		.setScheduler(
				new QueueScheduler()
						.setDuplicateRemover(new HashSetDuplicateRemover()))
						.addPipeline(new Train12306Pipeline())
		.thread(10);
		spider.run();
	}
}
