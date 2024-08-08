package com.xuecheng.messagesdk.service;

import com.xuecheng.messagesdk.model.po.MqMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author Mr.M
 * @version 1.0
 * @description Message processing abstract class
 * @date 2022/9/21 19:44
 */
@Slf4j
@Data
public abstract class MessageProcessAbstract {

    @Autowired
    MqMessageService mqMessageService;


    /**
     * @param mqMessage Execution task content
     * @return boolean true:Processing success，falseProcessing failed
     * @description Task Processing
     * @author Mr.M
     * @date 2022/9/21 19:47
     */
    public abstract boolean execute(MqMessage mqMessage);


    /**
     * @description Scan message table multi-threaded execution task
     * @param shardIndex Fragment number
     * @param shardTotal Total number of shards
     * @param messageType  Message Type
     * @param count  The total number of tasks to be retrieved at one time
     * @param timeout Estimated task execution time. If the task has not been completed by this time, it will be forced to end in seconds
     * @return void
     * @author Mr.M
    */
    public void process(int shardIndex, int shardTotal,  String messageType,int count,long timeout) {

        try {
            //Scan the message table to get the task list
            List<MqMessage> messageList = mqMessageService.getMessageList(shardIndex, shardTotal,messageType, count);
            //Number of tasks
            int size = messageList.size();
            log.debug("Get pending messages"+size+"item");
            if(size<=0){
                return ;
            }

            //Creating a Thread Pool
            ExecutorService threadPool = Executors.newFixedThreadPool(size);
            //counter
            CountDownLatch countDownLatch = new CountDownLatch(size);
            messageList.forEach(message -> {
                threadPool.execute(() -> {
                    log.debug("Start the task:{}",message);
                    //Processing tasks
                    try {
                        boolean result = execute(message);
                        if(result){
                            log.debug("Mission completed successfully:{})",message);
                            //Update task status, delete message table records, add to history table
                            int completed = mqMessageService.completed(message.getId());
                            if (completed>0){
                                log.debug("Mission completed successfully:{}",message);
                            }else{
                                log.debug("Task execution failed:{}",message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.debug("Task exception:{},Task:{}",e.getMessage(),message);
                    }finally {
                        //count
                        countDownLatch.countDown();
                    }
                    log.debug("Ending the task:{}",message);

                });
            });

            //Wait, give a sufficient timeout to prevent infinite waiting, and end the task
            // if the timeout is reached and the processing is not completed
            countDownLatch.await(timeout,TimeUnit.SECONDS);
            System.out.println("Finish....");
        } catch (InterruptedException e) {
           e.printStackTrace();

        }


    }



}
