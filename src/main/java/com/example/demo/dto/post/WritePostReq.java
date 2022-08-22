package com.example.demo.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WritePostReq {

    @ApiModelProperty(example = "게시글 제목")
    private String title;

    @ApiModelProperty(example = "게시글 내용")
    private String content;


}
