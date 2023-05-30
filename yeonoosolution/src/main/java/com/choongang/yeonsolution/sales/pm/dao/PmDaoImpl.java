package com.choongang.yeonsolution.sales.pm.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.choongang.yeonsolution.sales.pm.domain.CompanyDto;
import com.choongang.yeonsolution.sales.pm.domain.ItemDto;
import com.choongang.yeonsolution.sales.pm.domain.OrdersDetailDto;
import com.choongang.yeonsolution.sales.pm.domain.OrdersDto;
import com.choongang.yeonsolution.sales.pm.domain.Search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PmDaoImpl implements PmDao{
	private final SqlSession session;

	@Override
	public List<OrdersDto> selectOrdersBySearch(Search search) {
		
		List<OrdersDto> orderList = session.selectList("selectOrdersBySearch", search);
		log.info("orderList.size() -> {}",orderList.size());
		return orderList;
	}

	@Override
	public List<OrdersDetailDto> selectOrdersDetailByOrderCode(String orderCode) {
		log.info("orderCode -> {}", orderCode);
		List<OrdersDetailDto> ordersDetailList = session.selectList("selectOrdersDetailByOrderCode", orderCode);
		log.info("ordersDetailList.size() -> {}",ordersDetailList.size());
		return ordersDetailList;
	}

	@Override
	public int updateOrdersByOrderCode(Map<String, String> map) {
		log.info("updateOrdersByOrderCode map -> {}", map);
		int result = session.update("updateOrdersByOrderCode", map);
		return result;
	}

	@Override
	public List<CompanyDto> selectCompanyBySearch(String search) {
		List<CompanyDto> companyList = session.selectList("selectCompanyBySearch", search);
		return companyList;
	}

	@Override
	public List<ItemDto> selectItemyBySearch(String search) {
		List<ItemDto> itemList = session.selectList("selectItemBySearch", search);
		return itemList;
	}

}
