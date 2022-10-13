package com.chint.dama.vertx.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chint.dama.annotation.RequestMapping;
import com.chint.dama.annotation.RouteHandler;
import com.chint.dama.base.enums.RequestMethod;
import com.chint.dama.base.enums.ResultCodeEnum;
import com.chint.dama.base.result.BaseResponse;
import com.chint.dama.common.MediaType;
import com.chint.dama.core.AbsChintHandler;
import com.chint.dama.dao.po.TTcpApiPO;
import com.chint.dama.dao.po.VisitorPO;
import com.chint.dama.service.IVisitorService;
import com.chint.dama.service.impl.TTcpApiServiceImpl;
import com.chint.dama.service.impl.VisitorServiceImpl;
import com.chint.dama.utils.ParamUtil;
import com.chint.dama.utils.SpringContextUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * 访问者控制层
 */
@RouteHandler("/visitor")
public class VisitorController extends AbsChintHandler {

    /**
     * 演示过滤
     *
     * @return
     */
    @RequestMapping(value = "/*", method = RequestMethod.ROUTE, order = 300, isFilter = true)
    public Handler<RoutingContext> appFilter() {
        return ctx -> {
            System.err.println("我是appFilter过滤器！");
            ctx.next();
        };
    }

    /**
     * list查询
     *
     * @return Handler<RoutingContext>
     */
    @RequestMapping(value = "/list/:id", method = {RequestMethod.GET, RequestMethod.POST})
    public Handler<RoutingContext> list() {
        return ctx -> {
            String str = "success" + new Random().nextInt();
            ctx.response().putHeader("content-type", MediaType.APPLICATION_JSON_VALUE).end(JSON.toJSONString(BaseResponse.succeed(str)));
        };
    }

    /**
     * list分页查询
     *
     * @return Handler<RoutingContext>
     */
    @RequestMapping(value = "/page", method = {RequestMethod.GET, RequestMethod.POST})
    public Handler<RoutingContext> page() {
        return ctx -> {
            ctx.vertx().<List<VisitorPO>>executeBlocking(promise -> {
                List<VisitorPO> visitors = null;
                try {
                    JsonObject param = ParamUtil.getRequestParams(ctx);
                    QueryWrapper<VisitorPO> wrapper = Wrappers.query();
                    IPage<VisitorPO> pageParam = new Page<>(1, 10, false);
                    if (param.containsKey("id")) {
                        wrapper.eq("id", Integer.valueOf(param.getString("id")));
                    }
                    if (param.containsKey("pageSize") && param.containsKey("pageNo")) {
                        pageParam.setCurrent(Integer.valueOf(param.getString("pageNo")));
                        pageParam.setSize(Integer.valueOf(param.getString("pageSize")));
                    }
                    IVisitorService visitorService = SpringContextUtil.getBean(VisitorServiceImpl.class);
                    wrapper.eq("deleteflag", "0");
                    IPage<VisitorPO> pageResult = visitorService.page(pageParam, wrapper);
                    visitors = pageResult.getRecords();
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                    ctx.response().end(JSON.toJSONString(BaseResponse.create(ResultCodeEnum.SERVER_BUSY)));
                }
                promise.complete(visitors);
            }, asyncResultHandler(ctx));
        };
    }

    /**
     * 单个查询
     *
     * @return
     */
    @RequestMapping(value = "/single/:id", method = RequestMethod.GET)
    public Handler<RoutingContext> single() {
        return ctx -> {
            ctx.vertx().<VisitorPO>executeBlocking(promise -> {
                VisitorPO visitorPO = null;
                try {
                    QueryWrapper<VisitorPO> wrapper = Wrappers.query();
                    JsonObject param = ParamUtil.getRequestParams(ctx);
                    if (param.containsKey("id")) {
                        wrapper.eq("id", Integer.valueOf(param.getString("id")));
                    }
                    IVisitorService visitorService = SpringContextUtil.getBean(VisitorServiceImpl.class);
                    visitorPO = visitorService.getById(Integer.valueOf(param.getString("id")));
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                    ctx.response().end(JSON.toJSONString(BaseResponse.create(ResultCodeEnum.SERVER_BUSY)));
                }
                promise.complete(visitorPO);
            }, asyncResultHandler(ctx));
        };
    }

    /**
     * 新增访客信息
     *
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Handler<RoutingContext> add() {
        return ctx -> {
            ctx.vertx().<VisitorPO>executeBlocking(promise -> {
                VisitorPO visitorPO = null;
                try {
                    JsonObject jsonObject = ParamUtil.getRequestParams(ctx);
                    IVisitorService visitorService = SpringContextUtil.getBean(VisitorServiceImpl.class);
                    visitorPO = JSON.parseObject(jsonObject.toString(), VisitorPO.class);
                    visitorService.saveOrUpdate(visitorPO);
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                    ctx.response().end(JSON.toJSONString(BaseResponse.create(ResultCodeEnum.SERVER_BUSY)));
                }
                promise.complete(visitorPO);
            }, asyncResultHandler(ctx));
        };
    }

    @RequestMapping(value = "/addTcpApi/{id}", method = RequestMethod.POST)
    public List<Map<String, Object>> addTcpApi(Map<String, Object> sourcePort, String sourceHost, String id) {
        System.out.println(id);
        TTcpApiServiceImpl tTcpApiService = SpringContextUtil.getBean(TTcpApiServiceImpl.class);
        QueryWrapper<TTcpApiPO> wrapper = Wrappers.query();
        wrapper.eq("source_port", sourcePort.get("sourcePort"));
        List<Map<String, Object>> list = tTcpApiService.listMaps(wrapper);
        return list;
    }

    @RequestMapping(value = "/self/obj/{id}", method = RequestMethod.POST)
    public List<Map<String, Object>> addTcpApi(User user, String sourceHost, String id, HttpServerRequest request, HttpServerResponse response) {
        System.out.println(user);
        System.out.println(sourceHost);
        System.out.println(id);
        System.out.println(request.absoluteURI());
        System.out.println(response.headers());
        TTcpApiServiceImpl tTcpApiService = SpringContextUtil.getBean(TTcpApiServiceImpl.class);
        QueryWrapper<TTcpApiPO> wrapper = Wrappers.query();
        List<Map<String, Object>> list = tTcpApiService.listMaps(wrapper);
        return list;
    }
}

/**
 *
 */
class User {

    private String name;

    private int age;

    private Integer salary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", salary=" + salary +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return age == user.age && Objects.equals(name, user.name) && Objects.equals(salary, user.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, salary);
    }
}
