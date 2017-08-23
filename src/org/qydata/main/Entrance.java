package org.qydata.main;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.qydata.po.PrepayVendor;
import org.qydata.tools.SendEmail;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by jonhn on 2017/4/19.
 */
public class Entrance {

    private static String [] to  = {"ld@qianyandata.com","it@qianyandata.com","accounting@qianyandata.com"};
   // private static String [] to  = {"zhangjianhong@qianyandata.com"};

    public static SqlSession masterSqlSession(){
        //String resource_master = "mybatis_master_test.xml";
        String resource_master = "mybatis_master.xml";
        InputStream is = Entrance.class.getClassLoader().getResourceAsStream(resource_master);
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(is);
        SqlSession session = sessionFactory.openSession();
        return session;
    }

    public static SqlSession slaveSqlSession(){
        //String resource_slave = "mybatis_slave_test.xml";
        String resource_slave = "mybatis_slave.xml";
        InputStream is = Entrance.class.getClassLoader().getResourceAsStream(resource_slave);
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(is);
        SqlSession session = sessionFactory.openSession();
        return session;
    }




    public static void main(String[] args) {
        List<PrepayVendor> prepayVendorList = queryPrepayVendor();
        if (prepayVendorList == null){
            return;
        }
        for (int i = 0; i < prepayVendorList.size() ; i++) {
            PrepayVendor prepayVendor = prepayVendorList.get(i);
            if (prepayVendor == null || prepayVendor.getVendorId() == null || prepayVendor.getVendorName() == null){
                continue;
            }
            List<PrepayVendor> prepayVendor_apiList = queryApiIdByVendorId(prepayVendor.getVendorId());
            if (prepayVendor_apiList == null){
                continue;
            }
            Integer balance = prepayVendor.getBalance();
            Double sumCost = 0.0;
            Double avgeCost = 0.0;
            for (int j = 0; j < prepayVendor_apiList.size(); j++) {
                PrepayVendor prepayVendor_api = prepayVendor_apiList.get(j);
                if (prepayVendor_api.getApiId() == null || prepayVendor_api.getCost() == null){
                    continue;
                }
                Integer cost = prepayVendor_api.getCost();
                Integer count =  queryCostAmount(prepayVendor_api.getApiId());
                if (count != null){
                    sumCost = sumCost + (cost/100.0)*count;
                }
                Double avgeCost_1 = queryAvgeConsume(prepayVendor_api.getApiId());
                if (avgeCost_1 != null){
                    avgeCost = avgeCost + avgeCost_1;
                }
            }
            Double result = (balance/100.0) - sumCost;
            if (result <= 3000 && result > 2500){
                isSend(prepayVendor.getVendorId(),3000,prepayVendor.getVendorName(),result,avgeCost);
                updateOtherFlag(prepayVendor.getVendorId(),3000);
                continue;
            }
            if (result <= 2500 && result > 2000 ){
                isSend(prepayVendor.getVendorId(),2500,prepayVendor.getVendorName(),result,avgeCost);
                updateOtherFlag(prepayVendor.getVendorId(),2500);
                continue;
            }
            if (result <= 2000 && result > 1500 ){
                isSend(prepayVendor.getVendorId(),2000,prepayVendor.getVendorName(),result,avgeCost);
                updateOtherFlag(prepayVendor.getVendorId(),2000);
                continue;
            }
            if (result <= 1500 && result > 1000 ){
                isSend(prepayVendor.getVendorId(),1500,prepayVendor.getVendorName(),result,avgeCost);
                updateOtherFlag(prepayVendor.getVendorId(),1500);
                continue;
            }
            if (result <= 1000 && result > 500 ){
                isSend(prepayVendor.getVendorId(),1000,prepayVendor.getVendorName(),result,avgeCost);
                updateOtherFlag(prepayVendor.getVendorId(),1000);
                continue;
            }
            if (result <= 500){
                isSend(prepayVendor.getVendorId(),500,prepayVendor.getVendorName(),result,avgeCost);
                updateOtherFlag(prepayVendor.getVendorId(),500);
            }
        }
    }

    /**
     * 判断是否发送邮件
     * @param vid
     * @param level
     * @param name
     * @param result
     * @param avgeCost
     */
    public static void isSend(Integer vid, Integer level, String name,Double result,Double avgeCost){
        Integer flag = queryFlag(vid,level);
        if (flag == null){
            insertFlag(vid,level,-1);
            send(name,result,avgeCost);
        }else {
            if (flag == 1){
                send(name,result,avgeCost);
                updateFlag(vid,level,-1);
            }
        }
    }

