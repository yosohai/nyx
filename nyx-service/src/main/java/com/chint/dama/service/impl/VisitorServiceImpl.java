package com.chint.dama.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chint.dama.dao.po.VisitorPO;
import com.chint.dama.dao.mapper.VisitorMapper;
import com.chint.dama.service.IVisitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("visitorServiceImpl")
@DS("master_1")
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, VisitorPO> implements IVisitorService {

    @Resource
    private VisitorMapper visitorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VisitorPO addVisitor(VisitorPO visitorPO) {
        visitorMapper.insert(visitorPO);
        return visitorPO;
    }

    @Override
    public List<VisitorPO> page(int pageNo, int pageSize) {
        return visitorMapper.page(pageNo, pageSize, pageNo * pageSize);
    }
}
