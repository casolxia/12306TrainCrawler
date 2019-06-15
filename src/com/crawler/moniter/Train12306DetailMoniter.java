package com.crawler.moniter;


import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.crawler.CrawlerService;
import com.crawler.train.TrainDetail12306Page;
import com.crawler.train.TrainDetail12306Pipeline;
import com.crawler.util.BeanUtil;
import com.crawler.vo.TrainCodeVO;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

public class Train12306DetailMoniter extends Thread {

	private static Logger logger = LoggerFactory.getLogger(Train12306DetailMoniter.class);
	public CrawlerService crawlerService = (CrawlerService) BeanUtil.getBean("crawlerService");

	private Spider spider;
	
	private String dates;
	
	public Train12306DetailMoniter(Spider spider,String dates){
		this.spider = spider;
		this.dates = dates;
	}
	//Init(0), Running(1), Stopped(2);
	public void run(){
		while(true){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(spider.getStatus().toString()=="Stopped"){
				//运行一次之后，会有遗漏，需要再运行一次
				System.out.println("-----------spider结束："+spider.getStatus());
				//System.out.println("-----------spider结束："+spider.getStatus());
				/*System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());
				System.out.println("-----------spider结束："+spider.getStatus());*/

				//logger.info("spider："+spider.getStatus());
				//@SuppressWarnings("resource")
				//AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/conf/applicationContext.xml");
		    	//ctx.registerShutdownHook();
		    	List<TrainCodeVO> list = null;
		    	try {
				    	list = crawlerService.get12306TrainCode(dates);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    	if(list == null) return;
		    		Spider spider = new Spider(new TrainDetail12306Page());
		    	for(TrainCodeVO vo : list){
		    		String train_cod =vo.getTrainCode();
		    		String start_st = vo.getStart_();
		    		String end_st =vo.getEnd_();
		    		String date_s = vo.getDates();
		    		try {
						start_st = crawlerService.get12306Station(start_st);
					} catch (Exception e) {
						e.printStackTrace();
					}
		    		try {
						end_st = crawlerService.get12306Station(end_st);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        	String url = "https://kyfw.12306.cn/otn/czxx/queryByTrainNo?"
		        			+ "train_no="+train_cod+"&from_station_telecode="+start_st+"&to_station_telecode="+end_st+"&depart_date="+date_s;
		    		spider.addUrl(url);
		    	}
		    	spider.setScheduler(
						new QueueScheduler()
								.setDuplicateRemover(new HashSetDuplicateRemover()));
				spider.addPipeline(new TrainDetail12306Pipeline())
				.thread(10);
				TrainNextDayMoniter nextMoniter = new TrainNextDayMoniter(spider);
				nextMoniter.start();
				spider.run();
				break;
			}
		}
	}
}
