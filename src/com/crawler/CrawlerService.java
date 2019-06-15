package com.crawler;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crawler.dao.AppsDAO;
import com.crawler.vo.TrainCodeVO;
import com.crawler.vo.TrainVO;




@Service
public class CrawlerService {
	

	
	@Autowired
	AppsDAO appsDAO;
	
	public void saveExportedDates(String dates,String flag) throws Exception {
		appsDAO.saveExportedDates(dates,flag);
	}
	
	public void saveTrain(TrainCodeVO vo) throws Exception {
		appsDAO.saveTrain(vo);
	}
	
	public void saveTrainDetail(TrainVO vo) throws Exception {
		appsDAO.saveTrainDetail(vo);
	}
	
	public int existTrainDates(String dates) throws Exception{
		int twi = appsDAO.existTrainDates(dates);
		if(twi > 1){
				return 1;
		}else{
			return 0;
		}
	}
	
	public List<TrainCodeVO> get12306TrainCode(String dates) throws Exception{
		return appsDAO.get12306TrainCode(dates);
	}
	
	public List<TrainCodeVO> get12306TrainByDates(String dates) throws Exception{
		return appsDAO.get12306TrainByDates(dates);
	}
	public String get12306Station(String name) throws Exception{
		return appsDAO.get12306Station(name);
	}
	
	public List<String> getTrainDatesNotCrawler() throws Exception{
		return appsDAO.getTrainDatesNotCrawler();
	}

	public List<TrainVO> get12306TrainData(String dates) throws Exception{
		return appsDAO.get12306TrainData(dates);
	}
	
	public List<String> getExportDates() throws Exception{
		return appsDAO.getExportDates();
	}
}
