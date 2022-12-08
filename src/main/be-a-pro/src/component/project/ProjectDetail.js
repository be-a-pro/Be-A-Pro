import Header from "../Header";
import styles from './ProjectDetail.module.css';
import imageOfProject from '../../images/project/imageOfProject.png';
import imageOfUser from '../../images/project/profile-img-b.png';
import { Link } from 'react-router-dom';
import { ReactComponent as Calendar } from '../../images/icons/calendar.svg';
import { ReactComponent as Eye } from '../../images/icons/eye.svg';
import { ReactComponent as Heart } from '../../images/icons/heart.svg';
import PostionButton from "./projectComponent/PositionButton";
import HashTag from "./projectComponent/HashTag";
import Footer from "../Footer";
import $ from 'jquery';
import jquery from 'jquery';
import { useEffect, useState, useRef } from 'react';
import PositionOfMenu from "./projectComponent/PositionOfMenu";
import UseOpenGraph from "../../hooks/UseOpenGraph";

export default function ProejctDetail() {

    const [positionOfCurrent, setCurrent] = useState(0);
    const [positionOfActive, setActive] = useState(0);
    const [like, setLike] = useState(false);
    const [tab, setTab] = useState(0)
    const quickMenu = useRef();
    const detailOfProject = useRef();

    useEffect(() => {
        var currentPosition = parseInt($(quickMenu.current).css("top"));
        var innerHeight = $(window).innerHeight();
        window.addEventListener('scroll', handleScroll(currentPosition));
    }, []);

    const handleScroll = (currentPosition) => {
        $(window).scroll(function () {
            var position = $(window).scrollTop();
            /* position + currentPostion의 값으로 퀵메뉴의 top 값을 조절 */
            if (position + currentPosition < 2450 && position + currentPosition > 1590) {
                $(quickMenu.current).stop().animate({ "top": position + currentPosition - 700 + "px" }, 1200);
            }
        });
    };

    /* 요거는 카테고리 분류 별 페이지 구현할 때 쓸 변수*/
    let state = {
        activeTab: 0,
    }

    const catergoryNav = ["프로젝트 소개", "진행 방식", "사용 프로그램 및 언어", "참고 링크"];

    const clickOfLike = () => {
        setLike(!like);
    }

    const clickHandler = (id) => {
        setTab(id)
    }

    return (
        <>
            <div className={styles.containerOfPage}>
                <div className={styles.containerOfCenter}>
                    <div className={styles.containerOfTitle}>
                        <span className={styles.title}>사이드 프로젝트 매칭 플랫폼</span>
                    </div>
                    <div className={styles.sectionOfInfo}> {/* 게시글 게시 일자, 조회수, 사용자 이미지가 들어오는 컨테이너예요! */}
                        <img src={imageOfProject}></img> {/* 이미지가 들어올거예요! */}
                        <div className={styles.containerOfProject}>
                            <div className={styles.infoOfProject}>
                                <div className={styles.infoOfUser}> {/* 사용자 이름과 사용자 이미지가 들어와요 */}
                                    <img className={styles.imageOfUser} src={imageOfUser}></img>
                                    <span className={styles.nameOfUser}>홍길동</span>
                                </div>
                                <div className={styles.detailOfBoard}>
                                    <div className={styles.containerOfCalendar}> {/* 게시글 게시 날짜와 SVG가 들어와요 */}
                                        <Calendar className={styles.imageOfCalendar} />
                                        <span className={styles.textOfCalendar}>22.05.16</span>
                                    </div>
                                    <div className={styles.containerOfView}> {/* 조회수와 SVG가 들어와요 */}
                                        <Eye className={styles.imageOfView} />
                                        <span className={styles.textOfView}>1,584</span>
                                    </div>
                                </div>
                            </div>
                            <div className={styles.textOfPosition}>모집중인 포지션</div>
                            <div className={styles.infoOfPosition}>
                                <div className={styles.containerOfPosition}>
                                    <PostionButton text="서비스 기획" />
                                    <PostionButton text="IOS" />
                                    <PostionButton text="UXUI 디자인" />
                                    <PostionButton text="안드로이드" />
                                    <PostionButton text="마케팅" />
                                </div>
                            </div>
                            <div className={styles.textOfTag}>해시태그</div>
                            <div className={styles.containerOfTag}>
                                <HashTag text="#공부" />
                                <HashTag text="#창업" />
                                <HashTag text="#부업" />
                                <HashTag text="#사이드 프로젝트" />
                                <HashTag text="#대학생" />
                                <HashTag text="#플랫폼" />
                                <HashTag text="#플랫폼" />
                                <HashTag text="#플랫폼" />
                            </div>
                            <div className={styles.containerOfButton}>
                                <button className={styles.buttonOfLike}>
                                    <Heart onClick={clickOfLike} className={like ? styles.iconOfLike_selected : styles.iconOfLike} />
                                </button>
                                <button className={styles.buttonOfCall}>연락하기</button>
                                <button className={styles.buttonOfApply}>지원하기</button>
                            </div>
                        </div>
                    </div>

                    <div className={styles.navOfBoard}>
                        {catergoryNav.map(function (item, key) {
                            return <div onClick={() => clickHandler(key)} key={key} className={tab === key ? styles.textOfNav_Line : styles.textOfNav}>{item}</div>
                        })}
                    </div>

                    <div className={styles.contentsOfProject}>
                        <div className={styles.containerOfContent}>
                            <article>
                                <span className={styles.detailOfProject}> 프로젝트 소개 </span>
                                <pre>
                                    취뽀를 위해 기업의 채용공고를 보면 자주 보이는 내용이 있습니다.<br />
                                    - 플랫폼 서비스의 준비단계부터 출시, 운영까지의 경험이 있는 분..<br />
                                    - 윈도우 서버 운영관리 경험자..<br />
                                    - 프로덕션 환경에서 지속적인 서비스 개선 및 운영 경험..<br />
                                    <br />
                                    이제 막 대학을 졸업해 사회로 진출을 꿈꾸는 사회초년생이 경험하기는 힘든 사항들이죠.<br />
                                    <br />
                                    😵‍💫 ”나같은 신입은 어디서 경력을 쌓나?”<br />
                                    <br />
                                    비어는 이러한 기업들의 요구사항에 맞춰 사회에 나가기 전,<br />
                                    실무와 비슷한 경험을 해볼 수 있는 자리를 마련해주고자 시작하였습니다.
                                </pre>
                            </article>
                            <article className={styles.articleOfDetail}>
                                <span className={styles.detailOfContents}> 상세 소개 </span>
                                <pre>
                                    현재 팀원은 프로젝트 매니저 / 풀스택(앱/스마트 컨트랙트) 개발자 / 커뮤니티 매니저로 구성되어 있습니다.<br />
                                    <br />
                                    각자 거래소 NFT 마켓 PM 및 모더레이터 / KAIST 출신으로<br />
                                    Web3와 NFT에 대한 이해도로는 감히 국내 최고수준임을 자부할 수 있습니다.<br />
                                </pre>
                            </article>
                            <hr className={styles.lineOfSplit} />
                            <article>
                                <span className={styles.detailOfProject}> 진행방식 </span>
                                <pre>
                                    - 디스코드 : 주 1회 비대면 회의 <br />
                                    - 슬랙 : 프로젝트 관련 메인 커뮤니케이션 <br />
                                    - 카톡 : 프로젝트 이외의 커뮤니케이션 <br />
                                    <br />
                                    <br />
                                    회의 프로그램 <br />
                                    - 노션 - 기획 / 전략 개발 진행 <br />
                                    - 피그마 - 디자인 <br />
                                </pre>
                            </article>
                            <hr className={styles.lineOfSplit} />
                            <article>
                                <span className={styles.detailOfProject}> 사용프로그램 및 언어 </span>
                                <div className={styles.detailOfTag}>
                                    <HashTag text="#Adobe Photoshop" />
                                    <HashTag text="#Figma" />
                                    <HashTag text="#JAVA" />
                                    <HashTag text="#Swift" />
                                    <HashTag text="#HTML" />
                                    <HashTag text="#CSS" />
                                    <HashTag text="#React" />
                                </div>
                            </article>
                            <hr className={styles.lineOfSplit} />
                            <article>
                                <span className={styles.detailOfProject} ref={detailOfProject}> 참고링크 </span>
                                <UseOpenGraph />
                            </article>

                            {/* <article>
                                <span className={styles.detailOfProject} ref={detailOfProject}> 참고링크 </span>
                                <a href="https://www.google.com/webhp?hl=ko&sa=X&ved=0ahUKEwiNo4Hgnrv4AhVJtlYBHcdPAT4QPAgI" target="_blank" >
                                    <pre className={styles.urlOfGoogleForm}> https://www.google.com/webhp?hl=ko&sa=X&ved=0ahUKEwiNo4Hgnrv4AhVJtlYBHcdPAT4QPAgI </pre>
                                    <div className={styles.boxOfGoogleForm}>
                                        <div className={styles.boxOfImage} />
                                        <div className={styles.boxOfContents}>
                                            <span className={styles.boxTitle}>비어에 지원해주셔서 감사합니다!</span> <br />
                                            <span className={styles.boxContent}>대학생 사이드 프로젝트 매칭 플랫폼 제작에 함께 할 크루원을 찾고 있습니다.</span>
                                        </div>
                                        <div className={styles.boxOfUser}>
                                            <img src={profile} />
                                            <div className={styles.boxOfLink}>
                                                https://www.google.com/webhp?hl=ko&sa=X&ved=0ahUKEwiNo4Hgnrv4AhVJtlYBHcdPAT4QPAgI
                                            </div>
                                        </div>
                                    </div>
                                </a>
                                <UseOpenGraph />
                            </article> */}
                        </div>

                        {/* 여기부터 사용자 스크롤에 따라 내려오는 퀵메뉴가 구현될거예요 :-) */}
                        <div className={styles.containerOfMenu}>
                            <div className={styles.quickmenu} ref={quickMenu}>
                                <div className={styles.menuOfContents}>
                                    <div className={styles.menuOfCalendar}>
                                        <Calendar className={styles.imageOfCalendar} />
                                        <span className={styles.textOfCalendarM}>22년 09월 05일 17:00</span>
                                    </div>
                                    <div className={styles.menuOfPosition}>
                                        <PositionOfMenu text="서비스 기획" />
                                        <PositionOfMenu text="UXUI 디자인" />
                                        <PositionOfMenu text="IOS" />
                                        <PositionOfMenu text="안드로이드" />
                                    </div>
                                    <hr className={styles.splitLine} />
                                    <div className={styles.menuOfTag}>
                                        <HashTag text="#공부" />
                                        <HashTag text="#창업" />
                                        <HashTag text="#부업" />
                                        <HashTag text="#사이드 프로젝트" />
                                        <HashTag text="#대학생" />
                                        <HashTag text="#플랫폼" />
                                        <HashTag text="#플랫폼" />
                                        <HashTag text="#플랫폼" />
                                    </div>
                                    <hr className={styles.splitLine_twice} />
                                    <button className={styles.buttonOfCall_M}>연락하기</button>
                                    <button className={styles.buttonOfApply_M}>지원하기</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <Footer />
        </>
    )
}