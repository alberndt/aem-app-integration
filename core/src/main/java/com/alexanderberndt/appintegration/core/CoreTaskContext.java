package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.valuemap.Ranking;

public final class CoreTaskContext extends TaskContext {


    protected CoreTaskContext(GlobalContext globalContext, Ranking rank, String taskNamespace) {
        super(globalContext, rank, taskNamespace);
    }
}
