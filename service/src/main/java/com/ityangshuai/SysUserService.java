package com.ityangshuai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserService {

    @Autowired
    private SysUserMapper mapper;

    public SysUser selectByPrimaryKey(Integer id){
        return mapper.selectByPrimaryKey(id);
    }
    public List<SysUser> selectAllUser(){
            return mapper.selectAllUser();
        }

}
