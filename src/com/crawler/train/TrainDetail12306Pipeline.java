package com.crawler.train;

import java.util.List;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.crawler.CrawlerService;
import com.crawler.util.BeanUtil;
import com.crawler.vo.TrainVO;


public class TrainDetail12306Pipeline implements Pipeline {

	private CrawlerService crawlerService = (CrawlerService) BeanUtil.getBean("crawlerService");
	@Override
	public void process(ResultItems resultItems, Task task) {
		List<TrainVO> list = resultItems.get("trainDetail");
		if (list == null)
			return;
		for (int i = 0; i < list.size(); i++) {
			TrainVO vo = list.get(i);
			try {
				crawlerService.saveTrainDetail(vo);//保存车次详情数据到数据库
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
