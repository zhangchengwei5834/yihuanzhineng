package com.yihuankeji.pojo;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String tel;
    private java.time.LocalDateTime registerTime;
    private byte[] avatar; 

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public java.time.LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(java.time.LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
}
