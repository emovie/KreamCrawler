package com.itbank.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ProductDAO {
	
	@Insert("insert into product values (product_seq.nextval,#{productName},#{krName},#{model},#{brand},#{color},#{rdate},#{price},#{category})")
	int insertProduct(ProductDTO dto);

	@Select("select last_number from user_sequences where sequence_name = upper('product_seq')")
	int getIdx();
	
	@Insert("insert into productimg values (productimg_seq.nextval,#{idx},#{src})")
	void insertProductImg(@Param("idx") int idx,@Param("src") String src);

}
