package com.tss_app.api.rout;


import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;
import com.tss_app.api.request_class.convert;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpHeaders;

@RestController
@RequestMapping("api")
public class Routs {

    @PostMapping("convert")
    public String convert(@RequestBody String json) throws IOException {
        convert  obj = new Gson().fromJson(json, convert.class);
        if (obj.is_skey() && !obj.type.isEmpty() && !obj.base.isEmpty()){
            obj.SaveFile();
            return obj.convert2PDF();
        }
            return "";
    }

}
