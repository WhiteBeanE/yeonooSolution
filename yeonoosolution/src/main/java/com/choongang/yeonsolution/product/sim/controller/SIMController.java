package com.choongang.yeonsolution.product.sim.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choongang.yeonsolution.product.sim.domain.CompanyDto;
import com.choongang.yeonsolution.product.sim.domain.StInDto;
import com.choongang.yeonsolution.product.sim.service.SIMService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/product/sim")
@RequiredArgsConstructor
public class SIMController {
	private final SIMService simService;

	/** 페이지 접속 */
	@GetMapping(value = {"/status", "/register"})
	public String inPage(StInDto stInDto, Model model, HttpServletRequest request) {
	    String requestURI = request.getRequestURI();
	    
	   // 기타입고 등록
	    if(requestURI.endsWith("/register")) stInDto.setOrderType("2");
	    
	    List<StInDto> stInList = simService.stInList(stInDto);
	    model.addAttribute("stInList", stInList);
	    System.out.println(requestURI);
	    
	    if(requestURI.endsWith("/register")) {
	        return "product/sim/in-register";
	    } else {
	        return "product/sim/in-status";
	    }
	}
	
	/** 페이지 접속 */
//	@GetMapping(value = "register")
//	public String InRegister(StInDto stInDto, Model model) {
//		List<StInDto> stInList = simService.stInList(stInDto);
//		model.addAttribute("stInList", stInList);
//		return "product/sim/in-register";
//	}
//	
	/** 검색 조회 */
	@RequestMapping(value = "/find")
	public String inFind(@RequestParam(required = false)Map<String, Object> data, RedirectAttributes redirectAttributes) {
		
		ObjectMapper om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		StInDto stInDto = null;
		CompanyDto companyDto = null;
		stInDto = om.convertValue(data, StInDto.class);
		companyDto = om.convertValue(data, CompanyDto.class);
		System.out.println(stInDto);
		
		String inDate = stInDto.getInDate();
		stInDto.setInDate(inDate.replace("-", "/"));
		stInDto.setCompanyDto(companyDto);
		redirectAttributes.addFlashAttribute("stInDto", stInDto);
		
		return "redirect:/product/sim/status";
	}
	
	/** Ajax 등록, 수정, 삭제 */
	@RequestMapping(value = "/ajax/{action}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, String>> inModify(@PathVariable(name = "action")String action,
														@RequestBody(required = false)List<Map<String, Object>> dataList) {
		ObjectMapper om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		StInDto stInDto = null;
		CompanyDto companyDto = null;
		Map<String, String> result = new HashMap<String, String>();
		
		for(Map<String, Object> data : dataList) {
			stInDto = om.convertValue(data, StInDto.class);
			companyDto = om.convertValue(data, CompanyDto.class);
			
			String inDate = stInDto.getInDate();
			stInDto.setInDate(inDate.replace("-", "/"));
			stInDto.setCompanyDto(companyDto);
			
			System.out.println(stInDto);
			// 수정, 삭제, 확정, 확정취소, 등록
			switch (action) {
			case "update":
				simService.modifyStIn(stInDto); break;
			case "delete":
				simService.removeStIn(stInDto); break;
			case "fix":
				simService.modifyStInFix(stInDto); break;
			case "cancel":
				simService.modifyStInCancel(stInDto); break;
			case "save":
				simService.addStIn(stInDto); break;
			}
		}
		
		return ResponseEntity.ok(result);
	}
	

	
	@RequestMapping(value = "/register/save",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> receiveData(@RequestBody(required = false) List<Map<String, Object>> data){
		System.out.println(data);
			
		return ResponseEntity.ok("등록 완료");
	}
	
	/** Ajax 회사 목록 */
	@GetMapping(value = "/findCompany")
	@ResponseBody
	public List<CompanyDto> getCompany() {
		return simService.companyList();
	}
}
