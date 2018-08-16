package com.parkinglot.admin.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.parkinglot.admin.dao.IParkingLotDao;
import com.parkinglot.admin.entity.ParkingLotEntity;
import com.parkinglot.admin.entity.ParkingPriceReportEntity;
import com.parkinglot.admin.service.IParkingLotService;
import com.parkinglot.admin.service.IParkingPriceReportService;
import com.parkinglot.common.service.ServiceException;
import com.parkinglot.common.util.JsonResult;

/**
 * @Description:
 * @version:1.0
 * @author:MilyHuang
 * @Date:May 21, 20182:32:19 PM
 * @Email:Mily-ML.Huang@aia.com
 * 
 */
// @Transactional
@Service("com.parkinglot.admin.service.impl.ParkingLotServiceImpl")
public class ParkingLotServiceImpl implements IParkingLotService {

	@Autowired
	private IParkingLotDao parkingLotDao;

	@Autowired
	private IParkingPriceReportService parkingPriceReportService;

	@Override
	public JsonResult insertParkingLot(ParkingLotEntity entity) {
		JsonResult jsonResult = new JsonResult();
		// 设置初始使用的停车位为0
		entity.setInuse(0);
		// 设置初始价格为0元/月
		entity.setPrice(0.0);
		int rows = parkingLotDao.insertParkingLot(entity);
		if (rows <= 0) {
			jsonResult = new JsonResult(new ServiceException("添加停车场失败"));
			return jsonResult;
		}
		return jsonResult;
	}

	@Override
	public List<ParkingLotEntity> selectParkingLotForList() {
		List<ParkingLotEntity> parkings = parkingLotDao.selectParkingLotForList();
		return parkings;
	}

	@Override
	public ParkingLotEntity selectParkingLotByNum(String parkingNum) {
		ParkingLotEntity parkingLot = parkingLotDao.selectParkingLotByNum(parkingNum);
		return parkingLot;
	}

	@Override
	public JsonResult updateParkingLotPrice(ParkingLotEntity entity) {
		JsonResult jsonResult = new JsonResult();
		ParkingLotEntity en = selectParkingLotByNum(entity.getParkingNum());
		en.setPrice(entity.getPrice());
		int rows = parkingLotDao.updateParkingLotPrice(en);
		if (rows <= 0) {
			jsonResult = new JsonResult(new ServiceException("更新停车场失败"));
			return jsonResult;
		}

		ParkingPriceReportEntity parkingPriceReportEntity = new ParkingPriceReportEntity();
		parkingPriceReportEntity.setParkingId(en.getId());
		parkingPriceReportEntity.setPrice(entity.getPrice());
		parkingPriceReportEntity.setDatetime(new Date());
		parkingPriceReportService.insertParkingPriceReport(parkingPriceReportEntity);
		return jsonResult;
	}

	@Override
	public JsonResult updateParkingLotInuse(ParkingLotEntity entity) {
		JsonResult jsonResult = new JsonResult();
		ParkingLotEntity parkingLot = parkingLotDao.selectParkingLotByNum(entity.getParkingNum());
		int rows = parkingLotDao.updateParkingLotInuse(entity);
		if (rows <= 0) {
			jsonResult = new JsonResult(new ServiceException("更新使用情况失败"));
			return jsonResult;
		}
		return jsonResult;
	}

	@Override
	public ParkingLotEntity selectParkingLotById(Integer parkingId) {
		return parkingLotDao.selectParkingLotById(parkingId);
	}

	@Override
	public JsonResult deleteParkingLotById(int id) {
		//删除停车场
		JsonResult jsonResult = new JsonResult();
		int rows = parkingLotDao.deleteParkingLotById(id);
		if(rows <= 0) {
			jsonResult = new JsonResult(new ServiceException("删除停车场失败！"));
		}
		else {
			jsonResult = new JsonResult(new ServiceException("删除停车场成功！"));
		}
		return jsonResult;
	}

	@Override
	public int selectInUseParkingLot(int id) {
		 
		return parkingLotDao.selectInUseParkingLot(id);
	}
}
