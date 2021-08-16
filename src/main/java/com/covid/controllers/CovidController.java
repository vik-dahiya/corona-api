package com.covid.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.covid.models.LocationStats;
import com.covid.service.CovidDataService;

@Controller
public class CovidController {
	
	@Autowired
	private CovidDataService serv;
	
	@GetMapping("/")
	public String info(Model model) {
		List<LocationStats> allStats = serv.getAllStats();
		int totalReportedCases = allStats.stream().mapToInt(
				stat -> stat.getLatestTotalCases()).sum();
		int totalNewCases = allStats.stream().mapToInt(
				stat -> stat.getDiffFromPrevDay()).sum();
		model.addAttribute("locationStats", allStats);
		model.addAttribute("totalReportedCases", totalReportedCases);
		model.addAttribute("totalNewCases", totalNewCases);
		return "info";
	}

}
