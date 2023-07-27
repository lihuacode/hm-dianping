package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Random;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Result send(String phone, HttpSession session) {
        // 1、确认手机号是否正确
        if (RegexUtils.isPhoneInvalid(phone)){
            // 2、不符合，给出提示
            return Result.fail("手机号无效");
        }
        // 3、符合，生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 4、验证码放入session
        session.setAttribute("code",code);
        // 5、发送验证码
        log.debug("发送短信验证码成功，验证码：{}",code);
        // 6、返回ok
        return Result.ok();
    }
}
