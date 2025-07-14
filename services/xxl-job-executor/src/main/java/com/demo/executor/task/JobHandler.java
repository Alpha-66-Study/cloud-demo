package com.demo.executor.task;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JobHandler {

  /**
   * 简单任务示例（Bean模式）
   */
  @XxlJob("demoJobHandler")
  public void demoJobHandler() throws Exception {
    System.out.println("demoJobHandler start");
    XxlJobHelper.log("XXL-JOB, Hello World.");
    for (int i = 0; i < 5; i++) {
      XxlJobHelper.log("beat at:" + i);
      TimeUnit.SECONDS.sleep(2);
    }
  }

}
