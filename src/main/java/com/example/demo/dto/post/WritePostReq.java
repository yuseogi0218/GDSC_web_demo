package com.example.demo.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode // 왜 필요하지?
public class WritePostReq {

    @ApiModelProperty(example = "게시글 제목")
    private String title;

    @ApiModelProperty(example = "게시글 내용")
    private String content;

    @Builder
    public WritePostReq(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
