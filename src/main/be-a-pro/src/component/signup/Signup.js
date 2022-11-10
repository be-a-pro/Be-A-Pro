import styles from './Signup.module.scss';
import { useEffect, useRef, useState } from 'react';
import PostionBtn from './signupComponent/PositionBtn';

function Signup() {

    const toggleDOM = useRef(null);
    const [toggle, setToggle] = useState(false);

    function changeToggle() {
        setToggle(!toggle);
        console.log(toggle);
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
                                <div for={styles.toggle} onClick={changeToggle} class={toggle ? `${styles.toggleSwitch} ${styles.toggleSwitch_checked}` : styles.toggleSwitch}>
                                    <div class={toggle ? `${styles.toggleButton} ${styles.toggleButton_checked}` : styles.toggleButton}></div>
                                    <div class={styles.text}>
                                        <span>공개</span>
                                        <span>비공개</span>
                                    </div>
                                </div>
                            <span className={styles.descriptionOfNum}>
                                공개/비공개 여부를 선택할 수 있습니다.<br/>
                                전화번호는 프로젝트 팀장과 지원자의 연락 용도로만 사용됩니다. (단, 프로젝트 지원 시 프로젝트 팀장은 공개 여부와 상관없이 열람 가능합니다)
                            </span>
                            <input type="input" className={styles.boxOfInput} placeholder='전화번호를 입력해주세요'/>
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
                            <div class={styles.position1}>
                                <div class={styles.boxOfPostion1}>
                                    기획
                                    <hr className={styles.lineOfSplit}/>
                                </div>
                                <div class={styles.contentOfPosition1}>ㅍ</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    )
}

export default Signup;