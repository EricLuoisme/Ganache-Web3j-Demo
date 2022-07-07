package com.example.lighting.asynctask;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Task father
 *
 * @author Roylic
 * 2022/6/29
 */
@Data
@AllArgsConstructor
public abstract class AsyncTask {

    private AsyncTaskEnum taskName;

    private Integer preDebitId;

    private Integer debitOrderId;

    private String nodeIp;
}
