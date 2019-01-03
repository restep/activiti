drop table if exists t_leave;
create table t_leave 
(
   id                   int(11) primary key not null auto_increment comment '主键',
   process_instance_id  varchar(64)                                 comment '流程ID',
   user_id              varchar(20)  not null                       comment '工号',
   start_time           timestamp    not null                       comment '开始时间',
   end_time             timestamp    not null                       comment '结束时间',
   leave_type           varchar(20)                                 comment '请假类型',
   reason               varchar(2000)                               comment '原因',
   apply_time           timestamp    not null                       comment '申请时间',
   reality_start_time   timestamp                                   comment '实际开始时间',
   reality_end_time     timestamp                                   comment '实际结束时间'
) comment = '请假表'