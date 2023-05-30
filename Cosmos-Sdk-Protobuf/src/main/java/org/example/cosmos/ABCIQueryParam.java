package org.example.cosmos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ABCIQueryParam {

    private String path;

    private String data;

    private String height;

    private boolean prove;

}
