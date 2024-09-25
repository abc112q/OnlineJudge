package com.example.ojbackenduserservice.controller.inner;

import com.example.ojbackendmodel.model.entity.User;
import com.example.ojbackendserviceclient.service.UserFeignClient;
import com.example.ojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author Ariel
 * 仅仅是服务之间内部互相调用的接口
 * 这里使用的UserService继承了MyBatisPlus的Iservice接口，我们重写方法就直接调用mybatisplus现呈的方法即可
 */
@RestController // 注意这里不是写路径的
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;
    /**
     * 根据id获取用户
     *
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") long userId) {
        return userService.getById(userId);
    }

    /**
     * 根据id列表获取用户列表
     * @param idList
     * @return
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList){
        return userService.listByIds(idList);
    }
}