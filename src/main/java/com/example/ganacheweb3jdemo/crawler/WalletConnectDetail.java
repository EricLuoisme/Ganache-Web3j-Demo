package com.example.ganacheweb3jdemo.crawler;

import com.sargeraswang.util.ExcelUtil.ExcelCell;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * wallet ct detail
 *
 * @author Roylic
 * 2022/6/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletConnectDetail {

    @ExcelCell(index = 1)
    private String category;

    @ExcelCell(index = 2)
    private String id;

    @ExcelCell(index = 3)
    private String homepage;

    @ExcelCell(index = 4)
    private String name;

    @ExcelCell(index = 5)
    private String image;
}
