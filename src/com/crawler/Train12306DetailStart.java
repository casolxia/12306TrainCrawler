package com.crawler;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.crawler.job.JobManager;
import com.crawler.util.PropertyUtil;
import com.crawler.vo.ScheduleJob;


public class Train12306DetailStart {

	public static void main(String[] args) throws Exception{
		//第一步需要先获取站点编码并解析入库
		//https://kyfw.12306.cn/otn/resources/js/framework/station_name.js?station_version=1.9002
		
		@SuppressWarnings("resource")
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/conf/applicationContext.xml");
    	ctx.registerShutdownHook();
			String crawlerDetailTime= PropertyUtil.getInstants().getProp("train_detail");
			ScheduleJob detailJob = new ScheduleJob();
			detailJob.setJobName("addCrawlerJTask");
			detailJob.setJobCron(crawlerDetailTime);//定时时间
			detailJob.setJobGroup("CountDayGroup");
			detailJob.setJobClass("com.crawler.job.Train12306DetailJob");
			JobManager.getInstance().addJob(detailJob);
			
			String crawlerTime= PropertyUtil.getInstants().getProp("train_day");
			ScheduleJob job = new ScheduleJob();
			job.setJobName("addCrawlerJTask2");
			job.setJobCron(crawlerTime);//定时时间
			job.setJobGroup("CountDayGroup");
			job.setJobClass("com.crawler.job.Train12306Job");
			JobManager.getInstance().addJob(job);
		
			
	}
}