    /**
     * 查询需要送发送余额不足报警的上游
     * @return
     */
    public static List<PrepayVendor> queryPrepayVendor(){
        SqlSession session = slaveSqlSession();
        String queryPrepayVendor = "org.qydata.mapper.ApiBanMapper.queryPrepayVendor";
        List<PrepayVendor> prepayVendorList = session.selectList(queryPrepayVendor);
        session.close();
        return prepayVendorList;
    }

    /**
     * 根据供应商Id查询对应产品Id
     * @param vid
     * @return
     */
    public static List<PrepayVendor> queryApiIdByVendorId(Integer vid){
        SqlSession session = slaveSqlSession();
        String queryApiIdByVendorId = "org.qydata.mapper.ApiBanMapper.queryApiIdByVendorId";
        List<PrepayVendor> prepayVendor_apiList =  session.selectList(queryApiIdByVendorId,vid);
        session.close();
        return prepayVendor_apiList;
    }

    /**
     * 查询产品扣费条数
     * @param aid
     * @return
     */
    public static Integer queryCostAmount(Integer aid){
        SqlSession session = slaveSqlSession();
        String queryCostAmount = "org.qydata.mapper.ApiBanMapper.queryCostAmount";
        Integer count =  session.selectOne(queryCostAmount,aid);
        session.close();
        return count;
    }

    /**
     * 查询产品日平均消费金额
     * @param aid
     * @return
     */
    public static Double queryAvgeConsume(Integer aid){
        SqlSession session = slaveSqlSession();
        String queryAvgeConsume = "org.qydata.mapper.ApiBanMapper.queryAvgeConsume";
        Double avgeCost_1 = session.selectOne(queryAvgeConsume,aid);
        session.close();
        return avgeCost_1;
    }

    /**
     * 发送邮件
     * @param name
     * @param result
     * @param avgeCost
     * @return
     */
    public static boolean send(String name,Double result,Double avgeCost){
        String title = name + "余额不足提醒";
        String content = "<html>" +
                "<body>" +
                "<div>" +
                "<span>"+ name + "剩余余额为：" + Math.round(result*100)/100.0 +"元，</span>" +
                "<span>"+ "按目前业务量大约推算" + name + "</span>" +
                "<span>"+ "一天平均消费：" + Math.round(avgeCost*100)/100.0 +"元，</span>" +
                "<span>"+ "剩余金额预计能使用：" + Math.round((result/avgeCost)*100)/100.0 +"天</span>" +
                "</div>" +
                "</body>" +
                "</html>";
        try {
            SendEmail.sendMail(to,title,content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 修改发送标志
     * @param vid
     * @param level
     * @param flag
     */
    public static void updateFlag(Integer vid,Integer level,Integer flag){
        SqlSession session = masterSqlSession();
        String updateSendFlag = "org.qydata.mapper.ApiBanMapper.updateSendFlag";
        Map<String,Object> updateMap = new HashMap<>();
        updateMap.put("vid",vid);
        updateMap.put("bal",level);
        updateMap.put("flag",flag);
        session.update(updateSendFlag,updateMap);
        session.commit();
        session.close();
    }

    /**
     * 插入发送标志
     * @param vid
     * @param level
     * @param flag
     */
    public static void insertFlag(Integer vid,Integer level,Integer flag){
        SqlSession session = masterSqlSession();
        String insertSendFlag = "org.qydata.mapper.ApiBanMapper.insertSendFlag";
        Map<String,Object> insertMap = new HashMap<>();
        insertMap.put("vid",vid);
        insertMap.put("bal",level);
        insertMap.put("flag",flag);
        session.insert(insertSendFlag,insertMap);
        session.commit();
        session.close();
    }

    /**
     * 查询发送标志
     * @param vid
     * @param level
     * @return
     */
    public static Integer queryFlag(Integer vid,Integer level){
        SqlSession session = slaveSqlSession();
        String querySendFlag = "org.qydata.mapper.ApiBanMapper.querySendFlag";
        Map<String,Object> queryMap = new HashMap<>();
        queryMap.put("vid",vid);
        queryMap.put("bal",level);
        Integer flag = session.selectOne(querySendFlag,queryMap);
        session.close();
        return flag;
    }

    /**
     * 修改其他阶段发送标志
     * @param vid
     * @param level
     */
    public static void updateOtherFlag(Integer vid,Integer level){
        HashSet<Integer> set = new HashSet<>();
        set.add(3000);
        set.add(2500);
        set.add(2000);
        set.add(1500);
        set.add(1000);
        set.add(500);
        if (set.contains(level)){
            set.remove(level);
        }
        for (Integer lev : set) {
            updateFlag(vid,lev,1);
        }
    }

}
