-- 秒杀事务转为存储过程
-- 将结束符设置为 $$
DELIMITER $$
CREATE PROCEDURE seckill.proc_execute_seckill(
  IN in_seckill_id BIGINT,
  IN in_user_phone BIGINT,
  IN in_kill_time TIMESTAMP,
  OUT out_result INT
)
  BEGIN
    DECLARE v_count INT DEFAULT 0;
    INSERT IGNORE INTO success_killed(seckill_id, user_phone, create_time, state) VALUES (in_seckill_id, in_user_phone, in_kill_time, 0);
    SELECT row_count() INTO v_count;
    IF v_count = 0 THEN
      ROLLBACK ;
      set out_result = -1;
    ELSEIF v_count < 0 THEN
      ROLLBACK ;
      SET out_result = -2;
    ELSE
      UPDATE seckill set number = number - 1
      WHERE seckill_id = in_seckill_id
            AND number > 0
            AND start_time < in_kill_time
            AND end_time > in_kill_time;
      SELECT row_count() INTO v_count;
      IF v_count = 0 THEN
        ROLLBACK ;
        SET out_result = 0;
      ELSEIF v_count < 0 THEN
        ROLLBACK ;
        SET out_result = -2;
      ELSE
        COMMIT ;
        SET out_result = 1;
      END IF ;
    END IF ;
  END $$
DELIMITER ;
-- 定义一个输出
set @v_result = -3;
-- 调用存储过程
call proc_execute_seckill(1001,18204608384,now(),@v_result);
-- 查看变量的值
select @v_result;

