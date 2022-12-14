package com.beer.BeAPro.Controller;

import com.beer.BeAPro.Domain.Category;
import com.beer.BeAPro.Domain.Project;
import com.beer.BeAPro.Dto.ResponseDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Service.ProjectService;
import com.beer.BeAPro.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor
public class IndexApiController {

    private final ProjectService projectService;
    private final UserService userService;

    @Operation(summary = "NEW PROS 목록 불러오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해당하는 사용자 목록"),
            @ApiResponse(responseCode = "204", description = "해당하는 사용자 목록 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 position 파라미터 입력시"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/new/users")
    public ResponseEntity<List<ResponseDto.DataOfUserInIndexDto>> getUserList(
            @Parameter(description = "포지션 필터: development / design / planning / etc") @RequestParam(required = false) String position) {
        // 포지션 필터링
        Category category = null;
        if (position != null) {
            switch (position) {
                case "development":
                    category = Category.DEVELOPMENT;
                    break;
                case "design":
                    category = Category.DESIGN;
                    break;
                case "planning":
                    category = Category.PLANNING;
                    break;
                case "etc":
                    category = Category.ETC;
                    break;
                default:
                    throw new RestApiException(ErrorCode.INVALID_CATEGORY);
            }
        }

        // 사용자 목록에 보일 데이터 가져오기
        List<ResponseDto.DataOfUserInIndexDto> users = userService.pagingUserListInIndex(category);
        if (users == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @Operation(summary = "NEW PROJECTS 목록 불러오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해당하는 프로젝트 목록"),
            @ApiResponse(responseCode = "204", description = "해당하는 프로젝트 목록 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 position 파라미터 입력시"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/new/projects")
    public ResponseEntity<ResponseDto.ProjectListInIndexDto> getProjectList(
            @Parameter(description = "포지션 필터: development / design / planning / etc") @RequestParam(required = false) String position) {
        // 포지션 필터링
        Category category = null;
        if (position != null) {
            switch (position) {
                case "development":
                    category = Category.DEVELOPMENT;
                    break;
                case "design":
                    category = Category.DESIGN;
                    break;
                case "planning":
                    category = Category.PLANNING;
                    break;
                case "etc":
                    category = Category.ETC;
                    break;
                default:
                    throw new RestApiException(ErrorCode.INVALID_CATEGORY);
            }
        }
        List<Project> projects = projectService.pagingProjectListInIndex(category);

        // 프로젝트 목록에 보일 전체 데이터 DTO로 변환
        List<ResponseDto.TotalDataOfProjectListDto> projectList = new ArrayList<>();
        for (Project project : projects) {
            projectList.add(projectService.getTotalDataOfProjectList(project));
        }

        // 프로젝트 목록
        ResponseDto.ProjectListInIndexDto responseDto = ResponseDto.ProjectListInIndexDto.builder()
                .projectList(projectList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
