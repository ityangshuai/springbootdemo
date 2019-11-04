package com.ityangshuai;

public class SysPermission {
    private Integer id;

    private String permissioname;

    private Integer roleId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPermissioname() {
        return permissioname;
    }

    public void setPermissioname(String permissioname) {
        this.permissioname = permissioname == null ? null : permissioname.trim();
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}