package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Specification;

import java.util.List;

public interface ISpecificationService {

    List<SpecGroup> querySpecGroups(Long id);



    List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching, Boolean generic);

    /**
     * 查询规格组，同时在规格组中持有组内的所有参数
     * @param cid
     * @return
     */
    List<SpecGroup> querySpecsByCid(Long cid);
}
