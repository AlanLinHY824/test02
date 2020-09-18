package com.powernode.JDBC;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/9/15
 */
@Table(value = "user")
public class User {

    @Column(value = "uid",isPk = true)
    private Integer no;

    @Column(value = "username",isInsert = true)
    private String sname;

    @Column(value = "password",isInsert = true)
    private String pwd;


    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "User{" +
                "no=" + no +
                ", sname='" + sname + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
