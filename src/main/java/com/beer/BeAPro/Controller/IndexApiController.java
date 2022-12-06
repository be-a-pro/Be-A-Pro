package com.beer.BeAPro.Controller;

import com.beer.BeAPro.Domain.Category;
import com.beer.BeAPro.Domain.Project;
import com.beer.BeAPro.Dto.ResponseDto;
import com.beer.BeAPro.Service.ProjectService;
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

    @GetMapping("/new/projects")
    public ResponseEntity<ResponseDto.GetProjectListInIndexDto> getProjectList(@RequestParam(required = false) String position) {
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
            }
        }
        List<Project> projects = projectService.pagingProjectListInIndex(category);

        // 프로젝트 목록에 보일 전체 데이터 DTO로 변환
        List<ResponseDto.TotalDataOfProjectListDto> projectList = new ArrayList<>();
        for (Project project : projects) {
            projectList.add(projectService.getTotalDataOfProjectList(project));
        }

        // 프로젝트 목록
        ResponseDto.GetProjectListInIndexDto responseDto = ResponseDto.GetProjectListInIndexDto.builder()
                .projectList(projectList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
