package com.chint.dama.mapstruct.mapper;

import com.chint.dama.base.mapstruct.BasicObjectMapper;
import com.chint.dama.dao.po.VisitorPO;
import com.chint.dama.vo.VisitorVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VisitorMapper extends BasicObjectMapper<VisitorVO, VisitorPO> {

    VisitorMapper INSTANCE = Mappers.getMapper(VisitorMapper.class);

}
