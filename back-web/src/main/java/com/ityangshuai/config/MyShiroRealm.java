package com.ityangshuai.config;

import com.ityangshuai.*;
import com.ityangshuai.util.ShiroUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 实现AuthorizingRealm接口用户用户认证
 */
public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    private LoginService loginService;

    //角色权限和对应权限添加
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录用户名
        String name= (String) principalCollection.getPrimaryPrincipal();
        //查询用户名称
        SysUser user = loginService.findUserByName(name);
        List<SysUserRole> userRoles = loginService.findAllRoleByUserId(user.getId());
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        for (SysUserRole userRole : userRoles) {
            //添加角色
            SysRole sysRole = loginService.selectRoleById(userRole.getRoleId());
            simpleAuthorizationInfo.addRole(sysRole.getRolename());
            List<SysPermission> permissions = loginService.findByRoleId(sysRole.getId());
            for (SysPermission permission:permissions) {
                //添加权限
                simpleAuthorizationInfo.addStringPermission(permission.getPermissioname());
            }
        }
        return simpleAuthorizationInfo;
    }

    //用户认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        SysUser user = loginService.findUserByName(token.getUsername());
        if (user == null){
            throw new AuthenticationException("用户不存在！");
        }
        //盐值
        ByteSource salt = ByteSource.Util.bytes(user.getSalt());
        String saltPassword = ShiroUtil.salt(token.getPassword(), salt.toString());
        if (!user.getPassword().equals(saltPassword)){
            throw new AuthenticationException("输入密码不正确！");
        }

        //这里验证authenticationToken和simpleAuthenticationInfo的信息，第4个参数是realm名称
        AuthenticationInfo authInfo = new SimpleAuthenticationInfo(token.getPrincipal(), token.getPassword(), salt, getName());
        //不加盐
        //AuthenticationInfo authInfo = new SimpleAuthenticationInfo(token.getPrincipal(), user.getPassword().toString(), getName());

        this.setSession("user", user);

        return authInfo;
    }

    /**
     * 保存登录名
     */
    private void setSession(Object key, Object value){
        Session session = getSession();
        System.out.println("Session默认超时时间为[" + session.getTimeout() + "]毫秒");
        if(null != session){
            session.setAttribute(key, value);
        }
    }

    private Session getSession(){
        try{
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession(false);
            if (session == null){
                session = subject.getSession();
            }
            if (session != null){
                return session;
            }
        }catch (InvalidSessionException e){

        }
        return null;
    }

    @Test
    public  void test(){
        ByteSource salt = ByteSource.Util.bytes("3184ded3b2474a62a4f1e60778db3d32");
        String saltPassword = ShiroUtil.salt("000000",salt.toString());
        System.out.println(saltPassword);
    }

}
