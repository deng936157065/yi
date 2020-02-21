package com.leyou.item.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupsMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.mapper.SpecificationMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Specification;
import com.leyou.item.service.ISpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationServiceImpl implements ISpecificationService {

    @Autowired
    private SpecParamMapper specParamMapper;
    @Autowired
    private SpecGroupsMapper specGroupsMapper;
    /**
     * 查询规格参数
     * @param id
     * @return
     */
    public List<SpecGroup> querySpecGroups(Long id) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(id);
        return this.specGroupsMapper.select(specGroup);    }

    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching, Boolean generic) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setSearching(searching);
        param.setGeneric(generic);
        List<SpecParam> specParamList = specParamMapper.select(param);
        if (CollectionUtils.isEmpty(specParamList)) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return specParamList;
    }


    public List<SpecGroup> querySpecsByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> groups = this.querySpecGroups(cid);
        SpecParam param = new SpecParam();
        groups.forEach(g -> {
            // 查询组内参数
            g.setParams(this.querySpecParams(g.getId(), null, null, null));
        });
        return groups;
    }
}
