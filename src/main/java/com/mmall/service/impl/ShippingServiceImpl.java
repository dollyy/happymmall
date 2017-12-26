package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount=shippingMapper.insert(shipping);
        if(rowCount > 0){
            Map result= Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return  ServerResponse.createByErrorMessage("新建地址失败");
    }

    @Override
    public ServerResponse<String> delete(Integer userId, Integer shippingId){
        //这里没有调用deleteByPrimaryKey这个方法是因为这样传一个不是自己的shippingId也能删掉会横向越权,
        //所以要把shipping和userId进行绑定
        int resultCount=shippingMapper.deleteByShippingAndUserId(userId, shippingId);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createBySuccessMessage("删除地址失败");
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        shipping.setId(shipping.getId());
        int rowCount=shippingMapper.updateByShipping(shipping);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return  ServerResponse.createByErrorMessage("更新地址失败");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping=shippingMapper.selectByShippingIdAndUserId(userId, shippingId);
        if(shipping == null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess(shipping);
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList=shippingMapper.selectByUserId(userId);
        PageInfo pageInfo=new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);

    }

}
