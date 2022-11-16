package com.beer.BeAPro.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String hashtag;


    // == 생성 메서드 == //
    public static ProjectHashtag createProjectHashtag(Project project, String hashtag) {
        ProjectHashtag projectHashtag = new ProjectHashtag();

        projectHashtag.project = project;
        projectHashtag.hashtag = hashtag;

        return projectHashtag;
    }
}
