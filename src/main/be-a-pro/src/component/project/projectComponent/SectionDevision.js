import styles from './SectionDevision.module.css';
import { ReactComponent as Arrow } from '../../../images/icon/arrow.svg';
import ProCard from './ProCard';
import ProCardSlider from './ProCardSlider';
import FilterBar from './FilterBar';
import { Link } from 'react-router-dom';
import { useState } from 'react';

function SectionDevision(props) {
    return(
        <div className={styles.sectionDevision}>
            <div className={styles.container}>
                    <div className={styles.titleBar}>
                        <span className={styles.sectionTitle}>{props.title}</span> <br/>
                        <span className={styles.sectionContent}>{props.sub}</span>
                    </div>
                    <div className={styles.filterBar}>
                        <div className={styles.filterBarLocation}>
                            <FilterBar category={props.category} setCategory={props.setCategory}/>
                        </div>
                        <div className={styles.viewMore}>
                            <Link to={props.title === "NEW PRO" ? '/' : "/projectlist"}>
                                <span className={styles.viewMoreText}>{props.finding}</span>
                            <Arrow/>
                            </Link>
                        </div>
                    </div>
                </div>
            </div>

    )
}

export default SectionDevision;