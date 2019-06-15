/**
 * IAppsDAO.java
 */
package com.crawler.dao;


import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.crawler.vo.TrainCodeVO;
import com.crawler.vo.TrainVO;




/**
 * @author Administrator
 *
 */
@Repository 
public interface AppsDAO {
	
	public void saveTrain(TrainCodeVO vo) throws Exception;
	
	public void saveTrainDetail(TrainVO vo) throws Exception;

	public void saveExportedDates(@Param("dates")String dates,@Param("flag")String flag) throws Exception;

	public List<TrainCodeVO> get12306TrainCode(@Param("dates")String dates) throws Exception;

	public List<TrainCodeVO> get12306TrainByDates(@Param("dates")String dates) throws Exception;

	public String get12306Station(@Param("name")String name) throws Exception;
	
	public int  existTrainDates(@Param("dates")String dates) throws Exception;
	
	public List<String> getTrainDatesNotCrawler() throws Exception;
	
	public List<String> getExportDates() throws Exception;

	public List<TrainVO> get12306TrainData(@Param("dates")String dates) throws Exception;

}
