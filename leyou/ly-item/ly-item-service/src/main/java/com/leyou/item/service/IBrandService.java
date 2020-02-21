package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;

public interface IBrandService {
    /**
     * 分页查询
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key);

    /**
     *新增品牌
     * @param brand
     * @param cids
     */
    void saveBrand(Brand brand, List<Long> cids);

    List<Brand> queryByCid(Long cid);

    List<Brand> queryBrandByIds(List<Long> ids);
}
