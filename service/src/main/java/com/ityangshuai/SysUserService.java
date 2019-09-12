package com.ityangshuai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserService {

    @Autowired
    private SysUserMapper mapper;

    public SysUser selectByPrimaryKey(Integer id){
        return mapper.selectByPrimaryKey(id);
    }

}
