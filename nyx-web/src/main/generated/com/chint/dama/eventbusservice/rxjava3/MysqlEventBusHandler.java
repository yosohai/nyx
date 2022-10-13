/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.chint.dama.eventbusservice.rxjava3;

import io.vertx.rxjava3.RxHelper;
import io.vertx.rxjava3.ObservableHelper;
import io.vertx.rxjava3.FlowableHelper;
import io.vertx.rxjava3.impl.AsyncResultMaybe;
import io.vertx.rxjava3.impl.AsyncResultSingle;
import io.vertx.rxjava3.impl.AsyncResultCompletable;
import io.vertx.rxjava3.WriteStreamObserver;
import io.vertx.rxjava3.WriteStreamSubscriber;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.lang.rx.RxGen;
import io.vertx.lang.rx.TypeArg;
import io.vertx.lang.rx.MappingIterator;

/**
 * Created by Ruohong Cheng on 2021/11/26 19:34
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link com.chint.dama.eventbusservice.MysqlEventBusHandler original} non RX-ified interface using Vert.x codegen.
 */

@RxGen(com.chint.dama.eventbusservice.MysqlEventBusHandler.class)
public class MysqlEventBusHandler {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MysqlEventBusHandler that = (MysqlEventBusHandler) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<MysqlEventBusHandler> __TYPE_ARG = new TypeArg<>(    obj -> new MysqlEventBusHandler((com.chint.dama.eventbusservice.MysqlEventBusHandler) obj),
    MysqlEventBusHandler::getDelegate
  );

  private final com.chint.dama.eventbusservice.MysqlEventBusHandler delegate;
  
  public MysqlEventBusHandler(com.chint.dama.eventbusservice.MysqlEventBusHandler delegate) {
    this.delegate = delegate;
  }

  public MysqlEventBusHandler(Object delegate) {
    this.delegate = (com.chint.dama.eventbusservice.MysqlEventBusHandler)delegate;
  }

  public com.chint.dama.eventbusservice.MysqlEventBusHandler getDelegate() {
    return delegate;
  }


  public static com.chint.dama.eventbusservice.rxjava3.MysqlEventBusHandler create(io.vertx.rxjava3.core.Vertx vertx) { 
    com.chint.dama.eventbusservice.rxjava3.MysqlEventBusHandler ret = com.chint.dama.eventbusservice.rxjava3.MysqlEventBusHandler.newInstance((com.chint.dama.eventbusservice.MysqlEventBusHandler)com.chint.dama.eventbusservice.MysqlEventBusHandler.create(vertx.getDelegate()));
    return ret;
  }

  public static com.chint.dama.eventbusservice.rxjava3.MysqlEventBusHandler createProxy(io.vertx.rxjava3.core.Vertx vertx, java.lang.String address) { 
    com.chint.dama.eventbusservice.rxjava3.MysqlEventBusHandler ret = com.chint.dama.eventbusservice.rxjava3.MysqlEventBusHandler.newInstance((com.chint.dama.eventbusservice.MysqlEventBusHandler)com.chint.dama.eventbusservice.MysqlEventBusHandler.createProxy(vertx.getDelegate(), address));
    return ret;
  }

  public io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> getList(io.vertx.core.json.JsonObject jsonObject) { 
    io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> ret = rxGetList(jsonObject);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  public io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> rxGetList(io.vertx.core.json.JsonObject jsonObject) { 
    return AsyncResultSingle.toSingle(delegate.getList(jsonObject), __value -> __value);
  }

  public static final int NO_NAME_ERROR = com.chint.dama.eventbusservice.MysqlEventBusHandler.NO_NAME_ERROR;
  public static final int BAD_NAME_ERROR = com.chint.dama.eventbusservice.MysqlEventBusHandler.BAD_NAME_ERROR;
  public static MysqlEventBusHandler newInstance(com.chint.dama.eventbusservice.MysqlEventBusHandler arg) {
    return arg != null ? new MysqlEventBusHandler(arg) : null;
  }

}
