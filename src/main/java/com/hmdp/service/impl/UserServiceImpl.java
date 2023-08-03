package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
 */
@Slf4j
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

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 1、校验手机号
        String phone = loginForm.getPhone();
        if (phone == null || RegexUtils.isPhoneInvalid(phone)){
            return Result.fail("手机号格式不对");
        }
        // 校验验证码
        Object cacheCode = session.getAttribute("code");
        String code = loginForm.getCode();
        if (code == null || !cacheCode.toString().equals(code)){
            return Result.fail("验证码不对");
        }
        // 查询是否已注册
        User user = query().eq("phone", phone).one();
        // 没注册就注册
        if (user == null){
            user = createUserWithPhone(phone);
        }
        // 已注册直接存入session
        session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));
        return Result.ok();
    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName("user_"+RandomUtil.randomNumbers(10));
        save(user);
        return user;
    }
}
