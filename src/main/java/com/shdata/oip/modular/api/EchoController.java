package com.shdata.oip.modular.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xieguojun
 * @author (2021 / 12 / 17 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/")
public class EchoController {


    @GetMapping("echo/{name}")
    public String echo(@PathVariable String name) {
        return "Hello " + name;
    }
}
