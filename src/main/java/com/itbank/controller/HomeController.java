package com.itbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.itbank.service.KreamCrawler;

@Controller
public class HomeController {

	@Autowired
	KreamCrawler crawler = new KreamCrawler();
	
	@GetMapping("/")
	public String home() {
		return "home";
	}
	
	@GetMapping("/crawler")
	public String test() {
		
		
		
		// 2, 7, 11은 부모카테고리이므로 제외
		for(int i=1;i<17;i++) {
			if(i == 2 || i == 7 || i == 11) {
				continue;
			}
			crawler.crawling("https://kream.co.kr/search?category_id="+i,i);
		}
		
		return "home";
	}
	
	
}
