import styles from './Signup.module.scss';
import { useEffect, useRef, useState } from 'react';
import PostionBtn from './signupComponent/PositionBtn';
import Checkbox from './signupComponent/CheckBox';
import KeywordBtn from './signupComponent/KeywordBtn';
import Footer from '../Footer';
import {ReactComponent as Search} from '../../images/search-icon.svg';
import {ReactComponent as Add} from '../../images/icons/add.svg';
import SearchBar from './SearchBar';

function Signup() {

    const [toggle, setToggle] = useState(false);
    const [toggle2, setToggle2] = useState(false);
    const [file, setFile] = useState("");
    const [url, setUrl] = useState([]);
    const [tagPosition, setTagPostion] = useState([]);
    const [tagkeyword, setTagKeyword] = useState([]);
    const valueOfUrl = useRef(null);
    const label = useRef(null);
    const search = useRef(null);
    const keyword = useRef(null);
    const valueOfFile = useRef(null);
    const filelist = ['jpg', 'png', 'pdf', 'ppt', 'pptx', 'hwp', 'hwpx'];
    const nextID = useRef(0);
    const keywordID = useRef(0);

    const dummy = ['컨텐츠', '컨텐츠 디자인', '공룡'];
    const [query, setQuery] = useState("");

    useEffect(() => {
        if (url.length === 3) {
            valueOfUrl.current.disabled = true;
            valueOfUrl.current.value = "링크를 입력해주세요"
            valueOfUrl.current.style.color = "#bdbdbd";
        }
    }, [url, tagkeyword]);

    function changeToggle() {
        setToggle(!toggle);
    }

    function changeToggle2() {
        setToggle2(!toggle2);
    }

    function submitOfKeyword(e) {
        const data = e.target.value;
        if (e.key === "Enter") {
            setTagKeyword([...tagkeyword, {
                id : tagkeyword.length,
                tag : `#${data}`,
            }])
            keyword.current.value = "";
            search.current.style.visibility = "hidden";
        }
    }

    function inputOfKeyword(e) {
        const data = e.target.value;
        if (data !== "") {
            search.current.style.visibility = "visible";
        } else {
            search.current.style.visibility = "hidden";
        }
        setQuery(data);
    }

    function deleteOfKeyword(id) {
        const inputs = [];
        setTagKeyword(tagkeyword.filter(item => item.id !== id));

        const data = tagkeyword.filter(item => item.id !== id);
        if (id !== keyword.length-1) {
            data.map((item) => {
                item.id === 0 ? inputs.push({id : item.id, tag : item.tag}) : inputs.push({id : item.id-1, tag: item.tag})
            });
        }
        setTagKeyword(inputs);
    }

    function inputOfFile(e) {
        const data = e.target.value;    
        const fileName = data.split('\\');
        valueOfFile.current.disabled = true;
        label.current.style.backgroundColor = "#FAFAFA";

        let filter = "";
        // 사용자가 HTML 코드를 수정하여 접근하고자 할 때, 비정상적인 코드 차단
        filelist.map((item) => {
            if (item === fileName[fileName.length - 1].split('.')[1]) {
                setFile(fileName[fileName.length - 1]);
                filter = item;
            };
        });

        if (filter === "") {
            valueOfFile.current.value = "";
            alert("잘못된 접근입니다!");
        }
    }

    function deleteOfFile() {
        valueOfFile.current.disabled = false;
        label.current.disabled = false;
        label.current.style.backgroundColor = "white";
        setFile("");
    }

    function inputOfUrl() {
        let addOfUrl = {};
        const data = valueOfUrl.current.value;
        if (data !== "") {
            if (data.includes(`http`) && data.includes("www")) {
                addOfUrl = {
                    id: url.length,
                    url: data,
                }
                setUrl(url => [...url, addOfUrl]);
            } else {
                alert("올바른 URL을 입력해주세요!")
            }
        } else {
            alert("올바른 URL을 입력해주세요!")
        }
    }

    function deleteOfUrl(id) {
        valueOfUrl.current.disabled = false;
        valueOfUrl.current.style.color = "#000";
        valueOfUrl.current.value = "";
        const inputs = [];
        console.log("id : ", id);
        setUrl(url.filter(data => data.id !== id));

        const del = url.filter(data => data.id !== id);
        if (id !== 2) {
            del.map((item) => {
                item.id === 0 ? inputs.push({id : item.id, url : item.url}) : inputs.push({id : item.id-1, url : item.url});
            })
            setUrl(inputs);
        }
    }

    function inputOfUrl_key(e) {
        const data = e.target.value;
        let addOfUrl;
        if (url.length !== 3) {
            if (e.key === 'Enter') {
                if (data !== "") {
                    if (data.includes(`http`) && data.includes("www")) {
                    const data = e.target.value;
                        if (url.length === 0) {
                            addOfUrl = {
                                id: 0,
                                url: data,
                            }
                        } else {
                            addOfUrl = {
                                id: url.length,
                                url: data,
                            }
                        }
                        setUrl(url => [...url, addOfUrl]);
                        if (url.length === 3) {
                            valueOfUrl.current.disabled = true;
                            valueOfUrl.current.style.color = "#bdbdbd";
                        }
                    } else {
                        alert("올바른 URL을 입력해주세요!");
                    }
                } else {
                    alert("올바른 URL을 입력해주세요!")
                }
            }
        }
    }

    //$toggle.classList.toggle('active');

    return (
        <section>
            <div className={styles.container}>
                <div className={styles.contentOfContainer}>
                    <h1>회원가입</h1>
                    {/* 이름을 적는 칸이에요 */}
                    <div className={styles.boxOfNameForm}>
                        <span className={styles.boxOfTitle}>이름</span>
                        <input type="input" className={styles.boxOfInput} placeholder='이름을 입력해주세요'/>
                    </div>
                    {/* 전화번호 적는 칸이에요 */}
                    <div className={styles.boxOfNumForm}>
                        <span className={styles.boxOfTitle}>전화번호</span>
                        <div className={styles.contentOfBox}>
                            <input type="checkbox" className={styles.toggle} hidden/> 
                                <div htmlFor={styles.toggle} onClick={changeToggle} className={toggle ? `${styles.toggleSwitch} ${styles.toggleSwitch_checked}` : styles.toggleSwitch}>
                                    <div className={toggle ? `${styles.toggleButton} ${styles.toggleButton_checked}` : styles.toggleButton}></div>
                                    <div className={styles.text}>
                                        <span>공개</span>
                                        <span>비공개</span>
                                    </div>
                                </div>
                            <span className={styles.descriptionOfNum}>
                                공개/비공개 여부를 선택할 수 있습니다.<br/>
                                전화번호는 프로젝트 팀장과 지원자의 연락 용도로만 사용됩니다. (단, 프로젝트 지원 시 프로젝트 팀장은 공개 여부와 상관없이 열람 가능합니다)
                            </span>
                            <input type="input" className={styles.numOfInput} placeholder='전화번호를 입력해주세요'/>
                        </div>
                    </div>
                    {/* 포지션을 적는 칸이에요 */}
                    <div className={styles.boxOfPositionForm}>
                        <span className={styles.boxOfTitle}>포지션</span>
                        <div className={styles.contentOfBox}>
                            <span className={styles.descriptionOfPostion}>수행할 수 있는 직무를 선택해 주세요</span>
                            <div className={styles.sectionOfTag}>
                                <PostionBtn text="UXUI 디자인"/>
                                <PostionBtn text="서비스 기획"/>
                            </div>
      
                            <div className={styles.selectOfPosition}>
                                <div className={styles.positionBox}>
                                    기획
                                    <hr className={styles.lineOfSplit}/>
                                    디자인
                                    <hr className={styles.lineOfSplit}/>                                   
                                    개발
                                    <hr className={styles.lineOfSplit}/>                                   
                                    기타
                                </div>

                                <div className={styles.selectBox}>
                                    <div className={styles.selectBoxOfContent}>
                                        <div className={styles.contentSelect}>
                                            <Checkbox text="UX 기획"/>
                                            <Checkbox text="프로젝트 매니저"/>
                                            <Checkbox text="서비스 기획"/>
                                            <Checkbox text="제품 기획"/>
                                        </div>
                                        <hr className={styles.lineOfSplit}/>
                                    </div>
                                    <div className={styles.selectBoxOfContent}>
                                        <div className={styles.contentSelect2}>
                                            <Checkbox text="그래픽 디자인"/>
                                            <Checkbox text="3D 디자인"/>
                                            <Checkbox text="컨텐츠 디자인"/>
                                            <Checkbox text="UXUX 디자인"/>
                                            <Checkbox text="영상 디자인"/>
                                        </div>
                                        <hr className={styles.lineOfSplit}/>
                                    </div>
                                    <div className={styles.selectBoxOfContent}>
                                        <div className={styles.contentSelect3}>
                                            <Checkbox text="IOS"/>
                                            <Checkbox text="크로스 플랫폼"/>
                                            <Checkbox text="웹 서버"/>
                                            <Checkbox text="AI"/>
                                            <Checkbox text="안드로이드"/>
                                            <Checkbox text="블록체인"/>
                                            <Checkbox text="웹 퍼블리셔"/>
                                            <Checkbox text="DB"/>

                                        </div>
                                        <hr className={styles.lineOfSplit}/>
                                    </div>
                                    <div className={styles.selectBoxOfContent}>
                                        <div className={styles.contentSelect4}>
                                            <Checkbox text="마케팅"/>
                                            <Checkbox text="재무/회계"/>
                                            <Checkbox text="영업"/>
                                            <Checkbox text="기타"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    {/* 키워드를 적는 칸이에요 */}
                    <div className={styles.keywordOfForm}>
                        <span className={styles.boxOfTitle}>관심 키워드</span>
                        <div className={styles.contentOfBox}>
                            <div className={styles.sectionOfTag}>
                                {tagkeyword.map((item, id) => {
                                    return <KeywordBtn key={id} onClick={() => deleteOfKeyword(id)} text={item.tag}/>
                                })}
                            </div>
                            <div className={styles.searchBar}>
                                <Search className={styles.symbolOfSearch}/>
                                <input type="input" className={styles.keywordOfInput} onChange={inputOfKeyword} ref={keyword} onKeyPress={submitOfKeyword} placeholder='관심있는 키워드를 태그해주세요 (ex. 창업, 대학생, 컨텐츠 디자인 등)'/>
                            </div>
                            <div className={styles.containerOfSearch} ref={search}>
                                <ul>
                                    {dummy.map((item) => {
                                        if (item.includes(query, 0)) {
                                            if (item.indexOf(query) === 0) {
                                                return (
                                                    <li>
                                                        <span style={{ color: "#ff7827" }}>{item.slice(0, query.length)}</span>
                                                        <span>{item.slice(query.length, item.length)}</span>
                                                    </li>
                                                )
                                            }
                                        }})
                                    }   
                                </ul>                            
                            </div>
                        </div>
                    </div>
                    {/* 키워드를 적는 칸이에요 */}
                    <div className={styles.keywordOfForm_tool}>
                        <span className={styles.boxOfTitle}>사용 툴</span>
                        <div className={styles.contentOfBox_tool}>
                            <div className={styles.searchBar_tool}>
                                <Search className={styles.symbolOfSearch}/>
                                <input type="input" className={styles.keywordOfInput} placeholder='사용 가능한 툴을 태그해주세요   (ex. Figma, Adobe, 피그마, 어도비 등)'/>
                            </div>
                        </div>
                    </div>    
                    {/* 포트폴리오를 적는 칸이에요 */}
                    <div className={styles.boxOfPortfolioForm}>
                        <span className={styles.boxOfTitle}>포트폴리오</span>
                        <div className={styles.contentOfBox}>
                            <input type="checkbox" className={styles.toggle} hidden/> 
                                <div htmlFor={styles.toggle} onClick={changeToggle2} className={toggle2 ? `${styles.toggleSwitch} ${styles.toggleSwitch_checked}` : styles.toggleSwitch}>
                                    <div className={toggle2 ? `${styles.toggleButton} ${styles.toggleButton_checked}` : styles.toggleButton}></div>
                                    <div className={styles.text}>
                                        <span>공개</span>
                                        <span>비공개</span>
                                    </div>
                                </div>
                            <span className={styles.descriptionOfNum}>
                                본인의 작업물을 보여줄 수 있는 포트폴리오를 보여주세요<br/>
                                포트폴리오는 공개/비공개 여부를 선택할 수 있습니다 (단, 프로젝트 지원 시 프로젝트 팀장은 공개 여부와 상관없이 열람 가능합니다)
                            </span>

                            <span className={styles.textofLink}>링크</span>
                            <div className={styles.searchBar_portfolio}>
                            <input type="input" onKeyPress={inputOfUrl_key} ref={valueOfUrl} className={styles.portfolioOfInput_url} placeholder='링크를 입력하세요'/>
                            {url.length !== 3 ? <Add className={styles.addBtn} onClick={inputOfUrl}/> : null}
                            </div>

                            {url === [] ? null : url.map((item, id) => {
                                return(                                
                                <div key={id} className={styles.sectionOfFile}>
                                    <div className={styles.enrollFile}>
                                        <span>{item.url}</span>
                                        <div className={styles.deleteBtn} onClick={() => deleteOfUrl(id)}>
                                                삭제
                                            </div>
                                        </div>
                                </div>)
                            })}

                            <span className={styles.textofLink}>파일</span>
                            <div className={styles.searchBar_portfolio}>
                                <label type="input" className={styles.portfolioOfInput_file} htmlFor="input-file" ref={label}>
                                    파일형식 : JPG, PNG, PDF, PPT, PPTX, HWP, HWPX
                                </label>
                                {file !== "" ? null : <label htmlFor="input-file" className={styles.uploadBtn}>업로드</label>}
                                <input type="file" onChange={inputOfFile} ref={valueOfFile} id="input-file" className={styles.upload} accept=".jpg, .png, .pdf, .ppt, .pptx, .hwp, .hwpx"/>
                            </div>
                            {file === "" ? null : 
                            <div className={styles.sectionOfFile}>
                            <div className={styles.enrollFile}>
                                <span>{file}</span>
                                <div className={styles.deleteBtn} onClick={deleteOfFile}>
                                            삭제
                                        </div>
                                    </div>
                                </div>}
                        </div>
                    </div>    

                    {/* 마지막으로 버튼을 적는 칸이에요 */}
                    <div className={styles.containerOfBtn}>
                        <button className={styles.prevBtn}>
                            나중에 하기
                        </button>
                        <button className={styles.submitBtn}>
                            회원가입
                        </button>
                    </div>

                </div>
            </div>
            <Footer/>
        </section>
    )
}

export default Signup;