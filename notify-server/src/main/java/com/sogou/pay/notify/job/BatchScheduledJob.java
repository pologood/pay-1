package com.sogou.pay.notify.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author qibaichao
 * @ClassName BatchScheduledJob
 * @Date 2014年9月18日
 * @Description: 批处理定时任务
 */
@Service
public abstract class BatchScheduledJob extends AbstractScheduledJob {

    private static final Logger logger = LoggerFactory.getLogger(BatchScheduledJob.class);

    /**
     * @Author qibaichao
     * @MethodName doAction
     * @Date 2014年9月22日
     * @Description:
     */
    @Override
    protected void doAction() {

        /**
         * 初始化
         */
        init();

        while (true) {

            /**
             * 分批次获取数据
             */
            List<Object> objectList = getProcessObjectList();
            /**
             * 结束判断
             */
            if (isStop(objectList)) {
                break;
            }
            /**
             * 批量处理
             */
            batchProcess(objectList);
            /**
             * GC
             */
            objectList = null;
        }
        /**
         * 任务结束，清理内存
         */
        finish();

    }

    /**
     * @Author qibaichao
     * @MethodName init
     * @Date 2014年9月18日
     * @Description:生成查询对象.
     */
    public void init() {

    }

    /**
     * @Author qibaichao
     * @MethodName finish
     * @Date 2014年9月18日
     * @Description:任务结束
     */
    public void finish() {

    }

    /**
     * @param objectList
     * @return
     * @Author qibaichao
     * @MethodName isStop
     * @Date 2014年9月22日
     * @Description:
     */
    public boolean isStop(List<Object> objectList) {
        /**
         * 当对象为空时，停止处理
         */
        if (null == objectList || objectList.isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * @return
     * @Author qibaichao
     * @MethodName getProcessObjectList
     * @Date 2014年9月18日
     * @Description:查询任务
     */
    public abstract List<Object> getProcessObjectList();

    /**
     * @param objectList
     * @Author qibaichao
     * @MethodName batchProcess
     * @Date 2014年9月18日
     * @Description:批量处理任务
     */
    public abstract void batchProcess(List<Object> objectList);

    /**
     * @param list
     * @return
     * @Author qibaichao
     * @MethodName castToObjectList
     * @Date 2014年9月22日
     * @Description:
     */
    public List<Object> castToObjectList(List<?> list) {

        List<Object> objectList = new ArrayList<Object>();
        objectList.addAll(list);

        return objectList;
    }

}
