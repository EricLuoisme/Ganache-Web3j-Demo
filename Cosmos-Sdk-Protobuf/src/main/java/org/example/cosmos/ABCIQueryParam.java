package org.example.cosmos;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ABCIQueryParam {

    @JSONField(name = "path")
    private String path;

    @JSONField(name = "data")
    private String data;

    @JSONField(name = "height")
    private String height;

    @JSONField(name = "prove")
    private boolean prove;

}
