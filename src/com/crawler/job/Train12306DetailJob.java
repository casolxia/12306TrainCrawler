package com.crawler.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.crawler.CrawlerService;
import com.crawler.moniter.Train12306DetailMoniter;
import com.crawler.moniter.TrainNextDayMoniter;
import com.crawler.train.TrainDetail12306Page;
import com.crawler.train.TrainDetail12306Pipeline;
import com.crawler.util.BeanUtil;
import com.crawler.vo.TrainCodeVO;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;


public class Train12306DetailJob  implements Job {

	private static Logger logger = LoggerFactory.getLogger(Train12306DetailJob.class);

	public CrawlerService crawlerService = (CrawlerService) BeanUtil.getBean("crawlerService");

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		@SuppressWarnings("resource")
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/conf/applicationContext.xml");
    	ctx.registerShutdownHook();
    	List<TrainCodeVO> list = null;
    	List<String> dateList = null;
    	//dateList.add("2019-05-02");
    	try {
			 dateList = crawlerService.getTrainDatesNotCrawler();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    	if(dateList.size()>0){
        	String dates = dateList.get(0);
        	try {
		    	list = crawlerService.get12306TrainCode(dates);
		    	logger.info("抓取日期:"+dates);
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
			.thread(8);
			spider.run();
			Train12306DetailMoniter moniter = new Train12306DetailMoniter(spider,dates);
			moniter.start();
			TrainNextDayMoniter nextMoniter = new TrainNextDayMoniter(spider);
			nextMoniter.start();//中间会有失败的情况，需要多抓取几次，重试
			spider.run();
    	}
    	
    	
	}
}
