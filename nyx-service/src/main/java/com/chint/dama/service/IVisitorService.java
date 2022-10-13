package com.chint.dama.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chint.dama.dao.po.VisitorPO;

import java.util.List;

public interface IVisitorService extends IService<VisitorPO> {
    VisitorPO addVisitor(VisitorPO visitorPO);

    List<VisitorPO> page(int pageNo, int pageSize);
}
