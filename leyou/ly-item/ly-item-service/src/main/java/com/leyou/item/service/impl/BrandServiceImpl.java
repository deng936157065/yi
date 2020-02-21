package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.IBrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy,
                                                      Boolean desc, String key) {
        //构建分页数据
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)){
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter",key);
        }
        if (StringUtils.isNotBlank(sortBy)) {
            String sortByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(sortByClause);
        }
        List<Brand> brandList = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brandList)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);

        }
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);
        // 返回结果
        return new PageResult<>(pageInfo.getTotal(), brandList);
    }

    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌信息
        this.brandMapper.insertSelective(brand);
        for (Long cid: cids){
            this.brandMapper.insertCategoryBrand(cid,brand.getId());
        }
    }

    public List<Brand> queryByCid(Long cid) {
        return this.brandMapper.queryByCategoryId(cid);
    }

    @Override
    public List<Brand> queryBrandByIds(List<Long> ids) {
        return this.brandMapper.selectByIdList(ids);
    }
}
