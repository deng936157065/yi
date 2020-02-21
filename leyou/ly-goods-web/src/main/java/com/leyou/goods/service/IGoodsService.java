package com.leyou.goods.service;

import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.Spu;

import java.util.List;
import java.util.Map;

public interface IGoodsService {

    Map<String,Object> loadModel(Long spuId);

    List<Category> getCategories(Spu spu);
}
