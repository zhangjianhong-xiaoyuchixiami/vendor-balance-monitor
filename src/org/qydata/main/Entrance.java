package org.qydata.main;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.qydata.po.ApiCost;
import org.qydata.tools.CalendarAssistTool;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jonhn on 2017/4/19.
 */
public class Entrance {

    public static void main(String[] args) {

        String resource = "mybatis.xml";
        InputStream is = Entrance.class.getClassLoader().getResourceAsStream(resource);
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(is);
        SqlSession session = sessionFactory.openSession();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Map<String,Object> mapDelete = new HashMap<>();
            mapDelete.put("years", CalendarAssistTool.getCurrentYear());
            mapDelete.put("months",CalendarAssistTool.getCurrentMonth());
            mapDelete.put("days",CalendarAssistTool.getCurrentDay());
            String statementDelete = "org.qydata.mapper.ApiCostMapper.deleteApiConsume";
            int flag = session.delete(statementDelete,mapDelete);
            session.commit();
            String statementSelect = "org.qydata.mapper.ApiCostMapper.queryApiConsume";
            Map<String, Object> mapSelect = new HashMap<>();
            List<ApiCost> apiCostList = session.selectList(statementSelect, mapSelect);
            List<ApiCost> apiCosts = new ArrayList<>();
            if (apiCostList != null && apiCostList.size() >0){
                for (int i = 0; i < apiCostList.size(); i++) {
                    ApiCost apiCostResult = apiCostList.get(i);
                    ApiCost apiCost = new ApiCost();
                    apiCost.setApiId(apiCostResult.getApiId());
                    apiCost.setYears(apiCostResult.getYears());
                    apiCost.setMonths(apiCostResult.getMonths());
                    apiCost.setDays(apiCostResult.getDays());
                    apiCost.setTotleCost(apiCostResult.getTotleCost());
                    apiCost.setUsageAmount(apiCostResult.getUsageAmount());
                    apiCost.setConsuTime(sdf.parse(apiCostResult.getYears()+"/"+apiCostResult.getMonths()+"/"+apiCostResult.getDays()));
                    apiCosts.add(apiCost);
                }
                //执行增加操作
                String statementInsert = "org.qydata.mapper.ApiCostMapper.insertApiConsume";
                int result = session.insert(statementInsert, apiCosts);
                //增删改操作一定要提交事务
                session.commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            session.close();
        }
    }
}
