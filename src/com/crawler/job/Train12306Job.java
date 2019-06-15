package com.crawler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

import com.crawler.train.Train12306Page;
import com.crawler.train.Train12306Pipeline;


public class Train12306Job implements Job {

	private static Logger logger = LoggerFactory.getLogger(Train12306Job.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
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
