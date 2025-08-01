package com.example.demo.DTO;

public class UserDTO {
    private Long userId;
    private String userName;
    private String fullName;
    private String email;

    private Boolean status;
    private String password;
    private String avatar;
    private Long roleId;
    private String roleName;
    private String address;
    private String phoneNumber;
    public UserDTO() {
    }

    public UserDTO(Long userId, String userName, String fullName, String email, String address,String phoneNumber, Boolean status, String avatar, Long roleId, String roleName) {
        this.userId = userId;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;

        this.status = status;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.roleName = roleName;
        this.roleId = roleId;
    }

    public UserDTO(Long userId, String userName, String fullName, String email, String address,String phoneNumber, Boolean status, String password, String avatar, Long roleId, String roleName) {
        this.userId = userId;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.password = password;
        this.avatar = avatar;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}