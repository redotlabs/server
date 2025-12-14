package redot.redot_server.domain.cms.inquiry.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import redot.redot_server.domain.cms.inquiry.dto.request.RedotAppInquiryCreateRequest;
import redot.redot_server.domain.cms.inquiry.dto.request.RedotAppInquirySearchCondition;
import redot.redot_server.domain.cms.inquiry.dto.response.RedotAppInquiryResponse;
import redot.redot_server.global.security.principal.JwtPrincipal;
import redot.redot_server.global.util.dto.response.PageResponse;

@Tag(name = "CMS Inquiry", description = "CMS 문의 관리 API")
public interface RedotAppInquiryControllerDocs {

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "문의 생성", description = "CMS 앱에 새 문의를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = RedotAppInquiryResponse.class)))
    ResponseEntity<RedotAppInquiryResponse> createInquiry(@Parameter(hidden = true) Long redotAppId,
                                                          @Valid RedotAppInquiryCreateRequest request);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "문의 단건 조회", description = "문의 ID로 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = RedotAppInquiryResponse.class)))
    ResponseEntity<RedotAppInquiryResponse> getInquiry(@Parameter(hidden = true) Long redotAppId,
                                                       @Parameter(description = "문의 ID", example = "1") Long inquiryId);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "문의 목록 조회", description = "`status`, `inquiryNumber`, `title`, `inquirerName`, `startDate`, `endDate` 검색 조건과 `page`/`size`/`sort=createdAt,desc` 쿼리 파라미터로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    ResponseEntity<PageResponse<RedotAppInquiryResponse>> getAllInquiries(@Parameter(hidden = true) Long redotAppId,
                                                                          @ParameterObject RedotAppInquirySearchCondition searchCondition,
                                                                          @Parameter(description = "기본 정렬은 createdAt DESC 입니다.")
                                                                          @ParameterObject Pageable pageable);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "문의 처리 완료", description = "문의 상태를 완료로 변경합니다.")
    @ApiResponse(responseCode = "200", description = "상태 변경 완료")
    ResponseEntity<Void> markInquiryAsCompleted(@Parameter(hidden = true) Long redotAppId,
                                                @Parameter(description = "문의 ID", example = "1") Long inquiryId,
                                                @Parameter(hidden = true) JwtPrincipal jwtPrincipal);

    @Parameter(name = "X-App-Subdomain", in = ParameterIn.HEADER, required = true,
            description = "요청 대상 CMS 앱의 서브도메인")
    @Operation(summary = "문의 재오픈", description = "완료된 문의를 다시 열어 진행합니다.")
    @ApiResponse(responseCode = "200", description = "재오픈 완료")
    ResponseEntity<Void> reopenInquiry(@Parameter(hidden = true) Long redotAppId,
                                       @Parameter(description = "문의 ID", example = "1") Long inquiryId);
}
