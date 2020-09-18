package com.powernode.JDBC;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/9/15
 */
public class JDBCUtil {

    private static DruidDataSource dataSource;

    static {
        try {
            ResourceBundle db = ResourceBundle.getBundle("db");
            String driverName = db.getString("mysql.driver");
            String url = db.getString("mysql.url");
            String username = db.getString("mysql.username");
            String password = db.getString("mysql.password");
            Class.forName(driverName);
            dataSource = new DruidDataSource();
            dataSource.setDriverClassName(driverName);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setUrl(url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Example,E> PageInfo<E> pagination(Class<E> eClass,T example,int pageSize,int pageNo){
        if (eClass==null){
            throw new RuntimeException("请提供数据库表对应的对象");
        }
        Class<T> tClass= (Class<T>) example.getClass();
        Map<String, Object> map = getSql(example,null, KindOfDml.SELECT);
        String sql =(String)map.get("sql");
        List<String> filedName =(List<String>) map.get("conFiledName");
        ResultSet resultSet=null;
        List<E> result= new ArrayList<>();
        try {
            Field[] declaredFields = tClass.getDeclaredFields();
            //获取statement
            PreparedStatement preparedStatement=getStatement(example,null,filedName,sql);
            PageHelper.startPage(pageNo, pageSize);
            resultSet = preparedStatement.executeQuery();
            //结果集处理
            while (resultSet.next()){
                E resultE = eClass.newInstance();
                Field[] declaredFields1 = eClass.getDeclaredFields();
                for (Field declaredField : declaredFields1) {
                    Class<?> type = declaredField.getType();
                    Column annotation = declaredField.getAnnotation(Column.class);
                    String column = annotation.value();
                    Object object = resultSet.getObject(column);
                    String name = declaredField.getName();
                    Method setter = getSetter(eClass, type, name);
                    if (type.equals(int.class)||type.equals(Integer.class)){
                        object=(Integer)object;

                    }
                    if (type.equals(long.class)||type.equals(Long.class)){
                        object=(Long)object;

                    }
                    if (type.equals(byte.class)||type.equals(Byte.class)){
                        object=(Byte)object;

                    }
                    if (type.equals(double.class)||type.equals(Double.class)){
                        object=(Double)object;

                    }
                    setter.invoke(resultE,object);
                }
                result.add(resultE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询数据失败");
        }
        PageInfo<E> pageInfo=new PageInfo<>(result);
        return pageInfo;
    }

    public static <E> void insert(E record) {
        if (record==null){
            throw new RuntimeException("请提供数据库表对应的对象");
        }
        Map<String,Object> sqlMap = getSql(null,record, KindOfDml.INSERT);
        String sql =(String)sqlMap.get("sql");
        List<String> conFiledName =(List<String>) sqlMap.get("conFiledName");
        //获取statement
        PreparedStatement preparedStatement=getStatement(null,record,conFiledName,sql);
        try {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Example> void delete(T example){
        if (example==null){
            throw new RuntimeException("请提供数据库表对应的对象");
        }
        Map<String, Object> sqlMap = getSql(example,null, KindOfDml.DELETE);
        String sql = (String)sqlMap.get("sql");
        List<String> conFiledName = (List<String>)sqlMap.get("conFiledName");
        try {
            //获取statement
            PreparedStatement preparedStatement=getStatement(example,null,conFiledName,sql);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Example,E> void update(T example,E record) {
        if (record==null){
            throw new RuntimeException("请提供需要更新的记录");
        }
        Map<String,Object> sqlMap =getSql(example,record, KindOfDml.UPDATE);
        String sql =(String)sqlMap.get("sql");
        List<String> conFiledName =(List<String>) sqlMap.get("conFiledName");
        //获取statement
        PreparedStatement preparedStatement=getStatement(example,record,conFiledName,sql);
        try {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param example
     * @param record
     * @param kindOfDml
     * @param <T>
     * @param <E>
     * @return
     */
    private static <T extends Example,E> Map<String,Object> getSql(T example,E record, KindOfDml kindOfDml) {

        //定义容器
        StringBuilder sql=new StringBuilder();
        String condition="";
        Map<String,Object> result=new HashMap<>();
        List<String> conFiledName =new ArrayList<>();
        Class<E> eClass=null;
        Field[] recordFields=null;
        String table =null;
        if (record!=null){
            table = getTableName(record.getClass());
            eClass = (Class<E>) record.getClass();
            recordFields = eClass.getDeclaredFields();
        }
        if (example!=null){
            table=getTableName(example.getClass());
        }
        int count=0;
        switch (kindOfDml){
            case SELECT:
                sql.append("select * from ").append(table).append(" ");
                condition = getCondition(example, conFiledName);
                break;
            case INSERT:
                sql.append("insert into ").append(table);
                sql.append("(");
                for (Field declaredField : recordFields) {
                    String name = declaredField.getName();
                    Column annotation = declaredField.getAnnotation(Column.class);
                    String value = annotation.value();
                    Method getter = getGetter(eClass, name);
                    Object fieldValue=null;
                    try {
                        fieldValue= getter.invoke(record);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    boolean insert = annotation.isInsert();
                    if (fieldValue!=null&&!"".equals(fieldValue)){
                        if (insert){
                            if (count!=0){
                                sql.append(", ");
                            }
                            sql.append(value);
                            count++;
                            conFiledName.add(name);
                        }
                    }
                }
                sql.append(") values(");
                for (int i = 0; i < count; i++) {
                    if (i!=0){
                        sql.append(",");
                    }
                    sql.append("?");
                }
                sql.append(")");
                break;
            case DELETE:
                sql.append("delete from ");
                sql.append(table).append(" ");
                condition = getCondition(example, conFiledName);
                break;
            case UPDATE:
                sql.append("update ");
                sql.append(table).append(" ").append("set ");
                for (Field declaredField : recordFields) {
                    Column annotation = declaredField.getAnnotation(Column.class);
                    String columnName = annotation.value();
                    String name = declaredField.getName();
                    Method getter = getGetter(eClass, name);
                    Object fieldValue=null;
                    try {
                        fieldValue= getter.invoke(record);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (fieldValue!=null&&!"".equals(fieldValue)){
                        if (count!=0){
                            sql.append(",");
                        }
                        sql.append(columnName).append("=? ");
                        conFiledName.add(name);
                        count++;
                    }
                }
                condition = getCondition(example, conFiledName);
                break;
            default:
                throw new RuntimeException("请通过枚举对象指定需要执行的操作类型");
        }
        sql.append(condition);
        result.put("sql", sql.toString());
        result.put("conFiledName",conFiledName);
        return result;
    }

    private static <T extends Example> String getCondition(T example, List<String> filedName) {
        Class<T> tClass = (Class<T>) example.getClass();
        Field[] declaredFields = tClass.getDeclaredFields();
        StringBuilder conditon =new StringBuilder();
        int count;
        count=0;
        for (Field declaredField : declaredFields) {
            Column annotation = declaredField.getAnnotation(Column.class);
            String columnName = annotation.value();
            boolean isLike = annotation.isLike();
            String name = declaredField.getName();
            Method getter = getGetter(tClass, name);
            Object conditionValue=null;
            try {
                conditionValue = getter.invoke(example);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (conditionValue!=null&&!"".equals(conditionValue)){
                if (count!=0){
                    conditon.append("and ");
                }
                if (count==0){
                    conditon.append("where ");
                }
                conditon.append(columnName).append(" ");
                if (isLike){
                    conditon.append("like ? ");
                }else {
                    conditon.append("= ? ");
                }
                filedName.add(name);
                count++;
            }
        }
        return conditon.toString();
    }

    public static Method getSetter(Class<?> tClass,Class<?> parameterType,String fieldName){
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method declaredMethod=null;
        try {
            declaredMethod = tClass.getDeclaredMethod(methodName, parameterType);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("无法获取属性的setter方法");
        }
        return declaredMethod;
    }


    public static Method getGetter(Class<?> tClass,String fieldName){
        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method declaredMethod=null;
        try {
            declaredMethod = tClass.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("无法获取属性的getter方法");
        }
        return declaredMethod;
    }


    public static String getTableName(Class<?> tClass){
        Table declaredAnnotation = null;
        try {
            declaredAnnotation = tClass.getDeclaredAnnotation(Table.class);
        } catch (Exception e) {
            throw new RuntimeException("未注解表名");
        }

        return declaredAnnotation.value();
    }

    public static <T extends Example,E> PreparedStatement getStatement(T example,E record, List<String> fieldNames, String sql){
        PreparedStatement preparedStatement = null;

        int recColNo =sql.split("where")[0].split("[?]").length-1;
        try {
            Connection connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (example==null&&record==null){
                return preparedStatement;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取连接失败");
        }
        //注入参数
        if (record!=null){
            for (int i =1; i <=recColNo; i++) {
                Method getter = getGetter(record.getClass(), fieldNames.get(i-1));
                Object fieldValue = null;
                try {
                    fieldValue = getter.invoke(record);
                    preparedStatement.setObject(i,fieldValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (example!=null){
            for (int i = recColNo+1; i <=fieldNames.size(); i++) {
                Method getter = getGetter(example.getClass(), fieldNames.get(i-1));
                Class<? extends Example> aClass = example.getClass();
                Object conditionValue = null;
                try {
                    conditionValue = getter.invoke(example);
                    Field declaredField = aClass.getDeclaredField(fieldNames.get(i - 1));
                    Column annotation = declaredField.getAnnotation(Column.class);
                    if (annotation.isLike()){
                        conditionValue="%"+conditionValue+"%";
                    }
                    preparedStatement.setObject(i,conditionValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return preparedStatement;
    }

}
