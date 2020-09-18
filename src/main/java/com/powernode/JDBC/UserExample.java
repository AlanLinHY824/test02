package com.powernode.JDBC;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/9/15
 */
@Table(value = "user")
public class UserExample extends Example{

    @Column(value = "uid",isPk = true)
    private Integer no;

    @Column(value = "username",isInsert = true)
    private String sname;

    @Column(value = "password",isInsert = true)
    private String pwd;

    @Column(value = "username",isLike = true)
    private String snameLike;

    @Column(value = "password",isLike = true)
    private String pwdLike;

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

    public String getSnameLike() {
        return snameLike;
    }

    public void setSnameLike(String snameLike) {
        this.snameLike = snameLike;
    }

    public String getPwdLike() {
        return pwdLike;
    }

    public void setPwdLike(String pwdLike) {
        this.pwdLike = pwdLike;
    }

    @Override
    public String toString() {
        return "User{" +
                "no=" + no +
                ", sname='" + sname + '\'' +
                ", pwd='" + pwd + '\'' +
                ", snameLike='" + snameLike + '\'' +
                ", pwdLike='" + pwdLike + '\'' +
                '}';
    }
}
