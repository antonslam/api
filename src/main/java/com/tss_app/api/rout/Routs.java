package com.tss_app.api.rout;


import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;
import com.tss_app.api.request_class.convert;


import java.io.IOException;


@RestController
@RequestMapping("api")
public class Routs {

    @PostMapping("convert")
    public String convert(@RequestBody String json) throws IOException {
        convert  obj = new Gson().fromJson(json, convert.class);
        if (!obj.type.isEmpty() && !obj.base.isEmpty()){
            if (obj.SaveFile()) {
                return obj.convert2PDF();
            }else{
                return "Ошибка сохранения";
            }
        }
            return "Нет данных";
    }

}
