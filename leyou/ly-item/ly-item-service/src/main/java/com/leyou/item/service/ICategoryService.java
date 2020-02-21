package com.leyou.item.service;

import com.leyou.item.pojo.Category;

import java.util.List;

public interface ICategoryService {

    /**
     * 根据pid查询子类目
     * @param pid
     * @return
     */
    List<Category> queryCategoryListByParentId(Long pid);

    List<String> queryNameByIds(List<Long> asList);

    /**
     * 根据3级分类id，查询1~3级的分类
     * @param id
     * @return
     */
    List<Category> queryAllByCid3(Long id);
}
