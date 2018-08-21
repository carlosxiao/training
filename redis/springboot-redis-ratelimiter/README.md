# 分布式限流应用


## usage

``` java
@RestController
public class IndexController {

    @GetMapping("/t1")
    @RateLimiter(limit = 2, timeout = 10000)
    public void t1() {

    }

    @GetMapping("/t2")
    @RateLimiter(limit = 15, timeout = 10000)
    public void t2() {

    }
}
```
