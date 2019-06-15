package com.crawler.train;

import java.util.List;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.crawler.CrawlerService;
import com.crawler.util.BeanUtil;
import com.crawler.vo.TrainCodeVO;


public class Train12306Pipeline implements Pipeline {

	private CrawlerService crawlerService = (CrawlerService) BeanUtil.getBean("crawlerService");
	@Override
	public void process(ResultItems resultItems, Task task) {
		List<TrainCodeVO> list = resultItems.get("train");
		if (list == null)
			return;
		for (int i = 0; i < list.size(); i++) {
			TrainCodeVO vo = list.get(i);
			try {
					crawlerService.saveTrain(vo);//保存全国车次数据至数据库
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
