package com.kunbo.app.modle;

/**
 * 用户个人信息
 */
public class User {
    private static User instance;
    private String personId = "";//个人信息id
    private String userId = "";//系统用户id
    private String name = "";//姓名
    private String sex = "";//性别
    private String age = "";//年龄
    private String county = "";//籍贯
    private String mobile = "";//手机
    private String account = "";//工号，登录账号
    private String photo = "";//头像
    private String job = "";//岗位
    private String stationId = "";//站点id
    private String stationName = "";//站点名称
    private String teamName = "";//所在班组
    private String teamId = "";//所在班组id
    private String role = "";//角色  monitor班长
    private String signature = "";//签名
    public int[] unReads = new int[]{0, 0, 0};

    public int[] getUnReads() {
        return unReads;
    }

    public void setUnReads(int[] unReads) {
        this.unReads = unReads;
    }

    public synchronized static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}


