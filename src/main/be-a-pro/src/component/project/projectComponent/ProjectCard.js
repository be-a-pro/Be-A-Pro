import styles from './ProjectCard.module.css';
import profileImages from '../../../images/project/profile-img.png';
import { ReactComponent as Calendar } from '../../../images/icon/calendar.svg';
import { ReactComponent as Eye } from '../../../images/icon/eye.svg';
import { ReactComponent as Heart } from '../../../images/icon/heart.svg';
import { ReactComponent as HeartFilled } from '../../../images/icon/heart-filled.svg';
import { ReactComponent as People } from '../../../images/icon/people.svg';
import { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import $ from 'jquery';
import jquery from 'jquery';

function ProejctCard() {

    const [heartState, setState] = useState(false);
    const heart = useRef();

    /*
    useEffect(() => {
         여기서 링크에 관련된 부분을 작업해야해요! 
    }, []);
    */

    const actionLike = (e) => {
        setState(!heartState);
    }

    return (
        <>
            <div className={styles.card}>
                {(!heartState) ? <Heart ref={heart} onClick={actionLike} className={styles.heartIcon}/> : <HeartFilled onClick={actionLike} className={styles.heartIcon}/>}
                <Link to='/projectdetail'>
                <div className={styles.cardImg}>
                    <div className={styles.profilePlace}>
                        <div className={styles.profile}>
                            <img src={profileImages} className={styles.profileImages}></img>
                            <span className={styles.profileName}>김민지</span>
                        </div>
                        <div className={styles.calendarPlace}>
                            <Calendar className={styles.calendarImages}/>
                            <span className={styles.createTime}>22.05.16</span>
                        </div>
                        <div className={styles.viewPlace}>
                            <Eye className={styles.viewImages}/>
                            <span className={styles.viewNum}>1,584</span>
                        </div>
                    </div>
                    <div className={styles.titleBar}>
                        <span className={styles.title}>사이드 프로젝트 매칭 플랫폼</span>
                    </div>
                    <div className={styles.positionBar}>
                        <span className={styles.positionPM}>기획(0/1)</span>
                        <span className={styles.centerDot}>·</span>
                        <span className={styles.positionDevelop}>개발(0/2)</span>
                        <span className={styles.centerDot}>·</span>
                        <span className={styles.positionDesign}>디자인(0/1)</span>
                        <span className={styles.centerDot}>·</span>
                        <span className={styles.positionETC}>기타(0/1)</span>
                    </div>
                    <div className={styles.recruitBar}>
                        <People/>
                        <span className={styles.currentRecruit}>모집인원 (1/5)</span>
                    </div>
                    <div className={styles.recruitStatus}>
                        <div className={styles.currentStatus}></div>
                    </div>
                </div>
                </Link>
            </div>
            </>
    )
}

export default ProejctCard;