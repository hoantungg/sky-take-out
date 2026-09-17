package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    @Select("""
              select sum(amount)
              from orders
              where status = #{status}
                and checkout_time > #{begin}
                and checkout_time < #{end}
              """)
    Double sumByMap(
            HashMap<String, Object> map
    );

    @Select("""
          select count(id)
          from user
          where create_time > #{begin}
            and create_time < #{end}
          """)
    Integer countNewUsers(
            HashMap<String, Object> map
    );

    @Select("""
          select count(id)
          from user
          where create_time < #{end}
          """)
    Integer countTotalUsers(
            HashMap<String, Object> map
    );

    @Select("""
          select count(id)
          from orders
          where order_time > #{begin}
            and order_time < #{end}
          """)
    Integer countOrders(
            HashMap<String, Object> map
    );

    @Select("""
          select count(id)
          from orders
          where checkout_time > #{begin}
            and checkout_time < #{end}
            and status = #{status}
          """)
    Integer countValidOrders(
            HashMap<String, Object> map
    );

    @Select("""
          select od.name as name,
                 sum(od.number) as number
          from order_detail od
          inner join orders o
                  on od.order_id = o.id
          where o.status = #{status}
            and o.order_time > #{begin}
            and o.order_time < #{end}
          group by od.name
          order by number desc
          limit 10
          """)
    List<Map<String, Object>> salesTop10(
            Map<String, Object> map
    );

}
