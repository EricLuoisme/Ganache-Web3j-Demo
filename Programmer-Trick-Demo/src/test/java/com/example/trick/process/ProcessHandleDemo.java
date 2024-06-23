package com.example.trick.process;

import java.util.Optional;

public class ProcessHandleDemo {

    /**
     * ProcessHandle 是对进程API的enhancement
     */
    public static void main(String[] args) {

        ProcessHandle current = ProcessHandle.current();
        System.out.println("Current Process ID: " + current.pid());

        // info about the process
        ProcessHandle.Info info = current.info();
        System.out.println("User: " + info.user().orElse("Not available"));
        System.out.println("Command: " + info.command().orElse(null));
        System.out.println("Start Time: " + info.startInstant().orElse(null));
        System.out.println("Total CPU duration: " + info.totalCpuDuration());

        // listing all live processes
        ProcessHandle.allProcesses()
                .map(ph -> ph.pid() + "->" + ph.info().command().orElse("Unknown"))
                .forEach(System.out::println);

        // try to destroy a process
        long aProcessId = 1234;
        Optional<ProcessHandle> opProcessHandle = ProcessHandle.of(aProcessId);
        opProcessHandle.ifPresent(processHandle -> {
            System.out.println("Destroying: " + processHandle.pid());
            processHandle.destroy();
        });

    }
}
