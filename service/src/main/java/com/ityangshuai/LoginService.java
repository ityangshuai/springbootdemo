package com.ityangshuai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    public SysUser findUserByName(String username){
        return sysUserMapper.selectByName(username);
    }
    public SysRole selectRoleById(Integer id){
        return sysRoleMapper.selectByPrimaryKey(id);
    }

    public List<SysUserRole> findAllRoleByUserId(Integer userId){
        return sysUserRoleMapper.findAllRoleByUserId(userId);
    }

    public List<SysPermission> findByRoleId(Integer roleId){
        return sysPermissionMapper.findByRoleId(roleId);
    }

    public  int addSysUser(SysUser user){
        return sysUserMapper.insert(user);
    }
}
