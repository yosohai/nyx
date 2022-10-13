package com.chint.dama.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chint.dama.dao.po.VisitorPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VisitorMapper extends BaseMapper<VisitorPO> {

    @Select("SELECT  a.* from t_visitor a left join t_visitor b on a.id = b.id \n" +
            "where a.id >= (select id from t_visitor order by id limit #{offset},1) \n" +
            "order by a.id limit #{pageSize}")
    List<VisitorPO> page(int pageNo, int pageSize, long offset);
}
