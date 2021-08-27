package com.itbank.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itbank.model.Crawler;
import com.itbank.model.ProductDAO;
import com.itbank.model.ProductDTO;

@Service
public class KreamCrawler implements Crawler {
	
	@Autowired
	private ProductDAO productDao;
	
	public Document getDoc(String url) {
		// 페이지 모든 소스 담기
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	@Override
	public void crawling(String url,int category) {
		
		String cat = null;
		switch (category) {
		case 1:
			cat = "스니커즈";
			break;
		case 3:
			cat = "아우터";
			break;
		case 4:
			cat = "상의";
			break;
		case 5:
			cat = "하의";
			break;
		case 6:
			cat = "의류/기타";
			break;
		case 8:
			cat = "모자";
			break;
		case 9:
			cat = "가방";
			break;
		case 16:
			cat = "지갑 및 카드홀더";
			break;
		case 10:
			cat = "패션 잡화/기타";
			break;
		case 12:
			cat = "그래픽카드";
			break;
		case 13:
			cat = "게임기";
			break;
		case 14:
			cat = "테크/기타";
			break;
		case 15:
			cat = "라이프";
			break;
		default:
			break;
		}
		
		Document doc = getDoc(url);
		
		
		// <div class="main_title_box"> 태그 내부의 모든 내용이 담김
		Elements element = doc.select("div.filter_result");
			
		Iterator<Element> amount = element.select("span.amount").iterator();
		
		int amo = Integer.parseInt(amount.next().text().replace(",", ""));
		List<String> list;	// 카테고리 별 제품 url 담을 list
		if(amo > 50) {		// 등록된 물품이 50개 이상이라면 35제품을 스크래핑
			list = urlList(doc, 35);
			
		} else {			// 50이하라면 4제품을 스크래핑
			list = urlList(doc, 4);
		}
		for(String productUrl : list) {
			ProductDTO dto = getData(productUrl);
			dto.setCategory(cat);
			System.out.println(dto.toString());
			int check = 0;
			try {
				check = productDao.insertProduct(dto);
			} catch (Exception e) {
				System.out.println("productDAO insertProduct에서 오류발생");
			}
			if(check == 1) { 
				int idx = productDao.getIdx() - 1;		// insert된 product의 idx를 가져옴
				ArrayList<String> imgSrclist = getSrcList(productUrl);
				
				for(String src : imgSrclist) {
					System.out.println("idx : "+idx+" src : "+src);
					productDao.insertProductImg(idx,src);
				}
			}
		}
		
	}
	
	private ArrayList<String> getSrcList(String productUrl) {
		Document doc = getDoc(productUrl);
		ArrayList<String> srclist = new ArrayList<String>();
		
		//imgSrc
		Elements slideItem = doc.select("div.slide_item");
		Elements imgList = slideItem.select("img.product_img");
		for(Element img : imgList) {
			srclist.add(img.attr("abs:src"));
		}
		return srclist;
	}

	private ProductDTO getData(String productUrl) {
		Document doc = getDoc(productUrl);
		
		// product
		ProductDTO dto = new ProductDTO();
		Elements column = doc.select("div.column");
		String productName = column.select("h2.title").text();
		String krName = column.select("p.sub_title").text();
		Elements detailProduct = column.select("dl.detail_product");
		for(Element data : detailProduct) {
			String title = data.select("dt.product_title").text();
			String info = data.select("dd.product_info").text();
			switch (title) {
			case "브랜드":
				dto.setBrand(info);
				break;
			case "모델번호":
				dto.setModel(info);
				break;
			case "대표색상":
				dto.setColor(info);
				break;
			case "출시일":
				dto.setRdate(info);
				break;
			case "발매가":
				dto.setPrice(info);
				break;
			default:
				break;
			}
		}
		dto.setProductName(productName);
		dto.setKrName(krName);
		return dto;
	}

	public List<String> urlList(Document doc, int cnt) {
		List<String> urlList = new ArrayList<String>();
		
		Elements searchResultList = doc.select("a.item_inner");
		for(Element ele : searchResultList) {
			urlList.add(ele.attr("abs:href"));
		}
		return urlList;
	}

}
