package org.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "上传完成凭证请求")
public class CompleteProofRequest {

    @Schema(description = "完成凭证图片URL", required = true)
    private String proofImageUrl;
}
