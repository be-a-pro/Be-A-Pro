import styles from './ProCard.module.css';
import profileImages from '../../../images/project/profile-img-b.png';

function ProCard() {
    return (
            <div className={styles.profilePlace}>
                <div className={styles.profileDetail}>
                    <div className={styles.imgBar}>
                        <div className={styles.profileImages}/>

                    </div>
                    <span className={styles.profileName}>김현우</span>
                    <div className={styles.container}>
                        <div className={styles.positionBar}>
                            <span className={styles.position_1}>프론트엔드</span>
                            <span className={styles.centerDot}></span>
                            <span className={styles.position_2}>UX/UI 디자인</span>
                        </div>
                    </div>
                        <div className={styles.skillSection_1}>
                            <div className={styles.tagButton}>
                                <span className={styles.skillSpan}>#Figma</span>
                            </div>
                            <div className={styles.tagButton}>
                                <span className={styles.skillSpan}>#React</span>
                            </div>
                            <div className={styles.tagButton}>
                                <span className={styles.skillSpan}>#CSS</span>
                            </div>
                        </div>
                        <div className={styles.skillSection_2}>
                            <div className={styles.tagButton}>
                                <span className={styles.skillSpan}>#HTML</span>
                            </div>
                            <div className={styles.tagButton}>
                                <span className={styles.skillSpan}>#JS</span>
                            </div>
                        </div>
                </div>
            </div>
    )
}

export default ProCard;