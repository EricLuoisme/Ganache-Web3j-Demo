package com.example.trick.stream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapEntryDemo {

    public static void main(String[] args) {

        // 对于临时需要生成的Map, 可以使用SimpleEntry,
        // 可以临时的处理而不依赖record类型, 或者生成一个Dto

        // 1) 查询所有相关的Dto
        List<TaskDto> taskDtos = generateListOfTaskDto();
        List<Long> relatedTaskParamId = taskDtos.stream()
                .map(TaskDto::getTaskParamId)
                .distinct()
                .collect(Collectors.toList());

        // 2) 查询返回一个Map, 准备组装
        Map<Long, TaskParamDto> relatedTaskParamMap = findRelatedTaskParamMap(relatedTaskParamId);

        // 3) 使用SimpleEntry, 而不是循环的get
        List<TaskDto> param2TaskList = taskDtos.stream()
                .map(dto -> new AbstractMap.SimpleEntry<>(dto, relatedTaskParamMap.get(dto.getTaskParamId())))
                .filter(entry -> "Param2".equalsIgnoreCase(entry.getValue().getParamStuff()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskDto {
        private Long id;
        // 需要查询taskParam
        private Long taskParamId;
        private String ownTaskStuff;
        // 需要查询taskParam
        private String taskParamStuff;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskParamDto {
        private Long id;
        private String paramStuff;
    }


    private static List<TaskDto> generateListOfTaskDto() {
        TaskDto ownA = TaskDto.builder()
                .id(1L)
                .ownTaskStuff("ownA")
                .taskParamId(1L)
                .build();
        TaskDto ownB = TaskDto.builder()
                .id(2L)
                .ownTaskStuff("ownB")
                .taskParamId(1L)
                .build();
        TaskDto ownC = TaskDto.builder()
                .id(3L)
                .ownTaskStuff("ownC")
                .taskParamId(2L)
                .build();
        return List.of(ownA, ownB, ownC);
    }

    private static Map<Long, TaskParamDto> findRelatedTaskParamMap(List<Long> idList) {
        // 假设通过idList查找并返回一个Map
        TaskParamDto param1 = TaskParamDto.builder()
                .id(1L)
                .paramStuff("Param1")
                .build();
        TaskParamDto param2 = TaskParamDto.builder()
                .id(2L)
                .paramStuff("Param2")
                .build();
        return List.of(param1, param2).stream()
                .collect(
                        Collectors.toMap(
                                TaskParamDto::getId, Function.identity()));
    }

}
