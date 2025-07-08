## Sentinel异常处理
Sentinel默认的异常处理通常不符合我们需要的交互方式。

### web接口
对于web接口，Sentinel使用了拦截器BlockExceptionHandler处理，  
我们自定义一个ExceptionHandler来处理。

![](./images/cloud-32-01.png)