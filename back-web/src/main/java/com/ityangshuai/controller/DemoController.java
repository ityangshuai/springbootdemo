package com.ityangshuai.controller;

import com.ityangshuai.SysUser;
import com.ityangshuai.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Jason
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private SysUserService sysUserService;


    @RequestMapping("/find")
    @ResponseBody
    public SysUser find(Integer id){
        SysUser userBean = sysUserService.selectByPrimaryKey(id);
        logger.info("---------- /demo/find ----> " +userBean.toString());
        return userBean;
    }

}
